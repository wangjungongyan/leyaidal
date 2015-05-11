package com.leya.idal.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;

import com.leya.idal.model.ExecutionContext;
import com.leya.idal.model.SqlTrace;

public class SqlTraceAdvice implements MethodInterceptor {

    private static Logger LOGGER = Logger.getLogger(SqlTraceAdvice.class);

    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            long begin = System.currentTimeMillis();
            Object retVal = invocation.proceed();
            long timeCost = System.currentTimeMillis() - begin;

            SqlTrace sqlTrace = (SqlTrace) ExecutionContext.getContext();
            sqlTrace.setCost(timeCost);
            LOGGER.info(sqlTrace);

            return retVal;
        } finally {
            ExecutionContext.removeContext();
        }
    }
}
