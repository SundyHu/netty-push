package com.molbase.dts.server.service.pagination;

public interface BetweenIndex {
	/**
     * 获取开始分页链接索引
     * @return
     */
    int getBeginIndex();
    /**
     * 获取结束分页链接索引
     * @return
     */
    int getEndIndex();
}
