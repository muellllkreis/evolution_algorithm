package genetic_algorithm;

import java.util.HashSet;
import java.util.Set;

public class Game {
	GUI gui;
	
	Civilization civ;
	HashSet<Development> developments;
	HashSet<Resource> resources;
	
	public Game(HashSet<Development> developments, HashSet<Resource> resources) {
		this.developments = developments;
		this.resources = resources;
		Environment env = new Environment();
		System.out.println("A new planet is born.");
		System.out.printf("Water: %d%nCommon: %d%nMountain: %d%nDesert: %d%nForest: %d%nSnow: %d%nCaves: %d%n", env.getWater(), env.getCommon(), env.getMountain(), env.getDesert(), env.getForest(), env.getSnow(), env.getCaves());
		this.civ = new Civilization(env);
		
		this.gui = new GUI();
		
		this.playForever();
	}

	void playForever() {
		while(true) {
			this.takeTurn();
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	void takeTurn() {
		civ.applyRates();
		System.out.println(civ.printParameters());
		Set<Development> availableDevs = this.civ.findAvailableDevelopments(this.developments);
		Development choice = this.civ.chooseDevelopment(availableDevs);			
		civ.applyDevelopment(choice);
		this.logChoice(choice);
		this.gui.update(new double[]{civ.getHealth(), civ.getWealth(), civ.getPopulation(), civ.getIntelligence(), civ.getProduction()});
	}
	
	Development getDevelopment(String name) {
		for(Development d: this.developments) {
			if(d.name.equals(name)) {
				return d;
			}
		}
		return null;
	}
	
	void logChoice(Development choice) {
		if(choice == null) {
			return;
		}
		switch(choice.type) {
		case TECHNOLOGY:
			gui.logText("Fascinating! Your people developed " + choice.name + "!");
			break;
		case CULTURAL:
			gui.logText("Cultivated! Your people now like " + choice.name + "!");
			break;
		case DISCOVERY:
			gui.logText("Eureka! Your people discovered " + choice.name + "!");
			break;
		case POLICY:
			gui.logText("It's offical! Your people have the new policy " + choice.name + "!");
			break;
		case ORGANIZATION:
			break;
		case BUILDING:
			gui.logText("Engineered to perfection! Your people can now build a " + choice.name + "!");
			break;
		case NONE:
			break;
		default:
			break;
		}
	}
}
