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

const MIN_HEIGHT = 500;
const LABEL_HEIGHT = 20;
const TIMEOUT_TRESHOLD = 500;

window.org_knime_ext_js_molecule = (() => {

    const moleculeWidget = window.moleculeWidget;
    const ketcherBasePath = '/ketcher/lib/index.html';

    moleculeWidget.initSketcher = (resourceBaseUrl, molecule, format, serverURL) => {
        const sketcherPath = resourceBaseUrl ? resourceBaseUrl + ketcherBasePath : ketcherBasePath;
        const ketcherFrame = jQuery('<iframe id="ifKetcher" class="ketcher-frame">');
        ketcherFrame.attr('src', sketcherPath);
        ketcherFrame.css('min-height', (MIN_HEIGHT + LABEL_HEIGHT) + 'px');

        ketcherFrame.on("load", () => {
            // TODO revisit in UIEXT-820 - drop usage of setTimeout when refactoring this code into a vue component
            setTimeout(() => {
                const ketcher = ketcherFrame.get(0).contentWindow.ketcher;
                if (ketcher) {
                    if (molecule) {
                        ketcher.setMolecule(molecule).catch((error) => {
                            moleculeWidget.setErrorMessage(error);
                        });
                    }
                    ketcher.editor.subscribe("change", () => {
                        var moleculePromise;
                        switch (format) {
                            case 'MOL':
                                moleculePromise = ketcher.getMolfile('v2000');
                                break;
                            case 'MOL V3000':
                                moleculePromise = ketcher.getMolfile('v3000');
                                break;
                            case 'RXN':
                                moleculePromise = ketcher.getRxn('v2000');
                                break;
                            case 'RXN V3000':
                                moleculePromise = ketcher.getRxn('v3000');
                                break;
                            case 'SMILES':
                                moleculePromise = ketcher.getSmiles(false);
                                break;
                            case 'Extended SMILES':
                                moleculePromise = ketcher.getSmiles(true);
                                break;
                            case 'SMARTS':
                                moleculePromise = ketcher.getSmarts();
                                break;
                            case 'CML':
                                moleculePromise = ketcher.getCml();
                                break;
                            case 'InChI':
                                moleculePromise = ketcher.getInchi(false);
                                break;
                            case 'InChI with AuxInfo':
                                moleculePromise = ketcher.getInchi(true);
                                break;
                            case 'KET':
                                moleculePromise = ketcher.getKet();
                                break;
                            default:
                                moleculePromise = Promise.reject(new Error('Unsupported format: ' + format));
                        }
                        moleculePromise.then((molecule) => {
                            moleculeWidget.setMolecule(molecule);
                        }, (error) => {
                            moleculeWidget.setErrorMessage(error);
                        });
                    });
                } else {
                    moleculeWidget.setErrorMessage('Ketcher object not defined.');
                }
            }, TIMEOUT_TRESHOLD);
        });
        return ketcherFrame;
    }

    return moleculeWidget;
    
})();
