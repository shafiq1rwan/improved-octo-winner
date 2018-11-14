<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<html>
<style>
.sectioncalibrator {
	height: 85vh;
	overflow-y: scroll;
}

@media only screen and (max-width:600px) {
	.sectioncalibrator {
		height: 77vh;
		overflow-y: scroll;
	}
}
</style>

<body>

	<div ng-controller="Connection_QR_CTRL" ng-init="getQRConnection()">
		<div class="content-wrapper" style="font-size: 0.9em;">
			<section class="content-header">
				<h1>QR Connection</h1>
				<ol class="breadcrumb">
					<li>MAIN NAVIGATION</li>
					<li>QR Connection</li>
				</ol>
			</section>
			<section class="content sectioncalibrator">
				<div class="row">
					<div class="col-md-12 col-sm-12 col-xs-12">
						<div class="box">
							<div class="box-header">
								<div>
									<h3 class="box-title">SCAN TO CONNECT</h3>
								</div>
							</div>

							<div class="box-body">
								<div class="row">

									<div class="text-center">
										<input id="QRtext" type="hidden" value="IP" />
										<div class="row">
											<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4"></div>
											<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
												<div class="container-fluid">
													<div id="qrcode" align="center"
														style="width: 150px; height: 150px;"></div>
												</div>
											</div>
											<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4"></div>
										</div>
									</div>

								</div>
							</div>

						</div>
					</div>
				</div>
			</section>

		</div>
	</div>


</body>
</html>