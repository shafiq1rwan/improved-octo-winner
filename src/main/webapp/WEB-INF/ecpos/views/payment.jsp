<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
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
	<div ng-controller="payment_CTRL">
		<div class="well" style="background-color: white; height: 87vh; overflow-y: auto; padding: 15px;">
				
				<div id="paymentCarousel" class="carousel" data-interval="false">
					<div class="carousel-inner">
						<div class="item active">
							<div id="paymentType">
								<div class="row" style="font-size: large">
									<div class="col-sm-12" style="text-align: center">
										<label>Payment Type</label>
									</div>
								</div>
								<br>
								<div class="row" style="padding : 15px;">
									<div class="col-sm-6">
										<div style="margin: auto; width: 200px; height: 125px; border: 1px solid #ccc; border-radius: 5px; text-align: center;" ng-click="proceedPaymentMethod('full')">
											<label style="max-width: 200px; max-height: 125px; padding-top: 24%; padding-bottom: 24%;"><font size="4"><i>Full Payment</i></font></label>
										</div>
									</div>
									<div class="col-sm-6">
										<div style="margin: auto; width: 200px; height: 125px; border: 1px solid #ccc; border-radius: 5px; text-align: center;" ng-click="proceedPaymentMethod('partial')">
											<label style="max-width: 200px; max-height: 125px; padding-top: 24%; padding-bottom: 24%;"><font size="4"><i>Partial Payment</i></font></label>
										</div>
									</div>
								</div>
								<br>
								<div class="row" style="padding : 15px;">
									<div class="col-sm-6">
										<div style="margin: auto; width: 200px; height: 125px; border: 1px solid #ccc; border-radius: 5px; text-align: center;" ng-click="proceedPaymentMethod('split')">
											<label style="max-width: 200px; max-height: 125px; padding-top: 24%; padding-bottom: 24%;"><font size="4"><i>Split Payment</i></font></label>
										</div>
									</div>
									<div class="col-sm-6">
										<div style="margin: auto; width: 200px; height: 125px; border: 1px solid #ccc; border-radius: 5px; text-align: center;" ng-click="proceedPaymentMethod('deposit')">
											<label style="max-width: 200px; max-height: 125px; padding-top: 24%; padding-bottom: 24%;"><font size="4"><i>Deposit Payment</i></font></label>
										</div>
									</div>
								</div>
							</div>
						</div>
						
						<div class="item">
							<div id="paymentMethod">
								<div class="row" style="font-size: large">
									<div class="col-sm-2">
										<a href="#paymentCarousel" data-slide="prev">
											<i class="fa fa-arrow-left" style="color: black;"></i>
										</a>
									</div>
									<div class="col-sm-8" style="text-align: center">
										<label>Payment Method</label>
									</div>
									<div class="col-sm-2"></div>
								</div>
								<div style="padding: 50px;">
									<div style="margin: auto; width: 200px; height: 125px; border: 1px solid #ccc; border-radius: 5px; text-align: center;" ng-click="proceedPayment('Cash')">
										<img ng-src="${pageContext.request.contextPath}/img/icon/money.png" alt="Cash Payment" style="max-width: 200px; max-height: 125px; padding-top: 8%; padding-bottom: 8%;" />
									</div>
								</div>
								<div style="padding: 50px;">
									<div style="margin: auto; width: 200px; height: 125px; border: 1px solid #ccc; border-radius: 5px; text-align: center;" ng-click="proceedPayment('Card')">
										<img ng-src="${pageContext.request.contextPath}/img/icon/credit-card.png" alt="Card Payment" style="max-width: 200px; max-height: 125px; padding-top: 8%; padding-bottom: 8%;" />
									</div>
								</div>
<%-- 								<div style="padding: 15px;">
									<div style="margin: auto; width: 200px; height: 125px; border: 1px solid #ccc; border-radius: 5px; text-align: center;" ng-click="proceedPayment('QR')">
										<img ng-src="${pageContext.request.contextPath}/img/icon/qr-code.png" alt="QR Payment" style="max-width: 200px; max-height: 125px; padding-top: 8%; padding-bottom: 8%;" />
									</div>
								</div> --%>
							</div>
						</div>
						
						<div class="item">
							<div id="payment">
								<div class="row" style="font-size: large">
									<div class="col-sm-2">
										<a href="#paymentCarousel" data-slide="prev">
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
											<option selected>----- Select -----</option>
												<option ng-repeat="terminal in terminalList.terminals" value="{{terminal.serialNo}}">{{terminal.name}} ({{terminal.serialNo}})</option>
											</select>
										</div>
									</div>
								</div>
								<div style="padding-bottom: 15px; padding-left: 30px; padding-right: 30px;">
									<label>Tender Amount</label>
									<div style="border: 1px solid #d2d6de; padding: 10px; border-radius: 5px;">
										<div style="padding-top: 15px; padding-left: 15px; padding-right: 15px;">
											<hr style="border: 1px solid #eee;">
											<div class="row">
												<div class="col-sm-3" style="text-align: center; font-size: 20px; color: grey;">
													<label>RM</label>
												</div>
												<div class="col-sm-9" style="text-align: right; padding-right: 70px; font-size: 20px; color: grey;">
													<label id="amount">0.00</label>
												</div>
											</div>
											<table class="table table-borderless">
												<tbody>
													<tr style="border-top: 2px solid #eee;">
														<td><input type="button" value="7" class="form-control btn" ng-click="enterCalculator('amount',7)" /></td>
														<td><input type="button" value="8" class="form-control btn" ng-click="enterCalculator('amount',8)" /></td>
														<td><input type="button" value="9" class="form-control btn" ng-click="enterCalculator('amount',9)" /></td>
														<td rowspan=2 style="vertical-align: middle;">
															<button value="removeButton" class="form-control btn" ng-click="enterCalculator('amount',-10)" style="height: 200%;">
																<i class="fa fa-arrow-left" aria-hidden="true"></i>
															</button>
														</td>
													</tr>
													<tr>
														<td><input type="button" value="4" class="form-control btn" ng-click="enterCalculator('amount',4)" /></td>
														<td><input type="button" value="5" class="form-control btn" ng-click="enterCalculator('amount',5)" /></td>
														<td><input type="button" value="6" class="form-control btn" ng-click="enterCalculator('amount',6)" /></td>
													</tr>
													<tr>
														<td><input type="button" value="1" class="form-control btn" ng-click="enterCalculator('amount',1)" /></td>
														<td><input type="button" value="2" class="form-control btn" ng-click="enterCalculator('amount',2)" /></td>
														<td><input type="button" value="3" class="form-control btn" ng-click="enterCalculator('amount',3)" /></td>
														<td rowspan="2" style="vertical-align: middle; border-bottom: 1px solid #f4f4f4;">
															<button value="confirmButton" class="form-control btn" ng-click="submitPayment()" style="height: 200%;">
																<i class="fa fa-check-square" aria-hidden="true"></i>
															</button>
														</td>
													</tr>
													<tr>
														<td style="border-bottom: 1px solid #f4f4f4;"><input type="button" value="0" class="form-control btn" ng-click="enterCalculator('amount',0)" /></td>
														<td colspan="2" style="border-bottom: 1px solid #f4f4f4;"><input value="00" type="button" class="form-control btn" ng-click="enterCalculator('amount',100)" /></td>
													</tr>
												</tbody>
											</table>
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