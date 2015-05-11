package com.leya.idal.aop.replace;

import com.leya.idal.constant.CommonConstant;

public class MysqlSqlReplacer implements SqlReplacer {

    public String generatePageQuerySql(String originalSql, int start, int limit) {
        if (originalSql == null || originalSql.trim().isEmpty()) {
            throw new IllegalArgumentException("Sql is null.");
        }

        String _sql = originalSql.trim();

        boolean hasEndToken = false;
        if (_sql.endsWith(CommonConstant.SEMICOLON)) {
            _sql = _sql.substring(0, _sql.length() - 1);
            hasEndToken = true;
        }

        StringBuffer sb = new StringBuffer();
        sb.append(_sql);

        int skip = start - 1;
        if (skip > 0) {
            sb.append(" LIMIT ").append(skip).append(',').append(limit);
        } else {
            sb.append(" LIMIT ").append(limit);
        }

        if (hasEndToken) {
            sb.append(CommonConstant.SEMICOLON);
        }

        return sb.toString();
    }

}
