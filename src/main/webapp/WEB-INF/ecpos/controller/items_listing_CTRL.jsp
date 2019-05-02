<script>
	app.controller('items_listing_CTRL', function($scope, $http, $window, $routeParams, $location, $compile) {
		$scope.itemType = "";
		$scope.previousPage;
		
		$scope.initiation = function() {
			$http.get("${pageContext.request.contextPath}/rc/configuration/session_checking")
			.then(function(response) {
				if (response.data.responseCode == "00") {
					$scope.getDataTable("0");
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
						alert("Session TIME OUT");
						window.location.href = "${pageContext.request.contextPath}/signout";
					}
				},
				"searching": false,
				"pageLength": 8,
				"bLengthChange": false,
				"order" : [ [ 0, "asc" ] ],
				destroy : true,
				"columns" : [{"data" : "id", "width": "5%"}, 
					{"data" : "backendId", "width": "15%"}, 
					{"data" : "name", "width": "50%"},
					{"render" : function(data, type, full, meta) {
						var buttonValue;

						if ($scope.itemType == "0") {
							buttonValue = "Modifier Groups";
						} else if ($scope.itemType == "1") {
							buttonValue = "Combo Details";
						}
						
						return '<button class="btn btn-sm btn-info" ng-click="getItemDetails('+ full.id + ', \'' + full.backendId + '\', -1)">Details</button>' +
						'&nbsp;<button class="btn btn-sm btn-info green-button" style="background-color: #00FA9A; border-color: #00FA9A;" ng-click="redirectItemCarousel('+ $scope.itemType + ', \'' + full.id + '\', \'' + full.backendId + '\', \'' + full.name + '\', -1)">' + buttonValue + '</button>';
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
						alert("Session TIME OUT");
						window.location.href = "${pageContext.request.contextPath}/signout";
					}
				},
				"searching": false,
				"pageLength": 8,
				"bLengthChange": false,
				"order" : [ [ 0, "asc" ] ],
				destroy : true,
				"columns" : [{"data" : "id", "width": "5%"},
					{"data" : "name", "width": "67%"},
					{"render" : function(data, type, full, meta) {
						return '<button class="btn btn-sm btn-info" ng-click="getModifierItemsList(' + full.id + ', \'' + full.name + '\', -1)">Details</button>';
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
				window.location.href = "${pageContext.request.contextPath}/signout";
			});
		}
		
		$scope.getModifierItemsList = function(modifierGroupId, modifierGroupName, callingPage) {
			var table = $('#datatable_modifierItemsList').DataTable({
				"ajax" : {
					"url" : "${pageContext.request.contextPath}/rc/menu/get_modifier_items_list/"+modifierGroupId,
					"error" : function() {
						alert("Session TIME OUT");
						window.location.href = "${pageContext.request.contextPath}/signout";
					}
				},
				"searching": false,
				"pageLength": 8,
				"bLengthChange": false,
				"order" : [ [ 0, "asc" ] ],
				destroy : true,
				"columns" : [{"data" : "sequence", "width": "18%"}, 
					{"data" : "backendId", "width": "20%"}, 
					{"data" : "name", "width": "50%"},
					{"render" : function(data, type, full, meta) {
						return '<button class="btn btn-sm btn-info" ng-click="getItemDetails('+ full.id + ', \'' + full.backendId + '\', 1)">Details</button>';
					}, "width": "12%"}],
				"createdRow": function ( row, data, index ) {
					$compile(row)($scope);
				},
				"initComplete": function( settings, json ) {
					 $('#modifierGroupName').html(modifierGroupName);
						
					$('#itemDetailsCarousel').carousel(1);
					$('#itemDetailsModal').modal("show");
					
					if (callingPage == -1) {
						$('#back1').hide();
					} else {
						$('#back1').show();
					}
				 }
			});
		}
		
		$scope.redirectItemCarousel = function(itemType, itemId, itemBackendId, itemName, callingPage) {
			if (itemType == "0") {
				var table = $('#datatable_itemModifierGroupsList').DataTable({
					"ajax" : {
						"url" : "${pageContext.request.contextPath}/rc/menu/get_modifier_groups_by_menu_item/"+itemId+"/"+itemBackendId,
						"error" : function() {
							alert("Session TIME OUT");
							window.location.href = "${pageContext.request.contextPath}/signout";
						}
					},
					"searching": false,
					"pageLength": 8,
					"bLengthChange": false,
					"order" : [ [ 0, "asc" ] ],
					destroy : true,
					"columns" : [{"data" : "sequence", "width": "18%"},
						{"data" : "name", "width": "70%"},
						{"render" : function(data, type, full, meta) {
							return '<button class="btn btn-sm btn-info" ng-click="getModifierItemsList(' + full.id + ', \'' + full.name + '\')">Details</button>';
						}, "width": "12%"}],
					"createdRow": function ( row, data, index ) {
						$compile(row)($scope);
					},
					"initComplete": function( settings, json ) {
						$('#itemName').html(itemName);
							
						$('#itemDetailsCarousel').carousel(2);
						$('#itemDetailsModal').modal("show");
						
						if (callingPage == -1) {
							$('#back2').hide();
						} else {
							$('#back2').show();
						}
					 }
				});
			} else if (itemType == "1") {
				var table = $('#datatable_comboItemTiersList').DataTable({
					"ajax" : {
						"url" : "${pageContext.request.contextPath}/rc/menu/get_tiers/"+itemId+"/"+itemBackendId,
						"error" : function() {
							alert("Session TIME OUT");
							window.location.href = "${pageContext.request.contextPath}/signout";
						}
					},
					"searching": false,
					"pageLength": 8,
					"bLengthChange": false,
					"order" : [ [ 0, "asc" ] ],
					destroy : true,
					"columns" : [{"data" : "sequence", "width": "15%"}, 
						{"data" : "name", "width": "58%"}, 
						{"data" : "quantity", "width": "15%"},
						{"render" : function(data, type, full, meta) {
							return '<button class="btn btn-sm btn-info" ng-click="getComboItemTierItemsList(' + full.id + ', \'' + full.name + '\')">Details</button>';
						}, "width": "12%"}],
					"createdRow": function ( row, data, index ) {
						$compile(row)($scope);
					},
					"initComplete": function( settings, json ) {
						$('#comboItemName').html(itemName);
						
						$('#itemDetailsCarousel').carousel(3);
						$('#itemDetailsModal').modal("show");
					}
				});
			}
		}
		
		$scope.getComboItemTierItemsList = function(tierId, tierName) {
			var table = $('#datatable_comboItemTierItemsList').DataTable({
				"ajax" : {
					"url" : "${pageContext.request.contextPath}/rc/menu/get_tier_item_details/"+tierId,
					"error" : function() {
						alert("Session TIME OUT");
						window.location.href = "${pageContext.request.contextPath}/signout";
					}
				},
				"searching": false,
				"pageLength": 8,
				"bLengthChange": false,
				"ordering" : false,
				destroy : true,
				"columns" : [{"data" : "backendId", "width": "15%"}, 
					{"data" : "name", "width": "50%"},
					{"render" : function(data, type, full, meta) {
						return '<button class="btn btn-sm btn-info" ng-click="getItemDetails('+ full.id + ', \'' + full.backendId + '\', 4)">Details</button>' +
						'<button class="btn btn-sm btn-info green-button" style="background-color: #00FA9A; border-color: #00FA9A; margin-left: 7px;" ng-click="redirectItemCarousel(0, \'' + full.id + '\', \'' + full.backendId + '\', \'' + full.name + '\')">Modifier Groups</button>';
					}, "width": "35%"}],
				"createdRow": function ( row, data, index ) {
					$compile(row)($scope);
				},
				"initComplete": function( settings, json ) {
					$('#comboItemTierName').html(tierName);
					
					$('#itemDetailsCarousel').carousel(4);
					$('#itemDetailsModal').modal("show");
				}
			});
		}
		
		$scope.backCarousel = function() {
			$('#itemDetailsCarousel').carousel($scope.previousPage);
		}
	});
</script>