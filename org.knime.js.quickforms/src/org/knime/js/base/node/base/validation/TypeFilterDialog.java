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
 * ---------------------------------------------------------------------
 *
 * Created on Oct 10, 2013 by Patrick Winter, KNIME AG, Zurich, Switzerland
 */
package org.knime.js.base.node.base.validation;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.data.BooleanValue;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.DataValue;
import org.knime.core.data.DataValue.UtilityFactory;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.ExtensibleUtilityFactory;
import org.knime.core.data.IntValue;
import org.knime.core.data.LongValue;
import org.knime.core.data.StringValue;
import org.knime.core.data.time.localdatetime.LocalDateTimeValue;

/**
 * Filters based on the DataValues of columns.
 *
 * @author Adrian Nembach, KNIME GmbH, Konstanz, Germany
 */
final class TypeFilterDialog {

    private static final List<Class<? extends DataValue>> DEFAULT_TYPES = Arrays.asList(BooleanValue.class,
        IntValue.class, DoubleValue.class, LongValue.class, StringValue.class, LocalDateTimeValue.class);

    private final Map<String, TypeCheckBox> m_selections;

    private final JPanel m_selectionPanel;

    private List<ChangeListener> m_listeners;

    private Map<String, Boolean> m_selectionValues = new LinkedHashMap<>();

    private final JPanel m_panel = new JPanel(new GridBagLayout());

    /**
     * Creates a DataValue filter panel.
     *
     */
    public TypeFilterDialog() {
        m_selectionPanel = new JPanel(new GridBagLayout());
        m_selections = new LinkedHashMap<>();
        layout();
    }

