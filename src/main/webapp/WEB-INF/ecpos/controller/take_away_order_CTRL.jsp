<%@ page import="mpay.ecpos_manager.general.utility.UserAuthenticationModel"%>

<%
	UserAuthenticationModel user = (UserAuthenticationModel) session.getAttribute("session_user");
%>

<script>
	app.controller('take_away_order_CTRL', function($scope, $http, $routeParams, $window, $location, $route) {
		$scope.takeAway = {};
		
		$scope.initiation = function() {
			$http.get("${pageContext.request.contextPath}/rc/configuration/session_checking")
			.then(function(response) {
				if (response.data.responseCode == "00") {
					$('#customerName').focus();
				} else {
					// alert("Session TIME OUT");
					// window.location.href = "${pageContext.request.contextPath}/signout";
					Swal.fire({
						title: 'Oops...',
						text: "Session Timeout",
						icon: 'error',
						showCancelButton: false,
						confirmButtonColor: '#3085d6',
						cancelButtonColor: '#d33',
						confirmButtonText: 'OK'
						},function(isConfirm){
						if (isConfirm) {
						window.location.href = "${pageContext.request.contextPath}/signout";
						}
					});
				}
			},
			function(response) {
				// alert("Session TIME OUT");
				// window.location.href = "${pageContext.request.contextPath}/signout";
				Swal.fire({
					title: 'Oops...',
					text: "Session Timeout",
					icon: 'error',
					showCancelButton: false,
					confirmButtonColor: '#3085d6',
					cancelButtonColor: '#d33',
					confirmButtonText: 'OK'
					},function(isConfirm){
					if (isConfirm) {
					window.location.href = "${pageContext.request.contextPath}/signout";
					}
				});
			});
		}
		
		$('#keyboard').click(function() {
	    	$('#customerName').focus();
	    });
		
		$scope.create_new_check = function() {
			var takeAwayFlag = <%=user.isTakeAwayFlag()%>;			
			
			if (takeAwayFlag == true && ($('#customerName').val() == null || $('#customerName').val() == "")) {
				// alert("Kindly key in customer name.");
				Swal.fire("Warning","Kindly key in customer name.","warning");
			} else {
				var jsonData = JSON.stringify({
					"customerName" : $('#customerName').val() 
				});
				
				$http.post("${pageContext.request.contextPath}/rc/check/create/take_away", jsonData)
				.then(function(response) {
					if (response.data.response_code === "00") {
						$scope.redirect_to_check_detail(response.data.check_no);
					} else {
						if (response.data.response_message != null) {
							// alert(response.data.response_message);
							Swal.fire("Oops...",response.data.response_message,"error");
						} else {
							// alert("Error Occured While Create Check");
							Swal.fire("Oops...","Error Occured While Create Check","error");
						}
					}
				},
				function(response) {
					// alert("Session TIME OUT");
					// window.location.href = "${pageContext.request.contextPath}/signout";
					Swal.fire({
						title: 'Oops...',
						text: "Session Timeout",
						icon: 'error',
						showCancelButton: false,
						confirmButtonColor: '#3085d6',
						cancelButtonColor: '#d33',
						confirmButtonText: 'OK'
						},function(isConfirm){
						if (isConfirm) {
						window.location.href = "${pageContext.request.contextPath}/signout";
						}
					});
				});
			}
		}
				
		//Redirect to check detail page
		$scope.redirect_to_check_detail = function(chk_no) {
			var data = "/check/" + "take_away" + "/" + chk_no + "/" + -99;
			$location.path(data);
		}
	});
</script>