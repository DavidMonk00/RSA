package main;

import keygen.PrivateKey;
import java.io.*;

import filemanagement.FileData;
import java.math.BigInteger;
import filemanagement.DeleteFile;
import javax.swing.JOptionPane;
import org.apache.commons.net.ftp.*;

public class Decrypt {
	String key_file;
	PrivateKey privatekey;
	String filepath;
	String[] m;
	String file_extension;
	private void GetKey() throws IOException{
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
		OutputStream output = new FileOutputStream(System.getProperty("user.home") + "/" + this.key_file);
		ftp.retrieveFile("/public_html/keys/" + this.key_file, output);
		output.close();
		ftp.logout();
	    ftp.disconnect();
		FileData file = new FileData(System.getProperty("user.home") + "/" + this.key_file);
		String[] lines = null;
		try {
			lines = file.Read();
		} 
		catch (IOException e) {
			JOptionPane.showMessageDialog(null, key_file + " does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
		}
		this.privatekey = new PrivateKey(new BigInteger(lines[0]), new BigInteger(lines[2]));
	}
	public Decrypt(String filepath){
		this.filepath = filepath;
	}
	public void DecryptFile() throws IOException{
		FileData file = new FileData(this.filepath);
		String[] lines = null;
		try {
			lines = file.Read();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		this.file_extension = lines[0];
		this.key_file = lines[1];
		GetKey();
		String[] m = new String[lines.length - 2];
		for (int i = 2; i < lines.length; i++){
			String[] array = lines[i].split("-");
			String[] m_line = new String[array.length];
			for (int j = 0; j < array.length; j++){
				BigInteger integer = new BigInteger(array[j]);
				m_line[j] = integer.modPow(this.privatekey.d, this.privatekey.n).toString().substring(1);
			}
			StringBuilder builder = new StringBuilder();
			for(String s: m_line){
				builder.append(s);
			}
			String int_string = builder.toString();
			String[] char_array = new String[int_string.length()/4];
			for (int j = 0; j < int_string.length(); j = j + 4){
				char_array[j/4] = Character.toString((char) Integer.parseInt(int_string.substring(j, j + 4)));
			}
			StringBuilder b = new StringBuilder();
			for(String s: char_array){
				b.append(s);
			}
			m[i - 2] = b.toString();
		}
		this.m = m;
		
	}
	public void CreateFile(){
		//Create file
				String new_filepath = this.filepath.split("\\.")[0] + "." + this.file_extension;
				File file = new File(new_filepath);  
				try {  
					file.createNewFile();
				}
				catch (IOException e) {  
					e.printStackTrace();  
				}
				//Write to file
				for (int i = 0; i < this.m.length; i++){
					try {
						FileData.Write(new_filepath, this.m[i], true);
					} 
					catch (IOException e) {
					}
				}
	}
	public void DeleteFile(){
		@SuppressWarnings("unused")
		DeleteFile a = new DeleteFile(this.filepath);
	}
}
