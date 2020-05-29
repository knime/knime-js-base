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
 *   Mai 29, 2020 (Daniel Bogenrieder, KNIME AG, Zurich, Switzerland): created
 */
org_knime_ext_js_node_widget_input_molecule = function () {

    var MIN_WIDTH = 300;
    var MIN_HEIGHT = 300;

    var moleculeWidget = {
        version: "1.0.0"
    };
    moleculeWidget.name = "Molecule widget";
    var sketcherFrame;
    var errorMessage;
    var sketchTranslator;
    var currentMolecule, format;
    var viewValid = false;

    var localInitCode = '       var param_string = document.location.search;'
        + '            if (param_string.length > 0)' + '                param_string = param_string.substring(1);'
        + '            var param_list = param_string.split(/&/g);' + '            var param_hash = {};'
        + '            for (var i = 0; i < param_list.length; ++i) {'
        + '                var pair = param_list[i].split(\'=\', 2);'
        + '                param_hash[pair[0]] = pair.length != 2 || unescape(pair[1]);' + '            }'
        + '	    if (param_hash.ketcher_maximize) {'
        + '		jQuery(\'ketcher_div\').removeClassName(\'ketcherDivMaxSize\');' + '	    }'
        + '            // Initialize ketcher'
        + '            ketcher.init({ketcher_api_url: param_hash.ketcher_api_url});' + '		};'

    var inWebportal = false;
    var customSketcher = false;
    var callCount = 0;

    moleculeWidget.init = function (representation) {
        if (callCount++ > 0) {
            return;
        }
        if (checkMissingData(representation)) {
            return;
        }
        inWebportal = (knimeService && knimeService.isRunningInWebportal());
        customSketcher = inWebportal;
        currentMolecule = representation.currentValue.moleculeString;
        format = representation.format;
        var sketcherPath = representation.sketcherPath;
        if (!sketcherPath) {
            sketcherPath = representation.sketcherLocation;
        }
        customSketcher = (inWebportal && sketcherPath);

        var body = jQuery('body');
        var qfdiv = jQuery('<div class="quickformcontainer">');
        body.append(qfdiv);
        // var width = Math.max(MIN_WIDTH, representation.width);
        // var height = Math.max(MIN_HEIGHT, representation.height);

        if (inWebportal) {
            jQuery('script').each(function () {
                var s = jQuery(this);
                var src = s.attr("src");
                if (src && src.indexOf("js-lib/ketcher/") != -1) {
                    s.remove();
                }
            });
            sketcherFrame = jQuery('<iframe class="knime-sketcher-frame">');
            // sketcherFrame.width((width + 20) + "px");
            // sketcherFrame.height((height + 20) + "px");
            // qfdiv.width((width + 20) + "px");
            sketcherFrame.attr("frameborder", "0");
            sketcherFrame.css("border", "none");
            sketcherFrame.css("margin", "0");
            sketcherFrame.css("background", "none");
            var loc = knimeService.resourceBaseUrl + "/org/knime/ext/js/node/widget/input/molecule/MoleculeWidget.html";
            sketcherFrame.attr("name", currentMolecule);
            if (customSketcher) {
                loc = sketcherPath;
            }
            sketcherFrame.attr("src", loc);
            sketcherFrame.load(function () {
                setTimeout(function () {
                    if (customSketcher) {
                        sketchTranslator = sketcherFrame.get(0).contentWindow.SketchTranslator;
                        if (sketchTranslator) {
                            sketchTranslator.init(currentMolecule, null, moleculeWidget.update);
                        } else {
                            errorMessage.text("Could not initialize sketcher. SketchTranslator not found.");
                            errorMessage.css('display', 'block');
                            resizeParent();
                        }
                    } else {
                        var ketcher = sketcherFrame.get(0).contentWindow.ketcher;
                        if (ketcher) {
                            ketcher.init();
                            ketcher.setMolecule(currentMolecule);
                        } else {
                            errorMessage.text("Could not initialize sketcher. Ketcher object not found.");
                            errorMessage.css('display', 'block');
                            resizeParent();
                        }
                    }
                }, 500);
            });
            qfdiv.append(sketcherFrame);
        } else {
            require.config({
                paths: {
                    'prototype': 'js-lib/ketcher/prototype-min',
                    'raphael': 'js-lib/ketcher/raphael',
                    'base64': 'js-lib/ketcher/base64',
                    'keymaster': 'js-lib/ketcher/third_party/keymaster',
                    'common': 'js-lib/ketcher/util/common',
                    'vec2': 'js-lib/ketcher/util/vec2',
                    'set': 'js-lib/ketcher/util/set',
                    'map': 'js-lib/ketcher/util/map',
                    'pool': 'js-lib/ketcher/util/pool',
                    'element': 'js-lib/ketcher/chem/element',
                    'struct': 'js-lib/ketcher/chem/struct',
                    'molfile': 'js-lib/ketcher/chem/molfile',
                    'sgroup': 'js-lib/ketcher/chem/sgroup',
                    'struct_valence': 'js-lib/ketcher/chem/struct_valence',
                    'dfs': 'js-lib/ketcher/chem/dfs',
                    'cis_trans': 'js-lib/ketcher/chem/cis_trans',
                    'stereocenters': 'js-lib/ketcher/chem/stereocenters',
                    'smiles': 'js-lib/ketcher/chem/smiles',
                    'inchi': 'js-lib/ketcher/chem/inchi',
                    'visel': 'js-lib/ketcher/rnd/visel',
                    'restruct': 'js-lib/ketcher/rnd/restruct',
                    'restruct_rendering': 'js-lib/ketcher/rnd/restruct_rendering',
                    'render': 'js-lib/ketcher/rnd/render',
                    'templates': 'js-lib/ketcher/rnd/templates',
                    'editor': 'js-lib/ketcher/rnd/editor',
                    'elem_table': 'js-lib/ketcher/rnd/elem_table',
                    'rgroup_table': 'js-lib/ketcher/rnd/rgroup_table',
                    'ui': 'js-lib/ketcher/ui/ui',
                    'actions': 'js-lib/ketcher/ui/actions',
                    'reaxys': 'js-lib/ketcher/reaxys/reaxys',
                    'ketcher': 'js-lib/ketcher/ketcher'
                },
                shim: {
                    'vec2': {
                        deps: ['common']
                    },
                    'pool': {
                        deps: ['map', 'set', 'vec2']
                    },
                    'element': {
                        deps: ['vec2']
                    },
                    'struct': {
                        deps: ['pool']
                    },
                    'molfile': {
                        deps: ['struct']
                    },
                    'sgroup': {
                        deps: ['pool']
                    },
                    'struct_valence': {
                        deps: ['struct']
                    },
                    'dfs': {
                        deps: ['struct']
                    },
                    'cis_trans': {
                        deps: ['struct']
                    },
                    'stereocenters': {
                        deps: ['struct']
                    },
                    'smiles': {
                        deps: ['struct']
                    },
                    'inchi': {
                        deps: ['struct']
                    },
                    'visel': {
                        deps: ['struct']
                    },
                    'restruct': {
                        deps: ['raphael', 'struct', 'visel']
                    },
                    'restruct_rendering': {
                        deps: ['restruct']
                    },
                    'render': {
                        deps: ['prototype', 'restruct']
                    },
                    'templates': {
                        deps: ['visel']
                    },
                    'editor': {
                        deps: ['prototype', 'restruct']
                    },
                    'elem_table': {
                        deps: ['prototype', 'raphael', 'visel']
                    },
                    'rgroup_table': {
                        deps: ['prototype', 'visel']
                    },
                    'ui': {
                        deps: ['render', 'templates', 'editor', 'elem_table', 'rgroup_table']
                    },
                    'actions': {
                        deps: ['ui']
                    },
                    'reaxys': {
                        deps: ['ui']
                    },
                    'ketcher': {
                        deps: ['prototype', 'raphael', 'base64', 'keymaster', 'common', 'vec2', 'set', 'map', 'pool',
                            'element', 'struct', 'molfile', 'sgroup', 'struct_valence', 'dfs', 'cis_trans',
                            'stereocenters', 'smiles', 'inchi', 'visel', 'restruct', 'restruct_rendering', 'render',
                            'templates', 'editor', 'elem_table', 'rgroup_table', 'ui', 'actions', 'reaxys'],
                        exports: 'ketcher'
                    }
                }
            });
            require(['ketcher'], function () {
                var sketcherDiv = jQuery('<div class="knime-sketcher-div">');
                // sketcherDiv.width(width + "px");
                // sketcherDiv.height(height + "px");
                var xhr = new XMLHttpRequest();
                debugger;
                xhr.open('GET', 'org/knime/ext/js/node/widget/input/molecule/MoleculeWidget.html', true);
                var ketcherHTML;
                xhr.onreadystatechange = function () {
                    if (this.readyState !== 4)
                        return;
                    // if (this.status!==200) return; // or whatever error handling you want
                    ketcherHTML = this.responseText;
                    eval(localInitCode);
                    sketcherDiv.html(ketcherHTML);
                    ketcher.init();
                    ketcher.setMolecule(currentMolecule);
                };
                xhr.send();
                sketcherDiv.css("position", "relative");
                qfdiv.append(sketcherDiv);
                // qfdiv.width(width + "px");
            });
        }

        errorMessage = jQuery('<div>');
        errorMessage.css('display', 'none');
        errorMessage.css('color', 'red');
        errorMessage.css('font-style', 'italic');
        errorMessage.css('margin', '10px');
        qfdiv.prepend(errorMessage);
        qfdiv.prepend('<div class="label" style="margin-left: 10px">' + representation.label + '</div>');
        qfdiv.attr('title', representation.description);
        resizeParent();
        viewValid = true;
    };

    moleculeWidget.update = function () {
    // Do we need to remember the value right away? Probably not.
    }

    moleculeWidget.validate = function () {
        if (customSketcher) {
            if (!sketchTranslator) {
                errorMessage.text("Could not fetch molecule from sketcher. SketchTranslator not found.");
                errorMessage.css('display', 'block');
                resizeParent();
                return false;
            }
            try {
                molecule = sketchTranslator.getData(format);
            } catch (exception) {
                errorMessage.text("Could not fetch molecule from sketcher: " + exception);
                errorMessage.css('display', 'block');
                resizeParent();
                return false;
            }
        } else {
            var k = inWebportal ? sketcherFrame.get(0).contentWindow.ketcher : ketcher;
            if (typeof k == 'undefined') {
                errorMessage.text("Ketcher object not defined.");
                errorMessage.css('display', 'block');
                resizeParent();
                return false;
            }
        }
        return true;
    }

    moleculeWidget.setValidationErrorMessage = function (message) {
        if (!viewValid) {
            return;
        }
        if (message != null) {
            errorMessage.text(message);
            errorMessage.css('display', 'block');
        } else {
            errorMessage.text('');
            errorMessage.css('display', 'none');
        }
        resizeParent();
    }

    moleculeWidget.value = function () {
        if (!viewValid) {
            return null;
        }
        var molecule;
        if (customSketcher && sketchTranslator) {
            try {
                molecule = sketchTranslator.getData(format);
            } catch (exception) {
                // should not happen after succesful validate
                molecule = null;
            }
        } else {
            var k = inWebportal ? sketcherFrame.get(0).contentWindow.ketcher : ketcher;
            if (!format) {
                format = "SDF";
            }
            if (typeof k != 'undefined') {
                debugger;
                if (format.toLowerCase() === "rxn" || format.toLowerCase() === "sdf" || format.toLowerCase() === "mol")
                    molecule = k.getMolfile();
                else
                    molecule = k.getSmiles();
            } else {
                // should not happen after succesful validate
                molecule = null;
            }
        }
        var viewValue = new Object();
        viewValue.moleculeString = molecule;
        return viewValue;
    };

    return moleculeWidget;

}();
