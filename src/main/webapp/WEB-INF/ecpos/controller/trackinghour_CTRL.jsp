<script>
app.controller('trackinghour_CTRL', function($scope, $http, $window, $routeParams, $location) {	
	$scope.date = {};
	
	$scope.initiation = function() {
		$http.get("${pageContext.request.contextPath}/rc/configuration/session_checking")
		.then(function(response) {
			if (response.data.responseCode == "00") {

				$scope.getDropDownDataList();
				
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
				"endDate" : $scope.date.end.toISOString(),
				"checkStatus" : $scope.selectedOrderTypeDropDown == null ? "" : $scope.selectedOrderTypeDropDown
			};
			console.log(dataObj);
			
			var dataObj1 = {
					"startDate" : $scope.date.startItem.toISOString(),
					"endDate" : $scope.date.endItem.toISOString(),
					"checkStatus" : $scope.selectedOrderTypeDropDown == null ? "" : $scope.selectedOrderTypeDropDown
				};
			console.log(dataObj1);
			
			var table = $('#datatable_salesSummary').DataTable({
				"ajax" : {
					"url" : "${pageContext.request.contextPath}/rc/report/get_total_working_hour",
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
				"paging": true,
				"destroy" : true,
				"columns" : [{"data" : "staff_name", "width": "25%"}, 
					{"data" : "clock_in", "width": "25%"},
					{"data" : "clock_out", "width": "25%"},
					{"data" : "date_working", "width": "25%"}]
			});
			
			var table1 = $('#datatable_itemSummary').DataTable({
				"ajax" : {
					"url" : "${pageContext.request.contextPath}/rc/report/get_total_staff_sales",
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
				"paging": true,
				"destroy" : true,
				"columns" : [{"data" : "staff_name", "width": "33%"},
					{"data" : "total_sales", "width": "33%"}, 
					{"data" : "total_amount"}]
			});

			/* var ctx = document.getElementById('myChart').getContext('2d'); */
			var ctx2 = document.getElementById('myChart2').getContext('2d');
			/* var json_url="${pageContext.request.contextPath}/rc/report/get_sales_summary_chart?startDate="+$scope.date.start.toISOString()
			+"&endDate="+$scope.date.end.toISOString(); */
			var json_url1="${pageContext.request.contextPath}/rc/report/get_overall_sales_performance_staff?startDate="+$scope.date.startItem.toISOString()
			+"&endDate="+$scope.date.endItem.toISOString();

			/* $.ajax({
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
			}); */

			$.ajax({
			    url: json_url1,
			    dataType: "json",
			    success: function(response)
			    {
			    	const colorScheme = [
			    	    "#25CCF7","#FD7272","#54a0ff","#00d2d3",
			    	    "#1abc9c","#2ecc71","#3498db","#9b59b6","#34495e",
			    	    "#16a085","#27ae60","#2980b9","#8e44ad","#2c3e50",
			    	    "#f1c40f","#e67e22","#e74c3c","#ecf0f1","#95a5a6",
			    	    "#f39c12","#d35400","#c0392b","#bdc3c7","#7f8c8d",
			    	    "#55efc4","#81ecec","#74b9ff","#a29bfe","#dfe6e9",
			    	    "#00b894","#00cec9","#0984e3","#6c5ce7","#ffeaa7",
			    	    "#fab1a0","#ff7675","#fd79a8","#fdcb6e","#e17055",
			    	    "#d63031","#feca57","#5f27cd","#54a0ff","#01a3a4"
			    	];
			    	
			    	var myBarChart = new Chart(ctx2, {
			    	    type: 'line',
			    	    data: {
			    	        labels: response.item_name,
			    	        datasets: [{
			    	            label: 'Overall Sales Performance',
			    	            backgroundColor: colorScheme,
			    	            data: response.item_total,
			    	        }]
			    	    },
			    	    options: {
			    	    	responsive: true,
			    	        maintainAspectRatio: false,
			    	        scales: {
			    	            yAxes: [{
			    	            	stacked: true,
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

	$scope.getDropDownDataList = function() {
		$http.get("${pageContext.request.contextPath}/rc/report/getStaffDropdownList")
		.then(function (response) {
			$scope.dropdownData = response.data;
		}, function(response) {
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