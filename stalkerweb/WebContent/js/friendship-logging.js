var dateSeperator = '.';
var _dateFormat = 'dd' + dateSeperator + 'mm' + dateSeperator + 'yyyy';
var myApp = angular.module('app.friendshipActivityCtrl', []);
myApp
		.controller(
				'FriendshipActivityController',
				[
						'$scope',
						function($scope) {
							$scope.loaded = false;
							$scope.userURI = "http://instagram.com/";
							$scope.startDate = null;
							$scope.endDate = null;
							$scope.getReport = function() {
								if(!checkInputValidity($scope.startDate, $scope.endDate))
									return;
								$scope.loading = true;
								getReportFromServlet($scope.userURI, $scope.startDate.getTime(), $scope.endDate.getTime());
							}
							function checkInputValidity(startDate, endDate){
								var msg = null;
								console.debug(startDate, typeof startDate);
								if(startDate == null || endDate == null)
									msg = "Start date and end date fields both must be filled";
								else if(startDate.getTime() >= endDate.getTime())
									msg = "End date must be greater than start date";
								if(msg != null){
									msg += "!";
									var obj = $('#error-alert').text(msg);
									obj.css('visibility','visible');
									return false;
								}
								else return true;
							}
							
							function formatUserURI(uri) {
								var arr = uri.split('/');
								var str = arr[arr.length - 1];
								return str.replace('"', '');
							}
							
							function LineChart(_startDate, _endDate, _data) {

								var startDate = _startDate;
								var endDate = _endDate;

								var canvas = null;
								var data = null;
								var yMaxValue = 0;
								var yMinValue = Number.MAX_VALUE;
								var xMinValue = 0.0;
								var xMaxValue = 100.0;
								var xAxisLineColor = "grey";
								var xAxisTextColor = "black";
								var yAxisTextColor = "grey";
								var axisStrokeWidth = 2;
								var width = 800;
								var height = 500;
								var outlineSize = 40;
								var xScale = null;
								var yScale = null;
								var charts = [];

								init(_data);

								function formatDate(date) {
									var out = date.format(_dateFormat);
									var arr = out.split(dateSeperator);
									return arr[0] + dateSeperator + arr[1];
								}

								function init(_data) {
									data = _data;
									data.forEach(function(d) {
										if (d.Followings > yMaxValue)
											yMaxValue = d.Followings;
									});
									data.forEach(function(d) {
										if (d.Followings < yMinValue)
											yMinValue = d.Followings;
									});
									yMaxValue += yMaxValue / 10;
									xScale = d3.scale
											.linear()
											.domain([ xMinValue, xMaxValue ])
											.range(
													[ outlineSize,
															width + outlineSize ]);
									yScale = d3.scale.linear().domain(
											[ yMinValue, yMaxValue ]).range(
											[ height, 0 ]);
									canvas = d3
											.select("div#friendship-logging")
											.append("svg").attr("width",
													outlineSize + width).attr(
													"height",
													outlineSize + height);
								}

								var createAxisX = function(canvas, ticks) {
									var xAxis = canvas.append("g");
									xAxis.append("line").attr("x1", xScale(0))
											.attr("x2", xScale(100)).attr("y1",
													height).attr("y2", height)
											.attr("stroke", xAxisLineColor)
											.attr("stroke-width",
													axisStrokeWidth);
									var dateTickGap = (endDate - startDate)
											/ ticks;
									var tickGap = (xMaxValue - xMinValue)
											/ ticks;
									for (i = 1; i < ticks + 1; i++) {
										xAxis
												.append("text")
												.attr(
														"x",
														function() {
															if (i == ticks)
																return width;
															else
																return xScale(i
																		* tickGap);
														})
												.attr("y", height + 15)
												.attr("fill", xAxisTextColor)
												.text(
														function() {
															if (i == ticks)
																return "Time";
															else
																return formatDate(new Date(
																		i
																				* dateTickGap
																				+ startDate));
														});
									}
								}

								var createAxisY = function(canvas, ticks) {
									var yAxis = canvas.append("g");
									var tickGap = yMaxValue / ticks;
									for (i = 1; i < ticks + 1; i++) {
										yAxis.append("text").attr("x", 0).attr(
												"y",
												function() {
													if (i == ticks)
														return outlineSize / 2;
													else
														return yScale(i
																* tickGap);
												}).text(function() {
											if (i == ticks)
												return "Number of Followings";
											else
												return Math.round(i * tickGap);
										}).attr("fill", yAxisTextColor);
										if (i != ticks) {
											yAxis
													.append("line")
													.attr("class", "hor-line")
													.attr("x1", outlineSize)
													.attr("x2",
															outlineSize + width)
													.attr("y1",
															yScale(i * tickGap))
													.attr("y2",
															yScale(i * tickGap));
										}
									}
								}

								var drawLineChart = function(canvas, data,
										color) {
									var chart = canvas.append("g");
									var animDurationMS = 300;
									chart
											.selectAll("line")
											.data(data)
											.enter()
											.append("line")
											.attr(
													"x1",
													function(d, i) {
														if (i == 0)
															return 0;
														return xScale(data[i - 1].TimeScale);
													})
											.attr(
													"x2",
													function(d, i) {
														if (i == 0)
															return 0;
														return xScale(data[i - 1].TimeScale);
													})
											.attr(
													"y1",
													function(d, i) {
														if (i == 0)
															return 0;
														return yScale(data[i - 1].Followings);
													})
											.attr(
													"y2",
													function(d, i) {
														if (i == 0)
															return 0;
														return yScale(data[i - 1].Followings);
													})
											.attr("stroke", color)
											.attr("stroke-width", 3)
											.transition()
											.delay(
													function(d, i) {
														return (i - 1)
																* animDurationMS;
													}).duration(animDurationMS)
											.attr("x2", function(d, i) {
												if (i == 0)
													return 0;
												else
													return xScale(d.TimeScale);
											}).attr("y2", function(d, i) {
												if (i == 0)
													return 0;
												else
													return yScale(d.Followings)
											});
									var r = 3;
									var circleAnimDurationMS = 2000;
									chart
											.selectAll("circle")
											.data(data)
											.enter()
											.append("circle")
											.attr("class", "dot-circ")
											.attr("stroke", color)
											.attr("cx", function(d) {
												return xScale(d.TimeScale)
											})
											.attr("cy", function(d) {
												return yScale(d.Followings)
											})
											.attr("r", r)
											.on(
													"mouseover",
													function() {
														d3
																.select(this)
																.attr(
																		"r",
																		function() {
																			if (d3
																					.select(
																							this)
																					.attr(
																							"r") > r * 2)
																				return r * 10;
																			else
																				return r * 2;
																		});
														document.body.style.cursor = 'pointer';
													})
											.on(
													"mouseout",
													function() {
														d3
																.select(this)
																.attr(
																		"r",
																		function() {
																			if (d3
																					.select(
																							this)
																					.attr(
																							"r") > r * 2)
																				return r * 10;
																			else
																				return r;
																		});
														document.body.style.cursor = 'default';
													})
											.on(
													"mousedown",
													function(d, i) {
														canvas
																.selectAll("g")
																.selectAll(
																		"circle")
																.transition()
																.duration(
																		circleAnimDurationMS)
																.attr("r", r);
														var onlyFadeOut = false;
														if (d3.select(this)
																.attr("r") > 2 * r) {
															d3
																	.select(
																			this)
																	.transition()
																	.attr("r",
																			r)
																	.duration(
																			circleAnimDurationMS);
															onlyFadeOut = true;
														} else
															d3
																	.select(
																			this)
																	.transition()
																	.attr(
																			"r",
																			r * 10)
																	.duration(
																			circleAnimDurationMS);

														var selected = null;
														chart
																.selectAll(
																		"text")
																.each(
																		function(
																				d,
																				index) {
																			if (index == i)
																				selected = this;
																		});
														if (onlyFadeOut) {
															d3
																	.select(
																			selected)
																	.transition()
																	.duration(
																			circleAnimDurationMS * 100)
																	.attr(
																			"visibility",
																			"hidden")
																	.attr(
																			"opacity",
																			0);
															return;
														}

														canvas
																.selectAll(
																		"text")
																.filter(
																		function() {
																			return d3
																					.select(
																							this)
																					.attr(
																							"class") == "info"
																		})
																.transition()
																.duration(
																		circleAnimDurationMS * 10)
																.attr(
																		"visibility",
																		function() {
																			return (this == selected) ? "visible"
																					: "hidden";
																		})
																.attr(
																		"opacity",
																		function() {
																			return (this == selected) ? 100
																					: 0;
																		});
													});
									chart
											.selectAll("text")
											.data(data)
											.enter()
											.append("text")
											.attr("class", "info")
											.attr("text-anchor", "middle")
											.attr("x", function(d) {
												return xScale(d.TimeScale)
											})
											.attr("y", function(d) {
												return yScale(d.Followings) + 5
											})
											.text(
													function(d, i) {
														return formatUserURI(JSON
																.stringify(d.UserUri));
													}).attr("visibility",
													"hidden")
											.attr("opacity", 0);
									charts.push(chart);
									d3.select("div.loading").remove();
									$scope.$apply(function() {
										$scope.loaded = true;
									})
									return chart;
								}
								this.createAxes = function(xTicks, yTicks) {
									createAxisX(canvas, xTicks);
									createAxisY(canvas, yTicks);
								}

								this.createLineChart = function(color) {
									drawLineChart(canvas, data, color);
								}

								this.setXAxisLineColor = function(color) {
									xAxisLineColor = color;
								}

								this.setXAxisTextColor = function(color) {
									xAxisTextColor = color;
								}

								this.setYAxisTextColor = function(color) {
									yAxisTextColor = color;
								}

								this.removeChart = function(chart) {
									var chartToBeRemoved;
									if (chart instanceof int)
										chartToBeRemoved = charts[chart];
									else
										chartToBeRemoved = chart;

									var d3Obj = d3.select(chartToBeRemoved);
									if (d3Obj != null)
										d3Obj.remove();
									var index = charts
											.indexOf(chartToBeRemoved);
									if (index > -1)
										chart.splice(index, 1);
								}
							}
							function insertRecentActivities(followings, unfollowings){
								$('#recent-followings .badge').text(followings.length);
								var followingsList = $('#recent-followings');
								followings.forEach(function(d){
									followingsList.append('<li class="list-group-item "><strong>' +  formatUserURI(d.userURI) +'</strong></li>');									
								});
								
								$('#recent-unfollowings .badge').text(unfollowings.length);
								var unfollowingsList = $('#recent-unfollowings');
								unfollowings.forEach(function(d){
									unfollowingsList.append('<li class="list-group-item "><strong>' +  formatUserURI(d.userURI) +'</strong></li>');									
								});
							}
							
							function getReportFromServlet(userUri, startDate, endDate) {
								d3.json("/FriendshipActivityServlet?userURI="
										+ userUri + "&start_date=" + startDate
										+ "&end_date=" + endDate,
										function(data) {
											console.debug(data);
											insertRecentActivities(data.recentFollowings, data.recentUnfollowings)
											var chart = new LineChart(
													startDate, endDate, data.followings);
											chart.createAxes(10, 10);
											chart.createLineChart("#FF9900");
										});
							}
						} ]);
	
       myApp.directive('datetimepicker', function(){
		   	return {
		   		restrict: 'A',
		   		require: 'ngModel',
		   		link: function(scope, element, attrs, ngModelCtrl){
		   			console.debug(scope);
		   			element.datepicker({
		   				format: _dateFormat,
		   			})
		   			.on("changeDate", function(evArg){
		   				var targetName = evArg.target.name;
		   				var date = evArg.date;
		   				date.toString = function(){
		   					return this.format(_dateFormat);
		   				}
		   				if(targetName == "start")
		   					scope.startDate = date;
		   				else if(targetName == "end") scope.endDate = date;
	   					console.debug(scope);
		   				scope.$apply();
		   			});
		   		}
		   	}
		   });
