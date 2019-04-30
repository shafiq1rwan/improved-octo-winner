<%@ page import="mpay.ecpos_manager.general.utility.UserAuthenticationModel"%>

<%
	UserAuthenticationModel user = (UserAuthenticationModel) session.getAttribute("session_user");
%>

<script>
	app.controller('payment_CTRL',function($scope, $http, $timeout, $location, $route,$routeParams) {
		$scope.paymentType = "";
		$scope.paymentMethod = "";
		$scope.fullPaymentAmount = "";
		$scope.terminalSerialNo = "";
		$scope.qrContent = "";
		$scope.socketMessage = "";
		$scope.jsonResult;
	
		var counter = 0;
		
		$('#fullPayment').prop('disabled', false);
		$('#partialPayment').prop('disabled', false);
		$('#splitPayment').prop('disabled', false);
		$('#depositPayment').prop('disabled', false);
		
		$scope.paymentInitiation = function() {
			var jsonData = JSON.stringify({
				"tableNo" : $scope.tableNo,
				"checkNo" : $scope.checkNo
			});
			console.log(jsonData)

			$http.post("${pageContext.request.contextPath}/rc/transaction/get_previous_payment", jsonData)
			.then(function(response) {
				if (response.data.data == "0") {
					$('#fullPayment').prop('disabled', false);
					$('#partialPayment').prop('disabled', false);
					$('#splitPayment').prop('disabled', false);
					$('#depositPayment').prop('disabled', false);
				} else if (response.data.data.includes("3")) {
					$('#fullPayment').prop('disabled', true);
					$('#partialPayment').prop('disabled', true);
					$('#splitPayment').prop('disabled', false);
					$('#depositPayment').prop('disabled', true);
				} else {
					$('#fullPayment').prop('disabled', false);
					$('#partialPayment').prop('disabled', false);
					$('#splitPayment').prop('disabled', true);
					$('#depositPayment').prop('disabled', false);
				}
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/signout";
			});
		}
		
		$scope.proceedPaymentMethod = function(type) {
			$scope.paymentType = type;
	
			$('#paymentCarousel').carousel(1);
	
			counter += 1;
			if (counter == 1) {
				$scope.fullPaymentAmount = $('#amount').text();
			}
			
			if ($scope.paymentType == "full") {
				$('#amount').html(parseFloat($scope.fullPaymentAmount).toFixed(2));
	
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
			} else if ($scope.paymentType == "split") {
				$("input[name=grandParentItemCheckbox]:checked").each(function(){
					$(this).prop('checked', false);
				});
				
				$("#allGrandParentItemCheckbox").show();
				$("input[name=grandParentItemCheckbox]").show();
	
				$('#amount').html(parseFloat(0).toFixed(2));
	
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
				$('#amount').html(parseFloat(0).toFixed(2));
	
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
	
		$('#paymentMethodBack').click(function() {
			$("#allGrandParentItemCheckbox").hide();
			$("input[name=grandParentItemCheckbox]").hide();
		})
	
		$scope.proceedPayment = function(method) {
			$scope.paymentMethod = method;
	
			$('#paymentMethodName').html($scope.paymentMethod + " Payment");
			$('#paymentCarousel').carousel(2);
	
			if ($scope.paymentMethod == "Card" || $scope.paymentMethod == "QR") {
				$('#terminalList').show();
				$('#terminal').val("");
	
				$http.get("${pageContext.request.contextPath}/rc/configuration/get_terminal_list/" + "all")
				.then(function(response) {
					$scope.terminalList = response.data;
				},
				function(response) {
					alert("Session TIME OUT");
					window.location.href = "${pageContext.request.contextPath}/signout";
				});
			} else {
				$('#terminalList').hide();
			}
		}
	
		$scope.enterCalculator = function(id, number) {
			var amount = document.getElementById(id).innerHTML;
	
			if (amount < 0.00) {
				alert("Amount should not be less than 0.00");
			} else if (number != -10) {
				if (amount.length < 12) {
					amount = amount + number;
					
					var floatAmount = parseFloat(amount);
					
					if (number == 00) {
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
			if ($scope.paymentMethod == "QR") {
				if ($('#terminal').val() == "") {
					return alert("Kindly select terminal for QR payment");
				} else {
					$('#scan_qr_modal').modal('toggle');
	
					$('#scan_qr_modal').on('shown.bs.modal',function() {
						$('#qr_content').focus();
					})
				}
			} else {
				$scope.checkedValue = [];
	
				if ($scope.paymentType == "split") {
					$("input[name=grandParentItemCheckbox]:checked").each(function() {
						$scope.checkedValue.push($(this).val());
					});
	
					if ($scope.checkedValue === undefined || $scope.checkedValue == 0) {
						return alert("Kindly tick at least an item to proceed");
					}
				} else if ($scope.paymentMethod == "Card" && $('#terminal').val() == "") {
					return alert("Kindly select terminal");
				} else if ($('#amount').text() == "0.00") {
					return alert("Kindly enter payment amount");
				}
	
				var jsonData = JSON.stringify({
					"terminalSerialNo" : $('#terminal').val(),
					"checkDetailIdArray" : $scope.checkedValue,
					"paymentType" : $scope.paymentType,
					"paymentMethod" : $scope.paymentMethod,
					"paymentAmount" : $('#amount').text(),
					"tableNo" : $scope.tableNo,
					"checkNo" : $scope.checkNo
				});
				console.log("Send data: " + jsonData)
				
				if($scope.paymentMethod == "Cash"){
					$http.post("${pageContext.request.contextPath}/rc/transaction/submit_payment", jsonData)
					.then(function(response) {
						if (response.data.response_code === "00") {
							alert(response.data.response_message);
							
							//Print Receipt here
							printReceipt($scope.checkNo)

							<%if (user.getStoreType() == 2) {%>
							if ($scope.orderType == "table") {
								if ($scope.paymentType == "full") {
									$location.path("/table_order");
								} else {
									location.reload();
								}
							} else if ($scope.orderType == "take_away") {
								if ($scope.paymentType == "full") {
									$location.path("/take_away_order");
								} else {
									location.reload();
								}
							}
							<%} else {%>
							if ($scope.paymentType == "full") {
								$location.path("/take_away_order");
							} else {
								location.reload();
							}
							<%}%>
						} else {
							alert(response.data.response_message);
						}
					},
					function(response) {
						alert("Session TIME OUT");
						window.location.href = "${pageContext.request.contextPath}/signout";
					}); 
				} else if($scope.paymentMethod == "Card"){
					
					var context = "${pageContext.request.contextPath}";
					var wsURL = "";
					if (context == "") {
						wsURL = "ws://localhost:8080/paymentSocket";
					} else {
						wsURL = "ws://localhost:8080/${pageContext.request.contextPath}/paymentSocket";
					}
					
					var paymentSocket = new WebSocket("ws://localhost:8080${pageContext.request.contextPath}/paymentSocket");
					
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
							alert(jsonResult.response_message);
						}
					}

					paymentSocket.onerror = function(event) {
						console.error("WebSocket error observed:", event);
						$('#loading_modal').modal('hide');
						alert(event);
						$scope.socketMessage = "";
					}
							
					paymentSocket.onclose = function(event) {
						console.log($scope.jsonResult);
						console.log("Connection closed");
						$('#loading_modal').modal('hide');
						$scope.socketMessage = "";
						
						if ($scope.jsonResult.response_code == "01") {
							location.reload();
						} else {
							<%if (user.getStoreType() == 2) {%>
							if ($scope.orderType == "table") {
								if ($scope.paymentType == "full") {
									$location.path("/table_order");
								} else {
									location.reload();
								}
							} else if ($scope.orderType == "take_away") {
								if ($scope.paymentType == "full") {
									$location.path("/take_away_order");
								} else {
									location.reload();
								}
							}
							<%} else {%>
							if ($scope.paymentType == "full") {
								$location.path("/take_away_order");
							} else {
								location.reload();
							}
							<%}%>
						}
					};
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
	
		$scope.proceedToQRPayment = function() {
			if ($scope.qrContent === null || $scope.qrContent === "") {
				$('#scan_qr_modal').modal('toggle');
				return alert("The QR content is empty.");
			} else {
				$('#scan_qr_modal').modal('toggle');
				var qrContentHolder = $scope.qrContent;
				$scope.qrContent = "";
	
				$scope.checkedValue = [];
				if ($scope.paymentType == "split") {
					$("input[name=grandParentItemCheckbox]:checked").each(function() {
						$scope.checkedValue.push($(this).val());
					});
	
					if ($scope.checkedValue === undefined || $scope.checkedValue == 0) {
						return alert("Kindly tick at least an item to proceed");
					}
				} else if ($('#amount').text() == "0.00") {
					return alert("Kindly enter payment amount");
				}
	
				var jsonData = JSON.stringify({
					"terminalSerialNo" : $('#terminal').val(),
					"checkDetailIdArray" : $scope.checkedValue,
					"paymentType" : $scope.paymentType,
					"paymentMethod" : $scope.paymentMethod,
					"paymentAmount" : $('#amount').text(),
					"tableNo" : $scope.tableNo,
					"checkNo" : $scope.checkNo,
					"qrContent" : qrContentHolder
				});
				console.log(jsonData)
	
				$('#loading_modal').modal('show');
	
				$http.post("${pageContext.request.contextPath}/rc/transaction/submit_payment",jsonData)
				.then(function(response) {
					if (response.data.response_code === "00") {
						$('#loading_modal').modal('hide');
						console.log("success")
						alert(response.data.response_message);
						
						//Print Receipt here
						printReceipt($scope.checkNo)
						
						<%if (user.getStoreType() == 2) {%>
						if ($scope.orderType == "table") {
							if ($scope.paymentType == "full") {
								$location.path("/table_order");
							} else {
								location.reload();
							}
						} else if ($scope.orderType == "take_away") {
							if ($scope.paymentType == "full") {
								$location.path("/take_away_order");
							} else {
								location.reload();
							}
						}
						<%} else {%>
						if ($scope.paymentType == "full") {
							$location.path("/take_away_order");
						} else {
							location.reload();
						}
						<%}%>
					} else {
						$('#loading_modal').modal('hide');
						console.log("failed")
						alert(response.data.response_message);
					}
				},
				function(response) {
					$('#loading_modal').modal('hide');
					alert("Session TIME OUT");
					window.location.href = "${pageContext.request.contextPath}/signout";
				});
			}
		}
	
		$('#scan_qr_modal').on('hidden.bs.modal', function() {
			$scope.qrContent = "";
		});
		
		//Used upon success payment
		function printReceipt(checkNo) {
			var jsonData = JSON.stringify({
				"checkNo" : checkNo
			});

			$http.post("${pageContext.request.contextPath}/rc/configuration/print_receipt",jsonData)
			.then(function(response) {
				if (response.data.response_code === "00") {
					console.log("Success Printable");
				}
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/signout";
			});
		}

		
	});
</script>