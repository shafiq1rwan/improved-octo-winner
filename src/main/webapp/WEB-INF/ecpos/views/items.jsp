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
</style>
</head>

<body>
	<div ng-controller="items_CTRL">
		<div ng-init="initiation();">
			<div class="content-wrapper" style="font-size: 0.9em;">
				<section class="content sectioncalibrator" style="padding-right: 15px; padding-left: 15px;">
					<div class="row container-fluid" style="padding-right: 2px; padding-left: 2px;">
						<div class="col-md-12" style="padding-right: 2px; padding-left: 2px;">
							<div class="well" style="background-color: white; margin-bottom: 0px; height: 87vh; padding-top: 0px;">
								<div class="row">
									<div class="col-sm-12" style="text-align: center">
										<h3>Items</h3>
									</div>
								</div>
								<br>
								<table id="datatable" class="table table-bordered table-striped table-hover">
									<thead>
										<tr>
											<th>No</th>
											<th>Item Code</th>
											<th>Item Name</th>
											<th>Action</th>
										</tr>
									</thead>
									<tbody id="tbody2">
									</tbody>

									<tfoot>
									</tfoot>
								</table>
								
								
							</div>
						</div>	
					</div>
				</section>
			</div>
		</div>
	</div>
</body>
</html>