package ca.jahed.papyrusrt.livemodeling.debug;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

public class PapyrusrtLaunchShortcut implements ILaunchShortcut {

	@Override
    public void launch(ISelection selection, String mode) {
		if(selection instanceof StructuredSelection) {
			Object selected = ((StructuredSelection)selection).getFirstElement();
			if(selected instanceof IProject) {
				IProject project = (IProject) selected;
				
				try {
					for (IResource member : project.members()) {
						if(member.getFileExtension().equals("uml")) {
							doLaunch(member);
							return;
						}
					}
				} catch (CoreException e) {
				}
			}
		}
    }

    @Override
    public void launch(IEditorPart editor, String mode) {
		IEditorInput input = editor.getEditorInput();
		if(input instanceof IFileEditorInput) {
			IFile file = ((IFileEditorInput)input).getFile();
			if(file.getFileExtension().equals("di")) {
				String name = file.getName().substring(0,file.getName().length() - file.getFileExtension().length());
				IFile modelFile = file.getProject().getFile(name+"uml");
				doLaunch(modelFile);
			}
		}
    }
    
    public void doLaunch(IResource modelFile) {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type =manager.getLaunchConfigurationType("ca.jahed.papyrusrt.livemodeling.debug.launchConfigurationType");
		try {
			for(ILaunchConfiguration launchConfig : manager.getLaunchConfigurations(type)) {
				if(launchConfig.getName().equals(modelFile.getProject().getName())) {
					DebugUITools.launch(launchConfig, ILaunchManager.RUN_MODE);
					return;
				}
			}

			ILaunchConfigurationWorkingCopy workingCopy = type.newInstance(null, modelFile.getProject().getName());
			workingCopy.setMappedResources(new IResource[] {modelFile.getProject()});
			workingCopy.setAttribute(PapyrusrtLaunchConfigurationAttributes.ATTR_MODEL_FILE, modelFile.getRawLocation().makeAbsolute().toString());
			ILaunchConfiguration launchConfig = workingCopy.doSave();
			DebugUITools.launch(launchConfig, ILaunchManager.RUN_MODE);
		} catch (CoreException e) {
			e.printStackTrace();
		}
    }
}
