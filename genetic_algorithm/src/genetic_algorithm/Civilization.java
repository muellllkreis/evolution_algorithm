package genetic_algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Civilization {
	final static int NUMBER_OF_ATTRIBUTES = Attributes.values().length;
	final static int NUMBER_OF_PARAMETERS = Parameters.values().length;
	
	// attributes in order of Attributes.java (aggression, creativity, productivity, spirituality, curiousity, courage)
	private ArrayList<Integer> attributes = new ArrayList<>(Arrays.asList(50, 50, 50, 50, 50, 50));
	
	private ArrayList<Technology> technologies = new ArrayList<>();
	private ArrayList<Discovery> discoveries = new ArrayList<>();
	private ArrayList<Policy> policies = new ArrayList<>();
	private ArrayList<Cultural> cultural = new ArrayList<>();
	
	private Environment environment;

	private ArrayList<Building> buildings = new ArrayList<>();
	
	// overall parameters health, wealth, population, intelligence, production
	ArrayList<Double> parameters = new ArrayList<>(Arrays.asList(50d, 0d, 10d, 0d, 0d));
	private int health = 50;
	private int wealth = 0;
	private int population = 10;
	private int intelligence = 0;
	private int production = 0;
	
	// per time attributes (same order as parameters: healthrate, wealthrate, poprate, intrate, prodrate)
	
	private ArrayList<Double> rates = new ArrayList<>(Arrays.asList(0d, 0d, 0d, 0d, 0d));
	
	public Civilization(Environment env) {
		this.environment = env;
	}
	
	private Attributes findOrientation() {
		int max = -1;
		int maxIndex = -1;
		boolean uniqueMax = true;
		for(int i = 0; i < this.attributes.size(); i++) {
			if(attributes.get(i) > max) {
				max = attributes.get(i);
				maxIndex = i;
				uniqueMax = true;
			}
			else if(attributes.get(i) == max) {
				maxIndex = -1;
				uniqueMax = false;
			}
		}
		if(uniqueMax == false) {
			return null;
		}
		else {
			return Attributes.values()[maxIndex];
		}
	}
	
	Set<Development> findAvailableDevelopments(Set<Development> allDevelopments) {
		Set<Development> availableDevelopments = new HashSet<>(); 
		for(Development d: allDevelopments) {
			// discoveries are random
			if(Math.random() < 0.75 && d.type == DevelopmentType.DISCOVERY) {
				continue;
			}
			// discard already unlocked developments
			List<Development> allUnlockedDevs = new ArrayList<Development>();
			allUnlockedDevs.addAll(this.discoveries);
			allUnlockedDevs.addAll(this.technologies); 
			allUnlockedDevs.addAll(this.policies); 
			allUnlockedDevs.addAll(this.cultural);
			allUnlockedDevs.addAll(this.buildings);
			if(allUnlockedDevs.stream().map(x -> x.name).collect(Collectors.toList()).contains(d.name)) {
				continue;
			}
			boolean available = true;
			for(int i = 0; i < NUMBER_OF_ATTRIBUTES; i++) {
				if(!(d.requiredMinAttributes.get(i) <= this.attributes.get(i))
					&& (d.requiredMaxAttributes.get(i) == 0 || d.requiredMaxAttributes.get(i) >= this.attributes.get(i))) {
					available = false;
					break;
				}
			}
			for(String s: d.requiredResources) {
				if(this.environment.getResources() == null) {
					available = false;
					break;
				}
				else if(!this.environment.getResources().stream().map(x -> x.name).collect(Collectors.toList()).contains(s)) {
					available = false;
					break;
				}
			}
			for(String s: d.requiredDevelopments) {
				if(!allUnlockedDevs.stream().map(x -> x.name).collect(Collectors.toList()).contains(s)) {
					available = false;
					break;
				}
			}
			for(Subtype s: d.requiredSubtypes) {
				if(!this.technologies.stream().map(x -> x.subtype).collect(Collectors.toList()).contains(s)) {
					available = false;
					break;
				}
			}
			for(String s: d.requiredResourceType) {
				if(!this.environment.getResources().stream().map(x -> x.getClass().getName()).collect(Collectors.toList()).contains(s)) {
					available = false;
					break;
				}
			}
			for(HashSet<String> s: d.optionalDevelopments) {
				if(!s.stream().anyMatch(x -> allUnlockedDevs.stream().map(y -> y.name).collect(Collectors.toList()).contains(x))) {
					available = false;
					break;	
				}
			}
			for(HashSet<String> s: d.optionalResources) {
				if(this.environment.getResources() == null) {
					available = false;
					break;
				}
				else if(!s.stream().anyMatch(x -> this.environment.getResources().stream().map(y -> y.name).collect(Collectors.toList()).contains(x))) {
					available = false;
					break;	
				}
			}
			for (Map.Entry<String,Integer> entry : d.cost.entrySet()) {
				Parameters costName = Parameters.valueOf(entry.getKey().toUpperCase());
				int costAmount = entry.getValue();
				double parameterAmount = 0d;
				switch(costName) {
				case WEALTH:
					parameterAmount = this.getHealth();
					break;
				case INTELLIGENCE:
					parameterAmount = this.getIntelligence();
					break;
				case PRODUCTION:
					parameterAmount = this.getProduction();
					break;
				default:
					break;
				}
				if(parameterAmount < costAmount) {
					available = false;
					break;	
				}
			}
			if(t)
			if(available) {
				availableDevelopments.add(d);
			}
		}
		return availableDevelopments;
	}
	
	Development chooseDevelopment(Set<Development> availableDevelopments) {
		/* 
		 * 1. find orientation (prominent attribute)
		 * 2. based on orientation, choose preference:
		 *  - no preference = optimize all
		 * 	- aggression = optimize random (later: military), prioritize military
		 *  - creativity = optimize production (later: culture), prioritize cultural
		 *  - productivity = optimize production, wealth; prioritize buildings/technology
		 *  - spirituality = optimize random (later: religion), prioritize religion
		 *  - curiousity = optimize intelligence (later also culture), prioritize technology
		 *  - courage = optimize production (later also military), prioritize military/buildings
		 *  3. choose one weighted development
		 *  4. also 5% chance of discovery, 5% chance of random other development
		 *  
		 *  default behavior: first try to optimize according to preferences, then 
		 *  (i.e. if preferences cannot be optimized), optimize all
		 */
		Development choice = null;
		double maxRate = -100d;
		Attributes orientation = this.findOrientation();
		
		if(true) {//if(orientation == null) {
			for(Development d: availableDevelopments) {
				// TODO also consider buffs (make two choice arguments a) rate b) buffs,
				// both are max, go for it, if only one, put it in another list and make
				// more sophisticated decision
				
				// we only need 1 per subtype
				for(Subtype s: d.requiredSubtypes) {
					if(!this.technologies.stream().map(x -> x.subtype).collect(Collectors.toList()).contains(s)) {
						continue;
					}
				}
				
				double rate = d.rates.stream().reduce(0d, (a, b) -> a + b);
				if(rate > maxRate) {
					maxRate = rate;
					choice = d;
				}
			}
		}
		else {
			switch(orientation) {
			case AGGRESSION:
				break;
			case CREATIVITY:
				break;
			case PRODUCTIVITY:
				break;
			case SPIRITUALITY:
				break;
			case CURIOUSITY:
				break;
			case COURAGE:
				break;
			}
		}
		return choice;
	}
	
	boolean applyDevelopment(Development dev) {
		if(dev == null) {
			System.out.println("Nothing to develop left.");
			return false;
		}
		for (Map.Entry<String,Integer> entry : dev.cost.entrySet()) {
			Parameters costName = Parameters.valueOf(entry.getKey().toUpperCase());
			int costAmount = entry.getValue();
			switch(costName) {
			case WEALTH:
				this.setWealth(this.getWealth() - costAmount);
				break;
			case INTELLIGENCE:
				this.setIntelligence(this.getIntelligence() - costAmount);
				break;
			case PRODUCTION:
				this.setProduction(this.getProduction() - costAmount);
				break;
			default:
				break;
			}
		}
		for(int i = 0; i < NUMBER_OF_ATTRIBUTES; i++) {
			this.attributes.set(i, this.attributes.get(i) + dev.buffs.get(i));
		}
		for(int i = 0; i < NUMBER_OF_PARAMETERS; i++) {
			this.rates.set(i, this.rates.get(i) + dev.rates.get(i));
			//System.out.println(this.rates.get(i));
		}
		switch(dev.type) {
		case TECHNOLOGY:
			System.out.println("Fascinating! Your people developed " + dev.name + "!");
			this.technologies.add((Technology) dev);
			break;
		case CULTURAL:
			System.out.println("Cultivated! Your people now like " + dev.name + "!");
			this.cultural.add((Cultural) dev);
			break;
		case DISCOVERY:
			System.out.println("Eureka! Your people discovered " + dev.name + "!");
			this.discoveries.add((Discovery) dev);
			break;
		case POLICY:
			System.out.println("It's offical! Your people have the new policy " + dev.name + "!");
			this.policies.add((Policy) dev);
			break;
		case ORGANIZATION:
			break;
		case BUILDING:
			System.out.println("Engineered to perfection! Your people can now build a " + dev.name + "!");
			this.buildings.add((Building) dev);
			break;
		case NONE:
			break;
		default:
			break;
		}
		return true;
	}
	
	void applyRates() {
		if((this.getHealth() < 100 && this.rates.get(Parameters.HEALTH.ordinal()) > 0)
			|| (this.getHealth() > 0 && this.rates.get(Parameters.HEALTH.ordinal()) < 0)){
			this.setHealth(this.getHealth() + this.rates.get(Parameters.HEALTH.ordinal()));		
			if(this.health > 100) {
				this.health = 100;
			}
			if(this.health < 0) {
				this.health = 0;
			}
		}
		this.setWealth(this.getWealth() + this.rates.get(Parameters.WEALTH.ordinal()));
		
		if(this.getHealth() >= 50) {
			this.setPopulation(this.getPopulation() + this.rates.get(Parameters.POPULATION.ordinal()));	
		}
		else {
			this.setPopulation(this.getPopulation() - (100 - this.health) * Math.random());
		}
		
		this.setIntelligence(this.getIntelligence() + this.rates.get(Parameters.INTELLIGENCE.ordinal()));
		this.setProduction(this.getProduction() + this.rates.get(Parameters.PRODUCTION.ordinal()));
	}
	
	String printParameters() {
		return "Health: " + + this.parameters.get(Parameters.HEALTH.ordinal()) + "(" + this.rates.get(Parameters.HEALTH.ordinal()) + ") " +
				"Production: " + this.parameters.get(Parameters.PRODUCTION.ordinal()) + "(" + this.rates.get(Parameters.PRODUCTION.ordinal()) + ") " +
				"Wealth: " + this.parameters.get(Parameters.WEALTH.ordinal())  + "(" + this.rates.get(Parameters.WEALTH.ordinal()) + ") " +
				"Population: " + this.parameters.get(Parameters.POPULATION.ordinal())  + "(" + this.rates.get(Parameters.POPULATION.ordinal()) + ") " +
				"Intelligence: " + this.parameters.get(Parameters.INTELLIGENCE.ordinal())  + "(" + this.rates.get(Parameters.INTELLIGENCE.ordinal()) + ")";
	}
	
	public double getHealth() {
		return this.parameters.get(Parameters.HEALTH.ordinal());
	}
	
	public void setHealth(double value) {
		this.parameters.set(Parameters.HEALTH.ordinal(), value);
	}

	public double getWealth() {
		return this.parameters.get(Parameters.WEALTH.ordinal());
	}
	
	public void setWealth(double value) {
		this.parameters.set(Parameters.WEALTH.ordinal(), value);
	}

	public double getPopulation() {
		return this.parameters.get(Parameters.POPULATION.ordinal());
	}
	
	public void setPopulation(double value) {
		this.parameters.set(Parameters.POPULATION.ordinal(), value);
	}

	public double getIntelligence() {
		return this.parameters.get(Parameters.INTELLIGENCE.ordinal());
	}
	
	public void setIntelligence(double value) {
		this.parameters.set(Parameters.INTELLIGENCE.ordinal(), value);
	}

	public double getProduction() {
		return this.parameters.get(Parameters.PRODUCTION.ordinal());
	}
	
	public void setProduction(double value) {
		this.parameters.set(Parameters.PRODUCTION.ordinal(), value);
	}
}
