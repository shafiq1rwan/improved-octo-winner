<script>
app.controller('Printer_Configuration_CTRL', function($scope, $http, $timeout, $location) {
	
	$scope.portInfo = {};
	
	var successResponseCode = '00';
	var failResponseCode = '01';
	var printerAPIPrefix = '${pageContext.request.contextPath}/printerapi/';
	
	$scope.get_port_list = function(){
	 	$http
		.get(
				printerAPIPrefix + 'config_printer')
		.then(
				function(response) {
					$scope.portInfo = response.data;
					//$scope.portInfo.PortInfoList = $scope.portInfo.PortInfoList[1];
					// if ($scope.portInfo.response_code == successResponseCode) {
						
					// }
					// else if ($scope.portInfo.response_code == failResponseCode){
					// 	perform_error_redirection('Error Occured While Getting Printer Information',
					// 			'member/');			
					// }
				},
				function(response) {
			/* 		alert("Error Occured While Getting Printer Information");
					$(location)
					.attr('href',
							'${pageContext.request.contextPath}/member/'); */
					perform_error_redirection('Error Occured While Getting Printer Information GG',
					'member/');
				}); 
	}
	
	$scope.printer_configuration_info = function(){
		
		if($scope.selectedPort && $scope.selectedPaperSize){
			
	/* 		console.log("my data");
			console.log($scope.selectedPort);
			console.log($scope.selectedPaperSize); */
			
			var portModelName = $scope.portInfo.PortInfoList[$scope.selectedPort].ModelName;
			var portName = $scope.portInfo.PortInfoList[$scope.selectedPort].PortInfo.PortName;
			console.log(portModelName);
			
			var jsonData = JSON.stringify({
				'selectedPortModelName':portModelName,
				'selectedPortName' : portName,
				'selectedPaperSize':$scope.selectedPaperSize	
			});
			
			console.log(jsonData);
			
	 		$http
			.post(
					printerAPIPrefix + 'save_printer_config',jsonData)
			.then(
					function(response) {
						if (response.data.response_code == successResponseCode) {
							alert("Success");
							$(location)
							.attr('href',
									'${pageContext.request.contextPath}/member/#!sales');
							
						}
						else if (response.data.response_code == failResponseCode){
							perform_error_redirection('Error Occured While Saving Printer Information',
									'member/');		
						}
					},
					function(response) {
						perform_error_redirection('Error Occured While Saving Printer Information Error Error',
						'member/');
					});  

		}
	}
	
	$scope.open_cash_drawer = function(){
		$http
		.post(
				printerAPIPrefix + 'open_cash_drawer')
		.then(
				function(response) {
					if (response.data.response_code == successResponseCode) {
						alert("Success Open Drawer");
						$(location)
						.attr('href',
								'${pageContext.request.contextPath}/member/#!sales');
						
					}
					else if (response.data.response_code == failResponseCode){
						perform_error_redirection('Error Occured While Open Cash Drawer',
								'member/');		
					}
				},
				function(response) {
					perform_error_redirection('Error Occured While Open Cash Drawer',
					'member/');
				});  
	}
	
	$scope.print_receipt = function(check){
		
		var jsonData = 0;
		
		$http
		.post(
				printerAPIPrefix + 'print_receipt'. jsonData)
		.then(
				function(response) {
					if (response.data.response_code == successResponseCode) {
						
					}
					else if (response.data.response_code == failResponseCode){
						perform_error_redirection('Error Occured While Open Cash Drawer',
								'member/');		
					}
				},
				function(response) {
					perform_error_redirection('Error Occured While Open Cash Drawer GGG',
					'member/');
				});  

	}
	

	function perform_error_redirection(errorName, redirectPathName){
		alert(errorName);
		$(location)
		.attr('href',
				'${pageContext.request.contextPath}/' + redirectPathName);
	}
	
	
	
	
	
	
	
	
});
</script>