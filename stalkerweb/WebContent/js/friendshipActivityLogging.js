var myApp = angular.module('app.friendshipActivityCtrl', []);

myApp.controller('FriendshipActivityController', [ '$scope', '$http',
		function($scope, $http) {
			var userUri = "http://instagram.com/aliyesilkanat";
			var req = {
				method : 'GET',
				url : '/FriendshipActivityServlet',
				params : {
					userURI : userUri
				}
			}

			$http(req).success(function(data, status, headers, config) {

				console.log(data);
				// this callback will be called asynchronously
				// when the response is available
			}).error(function() {
			});
		} ]);