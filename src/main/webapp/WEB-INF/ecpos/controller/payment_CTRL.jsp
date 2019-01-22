<script>
	app.controller('payment_CTRL', function($scope, $http, $timeout, $location, $route, $routeParams) {
		var checkNumber = $routeParams.check_no;
		$scope.checkNum = checkNumber;
		console.log(checkNumber);

		$scope.chkTotal = {};
		$scope.takeAwayOrders = [];
		$scope.tableOrders = [];
		$scope.totalPriceDisplay = 0.00;

		$scope.terminalList = [];
		$scope.selectedTerminal; // the selected terminal used for transaction

		$scope.isRemoveAvailable = true;
		$scope.isPaymentAvailable = true;
		$scope.isPaymentDataAvailable = false;

		//reset upon close
		$scope.resetTerminalModel = function() {
			$('#terminal_device_selection').val('');
			$scope.selectedTerminal = {};
		}

		//Intiatiation
		$scope.paymentInitialization = function() {
			getCheckTotal(checkNumber);
			$scope.checkOrderList();
			$scope.getTerminalList();
			//$scope.renderPaymentDatatable($scope.chkTotal);		
		}

		$scope.getStoreId = function() {

		}

		function getCheckTotal(checkNumber) {

			$http
					.get(
							"${pageContext.request.contextPath}/payment/checkTotal/"
									+ String(checkNumber))
					.then(
							function(response) {
								if (response.status == 200) {
									if (response.data.transactionPrice) { //if it appear
										$scope.chkTotal = response.data;
										renderPaymentDatatable($scope.chkTotal);
										/* 	if(paymentDatatable.rows().data().count()> 1){
												$scope.isPaymentAvailable = false;
											}  */
										$scope
												.transactionAmountCalculation();
									} else {
										$scope.chkTotal = {}; //because user choose no check
									}
								} else if (response.status == 404) {
									$scope.chkTotal = {}; //empty result 
								}
							},
							function(response) {
								alert("Cannot Retrieve Payment Total");
								$(location)
										.attr('href',
												'${pageContext.request.contextPath}/ecpos/#!sales');
							});
		}

		$scope.ringgitMalaysiaArray = [ 'RM1', 'RM5', 'RM10',
				'RM20', 'RM50', 'RM100' ];

		//When user pay $$$ by pressing ringgit
		$scope.addRinggitIntoPayment = function(ringgitValue) {
			var numericalRinggitValue = parseFloat(
					ringgitValue.substring(2)).toFixed(2);
			console.log(numericalRinggitValue);
			addTransactionIntoDatatable("RM "
					+ String(numericalRinggitValue),
					numericalRinggitValue);
		}

		$scope.checkOrderList = function() {
			$http
					.get(
							"${pageContext.request.contextPath}/payment/unpaidOrderChecks")
					.then(
							function(response) {
								if (response.status == 200) {
									/* 		if(response.data.takeAwayOrderList)
											$scope.takeAwayOrders = response.data.takeAwayOrderList;
											console.log($scope.takeAwayOrders); */

									if (response.data.tableOrderList)
										$scope.tableOrders = response.data.tableOrderList;
									console
											.log($scope.tableOrders);
								} else if (response.status == 404) {
									$scope.chkTotal = {}; //empty result 
								}
							},
							function(response) {
								alert("Session TIME OUT");
								$(location)
										.attr('href',
												'${pageContext.request.contextPath}/ecpos/#!sales');
							});
		}

		//Datatable 
		var paymentDatatable;

		function renderPaymentDatatable(checkTotal) {

			console.log("Here my check total");
			var a = [ checkTotal ];
			console.log(checkTotal);

			paymentDatatable = $("#payment_datatable")
					.DataTable(
							{
								"responsive" : true,
								"scrollY" : '25vh',
								"paging" : false,
								"searching" : false,
								"info" : false,
								"data" : a,
								"destroy" : true,
								columnDefs : [
										{
											"data" : null,
											"defaultContent" : '',
											"className" : 'select-checkbox',
											"orderable" : false,
											"searchable" : false,
											"targets" : 0
										},
										{
											"data" : "transactionName",
											"targets" : 1
										},
										{
											"data" : "transactionPrice",
											"className" : 'text-right',
											"render" : function(
													data, type,
													full) {
												return parseFloat(
														data)
														.toFixed(
																2);
											},
											"targets" : 2
										} ],
								select : {
									style : 'single'
								},
								order : [ [ 1, 'asc' ] ],
								"initComplete" : function(
										settings, json) {

								}
							});

			//var firstRow = paymentDatatable.select.selector( 'td:first-child' );
			//firstRow.data()[0].removeClass('select-checkbox');

			paymentDatatable.on('select deselect', function(e,
					dt, type, indexes) {
				if (type === 'row') {
					var data = paymentDatatable.rows({
						selected : true
					}).data();

					if (data.count() > 0) {
						$scope.isRemoveAvailable = false;
						$scope.$apply();
					} else if (data.count() === 0) {
						$scope.isRemoveAvailable = true;
						$scope.$apply();
					}

				}

			});

		}

		$scope.getChkTotal = function(checkNumber) {
			var data = "/payment/" + checkNumber;
			$location.path(data).replace();
		}

		//Calculator function
		$scope.add_calc = function(id, n) {
			var amount = document.getElementById(id).innerHTML;

			if (n === 99) {
				console.log(amount);
				addTransactionIntoDatatable('RM '
						+ String(amount), amount);

				document.getElementById(id).innerHTML = "0.00";
				document.getElementById('hidden_' + id).value = "0.00";
			} else {
				if (n != 20 && n != -1) {
					amount = amount + n;
				}
				amount = amount.replace(",", "");

				var i = parseFloat(amount);
				if (n === -1) {
					i = i / 10;
				} else {
					if (amount.length < 10) {
						if (n === 20) {
							i = i * 100;
						} else {
							i = i * 10;
						}
					}
				}

				var temp = i.toFixed(3);
				temp = temp.substring(0, temp.length - 1);
				document.getElementById(id).innerHTML = temp;
				document.getElementById('hidden_' + id).value = temp;
			}
		}

		//Success
		function addTransactionIntoDatatable(description,
				amount) {
			paymentDatatable.row.add({
				'transactionName' : description,
				'transactionPrice' : amount
			}).draw();

			if (paymentDatatable.rows().data().count() > 1) {
				$scope.isPaymentAvailable = false;
			}

			//$scope.totalPriceDisplay -= parseFloat(amount);
			$scope.transactionAmountCalculation();
		}

		$scope.removePaymentTransaction = function() {
			//paymentDatatable.row({ selected: true }).remove().draw(true);

			var paymentTotal = paymentDatatable.row({
				selected : true
			}).data();
			if (paymentTotal.transactionName !== 'Check Total') {
				//console.log(paymentTotal.transactionName);
				paymentDatatable.row({
					selected : true
				}).remove().draw(true);
			}
			
			if (paymentDatatable.rows().data().count() === 1) {
				$scope.isPaymentAvailable = true;
			}

			$scope.transactionAmountCalculation();
		}

		$scope.transactionAmountCalculation = function() {

			var rowsData = paymentDatatable.rows().data()
					.toArray();
			var itemHolder = [];
			console.log(rowsData.length);
			for (var i = 0; i < rowsData.length; i++) {

				if (rowsData[i].transactionName === 'Check Total') {
					$scope.totalPriceDisplay = rowsData[i].transactionPrice;
				}

				if (rowsData[i].transactionName !== 'Check Total') {
					$scope.totalPriceDisplay -= parseFloat(rowsData[i].transactionPrice);

				}

			}

			if ($scope.totalPriceDisplay < 0) {
				$scope.positiveBalance = {
					"color" : "red",
				}
			} else {
				$scope.positiveBalance = {
					"color" : "black",
				}
			}

		}

		//Make cash payment
		$scope.makeCashPayment = function() {
			var rowsData = paymentDatatable.rows().data()
					.toArray(); //remove the first element
			var itemHolder = [];

			//Exclude first because it is non-related
			for (var i = 0; i < rowsData.length; i++) {

				if (rowsData[i].transactionName != 'Check Total') {
					var myTransactionItem = {
						'transactionName' : rowsData[i].transactionName,
						'transactionPrice' : rowsData[i].transactionPrice
					}

					itemHolder.push(myTransactionItem);
				}
			}

			var jsonData = JSON.stringify({
				'checkNumber' : checkNumber,
				'transactionList' : itemHolder
			});

			//http
			$http
					.post(
							"${pageContext.request.contextPath}/payment/cashPayment",
							jsonData)
					.then(
							function(response) {
								if (response.status == 200) {
									if (response.data.response_code === "00") {
										if (response.data.printReceipt)
											printReceipt(checkNumber);
									} else {
										alert('Cannot Perform Cash Payment.Please Try Again Later.');
									}

									$(location)
											.attr('href',
													'${pageContext.request.contextPath}/ecpos/#!sales');
								}
							},
							function(response) {
								alert("Cannot Retrieve Payment Total");
								$(location)
										.attr('href',
												'${pageContext.request.contextPath}/ecpos/#!sales');
							});

			console.log(itemHolder);
		}

		/* 	$scope.makeCardPayment = function(){
		
				console.log($scope.chkTotal.transactionPrice);
				
			 	var jsonData = JSON.stringify({
			 		'tranType':'card-sale',
			 		'amount':$scope.chkTotal.transactionPrice,
			 		'tips':0.00
				}); 
			
				//http
				$http
						.post(
								"${pageContext.request.contextPath}/payment/pingTest",
								jsonData)
						.then(
								function(response) {
									if(response.data.responseCode == "00"){
										alert("Ping Success");
									}
									else {
										alert("Ping Failed");	
									}
		
								},
								function(response) {
									
								}); 
			} */

		//Printing the receipt
		function printReceipt(checkNumber, cardPayment) {
			var jsonData
			var cardPayment = cardPayment || undefined;

			if (cardPayment) {
				jsonData = JSON.stringify({
					'checkNumber' : checkNumber
				});
			} else {
				jsonData = JSON.stringify({
					'checkNumber' : checkNumber,
					'cardResponse' : cardPayment
				});
			}

			console.log(jsonData);

			$http
					.post(
							"${pageContext.request.contextPath}/printerapi/print_tranx_receipt",
							jsonData).then(function(response) {
						if (response.data.responseCode == "00")
							alert("Print Receipt Success");
					}, function(response) {
						alert("Print Receipt Failure");
					});

		}

		//Success
		$scope.getTerminalList = function() {

			$http
					.get(
							"${pageContext.request.contextPath}/payment/terminalList")
					.then(
							function(response) {
								if (response.data.response_code == "00") {
									$scope.terminalList = response.data.terminalList;
									console
											.log("Available Terminal List");
									console
											.log($scope.terminalList);
								} else {
									alert("Cannot Retrieve Terminal List. Please Try Again Later.");
								}
							},
							function(response) {
								alert("Server Error In Retrieve Terminal List. Please Try Again Later.");
							});
		}

		//Make Card Payment //Need modified the printer
		$scope.submitTerminalSelectionData = function() {
			if ($scope.selectedTerminal) {
				var jsonData = JSON
						.stringify({
							'checkNumber' : checkNumber,
							'selectedTerminal' : $scope.selectedTerminal,
							'tranType' : 'card-sale',
							'amount' : $scope.chkTotal.transactionPrice,
							'tips' : 0.00
						});

				console.log(jsonData);

				$('#terminal_selection_modal').modal('hide');
				$(".modal-backdrop").remove();
				$('#loading_modal').modal('show');

				$http
						.post(
								'${pageContext.request.contextPath}/payment/cardPayment',
								jsonData)
						.then(
								function(response) {
									if (response.data.response_code === '00') {
										console.log("Success");

										$('#loading_modal')
												.modal('hide');
										$(".modal-backdrop")
												.remove();

										//print receipt here
										printReceipt(
												checkNumber,
												response.data.cardResponse); //todo

										$(location)
												.attr('href',
														'${pageContext.request.contextPath}/ecpos/#!sales');

									} else if (response.data.response_code === '01') {

										$('#loading_modal')
												.modal('hide');
										$(".modal-backdrop")
												.remove();
										$scope
												.resetTerminalModel();

										alert("Cannot Perform Transcation. Please Try Again Later.");
									}
								},
								function(response) {
									$('#loading_modal').modal(
											'hide');
									$(".modal-backdrop")
											.remove();
									$scope.resetTerminalModel();

									alert("Terminal Encountered some Problem while Performing Transaction");
								});
			}

		}

	});
</script>