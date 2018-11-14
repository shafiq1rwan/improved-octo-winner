<script>

	app.controller('Show_checks_CTRL', function ($scope, $http, $routeParams, $window, $location, $route) {

		$scope.check_detail = {};
		$scope.list_of_group = {};
		$scope.inside_group_status = 0;

		var chk_num = $routeParams.check_no;

		$scope.selected_items_ttl = 0.00;

		//Important
		$scope.valid_btn_status = true;
		$scope.isPaymentAvailable = true;

		$scope.splitted_checklist_no = [];

		$scope.temp_item_holder = []; //Important to store

		var addTransactionFlag = 0; //Not Important

		//$scope.check_number = $routeParams.check_no;

		//Init the list when entered the detail
		$scope.getInitCheckNum = function () {
			$scope.get_check_details(chk_num); //important
			//$scope.getCheckDetails(chk_num);
			get_splitted_checklist(chk_num);
		}

		//Deal with localstorage
		localStorage.setItem("myChkNo", chk_num);

		//Important
		$scope.get_check_details = function (data) {
			addTransactionFlag = 0;
			$http.get("${pageContext.request.contextPath}/ecposmanagerapi/checks/getcheckdetail/" + data)
				.then(
					function (response) {
						$scope.check_detail = response.data;
						displayCheckDetailTableData($scope.check_detail.item_detail_array);
					},
					function (response) {
						alert("Session TIME OUT");
						$(location)
							.attr('href',
								'${pageContext.request.contextPath}/ecpos/#!sales');
					});
		}
		
		function add_open_item(open_item_price) {

			var check_num = chk_num;
			console.log("Add open item chk_item: " + check_num);
			console.log("Add open item price: " + open_item_price);

			var jsonData = JSON.stringify({
				"check_no": check_num,
				"price": open_item_price
			});

			$http.post("${pageContext.request.contextPath}/ecposmanagerapi/checks/addopenitem",
				jsonData)
				.then(
					function (response) {
						if (response.status === 200) {
							$scope.added_open_item = response.data;
							console.log("add_open_item response: " + $scope.added_open_item);

							$scope.temp_item_holder.push(response.data.generatedItemId);
							localStorage.setItem("kitchenItemDisplay", JSON.stringify($scope.temp_item_holder)); //store into array
							$scope.get_check_details(check_num);
						}
					},
					function (response) {
						alert("Error Occured While Adding Item");
						$(location)
							.attr('href',
								'${pageContext.request.contextPath}/ecpos/#!sales');
					});

		}

		
	/* ================================================================================  */
		
		//Important
		//Calculator operation, 99 mean add it to check detail and refresh.
		$scope.add_calc = function (id, n) {
			var amount = document.getElementById(id).innerHTML;

			if (n === 99) {
				console.log(amount);
				add_open_item(amount);
				//add_open_item_into_temp_check(amount);
				document.getElementById(id).innerHTML = "0.00";
				document.getElementById('hidden_' + id).value = "0.00";
			}
			else {
				if (n != 20 && n != -1) {
					amount = amount + n;
				}
				amount = amount.replace(",", "");

				var i = parseFloat(amount);
				if (n === -1) {
					i = i / 10;
				}
				else {
					if (amount.length < 10) {
						if (n === 20) {
							i = i * 100;
						}
						else {
							i = i * 10;
						}
					}
				}

				var temp = i.toFixed(3);
				temp = temp.substring(0, temp.length - 1);
				document.getElementById(id).innerHTML = temp;

				document.getElementById('hidden_' + id).value = temp;
			}
		}
		
		/* ================================================================================  */

		//Important
		//Show group name list
		$scope.show_group = function () {

			$http.get("${pageContext.request.contextPath}/memberapi/show_sales/get_group_list")
				.then(
					function (response) {
						$scope.list_of_group = response.data;
						$scope.inside_group_status = 0;
					},
					function (response) {
						alert("Error Occured While Displaying Groups");
						$(location)
							.attr('href',
								'${pageContext.request.contextPath}/ecpos/#!sales');
					});

		}

		//Important
		//Show group item list
		$scope.get_group_item = function (groupid) {

			$http.get("${pageContext.request.contextPath}/memberapi/show_sales/get_group_items/" + groupid)
				.then(
					function (response) {
						$scope.list_of_item = response.data;
						console.log(response.data);
						$scope.inside_group_status = 1;
					},
					function (response) {
						alert("Error Occured While Displaying Items");
						$(location)
							.attr('href',
								'${pageContext.request.contextPath}/ecpos/#!sales');
					});

		}
		
/* 		function defaultImageFallback(itemImage){
			angular.forEach();
			
			
		} */
		

		//Important => Relevant
		//Add new item by using 
		$scope.add_item_into_check = function (chk_seq, item_id, detail_type) {

			//var check_num = $scope.check_detail.checknumber;
			var check_num = chk_num;

/* 			console.log("c1");
			console.log(check_num);

			console.log("c2");
			console.log(chk_seq);

			console.log("c3");
			console.log(item_id); */

			var jsonData = JSON.stringify({
				"check_num": check_num,
				"chk_seq": chk_seq,
				"item_id": item_id,
				"detail_type": detail_type
			});

			$http.post("${pageContext.request.contextPath}/ecposmanagerapi/checks/additem", jsonData)
				.then(
					function (response) {
						if (response.status === 200) {
							$scope.temp_item_holder.push(response.data.generatedItemId);
							localStorage.setItem("kitchenItemDisplay", JSON.stringify($scope.temp_item_holder)); //store into array
							$scope.get_check_details(check_num);
						}
					},
					function (response) {
						alert("Error Occured While Adding Item into Check");
						$(location)
							.attr('href',
								'${pageContext.request.contextPath}/ecpos/#!sales');
					});


		}

// -------------------------------````````````[Experiment Feature]``````````````````

		//if that item ady have open item in the table list
		var open_holder_name = '';

		//important after
		function findExistingOpenItemName(itemDetailArray) {
			var nameTempHolder = [];
			for (var i = 0; i < itemDetailArray.length; i++) {
				if (itemDetailArray[i].itemname.startsWith("OpenItem")) {
					nameTempHolder.push(itemDetailArray[i].itemname);
				}
			}
			//console.log(nameTempHolder);
			if (nameTempHolder.length > 0) {
				open_holder_name = nameTempHolder[nameTempHolder.length - 1];
			}
		}

		// $scope.add_item_into_check_temp = function (item_id, item_code, name, item_price) {
		// 	add_new_item_into_check(item_id, item_code, name, item_price, 'S');
		// }

		function add_open_item_into_temp_check(open_item_price) {

			console.log(open_item_price);

			if (open_holder_name == '') {
				open_holder_name = 'OpenItem_001';
				console.log(open_holder_name);
			}
			else {
				var open_item_splitted_str = open_holder_name.split('_');
				var open_item_number = parseInt(open_item_splitted_str[1]) + 1;
				open_holder_name = open_item_splitted_str[0] + '_' + add_zero_pad(open_item_number);
			}

			add_new_item_into_check(0, '#', open_holder_name, open_item_price, 'S');
		}

		$scope.check_payment = function () {

			var selectItems = check_detail_datatable.rows({
				selected: true
			}).data().toArray();

			var jsonData = JSON.stringify({




			});


			$http.post("${pageContext.request.contextPath}///", jsonData)
				.then(
					function (response) {

					},
					function (response) {

					});
		}

		$scope.add_payment_into_check = function (payment_id, payment_name, payment_amount) {
			add_new_item_into_check(payment_id, '', payment_name, payment_amount, 'T');
		}



		//done
		function add_new_item_into_check(item_id, item_code, name, item_price, detail_type) {

			var check_item_data = {
				"itemid": 0,
				"itemcode": item_code,
				"itemname": name,
				"itemprice": item_price,
				"detailtype": detail_type,
				"detailsequence": -1,
				"itemvoidstatus": -1,
				"itemmenudefid": item_id
			};

			check_detail_datatable.row.add({
				"itemid": 0,
				"itemcode": item_code,
				"itemname": name,
				"itemprice": item_price,
				"detailsequence": -1,
				"itemvoidstatus": -1,
				"itemmenudefid": item_id
			}
			).draw();

			$scope.temp_item_holder.push(check_item_data);
			// console.log("My Array of items");
			// console.log($scope.temp_item_holder);
			$scope.calculated_subttl = calculate_subtotal($scope.temp_item_holder);
		}

		//Not Relevant
		function add_zero_pad(num) {
			var num_zero_pad = num + '';
			var count = num_zero_pad.length;

			if (count === 1) {
				count = count + 2;
			}
			else {
				count++;
			}

			while (num_zero_pad.length < count) {
				num_zero_pad = "0" + num_zero_pad;
			}
			return num_zero_pad;
		}

		//Not Relevant
		function calculate_subtotal(item_array) {
			var total = 0;
			for (var i = 0; i < item_array.length; i++) {

				if (item_array[i].detailtype === 'S' && item_array[i].itemvoidstatus != 1) {
					total += parseFloat(item_array[i].itemprice);
				} else if (item_array[i].detailtype === 'T') {
					total -= parseFloat(item_array[i].itemprice);

					if (total < 0)
						total = Math.abs(total);
				}
			}
			console.log("total");
			console.log(total);
			return total;
		}

		// $scope.remove_selected_item_from_table = function () {

		// 	var selectItems = check_detail_datatable.rows({
		// 		selected: true
		// 	}).data().toArray();

		// 	$scope.temp_item_holder.reverse();
		// 	selectItems.reverse();

		// 	//Reverse to prevent out of bound because of array deletion
		// 	for (var i = selectItems.length - 1; i >= 0; i--) {
		// 		for (var j = $scope.temp_item_holder.length - 1; j >= 0; j--) {
		// 			//If the item got voidableoptions 
		// 			// if ($scope.temp_item_holder[j].itemvoidstatus >= 0) {
		// 			if ($scope.temp_item_holder[j].itemid == selectItems[i].itemid) {
		// 				selectItems.splice(i, 1);
		// 				//$scope.temp_item_holder.splice(j, 1);
		// 				$scope.temp_item_holder[j].itemvoidstatus = 1;
		// 				break;
		// 			}
		// 			// }
		// 			// else {
		// 			// 	if ($scope.temp_item_holder[j].itemid == selectItems[i].itemid) {
		// 			// 		selectItems.splice(i, 1);
		// 			// 		//$scope.temp_item_holder.splice(j, 1);
		// 			// 		$scope.temp_item_holder[j].itemvoidstatus = 1;

		// 			// 		break;
		// 			// 	}
		// 			// }
		// 		}
		// 	}

		// 	$scope.temp_item_holder.reverse();

		// 	$scope.calculated_subttl = calculate_subtotal($scope.temp_item_holder);

		// 	var rows = check_detail_datatable
		// 		.rows('.selected')
		// 		.remove()
		// 		.draw();

		// }

		// function transactionDeduction(item){
		// 	var total = 0;
		// 	if(item === 'T'){
		// 		total -= parseFloat(item_array[i].itemprice);


		// 	}

		// 	return total;
		// }



/* 		$scope.show_payment_options = function () {
			$http.get("${pageContext.request.contextPath}/ecposmanagerapi/checks/tender")
				.then(
					function (response) {
						$scope.payment_options = response.data;
						console.log($scope.payment_options);

						$('#payment_option_modal').modal('show');


					},
					function (response) {
						alert("Cannot Find Any Payment Options");
					});

		} */



		/* $scope.make_payment = function (amount, charge) {
			//Customer ady paid
			if (parseFloat(amount) >= 0.00) {
				$http.post("${pageContext.request.contextPath}/ecposmanagerapi/checks/update")
					.then(
						function (response) {
							alert("Success");
						},
						function (response) {
							alert("Cannot Find Any Payment Options");
						});
			}
			else {
				//trigger card tender payment
				var cahrgeData = charge;

				$http.get("${pageContext.request.contextPath}/ecposmanagerapi/checks/tender")
					.then(
						function (response) {
							$scope.payment_options = response.data;
							console.log($scope.payment_options);

							$('#payment_option_modal').modal('show');
						},
						function (response) {
							alert("Cannot Find Any Card Payment Options");
						});


			}
		} */


		var itemPriceHolderBalance = 0.00; //Hold the deduct amt 
		var transactionHolderBalance = 0.00;

		$scope.payment = function () {
			//  var all_row_data = check_detail_datatable
			//     .rows()
			//     .data().toArray();

			//  if(itemPriceHolderBalance >=0.00){
			// 	itemPriceHolderBalance = 0.00;
			// 	for(var i=0;i<all_row_data.length;i++){			 
			// 		itemPriceHolderBalance += parseFloat(all_row_data[i].itemprice);
			//  	}
			//  }


		}

	/* 	$scope.createSplitCheck = function () {
			var selectItems = check_detail_datatable.rows({
				selected: true
			}).data().toArray();

			var jsonData = JSON.stringify({
				"chk_num": chk_num,
				"selected_detail_items": selectItems
			});

			$http.post("${pageContext.request.contextPath}/ecposmanagerapi/checks/splitCheck",
				jsonData)
				.then(
					function (response) {
						if (response.data.response_code === "00") {
							//$scope.get_check_details(chk_num);
							//redirect to sub-checks
							$location.path('/checks/' + response.data.check_num).replace();								
						}
					},
					function (response) {
						alert("Error Occured While Creating Split Check!");
						$(location)
							.attr('href',
								'${pageContext.request.contextPath}/member');
					});

		} */







		// `````````````````````````````````````````````````````



		//Important
		$scope.remove_selected_check_items = function () {

			var array_of_selected_items = GetSelectedDetailItem();

			//console.log(array_of_selected_items);

			//Remove the dedicated items
			removeItemTempHolderItem(array_of_selected_items);
			console.log($scope.temp_item_holder);

			var jsonData = JSON.stringify(array_of_selected_items);

			$http.post("${pageContext.request.contextPath}/ecposmanagerapi/checks/void", jsonData)
				.then(
					function (response) {
						if (response.status === 200) {
							$scope.get_check_details(chk_num);
							$scope.valid_btn_status = true;
						}

					},
					function (response) {
						alert("Error Occured While Removing Check Item");
						$(location)
							.attr('href',
								'${pageContext.request.contextPath}/member');
					});
		}

		//Important
		function removeItemTempHolderItem(array_of_selected_items) {

			$scope.temp_item_holder.reverse();
			array_of_selected_items.reverse();

			//Reverse to prevent out of bound because of array deletion
			for (var i = array_of_selected_items.length - 1; i >= 0; i--) {
				for (var j = $scope.temp_item_holder.length - 1; j >= 0; j--) {
					if ($scope.temp_item_holder[j].itemid == array_of_selected_items[i].itemid
						&& array_of_selected_items[i].detailtype != 'T') {
						$scope.temp_item_holder.splice(j, 1);
						localStorage.setItem("kitchenItemDisplay", JSON.stringify($scope.temp_item_holder)); //store into array
						break;
					}
				}
			}

			$scope.temp_item_holder.reverse();
		}

		//Important
/* 		$scope.get_selected_detail_items_amt = function () {

			var selected_ttl = 0.00;

			var selectItems = check_detail_datatable.rows({
				selected: true
			}).data();

			//Seperate the data from other non-related information
			for (var i = 0; i < selectItems.count(); i++) {
				selected_ttl += parseFloat(selectItems[i].itemprice);
			}

			console.log(selected_ttl);
			$scope.selected_items_ttl = selected_ttl;
		} */
		
		
		//Important => Related
		$scope.goToPayment = function(checkNumber,checkSequence){
			
			var jsonData = JSON.stringify({
				'chk_num': checkNumber,
				'chk_seq': checkSequence,
				'store_balance_item_list': $scope.temp_item_holder
			});

			$http.post("${pageContext.request.contextPath}/ecposmanagerapi/checks/storebalance", jsonData)
				.then(
					function (response) {
						if (response.status === 200) {			
							localStorage.clear();
							$location.path("/payment/" + checkNumber).replace();		
						}
					});
		}
		
/*============================================ Not Relevant[START] ========================================*/

		//Method for inputting calc input into How much to pay..
		$scope.cash_payment_calc = function (id, n) {

			var amount = document.getElementById(id).innerHTML;

			if (n === 99) {
				console.log(amount);
				addTransactionFlag = 1;
				calc_balance($scope.selected_items_ttl, amount); //important dont del

				//calc_balance(amount);


				//$scope.add_payment_into_check(1, 'CASH', amount);

				//document.getElementById('hidden_keyin_amt').value = "0.00";
			}
			else {
				if (n != 20 && n != -1) {
					amount = amount + n;
				}
				amount = amount.replace(",", "");

				var i = parseFloat(amount);

				if (n === -1) {
					i = i / 10;
				}
				else {
					if (amount.length < 10) {
						if (n === 20) {
							i = i * 100;
						}
						else {
							i = i * 10;
						}
					}
				}


				var temp = i.toFixed(3);
				var tempHolder = temp.substring(0, temp.length - 1);
				document.getElementById(id).innerHTML = tempHolder;

				calc_balance($scope.selected_items_ttl, tempHolder);

				//document.getElementById('hidden_keyin_amt').value = temp;
			}

		}

/* 		$scope.reset_keyin_amt_when_zero = function (amount) {
			console.log("T");
			//var amount = document.getElementById(id).innerHTML;
			console.log("GG");
			console.log(parseFloat(amount));
			if (parseFloat(amount) === 0.00) {
				$scope.reset_balance_amt();
			}
		} */


		/* 	$scope.return_to_group_list = function(){
				//reset it so we can get in and out
				$scope.inside_group_status = 0;	
			} */

		//Calculate balance by minusing the amt to pay with key-in amt
		function calc_balance(amt_to_pay, key_in_amt) {

			var amt_diff = key_in_amt - amt_to_pay; //important
			var result = amt_diff.toFixed(2);
			console.log(result);

			//add transcation items
			if (addTransactionFlag === 1) {
				addTranxIntoCheck(key_in_amt, chk_num, 1);
			}

			//var result = key_in_amt - $scope.calculated_subttl;

			if (result >= 0.00) {
				$('#payment_balance_amt').css({ 'color': 'green' });
				// $('#paying_cash_btn').prop('disabled', false);
			} else {
				$('#payment_balance_amt').css({ 'color': 'red' });
				// $('#paying_cash_btn').prop('disabled', true);
			}

			//$scope.selected_items_ttl = $scope.selected_items_ttl-key_in_amt;

			//$('#calc_keyin_amt').html("0.00"); // Extra, remove if nessesary

			$('#payment_balance_amt').html(result);

			if (key_in_amt == 0.00) {
				$scope.reset_balance_amt(); //important
			}

			//var balance_display = document.getElementById('payment_balance_amt').innerHTML = result;

		}

		//Important
/* 		function addTranxIntoCheck(amount, checkNo, tenderId) {

			var jsonData = JSON.stringify({
				'chkNo': checkNo,
				'tenderId': tenderId,
				'tranxAmt': amount,
				'detailType': 'T'
			});

			$http.post("${pageContext.request.contextPath}/ecposmanagerapi/checks/addtransaction", jsonData)
				.then(
					function (response) {
						if (response.status === 200) {
							$scope.get_check_details(chk_num);
						}
					},
					function (response) {
						alert("Error Occured While Adding Transaction Item");
						$(location)
							.attr('href',
								'${pageContext.request.contextPath}/member');
					});
		} */



		$scope.reset_balance_amt = function () {
			$('#payment_balance_amt').html("0.00").css({ 'color': 'black' });
			$('#calc_keyin_amt').html("0.00");


			
			//$('#paying_cash_btn').prop('disabled', true);
		}

		//Make Cash Payment
		$scope.make_cash_payment = function () {
			var key_in_amt = $('#calc_keyin_amt').html();
			var payment_balance_amt = $('#payment_balance_amt').html();
			var selectItemIds = check_detail_datatable.rows().data().toArray();

			// updateDetailItem();
			// makeCashPayment(key_in_amt, payment_balance_amt);

			var jsonData = JSON.stringify({
				"chk_num": chk_num,
				"chk_seq": $scope.check_detail.chksequence,
				"selected_items_ttl": $scope.selected_items_ttl,
				"key_in_amt": key_in_amt,
				"payment_balance_amt": payment_balance_amt,
				"payment_type": 'Cash',
				"selected_detail_items": selectItemIds
			});

			$http.post("${pageContext.request.contextPath}/ecposmanagerapi/checks/payment", jsonData)
				.then(
					function (response) {
						console.log(response.data);
						if (response.data.response_code === "00") {

							$("#cash_payment_calculator_modal")
								.modal("hide");
							$(".modal-backdrop")
								.remove();

							$scope.reset_balance_amt();

							$scope.remaining_balance_amt = payment_balance_amt;

							$scope.get_check_details(chk_num);

							// if (response.data.detail_counts === 0) {

							$("#print_receipt_modal")
								.modal("show");

							// }
						}
						else {
							alert("Invalid Request While Making Cash Payment");
							$(location)
								.attr('href',
									'${pageContext.request.contextPath}/ecpos/#!sales');
						}
					},
					function (response) {
						alert("Error Occured While Making Cash Payment");
						$(location)
							.attr('href',
								'${pageContext.request.contextPath}/ecpos/#!sales');
					});



		}

		// function updateDetailItem() {
		// 	var jsonData = JSON.stringify({
		// 		'chk_num': chk_num,
		// 		'update_type': 1,
		// 		'ordered_item': $scope.temp_item_holder
		// 	});

		// 	$http.post("${pageContext.request.contextPath}/ecposmanagerapi/checks/update", jsonData)
		// 		.then(
		// 			function (response) {
		// 				console.log(response.status);
		// 			},
		// 			function (response) {
		// 				alert('Cannot perform checks and detail update');
		// 			});
		// }

		// function makeCashPayment(keyInAmount, paymentBalanceAmount) {

		// 	var jsonData = JSON.stringify({
		// 		'chk_num': chk_num,
		// 		'payment_type': 'Cash',
		// 		'selected_items_ttl': parseFloat($scope.calculated_subttl).toFixed(2)
		// 	});

		// 	console.log(jsonData);

		// 	$http.post("${pageContext.request.contextPath}/ecposmanagerapi/checks/payment/cash", jsonData)
		// 		.then(
		// 			function (response) {
		// 				console.log(response);
		// 				if (response.status === 200) {

		// 					$("#cash_payment_calculator_modal")
		// 						.modal("hide");
		// 					$(".modal-backdrop")
		// 						.remove();

		// 					$scope.remaining_balance_amt = paymentBalanceAmount;
		// 					$scope.reset_balance_amt();
		// 					$scope.get_check_details(chk_num);
		// 					$("#print_receipt_modal")
		// 						.modal("show");
		// 				}
		// 				else {
		// 					alert("Invalid Request While Making Cash Payment");
		// 					$(location)
		// 						.attr('href',
		// 							'${pageContext.request.contextPath}/member/#!sales');
		// 				}
		// 			},
		// 			function (response) {
		// 				alert("Error Occured While Making Cash Payment");
		// 				$(location)
		// 					.attr('href',
		// 						'${pageContext.request.contextPath}/member/#!sales');
		// 			});
		// }
		
		$scope.redirect_to_table_list = function () {
			$("#print_receipt_modal")
				.modal("hide");
			$(".modal-backdrop")
				.remove();

			$scope.remaining_balance_amt = 0.00;

			$(location)
				.attr('href',
					'${pageContext.request.contextPath}/member/#!sales');
		}

		$scope.match_amount = function (payment_amt) {
			$('#calc_keyin_amt').html(payment_amt);
			addTransactionFlag = 1;
			calc_balance(payment_amt, $('#calc_keyin_amt').html());
			//calc_balance(payment_amt);
			//$scope.add_payment_into_check(1, 'CASH', payment_amt);
		}
		
		// $scope.storeBalance = function () {
		// 	//add a flag to indicate this is store procedure or payment

		// 	console.log("B4 Send");
		// 	console.log($scope.temp_item_holder);

		// 	var jsonData = JSON.stringify({
		// 		'chk_num': chk_num,
		// 		'update_type': 2,
		// 		'ordered_item': $scope.temp_item_holder
		// 	});

		// 	$http.post("${pageContext.request.contextPath}/ecposmanagerapi/checks/update", jsonData)
		// 		.then(
		// 			function (response) {
		// 				console.log(response.status);
		// 				$(location)
		// 					.attr('href',
		// 						'${pageContext.request.contextPath}/member/#!/sales');
		// 			},
		// 			function (response) {
		// 				alert('Cannot perform checks and detail update');
		// 			});

		// }
		
		/* 		$scope.print_receipt = function (chksequence) {

			var chk_seq = chksequence.toString();

			$http.post("${pageContext.request.contextPath}/printerapi/print_receipt",
				chk_seq)
				.then(
					function (response) {
						if (response.status == 200) {
							console.log(response.data);
							localStorage.clear();
							$scope.redirect_to_table_list();
						}
						else {
							alert("Error Occured While Print Receipt!");
							localStorage.clear();
							$scope.redirect_to_table_list();
						}
					},
					function (response) {
						alert("Error Occured While Print Receipt!");
						localStorage.clear();
						$(location)
							.attr('href',
								'${pageContext.request.contextPath}/member');
					});

		} */
		
		/*============================ Not Relevant[END] ===================================*/

		//Important => Relevant		
		function GetSelectedDetailItem() {
			var selectItems = check_detail_datatable.rows({
				selected: true
			}).data();

			// var selectItems = check_detail_datatable
			// 	.rows()
			// 	.data();

			var item_holder = [];

			//Seperate the data from other non-related information
			for (var i = 0; i < selectItems.count(); i++) {
				item_holder.push(selectItems[i].itemid);
			}

			return item_holder;
		}

		//Important => Relevant	
		var check_detail_datatable;

		//Important => Relevant	
		var displayCheckDetailTableData = function (item_detail_array) {

			console.log(item_detail_array);

			if (item_detail_array)

				check_detail_datatable = $('#check_detail_datatable').DataTable({
					"responsive": true,
					"scrollY": '25vh',
					/* 			"scrollCollapse" : true, */
					"paging": false,
					"data": item_detail_array,
					"destroy": true,
					"dom": 'Bfrtip',
					buttons: [
						'selectAll',
						'selectNone'
					],
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
					},
					{
						"data": "itemmenudefid",
						"visible": false,
						"targets": 5
					},
					{
						"data": "detailtype",
						"visible": false,
						"targets": 6
					}
					],
					select: {
						style: 'multi'
					},
					order: [[1, 'asc']],
					"initComplete": function (settings, json) {

					}
				});

			check_detail_datatable.on('select deselect', function (e, dt, type, indexes) {
				if (type === 'row') {
					var data = check_detail_datatable.rows({ selected: true }).data();

					if (data.count() > 0) {
						$scope.valid_btn_status = false;
						$scope.$apply();
					}
					else if (data.count() === 0) {
						$scope.valid_btn_status = true;
						$scope.$apply();
					}

				}

			});

			if(check_detail_datatable.rows().data().count() >0){
				$scope.isPaymentAvailable = false;
			} else {
				$scope.isPaymentAvailable = true;
			}
			
			
		}

		//Important => Relevant	
		function get_splitted_checklist(check_num) {
			$http.get("${pageContext.request.contextPath}/memberapi/show_sales/get_splitted_checklist/" +
				check_num)
				.then(
					function (response) {
						//console.log(response.data);
						$scope.splitted_checklist_no = response.data;
					},
					function (response) {
						alert("Error Occured While Retrieving Split Check!");
						$(location)
							.attr('href',
								'${pageContext.request.contextPath}/ecpos/#!sales');
					});

		}

		//Important
		//Modified Needed
		$scope.createSplitCheck = function () {

			var array_of_selected_items = GetSelectedDetailItem();

			var jsonData = JSON.stringify({
				"chk_num": chk_num,
				"selected_detail_items": array_of_selected_items
			});

			console.log("Split Check Data");
			//console.log(jsonData);

			$http.post("${pageContext.request.contextPath}/ecposmanagerapi/checks/splitCheck",
				jsonData)
				.then(
					function (response) {
						if (response.data.response_code === "00") {
							//Trigger Store Balance
							splitCheckStoreBalance(response.data.pre_check_seq);
							//$scope.redirect_to_selected_split_check(response.data.check_num)
							// $scope.get_check_details(response.data.check_num);
							$location.path('/checks/' + response.data.check_num).replace();
						}
					},
					function (response) {
						alert("Error Occured While Creating Split Check!");
						$(location)
							.attr('href',
								'${pageContext.request.contextPath}/ecpos/#!sales');
					});

		}

		//Important => Relevant
		$scope.redirect_to_selected_split_check = function (data) {
			$location.path('/checks/' + data).replace();
		}
	
		//Important => Relevant
		$scope.storeBalance = function (chk_sequence) {

			var jsonData = JSON.stringify({
				'chk_num': chk_num,
				'chk_seq': chk_sequence,
				'store_balance_item_list': $scope.temp_item_holder
			});

			$http.post("${pageContext.request.contextPath}/ecposmanagerapi/checks/storebalance", jsonData)
				.then(
					function (response) {
						if (response.status === 200) {

							//Print the kitchen receipt
							//$scope.printKitchenDisplay();

							//Clear the holder array
							$scope.temp_item_holder = [];

							//Clear it not, need store
							localStorage.clear();
							
							$(location)
							.attr('href',
								'${pageContext.request.contextPath}/ecpos/#!sales');
						}
					},
					function (response) {
						alert('Cannot perform checks and detail update');
						$(location)
							.attr('href',
								'${pageContext.request.contextPath}/ecpos/#!sales');
					});

		}
		
		function splitCheckStoreBalance(checkSequence){

			var jsonData = JSON.stringify({
				'chk_num': chk_num,
				'chk_seq': checkSequence,
				'store_balance_item_list': $scope.temp_item_holder
			});
			
			$http.post("${pageContext.request.contextPath}/ecposmanagerapi/checks/storebalance", jsonData)
			.then(
				function (response) {
					if (response.status === 200) {
						$scope.temp_item_holder = [];
						localStorage.clear();
					}
				}
				);
		}


		//Important => Relevant
		//Modifed Needed
		$scope.printKitchenDisplay = function () {

			var retriveTempItemArray = JSON.parse(localStorage.getItem("kitchenItemDisplay"));
			console.log(retriveTempItemArray);

			var jsonData = JSON.stringify({
				'checkNo': chk_num,
				'kitchenReceipt': retriveTempItemArray
				// 'kitchenReceipt': $scope.temp_item_holder
			});

			//console.log(jsonData);

			$http.post("${pageContext.request.contextPath}/printerapi/printkitchenreceipt", jsonData)
				.then(
					function (response) {
						if (response.status === 200) {
							console.log("Success Print");
							$scope.temp_item_holder = [];
							localStorage.removeItem("kitchenItemDisplay");

							$(location)
								.attr('href',
									'${pageContext.request.contextPath}/ecpos/#!sales');
						}
						else if(response.status === 404){
							alert("Cannot Perform Printing due to no items.");
						}			
						else {
							alert("Cannot perform Printing. Please check your printer.");
						}
					},
					function (response) {
						alert('Cannot Print Kitchen Recipes');
					});


		}
		
		//Import - New Feature
		$scope.printKitchenReceipt = function (){
			
			var jsonData = JSON.stringify({
				'checkNumber': chk_num
			});
			
			$http.post("${pageContext.request.contextPath}/printerapi/printKitchenReceipt", jsonData)
			.then(
				function (response) {
					if (response.data.response_code == "00") {
						console.log("Success Printing");
					} else if(response.data.response_code == "01"){
						alert("Cannot Perform Printing. Please Check Your Printer Setting.");
					}
				},
				function (response) {
					alert("Cannot perform Printing.");
				});
		}
		
	

	}); 
</script>