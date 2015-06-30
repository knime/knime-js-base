(grouped_bar_chart_namespace = function() {
	
	var sample = {};
	var _representation, _value;
	
	/* Inspired by Lee Byron's test data generator. */
	function stream_layers(n, m, o) {
	  if (arguments.length < 3) o = 0;
	  function bump(a) {
	    var x = 1 / (.1 + Math.random()),
	        y = 2 * Math.random() - .5,
	        z = 10 / (.1 + Math.random());
	    for (var i = 0; i < m; i++) {
	      var w = (i / m - y) * z;
	      a[i] += x * Math.exp(-w * w);
	    }
	  }
	  return d3.range(n).map(function() {
	      var a = [], i;
	      for (i = 0; i < m; i++) a[i] = o + o * Math.random();
	      for (i = 0; i < 5; i++) bump(a);
	      return a.map(stream_index);
	    });
	}

	/* Another layer generator using gamma distributions. */
	function stream_waves(n, m) {
	  return d3.range(n).map(function(i) {
	    return d3.range(m).map(function(j) {
	        var x = 20 * j / m - i / 3;
	        return 2 * x * Math.exp(-.5 * x);
	      }).map(stream_index);
	    });
	}

	function stream_index(d, i) {
	  return {x: i, y: Math.max(0, d)};
	}
	
	sample.init = function(representation, value) {
		_representation = representation;
		_value = value;
		
		/*
		 * Process options
		 */
		var optWidth = _representation.options["width"];
		var optHeight = _representation.options["height"];
		
		var optTitle = _representation.options["title"];
		var optCatLabel = _representation.options["catLabel"];
		var optFreqLabel = _representation.options["freqLabel"];

		var optRotateLabels = _representation.options["rotateLabels"];
		var optLegend = _representation.options["legend"];

		var optOrientation = _representation.options["orientation"];	

		var optWidth = _representation.options["width"];
		var optHeight = _representation.options["height"];
		
		var optFreqCol = _value.options["freq"];
		var optCat = _representation.options["cat"];
		
		/* 
		 * Process data
		 */
		var knimeTable = new kt();
		// Add the data from the input port to the knimeTable.
		var port0dataTable = _representation.inObjects[0];
		knimeTable.setDataTable(port0dataTable);
		
		var categories = knimeTable.getColumn(optCat);
		
		// Get the frequency columns
		var valCols = []
		for (var k = 0; k < optFreqCol.length; k++) {
			var valCol = knimeTable.getColumn(optFreqCol[k]);
			valCols.push( valCol );
		}
		
		var plot_data = [];
				
		if (valCols.length > 0) {
			numDataPoints = valCols[0].length;
			for (var j = 0; j < optFreqCol.length; j++) {	

				var key = optFreqCol[j];
				var values = [];

				for (var i = 0; i < numDataPoints; i++) {
					var dataObj = {};

					dataObj["x"] = categories[i];
					dataObj["y"] = valCols[j][i];
					
					values.push(dataObj);
				}
				plot_stream = {"key": key, "values": values};
				plot_data[j] = plot_stream;
			}
		}
		// nvd3 will alter this data...
		console.log(plot_data);
		/*
		 * Plot chart
		 */

	    var svg = d3.select("body").append("svg")
	    
	    if (optWidth > 0) {
		    svg.attr("width", optWidth);
	    }
	    if (optHeight > 0) {
			svg.attr("height", optHeight);
	    }
	    
	    var chart;
	    nv.addGraph(function() {
	    	if (optOrientation == true) {
	    		chart = nv.models.multiBarHorizontalChart();
	    		//chart.reduceYTicks(false).staggerLabels(true);
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
	            .margin({bottom: 100, left: 70})
	            .groupSpacing(0.1)
	            .errorBarColor(function() { return 'red'; })
	        ;
	        
	        ////
        	// needs both label and category label
	        

	        
	        //chart.title
	        //	.title(optChartTitle);
	        
	        chart.xAxis
	            .axisLabel(optCatLabel)
	            .axisLabelDistance(35)
	            .showMaxMin(false)
	            //.tickFormat(d3.format(',.6f'))
	        ;
	        
	        // tick format probably needs scaling...
	        chart.yAxis
	            .axisLabel(optFreqLabel)
	            .axisLabelDistance(-5)
	            .tickFormat(d3.format(',.01f'))
	        ;
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
	
	sample.validate = function() {
		return true;
	}
	
	sample.getComponentValue = function() {
		return _value;
	}
	
	sample.getSVG = function() {
		var svgElement = d3.select("svg")[0][0];
		// Return the SVG as a string.
		return (new XMLSerializer()).serializeToString(svgElement);
	}
	
	return sample;
	
}());