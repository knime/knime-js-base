(pie_chart_namespace = function() {

	var pie = {};
	var _representation, _value;
	var layoutContainer;
	var MIN_HEIGHT = 200, MIN_WIDTH = 300;
	var chart, svg;
	var knimeTable;

	pie.init = function(representation, value) {
		_representation = representation;
		_value = value;

		drawChart(false);
	}

	function drawChart(redraw) {
		// Parse the options

		var optMethod = _representation.options["aggr"];
		var optCat = _representation.options["cat"];
		var optFreqCol = _value.options["freq"];
		var optTitle = _value.options["title"];
		var optSubtitle = _value.options["subtitle"];

		var showLabels = _value.options["showLabels"];
		var labelThreshold = _representation.options["labelThreshold"];
		var labelType = _value.options["labelType"].toLowerCase();
		var customColors = _representation.options["customColors"];
		
		var optDonutChart = _value.options["togglePie"];
		var holeSize = _value.options["holeSize"];
		var optInsideTitle = _value.options["insideTitle"];

		var showLegend = _representation.options["legend"];

		var optFullscreen = _representation.options["svg"]["fullscreen"] && _representation.runningInView;
		var optWidth = _representation.options["svg"]["width"]
		var optHeight = _representation.options["svg"]["height"]

		/*
		 * Setup interactive controls
		 */

		d3.selectAll("html, body").style("width", "100%").style("height",
				"100%").style("margin", "0").style("padding", "0");

		var body = d3.select("body");

		var width = optWidth + "px";
		var height = optHeight + "px";
		if (optFullscreen) {
			width = height = "100%";
		}
		
		if (redraw) {
			d3.select("svg").remove();
			div = d3.select("#svgContainer");
		} else {

			layoutContainer = body.append("div").attr("id", "layoutContainer")
				.style("width", width).style("height", height).style(
						"min-width", MIN_WIDTH + "px");

			var controlHeight;
			if (_representation.options.enableViewControls) {
			var controlsContainer = body.append("div").style({
				bottom : "0px",
				width : "100%",
				padding : "5px",
				"padding-left" : "60px",
				"border-top" : "1px solid black",
				"background-color" : "white",
				"box-sizing" : "border-box"
			}).attr("id", "controlContainer");

			createControls(controlsContainer);
			controlHeight = controlsContainer.node().getBoundingClientRect().height;
			layoutContainer.style("min-height", (MIN_HEIGHT + controlHeight)
					+ "px");
			if (optFullscreen) {
				layoutContainer.style("height", "calc(100% - " + controlHeight
						+ "px)");
			}
		} else {
			controlHeight = 0;
		}
		
		var div = layoutContainer.append("div")
			.attr("id", "svgContainer")
			.style("min-width", MIN_WIDTH + "px")
			.style("min-height", "calc(" + MIN_HEIGHT + "px - " + controlHeight + "px")
			.style("box-sizing", "border-box")
			.style("overflow", "hidden")
			.style("margin", "0");
		}

		/*
		 * Process data
		 */
		knimeTable = new kt();
		// Add the data from the input port to the knimeTable.
		var port0dataTable = _representation.inObjects[0];
		knimeTable.setDataTable(port0dataTable);

		var categories = knimeTable.getColumn(optCat);
		
		// Default color scale
		var colorScale = [];
		if (!customColors) {
			colorScale = d3.scale.category10();
			if (categories.length > 10) {
				colorScale = d3.scale.category20();
			}
		}

		var valCol;
		if (optMethod == "Occurence\u00A0Count") {
			valCol = knimeTable.getColumn(1);
		} else {
			valCol = knimeTable.getColumn(optFreqCol);
		}

		var plot_data = [];

		if (valCol.length > 0) {
			numDataPoints = valCol.length;

			for (var i = 0; i < numDataPoints; i++) {
				var plot_stream = {
					"label" : categories[i],
					"value" : valCol[i]
				};
				plot_data.push(plot_stream);
				
				if (customColors) {
					var color = knimeTable.getRowColors()[i];
					if (!color) {
						color = "#7C7C7C";
					}
					colorScale.push(color);
				}
			}

		}

		// Create the SVG object
		var svg1 = document
				.createElementNS('http://www.w3.org/2000/svg', 'svg');
		div[0][0].appendChild(svg1);

		svg = d3.select("svg");
		svg.style("font-family", "sans-serif");

		if (!optFullscreen) {
			if (optWidth > 0) {
				div.style("width", optWidth + "px")
				svg.attr("width", optWidth);
			}
			if (optHeight > 0) {
				svg.attr("height", optHeight);
				div.style("height", optHeight + "px");
			}
		} else {
			// Set full screen height/width
			div.style("width", "100%");
			div.style("height", "100%");

			svg.attr("width", "100%");
			svg.attr("height", "100%");
		}

		var colorRange = customColors ? colorScale : colorScale.range();
		
		// Pie chart
		nv.addGraph(function() {
			chart = nv.models.pieChart()
				.x(function(d) { return d.label })
				.y(function(d) { return d.value })
				.color(colorRange)
				.duration(300)
				.showLegend(showLegend)
				.showLabels(showLabels)
				.labelThreshold(labelThreshold) 
				.labelType(labelType); // "key", "value" or "percent"
			
			chart.width(optFullscreen ? "100%" : optWidth);
			chart.height(optFullscreen ? "100%" : optHeight);
			chart.margin({top: 0, bottom: 0, left: 20, right: 0});

			// TODO: Add a mechanism to remember the categories that are
			// switched on.

			chart.donut(optDonutChart);
			chart.donutRatio(holeSize);
			if (optInsideTitle) {
				chart.title(optInsideTitle);
			}
			updateTitles(false);

			svg.datum(plot_data).transition().duration(300).call(chart);
			nv.utils.windowResize(chart.update);

			return chart;
		});
	}
	
	function updateData(updateChart) {
		var categories = knimeTable.getColumn(_representation.options["cat"]);
		var valCol;
		if (_representation.options["aggr"] == "Occurence\u00A0Count") {
			valCol = knimeTable.getColumn(1);
		} else {
			valCol = knimeTable.getColumn(_value.options["freq"]);
		}

		var plot_data = [];

		if (valCol.length > 0) {
			numDataPoints = valCol.length;

			for (var i = 0; i < numDataPoints; i++) {
				var plot_stream = {
					"label" : categories[i],
					"value" : valCol[i]
				};
				plot_data.push(plot_stream);
			}
		}
		svg.datum(plot_data);
		if (updateChart) {
			chart.update();
		}
	}
	
	function updateTitles(updateChart) {
		if (chart) {
			var curTitle = d3.select("#title");
			var curSubtitle = d3.select("#subtitle");
			var chartNeedsUpdating = curTitle.empty() != !(_value.options.title) 
				|| curSubtitle.empty() != !(_value.options.subtitle);
			if (!_value.options.title) {
				curTitle.remove();
			}
			if (_value.options.title) {
				if (curTitle.empty()) {
					svg.append("text")
						.attr("x", 20)             
						.attr("y", 30)
						.attr("font-size", 24)
						.attr("id", "title")
						.text(_value.options.title);
				} else {
					curTitle.text(_value.options.title);
				}
			}
			if (!_value.options.subtitle) {
				curSubtitle.remove();
			} 
			if (_value.options.subtitle) {
				if (curSubtitle.empty()) {
					svg.append("text")
						.attr("x", 20)             
						.attr("y", _value.options.title ? 46 : 20)
						.attr("font-size", 12)
						.attr("id", "subtitle")
						.text(_value.options.subtitle);
				} else {
					curSubtitle.text(_value.options.subtitle)
					.attr("y", _value.options.title ? 46 : 20);
				}
			}
			
			var topMargin = 10;
			topMargin += _value.options.title ? 10 : 0;
			topMargin += _value.options.subtitle ? 8 : 0;
			chart.legend.margin({top: topMargin, bottom: topMargin});
			chart.margin({top: topMargin, bottom: topMargin});
			if (updateChart && chartNeedsUpdating) {
				chart.update();
			}
		}
	}

	function createControls(controlsContainer) {
		
		var titleEdit = _representation.options.enableTitleEdit;
		var subtitleEdit = _representation.options.enableSubtitleEdit;
		var donutToggle = _representation.options.enableDonutToggle;
		var holeEdit = _representation.options.enableHoleEdit;
		var insideTitleEdit = _representation.options.enableInsideTitleEdit;
		var colChooser = _representation.options.enableColumnChooser;
		var labelEdit = _representation.options.enableLabelEdit;
		
		if (_representation.options.enableViewControls) {
			
			var controlTable = controlsContainer.append("table")
    			.attr("id", "pieControls")
    			.style("padding", "10px")
    			.style("margin", "0 auto")
    			.style("box-sizing", "border-box")
    			.style("font-family", "sans-serif")
    			.style("font-size", "12px")
    			.style("border-spacing", 0)
    			.style("border-collapse", "collapse");
			
			if (titleEdit || subtitleEdit) {
				var titleEditContainer = controlTable.append("tr");
		    	if (titleEdit) {
		    		titleEditContainer.append("td").append("label").attr("for", "chartTitleText").text("Chart Title:").style("margin", "0 5px");
		    		var chartTitleText = titleEditContainer.append("td").append("input")
	    				.attr("type", "text")
	    				.attr("id", "chartTitleText")
	    				.attr("name", "chartTitleText")
	    				.attr("value", _value.options.title)
	    				.style("font-family", "sans-serif")
	    				.style("font-size", "12px")
	    				.style("width", "150px")
	    				.style("margin-right", "15px")
	    				.on("keyup", function() {
	    					if (_value.options.title != this.value) {
	    						_value.options.title = this.value;
	    						updateTitles(true);
	    					}
	    			});
		    	}
		    	if (subtitleEdit) {
		    		titleEditContainer.append("td").append("label").attr("for", "chartSubtitleText").text("Chart Subtitle:").style("margin", "0 5px");
		    		var chartSubtitleText = titleEditContainer.append("td").append("input")
	    				.attr("type", "text")
	    				.attr("id", "chartSubtitleText")
	    				.attr("name", "chartSubtitleText")
	    				.attr("value", _value.options.subtitle)
	    				.style("font-family", "sans-serif")
	    				.style("font-size", "12px")
	    				.style("width", "150px")
	    				.style("margin-right", "15px")
	    				.on("keyup", function() {
	    					if (_value.options.subtitle != this.value) {
	    						_value.options.subtitle = this.value;
	    						updateTitles(true);
	    					}
	    			});
		    	}
			}
			
			if (donutToggle || holeEdit) {
				var donutEditContainer = controlTable.append("tr");
		    	if (donutToggle) {
		    		donutEditContainer.append("td").append("label").attr("for", "donutCheckbox").text("Render donut chart:").style("margin", "0 5px");
		    		var donutCheckbox = donutEditContainer.append("td").append("input")
	    				.attr("type", "checkbox")
	    				.attr("id", "donutCheckbox")
	    				.style("margin-right", "15px")
	    				.property("checked", _value.options.togglePie)
	    				.on("change", function() {
	    					if (_value.options.togglePie != this.checked) {
	    						_value.options.togglePie = this.checked;
	    						chart.donut(this.checked);
	    						d3.selectAll("#insideTitle, #donutHole").property("disabled", !_value.options.togglePie);
	    						chart.update();
	    					}
	    				});
		    	}
		    	if (holeEdit) {
		    		donutEditContainer.append("td").append("label").attr("for", "donutHole").text("Donut hole ratio:").style("margin", "0 5px");
		    		var holeEdit = donutEditContainer.append("td").append("input")
		    			.attr("type", "number")
		    			.attr("id", "donutHole")
		    			.attr("min", 0).attr("max", 1).attr("step", 0.1)
		    			.attr("value", _value.options.holeSize)
		    			.style("width", "150px")
		    			.style("margin-right", "15px")
		    			.property("disabled", !_value.options.togglePie)
		    			.on("change", function() {
		    				if (this.value < 0) {
		    					this.value = 0;
		    				} else if (this.value > 1) {
		    					this.value = 1;
		    				}
		    				chart.donutRatio(this.value);
		    				chart.update();
		    			})
		    			.on("keyup", function() {
		    				if (this.value < 0) {
		    					this.value = 0;
		    				} else if (this.value > 1) {
		    					this.value = 1;
		    				}
		    				chart.donutRatio(this.value);
		    				chart.update();
		    			});
		    	}
			}
			
			if (insideTitleEdit || colChooser) {
				var freqColContainer = controlTable.append("tr");
		    	if (insideTitleEdit) {
		    		freqColContainer.append("td").append("label").attr("for", "insideTitle").text("Title inside:").style("margin", "0 5px");
		    		var chartSubtitleText = freqColContainer.append("td").append("input")
	    				.attr("type", "text")
	    				.attr("id", "insideTitle")
	    				.attr("name", "insideTitle")
	    				.attr("value", _value.options.insideTitle)
	    				.style("font-family", "sans-serif")
	    				.style("font-size", "12px")
	    				.style("width", "150px")
	    				.style("margin-right", "15px")
	    				.property("disabled", !_value.options.togglePie)
	    				.on("keyup", function() {
	    					if (_value.options.insideTitle != this.value) {
	    						_value.options.insideTitle = this.value;
	    						chart.title(this.value);
	    						chart.update();
	    					}
	    			});
		    	}
		    	if (colChooser) {
		    		freqColContainer.append("td").append("label").attr("for", "freq").text("Column:").style("margin", "0 5px");
		    		var colSelect = freqColContainer.append("td").append("select")
		    			.attr("id", "freq")
		    			.style("font-family", "sans-serif")
	    				.style("font-size", "12px")
	    				.style("width", "150px")
	    				.style("margin-right", "15px");
		    		var COLUMNS = _representation.inObjects[0].spec.colNames;
					var COLTYPES = _representation.inObjects[0].spec.colTypes;
					for (var i = 0; i < COLUMNS.length; i++) {
						if (COLTYPES[i] == "number") {
							var interp = COLUMNS[i];
							var o = colSelect.append("option").text(interp).attr(
									"value", interp);
							if (interp === _value.options.freq) {
								o.property("selected", true);
							}
						}
					}
					colSelect.on("change", function() {
						_value.options.freq = colSelect.property("value");
						updateData(true);
					});
		    	}
			}
			
			if (labelEdit) {
				var labelEditContainer = controlTable.append("tr");
				labelEditContainer.append("td").append("label").attr("for", "labelCheckbox").text("Show labels:").style("margin", "0 5px");
	    		var donutCheckbox = labelEditContainer.append("td").append("input")
	   				.attr("type", "checkbox")
	   				.attr("id", "labelCheckbox")
	   				.style("margin-right", "15px")
	    			.property("checked", _value.options.showLabels)
	    			.on("change", function() {
	    				if (_value.options.showLabels != this.checked) {
    						_value.options.showLabels = this.checked;
	   						chart.showLabels(this.checked);
	   						d3.selectAll("#labelType input").property("disabled", !_value.options.showLabels);
	   						//workaround for nvd3 bug, remove labels manually
	   						if (!this.checked) {
	   							d3.selectAll(".nv-pieLabels *").remove();
	   						}
	   						chart.update();
	   					}
	    			});
	    		
	    		labelEditContainer.append("td").append("label")/*.attr("for", "labelType")*/.text("Label type:").style("margin", "0 5px");
	    		var labelTypeBox = labelEditContainer.append("td").attr("id", "labelType");
	    		var type = _value.options.labelType.toLowerCase();
	    		labelTypeBox.append("input").attr("type", "radio").attr("id", "typeKey").attr("name", "labelRadio").attr("value", "key").property("checked", type == "key").property("disabled", !_value.options.showLabels);
	    		labelTypeBox.append("label").attr("for", "typeKey").text("Key").style("margin-right", "5px");
	    		labelTypeBox.append("input").attr("type", "radio").attr("id", "typeValue").attr("name", "labelRadio").attr("value", "value").property("checked", type == "value").property("disabled", !_value.options.showLabels);
	    		labelTypeBox.append("label").attr("for", "typeValue").text("Value").style("margin-right", "5px");
	    		labelTypeBox.append("input").attr("type", "radio").attr("id", "typePercent").attr("name", "labelRadio").attr("value", "percent").property("checked", type == "percent").property("disabled", !_value.options.showLabels);
	    		labelTypeBox.append("label").attr("for", "typePercent").text("Percent").style("margin-right", "15px");
	    		d3.selectAll("#labelType input").on("click", function() {
	    			var newValue = d3.select("#labelType input[name=labelRadio]:checked").attr("value");
	    			if (newValue != _value.options.labelType) {
	    				_value.options.labelType = newValue;
	    				chart.labelType(newValue);
	    				chart.update();
	    			}
	    		});
		    }
			
			if (d3.selectAll("#controlContainer table *").empty()) {
				controlContainer.remove();
			}
		}
	}

	pie.validate = function() {
		return true;
	}

	pie.getComponentValue = function() {
		return _value;
	}

	pie.getSVG = function() {
		// inline global style declarations for SVG export
		var styles = document.styleSheets;
		for (i = 0; i < styles.length; i++) {
			if (!styles[i].cssRules && styles[i].rules) {
				styles[i].cssRules = styles[i].rules;
			}
			// empty style declaration
			if (!styles[i].cssRules) continue;
			
			for (var j = 0; j < styles[i].cssRules.length; j++) {
				var rule = styles[i].cssRules[j];
				d3.selectAll(rule.selectorText).each(function(){
					for (var k = 0; k < rule.style.length; k++) {
						var curStyle = this.style.getPropertyValue(rule.style[k]);
						var curPrio = this.style.getPropertyPriority(rule.style[k]);
						var rulePrio = rule.style.getPropertyPriority(rule.style[k]);
						//only overwrite style if not set or priority is overruled
						if (!curStyle || (curPrio != "important" && rulePrio == "important")) {
							d3.select(this).style(rule.style[k], rule.style[rule.style[k]]);
						}
					}
				});
			}
		}
		// correct faulty rect elements
		d3.selectAll("rect").each(function() {
			var rect = d3.select(this);
			if (!rect.attr("width")) {
				rect.attr("width", 0);
			}
			if (!rect.attr("height")) {
				rect.attr("height", 0);
			}
		});
		var svgElement = d3.select("svg")[0][0];
		// Return the SVG as a string.
		return (new XMLSerializer()).serializeToString(svgElement);
	}

	return pie;

}());