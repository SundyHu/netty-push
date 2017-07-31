<%@ page language="java" contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
<title>DTS -- 会员关联客户搜索页</title>
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
			$('#bindStatus').change(function(){
				if('1'==$(this).val()){
					$('#binded').show();
					$('#nobind').hide();
				}else{
					$('#binded').hide();
					$('#nobind').show();
				}
			});
			
			$('#utoc_user_search').click(function(){
				$('#utoc_userList').html('');
				if($('#utoc_userId').val()!='' || $('#utoc_userName').val()!=''){
					$.post('${contextPath}/search.htm?view=utoc&action=noBindSearch',{"metaType":"user","object_id":$('#utoc_userId').val(),"object_name":$('#utoc_userName').val()},function(result){
						if(result.length>0){
							var html = "";
							for(var i in result){
								html += "<tr id='user_"+result[i].name1+"'><td><input type='radio' name='ucBind_userId' onclick=doValueMove('doBind_userId','"+result[i].name1+"','user_') value='"+result[i].name1+"' /></td><td>"+result[i].name1+"</td><td>"+result[i].name2+"</td></tr>";
							}
							$('#utoc_userList').html(html);
						}else{
							$("#alert-noresult-div").removeClass("hidden");
							setTimeout('$("#alert-noresult-div").addClass("hidden")',3000);
						}
					});
				}
			});
			
			$('#utoc_customer_search').click(function(){
				$('#utoc_customerList').html('');
				if(($('#utoc_customerId').val()=='' && ''==$('#utoc_customerName').val()) == false){
					$.post('${contextPath}/search.htm?view=utoc&action=noBindSearch',{"metaType":"customer","object_id":$('#utoc_customerId').val(),"object_name":$('#utoc_customerName').val()},function(result){
						if(result.length>0){
							var html = "";
							for(var i in result){
								html += "<tr id='customer_"+result[i].name1+"'><td><input type='radio' name='ucBind_customerId' onclick=doValueMove('doBind_customerId','"+result[i].name1+"','customer_') value='"+result[i].name1+"' /></td><td>"+result[i].name1+"</td><td>"+result[i].name2+"</td></tr>";
							}
							$('#utoc_customerList').html(html);
						}else{
							$("#alert-noresult-div").removeClass("hidden");
							setTimeout('$("#alert-noresult-div").addClass("hidden")',3000);
						}
					});
				}
			});
			
			$('#doBindBtn').click(function(){
				if($('#doBind_userId').val()!='' && $('#doBind_customerId').val()!=''){
					var params = {
						"metaTypeStr":"user@customer",
						"source_id":$('#doBind_userId').val(),
						"target_id":$('#doBind_customerId').val()
					};
					$.post('${contextPath}/search.htm?view=utoc&action=doBind',params,function(result){
						//alert("操作绑定结果："+result);
						$("#alert-info-div").removeClass("hidden");
						setTimeout('$("#alert-info-div").addClass("hidden")',2500);
						//清空前面查询到的数据
						$("input[id^=utoc_]").val('');
						$("input[id^=doBind_]").val('');
						$('#doBindBtn').toggleClass('disabled',true);
						$('#utoc_customerList').html('');
						$('#utoc_userList').html('');
						//重新查询绑定的内容
						doQueryBindData(1);
					});
				}
			});
			
			$('#utoc_bindSearch').click(function(){
				doQueryBindData(1);
			});
			
			doQueryBindData(1);
		});
		
		function doQueryBindData(page){
			$.post('${contextPath}/search.htm?view=utoc',{
				"action":"bindedSearch",
				"source_type":"user",
				"query_bind_id":$('#query_bind_id').val(),
				"query_bind_name":$('#query_bind_name').val(),
				"page":page,
			},function(data){
				var html = "";
				var result = data.currData;
				$("#totalElements").html(data.totalElements);
				for(var i in result){
					html += "<tr><td>"+result[i].source_object_id+"</td><td>"+result[i].source_object_name+"</td><td>"+result[i].target_object_id+"</td><td>"+result[i].target_object_name+"</td><td><span class='text text-success'>已关联</span></td><td>"+result[i].oper_user+"</td><td>"+result[i].last_update_time+"</td><td><a href='javascript:void(0)' onclick='delBindMap(this)' class='btn btn-danger' mapId='"+result[i].id+"'>删除绑定关系</a></td></tr>";
				}
				$('#utoc_bindList').html(html);
				
				var pagination = "";
				if(data.first==true){
					pagination += "<li class='disabled'><a href='javascript:void(0)' aria-label='Previous'><span aria-hidden=true>上一页</span></a><li>";
				}
				else{
					pagination += "<li><a href='javascript:doQueryBindData("+data.previousIndex+")' aria-label='Previous'><span aria-hidden=true>上一页</span></a><li>";
				}
				
				if(data.pageLinkNumber>0){
					var betweenIndex = data.betweenIndex;
					for(var i=betweenIndex.beginIndex;i<=betweenIndex.endIndex;i++){
						if(i==data.pageIndex){
							pagination += "<li class='active'><a href='javascript:void(0)'>"+i+"</a></li>";
						}else{
							pagination += "<li><a href='javascript:doQueryBindData("+i+")'>"+i+"</a></li>";
						}
					}
				}
				
				if(data.last==true) pagination += "<li class='disabled'><a href='javascript:void(0)' aria-label='Next'><span aria-hidden=true>下一页</span></a><li>";
				else pagination += "<li><a href='javascript:doQueryBindData("+data.nextIndex+")' aria-label='Next'><span aria-hidden=true>下一页</span></a><li>";

				$('#pagination').html(pagination);
			});
		}
		
		function delBindMap(obj){
			delMapId = $(obj).attr("mapId");
			$('#delConfirmModal').modal("show");
		}
	
		function executeDelBindMap(){
			$('#delConfirmModal').modal("hide");
			$.ajax({
				url:"${contextPath}/search.htm?action=deleteMap",
				type:"POST",
				dataType:"json",
				data:{"mapId":delMapId},
				success:function(result){
					$("#alert-info-div").removeClass("hidden");
					setTimeout('$("#alert-info-div").addClass("hidden")',2500);
					doQueryBindData(1);
				}
			});
		}
		
		function doValueMove(inputId,id,tab){
			$('#'+inputId).val(id);
			$('tr[id^='+tab+']').removeClass('tr_gc');
			$('#'+tab+''+id).addClass('tr_gc');
			var bindInputs = $("input[id^=doBind_]");
			if($(bindInputs[0]).val()!='' && $(bindInputs[1]).val()!=''){
				//两个框的值都有了，先看看是否存在有绑定关系了~
				$.post('${contextPath}/search.htm?view=utoc&action=findMapExists',{"metaTypeStr":"user@customer","source_id":$('#doBind_userId').val()},
					function(result){
					if(true==result){//存在啦!
						$("#alert-error-div").removeClass("hidden");
						setTimeout('$("#alert-error-div").addClass("hidden")',3000);
						$('#doBindBtn').toggleClass('disabled',true);
					}else{
						$('#doBindBtn').toggleClass('disabled',false);
					}
				});
			}
		}
		
		var delMapId=0;
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
	        <h2 class="pull-left"><i class="fa fa-list-alt"></i>会员客户搜索</h2>
	        <div class="bread-crumb pull-right">
	          <a href="javascript:void(0)"><i class="fa fa-home"></i> 首页</a> 
	          <span class="divider">/</span> 
	          <a href="javascript:void(0)" class="bread-current">会员客户搜索</a>
	        </div>
	        <div class="clearfix"></div>
	      </div>
		<!-- Page heading ends -->
      	
      	<!-- 主窗体  -->
      	<div id="alert-info-div" class="alert alert-success hidden" role="alert">
			<strong>提示：</strong><span id="alert-info-txt">操作成功！</span>
		</div>
		<div id="alert-error-div" class="alert alert-danger hidden" role="alert">
			<strong>警告：</strong><span id="alert-error-txt">已经存在绑定关系！</span>
		</div>
		<div id="alert-noresult-div" class="alert alert-danger hidden" role="alert">
			<strong>提示：</strong><span id="alert-noresult-txt">无搜索结果返回！</span>
		</div>
      	<fieldset>
      		<div class="row-fluid" style="margin:20px;">
			<div class="span12">
				关联状态选择：&nbsp;&nbsp;
				<select id="bindStatus">
					<option value="1" selected="selected">已关联</option>
					<option value="0">未关联</option>
				</select>
				<hr/>
				<div id="binded">
					搜索条件：&nbsp;&nbsp;
					<input type="number" min="1" placeholder="请输入会员ID" id="query_bind_id" name="query_bind_id"/>&nbsp;&nbsp;
					<input type="text" placeholder="请输入会员名称" id="query_bind_name" name="query_bind_name" maxlength="16"/>&nbsp;&nbsp;
					<button type="button" id="utoc_bindSearch" class="btn btn-info btn-search">搜索</button>
					&nbsp;&nbsp;已关联总数：<span id="totalElements"></span>
					<table class="table table-condensed table-bordered table-hover" style="margin-top:8px;">
						<thead>
							<tr class="info">
								<th width="10%">会员ID</th>
								<th width="15%">会员名称</th>
								<th width="10%">CRM客户ID</th>
								<th width="15%">CRM客户名称</th>
								<th width="10%">状态</th>
								<th width="5%">操作人</th>
								<th width="20%">关联时间</th>
								<th width="15%">操作</th>
							</tr>
						</thead>
						<tbody id="utoc_bindList">
							<%-- <c:forEach var="item" items="${bindList}">
							<tr>
								<td>${item.source_object_id }</td>
								<td>${item.source_object_name }</td>
								<td>${item.target_object_id }</td>
								<td>${item.target_object_name }</td>
								<td><span class="text text-success">已关联</span></td>
								<td>${item.last_update_time }</td>
								<td><a href="javascript:void(0)" onclick="delBindMap(this)" class="btn btn-danger" mapId="${item.id}">删除绑定关系</a></td>
							</tr>
							</c:forEach> --%>
						</tbody>
						<tfoot>
							<tr>
							<td colspan="8" align="center">
							<ul id="pagination" class="pagination pagination-lg">
		                        <!-- 
		                        <li><a href="javascript:doQueryBindData(1)">首页</a></li>
		                        <li><a href="javascript:doQueryBindData(1)">1</a></li>
		                        <li><a href="javascript:doQueryBindData(2)" class="disabled">2</a></li>
		                        <li><a href="javascript:doQueryBindData(3)">3</a></li>
		                        <li><a href="javascript:doQueryBindData(4)">4</a></li>
		                        <li><a href="javascript:doQueryBindData(5)">最后一页</a></li>
		                         -->
							</ul>
							</td>
							</tr>
						</tfoot>
					</table>
				</div>
				
				<div id="nobind" style="display:none;">
					会员信息搜索条件：&nbsp;&nbsp;
					<input id="utoc_userId" type="number" min="1" placeholder="请输入主站会员ID"/>&nbsp;&nbsp;
					<input id="utoc_userName" type="text" placeholder="请输入主站会员名称" maxlength="16"/>&nbsp;&nbsp;
					<button type="button" id="utoc_user_search" class="btn btn-info btn-search">搜索</button>
					<table class="table table-condensed table-bordered table-hover" style="margin-top:8px;">
						<thead>
							<tr class="info">
								<th width="20%">选择</th>
								<th width="40%">会员ID</th>
								<th width="40%">会员名称</th>
							</tr>
						</thead>
						<tbody id="utoc_userList">
							<!-- <tr>
								<th><input type="radio" name="ucBind.userId" value="123123"/></th>
								<td>123123</td>
								<td>辛凯生物</td>
							</tr> -->
						</tbody>
					</table>
					
					<hr/>
					
					客户信息搜索条件：&nbsp;&nbsp;
					<input id="utoc_customerId" type="number" min="1" placeholder="请输入CRM客户ID"/>&nbsp;&nbsp;
					<input id="utoc_customerName" type="text" placeholder="请输入CRM客户名称" maxlength="16"/>&nbsp;&nbsp;
					<button type="button" id="utoc_customer_search" class="btn btn-info btn-search">搜索</button>
					<table class="table table-condensed table-bordered table-hover" style="margin-top:8px;">
						<thead>
							<tr class="info">
								<th width="20%">选择</th>
								<th width="40%">CRM客户ID</th>
								<th width="40%">CRM客户名称</th>
							</tr>
						</thead>
						<tbody id="utoc_customerList">
							<!-- <tr>
								<th><input type="radio" name="ucBind.customerId" value="123123"/></th>
								<td>11</td>
								<td>辛凯生物科技有限公司</td>
							</tr> -->
						</tbody>
					</table>
					
					<hr/>
					
					关联 网站会员 --> CRM客户&nbsp;&nbsp;<br/>
					会员ID:&nbsp;&nbsp;<input id="doBind_userId" readonly="readonly" required="required"/>&nbsp;&nbsp;
					客户ID:&nbsp;&nbsp;<input id="doBind_customerId" readonly="readonly" required="required"/>&nbsp;&nbsp;
					<a href="javascript:void(0)" id="doBindBtn" class="btn btn-info disabled">关联</a>
				</div>
			</div>
			</div>
      	</fieldset>
      	
      	<!-- 删除弹出确认框  -->
      	<div id="delConfirmModal" class="modal fade" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
						<h4 class="modal-title">删除确认</h4>
					</div>
					<div class="modal-body">
						<p><strong>是否确认删除该绑定关系？</strong></p>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal" aria-hidden="true">取消</button>
						<button type="button" onclick="executeDelBindMap()" class="btn btn-primary">确认删除</button>
					</div>
				</div>
			</div>
		</div>
      	
	</div>
	<!-- Main bar end -->

</div>
<!-- Main Content ends -->

<script src="${contextPath}/static/bootstrap/dist/js/bootstrap.min.js"></script>
<script src="${contextPath}/static/bootstrap/dist/js/jquery-ui.min.js"></script>
</body>
</html>