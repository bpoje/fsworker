package filesystem.io;

import filesystem.hash.Hash;

public class DataTransfer {
	private String md5;
	private byte [] payload;
	
	public DataTransfer(byte[] payload)
	{
		this.payload = payload;
		recalculateMd5FromPayload();
	}
	
	public DataTransfer(byte[] payload, String md5)
	{
		this.md5 = md5;
		this.payload = payload;
	}
	
	boolean isMd5Equal(String currentMd5)
	{
		return currentMd5.compareToIgnoreCase(md5) == 0;
	}
	
	void recalculateMd5FromPayload()
	{
		//If not folder
		if (this.payload != null) {
			this.md5 = Hash.getMd5FromByteArray(payload);
		}
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public byte[] getPayload() {
		return payload;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}
}
