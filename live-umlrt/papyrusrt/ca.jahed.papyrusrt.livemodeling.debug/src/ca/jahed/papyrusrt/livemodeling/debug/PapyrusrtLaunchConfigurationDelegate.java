package ca.jahed.papyrusrt.livemodeling.debug;
 
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.util.URI;
import org.osgi.framework.Bundle;

public class PapyrusrtLaunchConfigurationDelegate extends LaunchConfigurationDelegate {
	
	@Override
    public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
            throws CoreException {

		String modelPath = configuration.getAttribute(PapyrusrtLaunchConfigurationAttributes.ATTR_MODEL_FILE, "");	
		
		try {
			Bundle bundle = Platform.getBundle("ca.jahed.papyrusrt.livemodeling.debug");
			
			File executor = new File(FileLocator.toFileURL(FileLocator.find(bundle, 
					new Path("tools"+Path.SEPARATOR+"executor.jar"), null)).toURI());
			
			File modelFile;
			if(modelPath.startsWith("platform"))
				modelFile = new File(ResourcesPlugin.getWorkspace().getRoot().getFile(
						new Path(CommonPlugin.resolve(URI.createURI(modelPath, true))
								.toFileString())).getFullPath().makeAbsolute().toString());
			else
				modelFile = new File(modelPath);
				
			ProcessBuilder builder = new ProcessBuilder("java", "-jar", executor.getAbsolutePath(), 
					modelFile.getAbsolutePath());
			
			Process execProcess = builder.start();
			DebugPlugin.newProcess(launch, execProcess, "Live Modeling");
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
    }
}
