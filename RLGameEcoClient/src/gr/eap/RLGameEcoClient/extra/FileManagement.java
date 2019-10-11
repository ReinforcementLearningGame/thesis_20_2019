package gr.eap.RLGameEcoClient.extra;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileManagement {
	
	public static void createfile(String name) {
		
		String myFile = System.getProperty("user.dir") + System.getProperty("file.separator") + name;
		File file = new File(myFile);
		
		try {
			if (file.exists()){
				file.delete();
			}
			file.createNewFile();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}		
	}
	
	
	public static void appendToFile(String name, String message){
		
		String myFile = System.getProperty("user.dir") + System.getProperty("file.separator") + name;
		String type = name.substring(name.lastIndexOf(".")+1);
		
		FileWriter myFR = null;
        BufferedWriter myBW = null;
                
        try {
        	myFR = new FileWriter(myFile, true);
            myBW = new BufferedWriter(myFR);
            
            switch(type){
            case "txt":
            	myBW.write(message+System.getProperty("line.separator"));
            	break;
            case "log":
            	String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
            	myBW.write(timeStamp +": "+message+System.getProperty("line.separator"));
            	break;
            default:
            	//TO DO //
            }
        }
        catch(IOException e){
        	System.err.println(e.getMessage());
        }
        finally {
            try {
                if (myBW != null) {myBW.close();}
                if (myFR != null) {myFR.close();}
            }
            catch (IOException e){
            	System.err.println(e.getMessage());
            }
        }
        
	}
	
	public static void absoluteAppendToFile(String name, String message){
				
		FileWriter myFR = null;
        BufferedWriter myBW = null;
                
        try {
        	myFR = new FileWriter(name, true);
            myBW = new BufferedWriter(myFR);
            
           	String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
           	myBW.write(timeStamp +": "+message+System.getProperty("line.separator"));
        }
        catch(IOException e){
        	System.err.println(e.getMessage());
        }
        finally {
            try {
                if (myBW != null) {myBW.close();}
                if (myFR != null) {myFR.close();}
            }
            catch (IOException e){
            	System.err.println(e.getMessage());
            }
        }
        
	}
	
}
