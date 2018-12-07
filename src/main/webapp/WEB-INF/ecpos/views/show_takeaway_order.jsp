<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>

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

#home {
	text-align: center;
}

.box {
	border-radius: 5px;
	box-shadow: 1px 2px 10px silver;
	display: inline-block;
	padding: 10px;
}

.special_box {
	border-radius: 5px;
	box-shadow: 1px 2px 10px silver;
	overflow-x: scroll;
	width: 100%;
}

.label {
	font-size: 120%;
	padding-right: 10px;
}

.buttons {
	padding: 20px;
}

.table-borderless tbody tr td, .table-borderless tbody tr th,
	.table-borderless thead tr th, .table-borderless thead tr td,
	.table-borderless tfoot tr th, .table-borderless tfoot tr td {
	border: none;
}

.table-fixed thead {
	height: calc(100vh - 100px);
	width: 97%;
}

.table-fixed tbody {
	height: calc(100vh - 100px);
	overflow-y: auto;
	width: 100%;
}

tfoot th {
	border: none;
}

.container {
	overflow: hidden;
}

.column {
	float: left;
	margin: 20px;
	background-color: grey;
	padding-bottom: 100%;
	margin-bottom: -100%;
}
</style>

<body>

	<div ng-controller="Show_Takeaway_Order_CTRL" ng-init="getItemGroup();">
		<div class="content-wrapper" style="font-size: 0.9em;">
			<section class="content sectioncalibrator"
				style="padding-right: 15px; padding-left: 15px;">

				<div class="row container-fluid"
					style="padding-right: 2px; padding-left: 2px;">

					<!-- START of left well -->
					<div class="col-md-6"
						style="padding-right: 2px; padding-left: 2px;">

						<div class="well" style="height: calc(100vh - 100px);">

							<div class="row">
								<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
									<a data-toggle="tab" ng-click="getItemGroup()"
										class="btn btn-block btn-primary">CATALOGUE</a>
								</div>
							</div>

							<div class="tab-pane">
								<hr class="divider">
								<div class="row" style="max-height: 65vh; overflow-y: auto;">

									<div class="panel-body" style="width: max-width">
										<div id='div_category'
											style="height: calc(100vh - 260px); overflow-y: auto;">

											<div id="group_item_container" ng-show="insideGroupStatus==0"
												ng-hide="insideGroupStatus==1">
												<div ng-repeat="group_list in groupList.group_list">
													<div class="col-lg-4 col-md-4 col-sm-4 col-xs-6"
														style="display: table-cell; padding-right: 2px; padding-left: 2px;">
														<a ng-click="getGroupItem(group_list.groupid)">
															<div class="panel panel-default text-center">
																<div class="panel-body center-block"
																	style="color: grey; font-weight: bold; font-size: small;">
																	{{group_list.groupname}}</div>
															</div>
														</a>
													</div>
												</div>
											</div>

											<div id="group_detail_item_container"
												ng-show="insideGroupStatus==1"
												ng-hide="insideGroupStatus==0">

												<div class="col-lg-4 col-md-4 col-sm-4 col-xs-6"
													ng-repeat="item in itemList.item_list">
													<a
														ng-click="addItemIntoTakeAwayCheck(item.item_id, item.item_code, item.name, item.item_price)">
														<div class="panel panel-default text-center">
															<img src="${pageContext.request.contextPath}"
																+"item.image_path" width="200" height="200"
																alt="nothing" />
															<div class="panel-body center-block"
																style="color: grey; font-weight: bold; font-size: small; word-wrap: break-word;">
																{{item.name}}</div>
														</div>
													</a>
												</div>

											</div>

										</div>
									</div>

								</div>
							</div>

						</div>
					</div>

					<!-- END of left well -->

					<div class="col-md-6"
						style="padding-right: 2px; padding-left: 2px;">
						<!-- START of right well -->
						<div class="well" style="height: calc(100vh - 100px);">

							<table class='table table-fixed' id="takeaway_datatable">
								<thead>
									<tr>
										<th class='col-md-1 col-xs-1'></th>
										<th></th>
										<th class='col-md-3 col-xs-3'>Code</th>
										<th class='col-md-5 col-xs-5 text-left'>Items</th>
										<!-- 					<th class='col-md-2 col-xs-2 text-center'>Qty</th> -->
										<th class='col-md-3 col-xs-3 text-right'>Price</th>

									</tr>
								</thead>
								<tbody>
								</tbody>

								<tfoot>

									<tr>
										<th class='col-md-1 col-xs-1'></th>
										<th></th>
										<th class='col-md-3 col-xs-3'>Total</th>
										<th class='col-md-5 col-xs-5'></th>
										<th class='col-md-3 col-xs-3 text-right'><font
											color='Grey'>{{takeAwayTotalPrice | number:2}}</font></th>

									</tr>
								</tfoot>
							</table>

							<!-- Button group control -->
							<div class="row">
								<hr class="divider">
							</div>

							<div class="row" style="margin-bottom: 5px;">
								<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
									<button class="btn btn-block btn-primary"
										ng-click="payTakeAwayOrder()"
										ng-disabled="isPaymentAvailable === true ? true:false"
										id="selectable_btn">PAY</button>
								</div>
								<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
									<button class="btn btn-block btn-danger"
										ng-click="removeItemFromTakeAwayCheck()"
										ng-disabled="isRemoveAvailable === true? true:false">REMOVE</button>
								</div>
							</div>
						</div>
						<!-- END of right well -->
					</div>
				</div>







			</section>
		</div>
	</div>

</body>
</html>