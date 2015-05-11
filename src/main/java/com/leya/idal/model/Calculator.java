package com.leya.idal.model;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Calculator {

    private static ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("JavaScript");

    public static int eval2intValue(String rule) {
        try {
            return ((Double) scriptEngine.eval(rule)).intValue();
        } catch (ScriptException e) {
            throw new RuntimeException("Caculate index error. After replace placeholder, rule is " + rule + e);
        }
    }

}
