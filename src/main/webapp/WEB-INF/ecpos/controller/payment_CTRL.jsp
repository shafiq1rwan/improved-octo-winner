<script>
	app.controller('payment_CTRL',function($scope, $http, $timeout, $location, $route, $routeParams, $sce) {
		$scope.paymentType = "";
		$scope.paymentMethod = "";
		$scope.terminalSerialNo = "";
		$scope.qrContent = "";
		$scope.isQRExecuted = false;
		$scope.socketMessage = "";
		$scope.jsonResult;
		
		$scope.selectedTerminal;
		$scope.alertMessage = "";
		$scope.paymentButtonFn;
	
		$scope.isCashAlert = false;
	
		var counter = 0;
		
		$scope.proceedPaymentMethod = function(type) {
			$scope.paymentType = type;
	
			$('#paymentCarousel').carousel(1);

			if ($scope.paymentType == "full") {
				$('#tenderAmount').html(parseFloat($scope.fullPaymentAmount).toFixed(2));
	
				$("#one").prop('disabled', true);
				$("#two").prop('disabled', true);
				$("#three").prop('disabled', true);
				$("#four").prop('disabled', true);
				$("#five").prop('disabled', true);
				$("#six").prop('disabled', true);
				$("#seven").prop('disabled', true);
				$("#eight").prop('disabled', true);
				$("#nine").prop('disabled', true);
				$("#zero").prop('disabled', true);
				$("#zerozero").prop('disabled', true);
				$("#remove").prop('disabled', true);
			} else {
				$('#tenderAmount').html(parseFloat(0).toFixed(2));
	
				$("#one").prop('disabled', false);
				$("#two").prop('disabled', false);
				$("#three").prop('disabled', false);
				$("#four").prop('disabled', false);
				$("#five").prop('disabled', false);
				$("#six").prop('disabled', false);
				$("#seven").prop('disabled', false);
				$("#eight").prop('disabled', false);
				$("#nine").prop('disabled', false);
				$("#zero").prop('disabled', false);
				$("#zerozero").prop('disabled', false);
				$("#remove").prop('disabled', false);
			}
		}
		
		$('#backToPaymentMethod').click(function() {
			if ($scope.paymentType != "full") {
				$('#tenderAmount').html(parseFloat(0).toFixed(2));
			}
		})
	
		$scope.proceedPayment = function(method) {
			$scope.paymentMethod = method;
	
			$('#paymentMethodName').html($scope.paymentMethod.toUpperCase() + " PAYMENT");
			$('#paymentCarousel').carousel(2);

			$http.get("${pageContext.request.contextPath}/rc/configuration/get_terminal_list/" + "all")
			.then(function(response) {
				$scope.terminalList = response.data;

				$scope.isIposQR = false;
				if($scope.terminalList.hasOwnProperty("selectedQRPayment")){
					$scope.isIposQR = response.data.selectedQRPayment == '1' && $scope.paymentMethod == "QR";
				}
				
				if ($scope.paymentMethod == "Card" || $scope.isIposQR) {
					$('#terminalList').show();
					$('#terminal').val("");
					if($scope.terminalList.terminals.length == 0){
						$('#terminal').val("");
					} 
					else {
						$scope.selectedTerminal = $scope.terminalList.terminals[0].serialNo;
					}
					
				} else {
					$('#terminalList').hide();
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
	
		$scope.enterCalculator = function(id, number) {
			var amount = document.getElementById(id).innerHTML;
			
			if (amount < 0.00) {
				/* alert("Tender Amount should not be less than 0.00"); */
				Swal.fire("Warning","Tender Amount should not be less than 0.00","warning");
			} else if (number != -10) {
				if (amount.length < 12) {
					if (number != 100 && number != 10) {
						amount = amount + number;
					}
					
					var floatAmount = parseFloat(amount);
					
					if (number == 100) {
						floatAmount = floatAmount * 100;
					} else {
						floatAmount = floatAmount * 10;
					}
					
					var temp = floatAmount.toFixed(3);
					temp = temp.substring(0, temp.length - 1);
					document.getElementById(id).innerHTML = temp;
				}
			} else if (number == -10) {
				var floatAmount = parseFloat(amount);
				
				floatAmount = floatAmount / 10;
				
				var temp = floatAmount.toFixed(3);
				temp = temp.substring(0, temp.length - 1);
				document.getElementById(id).innerHTML = temp;
			}
		}
		
		$scope.enterCalculator2 = function(id, number) {
			var amount = document.getElementById(id).innerHTML;
			
			if (amount < 0.00) {
				/* alert("Amount should not be less than 0.00"); */
				Swal.fire("Warning","Amount should not be less than 0.00","warning");
			} else if (number != -10) {
				if (amount.length < 12) {
					if (number != 100 && number != 10) {
						amount = amount + number;
					}
					
					var floatAmount = parseFloat(amount);
					
					if (number == 100) {
						floatAmount = floatAmount * 100;
					} else {
						floatAmount = floatAmount * 10;
					}
					
					var temp = floatAmount.toFixed(3);
					temp = temp.substring(0, temp.length - 1);
					document.getElementById(id).innerHTML = temp;
				}
			} else if (number == -10) {
				var floatAmount = parseFloat(amount);
				
				floatAmount = floatAmount / 10;
				
				var temp = floatAmount.toFixed(3);
				temp = temp.substring(0, temp.length - 1);
				document.getElementById(id).innerHTML = temp;
			}
		}

		$scope.submitPayment = function() {
			if (($scope.paymentMethod == "Card") && $('#terminal').val() == "") {
				/* return alert("Kindly select terminal"); */
				return Swal.fire("Warning","Kindly select terminal","warning");
			} else if ($('#tenderAmount').text() == "0.00") {
				/* return alert("Kindly enter payment amount"); */
				return Swal.fire("Warning","Kindly enter payment amount","warning");
			} else {
				if ($scope.paymentMethod == "Cash") {
					$('#receivedAmount').html(parseFloat(0).toFixed(2)); 
					$('#receivedAmountModal').modal('show');
				} else if ($scope.paymentMethod == "Card") {
					$scope.executePayment();
				} else if ($scope.paymentMethod == "QR") {
					//$scope.qrContent = "";
					$('#scan_qr_modal').modal('show');
	
					$('#scan_qr_modal').on('shown.bs.modal',function() {
						$('#qr_content').focus();
					})
				} else if($scope.paymentMethod == "Static QR"){
					$('#receivedAmount').html(parseFloat($('#tenderAmount').text()).toFixed(2)); 
					// $('#tenderAmount').html(parseFloat($scope.fullPaymentAmount).toFixed(2)); 
					$('#receivedAmountModal').modal('show');
				}
			}
		};
		
		$scope.executePayment = function() {
			if($scope.paymentMethod == "Cash") {
				if (parseFloat(document.getElementById('tenderAmount').innerHTML) > parseFloat(document.getElementById('receivedAmount').innerHTML)) {
					/* return alert("Received amount should be greater than or equal to Tender Amount"); */
					return Swal.fire("Warning","Received amount should be greater than or equal to Tender Amount","warning");
				} else {
					var jsonData = JSON.stringify({
						"paymentType" : $scope.paymentType,
						"paymentMethod" : $scope.paymentMethod,
						"paymentAmount" : $('#tenderAmount').text(),
						"tableNo" : $scope.tableNo,
						"checkNo" : $scope.checkNo,
						"receivedAmount" : $('#receivedAmount').text()
					});
					console.log("Send data: " + jsonData)

					$http.post("${pageContext.request.contextPath}/rc/transaction/submit_payment", jsonData)
					.then(function(response) {
						if (response.data.response_code === "00") {
							$scope.alertMessage = $sce.trustAsHtml(response.data.response_message + " " + "Change: RM" + parseFloat(response.data.change_amount).toFixed(2));
							$scope.isCashAlert = response.data.is_cash_alert;
							if ($scope.isCashAlert) {
								$scope.alertMessage = $sce.trustAsHtml($scope.alertMessage + "<br>" + "Cash amount exceed. Please perform cash collection.");
							}
							
							var jsonData2ndDisplay = JSON.stringify({
								"deviceType" : 1,
								"orderType" : $scope.orderType,
								"tableNo" : $scope.tableNo,
								"checkNo" : $scope.checkNo,
							});
							$scope.informSecondDisplay(jsonData2ndDisplay);
							
							$scope.paymentButtonFn = function() {
								$('#receivedAmountModal').modal('hide');
								$('.modal-backdrop').remove();
								$('#receivedAmount').html(parseFloat(0).toFixed(2)); 
								
								$('#paymentAlertModal').modal('hide'); 
								$('.modal-backdrop').remove();
								
								if ($scope.orderType == "table") {
									if ($scope.paymentType == "full" || response.data.check_status == "closed") {
										$location.path("/table_order");
									} else {
										location.reload();
									}
								} else if ($scope.orderType == "take_away") {
									if ($scope.paymentType == "full" || response.data.check_status == "closed") {
										$location.path("/take_away_order");
									} else {
										location.reload();
									}
								} else if ($scope.orderType == "deposit") {
									if ($scope.paymentType == "full" || response.data.check_status == "closed") {
										$location.path("/deposit_order");
									} else {
										location.reload();
									}
								}
							}
							$('#paymentAlertModal').modal('show'); 
							
							openDrawer();
							
							//Print Receipt here
							printReceipt($scope.checkNo);

						} else {
							/* alert(response.data.response_message); */
							Swal.fire("Warning",response.data.response_message,"warning");
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
			} else if($scope.paymentMethod == "Card") {	
				var jsonData = JSON.stringify({
					"terminalSerialNo" : $('#terminal').val(),
					"paymentType" : $scope.paymentType,
					"paymentMethod" : $scope.paymentMethod,
					"paymentAmount" : $('#tenderAmount').text(),
					"tableNo" : $scope.tableNo,
					"checkNo" : $scope.checkNo
				});
				console.log("Send data: " + jsonData)

				var wsProtocol = window.location.protocol;
				var wsHost = window.location.host;
				var wsURLHeader = "";
			
				if (wsProtocol.includes("https")) {
					wsURLHeader = "wss://"
				} else {
					wsURLHeader = "ws://"
				}
				wsURLHeader += wsHost;
					
				var paymentSocket = new WebSocket(wsURLHeader + "${pageContext.request.contextPath}/paymentSocket");
				
				paymentSocket.onopen = function(event) {
					console.log("Connection established");
					if (paymentSocket != null) {
						paymentSocket.send(jsonData);
					}
					
					$scope.socketMessage = "Perform Transaction. Please Wait.";
					$('#loading_modal').modal('show');
				}
				
				paymentSocket.onmessage = function(event) {
					console.log("onMessage :" + event.data);
					if(event.data.includes("IPOS")){
						switch (getPaymentSocketMessage(event.data)) {
						case "insertCard":
							$scope.socketMessage = "Please present card.";
							$scope.$apply();
							break;
						case "contactingBank":
							$scope.socketMessage = "Contacting Bank. Please wait.";
							$scope.$apply();
							break;
						case "enterPin":
							$scope.socketMessage = "Waiting for PIN input.";
							$scope.$apply();
							break;
						case "finalResult":
							$scope.socketMessage = getIposResponseMessage(event.data);
							$scope.$apply();
							break;
						}
						console.log("Received message " + $scope.socketMessage)
					} else {
						var jsonResult = JSON.parse(event.data);
						$scope.jsonResult = jsonResult;
						console.log($scope.jsonResult);
						$scope.alertMessage = $sce.trustAsHtml(jsonResult.response_message);
						$('#paymentAlertModal').modal('show');
						printReceipt($scope.checkNo)
					}
				}

				paymentSocket.onerror = function(event) {
					console.error("WebSocket error observed:", event);
					$('#loading_modal').modal('hide');
					/* alert(event); */
					Swal.fire("Warning",event,"warning");
					$scope.socketMessage = "";
				}
						
				paymentSocket.onclose = function(event) {
					console.log($scope.jsonResult);
					console.log("Connection closed");
					$('#loading_modal').modal('hide');
					$scope.socketMessage = "";
					
					$scope.paymentButtonFn = function() {
						$('#paymentAlertModal').modal('hide');
						$('.modal-backdrop').remove();
						
						if ($scope.jsonResult.response_code == "01") {
							location.reload();
						} else {
							if ($scope.orderType == "table") {
								if ($scope.paymentType == "full" || response.data.check_status == "closed") {
									$location.path("/table_order");
								} else {
									location.reload();
								}
							} else if ($scope.orderType == "take_away") {
								if ($scope.paymentType == "full" || response.data.check_status == "closed") {
									$location.path("/take_away_order");
								} else {
									location.reload();
								}
							} else if ($scope.orderType == "deposit") {
								if ($scope.paymentType == "full" || response.data.check_status == "closed") {
									$location.path("/deposit_order");
								} else {
									location.reload();
								}
							}
						}
					}
				}
			} else if($scope.paymentMethod == "QR") {
				//$scope.qrContent = $('#qr_content').val();
				
				if($scope.qrContent === null || $scope.qrContent === ""){
					$('#scan_qr_modal').modal('hide');
					/* return alert("The QR content is empty."); */
					return Swal.fire("Warning","The QR content is empty","warning");
				} else {
					$('#scan_qr_modal').modal('hide');
					var qrContentHolder = $scope.qrContent;
					$scope.qrContent = "";
					
					var jsonData = JSON.stringify({
						"terminalSerialNo" : $('#terminal').val(),
						"paymentType" : $scope.paymentType,
						"paymentMethod" : $scope.paymentMethod,
						"paymentAmount" : $('#tenderAmount').text(),
						"tableNo" : $scope.tableNo,
						"checkNo" : $scope.checkNo,
						"qrContent" : qrContentHolder,
						"isIposQR" : $scope.isIposQR
					});
					
					console.log("QR payment: " + qrContentHolder)
		
					$('#loading_modal').modal('show');
					$scope.socketMessage = "Contacting Bank. Please wait.";
					$http.post("${pageContext.request.contextPath}/rc/transaction/submit_payment",jsonData)
					.then(function(response) {
						if (response.data.response_code === "00") {
							$('#loading_modal').modal('hide');
					
							$scope.alertMessage = $sce.trustAsHtml(response.data.response_message);

							$('#paymentAlertModal').modal('show'); 
							
							//Print Receipt here
							printReceipt($scope.checkNo);
							
							$scope.paymentButtonFn = function() {
								$('#paymentAlertModal').modal('hide'); 
								$('.modal-backdrop').remove();
								
						 		if ($scope.orderType == "table") {
									if ($scope.paymentType == "full" || response.data.check_status == "closed") {
										$location.path("/table_order");
									} else {
										location.reload();
									}
								} else if ($scope.orderType == "take_away") {
									if ($scope.paymentType == "full" || response.data.check_status == "closed") {
										$location.path("/take_away_order");
									} else {
										location.reload();
									}
								} else if ($scope.orderType == "deposit") {
									if ($scope.paymentType == "full" || response.data.check_status == "closed") {
										$location.path("/deposit_order");
									} else {
										location.reload();
									}
								} 
							}

						} else {
							$('#loading_modal').modal('hide');
							/* alert(response.data.response_message); */
							Swal.fire("Warning",response.data.response_message,"warning");
						}
					},
					function(response) {
						$('#loading_modal').modal('hide');
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
			} else if($scope.paymentMethod == "Static QR") {
				if (parseFloat(document.getElementById('tenderAmount').innerHTML) > parseFloat(document.getElementById('receivedAmount').innerHTML)) {
					/* return alert("received amount should be greater than or equal to tender amount"); */
					return Swal.fire("Warning","Received amount should be greater than or equal to Tender Amount","warning");
				} else {
					var jsonData = JSON.stringify({
						"paymentType" : $scope.paymentType,
						"paymentMethod" : $scope.paymentMethod,
						"paymentAmount" : $('#tenderAmount').text(),
						"tableNo" : $scope.tableNo,
						"checkNo" : $scope.checkNo,
						"receivedAmount" : $('#receivedAmount').text()
					});
					console.log("Send data: " + jsonData)

					$http.post("${pageContext.request.contextPath}/rc/transaction/submit_payment", jsonData)
					.then(function(response) {
						if (response.data.response_code === "00") {
							$scope.alertMessage = $sce.trustAsHtml(response.data.response_message + " " + "Change: RM" + parseFloat(response.data.change_amount).toFixed(2));
							$scope.isCashAlert = response.data.is_cash_alert;
							if ($scope.isCashAlert) {
								$scope.alertMessage = $sce.trustAsHtml($scope.alertMessage + "<br>" + "Cash amount exceed. Please perform cash collection.");
							}
							
							var jsonData2ndDisplay = JSON.stringify({
								"deviceType" : 1,
								"orderType" : $scope.orderType,
								"tableNo" : $scope.tableNo,
								"checkNo" : $scope.checkNo,
							});
							$scope.informSecondDisplay(jsonData2ndDisplay);
							
							$scope.paymentButtonFn = function() {
								$('#receivedAmountModal').modal('hide');
								$('.modal-backdrop').remove();
								$('#receivedAmount').html(parseFloat(0).toFixed(2)); 
								
								$('#paymentAlertModal').modal('hide'); 
								$('.modal-backdrop').remove();
								
								if ($scope.orderType == "table") {
									if ($scope.paymentType == "full" || response.data.check_status == "closed") {
										$location.path("/table_order");
									} else {
										location.reload();
									}
								} else if ($scope.orderType == "take_away") {
									if ($scope.paymentType == "full" || response.data.check_status == "closed") {
										$location.path("/take_away_order");
									} else {
										location.reload();
									}
								} else if ($scope.orderType == "deposit") {
									if ($scope.paymentType == "full" || response.data.check_status == "closed") {
										$location.path("/deposit_order");
									} else {
										location.reload();
									}
								}
							}
							$('#paymentAlertModal').modal('show'); 
							
							// openDrawer();
							
							//Print Receipt here
							printReceipt($scope.checkNo);

						} else {
							/* alert(response.data.response_message); */
							Swal.fire("Warning",response.data.response_message,"warning");
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
			}
		}
	
		function getPaymentSocketMessage(responseData) {
			if (responseData.includes("[IPOS-CONTACT-BANK]"))
				return "contactingBank";
			else if (responseData.includes("[IPOS-CARD-ENTER-PIN]"))
				return "enterPin";
			else if (responseData.includes("[IPOS-INSERT-CARD]"))
				return "insertCard";
			else if (responseData.includes("[IPOS-RESPONSE]"))
				return "finalResult";
		}
	
		function getIposResponseMessage(iposResponse) {
			console.log("IPOS final Message")
			var jsonMessage = iposResponse.replace("[IPOS-RESPONSE]", '');
			console.log(jsonMessage)
			var responseJSON = JSON.parse(jsonMessage);
	
			return responseJSON['responseCode'] === '00' ? 'Transaction Approved' : responseJSON['responseMessage'];
		}
		//Used upon success payment
		function printReceipt(checkNo) {
			var jsonData = JSON.stringify({
				"checkNo" : checkNo
			});
			console.log("enter printReceipt");
			$http.post("${pageContext.request.contextPath}/rc/configuration/print_receipt",jsonData)
			.then(function(response) {
				if (response.data.response_code === "00") {
					console.log("Success Printable");
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
		
		function openDrawer() {
			console.log("enter openDrawer");
			$.ajax({
				type : 'post',
				url : '${pageContext.request.contextPath}/rc/configuration/open_cash_drawer',
				success : function(data) {
					if (data.response_code == 01) {
						/* alert(data.response_message); */
						Swal.fire("Warning",data.response_message,"warning");
					}
				},
				error : function(jqXHR) {
					if (jqXHR.status == 408) {
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

					} else {
						/* alert('Drawer cannot open. Please kindly check the cash drawer printer.'); */
						Swal.fire("Warning","Drawer cannot open. Please kindly check the cash drawer printer.","warning");
					}
				}
			});
		}

		$('#scan_qr_modal').on('hidden.bs.modal', function() {
			$scope.qrContent = $('#qr_content').val();
			$(document).off("keydown");
		});

		$('#scan_qr_modal').on('shown.bs.modal', function (e) {
			var lengthcount = 0;
			$scope.isQRExecuted = false;
			$scope.qrContent = "";
			$(document).off("keydown");
			$(document).keydown(function(e){
				lengthcount++;
				
				if (!$scope.isQRExecuted) {
					if (e.which == 16) {
						return;
					} else if (e.which == 13 &&lengthcount<124){
						$scope.isQRExecuted = true;
						console.log("Fire event");
						 console.log("lengthcount fire "+lengthcount); 
						/* $scope.qrContent = $scope.qrContent.replace(/»/g, "+"); */
						$scope.executePayment();
						$(document).off("keydown");
					} else if (e.which == 191) {
						$scope.qrContent += "/";
					} else if (e.which == 186) {
						$scope.qrContent += ";";
					} else if (e.which == 107) {
						$scope.qrContent += "+";
					} else if (e.shiftKey && e.which == 187) {
						$scope.qrContent += "+";
					} else if (e.which == 187) {
						$scope.qrContent += "=";
					} else {
					  if (e.which == 40) {
						e.keyCode = 13;
					  }
						if (e.shiftKey) {
							$scope.qrContent += String.fromCharCode(e.keyCode || e.which).toUpperCase();
						} else {
							$scope.qrContent += String.fromCharCode(e.keyCode || e.which).toLowerCase();
						}
					}
				}
				 console.log("lengthcount "+lengthcount);
				console.log("e char  "+e.which);
				console.log("$scope.qrContent "+$scope.qrContent); 
				if (lengthcount>=125)
					lengthcount = 0;
			});
			
		});
		
		$scope.informSecondDisplay = function (jsonData) {

			var wsProtocol = window.location.protocol;
			var wsHost = window.location.host;
			var wsURLHeader = "";

			if (wsProtocol.includes("https")) {
				wsURLHeader = "wss://"
			} else {
				wsURLHeader = "ws://"
			}
			wsURLHeader += wsHost + "${pageContext.request.contextPath}/secondDisplaySocket";
				
			var kdsSocket = new WebSocket(wsURLHeader);
			/* console.log("Send to : " + wsURLHeader) */
			kdsSocket.onopen = function(event) {
				console.log("Connection established");
				if (kdsSocket != null) {
					kdsSocket.send(jsonData);
				}
			}
			
			kdsSocket.onmessage = function(event) {
				console.log("onMessage :" + event.data);
			}

			kdsSocket.onerror = function(event) {
				console.error("WebSocket error observed:", event);
				Swal.fire("Error",event,"error");
			}
					
			kdsSocket.onclose = function(event) {
				console.log($scope.jsonResult);
				console.log("Connection closed");
			}	
		}
	});
</script>