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

import org.knime.core.node.DynamicNodeFactory;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDescription;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeView;
import org.knime.core.node.config.ConfigRO;
import org.knime.core.node.config.ConfigWO;
import org.knime.core.node.wizard.WizardNodeFactoryExtension;
import org.knime.dynamic.js.v212.DynamicJSNodeModel;
import org.knime.dynamic.js.v212.DynamicJSViewRepresentation;
import org.knime.dynamic.js.v212.DynamicJSViewValue;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * @since 2.12
 */
public class DynamicJSNodeFactory extends DynamicNodeFactory<DynamicJSNodeModel> implements
		WizardNodeFactoryExtension<DynamicJSNodeModel, DynamicJSViewRepresentation, DynamicJSViewValue> {

    private org.knime.dynamic.js.v212.DynamicJSNodeFactory m_delegateFactory = new org.knime.dynamic.js.v212.DynamicJSNodeFactory();

	@Override
	protected NodeDescription createNodeDescription() {
	    return m_delegateFactory.createNodeDescription();
	}

	@Override
	public void loadAdditionalFactorySettings(final ConfigRO config) throws InvalidSettingsException {
	    m_delegateFactory.loadAdditionalFactorySettings(config);
	}

	@Override
	public void saveAdditionalFactorySettings(final ConfigWO config) {
		m_delegateFactory.saveAdditionalFactorySettings(config);
	}

	/**
     * @since 3.0
     */
	@Override
	public DynamicJSNodeModel createNodeModel() {
		return m_delegateFactory.createNodeModel();
	}

	@Override
	protected int getNrNodeViews() {
		return m_delegateFactory.getNrNodeViews();
	}

	/**
     * @since 3.0
     */
	@Override
	public NodeView<DynamicJSNodeModel> createNodeView(final int viewIndex,
			final DynamicJSNodeModel nodeModel) {
		return m_delegateFactory.createNodeView(viewIndex, nodeModel);
	}

	@Override
	protected boolean hasDialog() {
	    return m_delegateFactory.hasDialog();
	}

	@Override
	protected NodeDialogPane createNodeDialogPane() {
		return m_delegateFactory.createNodeDialogPane();
	}

}
