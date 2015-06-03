/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
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
 *   24.04.2015 (Christian Albrecht, KNIME.com AG, Zurich, Switzerland): created
 */
package org.knime.dynamic.js;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.xmlbeans.XmlObject;
import org.knime.core.data.DataValue;
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
import org.knime.core.node.workflow.FlowVariable;
import org.knime.core.node.workflow.FlowVariable.Type;
import org.knime.dynamicjsnode.v212.DynamicJSKnimeNode;
import org.knime.dynamicnode.v212.CheckBoxOption;
import org.knime.dynamicnode.v212.ColorOption;
import org.knime.dynamicnode.v212.ColumnFilterOption;
import org.knime.dynamicnode.v212.ColumnSelectorOption;
import org.knime.dynamicnode.v212.DateOption;
import org.knime.dynamicnode.v212.DynamicFullDescription;
import org.knime.dynamicnode.v212.DynamicOptions;
import org.knime.dynamicnode.v212.DynamicTab;
import org.knime.dynamicnode.v212.FileOption;
import org.knime.dynamicnode.v212.FlowVariableSelectorOption;
import org.knime.dynamicnode.v212.FlowVariableType;
import org.knime.dynamicnode.v212.FlowVariableType.Enum;
import org.knime.dynamicnode.v212.NumberOption;
import org.knime.dynamicnode.v212.RadioButtonOption;
import org.knime.dynamicnode.v212.StringListOption;
import org.knime.dynamicnode.v212.StringOption;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
public class DynamicJSNodeDialog extends DefaultNodeSettingsPane {

	private DynamicJSKnimeNode m_nodeConfig;
	private DynamicJSConfig m_config;
	private Map<String, DialogComponent> m_components = new HashMap<String, DialogComponent>();

	private DialogComponentBoolean m_hideInWizardComponent;
	private DialogComponentNumber m_maxRowsComponent;
	private DialogComponentBoolean m_generateImageComponent;

	private String m_firstTab = "Options";

	/**
	 * @param nodeConfig
	 */
	public DynamicJSNodeDialog(final DynamicJSKnimeNode nodeConfig) {
		m_nodeConfig = nodeConfig;
		m_config = new DynamicJSConfig(nodeConfig);
		DynamicFullDescription desc = m_nodeConfig.getFullDescription();
		if (desc.getOptions() != null) {
		    createAdditionalOptions();
			fillOptions(desc.getOptions());
		}
		if (desc.getTabList() != null && desc.getTabList().size() > 0) {
			removeTab(m_firstTab);
			m_firstTab = desc.getTabArray(0).getName();
		}
		for (DynamicTab tab : desc.getTabList()) {
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
			   DialogComponentButtonGroup rComp = new DialogComponentButtonGroup(model, rO.getAlignVertical(), rO.getLabel(), possibleValues);
			   rComp.setToolTipText(rO.getTooltip());
			   m_components.put(rO.getId(), rComp);
			   addDialogComponent(rComp);
			} else if (option instanceof StringListOption) {
			    StringListOption sO = (StringListOption)option;
			    DialogComponent sComp;
			    @SuppressWarnings("unchecked")
			    List<String> possibleValues = sO.getPossibleValues();
			    if (sO.getAllowMultipleSelection()) {
			        SettingsModelStringArray model = (SettingsModelStringArray)m_config.getModel(sO.getId());
			        int numRows = -1;
			        if (sO.isSetNumRowsVisible()) {
			            numRows = sO.getNumRowsVisible().intValue();
			        }
			        sComp = new DialogComponentStringListSelection(model, sO.getLabel(), possibleValues, !sO.getOptional(), numRows);
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
			} else if (option instanceof ColorOption) {
			    ColorOption cO = (ColorOption)option;
			    SettingsModelColor model = (SettingsModelColor)m_config.getModel(cO.getId());
			    DialogComponentColorChooser cComp = new DialogComponentColorChooser(model, cO.getLabel(), true);
			    cComp.setColor(new Color(cO.getDefaultR(), cO.getDefaultG(), cO.getDefaultB(), cO.getDefaultAlpha()));
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
			    SettingsModelColumnName model = (SettingsModelColumnName)m_config.getModel(cO.getId());
			    ColumnFilter filter = null;
			    if (cO.isSetFilterClasses()) {
			        try {
			            filter = new DataValueColumnFilter(getFilterClasses(cO.getFilterClasses()));
			        } catch (Exception e) {
			            throw new ClassCastException(e.getMessage());
			        }
			    }
                DialogComponentColumnNameSelection cComp =
                    new DialogComponentColumnNameSelection(model, cO.getLabel(), cO.getInPortIndex(), cO.getOptional(),
                        true, filter);
                cComp.setToolTipText(cO.getTooltip());
                m_components.put(cO.getId(), cComp);
                addDialogComponent(cComp);
			}
		}
	}

