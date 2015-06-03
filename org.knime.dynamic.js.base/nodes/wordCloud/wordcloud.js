(wordcloud = function() {

	var wordCloud = {};
	var _representation
	var _value;

	wordCloud.init = function(representation, value) {
		_representation = representation;
		_value = value;
		var knimeTable = new kt();
		knimeTable.setDataTable(_representation.inObjects[0]);
		var fontSizeMinimum = _representation.options["fontSizeMinimum"];
		var fontSizeMaximum = _representation.options["fontSizeMaximum"];
		var wordCol = knimeTable
				.getColumn(_representation.options["wordColumn"]);
		if (typeof (wordCol) == "undefined") {
			$("body")
					.append(
							'<div style="color: red">The selected word column does not exist</div>');
		} else if (!_representation.options["useSizeProperty"]
				&& typeof (knimeTable
						.getColumn(_representation.options["sizeColumn"])) == "undefined") {
			$("body")
					.append(
							'<div style="color: red">The selected size column does not exist</div>');
		} else if (fontSizeMinimum > fontSizeMaximum) {
			$("body")
					.append(
							'<div style="color: red">The font size minimum is bigger than the font size maximum</div>');
		} else {
			var font = _representation.options["font"];
			var useSizeProperty = _representation.options["useSizeProperty"];
			if (useSizeProperty) {
				var sizeCol = knimeTable
						.getColumn(_representation.options["sizeColumn"]);
				// var sizeCol = knimeTable.getRowSizes();
			} else {
				var sizeCol = knimeTable
						.getColumn(_representation.options["sizeColumn"]);
			}
			var sizeCol = normalizeArray(sizeCol, fontSizeMinimum,
					fontSizeMaximum);
			var colors = knimeTable.getRowColors();
			var data = [];
			for (var i = 0; i < wordCol.length; i++) {
				var datum = {};
				datum["text"] = wordCol[i];
				datum["size"] = sizeCol[i];
				datum["color"] = colors[i];
				data.push(datum);
			}
			require(
					[ "https://raw.githubusercontent.com/jasondavies/d3-cloud/master/d3.layout.cloud.js" ],
					function() {
						var useColorProperty = _representation.options["useColorProperty"];
						var fill = d3.scale.category20();
						var colorPalette = _representation.options["colorPalette"];
						switch (colorPalette) {
						case "Palette-B":
							fill = d3.scale.category20b();
							break;
						case "Palette-C":
							fill = d3.scale.category20c();
							break;
						}
						function draw(words) {
							d3.select("body").append("svg").attr("width", 1000)
									.attr("height", 1000).append("g").attr(
											"transform", "translate(500,500)")
									.selectAll("text").data(words).enter()
									.append("text").style("font-size",
											function(d) {
												return d.size + "px";
											}).style("font-family", font)
									.style("fill", function(d, i) {
										if (useColorProperty) {
											return d.color;
										} else {
											return fill(i);
										}
									}).attr("text-anchor", "middle").attr(
											"transform",
											function(d) {
												return "translate("
														+ [ d.x, d.y ]
														+ ")rotate(" + d.rotate
														+ ")";
											}).text(function(d) {
										return d.text;
									});
						}
						d3.layout.cloud().size([ 1000, 1000 ]).words(data)
								.padding(5).rotate(function() {
									return Math.random() * 120 - 60;
								}).font(font).fontSize(function(d) {
									return d.size;
								}).on("end", draw).start();
					});
		}
	}

	normalizeArray = function(array, min, max) {
		var newArray = [];
		var range = max - min;
		var arrayMin = Math.min.apply(null, array);
		var arrayMax = Math.max.apply(null, array);
		for (var i = 0; i < array.length; i++) {
			newArray[i] = (array[i] - arrayMin) / (arrayMax - arrayMin);
			newArray[i] = newArray[i] * range + min;
		}
		return newArray;
	}

	wordCloud.validate = function() {
		return true;
	}

	wordCloud.setValidationError = function() {

	}

	wordCloud.getComponentValue = function() {
		return _value;
	}
	
	wordCloud.getSVG = function() {
		var svg = d3.select("svg")[0][0];
		return (new XMLSerializer()).serializeToString(svg);
	}

	return wordCloud;

}());
