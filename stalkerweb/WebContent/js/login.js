var myApp = angular.module('app.loginCtrl', []);
myApp.controller('LoginController', [ '$scope', '$http',
		function($scope, $http) {
			function logIn() {
				$http({
					url : "/LoginServlet",
					method : "GET",
					params : {
						email : $scope.username,
						password : $scope.password
					}
				}).success(function(data) {
					console.log(data);
				});
			}
			$scope.login = function() {
				console.log($scope.username + " " + $scope.password);
				logIn();
			}
		} ]);