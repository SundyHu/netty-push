<%@ page language="java" contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
<title>DTS -- 未翻译成功查看页</title>
	<link rel="stylesheet" href="${contextPath}/static/bootstrap/dist/css/bootstrap.min.css">
	<link rel="stylesheet" href="${contextPath}/static/bootstrap/dist/css/jquery-ui.css"> 
	<link rel="stylesheet" href="${contextPath}/static/bootstrap/dist/css/style.css" >
	<link rel="stylesheet" href="${contextPath}/static/bootstrap/dist/css/font-awesome.min.css">
  	<link rel="stylesheet" href="${contextPath}/static/bootstrap/dist/css/jquery.onoff.css">
	<link rel="shortcut icon" href="${contextPath}/static/bootstrap/dist/img/favicon/favicon.png">
	<script src="${contextPath}/static/bootstrap/dist/js/respond.min.js"></script>
	<script src="${contextPath}/static/bootstrap/dist/js/jquery.js"></script>
	<script type="text/javascript">
	function doQueryData(page){
		$.post('${contextPath}/untrans.htm',{"page":page},function(data){
			var html = "";
			var result = data.currData;
			for(var i in result){
				html += "<tr><td>"+result[i].id+"</td><td>"+result[i].content+"</td><td>"+result[i].create_time+"</td></tr>";
			}
			$('#untrans_list').html(html);
			
			var pagination = "";
			if(data.first==true){
				pagination += "<li class='disabled'><a href='javascript:void(0)' aria-label='Previous'><span aria-hidden=true>上一页</span></a><li>";
			}
			else{
				pagination += "<li><a href='javascript:doQueryData("+data.previousIndex+")' aria-label='Previous'><span aria-hidden=true>上一页</span></a><li>";
			}
			
			if(data.pageLinkNumber>0){
				var betweenIndex = data.betweenIndex;
				for(var i=betweenIndex.beginIndex;i<=betweenIndex.endIndex;i++){
					if(i==data.pageIndex){
						pagination += "<li class='active'><a href='javascript:void(0)'>"+i+"</a></li>";
					}else{
						pagination += "<li><a href='javascript:doQueryData("+i+")'>"+i+"</a></li>";
					}
				}
			}
			
			if(data.last==true) pagination += "<li class='disabled'><a href='javascript:void(0)' aria-label='Next'><span aria-hidden=true>下一页</span></a><li>";
			else pagination += "<li><a href='javascript:doQueryData("+data.nextIndex+")' aria-label='Next'><span aria-hidden=true>下一页</span></a><li>";

			$('#pagination').html(pagination);
		});
	}
	
	doQueryData(1);
	
	</script>
</head>
<body>

<jsp:include page="../common/header.jsp"></jsp:include>

<!-- Main content starts -->
<div class="content">

  	<jsp:include page="../common/left.jsp"></jsp:include>

  	<!-- Main bar start-->
  	<div class="mainbar">
      	<!-- Page heading -->
	      <div class="page-head">
	        <h2 class="pull-left"><i class="fa fa-list-alt"></i>未翻译成功查看</h2>
	        <div class="bread-crumb pull-right">
	          <a href="javascript:void(0)"><i class="fa fa-home"></i> 首页</a> 
	          <span class="divider">/</span> 
	          <a href="javascript:void(0)" class="bread-current">未翻译成功查看</a>
	        </div>
	        <div class="clearfix"></div>
	      </div>
		<!-- Page heading ends -->
      	
      	<!-- 主窗体  -->
      	<fieldset>
      		<div class="row-fluid" style="margin:20px;">
			<div class="span12">
				<table class="table table-condensed table-bordered table-hover" style="margin-top:8px;">
					<thead>
						<tr class="info">
							<th width="10%">ID</th>
							<th width="60%">翻译失败记录内容</th>
							<th width="30%">请求翻译时间&darr;</th>
						</tr>
					</thead>
					<tbody id="untrans_list"></tbody>
					<tfoot>
						<tr><td colspan="3" align="center">
						<ul id="pagination" class="pagination pagination-lg"></ul>
						</td></tr>
					</tfoot>
				</table>
			</div>
			</div>
      	</fieldset>
      	
	</div>
	<!-- Main bar end -->

</div>
<!-- Main Content ends -->

<script src="${contextPath}/static/bootstrap/dist/js/bootstrap.min.js"></script>
<script src="${contextPath}/static/bootstrap/dist/js/jquery-ui.min.js"></script>
</body>
</html>