<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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

#home{
  text-align: center;
}

</style>

<body>

<div ng-controller="Printer_Configuration_CTRL" ng-init="get_port_list()">
	<div class="content-wrapper" style="font-size: 0.9em;">
		<section class="content sectioncalibrator">
		
			<div class="jumbotron" id="home">
			
					<h2>Printer Configuration</h2>
				<form ng-submit="printer_configuration_info()" method="post" 
				 id="configure_printer_form" name="configure_printer_form">

						<div class="form-group">
							<label for="drop_down_list_printer_model">Printer Model</label> 
					 		<select
								class="form-control" id="drop_down_list_printer_model" ng-model="selectedPort" required>
								<option value="" selected>-- Select --</option>
								<option ng-repeat="info in portInfo.PortInfoList" value="{{$index}}">{{info.PortInfo.PortName}}</option>
							</select>
						</div>

						<div class="form-group">
							<label for="drop_down_list_paper_size">Paper Size Selection</label> 
							<select
								class="form-control" id="drop_down_list_paper_size" ng-model="selectedPaperSize"  required>
								<option value="" selected>-- Select --</option>
								<option ng-repeat="info in portInfo.PaperSizeList" value="{{$index}}">{{info}}</option>
							</select>
						</div>

						<button class="btn btn-default" ng-click="">Test Calling</button>
						<button class="btn btn-info" ng-click="open_cash_drawer()">Open Drawer</button>
					<button class="btn btn-primary" type="submit">Save</button>							
				</form>
			</div>
		</section>
	</div>
</div>

</body>
</html>