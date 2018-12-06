<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>

<style>
.sectioncalibrator {
	height: calc(100vh - 50px);
	overflow-y: scroll;
}

@media only screen and (max-width:600px) {
	.sectioncalibrator {
		height: calc(100vh - 100px);
		overflow-y: scroll;
	}
}

.btn-calculator-outline {
	background-color: transparent;
	border-color: transparent;
	font-size: 16px;
	height: 4vh;
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

#bigContainer {
	border: 2px solid blue;
	border-radius: 5px;
	position: relative;
	max-width: inherit;
	height: 70px;
	margin-bottom: 3px;
}

#bigContainer div {
	position: absolute;
	width: 50%;
	height: 35px;
}

.top {
	top: 0;
}

.right {
	right: 0;
	text-align: right;
}

.bottom {
	bottom: 0;
}

.left {
	left: 0;
	text-align: left;
	overflow: hidden;
}

.parent {
	display: table;
}

.child {
	display: table-cell;
	vertical-align: middle;
}
</style>


<body>
	<div ng-controller="Show_Payment_CTRL"
		ng-init="paymentInitialization()">
		<div class="content-wrapper" style="font-size: 0.9em;">
			<section class="content sectioncalibrator">
				<div id="payment-container" class="row container-fluid">
					<!-- Upper Half Container -->
					<div class="row" style="height:calc(100vh - 100px);">
			<!-- 			<div class="col-md-3" style="height: 38vh; overflow-y: scroll;">
							<div ng-repeat="tableOrder in tableOrders"
								ng-if="tableOrder.checkNumber != checkNum" id="bigContainer">
								<a ng-click="getChkTotal(tableOrder.checkNumber)"
									ng-model="tableOrder">
									<div class="top left">Check No:
										{{tableOrder.checkNumber}}</div>
									<div class="top right">Table No:
										{{tableOrder.tableNumber}}</div>
									<div class="bottom left">Created:
										{{tableOrder.createdDate}}</div>
									<div class="bottom right">Total: {{tableOrder.subTotal |
										currency:"RM"}}</div>
								</a>
							</div>
						</div> -->
						<!-- <div class="col-md-7" style="height: 38vh;"> -->
						<div class="col-md-7">
						<div class="well row container-fluid" style="height:calc(100vh - 100px);">
							<table id='payment_datatable' class='table table-fixed'>
								<thead>
									<tr>
										<th class='col-md-1 col-xs-1'></th>
										<th class='col-md-5 col-xs-5 text-left'>Transaction</th>
										<th class='col-md-3 col-xs-3 text-right'>Price</th>
									</tr>
								</thead>

								<tbody>
								</tbody>

								<tfoot>
									<tr>
										<th class='col-md-1 col-xs-1'></th>
										<th class='col-md-5 col-xs-5 text-left'>Balance (RM)</th>
										<th class='col-md-3 col-xs-3 text-right'
											ng-style="positiveBalance">{{totalPriceDisplay|number:2}}</th>
									</tr>
								</tfoot>
							</table>
							</div>
						</div>

						<!-- <div class="col-md-4 text-center parent" style="height: 38vh; border-left: 2px solid red;"> -->
						<div class="col-md-5 text-center parent">
							<div class="well row container-fluid" style="height:calc(100vh - 100px);">
							<div class="child">
								<button class="btn btn-danger" style="margin-bottom: 13px;"
									ng-disabled="isRemoveAvailable === true ? true:false"
									ng-click="removePaymentTransaction()">Remove</button>
								<br>
								<button class="btn btn-primary"
									ng-disabled="isPaymentAvailable === true? true:false"
									ng-click="makeCashPayment()">Cash Payment</button>
							</div>
							</div>
						</div>



					</div>

					<!-- Lower Half Container -->
