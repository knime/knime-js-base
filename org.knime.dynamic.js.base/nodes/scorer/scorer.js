(scorer_namespace = function() {
	
	var scorer = {};
	var confusionMatrix;
	var keyStore;
	var tableID;
	var _representation, _value;
	
	scorer.init = function(representation, value) {
		_representation = representation;
		_value = value;
		
		debugger;
		
		var data = _representation.inObjects[1];
		//keyStore = representation.inObjects[1];
		tableID = representation.inObjects[0].id;
		
		var body = document.querySelector('body');
		var table = document.createElement('table');
		table.setAttribute('id', 'knime-confusion-matrix');
		table.setAttribute('class', 'center');
		var tBody = document.createElement('tbody');
		table.appendChild(tBody);
		
		for (var row = 0; row <= data.length; row++) {
			var tRow = document.createElement('tr');
			for (var col = 0; col < data.length; col++) {
				var tCell = document.createElement('td');
				if (row === data.length) {
					tCell.setAttribute('class', 'no-border');
				} else { 
					tCell.appendChild(document.createTextNode(data[row][col]));
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
		body.appendChild(table);
		confusionMatrix = table;
		
		knimeService.subscribeToSelection(tableID, selectionChanged);
	}
	
	cellClicked = function(event) {
		confusionMatrix.querySelectorAll('td').forEach(function (cell) {
			cell.classList.remove('selected');
		});
		this.classList.add('selected');
		if (knimeService.isInteractivityAvailable()) {
			knimeService.setSelectedRows(tableID, ['Row20'], selectionChanged);
			/*var rowIds = keyStore[this.dataset.row][this.dataset.col];
			knimeService.setSelectedRows(tableID, rowIds);*/
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