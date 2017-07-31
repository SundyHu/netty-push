<%@ page language="java" contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
<title>DTS -- 业务定义管理</title>
	<link rel="stylesheet" href="${contextPath}/static/bootstrap/dist/css/bootstrap.min.css">
	<link rel="stylesheet" href="${contextPath}/static/bootstrap/dist/css/jquery-ui.css"> 
	<link rel="stylesheet" href="${contextPath}/static/bootstrap/dist/css/style.css" >
	<link rel="stylesheet" href="${contextPath}/static/bootstrap/dist/css/font-awesome.min.css">
  	<link rel="stylesheet" href="${contextPath}/static/bootstrap/dist/css/jquery.onoff.css">
	<link rel="shortcut icon" href="${contextPath}/static/bootstrap/dist/img/favicon/favicon.png">
	<script src="${contextPath}/static/bootstrap/dist/js/respond.min.js"></script>
	<script src="${contextPath}/static/bootstrap/dist/js/jquery.js"></script>
	<script type="text/javascript">
		function bindTypeChange(){
			if($('#bindType').val()=='1'){
				$('#override').show();
				$('#overrideTxt').show();
			}else{
				$('#override').hide();
				$('#overrideTxt').hide();
			}
		}
		
		function loadBizMetaList(page){
			if(!page){page=1;}
			$.post('${contextPath}/biz.htm',{"action":'bizMetaList',"page":page},function(data){
				//list
				$('#bizMetaListBody').empty();
				var result = data.currData;
				var html = '';
				for(var i in result){
					html += "<tr><td>"+result[i].id+"</td><td>"+result[i].name+"</td><td>"+(result[i].bind==1?'1对1':'1对N')+"</td><td>"+(result[i].override==1?'可以':'不可以')+"</td><td>"+result[i].operator+"</td><td>"+result[i].create_time+"</td><td>"+result[i].last_update_time+"</td><td><button onclick='edit("+result[i].id+")' class='btn btn-info btn-small'>修改</button></td></tr>";
				}
				$('#bizMetaListBody').html(html);
				//分页
				var pagination = "<li class='disabled'><a href='javascript:void(0)'>总记录数:"+data.totalElements+"</a></li>";
				if(data.first==true) pagination += "<li class='disabled'><a href='javascript:void(0)' aria-label='Previous'><span aria-hidden=true>上一页</span></a><li>";
				else pagination += "<li><a href='javascript:loadBizMetaList("+data.previousIndex+")' aria-label='Previous'><span aria-hidden=true>上一页</span></a><li>";
				if(data.pageLinkNumber>0){
					var betweenIndex = data.betweenIndex;
					for(var i=betweenIndex.beginIndex;i<=betweenIndex.endIndex;i++){
						if(i==data.pageIndex) pagination += "<li class='active'><a href='javascript:void(0)'>"+i+"</a></li>";
						else pagination += "<li><a href='javascript:loadBizMetaList("+i+")'>"+i+"</a></li>";
					}
				}
				if(data.last==true) pagination += "<li class='disabled'><a href='javascript:void(0)' aria-label='Next'><span aria-hidden=true>下一页</span></a><li>";
				else pagination += "<li><a href='javascript:loadBizMetaList("+data.nextIndex+")' aria-label='Next'><span aria-hidden=true>下一页</span></a><li>";
				$('#pagination').html(pagination);
			});
		}
		
		function edit(id){
			$.post('${contextPath}/biz.htm',{"action":'bizMetaFind',"id":id},function(data){
				if(0!=data && data.hasOwnProperty("id")){ //找到啦
					$('#bizIdHide').val(data.id);
					$('#bizId').html(data.id);
					$('#bizName').val(data.name);
					$('#override').val(data.override);
					$('#bindType').val(data.bind);
					if(data.bind==2){$('#override').hide();}
				}
			});
		}
		
		function clearForm(){
			$('#bizIdHide').val('0');
			$('#bizId').html('');
			$('#bizName').val('');
			$('#bindType').val('1');
			$('#override').val('1');
			$('#override').show();
		}
		
		function saveBizSet(){
			if(''==$('#bizName').val()){
				$("#alert-info-div").removeClass("hidden");
				setTimeout('$("#alert-info-div").addClass("hidden")',2000);
				return;
			}
			$.post('${contextPath}/biz.htm',{"action":"bizMetaSave",
				"id":$('#bizIdHide').val(),
				"name":$('#bizName').val(),
				"bind":$('#bindType').val(),
				"override":$('#override').val()
				},function(data){
					if(data == '1'){ //ok
						loadBizMetaList(1);
						clearForm();
					}
			});
		}
		
		loadBizMetaList(1)
	</script>
