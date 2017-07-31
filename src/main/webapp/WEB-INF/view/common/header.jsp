<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<style>
body{
	padding-top: 37px;
}
fieldset{
	border:1px solid #317ee7;
	margin:8px;
}
hr{
	border:1px solid #317ee7;
}
.tr_gc{
	background-color:#FFDEAD;
}
</style>
<!-- 顶部导航条 -->
<div class="navbar navbar-fixed-top bs-docs-nav" role="banner" style="z-index:0">
  
    <div class="conjtainer">
      <!-- Menu button for smallar screens -->
      <div class="navbar-header">
		  <button class="navbar-toggle btn-navbar" type="button" data-toggle="collapse" data-target=".bs-navbar-collapse">
			<span>Menu</span>
		  </button>
		  <!-- Site name for smallar screens -->
		  <a href="#" class="navbar-brand hidden-lg">MacBeth</a>
		</div>
      
      <!-- Navigation starts -->
      <nav class="collapse navbar-collapse bs-navbar-collapse" role="navigation">         

        <ul class="nav navbar-nav">  

          <!-- Upload to server link. Class "dropdown-big" creates big dropdown -->
          <li class="dropdown dropdown-big">
            <a href="${contextPath}/index.htm" class="dropdown-toggle">
            <span class="label label-success"><i class="fa fa-cloud-upload"></i></span> Home</a>
          </li>
          
        </ul>

        <!-- Links -->
        <ul class="nav navbar-nav pull-right">
          <li class="dropdown pull-right">            
            <a data-toggle="dropdown" class="dropdown-toggle" href="javascript:void(0)">
              <i class="fa fa-user"></i> ${sessionScope.user.user_name} <b class="caret"></b>
            </a>
            <ul class="dropdown-menu">
              <li><a href="${contextPath}/login.htm"><i class="fa fa-sign-out"></i> 退出</a></li>
            </ul>
          </li>
        </ul>
      </nav>

    </div>
</div>
<!-- 顶部导航条 结束 -->

<!-- Header starts -->
<header>
<div class="container">
    <div class="row">
        <h1><a href="${contextPath}/index.htm">MOLBASE DTS<span class="bold"> 关系管理后台</span></a></h1>
	</div>
</div>
</header>
<!-- Header ends -->