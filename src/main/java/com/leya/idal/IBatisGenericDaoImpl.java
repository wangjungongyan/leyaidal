package com.leya.idal;

import com.alibaba.fastjson.JSON;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.engine.impl.ExtendedSqlMapClient;
import com.ibatis.sqlmap.engine.mapping.sql.Sql;
import com.ibatis.sqlmap.engine.mapping.sql.SqlText;
import com.ibatis.sqlmap.engine.mapping.sql.dynamic.DynamicSql;
import com.ibatis.sqlmap.engine.mapping.statement.MappedStatement;
import com.ibatis.sqlmap.engine.scope.SessionScope;
import com.ibatis.sqlmap.engine.scope.StatementScope;
import com.leya.idal.constant.CommonConstant;
import com.leya.idal.constant.ExceptionConstant;
import com.leya.idal.constant.PageConstant;
import com.leya.idal.model.*;
import com.leya.idal.router.DbRouter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.dao.DataAccessException;

import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class IBatisGenericDaoImpl extends IsqlMapClientDao implements IGenericDao, ApplicationContextAware {

    private ApplicationContext context;

    private Map<SqlMapClient, Integer> readMaps = new HashMap<SqlMapClient, Integer>();

    private List<SqlMapClient> indexedDbs = new ArrayList<SqlMapClient>();

    @SuppressWarnings("unchecked")
    public List<Object> executeQuery(DAOMethod daoMethod) throws DataAccessException {
        preProcessing(daoMethod);
        String statementName = getStatementName(daoMethod);
        return this.getSqlMapClientTemplate().queryForList(statementName, daoMethod.getParams());
    }

    public Object executeLoad(DAOMethod daoMethod) throws DataAccessException {
        preProcessing(daoMethod);
        String statementName = getStatementName(daoMethod);
        return this.getSqlMapClientTemplate().queryForObject(statementName, daoMethod.getParams());
    }

    public Object executeInsert(DAOMethod daoMethod) throws DataAccessException {
        preProcessing(daoMethod);
        String statementName = this.getStatementName(daoMethod);

        return this.getSqlMapClientTemplate().insert(statementName, daoMethod.getParams());
    }

    public int executeUpdate(DAOMethod daoMethod) throws DataAccessException {
        preProcessing(daoMethod);
        String statementName = getStatementName(daoMethod);
        return this.getSqlMapClientTemplate().update(statementName, daoMethod.getParams());
    }

    public int executeDelete(DAOMethod daoMethod) throws DataAccessException {
        preProcessing(daoMethod);
        String statementName = getStatementName(daoMethod);

        return this.getSqlMapClientTemplate().delete(statementName, daoMethod.getParams());
    }

    public PageModel executePageQuery(DAOMethod daoMethod) throws DataAccessException {
        preProcessing(daoMethod);
        String statementName = getStatementName(daoMethod);

        Map<String, Object> params = daoMethod.getParams();
        int skipResults = (Integer) params.remove(PageConstant.SKIP_RESULTS);
        int maxResults = (Integer) params.remove(PageConstant.MAX_RESULTS);
        int pageSize = (Integer) params.remove(PageConstant.PAGE_SIZE);
        int pageNo = (Integer) params.remove(PageConstant.PAGENO);

        List<?> records = this.getSqlMapClientTemplate().queryForList(statementName, params, skipResults, maxResults);
        return preparePageModel(statementName, params, pageSize, pageNo, records);
    }

    public Object executeCall(DAOMethod daoMethod) throws DataAccessException {
        // TODO Auto-generated method stub
        return null;
    }

    private void setSqlTrace2Context(DAOMethod daoMethod) {

        String originSql = this.prepareOriginSql(daoMethod);
        Map<String, Object> sqlParameters = daoMethod.getParams();
        String sqlId = daoMethod.getName();
        long cost = 0;
        CutDbCutTableModel cutDbCutTableModel = prepareCutDbCutTableModel(originSql);

        // 若是分库分表操作，则需设置indexedDbs
        if (cutDbCutTableModel != null && indexedDbs.size() == 0) {
            throw new RuntimeException(ExceptionConstant.EMPTY_INDEXDBS);
        }

        ExecutionContext.setContext(new SqlTrace(daoMethod.getNamespace(), sqlId, originSql, cost, sqlParameters,
                                                 cutDbCutTableModel));
    }

    private String prepareOriginSql(DAOMethod daoMethod) {
        String statementName = getStatementName(daoMethod);
        SqlMapClient tempSqlMapClient = getWrite();
        MappedStatement mappedStatement = ((ExtendedSqlMapClient) (tempSqlMapClient)).getMappedStatement(statementName);

        Sql sql = mappedStatement.getSql();

        if (sql instanceof DynamicSql) {
            DynamicSql dynamicSql = (DynamicSql) sql;
            Field fields[] = dynamicSql.getClass().getDeclaredFields();
            try {
                Field.setAccessible(fields, true);
                for (int i = 0; i < fields.length; i++) {
                    String fieldsName = fields[i].getName();

                    if ("children".equals(fieldsName)) {
                        List values = (List) fields[i].get(dynamicSql);
                        SqlText text = (SqlText) values.get(0);
                        return text.getText();
                    }

                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        SessionScope sessionScope = new SessionScope();
        sessionScope.setSqlMapClient(tempSqlMapClient);
        sessionScope.setSqlMapExecutor(tempSqlMapClient);
        sessionScope.setSqlMapTxMgr(tempSqlMapClient);
        StatementScope statementScope = new StatementScope(sessionScope);

        return sql.getSql(statementScope, daoMethod.getParams());
    }

    private CutDbCutTableModel prepareCutDbCutTableModel(String originSql) {
        if (isExcludeNote(originSql)) {
            return null;
        }

        int beginIndex = originSql.indexOf(CommonConstant.NOTE_PREFIX) + 2;
        int endIndex = originSql.indexOf(CommonConstant.NOTE_SUFFIX);
        String note = originSql.substring(beginIndex, endIndex);

        CutDbCutTableModel model = JSON.parseObject(note, CutDbCutTableModel.class);

        if (model == null) {
            throw new RuntimeException(ExceptionConstant.EMPTY_CUT_DB_CUT_TABLE_NOTE);
        }

        if (model.getTable_prefix() == null) {
            throw new RuntimeException(ExceptionConstant.EMPTY_TABLE_PREFIX);
        }

        if (model.getRoutes() == null || model.getRoutes().length == 0) {
            throw new RuntimeException(ExceptionConstant.EMPTY_ROUTES);
        }

        return model;
    }

    private boolean isIncludeNote(String originSql) {
        return (originSql.trim().startsWith(CommonConstant.NOTE_PREFIX) && originSql.contains(
                CommonConstant.NOTE_SUFFIX));
    }

    private boolean isExcludeNote(String originSql) {
        return !isIncludeNote(originSql);
    }

    private PageModel preparePageModel(String statementName, Map<String, Object> params, int pageSize, int pageNo,
                                       List<?> records) {
        PageModel pageModel = new PageModel();

        pageModel.setRecords(records);
        pageModel.setTotalCount(countTotalRecord(statementName, params));
        pageModel.setPageSize(pageSize);
        pageModel.setCurrentPage(pageNo);

        return pageModel;
    }

    private int countTotalRecord(String statementName, Map<String, Object> params) {
        String totalCountStatementName = statementName + PageConstant.COUNT_SUFFIX;
        return (Integer) this.getSqlMapClientTemplate().queryForObject(totalCountStatementName, params);
    }

    private void preProcessing(DAOMethod daoMethod) {
        setSqlTrace2Context(daoMethod);
        DbRouter dbRouter = new DbRouter(daoMethod, getWrite(), readMaps, indexedDbs);
        SqlMapClient sqlMapClient = dbRouter.matchSqlMapClient();
        setSqlMapClient(sqlMapClient);
    }

    private String getStatementName(DAOMethod daoMethod) {
        return daoMethod.getNamespace() + CommonConstant.POINT + daoMethod.getName();
    }

    public void init() throws Exception {
        if (this.getReads() == null || this.getReads().size() == 0) {
            return;
        }

        for (Entry<String, String> dsEntry : getReads().entrySet()) {
            SqlMapClient readDs = (SqlMapClient) context.getBean(dsEntry.getKey());
            int weight = dsEntry.getValue() == null ? 0 : Integer.parseInt(dsEntry.getValue());
            readMaps.put(readDs, weight);
        }

        if (getIndexedDbs() != null) {
            String[] indexedDbArrs = getIndexedDbs().split(CommonConstant.COMMA);
            if (indexedDbArrs == null || indexedDbArrs.length == 0) {
                throw new RuntimeException(ExceptionConstant.ILLEGAL_INDEXDBS);
            }

            for (String indexedDb : indexedDbArrs) {
                SqlMapClient sqlMapClient = (SqlMapClient) this.context.getBean(indexedDb);
                if (sqlMapClient == null) {
                    throw new RuntimeException(ExceptionConstant.ILLEGAL_INDEXDBS);
                }

                indexedDbs.add(sqlMapClient);
            }
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    public static void main(String[] aa) throws Exception {
        System.out.print(URLDecoder.decode(
                "http%3A%2F%2Fm.51ping.com%2Fmobile%2Fevent%2Ffree%2Fqq%2Fredirect2Confirm%3Fuid%3D613A1D94E3D9F3DB8730994BA645C1BD*14C571696DA865738C493D19FCE5F546%26activityId%3D55998&k=1357709691",
                "UTF-8"));
    }

}
