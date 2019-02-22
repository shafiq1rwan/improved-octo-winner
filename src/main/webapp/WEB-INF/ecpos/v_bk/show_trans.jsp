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

.table-borderless tbody tr td, .table-borderless tbody tr th,
	.table-borderless thead tr th, .table-borderless thead tr td,
	.table-borderless tfoot tr th, .table-borderless tfoot tr td {
	border: none;
}

.fixed-panel {
	min-height: 230px;
	max-height: 230px;
	overflow-y: scroll;
}

.table-fixed thead {
	width: 97%;
}

.table-fixed tbody {
	min-height: 40vh;
	max-height: 40vh;
	overflow-y: auto;
	width: 100%;
}

.table-fixed tbody, .table-fixed tr, .table-fixed td, .table-fixed th {
	display: block;
}

.table-fixed thead {
	background-color: black;
	color: white;
}

.table-fixed tbody td, .table-fixed thead>tr>th {
	float: left;
	border-bottom-width: 0;
}

.selected_row {
	background-color: #66a3ff;
}
</style>

<body>

	<div ng-controller="Show_trans_CTRL" ng-init="initTransPage()">
		<div class="content-wrapper" style="font-size: 0.9em;">

			<section class="content sectioncalibrator">
				<div class="row container-fluid" style="padding-right:2px;padding-left:2px;">								
							<div id="transaction_list">
								<div class="col-md-7 col-lg-7 col-xl-7 container-fluid visible-lg visible-xl visible-md hidden-sm hidden-xs" style="padding-right:2px;padding-left:2px;">
									<div class="well" style="height: 80vh;">

										<div id="myCalendar" class="input-group date"
											data-provide="datepicker">
											<input type="text" class="form-control" name="datetextbar">
											<div class="input-group-addon">
												<span class="glyphicon glyphicon-th"></span>
											</div>
										</div>

										<h3>Transaction List</h3>
										<table id="myTable" class="table table-fixed ">
											<thead style="background-color: #205081; color: #ffffff">
												<tr>
													<th class="col-md-2 col-xs-2 text-left">Date Time</th>
													<th class="col-md-3 col-xs-3 text-center">Staff</th>
													<th class="col-md-5 col-xs-5 text-center">Description</th>
													<th class="col-md-2 col-xs-2 text-center">Amount (RM)</th>
												</tr>
											</thead>
											<tbody data-link="row" class="rowlink"
												style="max-height: 50vh;">

												<tr ng-repeat="tran_history in fields_TransList.trans_list"
													ng-class="{selected_row:$index == selectedRow}"
													ng-click="get_transaction_details(tran_history.tran_id); setClickedRow($index)">
													<td class="col-md-2 col-xs-2 text-left"><a>{{tran_history.tran_time}}</a></td>
													<td class="col-md-3 col-xs-3 text-center">{{tran_history.name}}</td>
													<td class="rowlink-skip col-md-5 col-xs-5 text-center">{{tran_history.description}}</td>
													<td class="col-md-2 col-xs-2 text-center">{{tran_history.amount
														|currency: "RM"}}</td>
												</tr>
											</tbody>
										</table>
									</div>
								</div>
							</div>
							<div id="transaction_detail">
								<div class="col-md-5 col-lg-5 col-xl-5 container-fluid" style="padding-right:2px;padding-left:2px;">
									<div class="well" style="height: 80vh;">
										<h4>
											<font color=#205081><b>Transaction Details</b></font>
										</h4>
										<div class="row">
											<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
												<div class="row">
													<div class="col-lg-9 col-md-9 col-sm-9 col-xs-9">
														<table>
															<tr style="color: grey; font-weight: bold;">
																<td>Check</td>
																<td>&ensp;</td>
																<td>&ensp;</td>
																<td>:</td>
																<td>&ensp;</td>
																<td>&ensp;</td>
																<td>{{transaction_details.chk_num}}</td>
															</tr>
															<tr style="color: grey; font-weight: bold;">
																<td>Table</td>
																<td>&ensp;</td>
																<td>&ensp;</td>
																<td>:</td>
																<td>&ensp;</td>
																<td>&ensp;</td>
																<td>{{transaction_details.tblno}}</td>
															</tr>
															<tr style="color: grey; font-weight: bold;">
																<td>Created Date</td>
																<td>&ensp;</td>
																<td>&ensp;</td>
																<td>:</td>
																<td>&ensp;</td>
																<td>&ensp;</td>
																<td>{{transaction_details.tran_datetime}}</td>
															</tr>
															<tr style="color: grey; font-weight: bold;">
																<td>Payment Type</td>
																<td>&ensp;</td>
																<td>&ensp;</td>
																<td>:</td>
																<td>&ensp;</td>
																<td>&ensp;</td>
																<td>{{transaction_details.payment_type}}</td>
															</tr>
														</table>
													</div>
													<div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
														<button type="button" data-toggle="modal"
															data-target="#modal_transasction_action"
															class="btn btn-primary">Action</button>
													</div>
												</div>

											</div>
										</div>

										<br />
										<div class="row">
											<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
												<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
													<span style="font-weight: bold;"><b>Total</b></span><br />
													<span
														style="font-size: 15pt; font-weight: bold; color: green;"><b>
															{{amount|currency:"RM "}}</b></span>
												</div>
												<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
													<table>
														<tr style="font-weight: bold;">
															<td>Subtotal</td>
															<td>&ensp;</td>
															<td>&ensp;</td>
															<td>&ensp;</td>
															<td>{{amount|currency:"RM "}}</td>
														</tr>
														<tr style="font-weight: bold;">
															<td>Tax</td>
															<td>&ensp;</td>
															<td>&ensp;</td>
															<td>&ensp;</td>
															<td>{{tax|currency:"RM "}}</td>
														</tr>
														<tr style="font-weight: bold;">
															<td>Tips</td>
															<td>&ensp;</td>
															<td>&ensp;</td>
															<td>&ensp;</td>
															<td>RM 0.00</td>
														</tr>
													</table>
												</div>
											</div>
										</div>
										<br />
										<div class="row">
											<table class="table table-fixed">
												<thead>
													<tr>
														<th class="col-md-6 col-xs-6 text-left">Items</th>
														<th class="col-md-2 col-xs-2 text-center">Qty</th>
														<th class="col-md-4 col-xs-4 text-center">Price</th>
													</tr>
												</thead>
												<tbody>
													<tr
														ng-repeat="detail_items in transaction_details.detail_list">
														<td class="col-md-6 col-xs-6 text-left">{{detail_items.name}}</td>
														<td class="col-md-2 col-xs-2 text-center">{{detail_items.qty}}</td>
														<td class="col-md-4 col-xs-4 text-center">{{detail_items.ttl}}</td>
													</tr>
												</tbody>
											</table>
										</div>
									</div>
								</div>
							</div>
				</div>


			</section>
		</div>


		<!-- MODAL START -->
		<div class="row">
			<div class="modal fade" id="modal_transasction_action" role="dialog"
				data-keyboard="false" data-backdrop="static">
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<h4 class="modal-title text-center content-format">
								<i class="fa fa-info"></i> <b>INFORMATION</b>
							</h4>
						</div>
						<div class="modal-body text-center">
							<h4>Please select an action:</h4>
						</div>
						<div class="modal-footer center-block text-center">
							<div class="form-group">
								<div class="col-md-12">
									<div class="row">
										<div class="col-md-12">
											<div class="col-lg-2 col-md-2 col-sm-0 col-xs-0"></div>
											<div class="col-lg-4 col-md-4 col-sm-6 col-xs-12">
												<a href="#" data-dismiss='modal'>
													<div class="panel panel-primary text-center">
														<div class="panel-body" style="background-color: #5cb85c;">
															<div class="panel-body center-block"
																ng-click="printExistingReceipt(transaction_details.chk_num)"
																style="color: white; font-weight: bold; font-size: small;">
																Print Receipt</div>
														</div>
													</div>
												</a>
											</div>
											<div class="col-lg-4 col-md-4 col-sm-6 col-xs-12">
												<a href="#" data-dismiss='modal'>
													<div class="panel panel-primary text-center">
														<div class="panel-body" style="background-color: #f0ad4e;">
															<div class="panel-body center-block"
																data-toggle="modal"
																data-target="#terminal_selection_modal"
																data-dismiss="modal"				
																style="color: white; font-weight: bold; font-size: small;">
																Void</div>
														</div>
													</div>
												</a>
											</div>




											<div class="panel-body">
												<div class="row"></div>
												<br>
												<div class="row"></div>
												<br>
												<div class="row">
													<a href="#" class="btn btn-danger center-block"
														data-dismiss="modal">CANCEL</a>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>

			</div>
		</div>

		<!-- MODAL END -->

		<!-- Terminal List Selection Modal [START] -->
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
								ng-submit="submitTerminalSelectionData(transaction_details.transactionId,transaction_details.chk_num)">
								<div class="form-group row">
									<label for="terminal_device_selection"
										class="col-sm-3 col-form-label">Terminal Name</label>
									<div class="col-sm-8">
										<select id="terminal_device_selection"
											name="terminal_device_selection" class="form-control"
											ng-model="selectedTerminal" required>
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
		<!-- Terminal List Selection Modal [END] -->

		<!-- Loading Modal [START] -->
				<div id="loading_modal" class="modal" tabindex="-1" role="dialog"
					aria-hidden="true" data-keyboard="false" data-backdrop="static"
					role="dialog">
					<div class="modal-content">
						<div class="modal-body text-center">
							<p>Void In Progress, Please Wait ...</p>
						</div>
					</div>
				</div>
		<!-- Loading Modal [END] -->

	</div>


</body>
</html>