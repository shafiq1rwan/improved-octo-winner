<script>
	app.controller('take_away_order_CTRL', function($scope, $http, $routeParams, $window, $location, $route) {
		$scope.initiation = function() {
			$scope.create_new_check();
		}
		
		$scope.create_new_check = function() {			
			$http.post("${pageContext.request.contextPath}/rc/check/create")
			.then(function(response) {
				if (response.data.response_code === "00") {
					$scope.redirect_to_check_detail(response.data.check_no);
				} else {
					alert("Error Occured While Create Check");
					window.location.href = "${pageContext.request.contextPath}/ecpos";
				}
			},
			function(response) {
				alert("Check Failed To Create");
			});
		}
		
		$scope.getCheckDetails = function() {
			$http.get("${pageContext.request.contextPath}/rc/check/get_check_detail/" + $scope.checkNo)
			.then(function(response) {
				$scope.checkDetail = response.data;
				
				if ($scope.checkDetail.grandParentItemArray.length == 0 || $scope.checkDetail.grandParentItemArray === undefined) {
					document.getElementById("itemLoop").style.height = "10vh";
				} else {
					document.getElementById("itemLoop").style.maxHeight = "40vh";
					document.getElementById("itemLoop").style.overflowY = "auto";
				}
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