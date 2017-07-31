<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<div id="alert-info-div" class="alert alert-success hidden" role="alert">
	<strong>提示：</strong><span id="alert-info-txt">关系绑定成功！</span>
</div>
<div id="alert-delete-div" class="alert alert-danger hidden" role="alert">
	<strong>错误：</strong><span id="alert-error-txt">该ID值不存在,请重新输入！</span>
</div>
<div id="alert-error-div" class="alert alert-danger hidden" role="alert">
	<strong>错误：</strong><span id="alert-exists-txt">已经存在绑定关系,不能重复绑定！</span>
</div>
<fieldset>
<div class="row-fluid" style="margin:20px;">
	<div class="span12">
	
		<!-- <span class="text-info text-left">关联 网站会员 ID --&gt; CRM客户 ID</span>
		<table class="table table-condensed table-bordered table-hover">
			<thead>
				<tr class="info">
					<th>网站会员ID</th>
					<th>绑定</th>
					<th>CRM客户ID</th>
					<th>操作</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td width="20%"><input id="uc-user" type="number" maxlength="8" min="1" required="required" placeholder="请输入网站会员ID"/></td>
					<td width="5%">==&gt;</td>
					<td width="20%"><input id="uc-customer" type="number" maxlength="8" min="1" required="required" placeholder="请输入CRM客户ID"/></td>
					<td width="55%">
						<a href="javascript:void(0)" id="uc-bindBtn" class="btn btn-info disabled">关联</a>
					</td>
				</tr>
			</tbody>
		</table>
		
		<hr/> -->
		
		<span class="text-info text-left">关联 CRM客户 ID --&gt; U8 客户 ID</span>
		<table class="table table-condensed table-bordered table-hover">
			<thead>
				<tr class="info">
					<th>CRM客户ID</th>
					<th>绑定</th>
					<th>U8客户ID&nbsp;&nbsp;&nbsp;&nbsp;<select id="u8farentype"><option value="u8_supplier" selected="selected">供应商</option><option value="u8_buyer">采购商</option></select> </th>
					<th>操作</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td width="20%"><input id="cf-customer" type="number" maxlength="8" min="1" required="required" placeholder="请输入CRM客户ID"/></td>
					<td width="5%">==&gt;</td>
					<td width="20%"><input id="cf-u8_customer" type="number" maxlength="8" min="1" required="required" placeholder="请输入U8客户ID"/></td>
					<td width="55%">
						<a href="javascript:void(0)" id="cf-bindBtn" class="btn btn-info disabled">关联</a>
					</td>
				</tr>
			</tbody>
		</table>
		
		<hr/>
		
		<span class="text-info text-left">关联 主站产品 ID --&gt; U8 产品 ID</span>
		<table class="table table-condensed table-bordered table-hover">
			<thead>
				<tr class="info">
					<th>主站产品ID</th>
					<th>绑定</th>
					<th>U8产品ID</th>
					<th>操作</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td width="20%"><input id="gp-molbase_goods" type="number" maxlength="8" min="1" required="required" placeholder="请输入主站产品ID"/></td>
					<td width="5%">==&gt;</td>
					<td width="20%"><input id="gp-u8_product" type="number" maxlength="8" min="1" required="required" placeholder="请输入U8产品ID"/></td>
					<td width="55%"><a href="javascript:void(0)" id="gp-bindBtn" class="btn btn-info disabled" >关联</a></td>
				</tr>
			</tbody>
		</table>
		
	</div>
</div>
</fieldset>
<script type="text/javascript">
$(function(){
	$('#u8farentype').change(function(){
		var val = $('#cf-u8_customer').val();
		if(''!=val){
			$.post('${contextPath}/index.htm?action=hasId',{"metaType":$(this).val(),"id":val},function(result){
				if(false==result){ //该ID不存在
					$('#cf-u8_customer').val('');
					$("#alert-delete-div").removeClass("hidden");
					setTimeout('$("#alert-delete-div").addClass("hidden")',2500);
					$('#cf-bindBtn').toggleClass('disabled',true);
				}else{ //该ID存在
					var inputs = $("input[id^=cf-]");
					if($(inputs[0]).val()!='' && $(inputs[1]).val()!=''){ //判断两个都有值，启用关联按钮
						$('#cf-bindBtn').toggleClass('disabled',false);
					}else{
						$('#cf-bindBtn').toggleClass('disabled',true);
					}
				}
			});
		}
	});
	
	$('div[class=span12] input[type=number]').mouseleave(function(){
		var val = $(this).val();
		if(''!=val){
			var tt = $(this).attr('id').split("-")[0];
			var inputId = $(this).attr('id');
			var metaType = $(this).attr("id").split("-")[1];
			if(metaType=="u8_customer"){
				metaType = $('#u8farentype').val();
			}
			$.post('${contextPath}/index.htm?action=hasId',{"metaType":metaType,"id":$(this).val()},function(result){
				if(false==result){ //该ID不存在
					$('#'+inputId).val('');
					$("#alert-delete-div").removeClass("hidden");
					setTimeout('$("#alert-delete-div").addClass("hidden")',2500);
				}else{ //该ID存在
					var inputs = $("input[id^="+tt+"-]");
					if($(inputs[0]).val()!='' && $(inputs[1]).val()!=''){ //判断两个都有值，启用关联按钮
						$('#'+tt+'-bindBtn').toggleClass('disabled',false);
					}else{
						$('#'+tt+'-bindBtn').toggleClass('disabled',true);
					}
				}
			});
		}
	});
	
	$('div[class=span12] a[id$=bindBtn]').click(function(){
		//执行ID绑定操作
		switch($(this).attr("id").split("-")[0]){
		case "uc":
			$.post('${contextPath}/index.htm?action=doBind',{"metaTypeStr":"user@customer","source_id":$("#uc-user").val(),"target_id":$("#uc-customer").val()},function(result){
				onBindOver(result);
			});
			break;
		case "cf":
			var metaTypeStr = "customer@"+$('#u8farentype').val();
			$.post('${contextPath}/index.htm?action=doBind',{"metaTypeStr":metaTypeStr,"source_id":$("#cf-customer").val(),"target_id":$("#cf-u8_customer").val()},function(result){
				onBindOver(result);
			});
			break;
		case "gp":
			$.post('${contextPath}/index.htm?action=doBind',{"metaTypeStr":"molbase_goods@u8_product","source_id":$("#gp-molbase_goods").val(),"target_id":$("#gp-u8_product").val()},function(result){
				onBindOver(result);
			});
		default:
			break;
		}
	});
});

function onBindOver(result){
	if("binded"==result){ //弹出提示，已经绑定了！
		$("#alert-error-div").removeClass("hidden");
		setTimeout('$("#alert-error-div").addClass("hidden")',3000);
	}else{
		$('div[class=span12] input[type=number]').val('');
		$('div[class=span12] a[id$=bindBtn]').toggleClass('disabled',true);
		$("#alert-info-div").removeClass("hidden");
		setTimeout('$("#alert-info-div").addClass("hidden")',2500);
	}
}
</script>