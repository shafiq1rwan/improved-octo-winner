<%@ page import="mpay.ecpos_manager.general.utility.UserAuthenticationModel"%>

<%
	UserAuthenticationModel user = (UserAuthenticationModel) session.getAttribute("session_user");
%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" href="${pageContext.request.contextPath}/keyboard/css/jkeyboard.css">
<script src="${pageContext.request.contextPath}/keyboard/js/jkeyboard.js"></script>
<script>
	$('#keyboard').hide();
	
    $('#keyboard').jkeyboard({
        layout: "english",
        input: $('#editField')
    });
    
    setTimeout(function() { 
    	$('#keyboard').show();
    }, 100);
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

@media (min-width: @screen-xs-max) and (max-width: @screen-sm-max) {
	.sectioncalibrator2 {
		position: relative;
  		min-height: 1px;
  		padding-right: 15px;
  		padding-left: 15px;
  		float: left;
  		width: 33.1%;
	}
}
.shadowBox {
  box-shadow: 1px 1px 4px grey;
}

.test:hover {
  background-color: #f6f6f6;
  /* outline: 2px solid #ababab; */
  /* border: 2px solid #696969; */
}

.modal-lg {
    width: 1000px;
}
</style>
</head>

<body>
	<div ng-controller="table_order_CTRL">
		<div ng-init="initiation();">
			<div class="content-wrapper" style="font-size: 0.9em;">
				<section class="content sectioncalibrator" style="padding-top: 8px;">
					<div class="row container-fluid" style="padding-right: 2px; padding-left: 2px;">
						<%if (user.getStoreType() == 2) {%>
						<div ng-repeat="table in manager_tablelist" class="col-xs-6 col-sm-2 sectioncalibrator2" style="padding-left: 12px; padding-right: 12px;">
							<div ng-click="get_table_checklist(table.table_number)" ng-model="table.table_number">
								<div class="panel text-center shadowBox test" ng-style="{'border-color':display_table_check_no(table.total_check)}">
									<div class="panel-heading" ng-style="{'background-color':display_table_check_no(table.total_check)}">
										<h3 class="panel-title" ng-style="{'color':display_table_check_no_title_color(table.total_check)}">CHECKS ({{table.total_check}})</h3>
									</div>
									<div class="panel-body" style="color: grey; font-weight: bold; font-size: small;">
										TABLE<br>{{table.table_name}}
									</div>
								</div>
							</div>
						</div>
						<%} else { %>
						<div ng-repeat="roomType in roomTypeList" class="col-xs-6 col-sm-2 sectioncalibrator2" style="padding-left: 12px; padding-right: 12px;">
							<div ng-click="get_room_list(roomType.id,roomType.name)" ng-model="roomType.id">
								<div class="panel text-center shadowBox test" ng-style="{'border-color':display_table_check_no(2)}">
									<div class="panel-heading" ng-style="{'background-color':'purple'}">
										<h3 class="panel-title" ng-style="{'color':display_table_check_no_title_color(2)}">{{roomType.name}}</h3>
									</div>
									<div class="panel-body" style="color: grey; font-weight: bold; font-size: small;">
										<img ng-src="${pageContext.request.contextPath}/{{roomType.image_path}}" alt={{roomType.name}} style="max-width: 100%; max-height: 100%;" />
									</div>
								</div>
							</div>
						</div>
						<%} %>
					</div>
				</section>
			</div>
			<div class="modal fade" id="modal_table_check_list" role="dialog" data-keyboard="false" data-backdrop="static" style="border-radius: 25px">
				<div class="modal-dialog">
					<div class="modal-content">
						<!-- <div class="modal-header">
							<h4 class="modal-title text-center">
								<i class="fa fa-info-circle"></i> <b>INFORMATION</b>
							</h4>
						</div> -->
						<div class="modal-body text-center">
							<div id="select_trxtypeModal_tblno" style="color: gray; font-weight: bold; font-size: large;"></div>
						</div>
						<div class="modal-footer" style="padding: 30px;">
							<div class="row">
								<div class="col-xs-6 col-sm-3 col-lg-4" ng-repeat="check in checks" style="padding-left: 10px; padding-right: 10px;">
									<div data-dismiss='modal'>
										<div class="panel panel-default text-center shadowBox test" style="border-color: #1EC676">
											<div class="panel-heading" style="color: #333333; background-color: #1EC676">
												<h3 class="panel-title" style="color: white;">CHECK NO</h3>
											</div>
											<div class="panel-body" ng-click="redirect_to_check_detail(check.check_number)">
												<div class="panel-body center-block" style="color: grey; font-weight: bold; font-size: medium;">
													{{check.check_ref_no}}
												</div>
											</div>
										</div>
									</div>
								</div>
	
								<div class="col-xs-6 col-sm-3 col-lg-4" style="padding-left: 10px; padding-right: 10px;">
									<div data-dismiss='modal'>
										<div class="panel panel-default text-center shadowBox test" style="border-color: black;">
											<div class="panel-heading" style="background-color: black;">
												<h3 class="panel-title" style="color: white;">NEW</h3>
											</div>
											<div class="panel-body" ng-click="create_new_check('','')">
												<div class="panel-body center-block" style="color: grey; font-weight: bold; font-size: medium;">
													<i class="fa fa-plus" aria-hidden="true"></i>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class="row">
								<button class="btn btn-block btn-google center-block" style="width: 97%; background-color: #ff0000d0;" data-dismiss="modal">CANCEL</button>
							</div>
						</div>
					</div>
				</div>
			</div>
			
			<div class="modal fade" id="modal_room_list" role="dialog" data-keyboard="false" data-backdrop="static">
				<div class="modal-dialog modal-lg">
					<div class="modal-content">
						<!-- <div class="modal-header">
							<h4 class="modal-title text-center">
								<i class="fa fa-info-circle"></i> <b>INFORMATION</b>
							</h4>
						</div> -->
						<div class="modal-body text-center">
							<div id="roomListModalTitle" style="background-color: purple; color: white; font-weight: bold; font-size: large;"></div>
						</div>
						<div class="modal-footer" style="padding: 30px;">
							<div class="row container-fluid" style="padding-right: 2px; padding-left: 2px;">
								<select id="roomStatusList" class="form-control" ng-model="status.id"
									ng-options="status.id as status.room_status for status in roomStatusList"
									ng-change="get_room_list_by_status(status.id)">
									<option value="">ALL</option>
								</select>
								</br>
								<div role="tabpanel">
				                    <ul class="nav nav-tabs" role="tablist">
				                        <li ng-repeat="floor in floorList" ng-class="{'active' : floorNo == floor}" 
				                        style="cursor: pointer; text-align: center; width: 20%; font-weight: bold; font-size: small; border-radius: 25px;">
				                        	<a ng-click="setFloorNo(floor)" data-toggle="tab" 
				                        	style="border-radius: 50px 50px 0px 0px;">LEVEL {{floor}}</a>
										</li>
				                        <li ng-show="!floorList.length">There is no result found</li>
				                    </ul>
				                </div>
				                </br>
				                <div class="tab-content">
									<div ng-repeat="room in roomList" ng-if="room.floor_no == floorNo" class="col-xs-6 col-sm-4 col-md-2 sectioncalibrator2" style="padding-left: 12px; padding-right: 12px;">
										<div ng-click="proceed_to_roomCheck(room.id,room.room_status_id,room.check_no)" ng-model="room.id">
											<div class="panel text-center shadowBox test" ng-style="{'border-color':display_table_check_no(2)}">
												<div class="panel-heading" ng-style="{'background-color':'{{room.status_bg_color}}'}">
													<h3 class="panel-title" ng-style="{'color':'#FFFFFF'}">{{room.room_status}}<span ng-if="room.check_no != null && room.room_status_id == 3">&nbsp;({{room.check_no}})</span></h3>
												</div>
												<div class="panel-body" style="font-weight: bold; font-size: 14px; height: 60px; ">
													{{room.room_name}}&nbsp;|&nbsp;<span style="color: grey;">{{room.room_category_name}}</span>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class="row">
								<button class="btn btn-block btn-google center-block" style="width: 97%; background-color: #ff0000d0;" data-dismiss="modal">CANCEL</button>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="modal fade" id="modal_user_details" role="dialog" data-keyboard="true" data-backdrop="static" style="border-radius: 25px">
				<div class="modal-dialog">
					<div class="modal-content">
						<!-- <div class="modal-header">
							
						</div> -->
						<div class="modal-body">
							<h4 class="modal-title text-center">
								<i class="fa fa-info-circle"></i> <b>INFORMATION</b>
							</h4>
						</div>
						<div class="modal-footer" style="padding: 30px; text-align: left;">
							<form id="customerDetailForm" ng-submit="create_new_check(customerName,customerPhone)">
								<div class="form-group">
									<label for="customerName" class="col-form-label">Customer Name:</label>&nbsp;<span style="color: red;">*</span> 
									<input type="text" class="form-control" ng-model="customerName" ng-required="true" ng-click="showKeyboard('Customer Name',customerName)" />
								</div>
								<div class="form-group">
									<label for="customerPhone" class="col-form-label">Customer Phone Number:</label>&nbsp;<span style="color: red;">*</span> 
									<input type="text" class="form-control" ng-model="customerPhone" ng-required="true" ng-click="showKeyboard('Customer Phone Number',customerPhone)" />
								</div>
								<div class="row">
									<button class="btn btn-block btn-google center-block" type="submit" style="width: 97%; background-color: #1F8CE8; color: white;">PROCEED</button>
								</div>
								<br/>
								<div class="row">
									<button class="btn btn-block btn-google center-block" style="width: 97%; background-color: #ff0000d0;" data-dismiss="modal">CANCEL</button>
								</div>
							</form>
						</div>
					</div>
				</div>
			</div>
			<div class="modal fade" id="modal_keyboard" role="dialog" data-keyboard="true" data-backdrop="static" style="border-radius: 25px">
				<div class="modal-dialog modal-lg">
					<div class="modal-content">
						<!-- <div class="modal-header">
							
						</div> -->
						<div class="modal-body">
							<!-- <h4 class="modal-title text-center">
								<i class="fa fa-info-circle"></i> <b>INFORMATION</b>
							</h4> -->
						</div>
						<div class="modal-footer" style="padding: 30px; text-align: left;">
							<!-- <div class="row" style="text-align: center">
								<div class="col-sm-10">
									<font size="4">DEPOSIT ORDER</font>
								</div>
								<div class="col-sm-1"></div>
							</div> -->
							<div class="row">
								<div class="col-sm-1 form-group"></div>
								<div class="col-sm-10 form-group">
									<label style="font-size: medium;">{{targetField}}</label> 
									<input type="text" class="form-control" id="editField" ng-model="targetValue" required onfocus="blur();"/>
									<br>
									<div id="keyboard"></div>
									<br>
									<div style="text-align: center;">
										<button class="btn btn-success btn-google center-block" style="width: 97%;" ng-click="retrieveData()">OK</button>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>