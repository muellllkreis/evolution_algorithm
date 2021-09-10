package genetic_algorithm;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Organization extends Development {

	public Organization(String name, DevelopmentType type, List<Integer> buffs, ArrayList<Double> rates,
			ArrayList<Integer> minAtts, ArrayList<Integer> maxAtts, HashSet<String> requiredDevs,
			HashSet<HashSet<String>> optionalDevs, HashSet<Subtype> requiredSubtypes, HashSet<String> requiredResources, HashSet<HashSet<String>> optionalResources, HashSet<String> requiredResourceType, HashMap<String, Integer> cost) {
		super(name, type, buffs, rates, minAtts, maxAtts, requiredDevs, optionalDevs, requiredSubtypes, requiredResources, optionalResources, requiredResourceType, cost);
		}
}
