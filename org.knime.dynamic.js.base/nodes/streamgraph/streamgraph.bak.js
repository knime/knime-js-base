(streamgraph_namespace = function() {

  var view = {};
  var _representation, _value;
  var _data = {};
  var _colorRange;
  var layoutContainer;
  var MIN_HEIGHT = 300, MIN_WIDTH = 400;
  var chart;

  view.init = function(representation, value) {
    _representation = representation;
    _value = value;

    // create Knime table from data
    var knimeTable1 = new kt();
    knimeTable1.setDataTable(_representation.inObjects[0]);
    var columnKeys = _representation.options.columns;

    // Load data from port 2 into knime table (optional, used for colors).
    var knimeTable2 = null;
    if (_representation.inObjects[1] !== null) {
      knimeTable2 = new kt();
      knimeTable2.setDataTable(_representation.inObjects[1]);
    }

    // Check if there is data in first table.
    if (columnKeys.length === 0 || knimeTable1.getNumRows() === 0) {
      alert("No data given.");
      return;
    }

    // Check if table2 has right number of rows.
    if (knimeTable2 !== null &&
        knimeTable1.getNumColumns() !== knimeTable2.getNumRows()) {

      alert("Number of objects is not equal between data port one and two.");
    }

    _colorRange = getColors(knimeTable1, knimeTable2, columnKeys, _representation.options["customColors"]);
    _data = transformData(knimeTable1, columnKeys);

    drawChart(false);
  }

  function drawChart(redraw) {
    // Parse the options

    var optTitle = _value.options["title"];
    var optSubtitle = _value.options["subtitle"];

    var interpolation = _representation.options["interpolation"];
    var customColors = _representation.options["customColors"];
    var enableLegend = _representation.options["legend"];
    var enableInteractiveGuideline = _representation.options["interactiveGuideline"];
    var optShowControls = _representation.options["enableViewControls"];
    var runningInView = _representation.runningInView;
    var optFullscreen = _representation.options["svg"]["fullscreen"] && runningInView;
    var optWidth = _representation.options["svg"]["width"];
    var optHeight = _representation.options["svg"]["height"];


    var body = d3.select("body");

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
      layoutContainer.style("min-height", (MIN_HEIGHT + controlHeight)
          + "px");
      if (optFullscreen) {
        layoutContainer.style("height", "calc(100% - " + controlHeight + "px)");
      }
    } else {
      controlHeight = 0;
    }

    // create div container to hold svg
    var svgContainer = layoutContainer.append("div")
      .attr("id", "svgContainer")
      .style("width", "100%")
      .style("height", "calc(100% - " + controlHeight + "px)")
      .style("box-sizing", "border-box")
      .style("overflow", "hidden")

    // Create the SVG object
    var svg = svgContainer.append("svg").attr("id", "svg");
    svg.style("font-family", "sans-serif");

    // set svg dimensions (apache batik needs width/height to be declared)
    if (!runningInView) {
      svg.attr("width", optWidth);
      svg.attr("height", optHeight);
    }

    // create the streamgraph
    nv.addGraph(function() {
      chart = nv.models.stackedAreaChart()
        .x(function(d) { return d[0]; })
        .y(function(d) { return d[1]; })
        .color(_colorRange)
        .interpolate(interpolation)
        .style("stream")
        .showControls(false)
        .margin({"top": 10,"bottom": 10})
        .showLegend(Boolean(enableLegend));

      // using if-clause because there is strange behaviour otherwise
      if (enableInteractiveGuideline) {
        chart.useInteractiveGuideline(true);
      } else {
        chart.interactive(false);
      }

      //Format x-axis labels.
      chart.yAxis
          .tickFormat(d3.format(',.2f'));

      updateTitles(false);

      svg.datum(_data).call(chart);

      nv.utils.windowResize(chart.update);

      return chart;
    });
  }

  transformData = function(knimeTable1, columnKeys) {
    // transform the tabular format into a JSON format
    var data = [];
    for (var i = 0; i < columnKeys.length; i++) {
      var columnKey = columnKeys[i];
      var columnIndex = knimeTable1.getColumnNames().indexOf(columnKey);

      data.push({
        "key": columnKey,
        "values": knimeTable1.getColumn(columnIndex).map(function(d, i) {
                    return [i, d];
                  })
      });
    }

    return data;
  };

  getColors = function(knimeTable1, knimeTable2, columnKeys, customColors) {
    // Set color scale: custom or default.
    var colorScale = [];
    if (customColors && knimeTable2 !== null) {
      var rowColors = knimeTable2.getRowColors();

      var numColumns = columnKeys.length;
      for (var i = 0; i < numColumns; i++) {
        var columnName = columnKeys[i]
        var rowIndex = knimeTable1.getColumnNames().indexOf(columnName);
        var color = rowColors[rowIndex];

        if (!color) {
          color = "#7C7C7C";
        }
        colorScale.push(color);
      }
    } else {
      colorScale = d3.scale.category10();
      if (columnKeys.length > 10) {
        colorScale = d3.scale.category20();
      }
    }

    return customColors ? colorScale : colorScale.range();
  }

  function updateTitles(updateChart) {
    if (chart) {
      var svg = d3.select("#svg");

      var curTitle = d3.select("#title");
      var curSubtitle = d3.select("#subtitle");
      var chartNeedsUpdating = curTitle.empty() != !(_value.options.title)
        || curSubtitle.empty() != !(_value.options.subtitle);
      if (!_value.options.title) {
        curTitle.remove();
      }
      if (_value.options.title) {
        if (curTitle.empty()) {
          svg.append("text")
            .attr("x", 20)
            .attr("y", 30)
            .attr("font-size", 24)
            .attr("id", "title")
            .text(_value.options.title);
        } else {
          curTitle.text(_value.options.title);
        }
      }
      if (!_value.options.subtitle) {
        curSubtitle.remove();
      }
      if (_value.options.subtitle) {
        if (curSubtitle.empty()) {
          svg.append("text")
            .attr("x", 20)
            .attr("y", _value.options.title ? 46 : 20)
            .attr("font-size", 12)
            .attr("id", "subtitle")
            .text(_value.options.subtitle);
        } else {
          curSubtitle.text(_value.options.subtitle)
          .attr("y", _value.options.title ? 46 : 20);
        }
      }

      var topMargin = 10;
      topMargin += _value.options.title ? 10 : 0;
      topMargin += _representation.options["legend"] ? 0 : 30;
      topMargin += _value.options.subtitle ? 8 : 0;
      chart.legend.margin({top: topMargin, bottom: topMargin});
      chart.margin({top: topMargin, bottom: topMargin});
      if (updateChart && chartNeedsUpdating) {
        chart.update();
      }
    }
  }

  function createControls(controlsContainer) {
    var titleEdit = _representation.options.enableTitleEdit;
    var subtitleEdit = _representation.options.enableSubtitleEdit;

    if (_representation.options.enableViewControls) {

      var controlTable = controlsContainer.append("table")
        .attr("id", "streamControls")
        .style("padding", "10px")
        .style("margin", "0 auto")
        .style("box-sizing", "border-box")
        .style("font-family", "sans-serif")
        .style("font-size", "12px")
        .style("border-spacing", 0)
        .style("border-collapse", "collapse");

      if (titleEdit || subtitleEdit) {
        var titleEditContainer = controlTable.append("tr");
          if (titleEdit) {
            titleEditContainer.append("td").append("label").attr("for", "chartTitleText").text("Chart Title:").style("margin", "0 5px");
            var chartTitleText = titleEditContainer.append("td").append("input")
              .attr("type", "text")
              .attr("id", "chartTitleText")
              .attr("name", "chartTitleText")
              .attr("value", _value.options.title)
              .style("font-family", "sans-serif")
              .style("font-size", "12px")
              .style("width", "150px")
              .style("margin-right", "15px")
              .on("keyup", function() {
                if (_value.options.title != this.value) {
                  _value.options.title = this.value;
                  updateTitles(true);
                }
            });
          }
          if (subtitleEdit) {
            titleEditContainer.append("td").append("label").attr("for", "chartSubtitleText").text("Chart Subtitle:").style("margin", "0 5px");
            var chartSubtitleText = titleEditContainer.append("td").append("input")
              .attr("type", "text")
              .attr("id", "chartSubtitleText")
              .attr("name", "chartSubtitleText")
              .attr("value", _value.options.subtitle)
              .style("font-family", "sans-serif")
              .style("font-size", "12px")
              .style("width", "150px")
              .style("margin-right", "15px")
              .on("keyup", function() {
                if (_value.options.subtitle != this.value) {
                  _value.options.subtitle = this.value;
                  updateTitles(true);
                }
            });
          }
      }

      if (d3.selectAll("#controlContainer table *").empty()) {
        controlContainer.remove();
      }
    }
  }

  view.validate = function() {
    return true;
  }

  view.getComponentValue = function() {
    return _value;
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
        d3.selectAll(rule.selectorText).each(function(){
          for (var k = 0; k < rule.style.length; k++) {
            var curStyle = this.style.getPropertyValue(rule.style[k]);
            var curPrio = this.style.getPropertyPriority(rule.style[k]);
            var rulePrio = rule.style.getPropertyPriority(rule.style[k]);
            //only overwrite style if not set or priority is overruled
            if (!curStyle || (curPrio != "important" && rulePrio == "important")) {
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

  return view;

}());
