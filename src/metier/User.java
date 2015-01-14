package metier;

import java.io.Serializable;

/**
 * @(#) Compte.java
 */

public class User implements Serializable, Comparable<User>
{
	/**
	 * 
	 */
	private static transient final long serialVersionUID = 1L;

	private transient int userId;
	
	private String userNickname;
	
	private String userEmail;
	
	private String userPassword;
	
	private int currentBeef;

	public User(String userNickname, String userEmail,	String userPassword) {
		this.userNickname = userNickname;
		this.userEmail = userEmail;
		this.userPassword = userPassword;
		this.currentBeef = 0;
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

	public int getCurrentBeef() {
		return currentBeef;
	}
	
	public void setCurrentBeef(int currentBeef) {
		this.currentBeef = currentBeef;
	}

	public int compareTo(User user) {
		if(this.getCurrentBeef()>user.getCurrentBeef()){
			return 1;
		} else if(this.getCurrentBeef()<user.getCurrentBeef()){
			return -1;
		} else {
			return 0;
		}
	}

}
