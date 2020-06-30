<script>
	var app = angular.module('secondDisplayApp',[]);

	app.controller("secondDisplay_CTRL", function ($scope,$interval,$http,$element,$sce){
	
	$scope.initOrder = function () {
		
		/* $scope.contentKds = $sce.trustAsHtml("<section class=\"content\"><div class=\"row\" style=\"padding-left: 10px; padding-right: 10px\"><span style=\"float:left;\">Total Amt: RM</span><span style=\"float:right;\">0.00</span><br><span style=\"float:left;\">Amt Received: RM</span><span style=\"float:right;\">0.00</span><br><span style=\"float:left;\">Balance Amt: RM</span><span style=\"float:right;\">0.00</span></div></section>"); */
		$scope.contentKds = $sce.trustAsHtml("<section class=\"content\"><div class=\"row\"><span style=\"0float: left; color:#1e90ff;\">TOTAL BILL&nbsp; </span><span>: RM</span><span style=\"float: right;\">0.00</span><br /><span style=\"0float: left;color:#1e90ff;\">TENDERED&nbsp;&nbsp; </span><span>: RM</span><span style=\"float: right;\">0.00</span><br /><span style=\"0float: left; color:#1e90ff;\">BALANCE&nbsp;&nbsp;&nbsp;&nbsp; </span><span>:  RM</span><span style=\"float: right;\">0.00</span></div></section>");

	}
	
	$scope.wsKdsOrder = function () {
		
		var wsProtocol = window.location.protocol;
		var wsHost = window.location.host;
		var wsURLHeader = "";

		if (wsProtocol.includes("https")) {
			wsURLHeader = "wss://"
		} else {
			wsURLHeader = "ws://"
		}
		wsURLHeader += wsHost + "${pageContext.request.contextPath}/secondDisplaySocket";
			
		var kdsSocket = new WebSocket(wsURLHeader);
		
		kdsSocket.onopen = function(event) {
			console.log("Connection established on SecondDisplay");
		}
		
		kdsSocket.onmessage = function(event) {
			$scope.json = angular.fromJson(event.data);
			var jsonData = JSON.stringify({
				"orderType" : $scope.json.orderType,
				"tableNo" : $scope.json.tableNo,
				"checkNo" : $scope.json.checkNo
			});
			
			/* console.log("orderType: "+$scope.json.orderType);
			console.log("tableNo: "+$scope.json.tableNo);
			console.log("checkNo: "+$scope.json.checkNo); */
			
			$http.post("${pageContext.request.contextPath}/rc/check/retrieve_order_secondDisplay", jsonData)
			.then(function(response) {
				if (response.data.response_code === "00") {
					$scope.contentKds = $sce.trustAsHtml(response.data.html_str);
				} else {
					if (response.data.response_message != null) {
						Swal.fire("Oops...",response.data.response_message,"error");
					} else {
						Swal.fire("Oops...","Error occured","error");
					}
				}
			});
			
			$scope.initOrder();
		}

		kdsSocket.onerror = function(event) {
			console.error("WebSocket error observed:", event);
			Swal.fire("Oops...",event,"error");
		}
				
		kdsSocket.onclose = function(event) {
			console.log("Connection closed");
		}	
	}

    $scope.wsKdsOrder();
	
});
</script>