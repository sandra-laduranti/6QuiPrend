package metier;

import java.io.Serializable;

/**
 * @(#) Compte.java
 */

public class User implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int userId;
	
	private String userNickname;
	
	private String userEmail;
	
	private String userPassword;
	
	private int currentBeef;

	public User(String userNickname, String userEmail,
			String userPassword) {
		this.userNickname = userNickname;
		this.userEmail = userEmail;
		this.userPassword = userPassword;
		this.currentBeef = 0;
	}
	
	public User(int userId, String userNickname, String userEmail, String userPassword){	
		this(userNickname, userEmail, userPassword);
		this.userId = userId;
	}
	
	public User(User user){
		this(user.getUserNickname(), user.getUserEmail(), user.getUserPassword());
	}
	
	public int getUserId() {
		return userId;
	}


	public String getUserNickname() {
		return userNickname;
	}

	public void setUserNickname(String userNickname) {
		this.userNickname = userNickname;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public int getCurrentBeef() {
		return currentBeef;
	}
	
	public void setCurrentBeef(int currentBeef) {
		this.currentBeef = currentBeef;
	}
}
