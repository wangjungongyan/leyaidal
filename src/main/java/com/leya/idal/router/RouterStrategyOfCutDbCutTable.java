package com.leya.idal.router;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.leya.idal.constant.CommonConstant;
import com.leya.idal.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RouterStrategyOfCutDbCutTable extends RouterStrategyTemplate {

    public SqlMapClient matchSqlMapClient() {
        SqlTrace sqlTrace = (SqlTrace) ExecutionContext.getContext();
        CutDbCutTableModel cutDbCutTableModel = sqlTrace.getCutDbCutTableModel();
        RouteRule[] routeRules = cutDbCutTableModel.getRoutes();

        String originDbRule = routeRules[0].getDb_rule();
        List<String> notePlaceholder = gainPlaceholder(originDbRule);
        sqlTrace.setNotePlaceholder(notePlaceholder);
        String replacedDbRule = replaceDbRulePlaceholder(originDbRule, notePlaceholder, sqlTrace.getSqlParameters());
        int dbIndex = Calculator.eval2intValue(replacedDbRule);

        return getSqlMapClinet(originDbRule, dbIndex);
    }

    private SqlMapClient getSqlMapClinet(String originDbRule, int dbIndex) {
        SqlMapClient sqlMapClient = this.getIndexedDbs().get(dbIndex);

        if (sqlMapClient == null) {
            throw new RuntimeException("Can not match sqlMapClient by db rule '" + originDbRule
                                       + "',and caculated dbIndex is " + dbIndex);
        }

        return sqlMapClient;
    }

    private static String replaceDbRulePlaceholder(String originDbRule, List<String> paramKeys,
                                                   Map<String, Object> sqlParameters) {
        String replacedDbRule = originDbRule;

        for (String paramKey : paramKeys) {
            Object paramValue = sqlParameters.get(paramKey);
            if (paramValue == null) {
                continue;
            }

            replacedDbRule = replacedDbRule.replaceFirst("\\" + CommonConstant.QUESTION_MARK + paramKey + "\\"
                                                         + CommonConstant.QUESTION_MARK, paramValue.toString());
        }
        return replacedDbRule;
    }

    private static List<String> gainPlaceholder(String originDbRule) {
        int currentIndex = originDbRule.indexOf(CommonConstant.QUESTION_MARK);
        int nextIndex = 0;
        int lastIndex = originDbRule.lastIndexOf(CommonConstant.QUESTION_MARK);
        List<String> paramKeys = new ArrayList<String>();

        while (lastIndex > nextIndex) {
            nextIndex = originDbRule.indexOf(CommonConstant.QUESTION_MARK, currentIndex + 1);
            String paramKey = originDbRule.substring(currentIndex + 1, nextIndex);
            if (paramKey == null) {
                break;
            }
            paramKeys.add(paramKey);
            currentIndex = nextIndex;
        }
        return paramKeys;
    }

}
