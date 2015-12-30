package network.module.data;

public abstract class TestData {

	public static final String TestAUri = "TestAUri";
	public static final String TestBUri = "TestBUri";

	protected String mUri;

	public TestData(String uri) {
		setUri(uri);
	}

	public String getUri() {
		return mUri;
	}

	public void setUri(String uri) {
		this.mUri = uri;
	}
}
