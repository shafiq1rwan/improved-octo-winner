<script>
app.controller('Show_reports_CTRL', function($scope, $http, $timeout, $location) {
	
	//Calendar feature start here
	var today = new Date();
	var dd = today.getDate();
	var mm = today.getMonth()+1-4; //January is 0!
	var yyyy = today.getFullYear();
	
	$scope.todayDate = mm + "/" + dd + "/" + yyyy;
	console.log($scope.todayDate);
	
	var selectedReportDate;
	
	$("#myCalendar")
			.on("changeDate",
					function(event) {
				//console.log("hahahaa");
						console.log(event);
						selectedReportDate = event
								.format("yyyy-mm-dd");
	});
	
	$scope.generateMonthlySalesReport = function(){		
		if(selectedReportDate){
			console.log(selectedReportDate);
			
	  		var jsonData = JSON.stringify({
				'date':selectedReportDate
			});
		
	 		$http
			.post(
					"${pageContext.request.contextPath}/report/monthlysalesreport", jsonData)
			.then(
					function(response) {
						console.log("Success");
					},
					function(response) {
						console.log("failed");
					}
			);
		}
}
	
	
});
</script>