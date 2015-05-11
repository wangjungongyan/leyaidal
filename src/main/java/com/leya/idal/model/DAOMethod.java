package com.leya.idal.model;

import java.util.HashMap;
import java.util.Map;

import com.leya.idal.enums.ActionType;

public class DAOMethod {

    /**
     * DAO method name
     */
    private final String name;

    /**
     * DAO action namespace
     */
    private String namespace;

    /**
     * DAO action type
     */
    public final ActionType actionType;

    /**
     * DAO action foreWirte
     */
    public final boolean foreWirte;

    /**
     * SQL parameters for statement
     */
    private Map<String, Object> params = new HashMap<String, Object>();

    /**
     * if specified call method result type {@link CallResult}
     */
    private boolean isCallResult;

    /**
     * Constructor
     */
    public DAOMethod(String name, ActionType actionType, boolean foreWirte) {
        this.name = name;
        this.actionType = actionType;
        this.foreWirte = foreWirte;
    }

    public String getName() {
        return name;
    }

    public void addParam(String name, Object value) {
        this.params.put(name, value);
    }

    public void addParams(Map<String, Object> params) {
        if (params != null) {
            this.params.putAll(params);
        }
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public boolean isCallResult() {
        return isCallResult;
    }

    public void setCallResult(boolean isCallResult) {
        this.isCallResult = isCallResult;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public boolean isForeWirte() {
        return foreWirte;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
