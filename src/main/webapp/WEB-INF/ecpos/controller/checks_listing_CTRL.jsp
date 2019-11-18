<script>
	app.controller('checks_listing_CTRL', function($scope, $http, $window, $routeParams, $location, $compile) {
		$scope.initiation = function() {
			$http.get("${pageContext.request.contextPath}/rc/configuration/session_checking")
			.then(function(response) {
				if (response.data.responseCode == "00") {
					$scope.getDropDownDataList();
					
					$scope.dateStart = new Date();
					var month = $scope.dateStart.getMonth() - 1;
					$scope.dateStart.setMonth(month);
					$scope.dateStart.setHours(0+8, 0, 0, 0);
					
					$scope.dateEnd = new Date();
					$scope.dateEnd.setHours(23+8, 59, 0, 0);
					
					$scope.getChecksList();
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
		
		$scope.getChecksList = function() {
			if ($scope.dateStart > $scope.dateEnd) {
				// alert("Start Date should be before End Date");
				Swal.fire("Warning","Start Date should be before End Date","warning");
			} else {
				var dataObj = {
						"startDate" : $scope.dateStart.toISOString(),
						"endDate" : $scope.dateEnd.toISOString(),
						"orderType" : $scope.selectedOrderTypeDropDown == null ? "" : $scope.selectedOrderTypeDropDown,
						"checkStatus" : $scope.selectedCheckStatusDropDown == null ? "" : $scope.selectedCheckStatusDropDown
				};
				
				var table = $('#datatable_checks').DataTable({
					"ajax" : {
						"url" : "${pageContext.request.contextPath}/rc/check/get_check_list",
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
					"searching": false,
					"pageLength": 8,
					"bLengthChange": false,
					"order" : [ [ 0, "desc" ] ],
					destroy : true,
					"columns" : [{"data" : "id"}, 
						{"data" : "checkNumber"}, 
						{"data" : "staffName"},
						{"data" : "orderType"},
						{"data" : "tableName"},
						{"data" : "grandTotalAmount"},
						{"data" : "overdueAmount"},
						{"data" : "checkStatus"},
						{"data" : "createdDate"}],
					rowCallback: function(row, data, index){
				    	$(row).find('td:eq(1)').css('color', 'blue');
				    	
				    	$(row).mouseenter (function() {
				    		$(row).find('td:eq(1)').css('text-decoration', 'underline');
			    		});
				    	
				    	$(row).mouseleave (function() {
				    		$(row).find('td:eq(1)').css('text-decoration', 'none');
			    		});
					},
					"createdRow": function ( row, data, index ) {
						$compile(row)($scope);
					}
				});
				
				$('#datatable_checks tbody').off('click', 'td');
				$('#datatable_checks tbody').on('click', 'td', function(){
					if ($(this).index() == 1) {
						var data = table.row($(this).closest('tr')).data();
						$scope.getCheckDetails(data.orderType + '/' + data.id + '/' + data.tableNumber);
					}
				});
			}
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
		
		$scope.redirectCheck = function() {
			$("#checkDetailsModal").modal('hide');
			$('.modal-backdrop').remove();
			
			if ($scope.checkDetail.orderType == 1) {
				$scope.checkDetail.orderType = "table"
			} else if ($scope.checkDetail.orderType == 2) {
				$scope.checkDetail.orderType = "take_away"
				$scope.checkDetail.tableNo = -99
			} else if ($scope.checkDetail.orderType == 3) {
				$scope.checkDetail.orderType = "deposit"
				$scope.checkDetail.tableNo = -98
			}
			
			var data = "/check/" + $scope.checkDetail.orderType + "/" + $scope.checkDetail.checkNo + "/" + $scope.checkDetail.tableNo;
			$location.path(data);
		}
		
		$scope.getDropDownDataList = function() {
			$http.get("${pageContext.request.contextPath}/rc/check/get_dropdown_filter")
			.then(function (response) {
				$scope.dropdownData = response.data;
			}, function(response) {
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
	});
</script>