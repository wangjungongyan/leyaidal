package com.leya.idal.router;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.leya.idal.constant.ExceptionConstant;
import com.leya.idal.enums.DbRouterStrategyType;
import com.leya.idal.model.DAOMethod;

import java.util.List;
import java.util.Map;

/**
 * 数据库路由器
 * 
 * @author vali
 */
public class DbRouter {

    private SqlMapClient               write;

    private Map<SqlMapClient, Integer> reads;

    private DAOMethod                  daoMethod;

    List<SqlMapClient>                 indexedDbs;

    public DbRouter(DAOMethod daoMethod, SqlMapClient write, Map<SqlMapClient, Integer> reads,
                    List<SqlMapClient> indexedDbs) {
        this.daoMethod = daoMethod;
        this.write = write;
        this.reads = reads;
        this.indexedDbs = indexedDbs;
    }

    public SqlMapClient matchSqlMapClient() {
        DbRouterStrategyType matchedStrategy = DbRouterStrategyType.matchRouterStrategy(daoMethod, reads);
        return getStrategyImpl(matchedStrategy);
    }

    private SqlMapClient getStrategyImpl(DbRouterStrategyType matchedStrategy) {
        String implementClass = matchedStrategy.getImplementClass();

        try {
            Class<?> strategyClass = Class.forName(implementClass);

            RouterStrategyTemplate strategyImpl = (RouterStrategyTemplate) strategyClass.newInstance();
            strategyImpl.setWrite(write);
            strategyImpl.setReads(reads);
            strategyImpl.setIndexedDbs(indexedDbs);

            return strategyImpl.matchSqlMapClient();

        } catch (Exception e) {
            throw new RuntimeException(ExceptionConstant.ILLEGAL_ROUTE_STRATEGY + e.getMessage());
        }
    }

}
