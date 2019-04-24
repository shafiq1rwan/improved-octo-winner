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
	overflow-y: scroll;
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
</style>
</head>

<body>
	<div ng-controller="settings_CTRL">
		<div ng-init="initiation();">
			<div class="content-wrapper" style="font-size: 0.9em;">
				<section class="content sectioncalibrator" style="padding-top: 8px; padding-bottom: 8px;">
					<div class="row container-fluid" style="padding-right: 2px; padding-left: 2px;">
						<div class="col-sm-12" style="padding-right: 2px; padding-left: 2px;">
							<div class="well" style="height: 89vh; overflow-y: auto; background-color: white; margin-bottom: 0px; padding: 15px;">
								<div class="row" style="text-align: center">
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
										<font size="3"><b>ECPOS - Check, Transaction & Settlement Info</b></font>
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
										<font size="3"><b>ECPOS - Reactivation</b></font>
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
									
									<div class ="col-sm-4">
										<font size="2"></font>
										<div style="padding: 10px;">
											<button class="btn btn-info" ng-click="saveCashDrawer()">Set</button>
										</div>			
									</div>
								</div>
								<hr>

								<div class="row" style="padding-bottom: 8px;">
									<div class="col-sm-12" style="padding-bottom: 8px;">
										<font size="3"><b>Printer Configuration</b></font>
									</div>
									<div class="col-sm-6">
										<font size="2">Printer Model</font>
										<div style="border: 1px solid #d2d6de; padding: 10px; border-radius: 5px;">
											<select id="printer" class="select2 form-control" style="width: 100%;" ng-click="savePrinter()">
												<option selected>----- Select -----</option>
												<option ng-repeat="printer in printerDetail.portInfoList" value="{{printer.PortInfo.PortName}}!!{{printer.PortInfo.ModelName}}">{{printer.PortInfo.PortName}}</option>
											</select>
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
												<button class="btn btn-primary" ng-click="showTerminalModal('update', terminal.id)"><i class="fa fa-edit"></i></button>
												<button class="btn btn-danger" ng-click="removeTerminal(terminal.id)"><i class="fa fa-trash-o"></i></button>
											</div>
										</div>
										<hr>
									</div>
								</div>
							</div>
						</div>	
					</div>
				</section>
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