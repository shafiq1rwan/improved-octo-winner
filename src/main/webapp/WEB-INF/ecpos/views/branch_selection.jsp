<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>

<style>
.sectioncalibrator {
	height:90vh;
	overflow-y:scroll;
}

@media only screen and (max-width:600px) {
    .sectioncalibrator {
		height:77vh;
		overflow-y:scroll;
    }
}

</style>


<!-- *****Admin LTE****** -->
<!-- Bootstrap 3.3.6 -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/member/bootstrap/css/bootstrap.min.css">
<!-- Font Awesome -->
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.5.0/css/font-awesome.min.css">
<!-- Ionicons -->
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/ionicons/2.0.1/css/ionicons.min.css">
<!-- Theme style -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/member/dist/css/AdminLTE.min.css">
<!-- AdminLTE Skins. Choose a skin from the css/skins folder instead of downloading all of them to reduce the load. -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/member/dist/css/skins/_all-skins.min.css">
	
<script
	src="${pageContext.request.contextPath}/member/plugins/jQuery/jquery-2.2.3.min.js"></script>
<!-- ****ADMIN LTE TEMPLATE UNIVERSAL JQUERY**** -->
<!-- jQuery 2.2.3 -->
<script src="${pageContext.request.contextPath}/member/js/loadImg.js"></script>
<!-- jQuery UI 1.11.4 -->
<script src="https://code.jquery.com/ui/1.11.4/jquery-ui.min.js"></script>

<body>

	<div ng-app="preActivate" ng-controller="Branch_selection_CTRL"
		id="branch_selection_controller_container">

		<div class="content-wrapper" style="font-size: 0.9em;">
		<section class="content sectioncalibrator">
		
		
			<div class="row">
				<div class="col-md-3 haha"
					ng-repeat="branch in branch_list">

					<a ng-click="select_branch(branch.id)" ng-model="branch.id">
						<div class="panel text-center">
							<div class="panel-heading">
								<h3 class="panel-title">{{branch.name}}</h3>
							</div>
							<div class="panel-body">
								<table>
									<tbody>
										<tr>
											<th>Id</th>
											<td>{{branch.id}}</td>
										</tr>
										<tr>
											<th>Name</th>
											<td>{{branch.name}}</td>
										</tr>
										<tr>
											<th>Tel No</th>
											<td>{{branch.telNo}}</td>
										</tr>
										<tr>
											<th>Fax No</th>
											<td>{{branch.faxNo}}</td>
										</tr>
									</tbody>


								</table>
							</div>
						</div>
					</a>


				</div>
			</div>
		
		
		</section>
		</div>

	</div>

</body>

<!-- *****ANGULAR JS****** -->
<script
	src="${pageContext.request.contextPath}/universal_lib/angular-1.6.6/angular.min.js"></script>
<script
	src="${pageContext.request.contextPath}/universal_lib/angular-1.6.6/angular-route.js"></script>
<jsp:include page="/WEB-INF/member/controller/branch_selection_CTRL.jsp" />


</html>