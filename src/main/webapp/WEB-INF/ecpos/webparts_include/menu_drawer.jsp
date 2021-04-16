<%@ page import="mpay.ecpos_manager.general.utility.UserAuthenticationModel"%>

<%
	UserAuthenticationModel user = (UserAuthenticationModel) session.getAttribute("session_user");
%>

<!DOCTYPE html>
<html>
<body>
	<aside class="main-sidebar" style="border-right: 1px solid lightgrey;">
		<section class="sidebar">
			<ul class="sidebar-menu">

				<li class="header" style="font-size: 1em; background-color: #008B8B; color: white; /* font-weight: bold; */">Order Management</li>
				<%if (user.getStoreType() == 2) {%>
				<li>
					<a href="${pageContext.request.contextPath}/#!table_order">
						<i class="fa fa-desktop"></i> 
						<span>Table Order</span> 
						<span class="pull-right-container"></span>
					</a>
				</li>
				<%}%>
				<%if (user.getStoreType() == 3) {%>
				<li>
					<a href="${pageContext.request.contextPath}/#!table_order">
						<i class="fa fa-desktop"></i> 
						<span>Room Order</span> 
						<span class="pull-right-container"></span>
					</a>
				</li>
				<%}%>
				<%if (user.getStoreType() != 3) {%>
				<li>
					<a href="${pageContext.request.contextPath}/#!take_away_order">
						<i class="fa fa-shopping-bag"></i> 
					<%if (user.getStoreType() == 2) {%>
						<span>Take Away Order</span> 
					<%} else {%>
						<span>Purchase</span> 
					<%}%>
						<span class="pull-right-container"></span>
					</a>
				</li>
				<li>
					<a href="${pageContext.request.contextPath}/#!deposit_order">
						<i class="fa fa-save"></i> 
						<span>Deposit Order</span> 
						<span class="pull-right-container"></span>
					</a>
				</li>
				<%} %>
				<%if (user.getRoleType() == 1 || user.getRoleType() == 3) {%>
				<li class="header" style="font-size: 1em; background-color: #008B8B; color: white; /* font-weight: bold; */">Record Management</li>
				<%if (user.getStoreType() != 3) {%>
				<li>
					<a href="${pageContext.request.contextPath}/#!items_listing">
						<i class="fa fa-object-group"></i> 
						<span>Items Listing</span> 
						<span class="pull-right-container"> </span>
					</a>
				</li>
				<%} %>
				<li>
					<a href="${pageContext.request.contextPath}/#!checks_listing">
						<i class="fa fa-list"></i> 
						<span>Checks Listing</span> 
						<span class="pull-right-container"> </span>
					</a>
				</li>
				<li>
					<a href="${pageContext.request.contextPath}/#!transactions_listing">
						<i class="fa fa-exchange"></i> 
						<span>Transactions Listing</span> 
						<span class="pull-right-container"> </span>
					</a>
				</li>
				<li>
					<a href="${pageContext.request.contextPath}/#!reports"> 
						<i class="fa fa-file"></i> 
						<span>Reports</span> 
						<span class="pull-right-container"> </span>
					</a>
				</li>
				<%}%>
				<li class="header" style="font-size: 1em; background-color: #008B8B; color: white; /* font-weight: bold; */">System Management</li>
<%-- 				<li>
					<a href="${pageContext.request.contextPath}/#!connection_qr">
						<i class="fa fa-qrcode"></i> 
						<span>QR</span> 
						<span class="pull-right-container"> </span>
					</a>
				</li> --%>
				<%if (user.getRoleType() == 1 || user.getRoleType() == 3) {
					boolean drawer = (boolean)request.getAttribute("cashDrawer");
					if(drawer) {
				%>
				<li class="cashDrawerBtn">
					<a onclick="openDrawer()" style="cursor: pointer;">
						<i class="fa fa-money"></i>
						<span>Open Cash Drawer</span> 
						<span class="pull-right-container"></span>
					</a>
				</li>
				<%}%>
				<li>
					<a href="${pageContext.request.contextPath}/#!trackinghour"> 
						<i class="fa fa-user-clock"></i> 
						<span>Employee Performance</span> 
						<span class="pull-right-container"> </span>
					</a>
				</li>
				<li>
					<a href="${pageContext.request.contextPath}/#!stock"> 
						<i class="fa fa-fax"></i> 
						<span>e-Commerce Sales</span> 
						<span class="pull-right-container"> </span>
					</a>
				</li>
				<li>
					<a href="${pageContext.request.contextPath}/#!settings"> 
						<i class="fa fa-cog"></i>
						<span>Settings</span> 
						<span class="pull-right-container"> </span>
					</a>
				</li>
				<%}%>
				<li>
					<a href="${pageContext.request.contextPath}/signout"> 
						<i class="fa fa-sign-out"></i> 
						<span>Logout</span>
						<span class="pull-right-container"> </span>
					</a>
				</li>
				
			</ul>
		</section>
	</aside>
</body>

<script>
/* 	$('li.cashDrawerBtn').hide();
	
	$(document).ready(function() {
  			$.ajax({
		        type: 'GET',
		        url: '${pageContext.request.contextPath}/rc/configuration/get_cash_drawer_data/x',
		        dataType: "json",
		        async:false,
		        success: function(data) {
		        	console.log("hi my data " + data.device_manufacturer);
					if(data.device_manufacturer == 1){
						//sessionStorage.setItem("no_printing", true);
						//isNoPrinting = true;
						$('li.cashDrawerBtn').hide();
						
					}
					else  {
						//sessionStorage.setItem("no_printing", false);
						//isNoPrinting = false;
						$('li.cashDrawerBtn').show();
					}
		        },
		        error: function(data){
					if (data.status == 408) {
						alert("Session TIME OUT");
						window.location.href = "${pageContext.request.contextPath}/signout";
					}
					
		        }
		    });
	}); */

	function openDrawer() {
		$.ajax({
			type : 'post',
			url : '${pageContext.request.contextPath}/rc/configuration/open_cash_drawer',
			success : function(data) {
				if (data.response_code == '01') {
					/* alert(data.response_message); */
					Swal.fire('Oops...',data.response_message,'error');
					
				} else if(data.response_code == '02') {
					/* alert(data.response_message); */
					Swal.fire('Oops...',data.response_message,'error');
				}
			},
			error : function(jqXHR) {
				if (jqXHR.status == 408) {
					/* alert("Session TIME OUT"); */
					/* window.location.href = "${pageContext.request.contextPath}/signout"; */
					Swal.fire({
						  title: 'Oops...',
						  text: "Session Timeout",
						  icon: 'error',
						  showCancelButton: false,
						  confirmButtonColor: '#3085d6',
						  cancelButtonColor: '#d33',
						  confirmButtonText: 'OK'
						}).then((result) => {
						  if (result.value) {
							  window.location.href = "${pageContext.request.contextPath}/signout";
						  }
						});
				} else {
					/* alert('Drawer cannot open. Please kindly check the cash drawer printer.'); */
					Swal.fire({
						  title: 'Oops...',
						  text: "Session Timeout",
						  icon: 'error',
						  showCancelButton: false,
						  confirmButtonColor: '#3085d6',
						  cancelButtonColor: '#d33',
						  confirmButtonText: 'OK'
						}).then((result) => {
						  if (result.value) {
							  /* window.location.href = "${pageContext.request.contextPath}/signout"; */
							  Swal.fire('Drawer cannot open. Please kindly check the cash drawer printer.');
						  }
						});
				}
			}
		});
	}
</script>