<%-- 					<div class="row" style="height: 38vh;">
						<div class="col-md-6"
							style="border-style: solid; border-width: 2px; border-radius: 4px;">
							<div class="row">
								<div class="col-md-12"
									style="overflow: auto; white-space: nowrap; height: 19vh;">
									<div ng-repeat="rm in ringgitMalaysiaArray"
										style="margin: 3px; display: inline-block;">
										<a ng-click="addRinggitIntoPayment(rm)" ng-model="rm"> <img
											ng-src="${pageContext.request.contextPath}/img/rm/{{rm}}.jpg"
											alt="{{rm}}" width="150px" height="75px" />
										</a>
									</div>
								</div>

								<div class="col-md-12"
									style="overflow: auto; white-space: nowrap; height: 19vh;">
									<div style="margin: 3px; display: inline-block;">
										<!-- 								<button class="btn" ng-click="">Visa</button>
										<button class="btn" ng-click="">Master</button> -->
										<button class="pull-right btn btn-sm btn-primary"
											type="button" data-toggle="modal"
											data-target="#terminal_selection_modal">Card Payment</button>
									</div>
								</div>
							</div>
						</div>


						<div class="col-md-6"
							style="border-style: solid; border-width: 5px; border-radius: 4px;">
							<div>
								<table class="table">
									<thead>

										<tr>
											<th colspan="4">
												<div class="row">
													<div class='col-sm-3 amount-calculator-outline'>RM</div>
													<div style="text-align-last: inherit;"
														class="col-sm-9 amount-calculator-outline" name="calc"
														id="calc">0.00</div>
													<input type="hidden" name="hidden_calc" id="hidden_calc" />
												</div>
											</th>
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
						</div>
					</div> --%>


				</div>

				<!-- 	<div class="row container" style="width: 50vw;">

					<div class="col-md-3" style="height: 38vh; overflow-y: scroll;">
						<div ng-repeat="tableOrder in tableOrders"
							ng-if="tableOrder.checkNumber != checkNum">
							<a ng-click="getChkTotal(tableOrder.checkNumber)"
								ng-model="tableOrder">
								<div>{{tableOrder.checkNumber}}</div>
							</a>
						</div>
					</div>

					<div class="col-md-7">
						<table id='payment_datatable' class='table table-fixed'>
							<thead>
								<tr>
									<th class='col-md-1 col-xs-1'></th>
									<th class='col-md-5 col-xs-5 text-left'>Transaction</th>
									<th class='col-md-3 col-xs-3 text-right'>Price</th>
								</tr>
							</thead>

							<tbody>
							</tbody>

							<tfoot>
								<tr>
									<th class='col-md-1 col-xs-1'></th>
									<th class='col-md-5 col-xs-5 text-left'>Balance (RM)</th>
									<th class='col-md-3 col-xs-3 text-right'>{{totalPriceDisplay|number:2}}</th>
								</tr>
							</tfoot>
						</table>
					</div>

				 	<div class="col-md-2 text-center" style="border-style: dotted; height: 38vh;">
						<button class="btn btn-danger" ng-click="removePaymentTransaction()">Remove</button>
						<br>
						<button class="btn btn-primary" ng-click="makeTenderPayment()"></button>
						<br>
					</div>

				</div> -->

				<%-- 	<div class="row container">
					<div class="col-md-6"
						style="border-style: solid; height: 35vh; border-width: 2px; border-radius: 4px;">
						<div class="row" style="overflow-x: scroll;">
							<div ng-repeat="rm in ringgitMalaysiaArray"
								style="margin: 3px; display: inline-block;">
								<a ng-click="addRinggitIntoPayment(rm)" ng-model="rm"> <img
									ng-src="${pageContext.request.contextPath}/member/img/rm/{{rm}}.jpg"
									alt="{{rm}}" width="150px" height="75px" />
								</a>
							</div>
						</div>

						<!-- 		<div class="row">
								<div style="border-style: solid; border-width: 5px; border-radius: 4px;">
					
								</div>
							</div> -->

					</div>

					<div class="col-md-6"
						style="border-style: solid; border-width: 5px; border-radius: 4px;">
						<div>
							<table class="table">
								<thead>

									<tr>
										<th colspan="4">
											<div class="row">
												<div class='col-sm-3 amount-calculator-outline'>RM</div>
												<div style="text-align-last: inherit;"
													class="col-sm-9 amount-calculator-outline" name="calc"
													id="calc">0.00</div>
												<input type="hidden" name="hidden_calc" id="hidden_calc" />
											</div>
										</th>
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
					</div>

				</div> --%>


			</section>
		</div>
		
			<!-- Terminal selection Dropdownlist [START] -->
					<div id="terminal_selection_modal" class="modal" tabindex="-1"
						role="dialog" aria-hidden="true" data-keyboard="false"
						data-backdrop="static">
						<div class="modal-dialog" role="document">
							<div class="modal-content">
								<div class="modal-header">
									<h3 class="modal-title">Terminal Selection</h3>
								</div>

								<div class="modal-body">

									<div id="terminal_selection_container">

										<form name="terminal_selection_form"
											ng-submit="submitTerminalSelectionData()">
											<div class="form-group row">
												<label for="terminal_device_selection"
													class="col-sm-3 col-form-label">Terminal Name</label>
												<div class="col-sm-8">
													<select id="terminal_device_selection"
														name="terminal_device_selection" class="form-control"
														ng-model ="selectedTerminal"
														 required>
														<option value="0" selected>-- Default --</option>
														<option ng-repeat="terminal in terminalList"
															value="{{terminal.id}}">{{terminal.terminalName}}</option>
													</select>

												</div>
											</div>

											<div class="text-right">
												<button type="submit" class="btn btn-primary">Confirm</button>
												<button type="button" class="btn btn-secondary"
													data-dismiss="modal" ng-click="resetTerminalModel()">Close</button>
											</div>

										</form>
										
									</div>
								</div>

							</div>
						</div>
					</div>

					<!-- Terminal selection Dropdownlist [END] -->
		
					<!-- Loading Modal [START] -->
						<div id="loading_modal" class="modal" tabindex="-1" role="dialog"
							aria-hidden="true" data-keyboard="false" data-backdrop="static"
							role="dialog">
							<div class="modal-content">
								<div class="modal-body text-center">
									<p>Transaction In Progress, Please Wait ...</p>
								</div>
							</div>
						</div>
					<!-- Loading Modal [END] -->
		
		
		
		
		
		
		
		
		
	</div>

</body>

</html>