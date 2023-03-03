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
 */
package org.knime.ext.js.molecule;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.knime.core.ui.util.SWTUtilities;

import static org.knime.ext.js.molecule.MoleculeSketcherPreferenceUtil.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

/**
 * Preference page for the Molecule Sketcher.
 * 
 * @author Marc Bux, KNIME GmbH, Berlin, Germany
 *
 * @since 5.1
 */
public final class MoleculeSketcherPreferencePage extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage {

    private final String[] m_supportedFormats;

    private boolean m_apply;

    /** Constructor */
    public MoleculeSketcherPreferencePage() {
        super(GRID);
        m_supportedFormats = MoleculeSketcherPreferenceUtil.getInstance().getSelectedSketcher().getSupportedFormats();
        Arrays.sort(m_supportedFormats);
    }

    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(MoleculeSketcherPreferenceUtil.getInstance().getStore());
        setDescription("Configuration option for the Molecule Widget node.");
    }

    @Override
    protected void createFieldEditors() {
        final var parent = getFieldEditorParent();

        addField(new RadioGroupFieldEditor(NAME_SELECTION_KEY, "Molecule sketcher", 1,
            MoleculeSketcherRegistry.getInstance().getMoleculeSketchers().stream()
                .map(sketcher -> new String[]{sketcher.getName(), sketcher.getClass().getName()})
                .toArray(String[][]::new),
            parent));

        addField(new StringFieldEditor(SERVER_URL_KEY, "Sketcher server URL", StringFieldEditor.UNLIMITED,
            StringFieldEditor.VALIDATE_ON_FOCUS_LOST, parent) {
            @Override
            protected boolean doCheckState() {
                final var urlString = getTextControl().getText();
                if (!urlString.isEmpty()) {
                    try {
                        @SuppressWarnings("unused")
                        final var url = new URL(urlString);
                    } catch (MalformedURLException e) {
                        final var message = e.getMessage();
                        setErrorMessage("Provided URL is invalid"
                            + (message != null && !message.isEmpty() ? (": " + message) : "."));
                        return false;
                    }
                }
                return true;
            }
        });
    }

    @Override
    protected void performApply() {
        m_apply = true;
        // note that super.performApply() entails a call to performOk()
        super.performApply();
    }

    @Override
    public boolean performOk() {
        final boolean result = super.performOk();
        checkChanges();
        return result;
    }

    @Override
    public boolean performCancel() {
        final boolean result = super.performCancel();
        checkChanges();
        return result;
    }

    private void checkChanges() {
        boolean apply = m_apply;
        m_apply = false;

        // we have to return here since we do not want to proceed (yet) in case of Apply
        // we only want to proceed in case of OK or cancel
        if (apply) {
            return;
        }

        final var supportedFormatsUpdated =
            MoleculeSketcherPreferenceUtil.getInstance().getSelectedSketcher().getSupportedFormats();
        Arrays.sort(supportedFormatsUpdated);

        if (!Arrays.equals(m_supportedFormats, supportedFormatsUpdated)) {
            Display.getDefault()
                .asyncExec(() -> promptRestartWithMessage(
                    String.format("The supported formats of the configured molecule sketcher have changed.%n"
                        + "Molecule Widget nodes in open workflows might be in an inconsistent state "
                        + "until the workbench is restarted.%nDo you want to restart the workbench now?")));
        }

    }

    private static void promptRestartWithMessage(final String message) {
        final var messageBox = new MessageBox(SWTUtilities.getActiveShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
        messageBox.setText("Restart workbench...");
        messageBox.setMessage(message);
        if (messageBox.open() != SWT.YES) {
            return;
        }

        PlatformUI.getWorkbench().restart();
    }

}
