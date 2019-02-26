<script>
	app.controller('check_CTRL', function($scope, $http, $routeParams, $window, $location, $route) {
		$scope.orderType = $routeParams.orderType;
		$scope.checkNo = $routeParams.checkNo;
		$scope.tableNo = $routeParams.tableNo;
		
		$scope.checkDetail = {};
		
		$('#menuWell').show();
		$('#paymentWell').hide();
		$('#printKitchenReceiptButton').show();
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
		
		$scope.printQR = function () {
			alert("This function is currently not available");
		}
		
		$scope.printKitchenReceipt = function () {
			alert("This function is currently not available");
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
						alert("Error Occured While Remove Order");
						window.location.href = "${pageContext.request.contextPath}/ecpos";
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
			$('#printKitchenReceiptButton').hide();
			$('#checkActionButtons').hide();
			$("#allGrandParentItemCheckbox").hide();
			$("input[name=grandParentItemCheckbox]").hide();
			
			$('#amount').html(parseFloat($scope.checkDetail.overdue).toFixed(2));
		}
	});
</script>