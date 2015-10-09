// JavaScript Document
kc.flotLineChart = function() {
	
	var flotLineChart = {};
	flotLineChart.name = "kc_flotLineChart";
	flotLineChart.extensionNames = [ "hilite" ];
	
	var container;
	
	flotLineChart.init = function(p_container) {
		container = "#" + p_container;
		var colNames = kc.getColumnNames();
		var colTypes = kc.getColumnTypes();
		var numberColsIDs = [];
		var numberColNames = [];
		
		for (var i = 0; i < colTypes.length; i++) {
			if (colTypes[i] === "number") {
				numberColsIDs.push(i);
				numberColNames.push(colNames[i]);
			}
		}
		
		//create flot data
		var allData = [];
		for (var j = 0; j < numberColsIDs.length; j++) {
			var colY = kc.getColumn(numberColsIDs[j]);
			var data = [];
	
			for (var i = 0; i < kc.getNumRows(); i++) {
				data.push([i, colY[i]]);
			}
			
			allData.push({label: numberColNames[j], data: data});
		}
		
		var options = {
			series: {
				bars: {
					show: true
				}
			},
			legend: {
				noColumns: 2
			},
			selection: {
				mode: "x"
			}
		};

		var placeholder = $(container);
		
		placeholder.bind("plotselected", function (event, ranges) {
			var from = Math.ceil(ranges.xaxis.from);
			var to = Math.floor(ranges.xaxis.to);
			flotLineChart.mark(from, to);
		});

		var plot = $.plot(placeholder, allData, options);
	
	};
	
	flotLineChart.mark = function(from ,to) {
		var hiliteHandler = kc.getExtension("hilite");
		hiliteHandler.fireClearHilite();
		
		for (var i = from; i <= to; i++) {
			hiliteHandler.setHilited(flotLineChart.name, i, true);
		}
		hiliteHandler.fireHiliteChanged();
	}
	
	flotLineChart.hiliteChangeListener = function(changedRowIDs) {
		//update(changedRowIDs);
	};
	
	flotLineChart.hiliteClearListener = function() {
		//update();
	};
	
	return flotLineChart;
};