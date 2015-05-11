package com.leya.idal.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.ibatis.sqlmap.engine.execution.SqlExecutor;
import com.leya.idal.aop.replace.SqlReplacer;

/**
 * 对于分页操作需改写sql
 * 
 * @author vali
 */
public class SqlLimitAdvice implements MethodInterceptor {

    private static final String QUERY_METHOD = "executeQuery";

    private SqlReplacer         sqlReplacer;

    public SqlLimitAdvice(SqlReplacer sqlReplacer) {
        this.sqlReplacer = sqlReplacer;
    }

    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object[] argus = invocation.getArguments();

        if (isPageQuery(invocation)) {

            String sql = (String) argus[2];
            int skipResults = (Integer) argus[4];
            int maxResults = (Integer) argus[5];

            argus[2] = sqlReplacer.generatePageQuerySql(sql, skipResults + 1, maxResults);
            argus[4] = SqlExecutor.NO_SKIPPED_RESULTS;
            argus[5] = SqlExecutor.NO_MAXIMUM_RESULTS;
        }

        return invocation.proceed();
    }

    private boolean isPageQuery(MethodInvocation invocation) {
        String methodName = invocation.getMethod().getName();

        if (isNotQuery(methodName)) {
            return false;
        }

        Object[] arguments = invocation.getArguments();
        int skipResults = (Integer) arguments[4];
        int maxResults = (Integer) arguments[5];

        return (skipResults != SqlExecutor.NO_SKIPPED_RESULTS || maxResults != SqlExecutor.NO_MAXIMUM_RESULTS);
    }

    private boolean isNotQuery(String methodName) {
        return !QUERY_METHOD.equals(methodName);
    }

}
