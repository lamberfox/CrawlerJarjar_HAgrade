package com.common.dao;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

/**
 * 数据库连接池
 *
 * @auther tonyjarjar
 * @create 2017/9/4
 */
public class C3P0pool {
    public static ComboPooledDataSource c3P0pool;

    static {
        c3P0pool = new ComboPooledDataSource();
        Properties props = new Properties();
        try {
            props.load(C3P0pool.class.getClassLoader().getResourceAsStream("c3p0.properties"));

            c3P0pool.setDriverClass(props.getProperty("DriverClass"));
            c3P0pool.setJdbcUrl(props.getProperty("JdbcUrl"));
            c3P0pool.setUser(props.getProperty("User"));
            c3P0pool.setPassword(props.getProperty("Password"));
            c3P0pool.setMaxPoolSize(Integer.parseInt(props.getProperty("MaxPoolSize")));
            c3P0pool.setMinPoolSize(Integer.parseInt(props.getProperty("MinPoolSize")));
            c3P0pool.setInitialPoolSize(Integer.parseInt(props.getProperty("InitialPoolSize")));
            c3P0pool.setMaxStatements(Integer.parseInt(props.getProperty("MaxStatements")));
            c3P0pool.setMaxIdleTime(Integer.parseInt(props.getProperty("MaxIdleTime")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ComboPooledDataSource getInstance(){
        return c3P0pool;
    }

    public static Connection getConnection(){
        Connection conn = null;
        try {
            conn = c3P0pool.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static void closeResource(Connection con , ResultSet resultSet , PreparedStatement preparedStatement) {
        try {
            if (con != null) {
                con.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        } catch (Exception e) {
            System.err.print("c3p0Pool closeResource() error!");
        } finally {
            con = null;
            preparedStatement = null;
            resultSet = null;
        }
    }
}
