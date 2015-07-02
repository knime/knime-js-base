(donut_chart_namespace = function() {
	
	// TODO: Correct width/height
	// Remember options
	// Dropdown for category column selection
	// Dropdown for frequency column selection
	// Selections out
	
	var donut = {};
	var _representation, _value;
	
	donut.init = function(representation, value) {
		_representation = representation;
		_value = value;
		
		// Parse the options
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
		console.log(plot_data);
		
		// Create the SVG object
	    var svg = d3.select("body").append("svg")
	    
	    if (optFullscreen = false) {
		    if (optWidth > 0) {
			    svg.attr("width", optWidth);
		    }
		    if (optHeight > 0) {
				svg.attr("height", optHeight);
		    }	    	
	    } else {
	    	// Set full screen height/width
	    }
		
	    
		//Donut chart example
		nv.addGraph(function() {
		  var chart = nv.models.pieChart()
		      .x(function(d) { return d.label })
		      .y(function(d) { return d.value })
		      .showLabels(showLabels)     //Display pie labels
		      .labelThreshold(labelThreshold)  //Configure the minimum slice size for labels to show up
		      .labelType(labelType) //Configure what type of data to show in the label. Can be "key", "value" or "percent"
		      .donut(showLabels)          //Turn on Donut mode. Makes pie chart look tasty!
		      .donutRatio(holeSize)     //Configure how big you want the donut hole size to be.
		      ;
		
		    svg
		        .datum(plot_data)
		        .transition().duration(350)
		        .call(chart);
		
		  return chart;
		});


	}
	
	donut.validate = function() {
		return true;
	}
	
	donut.getComponentValue = function() {
		return _value;
	}
	
	donut.getSVG = function() {
		var svgElement = d3.select("svg")[0][0];
		// Return the SVG as a string.
		return (new XMLSerializer()).serializeToString(svgElement);
	}
	
	return donut;
	
}());