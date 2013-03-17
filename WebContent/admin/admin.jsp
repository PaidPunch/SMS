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

<body>

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
  <script language="JavaScript">
    //Get array of classes without jQuery
    var array = new Array();
    array[0] = "Saab";
    array[1] = "Volvo";
    array[2] = "BMW";
  
    var arrayLength = array.length;
    var theTable = document.createElement('table');
    var att=document.createAttribute("class");
    att.value="table table-striped";
    theTable.setAttributeNode(att);
    
    // Note, don't forget the var keyword!
    for (var i = 0, tr, td; i < arrayLength; i++) {
        tr = document.createElement('tr');
        td = document.createElement('td');
        td.appendChild(document.createTextNode(array[i]));
        tr.appendChild(td);
        theTable.appendChild(tr);
    }
  
    document.getElementById('businesslist').appendChild(theTable);
  
  </script>
  <%@include file="script.html"%>
</body>
</html>