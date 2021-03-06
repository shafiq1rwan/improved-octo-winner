<script>
	var app = angular.module('kdsApp',[]);

	app.controller("kds_CTRL", function ($scope,$interval,$http,$element,$sce){
	
	$scope.initOrder = function () {
		
		$http.post("${pageContext.request.contextPath}/rc/check/retrieve_order_kds")
		.then(function(response) {
			if (response.data.response_code == "00") {
				$scope.contentKds = $sce.trustAsHtml(response.data.html_str);  
			} else {
				/* alert(response.data.response_message); */
				Swal.fire("Oops...",response.data.response_message,"error");
			}
		},
		function(response) {
			/* alert("Session TIME OUT"); */
			/* window.location.href = "${pageContext.request.contextPath}/signout"; */
			Swal.fire({
				title: 'Oops...',
				text: "Session Timeout",
				icon: 'error',
				showCancelButton: false,
				confirmButtonColor: '#3085d6',
				cancelButtonColor: '#d33',
				confirmButtonText: 'OK'
				},function(isConfirm){
				if (isConfirm) {
				window.location.href = "${pageContext.request.contextPath}/signout";
				}
			});
		});
	}
	
	$scope.wsKdsOrder = function (jsonData) {
		
		var wsProtocol = window.location.protocol;
		var wsHost = window.location.host;
		var wsURLHeader = "";

		if (wsProtocol.includes("https")) {
			wsURLHeader = "wss://"
		} else {
			wsURLHeader = "ws://"
		}
		wsURLHeader += wsHost + "${pageContext.request.contextPath}/kdsSocket";
			
		var kdsSocket = new WebSocket(wsURLHeader);
		
		kdsSocket.onopen = function(event) {
			console.log("Connection established");
		}
		
		kdsSocket.onmessage = function(event) {
			$scope.json = angular.fromJson(event.data);
			var jsonData = JSON.stringify({
				"orderType" : $scope.json.order_type,
				"tableNo" : $scope.json.table_no,
				"checkNo" : $scope.json.check_no,
				"orderDateTime" : $scope.json.order_date_time,
				"checkNoToday" : $scope.json.check_no_today,
				"kdsStatusId" : 2,
				"action" : $scope.json.init_action
			});

			$http.post("${pageContext.request.contextPath}/rc/check/check_kds_order_by_check", jsonData)
			.then(function(response) {
				if (response.data.response_code == "00") {
					if ($scope.json.init_action == "cancel_order") {
						Swal.fire({
							  position: 'top-end',
							  icon: 'info',
							  title: 'Cancel Order Received',
							  showConfirmButton: false,
							  timer: 1200
						});
					}else if ($scope.json.init_action == "split_order") {
						Swal.fire({
							  position: 'top-end',
							  icon: 'info',
							  title: 'Split Order Received',
							  showConfirmButton: false,
							  timer: 1200
						});
					}else if ($scope.json.init_action == "add_order") {
						Swal.fire({
							  position: 'top-end',
							  icon: 'info',
							  title: 'New Order Received',
							  showConfirmButton: false,
							  timer: 1200
						});
					}
					
					$scope.initOrder();
					
				} else {
					/* alert(response.data.response_message); */
					/*Swal.fire("Invalid order",response.data.response_message,"error");*/
				}
			},
			function(response) {
				/* alert("Session TIME OUT"); */
				/* window.location.href = "${pageContext.request.contextPath}/signout"; */
				Swal.fire({
					title: 'Oops...',
					text: "Session Timeout",
					icon: 'error',
					showCancelButton: false,
					confirmButtonColor: '#3085d6',
					cancelButtonColor: '#d33',
					confirmButtonText: 'OK'
					},function(isConfirm){
					if (isConfirm) {
					window.location.href = "${pageContext.request.contextPath}/signout";
					}
				});
			});
		}

		kdsSocket.onerror = function(event) {
			console.error("WebSocket error observed:", event);
			/* alert(event); */
			Swal.fire("Oops...",event,"error");
		}
				
		kdsSocket.onclose = function(event) {
			console.log("Connection closed");
		}	
	}
    
	$scope.getKdsSetting = function() {
		$scope.kdsConfig = {};
		$http.get("${pageContext.request.contextPath}/rc/configuration/get_kds_config")
		.then(function(response) {
			$scope.kdsConfig.id = response.data.id;
			$scope.kdsConfig.timeWarning = response.data.time_warning;
			$scope.kdsConfig.timeLate = response.data.time_late;
			$('#kds_setting').modal("show");
		},
		function(response) {
			/* alert("Session TIME OUT"); */
			/* window.location.href = "${pageContext.request.contextPath}/signout"; */
			Swal.fire({
				title: 'Oops...',
				text: "Session Timeout",
				icon: 'error',
				showCancelButton: false,
				confirmButtonColor: '#3085d6',
				cancelButtonColor: '#d33',
				confirmButtonText: 'OK'
				},function(isConfirm){
				if (isConfirm) {
				window.location.href = "${pageContext.request.contextPath}/signout";
				}
			});
		});
	}
	
	$scope.closeOrder = function (checkId,orderDateTime){
		
		Swal.fire({
			title: 'Are you sure?',
			text: "You won't be able to revert this!",
			icon: 'warning',
			showCancelButton: true,
			confirmButtonColor: '#3085d6',
			cancelButtonColor: '#d33',
			confirmButtonText: 'Yes'
			}).then((result) => {
			if (result.value) {
			//Add condition here
				var jsonData = JSON.stringify({
					"check_no" : checkId,
					"kds_date_time" : orderDateTime
				});
				
				$http.post("${pageContext.request.contextPath}/rc/check/close_kds_order", jsonData)
				.then(function(response) {
					/* alert(response.data.response_message); */
					Swal.fire("Success",response.data.response_message,"success");
					$scope.initOrder();
				},
				function(response) {
					/* alert("Session TIME OUT"); */
					/* window.location.href = "${pageContext.request.contextPath}/signout"; */
					Swal.fire({
						title: 'Oops...',
						text: "Session Timeout",
						icon: 'error',
						showCancelButton: false,
						confirmButtonColor: '#3085d6',
						cancelButtonColor: '#d33',
						confirmButtonText: 'OK'
						},function(isConfirm){
						if (isConfirm) {
						window.location.href = "${pageContext.request.contextPath}/signout";
						}
					});
				});
		}});

		// var confirmation = confirm("Confirm to close all order?");
		// if (confirmation == true) {
		// 	var jsonData = JSON.stringify({
		// 		"check_no" : checkId,
		// 		"kds_date_time" : orderDateTime
		// 	});
			
		// 	$http.post("${pageContext.request.contextPath}/rc/check/close_kds_order", jsonData)
		// 	.then(function(response) {
		// 		/* alert(response.data.response_message); */
		// 		Swal.fire("Success",response.data.response_message,"success");
		// 		$scope.initOrder();
		// 	},
		// 	function(response) {
		// 		/* alert("Session TIME OUT"); */
		// 		/* window.location.href = "${pageContext.request.contextPath}/signout"; */
		// 		Swal.fire({
		// 			title: 'Oops...',
		// 			text: "Session Timeout",
		// 			icon: 'error',
		// 			showCancelButton: false,
		// 			confirmButtonColor: '#3085d6',
		// 			cancelButtonColor: '#d33',
		// 			confirmButtonText: 'OK'
		// 			},function(isConfirm){
		// 			if (isConfirm) {
		// 			window.location.href = "${pageContext.request.contextPath}/signout";
		// 			}
		// 		});
		// 	});
		// }
	}
	
    $scope.saveKdsSetting = function() {
		var jsonData = JSON.stringify({
			"id" : $scope.kdsConfig.id,
			"timeWarning" : $scope.kdsConfig.timeWarning,
			"timeLate" : $scope.kdsConfig.timeLate
		});
		
		$http.post("${pageContext.request.contextPath}/rc/configuration/save_kds_setting", jsonData)
		.then(function(response) {
			/* alert(response.data.response_message); */
			Swal.fire("Success",response.data.response_message,"success");
			if (response.data.response_code === "00") {
				$("#kds_setting").modal("hide");
				$scope.initOrder();
			}
		},
		function(response) {
			/* alert("Session TIME OUT"); */
			/* window.location.href = "${pageContext.request.contextPath}/signout"; */
			Swal.fire({
				title: 'Oops...',
				text: "Session Timeout",
				icon: 'error',
				showCancelButton: false,
				confirmButtonColor: '#3085d6',
				cancelButtonColor: '#d33',
				confirmButtonText: 'OK'
				},function(isConfirm){
				if (isConfirm) {
				window.location.href = "${pageContext.request.contextPath}/signout";
				}
			});
		});
	}
    
    $scope.updateSendItem = function (checkDetailId,status){
		var jsonData = JSON.stringify({
			"check_detail_id" : checkDetailId,
			"is_check" : status
		});
		
		$http.post("${pageContext.request.contextPath}/rc/check/update_kds_sent_item", jsonData)
		.then(function(response) {
			if (response.data.response_code != "00") {
				/* alert(response.data.response_message); */
				Swal.fire("Oops...",response.data.response_message,"error");
			}
			$scope.initOrder();
		},
		function(response) {
			/* alert("Session TIME OUT"); */
			/* window.location.href = "${pageContext.request.contextPath}/signout"; */
			Swal.fire({
				title: 'Oops...',
				text: "Session Timeout",
				icon: 'error',
				showCancelButton: false,
				confirmButtonColor: '#3085d6',
				cancelButtonColor: '#d33',
				confirmButtonText: 'OK'
				},function(isConfirm){
				if (isConfirm) {
				window.location.href = "${pageContext.request.contextPath}/signout";
				}
			});
		});
	}
    
    $scope.wsKdsOrder();
	
	});
</script>


<script>
	function closeOrder(id, checkId, orderDate) {
		var scope = angular.element(document.getElementById(id)).scope();
		scope.$apply(function() {
			scope.closeOrder(checkId, orderDate);
		});
	}

	function checkItem(checkBoxId, checkDetailId) {
		var scope = angular.element(document.getElementById(checkBoxId))
				.scope();
		scope.$apply(function() {
			if (document.getElementById(checkBoxId).checked) {
				scope.updateSendItem(checkDetailId,'1');
			} else {
				scope.updateSendItem(checkDetailId,'0');
			}
		});
	}
	
</script>

