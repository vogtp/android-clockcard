package ch.almana.android.stechkarte.provider;


public class UriTableMapping {
	public UriTableMapping(String tableName, String contentItemName, String contentType, String contentItemType) {
		super();
		this.tableName = tableName;
		this.contentItemName = contentItemName;
		this.contentType = contentType;
		this.contentItemType = contentItemType;
	}
	String tableName;
	String contentItemName;
	String contentType;
	String contentItemType;
}