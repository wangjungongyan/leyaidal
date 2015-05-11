package com.leya.idal.model;

import java.util.LinkedList;
import java.util.List;

public class CutDbCutTableModel {

    /**
     * 分库分表规则
     */
    private RouteRule[] routes;

    /**
     * 表前缀
     */
    private String      table_prefix;

    /**
     * 索引前缀
     */
    private String      index_prefix;

    /**
     * 是否是多个分库分表操作
     */
    private boolean     multiple;

    public RouteRule[] getRoutes() {
        return routes;
    }

    public void setRoutes(RouteRule[] routes) {
        this.routes = routes;
    }

    public String getTable_prefix() {
        return table_prefix;
    }

    public void setTable_prefix(String table_prefix) {
        this.table_prefix = table_prefix;
    }

    public String getIndex_prefix() {
        return index_prefix;
    }

    public void setIndex_prefix(String index_prefix) {
        this.index_prefix = index_prefix;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }
    
    public static void main(String[] args) {
        List<String> l = new LinkedList<String>();
        l.add("aa");
        l.add("bb");
        l.add("cc");
        l.add("dd");
        l.clear();
        System.out.println();
        
    }

}
