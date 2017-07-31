package com.molbase.dts.server.service.pagination;

import java.util.List;

public class DefaultPagination<T> implements Pagination<T> {
	private int totalElements;
	private int pageSize;
	private int totalPages;
	private int pageIndex;
	private QueryHandler<T> queryHandler;
	private List<T> currData;
	private int pageLinkNumber;

	public DefaultPagination(int pageIndex, int pageSize,
			QueryHandler<T> queryHandler, int pageLinkNumber) {
		this(pageIndex, pageSize, queryHandler);
		setPageLinkNumber(pageLinkNumber);
	}

	public DefaultPagination(int pageIndex, int pageSize,
			QueryHandler<T> queryHandler) {
		// 初始化数据访问回调接口
		this.queryHandler = queryHandler;
		// 查询总行数
		setTotalElements();
		// 修正页大小
		setPageSize(pageSize);
		// 计算总页数:
		setTotalPages();
		// 修正页码
		setPageIndex(pageIndex);
		// 查询当前页数据
		setCurrData();
	}

	private void setCurrData() {
		// TODO Auto-generated method stub
		this.currData = queryHandler.getCurrData(pageIndex, pageSize);
	}
	
	public void setCurrData(List<T> currData) {
		// TODO Auto-generated method stub
		this.currData = currData;
	}

	private void setPageIndex(int pageIndex) {
		// TODO Auto-generated method stub
		if (pageIndex < 1) {
			this.pageIndex = 1;
		} else if (pageIndex > totalPages) {
			this.pageIndex = totalPages;
		} else {
			this.pageIndex = pageIndex;
		}
	}

	private void setTotalPages() {
		// TODO Auto-generated method stub
		if (pageSize > 0) {
			/*
			 * //普通算法: this.totalPages = totalElements % pageSize == 0 ?
			 * totalElements / pageSize : (totalElements / pageSize) + 1;
			 */
			// 减一公式:
			this.totalPages = (totalElements + pageSize - 1) / pageSize;
		}
	}

	private void setPageSize(int pageSize) {
		// TODO Auto-generated method stub
		if (pageSize < 1) {
			this.pageSize = 1;
		} else if (pageSize > totalElements) {
			this.pageSize = totalElements;
		} else {
			this.pageSize = pageSize;
		}
	}

	private void setTotalElements() {
		// TODO Auto-generated method stub
		this.totalElements = queryHandler.getTotalElements();
	}

	@Override
	public boolean isFirst() {
		// TODO Auto-generated method stub
		return pageIndex == 1;
	}

	@Override
	public boolean isLast() {
		// TODO Auto-generated method stub
		return pageIndex == totalPages;
	}

	@Override
	public boolean isPrevious() {
		// TODO Auto-generated method stub
		return pageIndex > 1;
	}

	@Override
	public boolean isNext() {
		// TODO Auto-generated method stub
		return pageIndex < totalPages;
	}

	@Override
	public int getPreviousIndex() {
		// TODO Auto-generated method stub
		return isPrevious() ? pageIndex - 1 : 1;
	}

	@Override
	public int getNextIndex() {
		// TODO Auto-generated method stub
		return isNext() ? pageIndex + 1 : totalPages;
	}

	@Override
	public int getPageIndex() {
		// TODO Auto-generated method stub
		return pageIndex;
	}

	@Override
	public int getPageSize() {
		// TODO Auto-generated method stub
		return pageSize;
	}

	@Override
	public int getTotalPages() {
		// TODO Auto-generated method stub
		return totalPages;
	}

	@Override
	public int getTotalElements() {
		// TODO Auto-generated method stub
		return totalElements;
	}

	@Override
	public List<T> getCurrData() {
		// TODO Auto-generated method stub
		return currData;
	}

	@Override
	public BetweenIndex getBetweenIndex() {
		// TODO Auto-generated method stub
		return new BetweenIndex() {
			private int beginIndex;
			private int endIndex;
			{
				boolean isOdd = pageLinkNumber % 2 == 0;
				int val = pageLinkNumber / 2;
				beginIndex = pageIndex - (isOdd ? val - 1 : val);
				endIndex = pageIndex + val;
				if (beginIndex < 1) {
					beginIndex = 1;
					endIndex = pageLinkNumber;
				}
				if (endIndex > totalPages) {
					endIndex = totalPages;
					beginIndex = endIndex - pageLinkNumber + 1;
				}
			}

			@Override
			public int getEndIndex() {
				// TODO Auto-generated method stub
				return endIndex;
			}

			@Override
			public int getBeginIndex() {
				// TODO Auto-generated method stub
				return beginIndex;
			}
		};
	}

	@Override
	public int getPageLinkNumber() {
		// TODO Auto-generated method stub
		return pageLinkNumber;
	}

	@Override
	public void setPageLinkNumber(int pageLinkNumber) {
		// TODO Auto-generated method stub
		if (pageLinkNumber < 0) {
			this.pageLinkNumber = 0;
		} else if (pageLinkNumber > totalPages) {
			this.pageLinkNumber = totalPages;
		} else {
			this.pageLinkNumber = pageLinkNumber;
		}
	}
}
