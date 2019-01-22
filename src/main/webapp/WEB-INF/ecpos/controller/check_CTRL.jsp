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
	});
</script>