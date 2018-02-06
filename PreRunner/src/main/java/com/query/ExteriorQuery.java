package com.query;


import com.common.dao.C3P0pool;
import com.model.NoUse;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * author:24KTai
 * time:2017-08-11 09:41
 * describe: 查询工具
 */
public class ExteriorQuery {

    /**
     * 查询处理
     *
     * @param sql   查询语句
     * @param clz   反序列化实体
     * @return
     */
    public static ArrayList<?> query(String sql, Class clz, int retry) {
        ArrayList<Object> tables = null;
        while (retry >= 0) {
            tables = ExteriorQuery.commonQuery(sql, clz);
            if (tables != null) {
                break;
            }
            --retry;
        }
        return tables;
    }

    public static ArrayList<?> query(String sql, Class clz){
        return query(sql, clz, 3);
    }

    /**
     * 普通查询方法
     *
     * @param sql 查询语句
     * @param clz 反序列化的类
     * @return
     */
    public static <T> ArrayList<T> commonQuery(String sql, Class clz) {

        if (sql == null || sql.length() == 0) {
            return null;
        }
        ArrayList<T> tables = new ArrayList<>();
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        Connection connection = null;
        List<Field> fields = getFields(clz);

        try {
            connection = C3P0pool.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                T table = (T) clz.newInstance();
                table = initVars(table, resultSet, fields);
                tables.add(table);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            tables = null;
        } finally {
            C3P0pool.closeResource(connection,resultSet,preparedStatement);
        }
        return tables;
    }

    /**
     * 初始化变量
     *
     * @param resultSet
     * @throws SQLException
     * @throws IllegalAccessException
     */
    public static <T> T initVars(T bean, ResultSet resultSet, List<Field> fields) throws SQLException, IllegalAccessException {
        if (fields == null) {
            fields = getFields(bean.getClass());
        }

        for (int index = 0; index < fields.size(); ++index) {
            Field field = fields.get(index);
            field.setAccessible(true);
            Class clz = field.getType();
            String name = field.getName();
            //TODO 后面根据情况，添加其他数据类型

            if (clz.equals(int.class)) { //后面
                field.setInt(bean, resultSet.getInt(name));
            } else if (clz.equals(String.class)) {
                int col = resultSet.findColumn(name);
                String type = resultSet.getMetaData().getColumnTypeName(col);
                String str = resultSet.getString(name);
                if (type.contains("TIMESTAMP") || type.contains("DATETIME")){
                    str = removeImpurity(str);
                }
                field.set(bean, str == null ? "" : str);
            } else {
                //后续添加
            }
        }
        return bean;
    }

    /**
     * 获取field
     * @param clz
     * @return
     */
    static ArrayList<Field> getFields(Class clz) {
        ArrayList<Field> fields = new ArrayList<Field>();

        while (clz != null) {
            List<Field> temp = Arrays.asList(clz.getDeclaredFields());
            fields.addAll(temp);
            clz = clz.getSuperclass();
        }

        for (int index = 0; index < fields.size(); ++index) {
            Field field = fields.get(index);

            if (field.getAnnotation(NoUse.class) != null) {
                fields.remove(index);
                --index;
            }
        }
        return fields;
    }

    /**
     * 移除掉时间杂质，时间为"2017-08-12 20:14:58.0",去掉杂质后为"2017-08-12 20:14:58"
     *
     * @param time
     * @return
     */
    private static String removeImpurity(String time) {
        if (time == null) {
            return null;
        }
        if (time.endsWith(".0")) {
            return time.substring(0, time.length() - 2);
        }
        return time;
    }
}
