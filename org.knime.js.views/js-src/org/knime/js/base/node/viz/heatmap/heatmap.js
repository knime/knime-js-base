window.heatmapNamespace = (function () {

    var svgNS = 'http://www.w3.org/2000/svg';
    var xhtmlNS = 'http://www.w3.org/1999/xhtml';

    var Heatmap = function () {
        this._representation = null;
        this._value = null;
        this._table = null;
        this._axis = null;
        this._scales = null;
        this._drawCellQueue = null;
        this._tooltip = null;
        this._colorRange = null;
        this._filteredData = null;
        this._zoomDimensions = null;
        this._cellWidth = null;
        this._cellHeight = null;
        this._transformer = null;
        this._cellHighlighter = null;
        this._maxExtensionY = null;
        this._maxExtensionX = null;
        this._axisMaskTopLeft = null;

        // Hardcoded Default Settings
        this._minCellSize = 12;
        this._maxCanvasHeight = 8000; // canvas has native size limits
        this._defaultMargin = { top: 10, left: 10, right: 10, bottom: 10 };
        this._margin = {};
        this._defaultZoomX = 0;
        this._defaultZoomY = 0;
        this._defaultZoomK = 1;
        this._continousLegendWidth = 200;
        this._discreteLegendWidth = 200;
        this._legendHeight = 50;
        this._legendColorRangeHeight = 20;
        this._legendStandardMargin = 5;
        this._legendTopMargin = 15;
        this._infoWrapperMinHeight = 80;
        this._xAxisLabelTransform = 'rotate(-65) translate(10 8)';
        this._canvases = [];
        this._labelsMargins = false;
        this._previousDataLength = 0;
        this._colNames = [];
        this.intervalTime = 10;
        this.showProgressBarMaxPercentage = 20;
        this.titleHeight = 30;
        this.subtitleHeight = 20;
        this.onSelectionChange = this.onSelectionChange.bind(this);
        this.onFilterChange = this.onFilterChange.bind(this);

    };

    Heatmap.prototype.init = function (representation, value) {
        if (!representation.table || !representation.columns.length) {
            d3.select('body')
                .append('p')
                .text('Error: No data available');
            return;
        }

        // prepare data
        this._representation = representation;
        this._value = value || {};
        this._value.selection = value.selection || [];
        this._value.currentPage = value.currentPage || 1;
        this._value.zoomX = value.zoomX || this._defaultZoomX;
        this._value.zoomY = value.zoomY || this._defaultZoomY;
        this._value.zoomK = value.zoomK || this._defaultZoomK;

        this._table = new kt();
        this._table.setDataTable(representation.table);

        // Get valid indexes for heatmap columns by comparing them to input colNames
        var repColNames = this._representation.table.spec.colNames;
        var self = this;
        this._representation.columns.forEach(function (hmColName) {
            self._colNames[repColNames.indexOf(hmColName)] = hmColName;
        });

        this.toggleSubscribeFilter();
        this.toggleSubscribeSelection();

        this.drawControls();

        var container = document.createElementNS(xhtmlNS, 'div');
        container.classList.add('knime-layout-container');
        document.body.appendChild(container);
        
        this.drawChart();
        
        this.registerOneTimeEvents();
    };

    Heatmap.prototype.getComponentValue = function () {
        return this._value;
    };

    Heatmap.prototype.validate = function () {
        return true;
    };

    Heatmap.prototype.getSVG = function () {
        var svgElement = d3.select('.heatmap').node();
        knimeService.inlineSvgStyles(svgElement);
        return new XMLSerializer().serializeToString(svgElement);
    };


    Heatmap.prototype.reset = function () {
        this._cellHeight = 0;
        this._cellWidth = 0;
        this._canvases = [];
        this._labelsMargins = false;
    };


    Heatmap.prototype.toggleSubscribeSelection = function () {
        if (this._value.subscribeSelection) {
            knimeService.subscribeToSelection(this._table.getTableId(), this.onSelectionChange);
        } else {
            knimeService.unsubscribeSelection(this._table.getTableId(), this.onSelectionChange);
        }
    };

    Heatmap.prototype.toggleSubscribeFilter = function () {
        if (this._value.subscribeFilter) {
            knimeService.subscribeToFilter(
                this._table.getTableId(),
                this.onFilterChange,
                this._table.getFilterIds()
            );
        } else {
            knimeService.unsubscribeFilter(this._table.getTableId(), this.onFilterChange);
        }
    };

    Heatmap.prototype.onFilterChange = function (data) {
        var self = this;
        this._filteredData = this._table.getRows().filter(function (row) {
            return self._table.isRowIncludedInFilter(row.rowKey, data);
        });
        this.drawChart();
    };

    Heatmap.prototype.onSelectionChange = function (data) {
        var self = this;
        if (data.reevaluate) {
            this._value.selection = knimeService.getAllRowsForSelection(this._table.getTableId());
        } else if (data.changeSet) {
            if (data.changeSet.added) {
                data.changeSet.added.forEach(function (rowId) {
                    var index = self._value.selection.indexOf(rowId);
                    if (index === -1) {
                        self._value.selection.push(rowId);
                    }
                });
            }
            if (data.changeSet.removed) {
                data.changeSet.removed.forEach(function (rowId) {
                    var index = self._value.selection.indexOf(rowId);
                    if (index > -1) {
                        self._value.selection.splice(index, 1);
                    }
                });
            }
        }

        this.styleSelectedRows();

        if (this._value.showSelectedRowsOnly) {
            this.drawChart();
            this.resetZoom(true);
        }
    };

    /**
     * Filter the available data to only the selected rows
     * @param {Array} data
     * @return {Array}
     */
    Heatmap.prototype.getSelectionData = function (data) {
        var self = this;
        if (this._value.showSelectedRowsOnly) {
            return data.filter(function (row) {
                return self._value.selection.indexOf(row.rowKey) > -1;
            });
        }

        return data;
    };

    /**
     * Draw and re-draw the whole chart
     * @return {Undefined}
     */
    Heatmap.prototype.drawChart = function () {
        var container = document.querySelector('.knime-layout-container');
        container.innerHTML = '';

        var svgWrapper =
            '<div class="knime-svg-container" data-iframe-height>\
                <span class="gradient-y"></span>\
                <span class="gradient-x"></span>\
                <svg class="heatmap"></svg>\
            </div>';
        var toolTipWrapper = '<div class="knime-tooltip"></div>';
        var infoWrapperEl = '<div class="info-wrapper"></div>';
        var progressBar = '<div class="progress-bar">Rendering ...<span class="progress"></span></div>';
        container.innerHTML = svgWrapper + infoWrapperEl + toolTipWrapper + progressBar;

        var data = this._filteredData
            ? this.getSelectionData(this._filteredData)
            : this.getSelectionData(this._table.getRows());

        // Meta info
        var paginationData = this.createPagination(data);

        this.drawMetaInfo(paginationData);

        // Build svg based on the paginated data
        this.drawContents(paginationData.rows);
    };

    Heatmap.prototype.getProgressBar = function (totalRowsCount) {
        if (!this._drawCellQueue) {
            return;
        }
        var self = this;
        var finishedPercentage = 100;
        var progressBar = document.querySelector('.progress-bar');
        var progressIndicator = progressBar.querySelector('.progress');

        var interval = this.requestInterval(function () {
            if (!self._drawCellQueue) {
                interval.clear();
            }
            var percentageComplete = finishedPercentage -
                self._drawCellQueue.remaining() / totalRowsCount * finishedPercentage;
            if (percentageComplete < self.showProgressBarMaxPercentage) { // only display progress bar if initial rendered percentage is low
                progressBar.style.opacity = 1;
            }
            percentageComplete = Math.min(finishedPercentage, percentageComplete);
            progressIndicator.style.width = percentageComplete + '%';
            if (percentageComplete >= finishedPercentage) {
                interval.clear();
                self.createImagesFromCanvases();
                progressBar.style.opacity = 0;
            }
        }, this.intervalTime);
    };

    /**
     * Displaying images via SVG images is
     * more cross-browser compatible than via foreignObject
     * @return {Undefined}
     */
    Heatmap.prototype.createImagesFromCanvases = function () {
        var xlinkNS = 'http://www.w3.org/1999/xlink';

        var existingImages = document.querySelectorAll('.imagewrapper');
        for (var i = 0; i < existingImages.length; i++) {
            this.removeElementFromDOM(existingImages[i]);
        }

        for (var key in this._canvases) {
            var currentCanvas = this._canvases[key];

            var el = currentCanvas.el;
            var group = document.createElementNS(svgNS, 'g');
            group.setAttribute('class', 'imagewrapper');
            var image = document.createElementNS(svgNS, 'image');

            image.setAttribute('data-imageid', currentCanvas.id);
            image.setAttributeNS(xlinkNS, 'href', el.toDataURL());
            image.setAttribute('width', currentCanvas.width);
            image.setAttribute('height', currentCanvas.height);

            group.setAttribute('style', 'width:' + currentCanvas.width + 'px; height:' + currentCanvas.height + 'px;');
            group.setAttribute('transform',
                'translate(' + currentCanvas.transformX + ' ' + currentCanvas.transformY + ')');
            group.appendChild(image);

            var highlighters = this._transformer.node().querySelector('.highlighters');
            this._transformer.node().insertBefore(group, highlighters);
        }
        var preview = document.querySelector('.preview');
        if (preview) {
            this.removeElementFromDOM(preview);
        }
    };

    Heatmap.prototype.drawMetaInfo = function (paginationData) {
        var displayedRows = '';
        var paginationHtml = '';

        if (this._representation.enablePaging) {
            paginationHtml += this.getPaginationHtml(paginationData);
            displayedRows +=
                '<div><p>Showing ' +
                (paginationData.totalRowCount > 1
                    ? paginationData.pageRowStartIndex + 1
                    : paginationData.totalRowCount) +
                ' to ' +
                paginationData.pageRowEndIndex +
                ' of ' +
                paginationData.totalRowCount +
                ' entries</p>';

            displayedRows += '<p class="partially-displayed-hint">(Partially displayed)</p></div>';
        }

        var infoWrapper = document.body.querySelector('.info-wrapper');
        infoWrapper.innerHTML = displayedRows + paginationHtml;
        infoWrapper.style.minHeight = this._infoWrapperMinHeight + 'px';
    };

    Heatmap.prototype.updateTitles = function () {
        document.querySelector('.knime-title').textContent = this._value.chartTitle;
        document.querySelector('.knime-subtitle').textContent = this._value.chartSubtitle;
    };

    Heatmap.prototype.getTitlesHeight = function () {
        var completeTitleHeight = 0;
        completeTitleHeight += this._value.chartTitle ? this.titleHeight : 0;
        completeTitleHeight += this._value.chartSubtitle ? this.subtitleHeight : 0;
        return completeTitleHeight;
    };

    Heatmap.prototype.drawControls = function () {
        var self = this;
        if (!this._representation.enableViewConfiguration) {
            return;
        }

        if (this._representation.displayFullscreenButton) {
            knimeService.allowFullscreen();
        }

        if (
            this._representation.enablePanning ||
            this._representation.enableZoom ||
            this._representation.enableSelection ||
            this._representation.showZoomResetButton
        ) {
            knimeService.addNavSpacer();
        }

        if (this._representation.enableSelection) {
            var selectionButtonClicked = function () {
                self._value.enableSelection = !self._value.enableSelection;
                var button = document.getElementById('heatmap-selection-mode');
                button.classList.toggle('active');

                self.toggleSelectionClass();
            };
            knimeService.addButton(
                'heatmap-selection-mode',
                'check-square-o',
                'Mouse Mode "Select"',
                selectionButtonClicked
            );
            if (this._representation.enableSelection && !this._representation.enablePanning) {
                selectionButtonClicked();
            }

            knimeService.addButton('heatmap-clear-selection-button', 'minus-square-o', 'Clear selection', function () {
                self._value.selection = [];
                if (self._value.publishSelection) {
                    knimeService.setSelectedRows(self._table.getTableId(), self._value.selection);
                }
                self.drawChart();
            });
            knimeService.addNavSpacer();
        }

        if (this._representation.enableZoom) {
            var zoomButtonClicked = function (evt, initialize) {
                self._value.enableZoom = !self._value.enableZoom;
                var button = document.getElementById('heatmap-mouse-mode-zoom');
                button.classList.toggle('active');
                if (!initialize) {
                    self.setZoomEvents();
                }
            };
            knimeService.addButton('heatmap-mouse-mode-zoom', 'search', 'Mouse Mode "Zoom"', zoomButtonClicked);
            if (this._representation.enableZoom && !this._representation.enableSelection &&
                !this._representation.enablePanning) {
                zoomButtonClicked(false, true);
            }
        }

        if (this._representation.enablePanning) {
            var panButtonClicked = function (evt, initialize) {
                self._value.enablePanning = !self._value.enablePanning;
                var button = document.getElementById('heatmap-mouse-mode-pan');
                button.classList.toggle('active');

                if (!initialize) {
                    self.setZoomEvents();
                }

                self.togglePanningClass();
            };
            knimeService.addButton('heatmap-mouse-mode-pan', 'arrows', 'Mouse Mode "Pan"', panButtonClicked);
            if (this._representation.enablePanning) {
                panButtonClicked(false, true);
            }
        }

        if (this._representation.showZoomResetButton) {
            knimeService.addButton('scatter-zoom-reset-button', 'search-minus', 'Reset Zoom', function () {
                self.resetZoom(true);
            });
        }
        // Create menu items
        if (this._representation.enableTitleChange) {
            var chartTitleText = knimeService.createMenuTextField(
                'chartTitleText',
                this._value.chartTitle,
                function () {
                    if (self._value.chartTitle !== this.value) {
                        self._value.chartTitle = this.value;
                        self.updateTitles();
                    }
                },
                true
            );
            knimeService.addMenuItem('Chart Title:', 'header', chartTitleText);
            var chartSubtitleText = knimeService.createMenuTextField(
                'chartSubtitleText',
                this._value.chartSubtitle,
                function () {
                    if (self._value.chartSubtitle !== this.value) {
                        self._value.chartSubtitle = this.value;
                        self.updateTitles();
                    }
                },
                true
            );
            knimeService.addMenuItem('Chart Subtitle:', 'header', chartSubtitleText, null, knimeService.SMALL_ICON);
            knimeService.addMenuDivider();
        }

        if (this._representation.enableShowToolTips) {
            var showToolTips = knimeService.createMenuCheckbox('showToolTips', this._value.showToolTips, function () {
                self._value.showToolTips = this.checked;
            });
            knimeService.addMenuItem('Show Tooltips', 'info', showToolTips);
        }

        if (this._representation.enableShowSelectedRowsOnly) {
            var showSelectedRowsOnly = knimeService.createMenuCheckbox(
                'showSelectedRowsOnly',
                this._value.showSelectedRowsOnly,
                function () {
                    self._value.showSelectedRowsOnly = this.checked;
                    self.drawChart();
                    self.resetZoom(true);
                }
            );
            knimeService.addMenuItem('Show Selected Rows Only', 'filter', showSelectedRowsOnly);
        }

        // Selection / Filter configuration
        knimeService.addMenuDivider();
        if (this._representation.enableSelection) {
            knimeService.addMenuItem(
                'Publish selection',
                knimeService.createStackedIcon('check-square-o', 'angle-right', 'faded left sm', 'right bold'),
                knimeService.createMenuCheckbox('publishSelectionCheckbox', this._value.publishSelection, function () {
                    self._value.publishSelection = this.checked;
                    if (self._value.publishSelection) {
                        knimeService.setSelectedRows(self._table.getTableId(), self._value.selection);
                    }
                })
            );

            knimeService.addMenuItem(
                'Subscribe to selection',
                knimeService.createStackedIcon('check-square-o', 'angle-double-right', 'faded right sm', 'left bold'),
                knimeService.createMenuCheckbox('subscribeSelectionCheckbox', this._value.subscribeSelection,
                    function () {
                        self._value.subscribeSelection = this.checked;
                        self.toggleSubscribeSelection();
                    })
            );

            knimeService.addMenuDivider();
        }

        knimeService.addMenuItem(
            'Subscribe to filter',
            knimeService.createStackedIcon('filter', 'angle-double-right', 'faded right sm', 'left bold'),
            knimeService.createMenuCheckbox('subscribeFilterCheckbox', this._value.subscribeFilter, function () {
                self._value.subscribeFilter = this.checked;
                self.toggleSubscribeFilter();
            })
        );

        knimeService.addMenuDivider();
        if (this._representation.enableColorModeEdit) {
            var updateScaleType = function () {
                self._value.continuousGradient = this.value === 'linear';
                self.drawChart();
            };
            var linearRadio = knimeService.createMenuRadioButton('linearRadio', 'scaleType', 'linear', updateScaleType);
            linearRadio.checked = this._value.continuousGradient;
            knimeService.addMenuItem('Continuous gradient', 'align-left fa-rotate-270', linearRadio);

            var quantizeRadio = knimeService.createMenuRadioButton(
                'quantizeRadio',
                'scaleType',
                'quantize',
                updateScaleType
            );
            quantizeRadio.checked = !this._value.continuousGradient;
            knimeService.addMenuItem('Discrete colors', 'tasks fa-rotate-270', quantizeRadio);

            knimeService.addMenuDivider();
        }

        if (this._representation.enablePageSizeChange) {
            var options = this._representation.allowedPageSizes;
            if (this._representation.enableShowAll) {
                options.push('all');
            }
            var pageSize = knimeService.createMenuSelect('pageSize', this._value.initialPageSize, options, function () {
                var isNewSizeSmaller = this.value < self._value.initialPageSize;
                self._value.initialPageSize = this.value;
                self.drawChart();
                if (isNewSizeSmaller) {
                    // prevent not showing any rows
                    self.resetZoom(true);
                }
            });

            knimeService.addMenuItem('Rows per Page', 'table', pageSize);
        }
    };

    Heatmap.prototype.togglePanningClass = function () {
        // add general css classes
        var layoutContainer = document.querySelector('.knime-layout-container');
        if (!layoutContainer) {
            return;
        }
        if (this._value.enablePanning) {
            layoutContainer.classList.add('panning-enabled');
        } else {
            layoutContainer.classList.remove('panning-enabled');
        }
    };

    Heatmap.prototype.toggleSelectionClass = function () {
        var layoutContainer = document.querySelector('.knime-layout-container');
        if (!layoutContainer) {
            return;
        }
        if (this._value.enableSelection) {
            layoutContainer.classList.add('selection-enabled');
        } else {
            layoutContainer.classList.remove('selection-enabled');
        }
    };

    Heatmap.prototype.togglePartiallyDisplayedClass = function () {
        var layoutContainer = document.querySelector('.knime-layout-container');
        if (!layoutContainer) {
            return;
        }
        var yAxis = document.querySelector('.knime-axis.knime-y');
        var xAxis = document.querySelector('.knime-axis.knime-x');
        var axisExist = yAxis && xAxis;
        if (!axisExist || this.areAxisCompletelyVisible(xAxis, yAxis)) {
            layoutContainer.classList.remove('partially-displayed');
        } else {
            layoutContainer.classList.add('partially-displayed');
        }
    };

    Heatmap.prototype.getPaginationHtml = function (pagination) {
        var paginationRange = this.createPaginationIntervals(pagination);
        var self = this;

        if (paginationRange.pages.length <= 1 || !this._representation.enablePaging) {
            return '';
        }
        var html = '<ul class="pagination">';

        if (paginationRange.prev) {
            html += '<li><a href="#' + paginationRange.prev + '">&laquo;</a></li>';
        } else {
            html += '<li class="disabled"><span>&laquo;</span></li>';
        }

        paginationRange.pages.forEach(function (item) {
            if (item === '...') {
                html += '<li class="disabled"><span>' + item + '</span></li>';
            } else {
                html +=
                    '<li class="' +
                    (self._value.currentPage === item ? 'active' : '') +
                    '"><a href="#' +
                    item +
                    '">' +
                    item +
                    '</a></li>';
            }
        });

        if (paginationRange.next) {
            html += '<li><a href="#' + paginationRange.next + '">&raquo;</a></li>';
        } else {
            html += '<li class="disabled"><span>&raquo;</span></li>';
        }
        html += '</ul>';
        return html;
    };

    Heatmap.prototype.onMousemove = function (e) {
        var data = this.lookupCell(e);

        if (!data) {
            return;
        }
        var toolTipInnerHTML =
            '<span class="knime-tooltip-caption">x:' +
            data.x +
            ' y:' +
            data.y +
            '</span><span class="knime-tooltip-value">' +
            (data.value === null
                ? '<span class="missing-value">?</span>'
                : '<span class="knime-double">' + data.value + '</span>') +
            '</span>';

        this.showTooltip(e, toolTipInnerHTML);
    };

    Heatmap.prototype.registerEvents = function () {
        var self = this;

        var pagination = document.body.querySelector('.pagination');
        if (pagination) {
            document.body.querySelector('.pagination').addEventListener('click', function (e) {
                if (e.target.tagName === 'A') {
                    var pageNumber = parseInt(e.target.getAttribute('href').substr(1), 10);
                    self._value.currentPage = pageNumber;
                    self.drawChart();
                }
            });
        }

        // Events for the svg are native js event listeners not
        // d3 event listeners for better performance
        var domWrapper = document.querySelector('.knime-svg-container svg .transformer');
        // Highlight mouseover cell and show tooltip
        domWrapper.addEventListener('mouseover', function (e) {
            domWrapper.addEventListener('mousemove', self.onMousemove.bind(self));
        });

        domWrapper.addEventListener('mouseout', function (e) {
            self.hideTooltip();
            domWrapper.removeEventListener('mouseover', self.onMousemove.bind(self));
        });

        // Row selection
        domWrapper.addEventListener('mousedown', function (e) {
            var data = self.lookupCell(e);
            if (!data) {
                return;
            }
            if (e.shiftKey) {
                self.selectDeltaRow(data.y);
            } else {
                self.selectSingleRow(data.y, e.ctrlKey || e.metaKey);
            }
        });

    };

    Heatmap.prototype.registerOneTimeEvents = function () {
        var self = this;
        window.addEventListener('resize', function () {
            self.reset();
            self.drawChart();
        });

    };

    /**
     * Create intervals from the pagination data to limit the amount of links shown
     * @param {Object} pagination data
     * @return {Object} pagination intervals
     */
    Heatmap.prototype.createPaginationIntervals = function (pagination) {
        var delta = 2; // number of pages displayed left and right to "center"
        var left = this._value.currentPage - delta;
        var right = this._value.currentPage + delta;
        var range = [];
        var paginationRange = [];
        var curPage;

        for (var i = 1; i <= pagination.pageCount; i++) {
            if (i === 1 || i === pagination.pageCount || (left <= i && i <= right)) {
                range.push(i);
            }
        }

        range.forEach(function (page) {
            if (curPage) {
                if (page - curPage !== 1) {
                    paginationRange.push('...');
                }
            }
            paginationRange.push(page);
            curPage = page;
        });

        return {
            prev: pagination.prev,
            next: pagination.next,
            pages: paginationRange
        };
    };

    /**
     * Create very basic pagination data from rows
     * @param {Array} data
     * @return {Object} pagination data
     */
    Heatmap.prototype.createPagination = function (data) {
        if (!this._representation.enablePaging || !data) {
            return { rows: data };
        }
        var pageSize = this._value.initialPageSize === 'all' ? data.length : this._value.initialPageSize;

        var pageCount = Math.ceil(data.length / pageSize);

        // jump to page 1 if total number of pages exceeds current page
        this._value.currentPage = this._value.currentPage <= pageCount ? this._value.currentPage : 1;

        var pageRowEndIndex = pageSize * this._value.currentPage;
        var pageRowStartIndex = pageSize * (this._value.currentPage - 1);
        var rows = data.slice(pageRowStartIndex, pageRowEndIndex);

        return {
            totalRowCount: data.length,
            rows: rows,
            pageCount: pageCount,
            pageRowEndIndex: pageRowEndIndex > data.length ? data.length : pageRowEndIndex,
            pageRowStartIndex: pageRowStartIndex,
            next: pageRowEndIndex < data.length ? this._value.currentPage + 1 : false,
            prev: pageRowStartIndex > 0 ? this._value.currentPage - 1 : false
        };
    };

    Heatmap.prototype.setMarginsOnce = function (measuredLabels) {
        if (this._labelsMargins) {
            return;
        }
        var labelBufferMargin = 15; // leave some wiggleroom for longer labels (e.g. counting up labels)
        var titlesHeight = this.getTitlesHeight();
        var headerHeight = Math.max(knimeService.headerHeight(), titlesHeight);

        this._margin = JSON.parse(JSON.stringify(this._defaultMargin));
        this._margin.top = measuredLabels.x.max.maxHeight + labelBufferMargin + this._defaultMargin.top + headerHeight;
        this._margin.left = measuredLabels.y.max.maxWidth + labelBufferMargin + this._defaultMargin.left;

        this._labelsMargins = {
            x: measuredLabels.y.max.maxWidth + labelBufferMargin,
            y: measuredLabels.x.max.maxHeight + labelBufferMargin
        };
    };

    /**
     * Format the data on a per-row level and
     * - get current row names
     * - get row label images
     * - get labels
     * @param {Array} rows
     * @return {Object} formatted page data
     */
    Heatmap.prototype.formatPageData = function (rows) {
        var rowLabelImages = [];
        var rowNames = [];
        var rowLabels = [];
        var self = this;

        var allValues = rows.reduce(function (accumulator, row) {
            rowNames.push(row.rowKey);

            var label = self._representation.labelColumn
                ? self._table.getCell(row.rowKey, self._representation.labelColumn)
                : row.rowKey;
            rowLabels[row.rowKey] = label;

            // Storing images in an separate array is enough
            if (self._representation.svgLabelColumn) {
                rowLabelImages[row.rowKey] = self._table.getCell(row.rowKey, self._representation.svgLabelColumn);
            }

            return accumulator.concat(row);
        }, []);

        var measuredLabels = this.getMarginsForLabels(rowLabels);
        this.setMarginsOnce(measuredLabels);

        return {
            rowLabelImages: rowLabelImages,
            data: allValues,
            rowNames: rowNames,
            measuredLabels: measuredLabels
        };
    };

    /**
     * Search for a data cell based on the mouse position
     * This needs some offsetting as the position calculated via d3 doesn't match the mouseposition
     * due to relative positioning of the canvas
     *
     * @param {e} e current mouse event
     * @return {Object} cell data
     */
    Heatmap.prototype.lookupCell = function (e) {
        if (!this._value.showToolTips && !this._value.enableSelection) {
            return null;
        }

        var offsetX = (e.clientX - this._margin.left - this._value.zoomX) / this._value.zoomK;
        var offsetY = (e.clientY - this._margin.top - this._value.zoomY) / this._value.zoomK;

        var xEachBand = this._scales.x.step();
        var xIndex = Math.floor(offsetX / xEachBand);
        var xVal = this._scales.x.domain()[xIndex];
        var xPos = this._scales.x(xVal);
        var yEachBand = this._scales.y.step();
        var yIndex = Math.floor(offsetY / yEachBand);
        var yVal = this._scales.y.domain()[yIndex];
        var yPos = this._scales.y(yVal);

        var value;
        var cell = {};
        if (xVal && yVal) {
            value = this._table.getCell(yVal, xVal);

            cell = {
                x: xVal,
                y: yVal,
                value: value
            };
        } else {
            return null;
        }

        this._cellHighlighter.setAttribute('x', xPos);
        this._cellHighlighter.setAttribute('y', yPos);
        this._cellHighlighter.setAttribute('width', this._cellWidth);
        this._cellHighlighter.setAttribute('height', this._cellHeight);

        return cell;
    };

    /**
     * Interpolate values to create a value range of all the colors
     * @param {Number} minimum
     * @param {Number} maximum
     * @return {Array} color domain
     */
    Heatmap.prototype.getLinearColorDomain = function (minimum, maximum) {
        var domain = [];
        var interpolator = d3.interpolateNumber(minimum, maximum);
        var color;
        for (var i = 0; i < this._colorRange.length; i++) {
            color = interpolator(i / (this._colorRange.length - 1));
            if (!isNaN(color)) {
                domain.push(color);
            }
        }
        return domain;
    };

    Heatmap.prototype.createScales = function (formattedDataset) {
        return {
            x: d3
                .scaleBand()
                .range([this._margin.left, this._representation.columns.length * this._cellWidth + this._margin.left])
                .domain(this._representation.columns),
            y: d3
                .scaleBand()
                .domain(formattedDataset.rowNames)
                .range([this._margin.top, formattedDataset.rowNames.length * this._cellHeight + this._margin.top]),
            colorScale: this._value.continuousGradient
                ? d3
                    .scaleLinear()
                    .domain(this.getLinearColorDomain(this._representation.minValue, this._representation.maxValue))
                    .range(this._colorRange)
                : d3
                    .scaleQuantize()
                    .domain([this._representation.minValue, this._representation.maxValue])
                    .range(this._colorRange)
        };
    };

    Heatmap.prototype.createAxis = function (formattedDataset) {
        return {
            x: d3.axisTop(this._scales.x).tickFormat(function (d) {
                var label = formattedDataset.measuredLabels.x.values.filter(function (value) {
                    return value && value.originalData === d;
                })[0];
                var title = document.createElementNS(svgNS, 'title');
                title.innerHTML = d;
                this.parentNode.appendChild(title);
                return label && label.truncated ? label.truncated : label.originalData;
            }),

            y: d3.axisLeft(this._scales.y).tickFormat(function (d) {
                var index = formattedDataset.rowNames.indexOf(d);
                var label = formattedDataset.measuredLabels.y.values[index];
                var title = document.createElementNS(svgNS, 'title');
                title.innerHTML = d;
                this.parentNode.appendChild(title);
                return label && label.truncated ? label.truncated : label.originalData;
            })
        };
    };

    /**
     * One time initialization of the zoom
     * @return {Undefined}
     */
    Heatmap.prototype.initializeZoom = function () {
        var svgD3 = d3.select('.knime-svg-container svg');

        var xAxisD3El = svgD3.select('.knime-axis.knime-x');
        var yAxisD3El = svgD3.select('.knime-axis.knime-y');
        var xAxisEl = xAxisD3El.node();
        var yAxisEl = yAxisD3El.node();
        var xAxisWidth = xAxisEl ? xAxisEl.getBoundingClientRect().width : 0;
        var yAxisHeight = yAxisEl ? yAxisEl.getBoundingClientRect().height : 0;

        // Set transform origins
        if (xAxisD3El.node()) {
            xAxisD3El.node().style['transform-origin'] = this._margin.left + 'px 0';
        }
        if (yAxisD3El.node()) {
            yAxisD3El.node().style['transform-origin'] = '0 ' + this._margin.top + 'px';
        }

        this._transformer.node().style['transform-origin'] = this._margin.left + 'px ' + this._margin.top + 'px';
        var infoWrapperHeight = document.querySelector('.info-wrapper').getBoundingClientRect().height || 0;

        this._zoomDimensions = {
            xAxisWidth: xAxisWidth,
            yAxisHeight: yAxisHeight,
            minimalZoomLevel: Math.min(
                (window.innerWidth - this._margin.left - this._margin.right) / xAxisWidth,
                (window.innerHeight - this._margin.top - infoWrapperHeight - this._margin.bottom) / yAxisHeight
            )
        };
        return this.setZoomEvents();
    };

    /**
     * Repeatedly called method to change zoom properties
     * or disable/enable the zoom
     * @return {Undefined}
     */
    Heatmap.prototype.setZoomEvents = function () {
        var svgD3 = d3.select('.knime-svg-container svg');
        var xAxisD3El = svgD3.select('.knime-axis.knime-x');
        var yAxisD3El = svgD3.select('.knime-axis.knime-y');
        var transformerNode = this._transformer.node();
        var self = this;

        var zoom = d3
            .zoom()
            .translateExtent([
                [0, 0],
                [
                    this._zoomDimensions.xAxisWidth + this._margin.left + this._margin.right,
                    this._zoomDimensions.yAxisHeight
                ]
            ])
            .scaleExtent([this._zoomDimensions.minimalZoomLevel, 1])
            .constrain(function (transform, extent, translateExtent) {
                // see https://github.com/d3/d3-zoom/blob/master/README.md#zoom_constrain
                // the translate extent needs to dynamically append to the zoom level, therefore we need to overwrite it
                var theight = translateExtent[1][1] + (self._margin.top + self._margin.bottom - 1) / transform.k;

                var dx0 = transform.invertX(extent[0][0]) - translateExtent[0][0];
                var dx1 = transform.invertX(extent[1][0]) - translateExtent[1][0];
                var dy0 = transform.invertY(extent[0][1]) - translateExtent[0][1];
                var dy1 = transform.invertY(extent[1][1]) - theight;

                return transform.translate(
                    dx1 > dx0 ? (dx0 + dx1) / 2 : Math.min(0, dx0) || Math.max(0, dx1),
                    dy1 > dy0 ? (dy0 + dy1) / 2 : Math.min(0, dy0) || Math.max(0, dy1)
                );
            })
            .on('zoom', function () {
                var t = d3.event.transform;

                // prevent jumpy layout
                t.x = t.x > 0 ? 0 : t.x;
                t.y = t.y > 0 ? 0 : t.y;

                xAxisD3El.attr('transform', 'translate(' + t.x + ', ' + self._margin.top + ') scale(' + t.k + ')');
                yAxisD3El.attr('transform', 'translate(' + self._margin.left + ', ' + t.y + ') scale(' + t.k + ')');
                transformerNode.setAttribute('transform', 'translate(' + t.x + ', ' + t.y + ') scale(' + t.k + ')');

                self._value.zoomX = t.x;
                self._value.zoomY = t.y;
                self._value.zoomK = t.k;

                // update mask
                if (self._axisMaskTopLeft) {
                    self._axisMaskTopLeft
                        .attr('width', self._margin.left + 2 * t.k)
                        .attr('height', self._margin.top + 2 * t.k);
                }

                // hack: force canvas refresh as sometimes canvas gets not fully painted
                self._transformer.node().style.opacity = 0.999;
                setTimeout(function () {
                    self._transformer.node().style.opacity = 1;
                }, 0);
            })
            .on('end', function () {
                self.togglePartiallyDisplayedClass();

                // style borders
                var strokeWidth = self.getCurrentStrokeWidth();
                self._cellHighlighter.setAttribute('stroke-width', strokeWidth);
                var rowHighlighters = svgD3.node().querySelectorAll('.row-highlighter');
                if (rowHighlighters.length) {
                    for (var i = 0; i < rowHighlighters.length; i++) {
                        rowHighlighters[i].setAttribute('stroke-width', strokeWidth);
                    }
                }
            });

        // reset
        svgD3.on('.zoom', null);

        // init
        if (this._value.enableZoom || this._value.enablePanning) {
            svgD3.call(zoom);
        }

        // disable zoom events
        if (!this._value.enableZoom) {
            svgD3.on('wheel.zoom', null).on('dblclick.zoom', null);
        }

        // disable panning events
        if (!this._value.enablePanning) {
            svgD3
                .on('mousedown.zoom', null)
                .on('touchstart.zoom', null)
                .on('touchmove.zoom', null)
                .on('touchend.zoom', null);
        }
        return zoom;
    };

    Heatmap.prototype.getCurrentStrokeWidth = function () {
        var strokeZoomFactor = 5;
        return Math.max(2, 1 + strokeZoomFactor * (1 - this._value.zoomK));
    };

    Heatmap.prototype.showTooltip = function (e, innerHtml) {
        if (!this._value.showToolTips && innerHtml) {
            return;
        }
        this._tooltip.classList.add('active');
        this._tooltip.innerHTML = innerHtml;
        var tooltipWidth = this._tooltip.getBoundingClientRect().width;
        var tooltipHeight = this._tooltip.getBoundingClientRect().height;
        var leftPos = e.clientX;
        var topPos = e.clientY - tooltipHeight;

        // make sure tooltip is visible on the right
        if (leftPos + tooltipWidth >= (window.innerWidth || document.documentElement.clientWidth)) {
            leftPos -= tooltipWidth;
        }
        this._tooltip.style.left = leftPos + 'px';
        this._tooltip.style.top = topPos + 'px';
    };

    Heatmap.prototype.hideTooltip = function () {
        this._tooltip.classList.remove('active');
    };

    /**
     * Because the browser has canvas size limits, we insert a new canvas "quadrant"
     * before the maximum height is reached.
     *
     * @param {Number} xExtension the x position the column is at
     * @param {Number} yExtension the y position the row is at
     * @param  {Number} canvasHeight The canvas height
     * @param  {Number} canvasWidth The canvas width
     * @return {Context} Canvas context to draw on
     */
    Heatmap.prototype.appendCanvasQuadrant = function (xExtension, yExtension, canvasHeight, canvasWidth) {
        var canvas = document.createElementNS(xhtmlNS, 'canvas');
        var id = 'c' + Math.round(this._maxExtensionX) + '-' + Math.round(this._maxExtensionY);

        canvas.setAttribute(
            'style',
            'position: absolute;top:' +
            yExtension +
            'px;left:  ' +
            xExtension +
            'px;width:' +
            canvasWidth +
            'px;height:' +
            canvasHeight +
            'px'
        );
        canvas.setAttribute('width', window.devicePixelRatio * canvasWidth + 'px');
        canvas.setAttribute('height', window.devicePixelRatio * canvasHeight + 'px');


        this._canvases[id] = {
            el: canvas,
            id: id,
            transformX: xExtension,
            transformY: yExtension,
            width: canvasWidth,
            height: canvasHeight
        };
        // append canvas for preview before converting to inline image later
        this._transformer.node().querySelector('.preview').appendChild(canvas);

        return canvas.getContext('2d');
    };

    /**
     * Get the current canvas context to draw on
     * @param {Number} x position of the current row/cell
     * @param {Number} y position of the current row/cell
     * @return {Context} Canvas Context
     */
    Heatmap.prototype.getContext = function (x, y) {
        var yExtension = this._scales.y(y);
        var xExtension = this._scales.x(x);
        var maxRows = Math.ceil(this._maxCanvasHeight / window.devicePixelRatio / this._cellHeight);
        var maxCols = Math.ceil(this._maxCanvasHeight / window.devicePixelRatio / this._cellWidth);
        this._maxExtensionX = this._maxExtensionX || 0;
        this._maxExtensionY = this._maxExtensionY || 0;

        var canvasHeight = Math.min(this._scales.y.domain().length * this._cellHeight, maxRows * this._cellHeight);
        var canvasWidth = Math.min(this._scales.x.domain().length * this._cellWidth, maxCols * this._cellWidth);
        var context;
        var rowEls = this._scales.x.domain();

        if (rowEls.indexOf(x) === 0) {
            // at the start of the row, reset max extension for x
            this._maxExtensionX = xExtension + canvasWidth;
        }

        // Extend the limits of the canvas
        if (xExtension + this._cellWidth > this._maxExtensionX) {
            this._maxExtensionX = xExtension + canvasWidth;
        }
        if (yExtension + this._cellHeight > this._maxExtensionY) {
            this._maxExtensionY = yExtension + canvasHeight;
        }

        // Check if canvas already exists
        var canvasIndex = 'c' + Math.round(this._maxExtensionX) + '-' + Math.round(this._maxExtensionY);
        var currentCanvas = this._canvases[canvasIndex];
        if (currentCanvas && currentCanvas.el) {
            return currentCanvas.el.getContext('2d');
        } else {
            // else create a new quadrant
            context = this.appendCanvasQuadrant(xExtension, yExtension, canvasHeight, canvasWidth);
            context.scale(window.devicePixelRatio, window.devicePixelRatio);
            context.translate(-xExtension, -yExtension);

        }

        return context;
    };

    Heatmap.prototype.getCellColor = function (value) {
        if (value === null) {
            return this._representation.missingValueColor;
        }
        if (value > this._representation.maxValue) {
            return this._representation.upperOutOfRangeColor;
        }
        if (value < this._representation.minValue) {
            return this._representation.lowerOutOfRangeColor;
        }
        return this._scales.colorScale(value);
    };

    Heatmap.prototype.drawCanvasRow = function (row) {
        var y = row.rowKey;
        var self = this;
        row.data.forEach(function (value, currentIndex) {
            if (typeof self._colNames[currentIndex] === 'undefined') {
                return;
            }
            var x = self._colNames[currentIndex];

            var context = self.getContext(x, y);
            context.fillStyle = self.getCellColor(value);
            context.fillRect(self._scales.x(x), self._scales.y(y), self._cellWidth, self._cellHeight);
        });
    };

    Heatmap.prototype.drawSvgRow = function (row) {
        var y = row.rowKey;
        var self = this;
        var cellGroup = this._transformer.select('.rows').append('g');

        row.data.forEach(function (value, currentIndex) {
            if (typeof self._colNames[currentIndex] === 'undefined') {
                return;
            }
            var cell = {
                x: self._colNames[currentIndex],
                y: y,
                value: value
            };

            cellGroup
                .data([cell])
                .append('rect')
                .attr('class', 'cell')
                .attr('width', self._cellWidth)
                .attr('height', self._cellHeight)
                .attr('y', function (d) {
                    return self._scales.y(d.y);
                })
                .attr('x', function (d) {
                    return self._scales.x(d.x);
                })
                .attr('fill', function (d) {
                    return self.getCellColor(d.value);
                });
        });
    };

    /**
     * Set new margins based on the label sizes
     * @param {Array} rowLabels which the margins are calculated with
     * @return {Undefined}
     */
    Heatmap.prototype.getMarginsForLabels = function (rowLabels) {
        var maxLabelPercentage = 0.33;
        var container = document.querySelector('svg.heatmap');
        var maxWidth = this._labelsMargins
            ? this._labelsMargins.x
            : container.getBoundingClientRect().width * maxLabelPercentage;
            
        var maxHeight = this._labelsMargins
            ? this._labelsMargins.y
            : container.getBoundingClientRect().height * maxLabelPercentage;

        var measuredLabelsY = knimeService.measureAndTruncate(d3.values(rowLabels), {
            tempContainerClasses: 'active',
            container: container,
            classes: 'knime-tick-label',
            maxWidth: maxWidth
        });

        var attributes = [];
        attributes.transform = this._xAxisLabelTransform;

        var measuredLabelsX = knimeService.measureAndTruncate(this._colNames, {
            container: container,
            classes: 'knime-tick-label',
            attributes: attributes,
            maxHeight: maxHeight
        });

        return {
            y: measuredLabelsY,
            x: measuredLabelsX
        };
    };

    Heatmap.prototype.drawContents = function (rows) {
        var infoWrapperHeight;
        var self = this;
        if (this._drawCellQueue) {
            this._drawCellQueue.invalidate();
        }

        if (!rows || rows.length === 0) {
            d3.select('.heatmap .wrapper').remove();
            d3.select('.heatmap .axis-wrapper').remove();
            return;
        }

        var formattedDataset = this.formatPageData(rows);
        this._colorRange = this._value.continuousGradient
            ? this._representation.threeColorGradient
            : this._representation.discreteGradientColors;

        var svg = d3.select('.knime-svg-container svg');

        // Create titles
        svg.append('text')
            .attr('class', 'knime-title')
            .attr('x', this._defaultMargin.left)
            .attr('y', this.titleHeight)
            .text(this._value.chartTitle);
        svg.append('text')
            .attr('class', 'knime-subtitle')
            .attr('x', this._defaultMargin.left)
            .attr('y', this.titleHeight + this.subtitleHeight)
            .text(this._value.chartSubtitle);
        this.updateTitles();


        // Determine cell sizes
        if (!this._cellHeight || !this._cellWidth) {
            infoWrapperHeight = document.querySelector('.info-wrapper').getBoundingClientRect().height || 0;
            var extraAxisLabelBuffer = 30; // TODO: calculate programatically
            var headerHeight = Math.max(knimeService.headerHeight(), this.getTitlesHeight());
            var containerWidth = this._representation.resizeToWindow
                ? window.innerWidth
                : this._representation.imageWidth;
            var containerHeight = this._representation.resizeToWindow
                ? window.innerHeight
                : this._representation.imageHeight;
            this._cellWidth = Math.max(
                this._minCellSize,
                (containerWidth - this._margin.left - this._margin.right - extraAxisLabelBuffer) /
                this._representation.columns.length
            );
            this._cellHeight = Math.max(
                this._minCellSize,
                (containerHeight - this._margin.top - headerHeight - infoWrapperHeight) / rows.length
            );
        }

        this._tooltip = document.querySelector('.knime-tooltip');
        this._scales = this.createScales(formattedDataset);
        this._axis = this.createAxis(formattedDataset);

        var defs = svg.append('defs');
        defs.append('clipPath')
            .attr('id', 'clip')
            .append('rect')
            .attr('y', this._margin.top + 1)
            .attr('x', this._margin.left + 1)
            .attr('width', '100%')
            .attr('height', '100%');


        var wrapper = svg.append('g').attr('clip-path', 'url(#clip)');
        this._transformer = wrapper.append('g').attr('class', 'transformer');
        this._transformer.append('foreignObject').attr('class', 'preview');
        this._transformer.append('g').attr('class', 'highlighters');

        var renderRate = 500;
        if (this._representation.runningInView) {
            // Improve performance: render cells progressively
            this._maxExtensionY = 0;
            this._maxExtensionX = 0;
            this._canvases = [];
            this._drawCellQueue = renderQueue(this.drawCanvasRow.bind(this)).rate(renderRate);
            this._drawCellQueue(formattedDataset.data);
        } else {
            // Render cells at once for image rendering
            this._transformer.append('g').attr('class', 'rows');
            formattedDataset.data.forEach(function (row) {
                self.drawSvgRow(row);
            });
        }

        this._cellHighlighter = document.createElementNS(svgNS, 'rect');
        this._cellHighlighter.setAttribute('width', 0);
        this._cellHighlighter.setAttribute('height', 0);
        this._cellHighlighter.setAttribute('class', 'cell-highlighter');
        document.querySelector('.knime-svg-container .transformer .highlighters').appendChild(this._cellHighlighter);

        this.registerEvents();

        this.getProgressBar(formattedDataset.data.length);

        this.drawAxis(svg, formattedDataset.rowLabelImages);

        this.drawLegend(svg);

        // Initialize and reset zoom
        // when row number is different than previous, reset to default to keep everything visible
        this.resetZoom(this._previousDataLength !== formattedDataset.data.length);
        this._previousDataLength = formattedDataset.data.length;

        this.styleSelectedRows();

        // Set a bottom padding to prevent footer overlapping the chart
        infoWrapperHeight = document.querySelector('.info-wrapper').getBoundingClientRect().height || 0;
        document.querySelector('.knime-svg-container').style.paddingBottom = infoWrapperHeight + 'px';
        document.querySelector('.gradient-x').style.bottom = infoWrapperHeight + 'px';

        // Set gradient overlays
        document.querySelector('.gradient-x').style.height = this._margin.bottom + 'px';
        document.querySelector('.gradient-y').style.width = this._margin.right + 'px';

        // add some general CSS classes
        this.togglePanningClass();
        this.toggleSelectionClass();
        this.togglePartiallyDisplayedClass();

        this.resizeSvg(svg);
    };

    Heatmap.prototype.resetZoom = function (setToDefault) {
        var zoom = this.initializeZoom();
        var svg = d3.select('.knime-svg-container svg');
        var resetLevel = setToDefault
            ? {
                x: this._defaultZoomX,
                y: this._defaultZoomY,
                k: this._defaultZoomK
            }
            : {
                x: this._value.zoomX,
                y: this._value.zoomY,
                k: this._value.zoomK
            };
        zoom.transform(svg, function () {
            return d3.zoomIdentity.translate(resetLevel.x, resetLevel.y).scale(resetLevel.k);
        });
        return zoom;
    };

    Heatmap.prototype.drawAxis = function (svg, rowLabelImages) {
        var self = this;

        // Append axis
        var maskAxis = svg
            .select('defs')
            .append('mask')
            .attr('id', 'maskAxis')
            .attr('maskUnits', 'userSpaceOnUse');
        maskAxis
            .append('rect')
            .attr('y', 0)
            .attr('x', 0)
            .attr('width', '100%')
            .attr('height', '100%')
            .attr('fill', 'white');
        this._axisMaskTopLeft = maskAxis
            .append('rect')
            .attr('y', 0)
            .attr('x', 0)
            .attr('width', this._margin.left + 2)
            .attr('height', this._margin.top + 2)
            .attr('fill', 'black');
        maskAxis
            .append('rect')
            .attr('height', 2)
            .attr('width', 2)
            .attr('x', this._margin.left)
            .attr('y', this._margin.top)
            .attr('fill', 'white');

        var axisWrapper = svg
            .append('g')
            .attr('class', 'axis-wrapper')
            .attr('mask', 'url(#maskAxis)');
        axisWrapper
            .append('g')
            .attr('class', 'knime-axis knime-y')
            .call(this._axis.y)
            .selectAll('text')
            .attr('font-weight', 'normal')
            .on('mouseover', function (d) {
                if (!rowLabelImages[d]) {
                    return;
                }
                var tooltipInnerHTML =
                    '<img src="data:image/svg+xml;base64,' + window.btoa(rowLabelImages[d]) + '" alt/>';
                self.showTooltip(d3.event, tooltipInnerHTML);
            })
            .on('mouseleave', function () {
                self.hideTooltip();
            });

        axisWrapper
            .append('g')
            .attr('class', 'knime-axis knime-x')
            .call(this._axis.x)
            .selectAll('text')
            .attr('font-weight', 'normal')
            .style('text-anchor', 'start')
            .attr('transform', this._xAxisLabelTransform);

        // general tick styling
        var ticks = axisWrapper.selectAll('.tick').attr('class', 'knime-tick');
        ticks.select('text').attr('class', 'knime-label knime-tick-label');
        ticks.select('line').attr('class', 'knime-tick-line');

        axisWrapper
            .selectAll('.knime-axis.knime-y .knime-tick')
            .attr('class', function (d) {
                if (self._value.selection.indexOf(d) > -1) {
                    return 'knime-tick active';
                } else {
                    return 'knime-tick';
                }
            })
            .attr('data-id', function (d) {
                return d;
            })
            .on('click', function (d) {
                self.selectSingleRow(d);
            });
    };

    Heatmap.prototype.resizeSvg = function (svg) {
        if (this._representation.runningInView && !this._representation.resizeToWindow) {
            var container = document.querySelector('.knime-layout-container');
            if (this._representation.imageHeight) {
                container.style.height = this._representation.imageHeight + 'px';
            }
            if (this._representation.imageWidth) {
                container.style.width = this._representation.imageWidth + 'px';
            }
        }
        if (!this._representation.runningInView) {
            var imageMargin = 50;
            var imageModeMarginTop = this._scales.y.domain().length * this._cellHeight +
                this._margin.top + this._legendTopMargin;
            var calcImageHeight = imageModeMarginTop + this._legendHeight + imageMargin;
            var calcImageWidth = this._representation.columns.length * this._cellWidth +
                this._margin.left + this._margin.right + imageMargin;
            svg.attr('viewBox', '0 0 ' + calcImageWidth + ' ' + calcImageHeight);
            if (this._representation.imageHeight) {
                svg.attr('height', this._representation.imageHeight + 'px');
            }
            if (this._representation.imageWidth) {
                svg.attr('width', this._representation.imageWidth + 'px');
            }
        }
    };

    /**
     * Calculate which ticks to hide,
     * maximum and minimum should be always shown
     * @return {Undefined}
     */
    Heatmap.prototype.removeLegendTickOverlap = function () {
        var legend = document.querySelector('.knime-legend');
        if (!legend) {
            return;
        }
        var ticks =  legend.querySelectorAll('.tick');

        for (var j = 1; j < ticks.length; j++) {
            var currentTick = ticks[j];

            // the number of previous ticks we check for overlapping
            var lookBehind = 5;
            var isLastTick = j === ticks.length - 1;

            for (var g = 1; g <= lookBehind; g++) {
                var prevTick = ticks[j - g];
                if (prevTick) {
                    this.removeTickOverlap(currentTick, prevTick, isLastTick);
                }
            }
        
        }
    };

    Heatmap.prototype.removeElementFromDOM = function (el) {
        if (el.parentNode) {
            el.parentNode.removeChild(el);
        }
    };

    Heatmap.prototype.removeTickOverlap = function (currentTick, prevTick, isLastTick) {
        var minimumSpacing = 2;
        var curTickRect = currentTick.getBoundingClientRect();
        var startPosition = curTickRect.left;
        var prevTickRect = prevTick.getBoundingClientRect();
        var prevEndposition = prevTickRect.left + prevTickRect.width + minimumSpacing;

        if (prevEndposition > startPosition) {
            if (isLastTick) {
                // Last tick for showing maximum should not be removed,
                // remove tick before that instead
                this.removeElementFromDOM(prevTick);
            } else {
                // current tick has not yet been removed if parent element exists
                this.removeElementFromDOM(currentTick);
            }
        }
    };

    Heatmap.prototype.drawLegend = function (svg) {
        var legend;

        var legendWidth = this._value.continuousGradient ? this._continousLegendWidth : this._discreteLegendWidth;

        if (this._representation.runningInView) {
            // append a separate svg
            legend = d3
                .select('.info-wrapper')
                .append('svg')
                .attr('class', 'knime-legend')
                .attr('width', legendWidth + 2 * this._legendStandardMargin)
                .attr('height', this._legendHeight);
        } else {
            // append in existing svg
            var imageModeMarginTop = this._scales.y.domain().length * this._cellHeight +
                this._margin.top + this._legendTopMargin;
            var transform = 'translate(' + this._defaultMargin.left + ' ' + imageModeMarginTop + ')';

            legend = svg
                .append('g')
                .attr('class', 'knime-legend')
                .attr('width', legendWidth + 2 * this._legendStandardMargin)
                .attr('height', this._legendHeight)
                .attr('transform', transform);
        }

        var legendDefs = legend.append('defs');
        var legendGradient = legendDefs.append('linearGradient').attr('id', 'legendGradient');

        var colorDomain = this.getLinearColorDomain(this._representation.minValue, this._representation.maxValue);

        var legendSymbolContainer =
            legend
                .append('svg')
                .attr('y', 0)
                .attr('x', this._legendStandardMargin)
                .attr('class', 'knime-legend-symbol')
                .attr('width', legendWidth)
                .attr('height', this._legendColorRangeHeight)
                .attr('right', this._legendStandardMargin);

        // set gradient stops
        var tickValues;
        if (this._value.continuousGradient) {
            // append a single rect to display a gradient
            legendSymbolContainer
                .append('rect')
                .attr('x', 0)
                .attr('y', 0)
                .attr('width', '100%')
                .attr('height', '100%')
                .attr('fill', 'url(#legendGradient)');

            for (var i = 0; i < colorDomain.length; i++) {
                var percentage = 100 / (colorDomain.length - 1) * i;
                legendGradient
                    .append('stop')
                    .attr('offset', percentage + '%')
                    .attr('style', 'stop-opacity:1; stop-color:' + this._scales.colorScale(colorDomain[i]));
            }
        } else if (!this._value.continuousGradient) {
            var legendCellPercentage = 100 / colorDomain.length;
            var previousPercentage = 0;
            var interpolator = d3.interpolateNumber(this._representation.minValue, this._representation.maxValue);

            tickValues = [this._representation.minValue];

            for (var j = 0; j < colorDomain.length; j++) {
                var currentPercentage = legendCellPercentage * (j + 1);

                tickValues.push(interpolator(currentPercentage / 100));
                
                var color = this._scales.colorScale(colorDomain[j]);
                var colorCell = legendSymbolContainer
                    .append('g');

                colorCell.append('rect')
                    .attr('x', previousPercentage  + '%')
                    .attr('y', 0)
                    .attr('style', 'position: absolute; top: 0; left: ' + previousPercentage + '%')
                    .attr('width', currentPercentage - previousPercentage + '%')
                    .attr('height', '100%')
                    .attr('fill', color);

                colorCell.append('svg:title')
                    .text(
                        Math.round(interpolator(previousPercentage / 100) * 100) / 100 +
                        ' | ' +
                        Math.round(interpolator(currentPercentage / 100) * 100) / 100
                    );

                previousPercentage = currentPercentage;
            }

        }

        var legendScale = d3
            .scaleLinear()
            .domain([this._representation.minValue, this._representation.maxValue])
            .range([0, legendWidth]);

        var legendAxis = d3
            .axisBottom(legendScale)
            .tickValues(tickValues || colorDomain)
            .tickFormat(function (d) {
                return Math.round(d * 100) / 100;
            });

        var axis = legend
            .append('g')
            .attr('transform', 'translate(' + this._legendStandardMargin + ', ' + this._legendColorRangeHeight + ')')
            .attr('class', 'legend-axis')
            .call(legendAxis);

        axis
            .selectAll('text')
            .attr('class', 'knime-legend-label')
            .attr('font-weight', 'normal');

        if (axis && axis.node().getBoundingClientRect().width > legendWidth) {
            // make legend svg wider if axis is wider, for example if tick values are too long
            legend.attr('width', axis.node().getBoundingClientRect().width);
        }
        this.removeLegendTickOverlap();

    };

    /**
     * Select multiple rows via shiftkey
     *
     * @param {String} selectedRowId
     * @return {Undefined}
     */
    Heatmap.prototype.selectDeltaRow = function (selectedRowId) {
        if (!this._value.selection.length) {
            // Delta selection is not possible if no row is selected
            return;
        }
        // Get closest selected row to newly selected row
        var rowNames = this._scales.y.domain();
        var currentIndex = rowNames.indexOf(selectedRowId);
        var closestRow = this._value.selection.reduce(
            function (closestRow, rowId) {
                var rowIdIndex = rowNames.indexOf(rowId);
                var indexDistance = Math.abs(currentIndex - rowIdIndex);
                if (indexDistance < closestRow.distance) {
                    return {
                        distance: indexDistance,
                        index: rowIdIndex
                    };
                }
                return closestRow;
            },
            {
                distance: Number.POSITIVE_INFINITY,
                index: Number.POSITIVE_INFINITY
            }
        );

        var startIndex = Math.min(closestRow.index, currentIndex);
        var endIndex = Math.max(closestRow.index, currentIndex);
        var rowKey;
        for (var i = startIndex; i <= endIndex; i++) {
            rowKey = rowNames[i];
            if (!this._value.selection.indexOf(rowKey) > -1) {
                this._value.selection.push(rowKey);
            }
        }

        this.styleSelectedRows();

        if (this._value.publishSelection) {
            knimeService.setSelectedRows(this._table.getTableId(), this._value.selection);
        }
    };

    Heatmap.prototype.selectSingleRow = function (selectedRowId, keepCurrentSelections) {
        if (!this._value.enableSelection) {
            return;
        }

        // Cast optional parameter to boolean
        keepCurrentSelections = Boolean(keepCurrentSelections);

        if (!keepCurrentSelections) {
            // Remove all selections
            this._value.selection = [];
            if (this._value.publishSelection) {
                knimeService.setSelectedRows(this._table.getTableId(), []);
            }
            this.styleSelectedRows();
        }

        if (this._value.selection.indexOf(selectedRowId) > -1) {
            this._value.selection = this._value.selection.filter(function (rowId) {
                return rowId !== selectedRowId;
            });
            this.styleSelectedRows();
            if (this._value.publishSelection) {
                knimeService.removeRowsFromSelection(this._table.getTableId(), [selectedRowId]);
            }
        } else {
            this._value.selection.push(selectedRowId);
            this.styleSelectedRows();
            if (this._value.publishSelection) {
                knimeService.addRowsToSelection(this._table.getTableId(), [selectedRowId]);
            }
        }

        if (this._value.showSelectedRowsOnly) {
            this.drawChart();
        }
    };

    Heatmap.prototype.styleSelectedRows = function () {
        var self = this;
        d3.selectAll('.knime-axis.knime-y .knime-tick').attr('class', 'knime-tick');

        // Style row labels
        this._value.selection.forEach(function (selectedRowId) {
            d3.select('.knime-axis.knime-y [data-id="' + selectedRowId + '"]').attr('class', 'knime-tick active');
        });

        // remove row highlighters
        var rowHighlighters = document.querySelectorAll('.row-highlighter');
        if (rowHighlighters.length) {
            for (var i = 0; i < rowHighlighters.length; i++) {
                this.removeElementFromDOM(rowHighlighters[i]);
            }
        }

        this._value.selection = this.sortByDatasetRows(this._value.selection.filter(this.onlyUniques));
        var startRowId = false;
        var selectionEnded = true;
        var yDomain = this._scales.y.domain();
        yDomain.forEach(function (rowId, rowIndex) {
            if (self._value.selection.indexOf(rowId) > -1) {
                if (!startRowId && selectionEnded) {
                    selectionEnded = false;
                    startRowId = rowId;
                }
            } else if (!selectionEnded && startRowId) {
                selectionEnded = true;
                self.endSelection(startRowId, yDomain[rowIndex - 1]);
                startRowId = false;
            }
        });
    };

    Heatmap.prototype.endSelection = function (startRowId, endRowId) {
        var startPosition = this._scales.y(startRowId);
        var endPosition = this._scales.y(endRowId) + this._cellHeight;
        var highlighter = document.createElementNS(svgNS, 'rect');
        highlighter.setAttribute('class', 'row-highlighter');

        highlighter.setAttribute('y', startPosition);
        highlighter.setAttribute('x', this._margin.left);
        highlighter.setAttribute('height', endPosition - startPosition);
        highlighter.setAttribute('width', this._representation.columns.length * this._cellWidth);
        highlighter.setAttribute('borderWidth', this.getCurrentStrokeWidth());
        var container = document.querySelector('.highlighters');
        if (container) {
            container.appendChild(highlighter);
        }
    };

    /**
     * Check if the axis overlap with the 'fade-out' gradients
     * to see if all the content is displayed
     *
     * @param {Element} xAxis
     * @param {Element} yAxis
     * @return {Boolean} is visible
     */
    Heatmap.prototype.areAxisCompletelyVisible = function (xAxis, yAxis) {
        var yAxisRect = yAxis.getBoundingClientRect();
        var xAxisRect = xAxis.getBoundingClientRect();
        var yAxisEndPos = yAxisRect.height + yAxisRect.top;
        var xAxisEndPos = xAxisRect.width + yAxisRect.left;
        var gradientXTopPos = document.querySelector('.gradient-x').getBoundingClientRect().top;
        var gradientYLeftPos = document.querySelector('.gradient-y').getBoundingClientRect().left;

        return Math.floor(yAxisEndPos) <= gradientXTopPos && Math.floor(xAxisEndPos) <= gradientYLeftPos;
    };

    Heatmap.prototype.onlyUniques = function (value, index, array) {
        return array.indexOf(value) === index;
    };

    Heatmap.prototype.sortByDatasetRows = function (arr) {
        var yDomain = this._scales.y.domain();
        return arr.sort(function (a, b) {
            return yDomain.indexOf(a) - yDomain.indexOf(b);
        });
    };

    /**
     * Create a callback to be called repeatedly via requestAnimationFrame
     * @param {Function} callback to be called repeatedly
     * @param {Number} delay
     * @return {Function} interval function
     */
    Heatmap.prototype.requestInterval = function (callback, delay) {
        var dateNow = Date.now;
        var start = dateNow();
        var stop;
        var interval = function () {
            if (dateNow() - start > delay) {
                start += delay;
                callback();
            }
            return stop || window.requestAnimationFrame(interval);
        };
        window.requestAnimationFrame(interval);
        return {
            clear: function () {
                stop = true;
            }
        };
    };

    return new Heatmap();
})();
