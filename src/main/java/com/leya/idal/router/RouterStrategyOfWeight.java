package com.leya.idal.router;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;

public class RouterStrategyOfWeight extends RouterStrategyTemplate {

    public SqlMapClient matchSqlMapClient() {
        List<SqlMapClient> totalSqlMapClient = this.caculateTotalSqlMapClient(getReads());

        return randomSqlMapClient(totalSqlMapClient);
    }

    private List<SqlMapClient> caculateTotalSqlMapClient(Map<SqlMapClient, Integer> reads) {
        List<SqlMapClient> ds = new ArrayList<SqlMapClient>();

        for (Map.Entry<SqlMapClient, Integer> r : reads.entrySet()) {
            int singleRate = r.getValue();

            if (singleRate == 0) {
                continue;
            }

            for (int i = 0; i < singleRate; i++) {
                ds.add(r.getKey());
            }
        }

        return ds;
    }

    private SqlMapClient randomSqlMapClient(List<SqlMapClient> totalSqlMapClient) {
        int index = randomIndex(totalSqlMapClient.size());
        return totalSqlMapClient.get(index);
    }

}
