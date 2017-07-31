<%@ page language="java" contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
<title>DTS -- 订单产品快速关联页</title>
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
			width:175px;
		}
		input[type="checkbox"]{
			width:30px;
		}
	</style>
	<script type="text/javascript">
		$(function(){
			$('#gtop_u8_product_search').click(function(){
				doQueryU8Product(1);
			});
		});
		
		var currentGoodsId = 0;
		var orderProductList = [];
		var u8Id = 0;
		
		function doDataBind(u8_id){
			//弹出确认框提示
			u8Id = u8_id;
			$('#confirmMsgHtml').html('是否确认绑定:['+currentGoodsId+']-->['+u8_id+']???');
			$('#bindConfirmModal').modal('show');
		}
		
		function orderProductReload(){
			for(var i in orderProductList){
				if(currentGoodsId == orderProductList[i].goods_id){
					orderProductList[i]["isBinded"] = 1;
				}
			}
			//重新加到table
			var html = "";
			for(var i in orderProductList){
				html += "<tr>";
				html += "<td>"+orderProductList[i].goods_id+"</td><td>"+orderProductList[i].name+"</td><td>"+orderProductList[i].store_name+"</td><td>"+orderProductList[i].pack_num+''+orderProductList[i].unit+'/'+orderProductList[i].base_unit+"</td><td>"+orderProductList[i].puritys+"</td><td>";
				if(1==orderProductList[i].isBinded){ //已经绑定了的
					html += "<span class='text text-success'>已关联</span></td>";
				}else{
					html += "<input type='radio' name='tmp' onclick=doGoodsValueCheck("+orderProductList[i].goods_id+",'"+orderProductList[i].name+"') value='"+orderProductList[i].goods_id+"' /></td></tr>";
				}
			}
			$('#molbase_goodsList').html(html);
		}
		
		function queryOrderProducts(){
			$('#molbase_goodsList').html('');
			if($('#ordersn').val()!=''){
				$('#loadingModal').modal('show');
				$.post('${contextPath}/search.htm?view=goods&action=orderFind',{"ordersn":$('#ordersn').val()},function(result){
					$('#loadingModal').modal('hide');
					if(result.length>0){
						orderProductList = result;
						var html = "";
						for(var i in result){
							html += "<tr>";
							html += "<td>"+result[i].goods_id+"</td><td>"+result[i].name+"</td><td>"+result[i].store_name+"</td><td>"+result[i].pack_num+''+result[i].unit+'/'+result[i].base_unit+"</td><td>"+result[i].puritys+"</td><td>";
							if(1==result[i].isBinded){ //已经绑定了的
								html += "<span class='text text-success'>已关联</span></td>";
							}else{
								html += "<input type='radio' name='tmp' onclick=doGoodsValueCheck("+result[i].goods_id+",'"+result[i].name+"') value='"+result[i].goods_id+"' /></td></tr>";
							}
						}
						$('#molbase_goodsList').html(html);
					}
					else{
						$("#alert-noresult-div").removeClass("hidden");
						setTimeout('$("#alert-noresult-div").addClass("hidden")',2000);
					}
				});
			}
		}
		
		function doQueryU8Product(page){
			if($('#gtop_u8_productId').val()!='' || ''!=$('#gtop_u8_productName').val()){
				$.post('${contextPath}/search.htm?view=goods&action=noBindSearch',{"metaType":"u8_product","object_id":$('#gtop_u8_productId').val(),"object_name":$('#gtop_u8_productName').val(),"page":page},function(data){
					$('#gtop_u8_productList').html('');
					$('#doBind_u8_productId').val('');
					$('#doBindBtn').toggleClass('disabled',true);
					if(data.currData.length>0){
						var html = "";
						var result = data.currData;
						for(var i in result){
							html += "<tr id='u8_product_"+result[i].name1+"'><td><button class='btn btn-success' onclick=doDataBind('"+result[i].name1+"')>绑定</button></td><td>"+result[i].name1+"</td><td>"+result[i].name2+"</td><td>"+result[i].name3+"</td><td>"+result[i].unit+"</td></tr>";
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
					}
					else{
						$("#alert-noresult-div").removeClass("hidden");
						setTimeout('$("#alert-noresult-div").addClass("hidden")',3000);
					}
				});
			}
		}
		
		function doGoodsValueCheck(goods_id, goods_name){
			currentGoodsId = goods_id;
			$('#gtop_u8_productName').val(goods_name);
			doQueryU8Product(1);
		}
		
		function confirmDoBind(){
			$('#bindConfirmModal').modal('hide');
			var params = {
				"metaTypeStr":"molbase_goods@u8_product",
				"source_id":currentGoodsId,
				"target_id":u8Id
			};
			$.post('${contextPath}/search.htm?view=goods&action=doBind',params,function(result){
				$("#alert-info-div").removeClass("hidden");
				setTimeout('$("#alert-info-div").addClass("hidden")',2500);
				//u8_product clear
				$('#gtop_u8_productName').val('');
				$('#gtop_u8_productList').html('');
				$('#pagination_u8_product').html('');
				//order product reload
				orderProductReload();
			});
		}
	</script>
</head>
<body id="goodsMain">

<jsp:include page="../common/header.jsp"></jsp:include>

<!-- Main content starts -->
<div class="content">

  	<jsp:include page="../common/left.jsp"></jsp:include>

  	<!-- Main bar start-->
  	<div class="mainbar">
      	<!-- Page heading -->
	      <div class="page-head">
	        <h2 class="pull-left"><i class="fa fa-list-alt"></i>产品快速关联搜索</h2>
	        <div class="bread-crumb pull-right">
	          <a href="javascript:void(0)"><i class="fa fa-home"></i> 首页</a> 
	          <span class="divider">/</span> 
	          <a href="javascript:void(0)" class="bread-current">产品快速关联</a>
	        </div>
	        <div class="clearfix"></div>
	      </div>
		<!-- Page heading ends -->
      	
      	<!-- 主窗体  -->
      	<div id="alert-info-div" class="alert alert-success hidden" role="alert">
			<strong>提示：</strong><span id="alert-info-txt">操作成功！</span>
		</div>
		<div id="alert-noresult-div" class="alert alert-danger hidden" role="alert">
			<strong>提示：</strong><span id="alert-noresult-txt">无搜索结果返回！</span>
		</div>
      	<fieldset>
     	<div class="row-fluid" style="margin:20px;">
		<div class="span12">
			
			<div class="row">
				<!-- Activity widget -->
	            <div class="col-md-6">
	              	订单号：&nbsp;&nbsp;
					<input id="ordersn" type="number" min="1" placeholder="请输入订单号"/>&nbsp;&nbsp;
					<button type="button" id="order_search" onclick="queryOrderProducts()" class="btn btn-info btn-search">搜索</button>
					<table class="table table-condensed table-bordered table-hover" style="margin-top:8px;">
						<thead>
							<tr class="info">
								<th width="10%">产品ID</th>
								<th width="30%">产品名称</th>
								<th width="30%">店铺名</th>
								<th width="10%">产品包装</th>
								<th width="10%">产品纯度</th>
								<th width="10%">选择</th>
							</tr>
						</thead>
						<tbody id="molbase_goodsList"></tbody>
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
								<th width="10%">关联</th>
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
						
		</div>
		</div>
      	</fieldset>
      	
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
      	
	</div>
	<!-- Main bar end -->

</div>
<!-- Main Content ends -->

<script src="${contextPath}/static/bootstrap/dist/js/bootstrap.min.js"></script>
<script src="${contextPath}/static/bootstrap/dist/js/jquery-ui.min.js"></script>
</body>
</html>