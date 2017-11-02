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
package org.knime.quickform.nodes.in.selection.single;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.quickform.QuickFormConfigurationPanel;
import org.knime.core.util.node.quickform.AbstractQuickFormElement;
import org.knime.core.util.node.quickform.in.AbstractQuickFormInElement;
import org.knime.core.util.node.quickform.in.SingleSelectionInputQuickFormInElement;
import org.knime.core.util.node.quickform.in.SingleSelectionInputQuickFormInElement.Layout;

/**
 * Panel shown in meta node dialogs, displaying a set of radio buttons.
 * @author Thomas Gabriel, KNIME AG, Zurich, Switzerland
 */
@SuppressWarnings("serial")
public class SingleSelectionInputQuickFormPanel
        extends QuickFormConfigurationPanel<SingleSelectionInputQuickFormValueInConfiguration> {

    private SingleSelectionComponent m_component;

    private Layout m_layout;

    /**
     * Create a new String Radio QF node panel shown in the QF wizard and meta node configuration dialag.
     * @param config to load this element
     */
    public SingleSelectionInputQuickFormPanel(final SingleSelectionInputQuickFormInConfiguration config) {
        super(new BorderLayout());
        String labelString = config.getLabel();
        JLabel label = new JLabel(labelString);
        label.setToolTipText(config.getDescription());
        add(label, BorderLayout.NORTH);
        SingleSelectionInputQuickFormValueInConfiguration vconfig = config.getValueConfiguration();
        m_layout = vconfig.getLayout();
        String[] choices = config.getChoices().split(",");
        if (choices != null) {
            if (m_layout == Layout.RADIO_VERTICAL) {
                m_component = new SingleSelectionComponentRadioButton(choices, true);
                add(m_component, BorderLayout.CENTER);
            } else if (m_layout == Layout.RADIO_HORIZONTAL) {
                m_component = new SingleSelectionComponentRadioButton(choices, false);
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
    public void saveSettings(final SingleSelectionInputQuickFormValueInConfiguration config)
            throws InvalidSettingsException {
        SingleSelectionInputQuickFormValueInConfiguration v = config;
        v.setValue(m_component.getSelectedValue());
        v.setLayout(m_layout);
    }

    /** {@inheritDoc} */
    @Override
    public void loadSettings(final SingleSelectionInputQuickFormValueInConfiguration config) {
        loadValueConfig(config);
    }

    private void loadValueConfig(final SingleSelectionInputQuickFormValueInConfiguration config) {
        String value = config.getValue();
        boolean found = m_component.setSelectedValue(value);
        if (!found) {
            NodeLogger.getLogger(getClass()).warn("Did not find matching value "
                    + "for \"" + value + "\" in choice configuration");
        }
    }

    @Override
    public void updateQuickFormInElement(final AbstractQuickFormInElement e) throws InvalidSettingsException {
        SingleSelectionInputQuickFormInElement cast =
            AbstractQuickFormElement.cast(SingleSelectionInputQuickFormInElement.class, e);
        cast.setValue(m_component.getSelectedValue());
    }

}

/** Single selection component interface. */
@SuppressWarnings("serial")
abstract class SingleSelectionComponent extends JPanel {
    /** Single selection component. */
    public SingleSelectionComponent() {
        super(new GridLayout(1, 1));
    }
    /** @return the selected value. */
    abstract String getSelectedValue();
    /** Set a new value being selected.
     * @param value the new value to be selected.
     * @return true, if value is selected, otherwise false
     */
    abstract boolean setSelectedValue(final String value);
}

/** Single selection component RadioButton. */
@SuppressWarnings("serial")
class SingleSelectionComponentRadioButton extends SingleSelectionComponent {
    private final List<JRadioButton> m_buttons;
    /** Create a new RadioButton selection component.
     * @param choices possible for RadioButton's
     * @param vertical true, if vertical layout, otherwise false for horizontal
     */
    public SingleSelectionComponentRadioButton(final String[] choices, final boolean vertical) {
        ButtonGroup bg = new ButtonGroup();
        m_buttons = new ArrayList<JRadioButton>();
        final JPanel buttonPanel;
        if (vertical) {
            buttonPanel = new JPanel(new GridLayout(choices.length, 1));
        } else {
            buttonPanel = new JPanel(new GridLayout(1, choices.length));
        }
        for (String s : choices) {
            JRadioButton button = new JRadioButton(s.trim());
            bg.add(button);
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
    public String getSelectedValue() {
        String selected = null;
        for (JRadioButton b : m_buttons) {
            if (b.isSelected()) {
                selected = b.getText();
                break;
            }
        }
        return selected;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setSelectedValue(final String value) {
        for (JRadioButton b : m_buttons) {
            if (b.getText().equals(value.trim())) {
                b.doClick();
                return true;
            }
        }
        return false;
    }
}


