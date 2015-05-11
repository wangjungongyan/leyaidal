package com.leya.idal.aop.replace;

public interface SqlReplacer {

    public String generatePageQuerySql(String originalSql, int start, int limit);
}
