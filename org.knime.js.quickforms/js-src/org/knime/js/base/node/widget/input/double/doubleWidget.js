/* eslint-env jquery */
/* global checkMissingData:false, callUpdate:false */
/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
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
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
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
 *   May 22, 2019 (Christian Albrecht, KNIME GmbH, Konstanz, Germany): created
 */
window.knimeDoubleWidget = (function () {
    
    var doubleWidget = {
        version: '2.0.0'
    };
    doubleWidget.name = 'KNIME Double Widget';
    var viewRepresentation, input, errorMessage;
    var viewValid = false;

    doubleWidget.init = function (representation) {
        if (checkMissingData(representation)) {
            return;
        }
        viewRepresentation = representation;
        var body = $('body');
        var qfdiv = $('<div class="quickformcontainer knime-qf-container">');
        body.append(qfdiv);
        input = $('<input class="knime-qf-input knime-double knime-spinner">');
        input.attr('aria-label', representation.label);
        qfdiv.attr('title', representation.description);
        qfdiv.attr('aria-label', representation.label);
        qfdiv.append('<div class="label knime-qf-title">' + representation.label + '</div>');
        qfdiv.append(input);
        input.spinner({
            step: 0.1
        });
        $('.ui-spinner').addClass('knime-spinner knime-double');
        if (viewRepresentation.useMin) {
            input.spinner('option', 'min', viewRepresentation.min);
        }
        if (viewRepresentation.useMax) {
            input.spinner('option', 'max', viewRepresentation.max);
        }
        input.width(100);
        var doubleValue = representation.currentValue.double;
        input.val(doubleValue);
        qfdiv.append($('<br>'));
        errorMessage = $('<span class="knime-qf-error">');
        errorMessage.css('display', 'none');
        errorMessage.attr('role', 'alert');
        qfdiv.append(errorMessage);
        input.blur(callUpdate);
        viewValid = true;
    };

    doubleWidget.validate = function () {
        if (!viewValid) {
            return false;
        }
        var min = viewRepresentation.min;
        var max = viewRepresentation.max;
        var value = input.val();
        if (!$.isNumeric(value)) {
            doubleWidget.setValidationErrorMessage('The set value is not a double');
            return false;
        }
        value = parseFloat(value);
        if (viewRepresentation.usemin && value < min) {
            doubleWidget.setValidationErrorMessage('The set double ' + value +
                ' is smaller than the allowed minimum of ' + min);
            return false;
        } else if (viewRepresentation.usemax && value > max) {
            doubleWidget.setValidationErrorMessage('The set double ' + value +
                ' is bigger than the allowed maximum of ' + max);
            return false;
        } else {
            doubleWidget.setValidationErrorMessage(null);
            return true;
        }
    };

    doubleWidget.setValidationErrorMessage = function (message) {
        if (!viewValid) {
            return;
        }
        if (message === null) {
            errorMessage.text('');
            errorMessage.css('display', 'none');
        } else {
            errorMessage.text(message);
            errorMessage.css('display', 'inline');
        }
    };

    doubleWidget.value = function () {
        if (!viewValid) {
            return null;
        }
        var viewValue = {};
        viewValue.double = parseFloat(input.val());
        return viewValue;
    };

    return doubleWidget;

})();
