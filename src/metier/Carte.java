package metier;

public class Carte {

	private int value;
	private int beefHead;

	/**
	 * Crée une Carte, avec son BeefHead calculé suivant sa valeur
	 * @param val
	 */
	public Carte(int val) {
		this.value = val;
		if(value==55){
			beefHead=7;
		} else if(value%10==5){ // 15,25,35,...
			beefHead=2;
		} else if(value%10==0){
			beefHead=3;
		} else if(value <100 && value%11==0){		/*verifier si les 2 chiffres sont les memes*/
			beefHead=5;
		} else {			   // Cartes normales (23,84,..)
			beefHead = 1;
		}
	}

	
	public int getValue() {
		return value;
	}

	public int getBeefHead() {
		return beefHead;
	}
}
