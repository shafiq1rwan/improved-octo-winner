<script>
	app.controller('take_away_order_CTRL', function($scope, $http, $routeParams, $window, $location, $route) {
		$scope.initiation = function() {
			$scope.create_new_check();
		}
		
		$scope.create_new_check = function() {
			var jsonData = JSON.stringify({
				"order_type" : "take away"
			});
			
			$http.post("${pageContext.request.contextPath}/rc/check/create", jsonData)
			.then(function(response) {
				if (response.data.response_code === "00") {
					$scope.redirect_to_check_detail(response.data.check_no);
				} else {
					alert("Error Occured While Create Check");
					window.location.href = "${pageContext.request.contextPath}/ecpos";
				}
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/ecpos";
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