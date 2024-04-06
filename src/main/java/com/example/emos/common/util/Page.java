package com.example.emos.common.util;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Page implements Serializable {

    private static final long serialVersionUID = 1165315506937599491L;

    /**
     * 当前页
     */
    private int pageIndex;

    /**
     * 每页展示的记录数
     */
    private int pageSize;

    /**
     * 总的记录数
     */
    private long totalCount;

    /**
     * 分页数据
     */
    private List<HashMap<String, Object>> list;

    public Page(int pageIndex, int pageSize, long totalCount, List<HashMap<String, Object>> list) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.list = list;
    }
}
