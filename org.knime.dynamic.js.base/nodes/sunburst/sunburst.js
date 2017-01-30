// used sources:
// http://bl.ocks.org/kerryrodden/7090426

(sunburst_namespace = function() {

  var view = {};
  var _representation, _value;
  var _data = {};
  var _colorMap;
  var layoutContainer;
  var freqColIndex = [];
  var columnIndexes = [];
  var MIN_HEIGHT = 300, MIN_WIDTH = 400;


  view.init = function(representation, value) {
    _representation = representation;
    _value = value;

    // Load data from port 1 into knime table (adjacency matrix with weights).
    var knimeTable1 = new kt();
    knimeTable1.setDataTable(_representation.inObjects[0]);
    var columnKeys = _representation.options.columns;
    var freqCol = _representation.options.freq;

    // Load data from port 2 into knime table (information on each of the nodes).
    var knimeTable2 = null;
    if (_representation.inObjects[1] !== null) {
      knimeTable2 = new kt();
      knimeTable2.setDataTable(_representation.inObjects[1]);
    }

    // Compute column indexes instead of names.
    // sequence frequency column
    freqColIndex = knimeTable1.getColumnNames().indexOf(freqCol);
    for (var i = 0; i < columnKeys.length; i++) {
      // sequence columns
      columnIndexes.push(knimeTable1.getColumnNames().indexOf(columnKeys[i]));
    }

    // Check if data from table2 has right format
    if (knimeTable2) {
      var columnTypes2 = knimeTable2.getColumnTypes();
      if (columnTypes2[0] !== "string") {
        alert("Expected data format at port two: Node label (string).");
        return;
      }
    }

    // Check if there is data in first table (this tests for string columns).
    if (columnKeys.length === 0 || knimeTable1.getNumRows() === 0) {
      alert("No data given.");
      return;
    }

    _colorMap = getColors(knimeTable1, knimeTable2, _representation.options["customColors"]);
    _data = transformData(knimeTable1, columnKeys, freqCol);

    drawChart(false);

    if (parent !==undefined && parent.KnimePageLoader !==undefined) {
      parent.KnimePageLoader.autoResize(window.frameElement.id);
    }
  };

  // Take a multi-column CSV and transform it into a hierarchical structure suitable
  // for a partition layout. The first column is a count of how
  // often the sequence occurred. The remaining columns give a sequence of step names, from
  // root to leaf, one for each column.
  transformData = function(knimeTable, columnKeys, freqCol) {
    var rows = knimeTable.getRows();
    var root = {
      "name": "root",
      "children": []
    };
    // Loop over rows
    for (var i = 0; i < rows.length; i++) {
      var size = +rows[i].data[freqColIndex];
      if (isNaN(size)) { // e.g. if this is a header row
        continue;
      }

      // Collect all user selected string cells for the current row.
      var parts = [];
      for (var j = 0; j < columnIndexes.length; j++) {
        parts.push(rows[i].data[columnIndexes[j]]);
      }
      // Loop over selected columns
      // append to hierarchical structure
      var currentNode = root;
      for (var j = 0; j < parts.length; j++) {
        var nodeName = parts[j];
        if (nodeName === null) {
          break;
        }
        var children = currentNode["children"];
        var childNode;
        if (j + 1 < parts.length) {
          // Not yet at the end of the sequence; move down the tree.
          var foundChild = false;
          for (var k = 0; k < children.length; k++) {
            if (children[k]["name"] === nodeName) {
              childNode = children[k];
              foundChild = true;
              break;
            }
          }
          // If we don't already have a child node for this branch, create it.
          if (!foundChild) {
            childNode = {
              "name": nodeName,
              "children": []
            };
            children.push(childNode);
          }
          currentNode = childNode;
        } else {
          // Reached the end of the sequence; create a leaf node.
          childNode = {
            "name": nodeName,
            "size": size
          };
          children.push(childNode);
        }
      }
    }
    return root;
  };

  getColors = function(knimeTable1, knimeTable2, customColors) {
    // Return a function that yields a color given a label/string.
    var colorMap = {};
    if (customColors && knimeTable2 !== null) {
      // loop over rows of table2 to get all labels and corresponding colors
      var rowColors = knimeTable2.getRowColors();
      var rows = knimeTable2.getRows();
      for (var i = 0; i < rows.length; i++) {
        var name = rows[i].data[0];
        var color = rowColors[i];
        colorMap[name] = color;
      }
    } else {
      // loop over cells of table1 to get all labels and assign colors
      var rows = knimeTable1.getRows();
      var colorMapping = {};
      var scale = d3.scale.category20();
      for (var i = 0; i < rows.length; i++) {
        for (var j = 0; j < columnIndexes.length; j++) {
          var index = columnIndexes[j];
          var label = rows[i].data[index];
          colorMap[label] = scale(label);
        }
      }
    }

    return colorMap;
  };

  function createControls(controlsContainer) {
    if (_representation.options.enableViewControls) {

      if (!_representation.options.multi && _representation.options.enableColumnSelection) {
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
          if (hasTitles !==hadTitles) {
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
          if (hasTitles !==hadTitles) {
            drawChart(true);
          }
        });
      }
    }
  }

  // Draws the chart. If redraw is true, there are no animations.
  function drawChart(redraw) {
    // Parse the options

    var optTitle = _value.options["title"];
    var optSubtitle = _value.options["subtitle"];

    var optShowControls = _representation.options["enableViewControls"];
    var optLegend = _representation.options["legend"];
    var optBreadcrumb = _representation.options["breadcrumb"];
    var runningInView = _representation.runningInView;
    var optFullscreen = _representation.options["svg"]["fullscreen"] && runningInView;
    var optWidth = _representation.options["svg"]["width"]
    var optHeight = _representation.options["svg"]["height"]


    var body = d3.select("body");

    var svgContainer;
    if (redraw) {
      d3.select("svg").remove();
      svgContainer = d3.select("#svgContainer");
    } else {
      d3.selectAll("html, body")
        .style("width", "100%")
        .style("height", "100%")
        .style("margin", "0")
        .style("padding", "0");

      layoutContainer = body.append("div")
        .attr("id", "layoutContainer")
        .style("min-width", MIN_WIDTH + "px")
        .style("min-height", MIN_HEIGHT + "px");

      // Size layout container based on sizing settings
      if (optFullscreen) {
          layoutContainer
            .style("width", "100%")
            .style("height", "100%");
      } else {
          layoutContainer
            .style("width", optWidth + "px")
            .style("height", optHeight + "px");
      }

      // Add container for user controls at the bottom if they are enabled and we are running in a view
      var controlHeight;
      if (optShowControls && runningInView) {
        var controlsContainer = body.append("div")
          .attr("id", "controlContainer")
          .style({
            "bottom" : "0px",
            "width" : "100%",
            "padding" : "5px",
            "padding-left" : "60px",
            "border-top" : "1px solid black",
            "background-color" : "white",
            "box-sizing" : "border-box"
          });

        createControls(controlsContainer);
        controlHeight = controlsContainer.node().getBoundingClientRect().height;
        layoutContainer
          .style("min-height", (MIN_HEIGHT + controlHeight) + "px");
        if (optFullscreen) {
          layoutContainer
            .style("height", "calc(100% - " + controlHeight + "px)");
        }
      } else {
        controlHeight = 0;
      }

      // create container for svg
      svgContainer = layoutContainer.append("div")
        .attr("id", "svgContainer")
        .style("min-width", MIN_WIDTH + "px")
        .style("min-height", "calc(" + MIN_HEIGHT + "px - " + controlHeight + "px")
        .style("box-sizing", "border-box")
        .style("overflow", "hidden")
        .style("margin", "0");
    }


    // Create the SVG object
    var svg = svgContainer.append("svg");
    svg.style("font-family", "sans-serif");

    // set width / height
    var boundingRect = layoutContainer.node().getBoundingClientRect();
    var computedWidth = boundingRect.width;
    var computedHeight = boundingRect.height;

    var width, heigth;
    if (optFullscreen) {
      width = computedWidth;
      height = computedHeight;
    } else {
      width = optWidth;
      height = optHeight;
    }
    svgContainer.style("width", width + "px");
    svg.attr("width", width);
    svgContainer.style("height", height + "px");
    svg.attr("height", height);

    // Title
    svg.append("text")
      .attr("id", "title")
      .attr("font-size", 24)
      .attr("x", 20)
      .attr("y", 30)
      .text(_value.options.title);

    // Subtitle
    svg.append("text")
      .attr("id", "subtitle")
      .attr("font-size", 12)
      .attr("x", 20)
      .attr("y", 46)
      .text(_value.options.subtitle);


    // The margins for the plot area
    var margin = {
      top : (optTitle || optSubtitle) ? 60 : 10,
      left : 10,
      bottom : 10,
      right : 10
    };

    var plottingSurface = svg.append("g")
      .attr("id", "plottingSurface")
      .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    // Calculate size of the plot area
    var w = Math.max(50, width - margin.left - margin.right);
    var h = Math.max(50, height - margin.top - margin.bottom);

    var options = {
      optLegend: optLegend,
      optBreadcrumb: optBreadcrumb
    };
    drawSunburst(_data, plottingSurface, w, h, options);

    // Set resize handler
    if (optFullscreen) {
      var win = document.defaultView || document.parentWindow;
      win.onresize = resize;
    }
  }

  function drawSunburst(data, plottingSurface, width, height, options) {
    var marginTop = options.optBreadcrumb ? 40 : 0;
    var marginLeft = options.optLegend ? 85 : 0;

    // Dimensions of sunburst.
    var radius = Math.min(width - marginLeft, height - marginTop) / 2;

    // Breadcrumb dimensions: width, height, spacing, width of tip/tail.
    var b = { w: 75, h: 30, s: 3, t: 10 };

    // Total size of all segments; we set this later, after loading the data.
    var totalSize = 0;

    var partition = d3.layout.partition()
        .size([2 * Math.PI, radius * radius])
        .value(function(d) { return d.size; });

    var arc = d3.svg.arc()
        .startAngle(function(d) { return d.x; })
        .endAngle(function(d) { return d.x + d.dx; })
        .innerRadius(function(d) { return Math.sqrt(d.y); })
        .outerRadius(function(d) { return Math.sqrt(d.y + d.dy); });

    // create new group for the sunburst plot (not legend, not breadcrumb)
    var sunburstGroup = plottingSurface.append("g")
        .attr("transform", "translate(" + ((width - marginLeft) / 2) + "," + ((height + marginTop) / 2) + ")");

    // Bounding circle underneath the sunburst, to make it easier to detect
    // when the mouse leaves the plottingSurface g.
    sunburstGroup.append("svg:circle")
        .attr("r", radius)
        .style("opacity", 0);

    // For efficiency, filter nodes to keep only those large enough to see.
    var nodes = partition.nodes(data)
        .filter(function(d) {
          return (d.dx > 0.005); // 0.005 radians = 0.29 degrees
        });

    var path = sunburstGroup.data([data]).selectAll("path")
        .data(nodes)
        .enter().append("svg:path")
        .attr("display", function(d) { return d.depth ? null : "none"; })
        .attr("d", arc)
        .attr("fill-rule", "evenodd")
        .style("fill", function(d) { return _colorMap[d.name]; })
        .style("opacity", 1)
        .on("mouseover", mouseover);

    // Basic setup of page elements.
    if (options.optBreadcrumb) {
      initializeBreadcrumbTrail(plottingSurface);
    }

    if (options.optLegend) {
      drawLegend(plottingSurface);
    }

    // Add the mouseleave handler to the bounding circle.
    sunburstGroup.on("mouseleave", mouseleave);

    // Get total size of the tree = value of root node from partition.
    totalSize = path.node().__data__.value;

    // add explanation in the middle of the circle
    var explanation = sunburstGroup.append("g")
        .attr("id", "explanation");
    explanation.append("text")
      .attr("id", "percentage")
      .attr("text-anchor", "middle")
      .attr("alignment-baseline", "middle");
    explanation.append("text")
      .attr("id", "explanationText");

    // Fade all but the current sequence, and show it in the breadcrumb trail.
    function mouseover(d) {

      var percentage = (100 * d.value / totalSize).toPrecision(3);
      var percentageString = percentage + "%";
      if (percentage < 0.1) {
        percentageString = "< 0.1%";
      }

      d3.select("#percentage")
          .text(percentageString);

      d3.select("#explanation")
          .style("visibility", "");

      var sequenceArray = getAncestors(d);
      updateBreadcrumbs(sequenceArray, percentageString);

      // Fade all the segments.
      d3.selectAll("path")
          .style("opacity", 0.3);

      // Then highlight only those that are an ancestor of the current segment.
      sunburstGroup.selectAll("path")
          .filter(function(node) {
                    return (sequenceArray.indexOf(node) >= 0);
                  })
          .style("opacity", 1);
    }

    // Restore everything to full opacity when moving off the visualization.
    function mouseleave(d) {

      // Hide the breadcrumb trail
      d3.select("#trail")
          .style("visibility", "hidden");

      // Deactivate all segments during transition.
      d3.selectAll("path").on("mouseover", null);

      // Transition each segment to full opacity and then reactivate it.
      d3.selectAll("path")
          .transition()
          .duration(200)
          .style("opacity", 1)
          .each("end", function() {
                  d3.select(this).on("mouseover", mouseover);
                });

      d3.select("#explanation")
          .style("visibility", "hidden");
    }

    // Given a node in a partition layout, return an array of all of its ancestor
    // nodes, highest first, but excluding the root.
    function getAncestors(node) {
      var path = [];
      var current = node;
      while (current.parent) {
        path.unshift(current);
        current = current.parent;
      }
      return path;
    }

    function initializeBreadcrumbTrail(plottingSurface) {
      // Add the svg area.
      var trail = plottingSurface.append("svg:svg")
          .attr("width", width)
          .attr("height", 50)
          .attr("id", "trail")

      // Add the label at the end, for the percentage.
      trail.append("svg:text")
        .attr("id", "endlabel")
        .style("fill", "#000");
    }

    // Generate a string that describes the points of a breadcrumb polygon.
    function breadcrumbPoints(d, i) {
      var points = [];
      points.push("0,0");
      points.push(b.w + ",0");
      points.push(b.w + b.t + "," + (b.h / 2));
      points.push(b.w + "," + b.h);
      points.push("0," + b.h);
      if (i > 0) { // Leftmost breadcrumb; don't include 6th vertex.
        points.push(b.t + "," + (b.h / 2));
      }
      return points.join(" ");
    }

    // Update the breadcrumb trail to show the current sequence and percentage.
    function updateBreadcrumbs(nodeArray, percentageString) {

      // Data join; key function combines name and depth (= position in sequence).
      var g = d3.select("#trail")
          .selectAll("g")
          .data(nodeArray, function(d) { return d.name + d.depth; });

      // Add breadcrumb and label for entering nodes.
      var entering = g.enter().append("svg:g");

      entering.append("svg:polygon")
          .attr("points", breadcrumbPoints)
          .style("fill", function(d) { return _colorMap[d.name]; });

      entering.append("svg:text")
          .attr("x", (b.w + b.t) / 2)
          .attr("y", b.h / 2)
          .attr("dy", "0.35em")
          .attr("text-anchor", "middle")
          .text(function(d) { return d.name; });

      // Set position for entering and updating nodes.
      g.attr("transform", function(d, i) {
        return "translate(" + i * (b.w + b.s) + ", 0)";
      });

      // Remove exiting nodes.
      g.exit().remove();

      // Now move and update the percentage at the end.
      d3.select("#trail").select("#endlabel")
          .attr("x", (nodeArray.length + 0.5) * (b.w + b.s))
          .attr("y", b.h / 2)
          .attr("dy", "0.35em")
          .attr("text-anchor", "middle")
          .text(percentageString);

      // Make the breadcrumb trail visible, if it's hidden.
      d3.select("#trail")
          .style("visibility", "");

    }

    function drawLegend(plottingSurface) {

      // Dimensions of legend item: width, height, spacing, radius of rounded rect.
      var li = {
        w: 75, h: 30, s: 3, r: 3
      };

      var legend = plottingSurface.append("g")
          .attr("width", li.w)
          .attr("height", d3.keys(_colorMap).length * (li.h + li.s))
          .attr("transform", "translate(" + (width - li.w) + ", 0)");

      var g = legend.selectAll("g")
          .data(d3.entries(_colorMap))
          .enter().append("svg:g")
          .attr("transform", function(d, i) {
                  return "translate(0," + i * (li.h + li.s) + ")";
               });

      g.append("svg:rect")
          .attr("rx", li.r)
          .attr("ry", li.r)
          .attr("width", li.w)
          .attr("height", li.h)
          .style("fill", function(d) { return d.value; });

      g.append("svg:text")
          .attr("x", li.w / 2)
          .attr("y", li.h / 2)
          .attr("dy", "0.35em")
          .attr("text-anchor", "middle")
          .text(function(d) { return d.key; });
    }

    function toggleLegend() {
      var legend = d3.select("#legend");
      if (legend.style("visibility") == "hidden") {
        legend.style("visibility", "");
      } else {
        legend.style("visibility", "hidden");
      }
    }
  }

  view.getSVG = function() {
    // inline global style declarations for SVG export
    var styles = document.styleSheets;
    for (i = 0; i < styles.length; i++) {
      if (!styles[i].cssRules && styles[i].rules) {
        styles[i].cssRules = styles[i].rules;
      }
      // empty style declaration
      if (!styles[i].cssRules) continue;

      for (var j = 0; j < styles[i].cssRules.length; j++) {
        var rule = styles[i].cssRules[j];
        d3.selectAll(rule.selectorText).each(function() {
          for (var k = 0; k < rule.style.length; k++) {
            var curStyle = this.style.getPropertyValue(rule.style[k]);
            var curPrio = this.style.getPropertyPriority(rule.style[k]);
            var rulePrio = rule.style.getPropertyPriority(rule.style[k]);
            //only overwrite style if not set or priority is overruled
            if (!curStyle || (curPrio !=="important" && rulePrio == "important")) {
              d3.select(this).style(rule.style[k], rule.style[rule.style[k]]);
            }
          }
        });
      }
    }
    // correct faulty rect elements
    d3.selectAll("rect").each(function() {
      var rect = d3.select(this);
      if (!rect.attr("width")) {
        rect.attr("width", 0);
      }
      if (!rect.attr("height")) {
        rect.attr("height", 0);
      }
    });
    var svgElement = d3.select("svg")[0][0];
    // Return the SVG as a string.
    return (new XMLSerializer()).serializeToString(svgElement);
  };

  function resize(event) {
    drawChart(true);
  }

  view.validate = function() {
    return true;
  };

  view.getComponentValue = function() {
    return _value;
  };

  return view;

}());