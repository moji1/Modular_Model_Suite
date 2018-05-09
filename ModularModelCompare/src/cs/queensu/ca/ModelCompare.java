package cs.queensu.ca;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.uml2.uml.internal.resource.UMLResourceFactoryImpl;

public class ModelCompare {
	static private Git git;

	private static  void loadGit(String repoPath) {
		try {
			git=Git.open(new File(repoPath+"/.git"));
			System.out.println("Repository  is loaded");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(repoPath+" is not a valid repository path");
			e.printStackTrace();
		}
	}

	
	public  long evaluateNonModular(String filePath, List<String> versions,String modelName) {
		long elapsedTimewithload=0;
		long elapsedTimewithoutload=0;
		//ModelCompareByGit gitModelCompare= new ModelCompareByGit(repoPath+"/.git");
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("uml", new UMLResourceFactoryImpl());
		System.out.println("Mode,ModelName,newversion,oldversion,timewithload,timewithoutload");
		for (int i=1;i<=versions.size()-1;i++) {
			long timestampStartLoad=System.currentTimeMillis();
			elapsedTimewithload=System.currentTimeMillis();
			ResourceSet resourceSet1 = new ResourceSetImpl();
			URI uri1 = URI.createFileURI(filePath+"/"+versions.get(i-1));
			resourceSet1.getResource(uri1, true);
			ResourceSet resourceSet2 = new ResourceSetImpl();
			URI uri2 = URI.createFileURI(filePath+"/"+versions.get(i));
			resourceSet2.getResource(uri2, true);
			long timestampStart=System.currentTimeMillis();
			IComparisonScope scope = new DefaultComparisonScope(resourceSet1, resourceSet2, null);
			Comparison comparison = EMFCompare.builder().build().compare(scope);
			List<Diff> differences = comparison.getDifferences();
			elapsedTimewithoutload=System.currentTimeMillis()-timestampStart;
			elapsedTimewithload=System.currentTimeMillis()-timestampStartLoad;
			System.out.print("nonmodular,"+modelName+","+versions.get(i)+","+versions.get(i-1)+",");
			System.out.print(elapsedTimewithload);
			System.out.print(",");
			System.out.print(elapsedTimewithoutload);
			//System.out.print("Number of changes is ");
			//System.out.println(differences.size());

			//System.out.println(gitModelCompare.measureTimeCompareAtTopLevel(versions.get(i), versions.get(i-1)));
		}
		
		

		return elapsedTimewithload;
		
	}

}
