var app = angular.module("app", [ "ui.router", "app.homeCtrl",
		"app.friendshipActivityCtrl", "app.friendshipConnectionCtrl",
		"app.loginCtrl" ]);
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
			}).state('friendshipConnectionGraph', {
				url : "/friendship-connection",
				templateUrl : "friendship-connection.html",
				controller : "FriendshipConnectionController"

			}).state('login', {
				url : "/login",
				templateUrl : "login.html",
				controller : "LoginController"

			});
		} ]);