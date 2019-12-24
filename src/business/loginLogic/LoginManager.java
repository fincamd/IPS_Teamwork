package business.loginLogic;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import dtos.UserDto;
import factories.ServiceFactory;

public class LoginManager {

	// Fields
	// ------------------------------------------------------------------------

	private String username, password;
	private boolean wasLoginSuccessful;

	// Constructors
	// ------------------------------------------------------------------------

	public LoginManager(String username, String password) {
		this.username = username;
		this.password = password;
	}

	// Basic methods
	// ------------------------------------------------------------------------

	public void process() {
		PasswordCypher cypher = new PasswordCypher();
		String sha512Hash = cypher.digest(password, "SHA-512");
		String md5Hash = cypher.digest(password, "MD5");
		UserDto userToLogin =
				ServiceFactory.createUserService().findUserBySurname(username);
		wasLoginSuccessful = hashesMatch(sha512Hash, userToLogin.shaHash)
				&& hashesMatch(md5Hash, userToLogin.md5Hash);
		if (wasLoginSuccessful) {
			User.getInstance(username, userToLogin.type);
		}
	}

	public boolean wasLoginSuccessful() {
		return wasLoginSuccessful;
	}

	// Auxiliary methods
	// ------------------------------------------------------------------------

	private boolean hashesMatch(String digestedPassword,
			String usernameDBHashed) {
		return digestedPassword.equals(usernameDBHashed);
	}

	// Auxiliary classes
	// ------------------------------------------------------------------------

	private class PasswordCypher {

		private final byte[] SALT = "ips-19-20_pwSHA-512&&Md-5".getBytes();
//		private final byte[] SALT2 = "casaDeCampoRolNumero".getBytes();

		private PasswordCypher() {
			/* Empty but private */
		}

		private String digest(String password, String hashingMethod) {
			try {
				MessageDigest md = MessageDigest.getInstance(hashingMethod);
				md.update(SALT);
				byte[] messageDigest =
						md.digest(password.getBytes(StandardCharsets.UTF_8));
				BigInteger no = new BigInteger(1, messageDigest);
				String hashtext = no.toString(16);
				while (hashtext.length() < 32) {
					hashtext = "0" + hashtext;
				}
				return hashtext;
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException(e);
			}
		}

	}
}
