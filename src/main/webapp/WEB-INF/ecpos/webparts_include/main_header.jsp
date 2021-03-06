<!DOCTYPE html>
<%@ page
	import="mpay.ecpos_manager.general.utility.UserAuthenticationModel"%>
<%@ page import="mpay.ecpos_manager.general.constant.Constant"%>
<%
	UserAuthenticationModel user = (UserAuthenticationModel) session.getAttribute("session_user");
	System.out.println("user kds hdr = " + user.getName());
	System.out.println("user storename hdr = " + user.getStoreName());
%>
<html>
<head>
<style>
div.containerkds {
	/* height: 50px; */
	position: relative;
	text-align: left;
}

div.containerkds p {
	position: absolute;
	margin-top: 7px;
	margin-left: 2px;
	height: 100%;
	width: 100%;
	color: white;
	font-size: 1.8em;
	/* font-weight: bold; */
}
</style>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/meta/css_responsive/mygroup.css">
</head>

<body class="font-roboto-regular">
	<header class="main-header">
		<!-- Logo -->
		<a href="${pageContext.request.contextPath}/" class="logo"> <span
			class="text-center"> <img style="height: 30px; width: 28px;"
				src="${pageContext.request.contextPath}/meta/img/ecpos_logo.png" />&nbsp;&nbsp;&nbsp;
				<b style='color: white; font-size: 0.8em;'>VERNPOS</b>
		</span>
		</a>

		<!-- Header Navbar: style can be found in header.less -->
		<nav class="navbar navbar-static-top">
			<%
				if (user.getRoleType() != Constant.KITCHEN_ROLE) {
			%>
			<a class="sidebar-toggle" data-toggle="push-menu" role="button">
				<span class="sr-only">Toggle navigation</span>
			</a>
			<%
				}
				if (user.getStoreType() == 1 || user.getStoreType() == 2) {
			%>
			<div class="containerkds">
				<p style="text-align: center;">Welcome to <%=user.getStoreName() %></p>
			</div>
			<%
				} else if (user.getStoreType() == 3) {
			%>
			<div class="containerkds">
				<p style="text-align: center;">Welcome to <%=user.getStoreName() %></p>
			</div>
			<%
				} 
			%>
			
			<div class="navbar-custom-menu">
				<ul class="nav navbar-nav">
				<li style="float: left;"><span id='date-part' style="word-spacing: 1px; color: white; padding-top: 10px"></span></li>
					<li class="dropdown user user-menu"><a class="dropdown-toggle"
						data-toggle="dropdown"><i class="fas fa-user"></i>&nbsp;&nbsp;<span class="hidden-xs"><%=user.getName()%></span>
					</a>
						<ul class="dropdown-menu">
							<!-- Menu Footer-->
							<li class="user-footer">
								<div class="pull-right">
									<%
										if (user.getRoleType() == Constant.KITCHEN_ROLE) {
									%>
									<!-- will be continue -->
									<!-- <a class="btn btn-primary btn-flat" ng_click="getKdsSetting()">Setting</a> -->
									<%
										}
									%>
									<a href="${pageContext.request.contextPath}/signout"
										class="btn btn-primary btn-flat">Sign out</a>
								</div>
							</li>
						</ul></li>
				</ul>
			</div>
		</nav>
	</header>
</body>
<script type="text/javascript">
	$(document).ready(
			function() {
				var interval = setInterval(function() {
					var momentNow = moment();
					$('#date-part').html(
							momentNow.format('hh:mm:ss A')
									+ '</br>'
									+ momentNow.format('DD/MM/YYYY'));
				}, 100);
			});
</script>
</html>