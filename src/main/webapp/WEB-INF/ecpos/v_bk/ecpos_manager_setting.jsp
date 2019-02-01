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

#home {
	text-align: center;
}
</style>

<body>

	<div ng-controller="ECPOS_Manager_Setting_CTRL"
		ng-init="loadSettingData()">
		<div class="content-wrapper" style="font-size: 0.9em;">
<!-- 			<section class="content-header">
				<h1>
					ECPOS <small>Setting</small>
				</h1>
				<ol class="breadcrumb">
					<li>MAIN NAVIGATION</li>
					<li>Setting</li>
				</ol>
			</section> -->
			<section class="content sectioncalibrator">
				<div class="form_container">

					<div class="box" style="padding:5px;">
						<div class="box-header">
							<div>

								<h2 class="text-center">ECPOS Manager</h2>
							</div>
						</div>

						<form ng-submit="saveSettingData()" method="post"
							id="manager_setting_form" name="manager_setting_form">

							<h3>Business Properties</h3>

							<div class="row">
								<div class="form-group col-md-6">
									<label for="property_name_input">Business Name *</label> <input
										class="form-control" id="property_name_input" type="text"
										maxlength="66" ng-model="appSettingData.propertyName" required />
								</div>

								<div class="form-group col-md-6">
									<label for="table_number_input">Table Number (From 1 to
										100) *</label> <input class="form-control" id="table_number_input"
										type="number" min="1" max="100" value=0
										ng-model="appSettingData.tableCount" required />
								</div>
							</div>

							<hr />
							<h3>Taxes (in %)</h3>

							<div class="row">

								<div class="form-group col-md-3">
									<label for="gst_input">GST</label> <input class="form-control"
										id="gst_input" type="number" min="0" max="100"
										ng-model="appSettingData.gstPercentage" />
								</div>

								<div class="form-group col-md-3">
									<label for="sst_input">Sales Tax</label> <input
										class="form-control" id="sst_input" type="number" min="0"
										max="100" ng-model="appSettingData.salesTaxPercentage" />
								</div>

								<div class="form-group col-md-3">
									<label for="service_tax_input">Service Tax</label> <input
										class="form-control" id="service_tax_input" type="number"
										min="0" max="100"
										ng-model="appSettingData.serviceTaxPercentage" />
								</div>

								<div class="form-group col-md-3">
									<label for="other_tax_input">Other</label> <input
										class="form-control" id="other_tax_input" type="number"
										min="0" max="100" ng-model="appSettingData.otherTaxPercentage" />
								</div>

							</div>

							<hr />
							<h3>Printer Configuration</h3>

							<div class="row" id="printer_config_div">
							</div>

							<hr />
							<h3>Terminal Configuration</h3>

							<div class="row" id="terminal_config_div">
								
								<div class="form-group col-md-3">
									<label for="terminal_port_input">Terminal Name</label> <input
										class="form-control" id="terminal_name_input" type="text"
										ng-model="terminalConfigurationInputData.terminalName" />
								</div>

								<div class="form-group col-md-3">
									<label for="terminal_ip_input">Wifi IP</label> <input
										class="form-control" id="terminal_ip_input" type="text"
										ng-model="terminalConfigurationInputData.ipAddress" />
								</div>

								<div class="form-group col-md-3">
									<label for="terminal_port_input">Wifi Port</label> <input
										class="form-control" id="terminal_port_input" type="number"
										ng-model="terminalConfigurationInputData.port" />
								</div>

								<div class="form-group col-md-3">
									<br>
									<button class="btn btn-primary" type="button"
										ng-click="addTerminal()">Add Terminal</button>
								</div>

								<div class="col-md-12" ng-if="terminalConfigurationData">
									<h4>Terminal List</h4>
									<div class="table-responsive">
										<table class="table">
											<thead>
												<tr>
													<th>Name</th>
													<th>IP</th>
													<th>Port</th>
													<th>Action</th>
												</tr>
											</thead>
											<tbody>
												<tr>
													<td>Default</td>
													<td></td>
													<td></td>
													<td>
														<button class="btn btn-info" type="button"
															data-toggle="modal"
															data-target="#settlement_selection_modal"
															ng-click="setSettlementTerminal(0)">Settlement</button>
													</td>
												</tr>
												<tr ng-repeat="configData in terminalConfigurationData">
													<td><a data-toggle="modal"
														data-target="#edit_terminal_modal"
														ng-click="editTerminalData(configData)">{{configData.terminalName}}</a></td>
													<td>{{configData.wifiIP}}</td>
													<td>{{configData.wifiPort}}</td>
													<td>
														<!-- 														<button class="btn btn-primary" type="button" ng-click="pingTerminal(configData.wifiIP,configData.wifiPort)">Ping</button>
 -->
														<button class="btn btn-danger" type="button"
															ng-click="removeTerminalInfo(configData.id)">Remove</button>
														<button class="btn btn-info" type="button"
															data-toggle="modal"
															data-target="#settlement_selection_modal"
															ng-click="setSettlementTerminal(configData.id)">Settlement</button>
													</td>
												</tr>

											</tbody>

										</table>
									</div>
								</div>


							</div>

							<button class="btn btn-primary" type="submit">Save</button>
						</form>

					</div>

				</div>

			</section>
		</div>


		<!-- Settlement Modal [START] -->
		<div id="settlement_selection_modal" class="modal" tabindex="-1"
			role="dialog" aria-hidden="true" data-keyboard="false"
			data-backdrop="static">
			<div class="modal-dialog" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<h3 class="modal-title">Settlement</h3>
					</div>

					<div class="modal-body">

						<div id="settlement_selection_container">

							<form name="settlement_selection_form"
								ng-submit="submitSettlementData()">

								<div class="form-group">
									<div class="radio">
										<label> <input type="radio" name="settlement_radio"
											ng-model="settlementType" value="NR" required>
											VISA/MASTER/JCB
										</label>
									</div>
									<div class="radio">
										<label> <input type="radio" name="settlement_radio"
											ng-model="settlementType" value="AMEX"> AMEX
										</label>
									</div>
									<div class="radio">
										<label> <input type="radio" name="settlement_radio"
											ng-model="settlementType" value="MCCS"> MCCS
										</label>
									</div>
									<div class="radio">
										<label> <input type="radio" name="settlement_radio"
											ng-model="settlementType" value="UNIONPAY"> UNIONPAY
										</label>
									</div>
								</div>

								<div class="text-right">
									<button type="submit" class="btn btn-info">Select</button>
									<button type="button" class="btn btn-secondary"
										data-dismiss="modal" ng-click="resetSettlementSelection()">Close</button>
								</div>

							</form>

						</div>
					</div>

				</div>
			</div>
		</div>

		<!-- Settlement Modal [END] -->

		<!-- Loading Modal [START] -->
		<div id="loading_modal" class="modal" tabindex="-1" role="dialog"
			aria-hidden="true" data-keyboard="false" data-backdrop="static"
			role="dialog">
			<div class="modal-content">
				<div class="modal-body text-center">
					<p>Perform Settlement In Progress, Please Wait ...</p>
				</div>
			</div>
		</div>
		<!-- Loading Modal [END] -->
		
		<!-- Edit Terminal Modal [START] -->
		<div id="edit_terminal_modal" class="modal" tabindex="-1"
			role="dialog" aria-hidden="true" data-keyboard="false"
			data-backdrop="static">
			<div class="modal-dialog" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<h3 class="modal-title">Edit Terminal</h3>
					</div>

					<div class="modal-body">

						<div id="edit_terminal_container">
							<form name="edit_terminal_form"
								ng-submit="editTerminalInfo()">

								<div class="form-group">
									<label for="edit_terminal_port_input">Terminal Name</label> <input
										class="form-control" id="edit_terminal_name_input" type="text"
										ng-model="editTerminalConfigurationInputData.terminalName" />
								</div>

								<div class="form-group">
									<label for="edit_terminal_ip_input">Wifi IP</label> <input
										class="form-control" id="edit_terminal_ip_input" type="text"
										ng-model="editTerminalConfigurationInputData.ipAddress" />
								</div>

								<div class="form-group">
									<label for="edit_terminal_port_input">Wifi Port</label> <input
										class="form-control" id="edit_terminal_port_input" type="number"
										ng-model="editTerminalConfigurationInputData.port" />
								</div>

								<div class="text-right">
									<button type="submit" class="btn btn-info">Edit</button>
									<button type="button" class="btn btn-secondary"
										data-dismiss="modal" ng-click="">Close</button>
								</div>

							</form>

						</div>
					</div>

				</div>
			</div>
		</div>
		<!-- Edit Terminal Modal [END] -->
		
		
	</div>

</body>
</html>