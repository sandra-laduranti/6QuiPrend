package metier;
/**
 * @(#) Compte.java
 */

public class Compte
{
	private int compte_Id;
	
	private String compte_Login;
	
	private String compte_Nom;
	
	private String compte_Prenom;
	
	private String compte_mdp;

	public Compte(int compte_Id, String compte_Login, String compte_Nom,
			String compte_Prenom, String compte_mdp) {
		super();
		this.compte_Id = compte_Id;
		this.compte_Login = compte_Login;
		this.compte_Nom = compte_Nom;
		this.compte_Prenom = compte_Prenom;
		this.compte_mdp = compte_mdp;
	}
	
	public Compte(){	}

	public int getCompte_Id() {
		return compte_Id;
	}

	public void setCompte_Id(int compte_Id) {
		this.compte_Id = compte_Id;
	}

	public String getCompte_Login() {
		return compte_Login;
	}

	public void setCompte_Login(String compte_Login) {
		this.compte_Login = compte_Login;
	}

	public String getCompte_Nom() {
		return compte_Nom;
	}

	public void setCompte_Nom(String compte_Nom) {
		this.compte_Nom = compte_Nom;
	}

	public String getCompte_Prenom() {
		return compte_Prenom;
	}

	public void setCompte_Prenom(String compte_Prenom) {
		this.compte_Prenom = compte_Prenom;
	}

	public String getCompte_mdp() {
		return compte_mdp;
	}

	public void setCompte_mdp(String compte_mdp) {
		this.compte_mdp = compte_mdp;
	}
	
	
}
