package filesystem.hash;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class Hash {
	public static String getMd5FromByteArray(byte byteArray[])
	{
		String md5 = null;
		
		try
		{
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			byte[] md5digest = messageDigest.digest(byteArray);
			
			//convert the byte to hex format
	        StringBuffer md5HexString = new StringBuffer();
	    	for (int i = 0; i < md5digest.length; i++)
	    	{
	    		String hex=Integer.toHexString(0xff & md5digest[i]);
	   	     	if(hex.length()==1) md5HexString.append('0');
	   	     md5HexString.append(hex);
	    	}
	    	
	    	md5 = md5HexString.toString();
	    	
	    	//System.out.println("MD5 digest(in hex format):: " + md5);
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
		
		return md5;
	}
	
	public static String getMd5FromFile(File file)
	{
		String md5 = null;
		
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			
			byte[] buffer = new byte[1024];
			
			int numberOfReadBytes = 0;
			while ((numberOfReadBytes = fileInputStream.read(buffer)) > 0)
			{
				messageDigest.update(buffer, 0, numberOfReadBytes);
				//System.out.println("numberOfReadBytes: " + numberOfReadBytes);
			}
			
			fileInputStream.close();
			
			byte[] md5digest = messageDigest.digest();
			
			//convert the byte to hex format
	        StringBuffer md5HexString = new StringBuffer();
	    	for (int i = 0; i < md5digest.length; i++)
	    	{
	    		String hex=Integer.toHexString(0xff & md5digest[i]);
	   	     	if(hex.length()==1) md5HexString.append('0');
	   	     md5HexString.append(hex);
	    	}
	    	
	    	md5 = md5HexString.toString();
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
		
		return md5;
	}
}
