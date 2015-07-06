(pie_chart_namespace = function() {

	// TODO: Correct width/height
	// Remember options
	// Dropdown for category column selection
	// Dropdown for frequency column selection
	//// Selections out

	var pie = {};
	var _representation, _value;
	var layoutContainer;
	var MIN_HEIGHT = 300, MIN_WIDTH = 400;

	// TODO: Redraw on resize?
	
	function createControls(controlsContainer) {
		if (_representation.options.enableViewControls) {

			if (_representation.options.enableColumnChooser) {
				var interpolationDiv = controlsContainer.append("div");
				interpolationDiv.append("label").attr("for", "columnSelect").text("Column: ");
				var select = interpolationDiv.append("select").attr("id", "freq");
				var COLUMNS = _representation.inObjects[0].spec.colNames;
				var COLTYPES = _representation.inObjects[0].spec.colTypes;
				for (var i = 0; i < COLUMNS.length; i++) {
					if (COLTYPES[i] == "number") {
						var interp = COLUMNS[i];
						var o = select.append("option").text(interp).attr("value", interp);
						if (interp === _value.options.freq) {
							o.property("selected", true);
						}
					}
				}
				select.on("change", function() {
					_value.options.freq = select.property("value");
					drawChart();
				});

				var titleDiv;

	            if (_representation.options.enableTitleEdit) {
	                titleDiv = controlsContainer.append("div").style({"margin-top" : "5px"});
	            }
	            
				if (_representation.options.enableTitleEdit) {
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
	}

	function drawChart() {
		// Parse the options
		var optTitle = _value.options["title"];

		var optTogglePieChart = _representation.options["togglePie"];

		var showLabels = _representation.options["showLabels"];
		var labelThreshold = _representation.options["labelThreshold"];
		var labelType = "percent"; //_representation.options["labelType"];
		var showLabels = _representation.options["showLabels"];
		var holeSize = _representation.options["holeSize"];

		var optFullscreen = _representation.options["svg"]["fullscreen"];
		var optWidth = _representation.options["svg"]["width"]
		var optHeight = _representation.options["svg"]["height"]

		var optCat = _representation.options["cat"];
		var optFreqCol = _value.options["freq"];

		/*
		 * Setup interactive controls
		 */

		d3.select("html").style("width", "100%").style("height", "100%")
		d3.select("body").style("width", "100%").style("height", "100%").style("margin", "0").style("padding", "0");

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
		.style("margin", "0");
		

		/* 
		 * Process data
		 */
		var knimeTable = new kt();
		// Add the data from the input port to the knimeTable.
		var port0dataTable = _representation.inObjects[0];
		knimeTable.setDataTable(port0dataTable);

		var categories = knimeTable.getColumn(optCat);

		var valCol = knimeTable.getColumn(optFreqCol);

		var plot_data = [];

		if (valCol.length > 0) {
			numDataPoints = valCol.length;

			for (var i = 0; i < numDataPoints; i++) {			
				plot_stream = {"label": categories[i], "value": valCol[i]};
				plot_data.push(plot_stream);
			}

		}

		// Create the SVG object
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
			// The above doesn't work.
			// TODO: Make the full screen work correctly.
		}
		
		//var svg = d3.select("body").append("svg");
		d3.select("body").attr("width", "100%");
		d3.select("body").attr("height", "100%");
		d3.select("body").attr("margin", "0");
		d3.select("body").attr("padding", "0");

		//Donut chart example
		nv.addGraph(function() {
			var chart = nv.models.pieChart()
			.x(function(d) { return d.label })
			.y(function(d) { return d.value })
			.showLabels(showLabels)     //Display pie labels
			.labelThreshold(labelThreshold)  //Configure the minimum slice size for labels to show up
			.labelType(labelType); //Configure what type of data to show in the label. Can be "key", "value" or "percent".

			// TODO: Add a mechanism to remember the categories that are
			// switched on.
			
			if (optTogglePieChart == true) {
				chart.donut(showLabels);          //Turn on Donut mode. Makes pie chart look tasty!
				chart.donutRatio(holeSize);     //Configure how big you want the donut hole size to be.		 }
			}

			console.log("value");
			console.log(_value);
			console.log("value");

			console.log("representation");
			console.log(_representation);
			console.log("representation");

			var colNames = _representation.inObjects[0].spec.colNames;

			chart.title(_value.options.title);

			svg
			.datum(plot_data)
			.transition().duration(350)
			.call(chart);

			return chart;
		});
	}

	pie.init = function(representation, value) {
		_representation = representation;
		_value = value;

		drawChart();
	}

	pie.validate = function() {
		return true;
	}

	pie.getComponentValue = function() {

		//_value.outColumns.selection;
		
		// TODO: Add a boolean column depending on whether the
		// part of the pie chart was selected/shown...

		//_value.outColumns["selection"];
		
		return _value;
	}

	pie.getSVG = function() {
		var svgElement = d3.select("svg")[0][0];

		// TODO: Doesn't work. Rect requires a width error message...
		
		// Return the SVG as a string.
		return (new XMLSerializer()).serializeToString(svgElement);
	}

	return pie;

}());