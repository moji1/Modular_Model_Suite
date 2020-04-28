package ca.jahed.papyrusrt.livemodeling.debug.standalone;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ProcessBuilder.Redirect;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import javax.xml.bind.DatatypeConverter;

public class LiveSessionExecutor {
	
	public static void main(String[] args) throws Exception {
		
		if(args.length != 1) {
			System.err.println("Usage: LiveSessionExecutor <model>");
			System.exit(1);
		}
				
		String packageDir = "/Users/kjahed/Desktop/moji/livemodeling/pmd_helper";
		String modelPath = args[0];
		
		File modelFile = new File(modelPath);
		File buildDir = new File("/tmp/livemodeling_"+modelFile.getName());
		if(!buildDir.exists())
			buildDir.mkdirs();
		
		if(!runCmake(packageDir, buildDir.getAbsolutePath()))
			System.exit(1);
		
		if(!runMake(buildDir.getAbsolutePath()))
			System.exit(1);
		
		File binDir = new File(buildDir, "bin");
		File executable = new File(binDir, "Top_Refined");
		
		execute(binDir, executable.getAbsolutePath());
		System.exit(0);
	}
	
	public static void copyEnvironment(String environment, String packageDir) throws IOException {
		Files.copy(Paths.get(environment), Paths.get(packageDir));
	}
	
	public static boolean runCmake(String packageDir, String buildDir) {
		try {
			ProcessBuilder builder = new ProcessBuilder("/usr/local/bin/cmake", "-H"+packageDir ,"-B", buildDir);
			builder.redirectOutput(Redirect.INHERIT);
			builder.redirectError(Redirect.INHERIT);
			    
			Process process = builder.start();
			process.waitFor();
			return process.exitValue() >= 0;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static boolean runMake(String buildDir) {
		try {
			ProcessBuilder builder = new ProcessBuilder("/usr/bin/make", "-C", buildDir);
			builder.redirectOutput(Redirect.INHERIT);
			builder.redirectError(Redirect.INHERIT);
			    
			Process process = builder.start();
			process.waitFor();
			return process.exitValue() >= 0;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static void execute(File binDir, String executablePath) {
		try {
			ProcessBuilder builder = new ProcessBuilder(executablePath);
			builder.directory(binDir);
			builder.redirectError(Redirect.INHERIT);
			
			Process process = builder.start();
			BufferedWriter processWriter = new BufferedWriter (
					new OutputStreamWriter(process.getOutputStream()));
			BufferedReader processReader = new BufferedReader (
					new InputStreamReader(process.getInputStream()));
			
			Socket diagramSock = new Socket("localhost", 6969);
			BufferedWriter diagramOut = new BufferedWriter(
					new PrintWriter(diagramSock.getOutputStream()));
			BufferedReader diagramIn = new BufferedReader (
					new InputStreamReader(diagramSock.getInputStream()));
			
			
			Thread diagramInHandler = new Thread(new Runnable() {	
				@Override
				public void run() {
					try {
						String line;
						while(process.isAlive() && (line = diagramIn.readLine()) != null) {
							synchronized (processWriter) {
								processWriter.write(line+"\n");
								processWriter.flush();
							}
							
							System.out.println(line);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
				}
			});
			diagramInHandler.start();
			
			
			Thread stdinHandler = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Socket daSock = null;
						BufferedWriter daOut = null;
						
						daSock = new Socket("localhost", 9696);
						daOut = new BufferedWriter(
								new PrintWriter(daSock.getOutputStream()));
						
						BufferedReader stdinReader = new BufferedReader (
								new InputStreamReader(System.in));
						
						int c;
						String soFar = "";
						while(process.isAlive() && (c = stdinReader.read()) != -1) {
							synchronized (processWriter) {
								soFar += (char)c;
								processWriter.write(c);
								processWriter.flush();
							}
							
							if(soFar.equals("b\n")) {
								System.out.print("break# ");
								String capsuleName = stdinReader.readLine().trim();
								daOut.write(capsuleName);
								daOut.flush();
								soFar = "";
							}
//
//							if(soFar.startsWith("next\n")) {
//								soFar = "";
//								if(diagramOut != null) {
//									synchronized (diagramOut) {
//										diagramOut.write("reset");
//										diagramOut.flush();
//									}
//								}
//							}
//							
							if(soFar.startsWith("exit\n")) {
								break;
							}
							
							if((char)c == '\n') {
								soFar = "";
							}
						}
						
						stdinReader.close();
						
						daOut.close();
						daSock.close();
					} catch (IOException e) {
						e.printStackTrace();
						return;
					}
				}
			});
			stdinHandler.start();
			
			int c;
			String soFar = "";
			while ((c = processReader.read()) != -1) {
				soFar += (char)c;
				System.out.print((char)c);
				
//				// got end of context
//				if(soFar.contains(" |-----------------------------------------------------|")) {
//					Scanner tokenizer = new Scanner(soFar);
//					
//					while(tokenizer.hasNextLine()) {
//						String line = tokenizer.nextLine();
//						int capTypeIdx = line.indexOf("Capsule Type |");
//						if(capTypeIdx != -1) {
//							String capsuleName = line.substring(capTypeIdx+15, line.length()-2).trim();
//							
//							// skip a line
//							tokenizer.nextLine();
//							
//							line = tokenizer.nextLine();
//							int stateNameIdx = line.indexOf("Current State |");
//							String stateName = line.substring(stateNameIdx+16, line.length()-2).trim();
//							if(diagramOut != null) {
//								synchronized (diagramOut) {
//									diagramOut.write("goto,"+capsuleName+","+stateName+"\n");
//									diagramOut.flush();
//								}
//							}
//						}
//					}
//					
//					tokenizer.close();
//					soFar = "";
//				}
			}
			
			processWriter.close();
			processReader.close();
			process.destroy();
			
			stdinHandler.join();
			diagramInHandler.join();
			
			diagramIn.close();
			diagramOut.close();
			diagramSock.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String md5(Path file) {
		try {
			byte[] b = Files.readAllBytes(file);
			byte[] hash = MessageDigest.getInstance("MD5").digest(b);
			return DatatypeConverter.printHexBinary(hash);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}