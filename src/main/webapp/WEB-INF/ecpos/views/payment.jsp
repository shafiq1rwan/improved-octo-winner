<%@ page import="mpay.ecpos_manager.general.utility.UserAuthenticationModel"%>

<%
	UserAuthenticationModel user = (UserAuthenticationModel) session.getAttribute("session_user");
%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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

.calculator {
  background: #f6f6f6;
  width: 100%;
  height: 45px;
  border-radius: 5px;
  font-size: medium;
  border: 2px solid #f6f6f6;
}

.calculator:hover:enabled {
  background-color: #00fa9a;
  color: white;
  border-color: #00fa9a;
}

.calculator:hover:disabled {
  background-color: #dddddd;
  cursor: not-allowed;
}

.calculatoraction {
  background: #f6f6f6;
  width: 400%;
  height: 95px;
  border-radius: 5px;
  font-size: medium;
  border: 2px solid #f6f6f6;
}

.calculatoraction:hover:enabled {
  background-color: #00fa9a;
  color: white;
  border-color: #00fa9a;
}

.calculatoraction:hover:disabled {
  background-color: #dddddd;
  cursor: not-allowed;
}

ul.select2-results__options li {
  padding: 0;
  font-size: 20px;
}
</style>
</head>

<body>
	<div ng-controller="payment_CTRL">
		<div ng-init="paymentInitiation();">
			<div class="well" style="height: 89vh; overflow-y: auto; background-color: white; margin-bottom: 0px; padding: 10px; padding-bottom: 22px;">
					
				<div id="paymentCarousel" class="carousel" data-interval="false">
					<div class="carousel-inner">
						<div class="item active">
							<div id="paymentType">
								<div class="row" style="font-size: large">
									<div class="col-sm-12" style="text-align: center">
										<label>Payment Type</label>
									</div>
								</div>
								<br><br><br>
								<div class="row">
									<div class="col-sm-6" style="margin-top: 15px;">
										<button id="fullPayment" class="btn btn-block" style="margin: auto; background: white; width: 165px; height: 125px; border: 1px solid #ccc; border-radius: 5px;" ng-click="proceedPaymentMethod('full')">
											<font size="4"><i>Full Payment</i></font>
										</button>
									</div>
									<div class="col-sm-6" style="margin-top: 15px;">
										<button id="partialPayment" class="btn btn-block" style="margin: auto; background: white; width: 165px; height: 125px; border: 1px solid #ccc; border-radius: 5px;" ng-click="proceedPaymentMethod('partial')">
											<font size="4"><i>Partial Payment</i></font>
										</button>
									</div>
								</div>
								<br><br>
								<div class="row">
									<div class="col-sm-6" style="margin-top: 15px;">
										<button id="splitPayment" class="btn btn-block" style="margin: auto; background: white; width: 165px; height: 125px; border: 1px solid #ccc; border-radius: 5px;" ng-click="proceedPaymentMethod('split')">
											<font size="4"><i>Split Payment</i></font>
										</button>
									</div>
									<%if (user.getStoreType() == 2) {%>
									<div class="col-sm-6" style="margin-top: 15px;">
										<button id="depositPayment" class="btn btn-block" style="margin: auto; background: white; width: 165px; height: 125px; border: 1px solid #ccc; border-radius: 5px;" ng-click="proceedPaymentMethod('deposit')">
											<font size="4"><i>Deposit Payment</i></font>
										</button>
									</div>
									<%}%>
								</div>
							</div>
						</div>
						
						<div class="item">
							<div id="paymentMethod">
								<div class="row" style="font-size: large">
									<div class="col-sm-2">
										<a id="paymentMethodBack" data-target="#paymentCarousel" data-slide="prev">
											<i class="fa fa-arrow-left" style="color: black;"></i>
										</a>
									</div>
									<div class="col-sm-8" style="text-align: center">
										<label>Payment Method</label>
									</div>
									<div class="col-sm-2"></div>
								</div>
								<div style="padding: 20px;">
									<button class="btn btn-block" style="margin: auto; background: white; width: 200px; height: 125px; border: 1px solid #ccc; border-radius: 5px;" ng-click="proceedPayment('Cash')">
										<img ng-src="${pageContext.request.contextPath}/img/icon/money.png" alt="Cash Payment" style="max-width: 200px; max-height: 125px; padding-top: 8%; padding-bottom: 8%;" />
									</button>
								</div>
								<div style="padding: 20px;">
									<button class="btn btn-block" style="margin: auto; background: white; width: 200px; height: 125px; border: 1px solid #ccc; border-radius: 5px;" ng-click="proceedPayment('Card')">
										<img ng-src="${pageContext.request.contextPath}/img/icon/credit-card.png" alt="Card Payment" style="max-width: 200px; max-height: 125px; padding-top: 8%; padding-bottom: 8%;" />
									</button>
								</div>
								<div style="padding: 20px;">
									<button class="btn btn-block" style="margin: auto; background: white; width: 200px; height: 125px; border: 1px solid #ccc; border-radius: 5px;" ng-click="proceedPayment('QR')">
										<img ng-src="${pageContext.request.contextPath}/img/icon/qr-code.png" alt="QR Payment" style="max-width: 200px; max-height: 125px; padding-top: 8%; padding-bottom: 8%;" />
									</button>
								</div>
							</div>
						</div>
						
						<div class="item">
							<div id="payment">
								<div class="row" style="font-size: large">
									<div class="col-sm-2">
										<a data-target="#paymentCarousel" data-slide="prev">
											<i class="fa fa-arrow-left" style="color: black;"></i>
										</a>
									</div>
									<div class="col-sm-8" style="text-align: center">
										<label id="paymentMethodName"></label>
									</div>
									<div class="col-sm-2"></div>
								</div>
								<br>
								<div id="terminalList" class="row" style="padding-bottom: 15px; padding-left: 30px; padding-right: 30px;">
									<div class="col-sm-12 form-group">
										<label>Terminal</label>
										<div style="border: 1px solid #d2d6de; padding: 10px; border-radius: 5px;">
											<select class="select2" id="terminal" style="width: 100%;">
												<option value="" selected>&nbsp;----- Select -----</option>
												<option ng-repeat="terminal in terminalList.terminals" value="{{terminal.serialNo}}">&nbsp;{{terminal.name}} ({{terminal.serialNo}})</option>
											</select>
										</div>
									</div>
								</div>
								<div style="padding-bottom: 15px; padding-left: 30px; padding-right: 30px;">
									<label>Tender Amount</label>
									<div style="border: 1px solid #d2d6de; padding: 10px; border-radius: 5px;">
										<div style="padding-top: 15px; padding-left: 15px; padding-right: 15px;">
											<hr style="border: none; height:2px; color: black; background-color: black;">
											<div class="row">
												<div class="col-sm-3" style="text-align: center; font-size: 25px; color: black;">
													<label>RM</label>
												</div>
												<div class="col-sm-9" style="text-align: right; padding-right: 25px; font-size: 25px; color: black;">
													<label id="amount">0.00</label>
												</div>
											</div>
											<hr style="border: none; height:2px; color: black; background-color: black;">
											<div class="row">
												<div class="col-sm-9">
													<div class="row" style="padding-left: 15px;">
														<div class="col-sm-4" style="padding: 0px; padding-right: 5px; padding-bottom: 5px;"><input id="seven" type="button" value="7" class="calculator" ng-click="enterCalculator('amount',7)" /></div>
														<div class="col-sm-4" style="padding: 0px; padding-right: 5px; padding-bottom: 5px;"><input id="eight" type="button" value="8" class="calculator" ng-click="enterCalculator('amount',8)" /></div>
														<div class="col-sm-4" style="padding: 0px; padding-right: 5px; padding-bottom: 5px;"><input id="nine" type="button" value="9" class="calculator" ng-click="enterCalculator('amount',9)" /></div>
													</div>
													<div class="row" style="padding-left: 15px;">
														<div class="col-sm-4" style="padding: 0px; padding-right: 5px; padding-bottom: 5px;"><input id="four" type="button" value="4" class="calculator" ng-click="enterCalculator('amount',4)" /></div>
														<div class="col-sm-4" style="padding: 0px; padding-right: 5px; padding-bottom: 5px;"><input id="five" type="button" value="5" class="calculator" ng-click="enterCalculator('amount',5)" /></div>
														<div class="col-sm-4" style="padding: 0px; padding-right: 5px; padding-bottom: 5px;"><input id="six" type="button" value="6" class="calculator" ng-click="enterCalculator('amount',6)" /></div>
													</div>
													<div class="row" style="padding-left: 15px;">
														<div class="col-sm-4" style="padding: 0px; padding-right: 5px; padding-bottom: 5px;"><input id="one" type="button" value="1" class="calculator" ng-click="enterCalculator('amount',1)" /></div>
														<div class="col-sm-4" style="padding: 0px; padding-right: 5px; padding-bottom: 5px;"><input id="two" type="button" value="2" class="calculator" ng-click="enterCalculator('amount',2)" /></div>
														<div class="col-sm-4" style="padding: 0px; padding-right: 5px; padding-bottom: 5px;"><input id="three" type="button" value="3" class="calculator" ng-click="enterCalculator('amount',3)" /></div>
													</div>
													<div class="row" style="padding-left: 15px;">
														<div class="col-sm-6" style="padding: 0px; padding-right: 5px; padding-bottom: 5px;"><input id="zero" type="button" value="0" class="calculator" ng-click="enterCalculator('amount',0)" /></div>
														<div class="col-sm-6" style="padding: 0px; padding-right: 5px; padding-bottom: 5px;"><input id="zerozero" value="00" type="button" class="calculator" ng-click="enterCalculator('amount',00)" /></div>
													</div>
												</div>
												<div class="col-sm-3">
													<div class="row" style="padding-right: 15px;">
														<div class="col-sm-3" style="padding: 0px; padding-bottom: 5px;">
															<button id="remove" value="removeButton" class="calculatoraction" ng-click="enterCalculator('amount',-10)">
																<i class="fa fa-arrow-left" aria-hidden="true"></i>
															</button>
														</div>
													</div>
													<div class="row" style="padding-right: 15px;">
														<div class="col-sm-3" style="padding: 0px; padding-bottom: 5px;">
															<button value="confirmButton" class="calculatoraction" ng-click="submitPayment()">
																<i class="fa fa-check-square" aria-hidden="true"></i>
															</button>
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
				
				<!-- Loading Modal [START] -->
				<div id="loading_modal" class="modal" tabindex="-1" role="dialog" aria-hidden="true" data-keyboard="false" data-backdrop="static" role="dialog">
					<div class="modal-content">
						<div class="modal-body text-center">
							<p>{{socketMessage}}</p>
						</div>
					</div>
				</div>
				<!-- Loading Modal [END] -->
	
			<!-- Scan QR Modal [START] -->
			<div class="modal fade" id="scan_qr_modal" tabindex="-1" role="dialog" aria-labelledby="scan_qr_modal" aria-hidden="true" data-keyboard="false" data-backdrop="static">
				<div class="modal-dialog">
					<div class="modal-content">
						 <div class="modal-header">
						 	
						        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
						          <span aria-hidden="true">&times;</span>
						        </button>
						        <h4 class="modal-title">QR Payment</h4>
						 </div>
						<div class="modal-body text-center">
							<form ng-submit="proceedToQRPayment()" autocomplete="off">
								<div class="row">
									<div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
										<div class="form-group">
											<h3>Please scan QR to Pay</h3>
											<input class="form-control" id="qr_content" type="text" ng-model="qrContent" required>
											<br>
											<button class="btn btn-primary" type="submit" ng-disabled="qrContent==''">Submit</button>
										</div>
									</div>
								</div>
							</form>
						</div>
					</div>
				
				</div>
			</div>
			<!-- Scan QR Modal [END] -->
			
			<!-- Print Receipt Modal [START] -->
			<div class="modal fade" id="print_receipt_modal" tabindex="-1" role="dialog" aria-labelledby="print_receipt_modal" aria-hidden="true" data-keyboard="false" data-backdrop="static">
				<div class="modal-dialog">
					<div class="modal-content">
						 <div class="modal-header">
						        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
						          <span aria-hidden="true">&times;</span>
						        </button>
						 </div>
						<div class="modal-body text-center">
							<form ng-submit="">
								<div class="row">
									<div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
										<div class="form-group">
											<h3>Print Receipt ?</h3>
											<button class="btn btn-primary" type="submit">Print</button>
					        				<button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
										</div>
									</div>
								</div>
							</form>
						</div>
					</div>
				
				</div>
			</div>
			<!-- Print Receipt Modal [END] -->
	
			</div>
		</div>
	</div>
</body>
</html>