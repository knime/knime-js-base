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
 *   16 May 2019 (albrecht): created
 */
package org.knime.js.base.node.viz.generic3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.fife.ui.rtextarea.RTextScrollPane;
import org.knime.base.util.flowvariable.FlowVariableResolver;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.util.FlowVariableListCellRenderer;
import org.knime.core.node.util.dialog.FieldsTableModel;
import org.knime.core.node.util.dialog.FieldsTableModel.Column;
import org.knime.core.node.util.dialog.OutFieldsTable;
import org.knime.core.node.util.dialog.OutFieldsTableModel;
import org.knime.core.node.workflow.FlowVariable;
import org.knime.js.base.node.ui.CSSSnippetTextArea;
import org.knime.js.base.node.ui.JSSnippetTextArea;
import org.knime.js.base.template.AddTemplateDialog;
import org.knime.js.base.template.JSTemplate;
import org.knime.js.base.template.TemplateProvider;
import org.knime.js.base.template.TemplateReceiver;

import com.google.common.collect.BiMap;

/**
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("serial")
public class GenericJSNodePanel extends JPanel implements TemplateReceiver {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(GenericJSNodePanel.class);

    private final BiMap<String, String> m_availableLibraries;
    private final GenericJSViewConfig m_config;
    private final Class<?> m_templateMetaCategory;

    private final JCheckBox m_generateViewCheckBox;
    private final JSpinner m_maxRowsSpinner;
    private final JCheckBox m_sanitizeInputCheckBox;
    @SuppressWarnings("rawtypes")
    private final JList m_flowVarList;
    private final JTable m_dependenciesTable;
    private final JSSnippetTextArea m_jsTextArea;
    private final JSSnippetTextArea m_jsSVGTextArea;
    private final CSSSnippetTextArea m_cssTextArea;
    private final JSpinner m_waitTimeSpinner;
    private final OutFieldsTable m_outFieldsTable;

    private Border m_noBorder = BorderFactory.createEmptyBorder();
    private Border m_paddingBorder = BorderFactory.createEmptyBorder(3, 3, 3, 3);
    private Border m_lineBorder = BorderFactory.createLineBorder(new Color(200, 200, 200), 1);

    private boolean m_enabled;

    /**
     * Creates a new panel
     *
     * @param templateMetaCategory the meta category to be used for js templates
     * @param config the current config
     * @param availableLibraries a pre-compiled map of available libraries that can be chosen as dependencies
     * @param isPreview if the panel is supposed to act as a preview (e.g. in the templates tab)
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected GenericJSNodePanel(final Class<?> templateMetaCategory, final GenericJSViewConfig config,
        final BiMap<String, String> availableLibraries, final boolean isPreview) {
        m_templateMetaCategory = templateMetaCategory;
        m_config = config;
        m_availableLibraries = availableLibraries;
        m_enabled = true;

        m_generateViewCheckBox = new JCheckBox("Generate image at outport");
        m_maxRowsSpinner = new JSpinner(new SpinnerNumberModel(0, 0, null, 1));
        m_sanitizeInputCheckBox = new JCheckBox("Sanitize input data");
        m_waitTimeSpinner = new JSpinner(new SpinnerNumberModel(0, 0, null, 500));
        m_flowVarList = new JList(new DefaultListModel());
        m_flowVarList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        m_flowVarList.setCellRenderer(new FlowVariableListCellRenderer());
        m_flowVarList.addMouseListener(new MouseAdapter() {
            /** {@inheritDoc} */
            @Override
            public final void mouseClicked(final MouseEvent e) {
                if (e.getClickCount() == 2) {
                    FlowVariable o = (FlowVariable)m_flowVarList.getSelectedValue();
                    if (o != null) {
                        m_jsTextArea.replaceSelection(FlowVariableResolver.getPlaceHolderForVariable(o));
                        m_flowVarList.clearSelection();
                        m_jsTextArea.requestFocus();
                    }
                }
            }
        });
        m_jsTextArea = new JSSnippetTextArea();
        m_jsSVGTextArea = new JSSnippetTextArea();
        m_cssTextArea = new CSSSnippetTextArea();
        TableModel tableModel = new DefaultTableModel(0, 2) {
            /**
             * {@inheritDoc}
             */
            @Override
            public Class<?> getColumnClass(final int column) {
                switch (column) {
                    case 0:
                        return Boolean.class;
                    case 1:
                        return String.class;
                    default:
                        return Boolean.class;
                }
            }
            /**
             * {@inheritDoc}
             */
            @Override
            public boolean isCellEditable(final int row, final int column) {
                if (column == 0) {
                    return true;
                }
                return false;
            }
        };
        m_dependenciesTable = new JTable(tableModel);
        m_dependenciesTable.getColumnModel().getColumn(0).setMaxWidth(30);
        m_dependenciesTable.setTableHeader(null);
        m_outFieldsTable = createOutVariableTable();
        m_outFieldsTable.getTable().addMouseListener(new MouseAdapter() {
            /** {@inheritDoc} */
            @Override
            public void mouseClicked(final MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JTable table = m_outFieldsTable.getTable();
                    FieldsTableModel model = (FieldsTableModel)table.getModel();
                    int col = table.getSelectedColumn();
                    if (col == model.getIndex(Column.TARGET_FIELD)) {
                        int row = table.getSelectedRow();
                        String fieldName = (String)model.getValueAt(row, col);
                        m_jsTextArea.replaceSelection(fieldName);
                        table.clearSelection();
                        m_jsTextArea.requestFocus();
                    }
                }
            }
        });

        createPanel(isPreview);
        setEnabled(!isPreview);
    }

    /**
     * @return the jsSVGTextArea
     */
    public JSSnippetTextArea getJsSVGTextArea() {
        return m_jsSVGTextArea;
    }

    /**
     * @return the generateViewCheckBox
     */
    public JCheckBox getGenerateViewCheckBox() {
        return m_generateViewCheckBox;
    }

    /**
     * @return the waitTimeSpinner
     */
    public JSpinner getWaitTimeSpinner() {
        return m_waitTimeSpinner;
    }

    private JPanel createPanel(final boolean isPreview) {
        setLayout(new BorderLayout());
        setBorder(m_paddingBorder);
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridBagLayout());
        GridBagConstraints topGbc = new GridBagConstraints();
        topGbc.anchor = GridBagConstraints.EAST;
        topGbc.fill = GridBagConstraints.HORIZONTAL;
        topGbc.insets = new Insets(0, 0, 0, 0);
        topGbc.gridx = topGbc.gridy = 0;
        topGbc.ipadx = 0;
        topPanel.setBorder(m_lineBorder);
        topPanel.add(new JLabel("Maximum number of rows: "), topGbc);
        topGbc.gridx++;
        m_maxRowsSpinner.setMaximumSize(new Dimension(100, 20));
        m_maxRowsSpinner.setMinimumSize(new Dimension(100, 20));
        m_maxRowsSpinner.setPreferredSize(new Dimension(100, 20));
        topPanel.add(m_maxRowsSpinner, topGbc);
        topGbc.gridx++;
        topGbc.anchor = GridBagConstraints.CENTER;
        topGbc.insets = new Insets(0, 10, 0, 0);
        topPanel.add(m_sanitizeInputCheckBox, topGbc);
        if (!isPreview) {
            final JButton addTemplateButton = new JButton("Create Template...");
            addTemplateButton.addActionListener(e -> {
                final Frame parent = (Frame)SwingUtilities.getAncestorOfClass(Frame.class, addTemplateButton);
                final NodeSettings settings = new NodeSettings("tempSettings");
                try {
                    saveSettingsTo(settings);
                    GenericJSViewConfig config = new GenericJSViewConfig();
                    config.loadSettingsForDialog(settings);
                    final JSTemplate newTemplate =
                            AddTemplateDialog.openUserDialog(parent, config, m_templateMetaCategory);
                    if (null != newTemplate) {
                        TemplateProvider.getDefault().addTemplate(newTemplate);
                        validate();
                    }
                } catch (InvalidSettingsException e1) {
                    LOGGER.error("Failed to create template: " + e1);
                }
            });
            topGbc.gridx++;
            topGbc.anchor = GridBagConstraints.WEST;
            topGbc.insets = new Insets(0, 60, 0, 10);
            topPanel.add(addTemplateButton, topGbc);
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = gbc.gridy = 0;
        add(topPanel, BorderLayout.NORTH);

        JPanel p = new JPanel(new BorderLayout());

        JSplitPane leftPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
        leftPane.setBorder(m_noBorder);
        leftPane.setDividerLocation(120);
        JPanel topLeftPanel = new JPanel(new BorderLayout(2, 2));
        topLeftPanel.setBorder(m_paddingBorder);
        topLeftPanel.add(new JLabel("Flow Variables"), BorderLayout.NORTH);
        JScrollPane flowVarScroller = new JScrollPane(m_flowVarList);
        topLeftPanel.add(flowVarScroller, BorderLayout.CENTER);
        topLeftPanel.setPreferredSize(new Dimension(400, 130));
        JPanel bottomLeftPanel = new JPanel(new BorderLayout(2, 2));
        bottomLeftPanel.setBorder(m_paddingBorder);
        bottomLeftPanel.add(new JLabel("CSS"), BorderLayout.NORTH);
        JScrollPane cssScroller = new RTextScrollPane(m_cssTextArea);
        bottomLeftPanel.add(cssScroller, BorderLayout.CENTER);
        bottomLeftPanel.setPreferredSize(new Dimension(400, 400));
        leftPane.setTopComponent(topLeftPanel);
        leftPane.setBottomComponent(bottomLeftPanel);

        JSplitPane rightPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
        rightPane.setBorder(m_noBorder);
        rightPane.setDividerLocation(120);
        JPanel topRightPanel = new JPanel(new BorderLayout(2, 2));
        topRightPanel.setBorder(m_paddingBorder);
        topRightPanel.add(new JLabel("Dependencies"), BorderLayout.NORTH);
        JScrollPane dependenciesScroller = new JScrollPane(m_dependenciesTable);
        topRightPanel.add(dependenciesScroller, BorderLayout.CENTER);
        topRightPanel.setPreferredSize(new Dimension(400, 130));
        JPanel bottomRightPanel = new JPanel(new BorderLayout(2, 2));
        bottomRightPanel.setBorder(m_paddingBorder);
        bottomRightPanel.add(new JLabel("JavaScript"), BorderLayout.NORTH);
        JScrollPane jsScroller = new RTextScrollPane(m_jsTextArea);
        bottomRightPanel.add(jsScroller, BorderLayout.CENTER);
        bottomRightPanel.setPreferredSize(new Dimension(400, 400));
        rightPane.setTopComponent(topRightPanel);
        rightPane.setBottomComponent(bottomRightPanel);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
        splitPane.setBorder(m_noBorder);
        splitPane.setDividerLocation(0.5);
        splitPane.setLeftComponent(leftPane);
        splitPane.setRightComponent(rightPane);

        p.add(splitPane, BorderLayout.CENTER);

        JSplitPane outFieldsPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
        outFieldsPane.setBorder(m_noBorder);
        outFieldsPane.setTopComponent(p);
        //m_outFieldsTable.getTable().setMaximumSize(new Dimension(m_outFieldsTable.getWidth(), 50));
        m_outFieldsTable.setBorder(BorderFactory.createTitledBorder("Output Flow Variables"));
        m_outFieldsTable.setPreferredSize(m_outFieldsTable.getMinimumSize());
        outFieldsPane.setBottomComponent(m_outFieldsTable);
        outFieldsPane.setDividerLocation(0.8);
        outFieldsPane.setResizeWeight(0.7);


        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy++;
        add(outFieldsPane, BorderLayout.CENTER);

        return this;
    }

    private static OutFieldsTable createOutVariableTable() {
        OutFieldsTable table = new OutFieldsTable(true, true);
        OutFieldsTableModel model = (OutFieldsTableModel)table.getTable().getModel();
        table.getTable().getColumnModel().getColumn(model.getIndex(
            Column.REPLACE_EXISTING)).setPreferredWidth(10);
        table.getTable().getColumnModel().getColumn(model.getIndex(
            Column.DATA_TYPE)).setPreferredWidth(20);
        return table;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyTemplate(final JSTemplate template, final DataTableSpec spec,
            final Map<String, FlowVariable> flowVariables) {
        // save and read settings to decouple objects.
        final NodeSettings settings = new NodeSettings(template.getUUID());
        template.getSnippetSettings().saveSettings(settings);
        try (final ByteArrayOutputStream os = new ByteArrayOutputStream()){
            settings.saveToXML(os);
            final NodeSettingsRO settingsro =
                NodeSettings.loadFromXML(new ByteArrayInputStream(os.toString("UTF-8").getBytes("UTF-8")));
            loadSettingsFrom(settingsro, spec, flowVariables);
        } catch (final Exception e) {
            LOGGER.error("Cannot apply template.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled() {
        return m_enabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnabled(final boolean enabled) {
        if (m_enabled != enabled) {
            m_maxRowsSpinner.setEnabled(enabled);
            m_sanitizeInputCheckBox.setEnabled(enabled);
            m_flowVarList.setEnabled(enabled);
            m_dependenciesTable.setEnabled(enabled);
            m_cssTextArea.setEnabled(enabled);
            m_jsTextArea.setEnabled(enabled);
            m_outFieldsTable.setEnabled(enabled);
            m_generateViewCheckBox.setEnabled(enabled);
            m_jsSVGTextArea.setEnabled(enabled);
        }
        m_enabled = enabled;
    }

    /**
     * Loads settings into panel
     * @param settings the settings to load
     * @param spec the current {@link DataTableSpec}
     * @param flowVariables a map of currently available {@link FlowVariable}s
     * @throws NotConfigurableException if loading the settings fails
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void loadSettingsFrom(final NodeSettingsRO settings, final DataTableSpec spec,
        final Map<String, FlowVariable> flowVariables)
        throws NotConfigurableException {
        DefaultListModel listModel = (DefaultListModel)m_flowVarList.getModel();
        listModel.removeAllElements();
        for (FlowVariable e : flowVariables.values()) {
            listModel.addElement(e);
        }
        DefaultTableModel tableModel = (DefaultTableModel)m_dependenciesTable.getModel();
        tableModel.setRowCount(0);
        List<String> libNameList = new ArrayList<String>(m_availableLibraries.values());
        Collections.sort(libNameList);
        for (String lib : libNameList) {
            tableModel.addRow(new Object[]{false, lib});
        }
        m_config.loadSettingsForDialog(settings);
        String[] activeLibs = m_config.getDependencies();
        for (String lib: activeLibs) {
            String displayLib = m_availableLibraries.get(lib);
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getValueAt(i, 1).equals(displayLib)) {
                    tableModel.setValueAt(true, i, 0);
                    break;
                }
            }
        }

        m_generateViewCheckBox.setSelected(m_config.getGenerateView());
        m_maxRowsSpinner.setValue(m_config.getMaxRows());
        m_sanitizeInputCheckBox.setSelected(m_config.isSanitizeInput());
        m_jsTextArea.setText(m_config.getJsCode());
        m_jsSVGTextArea.setText(m_config.getJsSVGCode());
        m_cssTextArea.setText(m_config.getCssCode());
        m_waitTimeSpinner.setValue(m_config.getWaitTime());

        m_cssTextArea.installAutoCompletion();

        m_outFieldsTable.updateData(m_config.getFieldCollection(), spec, flowVariables);

    }

    /**
     * Saves the current settings
     * @param settings the settings to save to
     * @throws InvalidSettingsException if settings are invalid
     */
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        List<String> dependencies = new ArrayList<String>();
        for (int row = 0; row < m_dependenciesTable.getRowCount(); row++) {
            if ((boolean)m_dependenciesTable.getValueAt(row, 0)) {
                String libDisplay = (String)m_dependenciesTable.getValueAt(row, 1);
                dependencies.add(m_availableLibraries.inverse().get(libDisplay));
            }
        }
        m_config.setGenerateView(m_generateViewCheckBox.isSelected());
        m_config.setMaxRows((Integer)m_maxRowsSpinner.getValue());
        m_config.setSanitizeInput(m_sanitizeInputCheckBox.isSelected());
        m_config.setJsCode(m_jsTextArea.getText());
        m_config.setJsSVGCode(m_jsSVGTextArea.getText());
        m_config.setCssCode(m_cssTextArea.getText());
        m_config.setDependencies(dependencies.toArray(new String[0]));
        m_config.setWaitTime((Integer)m_waitTimeSpinner.getValue());
        FieldsTableModel outFieldsModel = (FieldsTableModel)m_outFieldsTable.getTable().getModel();
        if (!outFieldsModel.validateValues()) {
            throw new IllegalArgumentException("The variable fields table has errors.");
        }
        m_config.setOutVarList(m_outFieldsTable.getOutVarFields());
        m_config.saveSettings(settings);
    }

}
