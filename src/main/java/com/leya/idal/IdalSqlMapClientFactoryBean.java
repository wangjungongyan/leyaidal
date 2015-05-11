package com.leya.idal;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.engine.execution.SqlExecutor;
import com.ibatis.sqlmap.engine.impl.ExtendedSqlMapClient;
import com.ibatis.sqlmap.engine.impl.SqlMapExecutorDelegate;
import com.leya.idal.aop.SqlCutDbCutTableAdvice;
import com.leya.idal.aop.SqlLimitAdvice;
import com.leya.idal.aop.replace.MysqlSqlReplacer;
import com.leya.idal.aop.replace.SqlReplacer;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.core.io.Resource;
import org.springframework.orm.ibatis.SqlMapClientFactoryBean;
import org.springframework.util.Assert;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

public class IdalSqlMapClientFactoryBean extends SqlMapClientFactoryBean {

    private SqlReplacer sqlReplacer;

    protected SqlMapClient buildSqlMapClient(Resource[] configLocations, Resource[] mappingLocations,
                                             Properties properties) throws IOException {

        SqlMapClient client = super.buildSqlMapClient(configLocations, mappingLocations, properties);
        SqlMapExecutorDelegate delegate = ((ExtendedSqlMapClient) client).getDelegate();

        try {
            setDeclaredFieldValue(delegate, "sqlExecutor", createSqlExecutor());
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        return client;
    }

    private SqlExecutor createSqlExecutor() {
        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        proxyFactoryBean.setTarget(new SqlExecutor());
        proxyFactoryBean.setProxyTargetClass(true);

        if (sqlReplacer == null) {
            proxyFactoryBean.addAdvice(0, new SqlLimitAdvice(new MysqlSqlReplacer()));
        } else {
            proxyFactoryBean.addAdvice(0, new SqlLimitAdvice(sqlReplacer));
        }

        proxyFactoryBean.addAdvice(1, new SqlCutDbCutTableAdvice());

        return (SqlExecutor) proxyFactoryBean.getObject();
    }

    private Field getDeclaredField(Class<?> clazz, String propertyName) throws NoSuchFieldException {
        Assert.notNull(clazz);
        Assert.hasText(propertyName);
        for (Class<?> superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {

            try {
                return superClass.getDeclaredField(propertyName);
            } catch (NoSuchFieldException e) {
            }
        }

        throw new NoSuchFieldException("No such field: " + clazz.getName() + '.' + propertyName);
    }

    private void setDeclaredFieldValue(Object object, String propertyName, Object newValue) throws NoSuchFieldException {

        Assert.notNull(object);
        Assert.hasText(propertyName);

        Field field = getDeclaredField(object.getClass(), propertyName);
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        try {
            field.set(object, newValue);
        } catch (IllegalAccessException e) {
            throw new NoSuchFieldException("No such field: " + object.getClass() + '.' + propertyName);
        } finally {
            field.setAccessible(accessible);
        }
    }

    public void setSqlReplacer(SqlReplacer sqlReplacer) {
        this.sqlReplacer = sqlReplacer;
    }

}
