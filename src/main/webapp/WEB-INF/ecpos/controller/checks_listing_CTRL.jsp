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
	});
</script>