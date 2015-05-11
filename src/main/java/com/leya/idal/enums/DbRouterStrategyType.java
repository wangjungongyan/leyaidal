package com.leya.idal.enums;

import java.util.Map;
import java.util.Map.Entry;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.leya.idal.model.DAOMethod;
import com.leya.idal.model.ExecutionContext;
import com.leya.idal.model.SqlTrace;

/**
 * 路由规则
 * 
 * @author vali
 */
public enum DbRouterStrategyType {

    WEIGHT("weight", "权重策略", "com.leya.idal.router.RouterStrategyOfWeight"),

    RANDOM("random", "随机策略", "com.leya.idal.router.RouterStrategyOfRandom"),

    FORCE_HIT_WRITE("forewrite", "强制走写库策略", "com.leya.idal.router.RouterStrategyOfForeWrite"),

    CUT_DB_CUT_TABLE("cutDbcutTable", "分库分表策略", "com.leya.idal.router.RouterStrategyOfCutDbCutTable");

    private String strategyName;

    private String desc;

    private String implementClass;

    private DbRouterStrategyType(String strategyName, String desc, String implementClass) {
        this.strategyName = strategyName;
        this.desc = desc;
        this.implementClass = implementClass;
    }

    public String getStrategyName() {
        return strategyName;
    }

    public void setStrategyName(String strategyName) {
        this.strategyName = strategyName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImplementClass() {
        return implementClass;
    }

    public void setImplementClass(String implementClass) {
        this.implementClass = implementClass;
    }

    public static DbRouterStrategyType matchRouterStrategy(String name) {
        for (DbRouterStrategyType r : DbRouterStrategyType.values()) {
            if (r.getStrategyName().equalsIgnoreCase(name)) {
                return r;
            }
        }

        return null;
    }

    public static String getAllRouterStrategy() {
        StringBuffer allRouterStrategy = new StringBuffer(20);
        allRouterStrategy.append("[");
        DbRouterStrategyType[] strategies = DbRouterStrategyType.values();
        for (int i = 0; i < strategies.length; i++) {
            allRouterStrategy.append(strategies[i].getStrategyName());
            if (i != (strategies.length - 1)) {
                allRouterStrategy.append(",");
            }
        }
        allRouterStrategy.append("]");
        return allRouterStrategy.toString();
    }

    public static void main(String[] args) {
        System.out.println(getAllRouterStrategy());
    }

    public static DbRouterStrategyType matchRouterStrategy(DAOMethod daoMethod, Map<SqlMapClient, Integer> reads) {
        ActionType daoActionType = daoMethod.getActionType();
        SqlTrace sqlTrace = (SqlTrace) ExecutionContext.getContext();

        // 如果是分库分表
        if (sqlTrace.isCutDbCutTableCall()) {
            return DbRouterStrategyType.CUT_DB_CUT_TABLE;
        }

        // 如果没有配置读数据源
        if (reads == null || reads.size() == 0) {
            return DbRouterStrategyType.FORCE_HIT_WRITE;
        }

        // 如果是写操作
        if (ActionType.isWriteOperation(daoActionType)) {
            return DbRouterStrategyType.FORCE_HIT_WRITE;
        }

        // 如果是读操作但是加了强制写标示
        if (daoMethod.isForeWirte()) {
            return DbRouterStrategyType.FORCE_HIT_WRITE;
        }

        // 如果设置了权重值
        if (hasWeightRate(reads)) {
            return DbRouterStrategyType.WEIGHT;
        }

        return DbRouterStrategyType.RANDOM;
    }

    private static boolean hasWeightRate(Map<SqlMapClient, Integer> reads) {
        for (Entry<SqlMapClient, Integer> dsEntry : reads.entrySet()) {
            Integer rate = dsEntry.getValue();
            if (rate > 0) {
                return true;
            }
        }

        return false;
    }

}
