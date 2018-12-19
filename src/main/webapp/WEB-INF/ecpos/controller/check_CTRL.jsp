<script>
	app.controller('check_CTRL', function($scope, $http, $routeParams, $window, $location, $route) {
		$scope.tableNo = $routeParams.tableNo;
		$scope.checkNo = $routeParams.checkNo;
		
		$scope.checkDetail = {};

		$scope.initiation = function() {
			$scope.getCheckDetails();
		}
		
		$scope.getCheckDetails = function() {
			$http.get("${pageContext.request.contextPath}/rc/check/getcheckdetail/" + $scope.tableNo + "/" + $scope.checkNo)
			.then(function(response) {
				$scope.checkDetail = response.data;
				
				if ($scope.checkDetail.grandParentItemArray.length == 0 || $scope.checkDetail.grandParentItemArray === undefined) {
					document.getElementById("itemLoop").style.height = "10vh";
				} else {
					document.getElementById("itemLoop").style.maxHeight = "27vh";
					document.getElementById("itemLoop").style.overflowY = "auto";
				}
			},
			function(response) {
				alert("Session TIME OUT");
				window.location.href = "${pageContext.request.contextPath}/ecpos";
			});
		}
		
		$scope.AllGrandParentItemCheckbox = function () {
			if(AllGrandParentItemCheckbox.checked) {
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
				AllGrandParentItemCheckbox.checked = true;
			} else {
				AllGrandParentItemCheckbox.checked = false;
			}
		}
	});
</script>