(scorer_namespace = function() {
	
	var scorer = {};
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
		
		classes = _representation.inObjects[1].classes;
		confusionMatrix = _representation.inObjects[1].confusionMatrix;
		keyStore = representation.inObjects[1].keyStore;
		tableID = _representation.inObjects[0].id;
		valueStatsList = _representation.inObjects[1].valueStatsList;
		

		var body = document.querySelector('body');
		
		//Building the confusion matrix table
		var table = document.createElement('table');
		table.setAttribute('id', 'knime-confusion-matrix');
		table.setAttribute('class', 'center');
		
		var tHeader = document.createElement('thead');
		//1st header row
		var tRow = document.createElement('tr');
		//Total
		var th = document.createElement('th');
		th.appendChild(document.createTextNode('Total'));
		th.setAttribute('colspan', 2);
		th.setAttribute('rowspan', 2);
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


		// //Building the accuracy statistics table
		// table = document.createElement('table');
		// table.setAttribute('id', 'knime-accuracy-statistics');
		// table.setAttribute('class', 'center');

		// tHeader = document.createElement('thead');
		// tRow = document.createElement('tr');
		// th = document.createElement('th');
		// th.appendChild(document.createTextNode('Class'));
		// tRow.appendChild(th);
		// // for (var i = 0; i < classes.length; i++) {
		// // 	th = document.createElement('th');
		// // 	th.appendChild(document.createTextNode(classes[i]));
		// // 	tRow.appendChild(th);
		// // }


		// table.appendChild(tHeader);

		// body.appendChild(table);
		
		
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
	
	scorer.validate = function() {
		return true;
	}
	
	scorer.getComponentValue = function() {
		return _value;
	}
	
	
	return scorer;
	
}());