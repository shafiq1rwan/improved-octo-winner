<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<html>
<body>
  <aside class="main-sidebar">
    <section class="sidebar">
<%--       <ul class="sidebar-menu">
 
        <li class="header" style="font-size:1em;color:white;font-weight:bold;">Menu</li>
        <!-- <li id="appointment" class="active"> -->
        
        <!-- <li data-toggle="offcanvas"> -->
        <li>
          <a href="${pageContext.request.contextPath}/ecpos/#!tableOrder">
            <i class="fa fa-desktop"></i> <span>Table Order</span>
            <span class="pull-right-container">
            </span>
          </a>
        </li>
        
        <li>
          <a href="${pageContext.request.contextPath}/ecpos/#!takeAwayOrder">
            <i class="fa fa-shopping-bag"></i> <span>Takeaway Order</span>
            <span class="pull-right-container">
            </span>
          </a>
        </li>
        
        <li>
          <a href="${pageContext.request.contextPath}/ecpos/#!payment">
            <i class="fa fa-desktop"></i> <span>Payment</span>
            <span class="pull-right-container">
            </span>
          </a>
        </li>
               
        <li>
          <a href="${pageContext.request.contextPath}/ecpos/#!transactionList">
            <i class="fa fa-file-text-o"></i> <span>Transactions</span>
            <span class="pull-right-container">
            </span>
          </a>
        </li>
        <li>
          <a href="${pageContext.request.contextPath}/ecpos/#!itemsManagement">
            <i class="fa fa-archive"></i> <span>Items</span>
            <span class="pull-right-container">
            </span>
          </a>
        </li>
        <li>
          <a href="${pageContext.request.contextPath}/ecpos/#!generateReport">
            <i class="fa fa-calculator"></i> <span>Report</span>
            <span class="pull-right-container">
            </span>
          </a>
        </li>
      	<li class="header" style="font-size:1em;color:white;font-weight:bold;">System</li>
      	<li>
          <a href="${pageContext.request.contextPath}/ecpos/#!clientQRConnection">
            <i class="fa fa-qrcode"></i> <span>QR</span>
            <span class="pull-right-container">
            </span>
          </a>
        </li>
        
        <li>
          <a onclick="openDrawer()">
            <i class="fa fa-print"></i> <span>Open Drawer</span>
            <span class="pull-right-container">
            </span>
          </a>
        </li>
              
        <li>
          <a href="${pageContext.request.contextPath}/member/#!printer_config">
            <i class="fa fa-print"></i> <span>Printer</span>
            <span class="pull-right-container">
            </span>
          </a>
        </li>
        
        <li>
          <a href="${pageContext.request.contextPath}/ecpos/#!configSetting">
            <i class="fa fa-gear"></i><span>Setting</span>
            <span class="pull-right-container">
            </span>
          </a>
        </li>
      
      	<li>
          <a onclick="Logout()">
            <i class="fa fa-key"></i> <span>Logout</span>
            <span class="pull-right-container">
            </span>
          </a>
        </li>
      </ul>
 --%>    
 
       <ul class="sidebar-menu">
 
        <li class="header" style="font-size:1em;color:white;font-weight:bold;">Menu</li>
        <!-- <li id="appointment" class="active"> -->
        
        <!-- <li data-toggle="offcanvas"> -->
        <li>
          <a href="${pageContext.request.contextPath}/ecpos/#!sales">
            <i class="fa fa-desktop"></i> <span>Table Order</span>
            <span class="pull-right-container">
              <!-- <small class="label pull-right bg-green">10</small> -->
            </span>
          </a>
        </li>
        
        <li>
          <a href="${pageContext.request.contextPath}/ecpos/#!take_away_order">
            <i class="fa fa-shopping-bag"></i> <span>Take Away Order</span>
            <span class="pull-right-container">
              <!-- <small class="label pull-right bg-green">10</small> -->
            </span>
          </a>
        </li>
        
<%--         <li>
          <a href="${pageContext.request.contextPath}/ecpos/#!payment">
            <i class="fa fa-desktop"></i> <span>Payment (Test)</span>
            <span class="pull-right-container">
              <!-- <small class="label pull-right bg-green">10</small> -->
            </span>
          </a>
        </li> --%>
               
        <li>
          <a href="${pageContext.request.contextPath}/ecpos/#!trans">
            <i class="fa fa-file-text-o"></i> <span>Transactions</span>
            <span class="pull-right-container">
            </span>
          </a>
        </li>
        <li>
          <a href="${pageContext.request.contextPath}/ecpos/#!items">
            <i class="fa fa-archive"></i> <span>Items</span>
            <span class="pull-right-container">
            </span>
          </a>
        </li>
        <li>
          <a href="${pageContext.request.contextPath}/ecpos/#!reports">
            <i class="fa fa-calculator"></i> <span>Report</span>
            <span class="pull-right-container">
            </span>
          </a>
        </li>
      	<li class="header" style="font-size:1em;color:white;font-weight:bold;">System</li>
      	<li>
          <a href="${pageContext.request.contextPath}/ecpos/#!connection_qr">
            <i class="fa fa-qrcode"></i> <span>QR</span>
            <span class="pull-right-container">
            </span>
          </a>
        </li>
        
        <li>
          <a onclick="openDrawer()">
            <i class="fa fa-print"></i> <span>Open Drawer</span>
            <span class="pull-right-container">
            </span>
          </a>
        </li>
              
      <%--   <li>
          <a href="${pageContext.request.contextPath}/member/#!printer_config">
            <i class="fa fa-print"></i> <span>Printer</span>
            <span class="pull-right-container">
            </span>
          </a>
        </li> --%>
        
        <li>
          <a href="${pageContext.request.contextPath}/ecpos/#!setting">
            <i class="fa fa-gear"></i><span>Setting</span>
            <span class="pull-right-container">
            </span>
          </a>
        </li>
      
      	<li>
          <a onclick="Logout()">
            <i class="fa fa-key"></i> <span>Logout</span>
            <span class="pull-right-container">
            </span>
          </a>
        </li>
      </ul>
 
 
 
 
 
 
 </section>
    <!-- /.sidebar -->
  </aside>
  
  <script>
function Logout() {
	$.ajax({
		type: 'post',
        url: '${pageContext.request.contextPath}/ecpos/logout',
        success: function( data, textStatus, jQxhr) {
        	alert('You have successfully logout! Proceed to Login Page.');
        	$(location).attr('href', '${pageContext.request.contextPath}/ecpos');
        },
        error: function( jqXhr, textStatus, errorThrown ){
        	alert(textStatus);
        }
	});
}

function openDrawer(){
	$.ajax({
		type: 'post',
        url: '${pageContext.request.contextPath}/printerapi/open_cash_drawer',
        success: function( data, textStatus, jQxhr) {
        	
        },
        error: function(){
        	alert('Drawer cannot be open. Please kindly check your printer.');
        }
	});
} 


</script>
</body>



