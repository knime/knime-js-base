/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 * 
 * History
 *   Sep 28, 2016 (Christian Albrecht, KNIME.com Gmbh, Konstanz, Germany): created
 */
org_knime_js_base_node_quickform_filter_slider = function() {
	var sliderFilter = {
			version: "1.0.0"
	};
	sliderFilter.name = "Slider Filter";
	var _representation, _value;
	var errorMessage;
	var viewValid = false;
	var slider;

	sliderFilter.init = function(representation, value) {
		
		_representation = representation;
		_value = value;
		var settings = representation.sliderSettings;
		
		var sliderContainer = document.createElement('div');
		sliderContainer.setAttribute('class', 'slidercontainer');
		
		document.getElementsByTagName('body')[0].appendChild(sliderContainer);
		slider = document.createElement('div');
		sliderContainer.appendChild(slider);
		setNumberFormatOptions(settings);
		noUiSlider.create(slider, settings);
		if (settings.orientation == 'vertical') {
			//TODO: make configurable
			slider.style.height = '500px';
			var pad = document.getElementsByClassName('noUi-handle')[0].offsetHeight / 2 + 'px';
			sliderContainer.style.paddingTop = sliderContainer.style.paddingBottom = pad;
		}
		if (settings.tooltips && settings.tooltips.length > 0) {
			var tips = document.getElementsByClassName('noUi-tooltip');
			var maxTipWidth = 0;
			var maxTipHeight = 0;
			for (var i = 0; i < tips.length; i++) {
				var tipStyle = getComputedStyle(tips[i]);
				var tipBorderHor = parseFloat(tipStyle.borderLeftWidth) + parseFloat(tipStyle.borderRightWidth);
				var tipBorderVer = parseFloat(tipStyle.borderTopWidth) + parseFloat(tipStyle.borderBottomWidth);
				maxTipWidth = Math.max(maxTipWidth, tips[i].offsetWidth + tipBorderHor);
				maxTipHeight = Math.max(maxTipHeight, tips[i].offsetHeight + tipBorderVer);
			}
			if (settings.orientation == 'vertical') {
				//account for 120% right
				sliderContainer.style.paddingLeft = (1.2 * maxTipWidth) + 'px';
			} else {
				//TODO: calculate based on tooltip height?
				sliderContainer.style.paddingTop = '38px';
				var padSide = Math.max(parseFloat(getComputedStyle(sliderContainer).paddingLeft), maxTipWidth/2) + 'px';
				sliderContainer.style.paddingLeft = sliderContainer.style.paddingRight = padSide;
			}
		}
		if (settings.pips && settings.pips.mode) {
			if (settings.orientation == 'vertical') {
				//TODO: right-padding?
			} else {
				sliderContainer.style.paddingBottom = "50px";
				//select first element
				var testElem = [document.getElementsByClassName('noUi-value')[0]];
				testElem.push(Array.prototype.slice.call(document.getElementsByClassName('noUi-value'), -1)[0]);
				var testElemWidth = Math.max(testElem[0].offsetWidth, testElem[1].offsetWidth);
				var padSide = Math.max(parseFloat(getComputedStyle(sliderContainer).paddingLeft), testElemWidth/2) + 'px';
				sliderContainer.style.paddingLeft = sliderContainer.style.paddingRight = padSide;
			}
		}
		
		if (value.filter && value.filter.columns) {
			var filter = value.filter.columns[0];
			var startValue = [filter.minimum, filter.maximum];
			slider.noUiSlider.set(startValue);
		}
		
		if (representation.disabled) {
			// domain column is not present, slider would have no effect
			slider.setAttribute('disabled', true);
		} else {
			if (knimeService && knimeService.isInteractivityAvailable()) {
				slider.noUiSlider.on('set', function() {
					setFilterOnValue();
					knimeService.addToFilter(representation.tableId, _value.filter)
				});
			}
		}
	};
	
	setNumberFormatOptions = function(settings) {
		if (settings.tooltips) {
			for (var i = 0; i < settings.tooltips.length; i++) {
				if (typeof settings.tooltips[i] == 'object') {
					settings.tooltips[i] = wNumb(settings.tooltips[i]);
				}
			}
		}
		if (settings.pips && settings.pips.format) {
			settings.pips.format = wNumb(settings.pips.format);
		}
	}
	
	setFilterOnValue = function() {
		if (!_value.filter) {
			_value.filter = {"id": _representation.filterId, "columns": []}
			_value.filter.columns.push({"minimum": null, "maximum": null});
		}
		var sliderValues = slider.noUiSlider.get();
		_value.filter.columns[0].minimum = sliderValues[0];
		_value.filter.columns[0].maximum = sliderValues[1];
	}
	
	sliderFilter.validate = function() {
		return true;
	};
	
	sliderFilter.setValidationErrorMessage = function(message) {
		/* nothing to do */
	};

	sliderFilter.value = function() {
		setFilterOnValue();
		return _value;
	};
	
	return sliderFilter;
	
}();
