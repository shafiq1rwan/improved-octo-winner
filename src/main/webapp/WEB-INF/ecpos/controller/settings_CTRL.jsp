<script>
	app.controller('settings_CTRL', function($scope, $http, $window, $routeParams, $location) {
		$scope.terminal = {};
		
		$scope.action = "";
		$scope.settlementType = "";
		
		$("#terminalList").hide();
		
		$scope.initiation = function() {
			$scope.getPrinterList();
			$scope.getTerminalList();
		}
		
		$scope.getPrinterList = function() {
			$http.get("${pageContext.request.contextPath}/rc/configuration/get_printer_detail/")
			.then(function(response) {
				$scope.printerDetail = response.data;
				
				if ($scope.printerDetail.hasOwnProperty("selectedPrinter")) {
					$("printer").val("");
					for (var i = 0; i < $scope.printerDetail.portInfoList.length; i++) {
						if ($scope.printerDetail.portInfoList.PortInfo[i].PortName === $scope.printerDetail.selectedPrinter) {
							$scope.printerDetail.portInfoList.PortInfo[i].selected = true;
						}
					}
				}
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/ecpos";
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
				window.location.href = "${pageContext.request.contextPath}/ecpos";
			});
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
					window.location.href = "${pageContext.request.contextPath}/ecpos";
				});
			}
			
			$("#terminalModal").modal("show");
		}
		
		$scope.showSettlementModal = function(serialNo) {
			$scope.settlementType = "";
			$scope.terminal.serialNo = serialNo;
			$("#settlementModal").modal("show");
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
				window.location.href = "${pageContext.request.contextPath}/ecpos";
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
				window.location.href = "${pageContext.request.contextPath}/ecpos";
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
				window.location.href = "${pageContext.request.contextPath}/ecpos";
			});
		}
	});
</script>