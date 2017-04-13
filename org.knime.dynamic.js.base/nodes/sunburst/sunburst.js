// TODO:
// maybe: formatter for count/percentage
// custom colors (waiting for christian)
// different mouse modi for zoom/selection - see scatterplott
// when zoomed in, mouse over goes mad
// use isInteractivityAvailable
// reset highliting

(sunburst_namespace = function() {

  var view = {};
  var _representation, _value;
  var knimeTable1, knimeTable2;
  var _data = {};
  var uniqueLabels;
  var nodes;
  var selectedRows = [];
  var highlitedNode;
  var rowKey2leaf = {};
  var _colorMap;

  var layoutContainer;
  var MIN_HEIGHT = 300, MIN_WIDTH = 400;

  var rootNodeName = "root";
  var nullNodeName = "?";

  var innerLabelStyles = ['count', 'percentage'];

  view.init = function(representation, value) {
    _representation = representation;
    _value = value;

    // Load data from port 1 into knime table.
    knimeTable1 = new kt();
    knimeTable1.setDataTable(_representation.inObjects[0]);

    // TODO: handle second port: color model

    // Load data from port 2 into knime table (information on each of the nodes).
    // knimeTable2 = null;
    // if (_representation.inObjects[1] !== null) {
    //   knimeTable2 = new kt();
    //   knimeTable2.setDataTable(_representation.inObjects[1]);
    // }

    transformData();
    setColors();
    drawControls();
    drawChart();

    if (_value.options.subscribeSelection) {
      knimeService.subscribeToSelection(knimeTable1.getTableId(), selectionChanged);
    }

    // CHECK: What does this actually do?
    if (parent !==undefined && parent.KnimePageLoader !==undefined) {
      parent.KnimePageLoader.autoResize(window.frameElement.id);
    }
  };

  // Transform data from first port into a hierarchical structure suitable
  // for a partition layout.
  var transformData = function() {
    // Get indices for path columns and frequency column.
    function indexOf(column) {
      return knimeTable1.getColumnNames().indexOf(column);
    }
    var pathColumns = _representation.options.pathColumns.map(indexOf);
    var freqColumn = indexOf(_representation.options.freqColumn);

    // Get unique labels from path columns.
    function notNull(value) {
      return value !== null;
    }
    function accumulate(accumulator, array) {
      return accumulator.concat(array);
    }
    function onlyUnique(value, index, self) {
      return self.indexOf(value) === index;
    }
    uniqueLabels = knimeTable1.getPossibleValues()
      .filter(notNull)
      .reduce(accumulate, [])
      .filter(onlyUnique);

    // make sure that reserved names do not collide whith user given classes
    while (uniqueLabels.indexOf(rootNodeName) > -1) {
      rootNodeName += "_";
    }
    while (uniqueLabels.indexOf(nullNodeName) > -1) {
      nullNodeName += "_";
    }

    var id = 0;

    // Initialize _data object
    _data = {
      id: id++,
      name: rootNodeName,
      children: [],
      active: false,
      highlited: false,
      selected: false
    };

    // Create hierarchical structure.
    var rows = knimeTable1.getRows();
    for (var i = 0; i < rows.length; i++) {
      var size = rows[i].data[freqColumn];
      if (size === null || isNaN(size)) {
        size = 0;
      }

      // get array of path elements from current row
      var parts = pathColumns.map(function(col) { return rows[i].data[col]; });
      // Remove trailing nulls
      while(parts[parts.length-1] === null) {
        parts.pop();
      }

      // Loop over path elements,
      // append to hierarchical structure
      var currentNode = _data;
      for (var j = 0; j < parts.length; j++) {
        var children = currentNode["children"];
        if (parts[j] === null) {
          var nodeName = nullNodeName;
        } else {
          var nodeName = parts[j];
        }
        
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
              id: id++,
              name: nodeName,
              children: [],
              active: false,
              highlited: false,
              selected: false
            };
            children.push(childNode);
          }
          currentNode = childNode;
        } else {
          // Reached the end of the sequence; create a leaf node.
          childNode = {
            id: id++,
            name: nodeName,
            size: size,
            children: [],
            active: false,
            highlited: false,
            selected: false,
            rowKey: rows[i].rowKey
          };
          children.push(childNode);

          // Add id of leaf to [row -> leaf]-data-structure. 
          rowKey2leaf[rows[i].rowKey] = childNode;
        }
      }
    }
  };

  var setColors = function() {
    // TODO: handle second port: color model

    // Return a function that yields a color given a label/string.
    // if (knimeTable2 !== null) {
    //   // loop over rows of table2 to get all labels and corresponding colors
    //   _colorMap = {};
    //   var rowColors = knimeTable2.getRowColors();
    //   var rows = knimeTable2.getRows();
    //   for (var i = 0; i < rows.length; i++) {
    //     var name = rows[i].data[0];
    //     var color = rowColors[i];
    //     colorMap[name] = color;
    //   }
    // } else {
    //   // Create object with key=label, value=color.
    // }
    
    if (uniqueLabels.length <= 10) {
      var scale = d3.scale.category10();
    } else {
      var scale = d3.scale.category20();
    }

    var colorMap = {};
    uniqueLabels.forEach(function(label) { colorMap[label] = scale(label); })

    _colorMap = function(label) {
      if (label === rootNodeName || label === nullNodeName) {
        return "#FFFFFF";
      } else {
        return colorMap[label];
      }
    }
    _colorMap.entries = d3.entries(colorMap);
    _colorMap.keys = d3.keys(colorMap);
  };

  var drawControls = function() {
    if (!knimeService || !_representation.options.enableViewControls) {
		  // TODO: error handling?
		  return;
	  }

    if (_representation.options.displayFullscreenButton) {
      knimeService.allowFullscreen();
    }

   	knimeService.addNavSpacer();


    // Reset controlls.
    if (_representation.options.zoomable) {
      knimeService.addButton('zoom-reset-button', 'search-minus', 'Reset Zoom', function() {
        resetZoom();
      });
    }
    if (_representation.options.selection) {
      knimeService.addButton('selection-reset-button', 'minus-square-o', 'Reset Selection', function() {
        clearSelection();
      });
    }
    if (_representation.options.highliting) {
      knimeService.addButton('highlite-reset-button', 'star-o', 'Reset Highlite', function() {
        clearHighliting();
      });
    }

   	knimeService.addNavSpacer();

    if (_value.options.mouseMode == null) {
      _value.options.mouseMode = "highlite";
    }

    function toggleButton() {
      var targetID = "mouse-mode-" + _value.options.mouseMode;
      d3.selectAll("#knime-service-header .service-button")
        .classed("active", function() {
          return targetID == this.getAttribute("id");
        });
     }

    // mouse mode controlls.
    if (_representation.options.zoomable) {
      knimeService.addButton('mouse-mode-zoom', 'search', 'Mouse Mode "Zoom"', function() {
        _value.options.mouseMode = "zoom";
     	  toggleButton();
      });
    }
    if (_representation.options.selection) {
      knimeService.addButton('mouse-mode-select', 'check-square-o', 'Mouse Mode "Select"', function() {
        _value.options.mouseMode = "select";
     	  toggleButton();
      });
    }
    if (_representation.options.highliting) {
      knimeService.addButton('mouse-mode-highlite', 'star', 'Mouse Mode "Highlite"', function() {
        _value.options.mouseMode = "highlite";
     	  toggleButton();
      });
    }
    toggleButton();

    // Title / Subtitle configuration
    var titleEdit = _representation.options.enableTitleEdit;
    var subtitleEdit = _representation.options.enableSubtitleEdit;
  	if (titleEdit || subtitleEdit) {
  	  if (titleEdit) {
  	    var chartTitleText = knimeService.createMenuTextField(
  	        'chartTitleText', _value.options.title, function() {
  	      if (_value.options.title != this.value) {
  	        _value.options.title = this.value;
  	        updateTitles(true);
  	      }
  	    }, true);
  	    knimeService.addMenuItem('Chart Title:', 'header', chartTitleText);
  	  }
  	  if (subtitleEdit) {
  	    var chartSubtitleText = knimeService.createMenuTextField(
  	        'chartSubtitleText', _value.options.subtitle,
  	        function() {
  	        	if (_value.options.subtitle != this.value) {
  	        		_value.options.subtitle = this.value;
  	        		updateTitles(true);
  	        	}
  	        }, true);
  	    knimeService.addMenuItem('Chart Subtitle:', 'header', chartSubtitleText, null, knimeService.SMALL_ICON);
  	  }
  	}

    // Filter-small-nodes configuration
    var filterSmallNodesToggle = _representation.options.filterSmallNodesToggle;
    if (filterSmallNodesToggle) {
      knimeService.addMenuDivider();

      var filterSmallCheckbox = knimeService.createMenuCheckbox(
          'filterSmallNodesCheckbox', _value.options.filterSmallNodes, function() {
            _value.options.filterSmallNodes = this.checked;
            drawChart();
          });
      knimeService.addMenuItem('Filter out tiny nodes:', 'search', filterSmallCheckbox);
    }

    // Legend / Interactive Guideline / Grid configuration
    var legendToggle = _representation.options.legendToggle;
    var breadcrumbToggle = _representation.options.breadcrumbToggle;
    if (legendToggle || breadcrumbToggle) {
      knimeService.addMenuDivider();

      if (legendToggle) {
        var legendCheckbox = knimeService.createMenuCheckbox(
            'legendCheckbox', _value.options.legend,
            function() {
              _value.options.legend = this.checked;
              drawChart();
            });
        knimeService.addMenuItem('Legend:', 'info-circle', legendCheckbox);
      }

      if (breadcrumbToggle) {
        var breadcrumbCheckbox = knimeService.createMenuCheckbox(
                'breadcrumbCheckbox', _value.options.breadcrumb,
                function() {
                  _value.options.breadcrumb = this.checked;
                  drawChart();
                });

        knimeService.addMenuItem('Breadcrumb:', 'ellipsis-h', breadcrumbCheckbox);
      }
    }

    // Donut hole configuration
    var donutHoleToggle = _representation.options.donutHoleToggle;
    if (donutHoleToggle) {
      knimeService.addMenuDivider();

      var donutHoleCheckbox = knimeService.createMenuCheckbox(
          'donutHoleCheckbox', _value.options.donutHole, function() {
            _value.options.donutHole = this.checked;
            drawChart();
          });
      knimeService.addMenuItem('Donut hole:', 'search', donutHoleCheckbox);
    }

    // Inner label configuration
    var innerLabelToggle = _representation.options.innerLabelToggle;
    var innerLabelStyleSelect = _representation.options.innerLabelStyleSelect;
    var enableInnerLabelEdit = _representation.options.enableInnerLabelEdit;
    if (innerLabelToggle || innerLabelStyleSelect || enableInnerLabelEdit) {
      knimeService.addMenuDivider();

      if (innerLabelToggle) {
        var innerLabelCheckbox = knimeService.createMenuCheckbox(
            'innerLabelCheckbox', _value.options.innerLabel,
            function() {
              _value.options.innerLabel = this.checked;
              drawChart();
            });
        knimeService.addMenuItem('Inner Label:', 'dot-circle-o', innerLabelCheckbox);
      }

      if (innerLabelStyleSelect) {
        var innerLabelStyleSelector =
          knimeService.createMenuSelect('innerLabelStyleSelector', _value.options.innerLabelStyle, innerLabelStyles, function() {
            _value.options.innerLabelStyle = this.options[this.selectedIndex].value;
            drawChart();
          });
        knimeService.addMenuItem('Inner Label Style:', 'percent', innerLabelStyleSelector);
      }

      if (enableInnerLabelEdit) {
  	    var innerLabelText = knimeService.createMenuTextField(
  	        'innerLabelText', _value.options.innerLabelText, function() {
    	        _value.options.innerLabelText = this.value;
    	        drawChart();
  	        }, true);
  	    knimeService.addMenuItem('Inner Label Text:', 'header', innerLabelText);
  	  }
    }

    if (knimeService.isInteractivityAvailable()) {
      // Selection / Filter configuration
      var publishSelectionToggle = _representation.options.publishSelectionToggle;
      var subscribeSelectionToggle = _representation.options.subscribeSelectionToggle;
      var subscribeFilterToggle = _representation.options.subscribeFilterToggle;
      if (publishSelectionToggle || subscribeSelectionToggle || subscribeFilterToggle) {
          knimeService.addMenuDivider();

          if (publishSelectionToggle) {
              var pubSelIcon = knimeService.createStackedIcon('check-square-o', 'angle-right', 'faded left sm', 'right bold');
              var pubSelCheckbox = knimeService.createMenuCheckbox('publishSelectionCheckbox', _value.options.publishSelection, function() {
                  if (this.checked) {
                      _value.options.publishSelection = true;
                  } else {
                      _value.options.publishSelection = false;
                  }
              });

              knimeService.addMenuItem('Publish selection', pubSelIcon, pubSelCheckbox);
          }

          if (subscribeSelectionToggle) {
              var subSelIcon = knimeService.createStackedIcon('check-square-o', 'angle-double-right', 'faded right sm', 'left bold');
              var subSelCheckbox = knimeService.createMenuCheckbox('subscribeSelectionCheckbox', _value.options.subscribeSelection, function() {
                  if (this.checked) {
                      _value.options.subscribeSelection = true;
                      knimeService.subscribeToSelection(knimeTable1.getTableId(), selectionChanged);
                  } else {
                      _value.options.subscribeSelection = false;
                      knimeService.unsubscribeSelection(knimeTable1.getTableId(), selectionChanged);
                  }
              });
              knimeService.addMenuItem('Subscribe to selection', subSelIcon, subSelCheckbox);
          }

          if (subscribeFilterToggle) {
              var subFilIcon = knimeService.createStackedIcon('filter', 'angle-double-right', 'faded right sm', 'left bold');
              var subFilCheckbox = knimeService.createMenuCheckbox('subscribeFilterCheckbox', _value.options.subscribeFilter, function() {
                  if (this.checked) {
                      _value.options.subscribeSelection = true;
                      // TODO
                      // knimeService.subscribeToFilter(_representation.keyedDataset.id, filterChanged, _representation.subscriptionFilterIds);
                  } else {
                      _value.options.subscribeSelection = true;
                      // TODO
                      // knimeService.unsubscribeFilter(_representation.keyedDataset.id, filterChanged);
                  }
              });
              knimeService.addMenuItem('Subscribe to filter', subFilIcon, subFilCheckbox);
              // TODO
              // if (_value.subscribeFilter) {
              //     knimeService.subscribeToFilter(_representation.keyedDataset.id, filterChanged, _representation.subscriptionFilterIds);
              // }
          }
      }
    }

    // if (!_representation.options.multi && _representation.options.enableColumnSelection) {
    //   var colSelectDiv = controlsContainer.append("div");
    //   colSelectDiv.append("label").attr("for", "colSelect").text("Selected column: ");
    //   var select = colSelectDiv.append("select").attr("id", "colSelect");
    //   for (var i = 0; i < _representation.options.columns.length; i++) {
    //     var txt = _representation.options.columns[i];
    //     var o = select.append("option").text(txt).attr("value", txt);
    //     if (txt === _value.options.numCol) {
    //       o.property("selected", true);
    //     }
    //   }
    //   select.on("change", function() {
    //     _value.options.numCol = select.property("value");
    //     drawChart();
    //   });
    // }
  }

  // TODO: in general case do not redraw!
  var updateTitles = function(updateChart) {
    d3.select("#title").text(this.value);
    d3.select("#subtitle").text(_value.options.subtitle);

    if (updateChart) {
       drawChart();
    }
  }

  // Draws the chart
  var drawChart = function() {
    // Remove earlier chart.
    d3.select("#layoutContainer").remove();

    /*
     * Parse some options.
     */
    var optFullscreen = _representation.options.svg.fullscreen && _representation.runningInView;
    var isTitle = _value.options.title !== "" || _value.options.subtitle !== "";

    d3.selectAll("html, body")
      .style({
        "width": "100%",
        "height": "100%",
        "margin": "0",
        "padding": "0"
      });

    var body = d3.select("body");

    // Determine available witdh and height.
    // CHECK
    if (optFullscreen) {
      var width = "100%";

      if (isTitle || !_representation.options.enableViewControls) {
        knimeService.floatingHeader(true);
        var height = "100%";
      } else {
        knimeService.floatingHeader(false);
        var height = "calc(100% - " + knimeService.headerHeight() + "px)"
      }

    } else {
      var width = _representation.options.svg.width + 'px';
      var height = _representation.options.svg.height + 'px';
    }

    layoutContainer = body.append("div")
      .attr("id", "layoutContainer")
      .style({
        "width": width,
        "height": height,
        "min-width": MIN_WIDTH + "px",
        "min-height": MIN_HEIGHT + "px",
        "position": "absolute"
      });

    // create div container to hold svg
    var svgContainer = layoutContainer.append("div")
      .attr("id", "svgContainer")
      .style({
        "min-width": MIN_WIDTH + "px",
        "min-height": MIN_HEIGHT + "px",
        "box-sizing": "border-box",
        "overflow": "hidden",
        "margin": "0",
        "width": "100%",
        "height": "100%"
      });
  
    // Create the SVG object
    svg = svgContainer.append("svg")
      .attr("id", "svg")
      .style("font-family", "sans-serif")

    // set width / height of svg
    if (optFullscreen) {
      // CHECK: Do I really need computedHeight/computedWidth ?
      var boundingRect = svgContainer.node().getBoundingClientRect();
      var svgWidth = boundingRect.width;
      var svgHeight = boundingRect.height;
    } else {
      var svgWidth = _representation.options.svg.width;
      var svgHeight = _representation.options.svg.height;
    }
    svg
      .style("width", svgWidth + "px")
      .style("height", svgHeight + "px")
      .attr("width", svgWidth)
      .attr("height", svgHeight);

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


    // Compute plotting options
    var margin = {
      top : isTitle ? 60 : 10,
      left : 10,
      bottom : 10,
      right : 10
    };

    var plottingSurface = svg.append("g")
      .attr("id", "plottingSurface")
      .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    var w = Math.max(50, svgWidth - margin.left - margin.right);
    var h = Math.max(50, svgHeight - margin.top - margin.bottom);

    var options = {
      legend: _value.options.legend,
      breadcrumb: _value.options.breadcrumb,
      zoomable: _representation.options.zoomable,
      aggregationType: _value.options.aggregationType,
      filterSmallNodes: _value.options.filterSmallNodes
    };

    drawSunburst(_data, plottingSurface, w, h, options);

    // Set resize handler
    if (optFullscreen) {
      var win = document.defaultView || document.parentWindow;
      win.onresize = resize;
    }
  }

  var drawSunburst = function(data, plottingSurface, width, height, options) {
    var marginTop = options.breadcrumb ? 40 : 0;
    var marginLeft = options.legend ? 85 : 0;

    // Dimensions of sunburst.
    var radius = Math.min(width - marginLeft, height - marginTop) / 2;

    // Breadcrumb dimensions: width, height, spacing, width of tip/tail.
    var b = { w: 100, h: 30, s: 3, t: 10 };

    var x = d3.scale.linear()
        .range([0, 2 * Math.PI]);

    var y = d3.scale.sqrt()
        .range([0, radius]);

    var partition = d3.layout.partition()
        //.sort(null)
        .value(
          _representation.options.freqColumn == null
          ? function(d) { return 1; }
          : function(d) { return d.size; }
        )

    // Create list of segment objects with cartesian orientation from data.
    if (options.filterSmallNodes) {
      // For efficiency, filter nodes to keep only those large enough to see.
      nodes = partition.nodes(data)
          .filter(function(d) {
            return (d.dx > _representation.options.filteringThreshold);
          });
    } else {
      nodes = partition.nodes(data);
    }

    // Functions to map cartesian orientation of partition layout into radial
    // orientation of sunburst chart.
    var arc = d3.svg.arc()
        .startAngle(function(d) { return Math.max(0, Math.min(2 * Math.PI, x(d.x))); })
        .endAngle(function(d) { return Math.max(0, Math.min(2 * Math.PI, x(d.x + d.dx))); })

    // Set display of donut hole depending on donut-hole-configuration and zoom-configuration.
    if (_value.options.donutHole) {
      arc
        .innerRadius(function(d) { return Math.max(0, y(d.y)); })
        .outerRadius(function(d) { return Math.max(0, y(d.y + d.dy)); });
    } else {
      var rootSegmentExtent = nodes[0].dy;
      arc
        .innerRadius(function(d) { 
          var notZoomed = !(_representation.options.zoomable && _value.options.zoomNode!=null)
          return Math.max(0, y(d.y - notZoomed * rootSegmentExtent));
        })
        .outerRadius(function(d) {
          var notZoomed = !(_representation.options.zoomable && _value.options.zoomNode!=null)
          return Math.max(0, y(d.y + d.dy - notZoomed * rootSegmentExtent));
        });
    }

    // create new group for the sunburst plot (not legend, not breadcrumb)
    var sunburstGroup = plottingSurface.append("g")
        .attr("transform", "translate(" + ((width - marginLeft) / 2) + "," + ((height + marginTop) / 2) + ")")
        .attr("id", "sunburstGroup");

    // Bounding circle underneath the sunburst, to make it easier to detect
    // when the mouse leaves the plottingSurface g.
    sunburstGroup.append("svg:circle")
        .attr("r", radius)
        .style("opacity", 0);

    var path = sunburstGroup.selectAll("path")
        .data(nodes)
      .enter().append("path")
        .attr("d", arc)
        .attr("fill-rule", "evenodd")
        .attr("fill", function(d) { return _colorMap(d.name); })
        .attr("stroke",function(d) { return d.selected ? "black" : "white" })
        .on("mouseover", mouseover)
        .on("click", click);

    // Basic setup of page elements.
    if (options.breadcrumb) {
      initializeBreadcrumbTrail(plottingSurface);
    }

    if (options.legend) {
      drawLegend(plottingSurface);
    }

    if (_value.options.zoomNode && _representation.options.zoomable) {
      path.transition()
        .attrTween("d", arcTweenZoom(_value.options.zoomNode));
    }

    // Add the mouseleave handler to the bounding circle.
    sunburstGroup.on("mouseleave", mouseleave);

    // Get total size of the tree = value of root node from partition.
    totalSize = path.node().__data__.value;

    // add explanation in the middle of the circle
    // if (!options.zoomable ) {
    // TODO: add other condition
    if (true) {
      var explanation = sunburstGroup.append("g")
          .attr("id", "explanation");
      explanation.append("text")
        .attr("id", "percentage")
        .attr("text-anchor", "middle")
        .attr("alignment-baseline", "middle");
      explanation.append("text")
        .attr("id", "explanationText")
        .attr("text-anchor", "middle")
        .attr("alignment-baseline", "middle")
        .attr("y", 30);
    }

    function click(d) {
      if (_value.options.mouseMode == "zoom") {
        zoom(d);
      } else if (_value.options.mouseMode == "select"){
        select(d);
      } else {
        highlite(d);
      }
    }

    function mouseover(d) {
      if (_value.options.mouseMode == "highlite") {
        setPropAllNodes('active', false);
        setPropsBackward(d, 'active', true);
        sunburstGroup.selectAll("path")
          .style("opacity", function(d) { return (d.active || d.highlited) ? 1 : 0.3; });

        // TODO
        drawBreadcrumb(d);
        drawInnerLabel(d);
      }
    }
    
    function mouseleave(d) {
      if (_value.options.mouseMode == "highlite") {

      setPropAllNodes('active', true);
      sunburstGroup.selectAll("path")
        .style("opacity", function(d) { return ((highlitedNode == null) || d.highlited) ? 1 : 0.3; });

      // TODO: breadcrumb // innerlabel

      }
    }

    function highlite(d) {
      highlitedNode = d.id;
      setPropAllNodes('highlited', false);
      setPropsBackward(d, 'highlited', true);
      sunburstGroup.selectAll("path")
        .style("opacity", function(d) { return (d.active || d.highlited) ? 1 : 0.3; });

      // TODO
      drawBreadcrumb(d);
      drawInnerLabel(d);
    }

    // Restore everything to full opacity when moving off the visualization.
    clearHighliting = function(d) {
      highlitedNode = null;
      setPropAllNodes('highlited', false);

      // Hide the breadcrumb trail
      // d3.select("#trail")
      //     .style("visibility", "hidden");

      sunburstGroup.selectAll("path")
        .style("opacity", 1);

      // d3.select("#explanation")
      //     .style("visibility", "hidden");
    }

    function renderSelection() {
      sunburstGroup.selectAll("path")
        .attr("stroke-width", function(d) {
          return d.selected ? 2 : 1;
        })
        .attr("stroke",function(d) {
          return d.selected ? "black" : "white"
        })
      };

    function select(node) {
      if (d3.event.shiftKey) {
        if (node.selected) {
          // Remove elements from selection.
          setPropsBackward(node, "selected", false);
          var leafs = setPropsForward(node, "selected", false);
          var rowKeys = leafs.map(function(leaf) { return leaf.rowKey; });
          for (var i = 0; i < rowKeys.length; i++) {
            var index = selectedRows.indexOf(rowKeys[i]);
            if (index > -1) {
              selectedRows.splice(index, 1);
            }
          }

          if (_value.options.publishSelection) {
            knimeService.removeRowsFromSelection(knimeTable1.getTableId(), rowKeys, selectionChanged);
          }
        } else {
          // Add element to selection.
          var leafs = setPropsForward(node, 'selected', true);
          addNodeToSelectionBackward(node);
          var rowKeys = leafs.map(function(leaf) { return leaf.rowKey; });
          for (var i = 0; i < rowKeys.length; i++) {
            var index = selectedRows.indexOf(rowKeys[i]);
            if (index == -1) {
              selectedRows.push(rowKeys[i]);
            }
          }

          if (_value.options.publishSelection) {
            knimeService.addRowsToSelection(knimeTable1.getTableId(), rowKeys, selectionChanged);
          }
        }
      } else {
        // Set selection.
        setPropAllNodes('selected', false);
        var leafs = setPropsForward(node, 'selected', true);
        addNodeToSelectionBackward(node);
        var rowKeys =  leafs.map(function(leaf) { return leaf.rowKey; });

        if (_value.options.publishSelection) {
          knimeService.setSelectedRows(knimeTable1.getTableId(), rowKeys, selectionChanged);
        }
      }
      renderSelection();
    }

    clearSelection = function() {
      setPropAllNodes('selected', false);
      renderSelection();
      selectedRows = [];
      knimeService.setSelectedRows(knimeTable1.getTableId(), [], selectionChanged);
    }

    selectionChanged = function(data) {
      if (data.changeSet) {
        if (data.changeSet.removed) {
          for (var i = 0; i < data.changeSet.removed.length; i++) {
            var removedKey = data.changeSet.removed[i];
            removeSelectedRow(removedKey)
            var index = selectedRows.indexOf(removedKey);
            if (index > -1) {
              selectedRows.splice(index, 1);
            }           
          }
        }
        if (data.changeSet.added) {
          for (var i = 0; i < data.changeSet.added.length; i++) {
            var addedKey = data.changeSet.added[i];
            var leaf = rowKey2leaf[addedKey];
            addNodeToSelectionBackward(leaf);
            var index = selectedRows.indexOf(addedKey);
            if (index == -1) {
              selectedRows.push(addedKey);
            }           
          }
        }
      } else if (data.reevaluate) {
        selectedRows = knimeService.getAllRowsForSelection(knimeTable1.getTableId());
        setPropAllNodes("selected", false);
        for (var i = 0; i < selectedRows.length; i++) {
          var leaf = rowKey2leaf[rowKey];
          addNodeToSelectionBackward(leaf);
        }
      }
      renderSelection();
    };

    var addNodeToSelectionBackward = function(node) {
      node.selected = true;
      var parent = node.parent;
      while (parent != null) {
        var allChildrenSelected = parent.children.every(function(child) { return child.selected; });
        if (allChildrenSelected) {
          parent.selected = true;
        } else {
          break;
        }
        parent = parent.parent;
      }
    }

    var removeSelectedRow = function(rowKey) {
      var parent = rowKey2leaf[rowKey];
      while (parent != null) {
        parent.selected = false;
        parent = parent.parent;
      }
    }

    var zoom = function(d) {
      path.transition()
        .duration(1000)
        .attrTween("d", arcTweenZoom(d));

      if (d.name === rootNodeName) {
        delete _value.options.zoomNode;
      } else {
        _value.options.zoomNode = {
          x: d.x,
          y: d.y,
          dx: d.dx,
          dy: d.dy
        };
      }
    }

    resetZoom = function() {
      zoom(_data);
    }

    // When zooming: interpolate the scales.
    function arcTweenZoom(d) {
      var xd = d3.interpolate(x.domain(), [d.x, d.x + d.dx]),
          yd = d3.interpolate(y.domain(), [d.y, 1]),
          yr = d3.interpolate(y.range(), [d.y ? 20 : 0, radius]);
      return function(d, i) {
        return i
            ? function(t) { return arc(d); }
            : function(t) { x.domain(xd(t)); y.domain(yd(t)).range(yr(t)); return arc(d); };
      };
    }

    // TODO: argument should be list
    function drawInnerLabel(d) {
      if (_value.options.innerLabelStyle === "percentage") {
        var statistic = (100 * d.value / totalSize).toPrecision(3);
        var statisticString = statistic + "%";
        if (statistic < 0.1) {
          statisticString = "< 0.1%";
        }
      } else {
        var statistic = d.value;
        var statisticString = d3.format(".6s")(statistic);
      }

      if (_value.options.innerLabel && _value.options.donutHole) {
        d3.select("#explanation")
          .style("visibility", "visible");

        d3.select("#percentage")
            .text(statisticString);

        d3.select("#explanationText")
            .text(_value.options.innerLabelText);
      }
    }

    function hideInnerLabel() {
      // TODO
    }

    function drawBreadcrumb(d) {
      // TODO

      // Breadcrumb
      // var sequenceArray = getAncestors(d);
      // updateBreadcrumbs(sequenceArray, statisticString);

      // for (var i = 0; i < d.tableRows; i++) {
      //   var rowId = d.tableRows[i];
      //   rowId2leafId[rowId]
      // }
      // d.tableRows

      // Sunburst segments
      // Fade all the segments.
      // d3.selectAll("path")
      //     .style("opacity", 0.3);

      // debugger;

      // // Then highlight only those that are an ancestor of the current segment.
      // sunburstGroup.selectAll("path")
      //     .filter(function(node) {
      //               return (sequenceArray.indexOf(node.id) >= 0);
      //             })
      //     .style("opacity", 1);

      // TODO: what was this for?
      // return sequenceArray;

      // Fade all but the current sequence, and show it in the breadcrumb trail.
    }

    function hideBreadcrumb() {
      // TODO
    }

    function setPropsForward(start, prop, val) {
      var stack = [start];
      var leafs = [];

      while (stack.length > 0) {
        start = stack.pop();
        if (prop != null && val != null) {
          start[prop] = val;
        }

        if (start.children.length === 0) {
          leafs.push(start);
        } else {
          for (var i = 0; i < start.children.length; i++) {
            stack.push(start.children[i]);
          }
        }
      }

      return leafs;
    }

    function setPropsBackward(start, prop, val) {
      while (start) {
        start[prop] = val;
        start = start.parent;
      }
    }

    function setPropAllNodes(prop, val) {
      for (var i = 0; i < nodes.length; i++) {
        nodes[i][prop] = val;
      }
    }

    // Given a node in a partition layout, return an array of all of its ancestor
    // nodes, highest first, but excluding the root.
    function getAncestors(node) {
      var path = [];
      var current = node;
      while (current.parent) {
        path.unshift(current.id);
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
    function updateBreadcrumbs(nodeArray, statisticString) {

      // Data join; key function combines name and depth (= position in sequence).
      var g = d3.select("#trail")
          .selectAll("g")
          .data(nodeArray, function(d) { return d.name + d.depth; });

      // Add breadcrumb and label for entering nodes.
      var entering = g.enter().append("svg:g");

      entering.append("svg:polygon")
          .attr("points", breadcrumbPoints)
          .attr("fill", function(d) { return _colorMap(d.name); })
          .attr("stroke", function(d) { return d.name === nullNodeName ? "black" : "none"; });

      entering.append("svg:text")
          .attr("x", (b.w + b.t) / 2)
          .attr("y", b.h / 2)
          .attr("width", b.w)
          .attr("dy", "0.35em")
          .attr("text-anchor", "middle")
          .text(function(d) { return d.name; })
          .each(wrap);

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
          .text(statisticString)

      // Make the breadcrumb trail visible, if it's hidden.
      d3.select("#trail")
          .style("visibility", "");

    }

    // TODO: sort legend
    function drawLegend(plottingSurface) {

      // Dimensions of legend item: width, height, spacing.
      var li = {
        w: 100, h: 15, s: 6, r: 6
      };

      var legend = plottingSurface.append("g")
          .attr("width", li.w)
          .attr("height", _colorMap.keys.length * (li.h + li.s))
          .attr("transform", "translate(" + (width - li.w) + ", 0)");

      var g = legend.selectAll("g")
          .data(_colorMap.entries)
          .enter().append("svg:g")
          .attr("transform", function(d, i) {
                  return "translate(0," + i * (li.h + li.s) + ")";
               });


      g.append("svg:circle")
          .attr("cx", 0)
          .attr("cy", 0.5 * (li.h - li.r))
          .attr("r", li.r)
          .style("fill", function(d) { return d.value; });
      // g.append("svg:rect")
      //     .attr("rx", li.r)
      //     .attr("ry", li.r)
      //     .attr("width", li.w)
      //     .attr("height", li.h)
      //     .style("fill", function(d) { return d.value; });

      g.append("svg:text")
          .attr("x", li.r + 5)
          .attr("y", li.r)
          .attr("width", li.w)
          .attr("dy", "0.35em")
          .text(function(d) { return d.key; })
          .each(wrap);
    }

    function toggleLegend() {
      var legend = d3.select("#legend");
      if (legend.style("visibility") == "hidden") {
        legend.style("visibility", "");
      } else {
        legend.style("visibility", "hidden");
      }
    }

    // Wrap text if too long.
    function wrap() {
      var self = d3.select(this),
        textLength = self.node().getComputedTextLength(),
        text = self.text(),
        width = self.attr("width");
      while (textLength+5 > width && text.length > 0) {
        text = text.slice(0, -1);
        self.text(text + '...');
        textLength = self.node().getComputedTextLength();
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
    drawChart();
  }

  view.validate = function() {
    return true;
  };

  view.getComponentValue = function() {
    return _value;
  };

  return view;

}());
