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
 *   24.04.2015 (Christian Albrecht, KNIME AG, Zurich, Switzerland): created
 */
package org.knime.dynamic.js.v30;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.xmlbeans.XmlObject;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponent;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentButtonGroup;
import org.knime.core.node.defaultnodesettings.DialogComponentColorChooser;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnFilter2;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.DialogComponentDate;
import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;
import org.knime.core.node.defaultnodesettings.DialogComponentFlowVariableNameSelection;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.DialogComponentStringListSelection;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModel;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelColor;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnFilter2;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnName;
import org.knime.core.node.defaultnodesettings.SettingsModelDate;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.defaultnodesettings.SettingsModelStringArray;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.ColumnFilter;
import org.knime.core.node.util.DataValueColumnFilter;
import org.knime.core.node.util.StringHistory;
import org.knime.core.node.workflow.FlowVariable;
import org.knime.core.node.workflow.FlowVariable.Type;
import org.knime.dynamic.js.DialogComponentSVGOptions;
import org.knime.dynamic.js.SettingsModelSVGOptions;
import org.knime.dynamicjsnode.v30.DynamicJSKnimeNode;
import org.knime.dynamicnode.v30.CheckBoxOption;
import org.knime.dynamicnode.v30.ColorOption;
import org.knime.dynamicnode.v30.ColumnFilterOption;
import org.knime.dynamicnode.v30.ColumnSelectorOption;
import org.knime.dynamicnode.v30.DateFormatOption;
import org.knime.dynamicnode.v30.DateOption;
import org.knime.dynamicnode.v30.DynamicFullDescription;
import org.knime.dynamicnode.v30.DynamicOptions;
import org.knime.dynamicnode.v30.DynamicTab;
import org.knime.dynamicnode.v30.FileOption;
import org.knime.dynamicnode.v30.FlowVariableSelectorOption;
import org.knime.dynamicnode.v30.FlowVariableType;
import org.knime.dynamicnode.v30.FlowVariableType.Enum;
import org.knime.dynamicnode.v30.NumberOption;
import org.knime.dynamicnode.v30.RadioButtonOption;
import org.knime.dynamicnode.v30.StringListOption;
import org.knime.dynamicnode.v30.StringOption;
import org.knime.dynamicnode.v30.SvgOption;
import org.knime.js.core.components.datetime.DialogComponentDateTimeOptions;
import org.knime.js.core.components.datetime.SettingsModelDateTimeOptions;

/**
 *
 * @author Christian Albrecht, KNIME AG, Zurich, Switzerland
 * @since 3.0
 */
public class DynamicJSNodeDialog extends DefaultNodeSettingsPane {

	private DynamicJSKnimeNode m_nodeConfig;
	private DynamicJSConfig m_config;
	private Map<String, DialogComponent> m_components = new HashMap<String, DialogComponent>();

	private DialogComponentNumber m_maxRowsComponent;
	private DialogComponentBoolean m_generateImageComponent;

	private Map<String, String> m_stringHistoryMap = new HashMap<String, String>();

	private String m_firstTab = "Options";
	private boolean m_showMaxRows = true;

	/**
	 * @param nodeConfig
	 */
	public DynamicJSNodeDialog(final DynamicJSKnimeNode nodeConfig) {
		m_nodeConfig = nodeConfig;
		m_config = new DynamicJSConfig(nodeConfig);

		if (m_nodeConfig.isSetJavaProcessor()) {
		    m_showMaxRows = !m_nodeConfig.getJavaProcessor().getHidesLimitRowOption();
		}

		DynamicFullDescription desc = m_nodeConfig.getFullDescription();
		if (desc.getOptions() != null) {
		    createAdditionalOptions();
			fillOptions(desc.getOptions());
		}
		if (desc.getTabArray() != null && desc.getTabArray().length > 0) {
			removeTab(m_firstTab);
			m_firstTab = desc.getTabArray(0).getName();
		}
		for (DynamicTab tab : desc.getTabArray()) {
		    createNewTab(tab.getName());
		    if (tab.getName().equals(m_firstTab)) {
		        createAdditionalOptions();
		    }
			fillOptions(tab.getOptions());
		}

	}

