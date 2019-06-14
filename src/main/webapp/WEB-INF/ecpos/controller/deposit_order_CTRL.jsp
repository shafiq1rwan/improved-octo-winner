<script>
	app.controller('deposit_order_CTRL', function($scope, $http, $window, $routeParams, $location) {
		$scope.initiation = function() {
			$http.post("${pageContext.request.contextPath}/rc/configuration/session_checking")
			.then(function(response) {
				if (response.data.responseCode == "00") {
					$scope.get_checklist();
				} else {
					alert("Session TIME OUT");
					window.location.href = "${pageContext.request.contextPath}/signout";
				}
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/signout";
			});
		}
		
		$scope.get_checklist = function() {
			$http.post("${pageContext.request.contextPath}/rc/check/get_deposit_checks")
			.then(function(response) {
				$scope.checks = response.data.checks;
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/signout";
			});
		}
		
		$('#depositOrderCarousel').on('slid.bs.carousel', function onSlide(ev) {
			var id = ev.relatedTarget.id;
			if (id == "checkList") {
				$('#customerName').val(null);
			} else if (id == "newCheck") {
				$('#customerName').focus();
			}
		})
		
		$('#keyboard').click(function() {
	    	$('#customerName').focus();
	    });

		$scope.create_new_check = function() {
			if ($('#customerName').val() == null || $('#customerName').val() == "") {
				alert("Kindly key in customer name.");
			} else {
				var jsonData = JSON.stringify({
					"customerName" : $('#customerName').val() 
				});
				
				$http.post("${pageContext.request.contextPath}/rc/check/create/deposit", jsonData)
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
		}
				
		//Redirect to check detail page
		$scope.redirect_to_check_detail = function(chk_no) {
			console.log(chk_no);
			var data = "/check/" + "deposit" + "/" + chk_no + "/" + -98;
			$location.path(data);
		}
	});
</script>