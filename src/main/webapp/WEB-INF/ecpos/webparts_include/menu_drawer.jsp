<!DOCTYPE html>
<html>
<body>
	<aside class="main-sidebar">
		<section class="sidebar">
			<ul class="sidebar-menu">

				<li class="header" style="font-size: 1em; color: white; font-weight: bold;">Order Management</li>
				<li>
					<a href="${pageContext.request.contextPath}/ecpos/#!table_order">
						<i class="fa fa-desktop"></i> 
						<span>Table Order</span> 
						<span class="pull-right-container"></span>
					</a>
				</li>
				<li>
					<a href="${pageContext.request.contextPath}/ecpos/#!take_away_order">
						<i class="fa fa-shopping-bag"></i> 
						<span>Take Away Order</span> 
						<span class="pull-right-container"></span>
					</a>
				</li>
				<li>
					<a href="${pageContext.request.contextPath}/ecpos/#!trans">
						<i class="fa fa-exchange"></i> 
						<span>Transactions</span> 
						<span class="pull-right-container"> </span>
					</a>
				</li>
				<li>
					<a href="${pageContext.request.contextPath}/ecpos/#!items">
						<i class="fa fa-object-group"></i> 
						<span>Items</span> 
						<span class="pull-right-container"> </span>
					</a>
				</li>
				<li>
					<a href="${pageContext.request.contextPath}/ecpos/#!reports"> 
						<i class="fa fa-file"></i> 
						<span>Report</span> 
						<span class="pull-right-container"> </span>
					</a>
				</li>
				
				<li class="header" style="font-size: 1em; color: white; font-weight: bold;">System Management</li>
				<li>
					<a href="${pageContext.request.contextPath}/ecpos/#!connection_qr">
						<i class="fa fa-qrcode"></i> 
						<span>QR</span> 
						<span class="pull-right-container"> </span>
					</a>
				</li>
				<li>
					<a onclick="openDrawer()">
						<i class="fa fa-print"></i>
						<span>Open Drawer</span> 
						<span class="pull-right-container"></span>
					</a>
				</li>
				<li>
					<a href="${pageContext.request.contextPath}/ecpos/#!setting"> 
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
			url : '${pageContext.request.contextPath}/printerapi/open_cash_drawer',
			success : function(data, textStatus, jQxhr) {
				//To Do
			},
			error : function() {
				alert('Drawer cannot be open. Please kindly check your printer.');
			}
		});
	}
</script>
