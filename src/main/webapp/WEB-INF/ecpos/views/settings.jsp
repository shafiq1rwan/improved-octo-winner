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
</style>
</head>

<body>
	<div ng-controller="settings_CTRL">
		<div ng-init="initiation();">
			<div class="content-wrapper" style="font-size: 0.9em;">
				<section class="content sectioncalibrator" style="padding-right: 15px; padding-left: 15px;">
					<div class="row container-fluid" style="padding-right: 2px; padding-left: 2px;">
						<div class="col-md-12" style="padding-right: 2px; padding-left: 2px;">
							<div class="well" style="background-color: white; margin-bottom: 0px; height: 87vh; padding-top: 0px;">
								<div class="row">
									<div class="col-sm-12" style="text-align: center">
										<h3>Settings</h3>
									</div>
								</div>
								<br>
								<div class="row">
									<div class="col-md-3 form-group" style="margin: 0px;">
										<label style="font-size: medium; margin-top: 6px;">Cloud Synchronization</label>
									</div>
									<div class="col-md-2 form-group">
										<button class="btn btn-block btn-info">Sync</button>
									</div>
								</div>
								<hr style="margin-top: 5px; margin-bottom: 18px;">
								<div class="row">
									<div class="col-md-12 form-group" style="margin: 0px;">
										<label style="font-size: medium; margin-top: 6px;">Printer Configuration</label>
									</div>
									<div class="col-md-5 form-group" style="margin: 0px;">
										<label style="margin-top: 6px;">Printer Model</label>
										<div style="border: 1px solid #d2d6de; padding: 10px; border-radius: 5px;">
											<select id="printer" class="select2 form-control" style="width: 100%;" ng-click="savePrinter()">
												<option selected>----- Select -----</option>
												<option ng-repeat="printer in printerDetail.portInfoList" value="{{printer.PortInfo.PortName}}!!{{printer.PortInfo.ModelName}}">{{printer.PortInfo.PortName}}</option>
											</select>
										</div>
									</div>
								</div>
								<hr>
								<div class="row">
									<div class="col-md-12 form-group" style="margin: 0px;">
										<label style="font-size: medium; margin-top: 6px;">Terminal Configuration</label>
										<button type="button" class="btn btn-social pull-right btn-primary bg-aqua" ng-click="showTerminalModal('create')">
											<i class="fa fa-plus"></i> Add Terminal
										</button>
									</div>
								</div>
								<br>
								<div id="terminalList">
									<div class="row">
										<div class='col-sm-1 text-center'><b>No</b></div>
										<div class='col-sm-2 text-left'><b>Terminal Name</b></div>
										<div class='col-sm-2 text-center'><b>Serial Number</b></div>
										<div class='col-sm-2 text-center'><b>Wifi IP</b></div>
										<div class='col-sm-2 text-center'><b>Wifi Port</b></div>
										<div class='col-sm-3 text-center'></div>
									</div>
									<hr style="margin-top: 5px; margin-bottom: 5px;">
									<div ng-repeat="terminal in terminalList.terminals">
										<div class="row">
											<div class='col-sm-1 text-center' style="margin-top: 7px;">{{$index+1}}</div>
											<div class='col-sm-2 text-left' style="margin-top: 7px;">{{terminal.name}}</div>
											<div class='col-sm-2 text-center' style="margin-top: 7px;">{{terminal.serialNo}}</div>
											<div class='col-sm-2 text-center' style="margin-top: 7px;">{{terminal.wifiIP}}</div>
											<div class='col-sm-2 text-center' style="margin-top: 7px;">{{terminal.wifiPort}}</div>
											<div class='col-sm-3 text-left'>
												<button class="btn btn-info" ng-click="showSettlementModal(terminal.id)">Settlement</button>
												<button class="btn btn-primary" style="margin-left: 12%; margin-right: 12%;" ng-click="showTerminalModal('update', terminal.id)"><i class="fa fa-edit"></i></button>
												<button class="btn btn-danger" ng-click="removeTerminal(terminal.serialNo)"><i class="fa fa-trash-o"></i></button>
											</div>
										</div>
										<hr style="margin-top: 5px; margin-bottom: 5px;">
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
										<input type="text" class="form-control" ng-model="terminal.wifiIP" required />
									</div>
								</div>
								<div class="row">
									<div class="col-sm-4 form-group">
										<label>WiFi Port</label> 
										<input type="text" class="form-control" ng-model="terminal.wifiPort" required />
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
											<input type="radio" ng-model="settlementType" value="NR" ng-required=!settlementType /> <b>VISA/MASTER/JCB</b>
										</label>
									</div>
									<div class="col-sm-6 form-group">
										<label> 
											<input type="radio" ng-model="settlementType" value="AMEX" ng-required=!settlementType /> <b>AMEX</b>
										</label>
									</div>
								</div>
								<br>
								<div class="row" >
									<div class="col-sm-6 form-group">
										<label>
											<input type="radio" ng-model="settlementType" value="MCCS" ng-required=!settlementType /> <b>MCCS</b>
										</label>
									</div>
									<div class="col-sm-6 form-group">
										<label> 
											<input type="radio" ng-model="settlementType" value="UNIONPAY" ng-required=!settlementType /> <b>UNIONPAY</b>
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
		</div>
	</div>
</body>
</html>