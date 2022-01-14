package myflink;
//import myflink.kafka.KafkaWriter;
import myflink.loads.Load;
import myflink.sink.MysqlConnect;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.shaded.guava18.com.google.common.collect.Lists;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.util.Collector;

/*
import org.apache.kafka.clients.producer.ProducerRecord;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
*/

import java.util.List;
import java.util.Properties;

/**
 * @author liushuai
 * @Date 2021-12-14
 */
public class SourceKafka_Flink {

    //本地的kafka机器列表
    /*public static final String BROKER_LIST = "hadoop102:9092";*/
    //kafka的topic
    /*public static final String TOPIC_Load = "first";*/
    //key序列化的方式，采用字符串的形式
   /* public static final String KEY_SERIALIZER = "org.apache.kafka.common.serialization.StringSerializer";
    //value的序列化的方式
    public static final String VALUE_SERIALIZER = "org.apache.kafka.common.serialization.StringSerializer";*/
    //string和date的转换
  /*  public static Date getNowDate(String dateString) throws ParseException {
           SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
           Date currentTime_2 = formatter.parse(dateString);
           return currentTime_2;
        }*/



    public static void main(String[] args) throws Exception{
        //构建流执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //kafka连接
        Properties prop = new Properties();
        prop.put("bootstrap.servers", "hadoop102:9092");
        prop.put("zookeeper.connect", "hadoop102:2181");
        prop.put("group.id", "first");
        //添加key和value的序列化器
        prop.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        prop.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        prop.put("auto.offset.reset", "latest");

        DataStreamSource<String> dataStreamSource = env.addSource(new FlinkKafkaConsumer010<String>(
                "first",
                new SimpleStringSchema(),
                prop
        )).
                //单线程打印，控制台不乱序，不影响结果
                setParallelism(1);
/*        ProducerRecord<String, String> record = new ProducerRecord<String, String>(TOPIC_Load, null,
                null, LoadJson);//如果能使用josn格式更好
        发送到缓存
        从kafka里读取数据，转换成Load对象*/
        //flatmap算子
        DataStream<Load> dataStream =dataStreamSource.flatMap(new FlatMapFunction<String, Load>() {
                    @Override
                    public void flatMap(String line, Collector<Load> out) throws Exception {
                        //将一行字符串按空格切分成一个字符串数组
                        String[] tokens = line.split(",");
                        Load Load=new Load();
                        if(tokens.length==7){
                            Load.setXH(Integer.parseInt(tokens[0]));
                            Load.setCKSJ(tokens[1]);
                            Load.setCX(tokens[2]);
                            Load.setRKSJ(tokens[4]);
                            Load.setCP(tokens[6]);
                            Load.setSFZRKMC(tokens[3]);
                            Load.setSFZCKMC(tokens[5]);
                        }
                        out.collect(Load);
                    }
                });
                     /*输出结果 (word, 1)
                    for (String token : tokens) {
                        if (token.length() > 0) {
                            collector.collect(new Tuple2<>(token, 1));
                        }
                    }
                })
                .returns(Types.VALUE(Load.class));
                .keyBy(0)
                .timeWindow(Time.seconds(5))
                .sum(1);
                dataStreamSource.map(value -> JSONObject.parseObject(value, Load.class));//处理josn格式的数据
         .flatMap((String line,Load ) -> {
                    String[] tokens = line.split(",");
                    if(tokens.length==5){
                        Load.setXH(Integer.parseInt(tokens[0]));
                        Load.setCX(tokens[1]);
                        Load.setSFZRKMC(tokens[2]);
                        Load.setSFZCKMC(tokens[3]);
                        Load.setCP(tokens[4]);
                    }
              //     收集1秒钟的总数
            dataStream.addSink(new MySqlSink());*/
        dataStream.timeWindowAll(Time.seconds(1L)).apply(new AllWindowFunction<Load, List<Load>, TimeWindow>() {
                    @Override
                    public void apply(TimeWindow timeWindow, Iterable<Load> iterable, Collector<List<Load>> out) throws Exception {
                        List<Load> Loads = Lists.newArrayList(iterable);
                        if(Loads.size() > 0) {
                            //用来判断是否写入成功
                            System.out.println("1秒的总共收到的条数：" + Loads.size());
                            out.collect(Loads);
                        }
                    }
                })
                //sink 到数据库
                .addSink(new MysqlConnect());
                /*打印到控制台
                .print();*/
        env.execute("kafka 消费任务开始");
    }
}