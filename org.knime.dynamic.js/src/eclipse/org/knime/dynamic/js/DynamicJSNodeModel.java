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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModel;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnFilter2;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnName;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.database.DatabasePortObject;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.port.flowvariable.FlowVariablePortObjectSpec;
import org.knime.core.node.port.image.ImagePortObject;
import org.knime.core.node.web.ValidationError;
import org.knime.core.node.workflow.FlowVariable;
import org.knime.dynamicjsnode.v212.DynamicJSKnimeNode;
import org.knime.dynamicjsnode.v212.WebDependency;
import org.knime.dynamicjsnode.v212.WebRessource;
import org.knime.dynamicjsnode.v212.WebRessources;
import org.knime.dynamicnode.v212.DynamicInPort;
import org.knime.dynamicnode.v212.DynamicOutPort;
import org.knime.dynamicnode.v212.DynamicPorts;
import org.knime.js.core.JSONDataTable;
import org.knime.js.core.node.AbstractWizardNodeModel;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of Konstanz
 */
public class DynamicJSNodeModel extends AbstractWizardNodeModel<DynamicJSViewRepresentation, DynamicJSViewValue> {

	private static final NodeLogger LOGGER = NodeLogger.getLogger(DynamicJSNodeModel.class);

	private DynamicJSKnimeNode m_node;
	private DynamicJSConfig m_config;
	private final String m_viewName;
	private final String m_rootPath;

	/**
	 * @param nodeConfig
	 * @param configRootPath
	 * @param viewName
	 */
	protected DynamicJSNodeModel(final DynamicJSKnimeNode nodeConfig, final String configRootPath,final String viewName) {
		super(getPortTypeArray(nodeConfig, true), getPortTypeArray(nodeConfig, false));
		m_node = nodeConfig;
		m_config = new DynamicJSConfig(nodeConfig);
		m_rootPath = configRootPath;
		m_viewName = viewName;
	}

	private static PortType[] getPortTypeArray(final DynamicJSKnimeNode nodeConfig, final boolean getInPorts) {
		DynamicPorts ports = nodeConfig.getPorts();
		if (getInPorts) {
			List<PortType> inPorts = new ArrayList<PortType>();
			for (DynamicInPort port : ports.getInPortList()) {
				inPorts.add(getPortType(port.getPortType()));
			}
			return inPorts.toArray(new PortType[0]);
		} else {
			List<PortType> outPorts = new ArrayList<PortType>();
			for (DynamicOutPort port : ports.getOutPortList()) {
				outPorts.add(getPortType(port.getPortType()));
			}
			return outPorts.toArray(new PortType[0]);
		}
	}

	private static PortType getPortType(final org.knime.dynamicnode.v212.PortType.Enum portType) {
		if (portType.equals(org.knime.dynamicnode.v212.PortType.DATA)) {
			return BufferedDataTable.TYPE;
		}
		if (portType.equals(org.knime.dynamicnode.v212.PortType.FLOW_VARIABLE)) {
			return FlowVariablePortObject.TYPE;
		}
		if (portType.equals(org.knime.dynamicnode.v212.PortType.IMAGE)) {
			return ImagePortObject.TYPE;
		}
		if (portType.equals(org.knime.dynamicnode.v212.PortType.DATABASE)) {
			return DatabasePortObject.TYPE;
		}
		return null;
	}

	private static PortObjectSpec getPortSpec(final org.knime.dynamicnode.v212.PortType.Enum portType, final PortObjectSpec[] inSpecs, final int portIndex) {
		if (portType.equals(org.knime.dynamicnode.v212.PortType.DATA)) {
			if (inSpecs[portIndex] instanceof DataTableSpec) {
				return inSpecs[portIndex];
			} else {
				return new DataTableSpec();
			}
		}
		if (portType.equals(org.knime.dynamicnode.v212.PortType.FLOW_VARIABLE)) {
			return FlowVariablePortObjectSpec.INSTANCE;
		}
		return null;
	}

	private static PortObject getPortObject(final org.knime.dynamicnode.v212.PortType.Enum portType, final PortObject[] inObjects, final int portIndex) {
		if (portType.equals(org.knime.dynamicnode.v212.PortType.DATA)) {
			if (inObjects[portIndex] instanceof BufferedDataTable) {
				return inObjects[portIndex];
			}
		}
		if (portType.equals(org.knime.dynamicnode.v212.PortType.FLOW_VARIABLE)) {
			return FlowVariablePortObject.INSTANCE;
		}
		return null;
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
		return false;
	}

	@Override
	public ValidationError validateViewValue(final DynamicJSViewValue viewContent) {
		// validation not possible
		return null;
	}

	@Override
	public void saveCurrentValue(final NodeSettingsWO content) {
		// TODO Auto-generated method stub
	}

