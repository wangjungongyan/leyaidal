package com.leya.idal.enums;

public enum ActionType {

    QUERY_LIST,

    QUERY_OBJECT,

    PAGE,

    INSERT,

    UPDATE,

    DELETE,

    CALL;

    public static boolean isReadOperation(ActionType type) {
        if (type == QUERY_LIST || type == PAGE || type == QUERY_OBJECT) {
            return true;
        }

        return false;
    }

    public static boolean isWriteOperation(ActionType type) {
        return !isReadOperation(type);
    }

    public static boolean isPageOperation(ActionType type) {
        return (type == ActionType.PAGE);
    }

}
