window.tiles_namespace = (function () {

    var htmlEncode = function (x) {
        return x.replace(/&/g, '%26').replace(/</g, '%3C').replace(/>/g, '%3E')
            .replace(/"/g, '%22').replace(/'/g, '%27').replace(/#/g, '%23');
    };

    var TileView = function () {
        this._representation = null;
        this._value = null;
        this._knimeTable = null;
        this._dataTable = null;
        this._selection = {};
        this._partialSelectedRows = [];
        this._currentFilter = null;
        this._initialized = false;
        this._dataTableConfig = null;
        this._infoColsCount = 0;
        this._nonSelectableColsCount = 0;
        this._rowIdColInd = null;
        this._nonHiddenDataIndexes = [];
    };

    TileView.prototype = Object.create(KnimeBaseTableViewer.prototype);
    TileView.prototype.constructor = TileView;

    TileView.prototype.init = function (representation, value) {
        var textAlignment = representation.alignRight ? 'right' : representation.alignCenter ? 'center' : 'left';

        var overrides = {
            displayRowIds: representation.useRowID,
            displayRowIndex: false,
            enableClearSortButton: false,
            enableColumnSearching: false,
            enableSearching: false,
            enableSorting: false,
            singleSelection: false,
            textAlignment: textAlignment
        };

        var options = Object.assign({}, representation, overrides);

        // super call
        KnimeBaseTableViewer.prototype.init.call(this, options, value);
    };

    // filtering
    TileView.prototype._buildMenu = function () {
        this._representation.subscriptionFilterIds = this._knimeTable.getFilterIds();
        KnimeBaseTableViewer.prototype._buildMenu.apply(this);
    };

    // disallow selection of individual cells
    TileView.prototype._cellMouseDownHandler = function () {};

    TileView.prototype._buildColumnDefinitions = function () {
        KnimeBaseTableViewer.prototype._buildColumnDefinitions.call(this);
        var colDefs = this._dataTableConfig.columns;
        var labelCol = this._representation.labelCol;
        var useRowID = this._representation.useRowID;
        if (labelCol || useRowID) {
            for (var i = 0; i < colDefs.length; i++) {
                if ((colDefs[i].title === labelCol) || (useRowID && i === this._rowIdColInd)) {
                    this._labelColIndex = i;
                    var colDef = colDefs[i];
                    colDef.className += ' knime-tile-title';
                    if (useRowID) {
                        // title column is in front already
                        colDef.className += ' knime-row-id';
                    } else {
                        // push title column to the front
                        colDefs.splice(i, 1);
                        colDefs.splice(this._infoColsCount, 0, colDef);
                    }
                    break;
                }
            }
        }
        // render SVGs as <img>s
        colDefs.forEach(function (colDef) {
            if (/\bknime-svg\b/.test(colDef.className)) {
                colDef.render = function (data) {
                    return '<img src="data:image/svg+xml;charset=utf-8,' + htmlEncode(data) + '" />';
                };
            }
        });
        if (this._representation.displayColumnHeaders) {
            this._addColumnTitles();
        }
    };

    // render columns along with cell entries
    TileView.prototype._addColumnTitles = function () {
        var self = this;
        this._dataTableConfig.columns.forEach(function (column) {
            if (!self._shouldShowTitleOnColumn(column)) {
                return;
            }
            var titlePrefix = '<span class="knime-tiles-rowtitle">' + column.title + ':</span> ';
            if (column.hasOwnProperty('render')) {
                column.render = (function (original) {
                    return function (data) {
                        if (typeof data === 'undefined' || data === null) {
                            return null;
                        }
                        return titlePrefix + (original.call(self, data) || '');
                    };
                })(column.render);
            } else {
                column.render = function (data) {
                    if (typeof data === 'undefined' || data === null) {
                        return null;
                    }
                    return titlePrefix + data;
                };
            }
            column.defaultContent = titlePrefix + (column.defaultContent || '');
        });
    };

    // helper for _addColumnTitles()
    TileView.prototype._shouldShowTitleOnColumn = function (column) {
        if (/\b(selection-cell|knime-tile-title|knime-svg|knime-png)\b/.test(column.className)) {
            return false;
        }
        if (!column.hasOwnProperty('title') || !column.title || column.title === 'RowID') {
            return false;
        }
        return true;
    };

    // push title column data to top
    TileView.prototype._getDataSlice = function (start, end) {
        var data = KnimeBaseTableViewer.prototype._getDataSlice.apply(this, arguments);
        if (this._representation.labelCol && this._labelColIndex) {
            var sourceIndex = this._labelColIndex;
            var targetIndex = this._infoColsCount;
            if (!this._representation.useRowID) {
                data.forEach(function (row) {
                    var titleData = row.splice(sourceIndex, 1)[0];
                    row.splice(targetIndex, 0, titleData);
                });
            }
        }
        return data;
    };

    // selection on click
    TileView.prototype._setSelectionHandlers = function () {
        KnimeBaseTableViewer.prototype._setSelectionHandlers.apply(this);
        if (!this._representation.enableSelection) {
            return;
        }
        $('#knimePagedTable tbody').addClass('knime-selection-enabled').on('click', 'tr', function (e) {
            if (e.target && e.target.tagName === 'INPUT' && e.target.type === 'checkbox') {
                return;
            }
            $(e.currentTarget).find('input[type="checkbox"]').click();
        });
    };

    // tile width
    TileView.prototype._prepare = function () {
        KnimeBaseTableViewer.prototype._prepare.apply(this);
        var tileWidth;
        if (this._representation.useColWidth) {
            tileWidth = this._representation.colWidth + 'px';
            if (this._representation.useNumCols) {
                var tableWidth = (this._representation.numCols * (this._representation.colWidth + 2 * 5)) + 'px';
                var tableStyle = document.createElement('style');
                tableStyle.textContent = 'table#knimePagedTable { width: ' + tableWidth + ' !important;}';
                document.head.appendChild(tableStyle);
            }
        } else {
            // this._representation.numCols must be set here (ensured by settings dialog)
            tileWidth = 'calc(100% / ' + this._representation.numCols + ' - 2 * 5px)';
        }
        var style = document.createElement('style');
        style.textContent = 'table#knimePagedTable tr { width: ' + tileWidth + ';}';
        document.head.appendChild(style);
    };

    // text alignment support
    TileView.prototype._createHtmlTableContainer = function () {
        KnimeBaseTableViewer.prototype._createHtmlTableContainer.apply(this);
        $('#knimePagedTableContainer').addClass('knime-tiles');
        $('#knimePagedTable').removeClass('table-striped').addClass('align-' + this._representation.textAlignment);
    };

    // auto-size cell heights
    TileView.prototype._dataTableDrawCallback = function () {
        KnimeBaseTableViewer.prototype._dataTableDrawCallback.apply(this);
        $('#knimePagedTable thead').remove();
        $('#knimePagedTableContainer .dataTables_scrollHead').remove();
        TileView.prototype._resetTableLayout.apply(this);
        var infoColsCount = this._infoColsCount;
        var columns = this._dataTableConfig.columns;
        // for some reason, images are rendered with size 0x0 in Chromium at this point, hence the timeout
        setTimeout(function () {
            for (var colIndex = infoColsCount; colIndex < columns.length; colIndex++) {
                var cells = Array.prototype.slice.call(document.querySelectorAll('#knimePagedTable .knime-table-cell:nth-child(' + (colIndex + 1) + ')'));
                var maxCellHeight = cells.reduce(function (max, cell) {
                    var cellHeight = cell.scrollHeight;
                    return cellHeight > max ? cellHeight : max;
                }, 0);
                if (maxCellHeight) {
                    cells.forEach(function (cell) {
                        cell.style.minHeight = maxCellHeight + 'px';
                    });
                }
            }
        }, 0);
    };

    TileView.prototype._resetTableLayout = function () {
        // use flex box in case when a single view is opened to always display the page selection
        if (!knimeService.isInteractivityAvailable()) {
            $('body').css({
                'display': 'flex',
                'flex-direction': 'column',
                'position': 'absolute',
                'top': '0',
                'bottom': '0',
                'left': '0',
                'right': '0' 
            });
            $('#knimePagedTableContainer').css({
                'margin': '0',
                'padding': '0 10px 10px',
                'overflow': 'hidden',                
                'flex': '1',
                'position': 'relative',
                'display': 'flex',
                'flex-direction': 'column'
            });
            $('#knimePagedTable_wrapper').css({
                'flex': '1',
                'display': 'flex',
                'flex-direction': 'column'
            });
            $('#knimePagedTable_wrapper > .row:nth-child(2)').css({
                'flex': '1',
                'position': 'relative',
                'overflow': 'auto'                
            });
            $('#knimePagedTable_wrapper > .row:nth-child(2) > .col-sm-12').css({
                'position': 'absolute',
                'top': '0',
                'left': '0',
                'right': '0',
                'bottom': '0'
            });
        }
    }
    
    // reset cell heights
    TileView.prototype._dataTablePreDrawCallback = function () {
        KnimeBaseTableViewer.prototype._dataTablePreDrawCallback.apply(this);
        var cells = Array.prototype.slice.call(document.querySelectorAll('#knimePagedTable .knime-table-cell'));
        cells.forEach(function (cell) {
            cell.style.minHeight = '';
        });
    };

    return new TileView();
})();
