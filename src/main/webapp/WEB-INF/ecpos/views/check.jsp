<%@ page
	import="mpay.ecpos_manager.general.utility.UserAuthenticationModel"%>

<%
	UserAuthenticationModel user = (UserAuthenticationModel) session.getAttribute("session_user");
%>

<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/adminLTE-2.4.5/plugins/iCheck/minimal/_all.css">
<style>
.sectioncalibrator {
	height: calc(100vh - 50px);
	overflow-y: hidden;
}

@media only screen and (max-width:600px) {
	.sectioncalibrator {
		height: calc(100vh - 100px);
		overflow-y: hidden;
	}
}

hr {
	margin-top: 0px;
	margin-bottom: 5px;
}

.green-button:hover {
	background-color: #3CB371 !important;
}

.test:hover {
	background-color: #f6f6f6;
}

.shadowBox {
	box-shadow: 1px 1px 4px grey;
}
</style>
</head>

<body>
	<div ng-controller="check_CTRL">
		<div ng-init="initiation();">
			<div class="content-wrapper" style="font-size: 0.9em;">
				<section class="content sectioncalibrator"
					style="padding-top: 8px; padding-bottom: 8px;">
					<div class="row container-fluid"
						style="padding-right: 2px; padding-left: 2px;">

						<!-- START of menu well -->
						<div id="menuWell">
							<div class="col-sm-6"
								style="padding-right: 2px; padding-left: 2px;">
								<jsp:include page="/WEB-INF/ecpos/views/menu.jsp" flush="true"></jsp:include>
							</div>
						</div>
						<!-- END of menu well -->

						<!-- START of check well -->
						<div id="checkWell">
							<div class="col-sm-6"
								style="padding-right: 2px; padding-left: 2px;">
								<div class="box box-success"
									style="background-color: white; margin-bottom: 0px; padding: 10px; height: 90vh; max-height: 90vh; overflow-x: hidden; overflow-y: auto;">
									<div class="row">
										<div class="col-lg-7 col-md-7 col-sm-7 col-xs-7" style="font-size: 14px;">
											<div style="margin-bottom: 10px;">
												<!-- <font><b>Check : {{checkDetail.checkNoToday}}</b> / <b>Table : {{checkDetail.tableName}}</b></font> -->
												<font><b>Check:</b> {{checkDetail.checkNoToday}} /
													<%if (user.getStoreType() == 3) {%>
													Room : {{checkDetail.tableName}} / {{checkDetail.roomType}} / {{checkDetail.roomCategory}}</font>
													<%} else {%>
													Table : {{checkDetail.tableName}}</font>
													<%} %>
													
											</div>
											<div style="margin-bottom: 10px;">
												<!-- <font><b>Created Date : {{checkDetail.createdDate}}</b></font> -->
												<font><b>Created Date:</b>
													{{checkDetail.createdDate}}</font>
											</div>
											<div style="margin-bottom: 10px;">
												<!-- <font><b>Status : {{checkDetail.status}}</b></font> -->
												<font><b>Status:</b> {{checkDetail.status}}</font>
											</div>
											<%if (user.getStoreType() == 3) { %>
											<div style="margin-bottom: 10px;">
												<!-- <font><b>Customer Name: : {{checkDetail.customerName}}</b></font> -->
												<font><b>Customer Name:</b><label id="customerNameee"></label>
													{{checkDetail.customerName}}</font>
											</div>
											<%} else { %>
											<div ng-if="checkDetail.orderType != '1'"
												style="margin-bottom: 10px;">
												<!-- <font><b>Customer Name: : {{checkDetail.customerName}}</b></font> -->
												<font><b>Customer Name:</b><label id="customerNameee"></label>
													{{checkDetail.customerName}}</font>
											</div>
											<%} %>
										</div>
										<div class="col-lg-5 col-md-5 col-sm-5 col-xs-5">
											<div ng-if="mode == 1">
												<%
													if (user.getStoreType() == 2) {
												%>
												<button id="generateQRButton"
													class="btn pull-right btn-danger shadowBox"
													style="width: 150px; margin-bottom: 10px"
													ng-click="generateQR()">
													<!-- <i class="fa fa-qrcode"></i> -->
													Generate QR
												</button>
												<br> <br>
												<button id="openItemButton"
													class="btn bg-navy pull-right shadowBox"
													style="width: 150px; margin-bottom: 10px"
													ng-click="openItem()">
													Open Item
												</button>
												<br> <br>
												<!-- <button id="CustomerInfoButton"
													class="btn bg-navy pull-right shadowBox"
													style="width: 150px; margin-bottom: 10px"
													ng-click="searchCustomerInfo()">
													Customer Info
												</button>
												<br> <br> -->
												<%
													}
												%>
												<!-- <button id="barcodeOrderButton" class="btn btn-social btn-sm pull-right bg-maroon" style="width: 100%;" ng-click="openBarcodeModal()">
												<i class="fa fa-barcode"></i> Barcode Order
											</button> -->
												<%
													if (user.getStoreType() == 2) {
												%>
												<button id="sendOrderButton"
													class="btn pull-right btn-primary shadowBox"
													style="width: 150px; margin-bottom: 10px"
													ng-click="sendOrdertoKds()">
													<!-- <i class="fa fa-utensil-fork"></i> -->
													To Kitchen
												</button>
												<%
													}
												%>
												<%
													if (user.getStoreType() == 1) {
												%>
												<button id="openItemButton"
													class="btn bg-navy pull-right shadowBox"
													style="width: 150px; margin-bottom: 10px"
													ng-click="openItem()">
													Open Item
												</button>
												<!-- <button id="CustomerInfoButton"
													class="btn bg-navy pull-right shadowBox"
													style="width: 150px; margin-bottom: 10px"
													ng-click="generateQR()">
													Customer Info
												</button>
												<br> <br> -->
												<!-- <button id="sendOrderButton"
													class="btn pull-right btn-primary shadowBox"
													style="width: 150px; margin-bottom: 10px"
													ng-click="sendOrdertoKds()">
													<i class="fa fa-location-arrow"></i> Send Order
												</button> -->
												<%
													}
												%>
												<%
													if (user.getStoreType() == 3) {
												%>
												<button class="btn pull-right btn-primary shadowBox" id="checkInButton" style="width: 150px; margin-bottom: 10px"
													ng-click="roomCheckInOut('in')" ng-if="roomStatus == 3">
													<!-- <i class="fa fa-utensil-fork"></i> -->
													<span >Check-in</span>
												</button>
												<!-- <button class="btn pull-right btn-primary shadowBox" style="width: 150px; margin-bottom: 10px"
													ng-click="roomCheckInOut('out')" ng-if="roomStatus == 1">
													<i class="fa fa-utensil-fork"></i>
													<span >Check-out</span>
												</button> -->
												<%
													}
												%>
											</div>
											<div ng-if="mode == 2">
												<button class="btn btn-danger shadowBox"
													ng-click="redirectMenu()">Back To Previous</button>
												<button class="btn btn-primary shadowBox"
													ng-click="printReceiptBeforePay()">Print Receipt</button>
											</div>
										</div>
									</div>
									<!-- <div class="input-group" style="width: 100%;margin-top: 5px">
										<input type="text" class="form-control" ng-model="barcode"
											ng-click="barcodeOrder()"
											ng-keydown="$event.keyCode === 13 && barcodeOrder()"
											id="barcode_input" required placeholder="Barcode" />
									</div> -->
									<div >
										<div class="row"
											style="background-color: purple; color: white; padding-top: 7px; padding-right: 15px; font-size: 14px;" id="rowBarrrruuuu">
											<div class='col-xs-1 col-sm-1 text-center'>
												<input type="checkbox"
													ng-click="allGrandParentItemCheckbox()"
													id="allGrandParentItemCheckbox" style="margin: 2px 0 8px;"
													class="icheckbox_minimal-red">
											</div>
											<div class='col-xs-2 col-sm-2 text-left'>
												<b>Code</b>
											</div>
											<div class='col-xs-4 col-sm-5 text-left'
												style="padding: 0px;">
												<b>Item</b>
											</div>
											<div class='col-xs-2 col-sm-2 text-center'>
												<b>Quantity</b>
											</div>
											<div class='col-xs-2 col-sm-2 text-right'>
												<b>Price</b>
											</div>
										</div>
										<hr>
										<div
											style="padding-right: 15px; max-height: 30vh; height: 30vh; overflow-y: auto;">
											<div
												ng-repeat="grandParentItem in checkDetail.grandParentItemArray">

												<div style="padding-bottom: 8px;"
													ng-if="grandParentItem.itemStatus != 'Paid'">
													<div class="row">
														<div class='col-xs-1 col-sm-1 text-center'>
															<input type="checkbox"
																ng-click="grandParentItemCheckbox()"
																name="grandParentItemCheckbox"
																value={{grandParentItem.checkDetailId}}
																style="margin: 2px 0 0;" class="icheckbox_minimal-red"
																ng-if="grandParentItem.itemCode != 'RR1'">
														</div>
														<div class='col-xs-2 col-sm-2 text-left'>{{grandParentItem.itemCode}}</div>
														<div class='col-xs-4 col-sm-5 text-left'
															style="padding: 0px;">{{grandParentItem.itemName}}@{{grandParentItem.itemPrice
															| number:2}}</div>
														<%
															if (user.getStoreType() == 1) {
														%>
														<div class='col-xs-2 col-sm-2 text-left'
															style="padding-left: 0px; padding-right: 0px;"
															ng-if="grandParentItem.isAlaCarte && !grandParentItem.hasModified">
															<div class="input-group">
																<input type="number"
																	id="{{grandParentItem.checkDetailId}}"
																	name="itemQuantity"
																	style="width: 100%; padding-left: 6px;"
																	value={{grandParentItem.itemQuantity}} min="1" max="99"
																	size="1" /> <span class="input-group-btn">
																	<button class="btn btn-flat btn-info"
																		style="height: 23px; width: 23px; padding: 0px;"
																		ng-click="submitUpdateItemQuantity(grandParentItem.checkDetailId)">&#10004;</button>
																</span>
															</div>
														</div>
														<div class='col-xs-2 col-sm-2 text-center'
															ng-if="!(grandParentItem.isAlaCarte && !grandParentItem.hasModified)">{{grandParentItem.itemQuantity}}</div>
														<%
															} else {
														%>
														<div class='col-xs-2 col-sm-2 text-center'>{{grandParentItem.itemQuantity}}</div>
														<%
															}
														%>
														<div class='col-xs-2 col-sm-2 text-right'>{{grandParentItem.totalAmount|
															number:2}}</div>
													</div>
													<div
														ng-repeat="parentItem in grandParentItem.parentItemArray">
														<div class="row">
															<div class='col-xs-1 col-sm-1 text-center'></div>
															<div class='col-xs-2 col-sm-2 text-left'>{{parentItem.itemCode}}</div>
															<div class='col-xs-4 col-sm-5 text-left'
																style="padding: 0px;">:{{parentItem.itemName}}@{{parentItem.itemPrice|
																number:2}}</div>
															<div class='col-xs-2 col-sm-2 text-center'>{{parentItem.itemQuantity}}</div>
															<div class='col-xs-2 col-sm-2 text-right'>{{parentItem.totalAmount|
																number:2}}</div>
														</div>
														<div ng-repeat="childItem in parentItem.childItemArray">
															<div class="row">
																<div class='col-xs-1 col-sm-1 text-center'></div>
																<div class='col-xs-2 col-sm-2 text-left'>{{childItem.itemCode}}</div>
																<div class='col-xs-4 col-sm-5 text-left'
																	style="padding: 0px;">&nbsp;&nbsp;&nbsp;&nbsp;*{{childItem.itemName}}@{{childItem.itemPrice|
																	number:2}}</div>
																<div class='col-xs-2 col-sm-2 text-center'>{{childItem.itemQuantity}}</div>
																<div class='col-xs-2 col-sm-2 text-right'>{{childItem.totalAmount|
																	number:2}}</div>
															</div>
														</div>
													</div>
												</div>

												<div style="padding-bottom: 8px; color: red;"
													ng-if="grandParentItem.itemStatus == 'Paid'">
													<div class="row">
														<div class='col-xs-1 col-sm-1 text-center'></div>
														<div class='col-xs-2 col-sm-2 text-left'>{{grandParentItem.itemCode}}</div>
														<div class='col-xs-4 col-sm-5 text-left'
															style="padding: 0px;">{{grandParentItem.itemName}}@{{grandParentItem.itemPrice
															| number:2}}</div>
														<%
															if (user.getStoreType() == 1) {
														%>
														<div class='col-xs-2 col-sm-2 text-left'
															style="padding-left: 0px; padding-right: 0px;"
															ng-if="grandParentItem.isAlaCarte && !grandParentItem.hasModified">
															<div class="input-group">
																<input type="number"
																	id="{{grandParentItem.checkDetailId}}"
																	name="itemQuantity"
																	style="width: 100%; padding-left: 6px;"
																	value={{grandParentItem.itemQuantity}} min="1" max="99"
																	size="1" /> <span class="input-group-btn">
																	<button class="btn btn-flat btn-info"
																		style="height: 23px; width: 23px; padding: 0px;"
																		ng-click="submitUpdateItemQuantity(grandParentItem.checkDetailId)">&#10004;</button>
																</span>
															</div>
														</div>
														<div class='col-xs-2 col-sm-2 text-center'
															ng-if="!(grandParentItem.isAlaCarte && !grandParentItem.hasModified)">{{grandParentItem.itemQuantity}}</div>
														<%
															} else {
														%>
														<div class='col-xs-2 col-sm-2 text-center'>{{grandParentItem.itemQuantity}}</div>
														<%
															}
														%>
														<div class='col-xs-2 col-sm-2 text-right'>{{grandParentItem.totalAmount|
															number:2}}</div>
													</div>
													<div
														ng-repeat="parentItem in grandParentItem.parentItemArray">
														<div class="row">
															<div class='col-xs-1 col-sm-1 text-center'></div>
															<div class='col-xs-2 col-sm-2 text-left'>{{parentItem.itemCode}}</div>
															<div class='col-xs-4 col-sm-5 text-left'
																style="padding: 0px;">:{{parentItem.itemName}}@{{parentItem.itemPrice|
																number:2}}</div>
															<div class='col-xs-2 col-sm-2 text-center'>{{parentItem.itemQuantity}}</div>
															<div class='col-xs-2 col-sm-2 text-right'>{{parentItem.totalAmount|
																number:2}}</div>
														</div>
														<div ng-repeat="childItem in parentItem.childItemArray">
															<div class="row">
																<div class='col-xs-1 col-sm-1 text-center'></div>
																<div class='col-xs-2 col-sm-2 text-left'>{{childItem.itemCode}}</div>
																<div class='col-xs-4 col-sm-5 text-left'
																	style="padding: 0px;">&nbsp;&nbsp;&nbsp;&nbsp;*{{childItem.itemName}}@{{childItem.itemPrice|
																	number:2}}</div>
																<div class='col-xs-2 col-sm-2 text-center'>{{childItem.itemQuantity}}</div>
																<div class='col-xs-2 col-sm-2 text-right'>{{childItem.totalAmount|
																	number:2}}</div>
															</div>
														</div>
													</div>
												</div>
											</div>
										</div>
										<hr style="margin-bottom: 0;">
										<div class="row" style="color: red;">
											<div class='col-xs-12 col-sm-12'>
												<label style="font-size: small; font-weight: normal;">*Item(s)
													in red indicates already paid</label>
											</div>
										</div>
										<div class="row"
											style="padding-right: 15px; margin-bottom: 5px;">
											<div class='col-xs-8 col-sm-9 text-left'>
												<b>Subtotal</b>
											</div>
											<div class='col-xs-1 col-sm-1 text-center'></div>
											<div class='col-xs-2 col-sm-2 text-right'>
												<b>{{checkDetail.totalAmount| number:2}}</b>
											</div>
										</div>
										<div ng-repeat="taxCharge in checkDetail.taxCharges">
											<div class="row"
												style="padding-right: 15px; margin-bottom: 5px;">
												<div class='col-xs-8 col-sm-9 text-left'>
													<b>{{taxCharge.name}} {{taxCharge.rate}}%</b>
												</div>
												<div class='col-xs-1 col-sm-1 text-center'></div>
												<div class='col-xs-2 col-sm-2 text-right'>
													<b>{{taxCharge.chargeAmount| number:2}}</b>
												</div>
											</div>
										</div>
										<div class="row"
											style="padding-right: 15px; margin-bottom: 5px;">
											<div class='col-xs-8 col-sm-9 text-left'>
												<b>Rounding Adjustment</b>
											</div>
											<div class='col-xs-1 col-sm-1 text-center'></div>
											<div class='col-xs-2 col-sm-2 text-right'>
												<b>{{checkDetail.totalAmountWithTaxRoundingAdjustment|
													number:2}}</b>
											</div>
										</div>
										<div class="row"
											style="padding-right: 15px; margin-bottom: 5px;">
											<div class='col-xs-8 col-sm-9 text-left'>
												<b>Grand Total</b>
											</div>
											<div class='col-xs-1 col-sm-1 text-left'
												style="border-top: solid; border-top-width: thin; border-bottom: 3px double;">&nbsp;</div>
											<div class='col-xs-2 col-sm-2 text-right'
												style="border-top: solid; border-top-width: thin; border-bottom: 3px double;">
												<b>{{checkDetail.grandTotalAmount| number:2}}</b>
											</div>
										</div>
										<hr>
										<div class="row"
											style="padding-right: 15px; margin-bottom: 5px;">
											<div class='col-xs-8 col-sm-9 text-left'>
												<b>Tender Amount</b>
											</div>
											<div class='col-xs-1 col-sm-1 text-center'></div>
											<div class='col-xs-2 col-sm-2 text-right'>
												<b>{{checkDetail.tenderAmount3| number:2}}</b>
											</div>
										</div>
										<div class="row"
											style="padding-right: 15px; margin-bottom: 5px;">
											<div class='col-xs-8 col-sm-9 text-left'>
												<b>Overdue Amount</b>
											</div>
											<div class='col-xs-1 col-sm-1 text-center'></div>
											<div class='col-xs-2 col-sm-2 text-right'>
												<b>{{checkDetail.overdueAmount| number:2}}</b>
											</div>
										</div>
									</div>
									<hr style="margin-bottom: 10px;">
									<div id="checkActionButtons" style="width: 100%">
										<div class="col-lg-3 col-md-3 col-sm-4 col-xs-5">
											<button id="cancelItemButton"
												class="btn btn-block test bg-orange shadowBox"
												ng-click="cancelItem()" style="white-space: normal; padding: 5px">
												<img src="${pageContext.request.contextPath}/img/icon/CancelItems.png" style="height: 35px; width: 35px"><br>CANCEL ITEM
												<!-- <font
													style="font-size: 14px; position: relative; bottom: -11px; left: -7%">CANCEL
													ITEM </font> -->
											</button>
										</div>

										<div class="col-lg-3 col-md-3 col-sm-4 col-xs-5">
											<button id="cancelCheckButton"
												class="btn btn-block btn-danger shadowBox"
												ng-click="cancelCheck()" style="white-space: normal; padding: 5px">
												<!-- <br> <font
													style="font-size: 14px; position: relative; bottom: -11px; left: -17%; white-space: normal;">CANCEL
													CHECK</font> -->
												<img src="${pageContext.request.contextPath}/img/icon/CancelCheck.png" style="height: 35px; width: 35px"><br>CANCEL CHECK	
											</button>
										</div>
										
										<%if (user.getStoreType() != 3) {%>
										<div ng-if="orderType == 'table'">
											<div class="col-lg-3 col-md-3 col-sm-4 col-xs-5">
												<button id="splitCheckButton"
													class="btn btn-block btn-primary shadowBox" 
													ng-click="splitCheck()" style="white-space: normal; padding: 5px">
													<!-- <font
														style="font-size: 14px; position: relative; bottom: -11px; left: -7%">
														<br> SPLIT CHECK
													</font> -->
													<img src="${pageContext.request.contextPath}/img/icon/SplitCheck.png" style="height: 35px; width: 35px"><br>SPLIT CHECK
												</button>
											</div>
										</div>
										<%} %>
										<div ng-if="orderType == 'deposit'">
											<div class="col-lg-3 col-md-3 col-sm-4 col-xs-5">
												<button id="closeCheckButton"
													class="btn btn-block btn-danger shadowBox"
													ng-click="closeCheck()" style="white-space: normal; padding: 5px">
													<!-- <font
														style="font-size: 14px; position: relative; bottom: -11px; left: -7%">
														<br> CLOSE CHECK
													</font> -->
													<img src="${pageContext.request.contextPath}/img/icon/CancelItems.png" style="height: 35px; width: 35px"><br>CLOSE CHECK
												</button>
											</div>
										</div>

										<div class="col-lg-3 col-md-3 col-sm-3 col-xs-4">
											<%
												if (user.getRoleType() == 1 || user.getRoleType() == 3) {
											%>
											<button id="paymentButton"
												class="btn btn-block btn-success shadowBox"
												ng-click="redirectPayment()" style="white-space: normal; padding: 5px">
												<!-- <font
													style="font-size: 14px; position: relative; bottom: -22px;">
													PAY </font> -->
												<img src="${pageContext.request.contextPath}/img/icon/Pay.png" style="height: 35px; width: 42px"><br>PAY
											</button>
											<%
												}
											%>
										</div>
									</div>
								</div>
							</div>
						</div>
						<!-- END of check well -->

						<!-- START of payment well -->
						<div id="paymentWell">
							<div class="col-sm-6 col-md-6"
								style="padding-right: 2px; padding-left: 2px;">
								<jsp:include page="/WEB-INF/ecpos/views/payment.jsp"
									flush="true"></jsp:include>
							</div>
						</div>
						<!-- END of payment well -->

					</div>
				</section>
			</div>

			<div class="modal fade" data-backdrop="static" id="QRImageModal"
				role="dialog">
				<div class="modal-dialog">
					<div class="modal-content">
						<!-- <div class="modal-header"></div> -->
						<div class="modal-body">
							<div class="row" style="font-size: large">
								<div class="col-xs-11 col-sm-11"></div>
								<div class="col-xs-1 col-sm-1">
									<button class="close" data-dismiss="modal">&times;</button>
								</div>
							</div>
							<div class="row" style="text-align: center;">
								<img id="QRImage" style="height: 50%; width: 50%;" />
							</div>
							<div class="row">
								<div class="col-xs-12 col-sm-12">
									<button class="btn bg-maroon" ng-click="printQR()"
										style="float: right;">
										<i class="fa fa-print" aria-hidden="true"></i> Print
									</button>
								</div>
								<!-- <div class="col-xs-6 col-sm-6" >
										<button class="btn bg-maroon" ng-click="displayQRPdf()"><i class="fa fa-file-pdf-o" aria-hidden="true"></i> PDF</button>
								</div> -->
							</div>
						</div>
						<!-- <div class="modal-footer"></div> -->
					</div>
				</div>
			</div>

			<div class="modal fade" data-backdrop="static" id="barcodeModal"
				role="dialog">
				<div class="modal-dialog">
					<div class="modal-content">
						<!-- <div class="modal-header"></div> -->
						<div class="modal-body">
							<div class="row" style="font-size: large">
								<div class="col-xs-11 col-sm-11"></div>
								<div class="col-xs-1 col-sm-1">
									<button class="close" data-dismiss="modal">&times;</button>
								</div>
							</div>
							<div style="height: 15vh; display: flex; align-items: center;">
								<div class="col-xs-1 col-sm-1 form-group"></div>
								<div class="col-xs-10 col-sm-10 form-group">
									<label>Please scan item's barcode:</label> <input type="text"
										class="form-control" ng-model="barcode"
										ng-click="barcodeOrder()"
										ng-keydown="$event.keyCode === 13 && barcodeOrder()"
										id="barcode_input" required />
								</div>
								<div class="col-xs-1 col-sm-1 form-group"></div>
							</div>
						</div>
						<!-- <div class="modal-footer"></div> -->
					</div>
				</div>
			</div>
			
			<div class="modal fade" data-backdrop="static" id="CustomerInfoModal"
				role="dialog">
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
			                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
			                  <span aria-hidden="true">×</span></button>
			                <h4 class="modal-title">Customer Information</h4><small style="color: red;">No Record Found. Please Enter New Customer Info.</small>
			            </div>
			            <form ng-submit="submitCustomerInfo()">
						<div class="modal-body">
								<div class="form-group">
				                  <label for="exampleInputEmail1">Customer Name</label>
				                  <input type="text" class="form-control" id="customerName" ng-model="customer.name" placeholder="Name">
				                </div>
								<div class="form-group">
				                  <label for="exampleInputEmail1">Customer Phone Number</label>
				                  <input type="text" class="form-control" id="customerPhoneNo" ng-model="customer.phoneno" placeholder="Phone Number"
				                  onkeypress="return (event.charCode !=8 && event.charCode ==0 || (event.charCode >= 48 && event.charCode <= 57))">
				                </div>
						</div>
						<div class="modal-footer">
			                <button type="button" class="btn btn-default pull-left" data-dismiss="modal">Close</button>
			                <button type="submit" class="btn btn-primary">Save changes</button>
		              	</div>
		              	</form>
					</div>
				</div>
			</div>
			
			<div class="modal fade" data-backdrop="static" id="OpenItemModal"
				role="dialog">
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
			                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
			                  <span aria-hidden="true">×</span></button>
			                <h4 class="modal-title">Enter the Item Price</h4>
			            </div>
						<!-- <form ng-submit="submitOpenItem()"> -->
							<div class="modal-body">
								<div
									style="border: 1px solid #d2d6de; padding: 10px; border-radius: 5px;">
									<div
										style="padding-top: 15px; padding-left: 15px; padding-right: 15px;">
										<hr
											style="border: none; height: 2px; color: black; background-color: black;">
										<div class="row">
											<div class="col-xs-3 col-sm-3"
												style="text-align: center; font-size: 25px; color: black;">
												<label>RM</label>
											</div>
											<div class="col-xs-9 col-sm-9"
												style="text-align: right; padding-right: 25px; font-size: 25px; color: black;">
												<label id="tenderAmount3">0.00</label>
											</div>
										</div>
										<hr
											style="border: none; height: 2px; color: black; background-color: black;">
										<div class="row">
											<div class="col-xs-9 col-sm-9">
												<div class="row" style="padding-left: 15px;">
													<div class="col-xs-4 col-sm-4"
														style="padding: 0px; padding-right: 5px; padding-bottom: 5px;">
														<input id="seven" type="button" value="7" style="border-color: lightgrey;"
															class="calculator"
															ng-click="enterCalculator3('tenderAmount3',7)" />
													</div>
													<div class="col-xs-4 col-sm-4"
														style="padding: 0px; padding-right: 5px; padding-bottom: 5px;">
														<input id="eight" type="button" value="8" style="border-color: lightgrey;"
															class="calculator"
															ng-click="enterCalculator3('tenderAmount3',8)" />
													</div>
													<div class="col-xs-4 col-sm-4"
														style="padding: 0px; padding-right: 5px; padding-bottom: 5px;">
														<input id="nine" type="button" value="9" style="border-color: lightgrey;"
															class="calculator"
															ng-click="enterCalculator3('tenderAmount3',9)" />
													</div>
												</div>
												<div class="row" style="padding-left: 15px;">
													<div class="col-xs-4 col-sm-4"
														style="padding: 0px; padding-right: 5px; padding-bottom: 5px;">
														<input id="four" type="button" value="4" style="border-color: lightgrey;"
															class="calculator"
															ng-click="enterCalculator3('tenderAmount3',4)" />
													</div>
													<div class="col-xs-4 col-sm-4"
														style="padding: 0px; padding-right: 5px; padding-bottom: 5px;">
														<input id="five" type="button" value="5" style="border-color: lightgrey;"
															class="calculator"
															ng-click="enterCalculator3('tenderAmount3',5)" />
													</div>
													<div class="col-xs-4 col-sm-4"
														style="padding: 0px; padding-right: 5px; padding-bottom: 5px;">
														<input id="six" type="button" value="6" class="calculator" style="border-color: lightgrey;"
															ng-click="enterCalculator3('tenderAmount3',6)" />
													</div>
												</div>
												<div class="row" style="padding-left: 15px;">
													<div class="col-xs-4 col-sm-4"
														style="padding: 0px; padding-right: 5px; padding-bottom: 5px;">
														<input id="one" type="button" value="1" class="calculator" style="border-color: lightgrey;"
															ng-click="enterCalculator3('tenderAmount3',1)" />
													</div>
													<div class="col-xs-4 col-sm-4"
														style="padding: 0px; padding-right: 5px; padding-bottom: 5px;">
														<input id="two" type="button" value="2" class="calculator" style="border-color: lightgrey;"
															ng-click="enterCalculator3('tenderAmount3',2)" />
													</div>
													<div class="col-xs-4 col-sm-4"
														style="padding: 0px; padding-right: 5px; padding-bottom: 5px;">
														<input id="three" type="button" value="3" style="border-color: lightgrey;"
															class="calculator"
															ng-click="enterCalculator3('tenderAmount3',3)" />
													</div>
												</div>
												<div class="row" style="padding-left: 15px;">
													<div class="col-xs-6 col-sm-6"
														style="padding: 0px; padding-right: 5px; padding-bottom: 5px;">
														<input id="zero" type="button" value="0" style="border-color: lightgrey;"
															class="calculator"
															ng-click="enterCalculator3('tenderAmount3',10)" />
													</div>
													<div class="col-xs-6 col-sm-6"
														style="padding: 0px; padding-right: 5px; padding-bottom: 5px;">
														<input id="zerozero" value="00" type="button" style="border-color: lightgrey;"
															class="calculator"
															ng-click="enterCalculator3('tenderAmount3',100)" />
													</div>
												</div>
											</div>
											<div class="col-xs-3 col-sm-3">
												<div class="row" style="padding-right: 15px;">
													<div class="col-xs-3 col-sm-3"
														style="padding: 0px; padding-bottom: 5px;">
														<button id="remove" value="removeButton" style="border-color: lightgrey;"
															class="calculatoraction"
															ng-click="enterCalculator3('tenderAmount3',-10)">
															<i class="fa fa-arrow-left" aria-hidden="true"></i>
														</button>
													</div>
												</div>
												<div class="row" style="padding-right: 15px;">
													<div class="col-xs-3 col-sm-3"
														style="padding: 0px; padding-bottom: 5px;">
														<button value="confirmButton" class="calculatoraction btn-primary" style="background: #1F8CE8; color: white;"
															ng-click="submitOpenItem()">
															<i class="fa fa-check-square" aria-hidden="true"></i>
														</button>
													</div>
												</div>
											</div>
										</div>
									</div>
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