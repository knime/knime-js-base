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
 *   Jun 22, 2011 (wiswedel): created
 */
package org.knime.quickform.nodes.in.stringselection;

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
import org.knime.core.util.node.quickform.in.StringSelectionInputQuickFormInElement;

/**
 * Panel shown in meta node dialogs, displaying a set of radio buttons.
 * @author Bernd Wiswedel, KNIME AG, Zurich, Switzerland
 */
public class StringSelectionInputQuickFormPanel
        extends QuickFormConfigurationPanel<StringSelectionInputQuickFormValueInConfiguration> {

    private final List<JRadioButton> m_radioButtons;

    /**
     * Create a new String Radio QF node panel shown in the QF wizard and meta node configuration dialag.
     * @param config to load this element
     */
    public StringSelectionInputQuickFormPanel(final StringSelectionInputQuickFormInConfiguration config) {
        super(new BorderLayout());
        String labelString = config.getLabel();
        JLabel label = new JLabel(labelString);
        label.setToolTipText(config.getDescription());
        add(label, BorderLayout.NORTH);
        String[] choices = config.getChoices();
        ButtonGroup bg = new ButtonGroup();
        m_radioButtons = new ArrayList<JRadioButton>();
        if (choices != null) {
            JPanel buttonPanel = new JPanel(new GridLayout(choices.length, 1));
            for (String s : choices) {
                JRadioButton button = new JRadioButton(s);
                bg.add(button);
                buttonPanel.add(button);
                m_radioButtons.add(button);
            }
            if (!m_radioButtons.isEmpty()) {
                m_radioButtons.get(0).doClick();
            }
            add(buttonPanel, BorderLayout.CENTER);
        } else {
            add(new JLabel("No choices defined in quickform node"), BorderLayout.NORTH);
        }
        loadValueConfig(config.getValueConfiguration());
    }

    /** {@inheritDoc} */
    @Override
    public void saveSettings(
            final StringSelectionInputQuickFormValueInConfiguration config)
            throws InvalidSettingsException {
        StringSelectionInputQuickFormValueInConfiguration v = config;
        v.setValue(getSelected());
    }

    /** {@inheritDoc} */
    @Override
    public void loadSettings(
            final StringSelectionInputQuickFormValueInConfiguration config) {
        loadValueConfig(config);
    }

    private void loadValueConfig(
            final StringSelectionInputQuickFormValueInConfiguration config) {
        String value = config.getValue();
        boolean found = false;
        for (JRadioButton b : m_radioButtons) {
            if (b.getText().equals(value)) {
                b.doClick();
                found = true;
                break;
            }
        }
        if (!found) {
            NodeLogger.getLogger(getClass()).warn("Did not find matching value "
                    + "for \"" + value + "\" in choice configuration");
        }
    }

    @Override
    public void updateQuickFormInElement(final AbstractQuickFormInElement e) throws InvalidSettingsException {
        StringSelectionInputQuickFormInElement cast =
            AbstractQuickFormElement.cast(StringSelectionInputQuickFormInElement.class, e);
        cast.setValue(getSelected());
    }

    private String getSelected() {
        String selected = null;
        for (JRadioButton b : m_radioButtons) {
            if (b.isSelected()) {
                selected = b.getText();
                break;
            }
        }
        return selected;
    }

}
