package main;

import keygen.Key;
import javax.swing.JOptionPane;
import java.io.*;
import java.net.SocketException;
import org.apache.commons.net.ftp.*;
import filemanagement.DeleteFile;

public class main {
	public static String GenerateKey(int strength) throws SocketException, IOException{
		Key key = new Key(strength);
		key.pGen();
		key.qGen();
		key.nGen();
		key.dGen();
		key.PublicKeyGen();
		key.PrivateKeyGen();
		key.SaveKey();
		key.UploadKey();
		key.DeleteFile();
		return String.valueOf(key.key_length);
	}
	public static void Encryption(String key_file, String filepath){
		Encrypt x = new Encrypt(key_file, filepath);
		x.ConvertFile();
		x.EncryptFile();
		x.SaveFile();
		x.DeleteFile();
	}
	public static void Decryption(String filepath) throws IOException{
		Decrypt x = new Decrypt(filepath);
		x.DecryptFile();
		x.CreateFile();
		x.DeleteFile();
	}
	public static String[] ShowFiles(String folderpath){
		// Shows all files within a certain folder
		File folder = new File(folderpath);
		File[] listOfFiles = folder.listFiles();
		String[] files = new String[listOfFiles.length];
		for (int i = 0; i < listOfFiles.length; i++){
			if (listOfFiles[i].isFile()){
			   files[i] = listOfFiles[i].getName();
			}	
		}
		return files;
	}
	public static void main( String[] args) throws NumberFormatException, SocketException, IOException{
		//Choose option
		String[] chooseprogram = {"Create key", "Encryption", "Decryption"};
		Object selectedValue = JOptionPane.showInputDialog(null,
		        "Choose option:", "RSA",
		        JOptionPane.PLAIN_MESSAGE, null,
		        chooseprogram, chooseprogram[0]);
		// Key Generation
		if (selectedValue == "Create key"){
			String strength = JOptionPane.showInputDialog("Enter desired strength:");
			if (strength != null){
				String key_length = GenerateKey(Integer.parseInt(strength));
				JOptionPane.showMessageDialog(null,"Key generated.\nKey Length: " + key_length);
			}	
		}
		// Encrypt File
		else if (selectedValue == "Encryption"){
			String filepath = JOptionPane.showInputDialog("Please enter filepath for file you wish to encrypt:");
			FTPClient ftp = new FTPClient();
			FTPClientConfig config = new FTPClientConfig();
			ftp.configure(config);
			String server = "nicmach.comxa.com";
			ftp.connect(server);
			String pwd = JOptionPane.showInputDialog("Please enter password for server:");
		    ftp.login("a8558342", pwd);
		    int reply = ftp.getReplyCode();
		    if(!FTPReply.isPositiveCompletion(reply)) {
		        ftp.disconnect();
		        System.err.println("FTP server refused connection.");
		        System.exit(1);
		    }
		    FTPFile[] files = ftp.listFiles("/public_html/keys");
		    String[] keys = new String[files.length - 2];
		    for (int i = 2; i < files.length; i++){
		    	keys[i - 2] = files[i].toString().split(" ")[files[i].toString().split(" ").length - 1];
		    }
			String key_chosen = JOptionPane.showInputDialog(null, "Choose key to encrypt with:", "RSA",
			        JOptionPane.PLAIN_MESSAGE, null,
			        keys, keys[0]).toString();
			OutputStream output = new FileOutputStream(System.getProperty("user.home") + "/" + key_chosen);
			ftp.retrieveFile("/public_html/keys/" + key_chosen, output);
			output.close();
			ftp.logout();
		    ftp.disconnect();
		    Object[] options = {"OK", "Cancel"};
		    int cont = JOptionPane.showOptionDialog(null, "Are you sure you wish to encrypt this file?", "Warning",
		            JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
		            null, options, options[0]);
		    if (cont == 0){
		    	Encryption(System.getProperty("user.home") + "/" + key_chosen, filepath);
		    	JOptionPane.showMessageDialog(null,"Encryption Complete");
		    }
		    DeleteFile a = new DeleteFile(System.getProperty("user.home") + "/" + key_chosen);
		}
		// Decrypt File
		else if (selectedValue == "Decryption"){
			String filepath_decryption = JOptionPane.showInputDialog("Please enter filepath for file you wish to decrypt:");
			if (filepath_decryption != null){
				Decryption(filepath_decryption);
				JOptionPane.showMessageDialog(null,"Decryption Complete");
			}
		}
		System.exit(0);
	}			
}
 