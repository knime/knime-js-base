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
 * History
 *   Jun 22, 2011 (wiswedel): created
 */
package org.knime.quickform.nodes.in.selection.multiple;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.quickform.QuickFormConfigurationPanel;
import org.knime.core.util.node.quickform.AbstractQuickFormElement;
import org.knime.core.util.node.quickform.in.AbstractQuickFormInElement;
import org.knime.core.util.node.quickform.in.MultipleSelectionInputQuickFormInElement;
import org.knime.core.util.node.quickform.in.MultipleSelectionInputQuickFormInElement.Layout;

/**
 * Panel shown in meta node dialogs, displaying a set of radio buttons.
 * @author Thomas Gabriel, KNIME AG, Zurich, Switzerland
 */
@SuppressWarnings("serial")
public class MultipleSelectionInputQuickFormPanel
        extends QuickFormConfigurationPanel<MultipleSelectionInputQuickFormValueInConfiguration> {

    private MultipleSelectionComponent m_component;

    private Layout m_layout;

    /**
     * Create a new String Radio QF node panel shown in the QF wizard and meta node configuration dialag.
     * @param config to load this element
     */
    public MultipleSelectionInputQuickFormPanel(final MultipleSelectionInputQuickFormInConfiguration config) {
        super(new BorderLayout());
        String labelString = config.getLabel();
        JLabel label = new JLabel(labelString);
        label.setToolTipText(config.getDescription());
        add(label, BorderLayout.NORTH);
        MultipleSelectionInputQuickFormValueInConfiguration vconfig = config.getValueConfiguration();
        m_layout = vconfig.getLayout();
        String[] choices = config.getChoices().split(",");
        if (choices != null) {
            if (m_layout == Layout.CHECKBOX_VERTICAL) {
                m_component = new MultipleSelectionComponentCheckBoxButton(choices, true);
                add(m_component, BorderLayout.CENTER);
            } else if (m_layout == Layout.CHECKBOX_HORIZONTAL) {
                    m_component = new MultipleSelectionComponentCheckBoxButton(choices, false);
                    add(m_component, BorderLayout.CENTER);
            } else {
                throw new IllegalArgumentException("QuickForm layout component not supported: " + m_layout);
            }
        } else {
            add(new JLabel("No choices defined in quickform node"), BorderLayout.NORTH);
        }
        loadValueConfig(vconfig);
    }

    /** {@inheritDoc} */
    @Override
    public void saveSettings(
            final MultipleSelectionInputQuickFormValueInConfiguration config)
            throws InvalidSettingsException {
        MultipleSelectionInputQuickFormValueInConfiguration v = config;
        v.setValues(m_component.getSelectedValues());
        v.setLayout(m_layout);
    }

    /** {@inheritDoc} */
    @Override
    public void loadSettings(
            final MultipleSelectionInputQuickFormValueInConfiguration config) {
        loadValueConfig(config);
    }

    private void loadValueConfig(final MultipleSelectionInputQuickFormValueInConfiguration config) {
        String values = config.getValues();
        boolean found = m_component.setSelectedValues(values);
        if (!found) {
            NodeLogger.getLogger(getClass()).warn("Did not find matching value "
                    + "for \"" + values + "\" in choice configuration");
        }
    }

    @Override
    public void updateQuickFormInElement(final AbstractQuickFormInElement e) throws InvalidSettingsException {
        MultipleSelectionInputQuickFormInElement cast =
            AbstractQuickFormElement.cast(MultipleSelectionInputQuickFormInElement.class, e);
        cast.setValues(m_component.getSelectedValues());
    }

}

/** Multiple selection component interface. */
@SuppressWarnings("serial")
abstract class MultipleSelectionComponent extends JPanel {
    /** Multiple selection component. */
    public MultipleSelectionComponent() {
        super(new GridLayout(1, 1));
    }
    /** @return the selected values as String */
    abstract String getSelectedValues();
    /** Set a new selection.
     * @param values the new values to be selected
     * @return true, if the selection was successful
     */
    abstract boolean setSelectedValues(final String values);
}

/** Multiple selection component for CheckBox'es. */
@SuppressWarnings("serial")
class MultipleSelectionComponentCheckBoxButton extends MultipleSelectionComponent {
    private final List<JCheckBox> m_buttons;
    /**
     * Creates a new CheckBox component.
     * @param choices array possible choices.
     * @param vertical true if vertical aligned, otherwise false for horizontal
     */
    public MultipleSelectionComponentCheckBoxButton(final String[] choices, final boolean vertical) {
        m_buttons = new ArrayList<JCheckBox>();
        final JPanel buttonPanel;
        if (vertical) {
            buttonPanel = new JPanel(new GridLayout(choices.length, 1));
        } else {
            buttonPanel = new JPanel(new GridLayout(1, choices.length));
        }
        for (String s : choices) {
            JCheckBox button = new JCheckBox(s.trim());
            buttonPanel.add(button);
            m_buttons.add(button);
        }
        if (!m_buttons.isEmpty()) {
            m_buttons.get(0).doClick();
        }
        super.add(buttonPanel, BorderLayout.CENTER);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getSelectedValues() {
        String selected = "";
        for (JCheckBox b : m_buttons) {
            if (b.isSelected()) {
                if (!selected.isEmpty()) {
                    selected += ",";
                }
                selected += b.getText();
            }
        }
        return selected;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setSelectedValues(final String values) {
        boolean click = false;
        List<String> valueArray = new ArrayList<String>();
        for (String s : values.split(",")) {
            valueArray.add(s.trim());
        }
        for (final JCheckBox b : m_buttons) {
            if (valueArray.contains(b.getText())) {
                b.setSelected(true);
                click = true;
            } else {
                b.setSelected(false);
            }
        }
        return click;
    }
}


