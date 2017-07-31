<%@ page language="java" contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
<title>DTS -- 主站产品关联U8产品搜索页</title>
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
		
		var currentGoodsArr = [];
		var currentProductArr = [];
	
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
				if($('#doBind_molbase_goodsId').val()!='' && $('#doBind_u8_productId').val()!=''){
					//判断是否需要弹框确认绑定?
					var validResult = validNameSame($('#doBind_molbase_goodsId').val(), $('#doBind_u8_productId').val());
					if(true == validResult){ //相同的，直接执行绑定
						confirmDoBind();
					}else{ //不相同,弹框提示
						$('#confirmMsgHtml').html(validResult);
						$('#bindConfirmModal').modal('show');
					}
				}
			});
			
			$('#gtop_molbase_goods_search').click(function(){
				doQueryMolbaseGoods(1);
			});
			
			$('#gtop_u8_product_search').click(function(){
				doQueryU8Product(1);
			});
			
			$('#gtop_bindSearch').click(function(){
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
		
		function confirmDoBind(){
			$('#doBindBtn').toggleClass('disabled',true);
			var params = {
				"metaTypeStr":"molbase_goods@u8_product",
				"source_id":$('#doBind_molbase_goodsId').val(),
				"target_id":$('#doBind_u8_productId').val()
			};
			
			$('#bindConfirmModal').modal('hide');
			
			$.post('${contextPath}/search.htm?view=gtop&action=doBind',params,function(result){
				$("#alert-info-div").removeClass("hidden");
				setTimeout('$("#alert-info-div").addClass("hidden")',2500);
				//清空前面查询到的数据
				$("input[id^=gtop_]").val('');
				$("input[id^=doBind_]").val('');
				$('#gtop_molbase_goodsList').html('');
				$('#pagination_molbase_goods').html('');
				$('#gtop_u8_productList').html('');
				$('#pagination_u8_product').html('');
				//重新查询绑定的内容
				doQueryBindData(1);
			});
		}
		
		//判断两个val对应的名称是否相同，返回true:相同,否则返回提示的msg
		//val1是goods一个或者多个,val2是u8的productID
		function validNameSame(val1, val2){
			//先找u8productName
			var productName = '';
			for(var i in currentProductArr){
				if(currentProductArr[i]["name1"] == val2){ //找到了!
					productName = currentProductArr[i]["name2"];
					break;
				}
			}
			//再找goods名字
			if(val1.indexOf(',')==-1){ //只有一个
				var goodsName = '';
				for(var j in currentGoodsArr){
					if(currentGoodsArr[j]["name1"] == val1){ //找到了!
						goodsName = currentGoodsArr[j]["name2"];
						break;
					}
				}
				
				if(goodsName == productName){
					console.info(goodsName+'------>'+productName);
					return true;
				}else{
					return '是否确认[ '+goodsName+' ] 关联 [ '+productName+' ]?';
				}
			}else{ //多个
				var goodsName = [];
				var goodsIdArr = val1.split(',');
				for(var z in goodsIdArr){
					for(var k in currentGoodsArr){
						if(goodsIdArr[z] == currentGoodsArr[k]["name1"]){ //找到一个,追加
							goodsName.push(currentGoodsArr[k]["name2"]);
						}
					}
				}
				//找完了
				return '是否确认[ '+goodsName.join(',')+' ] 关联 [ '+productName+' ]?';
			}
			
		}
		
		function doQueryMolbaseGoods(page){
			if($('#gtop_molbase_goodsId').val()!='' || ''!=$('#gtop_molbase_goodsName').val()){
				$('#loadingModal').modal('show');
				$('#doBind_molbase_goodsId').val('');
				$('#doBindBtn').toggleClass('disabled',true);
				$.post('${contextPath}/search.htm?view=gtop&action=noBindSearch',{"metaType":"molbase_goods","object_id":$('#gtop_molbase_goodsId').val(),"object_name":$('#gtop_molbase_goodsName').val(),"store_name":$('#gtop_molbase_goods_store').val(),"page":page},function(data){
					$('#loadingModal').modal('hide');
					$('#gtop_molbase_goodsList').html('');
					if(data.currData.length>0){
						//list
						var html = "";
						result = data.currData;
						currentGoodsArr = data.currData;
						for(var i in result){
							html += "<tr id='molbase_goods_"+result[i].name1+"'><td>";
							if(result[i].bind==1){
								html += "<span class='text text-success'>已关联</span></td>";
							}else{
								html += "<input type='checkbox' name='gpBind_molbase_goodsId' onclick=doGoodsValueCheck('doBind_molbase_goodsId','"+result[i].name1+"','molbase_goods_') value='"+result[i].name1+"' /></td>";
							}
							html += "<td>"+result[i].name1+"</td><td>"+result[i].name2+"</td><td>"+result[i].storeName+"</td><td>"+result[i].authLevel+"</td><td>"+result[i].pack+"</td><td>"+result[i].name3+"</td></tr>";
						}
						$('#gtop_molbase_goodsList').html(html);
						//分页html
						var pagination = "<li class='disabled'><span aria-hidden=true>匹配总记录数:["+data.totalElements+"]&nbsp;&nbsp;</span><li>";
						if(data.first==true) pagination += "<li class='disabled'><a href='javascript:void(0)' aria-label='Previous'><span aria-hidden=true>上一页</span></a><li>";
						else pagination += "<li><a href='javascript:doQueryMolbaseGoods("+data.previousIndex+")' aria-label='Previous'><span aria-hidden=true>上一页</span></a><li>";
						if(data.pageLinkNumber>0){
							var betweenIndex = data.betweenIndex;
							for(var i=betweenIndex.beginIndex;i<=betweenIndex.endIndex;i++){
								if(i==data.pageIndex) pagination += "<li class='active'><a href='javascript:void(0)'>"+i+"</a></li>";
								else pagination += "<li><a href='javascript:doQueryMolbaseGoods("+i+")'>"+i+"</a></li>";
							}
						}
						if(data.last==true) pagination += "<li class='disabled'><a href='javascript:void(0)' aria-label='Next'><span aria-hidden=true>下一页</span></a><li>";
						else pagination += "<li><a href='javascript:doQueryMolbaseGoods("+data.nextIndex+")' aria-label='Next'><span aria-hidden=true>下一页</span></a><li>";
						$('#pagination_molbase_goods').html(pagination);
					}
					else{
						$("#alert-noresult-div").removeClass("hidden");
						setTimeout('$("#alert-noresult-div").addClass("hidden")',3000);
					}
				});
			}
		}
		
		function doQueryU8Product(page){
			if($('#gtop_u8_productId').val()!='' || ''!=$('#gtop_u8_productName').val()){
				$.post('${contextPath}/search.htm?view=gtop&action=noBindSearch',{"metaType":"u8_product","object_id":$('#gtop_u8_productId').val(),"object_name":$('#gtop_u8_productName').val(),"page":page},function(data){
					$('#gtop_u8_productList').html('');
					$('#doBind_u8_productId').val('');
					$('#doBindBtn').toggleClass('disabled',true);
					if(data.currData.length>0){
						var html = "";
						var result = data.currData;
						currentProductArr = data.currData;
						for(var i in result){
							html += "<tr id='u8_product_"+result[i].name1+"'><td><input type='radio' name='gpBind_u8_productId' onclick=doValueMove('doBind_u8_productId','"+result[i].name1+"','u8_product_') value='"+result[i].name1+"' /></td><td>"+result[i].name1+"</td><td>"+result[i].name2+"</td><td>"+result[i].name3+"</td><td>"+result[i].unit+"</td></tr>";
						}
						$('#gtop_u8_productList').html(html);
						//分页html
						var pagination = "<li class='disabled'><span aria-hidden=true>匹配总记录数:["+data.totalElements+"]&nbsp;&nbsp;</span><li>";
						if(data.first==true) pagination += "<li class='disabled'><a href='javascript:void(0)' aria-label='Previous'><span aria-hidden=true>上一页</span></a><li>";
						else pagination += "<li><a href='javascript:doQueryU8Product("+data.previousIndex+")' aria-label='Previous'><span aria-hidden=true>上一页</span></a><li>";
						if(data.pageLinkNumber>0){
							var betweenIndex = data.betweenIndex;
							for(var i=betweenIndex.beginIndex;i<=betweenIndex.endIndex;i++){
								if(i==data.pageIndex) pagination += "<li class='active'><a href='javascript:void(0)'>"+i+"</a></li>";
								else pagination += "<li><a href='javascript:doQueryU8Product("+i+")'>"+i+"</a></li>";
							}
						}
						if(data.last==true) pagination += "<li class='disabled'><a href='javascript:void(0)' aria-label='Next'><span aria-hidden=true>下一页</span></a><li>";
						else pagination += "<li><a href='javascript:doQueryU8Product("+data.nextIndex+")' aria-label='Next'><span aria-hidden=true>下一页</span></a><li>";
						$('#pagination_u8_product').html(pagination);
						//$('#gtopMain').scrollTop($('#gtopMain')[0].scrollHeight );
					}
					else{
						$("#alert-noresult-div").removeClass("hidden");
						setTimeout('$("#alert-noresult-div").addClass("hidden")',3000);
					}
				});
			}
		}
		
		function doQueryBindData(page){
			$.post('${contextPath}/search.htm?view=gtop',{
				"action":"bindedSearch",
				"source_type":"molbase_goods",
				"query_bind_id":$('#query_bind_id').val(),
				"query_bind_name":$('#query_bind_name').val(),
				"operator":$('#query_operator').val(),
				"page":page
			},function(data){
				var html = "";
				var result = data.currData;
				$("#totalElements").html(data.totalElements);
				for(var i in result){
					html += "<tr><td>"+result[i].source_object_id+"</td><td>"+result[i].source_object_name+"</td><td>"+result[i].target_object_id+"</td><td>"+result[i].target_object_name+"</td><td><span class='text text-success'>已关联</span></td><td>"+result[i].oper_user+"</td><td>"+result[i].last_update_time+"</td><td><a href='javascript:void(0)' onclick='delBindMap(this)' class='btn btn-danger' mapId='"+result[i].id+"'>删除绑定关系</a></td></tr>";
				}
				$('#gtop_bindList').html(html);
				//分页html
				var pagination = "";
				if(data.first==true) pagination += "<li class='disabled'><a href='javascript:void(0)' aria-label='Previous'><span aria-hidden=true>上一页</span></a><li>";
				else pagination += "<li><a href='javascript:doQueryBindData("+data.previousIndex+")' aria-label='Previous'><span aria-hidden=true>上一页</span></a><li>";
				if(data.pageLinkNumber>0){
					var betweenIndex = data.betweenIndex;
					for(var i=betweenIndex.beginIndex;i<=betweenIndex.endIndex;i++){
						if(i==data.pageIndex) pagination += "<li class='active'><a href='javascript:void(0)'>"+i+"</a></li>";
						else pagination += "<li><a href='javascript:doQueryBindData("+i+")'>"+i+"</a></li>";
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

		function doGoodsValueCheck(inputId,id,tab){
			var checkedId = [];
			$('input:checkbox[name=gpBind_molbase_goodsId]:checked').each(function(i){
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
				/* $.post('${contextPath}/search.htm?view=gtop&action=findMapExists',{"metaTypeStr":"molbase_goods@u8_product","source_id":$('#doBind_molbase_goodsId').val()},
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
<body id="gtopMain">

<jsp:include page="../common/header.jsp"></jsp:include>

<!-- Main content starts -->
<div class="content">

  	<jsp:include page="../common/left.jsp"></jsp:include>

  	<!-- Main bar start-->
  	<div class="mainbar">
      	<!-- Page heading -->
	      <div class="page-head">
	        <h2 class="pull-left"><i class="fa fa-list-alt"></i>产品关联搜索</h2>
	        <div class="bread-crumb pull-right">
	          <a href="javascript:void(0)"><i class="fa fa-home"></i> 首页</a> 
	          <span class="divider">/</span> 
	          <a href="javascript:void(0)" class="bread-current">产品关联搜索</a>
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
				<input type="number" min="1" placeholder="请输入商城产品ID" id="query_bind_id" name="query_bind_id" />&nbsp;&nbsp;
				<input type="text" placeholder="请输入商城产品名称" id="query_bind_name" name="query_bind_name" maxlength="16"/>&nbsp;&nbsp;
				<select id="query_operator">
					<option value="0">--请选择--</option>
				</select>&nbsp;&nbsp;
				<button type="button" id="gtop_bindSearch" class="btn btn-info btn-search">搜索</button>
				&nbsp;&nbsp;已关联总数：<span id="totalElements"></span>
				<table class="table table-condensed table-bordered table-hover" style="margin-top:8px;">
					<thead>
						<tr class="info">
							<th width="10%">主站产品ID</th>
							<th width="15%">主站产品名称</th>
							<th width="10%">U8产品ID</th>
							<th width="15%">U8产品名称</th>
							<th width="10%">状态</th>
							<th width="5%">操作人</th>
							<th width="20%">关联时间</th>
							<th width="15%">操作</th>
						</tr>
					</thead>
					<tbody id="gtop_bindList"></tbody>
					<tfoot>
						<tr><td colspan="8" align="center">
						<ul id="pagination" class="pagination pagination-lg"></ul>
						</td></tr>
					</tfoot>
				</table>
			</div>
			
			<div id="nobind" style="display:none;">
				<div class="row">
					<!-- Activity widget -->
		            <div class="col-md-6">
		              	主站产品搜索条件：&nbsp;&nbsp;
						<input id="gtop_molbase_goodsId" type="number" min="1" placeholder="请输入商城产品ID"/>&nbsp;&nbsp;
						<input id="gtop_molbase_goodsName" type="text" placeholder="请输入商城产品名称" maxlength="16"/>&nbsp;&nbsp;
						<input id="gtop_molbase_goods_store" type="text" placeholder="请输入供应商名称" maxlength="16"/>&nbsp;&nbsp;
						<button type="button" id="gtop_molbase_goods_search" class="btn btn-info btn-search">搜索</button>
						<table class="table table-condensed table-bordered table-hover" style="margin-top:8px;">
							<thead>
								<tr class="info">
									<th width="8%">选择</th>
									<th width="10%">产品ID</th>
									<th width="20%">产品名称</th>
									<th width="30%">店铺名</th>
									<th width="12%">店铺认证等级</th>
									<th width="10%">产品包装</th>
									<th width="10%">产品纯度</th>
								</tr>
							</thead>
							<tbody id="gtop_molbase_goodsList"></tbody>
							<tfoot>
								<tr><td colspan="7" align="center">
								<ul id="pagination_molbase_goods" class="pagination pagination-sm"></ul>
								</td></tr>
							</tfoot>
						</table>
		            </div>
								
					<!-- Activity widget -->
		            <div class="col-md-6">
		              	U8产品搜索条件：&nbsp;&nbsp;
						<input id="gtop_u8_productId" type="text" placeholder="请输入U8产品ID" maxlength="20"/>&nbsp;&nbsp;
						<input id="gtop_u8_productName" type="text" placeholder="请输入U8产品名称" maxlength="16"/>&nbsp;&nbsp;
						<button type="button" id="gtop_u8_product_search" class="btn btn-info btn-search">搜索</button>
						<table class="table table-condensed table-bordered table-hover" style="margin-top:8px;">
							<thead>
								<tr class="info">
									<th width="10%">选择</th>
									<th width="25%">产品ID</th>
									<th width="35%">产品名称</th>
									<th width="15%">产品包装规格</th>
									<th width="15%">产品计量单位</th>
								</tr>
							</thead>
							<tbody id="gtop_u8_productList"></tbody>
							<tfoot>
								<tr><td colspan="5" align="center">
								<ul id="pagination_u8_product" class="pagination pagination-sm"></ul>
								</td></tr>
							</tfoot>
						</table>
		            </div>
				</div>
							
				<div class="row">
					<div class="col-md-12"><hr/></div>
					<div class="col-md-12">
						关联 主站产品 --> U8产品&nbsp;&nbsp;
						<table class="table table-condensed table-bordered table-hover" style="margin-top:8px;">
							<tr><td width="13.8%" align="right">主站产品ID:</td><td><input id="doBind_molbase_goodsId" readonly="readonly" required="required" style="width:89%"/></td></tr>
							<tr><td align="right">U8产品ID:</td><td><input id="doBind_u8_productId" readonly="readonly" required="required" style="width:89%"/></td></tr>
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
		<!-- 加载遮罩层  -->
      	<div id="loadingModal" class="modal fade" role="dialog" aria-labelledby="myLoadingLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
						<h4 class="modal-title">加载中...</h4>
					</div>
					<div class="modal-body">
						<img alt="正在加载，请稍后..." src="${contextPath}/static/images/loading.gif" />
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