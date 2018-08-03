heatmap_namespace = (function() {
    var heatmap = {};
    var _representation, _value, _table;

    // Hardcoded Settings
    var _imageColumnName = 'svg';
    var _colorRange = ['#FF0700', '#fff', '#00FF56'];
    var _itemSize = 18;
    var _margin = { top: 150, left: 100 };

    // State managment objects
    var defaultViewValues = {
        selectedRows: [],
        scaleType: 'linear',
        currentPage: 1,
        rowsPerPage: 100,
        tooltipsEnabled: true,
        zoomEnabled: true,
        selectionEnabled: false,
        initialZoomLevel: {
            x: 0,
            y: 0,
            k: 1
        }
    };

    heatmap.init = function(representation, value) {
        debugger;

        if (!representation.inObjects[0]) {
            //todo: error
            return;
        }

        if (!representation.options.heatmapCols.length) {
            //todo: error
            return;
        }

        _representation = representation;
        _value = Object.assign(defaultViewValues, value);
        _table = new kt();

        _table.setDataTable(representation.inObjects[0]);

        run();
    };

    // Run everything
    function run() {
        // prepare data
        var paginationData = createPagination(_table.getRows());

        // prepare Html
        var pagination = getPaginationHtml(paginationData);
        var wrapper = '<div class="heatmap"></div>';
        var toolTip = '<div class="tooltip"></div>';
        var controls = getControlHtml();

        var body = document.getElementsByTagName('body')[0];
        body.innerHTML = controls + wrapper + pagination + toolTip;

        // Build svg based on the current data
        buildSvg(paginationData.rows);

        // Events
        registerDomEvents();
    }

    function getControlHtml() {
        return (
            '<form class="wrapper">\
             <div class="form-group col-xs-3 rowsPerPage">\
                     <label for="rowsPerPage">Rows per page</label>\
                     <select id="rowsPerPage" class="form-control">\
                     <option ' +
            (_value.rowsPerPage === 50 ? 'selected ' : '') +
            'value="50">50</option>\
                         <option ' +
            (_value.rowsPerPage === 100 ? 'selected ' : '') +
            'value="100">100</option>\
                         <option ' +
            (_value.rowsPerPage === 200 ? 'selected ' : '') +
            'value="200">200</option>\
             <option ' +
            (_value.rowsPerPage === 500 ? 'selected ' : '') +
            'value="500">500</option>\
             <option ' +
            (_value.rowsPerPage === 1000 ? 'selected ' : '') +
            'value="1000">1000</option>\
                     </select>\
                 </div>\
             <div class="form-group col-xs-3 scaleselector">\
                     <label for="scale">Scale type</label>\
                     <select id="scale" class="form-control">\
                         <option ' +
            (_value.scaleType === 'linear' ? 'selected ' : '') +
            'value="linear">Linear</option>\
                         <option ' +
            (_value.scaleType === 'quantize' ? 'selected ' : '') +
            'value="quantize">Quantize</option>\
                     </select>\
                 </div>\
                 <div class="checkbox enableTooltips col-xs-3">\
                 <label>\
                     <input type="checkbox" ' +
            (_value.tooltipsEnabled ? 'checked ' : '') +
            '> Show tooltips\
                 </label>\
                 </div>\
                 <div class="checkbox enableZoom col-xs-3">\
                 <label>\
                     <input type="checkbox" ' +
            (_value.zoomEnabled ? 'checked ' : '') +
            '> Enable Drag and Zoom\
                 </label>\
                 </div>\
                 <div class="checkbox enableSelection col-xs-3">\
                 <label>\
                     <input type="checkbox" ' +
            (_value.selectionEnabled ? 'checked ' : '') +
            '> Enable Selection\
                 </label>\
                 </div>\
                 <button type="submit" class="btn btn-default hidden">Submit</button>\
             </form>'
        );
    }

    function getPaginationHtml(pagination) {
        if (pagination.pageCount <= 1) {
            return '';
        }
        var html = '<div class="paginationWrapper"><ul class="pagination">';

        if (pagination.prev) {
            html += '<li><a href="#' + pagination.prev + '">&laquo;</a></li>';
        } else {
            html += '<li class="disabled"><span>&laquo;</span></li>';
        }

        for (var i = 1; i <= pagination.pageCount; i++) {
            html +=
                '<li class="' +
                (_value.currentPage === i ? 'active' : '') +
                '"><a href="#' +
                i +
                '">' +
                i +
                '</a></li>';
        }

        if (pagination.next) {
            html += '<li><a href="#' + pagination.next + '">&raquo;</a></li>';
        } else {
            html += '<li class="disabled"><span>&raquo;</span></li>';
        }
        html += '</ul></div>';
        return html;
    }

    function registerDomEvents() {
        var body = document.getElementsByTagName('body')[0];

        var pagination = body.querySelector('.pagination');
        if (pagination) {
            body.querySelector('.pagination').addEventListener('click', function(e) {
                if (e.target.tagName === 'A') {
                    var pageNumber = parseInt(e.target.getAttribute('href').substr(1), 10);
                    _value.currentPage = pageNumber;
                    run();
                }
            });
        }
        body.querySelector('.scaleselector').addEventListener('change', function(e) {
            _value.scaleType = e.target.value;
            run();
        });
        body.querySelector('.rowsPerPage').addEventListener('change', function(e) {
            _value.rowsPerPage = parseInt(e.target.value, 10);
            run();
        });
        body.querySelector('.enableTooltips input').addEventListener('change', function(e) {
            _value.tooltipsEnabled = e.target.checked;
        });
        body.querySelector('.enableSelection input').addEventListener('change', function(e) {
            _value.selectionEnabled = e.target.checked;
        });
        body.querySelector('.enableZoom input').addEventListener('change', function(e) {
            _value.zoomEnabled = e.target.checked;
            initializeZoom();
        });
    }

    /**
     * Create very basic pagination data from rows
     * @param {Array} data
     */
    function createPagination(data) {
        var pageCount = Math.ceil(data.length / _value.rowsPerPage);

        // jump to page 1 if total number of pages exceeds current page
        _value.currentPage = _value.currentPage <= pageCount ? _value.currentPage : 1;

        var currentPage = _value.currentPage;
        var nextPageRowEnd = _value.rowsPerPage * currentPage;
        var nextPageRowStart = _value.rowsPerPage * (currentPage - 1);
        var rows = data.slice(nextPageRowStart, nextPageRowEnd);

        return {
            rows: rows,
            pageCount: pageCount,
            next: nextPageRowEnd < data.length ? currentPage + 1 : false,
            prev: nextPageRowStart > 0 ? currentPage - 1 : false
        };
    }

    function formatData(rows) {
        var minimum = Number.POSITIVE_INFINITY;
        var maximum = Number.NEGATIVE_INFINITY;
        var images = [];
        var rowNames = [];
        var colNames = [];

        // Get valid indexes for heatmap columns by comparing them to input colNames
        repColNames = _representation.inObjects[0].spec.colNames;
        _representation.options.heatmapCols.map(function(hmColName) {
            colNames[repColNames.indexOf(hmColName)] = hmColName;
        });

        var allValues = rows.reduce(function(accumulator, row) {
            rowNames.push(row.rowKey);
            var rowIsSelected = _value.selectedRows.indexOf(row.rowKey) != -1; // a bit slow

            // Storing images in an separate array is enough
            if (_imageColumnName) {
                images[row.rowKey] = _table.getCell(row.rowKey, _imageColumnName);
            }

            // Set values for each cell
            var vals = row.data.reduce(function(rowAcc, value, currentIndex) {
                if (colNames[currentIndex] === undefined) {
                    return rowAcc;
                }
                var newItem = {};
                newItem.y = row.rowKey;
                newItem.x = colNames[currentIndex];
                newItem.value = value;
                newItem.initallySelected = rowIsSelected;

                // Good opportunity to determine min and max
                minimum = Math.min(minimum, newItem.value);
                maximum = Math.max(maximum, newItem.value);
                rowAcc.push(newItem);
                return rowAcc;
            }, []);
            return accumulator.concat(vals);
        }, []);

        return {
            images: images,
            data: allValues,
            rowNames: rowNames,
            colNames: colNames,
            minimum: minimum,
            maximum: maximum
        };
    }

    function getLinearColorDomain(minimum, maximum) {
        var domain = [];
        var interpolator = d3.interpolateNumber(minimum, maximum);
        for (var i = 0; i < _colorRange.length; i++) {
            domain.push(interpolator(i / (_colorRange.length - 1)));
        }
        return domain;
    }

    function getScales(formattedDataset) {
        return {
            x: d3
                .scaleBand()
                .domain(formattedDataset.colNames)
                .range([_margin.left, formattedDataset.colNames.length * _itemSize - 1 + _margin.left]),
            y: d3
                .scaleBand()
                .domain(formattedDataset.rowNames)
                .range([_margin.top, formattedDataset.rowNames.length * _itemSize - 1 + _margin.top]),
            colorScale:
                _value.scaleType === 'quantize'
                    ? d3
                          .scaleQuantize()
                          .domain([formattedDataset.minimum, formattedDataset.maximum])
                          .range(_colorRange)
                    : d3
                          .scaleLinear()
                          .domain(getLinearColorDomain(formattedDataset.minimum, formattedDataset.maximum))
                          .range(_colorRange)
        };
    }

    function getAxis(scales) {
        return {
            x: d3.axisTop(scales.x).tickFormat(function(d) {
                return d;
            }),

            y: d3.axisLeft(scales.y).tickFormat(function(d) {
                return d;
            })
        };
    }

    function formatImage(string) {
        return 'data:image/svg+xml;base64,' + btoa(string);
    }

    function initializeZoom() {
        var svgEl = document.querySelector('.heatmap svg');
        var svgD3 = d3.select(svgEl);

        var wrapper = svgD3.select(':scope .wrapper');
        var xAxisD3El = svgD3.select('.xAxis');
        var yAxisD3El = svgD3.select('.yAxis');

        // Zoom and pan
        var zoom = d3
            .zoom()
            .scaleExtent([0, 1])
            .on('zoom', function() {
                var t = d3.event.transform;

                // Limit zoom
                t.x = d3.min([t.x, (1 - t.k) * _margin.left]);
                t.y = d3.min([t.y, (1 - t.k) * _margin.top]);

                // Save current zoom level
                _value.initialZoomLevel = t;

                xAxisD3El.attr('transform', 'translate(' + t.x + ', ' + _margin.top + ') scale(' + t.k + ')');
                yAxisD3El.attr('transform', 'translate(' + _margin.left + ', ' + t.y + ') scale(' + t.k + ')');

                wrapper.attr('transform', 'translate(' + t.x + ', ' + t.y + ') scale(' + t.k + ')');
            });

        if (_value.zoomEnabled) {
            svgD3.call(zoom);
        } else {
            svgD3.on('.zoom', null);
        }
        return zoom;
    }

    function showTooltip(e, innerHtml) {
        if (!_value.tooltipsEnabled && innerHtml) {
            return;
        }
        var tooltip = document.querySelector('.tooltip');
        tooltip.classList.add('active');
        e.target.classList.add('active');
        tooltip.innerHTML = innerHtml;
        tooltip.style.left = event.clientX + _itemSize + 'px';
        tooltip.style.top = event.clientY - _itemSize + 'px';
    }

    function hideTooltip() {
        var tooltip = document.querySelector('.tooltip');
        tooltip.classList.remove('active');
    }

    function buildSvg(rows) {
        var formattedDataset = formatData(rows);

        var scales = getScales(formattedDataset);
        var axis = getAxis(scales);

        var svg = d3.select('.heatmap').append('svg');

        var viewport = svg
            .append('g')
            .attr('class', 'viewport')
            .attr('clip-path', 'url(#clip)');

        var wrapper = viewport.append('g').attr('class', 'wrapper');

        var defs = svg.append('defs');
        defs.append('clipPath')
            .attr('id', 'clip')
            .append('rect')
            .attr('y', _margin.top)
            .attr('x', _margin.left)
            .attr('width', '100%')
            .attr('height', '100%');

        var cells = wrapper
            .selectAll('rect')
            .data(formattedDataset.data)
            .enter()
            .append('g')
            .append('rect')
            .attr('class', 'cell')
            .attr('width', _itemSize - 1)
            .attr('height', _itemSize - 1)
            .attr('y', function(d) {
                return scales.y(d.y);
            })
            .attr('x', function(d) {
                return scales.x(d.x);
            })
            .attr('fill', function(d) {
                return scales.colorScale(d.value);
            })
            .attr('selection', function(d) {
                //initialize selection if already selected
                return d.initallySelected ? 'active' : 'inactive';
            });

        // Events for the svg are native js event listeners not
        // d3 event listeners for better performance
        var domWrapper = document.querySelector('.heatmap svg .wrapper');

        // Highlight mouseover cell and show tooltip
        domWrapper.addEventListener('mouseover', function(e) {
            if (!e.target.classList.contains('cell')) {
                return;
            }

            var data = d3.select(e.target).data()[0];

            // Select rows
            if (event.button || event.which) {
                selectCell(data);
            }

            toolTipInnerHTML =
                '<span class="position">x:' +
                data.x +
                ' y:' +
                data.y +
                '</span><span class="value">' +
                data.value +
                '</span>';

            showTooltip(e, toolTipInnerHTML);
        });

        // Deactivation relies on gaps in the wrapper between the cells
        domWrapper.addEventListener('mouseout', function(e) {
            hideTooltip();
            e.target.classList.remove('active');
        });

        // Row selection
        domWrapper.addEventListener('mousedown', function(e) {
            if (e.target.tagName !== 'rect') {
                return;
            }
            var data = d3.select(e.target).data()[0];
            selectCell(data);
        });

        // Append axis
        var maskAxis = defs.append('mask').attr('id', 'maskAxis');
        maskAxis
            .append('rect')
            .attr('y', 0)
            .attr('x', 0)
            .attr('width', '100%')
            .attr('height', '100%')
            .attr('fill', 'white');
        maskAxis
            .append('rect')
            .attr('y', 0)
            .attr('x', 0)
            .attr('width', _margin.left + 1)
            .attr('height', _margin.top + 1)
            .attr('fill', 'black');

        var axisWrapper = svg
            .append('g')
            .attr('class', 'axisWrapper')
            .attr('mask', 'url(#maskAxis)');
        axisWrapper
            .append('g')
            .attr('class', 'yAxis')
            .call(axis.y)
            .selectAll('text')
            .attr('font-weight', 'normal')
            .on('mouseover', function(d) {
                if (!formattedDataset.images[d]) {
                    return;
                }
                d3.event.target.classList.add('active');
                tooltipInnerHTML = '<img src="' + formatImage(formattedDataset.images[d]) + '" alt/>';
                showTooltip(d3.event, tooltipInnerHTML);
            })
            .on('mouseleave', function() {
                hideTooltip();
                d3.event.target.classList.remove('active');
            });

        axisWrapper
            .append('g')
            .attr('class', 'xAxis')
            .call(axis.x)
            .selectAll('text')
            .attr('font-weight', 'normal')
            .style('text-anchor', 'start')
            .attr('dx', '1em')
            .attr('dy', '.5em')
            .attr('transform', function(d) {
                return 'rotate(-65)';
            });

        // Initialize zoom
        var zoom = initializeZoom();
        resetZoom(zoom);

        // Legend
        var legendWidth = 200;
        var legendHeight = 25;
        var legendGradient = defs
            .append('linearGradient')
            .attr('id', 'legendGradient')
            .attr('transform', 'translate(100, 100)');

        var legend = svg.append('g').attr('transform', 'translate(' + _margin.left + ', 0)');

        var colorDomain = getLinearColorDomain(formattedDataset.minimum, formattedDataset.maximum);

        // append a single rect to display a gradient
        legend
            .append('rect')
            .attr('width', legendWidth)
            .attr('height', legendHeight)
            .attr('fill', 'url(#legendGradient)');

        // set gradient stops
        if (_value.scaleType === 'linear') {
            for (var i = 0; i < colorDomain.length; i++) {
                var percentage = (100 / (colorDomain.length - 1)) * i;
                legendGradient
                    .append('stop')
                    .attr('offset', percentage + '%')
                    .attr('style', 'stop-opacity:1; stop-color:' + scales.colorScale(colorDomain[i]));
            }
        } else if (_value.scaleType === 'quantize') {
            var legendCellPercentage = 100 / colorDomain.length;
            var previousPercentage = 0;
            var interpolator = d3.interpolateNumber(formattedDataset.minimum, formattedDataset.maximum);
            var tickValues = [];
            tickValues.push(formattedDataset.minimum, formattedDataset.maximum);

            for (var i = 0; i < colorDomain.length; i++) {
                var currentPercentage = legendCellPercentage * (i + 1);

                tickValues.push(interpolator(currentPercentage / 100));

                legendGradient
                    .append('stop')
                    .attr('offset', previousPercentage + '%')
                    .attr('style', 'stop-opacity:1; stop-color:' + scales.colorScale(colorDomain[i]));
                legendGradient
                    .append('stop')
                    .attr('offset', currentPercentage + '%')
                    .attr('style', 'stop-opacity:1; stop-color:' + scales.colorScale(colorDomain[i]));
                previousPercentage = currentPercentage;
            }
        }

        var legendScale = d3
            .scaleLinear()
            .domain([formattedDataset.minimum, formattedDataset.maximum])
            .range([0, legendWidth]);

        var legendAxis = d3
            .axisBottom(legendScale)
            .tickValues(tickValues || colorDomain)
            .tickFormat(function(d) {
                return Math.round(d * 1000) / 1000;
            });

        legend
            .append('g')
            .attr('transform', 'translate(0, ' + legendHeight + ')')
            .attr('class', 'legendAxis')
            .call(legendAxis)
            .selectAll('text')
            .attr('font-weight', 'normal');
    }

    function resetZoom(zoom) {
        var svg = d3.select('.heatmap svg');
        zoom.transform(svg, function() {
            return d3.zoomIdentity
                .translate(_value.initialZoomLevel.x, _value.initialZoomLevel.y)
                .scale(_value.initialZoomLevel.k);
        });
    }

    function selectCell(d) {
        var selectedRowId = d.y;
        d3.selectAll('.cell').attr('selection', function(d) {
            var selected = d3.select(this).attr('selection');
            if (_value.selectionEnabled && d.y === selectedRowId) {
                if (selected === 'active') {
                    // remove them from our selected rows and set inactive
                    if (_value.selectedRows[d.y]) {
                        delete _value.selectedRows[d.y];
                    }
                    return 'inactive';
                } else {
                    // add to selected rows and set to active
                    _value.selectedRows[d.y] = d.y;
                    return 'active';
                }
            }
            return selected;
        });
    }

    return heatmap;
})();
