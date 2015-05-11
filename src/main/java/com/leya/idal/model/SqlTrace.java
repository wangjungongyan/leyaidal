package com.leya.idal.model;

import java.util.List;
import java.util.Map;

public class SqlTrace {

    /**
     * 命名空间
     */
    private String              nameSpace;

    /**
     * sql map 中配置的sql id
     */
    private String              sqlId;

    /**
     * xml中配置的原生sql语句
     */
    private String              originSql;

    /**
     * 耗时-微秒
     */
    private long                cost;

    /**
     * 参数
     */
    private Map<String, Object> sqlParameters;

    /**
     * sql注释上的占位符字段
     */
    private List<String>        notePlaceholder;

    /**
     * 根据sql上的注释抽象出的分库分表规则
     */
    private CutDbCutTableModel  cutDbCutTableModel;

    public SqlTrace(String nameSpace, String sqlId, String originSql, long cost, Map<String, Object> sqlParameters,
                    CutDbCutTableModel cutDbCutTableModel) {
        this.nameSpace = nameSpace;
        this.sqlId = sqlId;
        this.originSql = originSql;
        this.cost = cost;
        this.sqlParameters = sqlParameters;
        this.cutDbCutTableModel = cutDbCutTableModel;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getSqlId() {
        return sqlId;
    }

    public void setSqlId(String sqlId) {
        this.sqlId = sqlId;
    }

    public String getOriginSql() {
        return originSql;
    }

    public void setOriginSql(String originSql) {
        this.originSql = originSql;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public Map<String, Object> getSqlParameters() {
        return sqlParameters;
    }

    public void setSqlParameters(Map<String, Object> sqlParameters) {
        this.sqlParameters = sqlParameters;
    }

    public CutDbCutTableModel getCutDbCutTableModel() {
        return cutDbCutTableModel;
    }

    public void setCutDbCutTableModel(CutDbCutTableModel cutDbCutTableModel) {
        this.cutDbCutTableModel = cutDbCutTableModel;
    }

    public List<String> getNotePlaceholder() {
        return notePlaceholder;
    }

    public void setNotePlaceholder(List<String> notePlaceholder) {
        this.notePlaceholder = notePlaceholder;
    }

    // 是否是分库分表操作
    public boolean isCutDbCutTableCall() {
        return (cutDbCutTableModel != null);
    }

    @Override
    public String toString() {
        return "SqlTrace [nameSpace=" + nameSpace + ", sqlId=" + sqlId + ", originSql=" + originSql + ", cost=" + cost
               + ", sqlParameters=" + sqlParameters + "]";
    }

}
