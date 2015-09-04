package main;

import java.io.IOException;
import keygen.PublicKey;
import filemanagement.FileData;
import java.math.BigInteger;
import java.io.File;
import filemanagement.DeleteFile;
import javax.swing.JOptionPane;

public class Encrypt {
	String key_file;
	PublicKey publickey;
	String filepath;
	
	public BigInteger[][] m;
	public String[] c;
	public Encrypt(String key_file, String filepath){
		this.key_file = key_file;
		FileData file = new FileData(key_file);
		String[] lines = null;
		try {
			lines = file.Read();
		} 
		catch (IOException e) {
			JOptionPane.showMessageDialog(null, key_file + " does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
		}
		this.publickey = new PublicKey(new BigInteger(lines[0]), new BigInteger(lines[1]));
		this.filepath = filepath;
	}
	public void ConvertFile(){
		FileData file = new FileData(this.filepath);
		String[] lines = null;
		try {
			lines = file.Read();
		} 
		catch (IOException e) {
			JOptionPane.showMessageDialog(null, "File does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
		}
		BigInteger[][] array = new BigInteger[lines.length][];
		for (int i = 0; i < lines.length; i++){
			char[] char_array = lines[i].toCharArray();
			String[] int_array = new String[char_array.length];
			for (int j = 0; j < char_array.length; j++){
				int_array[j] = String.format("%04d", (int)char_array[j]);
			}
			StringBuilder builder = new StringBuilder();
			for(String s: int_array){
				builder.append(s);
			}
			String int_string = builder.toString();
			int n_length = publickey.n.toString().length() - 2;
			int key_number = (int) Math.ceil(int_string.length()/n_length) + 1;
			BigInteger[] split_array = new BigInteger[key_number];
			for (int j = 0; j < key_number; j++){
				int index = j*n_length;
				if (index + n_length < int_string.length()){
					split_array[j] = new BigInteger("1" + int_string.substring(index, index + n_length));
				}
				else {
					split_array[j] = new BigInteger("1" + int_string.substring(index));
				}
				array[i] = split_array;
			}
		}
		this.m = array;
	}
	public void EncryptFile(){
		String[] c = new String[this.m.length];
		for (int i = 0; i < this.m.length; i++){
			String[] x = new String[this.m[i].length];
			for (int j = 0; j < this.m[i].length; j++){
				x[j] = m[i][j].modPow(this.publickey.e, this.publickey.n).toString();
			}
			StringBuilder builder = new StringBuilder();
			for(String s: x){
				builder.append(s + "-");
			}
			String x_string = builder.toString();
			c[i] = x_string;
		}
		this.c = c;
	}
	public void SaveFile(){
		//Create file
		String new_filepath = this.filepath.split("\\.")[0] + ".encrypt";
		File file = new File(new_filepath);  
		try {  
			file.createNewFile();
		}
		catch (IOException e) {  
			e.printStackTrace();  
		}
		//Write to file
		try {
			FileData.Write(new_filepath, this.filepath.split("\\.")[1], false);
			FileData.Write(new_filepath, this.key_file.split("/")[this.key_file.split("/").length - 1], true);
		} 
		catch (IOException e) {
		}
		for (int i = 0; i < this.c.length; i++){
			try {
				FileData.Write(new_filepath, this.c[i], true);
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
