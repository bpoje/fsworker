package gui;

public class TableRowData
{
	private String source = null;
	private String client;
	private String spouse;
	private String family;
	private boolean isRoot;
	/**
	 * Created on: Feb 23, 2008
	 * @Author: sandarenu
	 * @param source
	 * @param client
	 * @param spouse
	 * @param family
	 * @param isRoot
	 */
	public TableRowData(String source, String client, String spouse, String family, boolean isLeaf)
	{
		this.source = source;
		this.client = client;
		this.spouse = spouse;
		this.family = family;
		this.isRoot = isLeaf;
	}
	/**
	 * @return the client
	 */
	public String getClient()
	{
		return client;
	}
	/**
	 * @param client the client to set
	 */
	public void setClient(String client)
	{
		this.client = client;
	}
	/**
	 * @return the family
	 */
	public String getFamily()
	{
		return family;
	}
	/**
	 * @param family the family to set
	 */
	public void setFamily(String family)
	{
		this.family = family;
	}
	/**
	 * @return the isRoot
	 */
	public boolean isRoot()
	{
		return isRoot;
	}
	/**
	 * @param isRoot the isRoot to set
	 */
	public void setRoot(boolean isLeaf)
	{
		this.isRoot = isLeaf;
	}
	/**
	 * @return the source
	 */
	public String getSource()
	{
		return source;
	}
	/**
	 * @param source the source to set
	 */
	public void setSource(String source)
	{
		this.source = source;
	}
	/**
	 * @return the spouse
	 */
	public String getSpouse()
	{
		return spouse;
	}
	/**
	 * @param spouse the spouse to set
	 */
	public void setSpouse(String spouse)
	{
		this.spouse = spouse;
	}
	
}