	private void fillOptions(final DynamicOptions options) {
		XmlObject[] oOptions = options.selectPath("$this/*");
		for (XmlObject option : oOptions) {
			if (option instanceof CheckBoxOption) {
				CheckBoxOption cO = (CheckBoxOption)option;
				SettingsModelBoolean model = (SettingsModelBoolean)m_config.getModel(cO.getId());
				DialogComponentBoolean bComp = new DialogComponentBoolean(model, cO.getLabel());
				bComp.setToolTipText(cO.getTooltip());
				m_components.put(cO.getId(), bComp);
				addDialogComponent(bComp);
			} else if (option instanceof NumberOption) {
			    NumberOption iO = (NumberOption)option;
			    SettingsModelNumber model = (SettingsModelNumber)m_config.getModel(iO.getId());
			    DialogComponentNumber iComp = new DialogComponentNumber(model, iO.getLabel(), iO.getStepSize());
			    iComp.setToolTipText(iO.getTooltip());
			    m_components.put(iO.getId(), iComp);
			    addDialogComponent(iComp);
			} else if (option instanceof RadioButtonOption) {
			   RadioButtonOption rO = (RadioButtonOption)option;
			   SettingsModelString model = (SettingsModelString)m_config.getModel(rO.getId());
			   @SuppressWarnings("unchecked")
                String[] possibleValues = ((List<String>)rO.getPossibleValues()).toArray(new String[0]);
                DialogComponentButtonGroup rComp =
                    new DialogComponentButtonGroup(model, rO.getAlignVertical(), rO.getLabel(), possibleValues);
                rComp.setToolTipText(rO.getTooltip());
			   m_components.put(rO.getId(), rComp);
			   addDialogComponent(rComp);
			} else if (option instanceof StringListOption) {
			    StringListOption sO = (StringListOption)option;
			    DialogComponent sComp;
			    Set<String> possibleValues = new LinkedHashSet<String>();
			    if (sO.isSetStringHistoryKey()) {
			        StringHistory history = StringHistory.getInstance(sO.getStringHistoryKey());
			        possibleValues.addAll(Arrays.asList(history.getHistory()));
			        m_stringHistoryMap.put(sO.getId(), sO.getStringHistoryKey());
			    }
			    @SuppressWarnings("unchecked")
                List<String> valuesFromOptions = sO.getPossibleValues();
			    possibleValues.addAll(valuesFromOptions);
                if (sO.getAllowMultipleSelection()) {
                    SettingsModelStringArray model = (SettingsModelStringArray)m_config.getModel(sO.getId());
                    int numRows = -1;
                    if (sO.isSetNumRowsVisible()) {
                        numRows = sO.getNumRowsVisible().intValue();
                    }
                    sComp = new DialogComponentStringListSelection(model, sO.getLabel(), possibleValues,
                        !sO.getOptional(), numRows);
                } else {
                    SettingsModelString model = (SettingsModelString)m_config.getModel(sO.getId());
                    sComp = new DialogComponentStringSelection(model, sO.getLabel(), possibleValues, sO.getEditable());
			    }
			    sComp.setToolTipText(sO.getTooltip());
			    m_components.put(sO.getId(), sComp);
			    addDialogComponent(sComp);
			} else if (option instanceof DateOption) {
			    DateOption dO = (DateOption)option;
			    SettingsModelDate model = (SettingsModelDate)m_config.getModel(dO.getId());
			    DialogComponentDate dComp = new DialogComponentDate(model, dO.getLabel(), dO.getOptional());
			    dComp.setToolTipText(dO.getTooltip());
			    m_components.put(dO.getId(), dComp);
			    addDialogComponent(dComp);
			} else if (option instanceof DateFormatOption) {
			    DateFormatOption dfO = (DateFormatOption)option;
			    SettingsModelDateTimeOptions model = (SettingsModelDateTimeOptions)m_config.getModel(dfO.getId());
			    DialogComponentDateTimeOptions.Config config = new DialogComponentDateTimeOptions.Config();
			    config.setShowLocaleChooser(dfO.getShowLocaleChooser());
			    config.setShowDateFormatChooser(dfO.getShowDateFormatChooser());
			    config.setShowDateTimeFormatChooser(dfO.getShowDateTimeFormatChooser());
			    config.setShowTimeFormatChooser(dfO.getShowTimeFormatChooser());
                config.setShowZonedDateTimeFormatChooser(dfO.getShowZonedDateTimeFormatChooser());
                config.setShowLegacyDateTimeFormatChooser(dfO.getShowLegacyDateTimeFormatChooser());
                config.setShowTimezoneChooser(dfO.getShowTimezoneChooser());
                DialogComponentDateTimeOptions dfComp =
                    new DialogComponentDateTimeOptions(model, dfO.getLabel(), config);
                dfComp.setToolTipText(dfO.getTooltip());
                m_components.put(dfO.getId(), dfComp);
                addDialogComponent(dfComp);
            } else if (option instanceof ColorOption) {
                ColorOption cO = (ColorOption)option;
			    SettingsModelColor model = (SettingsModelColor)m_config.getModel(cO.getId());
			    DialogComponentColorChooser cComp = new DialogComponentColorChooser(model, cO.getLabel(), true);
			    if (cO.isSetDefaultR() && cO.isSetDefaultG() && cO.isSetDefaultB()) {
			        Color color = new Color(cO.getDefaultR(), cO.getDefaultG(), cO.getDefaultB());
			        if (cO.isSetDefaultAlpha()) {
			            color = new Color(cO.getDefaultR(), cO.getDefaultG(), cO.getDefaultB(), cO.getDefaultAlpha());
			        }
			        cComp.setColor(color);
			    }
			    cComp.setToolTipText(cO.getTooltip());
			    m_components.put(cO.getId(), cComp);
			    addDialogComponent(cComp);
            } else if (option instanceof FlowVariableSelectorOption) {
                FlowVariableSelectorOption fO = (FlowVariableSelectorOption)option;
                SettingsModelString model = (SettingsModelString)m_config.getModel(fO.getId());
                Collection<FlowVariable> flowVars = getAvailableFlowVariables().values();
                Type flowVarType = null;
                if (fO.isSetFlowVariableType()) {
                    Enum type = fO.getFlowVariableType();
                    if (type.equals(FlowVariableType.INTEGER)) {
                        flowVarType = Type.INTEGER;
                    } else if (type.equals(FlowVariableType.DOUBLE)) {
                        flowVarType = Type.DOUBLE;
                    } else if (type.equals(FlowVariableType.STRING)) {
                        flowVarType = Type.STRING;
                    }
                }
                DialogComponentFlowVariableNameSelection fComp =
                    new DialogComponentFlowVariableNameSelection(model, fO.getLabel(), flowVars, fO.getOptional(),
                        flowVarType);
                fComp.setToolTipText(fO.getTooltip());
                m_components.put(fO.getId(), fComp);
                addDialogComponent(fComp);
            } else if (option instanceof FileOption) {
                FileOption fO = (FileOption)option;
                SettingsModelString model = (SettingsModelString)m_config.getModel(fO.getId());
                String[] extensions = null;
                if (fO.isSetValidExtensions()) {
                    extensions = fO.getValidExtensions().split(",");
                    for (int i = 0; i < extensions.length; i++) {
                        extensions[i] = extensions[i].trim();
                    }
                }
                DialogComponentFileChooser fComp =
                    new DialogComponentFileChooser(model, fO.getHistoryId(), JFileChooser.OPEN_DIALOG,
                        fO.getDirectoryOnly(), extensions);
                fComp.setToolTipText(fO.getTooltip());
                m_components.put(fO.getId(), fComp);
                addDialogComponent(fComp);
            } else if (option instanceof StringOption) {
                StringOption sO = (StringOption)option;
				SettingsModelString model = (SettingsModelString)m_config.getModel(sO.getId());
				DialogComponentString sComp = new DialogComponentString(model, sO.getLabel());
				sComp.setToolTipText(sO.getTooltip());
				m_components.put(sO.getId(), sComp);
				addDialogComponent(sComp);
			} else if (option instanceof ColumnFilterOption) {
				ColumnFilterOption cO = (ColumnFilterOption)option;
				SettingsModelColumnFilter2 model = (SettingsModelColumnFilter2)m_config.getModel(cO.getId());
				DialogComponentColumnFilter2 cComp = new DialogComponentColumnFilter2(model, cO.getInPortIndex());
				cComp.setIncludeTitle(cO.getIncludeTitle());
				cComp.setExcludeTitle(cO.getExcludeTitle());
				cComp.setToolTipText(cO.getTooltip());
				m_components.put(cO.getId(), cComp);
				addDialogComponent(cComp);
			} else if (option instanceof ColumnSelectorOption) {
			    ColumnSelectorOption cO = (ColumnSelectorOption)option;
			    SettingsModelString model = (SettingsModelString)m_config.getModel(cO.getId());
			    ColumnFilter filter = null;
			    if (cO.isSetFilterClasses()) {
			        try {
			            @SuppressWarnings("unchecked")
			            List<String> filterClasses = cO.getFilterClasses();
			            filter = new DataValueColumnFilter(m_config.getFilterClasses(filterClasses));
			        } catch (Exception e) {
			            throw new ClassCastException(e.getMessage());
			        }
			    }
                DialogComponentColumnNameSelection cComp =
                    new DialogComponentColumnNameSelection(model, cO.getLabel(), cO.getInPortIndex(), !cO.getOptional(),
                       cO.getAllowNoneColumn(), filter);
                cComp.setToolTipText(cO.getTooltip());
                m_components.put(cO.getId(), cComp);
                addDialogComponent(cComp);
			} else if (option instanceof SvgOption) {
			    SvgOption sO = (SvgOption)option;
			    SettingsModelSVGOptions model = (SettingsModelSVGOptions)m_config.getModel(sO.getId());
			    if (model != null) {
			        DialogComponentSVGOptions sComp = new DialogComponentSVGOptions(model, sO.getLabel());
			        sComp.setToolTipText(sO.getTooltip());
			        m_components.put(sO.getId(), sComp);
			        addDialogComponent(sComp);
			    }
			}
		}
	}

