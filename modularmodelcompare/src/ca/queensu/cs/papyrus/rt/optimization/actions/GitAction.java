package ca.queensu.cs.papyrus.rt.optimization.actions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownServiceException;
import java.nio.file.Paths;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import ca.queensu.cs.papyrus.rt.optimization.actions.EMFCompareAction;

public class GitAction implements IWorkbenchWindowActionDelegate{

	private IWorkbenchWindow window;
	   public static File filePath;
	   public static String filePathCodeGen;

	/**``
	 * The constructor.
	 */
	public GitAction() {
	}

	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */

public void run(IAction action) {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String commitOne =  null, commitTwo = null, repo = null;
	/*	System.out.print("Enter Commit 1: ");
        try {commitOne = br.readLine();}
        catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.print("Enter Commit 2: ");
        	try {commitTwo = br.readLine();}
        catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        	System.out.print("Enter your repository: ");
        	try{	repo = br.readLine();}
        	catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}*/
		
	try {
		takeInput();
	} catch (GitAPIException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (UnknownServiceException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	
public static void takeInput() throws InvalidRemoteException, TransportException, GitAPIException, IOException
{	
		
	//-------------For Evaluation - Parcel Router 
		String commitOne = "4c271a5587e63120811580959e2e618afb3de066"; //Original
		String commitTwo = "e5a82d042ecf243bcc046a4a24caad6f70b3b286"; //Structural Change
		String repo = "/Users/kanchan/Evaluation-Git/ParcelRouter_v0/.git";
		/*
		String commitOne = "291b270144c7e86dc840371a9b3ba36300c49c49"; //Original
		String commitTwo = "db0cdc9d413d335ba54f5ce0ae341c440368d4d9"; //Structural Change
		String repo = "/Users/kanchan/Evaluation-Git/ca.queensu.cs.rover.model/.git";
	*/
		/*	
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String commitOne =  null, commitTwo = null, repo = null;
		System.out.print("Enter Commit 1: ");
		commitOne = br.readLine();
    		System.out.print("Enter Commit 2: ");
    		commitTwo = br.readLine();
    		System.out.print("Enter your repository: ");
    		repo = br.readLine();
		 */
		/*
		Scanner scan = new Scanner(System.in);  // Reading from System.in
		String commitOne =  null, commitTwo = null, repo = null;
		System.out.print("Enter Old Commit: ");
		commitOne = scan.next();
    		System.out.print("Enter New Commit: ");
    		commitTwo = scan.next();
    		System.out.print("Enter your repository: ");
    		repo = scan.next();
		*/
	
	compare(commitOne,commitTwo,repo);
    
    //When using Remote Repository
	/* 
    Git git = Git.cloneRepository().setURI(repo).setDirectory(new File("/Users/kanchan/git/newFolder")).call();
    String gitDir = git.getRepository().getDirectory().toString();
	try {
		compare(commitOne,commitTwo,gitDir);
	} catch (UnknownServiceException e) { 
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	*/
}	
	public static void compare(String firstCommitid, String secondCommitid, String repPath) throws UnknownServiceException {

		ObjectId firstID = null; ObjectId secondID = null; ObjectLoader firstLoader = null; ObjectLoader secondLoader = null;

		FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
		Repository repository = null;
		try {
			repository = repositoryBuilder.setGitDir(new File(repPath))
			                .readEnvironment() // scan environment GIT_* variables
			                .findGitDir() // scan up the file system tree
			                .setMustExist(true)
			                .build();
			
			// prepare a new folder for the cloned repository
	        /*File localPath = File.createTempFile("TestGitRepository", "");
	        if(!localPath.delete()) {
	            throw new IOException("Could not delete temporary file " + localPath);
	        }
			
			repository = Git.cloneRepository()
	                .setURI(repPath)
	                .setDirectory(localPath)
	                .setCredentialsProvider(allowHosts)
	                .call();*/
			
			firstID = repository.resolve(firstCommitid);
			secondID = repository.resolve(secondCommitid);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e) {
            throw new UnknownServiceException("Service not found");
        }
		
		try (RevWalk revWalk = new RevWalk(repository)) {
	        RevCommit firstCommit = revWalk.parseCommit(firstID);
	        RevCommit secondCommit = revWalk.parseCommit(secondID);

	        // and using commit's tree find the path
	        RevTree firstTree = firstCommit.getTree();
	        RevTree secondTree = secondCommit.getTree();
	        
	     // now try to find a specific file
	        try (TreeWalk firstTreeWalk = new TreeWalk(repository)) {
	        		firstTreeWalk.addTree(firstTree);
	        		firstTreeWalk.setRecursive(true);
	        	//	firstTreeWalk.setFilter(PathFilter.create("PingPong.uml"));
	        	//	firstTreeWalk.setFilter(PathFilter.create("model.uml"));
	        	//	firstTreeWalk.setFilter(PathFilter.create("rover.uml"));
	        	//	firstTreeWalk.setFilter(PathFilter.create("CarDoorLock.uml"));
	        		firstTreeWalk.setFilter(PathFilter.create("ParcelRouter_v0.uml"));
	        	//	firstTreeWalk.setFilter(PathFilter.create("ImpactAnalysis/model.uml"));
	       // 		firstTreeWalk.setFilter(PathFilter.create("Replication.uml"));

	            if (!firstTreeWalk.next()) {
	                throw new IllegalStateException("Did not find expected file 'model.uml'");
	            }
	            ObjectId objectId = firstTreeWalk.getObjectId(0);
	            firstLoader = repository.open(objectId);
	            //firstLoader.copyTo(System.out);
	        
	            	try (TreeWalk secondTreeWalk = new TreeWalk(repository)) {
	        		secondTreeWalk.addTree(secondTree);
	        		secondTreeWalk.setRecursive(true);
		    // 	secondTreeWalk.setFilter(PathFilter.create("PingPong.uml"));
	        //		secondTreeWalk.setFilter(PathFilter.create("CarDoorLock.uml"));

	        	//	secondTreeWalk.setFilter(PathFilter.create("model.uml"));
	        	//	secondTreeWalk.setFilter(PathFilter.create("rover.uml"));
	        		secondTreeWalk.setFilter(PathFilter.create("ParcelRouter_v0.uml"));
	        	//	secondTreeWalk.setFilter(PathFilter.create("ImpactAnalysis/model.uml"));
	        	//	secondTreeWalk.setFilter(PathFilter.create("Replication.uml"));


	            if (!secondTreeWalk.next()) {
	                throw new IllegalStateException("Did not find expected file 'model.uml'");
	            }
	            ObjectId objectId2 = secondTreeWalk.getObjectId(0);
	            secondLoader = repository.open(objectId2);
	        
	        		String content = new String(firstLoader.getBytes());
	        		String newContent = new String(secondLoader.getBytes());
	        		
	        		File tmp=File.createTempFile("file1", ".uml");
	        		File tmp1=File.createTempFile("file2", ".uml");      
	        		
	        		System.out.println("File Path 1 is "+tmp);
	        		System.out.println("File Path 2 is "+tmp1);

	        		BufferedWriter firstBW = new BufferedWriter(new FileWriter(tmp));
	        		firstBW.write(content);
	        		filePath = tmp1;
	        		filePathCodeGen = tmp1.getPath();
	        		
	        		firstBW.close();		        
	        		BufferedWriter secondBW = new BufferedWriter(new FileWriter(tmp1));
	        		secondBW.write(newContent);
	        		secondBW.close();

	        		//Send the file location for EMF Compare
	        		EMFCompareAction cmp = new EMFCompareAction();
	        		cmp.compare(tmp.getPath(), tmp1.getPath());

	        		//Delete the temperory file on Exit
	        		tmp.deleteOnExit();
	        		tmp1.deleteOnExit();
	            	}catch (IOException e) {
	            	 e.printStackTrace();}
	        		}
	        		catch (Exception e) {
	            throw new UnknownServiceException("Service not found");
	        		}
	        		revWalk.dispose(); 
		}
			catch (MissingObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			} catch (IncorrectObjectTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
			}
	}
	
	/**
	 * Selection in the workbench has been changed. We 
	 * can change the state of the 'real' action here
	 * if we want, but this can only happen after 
	 * the delegate has been created.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {

	}

	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to
	 * be able to provide parent shell for the message dialog.
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

}
