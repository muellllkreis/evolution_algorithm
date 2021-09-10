package genetic_algorithm;

public class Animal extends Resource {
	private Nature nature;
	private boolean carriesStructures;
	
	public Animal(String name, Nature nature, boolean carriesStructures) {
		super(name);
		this.nature = nature;
		this.carriesStructures = carriesStructures;
	}
}
