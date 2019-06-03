/* eslint-env jquery */
/* global  */
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
 *   Jun 3, 2019 (Daniel Bogenrieder, KNIME AG, Zurich, Switzerland): created
 */
window.knimeFileUploadWidget = (function () {
    var fileUpload = {
        version: '2.0.0'
    };
    fileUpload.name = 'File upload';
    var viewRepresentation = null;
    var viewValue = null;
    var viewValid = false;
    var viewComponent = null;
    var viewErrorDiv = null;

    fileUpload.init = function (representation, value) {
        debugger;
        if (checkMissingData(representation)) {
            return;
        }

        // add native component
        var messageNotFound = 'File upload not available. Native component not found.';
        var messageNotStandalone = 'File upload only available on server.';
        viewComponent = insertNativeComponent(representation, messageNotFound, messageNotStandalone);

        // add error field
        viewErrorDiv = document.createElement('div');
        viewErrorDiv.setAttribute('class', 'knime-qf-error');
        viewErrorDiv.style.display = 'none';
        viewErrorDiv.style.marginTop = '1em';
        viewErrorDiv.setAttribute('role', 'alert');
        viewErrorDiv.appendChild(document.createTextNode(''));
        document.getElementsByTagName('body')[0].appendChild(viewErrorDiv);

        // set listener on label
        if (viewComponent) {
            viewComponent.setAttribute('aria-label', representation.label);
            var uLabel = viewComponent.getElementsByClassName('knime-upload-label')[0];
            if (uLabel) {
                uLabel.classList.add('knime-qf-label');
                // use mutation event instead of observer, since IE only supports it as of version 11
                try {
                    uLabel.addEventListener('DOMSubtreeModified', function () {
                        if (viewValid && viewErrorDiv.textContent) {
                            fileUpload.validate();
                        }
                    }, false);
                } catch (exception) { /* do nothing */}
            }
            // add button styles
            var btn = viewComponent.getElementsByClassName('v-upload')[0];
            if (btn) {
                btn.classList.add('knime-qf-button');
            }
        }

//        // Automatically resize component, since events of native component are not noticed
//        if (viewComponent) {
//            setInterval(resizeParent, 500);
//        }
        viewValid = true;
        viewRepresentation = representation;
        viewValue = value;
    };

    fileUpload.validate = function () {
        if (!viewValid) {
            return false;
        }
        if (viewComponent) {
            // get label component to check if uploaded file exists
            var uLabel = viewComponent.getElementsByClassName('knime-upload-label')[0];
            if (uLabel && uLabel.textContent.indexOf('<no file selected>') === -1) {
                fileUpload.setValidationErrorMessage(null);
                return true;
            }
        } else {
            // if native component is not present there can be no validation
            return true;
        }
        var errorMessage = 'No file selected';
        if (viewRepresentation.label) {
            errorMessage += ' for ' + viewRepresentation.label;
        }
        fileUpload.setValidationErrorMessage(errorMessage + '.');
        return false;
    };

    fileUpload.setValidationErrorMessage = function (message) {
        if (!viewValid) {
            return;
        }
        if (message === null) {
            viewErrorDiv.textContent = '';
            viewErrorDiv.style.display = 'none';
        } else {
            viewErrorDiv.textContent = message;
            viewErrorDiv.style.display = 'block';
        }
//        if (!viewComponent) {
//            resizeParent();
//        }
    };

    fileUpload.value = function () {
        if (!viewValid) {
            return null;
        }
        return viewValue;
    };

    return fileUpload;

})();
