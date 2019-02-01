<script>
	app.controller('payment_CTRL', function($scope, $http, $timeout, $location, $route, $routeParams) {
		$scope.paymentType = "";
		$scope.paymentMethod = "";
		$scope.fullPaymentAmount = "";
		$scope.terminalSerialNo = "";
		
		var counter = 0;
		
		$scope.proceedPaymentMethod = function(type) {
			$scope.paymentType = type;
			
			$('#paymentCarousel').carousel(1);
			
			counter += 1;
			if (counter == 1) {
				$scope.fullPaymentAmount = $('#amount').text();
			}
			
			if ($scope.paymentType == "full") {
				$("#allGrandParentItemCheckbox").show();
				$("input[name=grandParentItemCheckbox]").show();
				
				$('#amount').html(parseFloat($scope.fullPaymentAmount).toFixed(2));
			} else if ($scope.paymentType == "split") {
				$("#allGrandParentItemCheckbox").show();
				$("input[name=grandParentItemCheckbox]").show();
				
				$('#amount').html(parseFloat(0).toFixed(2));
			} else {
				$('#amount').html(parseFloat(0).toFixed(2));
			}
		}
		
		$scope.proceedPayment = function(method) {
			$scope.paymentMethod = method;
			
			$('#paymentMethodName').html($scope.paymentMethod + " Payment");
			$('#paymentCarousel').carousel(2);
			
			if ($scope.paymentMethod == "Card") {
				$('#terminalList').show();
				$('#terminal').val("");
				
				$http.get("${pageContext.request.contextPath}/rc/configuration/get_terminal_list/" + "all")
				.then(function(response) {
					$scope.terminalList = response.data;
				},
				function(response) {
					alert("Session TIME OUT");
					window.location.href = "${pageContext.request.contextPath}/ecpos";
				});
			} else {
				$('#terminalList').hide();
			}
		}
		
		$scope.enterCalculator = function(id, number) {
			var amount = document.getElementById(id).innerHTML;

			if (amount > 999999.99) {
				//do nothing
			} else if (amount < 0.00) {
				alert("Amount cannot be less than 0.00");
			} else {
				if (number != -10 && number != 100) {
					amount = amount + number;
				}

				var floatAmount = parseFloat(amount);
				if (number == -10) {
					floatAmount = floatAmount / 10;
				} else {
					if (amount.length < 10) {
						if (number == 100) {
							floatAmount = floatAmount * 100;
						} else {
							floatAmount = floatAmount * 10;
						}
					}
				}

				var temp = floatAmount.toFixed(3);
				temp = temp.substring(0, temp.length - 1);
				document.getElementById(id).innerHTML = temp;
			}
		}
		
		$scope.submitPayment = function() {
			$scope.checkedValue = [];
			
			if ($scope.paymentType == "split") {				
				$("input[name=grandParentItemCheckbox]:checked").each(function(){
					$scope.checkedValue.push($(this).val());
				});
				
				if ($scope.checkedValue === undefined || $scope.checkedValue == 0) {
					return alert("Kindly tick at least an item to proceed");
				}
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

			$http.post("${pageContext.request.contextPath}/rc/transaction/submit_payment", jsonData)
			.then(function(response) {
				if (response.data.response_code === "00") {
					alert(response.data.response_message);
					window.location.href = "${pageContext.request.contextPath}/ecpos";
				} else {
					alert(response.data.response_message);
				}
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/ecpos";
			});
		}
	});
</script>