<script>
	app.controller('checks_listing_CTRL', function($scope, $http, $window, $routeParams, $location, $compile) {
		$scope.initiation = function() {
			$http.get("${pageContext.request.contextPath}/rc/configuration/session_checking")
			.then(function(response) {
				if (response.data.responseCode == "00") {
					$scope.getChecksList();
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
		
		$scope.getChecksList = function() {
			var table = $('#datatable_checks').DataTable({
				"ajax" : {
					"url" : "${pageContext.request.contextPath}/rc/check/get_check_list",
					"error" : function() {
						alert("Session TIME OUT");
						window.location.href = "${pageContext.request.contextPath}/signout";
					}
				},
				"searching": false,
				"pageLength": 9,
				"bLengthChange": false,
				"order" : [ [ 0, "desc" ] ],
				destroy : true,
				"columns" : [{"data" : "id"}, 
					{"data" : "checkNumber"}, 
					{"data" : "staffName"},
					{"data" : "orderType"},
					{"data" : "tableNumber"},
					{"data" : "grandTotalAmount"},
					{"data" : "checkStatus"},
					{"data" : "createdDate"},
					{"render" : function(data, type, full, meta) {
						return '<div><button class="btn btn-sm btn-info" ng-click="getCheckDetails(\''+ full.orderType + '/' + full.checkNumber + '/' + full.tableNumber + '\')">Details</button></div>';
					}}],
				"createdRow": function ( row, data, index ) {
					$compile(row)($scope);
				}
			});
		}
		
		$scope.getCheckDetails = function(params) {
			if (params.includes("take away")) {
				params = params.replace("take away", "take_away");
			}
			
			$http.get("${pageContext.request.contextPath}/rc/check/get_all_check_detail/" + params)
			.then(function(response) {
				$scope.checkDetail = response.data;
				
				$('#checkDetailsModal').modal('show');
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/signout";
			});
		}
		
		$scope.cancelCheck = function(checkNo) {
			var confirmation = confirm("Confirm to cancel check?");
			if (confirmation == true) {
				var jsonData = JSON.stringify({
					"checkNo" : checkNo
				});
				
				$http.post("${pageContext.request.contextPath}/rc/check/cancel_check", jsonData)
				.then(function(response) {
					if (response.data.response_code === "00") {
						alert("Check has been cancelled.")

						location.reload();
					} else {
						if (response.data.response_message != null) {
							alert(response.data.response_message);
						} else {
							alert("Error Occured While Remove Check");
						}
					}
				},
				function(response) {
					alert("Session TIME OUT");
					window.location.href = "${pageContext.request.contextPath}/signout";
				});
			}
		}
	});
</script>