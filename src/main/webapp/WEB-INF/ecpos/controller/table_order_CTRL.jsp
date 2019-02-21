<script>
	app.controller('table_order_CTRL', function($scope, $http, $window, $routeParams, $location) {
		$scope.fields_TableInfo = {};
		$scope.check_detail = {};
		
		$scope.manager_tablelist = [];
		$scope.check_list = [];
		
		$scope.table_no ="";
		
		//Get table list
		$http.get("${pageContext.request.contextPath}/rc/configuration/get_table_list")
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
			$scope.table_no = table_no;
			
			document.getElementById("select_trxtypeModal_tblno").innerHTML = 'TABLE : ' + $scope.table_no;
			
			var jsonData = JSON.stringify({
				"table_no" : $scope.table_no
			});

			$http.post("${pageContext.request.contextPath}/rc/check/get_check_list", jsonData)
			.then(function(response) {
				$scope.check_list = response.data.check_list;
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/ecpos";
			});

			$("#modal_table_check_list").modal('show');
		}

		$scope.create_new_check = function() {
			var jsonData = JSON.stringify({
				"table_no" :  $scope.table_no,
				"order_type" : "table"
			});
			
			$http.post("${pageContext.request.contextPath}/rc/check/create", jsonData)
			.then(function(response) {
				if (response.data.response_code === "00") {
					$scope.redirect_to_check_detail(response.data.check_no);
				} else {
					alert("Error Occured While Create Check");
					window.location.href = "${pageContext.request.contextPath}/ecpos";
				}
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/ecpos";
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