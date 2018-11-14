<script>
	app.controller('Show_Takeaway_Order_CTRL', function ($scope, $http, $timeout, $location) {

		var vm = this;
		$scope.staffName = "";
		$scope.groupList = {};
		$scope.itemList = {};
		$scope.orderItemList = [];
		$scope.takeAwayTotalPrice = 0.00;
		
		$scope.isRemoveAvailable = true;
		$scope.isPaymentAvailable = true;
		
		$scope.getItemGroup = function () {
			$http.get("${pageContext.request.contextPath}/memberapi/show_sales/get_group_list")
				.then(
					function (response) {
						$scope.groupList = response.data;
						$scope.insideGroupStatus = 0;	
						takeAwayDatatableDisplay();
					},
					function (response) {
						alert("Error Occured While Displaying Groups");
						$(location)
							.attr('href',
								'${pageContext.request.contextPath}/ecpos/#!sales');
					});
		}

		$scope.getGroupItem = function (groupId) {
			$http.get("${pageContext.request.contextPath}/memberapi/show_sales/get_group_items/" + groupId)
				.then(
					function (response) {
						$scope.itemList = response.data;
						console.log(response.data);
						$scope.insideGroupStatus = 1;
					},
					function (response) {
						alert("Error Occured While Displaying Items");
						$(location)
							.attr('href',
								'${pageContext.request.contextPath}/ecpos/#!sales');
					});
		}



		/* 	$scope.removeItemFromTakeAwayCheck = function(){
				$scope.orderItemList.slice(itemId);
			} */

		//Data Table

		var takeAwayDatatable;

		function takeAwayDatatableDisplay() {
			takeAwayDatatable = $('#takeaway_datatable').DataTable({
				"responsive": true,
				"scrollY": '25vh',
				"paging": false,
				"destroy": true,
				columnDefs: [{
					"data": null,
					"defaultContent": '',
					"className": 'select-checkbox',
					"orderable": false,
					"searchable": false,
					"targets": 0
				}, {
					"data": "itemid",
					"visible": false,
					"targets": 1
				}, {
					"data": "itemcode",
					"defaultContent": "#",
					"targets": 2
				}, {
					"data": "itemname",
					"targets": 3
				},
				{
					"data": "itemprice",
					"className": 'text-right',
					"render":
						function (data, type, full) {
							return parseFloat(data).toFixed(2);
						},
					"targets": 4
				}
				],
				select: {
					style: 'single'
				},
				order: [[1, 'asc']],
				"initComplete": function (settings, json) {
	
				}
			});

			takeAwayDatatable.on('select deselect', function (e, dt, type, indexes) {
				if (type === 'row') {
					var data = takeAwayDatatable.rows({ selected: true }).data();

					if (data.count() > 0) {
						$scope.isRemoveAvailable = false;
						$scope.$apply();
					}
					else if (data.count() === 0) {
						$scope.isRemoveAvailable = true;
						$scope.$apply();
					}

				}

			});
		}

		//Success
		$scope.addItemIntoTakeAwayCheck = function (itemId, itemCode, itemName, itemPrice) {
			takeAwayDatatable.row.add({
				"itemid": itemId,
				"itemcode": itemCode,
				"itemname": itemName,
				"itemprice": itemPrice
			}).draw();
			
			if( takeAwayDatatable.rows().data().count()> 0){
				$scope.isPaymentAvailable = false;
			}	
			$scope.takeAwayTotalPrice += parseFloat(itemPrice);
		}

		//Success
		$scope.removeItemFromTakeAwayCheck = function () {
			var reduceItemPrice = takeAwayDatatable.row({ selected: true }).data();
				takeAwayDatatable.row({ selected: true }).remove().draw(true);
				
				if( takeAwayDatatable.rows().data().count()=== 0){
					$scope.isPaymentAvailable = true;
					$scope.isRemoveAvailable = true;
				} 
							
			$scope.takeAwayTotalPrice -= parseFloat(reduceItemPrice.itemprice);	
		}

		$scope.payTakeAwayOrder = function () {
			var rowsData = takeAwayDatatable.rows().data().toArray(); //Array of rowsData including dt metadata
			var itemHolder = [];

			//Filter the data out
			for (var i = 0; i < rowsData.length; i++) {
				itemHolder.push({
					"itemid": rowsData[i].itemid,
					"itemcode": rowsData[i].itemcode,
					"itemname": rowsData[i].itemname,
					"itemprice": rowsData[i].itemprice
				});
			}
			console.log(itemHolder);

			//Success
			$http.get("${pageContext.request.contextPath}/memberapi/show_sales/get_staff_name")
				.then(function (response) {

					createTakeAwayCheck(response.data.staff_name, itemHolder)
				},
					function (response) {
						alert("Error Occured While Obtaining Staff Name");
						$(location).attr('href', '${pageContext.request.contextPath}/member');
					});

		}

		function printKitchenReceipt(kitchenReceiptPrinting, checkNumber) {
			var jsonData = JSON.stringify({
				'checkNo': checkNumber,
				'kitchenReceipt': kitchenReceiptPrinting
			});

			$http.post("${pageContext.request.contextPath}/printerapi/printkitchenreceipt", jsonData)
				.then(function (response) {
					if (response.status === 200) {
						console.log("Success Print");
					}
				});
		}

		function createTakeAwayCheck(staffName, itemList) {
			var jsonData = JSON.stringify({
				'takeAwayItemList': itemList,
				'staffName': staffName
			});

			$http.post("${pageContext.request.contextPath}/memberapi/show_sales/takeaway", jsonData)
				.then(function (response) {
					if (response.status === 200) {
						var data = "/payment/" + response.data.checkNumber; //return checkNum from generated
						console.log(data);				
						$location.path(data);  
						printKitchenReceipt(response.data.kitchenReceiptPrinting, response.data.checkNumber);
					}
				},
					function (response) {
						alert("Error Occured When Redirecting to Payment");
						$(location).attr('href', '${pageContext.request.contextPath}/ecpos/#!sales');
					});
		}





	});
</script>