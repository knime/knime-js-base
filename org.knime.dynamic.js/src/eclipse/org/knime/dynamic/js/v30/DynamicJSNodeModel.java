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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.XmlObject;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.knime.base.data.xml.SvgCell;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.container.AbstractCellFactory;
import org.knime.core.data.container.CellFactory;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.data.date.DateAndTimeCell;
import org.knime.core.data.def.BooleanCell;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModel;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelColor;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnFilter2;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnName;
import org.knime.core.node.defaultnodesettings.SettingsModelDate;
import org.knime.core.node.defaultnodesettings.SettingsModelDouble;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.defaultnodesettings.SettingsModelStringArray;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.database.DatabasePortObject;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.port.flowvariable.FlowVariablePortObjectSpec;
import org.knime.core.node.port.image.ImagePortObject;
import org.knime.core.node.port.image.ImagePortObjectSpec;
import org.knime.core.node.port.inactive.InactiveBranchPortObject;
import org.knime.core.node.port.inactive.InactiveBranchPortObjectSpec;
import org.knime.core.node.port.viewproperty.ColorHandlerPortObject;
import org.knime.core.node.util.filter.NameFilterConfiguration.FilterResult;
import org.knime.core.node.web.ValidationError;
import org.knime.core.node.workflow.FlowVariable;
import org.knime.dynamic.js.DynamicJSDependency;
import org.knime.dynamic.js.SettingsModelSVGOptions;
import org.knime.dynamic.js.SettingsModelSVGOptions.JSONSVGOptions;
import org.knime.dynamicjsnode.v30.DynamicJSKnimeNode;
import org.knime.dynamicjsnode.v30.WebDependency;
import org.knime.dynamicjsnode.v30.WebResource;
import org.knime.dynamicjsnode.v30.WebRessources;
import org.knime.dynamicnode.v30.ColorFormat;
import org.knime.dynamicnode.v30.ColorOption;
import org.knime.dynamicnode.v30.ColumnFilterOption;
import org.knime.dynamicnode.v30.ColumnType;
import org.knime.dynamicnode.v30.DataOutOption;
import org.knime.dynamicnode.v30.DataOutputType;
import org.knime.dynamicnode.v30.DynamicInPort;
import org.knime.dynamicnode.v30.DynamicOption;
import org.knime.dynamicnode.v30.DynamicOptions;
import org.knime.dynamicnode.v30.DynamicOutPort;
import org.knime.dynamicnode.v30.DynamicPorts;
import org.knime.dynamicnode.v30.DynamicTab;
import org.knime.dynamicnode.v30.FlowVariableOutOption;
import org.knime.dynamicnode.v30.FlowVariableType;
import org.knime.dynamicnode.v30.PortType.Enum;
import org.knime.js.core.CSSUtils;
import org.knime.js.core.JSONDataTable;
import org.knime.js.core.color.JSONColorModel;
import org.knime.js.core.components.datetime.SettingsModelDateTimeOptions;
import org.knime.js.core.layout.LayoutTemplateProvider;
import org.knime.js.core.layout.bs.JSONLayoutViewContent;
import org.knime.js.core.layout.bs.JSONLayoutViewContent.ResizeMethod;
import org.knime.js.core.node.AbstractSVGWizardNodeModel;
import org.knime.js.core.node.CSSModifiable;

/**
 *
 * @author Christian Albrecht, KNIME AG, Zurich, Switzerland
 * @since 3.0
 */
