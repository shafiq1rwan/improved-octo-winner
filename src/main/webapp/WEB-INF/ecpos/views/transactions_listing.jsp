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

#receipt_content_section {
    height: 60vh;
    width: 100%;
    overflow-y: scroll;
    overflow-x: hidden;
}

.receipt_content_flex_container {
    display: flex;           /* establish flex container */
    flex-direction: row;  /* make main axis vertical */
    align-items: center;     /* center items horizontally, in this case */
}

.receipt_content_flex_item {
    margin: 0 auto;
}

.receipt_content_table>tbody>tr>td {
  border-top: none;
  padding: 2px 0px 0px 5px;
  margin: 0px;
}

</style>
</head>

<body>
	<div ng-controller="transactions_listing_CTRL">
		<div ng-init="initiation();">
			<div class="content-wrapper" style="font-size: 0.9em;">
				<section class="content sectioncalibrator" style="padding-top: 8px; padding-bottom: 8px;">
					<div class="row container-fluid" style="padding-right: 2px; padding-left: 2px;">
						<div class="col-sm-12" style="padding-right: 2px; padding-left: 2px;">
							<div class="well" style="height: 89vh; overflow-y: auto; background-color: white; margin-bottom: 0px; padding-top: 10px; padding-bottom: 10px; padding-left: 15px; padding-right: 15px;">
								<div class="row" style="text-align: center">
									<div class="col-sm-12">
										<font size="4">TRANSACTIONS LISTING</font>
									</div>
								</div>
								<table id="datatable_transactions" class="table table-bordered table-striped">
									<thead>
										<tr>
											<th>Check No</th>
											<th>By</th>
											<th>Transaction Type</th>
											<th>Payment Method</th>
											<th>Payment Type</th>
											<th>Amount</th>
											<th>Status</th>
											<th>Date</th>
											<th></th> 
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
			
			<div class="modal fade" data-backdrop="static" id="transactionDetailsModal" role="dialog">
				<div class="modal-dialog modal-lg">
					<div class="modal-content">
						<div class="modal-body" style="padding: 20px;">
							<button class="close" data-dismiss="modal" ng-click="closeTransactionDetailsModal()">&times;</button>
					
								<!-- upper button group control -->
								<div class="row">
									<!-- <div class="col-sm-4" ng-if="!transaction.isVoid && transaction.isApproved"> -->
									<div class="col-sm-4">
										<div>
											<button class="btn btn-block btn-primary" ng-click="voidTransaction(transaction.id, transaction.isVoid)">Void</button>
										</div>
									</div>
									<div class="col-sm-4">
										<div>
											<button class="btn btn-block btn-info" ng-click="printTransactionReceipt(transaction.id)">Print Receipt</button>
										</div>
									</div>
									<div class="col-sm-4">
										<div ng-if="">
											<button class="btn btn-block btn-info" ng-click="">Continue Action</button>
										</div>
									</div>
								</div>

							<!-- receipt content control -->	
							<div id="receipt_content_section">
							
				 			<div class="receipt_content_flex_container">
							<div class="receipt_content_flex_item">
							
								<!-- receipt header content -->
								<div class="row text-center">
									<h3>{{receiptHeader.storeName}}</h3>
									<p>{{receiptHeader.storeAddress}}</p>
									<p>Contact No: {{receiptHeader.storeContactHpNumber}}</p>
								</div>
								<br>

								<!-- receipt info content -->				
								<table class="table receipt_content_table" border="0">
									<tbody>
										<tr>
											<td>Check No</td>
											<td>{{receiptData.checkNo}}</td>
										</tr>
										<tr>
											<td>Table No</td>
											<td>{{receiptData.tableNo}}</td>
										</tr>								
										<tr>
											<td>Order At</td>
											<td>{{receiptData.createdDate}}</td>
										</tr>
										<tr>
											<td>Staff</td>
											<td>{{receiptData.staff}}</td>
										</tr>
										<tr>
											<td>Trans Type</td>
											<td>{{receiptData.transType}}</td>
										</tr>
									</tbody>
								</table>


								<!-- receipt content -->
								<table class="table receipt_content_table">
									<thead>
										<tr style="border-top: 2px solid #f4f4f4;">
											<th class="text-left" style="padding: 8px 0px 8px 5px;">Qty</th>
											<th class="text-left" style="padding: 8px 0px 8px 5px;">Name</th>
											<th class="text-right" style="padding: 8px 0px 8px 5px;">Amt({{receiptHeader.storeCurrency}})</th>
										</tr>
									</thead>
									<tbody>
										<tr ng-repeat-start="grandParentItem in grandParentItemArray">
											<td class="text-left">{{grandParentItem.itemQuantity}}</td>
											<td class="text-left">{{grandParentItem.itemName}}</td>
											<td class="text-right">{{grandParentItem.totalAmount| number:2}}</td>
										</tr>
										<tr ng-repeat-start="parentItem in grandParentItem.parentItemArray">
											<td class="text-left"></td>
											<td class="text-left">:{{parentItem.itemName}}</td>
											<td class="text-right">{{parentItem.totalAmount| number:2}}</td>
										</tr>
										<tr ng-repeat-start="childItem in parentItem.childItemArray">
											<td class="text-left"></td>
											<td class="text-left">&nbsp;&nbsp;&nbsp;&nbsp;*{{childItem.itemName}}</td>
											<td class="text-right">{{childItem.totalAmount| number:2}}</td>
										</tr>
										<!-- close the multiple ng-repeat start -->
										<tr ng-repeat-end ng-if="false"></tr>
										<tr ng-repeat-end ng-if="false"></tr>
										<tr ng-repeat-end ng-if="false"></tr>
									</tbody>
								</table>

								<!-- Content Summary -->
								<table class="table receipt_content_table" border="0">
									<tbody>
										<tr>
											<td class="text-left">Subtotal</td>
											<td class="text-right">{{receiptData.totalAmount|number:2}}</td>
										</tr>
										
										<tr ng-if="taxCharges.length > 0" ng-repeat="charge in taxCharges">
											<td class="text-left">{{charge.name}}({{charge.rate}})</td>
											<td class="text-right">{{charge.chargeAmount|number:2}}</td>
										</tr>
										
										<tr>
											<td class="text-left">Rounding Adjustment</td>
											<td class="text-right">{{receiptData.totalAmountWithTaxRoundingAdjustment|number:2}}</td>
										</tr>
										
										<tr>
											<td class="text-left"><strong>Net Total</strong></td>
											<td class="text-right"><strong>{{receiptData.grandTotalAmount|number:2}}</strong></td>
										</tr>
										
										<!-- if  this is cash payment -->
										<tr ng-if="cashData">
											<td class="text-left">Cash</td>
											<td class="text-right">{{cashData.cashReceivedAmount|number:2}}</td>
										</tr>
										
										<tr ng-if="cashData">
											<td class="text-left">Change</td>
											<td class="text-right">{{cashData.cashChangeAmount|number:2}}</td>
										</tr>
									
									</tbody>
								</table>
								<br>
								
								<!-- Card/QR -->
								<div ng-if="cardData" class="row">
									
									<p class="col-md-12 text-center">
										***Cashless Transaction Information***
									</p>
								
									<table class="table receipt_content_table" border="0">
										<tbody>
											<tr>
												<td>CARD TYPE</td>
												<td>{{cardData.cardType}}</td>
											</tr>
									
											<tr>
												<td>TID</td>
												<td>{{cardData.tid}}</td>
											</tr>
									
											<tr>
												<td>MID</td>
												<td>{{cardData.mid}}</td>
											</tr>
									
											<tr>
												<td>DATE</td>
												<td>{{cardData.date}}</td>
											</tr>
											
											<tr>
												<td>TIME</td>
												<td>{{cardData.time}}</td>
											</tr>
											
											
											<tr>
												<td>CARD NUM</td>
												<td>{{cardData.maskedCardNo}}</td>
											</tr>
											
											<tr>
												<td>EXPIRY DATE</td>
												<td>{{cardData.cardExpiry}}</td>
											</tr>
									
											<tr>
												<td>APPR CODE</td>
												<td>{{cardData.approvalCode}}</td>
											</tr>
											
											<tr>
												<td>RREF NUM</td>
												<td>{{cardData.rRefNo}}</td>
											</tr>
											
											<tr>
												<td>BATCH NUM</td>
												<td>{{cardData.batchNo}}</td>
											</tr>
											
											<tr>
												<td>INV NUM</td>
												<td>{{cardData.invoiceNo}}</td>
											</tr>
									
											<tr>
												<td>UID</td>
												<td>{{cardData.uid}}</td>
											</tr>
											
											<tr>
												<td>TC</td>
												<td>{{cardData.tc}}</td>
											</tr>

											<tr>
												<td>AID</td>
												<td>{{cardData.aid}}</td>
											</tr>
										
											<tr>
												<td>APP</td>
												<td>{{cardData.app}}</td>
											</tr>
										</tbody>
									</table>
								
									<br>
										
									<div ng-if="cardData.terminalVerification == '1'" class="text-center">
										<div class="text-center">
											Pin Verified Success,
										</div>
										<br>
										<div class="text-center">
											No Signature Required
										</div>
									</div>
									
									<div ng-if="cardData.terminalVerification == '2'" class="text-center">
										<div class="text-center">
											Signature Required
										</div>
									</div>
									
									<div ng-if="cardData.terminalVerification == '3'" class="text-center">
										<div class="text-center">
											No Signature Required
										</div>
									</div>
								
									<br>
								</div>
						
								<div ng-if="qrData" class="row">
									
									<p class="col-md-12 text-center">
										***Cashless Transaction Information***
									</p>
									
									<table class="table receipt_content_table" border="0">
										<tbody>
											<tr>
												<td>QR ISSUER</td>
												<td>{{qrData.issuerType}}</td>
											</tr>
											
											<tr>
												<td>UID</td>
												<td>{{qrData.uid}}</td>
											</tr>

											<tr>
												<td>TID</td>
												<td>{{qrData.tid}}</td>
											</tr>
											
											<tr>
												<td>MID</td>
												<td>{{qrData.mid}}</td>
											</tr>
											
											<tr>
												<td>DATE</td>
												<td>{{qrData.date}}</td>
											</tr>
											
											<tr>
												<td>TIME</td>
												<td>{{qrData.time}}</td>
											</tr>
											
											<tr>
												<td>TRACE NUM</td>
												<td>{{qrData.traceNo}}</td>
											</tr>
											
											<tr>
												<td>AUTH NUM</td>
												<td>{{qrData.authNo}}</td>
											</tr>
											
											<tr>
												<td>QR USER ID</td>
												<td>{{qrData.userID}}</td>
											</tr>
											
											<tr>
												<td>AMOUNT (MYR)</td>
												<td>{{qrData.amountMYR}}</td>
											</tr>
											
											<tr ng-if="amountRMB">
												<td>AMOUNT (RMB)</td>
												<td>{{qrData.amountRMB}}</td>
											</tr>
											
										</tbody>			
									</table>

									<br></br>
										<!-- QR image -->
										<div class="row">
											<div class="text-center">	
												<img id="QRImage" ng-src="{{qrImage}}" style="height: 50%; width: 50%;" />
											</div>
										</div>
										
									<br>
								</div>
								
								<div class="row text-center">
									Please Come Again
								</div>

								</div>
								</div>
								<!-- end of flex container  -->

							</div> <!-- end of modal body -->

						</div>
					</div>
				</div>		
			</div>
			
			
			<!-- Loading Modal [START] -->
			<div id="loading_modal" class="modal" tabindex="-1" role="dialog" aria-hidden="true" data-keyboard="false" data-backdrop="static" role="dialog">
					<div class="modal-content">
						<div class="modal-body text-center">
							<p>{{voidMessage}}</p>
						</div>
					</div>
			</div>
			<!-- Loading Modal [END] -->

		</div>
	</div>
</body>
</html>