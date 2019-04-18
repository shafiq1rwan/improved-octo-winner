<script>
	app.controller('checks_listing_CTRL', function($scope, $http, $window, $routeParams, $location, $compile) {
		$scope.initiation = function() {
			$scope.getChecksList();
		}
		
		$scope.getChecksList = function() {
			var table = $('#datatable_checks').DataTable({
				"ajax" : {
					"url" : "${pageContext.request.contextPath}/rc/check/get_check_list",
					"error" : function() {
						alert("Check list failed to display");
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
			console.log(params);
			$http.get("${pageContext.request.contextPath}/rc/check/get_check_detail/" + params)
			.then(function(response) {
				$scope.checkDetail = response.data;
				
				$('#checkDetailsModal').modal('show');
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/signout";
			});
		}
	});
</script>