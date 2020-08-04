<script>
	app.controller('table_order_CTRL', function($scope, $http, $window, $routeParams, $location) {
		$scope.fields_TableInfo = {};
		$scope.check_detail = {};
		
		$scope.manager_tablelist = [];
		$scope.check_list = [];
		
		$scope.table_no ="";
		
		$scope.initiation = function() {
			$http.post("${pageContext.request.contextPath}/rc/configuration/session_checking")
			.then(function(response) {
				if (response.data.responseCode == "00") {
					$scope.getTableList();
				} else {
					/* alert("Session TIME OUT"); */
					/* window.location.href = "${pageContext.request.contextPath}/signout"; */
				}
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/signout";
			});
		}
		
		$scope.getTableList = function() {
			$http.get("${pageContext.request.contextPath}/rc/configuration/get_table_list")
			.then(function(response) {
				console.log(response.data);
				$scope.fields_TableInfo = response.data;
				var table_list = $scope.fields_TableInfo.table_list;
	
				splitArray(table_list);
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/signout";
			});
		}

		function splitArray(array_table_list) {
			var i;
			for (i = 0; i < array_table_list.length; i++) {
				var table_number = array_table_list[i].split(",")[0];
				var table_name = array_table_list[i].split(",")[1];
				var total_check = array_table_list[i].split(",")[2];

				var result = {
					'table_number' : table_number,	
					'table_name' : table_name,
					'total_check' : total_check
				};

				$scope.manager_tablelist.push(result);
			}
		}
		
		$scope.display_table_check_no = function(check_no) {
			if (check_no > 0) {
				return '#1EC676';
			} else {
				return '#000000';
			}
		}

		$scope.display_table_check_no_title_color = function(check_no) {
			if (check_no > 0) {
				return '#ffffff';
			} else {
				return '#ffffff';
			}
		}
		
		//Get check list based on table
		$scope.get_table_checklist = function(table_no) {
			$scope.table_no = table_no;
			
			document.getElementById("select_trxtypeModal_tblno").innerHTML = 'TABLE : ' + $scope.table_no;
			
			var jsonData = JSON.stringify({
				"table_no" : $scope.table_no
			});

			$http.post("${pageContext.request.contextPath}/rc/check/get_checks", jsonData)
			.then(function(response) {
				$scope.checks = response.data.checks;

				$("#modal_table_check_list").modal('show');
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/signout";
			});
		}

		$scope.create_new_check = function() {
			var jsonData = JSON.stringify({
				"table_no" :  $scope.table_no,
			});
			
			$http.post("${pageContext.request.contextPath}/rc/check/create/table", jsonData)
			.then(function(response) {
				if (response.data.response_code === "00") {
					$scope.redirect_to_check_detail(response.data.check_no);
				} else {
					if (response.data.response_message != null) {
						alert(response.data.response_message);
					} else {
						alert("Error Occured While Create Check");
					}
				}
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/signout";
			});
		}
				
		//Redirect to check detail page
		$scope.redirect_to_check_detail = function(chk_no) {
			$("#modal_table_check_list").modal('hide');
			$('.modal-backdrop').remove();

			var data = "/check/" + "table" + "/" + chk_no + "/" + $scope.table_no;
			$location.path(data);
		}
	});
</script>