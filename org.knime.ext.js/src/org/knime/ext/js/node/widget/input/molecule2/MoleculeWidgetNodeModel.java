/*
 * ------------------------------------------------------------------------
 *
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
 * ---------------------------------------------------------------------
 */
package org.knime.ext.js.node.widget.input.molecule2;

import java.util.Arrays;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.wizard.WizardViewCreator;
import org.knime.ext.js.molecule.MoleculeSketcherPreferenceUtil;
import org.knime.js.base.node.widget.WidgetFlowVariableNodeModel;
import org.knime.js.core.JavaScriptViewCreator;
import org.knime.js.core.layout.LayoutTemplateProvider;
import org.knime.js.core.layout.bs.JSONLayoutViewContent;
import org.knime.js.core.layout.bs.JSONLayoutViewContent.ResizeMethod;

/**
 * Node model of the Molecule Widget node.
 *
 * @author Marc Bux, KNIME GmbH, Berlin, Germany
 */
final class MoleculeWidgetNodeModel
    extends WidgetFlowVariableNodeModel<MoleculeWidgetRepresentation, MoleculeWidgetValue, MoleculeWidgetConfig>
    implements LayoutTemplateProvider {

    MoleculeWidgetNodeModel(final String viewName) {
        super(new PortType[0], new PortType[]{FlowVariablePortObject.TYPE}, viewName);
    }

    @Override
    protected void createAndPushFlowVariable() throws InvalidSettingsException {
        final var molecule = getRelevantValue().getMoleculeString();
        pushFlowVariableString(getConfig().getFlowVariableName(), molecule == null ? "" : molecule);
        pushFlowVariableString("molecule_format", getViewRepresentation().getFormat());
    }

    @Override
    public WizardViewCreator<MoleculeWidgetRepresentation, MoleculeWidgetValue> getViewCreator() {
        // We create a new JavaScriptViewCreator here, since the JavascriptObjectID can change if the sketcher is
        // changed via the preference page.
        return new JavaScriptViewCreator<>(getJavascriptObjectID());
    }

    @Override
    public String getJavascriptObjectID() {
        return MoleculeSketcherPreferenceUtil.getInstance().getSelectedSketcher().getJavascriptObjectID();
    }

    @Override
    public MoleculeWidgetConfig createEmptyConfig() {
        return new MoleculeWidgetConfig();
    }

    @Override
    public MoleculeWidgetValue createEmptyViewValue() {
        return new MoleculeWidgetValue();
    }

    @Override
    protected MoleculeWidgetRepresentation getRepresentation() {
        final var config = getConfig();
        final var selectedSketcher = MoleculeSketcherPreferenceUtil.getInstance().getSelectedSketcher();
        final var supportedFormats = selectedSketcher.getSupportedFormats();
        final var selectedFormat = config.getFormat();
        if (Arrays.stream(supportedFormats).noneMatch(selectedFormat::equals)) {
            throw new IllegalStateException(
                String.format("Selected format \"%s\" is not supported by molecule sketcher \"%s\".", selectedFormat,
                    selectedSketcher.getName()));
        }
        return new MoleculeWidgetRepresentation(getRelevantValue(), config.getDefaultValue(), config,
            config.getLabelConfig(), MoleculeSketcherPreferenceUtil.getInstance().getServerURL());
    }

    @Override
    protected void useCurrentValueAsDefault() {
        getConfig().getDefaultValue().setMoleculeString(getViewValue().getMoleculeString());
    }

    @Override
    public JSONLayoutViewContent getLayoutTemplate() {
        final var view = new JSONLayoutViewContent();
        view.setResizeMethod(ResizeMethod.ASPECT_RATIO_4by3);
        return view;
    }

}
