<%@ page language="java" contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
<title>DTS -- 数据翻译测试页</title>
	<link rel="stylesheet" href="${contextPath}/static/bootstrap/dist/css/bootstrap.min.css">
	<link rel="stylesheet" href="${contextPath}/static/bootstrap/dist/css/jquery-ui.css"> 
	<link rel="stylesheet" href="${contextPath}/static/bootstrap/dist/css/style.css" >
	<link rel="stylesheet" href="${contextPath}/static/bootstrap/dist/css/font-awesome.min.css">
  	<link rel="stylesheet" href="${contextPath}/static/bootstrap/dist/css/jquery.onoff.css">
	<link rel="shortcut icon" href="${contextPath}/static/bootstrap/dist/img/favicon/favicon.png">
	
	<script src="${contextPath}/static/bootstrap/dist/js/respond.min.js"></script>
	<script src="${contextPath}/static/bootstrap/dist/js/jquery.js"></script>
	<script type="text/javascript">
		$(function(){
			$('#doTranslate').click(function(){
				$.post('${contextPath}/api/index.htm',{
					"action":"translate",
					"metaTypeStr":$('#source_type').val()+"@"+$('#target_type').val(),
					"sourceId":$('#sourceId').val(),
				},function(result){
					$('#show_result').text(JSON.stringify(result));
				});
			});
		});
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
	        <h2 class="pull-left"><i class="fa fa-list-alt"></i>数据翻译测试</h2>
	        <div class="bread-crumb pull-right">
	          <a href="javascript:void(0)"><i class="fa fa-home"></i> 首页</a> 
	          <span class="divider">/</span> 
	          <a href="javascript:void(0)" class="bread-current">数据翻译测试</a>
	        </div>
	        <div class="clearfix"></div>
	      </div>
		<!-- Page heading ends -->
      	
      	<!-- 主窗体  -->
      	<fieldset>
      		<div class="row-fluid" style="margin:20px;">
			<div class="span12">
				参数填写:&nbsp;&nbsp;<br/>
				源实体:
				<select id="source_type">
					<option value="user" selected="selected">主站会员用户</option>
					<option value="customer">CRM客户</option>
					<option value="u8_buyer">U8采购商</option>
					<option value="u8_supplier">U8供应商</option>
					<option value="molbase_goods">MOLBASE产品</option>
					<option value="u8_product">U8产品</option>
				</select>&nbsp;&nbsp;
				目标实体:
				<select id="target_type">
					<option value="user">主站会员用户</option>
					<option value="customer" selected="selected">CRM客户</option>
					<option value="u8_buyer">U8采购商</option>
					<option value="u8_supplier">U8供应商</option>
					<option value="molbase_goods">MOLBASE产品</option>
					<option value="u8_product">U8产品</option>
				</select>
				<br/>
				源实体ID:
				<input id="sourceId" placeholder="请输入需要翻译的实体ID"/>&nbsp;&nbsp;
				<button type="button" id="doTranslate" class="btn btn-info btn-search">执行翻译</button>
				<br/>
				<hr/>
				翻译结果:
				<textarea id="show_result" rows="6" cols="100" readonly="readonly"></textarea>
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