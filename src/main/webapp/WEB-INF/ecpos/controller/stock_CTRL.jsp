<script>
	app.controller('stock_CTRL', function($scope, $http, $window, $routeParams, $location, $compile) {
		$scope.initiation = function() {
			$http.get("${pageContext.request.contextPath}/rc/configuration/session_checking")
			.then(function(response) {
				if (response.data.responseCode == "00") {
					$scope.showSalesPasswordModal();
				} else {
					// alert("Session TIME OUT");
					// window.location.href = "${pageContext.request.contextPath}/signout";
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
				}
			},
			function(response) {
				// alert("Session TIME OUT");
				// window.location.href = "${pageContext.request.contextPath}/signout";
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
		
		$scope.getItemStockList = function() {
			if ($scope.dateStart == null || $scope.dateStart == 'undefined') {
				$scope.dateStart = new Date();
			}
			$scope.getDropDownItemList();
			var dataObj = {
					"startDate" : $scope.dateStart
			};
			
			var table = $('#datatable_stock').DataTable({
				"ajax" : {
					"url" : "${pageContext.request.contextPath}/rc/menu/get_item_stock_list",
					"type" : "post",
					"data" : function (d) {
					      return JSON.stringify(dataObj);
				    },
				    "contentType" : "application/json; charset=utf-8",
					"dataType" : "json",
					"error" : function() {
						// alert("Session TIME OUT");
						// window.location.href = "${pageContext.request.contextPath}/signout";
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
					}
				},
				"searching": false,
				"pageLength": 8,
				"bLengthChange": false,
				"order" : [ [ 0, "asc" ] ],
				destroy : true,
				"columns" : [{"data" : "id"},
					{"data" : "menu_item_name"}, 
					{"data" : "new_value"},
					{"render" : function(data, type, full, meta) {
						return '<button class="btn btn-sm btn-primary" ng-click="getMenuItems('+ full.saleItem_id + ')">Update</button>';
					}, "width": "20%"}],
				/*rowCallback: function(row, data, index){
			    	$(row).find('td:eq(1)').css('color', 'blue');
			    	
			    	$(row).mouseenter (function() {
			    		$(row).find('td:eq(1)').css('text-decoration', 'underline');
		    		});
			    	
			    	$(row).mouseleave (function() {
			    		$(row).find('td:eq(1)').css('text-decoration', 'none');
		    		});
				},*/
				"createdRow": function ( row, data, index ) {
					$compile(row)($scope);
				}
			});
		}
		
		$scope.showSalesPasswordModal = function() {
			$('#salesPasswordModal').modal('show');
			$('#password').focus();
		}
		
		$scope.redirectToMainMenu = function() {
			window.location.href = "${pageContext.request.contextPath}/#!take_away_order";
			$('.modal-backdrop').remove();
		}
		
		$scope.showAddMenuItemModal = function() {
			$('#addMenuItemModal').modal('show');
		}
		
		$scope.resetItemStockModal = function() {
			$('#newValue').val('');
		}
		
		$scope.resetAddMenuItemModal = function() {
			$('#itemListDropDown').val('');
		}
		
		$scope.getDropDownItemList = function() {
			var jsonData = JSON.stringify({
					"startDate" : $scope.dateStart
				});
			
			$http.post("${pageContext.request.contextPath}/rc/menu/get_menu_item_list/", jsonData)
			.then(function(response) {
				$scope.dropDownItemList = response.data.menu_item_list;
			},
			function(response) {
				// alert("Session TIME OUT");
				// window.location.href = "${pageContext.request.contextPath}/signout";
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
		
		$scope.getSalesUpdateLog = function() {
			var jsonData = JSON.stringify({
					"startDate" : $scope.dateStart
				});
			
			$http.post("${pageContext.request.contextPath}/rc/menu/get_sales_update_log/", jsonData)
			.then(function(response) {
				$scope.logList = response.data.logs;
				$('#updateLogModal').modal('show');
			},
			function(response) {
				// alert("Session TIME OUT");
				// window.location.href = "${pageContext.request.contextPath}/signout";
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
		
		$scope.getMenuItems = function(saleItem_id) {
			var dataObj = {
				"saleItem_id" : saleItem_id,
			};
			
			$http.post("${pageContext.request.contextPath}/rc/menu/get_menu_item_detail/", dataObj)
			.then(function(response) {
				$scope.itemsDetail = response.data;
				$scope.saleItemId = response.data.saleItem_id;
				$('#itemStockModal').modal('show');
			},
			function(response) {
				// alert("Session TIME OUT");
				// window.location.href = "${pageContext.request.contextPath}/signout";
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
		
		$scope.addNewMenuItem = function () {
			var id = $scope.selectedItemDropDown;
			if (id != null && id != '') {
				var jsonData = JSON.stringify({
					"id" : id,
					"startDate" : $scope.dateStart
				});
			
				$http.post("${pageContext.request.contextPath}/rc/menu/add_new_item/", jsonData)
				.then(function(response) {
					if (response.data.response_code === "00") {
						Swal.fire(
							'Success!',
							response.data.response_message,
							'success'
						)
						$scope.getItemStockList();
					} else {
						Swal.fire(
							'Failed!',
							response.data.response_message,
							'failed'
						)
					}
					$("#addMenuItemModal").modal("hide");
				},
				function(response) {
					// alert("Session TIME OUT");
					// window.location.href = "${pageContext.request.contextPath}/signout";
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
			} else {
				Swal.fire(
					'Failed!',
					'Please select menu item',
					'failed'
				)
			}
		}
		
		$scope.updateItemStock = function(newValue) {
			Swal.fire({
				title: 'Are you sure?',
				text: "You can change this information again.",
				icon: 'warning',
				showCancelButton: true,
				confirmButtonColor: '#3085d6',
				cancelButtonColor: '#d33',
				confirmButtonText: 'Yes'
			}).then((result) => {
				if (result.value) {
					var jsonData = JSON.stringify({
						"saleItem_id" : $scope.saleItemId,
						"newValue" : newValue,
						"startDate" : $scope.dateStart
					});
				
					$http.post("${pageContext.request.contextPath}/rc/menu/update_item_stock/", jsonData)
					.then(function(response) {
						if (response.data.response_code === "00") {
							Swal.fire(
								'Success!',
								response.data.response_message,
								'success'
							)
							$scope.getItemStockList();
							$scope.resetItemStockModal();
						} else {
							Swal.fire(
								'Failed!',
								response.data.response_message,
								'failed'
							)
						}
						$("#itemStockModal").modal("hide");
					},
					function(response) {
						// alert("Session TIME OUT");
						// window.location.href = "${pageContext.request.contextPath}/signout";
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
			});
		}
		
		$scope.checkSalesPassword = function () {
			var jsonData = JSON.stringify({
				"data" : $('#password').val()
			});
			$http.post("${pageContext.request.contextPath}/rc/configuration/checkStockPassword", jsonData)
			.then(function(response) {
				if(response.data.response_code == '00'){
					$scope.dateStart = new Date();
					$scope.maxDate = new Date().toISOString().split("T")[0];
					$scope.getItemStockList();
					$('#salesPasswordModal').modal('hide');
					let timerInterval
					Swal.fire({
						title: 'Success!',
						text: response.data.response_message,
						icon: 'success',
						/*timer: 1000,
						timerProgressBar: true,
						didOpen: () => {
							Swal.showLoading()
							timerInterval = setInterval(() => {
								const content = Swal.getContent()
								if (content) {
									const b = content.querySelector('b')
									if (b) {
										b.textContent = Swal.getTimerLeft()
									}
								}
							}, 100)
						},
						willClose: () => {
							clearInterval(timerInterval)
						}*/
					}).then((result) => {
						if (result.dismiss === Swal.DismissReason.timer) {
							console.log('I was closed by the timer')
						}
					});
				}else{
					Swal.fire({
						title: 'Error',
						text: response.data.response_message,
						icon: 'error',
						showCancelButton: false,
						confirmButtonColor: '#3085d6',
						cancelButtonColor: '#d33',
						confirmButtonText: 'OK'
					}).then((result) => {
						$(".modal-backdrop.in").hide();
						window.location.href = "${pageContext.request.contextPath}/#!take_away_order";
					})
				}
			});
		}
	});
</script>