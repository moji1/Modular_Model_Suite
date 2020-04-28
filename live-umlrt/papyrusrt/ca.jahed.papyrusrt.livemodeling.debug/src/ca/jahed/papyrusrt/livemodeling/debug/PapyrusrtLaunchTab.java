package ca.jahed.papyrusrt.livemodeling.debug;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ResourceListSelectionDialog;

public class PapyrusrtLaunchTab extends AbstractLaunchConfigurationTab implements ModifyListener {
	
	private Text modelFile;
	private Button modelFileButton;
	
    @Override
    public void createControl(Composite parent) {

        Composite comp = new Group(parent, SWT.BORDER);
        setControl(comp);
        
        Label modelFileLabel = new Label(comp, SWT.NONE);
        modelFileLabel.setText("Model File:");
        GridDataFactory.swtDefaults().applyTo(modelFileLabel);
        
        modelFile = new Text(comp, SWT.BORDER);
        modelFile.addModifyListener(this);
        modelFile.setEditable(false);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(modelFile);
        
        modelFileButton = createPushButton(comp, "&Browse...", null); //$NON-NLS-1$
        modelFileButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseModelFiles();
			}
		});
        GridDataFactory.swtDefaults().applyTo(modelFileButton);
        
        Label dummyLabel = new Label(comp, SWT.NONE);
        dummyLabel.setText("");
        GridDataFactory.swtDefaults().applyTo(dummyLabel);
        
        GridLayoutFactory.swtDefaults().numColumns(2).applyTo(comp);
    }

	private void browseModelFiles() {
		ResourceListSelectionDialog dialog = new ResourceListSelectionDialog(getShell(), ResourcesPlugin.getWorkspace().getRoot(), IResource.FILE);
		dialog.setTitle("Model File"); //$NON-NLS-1$
		dialog.setMessage("Select Model File"); //$NON-NLS-1$
		if (dialog.open() == Window.OK) {
			Object[] files = dialog.getResult();
			IFile file = (IFile) files[0];
			modelFile.setText(URI.createPlatformResourceURI(file.getFullPath().toString(), true).toString());
			updateLaunchConfigurationDialog();
		}
	}
	
    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
    }

    @Override
    public void initializeFrom(ILaunchConfiguration configuration) {
        try {
        		modelFile.setText(configuration.getAttribute(PapyrusrtLaunchConfigurationAttributes.ATTR_MODEL_FILE, ""));
        	} catch (CoreException e) {}
    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
    		configuration.setAttribute(PapyrusrtLaunchConfigurationAttributes.ATTR_MODEL_FILE, modelFile.getText());
	}

    @Override
    public String getName() {
        return "Configuration";
    }

	@Override
	public void modifyText(ModifyEvent arg0) {
		updateLaunchConfigurationDialog();
	}

}