	private void createAdditionalOptions() {
	    createNewGroup("General Settings");

        if (m_config.getHasSvgImageOutport()) {
            m_generateImageComponent =
                new DialogComponentBoolean(new SettingsModelBoolean(DynamicJSConfig.GENERATE_IMAGE_CONF + "model",
                    DynamicJSConfig.DEFAULT_GENERATE_IMAGE), "Generate image");
            addDialogComponent(m_generateImageComponent);
        }

        if (m_showMaxRows && m_config.getNumberDataInPorts() > 0) {
            m_maxRowsComponent =
                new DialogComponentNumber(new SettingsModelIntegerBounded(DynamicJSConfig.MAX_ROWS_CONF + "model",
                    DynamicJSConfig.DEFAULT_MAX_ROWS, 0, Integer.MAX_VALUE), "Maximum number of rows", 1);
            addDialogComponent(m_maxRowsComponent);
        }

	    closeCurrentGroup();
	}

    private void setEnabled(final SettingsModel source, final SettingsModel dest, final List<String> value) {
		if (value == null || value.size() < 1) {
            return;
        }
	    if (source instanceof SettingsModelBoolean) {
			SettingsModelBoolean bSource = (SettingsModelBoolean)source;
			// only take first value
			dest.setEnabled(bSource.getBooleanValue() == Boolean.parseBoolean(value.get(0)));
		} else if (source instanceof SettingsModelString) {
			SettingsModelString sSource = (SettingsModelString)source;
			dest.setEnabled(value.contains(sSource.getStringValue()));
		} else if (source instanceof SettingsModelColumnName) {
		    SettingsModelColumnName cSource = (SettingsModelColumnName)source;
		    dest.setEnabled(value.contains(cSource.getColumnName()));
		}
	}

