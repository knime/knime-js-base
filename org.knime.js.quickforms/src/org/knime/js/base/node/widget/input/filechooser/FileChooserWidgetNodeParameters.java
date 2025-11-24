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
 *   24 Nov 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.widget.input.filechooser;

import org.knime.core.webui.node.dialog.defaultdialog.internal.widget.PersistWithin;
import org.knime.js.base.node.base.input.filechooser.FileChooserNodeConfig;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.FormFieldSection;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.OutputSection;
import org.knime.js.base.node.parameters.filechooser.FileChooserNodeParameters;
import org.knime.js.base.node.widget.WidgetNodeParametersFlowVariable;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.updates.Effect;
import org.knime.node.parameters.updates.Effect.EffectType;
import org.knime.node.parameters.updates.ValueReference;
import org.knime.node.parameters.updates.util.BooleanReference;

/**
 * Settings for the file chooser widget node.
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("restriction")
public final class FileChooserWidgetNodeParameters extends WidgetNodeParametersFlowVariable {

    FileChooserWidgetNodeParameters() {
        super(FileChooserInputWidgetConfig.class);
    }

    @PersistWithin.PersistEmbedded
    FileChooserNodeParameters m_fileChooserNodeParameters = new FileChooserNodeParameters();

    @Widget(title = "Allow multiple selection",
        description = "Option to enable or disable the selection of multiple items."
            + " If unchecked only one item can be selected.")
    @Persist(configKey = FileChooserNodeConfig.CFG_MULTIPLE_SELECTION)
    @Layout(FormFieldSection.class)
    boolean m_allowMultipleSelection = FileChooserNodeConfig.DEFAULT_MULTIPLE_SELECTION;

    @Widget(title = "Root path",
        description = "An optional root path to only make items contained"
            + " within this given directory available for selection.")
    @Persist(configKey = FileChooserNodeConfig.CFG_ROOT_DIR)
    @Layout(FormFieldSection.class)
    String m_rootPath = FileChooserNodeConfig.DEFAULT_ROOT_DIR;

    @Widget(title = "Use default mount id of target",
        description = "Setting this option will query the mount id of the hub the node is running on for creating"
            + " absolute paths to the selected items. If unchecked a custom mount id can be provided.")
    @Persist(configKey = FileChooserNodeConfig.CFG_DEFAULT_MOUNTID)
    @Layout(OutputSection.class)
    @ValueReference(MountIdOptionReference.class)
    boolean m_mountIdOption = FileChooserNodeConfig.DEFAULT_DEFAULT_MOUNTID;

    @Widget(title = "Custom mount id",
        description = "A custom mount id to be included in the absolute paths to the selected items.")
    @Persist(configKey = FileChooserNodeConfig.CFG_CUSTOM_MOUNTID)
    @Layout(OutputSection.class)
    @Effect(predicate = MountIdOptionReference.class, type = EffectType.HIDE)
    String m_customMountId = FileChooserNodeConfig.DEFAULT_CUSTOM_MOUNTID;

    private static final class MountIdOptionReference implements BooleanReference {
    }

}
