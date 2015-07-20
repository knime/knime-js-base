(grouped_bar_chart_namespace = function() {

	var barchart = {};
	var layoutContainer;
	var MIN_HEIGHT = 200, MIN_WIDTH = 300;
	var _representation, _value;
	
	barchart.init = function(representation, value) {  
		_value = value;
		_representation = representation;

		drawChart();
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

		var optRotateLabels = _representation.options["rotateLabels"];
		var optLegend = _representation.options["legend"];
		var optControls = _representation.options["enableStackedEdit"] && viewControls;

		var optOrientation = _value.options["orientation"];	

		var optFullscreen = _representation.options["svg"]["fullscreen"] && _representation.runningInView;
		var optWidth = _representation.options["svg"]["width"]
		var optHeight = _representation.options["svg"]["height"]

		var optFreqCol = _value.options["freq"];
		var optCat = _value.options["cat"];

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
				.style("margin", "0")
		}


		var svg1 = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
		div[0][0].appendChild(svg1);

		var svg = d3.select("svg");
		svg.style("font-family", "sans-serif");
		svg.classed("colored", true);

		if (optFullscreen == false) {
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
		/////TODO: Check duplicates. Use a set, see API docs.
		var colSet = d3.set(optFreqCol);
		
		// Default color scale
		if (!customColors) {
			colorScale = d3.scale.category10();
			if (categories.length > 10) {
				colorScale = d3.scale.category20();
			}
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
		
		console.log('td', plot_data);

		/*
		 * Plot chart
		 */
		var chart;
		nv.addGraph(function() {
			if (optOrientation) {
				chart = nv.models.multiBarHorizontalChart();
			} else {
				chart = nv.models.multiBarChart();
				chart.reduceXTicks(false)
					.staggerLabels(false);
				// Not relevant for horizontal chart...
				if (optRotateLabels == true) {
					chart.rotateLabels(45);
				}
			}
			
			var colorRange = customColors ? colorScale : colorScale.range();
			
			chart
				.color(colorRange)
				.duration(300)
				.margin({left: 70, right: 20, top: 60, bottom: 40})
				.groupSpacing(0.1)
			;
			var topMargin = 10;
			topMargin += _value.options.title ? 10 : 0;
			topMargin += _value.options.subtitle ? 8 : 0;
			chart.legend.margin({top: topMargin, bottom: topMargin});
			chart.controls.margin({top: topMargin, bottom: topMargin})

	        chart.showControls(_representation.runningInView && optControls);
			//chart.legend.color(colorScale.range());
			chart.showLegend(optLegend);

			chart.xAxis
				.axisLabel(optCatLabel)
				.axisLabelDistance(0)
				.showMaxMin(false)
			;

			// tick format probably needs scaling...
			chart.yAxis
				.axisLabel(optFreqLabel)
				.axisLabelDistance(-10)
				.tickFormat(d3.format(',.01f'))
			;

			if (_value.options.title) {
				svg.append("text")
					.attr("x", 20)             
					.attr("y", 30)
					.attr("font-size", 24)
					.attr("id", "title")
					.text(_value.options.title);
			}
			if (_value.options.subtitle) {
				svg.append("text")
					.attr("x", 20)             
					.attr("y", _value.options.title ? 46 : 20)
					.attr("font-size", 12)
					.attr("id", "subtitle")
					.text(_value.options.subtitle);
			}

			/*chart.dispatch.on('renderEnd', function(){
				nv.log('Render Complete');
			});*/
			
			svg.datum(plot_data)
				.transition().duration(300)
				.call(chart);
			nv.utils.windowResize(chart.update);
			
			/*chart.dispatch.on('stateChange', function(e) {
				nv.log('New State:', JSON.stringify(e));
			});
			chart.state.dispatch.on('change', function(state){
				nv.log('state', JSON.stringify(state));
			});
			console.log('chart',chart);*/

			return chart;
		});	
	}

	function createControls(controlsContainer) {
		if (_representation.options.enableViewControls) {
			
			/*.style("width", "100%")*/
			/*var controlTable = controlsContainer.append("table")
	    		.attr("id", "scatterControls")
	    		.style("padding", "10px")
	    		.style("margin", "0 auto")
	    		.style("box-sizing", "border-box")
	    		.style("font-family", defaultFont)
	    		.style("font-size", defaultFontSize+"px")
	    		.style("border-spacing", 0)
	    		.style("border-collapse", "collapse");*/

			if (_representation.options.enableCategoryChooser) {
				var interpolationDiv = controlsContainer.append("div");
				interpolationDiv.append("label").attr("for", "columnSelect").text("Category Column: ");
				var select = interpolationDiv.append("select").attr("id", "cat");
				var COLUMNS = _representation.inObjects[0].spec.colNames;
				var COLTYPES = _representation.inObjects[0].spec.colTypes;
				for (var i = 0; i < COLUMNS.length; i++) {
					if (COLTYPES[i] == "string") {
						var interp = COLUMNS[i];
						var o = select.append("option").text(interp).attr("value", interp);
						if (interp === _value.options.cat) {
							o.property("selected", true);
						}
					}
				}
				select.on("change", function() {
					var orig = _value.options.cat;
					_value.options.cat = select.property("value");
					var res = drawChart(true);
					if (res == "missing") {
						_value.options.cat = orig;
						drawChart(true);
					}
				});
			}
			
			// Add orientation selector
			var orientationToggle;
			if (_representation.options.enableHorizontalToggle) {
				orientationToggle = controlsContainer.append("div").style({"margin-top" : "5px"});

				orientationToggle.append("label").attr("for", "orientation")
				.text("Plot horizontal bar chart:").style({"display" : "inline-block", "width" : "100px"});
				orientationToggle.append("input")
				.attr({id : "orientation", type : "checkbox"})
				.property("checked", _value.options["orientation"])
				.style("width", 150)
				.on("click", function() {
					if (_value.options["orientation"] != this.checked) {
						_value.options["orientation"] = this.checked;
						drawChart(true);
					}
				});
			}

			var axisDiv;

			if (_representation.options.enableAxisEdit) {
				axisDiv = controlsContainer.append("div").style({"margin-top" : "5px"});

				axisDiv.append("label").attr("for", "yaxisIn").text("y-axis title:").style({"display" : "inline-block", "width" : "100px"});
				axisDiv.append("input")
				.attr({id : "yaxisIn", type : "text", value : _value.options.freqLabel}).style("width", 150)
				.on("keyup", function() {
					var hadTitles = (_value.options.freqLabel.length > 0);
					_value.options.freqLabel = this.value;
					var hasTitles = (_value.options.freqLabel.length > 0);
					if (hasTitles != hadTitles) {
						drawChart(true);
					}
				});

				axisDiv.append("label").attr("for", "xaxisIn").text("x-axis title:").style({"display" : "inline-block", "width" : "100px"});
				axisDiv.append("input")
				.attr({id : "xaxisIn", type : "text", value : _value.options.catLabel}).style("width", 150)
				.on("keyup", function() {
					var hadTitles = (_value.options.catLabel.length > 0);
					_value.options.catLabel = this.value;
					var hasTitles = (_value.options.catLabel.length > 0);
					if (hasTitles != hadTitles) {
						drawChart(true);
					}
				});

			}

			var titleDiv;

			if (_representation.options.enableTitleEdit) {
				titleDiv = controlsContainer.append("div").style({"margin-top" : "5px"});

				titleDiv.append("label").attr("for", "titleIn").text("Title:").style({"display" : "inline-block", "width" : "100px"});
				titleDiv.append("input")
				.attr({id : "titleIn", type : "text", value : _value.options.title}).style("width", 150)
				.on("keyup", function() {
					var hadTitles = (_value.options.title.length > 0);
					_value.options.title = this.value;
					var hasTitles = (_value.options.title.length > 0);
					//d3.select("#title").text(this.value);
					if (hasTitles != hadTitles) {
						drawChart(true);
					}
				});
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
			if (!styles[i].cssRules) {
				styles[i].cssRules = styles[i].rules;
			}
			for (var j = 0; j < styles[i].cssRules.length; j++) {
				var rule = styles[i].cssRules[j];
				d3.selectAll(rule.selectorText).each(function(){
					for (var k = 0; k < rule.style.length; k++) {
						d3.select(this).style(rule.style[k], rule.style[rule.style[k]]);
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