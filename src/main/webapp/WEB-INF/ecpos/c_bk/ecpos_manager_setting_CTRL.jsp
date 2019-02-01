<script>
	app
			.controller(
					'ECPOS_Manager_Setting_CTRL',
					function($scope, $http, $timeout, $location, $compile) {

						$scope.appSettingData = {};
						$scope.printerConfigurationData = {};
						$scope.terminalConfigurationInputData = {};
						$scope.editTerminalConfigurationInputData = {};
						$scope.selectedTerminal;
						$scope.isDefaultTerminal = true;

						$scope.loadSettingData = function() {
							$http
									.get(
											"${pageContext.request.contextPath}/settingapi/loadsetting")
									.then(
											function(response) {
												$scope.appSettingData = response.data;
												obtainPrinterConfigData();
												manipulateTerminalInfoTable();
											},
											function(response) {
												alert("Error Occured While Retrieving Setting Data");
												$(location)
														.attr('href',
																'${pageContext.request.contextPath}/ecpos/#!sales');
											});
						}

						function obtainPrinterConfigData() {
							$http
									.get(
											"${pageContext.request.contextPath}/printerapi/retrieve_printer_data")
									.then(
											function(response) {
												$scope.printerConfigurationData = response.data;
												manipulatePrinterConfigDOM($scope.printerConfigurationData);
											},
											function(response) {
												alert("Error Occured While Retrieving Printer Data");
												$(location)
														.attr('href',
																'${pageContext.request.contextPath}/ecpos');
											});
						}

						function manipulatePrinterConfigDOM(printerData) {
							//Manipulate printer div
							$('#printer_config_div').find('.printerName')
									.remove();
							var prePrinterDiv = $('#printer_config_div').find(
									'.printerName');
							if (prePrinterDiv.length === 0) {
								$('#printer_config_div').append(
										$compile(printerData.htmlElementString)
												($scope)); //1
							}
							if ($('#printer_config_div').find('.printerName').length !== 0) {
								if ($('.printerName').find(
										'#selectedPrinterName').prop("tagName") === 'INPUT') {
									$('.printerName #selectedPrinterName').val(
											printerData.selectedPrinter);
								}
							}
						}

						$scope.removePrinter = function() {
							if ($('.printerName #selectedPrinterName').length !== 0) {

								var jsonData = JSON
										.stringify({
											'printerName' : $(
													'.printerName #selectedPrinterName')
													.val()
										});

								$http
										.post(
												"${pageContext.request.contextPath}/printerapi/remove_printer_config",
												jsonData)
										.then(
												function(response) {
													if (response.status === 200)
														obtainPrinterConfigData();
												},
												function(response) {
													alert("Error Occured While Removing Printer Data");
													$(location)
															.attr('href',
																	'${pageContext.request.contextPath}/ecpos');
												});
							}
						}

						$scope.saveSettingData = function() {

							var jsonData;

							//If dropdown list present (detect it)
							if ($('.printerName').find('#selectedPrinterName')
									.prop("tagName") === 'INPUT') {
								jsonData = JSON
										.stringify({
											'tableCount' : $scope.appSettingData.tableCount,
											'propertyName' : $scope.appSettingData.propertyName,
											'gstPercentage' : $scope.appSettingData.gstPercentage,
											'salesTaxPercentage' : $scope.appSettingData.salesTaxPercentage,
											'serviceTaxPercentage' : $scope.appSettingData.serviceTaxPercentage,
											'otherTaxPercentage' : $scope.appSettingData.otherTaxPercentage
										});
							} else {
								jsonData = JSON
										.stringify({
											'tableCount' : $scope.appSettingData.tableCount,
											'propertyName' : $scope.appSettingData.propertyName,
											'gstPercentage' : $scope.appSettingData.gstPercentage,
											'salesTaxPercentage' : $scope.appSettingData.salesTaxPercentage,
											'serviceTaxPercentage' : $scope.appSettingData.serviceTaxPercentage,
											'otherTaxPercentage' : $scope.appSettingData.otherTaxPercentage,
											'portName' : $scope.printerConfigurationData.PortInfoList[$scope.selectedPort].PortInfo.PortName,
											'printerModel' : $scope.printerConfigurationData.PortInfoList[$scope.selectedPort].ModelName,
											'paperSize' : 1
										});
							}

							// var jsonData = JSON.stringify({
							// 	'tableCount': $scope.appSettingData.tableCount,
							// 	'propertyName': $scope.appSettingData.propertyName,
							// 	'gstPercentage': $scope.appSettingData.gstPercentage,
							// 	'sstPercentage': $scope.appSettingData.sstPercentage,
							// 	'serviceTaxPercentage': $scope.appSettingData.serviceTaxPercentage,
							// 	'otherTaxPercentage': $scope.appSettingData.otherTaxPercentage,
							// 	'portName': $scope.printerConfigurationData.PortInfoList[$scope.selectedPort].PortInfo.PortName,
							// 	'printerModel': $scope.printerConfigurationData.PortInfoList[$scope.selectedPort].ModelName
							// });

							$http
									.post(
											'${pageContext.request.contextPath}/settingapi/savesetting',
											jsonData)
									.then(
											function(response) {
												$scope.appSettingData = {};
												$scope.printerConfigurationData = {};
												$(location)
														.attr('href',
																'${pageContext.request.contextPath}/ecpos/#!sales');
											},
											function(response) {
												alert('Error Occured While Updating Setting Data');
												$(location)
														.attr('href',
																'${pageContext.request.contextPath}/ecpos/#!sales');
											});
						}

						//Success
						$scope.addTerminal = function() {

							if ($scope.terminalConfigurationInputData.terminalName && $scope.terminalConfigurationInputData.ipAddress
									&& $scope.terminalConfigurationInputData.port) {

								if (!checkIPFormat($scope.terminalConfigurationInputData.ipAddress)) {
									alert('Invalid IP Address');
								}
								else {
									
									jsonData = JSON
											.stringify({
												'terminalName' : $scope.terminalConfigurationInputData.terminalName,
												'ipAddress' : $scope.terminalConfigurationInputData.ipAddress,
												'port' : $scope.terminalConfigurationInputData.port
											});

									$http
											.post(
													'${pageContext.request.contextPath}/settingapi/addTerminal',
													jsonData)
											.then(
													function(response) {
														if(response.status === 200){
															console.log('Success Added');
															//manipulate the table
															manipulateTerminalInfoTable();
														}
													},
													function(response) {
														alert('Error Occured While Adding Terminal');
													});
								}
								
								clearAddTerminalInputField();
							}
						}
						
						
				/* 		$scope.pingTerminal = function(ipAddress, portNumber){
							
							var jsonData;
							
							if(ipAddress && portNumber){
								jsonData = JSON
								.stringify({
							 		'tranType':'ping-test',
							 		'wifiIP':ipAddress,
							 		'wifiPort':portNumber
								});
							} else {
								jsonData = JSON
								.stringify({
							 		'tranType':'ping-test'
								});
							}
							
							$http
							.post(
									'${pageContext.request.contextPath}/payment/pingTest',
									jsonData)
							.then(
									function(response) {
										if(response.data.responseCode == "00"){
											alert('Ping Success');
										}
										else {
											alert('Ping Failed');
										}
									},
									function(response) {
											alert('Ping Failed. Special Error.');
									});

						} */
						
						//Success
						$scope.removeTerminalInfo = function(id){
							
							if(id){
								$http
								.delete(
										'${pageContext.request.contextPath}/settingapi/removeTerminalInfo/'+id)
								.then(
										function(response) {
											if(response.status === 200){
												console.log('Terminal Info Successfully Removed.');
												manipulateTerminalInfoTable();
											}
											else {
												alert('Remove Terminal Info Failed.');
											}
										},
										function(response) {
											alert('Remove Terminal Information Failed.');
										});
							}	
						}
						
			/* 			$scope.selectActiveTerminal = function(id){
							$http
							.post(
									'${pageContext.request.contextPath}/settingapi/useActiveTerminal',id)
							.then(
									function(response) {
										if(response.data.response_code === '00'){
											console.log('Success Set Default Terminal');
											$scope.isDefaultTerminal = true;
											manipulateTerminalInfoTable();
										}
									},
									function(response) {
										alert('Error Occured While Setting Active WiFi Terminal');
									});
							
							
						} */
						
						//Success
						function manipulateTerminalInfoTable(){
							
							$http
							.get(
									'${pageContext.request.contextPath}/settingapi/getTerminalInfo')
							.then(
									function(response) {
										if(response.status === 200){
											$scope.terminalConfigurationData = response.data.terminalInfo;
											//console.log($scope.terminalConfigurationData);
										}
									},
									function(response) {
										alert('Error Occured While Retrieving Terminal Information.');
									});	
						}

						//Check valid ip format
						function checkIPFormat(ipAddress) {
							if (/^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/
									.test(ipAddress)) {
								return true;
							} else {
								return false;
							}

						}
						
						//Success
						$scope.setSettlementTerminal = function(id){
							$scope.selectedTerminal = id;
						}
						
						//Todo Not yet tested
						$scope.submitSettlementData = function(){	
							if($scope.settlementType){
							/* 	if($scope.selectedTerminal==="0"){
									jsonData = JSON.stringify({
										'storeId':1,
										'selectedTerminal': $scope.selectedTerminal,
										'tranType':'card-settlement',
										'settlementType':$scope.settlementType
									}); 
									
								} else {
									jsonData = JSON.stringify({
										'storeId':1,
										'selectedTerminal': $scope.selectedTerminal,
										'tranType':'card-settlement',
										'settlementType':$scope.settlementType,
										'isWifi': true,
									}); 
								} */
								
								var jsonData = JSON.stringify({
									'storeId':1,
									'selectedTerminal': $scope.selectedTerminal,
									'tranType':'card-settlement',
									'settlementType':$scope.settlementType
								}); 
											
								$('#settlement_selection_modal').modal('hide');
								$(".modal-backdrop").remove();			
						        $('#loading_modal').modal('show');
							
								$http
								.post(
										'${pageContext.request.contextPath}/payment/settlement',jsonData)
								.then(
										function(response) {
											if(response.data.response_code === '00'){
												console.log("Success Performing Settlement");
												
												$('#loading_modal').modal('hide');
												$(".modal-backdrop").remove();
												resetSettlementSelection();
											}
											else {
												$('#loading_modal').modal('hide');
												$(".modal-backdrop").remove();
												resetSettlementSelection();
												
												alert("Cannot Perform Settlement. Please Try Again Later");
											}
										},
										function(response) {
											$('#loading_modal').modal('hide');
											$(".modal-backdrop").remove();
											resetSettlementSelection();
											
											alert('Error Occured While Performing Settlement');
										});
		
							}
						}
						
						//Success
						$scope.resetSettlementSelection = function(){
							$('input[name="settlement_radio"]').prop('checked', false);
							$scope.selectedTerminal = 0;
						}
						
						//Success
						$scope.editTerminalData = function(terminalConfigData){
							$scope.editTerminalConfigurationInputData.id = terminalConfigData.id;
							$scope.editTerminalConfigurationInputData.terminalName = terminalConfigData.terminalName;
							$scope.editTerminalConfigurationInputData.ipAddress = terminalConfigData.wifiIP;			
							$scope.editTerminalConfigurationInputData.port = parseInt(terminalConfigData.wifiPort);
						}
	
						//Success
						$scope.editTerminalInfo = function(){
							if($scope.editTerminalConfigurationInputData.id 
									&& $scope.editTerminalConfigurationInputData.terminalName 
									&& $scope.editTerminalConfigurationInputData.ipAddress 
									&& $scope.editTerminalConfigurationInputData.port){
								
								var jsonData = JSON.stringify({
									'id': $scope.editTerminalConfigurationInputData.id,
									'ipAddress':$scope.editTerminalConfigurationInputData.ipAddress,
									'port':$scope.editTerminalConfigurationInputData.port,
									'terminalName':	$scope.editTerminalConfigurationInputData.terminalName 
								});
								
								$http
								.post(
										'${pageContext.request.contextPath}/settingapi/editTerminalInfo', jsonData)
								.then(
										function(response) {
											if(response.status === 200){
												$('#edit_terminal_modal').modal('hide');
												$(".modal-backdrop").remove();
												
												$scope.editTerminalConfigurationInputData = {};
												manipulateTerminalInfoTable();
											}
										},
										function(response) {
											alert('Error Occured While Editing Terminal Information.');
										});	
							}
						}
						
						function clearAddTerminalInputField(){
							$("#terminal_name_input").val("");
							$("#terminal_ip_input").val("");
							$("#terminal_port_input").val("");
						}
						
					});
</script>