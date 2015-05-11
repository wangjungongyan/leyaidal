package com.leya.idal.aop;

import java.util.List;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.leya.idal.constant.CommonConstant;
import com.leya.idal.model.Calculator;
import com.leya.idal.model.CutDbCutTableModel;
import com.leya.idal.model.ExecutionContext;
import com.leya.idal.model.RouteRule;
import com.leya.idal.model.SqlTrace;

/**
 * 对于分库分表需改写sql
 * 
 * @author vali
 */
public class SqlCutDbCutTableAdvice implements MethodInterceptor {

    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object[] argus = invocation.getArguments();

        SqlTrace sqlTrace = (SqlTrace) ExecutionContext.getContext();
        if (sqlTrace.isCutDbCutTableCall()) {
            String tableRule = getTableRule(sqlTrace);
            int tableIndex = Calculator.eval2intValue(tableRule);
            CutDbCutTableModel cutDbCutTableModel = sqlTrace.getCutDbCutTableModel();
            String tablePrefix = cutDbCutTableModel.getTable_prefix();
            String indexPrefix = cutDbCutTableModel.getIndex_prefix();

            String replaceNewTable = replaceFromSecondReplaceMent((String) argus[2], tablePrefix,
                                                                  prepareNewTable(tablePrefix, tableIndex));
            String replaceNewIndex = replaceFromSecondReplaceMent(replaceNewTable, indexPrefix,
                                                                  prepareNewIndex(indexPrefix, tableIndex));
            argus[2] = replaceNewIndex;
        }

        return invocation.proceed();
    }

    private String replaceFromSecondReplaceMent(String originStr, String regex, String replacement) {
        if (!originStr.contains(regex)) {
            return originStr;
        }

        int firstIndex = originStr.indexOf(regex) + regex.length();

        String[] originStrArr = { originStr.substring(0, firstIndex), originStr.substring(firstIndex) };

        return originStrArr[0] + originStrArr[1].replaceAll(regex, replacement);
    }

    private String prepareNewTable(String tablePrefix, int index) {
        return tablePrefix + index;
    }

    private String prepareNewIndex(String indexPrefix, int index) {
        return indexPrefix + index;
    }

    private String getTableRule(SqlTrace sqlTrace) {
        List<String> notePlaceholder = sqlTrace.getNotePlaceholder();
        Map<String, Object> sqlParameters = sqlTrace.getSqlParameters();
        CutDbCutTableModel cutDbCutTableModel = sqlTrace.getCutDbCutTableModel();
        RouteRule[] routeRule = cutDbCutTableModel.getRoutes();
        String replacedTableRule = routeRule[0].getTable_rule();

        for (String paramKey : notePlaceholder) {
            Object paramValue = sqlParameters.get(paramKey);
            if (paramValue == null) {
                continue;
            }

            replacedTableRule = replacedTableRule.replaceFirst("\\" + CommonConstant.QUESTION_MARK + paramKey + "\\"
                                                               + CommonConstant.QUESTION_MARK, paramValue.toString());
        }

        return replacedTableRule;
    }

}
