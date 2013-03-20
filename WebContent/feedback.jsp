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
  
  <div style="text-align:center;" class="container">
    <div>
      <img src="images/animatedlogo-small.gif" alt="LocalCoop">
    </div>
    <div>
      <div id="feedback-section" class="control-group">  
        <div>
          <label class="control-label" for="feedback">Please let us know why you were not interested in this offer</label>
        </div>  
        <div class="controls">  
          <select id="feedback">  
            <option>Offer could be better</option>  
            <option>Not my kind of business</option>  
            <option>Business is too far away</option>  
            <option>I'm scared of chickens!</option>  
          </select>  
        </div>  
        <a id="feedback-btn" class="btn btn-large btn-primary" href="#">Submit</a>
      </div> 
      <div id="thankyou-section"  style="display:none;">
        <h3>
          Thanks for providing your feedback!
        </h3>
      </div> 
    </div>
  </div>
  
  <!-- Javascript - Placed at the end of the document so the pages load faster -->
  <%@include file="script.html"%>
  <script src="resources/js/feedback.js"></script>
  <script>
    // Some variables for feedback
    var offerid = "<%= request.getAttribute("offercode") %>";
    var type = "1";
  </script>
  <!-- END -->
</body>

</html>