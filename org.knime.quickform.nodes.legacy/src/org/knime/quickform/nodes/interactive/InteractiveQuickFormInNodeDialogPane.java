/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME GmbH, Konstanz, Germany
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
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
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
 */
package org.knime.quickform.nodes.interactive;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.quickform.AbstractQuickFormConfiguration;

/**
 * Dialog to node.
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * @since 2.7
 */
final class InteractiveQuickFormInNodeDialogPane
    <CFG extends AbstractQuickFormConfiguration<?>> extends NodeDialogPane {

    /** Default width (#columns) of text field elements. */
    public static final int DEF_TEXTFIELD_WIDTH = 20;

    private final JTextField m_labelField;
    private final JTextArea m_descriptionArea;
    private final JSpinner m_weightSpinner;

    /**
     *
     */
    public InteractiveQuickFormInNodeDialogPane() {
        m_labelField = new JTextField(DEF_TEXTFIELD_WIDTH);
        m_descriptionArea = new JTextArea(1, DEF_TEXTFIELD_WIDTH);
        m_descriptionArea.setLineWrap(true);
        m_descriptionArea.setPreferredSize(new Dimension(100, 50));
        m_descriptionArea.setMinimumSize(new Dimension(100, 30));
        m_weightSpinner = new JSpinner(new SpinnerNumberModel(1, -100, 100, 1));
    }

    /** To be called from subclasses as last line in their constructor. It
     * initializes the panel, call the
     * {@link #fillPanel(JPanel, GridBagConstraints)} method and adds the tab
     * to the dialog. */
    protected final void createAndAddTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.NORTHWEST;

        gbc.fill = GridBagConstraints.HORIZONTAL;
        addPairToPanel("Label: ", m_labelField, panel, gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        JScrollPane sp = new JScrollPane(m_descriptionArea);
        sp.setPreferredSize(m_descriptionArea.getPreferredSize());
        sp.setMinimumSize(m_descriptionArea.getMinimumSize());
        addPairToPanel("Description: ", sp, panel, gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0;
        addPairToPanel("Weight: ", m_weightSpinner, panel, gbc);

        //fillPanel(panel, gbc);

        addTab("Control", panel);
    }

    /*private void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
        addPairToPanel("Boolean Value: ", m_valueField, panelWithGBLayout, gbc);
    }*/

    /** Adds a panel sub-component to the dialog.
     * @param label The label (left hand column)
     * @param c The component (right hand column)
     * @param panelWithGBLayout Panel to add
     * @param gbc constraints.
     */
    protected final void addPairToPanel(final String label, final JComponent c,
            final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
        int fill = gbc.fill;

        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panelWithGBLayout.add(new JLabel(label), gbc);

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = fill;
        gbc.weightx = 1;
        panelWithGBLayout.add(c, gbc);
        gbc.weightx = 0;
    }

    /** {@inheritDoc} */
    /*@Override
    protected void loadSettingsFrom(final NodeSettingsRO settings,
            final PortObjectSpec[] specs) throws NotConfigurableException {
        CFG c = createConfiguration();
        c.loadSettingsInDialog(settings);
        m_labelField.setText(c.getLabel());
        m_descriptionArea.setText(c.getDescription());
        m_variableNameField.setText(c.getVariableName());
        m_weightSpinner.setValue(c.getWeight());
        m_hideInWizard.setSelected(c.isHideInWizard());
        loadAdditionalSettings(c);
    }*/


    /**
     * @return
     */
    /*private CFG createConfiguration() {
        return new InteractiveQuickFormValueInConfiguration();
    }*/

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        // TODO Auto-generated method stub

    }


}
