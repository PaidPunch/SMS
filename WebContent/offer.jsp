<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>LocalCoop</title>
<%@include file="style.html"%>
</head>

<body>
  <div style="text-align:center;">
    <div>
      <img src="images/localcoop-logo-small.png" alt="LocalCoop">
    </div>
    <a href="<%= request.getAttribute("redeemlink") %>">
      <div class="eggImg">
        <div class="eggText">
          <p>Crack the egg to get an offer from ${business_name}</p>
        </div>
      </div>
    </a>
  </div>
</body>

</html>