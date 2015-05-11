package com.leya.idal.router;

import com.ibatis.sqlmap.client.SqlMapClient;

public interface RouterStrategy {

    public SqlMapClient matchSqlMapClient();
}
