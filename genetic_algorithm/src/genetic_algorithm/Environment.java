package genetic_algorithm;

import java.util.ArrayList;
import java.util.Random;

public class Environment {
	private int water, 
				common,
				mountain,  
				desert, 
				forest, 
				snow, 
				caves;
	
	private int temperature;
	
	private boolean seasons;
	
	private ArrayList<Resource> resources = new ArrayList<>();
	
	public Environment() {
		Random rand = new Random();
		int[] ratios = new int[7];
		int max = 100;
		int total = 0;
		for(int i = 0; i < ratios.length; i++) {
			ratios[i] = rand.nextInt((max - total) / (ratios.length - i));
			total += ratios[i];
		}
		int rest = (max - total) / ratios.length;
		for(int i = 0; i < ratios.length; i++) {
			
		}
		this.water = ratios[6];
		this.common = ratios[5];
		this.mountain = ratios[4];
		this.desert = ratios[3];
		this.forest = ratios[2];
		this.snow = ratios[1];
		this.caves = ratios[0];
		
		this.resources.add(new Material("STONE"));
		//this.resources.add(new Material("WOOD"));
	}

	public Environment(String abc) {
		
	}
	
	public int getWater() {
		return water;
	}

	public int getCommon() {
		return common;
	}

	public int getMountain() {
		return mountain;
	}

	public int getDesert() {
		return desert;
	}

	public int getForest() {
		return forest;
	}

	public int getSnow() {
		return snow;
	}

	public int getCaves() {
		return caves;
	}

	public int getTemperature() {
		return temperature;
	}

	public boolean isSeasons() {
		return seasons;
	}

	public ArrayList<Resource> getResources() {
		return resources;
	}
}
