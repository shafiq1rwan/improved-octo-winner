<script>
app.controller('reports_CTRL', function($scope, $http, $window, $routeParams, $location) {	
	$scope.date = {};
	
	$scope.initiation = function() {
		$scope.date.start = new Date();
		var month = $scope.date.start.getMonth() - 1;
		$scope.date.start.setMonth(month);
		$scope.date.start.setHours(0+8, 0, 0, 0);
		
		$scope.date.end = new Date();
		$scope.date.end.setHours(23+8, 59, 0, 0);
		
		$scope.getSalesSummary();
	}
	
	$scope.getSalesSummary = function() {
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
					alert("Report failed to display");
					window.location.href = "${pageContext.request.contextPath}/ecpos";
				}
			},
			"ordering" : false,
			"searching" : false,
			"lengthChange" : false,
			"info" : false,
			"paging": false,
			"destroy" : true,
			"columns" : [{"data" : "deviceType", "width": "25%"}, 
				{"data" : "paymentMethod", "width": "25%"}, 
				{"data" : "totalCount", "width": "25%"},
				{"data" : "totalAmount", "width": "25%"}]
		});
	}
});
</script>