	private void createAdditionalOptions() {
	    createNewGroup("General Settings");

	    m_hideInWizardComponent = new DialogComponentBoolean(new SettingsModelBoolean(DynamicJSConfig.HIDE_IN_WIZARD_CONF + "model", DynamicJSConfig.DEFAULT_HIDE_IN_WIZARD), "Hide in Wizard");
	    addDialogComponent(m_hideInWizardComponent);

	    if (m_config.getHasSvgImageOutport()) {
	        m_generateImageComponent = new DialogComponentBoolean(new SettingsModelBoolean(DynamicJSConfig.GENERATE_IMAGE_CONF + "model", DynamicJSConfig.DEFAULT_GENERATE_IMAGE), "Generate image");
	        addDialogComponent(m_generateImageComponent);
	    }

	    if (m_config.getNumberDataInPorts() > 0) {
	        m_maxRowsComponent = new DialogComponentNumber(new SettingsModelIntegerBounded(DynamicJSConfig.MAX_ROWS_CONF + "model", DynamicJSConfig.DEFAULT_MAX_ROWS, 0, Integer.MAX_VALUE), "Maximum number of rows", 1);
	        addDialogComponent(m_maxRowsComponent);
	    }

	    closeCurrentGroup();
	}

	/**
     * @param filterClasses
     * @return
	 * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    private Class<? extends DataValue>[] getFilterClasses(final String filterClasses) throws ClassNotFoundException {
        String[] classes = filterClasses.split(",");
        Class<? extends DataValue>[] filters =  new Class[classes.length];
        int i = 0;
        for (String clazz : classes) {
            Class<? extends DataValue> c = (Class<? extends DataValue>)Class.forName(clazz);
            filters[i++] = c;
        }
        return filters;
    }

    private void setEnabled(final SettingsModel source, final SettingsModel dest, final String value) {
		if (value == null) {
            return;
        }
	    if (source instanceof SettingsModelBoolean) {
			SettingsModelBoolean bSource = (SettingsModelBoolean)source;
			dest.setEnabled(bSource.getBooleanValue() == Boolean.parseBoolean(value));
		} else if (source instanceof SettingsModelString) {
			SettingsModelString sSource = (SettingsModelString)source;
			dest.setEnabled(sSource.getStringValue().equals(value));
		} else if (source instanceof SettingsModelColumnName) {
		    SettingsModelColumnName cSource = (SettingsModelColumnName)source;
		    dest.setEnabled(value.equals(cSource.getColumnName()));
		}
	}

	@Override
	public void loadAdditionalSettingsFrom(final NodeSettingsRO settings,
			final PortObjectSpec[] specs) throws NotConfigurableException {
		m_config.loadAdditionalNodeSettingsInDialog(settings);
		super.loadAdditionalSettingsFrom(settings, specs);
        if (m_hideInWizardComponent != null) {
            ((SettingsModelBoolean)m_hideInWizardComponent.getModel()).setBooleanValue(m_config.getHideInWizard());
        }
        if (m_maxRowsComponent != null) {
            ((SettingsModelIntegerBounded)m_maxRowsComponent.getModel()).setIntValue(m_config.getMaxRows());
        }
        if (m_generateImageComponent != null) {
            ((SettingsModelBoolean)m_generateImageComponent.getModel()).setBooleanValue(m_config.getGenerateImage());
        }
		for (final Vector<String> dependency : m_config.getEnableDependencies()) {
			DialogComponent cFrom = m_components.get(dependency.get(0));
			final DialogComponent cTo = m_components.get(dependency.get(1));
			setEnabled(cFrom.getModel(), cTo.getModel(), dependency.get(2));
			cFrom.getModel().addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(final ChangeEvent e) {
					setEnabled((SettingsModel)e.getSource(), cTo.getModel(), dependency.get(2));
				}
			});
		}
	}

	@Override
	public void saveAdditionalSettingsTo(final NodeSettingsWO settings)
			throws InvalidSettingsException {
        if (m_hideInWizardComponent != null) {
            m_config.setHideInWizard(((SettingsModelBoolean)m_hideInWizardComponent.getModel()).getBooleanValue());
        }
        if (m_maxRowsComponent != null) {
            m_config.setMaxRows(((SettingsModelIntegerBounded)m_maxRowsComponent.getModel()).getIntValue());
        }
        if (m_generateImageComponent != null) {
            m_config.setGenerateImage(((SettingsModelBoolean)m_generateImageComponent.getModel()).getBooleanValue());
        }
        m_config.saveAdditionalSettings(settings);
        super.saveAdditionalSettingsTo(settings);
	}
}
