/* eslint-env jquery */
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
 *   Jun 3, 2019 (Daniel Bogenrieder, KNIME AG, Zurich, Switzerland): created
 */
window.knimeFileUploadWidget = (function () {
    var fileUpload = {
        version: '2.0.0'
    };
    fileUpload.name = 'KNIME File Upload Widget';
    var viewRepresentation = null;
    var viewValue = null;
    var viewValid = false;
    var viewComponent = null;
    var viewErrorDiv = null;
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
    
    var uploadFinished = function (filePath, fileBlob) {
        viewValue.path = filePath;
        viewValue.fileName = fileBlob.name;
        fileUpload.setValidationErrorMessage(null);
        sizeLabel.innerHTML = formatSize(fileBlob.size);
        sizeLabel.setAttribute('class', '');
        sizeLabel.setAttribute('title', '');
    };
    
    var uploadFile = function () {
        fileUpload.setValidationErrorMessage(null);
        viewValue.path = null;
        sizeLabel.innerHTML = '*';
        sizeLabel.setAttribute('class', 'knime-qf-error');
        sizeLabel.setAttribute('title', 'File upload required');
        
        var fileToUpload = this.files[0];
        if (!fileToUpload) {
            return;
        }
        if (knimeService.isRunningInAPWrapper()) {
            var reader = new FileReader();
            reader.onload = function (evt) {
                uploadFinished(evt.target.result, fileToUpload);
                viewValid = true;
                toggleProgress(false);
            };
            viewValid = false;
            toggleProgress(true);
            reader.readAsDataURL(fileToUpload);
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
                    uploadFinished(path, fileToUpload);
                } else {
                    viewValue.path = null;
                    fileUpload.setValidationErrorMessage('Upload failed.');
                }
                toggleProgress(false);
            };
            
            toggleProgress(true);
            progressContainer.title = progressPrefix;
            progressBar.style.width = '0';
            
            cancelButton.addEventListener('click', function () {
                xhr.abort();
            });
            
            xhr.send(fileToUpload);
            
        } else {
            input.setAttribute('disabled', 'disabled');
            fileUpload.setValidationErrorMessage('Upload not possible. Could not generate upload link.');
        }
    };

    fileUpload.init = function (representation, value) {
        if (checkMissingData(representation)) {
            return;
        }
        viewRepresentation = representation;
        viewValue = representation.currentValue;
        
        var messageNotFound = 'File upload not available. Native component not found.';
        var messageNotStandalone = 'File upload only available on server.';
        var displayElement = knimeService.pageBuilderPresent;
        var insertNative = !knimeService.pageBuilderPresent && knimeService.isRunningInWebportal();
        
        if (insertNative) {
            // add native component
            viewComponent = insertNativeComponent(representation, messageNotFound, messageNotStandalone);
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
                    } catch (exception) { /* do nothing */ }
                }
                // add button styles
                var btn = viewComponent.getElementsByClassName('v-upload')[0];
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
                input.style.paddingBottom = '2px';
                input.addEventListener('change', uploadFile);
                if (representation.fileTypes && representation.fileTypes.length > 0) {
                    input.setAttribute('accept', representation.fileTypes.join(','));
                }
                if (value.path) {
                    input.setAttribute('title', value.path);
                }
                div.appendChild(input);
                sizeLabel = document.createElement('label');
                sizeLabel.style.marginLeft = '10px';
                sizeLabel.style.paddingBottom = '2px';
                var labelText = '';
                if (viewValue.path) {
                    if (viewValue.path === viewRepresentation.defaultValue.path) {
                        labelText += 'Default file: ';
                    } else {
                        labelText += 'Current file: ';
                    }
                    if (viewValue.fileName) {
                        labelText += viewValue.fileName;
                        sizeLabel.setAttribute('title', viewValue.fileName);
                    } else {
                        var fileName = getFileFromPath(viewValue.path);
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
        viewErrorDiv = document.createElement('div');
        viewErrorDiv.setAttribute('class', 'knime-qf-error');
        viewErrorDiv.style.display = 'none';
        viewErrorDiv.style.marginTop = '1em';
        viewErrorDiv.setAttribute('role', 'alert');
        viewErrorDiv.appendChild(document.createTextNode(''));
        document.getElementsByTagName('body')[0].appendChild(viewErrorDiv);

        viewValid = true;
    };

    fileUpload.validate = function () {
        if (!viewValid) {
            return false;
        }
        
        if (knimeService.pageBuilderPresent) {
            if (viewValue.path) {
                return true;
            }
        } else if (viewComponent) {
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
    };

    fileUpload.value = function () {
        if (!viewValid) {
            return null;
        }
        return viewValue;
    };

    return fileUpload;

})();
