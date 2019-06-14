<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/select2.min.css">
<script src="${pageContext.request.contextPath}/select2/select2.full.min.js"></script>

<script>
	$(".select2").select2();
</script>

<style>
.sectioncalibrator {
	height: calc(100vh - 50px);
	/* overflow-y: scroll; */
}

@media only screen and (max-width:600px) {
	.sectioncalibrator {
		height: calc(100vh - 100px);
		overflow-y: scroll;
	}
}

hr {
	margin-top: 0px;
	margin-bottom: 5px;
}

.border-shadow {
	box-shadow: 1px 1px 1px 1px rgba(0,0,0,0.2);
}

.info-box{
	min-height: 60px;
}

.info-box-icon{
	height: 60px;
	line-height: 60px;
	font-size: 40px;
}

.info-box-content{
	height: 60px;
}

.custom-button{
	width: 130px;
}

.custom-card:hover {
	background-color: #d1e0e0;
}
</style>
</head>

<body>
	<div ng-controller="settings_CTRL">
		<div ng-init="initiation();">
			<div class="content-wrapper" style="font-size: 0.9em;">
				<section class="content sectioncalibrator" style="padding-top: 8px; padding-bottom: 8px;">
					<div class="row container-fluid" style="padding-right: 2px; padding-left: 2px;">
						<div class="col-sm-12" style="padding-right: 2px; padding-left: 2px;">
							<div class="well" style="height: 89vh; overflow-y: hidden; background-color: white; margin-bottom: 0px; padding: 15px;">
								<div class="row" style="text-align: center;">
								    <div class="col-sm-12">
										<font size="4">SETTINGS</font>
									</div>
								</div>
								<div class="row" style="margin-bottom:5px;">
								    <div class="col-sm-12">
										<font size="3"><b>Synchronization Configuration</b></font>
									</div>
								</div>
								<div class="row">
									<div class="col-sm-4"> 
									    <div class="custom-card info-box border-shadow" ng-click="submitSyncMenu()">
										  <!-- Apply any bg-* class to to the icon to color it -->
										  <span class="info-box-icon bg-blue"><i class="fa fa-object-group"></i></span>
										  <div class="info-box-content">						
										    <span class="info-box-text">Item Info</span>
										    <span class="info-box-number">Synchronize</span>
										  </div>
										</div>
									</div>
									<div class="col-sm-4"> 
									    <div class="custom-card info-box border-shadow" ng-click="submitSyncTransaction()">
										  <!-- Apply any bg-* class to to the icon to color it -->
										  <span class="info-box-icon bg-maroon"><i class="fa fa-exchange"></i></span>
										  <div class="info-box-content">
										    <span class="info-box-text">Transaction Info</span>
										    <span class="info-box-number">Synchronize</span>
										  </div>
										</div>
									</div>
									<div class="col-sm-4"> 
									    <div class="custom-card info-box border-shadow" ng-click="submitSyncStore()">
										  <!-- Apply any bg-* class to to the icon to color it -->
										  <span class="info-box-icon bg-olive"><i class="fa fa-users"></i></span>
										  <div class="info-box-content">
										    <span class="info-box-text">Store & Staff Info</span>
										    <span class="info-box-number">Synchronize</span>
										  </div>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-sm-4"> 
									    <div class="custom-card info-box border-shadow" ng-click="showTransConfigModal()">
										  <!-- Apply any bg-* class to to the icon to color it -->
										  <span class="info-box-icon bg-orange"><i class="fa fa-cogs"></i></span>
										  <div class="info-box-content">
										    <span class="info-box-text">Transaction Configuration</span>
										    <span class="info-box-number">Settings</span>
										  </div>
										</div>
									</div>
									<div class="col-sm-4"> 
									    <div class="custom-card info-box border-shadow" ng-click="showReactivationModal()">
										  <!-- Apply any bg-* class to to the icon to color it -->
										  <span class="info-box-icon bg-red"><i class="fa fa-plug"></i></span>
										  <div class="info-box-content">
										    <span class="info-box-text">Reactivation</span>
										    <span class="info-box-number">Reactivate</span>
										  </div>
										</div>
									</div>
							  	</div>
							  	<hr>
							  	<div class="row">
									<div class="col-sm-4">
										<div class="row" style="margin-bottom:5px;">
											<div class="col-sm-12"> 
												<font size="3"><b>Cash Drawer</b></font>
												<button type="button" class="btn btn-success btn-sm pull-right custom-button" ng-click="saveCashDrawer()">
												Update Cash Drawer
												</button>
											</div>
										</div>
										<div class="info-box border-shadow" style="padding:8px;border:1px solid;border-color:rgba(0,0,0,0.2);">
									    	<div class="row">
										    	<div class="col-sm-12">
													<font size="2">Device Manufacturer</font>
													<div style="border: 1px solid #d2d6de; padding: 10px; border-radius: 5px;">
														<select id="cashDrawerDeviceManufacturer" class="form-control" ng-model="selectedDeviceManufacturer" 
															ng-options="device_manufacturer.id as device_manufacturer.name for device_manufacturer in cashDrawerData.device_manufacturers">
																<option value="" disabled>-- SELECT --</option>
														</select>
													</div>
												</div>
											</div>
											<div class="row">	
												<div class="col-sm-12">
													<font size="2">Port Name</font>
													<div style="border: 1px solid #d2d6de; padding: 10px; border-radius: 5px;">
														<select id="cashDrawerDevicePortName" class="form-control" ng-model="selectedPortName" 
															ng-options="port_name.id as port_name.name for port_name in cashDrawerData.port_names">
																<option value="" disabled>-- SELECT --</option>
														</select>
													</div>
												</div>
											</div>
											<div class="row">	
												<div class="col-sm-12">
													<font size="2">Max Cash Alert</font>
													<div style="border: 1px solid #d2d6de; padding: 10px; border-radius: 5px;">
														<input type="number" step="1" class="form-control" ng-model="cashDrawerData.cash_alert">
													</div>
												</div>
											</div>
											<div class="row">
												<div class="col-sm-12">													
												    <button class="btn btn-primary btn-sm pull-right" style="margin: 4px 0 0 4px;" ng-click="showCashModal('cashOut')">Cash Out</button>
												    <button class="btn btn-primary btn-sm pull-right" style="margin: 4px 0 0 4px;" ng-click="showCashModal('cashIn')">Cash In</button>
												    <button class="btn btn-info btn-sm pull-right" style="margin: 4px 0 0 4px;" ng-click="showCashLogModal()">View Log</button>				
													<span class="pull-right" style="margin-top: 8px; margin-right: 15px; font-size:18px;"><b>Cash Amount : {{cashDrawerData.cash_amount | number : 2}}</b></span>											 
												</div>
											</div>
									    </div>
									</div>
									<div class="col-sm-8">
										<div class="row" style="margin-bottom:5px;">
											<div class="col-sm-12">
												<font size="3"><b>Terminal</b></font>
												<button type="button" class="btn btn-success btn-sm pull-right custom-button" ng-click="showTerminalModal('create')">
												Add Terminal
												</button>
											</div>
										</div>
										<div  id="terminalList" class="info-box border-shadow" style="height:157px; overflow-y:auto; overflow-x:hidden; padding:8px;border:1px solid;border-color:rgba(0,0,0,0.2);">
											<div class="row">
												<div class="col-sm-12">
													<table style="width:100%; padding:1px;">
														<thead>
															<tr>
																<th>No.  </th>
																<th>Name  </th>
																<th>Serial No.  </th>
																<th width="20%">Wifi IP  </th>
																<th>Wifi Port  </th>
																<th>Action  </th>
															</tr>
														</thead>
														<tbody>
															<tr ng-repeat="terminal in terminalList.terminals">
																<td>{{$index+1}}</td>
																<td>{{terminal.name}}</td>
																<td>{{terminal.serialNo}}</td>
																<td>{{terminal.wifiIP}}</td>
																<td>{{terminal.wifiPort}}</td>
																<td style="padding:1px;white-space:nowrap;">
																	<button class="btn btn-sm btn-info" ng-click="showSettlementModal(terminal.serialNo)">Settlement</button>
																	<button class="btn btn-sm btn-default" ng-click="pingTerminal(terminal.id)"><i class="fa fa-crosshairs"></i></button>
																	<button class="btn btn-sm btn-primary" ng-click="showTerminalModal('update', terminal.id)"><i class="fa fa-edit"></i></button>
																	<button class="btn btn-sm btn-danger" ng-click="removeTerminal(terminal.id)"><i class="fa fa-trash-o"></i></button>
																</td>
															</tr>
														</tbody>
													</table>
												</div>
											</div>
										</div>
										<hr>
										<div class="row">
											<div class="col-sm-6">
												<div class="row" style="margin-bottom:5px;">
													<div class="col-sm-12"> 
														<font size="3"><b>Receipt Printer</b></font>
														<button type="button" class="btn btn-success btn-sm pull-right custom-button" ng-click="saveReceiptPrinter()">
														Update Printer
														</button>
													</div>
												</div>
												<div class="info-box border-shadow" style="padding:8px;border:1px solid;border-color:rgba(0,0,0,0.2);">
											    	<div class="row">
														<div class="col-sm-12">
															<font size="2">Receipt Printer Manufacturer Model</font>
															<div style="border: 1px solid #d2d6de; padding: 10px; border-radius: 5px;">
																<select id="receiptPrinterManufacturer" class="form-control" ng-model="selectedReceiptPrinterManufacturer"
																	ng-options="manufacturer.id as manufacturer.name for manufacturer in receiptPrinterData.device_manufacturers">
																	<option value="" disabled>-- SELECT --</option>
																</select>
															</div>
														</div>
													</div>
												</div>
											</div>
										</div>
									</div>								
								</div>						  	
								<!-- <div class="row" style="text-align: center">
									<div class="col-sm-12">
										<font size="4">Settings</font>
									</div>
								</div>
								<br>
								<div class="row" style="padding-bottom: 8px;">
									<div class="col-sm-4" style="margin: 0px; margin-top: 6px;">
										<font size="3"><b>Cloud - Item Info</b></font>
									</div>
									<div class="col-sm-2">
										<button class="btn btn-block btn-info" ng-click="submitSyncMenu()">Synchronize</button>
									</div>
									<div class="col-sm-4" style="margin: 0px; margin-top: 6px;">
										<font size="3"><b>VERNPOS - Check, Transaction & Settlement Info</b></font>
									</div>
									<div class="col-sm-2">
										<button class="btn btn-block btn-info" ng-click="submitSyncTransaction()">Synchronize</button>
									</div>
								</div>
								<div class="row" style="padding-bottom: 8px;">
									<div class="col-sm-4" style="margin: 0px; margin-top: 6px;">
										<font size="3"><b>Cloud - Store & Staff Info</b></font>
									</div>
									<div class="col-sm-2">
										<button class="btn btn-block btn-info" ng-click="submitSyncStore()">Synchronize</button>
									</div>
									<div class="col-sm-4" style="margin: 0px; margin-top: 6px;">
										<font size="3"><b>VERNPOS - Reactivation</b></font>
									</div>
									<div class="col-sm-2">
										<button class="btn btn-block btn-danger" ng-click="showReactivationModal()">Reactivate</button>
									</div>
								</div>
								<hr>
								
								<div class="row" style="padding-bottom: 8px;">
									<div class="col-sm-12" style="padding-bottom: 8px;">
										<font size="3"><b>Cash Drawer Configuration</b></font>
									</div>
									
									<div class="col-sm-4">
										<font size="2">Device Manufacturer</font>
										<div style="border: 1px solid #d2d6de; padding: 10px; border-radius: 5px;">
											<select id="cashDrawerDeviceManufacturer" class="form-control" ng-model="selectedDeviceManufacturer" 
												ng-options="device_manufacturer.id as device_manufacturer.name for device_manufacturer in cashDrawerData.device_manufacturers">
													<option value="" disabled>-- SELECT --</option>
											</select>
										</div>
									</div>
									
									<div class="col-sm-4">
										<font size="2">Port Name</font>
										<div style="border: 1px solid #d2d6de; padding: 10px; border-radius: 5px;">
											<select id="cashDrawerDevicePortName" class="form-control" ng-model="selectedPortName" 
												ng-options="port_name.id as port_name.name for port_name in cashDrawerData.port_names">
													<option value="" disabled>-- SELECT --</option>
											</select>
										</div>
									</div>
									
									<div class="col-sm-4">
										<font size="2">Max Cash Alert</font>
										<div style="border: 1px solid #d2d6de; padding: 10px; border-radius: 5px;">
											<input type="number" step="1" class="form-control" ng-model="cashDrawerData.cash_alert">
										</div>
									</div>
									
									<div class ="col-sm-6">
										<div style="padding: 10px;">
											<font size="3"><b>Cash Amount: {{cashDrawerData.cash_amount | number : 2}}</b></font>
											<button class="btn btn-info" ng-click="showCashModal('cashIn')">Cash In</button>
											<button class="btn btn-info" ng-click="showCashModal('cashOut')">Cash Out</button>
										</div>
									</div>
									
									<div class ="col-sm-6">
										<font size="2"></font>
										<div style="padding: 10px; text-align: right">
											<button class="btn btn-info" ng-click="saveCashDrawer()">Set</button>
										</div>
									</div>
								</div>
								<hr>

								<div class="row" style="padding-bottom: 8px;">
									<div class="col-sm-12" style="padding-bottom: 8px;">
										<font size="3"><b>Receipt Printer Configuration</b></font>
									</div>
									
									<div class="col-sm-6">
										<font size="2">Receipt Printer Manufacturer Model</font>
										<div style="border: 1px solid #d2d6de; padding: 10px; border-radius: 5px;">
											<select id="receiptPrinterManufacturer" class="form-control" ng-model="selectedReceiptPrinterManufacturer"
												ng-options="manufacturer.id as manufacturer.name for manufacturer in receiptPrinterData.device_manufacturers">
												<option value="" disabled>-- SELECT --</option>
											</select>
										</div>
									</div>
									
									<div class ="col-sm-4">
										<font size="2"></font>
										<div style="padding: 10px;">
											<button class="btn btn-info" ng-click="saveReceiptPrinter()">Set</button>
										</div>			
									</div>
								</div>
								<hr>
								<div class="row" style="padding-bottom: 8px;">
									<div class="col-sm-12">
										<font size="3"><b>Terminal Configuration</b></font>
										<button type="button" class="btn btn-social pull-right btn-primary bg-aqua" ng-click="showTerminalModal('create')">
											<i class="fa fa-plus"></i> Add Terminal
										</button>
									</div>
								</div>
								<div id="terminalList">
									<div class="row">
										<div class='col-sm-1 text-center'><b>No</b></div>
										<div class='col-sm-2 text-left'><b>Terminal Name</b></div>
										<div class='col-sm-2 text-center'><b>Serial Number</b></div>
										<div class='col-sm-2 text-center'><b>Wifi IP</b></div>
										<div class='col-sm-2 text-center'><b>Wifi Port</b></div>
										<div class='col-sm-3 text-center'></div>
									</div>
									<hr>
									<div ng-repeat="terminal in terminalList.terminals">
										<div class="row" style="padding-bottom: 5px;">
											<div class='col-sm-1 text-center' style="margin-top: 7px;">{{$index+1}}</div>
											<div class='col-sm-2 text-left' style="margin-top: 7px;">{{terminal.name}}</div>
											<div class='col-sm-2 text-center' style="margin-top: 7px;">{{terminal.serialNo}}</div>
											<div class='col-sm-2 text-center' style="margin-top: 7px;">{{terminal.wifiIP}}</div>
											<div class='col-sm-2 text-center' style="margin-top: 7px;">{{terminal.wifiPort}}</div>
											<div class='col-sm-3 text-left' style="padding: 0px;">
												<button class="btn btn-info" ng-click="showSettlementModal(terminal.serialNo)">Settlement</button>
												<button class="btn btn-default" ng-click="pingTerminal(terminal.id)"><i class="fa fa-crosshairs"></i></button>
												<button class="btn btn-primary" ng-click="showTerminalModal('update', terminal.id)"><i class="fa fa-edit"></i></button>
												<button class="btn btn-danger" ng-click="removeTerminal(terminal.id)"><i class="fa fa-trash-o"></i></button>
											</div>
										</div>
										<hr>
									</div>
								</div> -->
							</div>
						</div>	
					</div>
				</section>
			</div>
			
			<div class="modal fade" data-backdrop="static" id="cashModal" role="dialog">
				<div class="modal-dialog">
					<div class="modal-content">
						<!-- <div class="modal-header"></div> -->
						<div class="modal-body">
							<div class="row" style="font-size: large">
								<div class="col-sm-1"></div>
								<div class="col-sm-10">
									<div style="text-align: center">
										<label ng-if="action=='cashIn'">Cash In</label> <label
											ng-if="action=='cashOut'">Cash Out</label>
									</div>
								</div>
								<div class="col-sm-1">
									<button class="close" data-dismiss="modal">&times;</button>
								</div>
							</div>
							<br>
							<div class="row">
								<div class="col-sm-6 form-group">
									<label ng-if="action=='cashIn'">Cash In Amount</label> <label
										ng-if="action=='cashOut'">Cash Out Amount</label> <input
										type="text" class="form-control" ng-model="cashFlowAmount"
										disabled />
								</div>
								<div class="col-sm-6 form-group">
									<button class="btn btn-default btn-lg" type="button"
										style="margin: 2px;" ng-click="updateCashAmount(1)">1</button>
									<button class="btn btn-default btn-lg" type="button"
										style="margin: 2px;" ng-click="updateCashAmount(2)">2</button>
									<button class="btn btn-default btn-lg" type="button"
										style="margin: 2px;" ng-click="updateCashAmount(3)">3</button>
									<br>
									<button class="btn btn-default btn-lg" type="button"
										style="margin: 2px;" ng-click="updateCashAmount(4)">4</button>
									<button class="btn btn-default btn-lg" type="button"
										style="margin: 2px;" ng-click="updateCashAmount(5)">5</button>
									<button class="btn btn-default btn-lg" type="button"
										style="margin: 2px;" ng-click="updateCashAmount(6)">6</button>
									<br>
									<button class="btn btn-default btn-lg" type="button"
										style="margin: 2px;" ng-click="updateCashAmount(7)">7</button>
									<button class="btn btn-default btn-lg" type="button"
										style="margin: 2px;" ng-click="updateCashAmount(8)">8</button>
									<button class="btn btn-default btn-lg" type="button"
										style="margin: 2px;" ng-click="updateCashAmount(9)">9</button>
									<br>
									<button class="btn btn-default btn-lg" type="button"
										style="margin: 2px; visibility: hidden;">0</button>
									<button class="btn btn-default btn-lg" type="button"
										style="margin: 2px;" ng-click="updateCashAmount(0)">0</button>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-12">
									<div class="pull-right">
										<input type="button" class="btn btn-info" value="Clear"
											ng-click="clearCashAmount()" /> <input type="button"
											class="btn btn-info" value="Update"
											ng-click="submitCashInfo()" />
									</div>
								</div>
							</div>
						</div>
						<!-- <div class="modal-footer"></div> -->
					</div>
				</div>
			</div>
			
			<div class="modal fade" data-backdrop="static" id="cashLogModal" role="dialog">
				<div class="modal-dialog modal-lg">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">&times;
							</button>
							<h4 class="modal-title" style="text-align: center;">Cash Log Listing</h4>
						</div>
						<div class="modal-body">
							<table id="datatable_cashflowlog"
								class="table table-bordered table-striped" style="margin: 0 auto;">
								<thead>
									<tr>
										<th>ID</th>
										<th>Amount</th>
										<th>New Amount</th>
										<th>Reference</th>
										<th>By</th>
										<th>Date Time</th>
									</tr>
								</thead>
								<tbody></tbody>
								<tfoot></tfoot>
							</table>
						</div>
						<!-- <div class="modal-footer"></div> -->
					</div>
				</div>
			</div>
			
			<div class="modal fade" data-backdrop="static" id="terminalModal" role="dialog">
				<div class="modal-dialog">
					<div class="modal-content">
						<!-- <div class="modal-header"></div> -->
						<div class="modal-body">
							<form ng-submit="submitTerminalInfo()">
								<div class="row" style="font-size: large">
									<div class="col-sm-1"></div>
									<div class="col-sm-10">
										<div style="text-align: center">
											<label ng-if="action=='create'">Add Terminal</label>
											<label ng-if="action=='update'">Update Terminal</label>
										</div>
									</div>
									<div class="col-sm-1">
										<button class="close" data-dismiss="modal">&times;</button>
									</div>
								</div>
								<br>
								<div class="row">
									<div class="col-sm-6 form-group">
										<label>Terminal Name</label> 
										<input type="text" class="form-control" ng-model="terminal.name" required />
									</div>
								</div>
								<div class="row">
									<div class="col-sm-6 form-group">
										<label>Serial Number</label> 
										<input type="text" class="form-control" ng-model="terminal.serialNo" required />
									</div>
								</div>
								<div class="row">
									<div class="col-sm-4 form-group">
										<label>WiFi IP</label> 
										<input type="text" class="form-control" ng-model="terminal.wifiIP" />
									</div>
								</div>
								<div class="row">
									<div class="col-sm-4 form-group">
										<label>WiFi Port</label> 
										<input type="text" class="form-control" ng-model="terminal.wifiPort" />
									</div>
								</div>
								<div class="row">
									<div class="col-sm-12" >
										<div class="pull-right">
											<input ng-if="action=='create'" type="submit" class="btn btn-info" value="Add" />  
											<input ng-if="action=='update'" type="submit" class="btn btn-info" value="Update" />  
										</div>
									</div>
								</div>
							</form>
						</div>
						<!-- <div class="modal-footer"></div> -->
					</div>
				</div>
			</div>
			
			<div class="modal fade" data-backdrop="static" id="reactivationModal" role="dialog">
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<h4 class="modal-title text-center">Reactivation</h4>
						</div>
						<div class="modal-body">
							<form>
								<div class="row">
									<div class="col-sm-6 form-group">
										<label>Brand ID</label> 
										<input type="text" class="form-control" ng-model="reactivate.brandId" required />
									</div>
								</div>
								<div class="row">
									<div class="col-sm-6 form-group">
										<label>Activation ID</label> 
										<input type="text" class="form-control" ng-model="reactivate.activationId" required />
									</div>
								</div>
								<div class="row">
									<div class="col-sm-6 form-group">
										<label>Activation Key</label> 
										<input type="text" class="form-control" ng-model="reactivate.activationKey" />
									</div>
								</div>
							</div>
							<div class="modal-footer">
								<div class="row">
									<div class="col-sm-12" >
										<div class="pull-right">
											<button class="btn btn-danger" data-dismiss="modal">Cancel</button>
											<input type="submit" ng-click="submitReactivation()" class="btn btn-info" value="Reactivate" />  
										</div>
									</div>
								</div>
							</form>
						</div>
						<!-- <div class="modal-footer"></div> -->
					</div>
				</div>
			</div>
			
			<div class="modal fade" data-backdrop="static" id="transConfigModal" role="dialog">
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<h4 class="modal-title text-center">Transaction Configuration </h4>
						</div>
						<form>
							<div class="modal-body">						
								<div class="row">
									<div class="col-sm-6 form-group">
										<label>Synchronize during staff login</label>
										<div>
											<label>
												<input type="radio" ng-model="transConfig.staffSyncFlag" class="form-check-input" ng-value=false checked> False
											</label>
											<label>
												<input type="radio" ng-model="transConfig.staffSyncFlag" class="form-check-input" ng-value=true checked> True
											</label>
										</div> 
									</div>
								</div>
								<div class="row">
									<div class="col-sm-6 form-group">
										<label>Synchronize during transaction performed</label>
										<div>
											<label>
												<input type="radio" ng-model="transConfig.transSyncFlag" class="form-check-input" ng-value=false checked> False
											</label>
											<label>
												<input type="radio" ng-model="transConfig.transSyncFlag" class="form-check-input" ng-value=true checked> True
											</label>
										</div> 
									</div>
								</div>
								<div class="row">
									<div class="col-sm-6 form-group">
										<label>Interval synchronization</label>
										<div style="border: 1px solid #d2d6de; padding: 10px; border-radius: 5px;">
											<select class="form-control" ng-model="transConfig.selectedInterval" 
												ng-options="interval.id as interval.intervalSyncName for interval in transConfig.intervalList">
													<option value="" disabled>-- SELECT --</option>
											</select>
										</div>
									</div>
								</div>
							</div>
							<div class="modal-footer">
								<div class="row">
									<div class="col-sm-12" >
										<div class="pull-right">
											<button class="btn btn-danger" data-dismiss="modal">Cancel</button>
											<input type="submit" ng-click="submitTransConfig()" class="btn btn-info" value="Update" />  
										</div>
									</div>
								</div>					
							</div>
						</form>
						<!-- <div class="modal-footer"></div> -->
					</div>
				</div>
			</div>
			
			<div class="modal fade" data-backdrop="static" id="settlementModal" role="dialog">
				<div class="modal-dialog">
					<div class="modal-content">
						<!-- <div class="modal-header"></div> -->
						<div class="modal-body">
							<form ng-submit="requestSettlement()">
								<div class="row" style="font-size: large">
									<div class="col-sm-1"></div>
									<div class="col-sm-10">
										<div style="text-align: center">
											<label>Settlement</label>
										</div>
									</div>
									<div class="col-sm-1">
										<button class="close" data-dismiss="modal">&times;</button>
									</div>
								</div>
								<br>
								<label>Kindly choose a settlement type:</label>
								<br><br>
								<div class="row" >
									<div class="col-sm-6 form-group">
										<label>
											<input type="radio" ng-model="settlementType" value="1" ng-required=!settlementType /> <b>VISA/MASTER/JCB</b>
										</label>
									</div>
									<div class="col-sm-6 form-group">
										<label> 
											<input type="radio" ng-model="settlementType" value="2" ng-required=!settlementType /> <b>AMEX</b>
										</label>
									</div>
								</div>
								<br>
								<div class="row" >
									<div class="col-sm-6 form-group">
										<label>
											<input type="radio" ng-model="settlementType" value="3" ng-required=!settlementType /> <b>MCCS</b>
										</label>
									</div>
									<div class="col-sm-6 form-group">
										<label> 
											<input type="radio" ng-model="settlementType" value="4" ng-required=!settlementType /> <b>UNIONPAY</b>
										</label>
									</div>
								</div>
								<br>
								<div class="row">
									<div class="col-sm-12" >
										<div class="pull-right">
											<input type="submit" class="btn btn-info" value="Proceed Settlement" />
										</div>
									</div>
								</div>
							</form>
						</div>
						<!-- <div class="modal-footer"></div> -->
					</div>
				</div>
			</div>
			
			<!-- Loading Modal [START] -->
			<div class="modal fade" data-backdrop="static" id="loading_modal" role="dialog">
				<div class="modal-dialog modal-sm">
				<div class="modal-content">
					<div class="modal-body">
						<div class="text-center">
							<img style="width:75%" src="${pageContext.request.contextPath}/img/gif/loading.gif"><br>
								<span>Loading Data...</span>
						</div>
					</div>
				</div>
				</div>
			</div>
			<!-- Loading Modal [END] -->
			
			<!-- Ping Loading Modal [START] -->
			<div class="modal fade" data-backdrop="static" id="ping_loading_modal" role="dialog">
				<div class="modal-dialog modal-sm">
				<div class="modal-content">
					<div class="modal-body">
						<div class="text-center">
							<img style="height:25vh;" src="${pageContext.request.contextPath}/img/gif/loading.gif"><br>
								<span>Pinging Terminal...</span>
						</div>
					</div>
				</div>
				</div>
			</div>
			<!-- Ping Loading Modal [END] -->
			
			<div class="modal fade" id="modal-dialog" tabindex="-1" role="dialog"
				aria-hidden="true">
				<div class="modal-dialog modal-sm">
					<div class="modal-content">
						<div class="modal-header">
						<!-- <button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								&times;
							</button> -->
							<h4 class="modal-title"
								ng-show="dialogData.title != ''">{{dialogData.title}}</h4>
						</div>
						<div class="modal-body"
							ng-show="dialogData.message != ''">{{dialogData.message}}</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-primary btn-main"
								ng-click="dialogData.button1.fn()" ng-show="dialogData.isButton1">{{dialogData.button1.name}}</button>
							<button type="button" class="btn btn-primary btn-main"
								ng-click="dialogData.button2.fn()" ng-show="dialogData.isButton2">{{dialogData.button2.name}}</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>