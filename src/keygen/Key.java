package keygen;

import java.math.BigInteger;
import java.util.Random;
import java.io.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import filemanagement.FileData;
import javax.swing.JOptionPane;
import filemanagement.DeleteFile;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.net.ftp.*;

public class Key {
	int strength;
	BigInteger start;
	BigInteger start_second;
	BigInteger p;
	BigInteger q;
	BigInteger n;
	BigInteger phi_n;
	public int key_length;
	BigInteger e = BigInteger.valueOf(65537);
	BigInteger d;
	public PublicKey publickey;
	public PrivateKey privatekey;
	String filepath;
	
	public Key(int exp){
		this.strength = exp;
		this.start = BigInteger.valueOf(2).pow(exp).add(new BigInteger(exp, new Random()));
		this.start_second = this.start.add(BigInteger.valueOf(2).pow(exp-2));
	}
	public void pGen() {
		this.p = PrimeGen.PrimeGen(this.start);
	}
	public void qGen() {
		this.q = PrimeGen.PrimeGen(this.start_second);
	}
	public void nGen() {
		this.n = this.p.multiply(this.q);
		this.phi_n = this.n.subtract(this.p.add(this.q.subtract(BigInteger.ONE)));
		this.key_length = this.n.bitLength();
	}
	public void dGen() {
		this.d = this.e.modInverse(phi_n);
	}
	public void PublicKeyGen(){
		this.publickey = new PublicKey(n,e);
	}
	public void PrivateKeyGen(){
		this.privatekey = new PrivateKey(n,d);
	}
	private String getDateTime() {
	     DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	     Date date = new Date();
	     return dateFormat.format(date);
	 }
	public void SaveKey(){
		Path path = Paths.get(System.getProperty("user.home") + "/keys");
		if (Files.notExists(path)) {
			File dir = new File(System.getProperty("user.home") + "/keys");
			dir.mkdir();
		}
		//Create file
		this.filepath = System.getProperty("user.home") + "/" + String.valueOf(this.strength)+ "_" + getDateTime();
		
		File file = new File(this.filepath);  
		try {  
		file.createNewFile();
		}
		catch (IOException e) {  
		e.printStackTrace();  
		}
		//Write to file
		try {
			FileData.Write(this.filepath, publickey.n.toString(),true);
			FileData.Write(this.filepath, publickey.e.toString(),true);
			FileData.Write(this.filepath, privatekey.d.toString(),true);
		} 
		catch (IOException e) {
		}
	}
	public void UploadKey() throws SocketException, IOException{
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
	    InputStream input = new FileInputStream(this.filepath);
	    ftp.storeFile("/public_html/keys/" + String.valueOf(this.strength)+ "_" + getDateTime(), input);
	    input.close();
	    ftp.logout();
	    ftp.disconnect();
	}
	public void DeleteFile(){
		@SuppressWarnings("unused")
		DeleteFile a = new DeleteFile(this.filepath);
	}
}
