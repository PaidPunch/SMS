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
    <div>
      <img src="images/localcoop-logo-small.png" alt="LocalCoop">
    </div>
    <div>
      <h4>${name}</h4>
      <img src="<%= request.getAttribute("logo") %>" alt="<%= request.getAttribute("name") %>">
      <h4>${desc}</h4>
      <h4><a href="https://maps.google.com/?q=<%= request.getAttribute("address") %>">${address}</a></h4>
      <h4>Telephone: <a href="tel:<%= request.getAttribute("phone") %>">${phone}</a></h4>
      <h4><b>Discount: $ ${discount} off on purchases of $ ${minvalue} or more</b></h4>
      <h4><b>This offer will expire in:</b></h4>
      
      <h1>
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
      </h1>
    </div>
  </div>
</body>

  <!-- Mobile No Broswer Bar -->
  <script>
  var page = document.getElementById('page'),
  ua = navigator.userAgent,
  iphone = ~ua.indexOf('iPhone') || ~ua.indexOf('iPod'),
  ipad = ~ua.indexOf('iPad'),
  ios = iphone || ipad,
  // Detect if this is running as a fullscreen app from the homescreen
  fullscreen = window.navigator.standalone,
  android = ~ua.indexOf('Android'),
  lastWidth = 0;
 
  if (android) 
  {
    // Android's browser adds the scroll position to the innerHeight, just to
    // make this really fucking difficult. Thus, once we are scrolled, the
    // page height value needs to be corrected in case the page is loaded
    // when already scrolled down. The pageYOffset is of no use, since it always
    // returns 0 while the address bar is displayed.
    window.onscroll = function() 
    {
      page.style.height = window.innerHeight + 'px';
    }; 
  }
  
  var setupScroll = window.onload = function() {
    // Start out by adding the height of the location bar to the width, so that
    // we can scroll past it
    if (ios) {
      // iOS reliably returns the innerWindow size for documentElement.clientHeight
      // but window.innerHeight is sometimes the wrong value after rotating
      // the orientation
      var height = document.documentElement.clientHeight;
      // Only add extra padding to the height on iphone / ipod, since the ipad
      // browser doesn't scroll off the location bar.
      if (iphone && !fullscreen) height += 85;
      page.style.height = height + 'px';
    } else if (android) {
      // The stock Android browser has a location bar height of 56 pixels, but
      // this very likely could be broken in other Android browsers.
      page.style.height = (window.innerHeight + 56) + 'px';
    }
    // Scroll after a timeout, since iOS will scroll to the top of the page
    // after it fires the onload event
    setTimeout(scrollTo, 0, 0, 1);
  };
  (window.onresize = function() 
  {
    var pageWidth = page.offsetWidth;
    // Android doesn't support orientation change, so check for when the width
    // changes to figure out when the orientation changes
    if (lastWidth == pageWidth) return;
    lastWidth = pageWidth;
    setupScroll();
  })();
  </script>
  
  <!-- END -->
  
  <!-- Le javascript
  ================================================== -->
  <!-- Placed at the end of the document so the pages load faster -->
  <script src="resources/js/jquery.js"></script>
  <script src="resources/js/bootstrap-transition.js"></script>
  <script src="resources/js/bootstrap-alert.js"></script>
  <script src="resources/js/bootstrap-modal.js"></script>
  <script src="resources/js/bootstrap-dropdown.js"></script>
  <script src="resources/js/bootstrap-scrollspy.js"></script>
  <script src="resources/js/bootstrap-tab.js"></script>
  <script src="resources/js/bootstrap-tooltip.js"></script>
  <script src="resources/js/bootstrap-popover.js"></script>
  <script src="resources/js/bootstrap-button.js"></script>
  <script src="resources/js/bootstrap-collapse.js"></script>
  <script src="resources/js/bootstrap-carousel.js"></script>
  <script src="resources/js/bootstrap-typeahead.js"></script>
</body>

</html>