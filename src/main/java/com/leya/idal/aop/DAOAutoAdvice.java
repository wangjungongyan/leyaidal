package com.leya.idal.aop;

import com.alibaba.fastjson.JSON;
import com.leya.idal.IGenericDao;
import com.leya.idal.annotation.Action;
import com.leya.idal.annotation.NameSpace;
import com.leya.idal.annotation.ParamName;
import com.leya.idal.constant.ExceptionConstant;
import com.leya.idal.constant.PageConstant;
import com.leya.idal.enums.ActionType;
import com.leya.idal.model.DAOMethod;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.IntroductionInterceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

public class DAOAutoAdvice implements IntroductionInterceptor {

    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();

        ActionType actionType = getActionType(method);
        DAOMethod daoMethod = instanceDAOMethod(invocation);
        IGenericDao genericDao = (IGenericDao) invocation.getThis();

        // 此处需根据传入的参数列表进行分组，分组的维度是“表维度”

        if (actionType == ActionType.QUERY_LIST) {
            return genericDao.executeQuery(daoMethod);
        }

        if (actionType == ActionType.QUERY_OBJECT) {
            return genericDao.executeLoad(daoMethod);
        }

        if (actionType == ActionType.INSERT) {
            return genericDao.executeInsert(daoMethod);
        }

        if (actionType == ActionType.UPDATE) {
            return genericDao.executeUpdate(daoMethod);
        }

        if (actionType == ActionType.DELETE) {
            return genericDao.executeDelete(daoMethod);
        }

        if (actionType == ActionType.PAGE) {
            return genericDao.executePageQuery(daoMethod);
        }

        if (actionType == ActionType.CALL) {
            return genericDao.executeCall(daoMethod);
        }

        throw new Exception(ExceptionConstant.ILLEGAL_ACTION);
    }

    private ActionType getActionType(Method method) {
        Action action = method.getAnnotation(Action.class);
        ActionType actionType = action.action();
        return actionType;
    }

    public boolean implementsInterface(@SuppressWarnings("rawtypes")
                                       Class intf) {
        return intf.isInterface();
    }

    private DAOMethod instanceDAOMethod(MethodInvocation invocation) {
        Method method = invocation.getMethod();
        Object[] arguments = invocation.getArguments();

        Action action = method.getAnnotation(Action.class);
        ActionType actionType = action.action();

        DAOMethod daoMethod = new DAOMethod(method.getName(), actionType, action.foreWrite());
        daoMethod.addParams(getParamsMap(method, arguments));

        if (ActionType.isPageOperation(actionType)) {
            fillPageNoAndPageSize(daoMethod);
        }

        daoMethod.setNamespace(getNameSpace(invocation));

        return daoMethod;
    }

    private void fillPageNoAndPageSize(DAOMethod daoMethod) {
        Map<String, Object> daoParams = daoMethod.getParams();

        if (!daoParams.containsKey(PageConstant.PAGENO)) {
            throw new RuntimeException(ExceptionConstant.ILLEGAL_PAGENO);
        }

        if (!daoParams.containsKey(PageConstant.PAGE_SIZE)) {
            throw new RuntimeException(ExceptionConstant.ILLEGAL_PAGE_SIZE);
        }

        int pageNo = (Integer) daoParams.get(PageConstant.PAGENO);
        int pageSize = (Integer) daoParams.get(PageConstant.PAGE_SIZE);

        if (pageNo <= 0) {
            throw new RuntimeException(ExceptionConstant.ILLEGAL_PAGENO);
        }

        if (pageSize <= 0) {
            throw new RuntimeException(ExceptionConstant.ILLEGAL_PAGE_SIZE);
        }

        int skipResults = (pageNo - 1) * pageSize;
        int maxResults = pageSize;

        daoMethod.addParam(PageConstant.SKIP_RESULTS, skipResults);
        daoMethod.addParam(PageConstant.MAX_RESULTS, maxResults);
    }

    private Map<String, Object> getParamsMap(Method method, Object[] arguments) {
        Map<String, Object> paramsMap = new HashMap<String, Object>();

        Class<?>[] paramTypes = method.getParameterTypes();

        if (paramTypes == null) {
            return paramsMap;
        }
        int paramLength = paramTypes.length;

        if (paramLength == 0) {
            return paramsMap;
        }

        Annotation[][] paramAnnos = method.getParameterAnnotations();

        for (int i = 0; i < paramLength; i++) {
            Annotation[] anno = paramAnnos[i];

            for (int j = 0; j < anno.length; j++) {
                ParamName p = (ParamName) anno[j];
                paramsMap.put(p.value(), arguments[i]);
            }
        }

        return paramsMap;
    }

    private String getNameSpace(MethodInvocation invocation) {
        Class declaringClass = invocation.getMethod().getDeclaringClass();
        NameSpace classAnnotation = (NameSpace) declaringClass.getAnnotation(NameSpace.class);
        String completeClassName = declaringClass.getName();

        if (classAnnotation != null) {
            return classAnnotation.value();
        }

        return completeClassName.substring(completeClassName.lastIndexOf(".") + 1, completeClassName.length());

    }

    public static void main(String[]dd){
        List s = new ArrayList<Date>();
        Date now =  new Date();
        s.add(new SimpleDateFormat("yyyy-MM-dd").format(now));
        System.out.print(JSON.toJSONString(s));
    }

    private void d(int x,String...d){

    }

}
