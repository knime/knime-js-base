(scorer_namespace = function() {
	
	var scorer = {};
	var title;
	var subtitle;
	var classes;	//Classifications 
	var confusionMatrix;
	var keyStore;
	var tableID;
	var valueStatsList
	var _representation, _value;
	var confusionTable;
	
	scorer.init = function(representation, value) {
		_representation = representation;
		_value = value;
		
		debugger;
		
		drawPage();

		if (_representation.options.enableViewControls) {
			drawControls();
		}
	}

	function drawPage() {
		title = _value.options["title"];
		subtitle = _value.options["subtitle"];
		classes = _representation.inObjects[0].classes;
		confusionMatrix = _representation.inObjects[0].confusionMatrix;
		keyStore = _representation.inObjects[0].keyStore;
		tableID = _representation.tableIds[0];
		valueStatsList = _representation.inObjects[0].valueStatsList;
		accuracy = _representation.inObjects[0].accuracy;
		cohensKappa = _representation.inObjects[0].cohensKappa;
		rowsNumber = _representation.inObjects[0].rowsNumber;

		
		var body = document.querySelector('body');

		//Title and subtitle
		var h1 = document.createElement('h1');
		h1.appendChild(document.createTextNode(title));
		h1.setAttribute('id', 'title');
		body.appendChild(h1);
		var h2 = document.createElement('h2');
		h2.appendChild(document.createTextNode(subtitle));
		h2.setAttribute('id', 'subtitle');
		h2.setAttribute('align', 'center');
		body.appendChild(h2);

		
		//Building the confusion matrix table
		var table = document.createElement('table');
		table.setAttribute('id', 'knime-confusion-matrix');
		table.setAttribute('class', 'center');
		var caption = document.createElement('caption');
		caption.appendChild(document.createTextNode('Confusion Matrix'));
		table.appendChild(caption);
		
		var tHeader = document.createElement('thead');
		//1st header row
		var tRow = document.createElement('tr');
		//Total
		var th = document.createElement('th');
		th.appendChild(document.createTextNode('Total rows number: \n' + rowsNumber));
		th.setAttribute('colspan', 2);
		th.setAttribute('rowspan', 2);
		th.setAttribute('style', 'border-right-width: 2px')
		tRow.appendChild(th);
		//Predicted
		th = document.createElement('th');
		th.appendChild(document.createTextNode('Predicted'));
		th.setAttribute('colspan', classes.length);
		tRow.appendChild(th);
		tHeader.appendChild(tRow);
		//2nd header row
		tRow = document.createElement('tr');
		for (var i = 0; i < classes.length; i++) {
			th = document.createElement('th');
			th.appendChild(document.createTextNode(classes[i]));
			tRow.appendChild(th);
		}
		tHeader.appendChild(tRow);
		table.appendChild(tHeader);
		
		var tBody = document.createElement('tbody');
		for (var row = 0; row <= confusionMatrix.length; row++) {
			tRow = document.createElement('tr');
			if (row === 0) {
				th = document.createElement('th');
				th.appendChild(document.createTextNode('Actual'));
				th.setAttribute('rowspan', classes.length);
				th.setAttribute('style', 'border-bottom-width: 2px')
				tRow.appendChild(th);
			}
			if (row !== confusionMatrix.length) {
				th = document.createElement('th');
				th.appendChild(document.createTextNode(classes[row]));
				tRow.appendChild(th);
			}
			for (var col = 0; col < confusionMatrix.length; col++) {
				var tCell = document.createElement('td');
				if (row === confusionMatrix.length) {
					tCell.setAttribute('class', 'no-border');
				} else { 
					tCell.appendChild(document.createTextNode(confusionMatrix[row][col]));
					tCell.setAttribute('data-row', row);
					tCell.setAttribute('data-col', col);
					if (row === col) {
						tCell.style.backgroundColor = _representation.options.diag_color;
					}
					tCell.onclick = cellClicked;
				}
				tRow.appendChild(tCell);
			}
			var emptyCell = document.createElement('td');
			emptyCell.setAttribute('class', 'no-border');
			tRow.appendChild(emptyCell);
			tBody.appendChild(tRow);
		}
		table.appendChild(tBody);
		
		confusionTable = table;
		body.appendChild(table);


		//Building the class statistics table
		table = document.createElement('table');
		table.setAttribute('id', 'knime-class-statistics');
		table.setAttribute('class', 'center');
		caption = document.createElement('caption');
		caption.appendChild(document.createTextNode('Class Statistics'));
		table.appendChild(caption);

		tHeader = document.createElement('thead');
		tRow = document.createElement('tr');
		th = document.createElement('th');
		var statNames = ['Class', 'True Positives', 'False Positives', 'True Negatives', 'False Negatives',
			'Recall', 'Precision', 'Sensitivity', 'Specificity', 'F-measure']
		for (var i = 0; i < statNames.length; i++) {
			th = document.createElement('th');
			th.appendChild(document.createTextNode(statNames[i]));
			tRow.appendChild(th);
		}
		tHeader.appendChild(tRow);
		table.appendChild(tHeader);

		tBody = document.createElement('tbody');
		for (var i = 0; i <= valueStatsList.length; i++) {
			tRow = document.createElement('tr');

			var th = document.createElement('th');
			if (i !== valueStatsList.length) {
				th.appendChild(document.createTextNode(valueStatsList[i].valueName));
			} else {
				th = document.createElement('td');
				th.setAttribute('class', 'no-border');
			}	
			tRow.appendChild(th);

			td = document.createElement('td');
			if (i !== valueStatsList.length) {
				td.appendChild(document.createTextNode(valueStatsList[i].tp));
			} else {
				td.setAttribute('class', 'no-border');
			}	
			tRow.appendChild(td);

			td = document.createElement('td');
			if (i !== valueStatsList.length) {
				td.appendChild(document.createTextNode(valueStatsList[i].fp));
			} else {
				td.setAttribute('class', 'no-border');
			}	
			tRow.appendChild(td);

			td = document.createElement('td');
			if (i !== valueStatsList.length) {
				td.appendChild(document.createTextNode(valueStatsList[i].tn));
			} else {
				td.setAttribute('class', 'no-border');
			}	
			tRow.appendChild(td);

			td = document.createElement('td');
			if (i !== valueStatsList.length) {
				td.appendChild(document.createTextNode(valueStatsList[i].fn));
			} else {
				td.setAttribute('class', 'no-border');
			}	
			tRow.appendChild(td);

			td = document.createElement('td');
			if (i !== valueStatsList.length) {
				td.appendChild(document.createTextNode(valueStatsList[i].recall.toFixed(3)));
			} else {
				td.setAttribute('class', 'no-border');
			}
			tRow.appendChild(td);

			td = document.createElement('td');
			if (i !== valueStatsList.length) {
				td.appendChild(document.createTextNode(valueStatsList[i].precision.toFixed(3)));
			} else {
				td.setAttribute('class', 'no-border');
			}				
			tRow.appendChild(td);

			td = document.createElement('td');
			if (i !== valueStatsList.length) {
				td.appendChild(document.createTextNode(valueStatsList[i].sensitivity.toFixed(3)));
			} else {
				td.setAttribute('class', 'no-border');
			}			
			tRow.appendChild(td);

			td = document.createElement('td');
			if (i !== valueStatsList.length) {
				td.appendChild(document.createTextNode(valueStatsList[i].specificity.toFixed(3)));
			} else {
				td.setAttribute('class', 'no-border');
			}			
			tRow.appendChild(td);

			td = document.createElement('td');
			if (i !== valueStatsList.length) {
				td.appendChild(document.createTextNode(valueStatsList[i].fmeasure.toFixed(3)));
			} else {
				td.setAttribute('class', 'no-border');
			}				
			tRow.appendChild(td);

			td = document.createElement('td');
			td.setAttribute('class', 'no-border');
			tRow.appendChild(td);

			tBody.appendChild(tRow);
		}
		table.appendChild(tBody);

		body.appendChild(table);

		toggleClassStatisticsDisplay();
		

		//Table containing the accuracy and Cohen's kappa values
		table = document.createElement('table');
		table.setAttribute('id', 'knime-overall-statistics');
		table.setAttribute('class', 'center');
		caption = document.createElement('caption');
		caption.appendChild(document.createTextNode('Overall Statistics'));
		table.appendChild(caption);		

		tBody = document.createElement('tbody');
		tRow = document.createElement('tr');
		th = document.createElement('th');
		th.appendChild(document.createTextNode('Overall Accuracy'));
		tRow.appendChild(th);
		td = document.createElement('td');
		td.appendChild(document.createTextNode(accuracy.toFixed(3)));
		tRow.appendChild(td);
		tBody.appendChild(tRow);

		tRow = document.createElement('tr');
		th = document.createElement('th');
		th.appendChild(document.createTextNode("Cohen's kappa"));
		tRow.appendChild(th);
		td = document.createElement('td');
		td.appendChild(document.createTextNode(cohensKappa.toFixed(3)));
		tRow.appendChild(td);
		tBody.appendChild(tRow);

		table.appendChild(tBody);
		body.appendChild(table);


		knimeService.subscribeToSelection(tableID, selectionChanged);
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
					// toggleClassStatisticsDisplay();
				}
	    	});
	    	knimeService.addMenuItem("Display confusion matrix rates: ", 'table', switchCMRatesDisplay);
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
				d3.select('body').append('h2')
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

	scorer.validate = function() {
		return true;
	}
	
	scorer.getComponentValue = function() {
		return _value;
	}
	
	
	return scorer;
	
}());