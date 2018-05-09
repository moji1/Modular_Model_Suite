package cs.queensu.ca;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

class EMFCompareTask extends Thread {
		private String path1;
		private String path2;
		private boolean exit=false;
		static private List<String> filesToCmprae;
		static private int lastProcessedVersionIndex=0;
		private int role; // 0 means worker, 1 means controller
		static boolean workStarted=false;
		private synchronized  int getCurrentVersionId() {
			if (workStarted && lastProcessedVersionIndex<=(filesToCmprae.size()-1) )
				return lastProcessedVersionIndex++;
			else 
				return -1;
		}
		private synchronized  void resetCurrentVersionId() {
			lastProcessedVersionIndex=0;
		}
		EMFCompareTask(String path1, String path2, int role ) {
		   this.path1=path1;
		   this.path2=path2;
		   this.role=role;
		}
	    public void controller() throws InterruptedException {
	    	resetCurrentVersionId();
	    	workStarted=true;
	    	while ((lastProcessedVersionIndex <= (filesToCmprae.size()-1))) {
			   Thread.sleep(3000);
			   System.out.println("Number of processed version is: " + Integer.toString(lastProcessedVersionIndex));
			  }
			   
	   	}
	    public void worker() {
		   ResourceSet resourceSet1 = new ResourceSetImpl();
		   ResourceSet resourceSet2 = new ResourceSetImpl();
		   IComparisonScope scope;
		   Comparison comparison;
		   int versionIndex=0;

		   while (! exit) {
			   versionIndex=getCurrentVersionId();
			   if (versionIndex==-1) {
				  try {
					Thread.sleep(1000);
				   } catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				   }
				   continue;
			   }
			   /*try {
				sleep(1000);
			   } catch (InterruptedException e) {
				// TODO Auto-generated catch block
				   e.printStackTrace();
			   }*/
			   URI uri1 = URI.createFileURI(path1+"/"+getFilesToCmprae().get(versionIndex));
			   URI uri2 = URI.createFileURI(path2+"/"+getFilesToCmprae().get(versionIndex));
			   resourceSet1.getResource(uri1, true);
			   resourceSet2.getResource(uri2, true);
			   scope = new DefaultComparisonScope(resourceSet1, resourceSet2, null);
			   comparison = EMFCompare.builder().build().compare(scope);
			   List<Diff> differences = comparison.getDifferences();
			   
		   }
	   }
	    
	   public void run() {
		   if (role==0)
			   worker();
		else
			try {
				controller();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   
	   }

	   public static List<String> getFilesToCmprae() {
		   return filesToCmprae;
	   }

	   public static void setFilesToCmprae(List<String> versions) {
		   EMFCompareTask.filesToCmprae = versions;
	   }
	   
	   public void exit() {
		   exit=true;
	   }
}
