(parallelcoords_namespace = function() {
    var input = {};
    var _data;
    var layoutContainer;
    var MIN_HEIGHT = 300, MIN_WIDTH = 400;
    var maxY = 0, minY;
    var _hasBottomView = false;
    var _representation, _value;
    
    input.init = function(representation, value) {  
        _value = value;
        _representation = representation;
        
        d3.select("html").style("width", "100%").style("height", "100%")
        d3.select("body").style("width", "100%").style("height", "100%").style("margin", "0").style("padding", "0");

        var body = d3.select("body");
        
        /*
        if (!representation.options.xCol1 || !representation.options.yCol1) {
        	body.html("Node is not configured.");
        	return;
        }
        */
        
        _data = createData(representation);
        
        layoutContainer = body.append("div").attr("id", "layoutContainer")
                .style("min-width", MIN_WIDTH + "px");
        
        if (_representation.options.svg.fullscreen && _representation.runningInView) {
            layoutContainer.style("width", "100%")
            .style("height", "100%");
        } else {
            layoutContainer.style("width", _representation.options.svg.width + "px")
            .style("height", _representation.options.svg.height + "px");
        }
        
        var controlHeight;
        if (_representation.options.enableViewControls && _representation.runningInView) {
             var controlsContainer = body.append("div").style({
            	 position : "relative", 
            	 bottom : "0px",
                 width : "100%", 
                 padding : "5px", 
                 "padding-left" : "60px",
                 "border-top" : "1px solid black", 
                 "background-color" : "white",
                 "box-sizing": "border-box"}).attr("id", "controlContainer");

            createControls(controlsContainer);
            controlHeight = controlsContainer.node().getBoundingClientRect().height;
        } else {
            controlHeight = 0;
        }

        if (_representation.options.svg.fullscreen && _representation.runningInView) {
            layoutContainer.style({
                "height" : "calc(100% - " + controlHeight + "px)",
                "min-height" :  (MIN_HEIGHT + controlHeight) + "px"
            });
        }
        
        var div = layoutContainer.append("div")
            .attr("id", "svgContainer")
            .style("min-width", MIN_WIDTH + "px")
            .style("min-height", MIN_HEIGHT + "px")
            .style("box-sizing", "border-box")
            .style("display", "inline-block")
            .style("overflow", "hidden")
            .style("margin", "0");
        
        var svg1 = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
        div[0][0].appendChild(svg1);
        
        var d3svg = d3.select("svg").style("font-family", "sans-serif");
        d3svg.append("rect").attr("id", "bgr").attr("fill", _representation.options.backgroundColor);
        
        var plotG = d3svg.append("g").attr("id", "plotG");
        plotG.append("rect").attr("id", "da").attr("fill", _representation.options.daColor);
        
        plotG.on("mouseover", function() {
        	d3.select(this).classed("pale", true);
        })
        .on("mouseout", function() {
        	d3.select(this).classed("pale", false);
        });
        
        d3svg.append("text")
            .attr("id", "title")
            .attr("font-size", 24)
            .attr("x", 20)
            .attr("y", 30)
            .text(_value.options.title);

        d3svg.append("text")
            .attr("id", "subtitle")
            .attr("font-size", 12)
            .attr("x", 20)
            .attr("y", 46)
            .text(_value.options.subtitle);
        
        plotG.append("line")
        	.attr("stroke", "rgba(0,0,0,0.5)")
        	.attr("stroke-width", "2")
        	.attr("id", "yMarker")
        	.attr("visibility", "hidden");
        
        drawChart();
    }
    
    function createData(representation) {
    	var data = { objects : [], colNames : [], colTypes : {}, domains : {}, minmax : {} };
    	var table = representation.inObjects[0];
    	
    	var catColIdx = getDataColumnID(_representation.options.catCol, table);
    	var indices = {};
    	
    	for (var col = 0; col < _representation.options.columns.length; col++) {
    		var columnName = _representation.options.columns[col];
    		data.colNames.push(columnName);
    		var idx = getDataColumnID(columnName, table);
    		indices[columnName] = idx;
    		data.colTypes[columnName] = table.spec.colTypes[idx];
    		if (table.spec.colTypes[idx] === "string") {
    			data.domains[columnName] = d3.set();
    		} else {
    			data.minmax[columnName] = [Number.POSITIVE_INFINITY, Number.NEGATIVE_INFINITY];
    		}
    	}
    	
    	if (catColIdx) {
    		data.domains[_representation.options.catCol] = d3.set();
    	}

    	for (var r = 0; r < table.rows.length; r++) {
    		var row = table.rows[r].data;
    		var obj = {};
    		for (var col = 0; col < _representation.options.columns.length; col++) {
    			obj[_representation.options.columns[col]] = row[indices[_representation.options.columns[col]]];
    		}
    		if (_representation.options.useColors) {
    			obj[":color"] = table.spec.rowColorValues[r];
    		} else if (catColIdx) {
    			obj[_representation.options.catCol] = row[catColIdx];
    		}
    		for(var key in data.domains) {
    			data.domains[key].add(obj[key]);
    		}
    		for(var key in data.minmax) {
    			data.minmax[key][0] = Math.min(data.minmax[key][0], obj[key]);
    			data.minmax[key][1] = Math.max(data.minmax[key][1], obj[key]);
    		}
    		data.objects.push(obj);
    	}
    	
    	return data;
    }

    function getDataColumnID(columnName, table) {
		var colID = null;
		for (var i = 0; i < table.spec.numColumns; i++) {
			if (table.spec.colNames[i] === columnName) {
				colID = i;
				break;
			};
		};
		return colID;
	};
    
    function createControls(controlsContainer) {
        if (_representation.options.enableViewControls) {
       
            var titleDiv;
        
            if (_representation.options.enableTitleEdit || _representation.options.enableSubtitleEdit) {
                titleDiv = controlsContainer.append("div").style({"margin-top" : "5px"});
            }
            
            if (_representation.options.enableTitleEdit) {
                titleDiv.append("label").attr("for", "titleIn").text("Title:").style({"display" : "inline-block", "width" : "100px"});
                titleDiv.append("input")
                .attr({id : "titleIn", type : "text", value : _value.options.title}).style("width", 150)
                .on("keyup", function() {
                    var hadTitles = (_value.options.title.length > 0) || (_value.options.subtitle.length > 0);
                    _value.options.title = this.value;
                    var hasTitles = (_value.options.title.length > 0) || (_value.options.subtitle.length > 0);
                    d3.select("#title").text(this.value);
                    if (hasTitles != hadTitles) {
                        drawChart(true);
                    }
                });
            }
        
            if (_representation.options.enableSubtitleEdit) {
                titleDiv.append("label").attr("for", "subtitleIn").text("Subtitle:").style({"margin-left" : "10px", "display" : "inline-block", "width" : "100px"});
                titleDiv.append("input")
                .attr({id : "subtitleIn", type : "text", value : _value.options.subtitle}).style("width", 150)
                .on("keyup", function() {
                    var hadTitles = (_value.options.title.length > 0) || (_value.options.subtitle.length > 0);
                    _value.options.subtitle = this.value;
                    var hasTitles = (_value.options.title.length > 0) || (_value.options.subtitle.length > 0);
                    d3.select("#subtitle").text(this.value);
                    if (hasTitles != hadTitles) {
                        drawChart(true);
                    }
                });
            }
        }
    }

    function drawChart() {
                 
        var cw = Math.max(MIN_WIDTH, _representation.options.svg.width);
        var ch = Math.max(MIN_HEIGHT, _representation.options.svg.height);
        var chartWidth = cw + "px;"
        var chartHeight = ch + "px";
        
        if (_representation.options.svg.fullscreen && _representation.runningInView) {
            chartWidth = "100%";
            chartHeight = "100%";
        }
        
        var div = d3.select("#svgContainer")
            .style("height", chartHeight)
            .style("width", chartWidth);
        
        var d3svg = d3.select("svg").attr({width : cw, height : ch}).style({width : chartWidth, height : chartHeight});
        
        var mTop = (_value.options.subtitle || _value.options.title) ? 80 : 30;

        var colors = _representation.options.catCol ?
        				d3.scale.category10().domain(_data.domains[_representation.options.catCol].values())
        				: null;

        var maxLength = 0;
        d3.select(".legend").remove();
        if (_representation.options.catCol && _representation.options.showLegend && !_representation.options.useColors) {
	        var legendG = d3svg.append("g").attr("class", "legend");
	        var maxLength = 0;
	        var catValues = _data.domains[_representation.options.catCol].values();
	        for (var i = 0; i < catValues.length; i++) {
	        	var cat = catValues[i];
	        	var txt = legendG.append("text").attr("x", 20).attr("y", i * 23).text(cat);
	        	maxLength = Math.max(maxLength, txt.node().getComputedTextLength());
	        	
	        	legendG.append("circle").attr("cx", 5).attr("cy", i * 23 - 4).attr("r", 5)
	        	.attr("fill", colors(cat));
	        }
	        maxLength += 35;
	        legendG.attr("transform", "translate(" + (parseInt(d3svg.style('width')) - maxLength) + "," + (mTop + 20) + ")");
        }
        
        var margin = {top : mTop, left : 40, bottom : 50, right : 10 + maxLength};
        
        var plotG = d3svg.select("#plotG")
                .attr("transform", "translate(" + margin.left + "," + margin.top + ")");
        
        var w = Math.max(50, parseInt(d3svg.style('width')) - margin.left - margin.right);
        var h = Math.max(50, parseInt(d3svg.style('height')) - margin.top - margin.bottom);
        
        plotG.select("#da").attr({y : -10, width : w, height : h + 45});
        d3svg.select("#bgr").attr({width : w + margin.left + margin.right, height : h + margin.top + margin.bottom});

        var scaleCols = d3.scale.ordinal().domain(_data.colNames).rangePoints([0, w], 0.5);
        
        var scales = {};
        for (var c = 0; c < _data.colNames.length; c++) {
        	var colName = _data.colNames[c];
        	var scale;
        	if (_data.colTypes[colName] === "number") {
        		scale = d3.scale.linear().range([h, 0]).domain(_data.minmax[colName]).nice();
        	} else {
        		scale = d3.scale.ordinal().domain(_data.domains[colName].values()).rangePoints([h, 0], 1.0);
        	}
        	scales[colName] = scale;
        }        
        
        plotG.selectAll("text, path, .axis").remove();
        
        plotG.selectAll("text.label").data(_data.colNames, function(d) { return d; })
        .enter().append("text").attr("class", "label").attr("text-anchor", "middle")
    	.attr("transform", function(d) { return "translate(" + scaleCols(d) + "," + (h + 20) + ")"; })
    	.text(function(d) { return d; });
        
        plotG.selectAll("g.axis").data(_data.colNames, function(d) { return d; })
        .enter().append("g").attr("class", "axis").style("font-weight", "bold")
        	.attr("transform", function(d) { return "translate(" + scaleCols(d) + ",0)"; })
        	.each(function(d) {
	        	var scale = scales[d];
	        	var axis = d3.svg.axis()
	            .scale(scale).orient("left");
	        	
	        	d3.select(this).call(axis);
	        });
        
        d3.selectAll(".axis path").attr("stroke-width", 1).attr("stroke", "black").attr("fill", "none");
        
        plotG.selectAll("path.row").data(_data.objects).enter()
        .append("path").attr("class", "row").attr("d", function(d) {
        	var path = "M";
        	for (var c = 0; c < _data.colNames.length; c++) {
        		var col = _data.colNames[c];
        		if (c > 0) {
        			path += "L";
        		}
        		path += [scaleCols(col), scales[col](d[col])];
        	}
        	return path;
        }).attr("stroke", function(d) {
        	if (_representation.options.useColors) {
        		return d[":color"];
        	} else if (_representation.options.catCol) {
        		return colors(d[_representation.options.catCol]);
        	} else {
        		return "black";
        	}
        })
        .attr("stroke-thickness", 1)
        .attr("stroke-opacity", 0.7)
        .attr("fill", "none").attr();

        if (_representation.options.svg.fullscreen) {
            var win = document.defaultView || document.parentWindow;
            win.onresize = resize;
        }  
    }
    
    input.getSVG = function() {
        var svg = d3.select("svg")[0][0];
        return (new XMLSerializer()).serializeToString(svg);
    };
    
    function resize(event) {
        drawChart(true);
    };
    

    input.validate = function() {
        return true;
    }

    input.getComponentValue = function() {
        return _value;
    }

    return input;

}());