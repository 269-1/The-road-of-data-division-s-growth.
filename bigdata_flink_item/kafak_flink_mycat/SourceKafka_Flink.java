package newFlink;
import newFlink.loads.Load;
import newFlink.sink.MysqlConnect;
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

import java.util.List;
import java.util.Properties;

/**
 * @author liushuai
 * @Date 2021-12-14
 */
public class SourceKafka_Flink {
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
                setParallelism(1);
        //flatmap算子
        DataStream<Load> dataStream =dataStreamSource.flatMap(new FlatMapFunction<String, Load>() {
                    @Override
                    public void flatMap(String line, Collector<Load> out) throws Exception {
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
        env.execute("kafka 消费任务开始");
    }
}