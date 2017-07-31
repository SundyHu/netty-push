<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<div class="sidebar" style="z-index:0">
	<ul id="nav">
      <li id="index_htm"><a href="${contextPath}/index.htm"><i class="fa fa-home"></i> 首页</a></li>
      <!-- 此项菜单前期不需要，暂时先注掉 -->
      <%-- <li id="utoc_htm"><a href="${contextPath}/search.htm?view=utoc"><i class="fa fa-list-alt"></i> 会员~客户关联</a></li> --%>
      <li id="ctof_htm"><a href="${contextPath}/search.htm?view=ctof"><i class="fa fa-list-alt"></i> 客户~法人关联</a></li>
      <li id="gtop_htm"><a href="${contextPath}/search.htm?view=gtop"><i class="fa fa-list-alt"></i> 货物~产品关联</a></li>
      <li id="goods_htm"><a href="${contextPath}/search.htm?view=goods"><i class="fa fa-list-alt"></i> 产品快速关联</a></li>
      <li id="uclist_htm"><a href="${contextPath}/search.htm?view=uclist"><i class="fa fa-list-alt"></i> 会员~客户列表</a></li>
      <li id="bizset_htm"><a href="${contextPath}/search.htm?view=bizset"><i class="fa fa-list-alt"></i> 业务定义管理</a></li>
      <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
      <c:if test="${sessionScope.user.user_id==18}">
		<li id="translate_htm"><a href="${contextPath}/translate.htm"><i class="fa fa-list-alt"></i> 数据翻译测试</a></li>
		<li id="untrans_htm"><a href="${contextPath}/untrans.htm"><i class="fa fa-list-alt"></i> 未翻译成功查看</a></li>
      </c:if>
	</ul>
</div>
<script type="text/javascript">
var current = "${currentView}";
$('#'+current).addClass("open");
</script>

