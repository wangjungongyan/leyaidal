package com.leya.idal.router;

import com.ibatis.sqlmap.client.SqlMapClient;

import java.util.List;
import java.util.Map;
import java.util.Random;

abstract public class RouterStrategyTemplate implements RouterStrategy {

    private SqlMapClient               write;

    private Map<SqlMapClient, Integer> reads;

    private List<SqlMapClient>         indexedDbs;

    protected int randomIndex(int scope) {
        return new Random().nextInt(scope);
    }

    public SqlMapClient getWrite() {
        return write;
    }

    public void setWrite(SqlMapClient write) {
        this.write = write;
    }

    public Map<SqlMapClient, Integer> getReads() {
        return reads;
    }

    public void setReads(Map<SqlMapClient, Integer> reads) {
        this.reads = reads;
    }

    public List<SqlMapClient> getIndexedDbs() {
        return indexedDbs;
    }

    public void setIndexedDbs(List<SqlMapClient> indexedDbs) {
        this.indexedDbs = indexedDbs;
    }

    public SqlMapClient matchSqlMapClient() {
        return null;
    }
}