public class DynamicJSNodeModel extends AbstractSVGWizardNodeModel<DynamicJSViewRepresentation,
        DynamicJSViewValue> implements LayoutTemplateProvider, CSSModifiable {

	private static final NodeLogger LOGGER = NodeLogger.getLogger(DynamicJSNodeModel.class);

	private DynamicJSKnimeNode m_node;
	private DynamicJSConfig m_config;
	private final String m_rootPath;
	private final DynamicJSProcessor m_processor;

	/**
	 * @param nodeConfig
	 * @param configRootPath
	 * @param viewName
	 */
	protected DynamicJSNodeModel(final DynamicJSKnimeNode nodeConfig, final String configRootPath,final String viewName) {
		super(getPortTypeArray(nodeConfig, true), getPortTypeArray(nodeConfig, false), viewName);
		m_node = nodeConfig;
		m_config = new DynamicJSConfig(nodeConfig);
		m_rootPath = configRootPath;
		m_processor = initProcessor();
	}

	/**
     * @return
     */
    private DynamicJSProcessor initProcessor() {
        DynamicJSProcessor processor = null;
        if (m_node.isSetJavaProcessor()) {
            String className = m_node.getJavaProcessor().getClassName();
            try {
                Class<?> processorClass = Class.forName(className);
                Object pO = processorClass.newInstance();
                if (!(pO instanceof DynamicJSProcessor)) {
                    throw new IllegalArgumentException("Processor class " + className
                        + " must implement the DynamicJSProcessor interface.");
                }
                processor = (DynamicJSProcessor)pO;
            } catch (Exception e) {
                LOGGER.error("Cannot instantiate java processor class " + className + " - " + e.getMessage(), e);
            }
        }
        return processor;
    }

    private static PortType[] getPortTypeArray(final DynamicJSKnimeNode nodeConfig, final boolean getInPorts) {
		DynamicPorts ports = nodeConfig.getPorts();
		if (getInPorts) {
			List<PortType> inPorts = new ArrayList<PortType>();
			for (DynamicInPort port : ports.getInPortList()) {
				inPorts.add(getPortType(port.getPortType(), port.getOptional()));
			}
			return inPorts.toArray(new PortType[0]);
		} else {
			List<PortType> outPorts = new ArrayList<PortType>();
			for (DynamicOutPort port : ports.getOutPortList()) {
				outPorts.add(getPortType(port.getPortType(), false));
			}
			return outPorts.toArray(new PortType[0]);
		}
	}

	private static PortType getPortType(final org.knime.dynamicnode.v30.PortType.Enum portType, final boolean optional) {
		if (portType.equals(org.knime.dynamicnode.v30.PortType.DATA)) {
			return optional ? BufferedDataTable.TYPE_OPTIONAL : BufferedDataTable.TYPE;
		}
		if (portType.equals(org.knime.dynamicnode.v30.PortType.FLOW_VARIABLE)) {
			return optional ? FlowVariablePortObject.TYPE_OPTIONAL : FlowVariablePortObject.TYPE;
		}
		if (portType.equals(org.knime.dynamicnode.v30.PortType.IMAGE)) {
			return optional ? ImagePortObject.TYPE_OPTIONAL : ImagePortObject.TYPE;
		}
		if (portType.equals(org.knime.dynamicnode.v30.PortType.DATABASE)) {
			return optional ? DatabasePortObject.TYPE_OPTIONAL : DatabasePortObject.TYPE;
		}
		if (portType.equals(org.knime.dynamicnode.v30.PortType.COLOR)) {
		    return optional ? PortObject.TYPE_OPTIONAL : ColorHandlerPortObject.TYPE;
		}
		return null;
	}

	private PortObjectSpec getPortSpec(final org.knime.dynamicnode.v30.PortType.Enum portType, final PortObjectSpec[] inSpecs, final int portIndex) {
		if (portType.equals(org.knime.dynamicnode.v30.PortType.DATA)) {
		    List<DataOutOption> optionList = new ArrayList<DataOutOption>();
		    Integer inSpecIndex = null;
		    boolean portIndexIsAppendColumn = false;
		    DataOutOption newTableOption = null;
            if (m_node.getOutputOptions() != null) {
                for (DataOutOption option : m_node.getOutputOptions().getDataOutputOptionList()) {
                    if (option.getOutPortIndex() == portIndex) {
                        org.knime.dynamicnode.v30.DataOutputType.Enum oType = option.getOutputType();
                        if (DataOutputType.APPEND_COLUMN.equals(oType)) {
                            // column appended
                            if (!option.isSetInPortIndex()) {
                                throw new IllegalArgumentException("Output option " + option.getId() + " was defined as APPEND_COLUMN but no inport index defined.");
                            }
                            if (newTableOption != null) {
                                throw new IllegalArgumentException("Multiple data output options for whole tables defined for output port " + portIndex);
                            }
                            if (inSpecIndex == null) {
                                inSpecIndex = option.getInPortIndex();
                                if (!(inSpecs[inSpecIndex] instanceof DataTableSpec)) {
                                    throw new IllegalArgumentException(
                                        "In port index spec for additional column definition should be of type DataTableSpec but was "
                                                + inSpecs[inSpecIndex].getClass().getSimpleName());
                                }
                            }
                            if (!inSpecIndex.equals(option.getInPortIndex())) {
                                throw new IllegalArgumentException("Several additional columns for data out port "
                                        + portIndex + " were defined but in port indices do not match (only one allowed).");
                            }
                            portIndexIsAppendColumn = true;
                            optionList.add(option);
                        } else {
                            // new or edited table
                            if (newTableOption != null || portIndexIsAppendColumn) {
                                throw new IllegalArgumentException("Multiple data output options for whole tables defined for output port " + portIndex);
                            }
                            newTableOption = option;
                            if (DataOutputType.EMPTY_WITH_SPEC.equals(oType) || DataOutputType.INPUT_TABLE.equals(oType)) {
                                if (!option.isSetInPortIndex()) {
                                    throw new IllegalArgumentException("Output option " + option.getId() + " does not have inport index defined.");
                                }
                                inSpecIndex = option.getInPortIndex();
                                if (!(inSpecs[inSpecIndex] instanceof DataTableSpec)) {
                                    throw new IllegalArgumentException(
                                        "In port index spec for output table needs to be DataTableSpec but was "
                                                + inSpecs[inSpecIndex].getClass().getSimpleName());
                                }
                            }
                        }
                    }
                }
            }
		    if (optionList.size() > 0 && inSpecIndex != null) {
		        // create appended column spec
		        ColumnRearranger rearranger = createColumnAppender((DataTableSpec)inSpecs[inSpecIndex], optionList, null);
		        return rearranger.createSpec();
		    }
		    if (newTableOption != null) {
		        // create/retrieve spec for new/edited table
		        org.knime.dynamicnode.v30.DataOutputType.Enum oType = newTableOption.getOutputType();
		        if (DataOutputType.EMPTY_TABLE.equals(oType)) {
		            return new DataTableSpec();
		        } else if (DataOutputType.EMPTY_WITH_SPEC.equals(oType) || DataOutputType.INPUT_TABLE.equals(oType)) {
		            return inSpecs[newTableOption.getInPortIndex()];
		        }
            }
		    // Otherwise spec is unknown at this point
		    return null;
		}
		if (portType.equals(org.knime.dynamicnode.v30.PortType.FLOW_VARIABLE)) {
			return FlowVariablePortObjectSpec.INSTANCE;
		}
		if (portType.equals(org.knime.dynamicnode.v30.PortType.IMAGE)) {
		    if (generateImage()) {
	            return new ImagePortObjectSpec(SvgCell.TYPE);
	        } else {
	            return InactiveBranchPortObjectSpec.INSTANCE;
	        }
		}
		if (portType.equals(org.knime.dynamicnode.v30.PortType.COLOR)) {
		    //TODO: create output option and fill with default settings for color output port
		}
		return null;
	}

	private PortObject getPortObject(final int outPortIndex, final PortObject[] inObjects, final ExecutionContext exec) throws CanceledExecutionException {
	    DynamicOutPort port = m_node.getPorts().getOutPortList().get(outPortIndex);
	    Enum portType = port.getPortType();
		if (portType.equals(org.knime.dynamicnode.v30.PortType.DATA)) {
		    List<DataOutOption> optionList = new ArrayList<DataOutOption>();
		    DataOutOption newTableOption = null;
		    Integer inSpecIndex = null;
            if (m_node.getOutputOptions() != null) {
                for (DataOutOption option : m_node.getOutputOptions().getDataOutputOptionList()) {
                    if (option.getOutPortIndex() == outPortIndex) {
                        if (DataOutputType.APPEND_COLUMN.equals(option.getOutputType())) {
                            optionList.add(option);
                            inSpecIndex = option.getInPortIndex();
                        } else {
                            if (newTableOption != null) {
                                LOGGER.error("Multiple dataOutOptions creating new tables found for out port "
                                    + outPortIndex + ".");
                                return InactiveBranchPortObject.INSTANCE;
                            }
                            newTableOption = option;
                        }
                    }
                }
            }
			if (optionList.size() == 0 && newTableOption == null) {
			    LOGGER.error("No corresponding dataOutOption found for data out port " + outPortIndex + ".");
			    return InactiveBranchPortObject.INSTANCE;
			}
			if (optionList.size() > 0 && newTableOption != null) {
			    LOGGER.error("Multiple dataOutOptions creating new tables and appending columns found for out port " + outPortIndex + ".");
			    return InactiveBranchPortObject.INSTANCE;
			}
			if (optionList.size() > 0 && inSpecIndex != null) {
			    List<Map<String, Object>> values = new ArrayList<Map<String,Object>>();
			    for (DataOutOption option : optionList) {
			        Map<String, Object> map = getViewValue().getOutColumns().get(option.getId());
			        if (map == null) {
			            map = new HashMap<String, Object>();
			        }
			        values.add(map);
			    }
			    ColumnRearranger rearranger = createColumnAppender((DataTableSpec)inObjects[inSpecIndex].getSpec(), optionList, values);
                return exec.createColumnRearrangeTable((BufferedDataTable)inObjects[inSpecIndex], rearranger, exec);
			}
			if (newTableOption != null) {
			    JSONDataTable table = getViewValue().getTables().get(newTableOption.getId());
			    if (table != null) {
			        //table is set on value already
			        return table.createBufferedDataTable(exec);
			    } else {
			        if (DataOutputType.INPUT_TABLE.equals(newTableOption.getOutputType())) {
			            return inObjects[newTableOption.getInPortIndex()];
			        }
			        DataTableSpec outSpec = new DataTableSpec();
			        if (DataOutputType.EMPTY_WITH_SPEC.equals(newTableOption.getOutputType())) {
			            outSpec = (DataTableSpec)inObjects[newTableOption.getInPortIndex()].getSpec();
			        }
			        BufferedDataContainer cont = exec.createDataContainer(outSpec, true);
			        cont.close();
			        return cont.getTable();
			    }
			    //TODO detect if table was expected on value after re-execute?
			    /*LOGGER.error("Table with id " + newTableOption.getId() + " expected but not found. Possible implementation error.");
			    return InactiveBranchPortObject.INSTANCE;*/
			}
		}
		if (portType.equals(org.knime.dynamicnode.v30.PortType.FLOW_VARIABLE)) {
			return FlowVariablePortObject.INSTANCE;
		}
		if (portType.equals(org.knime.dynamicnode.v30.PortType.COLOR)) {
		    // TODO: create color port object for output port, currently will be inactive and not supported
		}
		LOGGER.warn("Port object of type " + portType + " not supported.");
		return InactiveBranchPortObject.INSTANCE;
	}

	@Override
	public DynamicJSViewRepresentation createEmptyViewRepresentation() {
		return new DynamicJSViewRepresentation();
	}

	@Override
	public DynamicJSViewValue createEmptyViewValue() {
		return new DynamicJSViewValue();
	}

	@Override
	public String getJavascriptObjectID() {
		return "org.knime.dynamic.js";
	}

	@Override
	public boolean isHideInWizard() {
		return m_config.getHideInWizard();
	}

	/**
	 * {@inheritDoc}
	 * @since 3.5
	 */
	@Override
	public void setHideInWizard(final boolean hide) {
	    m_config.setHideInWizard(hide);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getCssStyles() {
	    return m_config.getCustomCSS();
	}

	@Override
	public ValidationError validateViewValue(final DynamicJSViewValue value) {
		//TODO: validate options, flow variables, tables and maps
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void loadViewValue(final DynamicJSViewValue value, final boolean useAsDefault) {
	    // TODO make sure maps of validated view value are converted to correct types before calling super method
	    super.loadViewValue(value, useAsDefault);
	}

	@Override
    protected void useCurrentValueAsDefault() {
        setOptionsOnConfig();
    }

	@Override
	public void saveCurrentValue(final NodeSettingsWO settings) {
		getViewValue().saveToNodeSettings(settings);
	}

	@Override
	protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs)
			throws InvalidSettingsException {
	    pushFlowVariables();
		List<DynamicOutPort> ports = m_node.getPorts().getOutPortList();
		PortObjectSpec[] specs = new PortObjectSpec[ports.size()];
		for (int i = 0; i < ports.size(); i++) {
			specs[i] = getPortSpec(ports.get(i).getPortType(), inSpecs, i);
		}
		return specs;
	}

	private ColumnRearranger createColumnAppender(final DataTableSpec spec, final List<DataOutOption> options, final List<Map<String, Object>> values) {
	    if (values != null && options.size() != values.size()) {
	        throw new IllegalArgumentException("Data out options defined in node description do not match actual values. Possible implementation error.");
	    }
	    List<DataColumnSpec> outSpecs = new ArrayList<DataColumnSpec>();
        for (DataOutOption option : options) {
            String newColName = option.getAdditionalColumnName();
            if (newColName == null || newColName.isEmpty()) {
                newColName = String.format(DynamicJSConfig.DEFAULT_APPENDED_COLUMN_NAME, m_node.getName(), option.getOutPortIndex());
            }
            newColName = DataTableSpec.getUniqueColumnName(spec, newColName);
            Class<? extends DataCell> colClass = null;
            if (option.getAdditionalColumnType().equals(ColumnType.BOOLEAN)) {
                colClass = BooleanCell.class;
            } else if (option.getAdditionalColumnType().equals(ColumnType.DOUBLE)) {
                colClass = DoubleCell.class;
            } else if (option.getAdditionalColumnType().equals(ColumnType.INTEGER)) {
                colClass = IntCell.class;
            } else if (option.getAdditionalColumnType().equals(ColumnType.DATETIME)) {
                colClass = DateAndTimeCell.class;
            } else {
                colClass = StringCell.class;
            }
            outSpecs.add(new DataColumnSpecCreator(newColName, DataType.getType(colClass)).createSpec());
        }
        ColumnRearranger rearranger = new ColumnRearranger(spec);
        CellFactory fac = new AbstractCellFactory(outSpecs.toArray(new DataColumnSpec[0])) {

            @Override
            public DataCell[] getCells(final DataRow row) {
                DataCell[] cells = new DataCell[values.size()];
                for (int i = 0; i < values.size(); i++) {
                    Map<String, Object> valueMap = values.get(i);
                    if (!valueMap.containsKey(row.getKey().getString())) {
                        cells[i] = DataType.getMissingCell();
                        break;
                    }
                    Object value = valueMap.get(row.getKey().getString());
                    DataOutOption option = options.get(i);
                    if (option.getAdditionalColumnType().equals(ColumnType.BOOLEAN)) {
                        cells[i] = BooleanCell.get((boolean)value);
                    } else if (option.getAdditionalColumnType().equals(ColumnType.DOUBLE)) {
                        cells[i] = new DoubleCell((double)value);
                    } else if (option.getAdditionalColumnType().equals(ColumnType.INTEGER)) {
                        cells[i] = new IntCell((int)value);
                    } else if (option.getAdditionalColumnType().equals(ColumnType.DATETIME)) {
                        try {
                            cells[i] = DateAndTimeCell.fromString((String)value);
                        } catch (ParseException e) {
                            LOGGER.error("Parsing value as date time failed: " + e.getMessage());
                            cells[i] = DataType.getMissingCell();
                        }
                    } else {
                        cells[i] = new StringCell((String)value);
                    }
                }
                return cells;
            }
        };
        rearranger.append(fac);
        return rearranger;
    }

	@Override
	protected void performExecuteCreateView(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
		synchronized (getLock()) {
			DynamicJSViewRepresentation viewRepresentation = getViewRepresentation();
            if (viewRepresentation.isNew()) {
                try {
                    // try pre-processing input
                    Object[] processedInputs = inObjects;
                    if (m_processor != null) {
                        processedInputs = m_processor.processInputObjects(inObjects, exec, m_config);
                        if (m_processor instanceof DynamicStatefulJSProcessor) {
                        String warnMessage = ((DynamicStatefulJSProcessor)m_processor).getWarningMessage();
                        if (warnMessage != null) {
                            viewRepresentation.setWarnMessage(warnMessage);
                            setWarningMessage(warnMessage);
                        }
                    }
                    }

                    List<Object> viewInObjects = new ArrayList<Object>();
                    String[] tableIdsForProcessed = new String[processedInputs.length];
                    double remainingProgress = 1d - exec.getProgressMonitor().getProgress();
                    double subProgress = remainingProgress / inObjects.length;
                    for (int i = 0; i < processedInputs.length; i++) {
                        Object processedObject = processedInputs[i];
                        String tableId = getTableId(i);
                        // unprocessed inObjects
                        if (processedObject instanceof PortObject) {
                            // only data in ports supported atm
                            if (processedObject instanceof BufferedDataTable) {
                                viewInObjects.add(createJSONTableFromBufferedDataTable(
                                    exec.createSubExecutionContext(subProgress),
                                    (BufferedDataTable)processedObject, tableId));
                            } else if (processedObject instanceof ColorHandlerPortObject) {
                                DataTableSpec colorTableSpec = ((ColorHandlerPortObject)processedObject).getSpec();
                                if (colorTableSpec.getNumColumns() == 1 && colorTableSpec.getColumnSpec(0).getColorHandler() != null) {
                                    viewInObjects.add(JSONColorModel.createFromColorModel(colorTableSpec.getColumnSpec(0).getColorHandler().getColorModel()));
                                } else {
                                    viewInObjects.add(null);
                                }
                            } else {
                                // add null for all other unprocessed in port types
                                viewInObjects.add(null);
                                exec.setProgress(exec.getProgressMonitor().getProgress() + subProgress);
                            }
                        } else {
                            // processed inObjects, assume they are directly serializable to JSON
                            viewInObjects.add(processedObject);
                            if (inObjects[i] instanceof BufferedDataTable) {
                                tableIdsForProcessed[i] = tableId;
                            }
                        }
                    }
                    viewRepresentation.setInObjects(viewInObjects.toArray(new Object[0]));
                    viewRepresentation.setTableIds(tableIdsForProcessed);

                    Map<String, String> vStringMap = new HashMap<String, String>();
                    for (Entry<String, FlowVariable> vEntry : getAvailableFlowVariables().entrySet()) {
                        vStringMap.put(vEntry.getKey(), vEntry.getValue().getValueAsString());
                    }
                    viewRepresentation.setFlowVariables(vStringMap);

                    readResourceContents();
                    viewRepresentation.setJsNamespace(m_node.getJsNamespace());
                    List<DynamicJSDependency> dependencies = setPathsFromLibNames(getDependencies(true));
                    dependencies.addAll(getDependencies(false));
                    viewRepresentation.setJsDependencies(dependencies.toArray(new DynamicJSDependency[0]));
                    setOptionsOnViewContent(inObjects);
                } catch (InvalidSettingsException e) {
                    //don't fail on invalid settings thrown from processor, but show an error message in the view and image
                    viewRepresentation.setErrorMessage(e.getMessage());
                    setWarningMessage(e.getMessage());
                }
                viewRepresentation.setInitialized();
            }
            viewRepresentation.setRunningInView(false);
            setOptionalViewWaitTime((long)m_config.getAdditionalWait());
        }
	}

    /**
	 * {@inheritDoc}
	 */
	@Override
	protected PortObject[] performExecuteCreatePortObjects(final PortObject svgImageFromView, final PortObject[] inObjects, final ExecutionContext exec)
	        throws Exception {
	    pushFlowVariables();
	    List<DynamicOutPort> ports = m_node.getPorts().getOutPortList();
        PortObject[] pOArray = new PortObject[ports.size()];
        for (int i = 0; i < ports.size(); i++) {
            if (ports.get(i).getPortType().equals(org.knime.dynamicnode.v30.PortType.IMAGE)) {
                pOArray[i] = svgImageFromView;
            } else {
                pOArray[i] = getPortObject(i, inObjects, exec);
            }
        }
        getViewRepresentation().setRunningInView(true);
        return pOArray;
	}

    private void pushFlowVariables() {
        if (m_node.getOutputOptions() == null) {
            return;
        }
        for (FlowVariableOutOption option : m_node.getOutputOptions().getFlowVariableOutputOptionList()) {
            String var = null;
            if (getViewValue() != null) {
                var = getViewValue().getFlowVariables().get(option.getId());
            }
            if (var == null && option.isSetDefaultFromOptions()) {
                SettingsModel model = m_config.getModel(option.getDefaultFromOptions());
                if (model instanceof SettingsModelInteger) {
                    var = Integer.toString(((SettingsModelInteger)model).getIntValue());
                } else if (model instanceof SettingsModelDouble) {
                    var = Double.toString(((SettingsModelDouble)model).getDoubleValue());
                } else if (model instanceof SettingsModelString) {
                    var = ((SettingsModelString)model).getStringValue();
                } else {
                    LOGGER.warn("Assigning default value to flow variable from option "
                        + option.getDefaultFromOptions() + " not possible. Type not supported.");
                }
            }
            if (var == null && option.isSetDefaultValue()) {
                var = option.getDefaultValue();
            }
            if (option.getVariableType().equals(FlowVariableType.INTEGER)) {
                Integer out = null;
                if (var == null) {
                    out = 0;
                } else {
                    try {
                        out = Integer.parseInt(var);
                    } catch (Exception e) {
                        throw new IllegalArgumentException(
                            "Value could not be parsed as integer when creating flow variable. " + e.getMessage());
                    }
                }
                if (out != null) {
                    pushFlowVariableInt(option.getVariableName(), out.intValue());
                }
            } else if (option.getVariableType().equals(FlowVariableType.DOUBLE)) {
                Double out = null;
                if (var == null) {
                    out = 0d;
                } else {
                    try {
                        out = Double.parseDouble(var);
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Value could not be parsed as double when creating flow variable. "
                                + e.getMessage());
                    }
                }
                if (out != null) {
                    pushFlowVariableDouble(option.getVariableName(), out.doubleValue());
                }
            } else {
                String out = var == null ? "" : var;
                pushFlowVariableString(option.getVariableName(), out);
            }
        }
    }

	private void setOptionsOnViewContent(final PortObject[] inObjects) {
	    Map<String, Object> repOptions = getViewRepresentation().getOptions();
	    Map<String, Object> valueOptions = getViewValue().getOptions();
		for (Entry<String, SettingsModel> entry : m_config.getModels().entrySet()) {
		    SettingsModel model = entry.getValue();
		    DynamicOption option = getOptionForId(entry.getKey());
		    if (option == null) {
		        continue;
		    }
		    Object value = null;
			if (model instanceof SettingsModelBoolean) {
				value = ((SettingsModelBoolean)model).getBooleanValue();
			} else  if (model instanceof SettingsModelInteger) {
			    value = ((SettingsModelInteger)model).getIntValue();
			} else if (model instanceof SettingsModelDouble) {
			    value = ((SettingsModelDouble)model).getDoubleValue();
			} else if (model instanceof SettingsModelDate) {
			    value = ((SettingsModelDate)model).getTimeInMillis();
			} else if (model instanceof SettingsModelDateTimeOptions) {
			    value = ((SettingsModelDateTimeOptions)model).getJSONSerializableObject();
			} else if (model instanceof SettingsModelColor) {
			    Color color = ((SettingsModelColor)model).getColorValue();
			    org.knime.dynamicnode.v30.ColorFormat.Enum colorFormat = ((ColorOption)option).getFormat();
			    if (colorFormat.equals(ColorFormat.HEX_STRING)) {
			        value = CSSUtils.cssHexStringFromColor(color);
			    } else if (colorFormat.equals(ColorFormat.RGBA_STRING)) {
			        value = CSSUtils.rgbaStringFromColor(color);
			    }
			} else if (model instanceof SettingsModelColumnFilter2) {
				SettingsModelColumnFilter2 cM = (SettingsModelColumnFilter2)model;
				ColumnFilterOption cf = (ColumnFilterOption)option;
				FilterResult filter = cM.applyTo((DataTableSpec)inObjects[cf.getInPortIndex()].getSpec());
				value = filter.getIncludes();
			} else if (model instanceof SettingsModelColumnName) {
			    value = ((SettingsModelColumnName)model).getColumnName();
			} else if (model instanceof SettingsModelString) {
                // This covers various components (String, FlowVariableSelection, FileInput, etc.)
                value = ((SettingsModelString)model).getStringValue();
			} else if (model instanceof SettingsModelStringArray) {
			    value = ((SettingsModelStringArray)model).getStringArrayValue();
			} else if (model instanceof SettingsModelSVGOptions) {
			    value = ((SettingsModelSVGOptions)model).getJSONSerializableObject();
			}
			if (value != null) {
			    if (option.getSaveInView()) {
			        // Don't overwrite options in view value if already set!
			        if (!valueOptions.containsKey(entry.getKey())) {
			            valueOptions.put(entry.getKey(), value);
			        }
			    } else {
			        repOptions.put(entry.getKey(), value);
			    }
			}
		}
	}

	private void setOptionsOnConfig() {
	    for (Entry<String, Object> entry : getViewValue().getOptions().entrySet()) {
            SettingsModel model = m_config.getModel(entry.getKey());
            if (model == null) {
                LOGGER.warn("SettingsModel for option " + entry.getKey() + " not found. Ignoring option.");
                continue;
            }
            DynamicOption option = getOptionForId(entry.getKey());
            if (option == null) {
                LOGGER.warn("No option in node description defined for " + entry.getKey() + ". Ignoring option.");
                continue;
            }
	        if (model instanceof SettingsModelBoolean) {
	            ((SettingsModelBoolean)model).setBooleanValue((boolean)entry.getValue());
	        } else if (model instanceof SettingsModelInteger) {
	            ((SettingsModelInteger)model).setIntValue((int)entry.getValue());
	        } else if (model instanceof SettingsModelDouble) {
	            ((SettingsModelDouble)model).setDoubleValue(((Number)entry.getValue()).doubleValue());
	        } else if (model instanceof SettingsModelDate) {
	            ((SettingsModelDate)model).setTimeInMillis((long)entry.getValue());
	        } else if (model instanceof SettingsModelColor) {
	            org.knime.dynamicnode.v30.ColorFormat.Enum colorFormat = ((ColorOption)option).getFormat();
	            Color color = null;
                try {
                    if (colorFormat.equals(ColorFormat.HEX_STRING)) {
                        color = CSSUtils.colorFromCssHexString((String)entry.getValue());
                    } else if (colorFormat.equals(ColorFormat.RGBA_STRING)) {
                        color = CSSUtils.colorFromRgbaString((String)entry.getValue());
                    }
                } catch (Exception e) {}
                if (color != null) {
                    ((SettingsModelColor)model).setColorValue(color);
                } else {
                    LOGGER.error("Could not parse color string " + entry.getValue()
                        + " Possible implementation error.");
                }
	        } else if (model instanceof SettingsModelColumnName) {
	            ((SettingsModelColumnName)model).setSelection((String)entry.getValue(), false);
	        } else if (model instanceof SettingsModelColumnFilter2) {
	            LOGGER.warn("Using columnFilterOption as new default is not supported");
	        } else if (model instanceof SettingsModelString) {
	            // This covers various components (String, FlowVariableSelection, FileInput)
	            ((SettingsModelString)model).setStringValue((String)entry.getValue());
	        } else if (model instanceof SettingsModelStringArray) {
	            ((SettingsModelStringArray)model).setStringArrayValue((String[])entry.getValue());
	        } else if (model instanceof SettingsModelSVGOptions) {
	            ((SettingsModelSVGOptions)model).setFromJSON((JSONSVGOptions)entry.getValue());
	        }
	    }
	}

    private DynamicOption getOptionForId(final String key) {
        DynamicOption option = null;
        if (m_node.getFullDescription().getOptions() != null) {
            option = getOptionForId(key, m_node.getFullDescription().getOptions());
            if (option != null) {
                return option;
            }
        }
        for (DynamicTab tab : m_node.getFullDescription().getTabList()) {
            option = getOptionForId(key, tab.getOptions());
            if (option != null) {
                return option;
            }
        }
        return null;
    }

    private DynamicOption getOptionForId(final String key, final DynamicOptions options) {
        XmlObject[] oOptions = options.selectPath("$this/*");
        for (XmlObject option : oOptions) {
            if (option instanceof DynamicOption) {
                if (key.equals(((DynamicOption)option).getId())) {
                    return (DynamicOption)option;
                }
            }
        }
        return null;
    }

    private void readResourceContents() {
		WebRessources resources = m_node.getResources();
		List<String> jsCode = new ArrayList<String>();
		List<String> cssCode = new ArrayList<String>();
		Map<String, String> binaryFiles = new HashMap<String, String>();
		if (resources != null) {
			for (WebResource res : resources.getResourceList()) {
				if (res.getType().equals(WebResource.Type.JS)) {
					jsCode.add(fileToString(res.getPath(), false));
				} else if (res.getType().equals(WebResource.Type.CSS)) {
					cssCode.add(fileToString(res.getPath(), false));
				} else if (res.getType().equals(WebResource.Type.FILE)) {
					binaryFiles.put(res.getPath(), fileToString(res.getPath(), true));
				}
			}
		}
		DynamicJSViewRepresentation representation = getViewRepresentation();
		representation.setJsCode(jsCode.toArray(new String[0]));
		representation.setCssCode(cssCode.toArray(new String[0]));
		representation.setBinaryFiles(binaryFiles);
	}

	private String fileToString(final String path, final boolean encodeBase64) {
		File rootFile = new File(m_rootPath);
		if (!rootFile.exists() || !rootFile.isDirectory()) {
			return null;
		}
		File file = new File(rootFile, path);
		if (!file.exists() || !file.isFile()) {
			LOGGER.error("Specified resource file " + file + " does not exist!");
			return null;
		}
		try {
			byte[] fileBytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
			if (encodeBase64) {
				return Base64.encodeBase64String(fileBytes);
			} else {
				return new String(fileBytes);
			}
		} catch (IOException e) {
			LOGGER.error("Reading file " + file + " failed: " + e.getMessage(), e);
		}
		return null;
	}

	private List<DynamicJSDependency> getDependencies(final boolean local) {
		List<DynamicJSDependency> deps = new ArrayList<DynamicJSDependency>();
		if (m_node.getDependencies() != null) {
			for (WebDependency dep : m_node.getDependencies().getDependencyList()) {
			    DynamicJSDependency jsDep = new DynamicJSDependency();
			    jsDep.setName(dep.getName());
			    jsDep.setPath(dep.getPath());
			    jsDep.setUsesDefine(dep.getUsesDefine());
			    jsDep.setExports(dep.getExports());
			    @SuppressWarnings("unchecked")
                List<String> recDeps = dep.getDependsOn();
			    if (recDeps != null && recDeps.size() > 0) {
			        jsDep.setDependencies(recDeps.toArray(new String[0]));
			    }
			    jsDep.setLocal(local);
				if (local && dep.getType().equals(WebDependency.Type.LOCAL)) {
					deps.add(jsDep);
				} else if (!local && dep.getType().equals(WebDependency.Type.URL)) {
					deps.add(jsDep);
				}
			}
		}
		return deps;
	}

	private JSONDataTable createJSONTableFromBufferedDataTable(final ExecutionContext exec, final BufferedDataTable inTable, final String tableId) throws CanceledExecutionException {
        JSONDataTable table = JSONDataTable.newBuilder()
                .setDataTable(inTable)
                .setId(tableId)
                .setFirstRow(1)
                .setMaxRows(m_config.getMaxRows())
                .build(exec);
        if (m_config.getMaxRows() < inTable.size()) {
            String warning = "Only the first "
                    + m_config.getMaxRows() + " rows are displayed.";
            setWarningMessage(warning);
            DynamicJSViewRepresentation rep = getViewRepresentation();
            if (rep != null) {
                if (StringUtils.isNotEmpty(rep.getWarnMessage())) {
                    warning = rep.getWarnMessage() + "\n" + warning;
                }
                rep.setWarnMessage(warning);
            }
        }
        return table;
    }

	private static final String ID_WEB_RES = "org.knime.js.core.webResources";
    private static final String ATTR_RES_BUNDLE_ID = "webResourceBundleID";
    private static final String ID_IMPORT_RES = "importResource";
    private static final String ID_DEPENDENCY = "webDependency";
    private static final String ATTR_PATH = "relativePath";
    private static final String ATTR_TYPE = "type";

    private List<DynamicJSDependency> setPathsFromLibNames(final List<DynamicJSDependency> libDeps) {
        ArrayList<DynamicJSDependency> jsDependencies = new ArrayList<DynamicJSDependency>();
        ArrayList<String> cssPaths = new ArrayList<String>();
        for (DynamicJSDependency lib : libDeps) {
            IConfigurationElement confElement = getConfigurationFromWebResID(lib.getPath());
            if (confElement != null) {
                for (IConfigurationElement resElement : confElement.getChildren(ID_IMPORT_RES)) {
                    String path = resElement.getAttribute(ATTR_PATH);
                    String type = resElement.getAttribute(ATTR_TYPE);
                    if (path != null && type != null) {
                        if (type.equalsIgnoreCase("javascript")) {
                            lib.setPath(path);
                            jsDependencies.add(lib);
                        } else if (type.equalsIgnoreCase("css")) {
                            cssPaths.add(path);
                        }
                    } else {
                        setWarningMessage("Required library " + lib.getPath() + " is not correctly configured");
                    }
                }
                List<DynamicJSDependency> recDeps = new ArrayList<DynamicJSDependency>();
                for (IConfigurationElement dependencyConf : confElement.getChildren(ID_DEPENDENCY)) {
                    String dependencyID = dependencyConf.getAttribute(ATTR_RES_BUNDLE_ID);
                    DynamicJSDependency dep = new DynamicJSDependency();
                    dep.setName(dependencyID);
                    dep.setPath(dependencyID);
                    dep.setLocal(true);
                    recDeps.add(dep);
                    lib.addDependencies(dependencyID);
                }
                if (recDeps.size() > 0) {
                    jsDependencies.addAll(setPathsFromLibNames(recDeps));
                }
            } else {
                setWarningMessage("Required library is not registered: " + lib.getPath());
            }
        }
        DynamicJSViewRepresentation representation = getViewRepresentation();
        representation.addCssDependencies(cssPaths.toArray(new String[0]));
        return jsDependencies;
    }

    private IConfigurationElement getConfigurationFromWebResID(final String id) {
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IConfigurationElement[] configurationElements = registry.getConfigurationElementsFor(ID_WEB_RES);
        for (IConfigurationElement element : configurationElements) {
            if (id.equals(element.getAttribute(ATTR_RES_BUNDLE_ID))) {
                return element;
            }
        }
        return null;
    }

	@Override
	protected void performReset() {
		// nothing to do?
	}

	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
		m_config.saveToNodeSettings(settings);
	}

	@Override
	protected void validateSettings(final NodeSettingsRO settings)
			throws InvalidSettingsException {
		(new DynamicJSConfig(m_node)).loadFromNodeSettings(settings);
	}

	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
			throws InvalidSettingsException {
		m_config.loadFromNodeSettings(settings);
	}

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean generateImage() {
        return m_config.getHasSvgImageOutport() & m_config.getGenerateImage();
    }

    /**
     * {@inheritDoc}
     * @since 3.3
     */
    @Override
    public JSONLayoutViewContent getLayoutTemplate() {
        JSONLayoutViewContent template = new JSONLayoutViewContent();
        boolean adaptToWindow = false;
        for (Entry<String, SettingsModel> entry : m_config.getModels().entrySet()) {
            if (entry.getValue() instanceof SettingsModelSVGOptions) {
                adaptToWindow = ((SettingsModelSVGOptions)entry.getValue()).getAllowFullscreen();
                break;
            }
        }
        if (adaptToWindow) {
            template.setResizeMethod(ResizeMethod.ASPECT_RATIO_16by9);
        } else {
            template.setResizeMethod(ResizeMethod.VIEW_LOWEST_ELEMENT);
        }
        return template;
    }
}
