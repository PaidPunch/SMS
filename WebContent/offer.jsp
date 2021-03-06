<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
  <title>LocalCoop</title>
  <%@include file="meta.html"%>   
  <%@include file="style.html"%>     
</head>

<body>

  <script language="JavaScript">
      var lastFired = new Date().getTime();
      setInterval(function() 
      {
          now = new Date().getTime();
          if(now - lastFired > 5000) 
          {
             // if it's been more than 5 seconds
             document.location.reload(true);
          }
          lastFired = now;
      }, 500);
    </script>
  
  <div style="text-align:center;" class="container">
    <div>
      <img src="images/animatedlogo-small.gif" alt="LocalCoop">
      <div style="margin-top: 10px;">
        ${prize}
      </div>
    </div>
    <div>
      <div id="egg-section">
        <br>
        <a href="#" id="displayRedeem" onClick="clickRedeem('<%= request.getAttribute("offercode") %>');">
          <div class="eggImg" id="egg-image">
            <div class="eggText" id="egg-text">
              <h3>Congratulations!</h3>
                 <h4>You won a prize from<br>
                     ${name}.<br>
                     Tap on the egg to reveal it!</h4>
            </div>
          </div>
        </a>
      </div>
      <div id="button-section" style="display:none;">
        <h3>${name}</h3>
        <h4 style="color:red;">${offer}</h4>
        <h4>
          <b>
            Offer expires in: 
            <script language="JavaScript">
            TargetDate = "<%= request.getAttribute("expirydate") %>";
            BackColor = "transparent";
            ForeColor = "red";
            CountActive = true;
            CountStepper = -1;
            LeadingZero = true;
            DisplayFormat = "%%H%%:%%M%%:%%S%%";
            FinishMessage = "Coupon Expired!";
            </script>
            <script language="JavaScript" src="resources/js/countdown.js"></script>
          </b>
        </h4>
        
        <p>
          <a class="btn btn-large" href="feedback?Code=<%= request.getAttribute("offercode") %>">No Thanks</a>
          &nbsp;
          <a class="btn btn-large btn-primary" href="<%= request.getAttribute("redeemlink") %>">Redeem</a>
        </p>
        
        <h4><u>About ${name}</u></h4>
        ${logo}
        <h4>${desc}</h4>
        <h4><a href="https://maps.google.com/?q=<%= request.getAttribute("address") %>">${address}</a></h4>
        <h4><a href="tel:<%= request.getAttribute("phone") %>">${phone}</a></h4>
      </div>
    </div>
  </div>
  
  <!-- Javascript - Placed at the end of the document so the pages load faster -->
  <%@include file="script.html"%>
  <script src="resources/js/offer.js"></script>
  <script>
    if (<%= request.getAttribute("displayoffer") %> == 1)
    {
       displayOffer();
    }
  </script>  
  <!-- END -->
</body>

</html>