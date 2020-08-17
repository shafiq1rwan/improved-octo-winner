<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>
<head>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/select2/select2.min.css">
<script
	src="${pageContext.request.contextPath}/select2/select2.full.min.js"></script>

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
	background-color: #1EC676;
	color: white;
	border-color: #1EC676;
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
	background-color: #1EC676;
	color: white;
	border-color: #1EC676;
}

.calculatoraction:hover:disabled {
	background-color: #dddddd;
	cursor: not-allowed;
}

ul.select2-results__options li {
	padding: 0;
	font-size: 20px;
}

.shadowBox {
	box-shadow: 1px 1px 4px grey;
}
</style>
</head>

<body>
	<div ng-controller="payment_CTRL">
		<div>
			<div class="box box-success"
				style="height: 90vh; overflow-y: auto; background-color: white; margin-bottom: 0px; padding: 10px;">

				<div id="paymentCarousel" class="carousel" data-interval="false">
					<div class="carousel-inner">
						<div class="item active">
							<div id="paymentType">
								<div class="row" style="font-size: large">
									<div class="col-sm-12" style="text-align: center">
										<label>PAYMENT TYPE</label>
									</div>
								</div>
								<div class="row">
									<div class="col-sm-12"
										style="margin-top: 15px; margin-bottom: 15px;">
										<button class="btn btn-block btn-success shadowBox"
											style="margin: auto; width: 200px; height: 125px; border: 1px solid #ccc; border-radius: 5px; background-image:url('${pageContext.request.contextPath}/img/icon/Pay.png'); background-repeat: no-repeat; background-size: 50% 75%; background-position: top; border: 10px solid transparent"
											ng-click="proceedPaymentMethod('full')">
											<font size="4"
												style="bottom: 5%; width: 100%; left: 0%; position: absolute;">Full
												Payment</font>
										</button>
									</div>
								</div>
								<div ng-if="checkDetail.orderType != '3'">
									<div class="row">
										<div class="col-sm-12"
											style="margin-top: 15px; margin-bottom: 15px;">
											<button class="btn btn-block btn-success shadowBox"
												style="margin: auto; width: 200px; height: 125px; border: 1px solid grey; border-radius: 5px; background-image:url('${pageContext.request.contextPath}/img/icon/Pay.png'); background-repeat: no-repeat; background-size: 50% 75%; background-position: top; border: 10px solid transparent"
												ng-click="proceedPaymentMethod('partial')" disabled>
												<font size="4"
													style="bottom: 5%; width: 100%; left: 0%; position: absolute;">Advance
													Payment</font>
											</button>
										</div>
									</div>
								</div>
								<div ng-if="checkDetail.orderType == '3'">
									<div class="row">
										<div class="col-sm-12"
											style="margin-top: 15px; margin-bottom: 15px;">
											<button class="btn btn-block btn-success shadowBox"
												style="margin: auto; width: 200px; height: 125px; border: 1px solid grey; border-radius: 5px; background-image:url('${pageContext.request.contextPath}/img/icon/Pay.png'); background-repeat: no-repeat; background-size: 50% 75%; background-position: top; border: 10px solid transparent"
												ng-click="proceedPaymentMethod('partial')">
												<font size="4"
													style="bottom: 5%; width: 100%; left: 0%; position: absolute;">Advance
													Payment</font>
											</button>
										</div>
									</div>
								</div>
							</div>
						</div>

						<div class="item">
							<div id="paymentMethod">
								<div class="row" style="font-size: large">
									<div class="col-sm-2">
										<a data-target="#paymentCarousel" data-slide="prev"> <i
											class="fa fa-arrow-left" style="color: black;"></i>
										</a>
									</div>
									<div class="col-sm-8" style="text-align: center">
										<label>PAYMENT METHOD</label>
									</div>
									<div class="col-sm-2"></div>
								</div>
								<div class="row">
									<div class="col-sm-12"
										style="margin-top: 15px; margin-bottom: 15px;">
										<%-- <button class="btn btn-block btn-success" style="margin: auto; width: 200px; height: 125px; border: 1px solid #ccc; border-radius: 5px; background-image:url('${pageContext.request.contextPath}/img/icon/money.png'); background-repeat: no-repeat; background-size: 50% 75%; background-position: center;" ng-click="proceedPayment('Cash')"></button> --%>
										<button class="btn btn-block btn-success shadowBox"
											style="margin: auto; width: 200px; height: 125px; border: 1px solid #ccc; border-radius: 5px; background-image:url('${pageContext.request.contextPath}/img/icon/Cash.png'); background-repeat: no-repeat; background-size: 47% 65%; background-position: top; border: 10px solid transparent"
											ng-click="proceedPayment('Cash')">
											<font size="4"
												style="bottom: 8%; width: 100%; left: 0%; position: absolute;">Cash</font>
										</button>
									</div>
								</div>
								<div class="row">
									<div class="col-sm-12"
										style="margin-top: 15px; margin-bottom: 15px;">
										<%-- <button class="btn btn-block btn-success" style="margin: auto; width: 200px; height: 125px; border: 1px solid #ccc; border-radius: 5px; background-image:url('${pageContext.request.contextPath}/img/icon/credit-card.png'); background-repeat: no-repeat; background-size: 50% 75%; background-position: center;" ng-click="proceedPayment('Card')"></button> --%>
										<button class="btn btn-block btn-success shadowBox"
											style="margin: auto; width: 200px; height: 125px; border: 1px solid #ccc; border-radius: 5px; background-image:url('${pageContext.request.contextPath}/img/icon/Cards.png'); background-repeat: no-repeat; background-size: 40% 58%; background-position: center; border: 10px solid transparent"
											ng-click="proceedPayment('Card')">
											<font size="4"
												style="bottom: 3%; width: 100%; left: 0%; position: absolute;">Card</font>
										</button>
									</div>
								</div>
								<div class="row">
									<div class="col-sm-12"
										style="margin-top: 15px; margin-bottom: 15px;">
										<%-- <button class="btn btn-block btn-success" style="margin: auto; width: 200px; height: 125px; border: 1px solid #ccc; border-radius: 5px; background-image:url('${pageContext.request.contextPath}/img/icon/qr-code.png'); background-repeat: no-repeat; background-size: 50% 75%; background-position: center;" ng-click="proceedPayment('QR')"></button> --%>
										<button class="btn btn-block btn-success shadowBox"
											style="margin: auto; width: 200px; height: 125px; border: 1px solid #ccc; border-radius: 5px; background-image:url('${pageContext.request.contextPath}/img/icon/eWallet.png'); background-repeat: no-repeat; background-size: 48% 75%; background-position: top; border: 10px solid transparent"
											ng-click="proceedPayment('QR')">
											<font size="4"
												style="bottom: 5%; width: 100%; left: 0%; position: absolute;">eWallet</font>
										</button>
									</div>
								</div>
							</div>
						</div>

						<div class="item">
							<div id="payment">
								<div class="row" style="font-size: large">
									<div class="col-sm-2">
										<a id="backToPaymentMethod" data-target="#paymentCarousel"
											data-slide="prev"> <i class="fa fa-arrow-left"
											style="color: black;"></i>
										</a>
									</div>
									<div class="col-sm-8" style="text-align: center">
										<label id="paymentMethodName"></label>
									</div>
									<div class="col-sm-2"></div>
								</div>
								<br>
								<div id="terminalList" class="row"
									style="padding-bottom: 15px; padding-left: 30px; padding-right: 30px;">
									<div class="col-sm-12 form-group">
										<label>Terminal</label>
										<div
											style="border: 1px solid #d2d6de; padding: 10px; border-radius: 5px;">
											<select class="select2" id="terminal" style="width: 100%;"
												ng-model="selectedTerminal">
												<option value="" selected>&nbsp;----- Select -----</option>
												<option ng-repeat="terminal in terminalList.terminals"
													value="{{terminal.serialNo}}">&nbsp;{{terminal.name}}
													({{terminal.serialNo}})</option>
											</select>
										</div>
									</div>
								</div>
								<div
									style="padding-bottom: 15px; padding-left: 30px; padding-right: 30px;">
									<label>Tender Amount</label>
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
													<label id="tenderAmount">0.00</label>
												</div>
											</div>
											<hr
												style="border: none; height: 2px; color: black; background-color: black;">
											<div class="row">
												<div class="col-xs-9 col-sm-9">
													<div class="row" style="padding-left: 15px;">
														<div class="col-xs-4 col-sm-4"
															style="padding: 0px; padding-right: 5px; padding-bottom: 5px;">
															<input id="seven" type="button" value="7"
																class="calculator"
																ng-click="enterCalculator('tenderAmount',7)" />
														</div>
														<div class="col-xs-4 col-sm-4"
															style="padding: 0px; padding-right: 5px; padding-bottom: 5px;">
															<input id="eight" type="button" value="8"
																class="calculator"
																ng-click="enterCalculator('tenderAmount',8)" />
														</div>
														<div class="col-xs-4 col-sm-4"
															style="padding: 0px; padding-right: 5px; padding-bottom: 5px;">
															<input id="nine" type="button" value="9"
																class="calculator"
																ng-click="enterCalculator('tenderAmount',9)" />
														</div>
													</div>
													<div class="row" style="padding-left: 15px;">
														<div class="col-xs-4 col-sm-4"
															style="padding: 0px; padding-right: 5px; padding-bottom: 5px;">
															<input id="four" type="button" value="4"
																class="calculator"
																ng-click="enterCalculator('tenderAmount',4)" />
														</div>
														<div class="col-xs-4 col-sm-4"
															style="padding: 0px; padding-right: 5px; padding-bottom: 5px;">
															<input id="five" type="button" value="5"
																class="calculator"
																ng-click="enterCalculator('tenderAmount',5)" />
														</div>
														<div class="col-xs-4 col-sm-4"
															style="padding: 0px; padding-right: 5px; padding-bottom: 5px;">
															<input id="six" type="button" value="6"
																class="calculator"
																ng-click="enterCalculator('tenderAmount',6)" />
														</div>
													</div>
													<div class="row" style="padding-left: 15px;">
														<div class="col-xs-4 col-sm-4"
															style="padding: 0px; padding-right: 5px; padding-bottom: 5px;">
															<input id="one" type="button" value="1"
																class="calculator"
																ng-click="enterCalculator('tenderAmount',1)" />
														</div>
														<div class="col-xs-4 col-sm-4"
															style="padding: 0px; padding-right: 5px; padding-bottom: 5px;">
															<input id="two" type="button" value="2"
																class="calculator"
																ng-click="enterCalculator('tenderAmount',2)" />
														</div>
														<div class="col-xs-4 col-sm-4"
															style="padding: 0px; padding-right: 5px; padding-bottom: 5px;">
															<input id="three" type="button" value="3"
																class="calculator"
																ng-click="enterCalculator('tenderAmount',3)" />
														</div>
													</div>
													<div class="row" style="padding-left: 15px;">
														<div class="col-xs-6 col-sm-6"
															style="padding: 0px; padding-right: 5px; padding-bottom: 5px;">
															<input id="zero" type="button" value="0"
																class="calculator"
																ng-click="enterCalculator('tenderAmount',10)" />
														</div>
														<div class="col-xs-6 col-sm-6"
															style="padding: 0px; padding-right: 5px; padding-bottom: 5px;">
															<input id="zerozero" value="00" type="button"
																class="calculator"
																ng-click="enterCalculator('tenderAmount',100)" />
														</div>
													</div>
												</div>
												<div class="col-xs-3 col-sm-3">
													<div class="row" style="padding-right: 15px;">
														<div class="col-xs-3 col-sm-3"
															style="padding: 0px; padding-bottom: 5px;">
															<button id="remove" value="removeButton"
																class="calculatoraction"
																ng-click="enterCalculator('tenderAmount',-10)">
																<i class="fa fa-arrow-left" aria-hidden="true"></i>
															</button>
														</div>
													</div>
													<div class="row" style="padding-right: 15px;">
														<div class="col-xs-3 col-sm-3"
															style="padding: 0px; padding-bottom: 5px;">
															<button value="confirmButton" class="calculatoraction"
																ng-click="submitPayment()">
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
				<div id="loading_modal" class="modal" tabindex="-1" role="dialog"
					aria-hidden="true" data-keyboard="false" data-backdrop="static"
					role="dialog">
					<div class="modal-content">
						<div class="modal-body text-center">
							<p>{{socketMessage}}</p>
						</div>
					</div>
				</div>
				<!-- Loading Modal [END] -->



				<!-- Scan QR Modal [START] -->
				<div class="modal fade" id="scan_qr_modal" tabindex="-1"
					role="dialog" aria-labelledby="scan_qr_modal" aria-hidden="true"
					data-keyboard="false" data-backdrop="static">
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title">QR Payment</h4>
							</div>
							<div class="modal-body text-center">
								<form ng-submit="executePayment()" autocomplete="off">
									<div class="row">
										<div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
											<div class="form-group">
												<h3>Please scan QR to Pay</h3>
												<!-- 	<input class="form-control" id="qr_content" type="text" ng-model="qrContent" required>
												<br> -->
												<!-- <button class="btn btn-primary" type="submit" ng-disabled="qrContent==''">Submit</button> -->
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
				<div class="modal fade" id="print_receipt_modal" tabindex="-1"
					role="dialog" aria-labelledby="print_receipt_modal"
					aria-hidden="true" data-keyboard="false" data-backdrop="static">
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
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
												<button type="button" class="btn btn-secondary"
													data-dismiss="modal">Close</button>
											</div>
										</div>
									</div>
								</form>
							</div>
						</div>

					</div>
				</div>
				<!-- Print Receipt Modal [END] -->

				<!-- Received Amount Modal -->
				<div class="modal fade" data-backdrop="static"
					id="receivedAmountModal" role="dialog">
					<div class="modal-dialog">
						<div class="modal-content">
							<!-- <div class="modal-header"></div> -->
							<div class="modal-body">
								<button type="button" class="close" data-dismiss="modal">&times;</button>
								<div
									style="padding-bottom: 15px; padding-left: 30px; padding-right: 30px;">
									<label>Received Amount</label>
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
													<label id="receivedAmount">0.00</label>
												</div>
											</div>
											<hr
												style="border: none; height: 2px; color: black; background-color: black;">
											<div class="row">
												<div class="col-xs-9 col-sm-9">
													<div class="row" style="padding-left: 15px;">
														<div class="col-xs-4 col-sm-4"
															style="padding: 0px; padding-right: 5px; padding-bottom: 5px;">
															<input type="button" value="7" class="calculator"
																ng-click="enterCalculator2('receivedAmount',7)" />
														</div>
														<div class="col-xs-4 col-sm-4"
															style="padding: 0px; padding-right: 5px; padding-bottom: 5px;">
															<input type="button" value="8" class="calculator"
																ng-click="enterCalculator2('receivedAmount',8)" />
														</div>
														<div class="col-xs-4 col-sm-4"
															style="padding: 0px; padding-right: 5px; padding-bottom: 5px;">
															<input type="button" value="9" class="calculator"
																ng-click="enterCalculator2('receivedAmount',9)" />
														</div>
													</div>
													<div class="row" style="padding-left: 15px;">
														<div class="col-xs-4 col-sm-4"
															style="padding: 0px; padding-right: 5px; padding-bottom: 5px;">
															<input type="button" value="4" class="calculator"
																ng-click="enterCalculator2('receivedAmount',4)" />
														</div>
														<div class="col-xs-4 col-sm-4"
															style="padding: 0px; padding-right: 5px; padding-bottom: 5px;">
															<input type="button" value="5" class="calculator"
																ng-click="enterCalculator2('receivedAmount',5)" />
														</div>
														<div class="col-xs-4 col-sm-4"
															style="padding: 0px; padding-right: 5px; padding-bottom: 5px;">
															<input type="button" value="6" class="calculator"
																ng-click="enterCalculator2('receivedAmount',6)" />
														</div>
													</div>
													<div class="row" style="padding-left: 15px;">
														<div class="col-xs-4 col-sm-4"
															style="padding: 0px; padding-right: 5px; padding-bottom: 5px;">
															<input type="button" value="1" class="calculator"
																ng-click="enterCalculator2('receivedAmount',1)" />
														</div>
														<div class="col-xs-4 col-sm-4"
															style="padding: 0px; padding-right: 5px; padding-bottom: 5px;">
															<input type="button" value="2" class="calculator"
																ng-click="enterCalculator2('receivedAmount',2)" />
														</div>
														<div class="col-xs-4 col-sm-4"
															style="padding: 0px; padding-right: 5px; padding-bottom: 5px;">
															<input type="button" value="3" class="calculator"
																ng-click="enterCalculator2('receivedAmount',3)" />
														</div>
													</div>
													<div class="row" style="padding-left: 15px;">
														<div class="col-xs-6 col-sm-6"
															style="padding: 0px; padding-right: 5px; padding-bottom: 5px;">
															<input type="button" value="0" class="calculator"
																ng-click="enterCalculator2('receivedAmount',10)" />
														</div>
														<div class="col-xs-6 col-sm-6"
															style="padding: 0px; padding-right: 5px; padding-bottom: 5px;">
															<input value="00" type="button" class="calculator"
																ng-click="enterCalculator2('receivedAmount',100)" />
														</div>
													</div>
												</div>
												<div class="col-xs-3 col-sm-3">
													<div class="row" style="padding-right: 15px;">
														<div class="col-xs-3 col-sm-3"
															style="padding: 0px; padding-bottom: 5px;">
															<button value="removeButton" class="calculatoraction"
																ng-click="enterCalculator2('receivedAmount',-10)">
																<i class="fa fa-arrow-left" aria-hidden="true"></i>
															</button>
														</div>
													</div>
													<div class="row" style="padding-right: 15px;">
														<div class="col-xs-3 col-sm-3"
															style="padding: 0px; padding-bottom: 5px;">
															<button value="confirmButton" class="calculatoraction"
																ng-click="executePayment()">
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
							<!-- <div class="modal-footer"></div> -->
						</div>
					</div>
				</div>

				<!-- Alert Message Modal [START] -->
				<div class="modal fade" id="paymentAlertModal" tabindex="-1"
					role="dialog" aria-labelledby="paymentAlertModal"
					aria-hidden="true" data-keyboard="false" data-backdrop="static">
					<div class="modal-dialog modal-dialog-centered">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close" ng-click="paymentButtonFn()">
									<span aria-hidden="true">&times;</span>
								</button>
							</div>
							<div class="modal-body text-center" ng-bind-html="alertMessage">
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-secondary"
									data-dismiss="modal" ng-click="paymentButtonFn()">Close</button>
							</div>
						</div>
					</div>
				</div>
				<!-- Alert Message Modal [END] -->

			</div>
		</div>
	</div>
</body>
</html>