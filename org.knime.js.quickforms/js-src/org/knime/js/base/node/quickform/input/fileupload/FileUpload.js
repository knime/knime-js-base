/* global checkMissingData:false, insertNativeComponent:false */
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
 *   Oct 17, 2014 (Christian Albrecht, KNIME AG, Zurich, Switzerland): created
 */
window.org_knime_js_base_node_quickform_input_fileupload = (function () { // eslint-disable-line camelcase
    var fileUpload = {
        version: '1.0.0'
    };
    fileUpload.name = 'File upload';
    var _representation = null;
    var _value = null;
    var _viewValid = false;
    var _component = null;
    var _errorDiv = null;
    
    var HTTP_CREATED = 201;
    var input, sizeLabel, progressContainer, progressBar, cancelButton;
    
    var toggleProgress = function (progress) {
        if (progress) {
            input.style.display = 'none';
            sizeLabel.style.display = 'none';
            progressContainer.style.display = 'inline-block';
            cancelButton.style.display = 'inline';
        } else {
            input.style.display = 'inline';
            sizeLabel.style.display = 'inline';
            progressContainer.style.display = 'none';
            cancelButton.style.display = 'none';
        }
    };
    
    var formatSize = function (sizeInBytes) {
        var base = 1024;
        var sizes = ['Byte', 'kB', 'MB', 'GB', 'TB'];
        var index = Math.min(sizes.length - 1, Math.floor(Math.log(sizeInBytes) / Math.log(base)));
        var computedSize = Number(sizeInBytes / Math.pow(base, index)).toFixed(2);
        return 'Size: ' + computedSize + ' ' + sizes[index];
    };
    
    var getFileFromPath = function (path) {
        var index = path.lastIndexOf('/');
        if (index < 0) {
            index = path.lastIndexOf('\\');
        }
        if (index + 1 >= path.length) {
            index = -1;
        }
        return index < 0 ? path : path.substring(index + 1);
    };
    
    var uploadFile = function () {
        fileUpload.setValidationErrorMessage(null);
        _value.path = null;
        sizeLabel.innerHTML = '*';
        sizeLabel.setAttribute('class', 'knime-qf-error');
        sizeLabel.setAttribute('title', 'File upload required');
        
        var fileToUpload = this.files[0];
        if (!fileToUpload) {
            return;
        }
        var uploadUrl;
        try {
            uploadUrl = parent.KnimePageBuilderAPI.getUploadLink({
                resourceId: fileToUpload.name,
                nodeId: knimeService.nodeId
            });
        } catch (e) {
            uploadUrl = null;
        }
        if (uploadUrl) {
            var progressPrefix = 'Uploading ' + fileToUpload.name;
            var reader = new FileReader();
            var xhr = new XMLHttpRequest();
            this.xhr = xhr;
            this.xhr.upload.addEventListener('progress', function (e) {
                if (e.lengthComputable) {
                    var percentage = Math.round((e.loaded * 100) / e.total);
                    progressBar.style.width = percentage + '%';
                    progressContainer.title = progressPrefix + ' - ' + percentage + '%';
                }
            }, false);
            xhr.upload.addEventListener('load', function () {
                progressBar.style.width = '100%';
            }, false);
            xhr.open('PUT', uploadUrl);
            xhr.onloadend = function () {
                var path = this.getResponseHeader('location');
                if (this.status === HTTP_CREATED && path) {
                    _value.path = path;
                    _value.fileName = fileToUpload.name;
                    fileUpload.setValidationErrorMessage(null);
                    sizeLabel.innerHTML = formatSize(fileToUpload.size);
                    sizeLabel.setAttribute('class', '');
                    sizeLabel.setAttribute('title', '');
                } else {
                    _value.path = null;
                    fileUpload.setValidationErrorMessage('Upload failed');
                }
                toggleProgress(false);
            };
            
            toggleProgress(true);
            progressContainer.title = progressPrefix;
            progressBar.style.width = '0';
            
            cancelButton.addEventListener('click', function () {
                xhr.abort();
            });
            
            reader.onload = function (evt) {
                xhr.send(evt.target.result);
            };
            reader.readAsArrayBuffer(fileToUpload);
        } else {
            input.setAttribute('disabled', 'disabled');
            fileUpload.setValidationErrorMessage('Upload not possible. Could not generate upload link.');
        }
    };

    fileUpload.init = function (representation, value) {
        if (checkMissingData(representation)) {
            return;
        }
        _representation = representation;
        _value = representation.currentValue;

        // add native component
        var messageNotFound = 'File upload not available. Native component not found.';
        var messageNotStandalone = 'File upload only available on server.';
        var displayElement = knimeService.pageBuilderPresent && !knimeService.isRunningInAPWrapper();
        var insertNative = !knimeService.pageBuilderPresent && knimeService.isRunningInWebportal();
        
        if (insertNative) {
            // add native component
            _component = insertNativeComponent(representation, messageNotFound, messageNotStandalone);
            // set listener on label
            if (_component) {
                _component.setAttribute('aria-label', representation.label);
                var uLabel = _component.getElementsByClassName('knime-upload-label')[0];
                if (uLabel) {
                    uLabel.classList.add('knime-qf-label');
                    // use mutation event instead of observer, since IE only supports it as of version 11
                    try {
                        uLabel.addEventListener('DOMSubtreeModified', function () {
                            if (_viewValid && _errorDiv.textContent) {
                                fileUpload.validate();
                            }
                        }, false);
                    } catch (exception) { /* do nothing */ }
                }
                // add button styles
                var btn = _component.getElementsByClassName('v-upload')[0];
                if (btn) {
                    btn.classList.add('knime-qf-button');
                }
            }
        } else {
            var body = document.getElementsByTagName('body')[0];
            var div = document.createElement('div');
            div.setAttribute('class', 'quickformcontainer knime-qf-container');
            body.appendChild(div);
            var label = document.createElement('div');
            label.setAttribute('class', 'label knime-qf-title');
            label.appendChild(document.createTextNode(representation.label));
            div.appendChild(label);
            div.setAttribute('title', representation.description);
            div.setAttribute('aria-label', representation.label);
            div.setAttribute('tabindex', 0);
            if (displayElement) {
                input = document.createElement('input');
                input.setAttribute('type', 'file');
                input.style.width = '250px';
                input.addEventListener('change', uploadFile);
                if (representation.fileTypes && representation.fileTypes.length > 0) {
                    input.setAttribute('accept', representation.fileTypes.join(','));
                }
                if (_value.path) {
                    input.setAttribute('title', value.path);
                }
                div.appendChild(input);
                sizeLabel = document.createElement('label');
                sizeLabel.style.marginLeft = '10px';
                sizeLabel.style.paddingBottom = '2px';
                var labelText = '';
                if (_value.path) {
                    if (_value.path === _representation.defaultValue.path) {
                        labelText += 'Default file: ';
                    } else {
                        labelText += 'Current file: ';
                    }
                    if (_value.fileName) {
                        labelText += _value.fileName;
                        sizeLabel.setAttribute('title', _value.fileName);
                    } else {
                        var fileName = getFileFromPath(_value.path);
                        labelText += fileName;
                        sizeLabel.setAttribute('title', fileName);
                    }
                } else {
                    labelText = '*';
                    sizeLabel.setAttribute('title', 'File upload required');
                    sizeLabel.setAttribute('class', 'knime-qf-error');
                }
                sizeLabel.appendChild(document.createTextNode(labelText));
                div.appendChild(sizeLabel);
                                
                // Progress bar
                progressContainer = document.createElement('div');
                progressContainer.setAttribute('class', 'progressBar');
                progressContainer.style.cssText = 'display: none; border: 1px solid hsl(12, 4.2%, 23.3%); ' +
                    'height: 5px; width: 250px;';
                progressBar = document.createElement('div');
                progressBar.style.cssText = 'height: 5px; width: 0px; background-color: hsl(50.8, 100%, 50%);';
                progressContainer.appendChild(progressBar);
                div.appendChild(progressContainer);

                // Cancel button
                cancelButton = document.createElement('button');
                cancelButton.appendChild(document.createTextNode('Cancel'));
                cancelButton.style.cssText = 'display: none; margin: 0; margin-left: 10px;';
                div.appendChild(cancelButton);
            } else {
                var placeHolder = window.createPlaceHolder(messageNotStandalone);
                div.appendChild(placeHolder);
            }
        }

        // add error field
        _errorDiv = document.createElement('div');
        _errorDiv.setAttribute('class', 'knime-qf-error');
        _errorDiv.style.display = 'none';
        _errorDiv.style.marginTop = '1em';
        _errorDiv.setAttribute('role', 'alert');
        _errorDiv.appendChild(document.createTextNode(''));
        document.getElementsByTagName('body')[0].appendChild(_errorDiv);

        _viewValid = true;
    };

    fileUpload.validate = function () {
        if (!_viewValid) {
            return false;
        }
        if (knimeService.pageBuilderPresent) {
            if (_value.path) {
                return true;
            }
        } else if (_component) {
            // get label component to check if uploaded file exists
            var uLabel = _component.getElementsByClassName('knime-upload-label')[0];
            if (uLabel && uLabel.textContent.indexOf('<no file selected>') === -1) {
                fileUpload.setValidationErrorMessage(null);
                return true;
            }
        } else {
            // if native component is not present there can be no validation
            return true;
        }
        var errorMessage = 'No file selected';
        if (_representation.label) {
            errorMessage += ' for ' + _representation.label;
        }
        fileUpload.setValidationErrorMessage(errorMessage + '.');
        return false;
    };

    fileUpload.setValidationErrorMessage = function (message) {
        if (!_viewValid) {
            return;
        }
        if (message === null) {
            _errorDiv.textContent = '';
            _errorDiv.style.display = 'none';
        } else {
            _errorDiv.textContent = message;
            _errorDiv.style.display = 'block';
        }
    };

    fileUpload.value = function () {
        if (!_viewValid) {
            return null;
        }
        return _value;
    };

    return fileUpload;

})();
