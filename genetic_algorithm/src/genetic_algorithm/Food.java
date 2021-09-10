package genetic_algorithm;

public class Food extends Resource {
	
	boolean edible;
	int health;

	public Food(String name, boolean edible, int health) {
		super(name);
		this.edible = edible;
		this.health = health;
	}

}
