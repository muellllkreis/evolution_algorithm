package genetic_algorithm;

public class Material extends Resource {

	public Material(String name) {
		super(name);
	}
	
	@Override
	public String toString() {
		return super.name;
	}
}
