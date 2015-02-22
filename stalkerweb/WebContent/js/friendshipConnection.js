var myApp = angular.module("app.friendshipConnectionCtrl", []);

myApp
		.controller(
				"FriendshipConnectionController",
				[
						'$scope',
						function($scope) {

							var width = 800;
							height = 700;
							$scope.userImage = "https://pbs.twimg.com/profile_images/1550954462/instagramIcon_400x400.png";
							$scope.userURI = "http://instagram.com/"

							var color = d3.scale.category20();

							var force = d3.layout.force().charge(-120)
									.linkDistance(250).size([ width, height ]);
							var svg;
							var data;
							var datarec;
							var link;
							var node;

							$scope.getReport = function() {
								$scope.loading = true;
								d3
										.json(
												"/FriendshipConnectionServlet?userURI="
														+ $scope.userURI,
												function(error, graph) {
													svg = d3
															.select(
																	"div#friendship-connection")
															.append("svg")
															.attr("width",
																	width)
															.attr("height",
																	height);

													data = graph;
													datarec = JSON.parse(JSON
															.stringify(data));
													force.nodes(data.nodes)
															.links(data.links)
															.start();

													link = svg
															.selectAll(".link")
															.data(data.links)
															.enter()
															.append("line")
															.attr("class",
																	"link")
															.style(
																	"stroke-width",
																	function(d) {
																		return Math
																				.sqrt(d.value);
																	});

													node = svg
															.selectAll(".node")
															.data(data.nodes)
															.enter()
															.append("circle")
															.attr("class",
																	"node")
															.attr("r", 7)
															.style(
																	"fill",
																	function(d) {
																		return color(d.group);
																	}).call(
																	force.drag);
													node
															.on(
																	"click",
																	function(d) {
																		$scope.userName = d.UserName;
																		$scope.userImage = d.UserImage;
																		$scope
																				.$apply();
																	})
													node
															.append("title")
															.text(
																	function(d) {
																		return d.UserUri;
																	});

													node.forEach(function(d) {
														d.y = d.depth * 180;
													});

													var text = svg
															.append("g")
															.selectAll("text")
															.data(force.nodes())
															.enter()
															.append("text")
															.attr("x", 8)
															.attr("y", ".31em")
															.text(
																	function(d) {
																		return d.UserName;
																	});

													force
															.on(
																	"tick",
																	function() {
																		link
																				.attr(
																						"x1",
																						function(
																								d) {
																							return d.source.x;
																						})
																				.attr(
																						"y1",
																						function(
																								d) {
																							return d.source.y;
																						})
																				.attr(
																						"x2",
																						function(
																								d) {
																							return d.target.x;
																						})
																				.attr(
																						"y2",
																						function(
																								d) {
																							return d.target.y;
																						});

																		node
																				.attr(
																						"cx",
																						function(
																								d) {
																							return d.x;
																						})
																				.attr(
																						"cy",
																						function(
																								d) {
																							return d.y;
																						});

																		text
																				.attr(
																						"transform",
																						function(
																								d) {
																							return "translate("
																									+ d.x
																									+ ","
																									+ d.y
																									+ ")";
																						});
																	});
													var safety = 0;
													while (force.alpha() > 0.05) { // You'll
														// want
														// to
														// try
														// out
														// different, "small"
														// values
														// for this
														force.tick();
														if (safety++ > 500) {
															break;// Avoids
															// infinite
															// looping
															// in case
															// this
															// solution was a
															// bad idea
														}
													}
													$scope.loaded = true;

													$scope.loading = false;
													$scope.$apply();
												});
							}
						} ]);