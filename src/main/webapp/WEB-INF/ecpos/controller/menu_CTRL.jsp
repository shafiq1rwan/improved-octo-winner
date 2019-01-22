<script>
	app.controller('menu_CTRL', function($scope, $http, $routeParams, $window, $location, $route) {
		$scope.tableNo = $routeParams.tableNo;
		$scope.checkNo = $routeParams.checkNo;
		
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
		
		$scope.initiation = function() {
			$scope.getCategories();
		}
		
		$scope.getCategories = function() {	
			$http.get("${pageContext.request.contextPath}/rc/menu/get_categories/")
			.then(function(response) {
				$scope.categories = response.data;
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/ecpos";
			});
		}
		
		$scope.getMenuItems = function(category) {
			$scope.category = category;
			
			$http.get("${pageContext.request.contextPath}/rc/menu/get_menu_items/"+$scope.category.id)
			.then(function(response) {
				$scope.menuItems = response.data;
				$('#categoryName').html($scope.category.name);
				$('#menuCarousel').carousel(1);
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/ecpos";
			});
		}
		
		$scope.action = function(item){
			$scope.temporary = {};
			$scope.temporary = item;
			
			if ($scope.temporary.type == 0) {
				$scope.alaCarte = $scope.temporary;
				$('#alaCarteItemName').html($scope.alaCarte.name);
				$('#menuCarousel').carousel(3);
				
				if ($scope.temporary.hasModifier == false) {
					$("#alaCarteModifier").hide();
				}
			} else if ($scope.temporary.type == 1) {
				$scope.getTiers($scope.temporary);
			}
		}
		
		$scope.getTiers = function(item) {
			$scope.item = item;
			
			$http.get("${pageContext.request.contextPath}/rc/menu/get_tiers/"+$scope.item.id+"/"+$scope.item.backendId)
			.then(function(response) {
				$scope.tiers = response.data;
				$scope.tiers.description = $scope.item.description;
				$scope.tiers.imagePath = $scope.item.imagePath;
				
				$('#itemName').html($scope.item.name);
				$('#menuCarousel').carousel(2);
				
				$scope.tierQuantityLoop($scope.tiers.jary[0]);
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/ecpos";
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
							if ($scope.temporaryTiers[i].items[j].sequence == j+1) {
								var index = j+1;
								var itemId = "#selectedItem"+index;
								$(itemId).html($scope.temporaryTiers[i].items[j].name.bold());
								
								var modifierId = "#selectedModifiers"+index;
								for (var k = 0; k < $scope.temporaryTiers[i].items[j].modifiers.length; k++) {
									$(modifierId).append("<div>" + $scope.temporaryTiers[i].items[j].modifiers[k].groupName + ": " + $scope.temporaryTiers[i].items[j].modifiers[k].name + "</div>");
								}
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
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/ecpos";
			});
		}
		
		$scope.getModifiers = function(tierItemDetail) {
			$scope.temporaryTierItem = {};
			$scope.temporaryTierItem = tierItemDetail;
			$scope.temporaryTierItem.sequence = $scope.sequence;
			
			$http.get("${pageContext.request.contextPath}/rc/menu/get_modifiers/"+$scope.temporaryTierItem.id+"/"+$scope.temporaryTierItem.backendId)
			.then(function(response) {
				$scope.modifiers = response.data;

				if (!($scope.modifiers.jary === undefined || $scope.modifiers.jary == 0)) {
					$('#tierItemDetailName').html($scope.temporaryTierItem.name);
					$('#itemCarousel').carousel(1);
				} else {
					$scope.saveTemporaryArray();
				}
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/ecpos";
			});
		}
		
		$scope.resetTemporary = function() {
			$scope.temporary = {};
			$scope.temporaryTiers = [];
			$scope.temporaryTierItems = [];
			$scope.temporaryModifiers = [];
			
			$scope.sequence = "";
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
			
			for (var i = 0; i < $scope.modifiers.jary.length; i++){
				$scope.temporaryModifier = {};
				$scope.temporaryModifier.groupId = $scope.modifiers.jary[i].id;
				$scope.temporaryModifier.groupName = $scope.modifiers.jary[i].name;
				
				var modifierValue = $("input[name='"+$scope.modifiers.jary[i].name+"']:checked").val();
				if (modifierValue == null) {
					return alert($scope.modifiers.jary[i].name + " cannot be blank.");
				}
				
				var modifierValueSplit = $("input[name='"+$scope.modifiers.jary[i].name+"']:checked").val().split("!!");
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
							if ($scope.temporaryTiers[i].items[j].sequence == j+1) {
								var index = j+1;
								var itemId = "#selectedItem"+index;
								$(itemId).html($scope.temporaryTiers[i].items[j].name.bold());
								
								var modifierId = "#selectedModifiers"+index;
								$(modifierId).html("");
								for (var k = 0; k < $scope.temporaryTiers[i].items[j].modifiers.length; k++) {
									$(modifierId).append("<div>" + $scope.temporaryTiers[i].items[j].modifiers[k].groupName + ": " + $scope.temporaryTiers[i].items[j].modifiers[k].name + "</div>");
								}
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
						return alert("Order is not complete. Kindly fill in.");
					}
				}
				$scope.temporary.modifiers = $scope.temporaryModifiers;
				$scope.temporary.orderQuantity = $('#alaCarteItemQuantity').val();
			} else if ($scope.temporary.type == 1) {
				if ($scope.temporaryTiers.length == 0) {
					return alert("Order is not complete. Kindly fill in.");
				} else {			
					for (var i = 0; i < $scope.temporaryTiers.length; i++) {
						if ($scope.temporaryTiers[i].quantity != $scope.temporaryTiers[i].items.length) {
							return alert("Order is not complete. Kindly fill in.");
						}
					}
				}
				$scope.temporary.tiers = $scope.temporaryTiers;
				$scope.temporary.orderQuantity = $('#itemQuantity').val();
			}
			
			var jsonData = JSON.stringify({
				"deviceType" : 2,
				"orderType" : 1,
				"tableNo" : $scope.tableNo,
				"checkNo" : $scope.checkNo,
				"item" : $scope.temporary
			});

			$http.post("${pageContext.request.contextPath}/rc/menu/order", jsonData)
			.then(function(response) {
				if (response.data.response_code === "00") {
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
				} else {
					alert("Error Occured While Submit Order");
					window.location.href = "${pageContext.request.contextPath}/ecpos";
				}
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/ecpos";
			});
		}
	});
</script>