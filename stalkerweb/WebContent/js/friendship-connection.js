var myApp = angular.module("app.friendshipConnectionCtrl", []);

myApp.controller("FriendshipConnectionController", [
		'$scope',
		function($scope) {

			var width = 1800;
			height = 800;

			var userURI = "http://instagram.com/aliyesilkanat"

			var color = d3.scale.category20();

			var force = d3.layout.force().charge(-120).linkDistance(150).size(
					[ width, height ]);

			var svg = d3.select("body").append("svg").attr("width", width)
					.attr("height", height);

			var data;
			var datarec;
			var link;
			var node;

			d3.json("/FriendshipConnectionServlet?userURI=" + userURI,
					function(error, graph) {

						data = graph;
						datarec = JSON.parse(JSON.stringify(data));
						force.nodes(data.nodes).links(data.links).start();

						link = svg.selectAll(".link").data(data.links).enter()
								.append("line").attr("class", "link").style(
										"stroke-width", function(d) {
											return Math.sqrt(d.value);
										});

						node = svg.selectAll(".node").data(data.nodes).enter()
								.append("circle").attr("class", "node").attr(
										"r", 5).style("fill", function(d) {
									return color(d.group);
								}).call(force.drag);

						node.append("title").text(function(d) {
							return d.UserUri;
						});

						force.on("tick", function() {
							link.attr("x1", function(d) {
								return d.source.x;
							}).attr("y1", function(d) {
								return d.source.y;
							}).attr("x2", function(d) {
								return d.target.x;
							}).attr("y2", function(d) {
								return d.target.y;
							});

							node.attr("cx", function(d) {
								return d.x;
							}).attr("cy", function(d) {
								return d.y;
							});
						});

						var optArray = [];
						for (var i = 0; i < data.nodes.length - 1; i++) {
							optArray.push(data.nodes[i].UserUri.substring(21));
						}

						optArray = optArray.sort();

						// $(function() {
						// $("#search").autocomplete({
						// source : optArray
						// });
						// });
					});

			$scope.searchNode = function() {

				// find the node

				var selectedVal = document.getElementById('search').value;
				var node = svg.selectAll(".node");

				if (selectedVal == "none") {
					node.style("stroke", "white").style("stroke-width", "1");
				} else {
					var selected = node.filter(function(d, i) {
						return d.UserUri.substring(21) != selectedVal;
					});
					selected.style("opacity", "0");
					var link = svg.selectAll(".link")
					link.style("opacity", "0");
					d3.selectAll(".node, .link").transition().duration(5000)
							.style("opacity", 1);

				}
			}

			function threshold(thresh) {
				data.links.splice(0, data.links.length);

				for (var i = 0; i < datarec.links.length; i++) {
					if (datarec.links[i].value > thresh) {
						data.links.push(datarec.links[i]);
					}
				}
				restart();
			}

			// Restart the visualisation after any node and link changes

			function restart() {

				link = link.data(data.links);
				link.exit().remove();
				link.enter().insert("line", ".node").attr("class", "link");
				node = node.data(data.nodes);
				node.enter().insert("circle", ".cursor").attr("class", "node")
						.attr("r", 5).call(force.drag);
				force.start();
			}
		} ]);