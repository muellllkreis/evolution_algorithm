package genetic_algorithm;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class Launcher {
	
	final static String devXmlPath = "res/developments.xml";
	final static String resXmlPath = "res/resources.xml";
	final static File developmentsXml = new File(devXmlPath);
	final static File resourcesXml = new File(resXmlPath);
	
	final static int NUMBER_OF_ATTRIBUTES = Attributes.values().length + 1;
	final static int NUMBER_OF_PARAMETERS = Parameters.values().length + 1;
	
	static HashSet<Resource> allResources = new HashSet<>();
	static HashSet<Development> allDevelopments = new HashSet<>();
	
	public static Document parseResources() throws DocumentException {
		String resName;
		String resType;
		
        SAXReader reader = new SAXReader();
        Document document = reader.read(resourcesXml);
        List<Node> resources = document.getRootElement().selectNodes("//resource");
        for(Node n: resources) {
          	// get name
        	Node name = n.selectSingleNode("name");
        	resName = name.getText();
        	System.out.println(name.getText());
        	// get type
        	Node type = n.selectSingleNode("type");
        	resType = type.getText();
        	switch(resType) {
        	case "MATERIAL":
        		allResources.add(new Material(resName));
        		break;
        	case "ANIMAL":
        		break;
        	case "FOOD":
        		break;
        	}
        }
        return document;
	}
	
    public static Document parseDevelopments() throws DocumentException {
		Consumer<Node> print = x -> System.out.println(x.getName() + ":" + x.getText());
    	
        SAXReader reader = new SAXReader();
        Document document = reader.read(developmentsXml);
        List<Node> developments = document.getRootElement().selectNodes("//development");
        for(Node n: developments) {
        	/* DEVELOPMENT ATTRIBUTES */
        	String devName;
        	DevelopmentType devType;
        	Subtype devSubtype = Subtype.NONE;
        	List<Integer> devBuffs;
        	ArrayList<Integer> devRequiredMinAttributes = initializeIntegerList(NUMBER_OF_ATTRIBUTES);
        	ArrayList<Integer> devRequiredMaxAttributes = initializeIntegerList(NUMBER_OF_ATTRIBUTES);
        	ArrayList<Double> devRates = initializeDoubleList(NUMBER_OF_PARAMETERS);
        	HashSet<String> devRequiredDevelopments = new HashSet<>();
        	HashSet<HashSet<String>> devOptionalDevelopments = new HashSet<>();
        	HashSet<Subtype> devRequiredSubtypes = new HashSet<>();
        	HashSet<String> devRequiredResources = new HashSet<>();
        	HashSet<HashSet<String>> devOptionalResources = new HashSet<>();
        	HashSet<String> devRequiredResourceType = new HashSet<>();
        	HashMap<String, Integer> devCost = new HashMap<>();
        	boolean devRequiredSeasons;
        	int[] devRequiredTerrain;
        	int devRequiredMinTemp;
        	int devRequiredMaxTemp;
        	
        	// get name
        	Node name = n.selectSingleNode("name");
        	devName = name.getText();
        	System.out.println(name.getText());
        	// get type
        	Node type = n.selectSingleNode("type");
        	devType = DevelopmentType.valueOf((type.getText()));
        	System.out.println(type.getText());
        	// get subtype
        	Node subtype = n.selectSingleNode("subtype");
        	if(subtype != null) {
            	devSubtype = Subtype.valueOf(subtype.getText());
        		System.out.println(subtype.getText());
        	}
        	// get effect list
        	List<Node> effectList = n.selectSingleNode("buffs").selectNodes("*");
        	effectList.stream().forEach(print);
        	devBuffs = effectList.stream().map(el -> Integer.parseInt(el.getText())).collect(Collectors.toList());
        	
        	// get rate list
        	List<Node> rateList = n.selectSingleNode("rates").selectNodes("*");
			for(Node rate: rateList) {
				devRates = addRequiredRate(devRates, rate.getName(), rate.getText());
			}
			
			// get cost
			Node costNode = n.selectSingleNode("cost");
			if(costNode != null) {
				List<Node> costList = costNode.selectNodes("*");
				for(Node cost: costList) {
					devCost.put(cost.getName(), Integer.parseInt(cost.getText()));
				}	
			}
        	
        	// get required list
        	Node required = n.selectSingleNode("required");
        	if(required != null) {
        		List<Node> requiredList = required.selectNodes("*");
            	for(Node category: requiredList) {
            		System.out.println("requires " + category.getName());
                	List<Node> valueList = category.selectNodes("*");
                	String currentName = category.getName();
                	switch(currentName) {
                	case "resources":
                		for(Node value: valueList) {
            				if(value.getName().equals("value")) {
            					devRequiredResources.add(value.getText());
	                		}
	        				else if(value.getName().equals("optionalValue")) {
	        					devOptionalResources.add(parseOptionValue(value.getText()));
	        				}
                    	}
                		break;
                	case "technologyType":
                		for(Node value: valueList) {
                    		devRequiredSubtypes.add(Subtype.valueOf(value.getText()));
                    	}
                		break;
                	case "resourceType":
                		for(Node value: valueList) {
                    		devRequiredResourceType.add(value.getText());
                    	}
                		break;
                	case "minTemperature":
                		devRequiredMinTemp = Integer.parseInt(valueList.get(0).getText());
                		break;
                	case "maxTemperature":
                		devRequiredMaxTemp = Integer.parseInt(valueList.get(0).getText());
                		break;
                	case "season":
                		devRequiredSeasons = true;
                		break;
                	case "terrain":
                		break;
                	default:
                		//min or max attribute
                		if (currentName.matches("(min)[A-Za-z]*")) {
                			for(Node value: valueList) {
                				devRequiredMinAttributes = addRequiredAttribute(devRequiredMinAttributes, currentName.toLowerCase(), value.getText());
                			}
                		}
                		else if (currentName.matches("(max)[A-Za-z]*")) {
                			for(Node value: valueList) {
                				devRequiredMaxAttributes = addRequiredAttribute(devRequiredMaxAttributes, currentName.toLowerCase(), value.getText());
                			}
                		}
                		// development
                		else {
                			for(Node value: valueList) {
                				if(value.getName().equals("value")) {
                        			devRequiredDevelopments.add(value.getText());	
                				}
                				else if(value.getName().equals("optionalValue")) {
                					devOptionalDevelopments.add(parseOptionValue(value.getText()));
                				}
                        	}
                		}
                		break;
                	}
                	for(Node value: valueList) {
                		System.out.println(value.getName() + ":" + value.getText());
                	}
            	}
        	}
        	switch(devType) {
        	case TECHNOLOGY:
        		allDevelopments.add(new Technology(devName, devType, devSubtype, devBuffs, devRates, devRequiredMinAttributes, devRequiredMaxAttributes, devRequiredDevelopments, devOptionalDevelopments, devRequiredSubtypes, devRequiredResources, devOptionalResources, devRequiredResourceType, devCost));
        		break;
        	case CULTURAL:
        		allDevelopments.add(new Cultural(devName, devType, devBuffs, devRates, devRequiredMinAttributes, devRequiredMaxAttributes, devRequiredDevelopments, devOptionalDevelopments, devRequiredSubtypes, devRequiredResources, devOptionalResources, devRequiredResourceType, devCost));
        		break;
        	case DISCOVERY:
        		allDevelopments.add(new Discovery(devName, devType, devBuffs, devRates, devRequiredMinAttributes, devRequiredMaxAttributes, devRequiredDevelopments, devOptionalDevelopments, devRequiredSubtypes, devRequiredResources, devOptionalResources, devRequiredResourceType, devCost));
        		break;
        	case POLICY:
        		allDevelopments.add(new Policy(devName, devType, devBuffs, devRates, devRequiredMinAttributes, devRequiredMaxAttributes, devRequiredDevelopments, devOptionalDevelopments, devRequiredSubtypes, devRequiredResources, devOptionalResources, devRequiredResourceType, devCost));
        		break;
        	case ORGANIZATION:
        		allDevelopments.add(new Organization(devName, devType, devBuffs, devRates, devRequiredMinAttributes, devRequiredMaxAttributes, devRequiredDevelopments, devOptionalDevelopments, devRequiredSubtypes, devRequiredResources, devOptionalResources, devRequiredResourceType, devCost));
        		break;
        	case BUILDING:
        		allDevelopments.add(new Building(devName, devType, devBuffs, devRates, devRequiredMinAttributes, devRequiredMaxAttributes, devRequiredDevelopments, devOptionalDevelopments, devRequiredSubtypes, devRequiredResources, devOptionalResources, devRequiredResourceType, devCost));
        		break;
        	case NONE:
        		System.out.println("Something went wrong");
        		break;
        	default:
        		System.out.println("Something went wrong");
        		break;
        	}
        	System.out.println("-------------------------------------------------------");
        }
        return document;
    }
    
    public static ArrayList<Integer> addRequiredAttribute(ArrayList<Integer> currentList, String currentName, String currentValue) {
    	currentList.set(Attributes.valueOf(currentName.substring(3).toUpperCase()).ordinal(), Integer.parseInt(currentValue));
    	return currentList;
    }
    
    public static ArrayList<Double> addRequiredRate(ArrayList<Double> currentList, String currentName, String currentValue) {
    	currentList.set(Parameters.valueOf(currentName.toUpperCase()).ordinal(), Double.parseDouble(currentValue));
    	return currentList;
    }
    
    public static HashSet<String> parseOptionValue(String optionValue) {
    	return new HashSet<>(Arrays.asList(optionValue.split(",")));
    }

	public static void main(String[] args) {
		try {
			parseResources();
			parseDevelopments();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		/*
		for(Resource r: allResources) {
			System.out.println(r.name);
		}
		*/
		for(Development d: allDevelopments) {
			System.out.println(d);
		}
		Game game = new Game(allDevelopments, allResources);
	}
	
	public static boolean isNumeric(String strNum) {
	    if (strNum == null) {
	        return false;
	    }
	    try {
	        double d = Double.parseDouble(strNum);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}
	
	public static ArrayList<Integer> initializeIntegerList(int size) {
		ArrayList<Integer> arr = new ArrayList<>();
		for (int i = 0; i < size - 1; i++) {
			  arr.add(0);
		}
		return arr;
	}
	
	public static ArrayList<Double> initializeDoubleList(int size) {
		ArrayList<Double> arr = new ArrayList<>();
		for (int i = 0; i < size - 1; i++) {
			  arr.add(0d);
		}
		return arr;
	}
}
