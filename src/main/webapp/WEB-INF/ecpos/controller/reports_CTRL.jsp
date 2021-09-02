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
				
				$scope.date.startItem = new Date();
				var month = $scope.date.startItem.getMonth() - 1;
				$scope.date.startItem.setMonth(month);
				$scope.date.startItem.setHours(0+8, 0, 0, 0);
				
				$scope.date.endItem = new Date();
				$scope.date.endItem.setHours(23+8, 59, 0, 0);
				
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
			
			var dataObj1 = {
					"startDate" : $scope.date.startItem.toISOString(),
					"endDate" : $scope.date.endItem.toISOString()
				};
			console.log(dataObj1);
			
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
			
			var table1 = $('#datatable_itemSummary').DataTable({
				"ajax" : {
					"url" : "${pageContext.request.contextPath}/rc/report/get_item_summary",
					"type" : "post",
					"data" : function (d) {
					      return JSON.stringify(dataObj1);
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
				"columns" : [{"data" : "items", "width": "33%"}, 
					{"data" : "totalItems", "width": "33%"}]
			});

			var ctx = document.getElementById('myChart').getContext('2d');
			var ctx2 = document.getElementById('myChart2').getContext('2d');
			var json_url="${pageContext.request.contextPath}/rc/report/get_sales_summary_chart?startDate="+$scope.date.start.toISOString()
			+"&endDate="+$scope.date.end.toISOString();
			var json_url1="${pageContext.request.contextPath}/rc/report/get_item_summary_charts?startDate="+$scope.date.startItem.toISOString()
			+"&endDate="+$scope.date.endItem.toISOString();

			$.ajax({
			    url: json_url,
			    dataType: "json",
			    success: function(response)
			    {
			        var myBarChart = new Chart(ctx, {
			            type: 'doughnut',
			            data: {
			                labels: response.paymentMethod,
			                datasets: [{
			                    label: 'Payment Method',
			                    data: response.totalCount,
			                    backgroundColor: ["#90be6d", "#43aa8b", "#4d908e"],
			                    borderColor: ['white'],
			                    borderWidth: 1
			                }]
			            },
			            options: {
			            	responsive: true,
			                maintainAspectRatio: false
			             }
			        });
			    }
			});

			$.ajax({
			    url: json_url1,
			    dataType: "json",
			    success: function(response)
			    {
			    	const colorScheme = [
			    	    "#5f27cd","#ff9f43","#ee5253","#0abde3",
			    	    "#01a3a4","#2e86de","#341f97","#8395a7","#222f3e",
			    	    "#16a085"];
			    	
			    	var myBarChart = new Chart(ctx2, {
			    	    type: 'bar',
			    	    data: {
			    	        labels: response.item_name,
			    	        datasets: [{
			    	        	beginAtZero: true,
			    	            label: 'Best Selling Item', 
			    	            backgroundColor: colorScheme, 
			    	            borderColor: 'white',
			    	            data: response.item_total,
			    	        }]
			    	    },
			    	    options: {
			    	    	responsive: true,
			    	        maintainAspectRatio: false,
			    	        scales: {
			    	            yAxes: [{
			    	            	display: true,
			    	                ticks: {
			    	                    beginAtZero: true,
			    	                    precision: 0
			    	                }
			    	            }]
			    	        }
			    	    }
			    	});
			    }
			});
		}
	}
	
	$scope.printEODReport = function() {
		
		$http.post("${pageContext.request.contextPath}/rc/configuration/print_eod")
		.then(function(response) {
			if (response.data.response_code === "00") {
				console.log("Success Printable");
			}
		},
		function(response) {
			/* alert("Session TIME OUT"); */
			/* window.location.href = "${pageContext.request.contextPath}/signout"; */
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
});
</script>