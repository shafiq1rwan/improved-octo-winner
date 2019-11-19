<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/adminLTE-2.4.5/plugins/iCheck/minimal/_all.css">
<style>
<style>
.nav-pills li.active a, .nav-pills li.active a:focus, .nav-pills li.active a:hover
	{
	border-top-color: #00dcfb;
	background-color: #00dcfb;
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

.itemname {
	height: 35px;
	max-height: 35px;
	overflow-wrap: break-word;
	overflow: hidden;
	text-overflow: ellipsis;
	-webkit-line-clamp: 2;
	display: -webkit-box;
	-webkit-box-orient: vertical;
}

@media only screen and (max-width:320px) {
	.sectioncalibrator {
		height: calc(100vh - 100px);
		overflow-y: scroll;
	}
	.testscreen {
		display: inline-block
	}
}

.input-container {
  display: -ms-flexbox; /* IE10 */
  display: flex;
  width: 100%;
  margin-bottom: 15px;
}

.icon {
  padding: 10px;
  background: dodgerblue;
  color: white;
  min-width: 20px;
  text-align: center;
}

.input-field {
  width: 100%;
  padding: 10px;
  outline: none;
}

.input-field:focus {
  border: 2px solid dodgerblue;
}

img {
  width:200px;
  height:200px;
  border-radius:4px;
  object-fit:cover;
}

img:hover {
	opacity: 0.5;
}

.shadowBox {
  box-shadow: 1px 1px 3px grey;
}
</style>
</head>

<body>
	<div ng-controller="menu_CTRL">
		<div ng-init="menuInitiation();">
			<div class="box box-primary" style="background-color: white; margin-bottom: 0px; padding: 10px; height: 80vh">

				<div id="menuCarousel" class="carousel" data-interval="false">
				<div class="input-container" style="width: 100%">
				<!-- <i class="fa fa-search icon" style="size: 10px"></i>  -->
						<input type="text" class="form-control" ng-model="barcode"
							ng-click="barcodeOrder()"
							ng-keydown="$event.keyCode === 13 && barcodeOrder()"
							id="barcode_input" required placeholder="Search or scan for items" autofocus="autofocus" onblur="this.focus()"/>
					</div>
					<div class="carousel-inner">
						<div class="item active">
							<div id="category">
								<div class="row border-1" style="font-size: large;">
									<div class="col-sm-12" style="text-align: center; border-bottom: 1px solid gray;">
										<label>MENU</label>
									</div>
								</div>
								<div class="row">
									<div ng-repeat="category in categories.data">
										<div class="col-xs-6 col-sm-4 col-m-6 col-lg-3 padding-0">
											<br>
											<div style="text-align: center;" ng-click="getMenuItems(category)">
												<div style="margin: auto; width: 120px; height: 120px; border: 5px solid white; border-radius: 5px; align-items: center; display: flex;" class="shadowBox">
													<img ng-src="${pageContext.request.contextPath}/{{category.imagePath}}" alt={{category.name}} style="max-width: 100%; max-height: 100%;" />
												</div>
												<br>
												<div class="itemname">
													<b>{{category.name}}</b>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
						
						<div class="item">
							<div id="menuItem">
								<div class="row" style="font-size: large">
									<div class="col-xs-2 col-sm-2" style="border-bottom: 1px solid gray;">
										<a data-target="#menuCarousel" data-slide="prev">
											<i class="fa fa-arrow-left" style="color: black;"></i>
										</a>
										<label>&nbsp;</label>
									</div>
									<div class="col-xs-8 col-sm-8 " style="text-align: center; border-bottom: 1px solid gray;">
										<label id="categoryName"></label>
									</div>
									<div class="col-xs-2 col-sm-2" style="border-bottom: 1px solid gray;"><label>&nbsp;</label></div>
								</div>
								<div class="row">
									<div ng-repeat="item in menuItems.data">
										<div class="col-xs-6 col-sm-4 col-m-6 col-lg-3 padding-0">
											<br>
											<div style="text-align: center" ng-click="action(item)">
												<div style="margin: auto; width: 120px; height: 120px; border: 5px solid white; border-radius: 5px; align-items: center; display: flex;" class="shadowBox">
													<img ng-src="${pageContext.request.contextPath}/{{item.imagePath}}" alt={{item.name}} style="max-width: 100%; max-height: 100%;" />
												</div>
												<br>
												<div class="itemname">
													<b>{{item.name}}</b>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>

						<div class="item">
							<div id="tier">
								<div class="row" style="font-size: large">
									<div class="col-xs-10 col-sm-12">
										<a data-target="#menuCarousel" data-slide="prev" ng-click="resetTemporary()">
											<i class="fa fa-arrow-left" style="color: black;"></i>
										</a>
									</div>
								</div>
								<div class="row" style="text-align: center">
									<div class="col-xs-4 col-sm-4" style="padding: 0px;"></div>
									<div class="col-xs-4 col-sm-4" style="padding: 0px;">
										<div ng-if="screenWidth > 1050" style="margin: auto; width: 120px; height: 120px; border: 1px solid #d2d6de; border-radius: 5px; align-items: center; display: flex;">
											<img ng-src="${pageContext.request.contextPath}/{{tiers.imagePath}}" style="max-width: 100%; max-height: 100%;" />
										</div>
										<div ng-if="screenWidth < 1050" style="margin: auto; width: 120px; height: 120px; border: 1px solid #d2d6de; border-radius: 5px; align-items: center; display: flex;">
											<img ng-src="${pageContext.request.contextPath}/{{tiers.imagePath}}" style="max-width: 100%; max-height: 100%;" />
										</div>
									</div>
									<div class="col-xs-2 col-sm-4" style="padding: 0px;">
										<div ng-if="screenWidth > 1050" style="width: 120px; height: 120px; align-items: flex-end; display: flex;">
											<div class="input-group input-group-sm">
												<span class="input-group-btn" ng-click="minusItemQuantity()">
													<button type="button" class="btn btn-number">
														<span class="glyphicon glyphicon-minus"></span>
													</button>
												</span> 
												<input id="itemQuantity" type="text" style="text-align: center;" class="form-control input-number" value="1"> 
												<span class="input-group-btn" ng-click="addItemQuantity()">
													<button type="button" class="btn btn-number">
														<span class="glyphicon glyphicon-plus"></span>
													</button>
												</span>
											</div>
										</div>
										<div ng-if="screenWidth < 1050" style="width: 120px; height: 120px; align-items: flex-end; display: flex;">
											<div class="input-group input-group-sm">
												<span class="input-group-btn" ng-click="minusItemQuantity()">
													<button type="button" class="btn btn-number">
														<span class="glyphicon glyphicon-minus"></span>
													</button>
												</span> 
												<input id="itemQuantity" type="text" style="text-align: center;" class="form-control input-number" value="1"> 
												<span class="input-group-btn" ng-click="addItemQuantity()">
													<button type="button" class="btn btn-number">
														<span class="glyphicon glyphicon-plus"></span>
													</button>
												</span>
											</div>
										</div>
									</div>
								</div>
								<div class="row" style="text-align: center">
									<div>
										<font size="3"><b id="itemName"></b></font>
									</div>
									<div>
										<font size="1">{{tiers.description}}</font>
									</div>
								</div>
								<br>
								<ul class="nav nav-pills nav-justified" role="tablist" style="border-radius: 8px;">
									<li class="tab-width" ng-repeat="tier in tiers.data" style="border-left: 1px solid #ddd;" id="pill{{tier.id}}">
										<a data-toggle="pill" ng-click="tierQuantityLoop(tier)" id="pillTab{{tier.id}}">{{tier.name}}</a>
									</li>
								</ul>
								<div style="padding: 5px; padding-bottom: 0px;">
									<div class="row" style="margin-bottom: 5px;">
										<div class="col-xs-1 col-sm-1">
											<div><b><u>No</u></b></div>
										</div>
										<div class="col-xs-7 col-sm-7">
											<div><b><u>Item</u></b></div>
										</div>
										<div class="col-xs-4 col-sm-4"></div>
									</div>
									<div ng-repeat="tierDetail in tierDetails.quantityArray">
										<div class="row" style="margin-bottom: 5px;">
											<div class="col-xs-1 col-sm-1">
												<div>{{$index+1}}</div>
											</div>
											<div class="col-xs-7 col-sm-7">
												<div id="selectedItem{{$index+1}}"></div>
												<div id="selectedModifiers{{$index+1}}"></div>
											</div>
											<div class="col-xs-7.5 col-sm-4">
												<button class="btn btn-primary pull-right" ng-click="openMenuItemModal(tierDetails, $index+1, 'combo')">Select Item</button>
											</div>
										</div>
										<hr style="margin: 8px;">
									</div>
								</div>
								<div style="text-align: center;">
									<button class="btn btn-primary" ng-click="submitOrder()">Submit</button>
								</div>
							</div>
						</div>
						
						<div class="item">
							<div id="alaCarte">
								<div class="row" style="font-size: large">
									<div class="col-xs-12 col-sm-12">
										<a data-target="#menuCarousel" data-slide-to="1" ng-click="resetTemporary()">
											<i class="fa fa-arrow-left" style="color: black;"></i>
										</a>
									</div>
								</div>
								<div class="row" style="text-align: center">
									<div class="col-xs-4 col-sm-4" style="padding: 0px;"></div>
									<div class="col-xs-4 col-sm-4" style="padding: 0px;">
										<div ng-if="screenWidth > 1050" style="margin: auto; width: 120px; height: 120px; border: 1px solid #d2d6de; border-radius: 5px; align-items: center; display: flex;">
											<img ng-src="${pageContext.request.contextPath}/{{alaCarte.imagePath}}" style="max-width: 100%; max-height: 100%;" />
										</div>
										<div ng-if="screenWidth < 1050" style="margin: auto; width: 120px; height: 120px; border: 1px solid #d2d6de; border-radius: 5px; align-items: center; display: flex;">
											<img ng-src="${pageContext.request.contextPath}/{{alaCarte.imagePath}}" style="max-width: 100%; max-height: 100%;" />
										</div>
									</div>
									<div class="col-xs-4 col-sm-4" style="padding: 0px;">
										<div ng-if="screenWidth > 1050" style="width: 120px; height: 120px; align-items: flex-end; display: flex;">
											<div class="input-group input-group-sm">
												<span class="input-group-btn" ng-click="minusAlaCarteItemQuantity()">
													<button type="button" class="btn btn-number">
														<span class="glyphicon glyphicon-minus"></span>
													</button>
												</span> 
												<input id="alaCarteItemQuantity" type="text" style="text-align: center;" class="form-control input-number" value="1"> 
												<span class="input-group-btn" ng-click="addAlaCarteItemQuantity()">
													<button type="button" class="btn btn-number">
														<span class="glyphicon glyphicon-plus"></span>
													</button>
												</span>
											</div>
										</div>
										<div ng-if="screenWidth < 1050" style="width: 120px; height: 120px; align-items: flex-end; display: flex;">
											<div class="input-group input-group-sm">
												<span class="input-group-btn" ng-click="minusAlaCarteItemQuantity()">
													<button type="button" class="btn btn-number">
														<span class="glyphicon glyphicon-minus"></span>
													</button>
												</span> 
												<input id="alaCarteItemQuantity" type="text" style="text-align: center;" class="form-control input-number" value="1"> 
												<span class="input-group-btn" ng-click="addAlaCarteItemQuantity()">
													<button type="button" class="btn btn-number">
														<span class="glyphicon glyphicon-plus"></span>
													</button>
												</span>
											</div>
										</div>
									</div>
								</div>
								<div class="row" style="text-align: center">
									<div>
										<font size="3"><b id="alaCarteItemName"></b></font>
									</div>
									<div>
										<font size="1">{{alaCarte.description}}</font>
									</div>
								</div>
								<br>
								<div style="padding: 5px; padding-bottom: 0px;">
									<div id="alaCarteModifier">
										<div class="row" style="margin-bottom: 5px;">
											<div class="col-xs-1 col-sm-1">
												<div><b><u>No</u></b></div>
											</div>
											<div class="col-xs-6 col-sm-6">
												<div><b><u>Modifier</u></b></div>
											</div>
											<div class="col-xs-5 col-sm-5"></div>
										</div>
										<div class="row" style="margin-bottom: 5px;">
											<div class="col-xs-1 col-sm-1">
												<div>{{$index+1}}</div>
											</div>
											<div class="col-xs-6 col-sm-6">
												<div id="selectedAlaCarteModifiers"></div>
											</div>
											<div class="col-xs-5 col-sm-5">
												<button class="btn btn-primary pull-right" ng-click="openMenuItemModal(alaCarte, 0, 'alaCarte')">Select Modifier</button>
											</div>
										</div>
									</div>
									<hr style="margin: 8px;">
								</div>
								<div style="text-align: center;">
									<button class="btn btn-primary" ng-click="submitOrder()">Submit</button>
								</div>
							</div>
						</div>
					</div>
				</div>
				
			</div>
			
			<div class="modal fade" data-backdrop="static" id="menuItemModal" role="dialog">
				<div class="modal-dialog">
					<div class="modal-content">
						<!-- <div class="modal-header"></div> -->
						<div class="modal-body">
						
							<div id="itemCarousel" class="carousel" data-interval="false">
								<div class="carousel-inner">
									<div class="item active">
										<div id="tierItem">
											<div class="row" style="font-size: large">
												<div style="text-align: center">
													<label id="tierName"></label>
												</div>
											</div>
											<div class="row">
												<div ng-repeat="tierItemDetail in tierItemDetails.data">
													<div class="col-xs-6 col-sm-4">
														<br>
														<div style="text-align: center" ng-click="getModifiers(tierItemDetail)">
															<div style="margin: auto; width: 120px; height: 120px; border: 1px solid #d2d6de; border-radius: 5px; align-items: center; display: flex;">
																<img ng-src="${pageContext.request.contextPath}/{{tierItemDetail.imagePath}}" style="max-width: 100%; max-height: 100%;" />
															</div>
															<div class="itemname">
																<input type="radio" name="{{tierItemDetails.tierName}}" value="{{tierItemDetail.id}}" class="iradio_minimal-green"/> <b>{{tierItemDetail.name}}</b>
															</div>
														</div>
													</div>
												</div>
											</div>
										</div>
									</div>
										
									<div class="item">
										<div id="modifier">
											<div class="row" style="font-size: large">
												<div class="col-xs-2 col-sm-2">
													<a id="back" data-target="#itemCarousel" data-slide="prev">
														<i class="fa fa-arrow-left" style="color: black;"></i>
													</a>
												</div>
												<div class="col-xs-8 col-sm-8" style="text-align: center">
													<label id="tierItemDetailName"></label>
												</div>
												<div class="col-xs-4 col-sm-2"></div>
											</div>
											<div ng-repeat="modifier in modifiers.data" style="padding-left: 15px; padding-right: 15px;">
												<br>
												<div>
													<label>{{modifier.name}}</label>
												</div>
												<div class="row">
													<div ng-repeat="modifierDetail in modifier.data">
														<div class="col-xs-6 col-sm-3">
															<label>
																<input type="radio" name="{{modifier.name}}" value="{{modifierDetail.id}}!!{{modifierDetail.backendId}}!!{{modifierDetail.name}}" class="iradio_minimal-green" required /> <b>{{modifierDetail.name}}</b>
															</label>
														</div>
													</div>
												</div>
											</div>
											<br>
											<div>
												<button class="btn btn-primary pull-right" ng-click="saveTemporaryArray()">Save</button>
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