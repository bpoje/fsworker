package hash;

import java.security.MessageDigest;

public class Hash {
	public static String getMd5FromFileData(byte fileData[])
	{
		String md5 = null;
		
		try
		{
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			byte[] md5digest = messageDigest.digest(fileData);
			
			//convert the byte to hex format
	        StringBuffer md5HexString = new StringBuffer();
	    	for (int i = 0; i < md5digest.length; i++)
	    	{
	    		String hex=Integer.toHexString(0xff & md5digest[i]);
	   	     	if(hex.length()==1) md5HexString.append('0');
	   	     md5HexString.append(hex);
	    	}
	    	
	    	md5 = md5HexString.toString();
	    	
	    	System.out.println("MD5 digest(in hex format):: " + md5);
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
		
		return md5;
	}
}
