<script>
	app.controller('check_CTRL', function($scope, $http, $routeParams, $window, $location, $route) {
		$scope.orderType = $routeParams.orderType;
		$scope.checkNo = $routeParams.checkNo;
		$scope.tableNo = $routeParams.tableNo;
		
		$scope.checkDetail = {};
		
		$scope.mode = 1;
		
		$scope.fullPaymentAmount = "";
		
		$('#menuWell').show();
		$('#paymentWell').hide();
		$('#generateQRButton').show();
		$('#barcodeOrderButton').show();
		$('#checkActionButtons').show();
		$('#cancelItemButton').prop('disabled', true);
		$('#paymentButton').prop('disabled', true);
		$('#splitCheckButton').prop('disabled', true);
		$('#closeCheckButton').prop('disabled', true);
		$('#allGrandParentItemCheckbox').show();
		$('input[name=grandParentItemCheckbox]').show();
		$('#terminalList').hide();

		$scope.initiation = function() {
			$http.get("${pageContext.request.contextPath}/rc/configuration/session_checking")
			.then(function(response) {
				if (response.data.responseCode == "00") {
					if ($scope.orderType == "table") {
						$('#generateQRButton').show();
					} else  {
						$('#generateQRButton').hide();
					}
					
					$scope.getCheckDetails();
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
		
		$scope.getCheckDetails = function() {
			$http.get("${pageContext.request.contextPath}/rc/check/get_all_check_detail/" + $scope.orderType + "/" + $scope.checkNo + "/" + $scope.tableNo)
			.then(function(response) {
				if (response.data.hasOwnProperty('response_code') && response.data.response_code == "01") {
					if ($scope.orderType == "table") {
						alert(response.data.response_message);
						$location.path("/table_order");
					} else if ($scope.orderType == "take_away") {
						alert(response.data.response_message);
						$location.path("/take_away_order");
					} else if ($scope.orderType == "deposit") {
						alert(response.data.response_message);
						$location.path("/deposit_order");
					}
				} else {
					$scope.checkDetail = response.data;
					
					if ($scope.checkDetail.grandParentItemArray === undefined || $scope.checkDetail.grandParentItemArray == 0) {
						$('#cancelItemButton').prop('disabled', true);
						$('#paymentButton').prop('disabled', true);
						$('#splitCheckButton').prop('disabled', true);
						$('#closeCheckButton').prop('disabled', true);
					} else {
						$('#cancelItemButton').prop('disabled', false);
						$('#paymentButton').prop('disabled', false);
						$('#splitCheckButton').prop('disabled', false);
						$('#closeCheckButton').prop('disabled', false);
					}
					
					setTimeout(function() {
						$('input[name=itemQuantity]').click(function(){
						    $(this).select();
						});
					},100);
				}
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/signout";
			});
		}
		
		$scope.generateQR = function () {
			var jsonData = JSON.stringify({
				"tableNo" : $scope.tableNo,
				"checkNo" : $scope.checkNo,
				"tableName" : $scope.checkDetail.tableName
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
				window.location.href = "${pageContext.request.contextPath}/signout";
			});
		}
		
		$scope.displayQRPdf = function(){
 			var jsonData = JSON.stringify({
				"tableNo" : $scope.tableNo,
				"checkNo" : $scope.checkNo,
				"qrImage" : $('#QRImage').attr('src')
			}); 

 			console.log(jsonData)
 			
			$http.post("${pageContext.request.contextPath}/rc/configuration/display_qr_pdf", jsonData,{responseType: 'arraybuffer'})
			.then(function(response) {			
			    var file = new Blob([response.data], {type: 'application/pdf'});
			    var fileURL = URL.createObjectURL(file);
			    window.open(fileURL);
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/signout";
			});
		}
		
		$scope.printQR = function(){
 			var jsonData = JSON.stringify({
				"tableNo" : $scope.tableNo,
				"checkNo" : $scope.checkNo,
				"qrImage" : $('#QRImage').attr('src')
			}); 

			$http.post("${pageContext.request.contextPath}/rc/configuration/print_qr", jsonData)
			.then(function(response) {
				if (response.data.response_code == "00") {
					$('#QRImageModal').modal('hide');
				} else {
					$('#QRImageModal').modal('hide');
					alert(response.data.response_message);
				}
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/signout";
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
					window.location.href = "${pageContext.request.contextPath}/signout";
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
		        
				$('#tenderAmount').html(parseFloat($scope.checkDetail.overdueAmount).toFixed(2));
		    } else {
		        $('[name=grandParentItemCheckbox]').each(function() {
		            this.checked = false;                       
		        });
		        
		        $('#tenderAmount').html(parseFloat(0).toFixed(2));
		    }
		}
		
		$scope.grandParentItemCheckbox = function() {
			if ($("[name=grandParentItemCheckbox]:checked").length == $scope.checkDetail.grandParentItemArray.length) {
				allGrandParentItemCheckbox.checked = true;
			} else {
				allGrandParentItemCheckbox.checked = false;
			}
			
			$scope.getAccumulatedAmount();
		}
		
		$scope.getAccumulatedAmount = function() {
			$scope.checkedValue = [];
			$('#tenderAmount').html(parseFloat(0).toFixed(2));
			
			$("input[name=grandParentItemCheckbox]:checked").each(function(){
				$scope.checkedValue.push($(this).val());
			});
			
			if (!($scope.checkedValue === undefined || $scope.checkedValue == 0)) {
				var jsonData = JSON.stringify({
					"checkDetailIdArray" : $scope.checkedValue
				});
				
				$http.post("${pageContext.request.contextPath}/rc/transaction/get_accumulated_amount/", jsonData)
				.then(function(response) {
					$('#tenderAmount').html(parseFloat(response.data.accumulatedAmount).toFixed(2));
				},
				function(response) {
					alert("Session TIME OUT");
					window.location.href = "${pageContext.request.contextPath}/signout";
				});
			}
		}
		
		$scope.submitUpdateItemQuantity = function(checkDetailId) {
			var jsonData = JSON.stringify({
				"id" : checkDetailId,
				"quantity" : $('#'+checkDetailId).val()
			});
			
			$http.post("${pageContext.request.contextPath}/rc/check/update_item_quantity", jsonData)
			.then(function(response) {
				if (response.data.response_code === "00") {
					$scope.getCheckDetails();
				} else {
					if (response.data.response_message != null) {
						alert(response.data.response_message);
					} else {
						alert("Error Occured While Updating Order");
					}
				}
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/signout";
			});
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
						window.location.href = "${pageContext.request.contextPath}/signout";
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

						if ($scope.orderType == "table") {
							$location.path("/table_order");
						} else if ($scope.orderType == "take_away") {
							$location.path("/take_away_order");
						} else if ($scope.orderType == "deposit") {
							$location.path("/deposit_order");
						}
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
					window.location.href = "${pageContext.request.contextPath}/signout";
				});
			}
		}
		
		$scope.splitCheck = function() {
			$scope.checkedValue = [];
			
			$("input[name=grandParentItemCheckbox]:checked").each(function(){
				$scope.checkedValue.push($(this).val());
			});
			
			if ($scope.checkDetail.grandParentItemArray.length <= 1) {
				alert("There is only 1 item ordered.");
			} else if ($scope.checkedValue === undefined || $scope.checkedValue == 0) {
				alert("Kindly tick at least an item to proceed");
			} else if ($scope.checkDetail.grandParentItemArray.length == $scope.checkedValue.length) {	
				alert("All ordered item has been selected.")
			} else {
				var confirmation = confirm("Confirm to split check?");
				if (confirmation == true) {
					var jsonData = JSON.stringify({
						"orderType" : $scope.orderType,
						"tableNo" : $scope.tableNo,
						"checkNo" : $scope.checkNo,
						"checkDetailIdArray" : $scope.checkedValue
					});
					
					$http.post("${pageContext.request.contextPath}/rc/check/split_check", jsonData)
					.then(function(response) {
						if (response.data.response_code === "00") {
							var data = "/check/" + "table" + "/" + response.data.new_check_no + "/" + $scope.tableNo;
							$location.path(data);
						} else {
							if (response.data.response_message != null) {
								alert(response.data.response_message);
							} else {
								alert("Error Occured While Split Check");
							}
						}
					},
					function(response) {
						alert("Session TIME OUT");
						window.location.href = "${pageContext.request.contextPath}/signout";
					});
				}
			}
		}
		
		$scope.closeCheck = function() {
			var confirmation = confirm("Confirm to close check?");
			if (confirmation == true) {
				if (parseFloat($scope.checkDetail.overdueAmount) > 0) {
					alert("Kindly clear the overdue amount.");
				} else {
					var jsonData = JSON.stringify({
						"orderType" : $scope.orderType,
						"checkNo" : $scope.checkNo
					});
					
					$http.post("${pageContext.request.contextPath}/rc/check/close_check", jsonData)
					.then(function(response) {
						if (response.data.response_code === "00") {
							var data = "/deposit_order";
							$location.path(data);
						} else {
							if (response.data.response_message != null) {
								alert(response.data.response_message);
							} else {
								alert("Error Occured While Close Check");
							}
						}
					},
					function(response) {
						alert("Session TIME OUT");
						window.location.href = "${pageContext.request.contextPath}/signout";
					});
				}
			}
		}
		
		$scope.redirectPayment = function() {
			$scope.mode = 2;
			$('#menuWell').hide();
			$('#paymentWell').show();
			$('#generateQRButton').hide();
			$('#barcodeOrderButton').hide();
			$('#checkActionButtons').hide();
			$("#allGrandParentItemCheckbox").hide();
			$("input[name=grandParentItemCheckbox]").hide();
			
			$('#tenderAmount').html(parseFloat($scope.checkDetail.overdueAmount).toFixed(2));
			$scope.fullPaymentAmount = $('#tenderAmount').text();

			$('#paymentCarousel').carousel(0);
		}
		
		$scope.redirectMenu = function() {
			$scope.mode = 1;
			$('#menuWell').show();
			$('#paymentWell').hide();
			$('#generateQRButton').show();
			$('#barcodeOrderButton').show();
			$('#checkActionButtons').show();
			$('#allGrandParentItemCheckbox').show();
			$('input[name=grandParentItemCheckbox]').show();
			
			allGrandParentItemCheckbox.checked = false;
			
		 	$('[name=grandParentItemCheckbox]').each(function() {
	            this.checked = false;                       
	     	});
		}
	});
</script>