<html>
<body>

<div ng-controller="Show_checks_CTRL">
		<div ng-repeat="group_list in list_of_group.group_list">
			<div class="col-lg-4 col-md-4 col-sm-4 col-xs-6">
				<a ng-click="">
					<div class="panel panel-default text-center">
						<div class="panel-body center-block"
							style="color: grey; font-weight: bold; font-size: small;">
							{{group_list.groupname}}
						</div>
					</div>
				</a>
			</div>
		</div>
	</div> 
	
	
	<h1>JJJJJJJJJJ</h1>

</body>
</html>