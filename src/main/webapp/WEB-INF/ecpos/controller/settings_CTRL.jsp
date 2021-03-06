<script>
	app.controller('settings_CTRL', function($scope, $http, $window, $routeParams, $location, $compile) {
		$scope.terminal = {};
		$scope.cashFlowLog = {};
		$scope.transConfig = {};
		
		$scope.action = "";
		$scope.settlementType = "";
		
		$scope.cashFlowAmount;
		
		$("#terminalList").hide();
		
		$scope.initiation = function() {
			$http.get("${pageContext.request.contextPath}/rc/configuration/session_checking")
			.then(function(response) {
				if (response.data.responseCode == "00") {
					$scope.getCashDrawerList();
					$scope.getPrinterList();
					$scope.getQRPaymentList();
					$scope.getTerminalList();
					$scope.getStoreInfo();
					$scope.getTransConfigList();
					$scope.getTransConfigIntervalList();
					$scope.getPaymentMethod();
				} else {
					/* alert("Session TIME OUT"); */
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
					/* window.location.href = "${pageContext.request.contextPath}/signout"; */
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

		$scope.getCashDrawerList = function() {
			$http.get("${pageContext.request.contextPath}/rc/configuration/get_cash_drawer_setup_info")
			.then(function (response) {
				$scope.cashDrawerData = response.data;
				
				//if selectedCashDrawer exist
				if($scope.cashDrawerData.hasOwnProperty("selectedCashDrawer")){
					$scope.selectedCashDrawer = response.data.selectedCashDrawer;
					
					for(var i =0; i< $scope.cashDrawerData.device_manufacturers.length; i++){
						if($scope.cashDrawerData.device_manufacturers[i].id === $scope.selectedCashDrawer.device_manufacturer){
							$scope.selectedDeviceManufacturer = $scope.selectedCashDrawer.device_manufacturer;
						}
					}
					
					for(var j = 0; j< $scope.cashDrawerData.port_names.length; j++){
						if($scope.cashDrawerData.port_names[j].id === $scope.selectedCashDrawer.port_name){
							$scope.selectedPortName = $scope.selectedCashDrawer.port_name;
						}
					}
				}
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

		$scope.getPrinterList = function() {
			$http.get("${pageContext.request.contextPath}/rc/configuration/get_receipt_printer_manufacturers")
			.then(function (response) {
				$scope.receiptPrinterData = response.data;
				
				if($scope.receiptPrinterData.hasOwnProperty("selectedReceiptPrinter")){
					$scope.selectedReceiptPrinterManufacturer = response.data.selectedReceiptPrinter;
					
		/* 			for(var i =0; i< $scope.cashDrawerData.device_manufacturers.length; i++){
						if($scope.cashDrawerData.device_manufacturers[i].id === $scope.selectedCashDrawer.device_manufacturer){
							$scope.selectedDeviceManufacturer = $scope.selectedCashDrawer.device_manufacturer;
						}
					} */

				}
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

		$scope.getQRPaymentList = function() {
			$http.get("${pageContext.request.contextPath}/rc/configuration/get_qr_payment_list/" + "all")
			.then(function (response) {
				$scope.qrPaymentData = response.data;
				
				if($scope.qrPaymentData.hasOwnProperty("selectedQRPayment")){
					$scope.selectedQRPaymentMethod = response.data.selectedQRPayment;
					$scope.disableEditDeleteButton();
				}
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
		
		$scope.getTerminalList = function() {
			$http.get("${pageContext.request.contextPath}/rc/configuration/get_terminal_list/" + "all")
			.then(function(response) {
				$scope.terminalList = response.data;
				
				if ($scope.terminalList.terminals === undefined || $scope.terminalList.terminals == 0) {
					$("#terminalList").hide();
				} else {
					$("#terminalList").show();
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
		
		$scope.getTransConfigList = function(){
			$http.get("${pageContext.request.contextPath}/rc/configuration/get_trans_config")
			.then(function(response) {
				var result = response.data;
				$scope.transConfig.staffSyncFlag = result.staffSyncFlag;
				$scope.transConfig.transSyncFlag = result.transSyncFlag;
				$scope.transConfig.selectedInterval = result.selectedInterval;
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

		$scope.getPaymentMethod = function(){
			$http.get("${pageContext.request.contextPath}/rc/configuration/getPaymentMethod")
			.then(function(response) {
				var result = response.data;
				$scope.paymentMethod = {
					       cash : result.cash === 'true',
					       card : result.card === 'true',
					       ewallet : result.ewallet === 'true',
					       staticqr : result.staticqr === 'true'};
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
		
		$scope.getTransConfigIntervalList = function(){
			$http.get("${pageContext.request.contextPath}/rc/configuration/get_trans_interval_list")
			.then(function(response) {
				$scope.transConfig.intervalList = response.data.transConfigIntervalList;
				console.log($scope.transConfig.intervalList);
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
		
		$scope.getStoreInfo = function() {
			$http.get("${pageContext.request.contextPath}/rc/configuration/get_store_data")
			.then(function(response) {
				$scope.store = response.data;
				console.log($scope.store);
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
		
		$scope.saveCashDrawer = function(){
			if($scope.selectedDeviceManufacturer == null || $scope.selectedPortName == null || $scope.selectedDeviceManufacturer == '' || $scope.selectedPortName == ''){
				/* alert("Please Select Both Device Manufacturer and Port Name."); */
				Swal.fire("Please Select Both Device Manufacturer and Port Name.");
			} else if (typeof $scope.cashDrawerData.cash_alert === "undefined") {
				/* alert("Invalid Cash Alert value."); */
				Swal.fire("Warning","Invalid Cash Alert value.","warning");
			} else if ($scope.cashDrawerData.cash_alert < 0) {
				/* alert("Cash Alert minimum value (0 as disable)."); */
				Swal.fire("Warning","Cash Alert minimum value (0 as disable).","warning");
			} else {
				console.log($scope.selectedDeviceManufacturer + " " + $scope.selectedPortName)
				
				//fire saved printer event
				var jsonData = JSON.stringify({
					'device_manufacturer' : $scope.selectedDeviceManufacturer,
					'port_name' : $scope.selectedPortName,
					'cash_alert' : $scope.cashDrawerData.cash_alert
				});
				
				$http.post("${pageContext.request.contextPath}/rc/configuration/save_cash_drawer", jsonData)
				.then(function(response) {
					/* alert("Cash Drawer Successfully Set."); */
					Swal.fire({
					  title: 'Are you sure?',
					  text: "You can change this anytime.",
					  icon: 'warning',
					  showCancelButton: true,
					  confirmButtonColor: '#3085d6',
					  cancelButtonColor: '#d33',
					  confirmButtonText: 'Yes'
					}).then((result) => {
					  if (result.value) {
						  location.reload();
					  }
					});
					/* location.reload(); */
					//$scope.getCashDrawerList();
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
		}
		
		$scope.saveReceiptPrinter = function(){
			if($scope.selectedReceiptPrinterManufacturer == null || $scope.selectedReceiptPrinterManufacturer == ''){
				console.log("Please Select Receipt Printer Manufacturer.")
			} else {
				var jsonData = JSON.stringify({
					'receipt_printer_manufacturer' : $scope.selectedReceiptPrinterManufacturer,
				});
				console.log("Saved Printer Data: " + jsonData);
				
				$http.post("${pageContext.request.contextPath}/rc/configuration/save_receipt_printer", jsonData)
				.then(function(response) {
					/* alert("Receipt Printer Successfully Set."); */
					/* $scope.getPrinterList(); */
					Swal.fire({
						  title: 'Are you sure?',
						  text: "You can change this information again.",
						  icon: 'warning',
						  showCancelButton: true,
						  confirmButtonColor: '#3085d6',
						  cancelButtonColor: '#d33',
						  confirmButtonText: 'Yes'
						}).then((result) => {
						  if (result.value) {
							  Swal.fire(
								      'Success!',
								      'Receipt Printer Successfully Set.',
								      'success'
								    )
							$scope.getPrinterList();
						  }
						});
					
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
		}

		$scope.saveQRPaymentMethod = function(){
			if($scope.selectedQRPaymentMethod == null || $scope.selectedQRPaymentMethod == ''){
				console.log("Please Select QR Payment Method.")
			} else {
				Swal.fire({
					title: 'Are you sure?',
					text: "You can change this information again.",
					icon: 'warning',
					showCancelButton: true,
					confirmButtonColor: '#3085d6',
					cancelButtonColor: '#d33',
					confirmButtonText: 'Yes'
				}).then((result) => {
					if (result.value) {
						var jsonData = JSON.stringify({
							'qr_payment_method' : $scope.selectedQRPaymentMethod,
						});
						console.log("Saved Payment Method: " + jsonData);
						
						$http.post("${pageContext.request.contextPath}/rc/configuration/save_qr_payment_method", jsonData)
						.then(function(response) {
							Swal.fire(
								'Success!',
								'QR Payment Method Successfully Set.',
								'success'
							)
							$scope.getPrinterList();
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
			}
		}
		
		$scope.showCashModal = function(action) {
			$scope.action = action;
			$scope.cashFlowAmount = "0.00";
			$("#cashModal").modal("show");
		}
		$scope.updateCashAmount = function(number) {
			if ($scope.cashFlowAmount.length < 10) {
				if ($scope.cashFlowAmount == "0.00") {
					if (number != 0) {
						$scope.cashFlowAmount = "0.0" + number;
					}
				} else {
					$scope.cashFlowAmount = parseFloat(($scope.cashFlowAmount + number) * 10).toFixed(2);
				}
			}
		}
		$scope.clearCashAmount = function() {
			$scope.cashFlowAmount = "0.00";
		}
		$scope.submitCashInfo = function() {
			if ($scope.cashFlowAmount == "0.00") {
				/* alert("Please enter cash value."); */
				Swal.fire('Please enter cash value.')
				return;
			}
			
			$("#cashModal").modal("hide");
			$('#loading_modal').modal('show');
			$http({
				method : 'POST',
				headers : {
					'Content-Type' : 'application/json'
				},
				params : {
					type : $scope.action,
					amount : $scope.cashFlowAmount
				},
				url : '${pageContext.request.contextPath}/rc/configuration/updateCashFlow'
			}).then(function(response) {
				if (response != null && response.data != null
						&& response.data.resultCode != null) {
					if (response.data.resultCode == "00") {
						$scope.cashDrawerData.cash_amount = response.data.amount;
						$scope.cashUpdateSuccess(response.data.resultMessage);
					} else {
						$scope.cashUpdateFailed(response.data.resultMessage);
					}
				} else {
					$scope.cashUpdateFailed("Invalid server response!");
				}
			}, function(error) {
				$scope.cashUpdateFailed("Unable to connect to server!");
			});
		}
		$scope.cashUpdateSuccess = function() {
			$('#loading_modal').modal('hide');
			var dialogOption = {};
			if ($scope.action == 'cashIn') {
				dialogOption.title = "Cash In Success!";
				dialogOption.message = "Cash In Performed Successfully!";
			} else {
				dialogOption.title = "Cash Out Success!";
				dialogOption.message = "Cash Out Performed Successfully!";
			}
			dialogOption.button1 = {
				name: "OK",
				fn: function() {
					$("div#modal-dialog").modal("hide");
				}
			}
			$scope.displayDialog(dialogOption);
		}
		$scope.cashUpdateFailed = function(message) {
			$('#loading_modal').modal('hide');
			var dialogOption = {};
			if ($scope.action == 'cashIn') {
				dialogOption.title = "Cash In Failed!";
			} else {
				dialogOption.title = "Cash Out Failed!";
			}
			dialogOption.message = message;
			dialogOption.button1 = {
				name: "OK",
				fn: function() {
					$("div#modal-dialog").modal("hide");
				}
			}
			$scope.displayDialog(dialogOption);
		}
		
		$scope.showCashLogModal = function(action) {
			$scope.getCashFlowLogList();
			$("#cashLogModal").modal("show");
		}
		
		$scope.showTerminalModal = function(action, id) {
			$scope.action = action;
			$scope.terminal = {};
			$scope.terminalId = id;
			
			if ($scope.action == "update") {
				$http.get("${pageContext.request.contextPath}/rc/configuration/get_terminal_list/" + $scope.terminalId)
				.then(function(response) {
					$scope.terminal.id = response.data.terminals[0].id;
					$scope.terminal.name = response.data.terminals[0].name;
					$scope.terminal.serialNo = response.data.terminals[0].serialNo;
					$scope.terminal.wifiIP = response.data.terminals[0].wifiIP;
					$scope.terminal.wifiPort = response.data.terminals[0].wifiPort;
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
			
			$("#terminalModal").modal("show");
		}

		$scope.showQRPaymentModal = function(action, id) {
			$scope.action = action;
			$scope.qrPayment = {};
			$scope.qrPaymentId = id;
			
			if ($scope.action == "update") {
				$http.get("${pageContext.request.contextPath}/rc/configuration/get_qr_payment_list/" + $scope.qrPaymentId)
				.then(function(response) {
					$scope.qrPayment.id = response.data.qrPayments[0].id;
					$scope.qrPayment.name = response.data.qrPayments[0].name;
					$scope.qrPayment.tid = response.data.qrPayments[0].tid;
					$scope.qrPayment.product_desc = response.data.qrPayments[0].product_desc;
					$scope.qrPayment.url = response.data.qrPayments[0].url;
					$scope.qrPayment.project_key = response.data.qrPayments[0].project_key;
					$scope.qrPayment.uuid = response.data.qrPayments[0].uuid;
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
			
			$("#qrPaymentModal").modal("show");
		}
		
		$scope.showSettlementModal = function(serialNo) {
			$scope.settlementType = "";
			$scope.terminal.serialNo = serialNo;
			$("#settlementModal").modal("show");
		}
		
		$scope.showReactivationModal = function() {
			$scope.reactivate = {};
			$("#reactivationModal").modal("show");
		}
		
		$scope.showTransConfigModal = function() {
			$("#transConfigModal").modal("show");
		}

		$scope.showPaymentMethodModal = function() {
			$("#paymentMethodModal").modal("show");
		}
		
		$scope.submitReactivation = function() {
			$('#loading_modal').modal('show');
			$http({
				method : 'POST',
				headers : {
					'Content-Type' : 'application/json'
				},
				params : {
					brandId : $scope.reactivate.brandId,
					activationId : $scope.reactivate.activationId,
					activationKey : $scope.reactivate.activationKey
				},
				url : '${pageContext.request.contextPath}/activation'
			}).then(function(response) {
				if (response != null && response.data != null
						&& response.data.resultCode != null) {
					if (response.data.resultCode == "00") {						
						$scope.syncSuccess(response.data.resultMessage, 1);							
					} else {
						$scope.syncFailed(response.data.resultMessage,  1);
					}
				} else {
					$scope.syncFailed("Invalid server response!", 1);
				}
			}, function(error) {
				$scope.syncFailed("Unable to connect to server!", 1);
			});
		}
		
		$scope.submitTransConfig = function() {
			$('#loading_modal').modal('show');
			var postdata = {
				staffSyncFlag : $scope.transConfig.staffSyncFlag,
				transSyncFlag : $scope.transConfig.transSyncFlag,
				selectedInterval : $scope.transConfig.selectedInterval
			}
			console.log(postdata);
			$http({
				method : 'POST',
				headers : {
					'Content-Type' : 'application/json'
				},
				data : postdata,
				url : '${pageContext.request.contextPath}/rc/configuration/save_trans_config'
			}).then(function(response) {
				if (response != null && response.data != null
						&& response.data.resultCode != null) {
					if (response.data.resultCode == "00") {						
						$scope.syncSuccess(response.data.resultMessage);
						$("#transConfigModal").modal("hide");
					} else {
						$scope.syncFailed(response.data.resultMessage);
					}
				} else {
					$scope.syncFailed("Invalid server response!");
				}
			}, function(error) {
				$scope.syncFailed("Unable to connect to server!");
			});
		}
		
		$scope.savePrinter = function() {
			$scope.printer = {};
			
			if ($("#printer").val() != null){
				var printerValueSplit = $("#printer").val().split("!!");
				$scope.printer.portName = printerValueSplit[0];
				$scope.printer.modelName = printerValueSplit[1];
				
				var jsonData = JSON.stringify({
					"portName" : $scope.printer.portName,
					"modelName" : $scope.printer.modelName,
					"paperSize" : 1
				});
				
				$http.post("${pageContext.request.contextPath}/rc/configuration/save_printer", jsonData)
			}
		}
		
		$scope.submitTerminalInfo = function() {
			var jsonData = JSON.stringify({
				"action" : $scope.action,
				"id" : $scope.terminal.id,
				"name" : $scope.terminal.name,
				"serialNo" : $scope.terminal.serialNo,
				"wifiIP" : $scope.terminal.wifiIP,
				"wifiPort" : $scope.terminal.wifiPort
			});
			
			$http.post("${pageContext.request.contextPath}/rc/configuration/save_terminal", jsonData)
			.then(function(response) {
				/* alert(response.data.response_message);
				
				if (response.data.response_code === "00") {
					$("#terminalModal").modal("hide");
					$scope.getTerminalList();
				} */
				Swal.fire({
					  title: 'Are you sure?',
					  text: "You can change this information again.",
					  icon: 'warning',
					  showCancelButton: true,
					  confirmButtonColor: '#3085d6',
					  cancelButtonColor: '#d33',
					  confirmButtonText: 'Yes'
					}).then((result) => {
					  if (result.value) {
						  Swal.fire(
							      'Success!',
							      response.data.response_message,
							      'success'
							    )
						$("#terminalModal").modal("hide");
						$scope.getTerminalList();
					  }
					});
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

		$scope.submitQRPaymentInfo = function() {
			Swal.fire({
				title: 'Are you sure?',
				text: "You can change this information again.",
				icon: 'warning',
				showCancelButton: true,
				confirmButtonColor: '#3085d6',
				cancelButtonColor: '#d33',
				confirmButtonText: 'Yes'
			}).then((result) => {
				if (result.value) {
					var jsonData = JSON.stringify({
						"action" : $scope.action,
						"id" : $scope.qrPayment.id,
						"name" : $scope.qrPayment.name,
						"tid" : $scope.qrPayment.tid,
						"product_desc" : $scope.qrPayment.product_desc,
						"url" : $scope.qrPayment.url,
						"project_key" : $scope.qrPayment.project_key,
						"uuid" : $scope.qrPayment.uuid
					});
					
					$http.post("${pageContext.request.contextPath}/rc/configuration/save_qrPayment", jsonData)
					.then(function(response) {
						if (response.data.response_code === "00") {
							Swal.fire(
								'Success!',
								response.data.response_message,
								'success'
							)
							$("#qrPaymentModal").modal("hide");
							$scope.getQRPaymentList();
						} else {
							Swal.fire(
								'Failed!',
								response.data.response_message,
								'failed'
							)
						}
						
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
		}
		
		$scope.removeTerminal = function(id) {
			var jsonData = JSON.stringify({
				"id" : id
			});
			
			$http.post("${pageContext.request.contextPath}/rc/configuration/remove_terminal", jsonData)
			.then(function(response) {
				/* alert(response.data.response_message);
				
				if (response.data.response_code === "00") {
					$scope.getTerminalList();
				} */
				Swal.fire({
					  title: 'Are you sure?',
					  text: "You won't be able to revert this!",
					  icon: 'warning',
					  showCancelButton: true,
					  confirmButtonColor: '#3085d6',
					  cancelButtonColor: '#d33',
					  confirmButtonText: 'Yes'
					}).then((result) => {
					  if (result.value) {
						  Swal.fire(
							      'Deleted!',
							      response.data.response_message,
							      'success'
							    )
							$scope.getTerminalList();
					  }
					});
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

		$scope.removeQRPayment = function(id) {
			Swal.fire({
				title: 'Are you sure?',
				text: "You won't be able to revert this!",
				icon: 'warning',
				showCancelButton: true,
				confirmButtonColor: '#3085d6',
				cancelButtonColor: '#d33',
				confirmButtonText: 'Yes'
			}).then((result) => {
				if (result.value) {
					var jsonData = JSON.stringify({
						"id" : id
					});

					$http.post("${pageContext.request.contextPath}/rc/configuration/remove_qrPayment", jsonData)
					.then(function(response) {
						if (response.data.response_code === "00") {
							Swal.fire(
								'Deleted!',
								response.data.response_message,
								'success'
							)
							$scope.getQRPaymentList();
						} else {
							Swal.fire(
								'Failed Deleting!',
								response.data.response_message,
								'failed'
							)
						}
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
		}
		
		$scope.requestSettlement = function() {
			var jsonData = JSON.stringify({
				"terminalSerialNo" : $scope.terminal.serialNo,
				"settlementType" : $scope.settlementType
			});
			
			$http.post("${pageContext.request.contextPath}/rc/transaction/request_settlement", jsonData)
			.then(function(response) {
				/* alert(response.data.response_message); */
				Swal.fire(response.data.response_message);
				$('#settlementModal').modal('hide');
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

		$scope.submitSyncMenu = function() {
			$('#loading_modal').modal('show');
			$http({
				method : 'POST',
				headers : {'Content-Type' : 'application/json'},
				/*  params : {
					brandId : $scope.syncData.brand_id,
					activationId : $scope.syncData.act_id,
					activationKey : $scope.syncData.key
				}, */
				url : '${pageContext.request.contextPath}/syncMenu'
			}).then(function(response) {
				if (response != null && response.data != null && response.data.resultCode != null) {
					if (response.data.resultCode == "00") {						
						$scope.syncSuccess(response.data.resultMessage);
					} else {
						$scope.syncFailed(response.data.resultMessage, 2, response.data.resultCode);
					}
				} else {
					$scope.syncFailed("Invalid server response!");
				}
			}, 
			function(error) {
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
		
		$scope.submitSyncStore = function() {
			$('#loading_modal').modal('show');
			$http({
				method : 'POST',
				headers : {'Content-Type' : 'application/json'},
				/*  params : {
					brandId : $scope.syncData.brand_id,
					activationId : $scope.syncData.act_id,
					activationKey : $scope.syncData.key
				}, */
				url : '${pageContext.request.contextPath}/syncStore'
			}).then(function(response) {
				if (response != null && response.data != null && response.data.resultCode != null) {
					if (response.data.resultCode == "00") {						
						$scope.syncSuccess(response.data.resultMessage, 1);
					} else {
						$scope.syncFailed(response.data.resultMessage);
					}
				} else {
					$scope.syncFailed("Invalid server response!");
				}
			}, 
			function(error) {
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
		
		$scope.submitSyncTransaction = function() {
			$('#loading_modal').modal('show');
			var postdata = {
				brandId : $scope.store.brandId,
				storeId : $scope.store.storeId	
			}
			$http({
				method : 'POST',
				headers : {'Content-Type' : 'application/json'},
				url : '${pageContext.request.contextPath}/syncTransaction',
				data: postdata
			}).then(function(response) {
				if (response != null && response.data != null && response.data.resultCode != null) {
					if (response.data.resultCode == "00") {						
						$scope.syncSuccess(response.data.resultMessage);
					} else {
						$scope.syncFailed(response.data.resultMessage);
					}
				} else {
					$scope.syncFailed("Invalid server response!");
				}
			}, 
			function(error) {
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
		
		$scope.syncFailed = function(message, type, errorCode) {
			// type = 2, sync menu
			$('#loading_modal').modal('hide');
			var dialogOption = {};
			dialogOption.title = "Sync Failed!";
			if(type==1){
				dialogOption.title = "Reactivation Failed!";
			}
			dialogOption.message = message;
			dialogOption.button1 = {
				name: "OK",
				fn: function() {
					$("div#modal-dialog").modal("hide");			
					if(type==2){
						if(errorCode=='E02' || errorCode=='E03'){				
							window.location.href = "${pageContext.request.contextPath}/signout";
						}
					}
				}
			}
			$scope.displayDialog(dialogOption);
		}

		$scope.syncSuccess = function(message, type) {
			// type = 1, reactivate
			$('#loading_modal').modal('hide');
			var dialogOption = {};
			dialogOption.title = "Sync Success!";
			if(type==1){
				dialogOption.title = "Reactivation Success!"
			}
			dialogOption.message = message;
			dialogOption.button1 = {
				name: "OK",
				fn: function() {
					$("div#modal-dialog").modal("hide");
					if(type==1){
						window.location.href = "${pageContext.request.contextPath}/signout";
					}
					else{
						$scope.getPrinterList();
						$scope.getTerminalList();
					}
				}
			}
			$scope.displayDialog(dialogOption);
		}
		
		$scope.displayDialog = function(dialogOption) {
			$scope.dialogData = {};
			$scope.dialogData.title = dialogOption.title;
			$scope.dialogData.message = dialogOption.message;
			$scope.dialogData.button1 = dialogOption.button1;
			$scope.dialogData.button2 = dialogOption.button2;
			$scope.dialogData.isButton1 = typeof $scope.dialogData.button1 !== "undefined";
			$scope.dialogData.isButton2 = typeof $scope.dialogData.button2 !== "undefined";
			$('#modal-dialog').modal({backdrop: 'static', keyboard: false});
		}
		
		$scope.specialPopOut = function(message, title) {
			$('#ping_loading_modal').modal('hide');
			var dialogOption = {};
			dialogOption.title = title;
			dialogOption.message = message;
			dialogOption.button1 = {
				name: "OK",
				fn: function() {
					$("div#modal-dialog").modal("hide");
				}
			}
			$scope.displayDialog(dialogOption);
		}

		$scope.pingTerminal = function(id){	
			var jsonData = JSON.stringify({
				"id" : id
			});

			$('#ping_loading_modal').modal('show');
			
			$http.post("${pageContext.request.contextPath}/rc/configuration/ping_terminal", jsonData)
			.then(function(response) {
				if(response.data.response_code == "00"){
					$scope.specialPopOut(response.data.response_message, response.data.response_message);
				} else {
					$scope.specialPopOut(response.data.response_message, response.data.response_message);
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
		
		$scope.getCashFlowLogList = function() {
			var table = $('#datatable_cashflowlog').DataTable({
				"ajax" : {
					"url" : "${pageContext.request.contextPath}/rc/configuration/get_cash_flow_list",
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
				"pageLength": 10,
				"bLengthChange": false,
				"order" : [ [ 0, "desc" ] ],
				destroy : true,
				"columns" : [{"data" : "id"},
					{"data" : "cashAmount"}, 
					{"data" : "newAmount"},
					{"data" : "reference"},
					{"data" : "staffName"},
					{"data" : "datetime"}
					],
				rowCallback: function(row, data, index){
				},
				"createdRow": function ( row, data, index ) {
					$compile(row)($scope);
				}
			})
		};
		
		$scope.osDetection = function(){
			
		  var userAgent = window.navigator.userAgent,
		      platform = window.navigator.platform,
		      macosPlatforms = ['Macintosh', 'MacIntel', 'MacPPC', 'Mac68K'],
		      windowsPlatforms = ['Win32', 'Win64', 'Windows', 'WinCE'],
		      iosPlatforms = ['iPhone', 'iPad', 'iPod'],
		      os = null;
		  
		  var osDetect = document.getElementsByName("osDetect");

		  if (macosPlatforms.indexOf(platform) !== -1) {
		    os = 'Mac OS';
		  } else if (iosPlatforms.indexOf(platform) !== -1) {
		    os = 'iOS';
		  } else if (windowsPlatforms.indexOf(platform) !== -1) {
		    os = 'Windows';
		  } else if (/Android/.test(userAgent)) {
		    os = 'Android';
		  } else if (!os && /Linux/.test(platform)) {
		    os = 'Linux';
		  }
		
		  Swal.fire("Warning","Your OS: "+os,"warning");
		};
		
		$scope.checkBoxReceipt = function () {
			var jsonData = JSON.stringify({
				"receiptKitchen" : 1
			});
			
			$http.post("${pageContext.request.contextPath}/rc/configuration/receiptKitchenSet", jsonData)
			.then(function(response) {
				if(response.data.response_code == "00"){
					/* $scope.specialPopOut(response.data.response_message, "Success"); */
					Swal.fire("Congratulation",response.data.response_message,"success");
				} else {
					/* $scope.specialPopOut(response.data.response_message, "Failed"); */
					Swal.fire("Oops...",response.data.response_message,"error");
				}
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
		
		$scope.checkBoxKDS = function () {
			var jsonData = JSON.stringify({
				"receiptKitchen" : 1
			});
			
			$http.post("${pageContext.request.contextPath}/rc/configuration/receiptKitchenSet", jsonData)
			.then(function(response) {
				if(response.data.response_code == "00"){
					Swal.fire("Congratulation",response.data.response_message,"success");
				} else {
					Swal.fire("Oops...",response.data.response_message,"error");
				}
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

		$scope.disableEditDeleteButton = function() {
			if ($scope.selectedQRPaymentMethod === 1) { //if IPOS selected
				$('#editButton').prop('disabled',true);
				$('#deleteButton').prop('disabled',true);
			} else {
				$('#editButton').prop('disabled',false);
				$('#deleteButton').prop('disabled',false);
			}
		};

		$scope.submitPaymentMethod = function() {
			$('#loading_modal').modal('show');
			var postdata = {
				cash : $scope.paymentMethod.cash,
				card : $scope.paymentMethod.card,
				ewallet : $scope.paymentMethod.ewallet,
				staticqr : $scope.paymentMethod.staticqr
			}
			console.log(postdata);
			$http({
				method : 'POST',
				headers : {
					'Content-Type' : 'application/json'
				},
				data : postdata,
				url : '${pageContext.request.contextPath}/rc/configuration/savePaymentMethod'
			}).then(function(response) {
				if (response != null && response.data != null
						&& response.data.resultCode != null) {
					if (response.data.resultCode == "00") {						
						$scope.syncSuccess(response.data.resultMessage);
						$("#paymentMethodModal").modal("hide");
					} else {
						$scope.syncFailed(response.data.resultMessage);
					}
				} else {
					$scope.syncFailed("Invalid server response!");
				}
			}, function(error) {
				$scope.syncFailed("Unable to connect to server!");
			});
		}
		
	});
</script>