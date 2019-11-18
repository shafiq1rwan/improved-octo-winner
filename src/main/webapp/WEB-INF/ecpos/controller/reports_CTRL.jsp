<script>
app.controller('reports_CTRL', function($scope, $http, $window, $routeParams, $location) {	
	$scope.date = {};
	
	$scope.initiation = function() {
		$http.get("${pageContext.request.contextPath}/rc/configuration/session_checking")
		.then(function(response) {
			if (response.data.responseCode == "00") {
				$scope.date.start = new Date();
				var month = $scope.date.start.getMonth() - 1;
				$scope.date.start.setMonth(month);
				$scope.date.start.setHours(0+8, 0, 0, 0);
				
				$scope.date.end = new Date();
				$scope.date.end.setHours(23+8, 59, 0, 0);
				
				$scope.getSalesSummary();
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
	
	$scope.getSalesSummary = function() {
		if ($scope.date.start > $scope.date.end) {
			// alert("Start Date should be before End Date");
			Swal.fire("Warning","Start Date should be before End Date","warning");
		} else {
			var dataObj = {
				"startDate" : $scope.date.start.toISOString(),
				"endDate" : $scope.date.end.toISOString()
			};
			console.log(dataObj);
			
			var table = $('#datatable_salesSummary').DataTable({
				"ajax" : {
					"url" : "${pageContext.request.contextPath}/rc/report/get_sales_summary",
					"type" : "post",
					"data" : function (d) {
					      return JSON.stringify(dataObj);
				    },
					"contentType" : "application/json; charset=utf-8",
					"dataType" : "json",
					"error" : function() {
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
				"ordering" : false,
				"searching" : false,
				"lengthChange" : false,
				"info" : false,
				"paging": false,
				"destroy" : true,
				"columns" : [{"data" : "paymentMethod", "width": "33%"}, 
					{"data" : "totalCount", "width": "33%"},
					{"data" : "totalAmount", "width": "33%"}]
			});
		}
	}
});
</script>