	@Override
	protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs)
			throws InvalidSettingsException {
		List<DynamicOutPort> ports = m_node.getPorts().getOutPortList();
		PortObjectSpec[] specs = new PortObjectSpec[ports.size()];
		for (int i = 0; i < ports.size(); i++) {
			specs[i] = getPortSpec(ports.get(i).getPortType(), inSpecs, i);
		}
		return specs;
	}

	@Override
	protected PortObject[] performExecute(final PortObject[] inObjects,
			final ExecutionContext exec) throws Exception {

		synchronized (getLock()) {
			DynamicJSViewRepresentation viewRepresentation = getViewRepresentation();
			if (viewRepresentation.getTables().length <= 0) {
				List<JSONDataTable> tables = new ArrayList<JSONDataTable>();
				for (PortObject inObject : inObjects) {
					if (inObject instanceof BufferedDataTable) {
						tables.add(createJSONTableFromBufferedDataTable(
								exec.createSubExecutionContext(1d / inObjects.length),
								(BufferedDataTable) inObject));
					}
					// nothing to do on other types?
				}
				viewRepresentation.setTables(tables.toArray(new JSONDataTable[0]));
			}
			if (viewRepresentation.getFlowVariables().size() <= 0) {
				Map<String, String> vStringMap = new HashMap<String, String>();
				for (Entry<String, FlowVariable> vEntry : getAvailableFlowVariables().entrySet()) {
					vStringMap.put(vEntry.getKey(), vEntry.getValue().getValueAsString());
				}
				viewRepresentation.setFlowVariables(vStringMap);
			}
			if (viewRepresentation.getJsCode().length <= 0) {
				readResourceContents();
			}
			viewRepresentation.setJsNamespace(m_node.getJsNamespace());
			setPathsFromLibNames(getDependencies(true));
			viewRepresentation.setUrlDependencies(getDependencies(false));
			setOptionsOnRepresentation();
		}
		List<DynamicOutPort> ports = m_node.getPorts().getOutPortList();
		PortObject[] pOArray = new PortObject[ports.size()];
		for (int i = 0; i < ports.size(); i++) {
			pOArray[i] = getPortObject(ports.get(i).getPortType(), inObjects, i);
		}
		return pOArray;
	}

	private void setOptionsOnRepresentation() {
		Map<String, Object> options = new HashMap<String, Object>();
		for (SettingsModel model : m_config.getModels().values()) {
			if (model instanceof SettingsModelBoolean) {
				SettingsModelBoolean bM = (SettingsModelBoolean)model;
				options.put(bM.getConfigName(), bM.getBooleanValue());
			} else if (model instanceof SettingsModelString) {
				SettingsModelString sM = (SettingsModelString)model;
				options.put(sM.getKey(), sM.getStringValue());
			} else if (model instanceof SettingsModelColumnFilter2) {
				SettingsModelColumnFilter2 cM = (SettingsModelColumnFilter2)model;
				//TODO: nothing to extract from column filter model!!!
			} else if (model instanceof SettingsModelColumnName) {
			    SettingsModelColumnName cM = (SettingsModelColumnName)model;
			    options.put(cM.getKey(), cM.getColumnName());
			}
		}
		getViewRepresentation().setOptions(options);
	}

	private void readResourceContents() {
		WebRessources resources = m_node.getResources();
		List<String> jsCode = new ArrayList<String>();
		List<String> cssCode = new ArrayList<String>();
		Map<String, String> binaryFiles = new HashMap<String, String>();
		if (resources != null) {
			for (WebRessource res : resources.getRessourceList()) {
				if (res.getType().equals(WebRessource.Type.JS)) {
					jsCode.add(fileToString(res.getPath(), false));
				} else if (res.getType().equals(WebRessource.Type.CSS)) {
					cssCode.add(fileToString(res.getPath(), false));
				} else if (res.getType().equals(WebRessource.Type.FILE)) {
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
			LOGGER.error("Specified resource file " + file + "does not exist!");
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

	private String[] getDependencies(final boolean local) {
		List<String> deps = new ArrayList<String>();
		if (m_node.getDependencies() != null) {
			for (WebDependency dep : m_node.getDependencies().getDependencyList()) {
				if (local && dep.getType().equals(WebDependency.Type.LOCAL)) {
					deps.add(dep.getPath());
				} else if (!local && dep.getType().equals(WebDependency.Type.URL)) {
					deps.add(dep.getPath());
				}
			}
		}
		return deps.toArray(new String[0]);
	}

	private JSONDataTable createJSONTableFromBufferedDataTable(final ExecutionContext exec, final BufferedDataTable inTable) throws CanceledExecutionException {
        JSONDataTable table = new JSONDataTable(inTable, 1, m_config.getMaxRows(), exec);
        if (m_config.getMaxRows() < inTable.getRowCount()) {
            setWarningMessage("Only the first "
                    + m_config.getMaxRows() + " rows are displayed.");
        }
        return table;
    }

	private static final String ID_WEB_RES = "org.knime.js.core.webResources";
    private static final String ATTR_RES_BUNDLE_ID = "webResourceBundleID";
    private static final String ID_IMPORT_RES = "importResource";
    private static final String ATTR_PATH = "relativePath";
    private static final String ATTR_TYPE = "type";

    private void setPathsFromLibNames(final String[] libNames) {
        ArrayList<String> jsPaths = new ArrayList<String>();
        ArrayList<String> cssPaths = new ArrayList<String>();
        for (String lib : libNames) {
            IConfigurationElement confElement = getConfigurationFromWebResID(lib);
            if (confElement != null) {
                for (IConfigurationElement resElement : confElement.getChildren(ID_IMPORT_RES)) {
                    String path = resElement.getAttribute(ATTR_PATH);
                    String type = resElement.getAttribute(ATTR_TYPE);
                    if (path != null && type != null) {
                        if (type.equalsIgnoreCase("javascript")) {
                            jsPaths.add(path);
                        } else if (type.equalsIgnoreCase("css")) {
                            cssPaths.add(path);
                        }
                    } else {
                        setWarningMessage("Required library " + lib + " is not correctly configured");
                    }
                }
            } else {
                setWarningMessage("Required library is not registered: " + lib);
            }
        }
        DynamicJSViewRepresentation representation = getViewRepresentation();
        representation.setJsDependencies(jsPaths.toArray(new String[0]));
        representation.setCssDependencies(cssPaths.toArray(new String[0]));
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
	protected String getInteractiveViewName() {
		return m_viewName;
	}

	@Override
	protected void useCurrentValueAsDefault() {
		// TODO Auto-generated method stub

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



}
