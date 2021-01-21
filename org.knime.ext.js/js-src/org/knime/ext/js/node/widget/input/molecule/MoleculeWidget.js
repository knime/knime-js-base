/* global jQuery:false, require:false, ketcher:false, checkMissingData:false */
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
 * History
 *   Mai 29, 2020 (Daniel Bogenrieder, KNIME AG, Zurich, Switzerland): created
 *   Nov 18, 2020 (Daniel Bogenrieder, KNIME AG, Zurich, Switzerland): added ketcher 2.0
 */
window.knimeMoleculeWidget = (function () {

    var moleculeWidget = {
        version: '1.0.0'
    };
    moleculeWidget.name = 'Molecule widget';
    var viewValid = false;

    var inWebportal = false;
    var isPageBuilderPresent = false;
    var customSketcher = false;
    var callCount = 0;
    var MIN_HEIGHT = 500;
    var LABEL_HEIGHT = 20;
    var TIMEOUT_TRESHOLD = 500;
    var DONE_STATE = 4;
    var basePathMoleculeFolder = 'org/knime/ext/js/node/widget/input/molecule/';
    var cssPathWebPortal = '/' + basePathMoleculeFolder + 'MoleculeWidget.css';
    var cssPathLegacyWebPortal = document.URL + 'VAADIN/src-js/' + basePathMoleculeFolder + 'MoleculeWidget.css';
    var ketcherBasePath = '/js-lib/ketcher2.0/ketcher.html';
    var ketcherPathLegacyWebPortal = './VAADIN/src-js' + ketcherBasePath;

    var wgdiv,
        sketcherFrame,
        errorMessage,
        sketchTranslator,
        currentMolecule,
        format,
        sketcherPath,

        createContainerFrame, initSketcher, requestResource, requireKetcher;

    moleculeWidget.init = function (representation) {
        if (checkMissingData(representation)) {
            return;
        }
        inWebportal = knimeService && knimeService.isRunningInWebportal();
        isPageBuilderPresent = knimeService && knimeService.pageBuilderPresent;
        customSketcher = inWebportal;
        currentMolecule = representation.currentValue.moleculeString;
        format = representation.format;
        sketcherPath = representation.sketcherLocation;
        customSketcher = inWebportal && sketcherPath;
        var body = jQuery('body');
        wgdiv = jQuery('<div class="quickformcontainer" data-iframe-height data-iframe-width>');
        body.append(wgdiv);

        // Check if it's executed in the new WebPortal
        if (inWebportal && isPageBuilderPresent) {
            sketcherPath = knimeService.resourceBaseUrl + ketcherBasePath;
            var customSketcherPath = parent.KnimePageBuilderAPI.getCustomSketcherPath();
            cssPathWebPortal = knimeService.resourceBaseUrl + cssPathWebPortal;
            if (customSketcherPath && customSketcherPath.startsWith('/')) {
                customSketcher = true;
                sketcherPath = customSketcherPath;
            }
            sketcherFrame = initSketcher(sketcherPath, cssPathWebPortal, customSketcher, currentMolecule);
            wgdiv.append(sketcherFrame);
        // Check if it is executed in the old WebPortal
        } else if (inWebportal) {
            sketcherFrame = initSketcher(ketcherPathLegacyWebPortal, cssPathLegacyWebPortal, customSketcher, currentMolecule);
            wgdiv.append(sketcherFrame);
        } else {
            var jsonPath = basePathMoleculeFolder + 'MoleculeWidgetConfig.json';
            requestResource(jsonPath, requireKetcher);
        }

        errorMessage = jQuery('<div>');
        errorMessage.css('display', 'none');
        errorMessage.css('color', 'red');
        errorMessage.css('font-style', 'italic');
        errorMessage.css('margin', '10px');
        wgdiv.prepend(errorMessage);
        var titleDiv = jQuery('<div class="label knime-qf-title"></div>');
        titleDiv.css('margin-left', '10px');
        titleDiv.text(representation.label);
        wgdiv.prepend(titleDiv);
        wgdiv.attr('title', representation.description);
        wgdiv.attr('aria-label', representation.label);
        wgdiv.attr('tabindex', 0);
        viewValid = true;
    };

    initSketcher = function (loc, cssPath, customSketcher, currentMolecule) {
        sketcherFrame = createContainerFrame();
        if (customSketcher) {
            loc = sketcherPath;
        }
        sketcherFrame.attr('name', currentMolecule);
        sketcherFrame.attr('src', loc);

        var cssLink = document.createElement('link');
        cssLink.href = cssPath;
        cssLink.rel = 'stylesheet';
        cssLink.type = 'text/css';
        sketcherFrame.load(function () {
            // Inject MoleculeWidget.css file as it is otherwise not in the iFrame
            sketcherFrame.get(0).contentWindow.document.body.appendChild(cssLink);
            setTimeout(function () {
                if (customSketcher) {
                    sketchTranslator = sketcherFrame.get(0).contentWindow.SketchTranslator;
                    if (sketchTranslator) {
                        sketchTranslator.init(currentMolecule, null, moleculeWidget.update, format);
                    } else {
                        errorMessage.text('Could not initialize sketcher. SketchTranslator not found.');
                        errorMessage.css('display', 'block');
                    }
                } else {
                    var ketcher = sketcherFrame.get(0).contentWindow.ketcher;
                    if (ketcher) {
                        if (currentMolecule !== '') {
                            ketcher.setMolecule(currentMolecule);
                        }
                    } else {
                        errorMessage.text('Could not initialize sketcher. Ketcher object not found.');
                        errorMessage.css('display', 'block');
                    }
                }
            }, TIMEOUT_TRESHOLD);
        });
        return sketcherFrame;
    };

    requestResource = function (filePath, onCallback) {
        var xhr = new XMLHttpRequest();
        xhr.open('GET', filePath, true);
        xhr.onreadystatechange = function () {
            if (this.readyState !== DONE_STATE) {
                return;
            }
            var result = onCallback(this.responseText);
            return result;
        };
        xhr.send();
    };

    requireKetcher = function (response) {
        var sketcherDiv = jQuery('<div class="knime-sketcher-div" role="application">');
        wgdiv.append(sketcherDiv);
        var ketcherConfig = JSON.parse(response);
        require.config(ketcherConfig);

        require(['ketcher'], function (ketcher) {
            window.ketcher = ketcher;
            // Set the custom Path to the ketcher files to "js-lib/ketcher2.0"
            var searchParams = new URLSearchParams(window.location.search)
            searchParams.set('api_path', 'js-lib/ketcher2.0/');
            var newRelativePathQuery = window.location.pathname + '?' + searchParams.toString();
            history.pushState(null, '', newRelativePathQuery);
            dispatchEvent(new Event('load'));
            if (currentMolecule !== '') {
                ketcher.setMolecule(currentMolecule);
            }
        });
    };

    createContainerFrame = function () {
        sketcherFrame = jQuery('<iframe class="knime-sketcher-frame">');
        sketcherFrame.width('100%');
        sketcherFrame.height('calc(100% - 20px)');
        sketcherFrame.attr('frameborder', '0');
        sketcherFrame.css('min-height', (MIN_HEIGHT + LABEL_HEIGHT) + 'px');
        return sketcherFrame;
    };

    moleculeWidget.update = function () {
    // Do we need to remember the value right away? Probably not.
    };

    moleculeWidget.validate = function () {
        var k;
        if (customSketcher) {
            if (!sketchTranslator) {
                errorMessage.text('Could not fetch molecule from sketcher. SketchTranslator not found.');
                errorMessage.css('display', 'block');
                return false;
            }
            try {
                sketchTranslator.getData(format);
            } catch (exception) {
                errorMessage.text('Could not fetch molecule from sketcher: ' + exception);
                errorMessage.css('display', 'block');
                return false;
            }
        } else {
            k = inWebportal ? sketcherFrame.get(0).contentWindow.ketcher : ketcher;
            if (typeof k === 'undefined') {
                errorMessage.text('Ketcher object not defined.');
                errorMessage.css('display', 'block');
                return false;
            }
        }
        return true;
    };

    moleculeWidget.setValidationErrorMessage = function (message) {
        if (!viewValid) {
            return;
        }
        if (message === null) {
            errorMessage.text('');
            errorMessage.css('display', 'none');
        } else {
            errorMessage.text(message);
            errorMessage.css('display', 'block');
        }
    };

    moleculeWidget.value = function () {
        var k, molecule;
        if (!viewValid) {
            return null;
        }
        if (customSketcher && sketchTranslator) {
            try {
                molecule = sketchTranslator.getData(format);
            } catch (exception) {
                // should not happen after succesful validate
                molecule = null;
            }
        } else {
            k = inWebportal ? sketcherFrame.get(0).contentWindow.ketcher : ketcher;
            if (!format) {
                format = 'SDF';
            }
            if (typeof k === 'undefined') {
                // should not happen after succesful validate
                molecule = null;
            } else if (format.toLowerCase() === 'rxn' ||
                        format.toLowerCase() === 'sdf' ||
                        format.toLowerCase() === 'mol') {
                molecule = k.getMolfile();
            } else {
                molecule = k.getSmiles();
            }
        }
        var viewValue = {};
        viewValue.moleculeString = molecule;
        return viewValue;
    };

    return moleculeWidget;
})();
