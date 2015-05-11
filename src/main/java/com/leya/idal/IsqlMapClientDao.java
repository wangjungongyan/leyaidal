package com.leya.idal;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.leya.idal.enums.ActionType;
import org.springframework.dao.support.DaoSupport;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.util.Map;

public class IsqlMapClientDao extends DaoSupport {

    private SqlMapClientTemplate  sqlMapClientTemplate = new SqlMapClientTemplate();

    private boolean               externalTemplate     = false;

    // 读对应的SqlMapClient
    protected SqlMapClient        write;

    // 写对应的SqlMapClient列表
    protected Map<String, String> reads;

    // 对于分库分表操作，需设置数据库顺序
    protected String              indexedDbs;

    protected SqlMapClient selectSqlMapClient(ActionType type) {
        return null;
    }

    /**
     * Set the JDBC DataSource to be used by this DAO. Not required: The SqlMapClient might carry a shared DataSource.
     * 
     * @see #setSqlMapClient
     */
    public final void setDataSource(DataSource dataSource) {
        if (!this.externalTemplate) {
            this.sqlMapClientTemplate.setDataSource(dataSource);
        }
    }

    /**
     * Return the JDBC DataSource used by this DAO.
     */
    public final DataSource getDataSource() {
        return this.sqlMapClientTemplate.getDataSource();
    }

    /**
     * Set the iBATIS Database Layer SqlMapClient to work with. Either this or a "sqlMapClientTemplate" is required.
     * 
     * @see #setSqlMapClientTemplate
     */
    public final void setSqlMapClient(SqlMapClient sqlMapClient) {
        if (!this.externalTemplate) {
            this.sqlMapClientTemplate.setSqlMapClient(sqlMapClient);
        }
    }

    /**
     * Return the iBATIS Database Layer SqlMapClient that this template works with.
     */
    public final SqlMapClient getSqlMapClient() {
        return this.sqlMapClientTemplate.getSqlMapClient();
    }

    /**
     * Set the SqlMapClientTemplate for this DAO explicitly, as an alternative to specifying a SqlMapClient.
     * 
     * @see #setSqlMapClient
     */
    public final void setSqlMapClientTemplate(SqlMapClientTemplate sqlMapClientTemplate) {
        Assert.notNull(sqlMapClientTemplate, "SqlMapClientTemplate must not be null");
        this.sqlMapClientTemplate = sqlMapClientTemplate;
        this.externalTemplate = true;
    }

    /**
     * Return the SqlMapClientTemplate for this DAO, pre-initialized with the SqlMapClient or set explicitly.
     */
    public final SqlMapClientTemplate getSqlMapClientTemplate() {
        return this.sqlMapClientTemplate;
    }

    protected final void checkDaoConfig() {
        // check write and reads dataSource
    }

    public SqlMapClient getWrite() {
        return write;
    }

    public void setWrite(SqlMapClient write) {
        this.write = write;
    }

    public Map<String, String> getReads() {
        return reads;
    }

    public void setReads(Map<String, String> reads) {
        this.reads = reads;
    }

    public String getIndexedDbs() {
        return indexedDbs;
    }

    public void setIndexedDbs(String indexedDbs) {
        this.indexedDbs = indexedDbs;
    }

}
