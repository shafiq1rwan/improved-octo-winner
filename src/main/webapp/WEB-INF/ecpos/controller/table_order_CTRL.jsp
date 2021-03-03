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
					$scope.getRoomTypeList();
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

		$scope.create_new_check = function(customerName,customerPhone) {
			var isHotel = false;
			var tableNo;
			
			if (customerName != null && customerName != "" && customerPhone != null && customerPhone != "") {
				tableNo = $scope.room_id;
				isHotel = true;
			} else {
				tableNo = $scope.table_no;
			}
			var jsonData = JSON.stringify({
				"table_no" : tableNo,
				"name" : customerName,
				"phone" : customerPhone
			});
			
			$http.post("${pageContext.request.contextPath}/rc/check/create/table", jsonData)
			.then(function(response) {
				if (response.data.response_code === "00") {
					if (response.data.roomstatus_id != null) {
						$scope.status_id = response.data.roomstatus_id;
					}
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

			var data;
			if ($scope.table_no != null && $scope.table_no != '') {
				data = "/check/" + "table" + "/" + chk_no + "/" + $scope.table_no;
			} else {
				data = "/check/" + "table" + "/" + chk_no + "/" + $scope.room_id + "/" + $scope.status_id;
			}
			$location.path(data);
		}

		//VernPOS Hotel part
		$scope.getRoomTypeList = function() {
			$http.get("${pageContext.request.contextPath}/rc/configuration/get_roomtype_list")
			.then(function(response) {
				$scope.roomTypeList = response.data.room_type_list;
				
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/signout";
			});
		}

		$scope.get_room_list = function(roomType_id,roomType_name) {
			$scope.roomType_id = roomType_id;

			document.getElementById("roomListModalTitle").innerHTML = "ROOM TYPE : " + roomType_name;
			
			$http.post("${pageContext.request.contextPath}/rc/configuration/get_room_list", roomType_id)
			.then(function(response) {
				$scope.roomList = response.data.room_list;
				$scope.floorList = response.data.floor_list;
				$scope.floorNo = response.data.first_floor_no;
				$scope.get_room_status();
				$("#modal_room_list").modal('show');
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/signout";
			});
		}

		$scope.get_room_status = function() {
			$http.post("${pageContext.request.contextPath}/rc/configuration/get_room_status")
			.then(function(response) {
				$scope.roomStatusList = response.data.roomstatus_list;
				
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/signout";
			});
		}

		$scope.get_room_list_by_status = function(status_id) {
			var data;
			if(status_id != null && status_id != "") {
				data = $scope.roomType_id + "," + status_id;
			} else {
				data = $scope.roomType_id;
			}
			$http.post("${pageContext.request.contextPath}/rc/configuration/get_room_list", data)
			.then(function(response) {
				$scope.roomList = response.data.room_list;
				$scope.floorList = response.data.floor_list;
				
				//$scope.get_room_status();
				//$("#modal_room_list").modal('show');
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/signout";
			});
		}

		$scope.proceed_to_roomCheck = function(room_id,status_id,check_no) {
			$scope.room_id = room_id;
			
			if (status_id == 4) {//available
				$("#modal_user_details").modal('show');
			} else if (status_id == 2) {//out of service
				alert("This room is out of service");
			} else if (status_id == 1 || status_id == 3) {//reserved or checked-in
				$scope.status_id = status_id;
				$scope.redirect_to_check_detail(check_no);
			} else {
				alert("Status ID invalid");
			}
			
		}

		$scope.showKeyboard = function(name,value) {
			$scope.targetField = name;
			$scope.targetValue = value;
			$("#modal_keyboard").modal('show');
		}

		$('#keyboard').click(function() {
	    	$('#editField').focus();
	    });

	    $scope.retrieveData = function() {
		    if($scope.targetField == "Customer Name") {
		    	$scope.customerName = $("#editField").val();
			} else if($scope.targetField == "Customer Phone Number") {
				$scope.customerPhone = $("#editField").val();
			}
		    $scope.targetField = null;
		    $scope.targetValue = null;
		    $("#modal_keyboard").modal('hide');
		}

	    $scope.setFloorNo = function(floor_no) {
			$scope.floorNo = floor_no;
		}
	});
</script>