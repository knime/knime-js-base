(grouped_bar_chart_namespace = function() {

	var barchart = {};
	var layoutContainer;
	var MIN_HEIGHT = 200, MIN_WIDTH = 300;
	var _representation, _value;
	var chart, svg;
	var staggerCheckbox;
	
	barchart.init = function(representation, value) {  
		_value = value;
		_representation = representation;
		drawChart();
		if (parent != undefined && parent.KnimePageLoader != undefined) {
			parent.KnimePageLoader.autoResize(window.frameElement.id);
		}
	}

	function drawChart(redraw) {
		d3.selectAll("html, body").style("width", "100%").style("height", "100%").style("margin", "0").style("padding", "0");

		/*
		 * Process options
		 */
		var viewControls = _representation.options.enableViewControls;
		var optWidth = _representation.options["width"];
		var optHeight = _representation.options["height"];

		var optTitle = _value.options["title"];
		var optCatLabel = _value.options["catLabel"];
		var optFreqLabel = _value.options["freqLabel"];

		var optStaggerLabels = _representation.options["staggerLabels"];
		var optLegend = _representation.options["legend"];
		var optControls = _representation.options["enableStackedEdit"] && viewControls;

		var optOrientation = _value.options["orientation"];	

		var optFullscreen = _representation.options["svg"]["fullscreen"] && _representation.runningInView;
		var optWidth = _representation.options["svg"]["width"]
		var optHeight = _representation.options["svg"]["height"]

		var optMethod = _representation.options["aggr"];
		var optFreqCol = _representation.options["freq"];
		var optCat = _representation.options["cat"];

		var body = d3.select("body");

		var width = optWidth + "px";
		var height = optHeight + "px";
		if (optFullscreen) {
			width = height = "100%";
		}
		
		var div;
		if (redraw) {
			d3.select("svg").remove();
			div = d3.select("#svgContainer");
		} else {
			layoutContainer = body.append("div").attr("id", "layoutContainer")
				.style("width", width).style("height", height)
				.style("min-width", MIN_WIDTH + "px");

			var controlHeight;
			if (_representation.options.enableViewControls) {
				var controlsContainer = body.append("div")
					.style({bottom : "0px",
							width : "100%", 
							padding : "5px", 
							"padding-left" : "60px",
							"border-top" : "1px solid black", 
							"background-color" : "white", 
							"box-sizing": "border-box"})
							.attr("id", "controlContainer");

				createControls(controlsContainer);
				controlHeight = controlsContainer.node().getBoundingClientRect().height;
				layoutContainer.style("min-height", (MIN_HEIGHT + controlHeight) + "px");
				if (optFullscreen) {
					layoutContainer.style("height", "calc(100% - " + controlHeight + "px)");
				}
			} else {
				controlHeight = 0;
			}
			
			div = layoutContainer.append("div")
				.attr("id", "svgContainer")
				.style("min-width", MIN_WIDTH + "px")
				.style("min-height", "calc(" + MIN_HEIGHT + "px - " + controlHeight + "px")
				.style("box-sizing", "border-box")
				.style("overflow", "hidden")
				.style("margin", "0");
		}


		var svg1 = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
		div[0][0].appendChild(svg1);

		svg = d3.select("svg");
		svg.style("font-family", "sans-serif");
		svg.classed("colored", true);

		if (!optFullscreen) {
			if (optWidth > 0) {
				div.style("width", optWidth+"px")
				svg.attr("width", optWidth);
				// Looks like the below doesn't work,
				// above does work...
				//chart.width(optWidth);
			}
			if (optHeight > 0) {
				svg.attr("height", optHeight);
				div.style("height", optHeight+"px");
				// Looks like the below doesn't work,
				// above does work...
				//chart.height(optHeight);
			}	    	
		} else {
			// Set full screen height/width
			div.style("width", "100%");
			div.style("height", "100%");

			svg.attr("width", "100%");
			svg.attr("height", "100%");
		}
		
		/* 
		 * Process data
		 */
    	var customColors, colorScale;
		if (_representation.inObjects[1]) {
			// Custom color scale
			var colorTable = new kt();
			colorTable.setDataTable(_representation.inObjects[1]);
			if (colorTable.getColumnTypes()[0] == 'string') {
				customColors = {};
				var colorCol = colorTable.getColumn(0);
				for (var i = 0; i < colorCol.length; i++) {
					customColors[colorCol[i]] = colorTable.getRowColors()[i];
				}
				colorScale = [];
			}
		}
		
		var knimeTable = new kt();
		// Add the data from the input port to the knimeTable.
		var port0dataTable = _representation.inObjects[0];
		knimeTable.setDataTable(port0dataTable);

		var categories = knimeTable.getColumn(optCat);
		
		// Default color scale
		if (!customColors) {
			colorScale = d3.scale.category10();
			if (categories.length > 10) {
				colorScale = d3.scale.category20();
			}
		}
		
		if (optMethod == "Occurence\u00A0Count") {
			optFreqCol = [knimeTable.getColumnNames()[1]];
		}

		// Get the frequency columns
		var valCols = [];
		var hasNull = false;
		var isDuplicate = false;
		var retained = [];

		for (var k = 0; k < optFreqCol.length; k++) {
			var colHasNull = false;
			
			var valCol = knimeTable.getColumn(optFreqCol[k]);
			for (var j=0; j < valCol.length; j++) {
				if (valCol[j] == null) {
					hasNull = true;
					colHasNull = true;
				};
				
			}
			// Add an isDuplicate test here...
			if ((colHasNull != true) && (isDuplicate != true)) {
				valCols.push( valCol );
				retained.push(optFreqCol[k]);
			}
		}
		
		var plot_data = [];
		if (valCols.length > 0) {
			var numDataPoints = valCols[0].length;
			for (var j = 0; j < retained.length; j++) {	

				var key = retained[j];
				if (optMethod == "Occurence\u00A0Count") {
					key = "Occurence Count";
				}
				var values = [];

				for (var i = 0; i < numDataPoints; i++) {
					var dataObj = {};

					if (categories != undefined) {
						if (isDuplicate == true)  {
							alert("Duplicate categories found in column.");
							return "duplicate";
						}
						
						if (categories[i] == null) {
							alert("Missing values in category column are not permitted.");
							return "missing";
						} else {
							dataObj["x"] = categories[i];
							dataObj["y"] = valCols[j][i];
							/*dataObj["color"] = colorScale(j);*/
							values.push(dataObj);
						} 						
					}

				}
				var plot_stream = {"key": key, "values": values};
				plot_data[j] = plot_stream;
				
				if (customColors) {
					var color = customColors[key];
					if (!color) {
						color = "#7C7C7C";
					}
					colorScale.push(color);
				}
			}
		} else {
			if (hasNull == false) {
				alert("No numeric columns detected.");
				return "numeric";
			} else {
				alert("Numeric columns detected, but contains missing values.");
				return "missing";
			}

		}
		
		//console.log('td', plot_data);

		/*
		 * Plot chart
		 */
		nv.addGraph(function() {
			if (optOrientation) {
				chart = nv.models.multiBarHorizontalChart();
			} else {
				chart = nv.models.multiBarChart();
				chart.reduceXTicks(false);
			}
			
			var colorRange = customColors ? colorScale : colorScale.range();
			
			chart
				.color(colorRange)
				.duration(300)
				.margin({right: 20, top: 60})
				.groupSpacing(0.1);
			
			updateTitles(false);

	        chart.showControls(_representation.runningInView && optControls);
			chart.showLegend(optLegend);

			updateAxisLabels(false);

			svg.datum(plot_data)
				.transition().duration(300)
				.call(chart);
			nv.utils.windowResize(chart.update);
			
			return chart;
		});	
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
			chart.controls.margin({top: topMargin, bottom: topMargin});
			if (updateChart && chartNeedsUpdating) {
				chart.update();
			}
		}
	}
	
	function updateAxisLabels(updateChart) {
		if (chart) {
			var optOrientation = _value.options["orientation"];
			var optStaggerLabels = _value.options["staggerLabels"];
			var curCatAxisLabel, curFreqAxisLabel;
			var curCatAxisLabelElement = d3.select(".nv-x.nv-axis .nv-axis-label");
			var curFreqAxisLabelElement = d3.select(".nv-y.nv-axis .nv-axis-label");
			if (!curCatAxisLabelElement.empty()) {
				curCatAxisLabel = curCatAxisLabelElement.text();
			}
			if (!curFreqAxisLabelElement.empty()) {
				curFreqAxisLabel = curCatAxisLabelElement.text();
			}
			var chartNeedsUpdating = curCatAxisLabel != _value.options.catLabel
				|| curFreqAxisLabel != _value.options.freqLabel;
			if (!chartNeedsUpdating) return;
			
			chart.xAxis
				.axisLabel(_value.options.catLabel)
				.axisLabelDistance(optOrientation ? 30 : optStaggerLabels ? 10 : -5)
				.showMaxMin(false);

			// tick format needed?
			chart.yAxis
				.axisLabel(_value.options.freqLabel)
				.axisLabelDistance(optOrientation ? -5 : 0)
				/*.tickFormat(d3.format(',.01f'))*/;
			
			var leftMargin = optOrientation ? 100 : 70;
			var bottomMargin = 35;
			if (!_value.options.catLabel) {
				bottomMargin = optOrientation ? bottomMargin : 25;
				leftMargin = optOrientation ? 70 : leftMargin;
			}
			if (!_value.options.freqLabel) {
				bottomMargin = optOrientation ? 25 : bottomMargin;
				leftMargin = optOrientation ? leftMargin : 50;
			}
			if (!optOrientation) {
				chart.staggerLabels(optStaggerLabels);
				if (optStaggerLabels) {
					bottomMargin += _value.options.catLabel ? 25 : 15;
				}
			}
			
			chart.margin({left: leftMargin, bottom: bottomMargin})
			
			if (updateChart) {
				chart.update();
			}
		}
	}

	function createControls(controlsContainer) {
		if (_representation.options.enableViewControls) {
			
			/*.style("width", "100%")*/
			var controlTable = controlsContainer.append("table")
	    		.attr("id", "barControls")
	    		.style("padding", "10px")
	    		.style("margin", "0 auto")
	    		.style("box-sizing", "border-box")
	    		.style("font-family", "sans-serif")
	    		.style("font-size", "12px")
	    		.style("border-spacing", 0)
	    		.style("border-collapse", "collapse");
			
			var orientationEdit = _representation.options.enableHorizontalToggle;
			var staggerLabels = _representation.options.enableStaggerToggle;
			var categoryEdit = _representation.options.enableCategoryChooser;
			
			if (orientationEdit || staggerLabels) {
				var orientationContainer = controlTable.append("tr");
				if (orientationEdit) {
					orientationContainer.append("td").append("label").attr("for", "orientation").text("Plot horizontal bar chart:").style("margin", "0 5px");
		    		var orientationCheckbox = orientationContainer.append("td").append("input")
	    				.attr("type", "checkbox")
	    				.attr("id", "orientation")
	    				.style("margin-right", "15px")
	    				.property("checked", _value.options.orientation)
	    				.on("click", function() {
	    					if (_value.options["orientation"] != this.checked) {
	    						_value.options["orientation"] = this.checked;
	    						d3.select("#stagger").property("disabled", this.checked);
	    						drawChart(true);
	    					}
	    				});
				}
				if (staggerLabels) {
					orientationContainer.append("td").append("label").attr("for", "stagger").text("Stagger labels:").style("margin", "0 5px");
		    		var staggerCheckbox = orientationContainer.append("td").append("input")
	    				.attr("type", "checkbox")
	    				.attr("id", "stagger")
	    				.style("margin-right", "15px")
	    				.property("checked", _value.options.staggerLabels)
	    				.property("disabled", _value.options["orientation"])
	    				.on("click", function() {
	    					if (_value.options.staggerLabels != this.checked) {
	    						_value.options.staggerLabels = this.checked;
	    						drawChart(true);
	    					}
	    				});
				}
				/*if (categoryEdit) {
					orientationContainer.append("td").append("label").attr("for", "cat").text("Category Column:").style("margin", "0 5px");
					var categoryBox = orientationContainer.append("td").append("select")
						.attr("id", "cat")
						.style("width", "150px")
						.style("margin-right", "15px");
					var COLUMNS = _representation.inObjects[0].spec.colNames;
					var COLTYPES = _representation.inObjects[0].spec.colTypes;
					for (var i = 0; i < COLUMNS.length; i++) {
						if (COLTYPES[i] == "string") {
							var interp = COLUMNS[i];
							var o = categoryBox.append("option").text(interp).attr("value", interp);
							if (interp === _representation.options.cat) {
								o.property("selected", true);
							}
						}
					}
					categoryBox.on("change", function() {
						var orig = _value.options.cat;
						_value.options.cat = categoryBox.property("value");
						var res = drawChart(true);
						if (res == "missing") {
							_representation.options.cat = orig;
							drawChart(true);
						}
					});
				}*/
			}
			
			var titleEdit = _representation.options.enableTitleEdit;
			var subtitleEdit = _representation.options.enableSubtitleEdit;
			
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
		    		var chartTitleText = titleEditContainer.append("td").append("input")
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
			
			if (_representation.options.enableAxisEdit) {
				var axisContainer = controlTable.append("tr");
				
				axisContainer.append("td").append("label").attr("for", "catAxisLabel").text("Category axis label:").style("margin", "0 5px");
				var categoryBox = axisContainer.append("td").append("input")
					.attr("id", "catAxisLabel")
					.attr("type", "text")
					.attr("value", _value.options.catLabel)
					.style("width", "150px")
					.style("margin-right", "15px")
					.on("keyup", function() {
						_value.options.catLabel = this.value;
						updateAxisLabels(true);
					});
				
				axisContainer.append("td").append("label").attr("for", "freqAxisLabel").text("Frequency axis label:").style("margin", "0 5px");
				var categoryBox = axisContainer.append("td").append("input")
					.attr("id", "freqAxisLabel")
					.attr("type", "text")
					.attr("value", _value.options.freqLabel)
					.style("width", "150px")
					.style("margin-right", "15px")
					.on("keyup", function() {
						_value.options.freqLabel = this.value;
						updateAxisLabels(true);
					});
					
			}
				
			if (d3.selectAll("#controlContainer table *").empty()) {
				controlContainer.remove();
			}
		}
	}

	barchart.validate = function() {
		return true;
	}

	barchart.getComponentValue = function() {
		return _value;
	}

	barchart.getSVG = function() {
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
		var svgElement = d3.select("svg")[0][0];
		// Return the SVG as a string.
		return (new XMLSerializer()).serializeToString(svgElement);
	}

	return barchart;

}());