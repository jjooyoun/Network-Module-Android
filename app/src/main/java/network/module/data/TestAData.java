package network.module.data;

public class TestAData extends TestData {

	private String mEmail;
	private String mPassword;
	private String mUsername;

	public TestAData() {
		super(TestAUri);
	}

	public String getEmail() {
		return mEmail;
	}

	public void setEmail(String email) {
		this.mEmail = email;
	}

	public String getPassword() {
		return mPassword;
	}

	public void setPassword(String password) {
		this.mPassword = password;
	}

	public String getUsername() {
		return mUsername;
	}

	public void setUsername(String username) {
		this.mUsername = username;
	}
}
