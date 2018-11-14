<script>

var preActivate = angular.module("preActivate",["ngRoute"]);
preActivate.config([
	'$routeProvider',
	'$compileProvider',
	function($routeProvider, $compileProvider) {
		$compileProvider
				.imgSrcSanitizationWhitelist(/^\s*(local|http|https|app|tel|ftp|file|blob|content|ms-appx|x-wmapp0|cdvfile):|data:image\//);
		$routeProvider
				.when(
						"/branch_selection",
						{
							templateUrl : "${pageContext.request.contextPath}/member/views/branch_selection"
						});
		
	} ]);

preActivate.controller('Branch_selection_CTRL', function($scope, $http, $location) {
	
	console.log("HHHH");
	
	$scope.branch_list = [];

	
 	$http
	.get(
			"${pageContext.request.contextPath}/member/branches_selection")
	.then(
			function(response) {
		
				if (response.data.response_code == "00") {
					$scope.branch_list = response.data.branchesList;
					console.log($scope.branch_list);
				}
				else if (response.data.response_code == "01"){
					alert("Error Occured While Displaying Branch Selection");
					$(location)
					.attr('href',
							'${pageContext.request.contextPath}/member/');
				}
			},
			function(response) {
				alert("Error Occured While Displaying Branch Selection");
				$(location)
				.attr('href',
						'${pageContext.request.contextPath}/member/');
			}); 
 	
 	$scope.select_branch = function(id){
 		
 	 	$http
 		.post(
 				"${pageContext.request.contextPath}/member/activate_manager",id)
 		.then(
 				function(response) {
 					alert("GGWP");
					$(location)
					.attr('href',
							'${pageContext.request.contextPath}/member/#!sales');
 	/* 				if (response.data.response_code == "00") {
 						alert("Success");
 						$(location)
 						.attr('href',
 								'${pageContext.request.contextPath}/member/');
 					}
 					else if (response.data.response_code == "01" ){
 						alert(response.data.response_message);
 						$(location)
						.attr('href',
								'${pageContext.request.contextPath}/member/');			
 					} */
 				},
 				function(response) {
 					alert("GG");
					$(location)
					.attr('href',
							'${pageContext.request.contextPath}/member/');
 				}); 
 		
 		
 		
 	}

});




</script>