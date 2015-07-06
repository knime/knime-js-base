(grouped_bar_chart_namespace = function() {

	var barchart = {};
	var layoutContainer;
	var MIN_HEIGHT = 300, MIN_WIDTH = 400;
	var _representation, _value;

	function createControls(controlsContainer) {
		if (_representation.options.enableViewControls) {

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

			if (_representation.options.enableFrequencyColumnChooser) {
				//TODO: Add a multi-column selector here...
			}

			var orientationToggle;

			if (_representation.options.enableHorizontalToggle) {
				//TODO: Make work in touch interface...
				orientationToggle = controlsContainer.append("div").style({"margin-top" : "5px"});

				orientationToggle.append("label").attr("for", "orientation")
				.text("Plot horizontal bar chart:").style({"display" : "inline-block", "width" : "100px"});
				orientationToggle.append("input")
				.attr({id : "orientation", type : "checkbox", checked : _value.options["orientation"]}).style("width", 150)
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

	function drawChart(redraw) {
		d3.select("html").style("width", "100%").style("height", "100%")
		d3.select("body").style("width", "100%").style("height", "100%").style("margin", "0").style("padding", "0");

		/*
		 * Process options
		 */
		var optWidth = _representation.options["width"];
		var optHeight = _representation.options["height"];

		var optTitle = _value.options["title"];
		var optCatLabel = _value.options["catLabel"];
		var optFreqLabel = _value.options["freqLabel"];

		var optRotateLabels = _representation.options["rotateLabels"];
		var optLegend = _representation.options["legend"];

		var optOrientation = _value.options["orientation"];	

		var optFullscreen = _representation.options["svg"]["fullscreen"];
		var optWidth = _representation.options["svg"]["width"]
		var optHeight = _representation.options["svg"]["height"]

		var optFreqCol = _value.options["freq"];
		var optCat = _value.options["cat"];

		var body = d3.select("body");

		layoutContainer = body.append("div").attr("id", "layoutContainer")
		.style("width", "100%").style("height", "calc(100% - 0px)")
		.style("min-width", MIN_WIDTH + "px");

		var controlHeight;
		if (_representation.enableControls || true) {
			var controlsContainer = body.append("div").style({position : "absolute", bottom : "0px",
				width : "100%", padding : "5px", "padding-left" : "60px",
				"border-top" : "1px solid black", "background-color" : "white"}).attr("id", "controlContainer");

			createControls(controlsContainer);
			controlHeight = controlsContainer.node().getBoundingClientRect().height;
		} else {
			controlHeight = 0;
		}

		layoutContainer.style({
			"height" : "calc(100% - " + controlHeight + "px)",
			"min-height" :  (MIN_HEIGHT + controlHeight) + "px"
		});

		var div = layoutContainer.append("div")
		.attr("id", "svgContainer")
		.style("min-width", MIN_WIDTH + "px")
		.style("min-height", MIN_HEIGHT + "px")
		.style("box-sizing", "border-box")
		.style("display", "inline-block")
		.style("overflow", "hidden")
		.style("margin", "0")


		if (redraw != true) {
			//TODO: Check API docs. Delete the current SVG element.
			//chart.drop/delete etc. See lines 231/233.
		} else {
			//d3.select("svg").remove();
		}

		var svg1 = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
		div[0][0].appendChild(svg1);

		var svg = d3.select("svg");

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
		var knimeTable = new kt();
		// Add the data from the input port to the knimeTable.
		var port0dataTable = _representation.inObjects[0];
		knimeTable.setDataTable(port0dataTable);

		var categories = knimeTable.getColumn(optCat);
		/////TODO: Check duplicates. Use a set, see API docs.
		var colSet = d3.set(optFreqCol);

		// Get the frequency columns
		var valCols = [];
		var hasNull = false;
		var isDuplicate = false;
		var retained = [];

		console.log(colSet);
		
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
				retained.push(j);
			}
		}
		console.log(valCols);
		var plot_data = [];

		if (valCols.length > 0) {
			numDataPoints = valCols[0].length;
			for (var j = 0; j < retained.length; j++) {	

				var key = optFreqCol[retained[j]];
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
							values.push(dataObj);
						} 						
					}

				}
				plot_stream = {"key": key, "values": values};
				plot_data[j] = plot_stream;
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

		/*
		 * Plot chart
		 */
		var chart;
		nv.addGraph(function() {
			if (optOrientation == true) {
				chart = nv.models.multiBarHorizontalChart();
			} else if (optOrientation == false) {
				chart = nv.models.multiBarChart();
				chart.reduceXTicks(false).staggerLabels(true);
				// Not relevant for horizontal chart...
				if (optRotateLabels == true) {
					chart.rotateLabels(45);
				}
			}
			chart
			.barColor(d3.scale.category20().range())
			.duration(300)
			.margin({bottom: 100, left: 100})
			.groupSpacing(0.1);
			;

			//TODO: Show/hide optChartLegend.

			chart.xAxis
			.axisLabel(optCatLabel)
			.axisLabelDistance(35)
			.showMaxMin(false)
			;

			// tick format probably needs scaling...
			chart.yAxis
			.axisLabel(optFreqLabel)
			.axisLabelDistance(15)
			.tickFormat(d3.format(',.01f'))
			;

			//TODO: Fix tooltip colours...

			//TODO: Add chart title. Check API docs.
			//chart.title(_value.options.title);

			chart.dispatch.on('renderEnd', function(){
				nv.log('Render Complete');
			});
			svg
			.datum(plot_data)
			.call(chart);
			nv.utils.windowResize(chart.update);
			chart.dispatch.on('stateChange', function(e) {
				nv.log('New State:', JSON.stringify(e));
			});
			chart.state.dispatch.on('change', function(state){
				nv.log('state', JSON.stringify(state));
			});
			console.log('chart',chart);

			return chart;
		});	
	}

	barchart.init = function(representation, value) {  
		_value = value;
		_representation = representation;

		drawChart();
	}

	barchart.validate = function() {
		return true;
	}

	barchart.getComponentValue = function() {
		return _value;
	}

	barchart.getSVG = function() {
		var svgElement = d3.select("svg")[0][0];
		// Return the SVG as a string.
		return (new XMLSerializer()).serializeToString(svgElement);
	}

	return barchart;

}());