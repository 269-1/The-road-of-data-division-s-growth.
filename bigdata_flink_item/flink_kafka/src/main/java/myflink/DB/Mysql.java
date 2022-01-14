package myflink.DB;

import com.alibaba.druid.pool.DruidDataSource;

import java.sql.Connection;


public class Mysql {

    private static DruidDataSource dataSource;

    public static Connection getConnection() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://hadoop102:8066/TESTDB?useUnicode=true&characterEncoding=utf8");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");
        //设置初始化连接数，最大连接数，最小闲置数
        dataSource.setInitialSize(10);
        dataSource.setMaxActive(75);
        dataSource.setMinIdle(20);
        return  dataSource.getConnection();
    }
}
