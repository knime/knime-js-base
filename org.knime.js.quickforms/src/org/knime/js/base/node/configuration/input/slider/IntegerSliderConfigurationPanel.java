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
 *   May 24, 2019 (Daniel Bogenrieder): created
 */
package org.knime.js.base.node.configuration.input.slider;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.BoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.node.InvalidSettingsException;
import org.knime.js.base.node.configuration.AbstractDialogNodeConfigurationPanel;
import org.knime.js.core.settings.DialogUtil;

/**
 * The component dialog panel for the slider configuration node
 *
 * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("serial")
public class IntegerSliderConfigurationPanel extends AbstractDialogNodeConfigurationPanel<IntegerSliderDialogNodeValue> {

    private final JSlider m_component;
    private final JLabel m_component_text;
    private final JPanel m_panel;

    /**
     * @param representation the dialog node settings
     */
    public IntegerSliderConfigurationPanel(final IntegerSliderDialogNodeRepresentation representation) {
        super(representation.getLabel(), representation.getDescription(), representation.getDefaultValue());

        // default min and max value
        int min = 0;
        int max = 100;

        // Steps of the slider
        int MAJOR_TICK_COUNT = 5;
        int MINOR_TICK_DIVISIONS = 20;

        if(representation.isUseCustomMin()) {
            min = (int)representation.getCustomMin();
        }
        if(representation.isUseCustomMax()) {
            max = (int)representation.getCustomMax();
        }
        // Text Label next to the slider to show current value
        m_component_text = new JLabel();
        m_component_text.setText(String.valueOf(representation.getDefaultValue().getDouble().intValue()));
        m_component = new JSlider();
        m_component.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                m_component_text.setText(((Integer)m_component.getValue()).toString());
            }
        });
        // Model of JSlider to adjust min and max
        BoundedRangeModel sliderModel = m_component.getModel();
        sliderModel.setMaximum(max);
        sliderModel.setMinimum(min);


        // Add first and last Value as labels to the slider
        Dictionary<Integer, JComponent> myDictionary = new Hashtable<Integer, JComponent>();
        Integer key = (int)min;
        JLabel value = new JLabel(String.valueOf(min));
        myDictionary.put(key, value);
        Integer key1 = (int)max;
        JLabel value1 = new JLabel(String.valueOf(max));
        myDictionary.put(key1, value1);

        m_component.setValue(representation.getDefaultValue().getDouble().intValue());
        m_component.setPaintTicks(true);
        m_component.setPaintLabels(true);
        m_component.setLabelTable(myDictionary);
//        m_component.setMajorTickSpacing((int)max / (MAJOR_TICK_COUNT - 1));
        m_component.setMinorTickSpacing(max / MINOR_TICK_DIVISIONS);

        m_panel = new JPanel(new GridBagLayout());
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        m_panel.add(m_component, gbc);
        gbc.gridx++;
        gbc.insets = new Insets(5, 5, 30, 5);
        m_panel.add(m_component_text, gbc);
        m_component.setPreferredSize(new Dimension(
            (new JTextField(DialogUtil.DEF_TEXTFIELD_WIDTH).getPreferredSize().width), getPreferredSize().height));
        setComponent(m_panel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void resetToDefault() {
        m_component.setValue(getDefaultValue().getDouble().intValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IntegerSliderDialogNodeValue createNodeValue() throws InvalidSettingsException {
        IntegerSliderDialogNodeValue value = new IntegerSliderDialogNodeValue();
        value.setDouble((double)m_component.getValue());
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadNodeValue(final IntegerSliderDialogNodeValue value) {
        super.loadNodeValue(value);
        if (value != null) {
            m_component.setValue(value.getDouble().intValue());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        m_component.setEnabled(enabled);
    }

}