	@Override
	public void loadAdditionalSettingsFrom(final NodeSettingsRO settings,
			final PortObjectSpec[] specs) throws NotConfigurableException {
		m_config.loadAdditionalNodeSettingsInDialog(settings);
		super.loadAdditionalSettingsFrom(settings, specs);
        if (m_maxRowsComponent != null) {
            ((SettingsModelIntegerBounded)m_maxRowsComponent.getModel()).setIntValue(m_config.getMaxRows());
        }
        if (m_generateImageComponent != null) {
            ((SettingsModelBoolean)m_generateImageComponent.getModel()).setBooleanValue(m_config.getGenerateImage());
        }
		for (final Vector<String> dependency : m_config.getEnableDependencies()) {
			DialogComponent cFrom = m_components.get(dependency.get(0));
			final DialogComponent cTo = m_components.get(dependency.get(1));
			final List<String> enableValues = dependency.subList(2, dependency.size());
			setEnabled(cFrom.getModel(), cTo.getModel(), enableValues);
			cFrom.getModel().addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(final ChangeEvent e) {
					setEnabled((SettingsModel)e.getSource(), cTo.getModel(), enableValues);
				}
			});
		}
	}

	@Override
	public void saveAdditionalSettingsTo(final NodeSettingsWO settings)
			throws InvalidSettingsException {
        if (m_maxRowsComponent != null) {
            m_config.setMaxRows(((SettingsModelIntegerBounded)m_maxRowsComponent.getModel()).getIntValue());
        }
        if (m_generateImageComponent != null) {
            m_config.setGenerateImage(((SettingsModelBoolean)m_generateImageComponent.getModel()).getBooleanValue());
        }
        m_config.saveAdditionalSettings(settings);
        m_stringHistoryMap.forEach((id, historyKey) -> {
            StringHistory history = StringHistory.getInstance(historyKey);
            SettingsModel model = m_components.get(id).getModel();
            if (model instanceof SettingsModelString) {
                history.add(((SettingsModelString)model).getStringValue());
            } else if (model instanceof SettingsModelStringArray) {
                Arrays.stream(((SettingsModelStringArray)model).getStringArrayValue())
                    .forEachOrdered(value -> history.add(value));
            }
        });
        super.saveAdditionalSettingsTo(settings);
	}
}
