<!DOCTYPE html>
<html>
<head>
</head>

<body>
	<div ng-controller="check_CTRL">
		<div class="box" style="border-style: none;">
			<div id="menu1">
				<div class="row" style="max-height: 55vh; overflow-y: auto;">
					<div class="panel-body" style="width: max-width">
						<div id='div_category' style="height: 50vh; overflow-y: auto;">
							<div id="group_item_container" ng-show="inside_group_status==0"
								ng-hide="inside_group_status==1">
								<div ng-repeat="group_list in list_of_group.group_list">
									<div class="col-lg-4 col-md-4 col-sm-4 col-xs-6">
										<a ng-click="get_group_item(group_list.groupid)">
											<div class="panel panel-default text-center">
												<div class="panel-body center-block"
													style="color: grey; font-weight: bold; font-size: small; word-wrap: break-word;">
													{{group_list.groupname}}</div>
											</div>
										</a>
									</div>
								</div>
							</div>
							<div id="group_detail_item_container"
								ng-show="inside_group_status==1"
								ng-hide="inside_group_status==0">
								<div class="col-lg-4 col-md-4 col-sm-4 col-xs-6"
									ng-repeat="item in list_of_item.item_list">
									<a
										ng-click="add_item_into_check(checkDetail.chksequence,item.item_id,'S')">
										<div class="panel panel-default text-center">
											<img src="${pageContext.request.contextPath}"
												+ "item.image_path" width="200" height="200" alt="nothing" />
											<div class="panel-body center-block"
												style="color: grey; font-weight: bold; font-size: small; word-wrap: break-word;">{{item.name}}</div>
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
</body>
</html>