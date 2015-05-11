package com.leya.idal.constant;

public class ExceptionConstant {

    public static final String ILLEGAL_PAGENO              = "If you call page query,need to set a key named 'pageNo' and the value must greater than 0.";

    public static final String ILLEGAL_PAGE_SIZE           = "If you call page query,need to set a key named 'pageSize' and the value must greater than 0.";

    public static final String EMPTY_INDEXDBS              = "This is a cut db cut table call,so set property indexedDbs for class IBatisGenericDaoImpl.";

    public static final String ILLEGAL_INDEXDBS            = "Property dbIndexes of class IBatisGenericDaoImpl is illegal.";

    public static final String ILLEGAL_ROUTE_STRATEGY      = "Can not get router strategy implment.";

    public static final String ILLEGAL_ACTION              = "You called a not supported action type.";

    public static final String EMPTY_CUT_DB_CUT_TABLE_NOTE = "This is a cut db cut table call,so need to set cut db cut table note under sql.";

    public static final String EMPTY_TABLE_PREFIX          = "This is a cut db cut table call,so need to set table prefix note under sql.";

    public static final String EMPTY_ROUTES                = "This is a cut db cut table call,so need to set routes note under sql.";

}
