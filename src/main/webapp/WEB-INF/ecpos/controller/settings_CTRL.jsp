<script>
	app.controller('settings_CTRL', function($scope, $http, $window, $routeParams, $location) {
		$scope.terminal = {};
		
		$scope.action = "";
		$scope.settlementType = "";
		
		$("#terminalList").hide();
		
		$scope.initiation = function() {
			$http.get("${pageContext.request.contextPath}/rc/configuration/session_checking")
			.then(function(response) {
				if (response.data.responseCode == "00") {
					$scope.getCashDrawerList();
					$scope.getPrinterList();
					$scope.getTerminalList();
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
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/signout";
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
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/signout";
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
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/signout";
			});
		}
		
		$scope.saveCashDrawer = function(){
			if($scope.selectedDeviceManufacturer == null || $scope.selectedPortName == null || $scope.selectedDeviceManufacturer == '' || $scope.selectedPortName == ''){
				console.log("Please Select Both Device Manufacturer and Port Name.")
			} else {
				console.log($scope.selectedDeviceManufacturer + " " + $scope.selectedPortName)
				
				//fire saved printer event
				var jsonData = JSON.stringify({
					'device_manufacturer' : $scope.selectedDeviceManufacturer,
					'port_name' : $scope.selectedPortName
				});
				
				$http.post("${pageContext.request.contextPath}/rc/configuration/save_cash_drawer", jsonData)
				.then(function(response) {
					alert("Cash Drawer Successfully Set.");
					$scope.getCashDrawerList();
				},
				function(response) {
					alert("Session TIME OUT");
					window.location.href = "${pageContext.request.contextPath}/signout";
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
					alert("Receipt Printer Successfully Set.");
					$scope.getPrinterList();
				},
				function(response) {
					alert("Session TIME OUT");
					window.location.href = "${pageContext.request.contextPath}/signout";
				});
			}
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
					alert("Session TIME OUT");
					window.location.href = "${pageContext.request.contextPath}/signout";
				});
			}
			
			$("#terminalModal").modal("show");
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
				alert(response.data.response_message);
				
				if (response.data.response_code === "00") {
					$("#terminalModal").modal("hide");
					$scope.getTerminalList();
				}
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/signout";
			});
		}
		
		$scope.removeTerminal = function(id) {
			var jsonData = JSON.stringify({
				"id" : id
			});
			
			$http.post("${pageContext.request.contextPath}/rc/configuration/remove_terminal", jsonData)
			.then(function(response) {
				alert(response.data.response_message);
				
				if (response.data.response_code === "00") {
					$scope.getTerminalList();
				}
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/signout";
			});
		}
		
		$scope.requestSettlement = function() {
			var jsonData = JSON.stringify({
				"terminalSerialNo" : $scope.terminal.serialNo,
				"settlementType" : $scope.settlementType
			});
			
			$http.post("${pageContext.request.contextPath}/rc/transaction/request_settlement", jsonData)
			.then(function(response) {
				alert(response.data.response_message);
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/signout";
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
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/signout";
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
						$scope.syncSuccess(response.data.resultMessage);
					} else {
						$scope.syncFailed(response.data.resultMessage);
					}
				} else {
					$scope.syncFailed("Invalid server response!");
				}
			}, 
			function(error) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/signout";
			});
		}
		
		$scope.submitSyncTransaction = function() {
			$('#loading_modal').modal('show');
			$http({
				method : 'POST',
				headers : {'Content-Type' : 'application/json'},
				url : '${pageContext.request.contextPath}/syncTransaction'
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
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/signout";
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
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/signout";
			});
		}
	});
</script>