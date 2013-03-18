<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="description" content="">
    <meta name="author" content="">
    <!-- No browser bar -->
    <meta name=viewport content="width=device-width, initial-scale=1.0, maximum-scale=1">
    <meta name=apple-mobile-web-app-capable content=yes>
    <meta name=apple-mobile-web-app-status-bar-style content=black>
    <title>LocalCoop</title>
    <%@include file="style.html"%> 
</head>

<body onload="getBusinessTable()">

  <div style="text-align:center;">
      <img src="../images/localcoop-logo-small.png" alt="LocalCoop">
  </div>
  
  <div id="content" class="container-fluid" style="padding-top:2em;padding-bottom:2em;">
    <div id="content-row-1" class="row-fluid">
      <div id="mainContainer" class="span10"> 
        <div id='businesslist'></div>
      </div>
      
      <div id="sidebar" class="span2">
        <a id="newbiz-btn" class="btn btn-large btn-primary btn-warning" href="#">Add New Business</a>
      </div>
    </div>  <!-- content-row-1 -->
  </div>  <!-- content -->

  <!-- Javascript - Placed at the end of the document so the pages load faster -->
  <%@include file="script.html"%>
  <script src="../resources/js/admin.js"></script>
</body>
</html>