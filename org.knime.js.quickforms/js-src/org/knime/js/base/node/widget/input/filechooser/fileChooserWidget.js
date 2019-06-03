/* eslint-env jquery */
/* global checkMissingData:false */
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
 *   Jun 3, 2019 (Christian Albrecht, KNIME GmbH, Konstanz, Germany): created
 */
window.knimeFileChooserWidget = (function () {
    
    var fileChooser = {
        version: '2.0.0'
    };
    fileChooser.name = 'KNIME File Chooser Widget';
    var _representation = null;
    var _value = null;
    var _viewValid = false;
    var _errorMessage = null;
    
    var getTypeFromSelectedItem = function (node, path) {
        var childNodes;
        if (node) {
            childNodes = node.children;
        } else {
            childNodes = _representation.tree;
        }
        if (node && node.id === path) {
            return node.type;
        }
        for (var i = 0; i < childNodes.length; i++) {
            if (path.startsWith(childNodes[i].id)) {
                return getTypeFromSelectedItem(childNodes[i], path);
            }
        }
        return 'UNKNOWN';
    };

    fileChooser.init = function (representation, value) {
        if (checkMissingData(representation)) {
            return;
        }
        _representation = representation;
        _value = representation.currentValue;
        // erase default selection when running on server
        if (representation.runningOnServer) {
            _value.items = [];
        }

        // define startsWith function on strings
        if (!String.prototype.startsWith) {
            String.prototype.startsWith = function (searchString, position) { // eslint-disable-line no-extend-native
                position = position || 0;
                return this.substr(position, searchString.length) === searchString;
            };
        }

        var qfdiv = $('<div class="quickformcontainer knime-qf-container">');
        $('body').append(qfdiv);

        qfdiv.attr('title', representation.description);
        qfdiv.attr('aria-label', representation.label);
        qfdiv.append('<div class="label knime-qf-title">' + representation.label + '</div>');

        if (!representation.tree || representation.tree.length < 1) {
            var errorText = 'No items found for selection. ';
            errorText += representation.runningOnServer ? 'Check your settings.'
                : 'View selection only possible on server.';
            representation.tree = [{
                id: 'emptyTree',
                text: errorText,
                icon: null,
                state: {
                    opened: false,
                    disabled: true,
                    selected: false
                },
                children: []
            }];
        }

        qfdiv.append('<div id="treeContainer" class="knime-qf-tree" aria-label="' + representation.label + '">');
        $('#treeContainer').jstree({
            core: {
                data: representation.tree,
                multiple: representation.multipleSelection,
                worker: false
            }
        });

        $('#treeContainer').on('changed.jstree', function (e, data) {
            var selectedItems = [];
            for (var i = 0; i < data.selected.length; i++) {
                var path = data.selected[i];
                var type = getTypeFromSelectedItem(null, path);
                if (_representation.prefix) {
                    path = _representation.prefix + path;
                }
                var item = {
                    path: encodeURI(path),
                    type: type
                };
                selectedItems.push(item);
            }
            _value.items = selectedItems;
        });

        qfdiv.append($('<br>'));
        _errorMessage = $('<span class="knime-qf-error">');
        _errorMessage.css('display', 'none');
        _errorMessage.attr('role', 'alert');
        qfdiv.append(_errorMessage);

        _viewValid = true;
    };

    fileChooser.validate = function () {
        if (!_viewValid) {
            return false;
        }
        if (_value.items && _value.items.length > 0 && _value.items[0].path) {
            return true;
        } else {
            fileChooser.setValidationErrorMessage('Select at least one item to proceed.');
            return false;
        }
    };

    fileChooser.setValidationErrorMessage = function (message) {
        if (!_viewValid) {
            return;
        }
        if (message === null) {
            _errorMessage.text('');
            _errorMessage.css('display', 'none');
        } else {
            _errorMessage.text(message);
            _errorMessage.css('display', 'inline');
        }
    };

    fileChooser.value = function () {
        if (!_viewValid) {
            return null;
        }
        return _value;
    };

    return fileChooser;
})();
