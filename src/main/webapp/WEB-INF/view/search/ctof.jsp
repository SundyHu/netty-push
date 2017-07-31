<%@ page language="java" contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
<title>DTS -- 客户关联法人搜索页</title>
	<link rel="stylesheet" href="${contextPath}/static/bootstrap/dist/css/bootstrap.min.css">
	<link rel="stylesheet" href="${contextPath}/static/bootstrap/dist/css/jquery-ui.css"> 
	<link rel="stylesheet" href="${contextPath}/static/bootstrap/dist/css/style.css" >
	<link rel="stylesheet" href="${contextPath}/static/bootstrap/dist/css/font-awesome.min.css">
  	<link rel="stylesheet" href="${contextPath}/static/bootstrap/dist/css/jquery.onoff.css">
	<link rel="shortcut icon" href="${contextPath}/static/bootstrap/dist/img/favicon/favicon.png">
	
	<script src="${contextPath}/static/bootstrap/dist/js/respond.min.js"></script>
	<script src="${contextPath}/static/bootstrap/dist/js/jquery.js"></script>
	<style type="text/css">
		input{
			width:125px;
		}
		input[type="checkbox"]{
			width:30px;
		}
	</style>
	<script type="text/javascript">
		var currentCustomerArr = [];
		var currentFarenArr = [];
		
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
			
			$('#doBindBtn').click(function(){
				if($('#doBind_customerId').val()!='' && $('#doBind_u8_customerId').val()!=''){
					//判断是否需要弹框确认绑定?
					var validResult = validNameSame($('#doBind_customerId').val(),$('#doBind_u8_customerId').val());
					if(true == validResult){ //相同的，直接执行绑定
						confirmDoBind();
					}else{ //不相同,弹框提示
						$('#confirmMsgHtml').html(validResult);
						$('#bindConfirmModal').modal('show');
					}
				}
			});
			
			$('#ctof_customer_search').click(function(){
				doQueryCustomer(1);
			});
			
			$('#ctof_u8_customer_search').click(function(){
				doQueryU8Customer(1);
			});
			
			$('#ctof_bindSearch').click(function(){
				doQueryBindData(1);
			});
			
			$('#faren_type').change(function(){
				doQueryBindData(1);
			});
			
			doQueryBindData(1);
			
			$('#query_operator').change(function(){
				doQueryBindData(1);
			});
			
			//加载oper_list
			$.post('${contextPath}/search.htm?view=ctof&action=operList',{},function(list){
				for(var i in list){
					$('#query_operator').append('<option value="'+list[i]+'">'+list[i]+'</option>');
				}
			});
			
		});
		
		//点击确认关联按钮或者直接关联时执行
		function confirmDoBind(){
			$('#doBindBtn').toggleClass('disabled',true);
			var params = {
				"metaTypeStr":"customer@"+$('#faren_type').val(),
				"source_id":$('#doBind_customerId').val(),
				"target_id":$('#doBind_u8_customerId').val()
			};
			
			$('#bindConfirmModal').modal('hide');
			
			$.post('${contextPath}/search.htm?view=ctof&action=doBind',params,function(result){
				$("#alert-info-div").removeClass("hidden");
				setTimeout('$("#alert-info-div").addClass("hidden")',2500);
				//清空前面查询到的数据
				$("input[id^=ctof_]").val('');
				$("input[id^=doBind_]").val('');
				$('#ctof_customerList').html('');
				$('#pagination_customer').html('');
				$('#ctof_u8_customerList').html('');
				$('#pagination_u8_customer').html('');
				//重新查询绑定的内容
				doQueryBindData(1);
			});
		}
		
		//判断两个val对应的名称是否相同，返回true:相同,否则返回提示的msg
		//val1是customer一个或者多个,val2是u8法人id
		function validNameSame(val1, val2){
			//先找法人公司名字
			var farenName = '';
			for(var i in currentFarenArr){
				if(currentFarenArr[i]["name1"] == val2){ //找到了!
					farenName = currentFarenArr[i]["name2"];
					break;
				}
			}
			//再找CRM客户名字
			if(val1.indexOf(',')==-1){ //只有一个
				var customerName = '';
				for(var j in currentCustomerArr){
					if(currentCustomerArr[j]["name1"] == val1){ //找到了!
						customerName = currentCustomerArr[j]["name2"];
						break;
					}
				}
				
				if(customerName == farenName){
					return true;
				}else{
					return '是否确认[ '+customerName+' ] 关联 [ '+farenName+' ]?';
				}
			}else{ //多个
				var customerName = [];
				var customerIdArr = val1.split(',');
				for(var z in customerIdArr){
					for(var k in currentCustomerArr){
						if(customerIdArr[z] == currentCustomerArr[k]["name1"]){ //找到一个,追加
							customerName.push(currentCustomerArr[k]["name2"]);
						}
					}
				}
				//找完了
				return '是否确认[ '+customerName.join(',')+' ] 关联 [ '+farenName+' ]?';
			}
			
		}
		
		function doQueryCustomer(page){
			if($('#ctof_customerId').val()!='' || ''!=$('#ctof_customerName').val()){
				$.post('${contextPath}/search.htm?view=ctof&action=noBindSearch',{"metaType":"customer","object_id":$('#ctof_customerId').val(),"object_name":$('#ctof_customerName').val(),"page":page,"target_type":$('#faren_type').val()},function(data){
					$('#ctof_customerList').html('');
					$('#doBind_customerId').val('');
					$('#doBindBtn').toggleClass('disabled',true);
					if(data.currData.length>0){
						//list
						var html = "";
						var result = data.currData;
						currentCustomerArr = data.currData; //全局变量存着
						for(var i in result){
							html += "<tr id='customer_"+result[i].name1+"'><td>";
							if(result[i].bind==1){
								html += "<span class='text text-success'>已关联</span></td>";
							}else{
								html += "<input type='checkbox' name='cfBind_customerId' onclick=doCustomerValueCheck('doBind_customerId','"+result[i].name1+"','customer_') value='"+result[i].name1+"' /></td>";
							}
							html += "<td>"+result[i].name1+"</td><td>"+result[i].name2+"</td></tr>";
						}
						$('#ctof_customerList').html(html);
						//分页html
						var pagination = "<li class='disabled'><span aria-hidden=true>匹配总记录数:["+data.totalElements+"]&nbsp;&nbsp;</span><li>";
						if(data.first==true) pagination += "<li class='disabled'><a href='javascript:void(0)' aria-label='Previous'><span aria-hidden=true>上一页</span></a><li>";
						else pagination += "<li><a href='javascript:doQueryCustomer("+data.previousIndex+")' aria-label='Previous'><span aria-hidden=true>上一页</span></a><li>";
						if(data.pageLinkNumber>0){
							var betweenIndex = data.betweenIndex;
							for(var i=betweenIndex.beginIndex;i<=betweenIndex.endIndex;i++){
								if(i==data.pageIndex) pagination += "<li class='active'><a href='javascript:void(0)'>"+i+"</a></li>";
								else pagination += "<li><a href='javascript:doQueryCustomer("+i+")'>"+i+"</a></li>";
							}
						}
						if(data.last==true) pagination += "<li class='disabled'><a href='javascript:void(0)' aria-label='Next'><span aria-hidden=true>下一页</span></a><li>";
						else pagination += "<li><a href='javascript:doQueryCustomer("+data.nextIndex+")' aria-label='Next'><span aria-hidden=true>下一页</span></a><li>";
						$('#pagination_customer').html(pagination);
					}
					else{
						$("#alert-noresult-div").removeClass("hidden");
						setTimeout('$("#alert-noresult-div").addClass("hidden")',3000);
					}
				});
			}
		}
		
		function doQueryU8Customer(page){
			if($('#ctof_farenId').val()!='' || ''!=$('#ctof_u8_customerName').val()){
				$.post('${contextPath}/search.htm?view=ctof&action=noBindSearch',{"metaType":$('#faren_type').val(),"object_id":$('#ctof_u8_customerId').val(),"object_name":$('#ctof_u8_customerName').val(),"page":page},function(data){
					$('#ctof_u8_customerList').html('');
					$('#doBind_u8_customerId').val('');
					$('#doBindBtn').toggleClass('disabled',true);
					if(data.currData.length>0){
						//list
						var html = "";
						var result = data.currData;
						currentFarenArr = data.currData;
						for(var i in result){
							html += "<tr id='u8_customer_"+result[i].name1+"'><td><input type='radio' name='cfBind_u8_customerId' onclick=doValueMove('doBind_u8_customerId','"+result[i].name1+"','u8_customer_') value='"+result[i].name1+"' /></td><td>"+result[i].name1+"</td><td>"+result[i].name2+"</td><td>"+$('#faren_type').find("option:selected").text()+"</td></tr>";
						}
						$('#ctof_u8_customerList').html(html);
						//分页html
						var pagination = "<li class='disabled'><span aria-hidden=true>匹配总记录数:["+data.totalElements+"]&nbsp;&nbsp;</span><li>";
						if(data.first==true) pagination += "<li class='disabled'><a href='javascript:void(0)' aria-label='Previous'><span aria-hidden=true>上一页</span></a><li>";
						else pagination += "<li><a href='javascript:doQueryU8Customer("+data.previousIndex+")' aria-label='Previous'><span aria-hidden=true>上一页</span></a><li>";
						if(data.pageLinkNumber>0){
							var betweenIndex = data.betweenIndex;
							for(var i=betweenIndex.beginIndex;i<=betweenIndex.endIndex;i++){
								if(i==data.pageIndex) pagination += "<li class='active'><a href='javascript:void(0)'>"+i+"</a></li>";
								else pagination += "<li><a href='javascript:doQueryU8Customer("+i+")'>"+i+"</a></li>";
							}
						}
						if(data.last==true) pagination += "<li class='disabled'><a href='javascript:void(0)' aria-label='Next'><span aria-hidden=true>下一页</span></a><li>";
						else pagination += "<li><a href='javascript:doQueryU8Customer("+data.nextIndex+")' aria-label='Next'><span aria-hidden=true>下一页</span></a><li>";
						$('#pagination_u8_customer').html(pagination);
					}
					else{
						$("#alert-noresult-div").removeClass("hidden");
						setTimeout('$("#alert-noresult-div").addClass("hidden")',3000);
					}
				});
			}
		}
		
		function doQueryBindData(page){
			$.post('${contextPath}/search.htm?view=ctof',{
				"action":"bindedSearch",
				"source_type":"customer",
				"target_type":$("#faren_type").val(),
				"query_bind_id":$('#query_bind_id').val(),
				"query_bind_name":$('#query_bind_name').val(),
				"operator":$('#query_operator').val(),
				"page":page
			},function(data){
				var html = "";
				var result = data.currData;
				$("#totalElements").html(data.totalElements);
				for(var i in result){
					html += "<tr><td>"+result[i].source_object_id+"</td><td>"+result[i].source_object_name+"</td><td>"+result[i].target_object_id+"</td><td>"+result[i].target_object_name+"</td><td>"+$('#faren_type').find("option:selected").text()+"</td><td><span class='text text-success'>已关联</span></td><td>"+result[i].oper_user+"</td><td>"+result[i].last_update_time+"</td><td><a href='javascript:void(0)' onclick='delBindMap(this)' class='btn btn-danger' mapId='"+result[i].id+"'>删除绑定关系</a></td></tr>";
				}
				$('#ctof_bindList').html(html);
				
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
		
		function doCustomerValueCheck(inputId,id,tab){
			var checkedId = [];
			$('input:checkbox[name=cfBind_customerId]:checked').each(function(i){
				checkedId.push($(this).val());
			});
			$('#'+inputId).val(checkedId.join(","));
			var bindInputs = $("input[id^=doBind_]");
			if($(bindInputs[0]).val()!='' && $(bindInputs[1]).val()!=''){
				$('#doBindBtn').toggleClass('disabled',false);
			}else{
				$('#doBindBtn').toggleClass('disabled',true);
			}
		}
		
		function doValueMove(inputId,id,tab){
			$('#'+inputId).val(id);
			$('tr[id^='+tab+']').removeClass('tr_gc');
			$('#'+tab+''+id).addClass('tr_gc');
			var bindInputs = $("input[id^=doBind_]");
			if($(bindInputs[0]).val()!='' && $(bindInputs[1]).val()!=''){
				$('#doBindBtn').toggleClass('disabled',false);
				//两个框的值都有了，先看看是否存在有绑定关系了~
				/* $.post('${contextPath}/search.htm?view=ctof&action=findMapExists',{"metaTypeStr":"customer@"+$('#faren_type').val(),"source_id":$('#doBind_customerId').val()},
					function(result){
					if(true==result){//存在啦!
						$("#alert-error-div").removeClass("hidden");
						setTimeout('$("#alert-error-div").addClass("hidden")',3000);
						$('#doBindBtn').toggleClass('disabled',true);
					}else{
						$('#doBindBtn').toggleClass('disabled',false);
					}
				}); */
			}else{
				$('#doBindBtn').toggleClass('disabled',true);
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
	        <h2 class="pull-left"><i class="fa fa-list-alt"></i>客户法人搜索</h2>
	        <div class="bread-crumb pull-right">
	          <a href="javascript:void(0)"><i class="fa fa-home"></i> 首页</a> 
	          <span class="divider">/</span> 
	          <a href="javascript:void(0)" class="bread-current">客户法人搜索</a>
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
			&nbsp;&nbsp;
			U8法人类型选择：&nbsp;&nbsp;
			<select id="faren_type">
				<option value="u8_supplier" selected="selected">供应商</option>
				<option value="u8_buyer">采购商</option>
			</select>
			<hr/>
			<div id="binded">
				搜索条件：&nbsp;&nbsp;
				<input type="number" min="1" placeholder="请输入客户ID" id="query_bind_id" name="query_bind_id" />&nbsp;&nbsp;
				<input type="text" placeholder="请输入客户名称" id="query_bind_name" name="query_bind_name" maxlength="16"/>&nbsp;&nbsp;
				<select id="query_operator">
					<option value="0">--请选择--</option>
				</select>&nbsp;&nbsp;
				<button type="button" id="ctof_bindSearch" class="btn btn-info btn-search">搜索</button>
				&nbsp;&nbsp;已关联总数：<span id="totalElements"></span>
				<table class="table table-condensed table-bordered table-hover" style="margin-top:8px;">
					<thead>
						<tr class="info">
							<th width="10%">客户ID</th>
							<th width="15%">客户名称</th>
							<th width="10%">U8法人ID</th>
							<th width="15%">U8法人名称</th>
							<th width="10%">U8客户属性</th>
							<th width="5%">状态</th>
							<th width="5">操作人</th>
							<th width="15%">关联时间</th>
							<th width="15%">操作</th>
						</tr>
					</thead>
					<tbody id="ctof_bindList"></tbody>
					<tfoot>
						<tr><td colspan="9" align="center">
						<ul id="pagination" class="pagination pagination-lg"></ul>
						</td></tr>
					</tfoot>
				</table>
			</div>
			
			<div id="nobind" style="display:none;">
				<div class="row">
					<!-- Activity widget -->
		            <div class="col-md-6">
		            	客户信息搜索条件：&nbsp;&nbsp;
						<input id="ctof_customerId" type="number" min="1" placeholder="请输入客户ID"/>&nbsp;&nbsp;
						<input id="ctof_customerName" type="text" placeholder="请输入客户名称" maxlength="16"/>&nbsp;&nbsp;
						<button type="button" id="ctof_customer_search" class="btn btn-info btn-search">搜索</button>
						<table class="table table-condensed table-bordered table-hover" style="margin-top:8px;">
							<thead>
								<tr class="info">
									<th width="20%">选择</th>
									<th width="40%">客户ID</th>
									<th width="40%">客户名称</th>
								</tr>
							</thead>
							<tbody id="ctof_customerList"></tbody>
							<tfoot>
								<tr><td colspan="3" align="center">
								<ul id="pagination_customer" class="pagination pagination-sm"></ul>
								</td></tr>
							</tfoot>
						</table>
		            </div>
		            
		            <!-- Activity widget -->
		            <div class="col-md-6">
		            	法人信息搜索条件：&nbsp;&nbsp;
						<input id="ctof_u8_customerId" type="text" placeholder="请输入U8法人ID" maxlength="20"/>&nbsp;&nbsp;
						<input id="ctof_u8_customerName" type="text" placeholder="请输入U8法人名称" maxlength="16"/>&nbsp;&nbsp;
						<button type="button" id="ctof_u8_customer_search" class="btn btn-info btn-search">搜索</button>
						<table class="table table-condensed table-bordered table-hover" style="margin-top:8px;">
							<thead>
								<tr class="info">
									<th width="15%">选择</th>
									<th width="35%">U8法人ID</th>
									<th width="35%">U8法人名称</th>
									<th width="15%">U8客户属性</th>
								</tr>
							</thead>
							<tbody id="ctof_u8_customerList"></tbody>
							<tfoot>
								<tr><td colspan="4" align="center">
								<ul id="pagination_u8_customer" class="pagination pagination-sm"></ul>
								</td></tr>
							</tfoot>
						</table>
		            </div>
				</div>
				
				<div class="row">
					<div class="col-md-12"><hr/></div>
					<div class="col-md-12">
						关联 CRM客户 --> U8法人&nbsp;&nbsp;<br/>
						<table class="table table-condensed table-bordered table-hover" style="margin-top:8px;">
							<tr><td width="13.8%" align="right">客户ID:</td><td><input id="doBind_customerId" readonly="readonly" required="required" style="width:89%"/></td></tr>
							<tr><td align="right">法人ID:</td><td><input id="doBind_u8_customerId" readonly="readonly" required="required" style="width:89%"/></td></tr>
							<tr><td colspan="2" align="center"><a href="javascript:void(0)" id="doBindBtn" class="btn btn-info disabled" style="width:81.5%">关联</a></td></tr>
						</table>
					</div>
				</div>
				
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
		
		<!-- 绑定确认框  -->
      	<div id="bindConfirmModal" class="modal fade" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
						<h4 class="modal-title">关联确认</h4>
					</div>
					<div class="modal-body">
						<p><strong id="confirmMsgHtml">是否确认执行关联?</strong></p>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal" aria-hidden="true">取消</button>
						<button type="button" id="btnConfirmBind" onclick="confirmDoBind()" class="btn btn-primary">确认关联</button>
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