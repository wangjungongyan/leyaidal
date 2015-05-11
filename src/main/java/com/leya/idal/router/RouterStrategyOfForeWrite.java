package com.leya.idal.router;

import com.ibatis.sqlmap.client.SqlMapClient;

public class RouterStrategyOfForeWrite extends RouterStrategyTemplate {

    public SqlMapClient matchSqlMapClient() {
        return getWrite();
    }

}
