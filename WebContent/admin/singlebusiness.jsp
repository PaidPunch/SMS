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
  
  <form class="form-horizontal">  
    <fieldset>   
      <div>
        <div class="control-group">  
          <label class="control-label" for="business_name">Name</label>  
          <div class="controls">  
            <input type="text" class="input-xlarge" id="business_name">  
          </div>  
        </div>
        
        <div class="control-group">  
          <label class="control-label" for="business_desc">Description</label>  
          <div class="controls">  
            <input type="text" class="input-xxlarge" id="business_desc">  
          </div>  
        </div>
        
        <div class="control-group">  
          <label class="control-label" for="business_category">Category</label>  
          <div class="controls">  
            <input type="text" class="input-xlarge" id="business_category">  
            <span class="help-inline">Only one for now, e.g. Korean, Barbeque, etc</span>  
          </div>  
        </div>
        
        <div class="control-group">  
          <label class="control-label" for="url_path">Website</label>  
          <div class="controls">  
            <input type="text" class="input-xlarge" id="url_path">  
          </div>  
        </div>
        
        <div class="control-group">  
          <label class="control-label" for="business_enabled">Enabled</label>  
          <div class="controls">  
            <label class="checkbox">  
              <input type="checkbox" id="business_enabled" value="1" checked>  
            </label>  
          </div>  
        </div>  
      </div>
      
      <div style="background-color:#C0C0C0; padding-top:1em; padding-bottom:1em">
        <div class="control-group">  
          <label class="control-label" for="business_address">Address</label>  
          <div class="controls">  
            <input type="text" class="input-xxlarge" id="business_address">  
          </div>  
        </div>
        
        <div class="control-group">  
          <label class="control-label" for="business_phone">Phone</label>  
          <div class="controls">  
            <input type="text" class="input-large" id="business_phone">  
          </div>  
        </div>
        
        <div class="control-group">  
          <label class="control-label" for="business_group">Group</label>  
          <div class="controls">  
            <input type="text" class="input-large" id="business_group">  
            <span class="help-inline">e.g. REDMOND or GREENWOOD</span>  
          </div>  
        </div>
        
        <div class="control-group">  
          <label class="control-label" for="business_code">Business Code</label>  
          <div class="controls">  
            <input type="text" class="input-large" id="business_code">  
            <span class="help-inline">e.g. KALBI or NEST</span>  
          </div>  
        </div>
      </div>
      
      <div style="padding-top:1em">
        <div class="control-group">  
          <label class="control-label" for="business_offer">Offer</label>  
          <div class="controls">  
            <input type="text" class="input-xxlarge" id="business_offer">  
          </div>  
        </div>
        
        <div class="control-group">  
          <label class="control-label" for="business_coupon_code">Coupon Code</label>  
          <div class="controls">  
            <input type="text" class="input-large" id="business_coupon_code">  
          </div>  
        </div>
      </div>
      
      <div class="form-actions">  
        <button id="createbusiness-btn" type="submit" class="btn btn-primary">Save changes</button>   
      </div>  
    </fieldset>
  </form>  
  
  <!-- Javascript - Placed at the end of the document so the pages load faster -->
  <%@include file="script.html"%>
  <script src="../resources/js/singlebusiness.js"></script>
</body>

</html>