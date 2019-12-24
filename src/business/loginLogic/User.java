package business.loginLogic;

public class User {

	// Fields
	// ------------------------------------------------------------------------

	private static User uniqueUser;
	private String username = "Not yet initialized";
	private String type = "Not yet initialized";

	// Constructor
	// ------------------------------------------------------------------------

	private User(String username, String type) {
		this.username = username;
		this.type = type;
	}

	// Basic methods
	// ------------------------------------------------------------------------

	public static User getInstance(String username, String type) {
		if (uniqueUser == null) {
			uniqueUser = new User(username, type);
		}
		return uniqueUser;
	}

	public String getType() {
		return type;
	}

	public String getUsername() {
		return username;
	}

	@Override
	public String toString() {
		return String.format("User [%s] of type [%s]", username, type);
	}

	public String toStringEs() {
		return String.format("Usuario [%s] de tipo [%s]", username, type);
	}

}
