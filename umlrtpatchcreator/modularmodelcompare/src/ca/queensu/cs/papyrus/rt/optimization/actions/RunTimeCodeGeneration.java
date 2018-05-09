package ca.queensu.cs.papyrus.rt.optimization.actions;

import java.util.Scanner;

public class RunTimeCodeGeneration {
	
   public static void main(String impactedElementsFilePath) {
		String modelFilePath;
		GitAction action = new GitAction();
		modelFilePath = action.filePathCodeGen;
      try {
    	  System.out.println(impactedElementsFilePath);
    	  System.out.println(modelFilePath);
    	  
    	  String[] fixedCmd = {
    	            "java",
    	            "-jar",
    	            "/Users/kanchan/workspace-papyrus-rt_user/ca.queensu.cs.papyrus.rt.optimization/executable/generator.jar",
    	            "-p",
    	            "/Users/kanchan/workspace-papyrus-rt_user/plugins",
    	            "-o",
    	            "/Users/kanchan/workspace-papyrus-rt_user/ca.queensu.cs.papyrus.rt.optimization/test/gen/test6",
    	            "-c",
    	            impactedElementsFilePath,
    	            modelFilePath
    	         };

    	  final long startTime = System.currentTimeMillis();
    	  System.out.println("Start execution time: " +  startTime );

    	  
         Process process = Runtime.getRuntime().exec(fixedCmd);
         Scanner reader = new Scanner(process.getInputStream());
         while(reader.hasNextLine()) {
        	 	System.out.println(reader.nextLine());
         } 
         process.waitFor();
         reader.close();
   	  final long endTime = System.currentTimeMillis();
	  System.out.println("Total execution time: " + (endTime - startTime) );


      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }
}