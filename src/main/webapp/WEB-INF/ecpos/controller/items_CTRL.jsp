<script>
	app.controller('items_CTRL', function($scope, $http, $window, $routeParams, $location, $compile) {
		$scope.itemType = "";
		
		$scope.initiation = function() {
			$scope.getDataTable("0");
		}
		
		$scope.getDataTable = function(itemType) {
			$scope.itemType = 1;

			if ($scope.itemType == "0" || $scope.itemType == "1") {
				$scope.getItemList();
			} else if ($scope.itemType == "2") {
				$scope.getModifierGroupList();
			}
		}
			
		$scope.getItemList = function() {
			var table = $('#datatable_item').DataTable({
				"ajax" : {
					"url" : "${pageContext.request.contextPath}/rc/menu/get_menu_items_by_item_type/" + $scope.itemType,
					"error" : function() {
						alert("Item list failed to display");
						window.location.href = "${pageContext.request.contextPath}/ecpos";
					}
				},
				"order" : [ [ 0, "asc" ] ],
				"destroy" : true,
				"columns" : [{"data" : "id", "width": "5%"}, 
					{"data" : "backendId"}, 
					{"data" : "name"},
					{"render" : function(data, type, full, meta) {
						var itemIdBackendIdName = full.id + "!!" + full.backendId + "!!" +full.name;
						
						var buttonColor = "";
						var buttonValue;
						var itemCarouselPage;
						if ($scope.itemType == "0") {
							buttonValue = "Modifier Group";
							itemCarouselPage = 3;
						} else if ($scope.itemType == "1") {
							buttonColor = 'style="background-color: #00FA9A; border-color: #00FA9A;"';
							buttonValue = "Combo Detail";
							itemCarouselPage = 1;
						}
						
						return '<button class="btn btn-info"' + buttonColor + 'ng-click="redirectItemCarousel('+ itemCarouselPage + ', \'' + itemIdBackendIdName + '\')">' + buttonValue + '</button>';
					}}],
				"createdRow": function ( row, data, index ) {
					$compile(row)($scope);
				}
			});
			console.log(table.page.info().recordsTotal);
			$('#datatable_item tbody').off('click', 'tr td:nth-child(-n+3)');
			$('#datatable_item tbody').on('click', 'tr td:nth-child(-n+3)', function() {
				$("#itemDetailModal").modal("show");
				console.log(table.row(this).data());
				$("#itemDetailModalName").html(table.row(this).data().name);
			});
		}
		
		$scope.getModifierGroupList = function() {
			var table = $('#datatable_modifierGroup').DataTable({
				"ajax" : {
					"url" : "${pageContext.request.contextPath}/rc/menu/get_modifier_group/",
					"error" : function() {
						alert("Modifier group list failed to display");
						window.location.href = "${pageContext.request.contextPath}/ecpos";
					}
				},
				"order" : [ [ 0, "asc" ] ],
				"destroy" : true,
				"columns" : [{"data" : "id", "width": "5%"},
					{"data" : "name"}]
			});
			
			table.on( 'order.dt search.dt', function () {
		        table.column(0, {search:'applied', order:'applied'}).nodes().each( function (cell, i) {
		            cell.innerHTML = i+1;
		        } );
		    }).draw();
			
			$('#datatable_modifierGroup tbody').off('click', 'tr');
			$('#datatable_modifierGroup tbody').on('click', 'tr', function() {
				$("#itemDetailModal").modal("show");
				$('#itemName').html(table.row(this).data().name);
				$("#itemImage").attr("src", "${pageContext.request.contextPath}" + table.row(this).data().imagePath);
				$('#itemDescription').html(table.row(this).data().description);
			});
		}
		
		$scope.redirectItemCarousel = function(itemCarouselPage, itemIdBackendIdName) {
			$scope.item = {};

			var itemIdBackendIdNameSplit = itemIdBackendIdName.split("!!");
			$scope.item.id = itemIdBackendIdNameSplit[0];
			$scope.item.backendId = itemIdBackendIdNameSplit[1];
			$scope.item.name = itemIdBackendIdNameSplit[2];
			
			
			
			$('#itemCarousel').carousel(itemCarouselPage);
		}
	});
</script>