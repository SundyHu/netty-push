package com.molbase.dts.server.service.pagination;

import java.util.List;

public interface QueryHandler<T> {

	int getTotalElements();
    
    /**
     * 获取当前页的数据
     * @param pageIndex
     * @param pageSize
     * @return
     */
    List<T> getCurrData(int pageIndex, int pageSize);
}
