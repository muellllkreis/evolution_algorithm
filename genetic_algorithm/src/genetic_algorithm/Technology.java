package genetic_algorithm;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Technology extends Development {

	Subtype subtype;
	
	public Technology(String name, DevelopmentType type, Subtype subtype, List<Integer> buffs, ArrayList<Double> rates,
			ArrayList<Integer> minAtts, ArrayList<Integer> maxAtts, HashSet<String> requiredDevs,
			HashSet<HashSet<String>> optionalDevs, HashSet<Subtype> requiredSubtypes, HashSet<String> requiredResources, HashSet<HashSet<String>> optionalResources, HashSet<String> requiredResourceType, HashMap<String, Integer> cost) {
		super(name, type, buffs, rates, minAtts, maxAtts, requiredDevs, optionalDevs, requiredSubtypes, requiredResources, optionalResources, requiredResourceType, cost);
		this.subtype = subtype;
	}
	
	public String toString() {
		return super.toString();
	}
}