</head>
<body id="bizSetMain">

<jsp:include page="../common/header.jsp"></jsp:include>

<!-- Main content starts -->
<div class="content">

  	<jsp:include page="../common/left.jsp"></jsp:include>

  	<!-- Main bar start-->
  	<div class="mainbar">
      	<!-- Page heading -->
	      <div class="page-head">
	        <h2 class="pull-left"><i class="fa fa-list-alt"></i>业务管理</h2>
	        <div class="bread-crumb pull-right">
	          <a href="javascript:void(0)"><i class="fa fa-home"></i> 首页</a> 
	          <span class="divider">/</span> 
	          <a href="javascript:void(0)" class="bread-current">业务管理</a>
	        </div>
	        <div class="clearfix"></div>
	      </div>
		<!-- Page heading ends -->
      	
      	<!-- 主窗体  -->
      	<div id="alert-info-div" class="alert alert-success hidden" role="alert">
			<strong>提示：</strong><span id="alert-info-txt">业务名称不能为空!</span>
		</div>
      	<fieldset>
     	<div class="row-fluid" style="margin:20px;">
		<div class="span12">
			
			<table class="table table-condensed table-bordered table-hover" style="margin-top:2px;margin-bottom:1px;">
				<thead>
					<tr class="info">
						<th width="10%">业务ID</th>
						<th width="20%">业务名称</th>
						<th width="10%">绑定关系</th>
						<th width="10%">1:1时是否可以覆盖</th>
						<th width="10%">操作人</th>
						<th width="10%">创建时间</th>
						<th width="10%">最后修改时间</th>
						<th width="20%">操作</th>
					</tr>
				</thead>
				<tbody id="bizMetaListBody"></tbody>
				<tfoot>
					<tr>
						<td colspan="8" align="center">
							<ul id="pagination" style="margin:0px 0px 0px 0px;" class="pagination pagination-sm"></ul>
						</td>
					</tr>
				</tfoot>
			</table>
		
			<hr style="margin:5px 0px 5px 0px;">
			
			<table class="table table-condensed table-bordered">
				<tr>
					<td colspan="4" class="text-left">
						<h3><button class="btn btn-primary" onclick="clearForm()">新增</button><b style="padding-left: 20px;">转换业务设置</b></h3>
					</td>
				</tr>
				<tr>
					<td width="20%" class="text-center"><label class="control-label"><b>业务名称</b></label></td>
					<td width="30%" class="text-center">
						<input type="text" placeholder="请输入业务名称" class="form-control" id="bizName"/>
					</td>
					<td width="20%" class="text-center"><label class="control-label"><b>业务ID</b></label></td>
					<td width="30%" class="text-center">
						<label class="control-label" id="bizId"></label>
						<input type="hidden" id="bizIdHide" />
					</td>
				</tr>
				<tr>
					<td class="text-center"><label class="control-label"><b>绑定关系</b></label></td>
					<td class="text-center">
						<select class="form-control" id="bindType" onchange="bindTypeChange()">
							<option value="1">1:1</option>
							<option value="2">1:N</option>
						</select>
					</td>
					<td class="text-center"><label class="control-label" id="overrideTxt"><b>存在是否可以覆盖</b></label></td>
					<td>
						<select class="form-control" id="override">
							<option value="1">可以</option>
							<option value="2">不可以</option>
						</select>
					</td>
				</tr>
				<tr>
					<td colspan="4" class="text-center">
						<button style="width:200px;" type="button" onclick="saveBizSet()" class="btn btn-success" id="btnSave">保存</button>
						<button style="width:200px;" type="button" onclick="clearForm()" class="btn btn-danger">清空</button>
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