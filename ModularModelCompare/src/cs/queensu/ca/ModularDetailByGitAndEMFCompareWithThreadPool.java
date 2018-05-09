package cs.queensu.ca;

/* source code for implementing modular model comparison based on sub-elements  using Git and EMFComprae*/

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class ModularDetailByGitAndEMFCompareWithThreadPool {
	
	
	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
		loggers.add(LogManager.getRootLogger());
		for ( Logger logger : loggers ) {
		    logger.setLevel(Level.OFF);
		}
		String repoPath="/Users/mojtababagherzadeh/git/MM_Evaluation/";
		String path2="/Users/mojtababagherzadeh/git/MM_Evaluation_1";
		String fileName="results/ModularDeatilLevelByGit.csv";
		BufferedWriter writer;
		writer = new BufferedWriter(new FileWriter(fileName,true));
		System.out.println("Mode,ModelName,newversion,oldversion,timewithload,timewithoutload");
		ModelCompareByGit modelCompare=new ModelCompareByGit(repoPath);
		List<String> versions= Arrays.asList("multifile1MBv1.0","multifile1MBv1.0.1","multifile1MBv1.5");
		modelCompare.evaluateDetailLevelModular(repoPath, repoPath, path2, versions, "1MBModularDetail",writer,5);
		writer.close();
	}

}
