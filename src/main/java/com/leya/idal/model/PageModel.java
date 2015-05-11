package com.leya.idal.model;

import java.io.Serializable;
import java.util.List;

public class PageModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录总数
     */
    private int               totalCount;

    /**
     * 记录明细
     */
    private List<?>           records;

    /**
     * 当前页码
     */
    private int               currentPage;

    /**
     * 每页显示记录数
     */
    private int               pageSize;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<?> getRecords() {
        return records;
    }

    public void setRecords(List<?> records) {
        this.records = records;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

}
