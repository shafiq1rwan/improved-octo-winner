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
		
		$scope.getTransactionsList = function() {
			if ($scope.dateStart > $scope.dateEnd) {
				/* alert("Start Date should be before End Date"); */
				Swal.fire("Warning","Start Date should be before End Date","warning");
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

						}
					},
					"searching": false,
					"pageLength": 8,
					"bLengthChange": false,
					"order" : [ [ 9, "desc" ] ],
					destroy : true,
					"columns" : [{"data" : "receipt_number"},
						{"data" : "checkNoByday"},
						{"data" : "transactionType"},
						{"data" : "paymentMethod"},
						{"data" : "paymentType"},
						{"data" : "transactionAmount"},
						{"data" : "transactionStatus"},
						{"data" : "staffName"},
						{"data" : "transactionDate"},
					 	{"data" : "id", "visible": false, "searchable": false}
						],
					rowCallback: function(row, data, index){
						var status = $(row).find('td:eq(7)').html();
						console.log("status: "+status);
			    		if(status !== 'Failed'){
			    			$(row).find('td:eq(0)').css('color', 'blue');
			    		}
	
				    	$(row).mouseenter (function() {
				    		if(status !== 'Failed'){
					    		$(row).find('td:eq(0)').css('text-decoration', 'underline');
				    		}
			    		});
				    	
				    	$(row).mouseleave (function() {
				    		if(status !== 'Failed'){
				    			$(row).find('td:eq(0)').css('text-decoration', 'none');
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
				    if(status == 'Failed'){
				    } else {
						if ($(this).index() == 0) {
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
			Swal.fire({
				  title: 'Enter your password',
				  html: '<input id="password" class="swal2-input" type="password" placeholder="Password">',
				  preConfirm: () => {
					  var jsonData = JSON.stringify({
							"data" : document.getElementById('password').value,
					  });
					  $http.post("${pageContext.request.contextPath}/rc/configuration/checkVoidPassword", jsonData)
						.then(function(response) {
							if(response.data.response_code == '00'){
								let timerInterval
								Swal.fire({
								  title: 'Success!',
								  text: response.data.response_message,
								  icon: 'success',
								  timer: 1000,
								  timerProgressBar: true,
								  didOpen: () => {
								    Swal.showLoading()
								    timerInterval = setInterval(() => {
								      const content = Swal.getContent()
								      if (content) {
								        const b = content.querySelector('b')
								        if (b) {
								          b.textContent = Swal.getTimerLeft()
								        }
								      }
								    }, 100)
								  },
								  willClose: () => {
								    clearInterval(timerInterval)
								  }
								}).then((result) => {
								  /* Read more about handling dismissals below */
								  if (result.dismiss === Swal.DismissReason.timer) {
								    console.log('I was closed by the timer')
								  }

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
												Swal.fire('Success',response.data.response_message,'success');
												$('#loading_modal').modal('hide');
												$scope.printTransactionReceipt(transactionId); //print receipt
												$scope.getTransactionsList();
											} else {
												Swal.fire('Error',response.data.response_message,'error');
												$('#loading_modal').modal('hide');
											}
											$scope.voidMessage = "";
										},
										function(response) {
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
							}else{
								Swal.fire('Error',response.data.response_message,'error');
							}
						});
				  }
			});
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
					/* alert(response.data.response_message); */
					Swal.fire("Oops...",response.data.response_message,"error");
					$('#transactionDetailsModal').modal('hide');
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
		
		$scope.closeTransactionDetailsModal = function(){
 			console.log("haha");
			document.getElementById("receipt_content_section").scrollTop=0;
		}
		
		$scope.getDropDownDataList = function() {
			$http.get("${pageContext.request.contextPath}/rc/transaction/get_dropdown_filter")
			.then(function (response) {
				$scope.dropdownData = response.data;
			}, function(response) {
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