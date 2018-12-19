<script>
	app
			.controller(
					'show_trans_CTRL',
					function($scope, $http, $timeout) {

						$scope.fields_TransList = {};
						$scope.transaction_details = {};
						$scope.amount = 0.00;
						
						$scope.selectedTerminal = {};
						$scope.terminalList = [];
						
						//Init Function
						$scope.initTransPage = function(){
							$scope.getTransactionList();
							$scope.getTerminalList();
						}
						
						//Success
						$scope.getTransactionList = function() {
							$http
									.get(
											"${pageContext.request.contextPath}/memberapi/show_trans/get_transaction_list")
									.then(
											function(response) {
												console.log(response.data);
												$scope.fields_TransList = response.data;

												//load first transaction data on page-show
												$scope.selectedTransaction = $scope.fields_TransList.trans_list[0].tran_id;
												$scope
														.get_transaction_details($scope.selectedTransaction);

											},
											function(response) {
												alert("Session TIME OUT");
												$(location)
														.attr('href',
																'${pageContext.request.contextPath}/ecpos/#!sales');
											});
						}

						//Success
						$scope.get_transaction_details = function(tran_id) {

							$http
									.get(
											"${pageContext.request.contextPath}/memberapi/show_trans/get_transaction_details/"
													+ tran_id)
									.then(
											function(response) {
												console
														.log("Transaction Detail");
												console.log(response.data);
												$scope.transaction_details = response.data;
												$scope.amount = $scope.transaction_details.amount;
												$scope.tax = $scope.transaction_details.tax;

											},
											function(response) {
												alert("Session TIME OUT");
												$(location)
														.attr('href',
																'${pageContext.request.contextPath}/ecpos/#!sales');
											});

						}

						$scope.selectedRow = null;

						$scope.setClickedRow = function(index) {
							$scope.selectedRow = index;
						}

						//Success
						function getTransactionListDate(date){
							
							var jsonDate = JSON.stringify( {
								"datetime": date
							}) ;
													
							$http
							.post(
									"${pageContext.request.contextPath}/memberapi/show_trans/get_transaction_list_date"
											, jsonDate)
							.then(
									function(response) {
										console.log(response.data);
										$scope.fields_TransList = response.data;

										//load first transaction data on page-show
										$scope.selectedTransaction = $scope.fields_TransList.trans_list[0].tran_id;
										$scope
												.get_transaction_details($scope.selectedTransaction);

									},
									function(response) {
										alert("Session TIME OUT");
										$(location)
												.attr('href',
														'${pageContext.request.contextPath}/ecpos/#!sales');
									});
							
						}
						
						//Calendar feature start here
						$("#myCalendar")
								.on("changeDate",
										function(event) {
											console.log(event);
											var date = event
													.format("yyyy-mm-dd");

											getTransactionListDate(date);
						});
						
						
						
						//To Print Receipt
						$scope.printExistingReceipt = function(checkNumber){
							
							console.log("Printing check no");
							console.log(checkNumber);
							
							var jsonData = JSON.stringify( {
								"checkNumber": checkNumber
							}) ;
							
							console.log(jsonData);
							
		 					$http
							.post(
									"${pageContext.request.contextPath}/printerapi/print_tranx_receipt"
											, jsonData)
							.then(
									function(response) {
										console.log("Success Printing Transaction Receipt");				
									},
									function(response) {
										alert("Cannot Print Receipt. Please Check Your Printer Setting.");
									});
	
						}
						
						/*================ Terminal Model Functions[START] ================*/
						$scope.submitTerminalSelectionData = function(transactionId, checkNumber){	
							if($scope.selectedTerminal){		 				
								var jsonData = JSON.stringify({
									'selectedTerminal': $scope.selectedTerminal,
									'transactionId': transactionId,
									'checkNumber': checkNumber
								}); 
								
								console.log(jsonData);

								$('#terminal_selection_modal').modal('hide');
								$(".modal-backdrop").remove();
								$('#loading_modal').modal('show');
								
 					 			$http
								.post(
										'${pageContext.request.contextPath}/payment/voidPayment', jsonData)
								.then(
										function(response) {
											if(response.data.response_code === '00'){
												console.log("Success");
												
												$('#loading_modal').modal('hide');
												$(".modal-backdrop").remove();
												$scope.resetTerminalModel(); //reset the field
												
												$scope.getTransactionList(); //refresh the transaction list
											}
											else if(response.data.response_code === '01'){
												$('#loading_modal').modal('hide');
												$(".modal-backdrop").remove();
												$scope.resetTerminalModel(); //reset the field
												alert("Failed to Void Transaction! Please Try Again Later.");
											}
										},
										function(response) {
											$('#loading_modal').modal('hide');
											$(".modal-backdrop").remove();
											$scope.resetTerminalModel(); //reset the field
											alert("Cannot Void The Selected Transaction!");
										}); 
		 
							}
						}
						
						$scope.resetTerminalModel = function(){
							$('#terminal_device_selection').val('');
							$scope.selectedTerminal = {};
						}
						
						$scope.getTerminalList = function(){			
							$http
							.get(
									"${pageContext.request.contextPath}/payment/terminalList")
							.then(
									function(response) {
										if(response.data.response_code == "00"){
											$scope.terminalList = response.data.terminalList;
										}
										else {
											alert("Cannot Retrieve Terminal List. Please Try Again Later.");
										}
									},
									function(response) {
										alert("Server Error In Retrieve Terminal List. Please Try Again Later.");
									}); 
						}
						
						/*================ Terminal Model Functions[END] ================*/
				
					});
</script>
