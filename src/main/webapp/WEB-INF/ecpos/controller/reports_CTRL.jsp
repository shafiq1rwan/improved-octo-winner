<script>
	app.controller('reports_CTRL', function($scope, $http, $window, $routeParams, $location) {	
		$scope.date = {};
		
		$scope.initiation = function() {
			$scope.date.start = new Date();
			$scope.date.start.setUTCHours(0, 0, 0, 0);
			var m = $scope.date.start.getMonth() - 1;
			$scope.date.start.setMonth(m);
			
			$scope.date.end = new Date();
			$scope.date.end.setUTCHours(0, 0, 0, 0);
		}
		
		$scope.generateMonthlySalesReport = function(){
	  		var jsonData = JSON.stringify({
				"startDate" : $scope.date.start,
				"endDate" : $scope.date.end
			});
		
	 		$http.post("${pageContext.request.contextPath}/rc/report/monthly_sales_report", jsonData)
			.then(function(response) {
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/ecpos";
			});
		}
	});
</script>