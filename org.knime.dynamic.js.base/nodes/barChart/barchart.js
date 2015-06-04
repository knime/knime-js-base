(barchart_namespace = function() {

	var input = {};
	var _representation, _value;
	var barchartOptions = [];
	var textField, errorMessage;
	var interval;

	input.init = function(representation, value) {	
		
		// At the moment we need to include d3-tip here, not in node.xml
		// This ensures that it is loaded at the correct time relative
		// to dependencies.
		require([
		    	"https://cdnjs.cloudflare.com/ajax/libs/d3-tip/0.6.3/d3-tip.min.js"], 
                 function () {
                 
		_representation = representation;
		_value = value;

		// Should check if null
		// Initialise a new knimeTable object.
		var knimeTable = new kt();
		// Add the data from the input port to the knimeTable.
		var port0dataTable = _representation.inObjects[0];
		knimeTable.setDataTable(port0dataTable);
		
		
		/*
		 * Option processing...
		 */
		var optCat = _representation.options["cat"];
		var optFreqCol = _representation.options["freq"];
		

		var optTitle = _representation.options["title"];
		var optCatLabel = _representation.options["catLabel"];
		var optFreqLabel = _representation.options["freqLabel"];
		
		
		var optWidth = _representation.options["width"];
		var optHeight = _representation.options["height"];
		
		var optHorizontal = _representation.options["horizontal"];	
		
		var optBarColor = _representation.options["barColor"];
			
		/*
		/ Data processing...
		*/

		// Get the categories column...
		var categories = knimeTable.getColumn(optCat);
		
		// below is an array.
		// at the moment just take the first value...
		freqCol = optFreqCol[0];
		
		// Get the frequency column
		var valCol = knimeTable.getColumn(freqCol);
				
		// Convert the knimeTable columns into a JSON type format
		var valArray = [];
		for (var i = 0; i < valCol.length; i++) {
			var obj = {};
			obj[freqCol] = valCol[i];
			obj[optCat] = categories[i];
			obj["color"] = optBarColor;
			valArray.push(  obj  );
		}
		
		// Create the data object used to plot the chart
		var data = valArray;
			
		/*
		 * Set view options
		 */
		
		// Set plot size options...
		var margin = {top: 40, right: 40, bottom: 60, left: 60},
		width = optWidth - margin.left - margin.right,
		height = optHeight - margin.top - margin.bottom;
		
		/*
		 * Generate plot
		 */
		
		var formatPercent = d3.format(".0%");

		var x = d3.scale.ordinal()
		.rangeRoundBands([0, width], .1);

		var y = d3.scale.linear()
		.range([height, 0]);

		var xAxis = d3.svg.axis()
		.scale(x)
		.orient("bottom");

		var yAxis = d3.svg.axis()
		.scale(y)
		.orient("left")
		.tickFormat(formatPercent);

		
		var tip = d3.tip()
		.html(function(d) {
			return "<strong>Frequency:</strong> <span style='color:"+optBarColor+"'>" 
					+ d.frequency + "</span>";
		})
		.attr('class', 'd3-tip')
		.offset([-10, 0])
	
		 var svg = d3.select("body").append("svg")
		.attr("width", width + margin.left + margin.right)
		.attr("height", height + margin.top + margin.bottom)
		.append("g")
		.attr("transform", "translate(" + margin.left + "," + margin.top + ")");
		
		svg.call(tip);

		x.domain(data.map(function(d) { return d.letter; }));
		y.domain([0, d3.max(data, function(d) { return d.frequency; })]);

		svg.append("g")
		.attr("class", "title")
		.append("text")
		.attr("title", "title")
		.attr("x", (width)/2)
		.attr("y", 0)
		.attr("text-anchor", "middle")
		.style("font-size", "16px") 
		.text(optTitle);
		
		
		svg.append("g")
		.attr("class", "x axis")
		.attr("transform", "translate(0," + height + ")")
		.call(xAxis)
		.append("text")
		.attr("class", "x label")
		.attr("x", (width+margin.left)/2)
	    .attr("y", 40)
	    .attr("text-anchor", "end")
   		.style("font-size", "12px") 
	    .text(optCatLabel);

		svg.append("g")
		.attr("class", "y axis")
		.call(yAxis)
		.append("text")
		.attr("class", "y label")
	    .attr("transform", "rotate(-90)")
	    .attr("x", -(height-margin.top-margin.bottom)/2)
	    .attr("y", -40)
	    .attr("text-anchor", "end")    
		.style("font-size", "12px") 
	    .text(optFreqLabel);
		
		svg.selectAll(".bar")
		.data(data)
		.enter().append("rect")
		.attr("class", "bar")
		.attr("x", function(d) { return x(d.letter); })
		.attr("width", x.rangeBand())
		.attr("y", function(d) { return y(d.frequency); })
		.attr("height", function(d) { return height - y(d.frequency); })
		.style("fill", optBarColor)
		.on("mouseover", tip.show)                  
		.on("mouseout", tip.hide);

		function type(d) {
			d.frequency = +d.frequency;
			return d;
		}

		});
	}

	input.validate = function() {
		return true;
	}

	input.getComponentValue = function() {
		var knimeTable = new kt();
		knimeTable.setDataTable(_representation.dataTables[0]);
		knimeTable.setId("selection");
		return knimeDataTable;
	}

	return input;

}());