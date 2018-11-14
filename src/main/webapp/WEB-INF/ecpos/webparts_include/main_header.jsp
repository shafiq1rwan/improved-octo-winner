<link rel="stylesheet" href="${pageContext.request.contextPath}/meta/css_responsive/mygroup.css">

<html>
<body class="font-roboto-regular">
  <header class="main-header">
    <!-- Logo -->
    <a href="${pageContext.request.contextPath}/ecpos/#!sales" class="logo">
      <span class="text-center"><img style="height:22px; width:20px;" src="${pageContext.request.contextPath}/meta/img/ecpos_logo.png"/><b style='color:white;font-size:1.3em;'>ECPOS</b></span>
    </a>
    
    <!-- Header Navbar: style can be found in header.less -->
    <nav class="navbar navbar-static-top">
      <!-- Sidebar toggle button-->
<!--        <a class="sidebar-toggle" data-toggle="offcanvas" role="button">
        <span class="sr-only">Toggle navigation</span>
      </a> -->
     
      <div class="navbar-custom-menu" class="text-center center-block">
<%--               <div style="height:100%">

                 <form action="${pageContext.request.contextPath}/member/logout" method="post">
				  <input type="submit" class="btn btn-default btn-flat" value="Sign Out" /> 
				  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				 </form> 
              </div> --%>    
      </div>
    </nav>
  </header>
</body>
</html>