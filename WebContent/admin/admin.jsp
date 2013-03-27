<!DOCTYPE html>
<html>
<head>
  <title>LocalCoop</title>
  <%@include file="style.html"%> 
  <%@include file="meta.html"%> 
</head>

<body>

  <!-- .height-wrapper -->
  <div class="height-wrapper">
    
    <!-- header -->
    <header>
      <!-- tool bar -->
      <div id="header-toolbar" class="container-fluid">
        <!-- .contained -->
        <div class="contained">
          
          <!-- theme name -->
          <h1> Administrative Dashboard </h1>
          <!-- end theme name -->
          
          <!-- span4 -->
          <div class="pull-right">
            <!-- demo theme switcher-->
            <div id="theme-switcher" class="btn-toolbar">
              
              <!-- theme dropdown -->
              <div class="btn-group hidden-phone">
                <a href="javascript:void(0)" class="btn btn-small btn-inverse" id="reset-widget"><i class="icon-refresh"></i></a>
                <a href="javascript:void(0)" class="btn btn-small btn-inverse dropdown-toggle" data-toggle="dropdown">Themes <span class="caret"></span></a>
                <ul id="theme-links-js" class="dropdown-menu toolbar pull-right">
                  <li>
                    <a href="javascript:void(0)" data-rel="purple"><i class="icon-sign-blank purple-icon"></i>Royal Purple</a>
                  </li>
                  <li>
                    <a href="javascript:void(0)" data-rel="blue"><i class="icon-sign-blank navyblue-icon"></i>Navy Blue</a>
                  </li>
                  <li>
                    <a href="javascript:void(0)" data-rel="green"><i class="icon-sign-blank green-icon "></i>Emerald</a>
                  </li>
                  <li>
                    <a href="javascript:void(0)" data-rel="darkred"><i class="icon-sign-blank red-icon "></i>Dark Rose</a>
                  </li>
                  <li>
                    <a href="javascript:void(0)" data-rel="default"><i class="icon-sign-blank grey-icon"></i>Default</a>
                  </li>
                </ul>
              </div>
              <!-- end theme dropdown-->
              
            </div>
            <!-- end demo theme switcher-->
          </div>
          <!-- end span4 -->
        </div>
        <!-- end .contained -->
      </div>
      <!-- end tool bar -->
      
    </header>
    <!-- end header -->
  
    <div id="main" role="main" class="container-fluid">
      <div class="contained">
        
        <!-- aside -->  
        <aside> 
        
          <!-- aside item: Logo -->
          <div>
            <span><img src="img/localcoop-logo-small.png" alt="LocalCoop"></span>
          </div>
          <div class="divider"></div>
          <!-- end aside item: Logo -->
  
          <!-- aside item: Menu -->
          <div class="sidebar-nav-fixed">
            
            <ul class="menu" id="accordion-menu-js">
              <li class="current">
                <a href="admin.jsp"><i class="icon-envelope"></i>Dashboard</a>
              </li>
              <li class="">
                <a href="javascript:void(0)"><i class="icon-check"></i>Businesses<span class="badge">2</span></a>
                <ul>
                  <li>
                    <a href="businesses.jsp">Current businesses</a>
                  </li>
                  <li>
                    <a href="singlebusiness.jsp">Add new business</a>
                  </li>
                </ul>
              </li>
              <li class="">
                <a href=""><i class="icon-envelope"></i>Prizes</a>
              </li>
            </ul>
          </div>
          
          <div class="divider"></div>
          <!-- end aside item: Menu -->
        </aside>
        <!-- aside end -->
        
        <!-- main content -->
        <div id="page-content">
          <!-- page header -->
          <h1 id="page-header">Dashboard</h1> 
  
          <div class="alert adjusted alert-info">
            <button class="close" data-dismiss="alert">×</button>
            <i class="cus-exclamation"></i>
            <strong>Welcome to the LocalCoop admin dashboard</strong>. Please excuse our construction dust.
          </div>
          
          <div class="fluid-container">
          
            <!-- widget grid -->
            <section id="widget-grid" class="">
            
              <div id="content-row-1" class="row-fluid">    
                <!-- new widget -->
                <div class="jarviswidget" id="widget-id-0">
                  <header>
                      <h2>Weekly usage over the past 2 months</h2>                           
                  </header>
                  <div>
                    <div class="jarviswidget-editbox">
                      <div>
                          <label>Title:</label>
                          <input type="text" />
                      </div>
                      <div>
                          <label>Styles:</label>
                          <span data-widget-setstyle="purple" class="purple-btn"></span>
                          <span data-widget-setstyle="navyblue" class="navyblue-btn"></span>
                          <span data-widget-setstyle="green" class="green-btn"></span>
                          <span data-widget-setstyle="yellow" class="yellow-btn"></span>
                          <span data-widget-setstyle="orange" class="orange-btn"></span>
                          <span data-widget-setstyle="pink" class="pink-btn"></span>
                          <span data-widget-setstyle="red" class="red-btn"></span>
                          <span data-widget-setstyle="darkgrey" class="darkgrey-btn"></span>
                          <span data-widget-setstyle="black" class="black-btn"></span>
                      </div>
                    </div>
          
                    <div class="inner-spacer"> 
                      <!-- content goes here -->
          
                      <!-- sin chart -->
                      <div id="monthly-bar-chart" class="chart"></div>
          
                    </div>
                  </div>
                </div>
                <!-- end widget -->
              </div>  <!-- content-row-1 -->
              
              <div id="content-row-2" class="row-fluid">    
                <!-- new widget -->
                <div class="jarviswidget" id="widget-id-0">
                  <header>
                      <h2>Daily usage over the past week</h2>                           
                  </header>
                  <div>
                    <div class="jarviswidget-editbox">
                      <div>
                          <label>Title:</label>
                          <input type="text" />
                      </div>
                      <div>
                          <label>Styles:</label>
                          <span data-widget-setstyle="purple" class="purple-btn"></span>
                          <span data-widget-setstyle="navyblue" class="navyblue-btn"></span>
                          <span data-widget-setstyle="green" class="green-btn"></span>
                          <span data-widget-setstyle="yellow" class="yellow-btn"></span>
                          <span data-widget-setstyle="orange" class="orange-btn"></span>
                          <span data-widget-setstyle="pink" class="pink-btn"></span>
                          <span data-widget-setstyle="red" class="red-btn"></span>
                          <span data-widget-setstyle="darkgrey" class="darkgrey-btn"></span>
                          <span data-widget-setstyle="black" class="black-btn"></span>
                      </div>
                    </div>
          
                    <div class="inner-spacer"> 
                      <!-- content goes here -->
          
                      <!-- sin chart -->
                      <div id="weekly-bar-chart" class="chart"></div>
          
                    </div>
                  </div>
                </div>
                <!-- end widget -->
              </div>  <!-- content-row-2 -->
            </section>
          </div>
          
        </div>
        <!-- end main content -->
        
        <!-- aside right on high res -->
        <aside class="right">
          <!-- aside item: Tiny Stats -->
          <div class="number-stats">
            <ul>
              <li>Filters<span>forthcoming</span></li>
            </ul>
          </div>
          <div class="divider"></div>
          <!-- end aside item: Tiny Stats -->
          
        </aside>
        <!-- end aside right -->
        
      </div>
        
    </div><!--end fluid-container-->
    <div class="push"></div>
    
  </div>
  <!-- end .height wrapper -->

  <!-- Javascript - Placed at the end of the document so the pages load faster -->
  <%@include file="script.html"%>
  <script src="js/admin.js"></script>
</body>
</html>