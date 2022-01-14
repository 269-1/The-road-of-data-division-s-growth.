package newFlink.sink;

import com.alibaba.druid.pool.DruidDataSource;
import newFlink.DB.Mysql;
import newFlink.loads.Load;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;


public class MysqlConnect extends RichSinkFunction<List<Load>> {

    private PreparedStatement ps;
    private Connection connection;
    private static DruidDataSource dataSource;
    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        //获取数据库连接，准备写入数据库
        connection = Mysql.getConnection();
        String sql = "insert into expressway(XH,CKSJ,CX,SFZRKMC,RKSJ,SFZCKMC,CP) values (?,?,?,?,?,?,?); ";
        ps = connection.prepareStatement(sql);
    }
    @Override
    public void close() throws Exception {
        super.close();
        //关闭并释放资源
        if(connection != null) {
            connection.close();
        }

        if(ps != null) {
            ps.close();
        }
    }
//XH,CX,SFZRKMC,SFZCKMC,CP，从flink处理好的数据拿取
    @Override
    public void invoke(List<Load> Loads, Context context) throws Exception {
        for(newFlink.loads.Load Load : Loads) {
            ps.setInt(1, Load.getXH());
            ps.setString(2, Load.getCKSJ());
            ps.setString(3, Load.getCX());
            ps.setString(4, Load.getSFZRKMC());
            ps.setString(5,Load.getRKSJ());
            ps.setString(6, Load.getSFZCKMC());
            ps.setString(7, Load.getCP());
            ps.executeUpdate();
        }
    }

}