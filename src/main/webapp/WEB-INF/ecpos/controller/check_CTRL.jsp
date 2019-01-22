<script>
	app.controller('check_CTRL', function($scope, $http, $routeParams, $window, $location, $route) {
		$scope.tableNo = $routeParams.tableNo;
		$scope.checkNo = $routeParams.checkNo;
		
		$scope.checkDetail = {};

		$scope.initiation = function() {
			$scope.getCheckDetails();
		}
		
		$scope.getCheckDetails = function() {
			$http.get("${pageContext.request.contextPath}/rc/check/get_check_detail/" + $scope.tableNo + "/" + $scope.checkNo)
			.then(function(response) {
				$scope.checkDetail = response.data;
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/ecpos";
			});
		}
		
		$scope.allGrandParentItemCheckbox = function () {
			if(allGrandParentItemCheckbox.checked) {
		        $('[name=grandParentItemCheckbox]').each(function() {
		            this.checked = true;                        
		        });
		    } else {
		        $('[name=grandParentItemCheckbox]').each(function() {
		            this.checked = false;                       
		        });
		    }
		}
		
		$scope.grandParentItemCheckbox = function () {
			if ($("[name=grandParentItemCheckbox]:checked").length == $scope.checkDetail.grandParentItemArray.length) {
				allGrandParentItemCheckbox.checked = true;
			} else {
				allGrandParentItemCheckbox.checked = false;
			}
		}
		
		$scope.redirectTableOrder = function() {
			window.location.href = "${pageContext.request.contextPath}/ecpos/#!table_order";
		}
		
		$scope.cancelOrder = function() {
			$scope.checkedValue = [];
			
			$("input[name=grandParentItemCheckbox]:checked").each(function(){
				$scope.checkedValue.push($(this).val());
			});
			
			if ($scope.checkedValue === undefined || $scope.checkedValue == 0) {
				alert("Kindly tick at least an item to proceed");
			} else {
				var jsonData = JSON.stringify({
					"checkDetailIdArray" : $scope.checkedValue
				});
				
				$http.post("${pageContext.request.contextPath}/rc/check/cancelOrder", jsonData)
				.then(function(response) {
					if (response.data.response_code === "00") {
						alert("Order has been cancelled.")
						
						$scope.getCheckDetails();
					} else {
						alert("Error Occured While Remove Order");
						window.location.href = "${pageContext.request.contextPath}/ecpos";
					}
				},
				function(response) {
					alert("Session TIME OUT");
					window.location.href = "${pageContext.request.contextPath}/ecpos";
				});
			}
		}
		
		$scope.redirectPayment = function(type) {
			var data = "/payment/" + $scope.checkNo;
			$location.path(data);
		}
	});
</script>