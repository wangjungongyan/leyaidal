package com.leya.idal.router;

import java.util.ArrayList;
import java.util.List;

import com.ibatis.sqlmap.client.SqlMapClient;

public class RouterStrategyOfRandom extends RouterStrategyTemplate {

    public SqlMapClient matchSqlMapClient() {

        int index = randomIndex(getReads().size());

        List<SqlMapClient> l = new ArrayList<SqlMapClient>();
        l.addAll(getReads().keySet());

        return l.get(index);
    }

}
