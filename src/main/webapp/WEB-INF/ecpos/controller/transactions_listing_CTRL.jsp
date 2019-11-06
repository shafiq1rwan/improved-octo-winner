<script>
	app.controller('transactions_listing_CTRL', function($scope, $http, $window, $routeParams, $location, $compile) {
		
		$scope.transaction = {};
		$scope.voidMessage = "";
		
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
					
					$scope.getTransactionsList();
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
		
		$scope.getTransactionsList = function() {
			if ($scope.dateStart > $scope.dateEnd) {
				alert("Start Date should be before End Date");
			} else {
				var dataObj = {
						"startDate" : $scope.dateStart.toISOString(),
						"endDate" : $scope.dateEnd.toISOString(),
						"paymentMethod" : $scope.selectedPaymentMethod == null ? "" : $scope.selectedPaymentMethod,
						"tsStatus" : $scope.selectedTsStatus == null ? "" : $scope.selectedTsStatus
				};
				console.log(dataObj);
				var table = $('#datatable_transactions').DataTable({
					"ajax" : {
						"url" : "${pageContext.request.contextPath}/rc/transaction/get_transaction_list",
						"type" : "post",
						"data" : function (d) {
						      return JSON.stringify(dataObj);
					    },
					    "contentType" : "application/json; charset=utf-8",
						"dataType" : "json",
						"dataSrc": function ( json ) {                
			                return json.data;
			            },
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
					"columns" : [{"data" : "checkNoByday"},
						{"data" : "staffName"}, 
						{"data" : "transactionType"},
						{"data" : "paymentMethod"},
						{"data" : "paymentType"},
						{"data" : "transactionAmount"},
						{"data" : "transactionStatus"},
						{"data" : "transactionDate"},
					 	{"data" : "id", "visible": false, "searchable": false}
						],
					rowCallback: function(row, data, index){
						var status = $(row).find('td:eq(6)').html();		
			    		if(status !== 'Pending'){
			    			$(row).find('td:eq(1)').css('color', 'blue');
			    		}
	
				    	$(row).mouseenter (function() {
				    		if(status !== 'Pending'){
					    		$(row).find('td:eq(1)').css('text-decoration', 'underline');
				    		}
			    		});
				    	
				    	$(row).mouseleave (function() {
				    		if(status !== 'Pending'){
				    			$(row).find('td:eq(1)').css('text-decoration', 'none');
				    		}
			    		});
					},
					"createdRow": function ( row, data, index ) {
						$compile(row)($scope);
					}
				});
				
				$('#datatable_transactions tbody').off('click', 'td');
				$('#datatable_transactions tbody').on('click', 'td', function(){
				    var status = table.row(this.closest('tr')).data().transactionStatus;
				    if(status == 'Pending'){
				    } else {
						if ($(this).index() == 1) {
			 				$http({
								method : 'GET',
								headers : {'Content-Type' : 'application/json'},
								url : '${pageContext.request.contextPath}/rc/transaction/get_transaction_details?id=' + table.row(this.closest('tr')).data().id			
							})
							.then(function(response) {
								if(response.status == "200") {
									$scope.transaction.id = response.data.id;
									$scope.transaction.isVoid = response.data.isVoid;
									$scope.receiptHeader = response.data.receiptHeader;
									$scope.receiptData = response.data.receiptData;
									$scope.cashData = response.data.cashData;
									$scope.cardData = response.data.cardData;
									$scope.qrData = response.data.qrData;
									$scope.qrImage = response.data.qrImg;
									$scope.grandParentItemArray = response.data.grandParentItemArray;
									
									//$scope.transaction.isApproved = response.data.isApproved;
									$('#transactionDetailsModal').modal('show');
								}
							});
						} 
				    }
				});
			}
		}
		
		$scope.voidTransaction = function(transactionId, isVoid){
			if(isVoid){
				$scope.printTransactionReceipt(transactionId); //print receipt
			} else {
				var jsonData = JSON.stringify({
					"transactionId" : transactionId
				});
				
				$scope.voidMessage = "Void In Progress";
				$('#transactionDetailsModal').modal('hide');
				
				$('#loading_modal').modal('show');
				
	 			$http.post("${pageContext.request.contextPath}/rc/transaction/void_transaction", jsonData)
				.then(function(response) {
					if(response.data.response_code == '00'){
						alert(response.data.response_message);
						$('#loading_modal').modal('hide');
						$scope.printTransactionReceipt(transactionId); //print receipt
						$scope.getTransactionsList();
					} else {
						alert(response.data.response_message);
						$('#loading_modal').modal('hide');
					}
					$scope.voidMessage = "";
				},
				function(response) {
					alert("Session TIME OUT");
					window.location.href = "${pageContext.request.contextPath}/signout";
				});
			}
		}
		
		$scope.printTransactionReceipt = function(transactionId){
			var jsonData = JSON.stringify({
				"transactionId" : transactionId
			});
			
 			$http.post("${pageContext.request.contextPath}/rc/configuration/print_transaction_receipt", jsonData)
			.then(function(response) {
				if(response.data.response_code == '00'){
					$('#transactionDetailsModal').modal('hide');
				} else {
					alert(response.data.response_message);
					$('#transactionDetailsModal').modal('hide');
				}
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/signout";
			});
		}
		
		$scope.displayPdf = function(transactionId){
			var jsonData = JSON.stringify({
				"transactionId" : transactionId
			});
			
 			$http.post("${pageContext.request.contextPath}/rc/configuration/display_receipt", jsonData, {responseType: 'arraybuffer'})
			.then(function(response) {
					$('#transactionDetailsModal').modal('hide');
				    var file = new Blob([response.data], {type: 'application/pdf'});
				    var fileURL = URL.createObjectURL(file);
				    window.open(fileURL);
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/signout";
			});
		}
		
		$scope.closeTransactionDetailsModal = function(){
 			console.log("haha");
			document.getElementById("receipt_content_section").scrollTop=0;
		}
		
		$scope.getDropDownDataList = function() {
			$http.get("${pageContext.request.contextPath}/rc/transaction/get_dropdown_filter")
			.then(function (response) {
				$scope.dropdownData = response.data;
			}, function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/signout";
			});
		}
	});
</script>