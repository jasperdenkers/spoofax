package org.strategoxt.imp.runtime.services.menus;

import org.eclipse.ui.commands.ICommandService;
import org.strategoxt.imp.runtime.EditorState;
import org.strategoxt.imp.runtime.dynamicloading.AbstractServiceFactory;
import org.strategoxt.imp.runtime.dynamicloading.BadDescriptorException;
import org.strategoxt.imp.runtime.dynamicloading.Descriptor;
import org.strategoxt.imp.runtime.parser.SGLRParseController;

/**
 * @author Oskar van Rest
 */
public class MenusServiceFactory extends AbstractServiceFactory<MenusService> {

	public MenusServiceFactory() {
		super(MenusService.class, true);
	}

	@Override
	public MenusService create(Descriptor descriptor, SGLRParseController controller) throws BadDescriptorException {
		return new MenusService(descriptor);
	}

	public static void eagerInit(EditorState lastEditor) {
		if (lastEditor.getEditor() != null) {
			ICommandService commandService = (ICommandService) lastEditor.getEditor().getSite().getService(ICommandService.class);
			for (int i = 1; i <= MenusServiceConstants.NO_OF_TOOLBAR_MENUS; i++) {
				commandService.refreshElements(MenusServiceConstants.TOOLBAR_BASECOMMAND_ID_PREFIX + i, null);
			}
			commandService.refreshElements("org.spoofax.menus.toolbar", null);
		}
	}
}