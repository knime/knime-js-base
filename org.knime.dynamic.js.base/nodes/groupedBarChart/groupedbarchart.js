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
		
		var optWidth = _representation.options["width"];
		var optHeight = _representation.options["height"];
		
		var test_data = stream_layers(3,10+Math.random()*100,.1).map(function(data, i) {
	        return {
	            key: 'Stream' + i,
	            values: data
	        };
	    });
	    console.log('td',test_data);
	    var negative_test_data = new d3.range(0,3).map(function(d,i) {
	        return {
	            key: 'Stream' + i,
	            values: new d3.range(0,11).map( function(f,j) {
	                return {
	                    y: 10 + Math.random()*100 * (Math.floor(Math.random()*100)%2 ? 1 : -1),
	                    x: j,
	                    yErr: [-Math.random() * 30, Math.random() * 30]
	                }
	            })
	        };
	    });

	    var svg = d3.select("body").append("svg")
	    .attr("width", optWidth)
		.attr("height", optHeight)
	    var chart;
	    nv.addGraph(function() {
	        chart = nv.models.multiBarChart()
	            .barColor(d3.scale.category20().range())
	            .duration(300)
	            .margin({bottom: 100, left: 70})
	            .rotateLabels(45)
	            .groupSpacing(0.1)
	            .errorBarColor(function() { return 'red'; })
	        ;
	        chart.reduceXTicks(false).staggerLabels(true);
	        chart.xAxis
	            .axisLabel("ID of Furry Cat Households")
	            .axisLabelDistance(35)
	            .showMaxMin(false)
	            .tickFormat(d3.format(',.6f'))
	        ;
	        chart.yAxis
	            .axisLabel("Change in Furry Cat Population")
	            .axisLabelDistance(-5)
	            .tickFormat(d3.format(',.01f'))
	        ;
	        chart.dispatch.on('renderEnd', function(){
	            nv.log('Render Complete');
	        });
	        svg
	            .datum(negative_test_data)
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