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
 */

// https://docs.chemaxon.com/display/docs/how-to-embed.md
// https://marvinjs-demo.chemaxon.com/latest/jsdoc.html
// https://marvinjs-demo.chemaxon.com/latest/jsdoc.html#marvin.Sketch.setServices(JavaScriptObject)
// https://docs.chemaxon.com/display/docs/marvin-js-web-services-dev.md#referring-web-services
// {"importFormats":["mrv","cxon","mol","rxn","rgf","rdf","smiles","cxsmiles","smarts","cxsmarts","inchi","d2s","name","cml","mol:V3","rxn:V3","rgf:V3","sdf","csmol","cssdf","cdxml","pdb"],
//  "exportFormats":["mrv","cxon","mol","rxn","rgf","rdf","smiles","cxsmiles","smarts","cxsmarts","inchi","inchikey","name","cml","mol:V3","rxn:V3","rgf:V3","sdf","sdf:ctab","csmol","cssdf","pdb"]}

const MIN_HEIGHT = 450;
const MIN_WIDTH = 500;
const LABEL_HEIGHT = 20;

window.org_knime_ext_js_molecule = (() => {

    const moleculeWidget = window.moleculeWidget;
    //const marvinBasePath = '/marvin/lib/editor.html';
    const marvinBasePath = '/marvin/lib/editorws.html';

    moleculeWidget.initSketcher = (resourceBaseUrl, molecule, format, serverURL) => {
        const sketcherPath = resourceBaseUrl ? resourceBaseUrl + marvinBasePath : marvinBasePath;
        const marvinFrame = jQuery('<iframe id="ifMarvin" class="marvin-frame">');
        marvinFrame.attr('src', sketcherPath);
        marvinFrame.css('min-height', (MIN_HEIGHT + LABEL_HEIGHT) + 'px');
        marvinFrame.css('min-width', MIN_WIDTH + 'px');

        marvinFrame.on("load", () => {
            MarvinJSUtil.getEditor("ifMarvin").then(function(editor) {
                const marvin = editor;
                console.error(JSON.stringify(marvin.getSupportedFormats()));
                console.error(serverURL + "/rest-v1/util/convert/clean");

                const services = {
                    "clean2dws" : serverURL + "/rest-v1/util/convert/clean",
                    "clean3dws" : serverURL + "/rest-v1/util/convert/clean",
                    "molconvertws" : serverURL + "/rest-v1/util/convert/molExport",
                    "stereoinfows" : serverURL + "/rest-v1/util/convert/cipStereoInfo",
                    "reactionconvertws" : serverURL + "/rest-v1/util/convert/reactionExport",
                    "hydrogenizews" : serverURL + "/rest-v1/util/convert/hydrogenizer",
                    "automapperws" : serverURL + "/rest-v1/util/convert/reactionConverter",
                    "aromatizews" : serverURL + "/rest-v1/util/convert/molExport"
                };
                console.error(JSON.stringify(services));
                /*marvin.setServices(services);*/
                //marvin.Sketch.license(serverURL, true);

                console.error(JSON.stringify(marvin.getSupportedFormats()));
                
                if (molecule) {
                    var importPromise;
                    switch (format) {
                        // TODO additional formats are missing here:
                        case 'MOL':
                            importPromise = marvin.importStructure('mol', molecule);
                            break;
                        case 'MOL V3000':
                            importPromise = marvin.importStructure('mol:V3', molecule);
                            break;
                        case 'MRV':
                            importPromise = marvin.importStructure('mrv', molecule);
                            break;
                        default:
                            importPromise = Promise.reject(new Error('Unsupported format: ' + format));
                    }
                    importPromise.catch((error) => {
                        moleculeWidget.setErrorMessage(error);
                    });
                }

                marvin.on("molchange", () => {
                    var exportPromise;
                    switch (format) {
                        // TODO additional formats are missing here:
                        case 'MOL':
                            exportPromise = marvin.exportStructure('mol');
                            break;
                        case 'MOL V3000':
                            exportPromise = marvin.exportStructure('mol:V3');
                            break;
                        case 'MRV':
                            exportPromise = marvin.exportStructure('mrv');
                            break;
                        default:
                            exportPromise = Promise.reject(new Error('Unsupported format: ' + format));
                    }
                    exportPromise.then((molecule) => {
                        moleculeWidget.setMolecule(molecule);
                    }, (error) => {
                        moleculeWidget.setErrorMessage(error);
                    });
                });

            }, function(error) {
                moleculeWidget.setErrorMessage('Loading of MarvinJS failed. ' + error);
            });
        });
        return marvinFrame;
    }

    return moleculeWidget;
    
})();
