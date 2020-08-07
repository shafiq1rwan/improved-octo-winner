<%@ page
	import="mpay.ecpos_manager.general.utility.UserAuthenticationModel"%>

<%
	UserAuthenticationModel user = (UserAuthenticationModel) session.getAttribute("session_user");
%>

<!DOCTYPE html>
<html>
<head>
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
										<div class="col-sm-7">
											<div style="margin-bottom: 10px;">
												<!-- <font><b>Check : {{checkDetail.checkNoToday}}</b> / <b>Table : {{checkDetail.tableName}}</b></font> -->
												<font><b>Check:</b> {{checkDetail.checkNoToday}} /
													Table : {{checkDetail.tableName}}</font>
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
											<div ng-if="checkDetail.orderType != '1'"
												style="margin-bottom: 10px;">
												<!-- <font><b>Customer Name: : {{checkDetail.customerName}}</b></font> -->
												<font><b>Customer Name:</b>
													{{checkDetail.customerName}}</font>
											</div>
										</div>
										<div class="col-sm-5">
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
												<button id="sendOrderButton"
													class="btn pull-right btn-primary shadowBox"
													style="width: 150px; margin-bottom: 10px"
													ng-click="sendOrdertoKds()">
													<i class="fa fa-location-arrow"></i> Send Order
												</button>
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
									<div>
										<div class="row"
											style="padding-top: 8px; padding-right: 15px;">
											<div class='col-xs-1 col-sm-1 text-center'>
												<input type="checkbox"
													ng-click="allGrandParentItemCheckbox()"
													id="allGrandParentItemCheckbox" style="margin: 2px 0 0;"
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
																style="margin: 2px 0 0;" class="icheckbox_minimal-red">
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
												<b>{{checkDetail.tenderAmount| number:2}}</b>
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
										<table style="width: 100%;">
											<tbody>
												<tr style="width: 100%;">
													<td style="padding: 2px;"><button
															id="cancelItemButton"
															class="btn btn-block test bg-orange shadowBox"
															ng-click="cancelItem()"
															style="word-spacing: normal; word-wrap: break-word; width: 140px; height: 80px; margin-right: 13px; background-image:url('${pageContext.request.contextPath}/img/icon/CancelItems.png'); background-repeat: no-repeat; background-size: 32% 60.5%; background-position: top; border: 10px solid transparent;">
															<!-- <i class="fa fa-trash"></i> -->
															<br> <font
																style="font-size: 14px; position: relative; bottom: -11px;">CANCEL
																ITEM </font>
														</button></td>
													<td style="padding: 2px;">
														<button id="cancelCheckButton"
															class="btn btn-block btn-danger shadowBox"
															ng-click="cancelCheck()"
															style="width: 140px; height: 80px; margin-right: 13px; background-image:url('${pageContext.request.contextPath}/img/icon/CancelCheck.png'); background-repeat: no-repeat; background-size: 32% 60.5%; background-position: top; border: 10px solid transparent;">
															<!-- <i class="fa fa-remove"></i> -->
															<br> <font style="font-size: 14px; position: relative; bottom: -11px;">CANCEL CHECK</font>
														</button>
													</td>
													<td style="padding: 2px;"><div
															ng-if="orderType == 'table'">
															<button id="splitCheckButton"
																class="btn btn-primary shadowBox"
																ng-click="splitCheck()"
																style="word-spacing: normal; background-color: #1F8CE8; width: 140px; height: 80px; margin-right: 13px; background-image:url('${pageContext.request.contextPath}/img/icon/SplitCheck.png'); background-repeat: no-repeat; background-size: 32% 60.5%; background-position: top; border: 10px solid transparent;">
																<font style="font-size: 14px; position: relative; bottom: -11px;"> <!-- <i
																	class="fa fa-copy"></i> --> <br> SPLIT CHECK
																</font>
															</button>
														</div></td>
													<td style="padding: 2px;">
														<div ng-if="orderType == 'deposit'">
															<button id="closeCheckButton"
																class="btn btn-block btn-danger shadowBox"
																ng-click="closeCheck()"
																style="word-spacing: normal; width: 140px; height: 80px; margin-right: 13px; background-image:url('${pageContext.request.contextPath}/img/icon/CancelItems.png'); background-repeat: no-repeat; background-size: 32% 60.5%; background-position: top; border: 10px solid transparent;">
																<font style="font-size: 14px; position: relative; bottom: -11px;"> <!-- <i class="fa fa-remove"></i> -->
																	<br> CLOSE CHECK
																</font>
															</button>
														</div>
													</td>
													<%
														if (user.getRoleType() == 1 || user.getRoleType() == 3) {
													%>
													<td style="padding: 2px;"><button
															id="paymentButton"
															class="btn btn-block btn-success shadowBox"
															ng-click="redirectPayment()"
															style="word-spacing: normal; width: 140px; height: 80px; margin-right: 15px; background-image:url('${pageContext.request.contextPath}/img/icon/Pay.png'); background-repeat: no-repeat; background-size: 32% 60.5%; background-position: top; border: 10px solid transparent;">
															<font style="font-size: 14px; position: relative; bottom: -22px;"> <!-- <i
																class="fa fa-dollar"></i> --> PAY
															</font>
														</button></td>
													<%
														}
													%>
												</tr>
											</tbody>
										</table>
										<!-- Comment on 17-09-2019 - For redesign and new arrangement -->
										<!-- <div class="row" style="margin-bottom: 5px;">
											<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
												<button id="cancelItemButton" class="btn btn-block btn-info" ng-click="cancelItem()">CANCEL ITEM</button>
											</div>
											<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
												<button id="paymentButton" class="btn btn-block btn-info" ng-click="redirectPayment()">PAYMENT</button>
											</div>
										</div>
										<div class="row">
											<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
												<button id="cancelCheckButton" class="btn btn-block btn-info" ng-click="cancelCheck()">CANCEL CHECK</button>
											</div>
											<div ng-if="orderType == 'table'">
												<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
													<button id="splitCheckButton" class="btn btn-block btn-info" ng-click="splitCheck()">SPLIT CHECK</button>
												</div>
											</div>
											<div ng-if="orderType == 'deposit'">
												<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
													<button id="closeCheckButton" class="btn btn-block btn-info" ng-click="closeCheck()">CLOSE CHECK</button>
												</div>
											</div>
										</div> -->
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
		</div>
	</div>
</body>
</html>