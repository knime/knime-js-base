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
 *
 * History
 *   1 Jun 2019 (albrecht): created
 */
package org.knime.js.base.node.configuration.selection.multiple;

import org.knime.core.node.InvalidSettingsException;
import org.knime.js.base.dialog.selection.multiple.MultipleSelectionsComponent;
import org.knime.js.base.dialog.selection.multiple.MultipleSelectionsComponentFactory;
import org.knime.js.base.node.configuration.AbstractDialogNodeConfigurationPanel;

/**
 * The component dialog panel for the multiple selection configuration node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("serial")
public class MultipleSelectionConfigurationPanel
    extends AbstractDialogNodeConfigurationPanel<MultipleSelectionDialogNodeValue> {

    private MultipleSelectionsComponent m_selectionComponent;

    /**
     * @param representation the dialog node settings
     */
    public MultipleSelectionConfigurationPanel(final MultipleSelectionDialogNodeRepresentation representation) {
        super(representation.getLabel(), representation.getDescription(), representation.getDefaultValue());
        String[] choices = representation.getPossibleChoices();
        m_selectionComponent =
                MultipleSelectionsComponentFactory.createMultipleSelectionsComponent(representation.getType());
        m_selectionComponent.setChoices(choices);
        m_selectionComponent.setSelections(representation.getDefaultValue().getVariableValue());
        setComponent(m_selectionComponent.getComponent());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void resetToDefault() {
        m_selectionComponent.setSelections(getDefaultValue().getVariableValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected MultipleSelectionDialogNodeValue createNodeValue() throws InvalidSettingsException {
        MultipleSelectionDialogNodeValue value = new MultipleSelectionDialogNodeValue();
        value.setVariableValue(m_selectionComponent.getSelections());
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadNodeValue(final MultipleSelectionDialogNodeValue value) {
        super.loadNodeValue(value);
        if (value != null) {
            m_selectionComponent.setSelections(value.getVariableValue());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        m_selectionComponent.setEnabled(enabled);
    }

}
