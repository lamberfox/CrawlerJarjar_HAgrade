package com.query;

import com.common.bean.General;
import com.common.bean.RedisKey;
import com.model.TableInfo;
import com.model.TableWeight;
import com.model.Urgent;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * author:24KTai
 * time:2017-08-20 15:01
 * describe: sql语法
 */
public class SqlSyntax {


    /**
     * 获取表的权重查询语句
     *
     * @param table
     * @return
     */
    static public String getWeightLevelSql(String table) {
        return "select distinct Weight from " + table;
    }

    /**
     * 获取按权重加载查询语句
     *
     * @param tableWeights
     * @return
     */
    static public ArrayList<String> getLoadWeightSqls(ArrayList<TableWeight> tableWeights) {
        String sql = "select * from %s where weight=%d";
        ArrayList<String> sqls = new ArrayList<String>();
        for (TableWeight tableWeight : tableWeights) {
            sqls.add(String.format(sql, tableWeight.getTable(), tableWeight.getWeight()));
        }
        return sqls;
    }


    /**
     * 获取更新查询语句
     *
     * @param table
     * @param time
     * @return
     */
    static public String getUpdateSql(String table, long time) {
        String sql = "select * from %s where AddOn >= '%s' OR UpdateOn >= '%s'";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = simpleDateFormat.format(new Date(time));
        return String.format(sql, table, format, format);
    }

    /**
     * 获取数据库表信息查询语句
     *
     * @param table
     * @return
     */
    static public String getTableInfoSql(String table) {
        String sql = "select min(ID) as minID, max(ID) as maxID from %s";
        return String.format(sql, table);
    }

    /**
     * 批量获取加载查询语句
     *
     * @param info
     * @param number
     * @return
     */
    static public ArrayList<String> getLoadSqlArray(TableInfo info, int number) {
        ArrayList<String> sqls = new ArrayList<String>();
        int start = info.getMinID();

        while (start < info.getMaxID()) {
            sqls.add(getLoadSql(info.getTable(), start, start + number));
            start += number;
        }
        return sqls;
    }


    /**
     * 获取加载查询语句
     *
     * @param table
     * @param minID
     * @param maxID
     * @return
     */
    static public String getLoadSql(String table, int minID, int maxID) {
        String sql = "select * from %s where ID >=%d AND ID <= %d";
        return String.format(sql, table, minID, maxID);
    }

    static public String getCustomSql(List<RedisKey> keys) {
        String sql = "select * from custom where ID in (%s)";
        String ids = "";
        for (RedisKey key : keys) {
            General general = (General) key;
            ids += "," + general.getCustomID();
        }
        if (ids.startsWith(",")) {
            ids = ids.substring(1, ids.length());
        }
        return String.format(sql, ids);
    }

    static public String getIdSql(String table) {
        String sql = "select ID from %s";
        return String.format(sql, table);
    }

    static public String getUrgentSql(String table, List<Urgent> list) {
        String sql = "select * from %s where ID in(%s)";
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < list.size(); ++index) {
            builder.append(list.get(index).getId());
            if (index < list.size() - 1) {
                builder.append(",");
            }
        }
        return String.format(sql, table, builder.toString());
    }

}