    private void layout() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.gridy++;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(4, 0, 4, 0);
        c.anchor = GridBagConstraints.BASELINE_LEADING;
        m_panel.add(m_selectionPanel, c);
        c.gridy++;
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.insets = new Insets(0, 0, 0, 0);
    }

    public JPanel getPanel() {
        return m_panel;
    }

    public void setEnabled(final boolean enabled) {
        m_panel.setEnabled(enabled);
        for (TypeCheckBox checkBox : m_selections.values()) {
            checkBox.setEnabled(enabled);
        }
    }

    /**
     * @param config to load from
     * @param spec specs of the available columns that will be shown in the selection preview
     */
    public void loadConfiguration(final TypeFilterConfig config, final DataTableSpec spec) {
        clearTypes();
        // ArrayList holding all the JPanels with the type checkboxes
        ArrayList<JPanel> checkboxes = new ArrayList<>();
        // add checkboxes from the DataTableSpec
        checkboxes.addAll(getCheckBoxPanelsForDataColumns(spec));
        // add checkboxes for the default data values
        checkboxes.addAll(getCheckBoxPanelsForDataValues(DEFAULT_TYPES));
        // add checkboxes from the configuration
        Map<String, Boolean> mapping = new LinkedHashMap<>(config.getSelections());
        for (Map.Entry<String, Boolean> entry : mapping.entrySet()) {
            final String valueClassName = entry.getKey();
            final Boolean valueSelected = entry.getValue();
            TypeCheckBox box = m_selections.get(valueClassName);
            if (box != null) {
                box.setSelected(valueSelected);
            } else if (valueSelected) {
                // type included by currently not in the input spec
                checkboxes.add(getCheckBoxPanel(null, valueClassName, true, valueClassName, true, false));
            } else {
                // do nothing
            }
        }
        initTypeSelectionPanel(checkboxes);
    }

    /**
     * Initializes the type selection panel with the provided CheckBoxes
     *
     * @param checkboxes List of JPanels containing the JCheckBoxes, an Icon depicting the type and a Label
     */
    private void initTypeSelectionPanel(final ArrayList<JPanel> checkboxes) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.BASELINE_LEADING;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        // number of columns
        int columns = 3;

        for (JPanel jp : checkboxes) {
            // add CheckBox Panel to selectionPanel
            m_selectionPanel.add(jp, gbc);
            // if CheckBox was the last in entry in the row
            if (gbc.gridx == (columns - 1)) {
                gbc.gridx = 0;
                gbc.gridy++;
            } else {
                gbc.gridx++;
            }
        }
    }

    /** @param config to save to */
    public void saveConfiguration(final TypeFilterConfig config) {
        LinkedHashMap<String, Boolean> mapping = new LinkedHashMap<>();
        for (Entry<String, TypeCheckBox> selection : m_selections.entrySet()) {
            mapping.put(selection.getKey(), selection.getValue().isSelected());
        }
        config.setSelections(mapping);
    }

    /**
     * Returns the type checkboxes (each in a JPanel) columns.
     *
     * @param columns The columns contained in the current data table.
     */
    private ArrayList<JPanel> getCheckBoxPanelsForDataColumns(final Iterable<DataColumnSpec> columns) {
        ArrayList<JPanel> panels = new ArrayList<>();
        for (DataColumnSpec column : columns) {
            final Class<? extends DataValue> prefValueClass = column.getType().getPreferredValueClass();
            if (!m_selections.containsKey(prefValueClass.getName())) {
                consumeIfExtensibleUtility(panels::add, prefValueClass, true);
            }
        }
        return panels;
    }

    private void consumeIfExtensibleUtility(final Consumer<JPanel> consumer,
        final Class<? extends DataValue> prefValueClass, final boolean isInTableSpec) {
        final UtilityFactory utilityFor = DataType.getUtilityFor(prefValueClass);
        if (utilityFor instanceof ExtensibleUtilityFactory) {
            final ExtensibleUtilityFactory eu = (ExtensibleUtilityFactory)utilityFor;
            final String label = eu.getName();
            final String key = prefValueClass.getName();
            consumer.accept(getCheckBoxPanel(utilityFor.getIcon(), label, false, key, false, isInTableSpec));
        }
    }

    /**
     * Returns the type checkboxes (each in a JPanel) for the data values.
     *
     * @param values The data values to add
     */
    private ArrayList<JPanel> getCheckBoxPanelsForDataValues(final Iterable<Class<? extends DataValue>> values) {
        ArrayList<JPanel> panels = new ArrayList<>();
        for (Class<? extends DataValue> value : values) {
            if (value != null && !m_selections.containsKey(value.getName())) {
                consumeIfExtensibleUtility(panels::add, value, false);
            }
        }
        return panels;
    }

    private void clearTypes() {
        m_selectionPanel.removeAll();
        m_selections.clear();
    }

    /**
     * Returns a CheckBox with an icon and label representing the type. A red border is painted around the label if the
     * type no longer exists in the input. The checkbox label is written italic if the type is not present in the
     * DataTableSpec.
     *
     * @param label label of the type
     * @param addRedBorderAsInvalid if a red border should be painted around the label
     * @param icon type icon (see getIcon() in {@link DataValue.UtilityFactory})
     * @param setSelected if the checkbox should be selected
     * @param inDataTableSpec whether the type of this checkbox is contained in the DataTableSpec
     * @return
     */
    private JPanel getCheckBoxPanel(final Icon icon, final String label, final boolean addRedBorderAsInvalid,
        final String key, final boolean setSelected, final boolean inDataTableSpec) {
        final Font parentFont = m_panel.getFont();
        final TypeCheckBox typeCheckbox = new TypeCheckBox(icon, label, addRedBorderAsInvalid, setSelected,
            inDataTableSpec ? parentFont : parentFont.deriveFont(Font.ITALIC));
        typeCheckbox.addListener(e -> {
            if (m_selectionValues.get(label).booleanValue() != typeCheckbox.isSelected()) {
                m_selectionValues.put(label, typeCheckbox.isSelected());
                fireFilteringChangedEvent();
            }
        });

        m_selectionValues.put(label, typeCheckbox.isSelected());
        m_selections.put(key, typeCheckbox);
        return typeCheckbox.m_panel;
    }

    private static class TypeCheckBox {
        private final JPanel m_panel = new JPanel(new GridBagLayout());

        private final JCheckBox m_checkbox = new JCheckBox();

        private final JLabel m_label;

        TypeCheckBox(final Icon icon, final String label, final boolean addRedBorderAsInvalid, final boolean isSelected,
            final Font font) {
            if (addRedBorderAsInvalid) {
                m_checkbox.setToolTipText("Type no longer exists in input");
                m_panel.setBorder(BorderFactory.createLineBorder(Color.RED));
            }
            m_checkbox.setSelected(isSelected);
            m_label = createLabel(label, icon, font);
            m_label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    if (m_label.isEnabled()) {
                        m_checkbox.setSelected(!m_checkbox.isSelected());
                    }
                }
            });
            layout();
        }

        void addListener(final ChangeListener listener) {
            m_checkbox.addChangeListener(listener);
        }

        private void layout() {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(0, 0, 4, 0);
            gbc.anchor = GridBagConstraints.WEST;
            m_panel.add(m_checkbox, gbc);
            gbc.gridx++;
            gbc.weightx = 0.1;
            m_panel.add(m_label, gbc);
        }

        private static JLabel createLabel(final String text, final Icon icon, final Font font) {
            final JLabel label = new JLabel(text);
            if (icon != null) {
                label.setIcon(icon);
            }
            label.setFont(font);
            return label;
        }

        void setEnabled(final boolean enabled) {
            m_panel.setEnabled(enabled);
            m_checkbox.setEnabled(enabled);
            m_label.setEnabled(enabled);
        }

        boolean isSelected() {
            return m_checkbox.isSelected();
        }

        void setSelected(final boolean isSelected) {
            m_checkbox.setSelected(isSelected);
        }
    }

    /**
     * Adds a listener which gets informed whenever the filtering changes.
     *
     * @param listener the listener
     */
    public void addChangeListener(final ChangeListener listener) {
        if (m_listeners == null) {
            m_listeners = new LinkedList<>();
        }
        m_listeners.add(listener);
    }

    /**
     * Removes the given listener from this filter panel.
     *
     * @param listener the listener.
     */
    public void removeChangeListener(final ChangeListener listener) {
        if (m_listeners != null) {
            m_listeners.remove(listener);
        }
    }

    private void fireFilteringChangedEvent() {
        if (m_listeners != null) {
            for (ChangeListener listener : m_listeners) {
                listener.stateChanged(new ChangeEvent(this));
            }
        }
    }

}
