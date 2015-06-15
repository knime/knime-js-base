(barchart_namespace = function() {

	/*
	 * TODO: Multiple bars in a group - done
	 * Legend - done (could be beautified)
	 * Label rotation - to do
	 * Scale font size - partial
	 * Negative values - done
	 * Transitions
	 * Display/hide bars
	 * Selection
	 * 
	 * https://github.com/mbostock/d3/wiki/Ordinal-Scales - Color scales
	 * http://bl.ocks.org/mbostock/3887051 - Grouped Bar Chart
	 * http://zeroviscosity.com/d3-js-step-by-step/step-3-adding-a-legend - legend
	 * http://jsfiddle.net/juY5E/7/ - Paired bars
	 * http://d3-generator.com/ - More ideas
	 * http://bl.ocks.org/slnader/9452976 - Positive and negative values bar chart (and transitions)
	 */ 

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
console.log(_representation);
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

			var optRotateCatLabels = _representation.options["rotateCatLabels"];
			var optLegend = _representation.options["legend"];
			var optScaleFont = _representation.options["scaleFont"];

			var optHorizontal = _representation.options["horizontal"];	

			var optWidth = _representation.options["width"];
			var optHeight = _representation.options["height"];


			var optBarColor = _representation.options["barColor"];



			/*
		/ Data processing...
			 */

			// Get the categories column...
			var categories = knimeTable.getColumn(optCat);

			// below is an array.
			// at the moment just take the first value...
			var freqCol = optFreqCol[0];
			var numBars = optFreqCol.length;

			// Generate a color scale
			colorScale = d3.scale.category20c();
			var colors = [];
			for (i = 0; i < optFreqCol.length; i++) {
				colors[i] = [ optFreqCol[i], colorScale(i) ];			
			}

			// Get the frequency columns
			var valCols = []
			
			for (var k = 0; k < optFreqCol.length; k++) {
				var valCol = knimeTable.getColumn(optFreqCol[k]);
				valCols.push( valCol );
			}
			console.log( valCols );
			
			console.log(categories);
			// Convert the knimeTable columns into a JSON type format
			var endArray = [];
			if (valCols.length > 0) {
				numDataPoints = valCols[0].length;
				for (var i = 0; i < numDataPoints; i++) {
					
					var endObj = {};

					endObj["category"] = categories[i];
					endObj["columnNames"] = optFreqCol;
					var valArray = [];
					for (var j = 0; j < optFreqCol.length; j++) {	

						var obj = {};
						// This is a bit stupid, but allows to look back
						// at where the data came from...
						obj["column"] = optFreqCol[j];
						obj["category"] = categories[i];
						obj["value"] = valCols[j][i];
						obj["color"] = colors[j][1];
						valArray.push(  obj  );
					}
					endObj["info"] = valArray;
					endArray.push(endObj);
				}

				console.log(endArray);
			}
			

			
			// Create the data object used to plot the chart
			var data = endArray;
			
			/*
			 * Could acheive the above with this...
			 */
			//data.forEach(function(d) {
			//    d.ages = data.map(function(name) { return {name: name, value: +d[name]}; });
			//  });
			
			console.log(data);
			/*
			 * Set view options
			 */

			// Set plot size options...
			var margin = {top: 40, right: 40, bottom: 80, left: 80},
			width = optWidth - (margin.left + margin.right);
			height = optHeight - (margin.top + margin.bottom);

			var defaultFontSize = 10 * optScaleFont + "px";
			var xLabelFontSize = 12 * optScaleFont + "px";
			var yLabelFontSize = 12 * optScaleFont + "px";
			var titleFontSize = 16 * optScaleFont + "px";

			var legendRectSize = 18;
			var legendSpacing = 4;


			/*
			 * Generate plot
			 */

			var formatPercent = d3.format(".0%");

			//var x = d3.scale.ordinal()
			//.rangeRoundBands([0, width], .1);

			var x0 = d3.scale.ordinal()
			.rangeRoundBands([0, width], .1);

			var x1 = d3.scale.ordinal();
			
			var y = d3.scale.linear()
			.range([height, 0]);
			
			var xAxis = d3.svg.axis()
			.scale(x0)
			.orient("bottom");

			var yAxis = d3.svg.axis()
			.scale(y)
			.orient("left");
			//.tickFormat(formatPercent);

			var color = d3.scale.category20b();

			var tip = d3.tip()
			.html(function(d) {
				return "<strong>Frequency:</strong> <span style='color:"+d.color+"'>" 
				+ d.value + "</span>"
				+ "<br>Category: " + d.category;
			})
			.attr('class', 'd3-tip')
			.offset([-10, 0])
			
			var svg = d3.select("body").append("svg")
			.attr("width", optWidth)
			.attr("height", optHeight)
			.append("g")
			.attr("transform", "translate(" + margin.left + "," + margin.top + ")");

			svg.call(tip);

			// Edit here for negative values
			//x.domain(data.map(function(d) { return d.category; }));

			x0.domain(data.map(function(d) { return d.category; }));
			x1.domain(optFreqCol)
						.rangeBands([0, x0.rangeBand()]);
			
			//x1.domain(categories).rangeRoundBands([0, x0.rangeBand()]);

			//y.domain([d3.min(data, function(d) { return d.value}), 
			//          d3.max(data, function(d) { return d.value; })]);
			 
			y.domain([d3.min(data, function(d) { 
				return d3.min(d.info, function(d) { 
					return d.value; }); 
				}), 
			          d3.max(data, function(d) { 
				return d3.max(d.info, function(d) { 
					return d.value; }); 
				})]);
			// I think that this should return 0 when not negative...
			
			
			svg.append("g")
			.attr("class", "title")
			.append("text")
			.attr("title", "title")
			.attr("x", (width)/2)
			.attr("y", 0)
			.attr("text-anchor", "middle")
			.style("font-size", titleFontSize) 
			.text(optTitle);


			svg.append("g")
			.attr("class", "x axis")
			.attr("transform", "translate(0," + (height + margin.top) + ")")
			.call(xAxis)
			.append("text")
			.attr("class", "x label")
			.attr("x", (width)/2)
			//.attr("transform", "rotate("+optRotateCatLabels+")")
			.attr("y", 40)
			.attr("text-anchor", "middle")
			.style("font-size", xLabelFontSize) 
			.text(optCatLabel);
		
			svg.append("g")
			.attr("class", "y axis")
			.call(yAxis)
			.append("text")
			.attr("class", "y label")
			.attr("transform", "rotate(-90)")
			.attr("x", -(height)/2)
			.attr("y", -40)
			.attr("text-anchor", "middle")    
			.style("font-size", yLabelFontSize) 
			.text(optFreqLabel);

			//svg.append("g")
			//.attr("class", "legend")
			//.call(legend);
			console.log(data);
			var colGroup = svg.selectAll(".colGroup")
				.data(data)
			.enter().append("g")
				.attr("class", "g")
				.attr("transform", function(d) { return "translate(" + x0(d.category) + ",0)"; });
			// Needs to be coded with variable variables...
			// Need to 'loop' over each group of bars.
			// Need to apply a different class to each group.
			colGroup.selectAll("rect")
			.data(function(d) { return d.info; })
			.enter().append("rect")
			.attr("class", "bar")
			.attr("id", function(d) { return d.column })
			// The problem is currently to do with setting the width (and offset)
			// of the bars. x1 function probably broken.
			.attr("width", function(d) { return x1.rangeBand(); })
			.attr("x", function(d) { return x1(d.column); })
			//.attr("dx", function(d) { return x1(d.column); })
			.attr("y", function(d) { return y(Math.max(0, d.value)); })
			.attr("height", function(d) { return Math.abs(y(d.value)
					- y(0)); })
			.style("fill", function(d) { return d.color })
			.on("mouseover", tip.show)                  
			.on("mouseout", tip.hide);

			function type(d) {
				d.value = +d.value;
				return d;
			}

			/*
			 * Do something about a legend.
			 */
			console.log("Here");

			var legend = svg.append("g")
			.attr("class", "legend")
			//.attr("x", w - 65)
			//.attr("y", 50)
			.attr("height", 100)
			.attr("width", 100)
			.attr('transform', 'translate(-20,50)');

			var legendRect = legend.selectAll('rect').data(colors);

			legendRect.enter()
			.append("rect")
			.style("opacity", 0.6)
			.attr("x", width - 65)
			.attr("width", 10)
			.attr("height", 10);

			legendRect
			.attr("y", function(d, i) {
				return i * 20;
			})
			.style("fill", function(d) {
				return d[1];
			});

			var legendText = legend.selectAll('text').data(colors);

			legendText.enter()
			.append("text")
			.attr("x", width - 52)
			.style("font-size", defaultFontSize);

			legendText
			.attr("y", function(d, i) {
				return i * 20 + 9;
			})
			.text(function(d) {
				return d[0];
			});

			console.log(colors);

			// Create a dropdown to select columns?
			
			// End of require...
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