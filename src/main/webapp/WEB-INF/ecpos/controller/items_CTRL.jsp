<script>
	app.controller('items_CTRL', function($scope, $http, $window, $routeParams, $location, $compile) {
		$scope.itemType = "";
		$scope.previousPage;
		
		$scope.initiation = function() {
			$scope.getDataTable("0");
		}
		
		$scope.getDataTable = function(itemType) {
			$scope.itemType = itemType;

			if ($scope.itemType == "0" || $scope.itemType == "1") {
				$scope.getItemsList();
			} else if ($scope.itemType == "2") {
				$scope.getModifierGroupsList();
			}
		}
		
		$scope.getItemsList = function() {
			var table = $('#datatable_items').DataTable({
				"ajax" : {
					"url" : "${pageContext.request.contextPath}/rc/menu/get_menu_items_by_item_type/" + $scope.itemType,
					"error" : function() {
						alert("Item list failed to display");
						window.location.href = "${pageContext.request.contextPath}/ecpos";
					}
				},
				"order" : [ [ 0, "asc" ] ],
				destroy : true,
				"columns" : [{"data" : "id", "width": "5%"}, 
					{"data" : "backendId", "width": "10%"}, 
					{"data" : "name", "width": "55%"},
					{"render" : function(data, type, full, meta) {
						var buttonValue;

						if ($scope.itemType == "0") {
							buttonValue = "Modifier Groups";
						} else if ($scope.itemType == "1") {
							buttonValue = "Combo Details";
						}
						
						return '<div class="col-xs-3"><button class="btn btn-info" ng-click="getItemDetails('+ full.id + ', \'' + full.backendId + '\', -1)">Details</button></div>' +
						'<div class="col-xs-9"><button class="btn btn-info green-button" style="background-color: #00FA9A; border-color: #00FA9A;" ng-click="redirectItemCarousel('+ $scope.itemType + ', \'' + full.id + '\', \'' + full.backendId + '\', \'' + full.name + '\', -1)">' + buttonValue + '</button></div>';
					}, "width": "30%"}],
				"createdRow": function ( row, data, index ) {
					$compile(row)($scope);
				}
			});
		}
		
		$scope.getModifierGroupsList = function() {
			var table = $('#datatable_modifierGroups').DataTable({
				"ajax" : {
					"url" : "${pageContext.request.contextPath}/rc/menu/get_modifier_groups/",
					"error" : function() {
						alert("Modifier group list failed to display");
						window.location.href = "${pageContext.request.contextPath}/ecpos";
					}
				},
				"order" : [ [ 0, "asc" ] ],
				destroy : true,
				"columns" : [{"data" : "id", "width": "5%"},
					{"data" : "name", "width": "67%"},
					{"render" : function(data, type, full, meta) {
						return '<div><button class="btn btn-info" ng-click="getModifierItemsList(' + full.id + ', \'' + full.name + '\', -1)">Details</button></div>';
					}, "width": "28%"}],
				"createdRow": function ( row, data, index ) {
					$compile(row)($scope);
				}
			});
		}
		
		$scope.getItemDetails = function(itemId, itemBackendId, callingPage) {
			$http.get("${pageContext.request.contextPath}/rc/menu/get_menu_items/"+itemId+"/"+itemBackendId)
			.then(function(response) {
				$scope.itemDetails = response.data;
				
				$('#itemDetailsCarousel').carousel(0);
				$('#itemDetailsModal').modal("show");
				
				if (callingPage == -1) {
					$('#back').hide();
				} else {
					$('#back').show();
					$scope.previousPage = callingPage;
				}
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/ecpos";
			});
		}
		
		$scope.getModifierItemsList = function(modifierGroupId, modifierGroupName, callingPage) {
			var table = $('#datatable_modifierItemsList').DataTable({
				"ajax" : {
					"url" : "${pageContext.request.contextPath}/rc/menu/get_modifier_items_list/"+modifierGroupId,
					"error" : function() {
						alert("Modifier item list failed to display");
						window.location.href = "${pageContext.request.contextPath}/ecpos";
					}
				},
				"order" : [ [ 0, "asc" ] ],
				destroy : true,
				"columns" : [{"data" : "sequence", "width": "5%"}, 
					{"data" : "backendId", "width": "10%"}, 
					{"data" : "name", "width": "55%"},
					{"render" : function(data, type, full, meta) {
						return '<div><button class="btn btn-info" ng-click="getItemDetails('+ full.id + ', \'' + full.backendId + '\', 1)">Details</button></div>';
					}, "width": "30%"}],
				"createdRow": function ( row, data, index ) {
					$compile(row)($scope);
				}
			});
			
			$('#modifierGroupName').html(modifierGroupName);
			
			$('#itemDetailsCarousel').carousel(1);
			$('#itemDetailsModal').modal("show");
			
			if (callingPage == -1) {
				$('#back1').hide();
			} else {
				$('#back1').show();
			}
		}
		
		$scope.redirectItemCarousel = function(itemType, itemId, itemBackendId, itemName, callingPage) {
			if (itemType == "0") {
				var table = $('#datatable_itemModifierGroupsList').DataTable({
					"ajax" : {
						"url" : "${pageContext.request.contextPath}/rc/menu/get_modifier_groups_by_menu_item/"+itemId+"/"+itemBackendId,
						"error" : function() {
							alert("Item modifier group list failed to display");
							window.location.href = "${pageContext.request.contextPath}/ecpos";
						}
					},
					"order" : [ [ 0, "asc" ] ],
					destroy : true,
					"columns" : [{"data" : "sequence", "width": "5%"},
						{"data" : "name", "width": "67%"},
						{"render" : function(data, type, full, meta) {
							return '<div><button class="btn btn-info" ng-click="getModifierItemsList(' + full.id + ', \'' + full.name + '\')">Details</button></div>';
						}, "width": "28%"}],
					"createdRow": function ( row, data, index ) {
						$compile(row)($scope);
					}
				});
				
				$('#itemName').html(itemName);
				
				$('#itemDetailsCarousel').carousel(2);
				$('#itemDetailsModal').modal("show");
				
				if (callingPage == -1) {
					$('#back2').hide();
				} else {
					$('#back2').show();
				}
			} else if (itemType == "1") {
				var table = $('#datatable_comboItemTiersList').DataTable({
					"ajax" : {
						"url" : "${pageContext.request.contextPath}/rc/menu/get_tiers/"+itemId+"/"+itemBackendId,
						"error" : function() {
							alert("Combo item tiers list failed to display");
							window.location.href = "${pageContext.request.contextPath}/ecpos";
						}
					},
					"order" : [ [ 0, "asc" ] ],
					destroy : true,
					"columns" : [{"data" : "sequence", "width": "5%"}, 
						{"data" : "name", "width": "60%"}, 
						{"data" : "quantity", "width": "5%"},
						{"render" : function(data, type, full, meta) {
							return '<div><button class="btn btn-info" ng-click="getComboItemTierItemsList(' + full.id + ', \'' + full.name + '\')">Details</button></div>';
						}, "width": "30%"}],
					"createdRow": function ( row, data, index ) {
						$compile(row)($scope);
					}
				});
				
				$('#comboItemName').html(itemName);
				
				$('#itemDetailsCarousel').carousel(3);
				$('#itemDetailsModal').modal("show");
			}
		}
		
		$scope.getComboItemTierItemsList = function(tierId, tierName) {
			var table = $('#datatable_comboItemTierItemsList').DataTable({
				"ajax" : {
					"url" : "${pageContext.request.contextPath}/rc/menu/get_tier_item_details/"+tierId,
					"error" : function() {
						alert("Tier item list failed to display");
						window.location.href = "${pageContext.request.contextPath}/ecpos";
					}
				},
				"ordering" : false,
				destroy : true,
				"columns" : [{"data" : "backendId", "width": "10%"}, 
					{"data" : "name", "width": "49%"},
					{"render" : function(data, type, full, meta) {
						return '<button class="btn btn-info" ng-click="getItemDetails('+ full.id + ', \'' + full.backendId + '\', 4)">Details</button>' +
						'<button class="btn btn-info green-button" style="background-color: #00FA9A; border-color: #00FA9A; margin-left: 7px;" ng-click="redirectItemCarousel(0, \'' + full.id + '\', \'' + full.backendId + '\', \'' + full.name + '\')">Modifier Groups</button>';
					}, "width": "41%"}],
				"createdRow": function ( row, data, index ) {
					$compile(row)($scope);
				}
			});
			
			$('#comboItemTierName').html(tierName);
			
			$('#itemDetailsCarousel').carousel(4);
			$('#itemDetailsModal').modal("show");
		}
		
		$scope.backCarousel = function() {
			$('#itemDetailsCarousel').carousel($scope.previousPage);
		}
	});
</script>