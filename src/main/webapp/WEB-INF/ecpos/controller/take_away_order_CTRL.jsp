<script>
	app.controller('take_away_order_CTRL', function($scope, $http, $routeParams, $window, $location, $route) {
		$scope.initiation = function() {
			$http.get("${pageContext.request.contextPath}/rc/configuration/session_checking")
			.then(function(response) {
				if (response.data.responseCode == "00") {
					$scope.create_new_check();
				} else {
					alert("Session TIME OUT");
					window.location.href = "${pageContext.request.contextPath}";
				}
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/signout";
			});
		}
		
		$scope.create_new_check = function() {
			$http.post("${pageContext.request.contextPath}/rc/check/create/take_away")
			.then(function(response) {
				if (response.data.response_code === "00") {
					$scope.redirect_to_check_detail(response.data.check_no);
				} else {
					if (response.data.response_message != null) {
						alert(response.data.response_message);
					} else {
						alert("Error Occured While Create Check");
					}
				}
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/signout";
			});
		}
				
		//Redirect to check detail page
		$scope.redirect_to_check_detail = function(chk_no) {
			$("#modal_table_check_list").modal('hide');
			$('.modal-backdrop').remove();

			var data = "/check/" + "take_away" + "/" + chk_no + "/" + -99;
			$location.path(data);
		}
	});
</script>