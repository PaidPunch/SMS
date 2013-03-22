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
  <div style="text-align:center;">
      <img src="images/localcoop-logo-small.png" alt="LocalCoop">
  </div>
  
  <div id="form-section">
    <form class="form-horizontal">  
      <fieldset>   
        <div>
          <div class="control-group">  
            <label class="control-label" for="phone"><b>Phone</b></label>  
            <div class="controls">  
              <input type="text" class="input-large" id="phone" maxlength="12"> 
              <span class="help-inline">We need your phone to confirm your identity. Example 555-555-1212.</span>   
            </div>  
          </div>
          
          <div class="control-group">  
            <label class="control-label" for="email"><b>Email</b></label>  
            <div class="controls">  
              <input type="text" class="input-large" id="email">  
              <span class="help-inline">We need your email to send you the prize</span>  
            </div>  
          </div>
          
          <div class="form-actions">  
            <button id="claimprize-btn" type="submit" class="btn btn-primary">Claim Prize</button>   
          </div> 
        </div>
      </fieldset>
    </form>
  </div>
  
  <div id="thankyou-section" style="display:none;text-align:center;">
    <h3>
      Thanks! We'll be in touch soon.
    </h3>
  </div> 

  <!-- Javascript - Placed at the end of the document so the pages load faster -->
  <%@include file="script.html"%>
  <script>
  var prizeid = "<%= request.getParameter("PrizeCode") %>";
  </script>
  <script src="resources/js/claimprize.js"></script>
  <!-- END -->
</body>

</html>