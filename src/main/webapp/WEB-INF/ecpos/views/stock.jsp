<%@ page 
	import="mpay.ecpos_manager.general.utility.UserAuthenticationModel"%>
<%
	UserAuthenticationModel user = (UserAuthenticationModel) session.getAttribute("session_user");
%>

<!DOCTYPE html>
<html>
<head>
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
<link rel="stylesheet" href="${pageContext.request.contextPath}/keyboard/css/jkeyboard.css">
<script src="${pageContext.request.contextPath}/keyboard/js/jkeyboard.js"></script>
<script>
	$('#keyboard').hide();
	
    $('#keyboard').jkeyboard({
        layout: "english",
        input: $('#password')
    });
    
    setTimeout(function() { 
    	$('#keyboard').show();
    }, 100);
    
    $('#keyboard_1').hide();
	
    $('#keyboard_1').jkeyboard({
        layout: "numeric_2",
        input: $('#newValue')
    });
    
    setTimeout(function() { 
    	$('#keyboard_1').show();
    }, 100);
    
</script>
</head>

<body>
	<div ng-controller="stock_CTRL">
		<div ng-init="initiation();">
			<div class="content-wrapper" style="font-size: 0.9em;">
				<section class="content sectioncalibrator" style="padding-top: 8px; padding-bottom: 8px;">
					<div class="row container-fluid" style="padding-right: 2px; padding-left: 2px;">
						<div class="col-sm-12" style="padding-right: 2px; padding-left: 2px;">
							<div class="box box-success" style="height: 89vh; overflow-y: auto; background-color: white; margin-bottom: 0px; padding-top: 10px; padding-bottom: 10px; padding-left: 15px; padding-right: 15px;">
								<div class="row" style="text-align: center">
									<div class="col-sm-12">
										<font size="4">E-COMMERCE SALES</font>
									</div>
								</div>
								<br>
								<div class="row">
									<div class="col-sm-3 col-lg-2 form-group">
										<!-- <label>Select Date</label>  -->
										<input type="date" class="form-control" id="showDate" ng-model="dateStart" ng-change="getItemStockList()" max="{{maxDate}}" required />
									</div>
									<div class="col-sm-3 col-lg-2 form-group">
										<button class="btn btn-block btn-google center-block" style="background-color: #1F8CE8; color: white;" ng-click="showAddMenuItemModal()">Add New Item</button>
									</div>
									<div class="col-sm-3 col-lg-2 form-group">
										<button class="btn btn-block btn-google center-block" style="background-color: #1F8CE8; color: white;" ng-click="getSalesUpdateLog()">Get Log</button>
									</div>
								</div>
								<table id="datatable_stock" class="table table-bordered table-striped" style="width:100%;">
									<thead>
										<tr style="background-color: purple; color: white;">
											<th>ID</th>
											<th>Item Name</th>
											<th>Current Sales</th>
											<th>Action</th>
										</tr>
									</thead>
									<tbody></tbody>
									<tfoot></tfoot>
								</table>
							</div>
						</div>
					</div>
				</section>
			</div>
			
			<div class="modal fade" data-backdrop="static" id="itemStockModal" role="dialog">
				<div class="modal-dialog">
					<div class="modal-content">
						<form id="updateStock" ng-submit="updateItemStock(newValue)">
							<div class="modal-header text-center">
								<button type="button" class="close" data-dismiss="modal"
									ng-click="resetItemStockModal()" aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title">Update Sales</h4>
							</div>
							<div class="modal-body" style="padding: 30px;">
								
									<div class="row container-fluid" style="padding-right: 2px; padding-left: 2px;">
										<div class="col-sm-6">
											<p><font><b>Date : </b><span ng-bind="dateStart | date:'dd/MM/yyyy'"></span></font></p>
											<p><font><b>Name : </b>{{itemsDetail.name}}</font></p>
											<p><font><b>Current Stock : </b>{{itemsDetail.new_value}}</font></p>
											<p><label>New Value : </label>
												<input type="text" class="form-control" id="newValue" required></p>
										</div>
										<div class="col-sm-6" id="keyboard_1">
										</div>
									</div>
								
							</div>
							<div class="modal-footer">
								<div class="row" style="padding-top: 20px;">
									<div class="col-sm-12 text-center">
										<button class="btn btn-primary" type="submit" style="background-color: #1F8CE8; color: white;">UPDATE</button>
										<button class="btn btn-danger" data-dismiss="modal" ng-click="resetItemStockModal()" aria-label="Cancel">Cancel</button>
									</div>
								</div>
							</div>
						</form>
					</div>
				</div>
			</div>
			
			<div class="modal fade" id="updateLogModal" role="dialog">
				<div class="modal-dialog modal-lg" style="overflow-y: initial;">
					<div class="modal-content">
						<div class="modal-header text-center">
							<button type="button" class="close" data-dismiss="modal"
								ng-click="resetItemStockModal()" aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">Sales Update Log</h4>
						</div>
						<div class="modal-body" style="padding: 30px; height: 400px; overflow-y: auto;">
							<div class="row container-fluid" style="padding-right: 2px; padding-left: 2px;">
								<div class="row" ng-repeat="log in logList">
									<div class="col-sm-12">
										<strong>{{log.created_date}} :</strong> {{log.staff_name}} <strong>|</strong> {{log.event}}
									</div>
								</div>
								<div ng-show="!logList.length">There is no log to be found</div>
							</div>
							
						</div>
						<div class="modal-footer">
							<div class="row" style="padding-top: 20px;">
								<div class="col-sm-12 text-center">
									<button class="btn btn-danger" data-dismiss="modal" ng-click="resetUpdateLogModal()" aria-label="Cancel">Close</button>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			
			<div class="modal fade" id="addMenuItemModal" role="dialog"
				aria-labelledby="addMenuItemModal" aria-hidden="true">
				<div class="modal-dialog modal-sm">
					<div class="modal-content">
						<div class="modal-header text-center">
							<!-- <h5 ng-show="action=='create'" class="modal-title">Create Menu Item</h5>
		        			<h5 ng-show="action=='update'" class="modal-title">Edit Menu Item</h5> -->
		        			<h4 class="modal-title">Add Menu Item</h4>
							<button type="button" class="close" data-dismiss="modal"
								ng-click="resetAddMenuItemModal()" aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
						</div>
	
						<div class="modal-body">
							<div class="form-section">
								
								<div class="row">
									<div class="col-sm-12 col-lg-12 form-group">
										<label>Select Item</label>
										<select id="itemListDropDown" class="form-control" ng-model="selectedItemDropDown" ng-change=""
																ng-options="item.id as item.name for item in dropDownItemList">
												<option value="">-- SELECT --</option>
										</select>
									</div>
								</div>
							</div>
						</div>
	
						<div class="modal-footer">
							<div class="row" style="padding-top: 20px;">
								<div class="col-sm-12 text-center">
									<button class="btn btn-primary" type="submit" ng-click="addNewMenuItem()">Add Item</button>
									<button class="btn btn-danger" data-dismiss="modal" ng-click="resetAddMenuItemModal()" aria-label="Cancel">Cancel</button>
								</div>
							</div>
						</div>
	
					</div>
				</div>
			</div>
			
			<div class="modal fade" data-backdrop="static" id="salesPasswordModal"
				role="dialog">
				<div class="modal-dialog modal-lg">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close" ng-click="redirectToMainMenu()">
								<span aria-hidden="true">×</span>
							</button>
							<h4 class="modal-title">Enter Sales Password</h4>
						</div>
						<!-- <form ng-submit="submitOpenItem()"> -->
						<div class="modal-body">
							<div class="row">
								<div class="col-sm-1 form-group"></div>
								<div class="col-sm-10 form-group">
									<label style="font-size: medium;">Sales Password</label> <input
										type="password" class="form-control" id="password" required
										onfocus="blur();"/> <br>
									<div id="keyboard"></div>
									<br>
									<div style="text-align: center;">
										<button class="btn btn-primary" ng-click="checkSalesPassword()">Submit</button>
									</div>
								</div>
								<div class="col-sm-1 form-group"></div>
							</div>
						</div>
						<!-- <div class="modal-footer">
			                <button type="button" class="btn btn-danger pull-left" data-dismiss="modal">Close</button>
			                <button type="submit" class="btn btn-primary">Save changes</button>
		              	</div> -->
						<!-- </form> -->
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>