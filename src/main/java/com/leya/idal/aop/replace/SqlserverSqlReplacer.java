package com.leya.idal.aop.replace;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class SqlserverSqlReplacer implements SqlReplacer {

    private static final String ORDER_BY_PATTERN    = "\\s(order\\s+by(.+)($))";

    private static final String RIGH_PARENTHESIS    = ")";

    private static final String FROM_PATTERN        = "(\\s+)from\\s";

    private static final String PAGINATE_TEMP_TABLE = "PAGINATE_TEMP_TABLE";

    private static final String DISTINCT_PATTERN    = "select(\\s+)(distinct(\\s))?";

    public String generatePageQuerySql(String originalSql, int start, int limit) {
        originalSql = StringUtils.removeEnd(originalSql.trim(), ";");
        if (start > 1) {
            String select = getSelectSegment(originalSql);
            String orderBy = getOrderBySegment(originalSql);
            String remainder = getRemainderSegment(originalSql, select, orderBy);
            StringBuilder limitSql = new StringBuilder();
            limitSql.append("WITH " + PAGINATE_TEMP_TABLE + " AS ( ").append(select).append(", ROW_NUMBER() OVER (").append(StringUtils.isNotBlank(orderBy) ? orderBy.trim() : "order by (select 0)").append(") AS RowNumber ").append(remainder).append(")").append(" SELECT * FROM "
                                                                                                                                                                                                                                                                             + PAGINATE_TEMP_TABLE).append(" WHERE RowNumber BETWEEN "
                                                                                                                                                                                                                                                                                                                   + start
                                                                                                                                                                                                                                                                                                                   + " AND "
                                                                                                                                                                                                                                                                                                                   + (start
                                                                                                                                                                                                                                                                                                                      + limit - 1)).append(" ORDER BY RowNumber");
            return limitSql.toString();
        } else {
            int firstDistinctEnd = getEndMatched(DISTINCT_PATTERN, originalSql);
            String selectAndDistinct = originalSql.substring(0, firstDistinctEnd);
            String afterDistinct = originalSql.substring(firstDistinctEnd);
            return selectAndDistinct + " top " + limit + " " + afterDistinct;
        }
    }

    private String getSelectSegment(String sql) {
        int firstFrom = getStartMatched(FROM_PATTERN, sql);
        if (firstFrom < 0) {
            throw new IllegalArgumentException("Illegal paginate sql[" + sql + "], cause: no 'from' clause.");
        }
        return sql.substring(0, firstFrom);
    }

    private String getMatchedString(String regex, String text, int group) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(group);
        }
        return null;
    }

    private int getStartMatched(String regex, String text) {
        return getStartMatched(regex, text, 0);
    }

    private int getStartMatched(String regex, String text, int group) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.start(group);
        }
        return -1;
    }

    private int getEndMatched(String regex, String text) {
        return getEndMatched(regex, text, 0);
    }

    private String getOrderBySegment(String sql) {
        int lastRightParenthesis = sql.lastIndexOf(RIGH_PARENTHESIS);
        if (lastRightParenthesis == -1) {
            return getMatchedString(ORDER_BY_PATTERN, sql, 1);
        } else {
            return getMatchedString(ORDER_BY_PATTERN, StringUtils.substringAfterLast(sql, RIGH_PARENTHESIS), 1);
        }
    }

    private String getRemainderSegment(String sql, String select, String orderBy) {
        String afterSelect = StringUtils.substringAfter(sql, select);
        return StringUtils.substringBeforeLast(afterSelect, orderBy);
    }

    private int getEndMatched(String regex, String text, int group) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.end(group);
        }
        return -1;
    }

}
