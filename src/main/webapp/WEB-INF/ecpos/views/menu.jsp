<!DOCTYPE html>
<html>
<head>
<style>
.nav-pills li.active a, .nav-pills li.active a:focus, .nav-pills li.active a:hover {
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
</style>
</head>

<body>
	<div ng-controller="menu_CTRL">
		<div ng-init="initiation();">
			<div class="well" style="background-color: white; max-height: 87vh; overflow-y: auto; padding: 15px;">

				<div id="menuCarousel" class="carousel" data-interval="false">
					<div class="carousel-inner">
						<div class="item active">
							<div id="category">
								<div class="row" style="font-size: large">
									<div class="col-sm-2"></div>
									<div class="col-sm-8" style="text-align: center">
										<label>Menu</label>
									</div>
									<div class="col-sm-2"></div>
								</div>
								<br>
								<div class="row">
									<div ng-repeat="category in categories.jary">
										<div class="col-sm-4 form-group" style="margin-bottom: 0px; padding: 15px; padding-top: 0px;">
											<div style="text-align: center;" ng-click="getMenuItems(category)">
												<div style="margin: auto; width: 150px; height: 150px; border: 1px solid #d2d6de; border-radius: 5px; align-items: center; display: flex;">
													<img ng-src="${pageContext.request.contextPath}{{category.imagePath}}" alt={{category.name}} style="max-width: 150px; max-height: 150px;" />
												</div>
												<div>
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
									<div class="col-sm-2">
										<a href="#menuCarousel" data-slide="prev">
											<i class="fa fa-arrow-left" style="color: black;"></i>
										</a>
									</div>
									<div class="col-sm-8" style="text-align: center">
										<label id="categoryName"></label>
									</div>
									<div class="col-sm-2"></div>
								</div>
								<br>
								<div class="row">
									<div ng-repeat="item in menuItems.jary">
										<div class="col-sm-4 form-group" style="margin-bottom: 0px; padding: 15px; padding-top: 0px;">
											<div style="text-align: center" ng-click="action(item)">
												<div style="margin: auto; width: 150px; height: 150px; border: 1px solid #d2d6de; border-radius: 5px; align-items: center; display: flex;">
													<img ng-src="${pageContext.request.contextPath}{{item.imagePath}}" alt={{item.name}} style="max-width: 150px; max-height: 150px;" />
												</div>
												<div>
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
									<div class="col-sm-12">
										<a href="#menuCarousel" data-slide="prev" ng-click="resetTemporary()">
											<i class="fa fa-arrow-left" style="color: black;"></i>
										</a>
									</div>
								</div>
								<div class="row" style="text-align: center">
									<div class="col-sm-4"></div>
									<div class="col-sm-4">
										<div style="margin: auto; width: 150px; height: 150px; border: 1px solid #d2d6de; border-radius: 5px; align-items: center; display: flex;">
											<img ng-src="${pageContext.request.contextPath}{{tiers.imagePath}}" style="max-width: 150px; max-height: 150px;" />
										</div>
									</div>
									<div class="col-sm-4">
										<div style="width: 150px; height: 150px; align-items: flex-end; display: flex;">
											<div class="input-group">
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
										<font size="4"><b id="itemName"></b></font>
									</div>
									<div>
										<font size="2">{{tiers.description}}</font>
									</div>
								</div>
								<br>
								<ul class="nav nav-pills nav-justified" role="tablist" style="border-radius: 5px;">
									<li ng-repeat="tier in tiers.jary | limitTo : 1" class="active">
										<a data-toggle="pill" ng-click="tierQuantityLoop(tier)" id="pillTab{{tier.id}}">{{tier.name}}</a>
									</li>
									<li ng-repeat="tier in tiers.jary | limitTo : 10 : 1" style="border-left: 1px solid #ddd;">
										<a data-toggle="pill" ng-click="tierQuantityLoop(tier)" id="pillTab{{tier.id}}">{{tier.name}}</a>
									</li>
								</ul>
								<div style="padding: 15px; padding-bottom: 0px;">
									<div class="row">
										<div class="col-sm-1 form-group">
											<div><b><u>No</u></b></div>
										</div>
										<div class="col-sm-8 form-group">
											<div><b><u>Item</u></b></div>
										</div>
										<div class="col-sm-3 form-group"></div>
									</div>
									<div ng-repeat="tierDetail in tierDetails.quantityArray">
										<div class="row">
											<div class="col-sm-1 form-group">
												<div>{{$index+1}}</div>
											</div>
											<div class="col-sm-8 form-group">
												<div id="selectedItem{{$index+1}}"></div>
												<div id="selectedModifiers{{$index+1}}"></div>
											</div>
											<div class="col-sm-3 form-group">
												<button class="btn btn-primary pull-right" ng-click="openMenuItemModal(tierDetails, $index+1, 'combo')">Select Item</button>
											</div>
										</div>
										<hr style="padding-bottom: 15px;">
									</div>
								</div>
								<div style="text-align: center;">
									<button class="btn btn-primary" ng-click="submitOrder()" style="background-color: #00FA9A; border-color: #00FA9A;">Submit</button>
								</div>
							</div>
						</div>
						
						<div class="item">
							<div id="alaCarte">
								<div class="row" style="font-size: large">
									<div class="col-sm-12">
										<a href="#menuCarousel" data-slide-to="1" ng-click="resetTemporary()">
											<i class="fa fa-arrow-left" style="color: black;"></i>
										</a>
									</div>
								</div>
								<div class="row" style="text-align: center">
									<div class="col-sm-4"></div>
									<div class="col-sm-4">
										<div style="margin: auto; width: 150px; height: 150px; border: 1px solid #d2d6de; border-radius: 5px; align-items: center; display: flex;">
											<img ng-src="${pageContext.request.contextPath}{{alaCarte.imagePath}}" style="max-width: 150px; max-height: 150px;" />
										</div>
									</div>
									<div class="col-sm-4">
										<div style="width: 150px; height: 150px; align-items: flex-end; display: flex;">
											<div class="input-group">
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
										<font size="4"><b id="alaCarteItemName"></b></font>
									</div>
									<div>
										<font size="2">{{alaCarte.description}}</font>
									</div>
								</div>
								<div style="padding: 15px; padding-bottom: 0px;">
									<div id="alaCarteModifier">
										<div class="row">
											<div class="col-sm-1 form-group"></div>
											<div class="col-sm-8 form-group">
												<div><b><u>Modifiers</u></b></div>
											</div>
											<div class="col-sm-3 form-group"></div>
										</div>
										<div class="row">
											<div class="col-sm-1 form-group"></div>
											<div class="col-sm-8 form-group">
												<div id="selectedAlaCarteModifiers"></div>
											</div>
											<div class="col-sm-3 form-group">
												<button class="btn btn-primary pull-right" ng-click="openMenuItemModal(alaCarte, 0, 'alaCarte')">Select Modifier(s)</button>
											</div>
										</div>
									</div>
									<hr style="padding-bottom: 15px;">
								</div>
								<div style="text-align: center;">
									<button class="btn btn-primary" ng-click="submitOrder()" style="background-color: #00FA9A; border-color: #00FA9A;">Submit</button>
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
												<br>
												<div class="row">
													<div ng-repeat="tierItemDetail in tierItemDetails.jary">
														<div class="col-sm-4 form-group">
															<div style="text-align: center">
																<label ng-click="getModifiers(tierItemDetail)">
																	<div style="margin: auto; width: 150px; height: 150px; border: 1px solid #d2d6de; border-radius: 5px; align-items: center; display: flex;">
																		<img ng-src="${pageContext.request.contextPath}{{tierItemDetail.imagePath}}" style="max-width: 150px; max-height: 150px;" />
																	</div>
																	<div>
																		<input type="radio" name="{{tierItemDetails.tierName}}" value="{{tierItemDetail.id}}" /> <b>{{tierItemDetail.name}}</b>
																	</div>
																</label>
															</div>
														</div>
													</div>
												</div>
											</div>
										</div>
											
										<div class="item">
											<div id="modifier">
												<div class="row" style="font-size: large">
													<div class="col-sm-2">
														<a id="back" href="#itemCarousel" data-slide="prev">
															<i class="fa fa-arrow-left" style="color: black;"></i>
														</a>
													</div>
													<div class="col-sm-8" style="text-align: center">
														<label id="tierItemDetailName"></label>
													</div>
													<div class="col-sm-2"></div>
												</div>
												<div ng-repeat="modifier in modifiers.jary" style="padding-left: 15px; padding-right: 15px;">
													<br>
													<div>
														<label>{{modifier.name}}</label>
													</div>
													<div class="row">
														<div ng-repeat="modifierDetail in modifier.jary">
															<div class="col-sm-3">
																<label>
																	<input type="radio" name="{{modifier.name}}" value="{{modifierDetail.id}}!!{{modifierDetail.backendId}}!!{{modifierDetail.name}}" required /> <b>{{modifierDetail.name}}</b>
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
	</div>
</body>
</html>