<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>

<style>
.sectioncalibrator {
	height: 90vh;
	overflow-y: scroll;
}

@media only screen and (max-width:600px) {
	.sectioncalibrator {
		height: 77vh;
		overflow-y: scroll;
	}
}

.btn-calculator-outline {
	background-color: transparent;
	border-color: transparent;
	font-size: 22px;
	height: 8vh;
}

.btn-calculator-outline:hover {
	color: black;
}

.amount-calculator-outline {
	background-color: transparent;
	border-color: transparent;
	font-size: 26px;
	color: grey;
	padding-left: 5%;
}

.jumbotron {
	padding-top: 15px;
	padding-bottom: 15px;
	margin-top: 10px;
}

hr {
	margin-top: 5px;
	margin-bottom: 8px;
}

.text-info {
	border-left-style: solid;
	border-left-width: 5px;
	border-left-color: orange;
	padding: 5px 5px 5px 10px;
	font-size: 110%;
}

.box {
	border-radius: 5px;
	box-shadow: 1px 2px 10px silver;
	  display: inline-block;
	padding: 10px;
}

.special_box {
	border-radius: 5px;
	box-shadow: 1px 2px 10px silver;
	overflow-x: scroll;
	width: 100%;
}

.label {
	font-size: 120%;
	padding-right: 10px;
}

.buttons {
	padding: 20px;
}

.table-borderless tbody tr td, .table-borderless tbody tr th,
	.table-borderless thead tr th, .table-borderless thead tr td,
	.table-borderless tfoot tr th, .table-borderless tfoot tr td {
	border: none;
}

/* .fixed-panel {
	min-height: 230px;
	max-height: 230px;
	overflow-y: scroll;
} */
.table-fixed thead {
	width: 97%;
}

 .table-fixed tbody {
	min-height: 40vh;
	max-height: 40vh;
	overflow-y: auto;
	width: 100%;
}

tfoot th {
	border: none;
}
</style>

<script>
	//var check_detail_datatable;

	/*  $(document).ready(function($) {
	 DisplayCheckDetailTableData(check_no);
	 });  */

	/* var domino = function(){
	 alert("HRUJ");
	 } */

	/* 	var DisplayCheckDetailTableData = function(item_detail_array) {

	 //console.log(item_detail_array);

	 check_detail_datatable = $('#check_detail_datatable').DataTable({
	 "responsive" : true,
	 "scrollY" : '30vh',
	 "scrollCollapse" : true,
	 "paging" : false,
	 "data" : item_detail_array,
	 "destroy" : true,
	 columnDefs : [ {
	 "targets" : 0,
	 "visible" : false,
	 "data" : null,
	 "defaultContent" : "",
	 "orderable" : false,
	 "searchable" : false,
	 "className" : 'select-checkbox'
	 }, {
	 "data" : "itemid",
	 "visible" : false,
	 "targets" : 1
	 }, {
	 "data" : "itemcode",
	 "defaultContent" : "#",
	 "targets" : 2

	 }, {
	 "data" : "itemname",
	 "targets" : 3
	 },
	 {
	 "data" : "itemprice",
	 "className" : 'text-right',
	 "targets" : 4
	 } ],
	 select : {
	 style : 'multi',
	 selector : 'td:first-child'
	 },

	 order : [ [ 1, 'asc' ] ],
	 "initComplete" : function(settings, json) {

	 }
	 });

	 } */

	/* function showHideSplitPaymentCheckboxes(){
	 var column = check_detail_datatable.column( $(this).attr('data-column'));
	 column.visible( ! column.visible() ); 
	 } */

	/* 	$('#selectable_btn').on(
	 'click',
	 function(e) {
	 e.preventDefault();
	 var column = check_detail_datatable.column($(this).attr(
	 'data-column'));
	 column.visible(!column.visible());

	 // $(".select-checkbox").toggleClass('selected');
	 check_detail_datatable.rows().deselect();
	 });

	 function CashDummyRunner() {
	 var selectItems = check_detail_datatable.rows({
	 selected : true
	 }).data();

	 var item_holder = [];

	 //Seperate the data from other non-related information
	 for (var i = 0; i < selectItems.count(); i++) {
	 item_holder.push(selectItems[i].itemid);
	 }

	 console.log(item_holder);

	 angular.element($('#show_checks_controller_container')).scope()
	 .remove_selected_check_items(item_holder);

	 } */

	function check_if_zero(id) {

		var td = document.getElementById(id);

		td.addEventListener('change', function() {
			console.log("{w}");
			console.log(td.innerHTML);
		})

	}

