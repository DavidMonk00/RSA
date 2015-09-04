package filemanagement;

import java.io.File;  
import java.io.IOException;  
  
public class CreateFile {  
	public void createFile(String filePath) {  
		File file = new File(filePath);  
		try {  
			if (file.createNewFile()) {  
				System.out.println("File " + filePath  
						+ " created successfully !");  
			}
			else {  
				System.out.println("File " + filePath 
						+ " already exixts !");  
			}  
		}
		catch (IOException e) {  
			e.printStackTrace();  
		}  
	}  
}  
