(scorer_namespace = function() {
	
	var scorer = {};
	var _representation, _value;
	var title;
	var subtitle;
	var confusionMatrix;
	var classes;
	var confusionMatrixWithRates;
	var classStatistics;
	var overallStatistics;
	var rowsNumber;
	var tableID;
	var keyStore;
	var body;
	var confusionTable;


	scorer.init = function(representation, value) {
		_representation = representation;
		_value = value;
		
		debugger;
		
		createPage();

		if (_representation.options.enableViewControls) {
			drawControls();
		}
	}

	function createPage() {
		title = _value.options["title"];
		subtitle = _value.options["subtitle"];
		confusionMatrix = new kt();
		confusionMatrix.setDataTable(_representation.inObjects[0].confusionMatrix);
		classes = confusionMatrix.getColumnNames();
		confusionMatrixWithRates = createConfusionMatrixWithRates(confusionMatrix, classes.length + 1, classes.length + 1);
		classStatistics = new kt();
		classStatistics.setDataTable(_representation.inObjects[0].classStatistics);
		overallStatistics = new kt();
		overallStatistics.setDataTable(_representation.inObjects[0].overallStatistics);	
		rowsNumber = overallStatistics.getCell('Overall','Correct Classified') + overallStatistics.getCell('Overall','Wrong Classified');
		tableID = _representation.tableIds[0];
		keyStore = _representation.inObjects[0].keyStore;

		body = document.querySelector('body');

		//Title and subtitle
		var h1 = document.createElement('h1');
		h1.appendChild(document.createTextNode(title));
		h1.setAttribute('id', 'title');
		body.appendChild(h1);
		var h4 = document.createElement('h4');
		h4.appendChild(document.createTextNode(subtitle));
		h4.setAttribute('id', 'subtitle');
		h4.setAttribute('align', 'center');
		body.appendChild(h4);

		//Building the confusion matrix table
		createConfusionMatrixTable();

		// //Building the class statistics table
		createClassStatisticsTable();

		// //Table containing the accuracy and Cohen's kappa values
		createOverallStatisticsTable();

		knimeService.subscribeToSelection(tableID, selectionChanged);
	}

	createConfusionMatrixTable = function() {
		var table = document.createElement('table');
		table.setAttribute('id', 'knime-confusion-matrix');
		table.setAttribute('class', 'center');
		var caption = document.createElement('caption');
		caption.appendChild(document.createTextNode('Confusion Matrix'));
		table.appendChild(caption);
		
		var tHeader = document.createElement('thead');
		//header row
		var tRow = document.createElement('tr');
		var th = document.createElement('th');
		th.appendChild(document.createTextNode('Rows Number : \n' + rowsNumber));
		th.setAttribute('style', 'border-right-width: 2px');
		th.style.backgroundColor = _representation.options.header_color;
		tRow.appendChild(th);
		for (var i = 0; i < classes.length; i++) {
			th = document.createElement('th');
			th.appendChild(document.createTextNode('Predicted ' + classes[i]));
			th.style.backgroundColor = _representation.options.header_color;
			tRow.appendChild(th);
		}
		tHeader.appendChild(tRow);
		table.appendChild(tHeader);
		
		var tBody = document.createElement('tbody');
		for (var row = 0; row < confusionMatrix.getNumRows(); row++) {
			tRow = document.createElement('tr');
			th = document.createElement('th');
			th.appendChild(document.createTextNode('Actual ' + classes[row]));
			th.style.backgroundColor = _representation.options.header_color;
			tRow.appendChild(th);
			for (var col = 0; col < confusionMatrix.getNumColumns(); col++) {
				var td = document.createElement('td');
				td.appendChild(document.createTextNode(confusionMatrix.getCell(row, col)));
				td.setAttribute('data-row', row);
				td.setAttribute('data-col', col);
				if (row === col) {
					td.style.backgroundColor = _representation.options.diag_color;
				}
				td.onclick = cellClicked;
				tRow.appendChild(td);
			}
			var lastCell = document.createElement('td');
			lastCell.appendChild(document.createTextNode(confusionMatrixWithRates[row][confusionMatrixWithRates.length-1].toFixed(3)));
			lastCell.setAttribute('class', 'rateCell');
			tRow.appendChild(lastCell);
			tBody.appendChild(tRow);
		}
		tRow = document.createElement('tr');
		td = document.createElement('td');
		td.setAttribute('class', 'no-border');
		tRow.appendChild(td);
		for (var col = 0; col < confusionMatrix.getNumRows(); col++) {
			td = document.createElement('td');
			td.appendChild(document.createTextNode(confusionMatrixWithRates[confusionMatrixWithRates.length-1][col].toFixed(3)));
			td.setAttribute('class', 'rateCell');			
			tRow.appendChild(td);
		}
		tBody.appendChild(tRow);
		table.appendChild(tBody);
		confusionTable = table;
		body.appendChild(table);

		toggleConfusionMatrixRatesDisplay();
	}
	
	createConfusionMatrixWithRates = function(confusionMatrix, numRows, numColumns) {
		var confusionMatrixWithRates = new Array(numRows); 
		for(var i = 0; i < numRows; i++) {
			confusionMatrixWithRates[i] = new Array(numColumns); 
		}
		for (var i = 0; i < confusionMatrix.getNumRows(); i++) {
			var currentRow = confusionMatrix.getRow(i);
            for (var j = 0; j < confusionMatrix.getRow(i).data.length; j++) {
                confusionMatrixWithRates[i][j] = confusionMatrix.getCell(i, j);
            }
        }
        for (var i = 0; i < confusionMatrixWithRates.length-1; i++) {
            var rowSum = 0;
            for (var j = 0; j < confusionMatrixWithRates[i].length-1; j++) {
                rowSum += confusionMatrixWithRates[i][j];
            }
            confusionMatrixWithRates[i][confusionMatrixWithRates.length-1] = confusionMatrixWithRates[i][i] / rowSum;
        }
        for (var i = 0; i < confusionMatrixWithRates.length-1; i++) {
            var columnSum = 0;
            for (var j = 0; j < confusionMatrixWithRates.length-1; j++) {
                columnSum += confusionMatrixWithRates[j][i];
            }
            confusionMatrixWithRates[confusionMatrixWithRates.length-1][i] = confusionMatrixWithRates[i][i] / columnSum;
        }
		return confusionMatrixWithRates;
	}

	cellClicked = function(event) {
		confusionTable.querySelectorAll('td').forEach(function (cell) {
			cell.classList.remove('selected');
		});
		this.classList.add('selected');
		if (knimeService.isInteractivityAvailable()) {
			// knimeService.setSelectedRows(tableID, ['Row20'], selectionChanged);
			var rowIds = keyStore[this.dataset.row][this.dataset.col];
			knimeService.setSelectedRows(tableID, rowIds);
		}
	}
	
	selectionChanged = function(data) {
		//TODO should we support this?
	}
	
	createClassStatisticsTable = function() {
		var table = document.createElement('table');
		table.setAttribute('id', 'knime-class-statistics');
		table.setAttribute('class', 'center');
		var caption = document.createElement('caption');
		caption.appendChild(document.createTextNode('Class Statistics'));
		table.appendChild(caption);

		var tHeader = document.createElement('thead');
		var tRow = document.createElement('tr');
		var statNames = ['Class'];
		Array.prototype.push.apply(statNames, classStatistics.getColumnNames());
		for (var i = 0; i < statNames.length; i++) {
			var th = document.createElement('th');
			th.appendChild(document.createTextNode(statNames[i]));
			th.style.backgroundColor = _representation.options.header_color;
			tRow.appendChild(th);
		}
		tHeader.appendChild(tRow);
		table.appendChild(tHeader);

		tBody = document.createElement('tbody');
		for (var row = 0; row < classStatistics.getNumRows(); row++) {
			tRow = document.createElement('tr');
			td = document.createElement('td');
			td.appendChild(document.createTextNode(classStatistics.getRow(row).rowKey));
			tRow.appendChild(td);
			for (var col = 0; col < classStatistics.getNumColumns(); col++) {
				var td = document.createElement('td');
				var cellValue  = classStatistics.getCell(row, col);
				if (cellValue % 1 === 0) {
					// cellValue is an integer
					td.appendChild(document.createTextNode(cellValue));
				} else {
					// cellValue is a float
					td.appendChild(document.createTextNode(cellValue.toFixed(3)));
				}
				tRow.appendChild(td);
			}
			// last cell of each row has no border
			td = document.createElement('td');
			td.setAttribute('class', 'no-border');
			tRow.appendChild(td);
			tBody.appendChild(tRow);
		}
		// last row has one cell without any border
		tRow = document.createElement('tr');
		td = document.createElement('td');
		td.setAttribute('class', 'no-border');
		tRow.appendChild(td);
		tBody.appendChild(tRow);
		table.appendChild(tBody);

		body.appendChild(table);

		toggleClassStatisticsDisplay();		
	}

	createOverallStatisticsTable = function() {
		var table = document.createElement('table');
		table.setAttribute('id', 'knime-overall-statistics');
		table.setAttribute('class', 'center');
		var caption = document.createElement('caption');
		caption.appendChild(document.createTextNode('Overall Statistics'));
		table.appendChild(caption);		

		var tHeader = document.createElement('thead');
		var tRow = document.createElement('tr');
		var th = document.createElement('th');
		var statNames = overallStatistics.getColumnNames()
		for (var i = 0; i < statNames.length; i++) {
			th = document.createElement('th');
			th.appendChild(document.createTextNode(statNames[i]));
			th.style.backgroundColor = _representation.options.header_color;
			tRow.appendChild(th);
		}
		tHeader.appendChild(tRow);
		table.appendChild(tHeader);

		var tBody = document.createElement('tbody');
		tRow = document.createElement('tr');
		for (var col = 0; col < overallStatistics.getNumColumns(); col++) {
			var td = document.createElement('td');
			var cellValue  = overallStatistics.getCell(0, col);
			if (cellValue % 1 === 0) {
				// cellValue is an integer
				td.appendChild(document.createTextNode(cellValue));
			} else {
				// cellValue is a float
				td.appendChild(document.createTextNode(cellValue.toFixed(3)));
			}
			tRow.appendChild(td);
		}
		// last cell of each row has no border
		td = document.createElement('td');
		td.setAttribute('class', 'no-border');
		tRow.appendChild(td);
		tBody.appendChild(tRow);
		// last row has one cell without any border
		tRow = document.createElement('tr');
		td = document.createElement('td');
		td.setAttribute('class', 'no-border');
		tRow.appendChild(td);
		tBody.appendChild(tRow);
		table.appendChild(tBody);

		body.appendChild(table);		
	}

	drawControls = function() {
		if (!knimeService) {
			// TODO: error handling?
			return;
		}
		
		if (_representation.displayFullscreenButton) {
			knimeService.allowFullscreen();
		}
		
	    if (!_representation.options.enableViewControls) return;
	    
	    var titleEdit = _representation.options.enableTitleEdit;
	    var subtitleEdit = _representation.options.enableSubtitleEdit;
	    var classStatsDisplay = _representation.options.enableClassStatisticsDisplay;	    
	    var CMRatesDisplay = _representation.options.enableCMRatesDisplay;	    
	    
	    if (titleEdit || subtitleEdit) {	    	    
	    	if (titleEdit) {
	    		var chartTitleText = knimeService.createMenuTextField('chartTitleText', _value.options.title, function() {
	    			if (_value.options.title != this.value) {
						_value.options.title = this.value;
						updateTitles(true);
					}
	    		}, true);
	    		knimeService.addMenuItem('Chart Title:', 'header', chartTitleText);
	    	}
	    	if (subtitleEdit) {
	    		var chartSubtitleText = knimeService.createMenuTextField('chartSubtitleText', _value.options.subtitle, function() {
	    			if (_value.options.subtitle != this.value) {
						_value.options.subtitle = this.value;
						updateTitles(true);
					}
	    		}, true);
	    		var mi = knimeService.addMenuItem('Chart Subtitle:', 'header', chartSubtitleText, null, knimeService.SMALL_ICON);
	    	}
	    	if (CMRatesDisplay || classStatsDisplay) {
	    		knimeService.addMenuDivider();
	    	}
	    }

	    if (CMRatesDisplay) {
	    	var switchCMRatesDisplay = knimeService.createMenuCheckbox('switchCMRatesDisplay', _value.options.displayCMRates, function() {
	    		if (_value.options.displayCMRates != this.checked) {
					_value.options.displayCMRates = this.checked;
					toggleConfusionMatrixRatesDisplay();
				}
	    	});
	    	knimeService.addMenuItem("Display confusion matrix totals as rates: ", 'table', switchCMRatesDisplay);
	    }

	    if (classStatsDisplay) {
	    	var switchClassStatsDisplay = knimeService.createMenuCheckbox('switchClassStatsDisplay', _value.options.displayClassStatistics, function() {
	    		if (_value.options.displayClassStatistics != this.checked) {
					_value.options.displayClassStatistics = this.checked;
					toggleClassStatisticsDisplay();
				}
	    	});
	    	knimeService.addMenuItem("Display class statistics: ", 'table', switchClassStatsDisplay);
	    }
	};

	function updateTitles(updateChart) {
		var curTitle = d3.select("#title");
		var curSubtitle = d3.select("#subtitle");
		if (!_value.options.title) {
			curTitle.remove();
		}
		if (_value.options.title) {
			if (curTitle.empty()) {
				d3.select('body').append('h1')
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
				d3.select('body').append('h4')
				.attr("id", "subtitle")
				.text(_value.options.subtitle);
			} else {
				curSubtitle.text(_value.options.subtitle)
			}
		}
		
		var isTitle = _value.options.title || _value.options.subtitle;
		knimeService.floatingHeader(isTitle);
	}

	function toggleClassStatisticsDisplay() {
		if (_value.options.displayClassStatistics === true) {
			d3.select("#knime-class-statistics").style("display", "block");
		} else {
			d3.select("#knime-class-statistics").style("display", "none");
		}
	}

	function toggleConfusionMatrixRatesDisplay() {
		if (_value.options.displayCMRates === true) {
			d3.selectAll(".rateCell").style("display", "table-cell");
			d3.selectAll(".no-border").style("display", "table-cell");
		} else {
			d3.selectAll(".rateCell").style("display", "none");
			d3.selectAll(".no-border").style("display", "none");
		}
	}	

	scorer.validate = function() {
		return true;
	}
	
	scorer.getComponentValue = function() {
		return _value;
	}
	
	
	return scorer;
	
}());