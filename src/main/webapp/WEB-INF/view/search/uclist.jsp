<%@ page language="java" contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
<title>DTS -- 会员与客户关系搜索列表页</title>
	<link rel="stylesheet" href="${contextPath}/static/bootstrap/dist/css/bootstrap.min.css">
	<link rel="stylesheet" href="${contextPath}/static/bootstrap/dist/css/jquery-ui.css"> 
	<link rel="stylesheet" href="${contextPath}/static/bootstrap/dist/css/style.css" >
	<link rel="stylesheet" href="${contextPath}/static/bootstrap/dist/css/font-awesome.min.css">
  	<link rel="stylesheet" href="${contextPath}/static/bootstrap/dist/css/jquery.onoff.css">
	<link rel="shortcut icon" href="${contextPath}/static/bootstrap/dist/img/favicon/favicon.png">
	<script src="${contextPath}/static/bootstrap/dist/js/respond.min.js"></script>
	<script src="${contextPath}/static/bootstrap/dist/js/jquery.js"></script>
	<script type="text/javascript">
		var conf = {"1":"通过","2":"激活","3":"作废"};
	
		function isNull(val){
			return undefined == val ? '':val;
		}
		
		//定义查询fun
		function doQueryBizListData(page){
			var params = {"action":"bizQuery","page":page};
			params["bizId"] = $('#searchBizType').val();
			params["field"] = $('#searchField').val();
			params["keyword"] = $('#keyword').val();
			params["status"] = $('#status').val();
			$.post('${contextPath}/biz.htm',params,function(data){
				var html = "";
				var result = data.currData;
				for(var i in result){
					var t = "";
					if(result[i].status<=2){t+="<button class='btn btn-danger' onclick='changeStatus("+result[i].id+",3)'>作废</button>";}
					if(result[i].status==3){t+="<button class='btn btn-info' onclick='changeStatus("+result[i].id+",1)'>通过</button>";}
					if(result[i].status==1 && result[i].biz_name.indexOf('金融')>-1){t+="&nbsp;&nbsp;<button class='btn btn-success' onclick='changeStatus("+result[i].id+",2)'>激活</button>";}
					html += "<tr><td>"+result[i].user_id+"</td><td>"+result[i].user_name+"</td><td>"+result[i].biz_id+"</td><td>"+result[i].biz_name+"</td><td>"+result[i].bill_id+"</td><td>"+result[i].bill_name+"</td><td>"+result[i].customer_id+"</td><td>"+result[i].customer_name+"</td><td>"+conf[result[i].status]+"</td><td>"+result[i].create_time+"</td><td>"+isNull(result[i].valid_date)+"</td><td>"+isNull(result[i].invalid_date)+"</td><td>"+result[i].operator+"</td><td>"+t+"</td></tr>";
				}
				$('#ucListBody').html(html);
				//分页html
				var pagination = "<li class='disabled'><a href='javascript:void(0)'>总记录数:"+data.totalElements+"</a></li>";
				if(data.first==true) pagination += "<li class='disabled'><a href='javascript:void(0)' aria-label='Previous'><span aria-hidden=true>上一页</span></a><li>";
				else pagination += "<li><a href='javascript:doQueryBizListData("+data.previousIndex+")' aria-label='Previous'><span aria-hidden=true>上一页</span></a><li>";
				if(data.pageLinkNumber>0){
					var betweenIndex = data.betweenIndex;
					for(var i=betweenIndex.beginIndex;i<=betweenIndex.endIndex;i++){
						if(i==data.pageIndex) pagination += "<li class='active'><a href='javascript:void(0)'>"+i+"</a></li>";
						else pagination += "<li><a href='javascript:doQueryBizListData("+i+")'>"+i+"</a></li>";
					}
				}
				if(data.last==true) pagination += "<li class='disabled'><a href='javascript:void(0)' aria-label='Next'><span aria-hidden=true>下一页</span></a><li>";
				else pagination += "<li><a href='javascript:doQueryBizListData("+data.nextIndex+")' aria-label='Next'><span aria-hidden=true>下一页</span></a><li>";
				$('#pagination').html(pagination);
			});
		}
	
		function loadBizSelectList(){
			$.post('${contextPath}/biz.htm',{"action":"bizMetaListAll"},function(data){
				if(data && data.length>0){
					$('#bizType').empty(); //先清空
					$('#searchBizType').empty();
					$('#searchBizType').append("<option value='0'>所有业务类型</option>");
					for(var i in data){
						$('#bizType').append("<option value='"+data[i].id+"'>"+data[i].name+"</option>"); //逐个追加到select
						$('#searchBizType').append("<option value='"+data[i].id+"'>"+data[i].name+"</option>");
					}
					$('#bizType').val(data[0].id); //选取第一个默认选中
					$('#searchBizType').val('0');
				}
			});
		}
		
		function clearForm(){
			$('#userId').val('');
			$('#userName').val('');
			$('#customerId').val('');
			$('#customerName').val('');
			$('#billId').val('');
			$('#billName').val('');
		}
		
		function changeStatus(id,status){
			$.post('${contextPath}/biz.htm',{"action":"bizStatusChange","id":id,"status":status},function(data){
				if(data && data=='1'){
					doQueryBizListData(1);
				}
			});
		}
		
		$(function(){
			
			//init
			doQueryBizListData(1);
			loadBizSelectList();
			
			//bindBtn
			$('#bindBtn').click(function(){
				$.post('${contextPath}/biz.htm',{
					"action":"bizBind",
					"userId":$('#userId').val(),
					"userName":$('#userName').val(),
					"billId":$('#billId').val(),
					"billName":$('#billName').val(),
					"customerId":$('#customerId').val(),
					"customerName":$('#customerName').val(),
					"bizId":$('#bizType').val(),
					"bizName":$("#bizType").find("option:selected").text()
				},function(data){
					if('1'== data){ //ok
						doQueryBizListData(1);
						clearForm();
					}
				});
			});
			
			$('#btnSearch').click(function(){
				doQueryBizListData(1);
			});
			
			$('#searchBizType').change(function(){
				doQueryBizListData(1);
			});
			
			$('#searchField').change(function(){
				doQueryBizListData(1);
			});
			
			$('#status').change(function(){
				doQueryBizListData(1);
			});
			
			if('${sessionScope.user.user_id}'=='18'){
				$('#quickBindDiv').show();
			}
		});
	</script>
