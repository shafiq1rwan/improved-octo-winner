<script>
	app.controller('check_CTRL', function($scope, $http, $routeParams, $window, $location, $route) {
		$scope.orderType = $routeParams.orderType;
		$scope.checkNo = $routeParams.checkNo;
		$scope.tableNo = $routeParams.tableNo;
		
		$scope.checkDetail = {};
		
		$('#menuWell').show();
		$('#paymentWell').hide();
		$('#generateQRButton').show();
		$('#barcodeOrderButton').show();
		$('#checkActionButtons').show();
		$('#cancelItemButton').prop('disabled', true);
		$('#paymentButton').prop('disabled', true);
		$('#allGrandParentItemCheckbox').show();
		$('input[name=grandParentItemCheckbox]').show();
		$('#terminalList').hide();

		$scope.initiation = function() {
			$scope.getCheckDetails();
		}
		
		$scope.getCheckDetails = function() {
			$http.get("${pageContext.request.contextPath}/rc/check/get_check_detail/" + $scope.orderType + "/" + $scope.checkNo + "/" + $scope.tableNo)
			.then(function(response) {
				$scope.checkDetail = response.data;
				
				if ($scope.checkDetail.grandParentItemArray === undefined || $scope.checkDetail.grandParentItemArray == 0) {
					$('#cancelItemButton').prop('disabled', true);
					$('#paymentButton').prop('disabled', true);
				} else {
					$('#cancelItemButton').prop('disabled', false);
					$('#paymentButton').prop('disabled', false);
				}
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/ecpos";
			});
		}
		
		$scope.generateQR = function () {
			var jsonData = JSON.stringify({
				"tableNo" : $scope.tableNo,
				"checkNo" : $scope.checkNo
			});
			
			$http.post("${pageContext.request.contextPath}/rc/configuration/generate_qr/", jsonData)
			.then(function(response) {
				if (response.data.response_code == "00") {
					$('#QRImage').attr('src', response.data.QRImage);
					$('#QRImageModal').modal('show');
				} else {
					alert(response.data.response_message);
				}
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/ecpos";
			});
		}
		
		$scope.openBarcodeModal = function () {
			$('#barcodeModal').modal('show');
		}
		
		$( "#barcodeModal" ).on('shown.bs.modal', function(){
			$('#barcode_input').focus();
		});
		
		$scope.barcodeOrder = function() {
			if ($scope.barcode) {
				if ($scope.orderType == "table") {
					orderType = 1;
				} else if ($scope.orderType == "take_away") {
					orderType = 2;
				}
			
				var jsonData = JSON.stringify({
					"deviceType" : 1,
					"orderType" : orderType,
					"tableNo" : $scope.tableNo,
					"checkNo" : $scope.checkNo,
					"barcode" : $scope.barcode
				});
	
				$http.post("${pageContext.request.contextPath}/rc/check/barcode_order", jsonData)
				.then(function(response) {
					if (response.data.response_code === "00") {
						$scope.getCheckDetails();
						
						$scope.barcode = null;
					} else {
						if (response.data.response_message != null) {
							alert(response.data.response_message);
						} else {
							alert("Error Occured While Submit Order");
						}
					}
				},
				function(response) {
					alert("Session TIME OUT");
					window.location.href = "${pageContext.request.contextPath}/ecpos";
				});
			} else {
				alert("Barcode value is empty");
			}
		}
		
		$scope.allGrandParentItemCheckbox = function () {
			if(allGrandParentItemCheckbox.checked) {
		        $('[name=grandParentItemCheckbox]').each(function() {
		            this.checked = true;                        
		        });
		        
				$('#amount').html(parseFloat($scope.checkDetail.overdue).toFixed(2));
		    } else {
		        $('[name=grandParentItemCheckbox]').each(function() {
		            this.checked = false;                       
		        });
		        
		        $('#amount').html(parseFloat(0).toFixed(2));
		    }
		}
		
		$scope.grandParentItemCheckbox = function () {
			if ($("[name=grandParentItemCheckbox]:checked").length == $scope.checkDetail.grandParentItemArray.length) {
				allGrandParentItemCheckbox.checked = true;
			} else {
				allGrandParentItemCheckbox.checked = false;
			}
			
			$scope.getAccumulatedAmount();
		}
		
		$scope.getAccumulatedAmount = function() {
			$scope.checkedValue = [];
			$('#amount').html(parseFloat(0).toFixed(2));
			
			$("input[name=grandParentItemCheckbox]:checked").each(function(){
				$scope.checkedValue.push($(this).val());
			});
			
			if (!($scope.checkedValue === undefined || $scope.checkedValue == 0)) {
				var jsonData = JSON.stringify({
					"checkDetailIdArray" : $scope.checkedValue
				});
				
				$http.post("${pageContext.request.contextPath}/rc/transaction/get_accumulated_amount/", jsonData)
				.then(function(response) {
					$('#amount').html(parseFloat(response.data.accumulatedAmount).toFixed(2));
				},
				function(response) {
					alert("Session TIME OUT");
					window.location.href = "${pageContext.request.contextPath}/ecpos";
				});
			}
		}
		
		$scope.redirectTableOrder = function() {
			window.location.href = "${pageContext.request.contextPath}/ecpos/#!table_order";
		}
		
		$scope.cancelItem = function() {
			var confirmation = confirm("Confirm to cancel ordered item?");
			if (confirmation == true) {
				$scope.checkedValue = [];
				
				$("input[name=grandParentItemCheckbox]:checked").each(function(){
					$scope.checkedValue.push($(this).val());
				});
				
				if ($scope.checkedValue === undefined || $scope.checkedValue == 0) {
					alert("Kindly tick at least an item to proceed");
				} else {
					var jsonData = JSON.stringify({
						"checkDetailIdArray" : $scope.checkedValue
					});
					
					$http.post("${pageContext.request.contextPath}/rc/check/cancel_item", jsonData)
					.then(function(response) {
						if (response.data.response_code === "00") {
							alert("Order has been cancelled.")
							
							$scope.getCheckDetails();
							allGrandParentItemCheckbox.checked = false;
						} else {
							if (response.data.response_message != null) {
								alert(response.data.response_message);
							} else {
								alert("Error Occured While Remove Order");
							}
						}
					},
					function(response) {
						alert("Session TIME OUT");
						window.location.href = "${pageContext.request.contextPath}/ecpos";
					});
				}
			}
		}
		
		$scope.cancelCheck = function() {
			var confirmation = confirm("Confirm to cancel check?");
			if (confirmation == true) {
				var jsonData = JSON.stringify({
					"checkNo" : $scope.checkNo
				});
				
				$http.post("${pageContext.request.contextPath}/rc/check/cancel_check", jsonData)
				.then(function(response) {
					if (response.data.response_code === "00") {
						alert("Check has been cancelled.")

						window.location.href = "${pageContext.request.contextPath}/ecpos/#!table_order";
					} else {
						if (response.data.response_message != null) {
							alert(response.data.response_message);
						} else {
							alert("Error Occured While Remove Check");
						}
					}
				},
				function(response) {
					alert("Session TIME OUT");
					window.location.href = "${pageContext.request.contextPath}/ecpos";
				});
			}
		}
		
		$scope.redirectPayment = function() {
			$('#menuWell').hide();
			$('#paymentWell').show();
			$('#generateQRButton').hide();
			$('#barcodeOrderButton').hide();
			$('#checkActionButtons').hide();
			$("#allGrandParentItemCheckbox").hide();
			$("input[name=grandParentItemCheckbox]").hide();
			
			$('#amount').html(parseFloat($scope.checkDetail.overdue).toFixed(2));
		}
	});
</script>