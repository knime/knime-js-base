window.parallelcoords_namespace = (function () {
    var extraRows, getExtents, applyFilter, filterChanged, saveSelected, selectRows, drawSavedBrushes, createXAxis,
        drawBrushes, brushstart, getLine, drawElements, position, refreshView, brush, noBrushes, saveSelectedRows,
        saveSettingsToValue, containMissing, clearBrushes, checkClearSelectionButton, drawChart, createControls,
        getDataColumnID, createData, publishCurrentSelection, selectionChanged, mzd, w, h, plotG, bottomBar, scales,
        escapeId, scaleCols, extents, _data, layoutContainer, _representation, _value, line, colors, oldHeight,
        oldWidth, ordinalScale, xBrushScale, xBrush, xExtent, legendWidth, maxLeftLabelWidth, firstColumn;

    var MIN_HEIGHT = 100;
    var MIN_WIDTH = 100;
    var MISSING_VALUE_MODE = 'Show\u00A0missing\u00A0values';

    var leftLabelsMaxPercentage = 0.33;
    var pcPlot = {};

    var brushes = {};
    var draggingNow = false;
    var dragging = {};
    var rowsSelected = false;
    var sortedCols = [];
    var filterIds = [];
    var currentFilter = null;

    var knimeTable;

    pcPlot.init = function (representation, value) {
        _value = value;
        _representation = representation;

        knimeTable = new kt();
        knimeTable.setDataTable(_representation.inObjects[0]);

        d3.select('html').style('width', '100%').style('height', '100%');
        d3.select('body').style('width', '100%').style('height', '100%');

        var body = d3.select('body');

        _data = createData(representation);
        // initially included columns
        sortedCols = _data.colNames;

        layoutContainer = body.append('div')
            .attr('id', 'layoutContainer')
            .attr('class', 'knime-layout-container')
            .style('min-width', MIN_WIDTH + 'px');

        if (_representation.options.svg.fullscreen && _representation.runningInView) {
            layoutContainer.style('width', '100%').style('height', '100%');
        } else {
            layoutContainer.style('width', _representation.options.svg.width + 'px')
                .style('height', _representation.options.svg.height + 'px');
        }

        createControls();

        var div = layoutContainer.append('div')
            .attr('id', 'svgContainer')
            .attr('class', 'knime-svg-container')
            .style('min-width', MIN_WIDTH + 'px')
            .style('min-height', MIN_HEIGHT + 'px');

        var svg1 = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
        div[0][0].appendChild(svg1);

        var d3svg = d3.select('svg');
        d3svg.append('rect').attr('id', 'bgr').attr('fill', _representation.options.backgroundColor);

        var plotG = d3svg.append('g').attr('id', 'plotG');
        plotG.append('rect').attr('id', 'da').attr('fill', _representation.options.daColor);

        d3svg.append('text')
            .attr('id', 'title')
            .attr('class', 'knime-title')
            .attr('x', 20)
            .attr('y', 30)
            .text(_value.options.title);

        d3svg.append('text')
            .attr('id', 'subtitle')
            .attr('class', 'knime-subtitle')
            .attr('x', 20)
            .attr('y', 46)
            .text(_value.options.subtitle);

        plotG.append('line')
            .attr('stroke', 'rgba(0,0,0,0.5)')
            .attr('stroke-width', '2')
            .attr('id', 'yMarker')
            .attr('visibility', 'hidden');

        drawChart();

        // draw saved brushes
        if (_representation.options.enableSelection && _representation.options.enableBrushing) {
            drawSavedBrushes();
        }
        // select saved (selected) rows
        if (_representation.options.enableSelection && _representation.options.enableBrushing && !_value.options.selections) {
            selectRows();
        }
        if (_representation.options.enableSelection && _value.options.selectedrows) {
            selectRows();
        }
        checkClearSelectionButton();
        saveSelected();
    };

    function sortArray(sorted, toInclude) {
        var array = [];
        for (var i = 0; i < sorted.length; i++) {
            for (var j = 0; j < toInclude.length; j++) {
                if (sorted[i] == toInclude[j]) {
                    array.push(sorted[i]);
                }
            }
        }
        return array;
    }

    function filterColumns(cols) {
        var includedColumns = [];
        for (var col = 0; col < cols.length; col++) {
            var idx = getDataColumnID(cols[col], _representation.inObjects[0]);
            if (_representation.inObjects[0].spec.colTypes[idx] === 'string' || _representation.inObjects[0].spec.colTypes[idx] === 'number' ||
                _representation.inObjects[0].spec.colTypes[idx] === 'dateTime') {
                includedColumns.push(cols[col]);
            }
        }
        return includedColumns;
    }

    createData = function (representation) {
        var data = { objects: [], colNames: [], colTypes: {}, domains: {}, minmax: {} };
        var table = representation.inObjects[0];
        var key, val, col;

        filterIds = [];
        for (var i = 0; i < table.spec.filterIds.length; i++) {
            if (table.spec.filterIds[i]) {
                filterIds.push(table.spec.filterIds[i]);
            }
        }
        if (filterIds.length < 1) {
            filterIds = null;
        }

        var catColIdx = getDataColumnID(_representation.options.catCol, table);
        var indices = {};

        var columnNames;
        if (_representation.options.enableAxesSwapping && _value.options.sortedCols &&
            _value.options.sortedCols.length > 0 && !_value.options.columns) {
            columnNames = _value.options.sortedCols;
        }
        if (_representation.options.enableAxesSwapping && _value.options.sortedCols &&
            _value.options.sortedCols.length > 0 && _value.options.columns) {
            if (_value.options.sortedCols.length < _value.options.columns.length) {
                columnNames = sortArray(sortedCols, _value.options.columns);
            } else {
                columnNames = sortArray(_value.options.sortedCols, _value.options.columns);
                // sortedCols[_value.options.sortedCols.length] = _value.options.sortedCols;
            }
            /* if (_value.options.sortedCols.length == _data.colNames.length){
                sortedCols =_value.options.sortedCols;
            } */
        } else {
            columnNames = filterColumns(_value.options.columns);
        }


        for (col = 0; col < columnNames.length; col++) {
            var columnName;
            columnName = columnNames[col];
            data.colNames.push(columnName);
            var idx = getDataColumnID(columnName, table);
            indices[columnName] = idx;
            data.colTypes[columnName] = table.spec.colTypes[idx];
            if (table.spec.colTypes[idx] === 'string') {
                data.domains[columnName] = d3.set();
            } else {
                data.minmax[columnName] = [Number.POSITIVE_INFINITY, Number.NEGATIVE_INFINITY];
            }
        }

        if (catColIdx != null) {
            data.domains[_representation.options.catCol] = d3.set();
        }
        for (var r = 0; r < table.rows.length; r++) {
            var row = table.rows[r].data;
            var obj = {};
            for (col = 0; col < _value.options.columns.length; col++) {
                obj[_value.options.columns[col]] = row[indices[_value.options.columns[col]]];
                if (obj[_value.options.columns[col]] === null) {
                    obj.containsMissing = true;
                }

            }
            if (_representation.options.useColors) {
                obj.color = table.spec.rowColorValues[r];
            } else if (catColIdx) {
                obj.color = row[catColIdx];
            }

            for (key in data.domains) {
                val = row[indices[key]];
                if (val != null) {
                    data.domains[key].add(val);
                }
            }
            for (key in data.minmax) {
                val = row[indices[key]];
                if (val != null) {
                    data.minmax[key][0] = Math.min(data.minmax[key][0], val);
                    data.minmax[key][1] = Math.max(data.minmax[key][1], val);
                }
            }
            obj.id = table.rows[r].rowKey;
            data.objects.push(obj);
        }

        return data;
    };

    getDataColumnID = function (columnName, table) {
        var colID = null;
        for (var i = 0; i < table.spec.numColumns; i++) {
            if (table.spec.colNames[i] === columnName) {
                colID = i;
                break;
            }

        }

        return colID;
    };

    escapeId = function (str) {
        // html5 can handle any type of id character, but d3 v3 can not
        var string = 'knid_' + str;
        return string.replace(/^[^a-z]+|[^\w:-]+/gi, '_______');
    };

    createControls = function () {

        // -- Buttons --
        if (_representation.options.displayFullscreenButton) {
            knimeService.allowFullscreen();
        }

        if (_representation.options.displayClearSelectionButton && _representation.options.enableSelection) {
            knimeService.addButton('clearSelectionButton', 'minus-square-o', 'Clear selection', function () {
                d3.selectAll('.row').classed({ selected: false, 'knime-selected': false, unselected: false });
                clearBrushes();
                publishCurrentSelection();
            });
            d3.select('#clearSelectionButton').classed('inactive', true);
        }

        // -- Initial interactivity settings --
        if (knimeService.isInteractivityAvailable()) {
            if (_representation.options.enableSelection && _value.options.subscribeSelection) {
                knimeService.subscribeToSelection(_representation.inObjects[0].id, selectionChanged);
            }
            if (filterIds && _value.options.subscribeFilter) {
                knimeService.subscribeToFilter(_representation.inObjects[0].id, filterChanged, filterIds);
            }
        }

        // -- Menu Items --
        if (!_representation.options.enableViewControls) {
            return;
        }

        if (_representation.options.enableTitleEdit) {
            var plotTitleText = knimeService.createMenuTextField('plotTitleText', _value.options.title, function () {
                var hadTitles = _value.options.title.length > 0 || _value.options.subtitle.length > 0;
                _value.options.title = this.value;
                var hasTitles = _value.options.title.length > 0 || _value.options.subtitle.length > 0;
                d3.select('#title').text(this.value);
                if (hasTitles != hadTitles) {
                    drawChart();
                    applyFilter();
                }
            }, true);
            knimeService.addMenuItem('Plot Title:', 'header', plotTitleText);
        }


        if (_representation.options.enableSubtitleEdit) {
            var plotSubtitleText = knimeService.createMenuTextField('plotSubtitleText', _value.options.subtitle, function () {
                var hadTitles = _value.options.title.length > 0 || _value.options.subtitle.length > 0;
                _value.options.subtitle = this.value;
                var hasTitles = _value.options.title.length > 0 || _value.options.subtitle.length > 0;
                d3.select('#subtitle').text(this.value);
                if (hasTitles != hadTitles) {
                    drawChart();
                    applyFilter();
                }
            }, true);
            knimeService.addMenuItem('Plot Subtitle:', 'header', plotSubtitleText, null, knimeService.SMALL_ICON);
        }
        if (_representation.options.enableTitleEdit || _representation.options.enableSubtitleEdit ||
            _representation.options.enableMValuesHandling && containMissing()) {
            knimeService.addMenuDivider();
        }
        if (_representation.options.enableMValuesHandling && containMissing()) {


            var missingMenuSelect = knimeService.createMenuSelect('missingMenuSelect', 'Skip\u00A0rows\u00A0with\u00A0missing\u00A0values', ['Skip\u00A0rows\u00A0with\u00A0missing\u00A0values', 'Skip\u00A0missing\u00A0values', MISSING_VALUE_MODE], function () {
                _value.options.mValues = this.value;
                if (this.value == 'Skip\u00A0rows\u00A0with\u00A0missing\u00A0values') {
                    if (_representation.options.enableSelection && _representation.options.enableBrushing &&
                        noBrushes() && !d3.selectAll('.row.selected').empty()) {
                        saveSelectedRows();
                    }
                    if (_representation.options.enableSelection && !_representation.options.enableBrushing &&
                        !d3.selectAll('.row.selected').empty()) {
                        saveSelectedRows();
                    }
                    if (_representation.options.enableSelection && _representation.options.enableBrushing && brushes && !rowsSelected) {
                        getExtents();
                    }

                    drawChart();
                    if (_representation.options.enableSelection && _representation.options.enableBrushing && brushes && !rowsSelected) {
                        drawBrushes();
                        brush();
                    }

                    applyFilter();
                }
                if (this.value == 'Skip\u00A0missing\u00A0values') {
                    if (_representation.options.enableSelection && _representation.options.enableBrushing &&
                        noBrushes() && !d3.selectAll('.row.selected').empty()) {
                        saveSelectedRows();
                    }
                    if (_representation.options.enableSelection && !_representation.options.enableBrushing &&
                        !d3.selectAll('.row.selected').empty()) {
                        saveSelectedRows();
                    }
                    if (_representation.options.enableSelection && _representation.options.enableBrushing && brushes && !rowsSelected) {
                        getExtents();
                    }

                    drawChart();
                    if (_representation.options.enableSelection && _representation.options.enableBrushing && brushes && !rowsSelected) {
                        drawBrushes();
                        brush();
                    }

                    extraRows();
                    applyFilter();
                }
                if (this.value == MISSING_VALUE_MODE) {
                    if (_representation.options.enableSelection && _representation.options.enableBrushing &&
                        noBrushes() && !d3.selectAll('.row.selected').empty()) {
                        saveSelectedRows();
                    }

                    if (_representation.options.enableSelection && !_representation.options.enableBrushing &&
                        !d3.selectAll('.row.selected').empty()) {
                        saveSelectedRows();
                    }

                    if (_representation.options.enableSelection && _representation.options.enableBrushing && brushes && !rowsSelected) {
                        getExtents();
                    }

                    drawChart();
                    if (_representation.options.enableSelection && _representation.options.enableBrushing && brushes && !rowsSelected) {
                        drawBrushes();
                        brush();
                    }

                    extraRows();
                    applyFilter();
                }
            });
            knimeService.addMenuItem('Missing values:', 'braille', missingMenuSelect);
        }

        if ((_representation.options.enableTitleEdit || _representation.options.enableSubtitleEdit) &&
            _representation.options.enableMValuesHandling && containMissing()) {
            knimeService.addMenuDivider();
        }

        if (_representation.options.enableLineChange) {
            var lineTypeRadio = knimeService.createInlineMenuRadioButtons('lineType', 'lineType',
                _value.options.lType, ['Straight', 'Curved'], function () {
                    _value.options.lType = this.value;
                    if (_representation.options.enableSelection && _representation.options.enableBrushing &&
                        noBrushes() && !d3.selectAll('.row.selected').empty()) {
                        saveSelectedRows();
                    }
                    if (_representation.options.enableSelection && !_representation.options.enableBrushing &&
                        !d3.selectAll('.row.selected').empty()) {
                        saveSelectedRows();
                    }
                    if (_representation.options.enableSelection && _representation.options.enableBrushing && brushes && !rowsSelected) {
                        getExtents();
                    }

                    drawChart();
                    if (_representation.options.enableSelection && _representation.options.enableBrushing && brushes && !rowsSelected) {
                        drawBrushes();
                        brush();
                    }

                    applyFilter();
                });
            knimeService.addMenuItem('Line type:', 'bars', lineTypeRadio);

            var lineThicknessSpin = knimeService.createMenuNumberField('lineThickness', _value.options.lThickness, 0.1, 100, 0.1, function () {
                _value.options.lThickness = Number(this.value);
                d3.selectAll('.row').attr('stroke-width', this.value);
            }, true);
            knimeService.addMenuItem('Line thickness:', 'minus', lineThicknessSpin);
        }

        // temporarily use controlContainer to solve th resizing problem with ySelect
        if (_representation.options.enableColumnSelection) {
            var layoutContainer = 'layoutContainer';
            var containerID = 'plotContainer';
            var controlContainer = d3.select('#' + layoutContainer).insert('table', '#' + containerID + ' ~ *')
                .attr('id', 'plotControls')
                .style('width', '100%')
                .style('padding', '10px')
                .style('margin', '0 auto')
                .style('box-sizing', 'border-box')
                .style('border-spacing', 0)
                .style('border-collapse', 'collapse');

            var columnChangeContainer = controlContainer.append('tr');
            var ySelect = new twinlistMultipleSelections();
            var ySelectComponent = ySelect.getComponent().get(0);
            columnChangeContainer.append('td').attr('colspan', '3').node().appendChild(ySelectComponent);
            ySelect.setChoices(filterColumns(_value.options.columns));
            ySelect.setSelections(filterColumns(_value.options.columns));
            ySelect.addValueChangedListener(function () {
                _value.options.columns = ySelect.getSelections();
                saveSettingsToValue();
                _data = createData(_representation);
                drawChart();
                applyFilter();
                if (_representation.options.enableSelection && _representation.options.enableBrushing) {
                    drawSavedBrushes();
                }
                // select saved (selected) rows
                if (_representation.options.enableSelection && _representation.options.enableBrushing && !_value.options.selections) {
                    selectRows();
                }
                if (_representation.options.enableSelection && _value.options.selectedrows) {
                    selectRows();
                }
            });

            knimeService.addMenuItem('Axes:', 'long-arrow-up', ySelectComponent);
            ySelectComponent.style.margin = '0';
            ySelectComponent.style.outlineOffset = '-3px';
            ySelectComponent.style.width = '';
            ySelectComponent.style.height = '';
            controlContainer.remove();
        }

        if (_representation.options.enableTitleEdit || _representation.options.enableSubtitleEdit ||
            _representation.options.enableMValuesHandling && containMissing() || _representation.options.enableLineChange &&
            knimeService.isInteractivityAvailable() && _representation.options.enableSelection) {
            knimeService.addMenuDivider();
        }

        if (knimeService.isInteractivityAvailable()) {
            if (_representation.options.enableSelection) {
                var pubSelIcon = knimeService.createStackedIcon('check-square-o', 'angle-right', 'faded left sm', 'right bold');
                var pubSelCheckbox = knimeService.createMenuCheckbox('publishSelectionCheckbox', _value.options.publishSelection, function () {
                    if (this.checked) {
                        _value.options.publishSelection = true;
                        publishCurrentSelection();
                    } else {
                        _value.publishSelection = false;
                    }
                });
                knimeService.addMenuItem('Publish selection', pubSelIcon, pubSelCheckbox);
                var subSelIcon = knimeService.createStackedIcon('check-square-o', 'angle-double-right', 'faded right sm', 'left bold');
                var subSelCheckbox = knimeService.createMenuCheckbox('subscribeSelectionCheckbox', _value.options.subscribeSelection, function () {
                    if (this.checked) {
                        knimeService.subscribeToSelection(_representation.inObjects[0].id, selectionChanged);
                    } else {
                        knimeService.unsubscribeSelection(_representation.inObjects[0].id, selectionChanged);
                    }
                });
                knimeService.addMenuItem('Subscribe to selection', subSelIcon, subSelCheckbox);
            }
        }

        if (filterIds) { // .length > 0
            if (_representation.enableSelection) {
                knimeService.addMenuDivider();
            }
            var subFilIcon = knimeService.createStackedIcon('filter', 'angle-double-right', 'faded right sm', 'left bold');
            var subFilCheckbox = knimeService.createMenuCheckbox('subscribeFilterCheckbox', _value.options.subscribeFilter, function () {
                if (this.checked) {
                    knimeService.subscribeToFilter(_representation.inObjects[0].id, filterChanged, filterIds);
                } else {
                    knimeService.unsubscribeFilter(_representation.inObjects[0].id, filterChanged);
                }
            });
            knimeService.addMenuItem('Subscribe to filter', subFilIcon, subFilCheckbox);
        }
    };

    filterChanged = function (data) {
        currentFilter = data;
        applyFilter(true);
    };

    applyFilter = function (clear) {
        if (currentFilter) {
            d3.selectAll('.row').each(function (d) {
                var included = knimeTable.isRowIncludedInFilter(d.id, currentFilter);
                d3.select(this).classed('filtered', !included);
            });
            if (clear) {
                clearBrushes();
            }
        }

    };


    publishCurrentSelection = function () {
        if (knimeService && knimeService.isInteractivityAvailable() && _value.options.publishSelection) {
            var selArray = [];
            // set to true selected
            d3.selectAll('.row').filter('.selected').each(function (row) {
                selArray.push(row.id);
            });
            knimeService.setSelectedRows(_representation.inObjects[0].id, selArray, selectionChanged);
        }
        checkClearSelectionButton();
    };

    checkClearSelectionButton = function () {
        var button = d3.select('#clearSelectionButton');
        if (!button.empty()) {
            button.classed('inactive', function () { return d3.select('.row.selected').empty(); });
        }
    };

    selectionChanged = function (data) {
        var i, row;
        clearBrushes();
        if (data.changeSet) {
            if (data.changeSet.removed) {
                for (i = 0; i < data.changeSet.removed.length; i++) {
                    var removedId = data.changeSet.removed[i];
                    row = d3.select('#' + escapeId(removedId));
                    if (!row.empty() && !row.classed('filtered')) {
                        row.classed({ unselected: true, selected: false, 'knime-selected': false });
                    }
                }
                if (d3.selectAll('.selected').empty()) {
                    d3.selectAll('.row').classed('unselected', false);
                    rowsSelected = false;
                }
            }
            if (data.changeSet.added) {
                for (i = 0; i < data.changeSet.added.length; i++) {
                    var addedId = data.changeSet.added[i];
                    row = d3.select('#' + escapeId(addedId));
                    if (!row.empty() && !row.classed('filtered')) {
                        if (d3.selectAll('.selected').empty()) {
                            d3.selectAll('.row').classed('unselected', true);
                        }
                        row.classed({ selected: true, 'knime-selected': true, unselected: false });
                        rowsSelected = true;
                    }
                }
            }
            saveSelectedRows();
        }
        checkClearSelectionButton();
    };

    var drawLegend = function (d3svg, mTop) {
        d3.select('.legend').remove();
        legendWidth = 0;
        if (_representation.options.catCol && _representation.options.showLegend && !_representation.options.useColors) {
            var legendG = d3svg.append('g').attr('class', 'legend knime-legend');
            var catValues = _data.domains[_representation.options.catCol].values();
            for (var i = 0; i < catValues.length; i++) {
                var cat = catValues[i];
                var txt = legendG.append('text')
                    .attr('class', 'knime-legend-label')
                    .attr('x', 20)
                    .attr('y', i * 23)
                    .text(cat);
                legendWidth = Math.max(legendWidth, txt.node().getComputedTextLength());
                legendG.append('circle')
                    .attr('class', 'knime-legend-symbol')
                    .attr('cx', 5)
                    .attr('cy', i * 23 - 4)
                    .attr('r', 5)
                    .attr('fill', colors(cat));
            }
            var svgWidth = parseInt(d3svg.style('width'), 10);
            legendWidth += 35;
            legendWidth = Math.min(svgWidth / 2, legendWidth);
            legendG.attr('transform', 'translate(' + (svgWidth - legendWidth) + ',' + (mTop + 20) + ')');
        }
    };

    var makeScales = function (d3svg) {
        scales = {};

        for (var c = 0; c < _data.colNames.length; c++) {
            var colName = _data.colNames[c];
            var scale;
            if (_data.colTypes[colName] === 'number' || _data.colTypes[colName] === 'dateTime') {
                scale = d3.scale.linear().range([h, 0]).domain(_data.minmax[colName]).nice();
            } else {
                // sort domain alphabetically, needs to be reverse to fit the axis top to bottom (AP-10540)
                var colDomain = _data.domains[colName].values().sort(function (val1, val2) {
                    return val2.localeCompare(val1);
                });
                scale = d3.scale.ordinal().domain(colDomain).rangePoints([h, 0], 1.0);
            }
            scales[colName] = scale;

            if (c === 0) { // measure label widths of leftmost axis
                var labels = scale.domain();
                maxLeftLabelWidth = knimeService.measureAndTruncate(labels, {
                    container: d3svg.node(),
                    tempContainerClasses: 'axis knime-axis knime-y',
                    classes: 'knime-tick-label',
                    maxWidth: d3svg.node().getBoundingClientRect().width * leftLabelsMaxPercentage
                }).max.maxWidth;
            }
        }

    };


    drawChart = function () {

        var transition;
        var naturalChartWidth = Math.max(MIN_WIDTH, _representation.options.svg.width);
        var naturalChartHeight = Math.max(MIN_HEIGHT, _representation.options.svg.height);
        var chartWidth = naturalChartWidth + 'px';
        var chartHeight = naturalChartHeight + 'px';

        if (_representation.options.svg.fullscreen && _representation.runningInView) {
            chartWidth = '100%';
            chartHeight = '100%';
        }

        d3.select('#svgContainer')
            .style('height', chartHeight)
            .style('width', chartWidth);

        var d3svg = d3.select('svg').attr({ width: naturalChartWidth, height: naturalChartHeight }).style({ width: chartWidth, height: chartHeight });


        colors = _representation.options.catCol
            ? d3.scale.category10().domain(_data.domains[_representation.options.catCol].values())
            : null;

        var mTop = _value.options.subtitle || _value.options.title ? 80 : 30;

        drawLegend(d3svg, mTop);

        var bottomMargin = (_value.options.mValues == MISSING_VALUE_MODE && containMissing()) ? 60 : 30;

        var margin = { top: mTop, bottom: bottomMargin, right: 10 + legendWidth };

        h = Math.max(50, parseInt(d3svg.style('height'), 10) - margin.top - margin.bottom);

        makeScales(d3svg);

        margin.left = Math.max(40,  maxLeftLabelWidth);
        
        plotG = d3svg.select('#plotG')
            .attr('transform', 'translate(' + margin.left + ',' + margin.top + ')');

        var svgWidth = parseInt(d3svg.style('width'), 10);
        w = Math.max(50, svgWidth - margin.left - margin.right);

        plotG.select('#da').attr({ y: -10, width: w, height: h + 45 });
        d3svg.select('#bgr').attr({ width: w + margin.left + margin.right, height: h + margin.top + margin.bottom });

        scaleCols = d3.scale.ordinal().domain(_data.colNames).rangePoints([0, w], 0.5);

        mzd = _data.objects;

        plotG.selectAll('text, path, .axis, .xAxis').remove();

        // create an additional axis for the missing values selection
        if (_representation.options.enableMValuesHandling &&
                _representation.options.enableViewControls &&
                _representation.runningInView && _value.options.mValues == MISSING_VALUE_MODE &&
                _representation.options.enableSelection &&
                _representation.options.enableBrushing && containMissing()) {
            createXAxis();
        }


        var g;
        var axisPositions = [];
        g = plotG.selectAll('g.axis')
            .data(_data.colNames, function (d) { return d; })
            .enter().append('g').attr('class', 'axis knime-axis knime-y')
            .attr('transform', function (d) {
                axisPositions.push(scaleCols(d));
                return 'translate(' + scaleCols(d) + ',0)';
            });
        
        // calculate distances of first and second axis as other distances are the same
        var axisDistance = axisPositions[0] && axisPositions[1] ? axisPositions[1] - axisPositions[0] : 0;
        var axisLabelsBuffer = 15;
        
        var svgNS = 'http://www.w3.org/2000/svg';

        plotG.selectAll('g.axis')
            .each(function (d) {
                var scale = scales[d];
                var isFirstCol = d === Object.keys(scales)[0];
                var labels = scale.domain();
                
                var measuredLabels = knimeService.measureAndTruncate(labels, {
                    container: d3svg.node(),
                    tempContainerClasses: 'axis knime-axis knime-y',
                    classes: 'knime-tick-label',
                    maxWidth: isFirstCol ? maxLeftLabelWidth : axisDistance - axisLabelsBuffer
                });

                var axis = d3.svg.axis()
                    .scale(scale)
                    .tickFormat(function (d) {
                        var label = measuredLabels.values.filter(function (value) {
                            return value && value.originalData === d;
                        })[0];
                        var title = document.createElementNS(svgNS, 'title');
                        title.innerHTML = d;
                        this.parentNode.appendChild(title);
                        return label && label.truncated ? label.truncated : d;
                    })
                    .orient('left');
                d3.select(this).call(axis);
            })
            .each(function (d, i) {
                d3.select(this).append('text').datum(_data.colNames[i])
                    .attr('class', 'label knime-axis-label').attr('text-anchor', 'middle')
                    .attr('transform', function (d) { return 'translate(0, -15)'; })// h + 40
                    .attr('text-anchor', 'middle')
                    .text(function (d) { 
                        // Axis labels are positioned in the middle of an axis,
                        // so they as well can take up the space of the distances of axis 
                        var measuredLabels = knimeService.measureAndTruncate([d], {
                            container: d3svg.node(),
                            tempContainerClasses: 'axis knime-axis knime-y',
                            classes: 'label knime-axis-label',
                            maxWidth: axisDistance - axisLabelsBuffer
                        });
                        var label = measuredLabels.values[0];
                        var title = document.createElementNS(svgNS, 'title');
                        title.innerHTML = d;
                        this.parentNode.appendChild(title);
                        return label && label.truncated ? label.truncated : d;
                     });
            });

        d3.selectAll('.domain')
            .classed('knime-axis-line', true);
        var ticks = d3.selectAll('.tick')
            .classed('knime-tick', true);
        ticks.selectAll('line')
            .classed('knime-tick-line', true);
        ticks.selectAll('text')
            .classed('knime-tick-label', true);

        if (_representation.options.enableAxesSwapping) {
            g.call(d3.behavior.drag()
                .origin(function (d) {
                    return { x: scaleCols(d) };
                })
                .on('dragstart', function (d) {
                    firstColumn = _data.colNames[0];
                    dragging[d] = scaleCols(d);
                    draggingNow = true;
                })
                .on('drag', function (d) {
                    if (draggingNow) {
                        dragging[d] = Math.min(w, Math.max(0, d3.event.x));
                        _data.colNames.sort(function (a, b) { return position(a) - position(b); });
                        scaleCols.domain(_data.colNames);
                        d3.selectAll('.row').attr('d', getLine);
                        g.attr('transform', function (d) { return 'translate(' + position(d) + ',0)'; });
                    }
                })
                .on('dragend', function (d) {
                    delete dragging[d];
                    draggingNow = false;
                    if (_data.colNames[0] !== firstColumn) {
                        // if the leftmost column has changed, we redraw the whole diagram because the labels might
                        // need different spacing on the left
                        refreshView();
                        return;
                    }
                    transition(d3.select(this)).attr('transform', 'translate(' + scaleCols(d) + ')');
                    transition(d3.selectAll('.row')).attr('d', getLine);
                    if (_value.options.mValues == MISSING_VALUE_MODE && containMissing()) {
                        if (!xBrush.empty()) {
                            xBrush.extent(xBrush.extent());
                            d3.select('.xBrush').call(xBrush);
                            brush();
                        }
                    }

                }));
            d3.selectAll('.label').style('cursor', 'move');
            if (_data.colNames.length == sortedCols.length) {
                sortedCols = _data.colNames;
            }


        }

        d3.selectAll('.axis path').attr('stroke-width', 1).attr('stroke', 'black').attr('fill', 'none');

        // brush - rows selection
        if (_representation.options.enableSelection && _representation.options.enableBrushing) {
            g.append('g')
                .attr('class', 'brush')
                .each(function (d, i) {
                    d3.select(this).call(brushes[d] = d3.svg.brush().y(scales[d]).on('brush', brush)
                        .on('brushend', publishCurrentSelection).on('brushstart', brushstart));
                    d3.select(this).attr('id', escapeId(i));
                })
                .selectAll('rect')
                .attr('x', -8)
                .attr('width', 16)
                .attr('fill-opacity', '0.2')
                .attr('stroke', '#fff')
                .attr('shape-rendering', 'crispEdges');
        }


        transition = function (g) {
            return g.transition().duration(500);
        };

        // representation.options.enableViewControls
        // && _representation.runningInView_

        if (_representation.options.enableMValuesHandling && containMissing()) {
            bottomBar = _value.options.mValues == MISSING_VALUE_MODE;
        }

        if (bottomBar) {
            plotG.append('text')
                .attr('id', 'missingVtitle')
                .attr('class', 'knime-label')
                .attr('x', -30)
                .attr('y', h + 38)
                .text('Miss.values');
        }


        line = d3.svg.line()
            .x(function (d, i) {
                return position(_data.colNames[i]);
            })
            .y(function (d, i) {
                if (bottomBar && d === null) {
                    return h + 40;
                } else if (d === null) {
                    return h;
                }
                return scales[_data.colNames[i]](d);
            });

        // Skipping missing cells
        if (_representation.options.enableMValuesHandling &&
            _representation.options.enableViewControls &&
            _representation.runningInView) {
            if (_value.options.mValues == 'Skip\u00A0missing\u00A0values') {
                line.defined(function (d) {
                    return d != null;
                });
            }

        }


        if (_representation.options.enableMValuesHandling &&
            _representation.options.enableViewControls &&
            _representation.runningInView) {
            if (_value.options.mValues == 'Skip\u00A0rows\u00A0with\u00A0missing\u00A0values') {
                mzd = mzd.filter(function (d) {
                    return !d.containsMissing;
                });
            }

        }


        // Curved lines
        if (_representation.options.enableLineChange &&
            _representation.options.enableViewControls &&
            _representation.runningInView) {
            if (_value.options.lType == 'Curved') {
                line.interpolate('monotone');
            }

        }


        plotG.selectAll('path.row').each(function (d, i) {
            d3.select(this).datum(mzd[i]);
        });

        drawElements(mzd);

        if (_representation.options.svg.fullscreen) {
            var win = document.defaultView || document.parentWindow;
            win.onresize = refreshView;
        }

    };

    getLine = function (dp) {
        return line(_data.colNames.map(function (col) {
            return dp[col];
        }));
    };

    drawElements = function (data) {
        var rows = plotG.selectAll('path.row').data(data).enter()
            .insert('path', '.axis').attr('class', 'row')
            .attr('id', function (d) { return escapeId(d.id); })
            .attr('d', getLine)
            .attr('stroke', function (d) {
                if (_representation.options.useColors) {
                    return d.color;
                } else if (_representation.options.catCol) {
                    return colors(d[_representation.options.catCol]);
                } else {
                    return 'black';
                }
            })
            .attr('stroke-width', function () {
                if (_representation.options.enableLineChange) {
                    return _value.options.lThickness;
                } else {
                    return 1;
                }
            })
            .attr('stroke-opacity', 0.9)
            .attr('fill', 'none');

        if (_representation.options.enableSelection) {
            rows.on('click', function (d, i) {
                // eslint-disable-next-line no-negated-condition
                if (!d3.event.shiftKey) {
                    d3.selectAll('.selected').classed({ selected: false, 'knime-selected': false });
                    d3.selectAll('.row').classed('unselected', true);
                    d3.select(this).classed({ selected: true, 'knime-selected': true, unselected: false });
                    rowsSelected = true;
                    if (knimeService && knimeService.isInteractivityAvailable() && _value.options.publishSelection) {
                        knimeService.setSelectedRows(_representation.inObjects[0].id, [this.getAttribute('id')], selectionChanged);
                    }
                } else {
                    var selected = d3.select(this).classed('selected');
                    d3.select(this)
                        .classed({ selected: !selected, 'knime-selected': !selected, unselected: selected });
                    if (selected && d3.selectAll('.selected').empty()) {
                        rowsSelected = false;
                    }
                    if (!selected && d3.selectAll('.selected').empty()) {
                        rowsSelected = false;
                    }
                    if (!selected && !d3.selectAll('.selected').empty()) {
                        rowsSelected = true;
                    }
                    if (knimeService && knimeService.isInteractivityAvailable() && _value.options.publishSelection) {
                        if (selected) {
                            knimeService.removeRowsFromSelection(_representation.inObjects[0].id, [this.getAttribute('id')], selectionChanged);
                        } else {
                            knimeService.addRowsToSelection(_representation.inObjects[0].id, [this.getAttribute('id')], selectionChanged);
                        }
                    }
                    if (d3.selectAll('.selected').empty()) {
                        d3.selectAll('.row').classed('unselected', false);
                        d3.selectAll('.row').datum(function (d) {
                            delete d.selected;
                            return d;
                        });
                        rowsSelected = false;
                    }
                }

                clearBrushes();
                checkClearSelectionButton();
                d3.event.stopPropagation();
            }).on('mouseover', function (d, i) {
                var selected = d3.select(this).classed('selected'); // returns true if selected
                selected &= d3.event.shiftKey;
                d3.select(this).classed({ addSelection: !selected, removeSelection: selected });
            }).on('mouseout', function (d, i) {
                d3.selectAll('.rows').classed({ addSelection: false, removeSelection: false });
            });
        }


        // select previously selected rows
        var selected = false;
        for (var i = 0; i < data.length; i++) {
            selected = selected || (data[i].selected == true);
        }

        if (selected) {
            rows.classed('selected', function (d) {
                return d.selected;
            });
            rows.classed('knime-selected', function (d) {
                return d.selected;
            });

            rows.classed('unselected', function (d) {
                return d.selected == false;
            });
        }

    };

    position = function (d) {
        var v = dragging[d];
        return v == null ? scaleCols(d) : v;
    };

    pcPlot.getSVG = function () {
        var svgElement = d3.select('svg')[0][0];
        knimeService.inlineSvgStyles(svgElement);
        // Return the SVG as a string.
        return (new XMLSerializer()).serializeToString(svgElement);
    };

    createXAxis = function () {
        var xAxis = d3.svg.axis().scale(scaleCols).tickSize(5).orient('bottom');
        var gx = plotG.append('g').attr('class', 'xAxis knime-axis knime-x')
            .attr('transform', function (d) { return 'translate(0,' + (h + 40) + ')'; })
            .attr('stroke', 'transparent')
            .call(xAxis);

        gx.append('g')
            .attr('class', 'xBrush')
            .call(xBrush = d3.svg.brush().x(scaleCols).on('brush', brush).on('brushend', publishCurrentSelection)
                .on('brushstart', function () { rowsSelected = false; }))
            .selectAll('rect')
            .attr('y', -8)
            .attr('height', 16)
            .attr('fill-opacity', '0.2')
            .attr('stroke', '#fff')
            .attr('shape-rendering', 'crispEdges');

        var dataBg = plotG.select('#da');
        dataBg.attr({ height: Number(dataBg.attr('height')) + 30 });
    };

    clearBrushes = function () {
        if (_representation.options.enableSelection && _representation.options.enableBrushing && brushes) {
            d3.selectAll('.brush').each(function (d, i) {
                d3.select(this).call(brushes[_data.colNames[i]].clear());
                if (extents) {
                    if (d3.entries(extents).length > 0) {
                        extents = {};
                    }
                }
            });

            if (_representation.options.enableSelection && _representation.options.enableBrushing &&
                _value.options.mValues == MISSING_VALUE_MODE && xBrush && containMissing()) {
                d3.select('.xBrush').call(xBrush.clear());
                if (xExtent) {
                    xExtent = [];
                }
            }

        }

    };

    brush = function (axis, start, end, par) {
        par = par || false;
        extents = _data.colNames.map(function (p) { return brushes[p].extent(); });
        var missingSelected = xBrush && !xBrush.empty() && _value.options.mValues == MISSING_VALUE_MODE;
        var xExtent;
        if (xBrush) {
            xExtent = xBrush.extent();
        }
        var nothingSelected = true;
        for (var i = 0; i < _data.colNames.length; i++) {
            nothingSelected &= brushes[_data.colNames[i]].empty();
        }

        if (_value.options.mValues == MISSING_VALUE_MODE && xBrush && containMissing()) {
            nothingSelected &= xBrush.empty();
        }
        if (nothingSelected) {
            d3.selectAll('.row').classed({ selected: false, 'knime-selected': false, unselected: false });
            return;
        }

        d3.selectAll('.row').each(function (dp) {
            var row = d3.select(this);
            if (row.classed('filtered')) {
                return;
            }

            var selected = _data.colNames.every(function (p, i) {
                var extentEmpty = brushes[p].empty();
                if (xBrush) {
                    if (extentEmpty && !missingSelected) {
                        return true;
                    }

                } else if (extentEmpty) {
                    return true;
                }
                var missValueSelected = false;
                if (missingSelected) {
                    var xScale = scaleCols(_data.colNames[i]);
                    if (par) {
                        missValueSelected = xBrushScale(xExtent[0]) <= xScale && xScale <= xBrushScale(xExtent[1]);
                    } else {
                        missValueSelected = xExtent[0] <= xScale && xScale <= xExtent[1];
                    }

                    if (extentEmpty && !missValueSelected) {
                        return true;
                    }

                }

                if (dp[p] == null) {
                    return missValueSelected;
                }

                if (extentEmpty) {
                    return false;
                }

                if (_data.colTypes[p] == 'string') {
                    if (par) {
                        return ordinalScale(extents[i][0]) <= scales[p](dp[p]) && scales[p](dp[p]) <= ordinalScale(extents[i][1]);
                    } else {
                        return extents[i][0] <= scales[p](dp[p]) && scales[p](dp[p]) <= extents[i][1];
                    }
                } else if (_data.colTypes[p] == 'number') {
                    return extents[i][0] <= dp[p] && dp[p] <= extents[i][1];
                }

            });
            row.classed({ selected: selected, 'knime-selected': selected, unselected: !selected });
        });
    };

    getExtents = function () {
        extents = {};
        d3.entries(brushes).forEach(function (brush) {
            if (!brush.value.empty()) {
                extents[brush.key] = brush.value.extent();
            }

        });
        xExtent = [];
        if (_value.options.mValues == MISSING_VALUE_MODE && xBrush && containMissing()) {
            if (!xBrush.empty()) {
                xExtent = xBrush.extent();
            }

        }

    };

    drawSavedBrushes = function () {
        if (_value.options.selections) {
            var yScale;
            if (d3.entries(_data.domains).length > 0) {
                yScale = d3.scale.linear().domain([_value.options.oldHeight, 0]).range([h, 0]);
            }

            d3.keys(brushes).forEach(function (b) {
                if (_value.options.selections.extents[b]) {
                    if (_data.colTypes[b] == 'string' && _value.options.oldHeight) {
                        brushes[b].extent([yScale(_value.options.selections.extents[b][0]), yScale(_value.options.selections.extents[b][1])]);
                    } else {
                        brushes[b].extent(_value.options.selections.extents[b]);
                    }
                }

            });
            d3.selectAll('.brush').each(function (d) {
                d3.select(this).call(brushes[d]);
            });
            // draw xBrush
            if (_value.options.selections.xBrush) {
                if (_value.options.oldWidth) {
                    var xScale = d3.scale.linear().domain([0, _value.options.oldWidth]).range([0, w]);
                    xBrush.extent([xScale(_value.options.selections.xBrush[0]), xScale(_value.options.selections.xBrush[1])]);
                } else {
                    xBrush.extent(_value.options.selections.xBrush);
                }

                d3.select('.xBrush').call(xBrush);
            }
            brush();
        }


    };

    selectRows = function (optSelection) {
        var selection = optSelection || _value.options.selectedrows;
        d3.selectAll('.row').each(function (d, i) {
            var selected = false;
            var unselected = false;
            if (selection && selection.length > 0) {
                selected = selection && selection.indexOf(this.getAttribute('id')) > -1;
                unselected = !selected;

            }
            var row = d3.select(this);
            if (!row.classed('filtered')) {
                d3.select(this).classed({ selected: selected, 'knime-selected': selected, unselected: unselected });
            }
        });
        if (selection && selection.length > 0) {
            rowsSelected = true;
        }
        if (d3.select('.selected').empty()) {
            d3.selectAll('.row.unselected').classed('unselected', false);
            rowsSelected = false;
        }
    };

    brushstart = function () {
        rowsSelected = false;
        d3.event.sourceEvent.stopPropagation();
    };

    drawBrushes = function (par) {
        par = par || false;
        d3.keys(brushes).forEach(function (b) {
            if (extents[b]) {
                if (par && _data.colTypes[b] == 'string') {
                    brushes[b].extent([ordinalScale(extents[b][0]), ordinalScale(extents[b][1])]);
                } else {
                    brushes[b].extent(extents[b]);
                }
            }
        });
        d3.selectAll('.brush').each(function (d) {
            d3.select(this).call(brushes[d]);
        });
        // draw xBrush
        if (_value.options.mValues == MISSING_VALUE_MODE && xExtent && containMissing()) {
            if (par) {
                xBrush.extent([xBrushScale(xExtent[0]), xBrushScale(xExtent[1])]);
            } else {
                xBrush.extent(xExtent);
            }
            d3.select('.xBrush').call(xBrush);
        }

    };

    refreshView = function (event) {
        if (_representation.options.enableSelection && _representation.options.enableBrushing && brushes && !rowsSelected) {
            getExtents();
            oldHeight = h;
            oldWidth = w;
        }

        if (_representation.options.enableSelection && _representation.options.enableBrushing &&
            noBrushes() && !d3.selectAll('.row.selected').empty()) {
            saveSelectedRows();
        }

        if (_representation.options.enableSelection && !_representation.options.enableBrushing &&
            !d3.selectAll('.row.selected').empty()) {
            saveSelectedRows();
        }
        drawChart();
        if (_representation.options.enableSelection && _representation.options.enableBrushing && brushes && !rowsSelected) {
            ordinalScale = d3.scale.linear().domain([oldHeight, 0]).range([h, 0]);
            if (_value.options.mValues == MISSING_VALUE_MODE && xExtent && containMissing()) {
                xBrushScale = d3.scale.linear().domain([0, oldWidth]).range([0, w]);
            }
            drawBrushes(true);
            brush(null, null, null, true);
        }

        applyFilter();
    };

    saveSelected = function () {
        _value.outColumns.selection = {};
        // set every RowId to false
        d3.selectAll('.row').each(function (row) {
            _value.outColumns.selection[row.id] = false;
        });
        // set to true selected
        d3.selectAll('.row').filter('.selected').each(function (row) {
            _value.outColumns.selection[row.id] = true;
        });
    };

    pcPlot.validate = function () {
        return true;
    };

    saveSelectedRows = function () {
        var selected = d3.selectAll('.row.selected');
        if (!selected.empty()) {
            d3.selectAll('.row.selected').datum(function (d) {
                d.selected = true;
                return d;
            });
            d3.selectAll('.row.knime-selected').datum(function (d) {
                d.selected = true;
                return d;
            });
            d3.selectAll('.row.unselected').datum(function (d) {
                d.selected = false;
                return d;
            });
        }

    };

    noBrushes = function () {
        var noBrushes = true;
        for (var i = 0; i < _data.colNames.length; i++) {
            // noBrushes &= brushes[_data.colNames[i]].empty();
            noBrushes = noBrushes && brushes[_data.colNames[i]].empty();
        }

        if (_value.options.mValues == MISSING_VALUE_MODE && xBrush && containMissing()) {
            noBrushes = noBrushes && xBrush.empty();
        }

        if (xBrush) {
            noBrushes = noBrushes && xBrush.empty();
        }

        return noBrushes;
    };

    extraRows = function () {
        if (!d3.selectAll('.row.selected').empty()) {
            d3.selectAll('.row').each(function (d) {
                if (!d3.select(this).classed('selected') && !d3.select(this).classed('unselected')) {
                    d3.select(this).classed('unselected', true);
                }

            });
        }

    };

    pcPlot.getComponentValue = function () {
        if (d3.selectAll('.axis').empty()) {
            return null;
        }
        saveSettingsToValue(true);
        return _value;
    };

    containMissing = function () {
        var missing = false;
        for (var i = 0; i < _data.objects.length; i++) {
            missing = missing || _data.objects[i].containsMissing == true;
        }

        return missing;
    };

    saveSettingsToValue = function (par) {
        par = par || false;
        if (_representation.options.enableSelection && _representation.options.enableBrushing) {
            getExtents();
            _value.options.selections = {};
            _value.options.selections.extents = extents;
            if (_value.options.mValues == MISSING_VALUE_MODE && containMissing()) {
                if (!xBrush.empty()) {
                    _value.options.selections.xBrush = xExtent;
                }
            }
            // empty saved single rows selection
            if (_value.options.selectedrows) {
                delete _value.options.selectedrows;
            }
        }

        if (_representation.options.enableSelection && _representation.options.enableBrushing && noBrushes() &&
            !d3.selectAll('.row.selected').empty() ||
            _representation.options.enableSelection && !_representation.options.enableBrushing &&
            !d3.selectAll('.row.selected').empty()) {
            _value.options.selectedrows = [];
            d3.selectAll('.row.selected').each(function (row) {
                _value.options.selectedrows.push(row.id);
            });
            // empty saved brushes
            if (_value.options.selections) {
                delete _value.options.selection;
            }

        }

        if (_representation.options.enableAxesSwapping) {
            _value.options.sortedCols = _data.colNames;
        }
        if (par) {
            _value.options.oldHeight = h;
            _value.options.oldWidth = w;
        }
        // save selected rows for the node output column
        saveSelected();
    };

    return pcPlot;

})();
