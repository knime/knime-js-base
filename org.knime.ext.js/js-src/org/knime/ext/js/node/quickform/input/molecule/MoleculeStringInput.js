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
 *   Oct 14, 2013 (Patrick Winter, KNIME AG, Zurich, Switzerland): created
 */
org_knime_ext_js_node_quickform_input_molecule = function() {

	var MIN_WIDTH = 300;
	var MIN_HEIGHT = 300;
	
	var moleculeInput = {
			version: "1.0.0"
	};
	moleculeInput.name = "Molecule input";
	var sketcherFrame;
	var errorMessage;
	var sketchTranslator;
	var currentMolecule, format;
	var viewValid = false;
	
	var localInitCode = 
	'       var param_string = document.location.search;'
	+ '            if (param_string.length > 0)'
	+ '                param_string = param_string.substring(1);'
	+ '            var param_list = param_string.split(/&/g);'
	+ '            var param_hash = {};'
	+ '            for (var i = 0; i < param_list.length; ++i) {'
	+ '                var pair = param_list[i].split(\'=\', 2);'
	+ '                param_hash[pair[0]] = pair.length != 2 || unescape(pair[1]);'
	+ '            }'
	+ '	    if (param_hash.ketcher_maximize) {'
	+ '		jQuery(\'ketcher_div\').removeClassName(\'ketcherDivMaxSize\');'
	+ '	    }'
	+ '            // Initialize ketcher'
	+ '            ketcher.init({ketcher_api_url: param_hash.ketcher_api_url});'
	+ '		};'
	
	var localKetcherContent =
	'        <div id="ketcher_div" class="ketcherDivMaxSize">'
	+ '            <table id="ketcher_window">'
	+ '                <tr align="center" id="main_toolbar">'
	+ '                    <td style="width:36px"><div style="position:relative"><img class="sideButton modeButton stateButton" id="selector" selid="selector_lasso" src="js-lib/ketcher/icons/png/selection/lasso.sidebar.png" alt="" title="Lasso Selection Tool (Esc)" /><img class="dropdownButton" id="selector_dropdown" src="js-lib/ketcher/icons/png/main/dropdown.png" alt="" /></div></td>'
	+ '                    <td class="toolDelimiter"></td>'
	+ '                    <!--td style="width:36px"><object type="image/svg+xml" width="28" height="28" data="svg/document-new28x28.svg"></object></td-->'
	+ '                    <td class="toolButtonCell toolButton" id="new"><img src="js-lib/ketcher/icons/png/main/document-new.png" alt="" title="Clear Canvas (Ctrl+N)" /></td>'
	+ '                    <td class="toolButtonCell toolButton" id="open"><img src="js-lib/ketcher/icons/png/main/document-open.png" alt="" title="Open... (Ctrl+O)" /></td>'
	+ '                    <td class="toolButtonCell toolButton" id="save"><img src="js-lib/ketcher/icons/png/main/document-save-as.png" alt="" title="Save As... (Ctrl+S)" /></td>'
	+ '                    <td class="toolDelimiter"></td>'
	+ '                    <td class="toolButtonCell toolButton buttonDisabled" id="undo"><img src="js-lib/ketcher/icons/png/main/edit-undo.png" alt="" title="Undo (Ctrl+Z)" /></td>'
	+ '                    <td class="toolButtonCell toolButton buttonDisabled" id="redo"><img src="js-lib/ketcher/icons/png/main/edit-redo.png" alt="" title="Redo (Ctrl+Y)" /></td>'
	+ '                    <td class="toolButtonCell toolButton buttonDisabled" id="cut"><img src="js-lib/ketcher/icons/png/main/edit-cut.png" alt="" title="Cut (Ctrl+X)" /></td>'
	+ '                    <td class="toolButtonCell toolButton buttonDisabled" id="copy"><img src="js-lib/ketcher/icons/png/main/edit-copy.png" alt="" title="Copy (Ctrl+C)" /></td>'
	+ '                    <td class="toolButtonCell toolButton buttonDisabled" id="paste"><img src="js-lib/ketcher/icons/png/main/edit-paste.png" alt="" title="Paste (Ctrl+V)" /></td>'
	+ '                    <td class="toolDelimiter"></td>'
	+ '                    <td class="toolButtonCell toolButton" id="zoom_in"><img src="js-lib/ketcher/icons/png/main/view-zoom-in.png" alt="" title="Zoom In (+)" /></td>'
	+ '                    <td class="toolButtonCell toolButton" id="zoom_out"><img src="js-lib/ketcher/icons/png/main/view-zoom-out.png" alt="" title="Zoom Out (-)" /></td>'
	+ '                    <td id="zoom_list_cell" style="width:95px"><select id="zoom_list"></select></td>'
	+ '                    <td class="toolDelimiter"></td>'
	+ '                    <td class="toolButtonCell toolButton serverRequired" id="clean_up"><img title="Clean Up (Ctrl+L)" alt="" src="js-lib/ketcher/icons/png/main/layout.png" /></td>'
	+ '                    <td class="toolButtonCell toolButton serverRequired" id="aromatize"><img title="Aromatize" alt="" src="js-lib/ketcher/icons/png/main/arom.png" /></td>'
	+ '                    <td class="toolButtonCell toolButton serverRequired" id="dearomatize"><img title="Dearomatize" alt="" src="js-lib/ketcher/icons/png/main/dearom.png" /></td>'
	+ '                    <td style="width:100%"></td>'
	+ '                    <td id="toolText" style="width:32px"></td>'
	+ '                    <td class="toolDelimiter"></td>'
	+ '                    <td style="width:1px" rowspan="15"></td>'
	+ '                    <td style="width:36px;padding:0 2px 0 0;"><a href="#" onclick="ui.showDialog(\'about_dialog\')"><img src="js-lib/ketcher/icons/png/main/logo.png" alt="" title="GGA Software Services" /></a></td>'
	+ '                </tr>'
	+ '                <tr align="center" class="sideButtonCell">'
	+ '                    <td><img class="sideButton modeButton" id="select_erase" src="js-lib/ketcher/icons/png/main/edit-clear.png" alt="" title="Erase" /></td>'
	+ '                    <td colspan="21" rowspan="14"><div id="client_area"></div></td>'
	+ '                    <td><div class="sideButton modeButton" id="atom_h" title="H Atom (H)"></div></td>'
	+ '                </tr>'
	+ '                <tr align="center" class="sideButtonCell">'
	+ '                    <td>'
	+ '                        <div style="position:relative">'
	+ '                            <img class="sideButton modeButton stateButton" id="bond" selid="bond_single" src="js-lib/ketcher/icons/png/bond/bond_single.sidebar.png" alt="" title="Single Bond (1)" />'
	+ '                            <img class="dropdownButton" id="bond_dropdown" src="js-lib/ketcher/icons/png/main/dropdown.png" alt="" />'
	+ '                        </div>'
	+ '                    </td>'
	+ ''
	+ '                    <td><div class="sideButton modeButton" id="atom_c" title="C Atom (C)"></div></td>'
	+ '                </tr>'
	+ '                <tr align="center" class="sideButtonCell">'
	+ '                    <td>'
	+ '                        <div style="position:relative">'
	+ '                            <img class="sideButton modeButton" id="chain" src="js-lib/ketcher/icons/png/main/chain.png" alt="" title="Chain Tool" />'
	+ '                        </div>'
	+ '                    </td>'
	+ '                    <td><div class="sideButton modeButton" id="atom_n" title="N Atom (N)"></div></td>'
	+ '                </tr>'
	+ '                <tr align="center" class="sideButtonCell">'
	+ '                    <td>'
	+ '                        <div style="position:relative">'
	+ '                            <img class="sideButton modeButton stateButton" id="template" selid="template_0" src="js-lib/ketcher/icons/png/template/template0.sidebar.png" alt="" title="Benzene (T)" />'
	+ '                            <img class="dropdownButton" id="template_dropdown" src="js-lib/ketcher/icons/png/main/dropdown.png" alt="" />'
	+ '                        </div>'
	+ '                    </td>'
	+ '                    <td><div class="sideButton modeButton" id="atom_o" title="O Atom (O)"></div></td>'
	+ '                </tr>'
	+ '                <tr align="center" class="sideButtonCell">'
	+ '                    <td>'
	+ '                        <div style="position:relative">'
	+ '                            <img class="sideButton modeButton" id="charge_plus" src="js-lib/ketcher/icons/png/main/charge_plus.png" alt="" title="Charge Plus (5)" />'
	+ '                        </div>'
	+ '                    </td>'
	+ '                    <td><div class="sideButton modeButton" id="atom_s" title="S Atom (S)"></div></td>'
	+ '                </tr>'
	+ '                <tr align="center" class="sideButtonCell">'
	+ '                    <td>'
	+ '                        <div style="position:relative">'
	+ '                            <img class="sideButton modeButton" id="charge_minus" src="js-lib/ketcher/icons/png/main/charge_minus.png" alt="" title="Charge Minus (5)" />'
	+ '                        </div>'
	+ '                    </td>'
	+ '                    <td><div class="sideButton modeButton" id="atom_p" title="P Atom (P)"></div></td>'
	+ '                </tr>'
	+ '                <tr align="center" class="sideButtonCell">'
	+ '                    <td><div style="position:relative"><img class="sideButton modeButton stateButton" id="reaction" selid="reaction_arrow" src="js-lib/ketcher/icons/png/reaction/reaction-arrow.sidebar.png" alt="" title="Reaction Arrow Tool" /><img class="dropdownButton" id="reaction_dropdown" src="js-lib/ketcher/icons/png/main/dropdown.png" alt="" /></div></td>'
	+ '                    <td><div class="sideButton modeButton" id="atom_f" title="F Atom (F)"></div></td>'
	+ '                </tr>'
	+ '                <tr align="center" class="sideButtonCell">'
	+ '                    <td><img class="sideButton modeButton" id="sgroup" src="js-lib/ketcher/icons/png/main/sgroup.png" alt="" title="S-Group (Ctrl+G)" /></td>'
	+ '                    <td><div class="sideButton modeButton" id="atom_cl" title="Cl Atom (Shift+C)"></div></td>'
	+ '                </tr>'
	+ '                <tr align="center" class="sideButtonCell">'
	+ '                    <td><div style="position:relative"><img class="sideButton modeButton stateButton" id="rgroup" selid="rgroup_label" src="js-lib/ketcher/icons/png/rgroup/rgroup-label.sidebar.png" alt="" title="R-Group Tool (Shift+R)" /><img class="dropdownButton" id="rgroup_dropdown" src="js-lib/ketcher/icons/png/main/dropdown.png" alt="" /></div></td>'
	+ '                    <td><div class="sideButton modeButton" id="atom_br" title="Br Atom (Shift+B)"></div></td>'
	+ '                </tr>'
	+ '                <tr align="center" class="sideButtonCell">'
	+ '                    <td><div style="position:relative"><img class="sideButton modeButton stateButton" id="transform" selid="transform_rotate" src="js-lib/ketcher/icons/png/transform/transform-rotate.sidebar.png" alt="" title="Rotate Tool" /><img class="dropdownButton" id="transform_dropdown" src="js-lib/ketcher/icons/png/main/dropdown.png" alt="" /></div></td>'
	+ '                    <td><div class="sideButton modeButton" id="atom_i" title="I Atom (I)"></div></td>'
	+ '                </tr>'
	+ '                <tr align="center" class="sideButtonCell serverRequired">'
	+ '                    <td>'
	+ '                        <div style="position:relative">'
	+ '                            <img class="sideButton modeButton stateButton" id="customtemplate" selid="customtemplate_0" src="js-lib/ketcher/icons/png/customtemplate/customtemplate0.sidebar.png" alt="" title="" />'
	+ '                            <img class="dropdownButton" id="customtemplate_dropdown" src="js-lib/ketcher/icons/png/main/dropdown.png" alt="" />'
	+ '                        </div>'
	+ '                    </td>'
	+ '                    <td><div class="sideButton modeButton" id="atom_table" title="Periodic table"></div></td>'
	+ '                </tr>'
	+ '                <tr align="center" class="sideButtonCell">'
	+ '                    <td></td>'
	+ '                    <td><div class="sideButton modeButton" id="atom_reagenerics" title="Reaxys Generics"></div></td>'
	+ '                </tr>'
	+ '                <tr align="center">'
	+ '                    <td></td>'
	+ '                    <td></td>'
	+ '                </tr>'
	+ '            </table>'
	+ ''
	+ '            <div class="dropdownList" id="selector_dropdown_list" style="display:none">'
	+ '                <table>'
	+ ''
	+ '                    <tr class="dropdownListItem" id="selector_lasso" title="Lasso Selection Tool (Esc)">'
	+ '                        <td><div id="select_lasso_todo"><img class="dropdownIcon" src="js-lib/ketcher/icons/png/selection/lasso.dropdown.png" alt="" /></div>Lasso Selection Tool</td>'
	+ '                    </tr>'
	+ '                    <tr class="dropdownListItem" id="selector_square" title="Rectangle Selection Tool (Esc)">'
	+ '                        <td><div id="select_square_todo"><img class="dropdownIcon" src="js-lib/ketcher/icons/png/selection/rectangle.dropdown.png" alt="" /></div>Rectangle Selection Tool</td>'
	+ '                    </tr>'
	+ '                    <tr class="dropdownListItem" id="selector_fragment" title="Fragment Selection Tool (Esc)">'
	+ '                        <td><div id="select_fragment"><img class="dropdownIcon" src="js-lib/ketcher/icons/png/selection/structure.dropdown.png" alt="" /></div>Fragment Selection Tool</td>'
	+ '                    </tr>'
	+ '                </table>'
	+ '            </div>'
	+ ''
	+ '            <div class="dropdownList renderFirst" id="bond_dropdown_list" style="display:none">'
	+ '                <table>'
	+ '                    <tr class="dropdownListItem" id="bond_single" title="Single Bond (1)">'
	+ '                        <td><div id="bond_single_preview"><img class="dropdownIcon" src="js-lib/ketcher/icons/png/bond/bond_single.dropdown.png" alt="" /></div>Single</td>'
	+ '                    </tr>'
	+ '                    <tr class="dropdownListItem" id="bond_double" title="Double Bond (2)">'
	+ '                        <td><div id="bond_double_preview"><img class="dropdownIcon" src="js-lib/ketcher/icons/png/bond/bond_double.dropdown.png" alt="" /></div>Double</td>'
	+ '                    </tr>'
	+ '                    <tr class="dropdownListItem" id="bond_triple" title="Triple Bond (3)">'
	+ '                        <td><div id="bond_triple_preview"><img class="dropdownIcon" src="js-lib/ketcher/icons/png/bond/bond_triple.dropdown.png" alt="" /></div>Triple</td>'
	+ '                    </tr>'
	+ '                    <tr>'
	+ '                        <td class="dropdownListDelimiter"></td>'
	+ '                    </tr>'
	+ '                    <tr class="dropdownListItem" id="bond_up" title="Single Up Bond (1)">'
	+ '                        <td><div id="bond_up_preview"><img class="dropdownIcon" src="js-lib/ketcher/icons/png/bond/bond_up.dropdown.png" alt="" /></div>Single Up</td>'
	+ '                    </tr>'
	+ '                    <tr class="dropdownListItem" id="bond_down" title="Single Down Bond (1)">'
	+ '                        <td><div id="bond_down_preview"><img class="dropdownIcon" src="js-lib/ketcher/icons/png/bond/bond_down.dropdown.png" alt="" /></div>Single Down</td>'
	+ '                    </tr>'
	+ '                    <tr class="dropdownListItem" id="bond_updown" title="Single Up/Down Bond (1)">'
	+ '                        <td><div id="bond_updown_preview"><img class="dropdownIcon" src="js-lib/ketcher/icons/png/bond/bond_updown.dropdown.png" alt="" /></div>Single Up/Down</td>'
	+ '                    </tr>'
	+ '                    <tr class="dropdownListItem" id="bond_crossed" title="Double Cis/Trans Bond (2)">'
	+ '                        <td><div id="bond_crossed_preview"><img class="dropdownIcon" src="js-lib/ketcher/icons/png/bond/bond_crossed.dropdown.png" alt="" /></div>Double Cis/Trans</td>'
	+ '                    </tr>'
	+ '                    <tr>'
	+ '                        <td class="dropdownListDelimiter"></td>'
	+ '                    </tr>'
	+ '                    <tr class="dropdownListItem" id="bond_any" title="Any Bond (0)">'
	+ '                        <td><div id="bond_any_preview"><img class="dropdownIcon" src="js-lib/ketcher/icons/png/bond/bond_any.dropdown.png" alt="" /></div>Any</td>'
	+ '                    </tr>'
	+ '                    <tr class="dropdownListItem" id="bond_aromatic" title="Aromatic Bond (4)">'
	+ '                        <td><div id="bond_aromatic_preview"><img class="dropdownIcon" src="js-lib/ketcher/icons/png/bond/bond_aromatic.dropdown.png" alt="" /></div>Aromatic</td>'
	+ '                    </tr>'
	+ '                    <tr class="dropdownListItem" id="bond_singledouble" title="Single/Double Bond">'
	+ '                        <td><div id="bond_singledouble_preview"><img class="dropdownIcon" src="js-lib/ketcher/icons/png/bond/bond_singledouble.dropdown.png" alt="" /></div>Single/Double</td>'
	+ '                    </tr>'
	+ '                    <tr class="dropdownListItem" id="bond_singlearomatic" title="Single/Aromatic Bond">'
	+ '                        <td><div id="bond_singlearomatic_preview"><img class="dropdownIcon" src="js-lib/ketcher/icons/png/bond/bond_singlearomatic.dropdown.png" alt="" /></div>Single/Aromatic</td>'
	+ '                    </tr>'
	+ '                    <tr class="dropdownListItem" id="bond_doublearomatic" title="Double/Aromatic Bond">'
	+ '                        <td><div id="bond_doublearomatic_preview"><img class="dropdownIcon" src="js-lib/ketcher/icons/png/bond/bond_doublearomatic.dropdown.png" alt="" /></div>Double/Aromatic</td>'
	+ '                    </tr>'
	+ '                </table>'
	+ '            </div>'
	+ ''
	+ '            <div class="dropdownList renderFirst" id="template_dropdown_list" style="display:none">'
	+ '                <table>'
	+ '                    <tr class="dropdownListItem" id="template_0" title="Benzene (T)">'
	+ '                        <td><div id="template_0_preview"><img class="dropdownIcon" src="js-lib/ketcher/icons/png/template/template0.dropdown.png" alt="" /></div>Benzene</td>'
	+ '                    </tr>'
	+ '                    <tr class="dropdownListItem" id="template_1" title="Cyclopentadiene (T)">'
	+ '                        <td><div id="template_1_preview"><img class="dropdownIcon" src="js-lib/ketcher/icons/png/template/template1.dropdown.png" alt="" /></div>Cyclopentadiene</td>'
	+ '                    </tr>'
	+ '                    <tr class="dropdownListItem" id="template_2" title="Cyclohexane (T)">'
	+ '                        <td><div id="template_2_preview"><img class="dropdownIcon" src="js-lib/ketcher/icons/png/template/template2.dropdown.png" alt="" /></div>Cyclohexane</td>'
	+ '                    </tr>'
	+ '                    <tr class="dropdownListItem" id="template_3" title="Cyclopentane (T)">'
	+ '                        <td><div id="template_3_preview"><img class="dropdownIcon" src="js-lib/ketcher/icons/png/template/template3.dropdown.png" alt="" /></div>Cyclopentane</td>'
	+ '                    </tr>'
	+ '                    <tr class="dropdownListItem" id="template_4" title="Cyclopropane (T)">'
	+ '                        <td><div id="template_4_preview"><img class="dropdownIcon" src="js-lib/ketcher/icons/png/template/template4.dropdown.png" alt="" /></div>Cyclopropane</td>'
	+ '                    </tr>'
	+ '                    <tr class="dropdownListItem" id="template_5" title="Cyclobutane (T)">'
	+ '                        <td><div id="template_5_preview"><img class="dropdownIcon" src="js-lib/ketcher/icons/png/template/template5.dropdown.png" alt="" /></div>Cyclobutane</td>'
	+ '                    </tr>'
	+ '                    <tr class="dropdownListItem" id="template_6" title="Cycloheptane (T)">'
	+ '                        <td><div id="template_6_preview"><img class="dropdownIcon" src="js-lib/ketcher/icons/png/template/template6.dropdown.png" alt="" /></div>Cycloheptane</td>'
	+ '                    </tr>'
	+ '                    <tr class="dropdownListItem" id="template_7" title="Cyclooctane (T)">'
	+ '                        <td><div id="template_7_preview"><img class="dropdownIcon" src="js-lib/ketcher/icons/png/template/template7.dropdown.png" alt="" /></div>Cyclooctane</td>'
	+ '                    </tr>'
	+ '                </table>'
	+ '            </div>'
	+ '            <div class="dropdownList renderFirst" id="customtemplate_dropdown_list" style="display:none">'
	+ '                <table>'
	+ '                    <tr class="dropdownListItem" id="customtemplate_0" title="Benzene (T)">'
	+ '                        <td><div id="customtemplate_0_preview"><img class="dropdownIcon" src="js-lib/ketcher/icons/png/customtemplate/customtemplate0.dropdown.png" alt="" /></div>Benzene</td>'
	+ '                    </tr>'
	+ '                </table>'
	+ '            </div>'
	+ ''
	+ '            <div class="dropdownList" id="reaction_dropdown_list" style="display:none">'
	+ '                <table>'
	+ '                    <tr class="dropdownListItem" id="reaction_arrow" title="Reaction Arrow Tool">'
	+ '                        <td><div id="reaction_arrow_todo"><img class="dropdownIcon" src="js-lib/ketcher/icons/png/reaction/reaction-arrow.dropdown.png" alt="" /></div>Reaction Arrow Tool</td>'
	+ '                    </tr>'
	+ '                    <tr class="dropdownListItem" id="reaction_plus" title="Reaction Plus Tool">'
	+ '                        <td><div id="reaction_plus_todo"><img class="dropdownIcon" src="js-lib/ketcher/icons/png/reaction/reaction-plus.dropdown.png" alt="" /></div>Reaction Plus Tool</td>'
	+ '                    </tr>'
	+ '                    <tr class="dropdownListItem serverRequired" id="reaction_automap" title="Reaction Auto-Mapping Tool">'
	+ '                        <td><div id="reaction_automap_todo"><img class="dropdownIcon" src="js-lib/ketcher/icons/png/reaction/reaction-automap.dropdown.png" alt="" /></div>Reaction Auto-Mapping</td>'
	+ '                    </tr>'
	+ '                    <tr class="dropdownListItem" id="reaction_map" title="Reaction Mapping Tool">'
	+ '                        <td><div id="reaction_map_todo"><img class="dropdownIcon" src="js-lib/ketcher/icons/png/reaction/reaction-map.dropdown.png" alt="" /></div>Reaction Mapping Tool</td>'
	+ '                    </tr>'
	+ '                    <tr class="dropdownListItem" id="reaction_unmap" title="Reaction Unmappping Tool">'
	+ '                        <td><div id="reaction_unmap_todo"><img class="dropdownIcon" src="js-lib/ketcher/icons/png/reaction/reaction-unmap.dropdown.png" alt="" /></div>Reaction Unmapping Tool</td>'
	+ '                    </tr>'
	+ '                </table>'
	+ '            </div>'
	+ ''
	+ '            <div class="dropdownList" id="rgroup_dropdown_list" style="display:none">'
	+ '                <table>'
	+ '                    <tr class="dropdownListItem" id="rgroup_label" title="R-Group Label Tool (Shift+R)">'
	+ '                        <td><div id="rgroup_label_todo"><img class="dropdownIcon" src="js-lib/ketcher/icons/png/rgroup/rgroup-label.dropdown.png" alt="" /></div>R-Group Label Tool</td>'
	+ '                    </tr>'
	+ '                    <tr class="dropdownListItem" id="rgroup_fragment" title="R-Group Fragment Tool (Shift+R)">'
	+ '                        <td><div id="rgroup_fragment_todo"><img class="dropdownIcon" src="js-lib/ketcher/icons/png/rgroup/rgroup-fragment.dropdown.png" alt="" /></div>R-Group Fragment Tool</td>'
	+ '                    </tr>'
	+ '                    <tr class="dropdownListItem" id="rgroup_attpoints" title="Attachment Point Tool (Shift+R)">'
	+ '                        <td><div id="rgroup_attpoints_todo"><img class="dropdownIcon" src="js-lib/ketcher/icons/png/rgroup/rgroup-attpoints.dropdown.png" alt="" /></div>Attachment Point Tool</td>'
	+ '                    </tr>'
	+ '                </table>'
	+ '            </div>'
	+ ''
	+ '            <div class="dropdownList" id="transform_dropdown_list" style="display:none">'
	+ '                <table>'
	+ '                    <tr class="dropdownListItem" id="transform_rotate" title="Rotate Tool">'
	+ '                        <td><div id="transform_rotate_todo"><img class="dropdownIcon" src="js-lib/ketcher/icons/png/transform/transform-rotate.dropdown.png" alt="" /></div>Rotate Tool</td>'
	+ '                    </tr>'
	+ '                    <tr class="dropdownListItem" id="transform_flip_h" title="Horizontal Flip">'
	+ '                        <td><div id="transform_flip_h_todo"><img class="dropdownIcon" src="js-lib/ketcher/icons/png/transform/transform-flip-h.dropdown.png" alt="" /></div>Horizontal Flip</td>'
	+ '                    </tr>'
	+ '                    <tr class="dropdownListItem" id="transform_flip_v" title="Vertical Flip">'
	+ '                        <td><div id="transform_flip_v_todo"><img class="dropdownIcon" src="js-lib/ketcher/icons/png/transform/transform-flip-v.dropdown.png" alt="" /></div>Vertical Flip</td>'
	+ '                    </tr>'
	+ '                </table>'
	+ '            </div>'
	+ ''
	+ '            <input id="input_label" type="text" maxlength="4" size="4" style="display:none;" />'
	+ ''
	+ '            <div id="window_cover" style="width:0;height:0;display:none"><div id="loading" style="display:none"></div></div>'
	+ ''
	+ '            <div class="dialogWindow fileDialog" id="open_file" style="display:none;">'
	+ '                <div style="width:100%">'
	+ '                    <div>'
	+ '                        Open File'
	+ '                    </div>'
	+ '                    <div style="height:0.5em"></div>'
	+ '                    <div class="serverRequired" style="font-size:small">'
	+ '                        <input type="radio" id="radio_open_from_input" name="input_source" checked>Input</input>'
	+ '                        <input type="radio" id="radio_open_from_file" name="input_source">File</input>'
	+ '                    </div>'
	+ '                    <div class="serverRequired" style="font-size:small" align="left">'
	+ '                        <input type="checkbox" id="checkbox_open_copy" name="open_mode">Load as a fragment and copy to the Clipboard</input>'
	+ '                    </div>'
	+ '                    <div class="serverRequired" id="open_from_file">'
	+ '                        <form id="upload_mol" style="margin-top:4px" action="open" enctype="multipart/form-data" target="buffer_frame" method="post">'
	+ '                            <input type="file" name="filedata" id="molfile_path" />'
	+ '                            <div style="margin-top:0.5em;text-align:center">'
	+ '                                <input id="upload_cancel" type="button" value="Cancel" />'
	+ '                                <input type="submit" value="OK" />'
	+ '                            </div>'
	+ '                        </form>'
	+ '                    </div>'
	+ '                    <div style="margin:4px;" id="open_from_input">'
	+ '                        <textarea class="chemicalText" id="input_mol" wrap="off"></textarea>'
	+ '                        <div style="margin-top:0.5em;text-align:center">'
	+ '                            <input id="read_cancel" type="button" value="Cancel" />'
	+ '                            <input id="read_ok" type="submit" value="OK" />'
	+ '                        </div>'
	+ '                    </div>'
	+ '                </div>'
	+ '            </div>'
	+ ''
	+ '            <div class="dialogWindow fileDialog" id="save_file" style="display:none;">'
	+ '                <div style="width:100%">'
	+ '                    <div>'
	+ '                        Save File'
	+ '                    </div>'
	+ '                    <div style="height:0.5em"></div>'
	+ '                    <div>'
	+ '                        <label>Format:</label>'
	+ '                        <select id="file_format">Format:'
	+ '                            <option value="mol">MDL/Symyx Molfile</option>'
	+ '                            <option value="smi">Daylight SMILES</option>'
	+ '                            <option id="file_format_inchi" value="inchi">InChI String</option>'
	+ '                            <!--option value="png">Portable Network Graphics PNG</option>'
	+ '                            <option value="svg">Scalable Vector Graphics SVG</option-->'
	+ '                        </select>'
	+ '                    </div>'
	+ '                    <div style="margin:4px;">'
	+ '                        <textarea class="chemicalText" id="output_mol" wrap="off" readonly></textarea>'
	+ '                        <form  id="download_mol" style="margin-top:0.5em;text-align:center" action="save" enctype="multipart/form-data" target="_self" method="post">'
	+ '                            <input type="hidden" id="mol_data" name="filedata" />'
	+ '                            <input type="submit" class="serverRequired" value="Save..." />'
	+ '                            <input id="save_ok" type="button" value="Close" />'
	+ '                        </form>'
	+ '                    </div>'
	+ '                </div>'
	+ '            </div>'
	+ ''
	+ '            <div class="dialogWindow propDialog" id="atom_properties" style="display:none;">'
	+ '                <div style="width:100%">'
	+ '                    <div>'
	+ '                        Atom Properties'
	+ '                    </div>'
	+ '                    <div style="height:0.5em"></div>'
	+ '                    <table style="text-align:left">'
	+ '                        <tr>'
	+ '                            <td>'
	+ '                                <label>Label:</label>'
	+ '                            </td>'
	+ '                            <td>'
	+ '                                <input id="atom_label" type="text" maxlength="2" size="3" />'
	+ '                            </td>'
	+ '                            <td rowspan="5" style="width:5px">'
	+ '                            </td>'
	+ '                            <td>'
	+ '                                <label>Number:</label>'
	+ '                            </td>'
	+ '                            <td>'
	+ '                                <input id="atom_number" type="text" readonly="readonly" maxlength="3" size="3" />'
	+ '                            </td>'
	+ '                            <td rowspan="5" style="width:10px">'
	+ '                            </td>'
	+ '                            <td colspan="2" style="background-color: #D7D7D7">'
	+ '                                Query specific'
	+ '                            </td>'
	+ '                        </tr>'
	+ '                        <tr>'
	+ '                            <td>'
	+ '                                <label>Charge:</label>'
	+ '                            </td>'
	+ '                            <td>'
	+ '                                <input id="atom_charge" type="text" maxlength="3" size="3" />'
	+ '                            </td>'
	+ '                            <td>'
	+ '                                <label>Valency:</label>'
	+ '                            </td>'
	+ '                            <td>'
	+ '                                <select id="atom_valence" style="width:100%">'
	+ '                                    <option value=""></option>'
	+ '                                    <option value="0">0</option>'
	+ '                                    <option value="1">I</option>'
	+ '                                    <option value="2">II</option>'
	+ '                                    <option value="3">III</option>'
	+ '                                    <option value="4">IV</option>'
	+ '                                    <option value="5">V</option>'
	+ '                                    <option value="6">VI</option>'
	+ '                                    <option value="7">VII</option>'
	+ '                                    <option value="8">VIII</option>'
	+ '                                </select>'
	+ '                            </td>'
	+ '                            <td>'
	+ '                                <label>Ring bond count:</label>'
	+ '                            </td>'
	+ '                            <td>'
	+ '                                <select id="atom_ringcount" style="width:100%">'
	+ '                                    <option value="0"></option>'
	+ '                                    <option value="-2">As drawn</option>'
	+ '                                    <option value="-1">0</option>'
	+ '                                    <option value="2">2</option>'
	+ '                                    <option value="3">3</option>'
	+ '                                    <option value="4">4</option>'
	+ '                                </select>'
	+ '                            </td>'
	+ '                        </tr>'
	+ '                        <tr>'
	+ '                            <td>'
	+ '                                <label>Isotope:</label>'
	+ '                            </td>'
	+ '                            <td>'
	+ '                                <input id="atom_isotope" type="text" maxlength="3" size="3" />'
	+ '                            </td>'
	+ '                            <td>'
	+ '                                <label>Radical:</label>'
	+ '                            </td>'
	+ '                            <td>'
	+ '                                <select id="atom_radical">'
	+ '                                    <option value="0"></option>'
	+ '                                    <option value="2">Monoradical</option>'
	+ '                                    <option value="1">Diradical (singlet)</option>'
	+ '                                    <option value="3">Diradical (triplet)</option>'
	+ '                                </select>'
	+ '                            </td>'
	+ '                            <td>'
	+ '                                <label>H count:</label>'
	+ '                            </td>'
	+ '                            <td>'
	+ '                                <select id="atom_hcount" style="width:100%">'
	+ '                                    <option value="0"></option>'
	+ '                                    <option value="1">0</option>'
	+ '                                    <option value="2">1</option>'
	+ '                                    <option value="3">2</option>'
	+ '                                    <option value="4">3</option>'
	+ '                                    <option value="5">4</option>'
	+ '                                </select>'
	+ '                            </td>'
	+ '                        </tr>'
	+ '                        <tr>'
	+ '                            <td colspan="5" style="background-color: #D7D7D7">'
	+ '                                Reaction flags'
	+ '                            </td>'
	+ '                            <td>'
	+ '                                <label>Substitution count:</label>'
	+ '                            </td>'
	+ '                            <td>'
	+ '                                <select id="atom_substitution" style="width:100%">'
	+ '                                    <option value="0"></option>'
	+ '                                    <option value="-2">As drawn</option>'
	+ '                                    <option value="-1">0</option>'
	+ '                                    <option value="1">1</option>'
	+ '                                    <option value="2">2</option>'
	+ '                                    <option value="3">3</option>'
	+ '                                    <option value="4">4</option>'
	+ '                                    <option value="5">5</option>'
	+ '                                    <option value="6">6</option>'
	+ '                                </select>'
	+ '                            </td>'
	+ '                        </tr>'
	+ '                        <tr>'
	+ '                            <td>'
	+ '                                <label>Inversion:</label>'
	+ '                            </td>'
	+ '                            <td>'
	+ '                                <select id="atom_inversion">'
	+ '                                    <option value="0"></option>'
	+ '                                    <option value="1">Inverts</option>'
	+ '                                    <option value="2">Retains</option>'
	+ '                                </select>'
	+ '                            </td>'
	+ '                            <td>'
	+ '                                <label>Exact:</label>'
	+ '                            </td>'
	+ '                            <td>'
	+ '                                <select id="atom_exactchange" style="width:100%">'
	+ '                                    <option value="0"></option>'
	+ '                                    <option value="1">Exact change</option>'
	+ '                                </select>'
	+ '                            </td>'
	+ '                            <td>'
	+ '                                <label>Unsaturation:</label>'
	+ '                            </td>'
	+ '                            <td>'
	+ '                                <select id="atom_unsaturation">'
	+ '                                    <option value="0"></option>'
	+ '                                    <option value="1">Unsaturated</option>'
	+ '                                </select>'
	+ '                            </td>'
	+ '                        </tr>'
	+ '                    </table>'
	+ '                    <div style="margin-top:0.5em">'
	+ '                        <input id="atom_prop_cancel" type="button" value="Cancel" />'
	+ '                        <input id="atom_prop_ok" type="button" value="OK" />'
	+ '                    </div>'
	+ '                </div>'
	+ '            </div>'
	+ ''
	+ '            <div class="dialogWindow propDialog" id="atom_attpoints" style="display:none;">'
	+ '                <div style="width:100%">'
	+ '                    <div>'
	+ '                        Attachment Points'
	+ '                    </div>'
	+ '                    <table style="text-align:left">'
	+ '                        <tr>'
	+ '                            <td>'
	+ '                                <input type="checkbox" id="atom_ap1">'
	+ '                            </td>'
	+ '                            <td>'
	+ '                                Primary attachment point'
	+ '                            </td>'
	+ '                        </tr>'
	+ '                        <tr>'
	+ '                            <td>'
	+ '                                <input type="checkbox" id="atom_ap2">'
	+ '                            </td>'
	+ '                            <td>'
	+ '                                Secondary attachment point'
	+ '                            </td>'
	+ '                        </tr>'
	+ '                    </table>'
	+ '                    <div style="height:0.5em"></div>'
	+ '                    <div style="margin-top:0.5em">'
	+ '                        <input id="atom_attpoints_cancel" type="button" value="Cancel" />'
	+ '                        <input id="atom_attpoints_ok" type="button" value="OK" />'
	+ '                    </div>'
	+ '                </div>'
	+ '            </div>'
	+ ''
	+ '            <div class="dialogWindow propDialog" id="bond_properties" style="display:none;">'
	+ '                <div style="width:100%">'
	+ '                    <div>'
	+ '                        Bond Properties'
	+ '                    </div>'
	+ '                    <div style="height:0.5em"></div>'
	+ '                    <table style="text-align:left">'
	+ '                        <tr>'
	+ '                            <td>'
	+ '                                <label>Type:</label>'
	+ '                            </td>'
	+ '                            <td>'
	+ '                                <select id="bond_type" style="width:100%">'
	+ '                                    <option value="single">Single</option>'
	+ '                                    <option value="up">Single Up</option>'
	+ '                                    <option value="down">Single Down</option>'
	+ '                                    <option value="updown">Single Up/Down</option>'
	+ '                                    <option value="double">Double</option>'
	+ '                                    <option value="crossed">Double Cis/Trans</option>'
	+ '                                    <option value="triple">Triple</option>'
	+ '                                    <option value="aromatic">Aromatic</option>'
	+ '                                    <option value="any">Any</option>'
	+ '                                    <option value="singledouble">Single/Double</option>'
	+ '                                    <option value="singlearomatic">Single/Aromatic</option>'
	+ '                                    <option value="doublearomatic">Double/Aromatic</option>'
	+ '                                </select>'
	+ '                            </td>'
	+ '                        </tr>'
	+ '                        <tr>'
	+ '                            <td>'
	+ '                                <label>Topology:</label>'
	+ '                            </td>'
	+ '                            <td>'
	+ '                                <select id="bond_topology" style="width:100%">'
	+ '                                    <option value="0">Either</option>'
	+ '                                    <option value="1">Ring</option>'
	+ '                                    <option value="2">Chain</option>'
	+ '                                </select>'
	+ '                            </td>'
	+ '                        </tr>'
	+ '                        <tr>'
	+ '                            <td>'
	+ '                                <label>Reacting Center:</label>'
	+ '                            </td>'
	+ '                            <td>'
	+ '                                <select id="bond_center">'
	+ '                                    <option value="0">Unmarked</option>'
	+ '                                    <option value="-1">Not center</option>'
	+ '                                    <option value="1">Center</option>'
	+ '                                    <option value="2">No change</option>'
	+ '                                    <option value="4">Made/broken</option>'
	+ '                                    <option value="8">Order changes</option>'
	+ '                                    <option value="12">Made/broken and changes</option>'
	+ '                                    <!--option value="5">Order changes</option>'
	+ '                                    <option value="9">Order changes</option>'
	+ '                                    <option value="13">Order changes</option-->'
	+ '                                </select>'
	+ '                            </td>'
	+ '                        </tr>'
	+ '                    </table>'
	+ '                    <div style="margin-top:0.5em">'
	+ '                        <input id="bond_prop_cancel" type="button" value="Cancel" />'
	+ '                        <input id="bond_prop_ok" type="button" value="OK" />'
	+ '                    </div>'
	+ '                </div>'
	+ '            </div>'
	+ ''
	+ '            <div class="dialogWindow sgroupDialog" id="sgroup_properties" style="display:none;">'
	+ '                <div style="width:100%">'
	+ '                    <div>'
	+ '                        S-Group Properties'
	+ '                    </div>'
	+ '                    <div style="height:0.5em"></div>'
	+ '                    <table style="text-align:left">'
	+ '                        <tr>'
	+ '                            <td>'
	+ '                                <label>Type:</label>'
	+ '                            </td>'
	+ '                            <td>'
	+ '                                <select id="sgroup_type">'
	+ '                                    <option value="GEN">Generic</option>'
	+ '                                    <option value="MUL">Multiple group</option>'
	+ '                                    <option value="SRU">SRU polymer</option>'
	+ '                                    <option value="SUP">Superatom</option>'
	+ '                                    <option value="DAT">Data</option>'
	+ '                                </select>'
	+ '                            </td>'
	+ '                        </tr>'
	+ '                        <tr class="generalSGroup">'
	+ '                            <td>'
	+ '                                <label>Connection:</label>'
	+ '                            </td>'
	+ '                            <td>'
	+ '                                <select id="sgroup_connection">'
	+ '                                    <option value="ht">Head-to-tail</option>'
	+ '                                    <option value="hh">Head-to-head</option>'
	+ '                                    <option value="eu">Either unknown</option>'
	+ '                                </select>'
	+ '                            </td>'
	+ '                        </tr>'
	+ '                        <tr class="generalSGroup">'
	+ '                            <td>'
	+ '                                <label>Label (subscript):</label>'
	+ '                            </td>'
	+ '                            <td>'
	+ '                                <input id="sgroup_label" type="text" maxlength="15" size="15" />'
	+ '                            </td>'
	+ '                        </tr>'
	+ '                        <tr class="dataSGroup">'
	+ '                            <td>'
	+ '                                <label>Field name:</label>'
	+ '                            </td>'
	+ '                            <td>'
	+ '                                <input id="sgroup_field_name" type="text" maxlength="30" size="30" />'
	+ '                            </td>'
	+ '                        </tr>'
	+ '                        <tr class="dataSGroup">'
	+ '                            <td>'
	+ '                                <label>Field value:</label>'
	+ '                            </td>'
	+ '                        </tr>'
	+ '                        <tr class="dataSGroup">'
	+ '                            <td colspan="2">'
	+ '                                <textarea class="dataSGroupValue" id="sgroup_field_value"></textarea>'
	+ '                            </td>'
	+ '                        </tr>'
	+ '                        <tr class="dataSGroup">'
	+ '                            <td colspan="2">'
	+ '                                <input type="radio" id="sgroup_pos_absolute" name="data_field_pos" checked>Absolute</input>'
	+ '                                <input type="radio" id="sgroup_pos_relative" name="data_field_pos">Relative</input>'
	+ '                                <input type="radio" id="sgroup_pos_attached" name="data_field_pos">Attached</input>'
	+ '                            </td>'
	+ '                        </tr>'
	+ '                    </table>'
	+ '                    <div style="margin-top:0.5em">'
	+ '                        <input id="sgroup_prop_cancel" type="button" value="Cancel" />'
	+ '                        <input id="sgroup_prop_ok" type="button" value="OK" />'
	+ '                    </div>'
	+ '                </div>'
	+ '            </div>'
	+ ''
	+ '            <div class="dialogWindow automapDialog" id="automap_properties" style="display:none;">'
	+ '                <div style="width:100%">'
	+ '                    <div>'
	+ '                        Reaction Auto-Mapping Parameter'
	+ '                    </div>'
	+ '                    <div style="height:0.5em"></div>'
	+ '                    <table style="text-align:left">'
	+ '                        <tr>'
	+ '                            <td>'
	+ '                                <label>Mode:</label>'
	+ '                            </td>'
	+ '                            <td>'
	+ '                                <select id="automap_mode">'
	+ '                                    <option value="discard">Discard</option>'
	+ '                                    <option value="keep">Keep</option>'
	+ '                                    <option value="alter">Alter</option>'
	+ '                                    <option value="clear">Clear</option>'
	+ '                                </select>'
	+ '                            </td>'
	+ '                        </tr>'
	+ '                    </table>'
	+ '                    <div style="margin-top:0.5em">'
	+ '                        <input id="automap_cancel" type="button" value="Cancel" />'
	+ '                        <input id="automap_ok" type="button" value="OK" />'
	+ '                    </div>'
	+ '                </div>'
	+ '            </div>'
	+ ''
	+ '            <div class="dialogWindow elemTableDialog" id="elem_table" style="display:none;">'
	+ '                <div>'
	+ '                    <div>'
	+ '                        Periodic table'
	+ '                    </div>'
	+ '                    <div style="height:0.5em"></div>'
	+ '                    <div id="elem_table_area"></div>'
	+ '                    <div align="left">'
	+ '                        <input type="radio" id="elem_table_single" name="atom_list"'
	+ '                               onchange="if (!Prototype.Browser.IE) ui.elem_table_obj.setMode(\'single\')"'
	+ '                               onclick="if (Prototype.Browser.IE) ui.elem_table_obj.setMode(\'single\')"'
	+ '                               >Single</input> <br />'
	+ '                        <input type="radio" id="elem_table_list" name="atom_list"'
	+ '                               onchange="if (!Prototype.Browser.IE) ui.elem_table_obj.setMode(\'list\')"'
	+ '                               onclick="if (Prototype.Browser.IE) ui.elem_table_obj.setMode(\'list\')"'
	+ '                               >List</input> <br />'
	+ '                        <input type="radio" id="elem_table_notlist" name="atom_list"'
	+ '                               onchange="if (!Prototype.Browser.IE) ui.elem_table_obj.setMode(\'notlist\')"'
	+ '                               onclick="if (Prototype.Browser.IE) ui.elem_table_obj.setMode(\'notlist\')"'
	+ '                               >Not List</input>'
	+ '                    </div>'
	+ '                    <div style="margin-top:0.5em">'
	+ '                        <input id="elem_table_cancel" type="button" value="Cancel" />'
	+ '                        <input id="elem_table_ok" type="button" value="OK" />'
	+ '                    </div>'
	+ '                </div>'
	+ '            </div>'
	+ ''
	+ '            <div class="dialogWindow rgroupTableDialog" id="rgroup_table" style="display:none;">'
	+ '                <div>'
	+ '                    <div>'
	+ '                        R-Group'
	+ '                    </div>'
	+ '                    <div style="height:0.5em"></div>'
	+ '                    <div id="rgroup_table_area"></div>'
	+ '                    <div style="margin-top:0.5em">'
	+ '                        <input id="rgroup_table_cancel" type="button" value="Cancel" />'
	+ '                        <input id="rgroup_table_ok" type="button" value="OK" />'
	+ '                    </div>'
	+ '                </div>'
	+ '            </div>'
	+ ''
	+ '            <div class="dialogWindow rlogicTableDialog" id="rlogic_table" style="display:none;">'
	+ '                <div style="width:100%">'
	+ '                    <div>'
	+ '                        R-Group Logic'
	+ '                    </div>'
	+ '                    <div style="height:0.5em"></div>'
	+ '                    <table style="text-align:left">'
	+ '                        <tr>'
	+ '                            <td>'
	+ '                                <label for="rlogic_occurrence">Occurrence:</label>'
	+ '                                <input id="rlogic_occurrence" type="text" maxlength="50" size="10" />'
	+ '                            </td>'
	+ '                            <td>'
	+ '                                <label for="rlogic_resth">RestH:</label>'
	+ '                                <select id="rlogic_resth">'
	+ '                                    <option value="0">Off</option>'
	+ '                                    <option value="1">On</option>'
	+ '                                </select>'
	+ '                            </td>'
	+ '                            <td>'
	+ '                                <label for="rlogic_if">Condition:</label>'
	+ '                                <select id="rlogic_if">'
	+ '                                    <option value="0">Always</option>'
	+ '                                </select>'
	+ '                            </td>'
	+ '                        </tr>'
	+ '                    </table>'
	+ '                    <div style="margin-top:0.5em">'
	+ '                        <input id="rlogic_cancel" type="button" value="Cancel" />'
	+ '                        <input id="rlogic_ok" type="button" value="OK" />'
	+ '                    </div>'
	+ '                </div>'
	+ '            </div>'
	+ ''
	+ '            <div class="dialogWindow reagenericsTableDialog" id="reagenerics_table" style="display:none;">'
	+ '                <div>'
	+ '                    <div>'
	+ '                        Reaxys Generics'
	+ '                    </div>'
	+ '                    <div style="height:0.5em"></div>'
	+ '                    <div id="reagenerics_table_area"></div>'
	+ '                    <div style="margin-top:0.5em">'
	+ '                        <input id="reagenerics_table_cancel" type="button" value="Cancel" />'
	+ '                        <input id="reagenerics_table_ok" type="button" value="OK" disabled />'
	+ '                    </div>'
	+ '                </div>'
	+ '            </div>'
	+ ''
	+ '            <div class="aboutDialog" id="about_dialog" style="display:none;">'
	+ '                <div>'
	+ '                    <div>'
	+ '                        About'
	+ '                    </div>'
	+ '                    <div style="height:0.5em"></div>'
	+ '                    <div>'
	+ '                        <a href="http://ggasoftware.com/opensource/ketcher#overview" target="_blank"><img src="js-lib/ketcher/icons/logo_small01.jpg"></img></a>'
	+ '                    </div>'
	+ '                    <table style="width:100%" cellspacing="5">'
	+ '                        <tr>'
	+ '                            <td style="float:left"><a href="http://ggasoftware.com/opensource/ketcher#overview" target="_blank" style="color:#0000CC">Ketcher</a></td>'
	+ '                            <td style="float:right" id="ketcher_version">Version 1.0</td>'
	+ '                        </tr>'
	+ '                        <tr>'
	+ '                            <td style="float:left"><a href="http://ggasoftware.com" target="_blank" style="color:#0000CC">GGA Software Services</a></td>'
	+ '                            <td style="float:right"><a href="http://ggasoftware.com/opensource/ketcher#feedback" target="_blank" style="color:#0000CC">Feedback</a></td>'
	+ '                        </tr>'
	+ '                    </table>'
	+ '                    <div style="margin:4px;">'
	+ '                        <button onclick="ui.hideDialog(\'about_dialog\')">Close</button>'
	+ '                    </div>'
	+ '                </div>'
	+ '            </div>'
	+ ''
	+ '            <iframe name="buffer_frame" id="buffer_frame" src="about:blank" style="display:none">'
	+ '            </iframe>'
	+ '        </div>';
	
	var inWebportal = false;
	var customSketcher = false;
	var callCount = 0;
	
	moleculeInput.init = function(representation) {
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
		var qfdiv = jQuery('<div class="quickformcontainer" data-iframe-height data-iframe-width>');
		body.append(qfdiv);
		var width = Math.max(MIN_WIDTH, representation.width);
		var height = Math.max(MIN_HEIGHT, representation.height);
		var isPageBuilderPresent = knimeService && knimeService.pageBuilderPresent;
    	
		if (inWebportal) {
			jQuery('script').each(function() {
				var s = jQuery(this);
				var src = s.attr("src");
				if (src && src.indexOf("js-lib/ketcher/") != -1) {
					s.remove();
				}
			});
			sketcherFrame = jQuery('<iframe class="knime-sketcher-frame">');
			sketcherFrame.width((width + 20) + "px");
			sketcherFrame.height((height + 20) + "px");
			qfdiv.width((width + 20) + "px");
			sketcherFrame.attr("frameborder", "0");
	    	sketcherFrame.css("border", "none");
	    	sketcherFrame.css("margin", "0");
	    	sketcherFrame.css("background", "none");
			var loc = "./VAADIN/src-js/js-lib/ketcher/ketcher.html";

			sketcherFrame.attr("name", currentMolecule);
			if (customSketcher) {
				loc = sketcherPath;
			}
			if (isPageBuilderPresent) {
			    var customSketcherPath = parent.KnimePageBuilderAPI.getCustomSketcherPath();
			    loc = knimeService.resourceBaseUrl + '/js-lib/ketcher/ketcher.html';
			    if (customSketcherPath && customSketcherPath.startsWith('/')) {
			        customSketcher = true;
			        loc = customSketcherPath;
			    }
			}
			sketcherFrame.attr("src", loc);
			sketcherFrame.load(function() {
				setTimeout(function() {
					if (customSketcher) {
						sketchTranslator = sketcherFrame.get(0).contentWindow.SketchTranslator;
						if (sketchTranslator) {
							sketchTranslator.init(currentMolecule, null, moleculeInput.update, format);
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
					'dfs' : {
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
						deps: ['prototype', 'raphael', 'base64', 'keymaster', 'common',
								'vec2', 'set', 'map', 'pool', 'element', 'struct', 'molfile',
								'sgroup', 'struct_valence', 'dfs', 'cis_trans', 'stereocenters',
								'smiles', 'inchi', 'visel', 'restruct', 'restruct_rendering',
								'render', 'templates', 'editor', 'elem_table', 'rgroup_table',
								'ui', 'actions', 'reaxys'
						],
						exports: 'ketcher'
					}
				}
			});
			require(['ketcher'], function() {
				var sketcherDiv = jQuery('<div class="knime-sketcher-div">');
				sketcherDiv.width(width + "px");
				sketcherDiv.height(height + "px");
				sketcherDiv.css("position", "relative");
				qfdiv.append(sketcherDiv);
				qfdiv.width(width + "px");
				eval(localInitCode);
				sketcherDiv.html(localKetcherContent);
				ketcher.init();
				ketcher.setMolecule(currentMolecule);
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
	
	moleculeInput.update = function() {
		// Do we need to remember the value right away? Probably not.
	}
	
	moleculeInput.validate = function() {
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
	
	moleculeInput.setValidationErrorMessage = function(message) {
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

	moleculeInput.value = function() {
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
            	if (format.toLowerCase() === "rxn" || format.toLowerCase() === "sdf" || format.toLowerCase() === "mol")
            		molecule = k.getMolfile();
            	else	
            		molecule = k.getSmiles();
            }
            else {
            	// should not happen after succesful validate
            	molecule = null;
            }
		}
		var viewValue = new Object();
		viewValue.moleculeString = molecule;
		return viewValue;
	};
	
	moleculeInput.getSVG = function() {
		// specific ketcher svg selection
		var svg = jQuery("#client_area svg");
		if (svg.length == 0) {
			// generic svg selection
			svg = jQuery("svg");
		}
		if (svg.length > 0) {
			knimeService.inlineSvgStyles(svg.get(0));
			return (new XMLSerializer()).serializeToString(svg.get(0));
		}
	};
	
	return moleculeInput;
	
}();
