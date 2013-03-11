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
    <div>
      <p>${name}</p>
      <img src="<%= request.getAttribute("logo") %>" alt="<%= request.getAttribute("name") %>">
      <p>${desc}</p>
      <p>${address}</p>
      <p><b>Discount: $ ${discount} off on purchases of $ ${minvalue} or more</b></p>
      <p><b>This offer will expire in:</b></p>
      
      <script language="JavaScript">
      TargetDate = "<%= request.getAttribute("expirydate") %>";
      BackColor = "white";
      ForeColor = "darkred";
      CountActive = true;
      CountStepper = -1;
      LeadingZero = true;
      DisplayFormat = "%%D%% Days, %%H%% Hours, %%M%% Minutes, %%S%% Seconds.";
      FinishMessage = "Coupon Expired!";
      </script>
      <script language="JavaScript" src="resources/js/countdown.js"></script>
    </div>
  </div>
</body>

</html>