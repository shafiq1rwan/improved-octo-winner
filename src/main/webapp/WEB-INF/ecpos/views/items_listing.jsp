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

.nav-pills li.active a, .nav-pills li.active a:focus, .nav-pills li.active a:hover {
	border-top-color: #7C01A6;
	background-color: #9200C4;
}

.nav-pills {
	overflow: hidden;
	border: 1px solid #ddd;
	background-color: #f9f9f9;
}

.nav-pills {
	overflow: hidden;
	border: 1px solid #ddd;
	background-color: #f9f9f9;
}

.nav-pills li a {
	border-radius: 0;
	border-top: 0;
	color: #444;
}

.green-button:hover {
	background-color: #3CB371!important;
}

.blue-button:hover {
	background-color: #3C5CB3!important;
}
</style>
</head>

<body>
	<div ng-controller="items_listing_CTRL">
		<div ng-init="initiation();">
			<div class="content-wrapper" style="font-size: 0.9em;">
				<section class="content sectioncalibrator" style="padding-top: 8px; padding-bottom: 8px;">
					<div class="row container-fluid" style="padding-right: 2px; padding-left: 2px;">
						<div class="col-sm-12" style="padding-right: 2px; padding-left: 2px;">
							<div class="box box-primary" style="height: 89vh; overflow-y: auto; background-color: white; margin-bottom: 0px; padding-top: 10px; padding-bottom: 10px; padding-left: 15px; padding-right: 15px;">
								<div class="row" style="text-align: center">
									<div class="col-sm-12">
										<font size="4">ITEMS LISTING</font>
									</div>
								</div>
								<div style="padding-top: 5px; padding-bottom: 5px;">
									<ul class="nav nav-pills nav-justified" role="tablist" style="border-radius: 5px;">
										<li class="active">
											<a data-toggle="pill" ng-click="getDataTable('0')">A La Carte</a>
										</li>
										<li style="border-left: 1px solid #ddd;">
											<a data-toggle="pill" ng-click="getDataTable('1')">Combo</a>
										</li>
										<li style="border-left: 1px solid #ddd;">
											<a data-toggle="pill" ng-click="getDataTable('2')">Modifier Group</a>
										</li>
									</ul>
								</div>
								<div ng-show="itemType == '0' || itemType == '1'">
									<table id="datatable_items" class="table table-bordered table-striped">
										<thead>
											<tr>
												<th>No</th>
												<th>Item Code</th>
												<th>Item Name</th>
												<th></th>
											</tr>
										</thead>
										<tbody></tbody>
										<tfoot></tfoot>
									</table>
								</div>
								<div ng-show="itemType == '2'">
									<table id="datatable_modifierGroups" class="table table-bordered table-striped">
										<thead>
											<tr>
												<th>No</th>
												<th>Item Name</th>
												<th></th>
											</tr>
										</thead>
										<tbody></tbody>
										<tfoot></tfoot>
									</table>
								</div>
							</div>
						</div>
					</div>
				</section>
			</div>
			
			<div class="modal fade" data-backdrop="static" id="itemDetailsModal" role="dialog">
				<div class="modal-dialog">
					<div class="modal-content">
						<!-- <div class="modal-header"></div> -->
						<div class="modal-body">
						
							<div id="itemDetailsCarousel" class="carousel" data-interval="false">
								<div class="carousel-inner">
									<div class="item active">
										<div id="itemDetails">
											<form>
												<div class="row" style="font-size: large">
													<div class="col-sm-1">
														<a id="back" ng-click="backCarousel()">
															<i class="fa fa-arrow-left" style="color: black;"></i>
														</a>
													</div>
													<div class="col-sm-10">
														<div style="text-align: center">
															<label>{{itemDetails.name}}</label>
														</div>
													</div>
													<div class="col-sm-1">
														<button class="close" data-dismiss="modal">&times;</button>
													</div>
												</div>
												<div class="row">
													<div style="text-align: center">
														<div style="margin: auto; margin-bottom: 10px; width: 135px; height: 135px; border: 1px solid #d2d6de; border-radius: 5px; align-items: center; display: flex;">
															<img ng-src="${pageContext.request.contextPath}/{{itemDetails.imagePath}}" alt=itemImage style="max-width: 135px; max-height: 135px;" />
														</div>
													</div>
												</div>
												<div class="row">
													<div class="col-sm-12">
														<div style="text-align: center">
															<font size="2">{{itemDetails.description}}</font>
														</div>
													</div>
												</div>
												<br>
												<div style="margin-left: 2%; margin-right: 2%;">
													<div class="row">
														<div class="col-sm-12 form-group">
															<label>Backend Id</label> 
															<input type="text" class="form-control" ng-model="itemDetails.backendId" disabled />
														</div>
													</div>
													<div class="row">
														<div class="col-sm-12 form-group">
															<label>Shortened Name</label> 
															<input type="text" class="form-control" ng-model="itemDetails.alternativeName" disabled />
														</div>
													</div>
													<div class="row">
														<div class="col-sm-12 form-group">
															<label>Barcode</label> 
															<input type="text" class="form-control" ng-model="itemDetails.barcode" disabled />
														</div>
													</div>
													<div class="row">
														<div class="col-sm-12 form-group">
															<label>Price</label> 
															<input type="text" class="form-control" ng-model="itemDetails.price" disabled />
														</div>
													</div>
													<div class="row">
														<div class="col-sm-6 form-group">
															<label>Taxable</label> 
															<input type="checkbox" class="form-check" ng-model="itemDetails.taxable" disabled />
														</div>
														<div class="col-sm-6 form-group">
															<label>Discountable</label> 
															<input type="checkbox" class="form-check" ng-model="itemDetails.discountable" disabled />
														</div>
													</div>
												</div>
											</form>
										</div>
									</div>
									
									<div class="item">
										<div id="modifierItemList">
											<div class="row" style="font-size: large">
												<div class="col-sm-1">
													<a id="back1" data-target="#itemDetailsCarousel" data-slide-to="2">
														<i class="fa fa-arrow-left" style="color: black;"></i>
													</a>
												</div>
												<div class="col-sm-10">
													<div style="text-align: center">
														<label id="modifierGroupName"></label>
													</div>
												</div>
												<div class="col-sm-1">
													<button class="close" data-dismiss="modal">&times;</button>
												</div>
											</div>
											<div style="border: 1px solid #d2d6de; padding: 10px;">
												<table id="datatable_modifierItemsList" class="table table-bordered table-striped" style="width:100%;">
													<thead>
														<tr>
															<th>Sequence</th>
															<th>Item Code</th>
															<th>Item Name</th>
															<th></th>
														</tr>
													</thead>
													<tbody></tbody>
													<tfoot></tfoot>
												</table>
											</div>
										</div>
									</div>
									
									<div class="item">
										<div id="itemModifierGroupList">
											<div class="row" style="font-size: large">
												<div class="col-sm-1">
													<a id="back2" data-target="#itemDetailsCarousel" data-slide-to="4">
														<i class="fa fa-arrow-left" style="color: black;"></i>
													</a>
												</div>
												<div class="col-sm-10">
													<div style="text-align: center">
														<label id="itemName"></label>
													</div>
												</div>
												<div class="col-sm-1">
													<button class="close" data-dismiss="modal">&times;</button>
												</div>
											</div>
											<div style="border: 1px solid #d2d6de; padding: 10px;">
												<table id="datatable_itemModifierGroupsList" class="table table-bordered table-striped" style="width:100%;">
													<thead>
														<tr>
															<th>Sequence</th>
															<th>Item Name</th>
															<th></th>
														</tr>
													</thead>
													<tbody></tbody>
													<tfoot></tfoot>
												</table>
											</div>
										</div>
									</div>
									
									<div class="item">
										<div id="comboItemTiersList">
											<div class="row" style="font-size: large">
												<div class="col-sm-1"></div>
												<div class="col-sm-10">
													<div style="text-align: center">
														<label id="comboItemName"></label>
													</div>
												</div>
												<div class="col-sm-1">
													<button class="close" data-dismiss="modal">&times;</button>
												</div>
											</div>
											<div style="border: 1px solid #d2d6de; padding: 10px;">
												<table id="datatable_comboItemTiersList" class="table table-bordered table-striped" style="width:100%;">
													<thead>
														<tr>
															<th>Sequence</th>
															<th>Tier Name</th>
															<th>Quantity</th>
															<th></th>
														</tr>
													</thead>
													<tbody></tbody>
													<tfoot></tfoot>
												</table>
											</div>
										</div>
									</div>
									
									<div class="item">
										<div id="comboItemTierItemsList">
											<div class="row" style="font-size: large">
												<div class="col-sm-1">
													<a id="back4" data-target="#itemDetailsCarousel" data-slide-to="3">
														<i class="fa fa-arrow-left" style="color: black;"></i>
													</a>
												</div>
												<div class="col-sm-10">
													<div style="text-align: center">
														<label id="comboItemTierName"></label>
													</div>
												</div>
												<div class="col-sm-1">
													<button class="close" data-dismiss="modal">&times;</button>
												</div>
											</div>
											<div style="border: 1px solid #d2d6de; padding: 10px;">
												<table id="datatable_comboItemTierItemsList" class="table table-bordered table-striped" style="width:100%;">
													<thead>
														<tr>
															<th>Item Code</th>
															<th>Item Name</th>
															<th></th>
														</tr>
													</thead>
													<tbody></tbody>
													<tfoot></tfoot>
												</table>
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
			
		</div>
	</div>
</body>
</html>