package com.molbase.dts.server.service.pagination;

import java.util.List;

public interface Pagination<T> {
	/**
     * 判断是否是首页
     * @return
     */
    boolean isFirst();
    /**
     * 判断是否是尾页
     * @return
     */
    boolean isLast();
    /**
     * 判断是否有上一页
     * @return
     */
    boolean isPrevious();
    /**
     * 判断是否有下一页
     * @return
     */
    boolean isNext();
    
    
    /**
     * 获取上一页的页码
     * @return
     */
    int getPreviousIndex();
    /**
     * 获取下一页的页码
     * @return
     */
    int getNextIndex();
    /**
     * 获取当前页码
     * @return
     */
    int getPageIndex();
    /**
     * 获取当前页大小
     * @return
     */
    int getPageSize();
    /**
     * 获取总页数
     * @return
     */
    int getTotalPages();
    /**
     * 获取数据总行数
     * @return
     */
    int getTotalElements();
    
    /**
     * 获取当前页的数据
     * @return
     */
    List<T> getCurrData();
    
    void setCurrData(List<T> currData);
    
    /**
     * 获取数字分页链接对象
     * @return
     */
    BetweenIndex getBetweenIndex();
    
    /**
     * 获取每页显示的分页链接数
     * @return
     */
    int getPageLinkNumber();
    
    /**
     * 设置每页的分页链接数量
     * @param pageLinkNumber
     */
    void setPageLinkNumber(int pageLinkNumber);
}
