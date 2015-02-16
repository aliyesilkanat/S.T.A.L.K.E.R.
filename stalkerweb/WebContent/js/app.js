var app = angular.module("app", [ "ui.router", "app.homeCtrl",
		"app.friendshipActivityCtrl" ]);
app.config([ '$stateProvider', '$urlRouterProvider', '$locationProvider',
		function($stateProvider, $urlRouterProvider, $locationProvider) {

			// For any unmatched url, redirect to /state1
			$urlRouterProvider.otherwise("/home");
			//
			// Now set up the states
			$stateProvider.state('home', {
				url : "/home",
				templateUrl : "home.html",
				controller : 'HomeCtrl'
			}).state('about', {
				url : "/about",
				templateUrl : "about.html",
			}).state('contact', {
				url : "/contact",
				templateUrl : "contact.html"
			}).state('friendshipactivitylogging', {
				url : "/friendship-logging",
				templateUrl : "friendship-logging.html",
				controller : 'FriendshipActivityController'

			});
		} ]);