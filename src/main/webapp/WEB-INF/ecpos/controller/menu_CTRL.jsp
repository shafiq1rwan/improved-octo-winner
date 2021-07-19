<script>
	app.controller('menu_CTRL', function($scope, $http, $routeParams, $window, $location, $route) {
		$scope.tableNo = $routeParams.tableNo;
		$scope.checkNo = $routeParams.checkNo;
		
		$scope.screenWidth = $("html").innerWidth();
		
		$scope.categories = {};
		$scope.menuItems = {};
		$scope.tiers = {};
		$scope.tierItemDetails = {};
		$scope.modifiers = {};
		
		$scope.temporary = {};
		$scope.temporaryTiers = [];
		$scope.temporaryTierItems = [];
		$scope.temporaryModifiers = [];
		
		$scope.sequence = "";
		
		$("#alaCarteModifier").show();
		$("#back").show();
		
		$scope.menuInitiation = function() {
			$http.get("${pageContext.request.contextPath}/rc/configuration/session_checking")
			.then(function(response) {
				if (response.data.responseCode == "00") {
					$scope.getCategories();
				}
			});
		}
		
		$scope.getCategories = function() {	
			$http.get("${pageContext.request.contextPath}/rc/menu/get_categories/")
			.then(function(response) {
				$scope.categories = response.data;
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
		
		$scope.getMenuItems = function(category) {
			$scope.category = category;
			
			if($scope.category.id == 0){// if item group
				$http.get("${pageContext.request.contextPath}/rc/menu/get_all_item_group/")
				.then(function(response) {
					$scope.menuItems = response.data;
					$('#categoryName').html($scope.category.name);
					$('#menuCarousel').carousel(1);
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
			}else{// if normal category
				$http.get("${pageContext.request.contextPath}/rc/menu/get_menu_items/"+$scope.category.id)
				.then(function(response) {
					$scope.menuItems = response.data;
					$('#categoryName').html($scope.category.name);
					$('#menuCarousel').carousel(1);
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
		
		$scope.action = function(item){
			$scope.temporary = {};
			$scope.temporary = item;
			
			if($scope.category.id == 0){// if item group menu item
				if ($scope.orderType == "table") {
					orderType = 1;
				} else if ($scope.orderType == "take_away") {
					orderType = 2;
				} else if ($scope.orderType == "deposit") {
					orderType = 3;
				}				

				var jsonData = JSON.stringify({
					"deviceType" : 1,
					"orderType" : orderType,
					"tableNo" : $scope.tableNo,
					"checkNo" : $scope.checkNo,
					"itemGroup" : $scope.temporary
				});
				$http.post("${pageContext.request.contextPath}/rc/check/item_group_order", jsonData)
				.then(function(response) {
					if (response.data.response_code === "00") {
						allGrandParentItemCheckbox.checked = false;
						$scope.getCheckDetails();
						
						$('#itemQuantity').val(1);
						$('#alaCarteItemQuantity').val(1);
						
	 					$('#menuCarousel').carousel(0);
	 					$('#itemCarousel').carousel(0);
						$scope.temporary = {};
						$scope.temporaryTiers = [];
						$scope.temporaryTierItems = [];
						$scope.temporaryModifiers = [];
						
						$scope.sequence = "";
						
						$("#alaCarteModifier").show();
						$("#back").show();
						$scope.informSecondDisplay(jsonData);
					} else {
						if (response.data.response_message != null) {
							Swal.fire("Oops...",response.data.response_message,"error");
						} else {
							Swal.fire("Oops...","Error Occured While Submit Order","error");
						}
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
			}else{// if normal menu item
				if ($scope.temporary.type == 0) {// if menu item type = ala carte
					$scope.alaCarte = $scope.temporary;
					$('#alaCarteItemName').html($scope.alaCarte.name);
					$('#menuCarousel').carousel(3);
					
					if ($scope.temporary.hasModifier == false) {
						$("#alaCarteModifier").hide();
					} else {
						$("#alaCarteModifier").show();
					}
				} else if ($scope.temporary.type == 1) {// if menu item type = combo
					$scope.getTiers($scope.temporary);
				}
			}
		}
		
		/*$scope.submitItemGroupOrder = function(jsonData) {

			$http.post("${pageContext.request.contextPath}/rc/check/item_group_order", jsonData)
			.then(function(response) {
				if (response.data.response_code === "00") {
					allGrandParentItemCheckbox.checked = false;
					$scope.getCheckDetails();
					
					$('#itemQuantity').val(1);
					$('#alaCarteItemQuantity').val(1);
					
 					$('#menuCarousel').carousel(0);
 					$('#itemCarousel').carousel(0);
					$scope.temporary = {};
					$scope.temporaryTiers = [];
					$scope.temporaryTierItems = [];
					$scope.temporaryModifiers = [];
					
					$scope.sequence = "";
					
					$("#alaCarteModifier").show();
					$("#back").show();
					$scope.informSecondDisplay(jsonData);
				} else {
					if (response.data.response_message != null) {
						Swal.fire("Oops...",response.data.response_message,"error");
					} else {
						Swal.fire("Oops...","Error Occured While Submit Order","error");
					}
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
		}*/
		
		
		$scope.getTiers = function(item) {
			$scope.item = item;
			
			$http.get("${pageContext.request.contextPath}/rc/menu/get_tiers/"+$scope.item.id+"/"+$scope.item.backendId)
			.then(function(response) {
				$scope.tiers = response.data;
				$scope.tiers.description = $scope.item.description;
				$scope.tiers.imagePath = $scope.item.imagePath;
				
				$('#itemName').html($scope.item.name);
				$('#menuCarousel').carousel(2);
				
				setTimeout(function() { 
					$('#pill'+$scope.tiers.data[0].id).addClass("active");
				}, 10);
				$scope.tierQuantityLoop($scope.tiers.data[0]);
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
		
		$scope.tierQuantityLoop = function (tier) {
			$scope.tierDetails = {};
			$scope.tierDetails = tier;
			
			$scope.tierDetails.quantityArray = [];
			for (var i = 0; i < $scope.tierDetails.quantity; i++) {
				$scope.tierDetails.quantityArray.push({});
			}
			
			setTimeout(function() {
				for (var i = 0; i < $scope.temporaryTiers.length; i++) {
					if ($scope.temporaryTiers[i].id == $scope.tierDetails.id) {
						for (var j = 0; j < $scope.temporaryTiers[i].items.length; j++) {
							var index = $scope.temporaryTiers[i].items[j].sequence;
							var itemId = "#selectedItem"+index;
							$(itemId).html($scope.temporaryTiers[i].items[j].name.bold());
							
							var modifierId = "#selectedModifiers"+index;
							for (var k = 0; k < $scope.temporaryTiers[i].items[j].modifiers.length; k++) {
								$(modifierId).append("<div>" + $scope.temporaryTiers[i].items[j].modifiers[k].groupName + ": " + $scope.temporaryTiers[i].items[j].modifiers[k].name + "</div>");
							}
						}
					}
				}
			},10);
		}
		
		$scope.openMenuItemModal = function(data, sequence, type) {
			if (type == "combo") {
				$scope.getTierItemDetails(data);
				$scope.sequence = sequence;
				$('#menuItemModal').modal('show');
			} else if (type == "alaCarte") {
				$scope.getModifiers(data);
				$("#back").hide();
				$("#menuItemModal").modal("show");
			}
		}
		
		$scope.getTierItemDetails = function(tier) {
			$scope.temporaryTier = {};
			$scope.temporaryTier = tier;
			
			$http.get("${pageContext.request.contextPath}/rc/menu/get_tier_item_details/"+$scope.temporaryTier.id)
			.then(function(response) {
				$scope.tierItemDetails = response.data;
				
				$scope.tierItemDetails.tierName = $scope.temporaryTier.name;
				
				$('#tierName').html($scope.temporaryTier.name);
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
		
		$scope.getModifiers = function(tierItemDetail) {
			$scope.temporaryTierItem = {};
			$scope.temporaryTierItem = tierItemDetail;
			$scope.temporaryTierItem.sequence = $scope.sequence;
			
			$http.get("${pageContext.request.contextPath}/rc/menu/get_modifiers/"+$scope.temporaryTierItem.id+"/"+$scope.temporaryTierItem.backendId)
			.then(function(response) {
				$scope.modifiers = response.data;

				if (!($scope.modifiers.data === undefined || $scope.modifiers.data == 0)) {
					$('#tierItemDetailName').html($scope.temporaryTierItem.name);
					$('#itemCarousel').carousel(1);
				} else {
					$scope.saveTemporaryArray();
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
		
		$scope.resetTemporary = function() {
			$scope.temporary = {};
			$scope.temporaryTiers = [];
			$scope.temporaryTierItems = [];
			$scope.temporaryModifiers = [];
			
			$scope.sequence = "";
			
			$('#itemQuantity').val(1);
			$('#alaCarteItemQuantity').val(1);
		}
		
		$scope.addItemQuantity = function() {
			var existing = parseInt($('#itemQuantity').val());
			var current = existing + 1;
			$('#itemQuantity').val(current);
		}
		
		$scope.minusItemQuantity = function() {
			var existing = parseInt($('#itemQuantity').val());
			var current = existing - 1;
			
			if (current > 0) {
				$('#itemQuantity').val(current);
			}
		}
		
		$scope.addAlaCarteItemQuantity = function() {
			var existing = parseInt($('#alaCarteItemQuantity').val());
			var current = existing + 1;
			$('#alaCarteItemQuantity').val(current);
		}
		
		$scope.minusAlaCarteItemQuantity = function() {
			var existing = parseInt($('#alaCarteItemQuantity').val());
			var current = existing - 1;
			
			if (current > 0) {
				$('#alaCarteItemQuantity').val(current);
			}
		}
		
		$scope.saveTemporaryArray = function() {
			$scope.temporaryModifiers = [];
			
			for (var i = 0; i < $scope.modifiers.data.length; i++){
				$scope.temporaryModifier = {};
				$scope.temporaryModifier.groupId = $scope.modifiers.data[i].id;
				$scope.temporaryModifier.groupName = $scope.modifiers.data[i].name;
				
				var modifierValue = $("input[name='"+$scope.modifiers.data[i].name+"']:checked").val();
				if (modifierValue == null) {
					/* return alert($scope.modifiers.data[i].name + " cannot be blank."); */
					return Swal.fire("Warning",$scope.modifiers.data[i].name + " cannot be blank.","warning");
				}
				
				var modifierValueSplit = $("input[name='"+$scope.modifiers.data[i].name+"']:checked").val().split("!!");
				$scope.temporaryModifier.id = modifierValueSplit[0];
				$scope.temporaryModifier.backendId = modifierValueSplit[1];
				$scope.temporaryModifier.name = modifierValueSplit[2];
				
				$scope.temporaryModifiers.push($scope.temporaryModifier);
			}
			
			if ($scope.temporary.type == 0) {
				$("#selectedAlaCarteModifiers").html("");
				for (var i = 0; i < $scope.temporaryModifiers.length; i++) {
					$("#selectedAlaCarteModifiers").append("<div>" + $scope.temporaryModifiers[i].groupName + ": " + $scope.temporaryModifiers[i].name + "</div>");
				}
			} else if ($scope.temporary.type == 1) {
				$scope.temporaryTierItem.modifiers = $scope.temporaryModifiers;
				
				if ($scope.temporaryTiers.length == 0) {
					$scope.temporaryTierItems.push($scope.temporaryTierItem);
					$scope.temporaryTier.items = $scope.temporaryTierItems;
					$scope.temporaryTiers.push($scope.temporaryTier);
				} else {
					var isTierIdExist = false;
					var tierObjectSequence = 0;
					$scope.temporaryTierItems = [];
					
					for (var i = 0; i < $scope.temporaryTiers.length; i++){
						if ($scope.temporaryTiers[i].id.indexOf($scope.temporaryTier.id) != -1) {
							isTierIdExist = true;
							tierObjectSequence = i;
						}
					}
					
					if (isTierIdExist) {
						var isTierItemSequenceExist = false;
						var tierItemObjectSequence = 0;
						
						for (var i = 0; i < $scope.temporaryTiers[tierObjectSequence].items.length; i++){
							if ($scope.temporaryTiers[tierObjectSequence].items[i].sequence.toString().indexOf($scope.temporaryTierItem.sequence.toString()) != -1) {
								isTierItemSequenceExist = true;
								tierItemObjectSequence = i;
							}
						}
	
						if (isTierItemSequenceExist) {
							$scope.temporaryTiers[tierObjectSequence].items.splice(tierItemObjectSequence,1);
						}
						$scope.temporaryTiers[tierObjectSequence].items.push($scope.temporaryTierItem);
					} else {
						$scope.temporaryTierItems.push($scope.temporaryTierItem);
						$scope.temporaryTier.items = $scope.temporaryTierItems;
						$scope.temporaryTiers.push($scope.temporaryTier);
					}
				}

				for (var i = 0; i < $scope.temporaryTiers.length; i++) {
					if ($scope.temporaryTiers[i].id == $scope.temporaryTier.id) {
						for (var j = 0; j < $scope.temporaryTiers[i].items.length; j++) {
							var index = $scope.temporaryTiers[i].items[j].sequence;
							var itemId = "#selectedItem"+index;
							
							$(itemId).html($scope.temporaryTiers[i].items[j].name.bold());
							
							var modifierId = "#selectedModifiers"+index;
							$(modifierId).html("");
							for (var k = 0; k < $scope.temporaryTiers[i].items[j].modifiers.length; k++) {
								$(modifierId).append("<div>" + $scope.temporaryTiers[i].items[j].modifiers[k].groupName + ": " + $scope.temporaryTiers[i].items[j].modifiers[k].name + "</div>");
							}
						}
					}
					
					if (!$scope.temporaryTiers[i].hasOwnProperty("status")) {
						if ($scope.temporaryTiers[i].quantity == $scope.temporaryTiers[i].items.length) {
							var tabId = "#pillTab" + $scope.temporaryTiers[i].id;
							$(tabId).append(' <b><font color="green">&#10004;</font></b>');
							$scope.temporaryTiers[i].status = "done";
						}
					}
				}
			}

			$('#menuItemModal').modal('hide');
			$('#itemCarousel').carousel(0);
		}
		
		$scope.submitOrder = function() {
			if ($scope.temporary.type == 0) {
				if ($scope.temporary.hasModifier == true) {
					if ($scope.temporaryModifiers.length == 0) {
						/* return alert("Order is not complete. Kindly fill in."); */
						return Swal.fire("Warning","Order is not complete. Kindly fill in","warning");
					}
				}
				$scope.temporary.modifiers = $scope.temporaryModifiers;
				$scope.temporary.orderQuantity = $('#alaCarteItemQuantity').val();
			} else if ($scope.temporary.type == 1) {
				if ($scope.temporaryTiers.length == 0) {
					/* return alert("Order is not complete. Kindly fill in."); */
					return Swal.fire("Warning","Order is not complete. Kindly fill in","warning");
				} else {			
					for (var i = 0; i < $scope.temporaryTiers.length; i++) {
						if ($scope.temporaryTiers[i].quantity != $scope.temporaryTiers[i].items.length) {
							/* return alert("Order is not complete. Kindly fill in."); */
							return Swal.fire("Warning","Order is not complete. Kindly fill in","warning");
						}
					}
				}
				$scope.temporary.tiers = $scope.temporaryTiers;
				$scope.temporary.orderQuantity = $('#itemQuantity').val();
			}
			
			if ($scope.orderType == "table") {
				orderType = 1;
			} else if ($scope.orderType == "take_away") {
				orderType = 2;
			} else if ($scope.orderType == "deposit") {
				orderType = 3;
			}
			
			var jsonData = JSON.stringify({
				"deviceType" : 1,
				"orderType" : orderType,
				"tableNo" : $scope.tableNo,
				"checkNo" : $scope.checkNo,
				"item" : $scope.temporary
			});

			$http.post("${pageContext.request.contextPath}/rc/check/order", jsonData)
			.then(function(response) {
				if (response.data.response_code === "00") {
					allGrandParentItemCheckbox.checked = false;
					$scope.getCheckDetails();
					
					$('#itemQuantity').val(1);
					$('#alaCarteItemQuantity').val(1);
					
 					$('#menuCarousel').carousel(0);
 					$('#itemCarousel').carousel(0);
					$scope.temporary = {};
					$scope.temporaryTiers = [];
					$scope.temporaryTierItems = [];
					$scope.temporaryModifiers = [];
					
					$scope.sequence = "";
					
					$("#alaCarteModifier").show();
					$("#back").show();
					$scope.informSecondDisplay(jsonData);
				} else {
					if (response.data.response_message != null) {
						/* alert(response.data.response_message); */
						Swal.fire("Oops...",response.data.response_message,"error");
					} else {
						/* alert("Error Occured While Submit Order"); */
						Swal.fire("Oops...","Error Occured While Submit Order","error");
					}
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
		
		$scope.barcodeOrder = function() {
			if ($scope.barcode) {
				if ($scope.orderType == "table") {
					orderType = 1;
				} else if ($scope.orderType == "take_away") {
					orderType = 2;
				} else if ($scope.orderType == "deposit") {
					orderType = 3;
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
						$scope.barcode = null;
						if (response.data.response_message != null) {
							/* alert(response.data.response_message); */
							Swal.fire("Oops...",response.data.response_message,"error");
						} else {
							/* alert("Error Occured While Submit Order"); */
							Swal.fire("Oops...","Error Occured While Submit Order","error");
						}
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
			/* else {
				alert("Barcode value is empty");
			} */
		}
		
		$scope.informSecondDisplay = function (jsonData) {
			/* var json = JSON.stringify(jsonData); */
			/* console.log("Send data: " + json) */

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
				/* alert(event); */
			}
					
			kdsSocket.onclose = function(event) {
				console.log($scope.jsonResult);
				console.log("Connection closed");
			}	
		}
	});
</script>