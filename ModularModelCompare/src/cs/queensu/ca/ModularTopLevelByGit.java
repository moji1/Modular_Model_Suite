package cs.queensu.ca;

/* source code for implementing modular model comparison based on top-elements  using Git */

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ModularTopLevelByGit {
	public static void evaluateTopLeveModular(String repoPath, List<String> versions,String modelName,BufferedWriter writer) {
		ModelCompareByGit gitModelCompare= new ModelCompareByGit(repoPath);
		for (int i=0;i<=versions.size()-1;i++) { 
			for (int j=0;j<=versions.size()-1;j++) {
				if (i!=j) {
					System.out.print("Modular,"+modelName+","+versions.get(i)+","+versions.get(j)+",");
					try {
						writer.write("Modular,"+modelName+","+versions.get(i)+","+versions.get(j)+",");
						long processingTime=gitModelCompare.measureTimeCompareAtTopLevel(versions.get(i), versions.get(j));
						writer.write(Long.toString(processingTime));
						writer.write(System.getProperty("line.separator"));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					System.out.println(gitModelCompare.measureTimeCompareAtTopLevel(versions.get(i), versions.get(j)));
				}
			}
		}
	}

	public static void main(String[] args) throws IOException {
		
		String fileName="results/ModularTopLevelByGit.csv";
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
		System.out.println("Mode,ModelName,newversion,oldversion,timewithload,timewithoutload");
		writer.write("Mode,ModelName,newversion,oldversion,timewithload,timewithoutload"+System.getProperty("line.separator"));
		List<String> versions= Arrays.asList("multifile1MBv1.0","multifile1MBv1.0.1");
		evaluateTopLeveModular("/Users/mojtababagherzadeh/git/MM_Evaluation/", versions,"1MBModular",writer);
		writer.close();
	}

}
