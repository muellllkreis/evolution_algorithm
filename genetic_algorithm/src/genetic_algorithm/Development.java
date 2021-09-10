package genetic_algorithm;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public abstract class Development {
	String name;
	DevelopmentType type;
	List<Integer> buffs;
	ArrayList<Double> rates;
	ArrayList<Integer> requiredMinAttributes;
	ArrayList<Integer> requiredMaxAttributes;
	HashSet<String> requiredDevelopments;
	HashSet<Subtype> requiredSubtypes;
	HashSet<String> requiredResources; 
	HashSet<HashSet<String>> optionalDevelopments;
	HashSet<HashSet<String>> optionalResources;
	HashSet<String> requiredResourceType;
	HashMap<String, Integer> cost;
	
	public Development(
			String name, 
			DevelopmentType type, 
			List<Integer> buffs,
			ArrayList<Double> rates,
			ArrayList<Integer> minAtts, 
			ArrayList<Integer> maxAtts, 
			HashSet<String> requiredDevs,
			HashSet<HashSet<String>> optionalDevs,
			HashSet<Subtype> requiredSubtypes,
			HashSet<String> requiredResources,
			HashSet<HashSet<String>> optionalResources,
			HashSet<String> requiredResourceType,
			HashMap<String, Integer> cost) {
		this.name = name;
		this.type = type;
		this.buffs = buffs;
		this.requiredMinAttributes = minAtts;
		this.requiredMaxAttributes = maxAtts;
		this.requiredDevelopments = requiredDevs;
		this.requiredSubtypes = requiredSubtypes;
		this.requiredResources = requiredResources;
		this.rates = rates;
		this.optionalDevelopments = optionalDevs;
		this.optionalResources = optionalResources;
		this.requiredResourceType = requiredResourceType;
		this.cost = cost;
	}
	
	public String toString() {
		String buffs = "";
		for(int buff: this.buffs) {
			buffs += " " + buff;
		}
		return "Development: " + this.name 
				+ "\nType: " + this.type.name() 
				+ "\nBuffs: " + buffs
				+ "\nminAtts: " + Arrays.toString(this.requiredMinAttributes.toArray())
				+ "\nmaxAtts: " + Arrays.toString(this.requiredMaxAttributes.toArray())
				+ "\nrequired Resources: " + Arrays.toString(this.requiredResources.toArray())
				+ "\nRates: " + Arrays.toString(this.rates.toArray())
				+ "\nrequired Devs: " + Arrays.toString(this.requiredDevelopments.toArray())
				+ "\none of Resources: " + Arrays.toString(this.optionalResources.toArray())
				+ "\none of Devs: " + Arrays.toString(this.optionalDevelopments.toArray());
	}
}
