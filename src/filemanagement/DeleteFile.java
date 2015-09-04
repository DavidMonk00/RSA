package filemanagement;


import java.io.File;  

public class DeleteFile {  
	public DeleteFile(String filePath) {  
		try {  
			File fileToDelete = new File(filePath);  
			if (fileToDelete.delete()) {  
//				System.out.println("File deleted successfully!");  
			}
			else {  
//				System.out.println("File delete operation failed!");  
			}  
		}
		catch (Exception e) {  
			e.printStackTrace();  
		}  
 	}  
}  