</head>
<body id="ucListMain">

<jsp:include page="../common/header.jsp"></jsp:include>

<!-- Main content starts -->
<div class="content">

  	<jsp:include page="../common/left.jsp"></jsp:include>

  	<!-- Main bar start-->
  	<div class="mainbar">
      	<!-- Page heading -->
	      <div class="page-head">
	        <h2 class="pull-left"><i class="fa fa-list-alt"></i>会员客户关系搜索</h2>
	        <div class="bread-crumb pull-right">
	          <a href="javascript:void(0)"><i class="fa fa-home"></i> 首页</a> 
	          <span class="divider">/</span> 
	          <a href="javascript:void(0)" class="bread-current">会员客户关系搜索</a>
	        </div>
	        <div class="clearfix"></div>
	      </div>
		<!-- Page heading ends -->
      	
      	<!-- 主窗体  -->
      	<div id="alert-info-div" class="alert alert-success hidden" role="alert">
			<strong>提示：</strong><span id="alert-info-txt">操作成功！</span>
		</div>
      	<fieldset>
     	<div class="row-fluid" style="margin:20px;">
		<div class="span12">
			<table class="table table-condensed table-bordered" style="margin-top:2px;margin-bottom:1px;">
				<tr>
					<td width="5%" class="text-center" style="vertical-align:middle;"><label class="control-label">业务类型:</label></td>
					<td width="10%" class="text-center">
						<select class="form-control" id="searchBizType"></select>
					</td>
					<td width="5%" class="text-center" style="vertical-align:middle;"><label class="control-label">状态:</label></td>
					<td width="10%" class="text-center">
						<select class="form-control" id="status">
							<option value="0" selected="selected">所有状态</option>
							<option value="1">通过</option>
							<option value="2">激活</option>
							<option value="3">作废</option>
						</select>
					</td>
					<td width="5%" class="text-center" style="vertical-align:middle;"><label class="control-label">过滤条件:</label></td>
					<td width="10%" class="text-center">
						<select class="form-control" id="searchField">
							<option value="0" selected="selected">所有</option>
							<option value="customer_id">客户ID</option>
							<option value="customer_name">客户名称</option>
							<option value="bill_id">单据ID</option>
							<option value="bill_name">单据名称</option>
						</select>
					</td>
					<td width="10%" class="text-center">
						<input type="text" placeholder="请输入搜索条件" class="form-control" id="keyword" maxlength="16"/>
					</td>
					<td width="45%" class="text-left">
						<button type="button" id="btnSearch" class="btn btn-info btn-search">搜索</button>
					</td>
				</tr>
			</table>
			
			<table class="table table-condensed table-bordered table-hover" style="margin-top:2px;margin-bottom:1px;">
				<thead>
					<tr class="info">
						<th width="5%">会员ID</th>
						<th width="5%">会员名称</th>
						<th width="5%">业务ID</th>
						<th width="5%">业务名称</th>
						<th width="5%">单据ID</th>
						<th width="10%">单据名称</th>
						<th width="5%">CRM客户ID</th>
						<th width="10%">CRM客户名称</th>
						<th width="5%">状态</th>
						<th width="10%">创建时间</th>
						<th width="10%">有效时间</th>
						<th width="10%">过期时间</th>
						<th width="5%">操作人</th>
						<th width="10%">状态修改</th>
					</tr>
				</thead>
				<tbody id="ucListBody"></tbody>
				<tfoot>
					<tr>
						<td colspan="14" align="center">
							<ul id="pagination" style="margin:0px 0px 0px 0px;" class="pagination pagination-lg"></ul>
						</td>
					</tr>
				</tfoot>
			</table>
		</div>
		
		<!-- <div class="span12">
		<hr style="margin:5px 0px 5px 0px;"/>
		</div> -->
		
		<div class="span12" id="quickBindDiv" style="display:none;">
			<table class="table table-condensed table-bordered">
				<tr>
					<td colspan="4" class="text-center"><h3><b>快速关联</b></h3></td>
				</tr>
				<tr>
					<td width="20%" class="text-center"><label class="control-label"><b>会员ID</b></label></td>
					<td width="30%" class="text-center">
						<input type="text" placeholder="请输入会员ID" class="form-control" id="userId"/>
					</td>
					<td width="20%" class="text-center"><label class="control-label"><b>会员名称</b></label></td>
					<td width="30%" class="text-center">
						<input type="text" placeholder="请输入会员名称" class="form-control" id="userName"/>
					</td>
				</tr>
				<tr>
					<td class="text-center"><label class="control-label"><b>客户ID</b></label></td>
					<td class="text-center">
						<input type="text" placeholder="请输入客户ID" class="form-control" id="customerId"/>
					</td>
					<td class="text-center"><label class="control-label"><b>客户名称</b></label></td>
					<td class="text-center">
						<input type="text" placeholder="请输入客户名称" class="form-control" id="customerName"/>
					</td>
				</tr>
				<tr>
					<td class="text-center"><label class="control-label"><b>业务类型</b></label></td>
					<td class="text-center">
						<select class="form-control" id="bizType">
							<!-- load db bizList -->
						</select>
					</td>
					<td class="text-center">&nbsp;</td>
					<td class="text-center">&nbsp;</td>
				</tr>
				<tr>
					<td class="text-center"><label class="control-label"><b>单据ID</b></label></td>
					<td class="text-center">
						<input type="text" placeholder="请输入单据ID" class="form-control" id="billId"/>
					</td>
					<td class="text-center"><label class="control-label"><b>单据名称</b></label></td>
					<td class="text-center">
						<input type="text" placeholder="请输入单据名称" class="form-control" id="billName"/>
					</td>
				</tr>
				<tr>
					<td colspan="4" class="text-center">
						<button style="width:250px;" type="button" id="bindBtn" class="btn btn-success">关联</button>
					</td>
				</tr>
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