package ca.jahed.papyrusrt.livemodeling.debug;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

public class PapyrusrtLaunchTabGroup extends AbstractLaunchConfigurationTabGroup {

	@Override
    public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
        setTabs(new ILaunchConfigurationTab[] { new PapyrusrtLaunchTab() });
    }
}
