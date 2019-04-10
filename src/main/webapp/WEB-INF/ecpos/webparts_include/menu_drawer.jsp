<%
	int storeType = (int) session.getAttribute("storeType");
%>

<!DOCTYPE html>
<html>
<body>
	<aside class="main-sidebar">
		<section class="sidebar">
			<ul class="sidebar-menu">

				<li class="header" style="font-size: 1em; color: white; font-weight: bold;">Order Management</li>
				<%if (storeType == 2) {%>
				<li>
					<a href="${pageContext.request.contextPath}/ecpos/#!table_order">
						<i class="fa fa-desktop"></i> 
						<span>Table Order</span> 
						<span class="pull-right-container"></span>
					</a>
				</li>
				<%}%>
				<li>
					<a href="${pageContext.request.contextPath}/ecpos/#!take_away_order">
						<i class="fa fa-shopping-bag"></i> 
					<%if (storeType == 2) {%>
						<span>Take Away Order</span> 
					<%} else {%>
						<span>Purchase</span> 
					<%}%>
						
						<span class="pull-right-container"></span>
					</a>
				</li>
				
				<li class="header" style="font-size: 1em; color: white; font-weight: bold;">Record Management</li>
				<li>
					<a href="${pageContext.request.contextPath}/ecpos/#!items_listing">
						<i class="fa fa-object-group"></i> 
						<span>Items Listing</span> 
						<span class="pull-right-container"> </span>
					</a>
				</li>
				<li>
					<a href="${pageContext.request.contextPath}/ecpos/#!checks_listing">
						<i class="fa fa-list"></i> 
						<span>Checks Listing</span> 
						<span class="pull-right-container"> </span>
					</a>
				</li>
				<li>
					<a href="${pageContext.request.contextPath}/ecpos/#!transactions_listing">
						<i class="fa fa-exchange"></i> 
						<span>Transactions Listing</span> 
						<span class="pull-right-container"> </span>
					</a>
				</li>
				<li>
					<a href="${pageContext.request.contextPath}/ecpos/#!reports"> 
						<i class="fa fa-file"></i> 
						<span>Reports</span> 
						<span class="pull-right-container"> </span>
					</a>
				</li>
				
				<li class="header" style="font-size: 1em; color: white; font-weight: bold;">System Management</li>
<%-- 				<li>
					<a href="${pageContext.request.contextPath}/ecpos/#!connection_qr">
						<i class="fa fa-qrcode"></i> 
						<span>QR</span> 
						<span class="pull-right-container"> </span>
					</a>
				</li> --%>
				<li>
					<a onclick="openDrawer()">
						<i class="fa fa-money"></i>
						<span>Open Cash Drawer</span> 
						<span class="pull-right-container"></span>
					</a>
				</li>
				<li>
					<a href="${pageContext.request.contextPath}/ecpos/#!settings"> 
						<i class="fa fa-cog"></i>
						<span>Settings</span> 
						<span class="pull-right-container"> </span>
					</a>
				</li>
				<li>
					<a href="${pageContext.request.contextPath}/ecpos/logout"> 
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
	function openDrawer() {
		$.ajax({
			type : 'post',
			url : '${pageContext.request.contextPath}/rc/configuration/open_cash_drawer',
			success : function(data) {
				if (data.response_code == 01) {
					alert(data.response_message);
				}
			},
			error : function() {
				alert('Drawer cannot be open. Please kindly check your printer.');
			}
		});
	}
</script>
