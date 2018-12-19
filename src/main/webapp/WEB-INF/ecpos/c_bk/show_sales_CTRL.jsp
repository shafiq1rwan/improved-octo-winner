<script>
	app.controller('show_sales_CTRL', ['$scope', '$http', '$window', '$routeParams', '$location', function($scope, $http, $window, $routeParams, $location) {
		$scope.fields_TableInfo = {};
		$scope.check_detail = {};
		$scope.create_check = {};
		
		$scope.manager_tablelist = [];
		$scope.check_list = [];
		$scope.table_no = [];
		
		$scope.staff_name = "";
		
		//Get table list
		$http.get("${pageContext.request.contextPath}/memberapi/show_sales/get_table_list")
		.then(function(response) {
			$scope.fields_TableInfo = response.data;
			var table_list = $scope.fields_TableInfo.table_list;

			splitArray(table_list);
		},
		function(response) {
			alert("Session TIME OUT");
			window.location.href = "${pageContext.request.contextPath}/ecpos";
		});

		function splitArray(array_table_list) {
			var i;
			for (i = 0; i < array_table_list.length; i++) {
				var table_number = array_table_list[i].split(",")[0];
				var check_number = array_table_list[i].split(",")[1];

				var result = {
					'table' : table_number,
					'check' : check_number
				};

				$scope.manager_tablelist.push(result);
			}
		}
		
		$scope.display_table_check_no = function(check_no) {
			if (check_no > 0) {
				return '#00FA9A';
			} else {
				return '#000000';
			}
		}

		$scope.display_table_check_no_title_color = function(check_no) {
			if (check_no > 0) {
				return '#696969';
			} else {
				return '#a6a6a6';
			}
		}
		
		//Get check list based on table
		$scope.get_table_checklist = function(table_no) {
			document.getElementById("select_trxtypeModal_tblno").innerHTML = 'TABLE : ' + table_no;
			
			var jsonData = JSON.stringify({
				"table_no" : table_no
			});

			$http.post("${pageContext.request.contextPath}/memberapi/show_sales/get_check_list", jsonData)
			.then(function(response) {
				$scope.check_list = response.data.check_list;
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/ecpos";
			});

			$("#modal_table_check_list").modal('show');
		}
		
		//Redirect to check detail page
		$scope.redirect_to_check_detail = function(chk_no) {
			$("#modal_table_check_list").modal('hide');
			$('.modal-backdrop').remove();

			var data = "/checks/" + chk_no;
			$location.path(data);
		}
		
		//Show the check detail if store balance not perform
		if (typeof (localStorage) !== undefined) {
			var checkNumber = localStorage.getItem("myChkNo");
			if (checkNumber) {
				$location.path('/checks/' + checkNumber).replace();
			}
		}
		
		//Create new check for the selected table
		$scope.create_new_check = function() {
			$http.get("${pageContext.request.contextPath}/memberapi/show_sales/get_staff_name")
			.then(function(response) {
				$scope.staff_name = response.data.staff_name;

				var table_no = document.getElementById("select_trxtypeModal_tblno").innerHTML.slice(8).trim();

				$scope.create_check_data($scope.staff_name, table_no);
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/ecpos";
			});
		}

		//Success
		$scope.create_check_data = function(staff_data, table_data) {
			var jsonData = JSON.stringify({
				"staff_name" : staff_data,
				"table_no" : table_data
			});

			$http.post("${pageContext.request.contextPath}/memberapi/show_sales/create_check", jsonData)
			.then(function(response) {
				$scope.create_check = response.data;

				if ($scope.create_check.response_code === "00") {
					$scope.redirect_to_check_detail($scope.create_check.check_no);
				} else {
					alert("Success created but failed display detail");
					window.location.href = "${pageContext.request.contextPath}/ecpos";
				}
			},
			function(response) {
				alert("Error Occured While Create Record");
				window.location.href = "${pageContext.request.contextPath}/ecpos";
			});
		}

		//Get check details for the selected table
		/* 	$scope.get_check_detail = function(data) {

				console.log("Check");
				console.log(data); 
		
				var jsonData = JSON.stringify({
					"check_no" : data
				});
				
				console.log(jsonData);
				
				

				$http
						.post(
								"${pageContext.request.contextPath}/memberapi/show_sales/manager_get_check_detail",
								jsonData)
						.then(
								function(response) {
																																							
									$("#modal_table_check_list").modal('hide');
									$('.modal-backdrop').remove();
									
									$scope.check_detail = response.data;
									console.log($scope.check_detail);
									
									Show_sales_SVR.setData($scope.check_detail, $scope.keys.firstView);
																				
									redirectToCheckDetail();
																					 																			
								},
								function(response) {
									alert("Session TIME OUT");
									$(location)
											.attr('href',
													'${pageContext.request.contextPath}/member');
								});

			} */

		/*  	$scope.get_check_detail = function(data) {
										
											console.log(data);
											
											$("#modal_table_check_list").modal('hide');
											$('.modal-backdrop').remove();
											
											//$window.location.href = '${pageContext.request.contextPath}/member/#!checks/'+data;
					 					 	
											
											$http
													.get(
															"${pageContext.request.contextPath}/memberapi/show_sales/manager_get_check_detail/"+data
															)
													.then(
															function(response) {
																																																																
																$scope.check_detail = response.data;
																console.log($scope.check_detail);
																
									
						 																			
															},
															function(response) {
																alert("Session TIME OUT");
																$(location)
																		.attr('href',
																				'${pageContext.request.contextPath}/member');
															});

										} */
	} ]);
</script>