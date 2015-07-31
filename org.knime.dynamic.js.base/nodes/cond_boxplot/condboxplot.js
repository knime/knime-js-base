(boxplot_namespace = function() {
    var input = {};
    var _data = {};
    var layoutContainer;
    var MIN_HEIGHT = 300, MIN_WIDTH = 400;
    var maxY = 0;
    var _representation, _value;
    
    input.init = function(representation, value) { 
                
        _value = value;
        _representation = representation;
     
        if (!_value.options.numCol) {
            _value.options.numCol = _representation.options.columns[0];
        }
        
        if (_representation.options.columns.length == 0) {
            alert("No numeric columns selected");
            return;
        }

        d3.select("html").style("width", "100%").style("height", "100%")
        d3.select("body").style("width", "100%").style("height", "100%").style("margin", "0").style("padding", "0");

        var body = d3.select("body");
        
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
                 "box-sizing" : "border-box"}).attr("id", "controlContainer");

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
        
        drawChart();
        
        if (parent != undefined && parent.KnimePageLoader != undefined) {
            parent.KnimePageLoader.autoResize(window.frameElement.id);
        }
    }
    
    function createControls(controlsContainer) {
        if (_representation.options.enableViewControls) {
        
            if (_representation.options.enableColumnSelection) {
                var colSelectDiv = controlsContainer.append("div");
                colSelectDiv.append("label").attr("for", "colSelect").text("Selected column: ");
                var select = colSelectDiv.append("select").attr("id", "colSelect");
                for (var i = 0; i < _representation.options.columns.length; i++) {
                    var txt = _representation.options.columns[i];
                    var o = select.append("option").text(txt).attr("value", txt);
                    if (txt === _value.options.numCol) {
                        o.property("selected", true);
                    }
                }
                select.on("change", function() {
                    _value.options.numCol = select.property("value");
                    drawChart();
                });
            }
            
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

    function drawChart(resizing) {
        _data = _representation.inObjects[0][_value.options.numCol];
        maxY = Number.NEGATIVE_INFINITY;
        for (var key in _data) {
            maxY = Math.max(_data[key].max, maxY);
        }
        
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

        var margin = {top : (_value.options.subtitle || _value.options.title) ? 60 : 10, left : 40, bottom : 40, right : 10};

        var d3svg = d3.select("svg").attr({width : cw, height : ch}).style({width : chartWidth, height : chartHeight});
        
        var plotG = d3svg.select("#plotG")
                .attr("transform", "translate(" + margin.left + "," + margin.top + ")");
        
        var w = Math.max(50, parseInt(d3svg.style('width')) - margin.left - margin.right);
        var h = Math.max(50, parseInt(d3svg.style('height')) - margin.top - margin.bottom);
        
        plotG.select("#da").attr({width : w, height : h + 5});
        d3svg.select("#bgr").attr({width : w + margin.left + margin.right, height : h + margin.top + margin.bottom});
        
        var x = d3.scale.ordinal().domain(d3.keys(_data)).rangeBands([0,w], 0.75, 0.5);
        var y = d3.scale.linear().domain([0, maxY]).range([h, 0]).nice();
        
        var xAxis = d3.svg.axis().scale(x)
                .orient("bottom");
            
        var yAxis = d3.svg.axis().scale(y)
                .orient("left").ticks(5);
        
        // Redraw axis
        d3.selectAll(".axis").remove();
             
        var d3XAxis = plotG.append("g")
            .attr("class", "x axis")
            .attr("transform", "translate(0," + (h + 5) + ")")
            .call(xAxis);
            
        d3XAxis.selectAll("line,path").attr("fill", "none").attr("stroke", "black").attr("shape-rendering", "crispEdges"); 
    
        // Add the Y Axis
        var d3YAxis = plotG.append("g")
            .attr("class", "y axis")
            .call(yAxis);
            
       d3YAxis.selectAll("line,path").attr("fill", "none").attr("stroke", "black").attr("shape-rendering", "crispEdges"); 
        
        plotG.selectAll(".axis-label").remove();

        plotG.append("text")
            .attr("class", "y axis-label")
            .attr("text-anchor", "end")
            .attr("y", 6)
            .attr("dy", ".75em")
            .attr("transform", "rotate(-90)")
            .text(_value.options.numCol);
            
       plotG.append("text")
        .attr("class", "x axis-label")
        .attr("text-anchor", "end")
        .attr("x", w)
        .attr("y", h - 6)
        .text("class");

        var range = x.range();
        var duration = _representation.runningInView ? 500 : 0;
        
        var boxG = plotG.selectAll("g.box")
        .data(d3.entries(_data), function(d) {
            return d.key;
        });
        
        boxG.exit().remove();

        var bge = boxG.enter();
        
        var box = bge.append("g")
            .attr("class", "box")
            .attr("transform", function(d) { return "translate(" + x(d.key) + ",0)"; });
        
        d3.selectAll(".box").transition().duration(resizing ? 0 : duration)
            .attr("transform", function(d) { return "translate(" + x(d.key) + ",0)"; });
            
        box.append("rect")
            .attr("class", "boxrect")
            .attr("stroke", "black")
            .attr("fill", _representation.options.boxColor || "none");
       
        boxG.selectAll(".boxrect")
                .data(function(d) { return [d]; } )
                .transition().duration(resizing ? 0 : duration)
                .attr("y", function(d) { return y(d.value.upperQuartile); })
                .attr("height", function(d) { return y(d.value.lowerQuartile) - y(d.value.upperQuartile); })
                .attr("width", x.rangeBand());
        
        var middle = x.rangeBand() / 2;
        
        box.append("text")
            .attr("x", -5)
            .attr("text-anchor", "end")
            .attr("class", "uqText");
            
        boxG.selectAll(".uqText")
            .data(function(d) { return [d]; } )
            .transition().duration(resizing ? 0 : duration)
            .attr("y", function(d) { return y(d.value.upperQuartile) + 3; })
            .text(function(d) { return Math.round(d.value.upperQuartile * 100) / 100; });
            
        box.append("text")
            .attr("x", -5)
            .attr("text-anchor", "end")
            .attr("class", "lqText");
            
       boxG.selectAll(".lqText")
            .data(function(d) { return [d]; } )
            .transition().duration(resizing ? 0 : duration)
            .attr("y", function(d) { return y(d.value.lowerQuartile) + 3; })
            .text(function(d) { return Math.round(d.value.lowerQuartile * 100) / 100; });
        

        // median
        box.append("line")
            .attr("stroke", "black")
            .attr("stroke-width", 3)
            .attr("x1", "0")
            .attr("class", "median");
            
        boxG.selectAll(".median")
            .data(function(d) { return [d]; } )
            .transition().duration(resizing ? 0 : duration)
            .attr("x2", x.rangeBand())
            .attr("y1", function(d) { return y(d.value.median); })
            .attr("y2", function(d) { return y(d.value.median); });
        
        box.append("text")
            .attr("class", "medianText");
            
        boxG.selectAll(".medianText")
            .data(function(d) { return [d]; } )
            .transition().duration(resizing ? 0 : duration)
            .attr("x", x.rangeBand() + 5)
            .attr("y", function(d) { return y(d.value.median) + 3; })
            .text(function(d) { return Math.round(d.value.median * 100) / 100; });
        
        // Upper whisker       
        box.append("line")
            .attr("stroke", "black")
            .attr("class", "uwL1");
            
        boxG.selectAll(".uwL1")
            .data(function(d) { return [d]; } )
            .transition().duration(resizing ? 0 : duration)
            .attr("x1", middle)
            .attr("x2", middle)
            .attr("stroke-dasharray", "5,5")
            .attr("y1", function(d) { return y(d.value.upperQuartile); })
            .attr("y2", function(d) { return y(d.value.upperWhisker); });
     

        box.append("line")
            .attr("stroke", "black")
            .attr("x1", "0")
            .attr("class", "uwL2");
            
        boxG.selectAll(".uwL2")
            .data(function(d) { return [d]; } )
            .transition().duration(resizing ? 0 : duration)
            .attr("x2", x.rangeBand())
            .attr("y1", function(d) { return y(d.value.upperWhisker); })
            .attr("y2", function(d) { return y(d.value.upperWhisker); });

        box.append("text")
            .attr("class", "uwText");
            
        boxG.selectAll(".uwText")
            .data(function(d) { return [d]; } )
            .transition().duration(resizing ? 0 : duration)
            .attr("x", x.rangeBand() + 5)
            .attr("y", function(d) { return y(d.value.upperWhisker) + 10; })
            .text(function(d) { return Math.round(d.value.upperWhisker * 100) / 100; });

        // Lower whisker
        box.append("line")
            .attr("stroke", "black")
            .attr("class", "ulL1");
            
       boxG.selectAll(".ulL1")
            .data(function(d) { return [d]; } )
            .transition().duration(resizing ? 0 : duration)
            .attr("x1", middle)
            .attr("x2", middle)
            .attr("stroke-dasharray", "5,5")
            .attr("y1", function(d) { return y(d.value.lowerQuartile); })
            .attr("y2", function(d) { return y(d.value.lowerWhisker); });
            
       box.append("line")
            .attr("stroke", "black")
            .attr("x1", "0")
            .attr("class", "ulL2");
            
       boxG.selectAll(".ulL2")
            .data(function(d) { return [d]; } )
            .transition().duration(resizing ? 0 : duration)
            .attr("x2", x.rangeBand())
            .attr("y1", function(d) { return y(d.value.lowerWhisker); })
            .attr("y2", function(d) { return y(d.value.lowerWhisker); });
            
       box.append("text")
            .attr("class", "ulText");
            
       boxG.selectAll(".ulText")
            .data(function(d) { return [d]; } )
            .transition().duration(resizing ? 0 : duration)
            .attr("x", x.rangeBand() + 5)
            .attr("y", function(d) { return y(d.value.lowerWhisker) - 3; })
            .text(function(d) { return Math.round(d.value.lowerWhisker * 100) / 100; });

        // Mild outlier

       var outl = boxG.selectAll("circle.mo")
                 .data(function(d) { return d.value.mildOutliers; } );    
       
       outl.enter().append("circle")
        .attr("class", "mo")
        .attr("r", 5)
        .attr("fill", _representation.options.daColor)
        .attr("stroke", "black")
        .attr("cx", middle)
        .attr("cy", function(d) { return y(d.value); })
        .append("title").text(function(d) { return d.rowKey; });
       
       outl.transition().duration(resizing ? 0 : duration)
       .attr("cx", middle)
       .attr("cy", function(d) { return y(d.value); });
       
       //outl.exit().remove();
       outl.exit().transition().style("opacity", 0).each("end", function() { d3.select(this).remove(); });
       
       // Extreme outlier
       
       var exoutl = boxG.selectAll("g.eo")
                 .data(function(d) { return d.value.extremeOutliers; } );    
       
       var enterG = exoutl.enter().append("g")
         .attr("class", "eo")
         .attr("transform", function(d) { return "translate(" + middle + "," + y(d.value) + ")"; });
         
         var crossSize = 4;
         enterG.append("line").attr({x1 : -crossSize, y1 : -crossSize, x2 : crossSize, y2 : crossSize, "stroke-width" : 1.5, "stroke-linecap" : "round"})
        .append("title").text(function(d) { return d.rowKey; });
         enterG.append("line").attr({x1 : -crossSize, y1 : crossSize, x2 : crossSize, y2 : -crossSize, "stroke-width" : 1.5, "stroke-linecap" : "round"})
        .append("title").text(function(d) { return d.rowKey; });
       
       exoutl.transition().duration(resizing ? 0 : duration)
       .attr("transform", function(d) { return "translate(" + middle + "," + y(d.value) + ")"; });
       
       exoutl.exit().transition().style("opacity", 0).each("end", function() { d3.select(this).remove(); });

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