</script>

<body>
	<div ng-controller="Show_checks_CTRL"
		id="show_checks_controller_container">
		<div ng-init="getInitCheckNum(); name='calculator'">
			<div class="content-wrapper" style="font-size: 0.9em;">

				<section class="content sectioncalibrator">

					<div class="row container-fluid">
						<div class="row">
							<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
								<div class="col-sm-5 col-md-5 col-lg-5 col-xl-5 container-fluid">
									<div class="well" style="height: 75vh;">
										<div>
											<div class="row">
												<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6"
													ng-class="{active:name == 'calculator'}">
													<a data-toggle="tab" ng-click="name='calculator'"
														class="btn btn-block btn-primary"
														id="carousel2-selector-0">CUSTOM</a>
												</div>

												<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6"
													ng-class="{active:name == 'catalogue'}">
													<a data-toggle="tab"
														ng-click="show_group(); name='catalogue'"
														class="btn btn-block btn-primary"
														id="carousel2-selector-1">CATALOGUE</a>
												</div>
											</div>
										</div>

										<div class="tab-content">
											<div ng-switch="name" class="clearfix">
												<div id="menu0" class="tab-pane active"
													ng-switch-when="calculator">
													<hr class="divider">
													<table class="table table-borderless"
														style="height: 0; padding-bottom: 100%;">
														<thead>
															<tr>
																<th class="col-lg-3 col-md-3 col-sm-3 col-xs-3"></th>
																<th class="col-lg-3 col-md-3 col-sm-3 col-xs-3"></th>
																<th class="col-lg-3 col-md-3 col-sm-3 col-xs-3"></th>
																<th class="col-lg-3 col-md-3 col-sm-3 col-xs-3"></th>
															</tr>
															<tr>
																<!-- <th colspan="4"><div class='amount-calculator-outline'>RM<input type="text" style="border:none" readonly="readonly" name="calc" id="calc" /><input type="hidden" name="hidden_calc" id="hidden_calc" /></div></th> -->
																<th colspan="4"><div class="row">
																		<div class='col-sm-3 amount-calculator-outline'>RM</div>
																		<div style="text-align-last: inherit;"
																			class="col-sm-9 amount-calculator-outline"
																			name="calc" id="calc">0.00</div>
																		<input type="hidden" name="hidden_calc"
																			id="hidden_calc" />
																	</div></th>
															</tr>
														</thead>
														<tbody>
															<tr>
																<td><input type="button" value="1"
																	class="form-control btn btn-calculator-outline"
																	ng-click="add_calc('calc',1)" /></td>
																<td><input type="button" value="2"
																	class="form-control btn btn-calculator-outline"
																	ng-click="add_calc('calc',2)" /></td>
																<td><input type="button" value="3"
																	class="form-control btn btn-calculator-outline"
																	ng-click="add_calc('calc',3)" /></td>
																<td rowspan=2><button value="btnDelete"
																		class="form-control btn btn-calculator-outline"
																		ng-click="add_calc('calc',-1)"
																		style="width: 100%; height: 100%;">
																		<i class="fa fa-arrow-left" aria-hidden="true"></i>
																	</button></td>
															</tr>
															<tr>
																<td><input type="button" value="4"
																	class="form-control btn btn-calculator-outline"
																	ng-click="add_calc('calc',4)" /></td>
																<td><input type="button" value="5"
																	class="form-control btn btn-calculator-outline"
																	ng-click="add_calc('calc',5)" /></td>
																<td><input type="button" value="6"
																	class="form-control btn btn-calculator-outline"
																	ng-click="add_calc('calc',6)" /></td>

															</tr>
															<tr>
																<td><input type="button" value="7"
																	class="form-control btn btn-calculator-outline"
																	ng-click="add_calc('calc',7)" /></td>
																<td><input type="button" value="8"
																	class="form-control btn btn-calculator-outline"
																	ng-click="add_calc('calc',8)" /></td>
																<td><input type="button" value="9"
																	class="form-control btn btn-calculator-outline"
																	ng-click="add_calc('calc',9)" /></td>
																<td rowspan="2"><button value="btnConfirm"
																		class="form-control btn btn-calculator-outline"
																		ng-click="add_calc('calc',99)"
																		style="width: 100%; height: 100%;">
																		<i class="fa fa-check-square" aria-hidden="true"></i>
																	</button></td>
															</tr>
															<tr>
																<td><input type="button" value="0"
																	class="form-control btn btn-calculator-outline"
																	ng-click="add_calc('calc',0)" /></td>
																<td colspan="2"><input value="00" type="button"
																	class="form-control btn btn-calculator-outline"
																	ng-click="add_calc('calc',20)" /></td>
															</tr>
														</tbody>
													</table>
												</div>


												<div id="menu1" class="tab-pane" ng-switch-when="catalogue">
													<hr class="divider">
													<div class="row"
														style="max-height: 55vh; overflow-y: auto;">

														<div class="panel-body" style="width: max-width">
															<div id='div_category'
																style="height: 50vh; overflow-y: auto;">

																<div id="group_item_container"
																	ng-show="inside_group_status==0"
																	ng-hide="inside_group_status==1">
																	<div ng-repeat="group_list in list_of_group.group_list">
																		<div class="col-lg-4 col-md-4 col-sm-4 col-xs-6">
																			<a ng-click="get_group_item(group_list.groupid)">
																				<div class="panel panel-default text-center">
																					<div class="panel-body center-block"
																						style="color: grey; font-weight: bold; font-size: small; word-wrap: break-word;">
																						{{group_list.groupname}}</div>
																				</div>
																			</a>
																		</div>
																	</div>
																</div>

																<div id="group_detail_item_container"
																	ng-show="inside_group_status==1"
																	ng-hide="inside_group_status==0">

																	<div class="col-lg-4 col-md-4 col-sm-4 col-xs-6"
																		ng-repeat="item in list_of_item.item_list">								
																		<a
																			ng-click="add_item_into_check(check_detail.chksequence,item.item_id,'S')">
																			<div class="panel panel-default text-center">
																				<img src="${pageContext.request.contextPath}"+"item.image_path" width="200" height="200" alt="nothing" />
																				<div class="panel-body center-block"
																					style="color: grey; font-weight: bold; font-size: small; word-wrap: break-word;">
																					{{item.name}}</div>
																			</div>
																		</a>
																	</div>

																</div>




															</div>
														</div>

													</div>
												</div>
											</div>


										</div>
										<!-- end -->



									</div>
								</div>


								<div
									class="col-sm-7 col-md-7 col-lg-7 col-xl-7 container-fluid visible-lg visible-xl visible-md visible-sm hidden-xs">
									<div class="well" style="height: 80vh;">
										<div class="special_box" ng-show="splitted_checklist_no.length != 0" ng-hide="splitted_checklist_no.length == 0">
											<button class="btn btn-info" 
												ng-repeat="splitted_chk_no in splitted_checklist_no" 
												ng-click="redirect_to_selected_split_check(splitted_chk_no)">{{splitted_chk_no}}</button>
										</div>
										
										<div class="box">
											<div id="west_receipt">

												<div style="margin-bottom: 5px">
													<div class="pull-left">
														<div>
															<font color='Grey'><b>Check :
																	{{check_detail.chksequence}}</b>/<b>Table :
																	{{check_detail.tableno}}</b></font>
														</div>
														<div>
															<font color='Green'><b>Created Date :
																	{{check_detail.datetime}}</b></font>
														</div>
													</div>
													<div class="text-right">
														<button id="print_kitchen_receipt_btn" class="btn-sm btn-default"
															ng-click="printKitchenReceipt()">PRINT KITCHEN RECEIPT</button>
													</div>
												</div>


												<table class='table table-fixed' id="check_detail_datatable">
													<thead>
														<tr>
															<th class='col-md-1 col-xs-1'></th>
															<th></th>
															<th class='col-md-3 col-xs-3'>Code</th>
															<th class='col-md-5 col-xs-5 text-left'>Items</th>
															<!-- 					<th class='col-md-2 col-xs-2 text-center'>Qty</th> -->
															<th class='col-md-3 col-xs-3 text-right'>Price</th>
															<th></th>
															<th></th>
													
														</tr>
													</thead>
													<tbody>

												

													</tbody>

													<tfoot>
														<tr>
															<th class='col-md-1 col-xs-1'></th>
															<th></th>
															<th class='col-md-3 col-xs-3'>Discount(0.00%)</th>
															<th class='col-md-5 col-xs-5'></th>
															<th class='col-md-3 col-xs-3 text-right'><font
																color='Grey'><b>0.00</b></font></th>
															<th></th>
															<th></th>
						
														</tr>

														<tr>
															<th class='col-md-1 col-xs-1'></th>
															<th></th>
															<th class='col-md-3 col-xs-3'>Total</th>
															<th class='col-md-5 col-xs-5'></th>
															<th class='col-md-3 col-xs-3 text-right'><font
																color='Grey'>{{check_detail.subttl| number:2}}</font></th>
																<th></th>
																<th></th>
												
														</tr>
													</tfoot>
												</table>

												<!-- 									<div class='row'>
													<div
														class='col-lg-6 col-md-6 container-fluid visible-lg visible-md hidden-sm hidden-xs'>
														<font color='Grey'><b>Discount(0.00%)</b></font>
													</div>
													<div
														class='col-lg-6 col-md-6 container-fluid visible-lg visible-md hidden-sm hidden-xs text-right'>
														<font color='Grey'>0.00</font>
													</div>
												</div>
												<div class='row'>
													<div
														class='col-lg-6 col-md-6 container-fluid visible-lg visible-md hidden-sm hidden-xs'>
														<font color='Grey'><b>Total</b></font>
													</div>
													<div
														class='col-lg-6 col-md-6 container-fluid visible-lg visible-md hidden-sm hidden-xs text-right'>
														<font color='Grey'>{{check_detail.subttl|
															number:2}}</font>
													</div>
												</div> -->

											</div>


											<div class="row">
												<hr class="divider">
											</div>

											<!-- 			<div class="row">
											<div class="col-lg-6 col-md-6 col-lg-push-6 col-md-push-6">
												<a href="#" class="btn btn-block btn-social btn-bitbucket"
													onclick=""><span
													class="fa fa-adn"></span>Confirm</a>
											</div>
										</div> -->

											<div class="row" style="margin-bottom: 5px;">
												<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
													<button class="btn btn-block btn-primary"
														ng-disabled="valid_btn_status === true ? true:false"
														ng-click="createSplitCheck()"
														id="selectable_btn">SPLIT CHECK</button>
												</div>
												<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
													<button class="btn btn-block btn-primary"
														ng-click="remove_selected_check_items()"
														ng-disabled="valid_btn_status === true? true:false">VOID</button>
												</div>
											</div>
											<div class="row">
												<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
													<button class="btn btn-block btn-primary"
														id="cash_payment_btn"
														ng-disabled="isPaymentAvailable"
														ng-click="goToPayment(check_detail.checknumber,check_detail.chksequence)">PAYMENT</button>
												</div>
												<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
													<button class="btn btn-block btn-primary" id=""
														 ng-click="storeBalance(check_detail.chksequence)">STORE BALANCE</button>
												</div>
											</div>
										</div>
									</div>
								</div>



							</div>





							<div id="check_receipt"></div>










						</div>
					</div>

					<!-- Modal Payment [START] -->

					<!-- Modal -->
					<div class="modal fade" id="cash_payment_calculator_modal"
						tabindex="-1" role="dialog" aria-hidden="true">
						<div class="modal-dialog" role="document">
							<div class="modal-content">
								<div class="modal-body">

									<div>
										<!-- Calculator Container -->
										<div class="col-lg-6 col-md-6 col-sm-6">
											<input type="hidden" id="hidden_keyin_amt" />
											<table class="table">
												<thead>
													<tr>
														<th class="col-lg-3 col-md-3 col-sm-3 col-xs-3"></th>
														<th class="col-lg-3 col-md-3 col-sm-3 col-xs-3"></th>
														<th class="col-lg-3 col-md-3 col-sm-3 col-xs-3"></th>
														<th class="col-lg-3 col-md-3 col-sm-3 col-xs-3"></th>
													</tr>
												</thead>

												<tbody>

													<tr>
														<td><input type="button" value="1"
															class="form-control btn btn-calculator-outline"
															ng-click="cash_payment_calc('calc_keyin_amt',1)" /></td>
														<td><input type="button" value="2"
															class="form-control btn btn-calculator-outline"
															ng-click="cash_payment_calc('calc_keyin_amt',2)" /></td>
														<td><input type="button" value="3"
															class="form-control btn btn-calculator-outline"
															ng-click="cash_payment_calc('calc_keyin_amt',3)" /></td>
														<td rowspan=2><button value="btnDelete"
																class="form-control btn btn-calculator-outline"
																ng-click="cash_payment_calc('calc_keyin_amt',-1)"
																style="width: 100%; height: 100%;">
																<i class="fa fa-arrow-left" aria-hidden="true"></i>
															</button></td>
													</tr>
													<tr>
														<td><input type="button" value="4"
															class="form-control btn btn-calculator-outline"
															ng-click="cash_payment_calc('calc_keyin_amt',4)" /></td>
														<td><input type="button" value="5"
															class="form-control btn btn-calculator-outline"
															ng-click="cash_payment_calc('calc_keyin_amt',5)" /></td>
														<td><input type="button" value="6"
															class="form-control btn btn-calculator-outline"
															ng-click="cash_payment_calc('calc_keyin_amt',6)" /></td>

													</tr>
													<tr>
														<td><input type="button" value="7"
															class="form-control btn btn-calculator-outline"
															ng-click="cash_payment_calc('calc_keyin_amt',7)" /></td>
														<td><input type="button" value="8"
															class="form-control btn btn-calculator-outline"
															ng-click="cash_payment_calc('calc_keyin_amt',8)" /></td>
														<td><input type="button" value="9"
															class="form-control btn btn-calculator-outline"
															ng-click="cash_payment_calc('calc_keyin_amt',9)" /></td>
														<td rowspan="2"><button value="btnConfirm"
																class="form-control btn btn-calculator-outline"
																ng-click="cash_payment_calc('calc_keyin_amt',99)"
																style="width: 100%; height: 100%;">
																<i class="fa fa-check-square" aria-hidden="true"></i>
															</button></td>
													</tr>
													<tr>
														<td><input type="button" value="0"
															class="form-control btn btn-calculator-outline"
															ng-click="cash_payment_calc('calc_keyin_amt',0)" /></td>
														<td colspan="2"><input value="00" type="button"
															class="form-control btn btn-calculator-outline"
															ng-click="cash_payment_calc('calc_keyin_amt',20)" /></td>
													</tr>

												</tbody>
											</table>

										</div>

										<!-- Charge and Value Display -->
										<div class="col-lg-6 col-md-6 col-sm-6">
											<table class="table table-borderless">
												<thead>
													<tr></tr>
												</thead>

												<tbody>
													<tr>
														<th>Key-in Amount:</th>
														<td></td>
														<td class="text-right" id="calc_keyin_amt"
															onchange="check_if_zero(this.id)">0.00</td>

													</tr>
													<tr>
														<th>Amount to Pay:</th>
														<td></td>
														<!-- <td class="text-right" id="">{{selected_items_ttl|number:2}}</td> -->
														<td class="text-right" id="">{{selected_items_ttl|number:2}}</td>
													</tr>

													<tr>
														<th>Balance:</th>
														<td></td>
														<td class="text-right" id="payment_balance_amt">0.00</td>
													</tr>
												</tbody>
												<tfoot>
													<tr>
														<td class="text-left">
															<button id="paying_cash_btn" class="btn btn-primary"
																ng-click="make_cash_payment()">Pay</button>
														</td>
														<td class="text-center">
															<button id="cash_amt_reset_btn" class="btn btn-default"
																ng-click="reset_balance_amt()">Reset</button>
														</td>
														<td class="text-right">
															<button id="cancel_cash_pymt_btn" data-dismiss="modal"
																ng-click="reset_balance_amt()" class="btn btn-warning">Cancel</button>
														</td>
													</tr>
													<tr>
														<td class="text-left">
															<button id="match_amount_btn" class="btn btn-primary"
																ng-click="match_amount(selected_items_ttl|number:2)">Match</button>
														</td>
													</tr>

												</tfoot>
											</table>
										</div>
									</div>


								</div>


								<div class="modal-footer"></div>

							</div>
						</div>
					</div>


					<!-- Modal Payment [END] -->


					<!-- Modal Print Receipt [START] -->
					<div id="print_receipt_modal" class="modal" tabindex="-1"
						role="dialog" aria-hidden="true">
						<div class="modal-dialog" role="document">
							<div class="modal-content">
								<div class="modal-header">
									<h3 class="modal-title">Print Receipt</h3>
								</div>

								<div class="modal-body">
									<h4>Total Change Amount: {{remaining_balance_amt| currency:"RM"}}</h4>
									<p>Do you want to print the receipt ?</p>

									<div id="print_receipt_group_buttons_container">
										<div class="text-left" style="display: inline;">
											<button class="btn btn-info"
												ng-click="print_receipt(check_detail.chksequence)">Print</button>
										</div>
										<div class="pull-right" style="display: inline;">
											<button class="btn btn-primary"
												ng-click="redirect_to_table_list()" style="display: inline;">Done</button>
										</div>
									</div>
								</div>
								
							</div>
						</div>
					</div>
					<!-- Modal Print Receipt [END] -->


					<!--  Modal Payment Option [START] -->
			<!-- <div id="payment_option_modal" class="modal" tabindex="-1"
						role="dialog" aria-hidden="true">
						<div class="modal-dialog" role="document">
							<div class="modal-content">
								<div class="modal-header">
									<h3 class="modal-title">Payment Option</h3>
								</div>

								<div class="modal-body">
									
									<div class="row">
										<div class="col-md-4">
											<div style="height: 40vh;overflow-y: scroll;">
												<button ng-repeat="payment_option in payment_options"
													class="btn btn-info" ng-click="" style="display: block; margin:5px">
													{{payment_option.tender_name}}
												</button>										
											</div>
											<div id="cash_balance_display_div">
												<table>
													<tbody>
														
													
													
													
													
													</tbody>		
												</table>								
											</div>
										</div>
									</div>
									
									<div id="print_receipt_group_buttons_container">
										<div class="text-left" style="display: inline;">
											<button class="btn btn-info"
												ng-click="print_receipt(check_detail.chksequence)">Print</button>
										</div>
										<div class="pull-right" style="display: inline;">
											<button class="btn btn-primary"
												ng-click="redirect_to_table_list()" style="display: inline;">Cancel</button>
										</div>
									</div>
								</div>
								
							</div>
						</div>
					</div> -->



					<!--  Modal Payment Option [END] -->
					

				</section>
			</div>
		</div>


	</div>
</body>
</html>