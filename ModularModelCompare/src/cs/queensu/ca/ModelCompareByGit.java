package cs.queensu.ca;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.uml2.uml.internal.resource.UMLResourceFactoryImpl;

public class ModelCompareByGit {
	private Git git;
	private static List<EMFCompareTask> emfCompareTasks;
	
	public void evaluateDetailLevelModular(String repoPath, String path1, String path2,
			List<String> versions,String modelName, BufferedWriter writer, int threadCount ) throws InterruptedException {
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("uml", new UMLResourceFactoryImpl());
		long elapsedTime=0;
		for (int ii=1;ii<threadCount;ii++) {
			emfCompareTasks.add(new EMFCompareTask(path1, path2,0));
			emfCompareTasks.get(emfCompareTasks.size()-1).start();;
		}
		List<String> diffFiles=new ArrayList<String> ();
		for (int i=1;i<=versions.size()-1;i++) {
			long startTime=System.currentTimeMillis();
			diffFiles=compareAtTopLevel(versions.get(i), versions.get(i-1));
			System.out.println("Number of file to b compared is "+ Integer.toString(diffFiles.size()));
			//elapsedTime=measureTimeCompareAtTopLevel(versions.get(i), versions.get(i-1));
			EMFCompareTask.setFilesToCmprae(diffFiles);
			EMFCompareTask controller=new EMFCompareTask(path1, path2,1);
			controller.start();
			try {
				controller.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			 }
			//emfCompareTasks.clear();
			elapsedTime=System.currentTimeMillis()-startTime;
			System.out.print("modular,"+modelName+","+versions.get(i)+","+versions.get(i-1)+",");
			System.out.println(elapsedTime);
			try {
				writer.write("modular,"+modelName+","+versions.get(i)+","+versions.get(i-1)+","+ 
						Integer.toString(diffFiles.size())+",");
				writer.write(Long.toString(elapsedTime)+System.getProperty("line.separator"));
				writer.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for (EMFCompareTask t : emfCompareTasks)
			t.exit();
		
	}
	public static void evaluateTopLeveModular(String repoPath, List<String> versions,String modelName) {
		ModelCompareByGit gitModelCompare= new ModelCompareByGit(repoPath);
		System.out.println("Mode,ModelName,newversion,oldversion,time");
		for (int i=1;i<=versions.size()-1;i++) { 
			System.out.print("Modular,"+modelName+","+versions.get(i)+","+versions.get(i-1)+",");
			System.out.println(gitModelCompare.measureTimeCompareAtTopLevel(versions.get(i), versions.get(i-1)));
		}
		List<String> e=gitModelCompare.compareAtTopLevel(versions.get(0), versions.get(1));
		for (String s: e) {
			System.out.println(s);
		}
		//System.out.print(e);
		//System.out.println(e.size());
	}
	public ModelCompareByGit(String repoPath) {
		
		try {
			git=Git.open(new File(repoPath));
			//System.out.println("Repository  is loaded");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(repoPath+" is not a valid repository path");
			e.printStackTrace();
		}
		emfCompareTasks= new ArrayList<EMFCompareTask>();

	}
	
	public List<String> compareAtTopLevel(String newVer, String oldVer){
		List<String> diffFiles= new ArrayList<String>();
	    ObjectId headId;
		try {
			headId = git.getRepository().resolve(newVer+"^{tree}");
		    ObjectId oldId = git.getRepository().resolve(oldVer + "^{tree}");
		    ObjectReader reader = git.getRepository().newObjectReader();
		    CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
		    oldTreeIter.reset(reader, oldId);
		    CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
		    newTreeIter.reset(reader, headId);
		    List<DiffEntry> diffs= git.diff()
		            .setNewTree(newTreeIter)
		            .setOldTree(oldTreeIter)
		            .setShowNameAndStatusOnly(true)
		            .call();
		   for(DiffEntry diff : diffs){
			   if (!(diff.getNewPath().contains("/dev/null")  || diff.getNewPath().contains("single") || diff.getNewPath().contains("Single")) ) 
				   diffFiles.add(diff.getNewPath());
		    }
		} catch (RevisionSyntaxException | IOException | GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return diffFiles;
		
	}
	
	public long measureTimeCompareAtTopLevel(String newVer, String oldVer){
		long startTime=System.currentTimeMillis();
		try {
		    ObjectId headId;
			headId = git.getRepository().resolve(newVer+"^{tree}");
		    ObjectId oldId = git.getRepository().resolve(oldVer + "^{tree}");
		    ObjectReader reader = git.getRepository().newObjectReader();
		    CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
		    oldTreeIter.reset(reader, oldId);
		    CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
		    newTreeIter.reset(reader, headId);
		    List<DiffEntry> diffs= git.diff()
		            .setNewTree(newTreeIter)
		            .setOldTree(oldTreeIter)
		            .setShowNameAndStatusOnly(true)
		            .call();
		    /*int i=0;
		    for (DiffEntry diff : diffs) {
		    	System.out.println(diff.getChangeType().toString());
		    	i=i++;
		    }*/
		    //System.out.println(i);
		} catch (RevisionSyntaxException | IOException | GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return  System.currentTimeMillis()-startTime;	
	}
	
}
