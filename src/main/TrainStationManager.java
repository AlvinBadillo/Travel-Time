package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import data_structures.ArrayList;
import data_structures.HashSet;
import data_structures.HashTableSC;
import data_structures.LinkedListStack;
import data_structures.LinkedStack;
import data_structures.SimpleHashFunction;
import interfaces.List;
import interfaces.Map;
import interfaces.Stack;
/**  
 * TrainStationManager is a class that will recive the locations and distances of different locations and caluculate the shortest distance from a starting ppoint
 * In the particular class, the starting point will be Westside station, it will be able to find the shortest distance between any other station
 * The class contains two key parts, Map called stations and Map called shortest distance.
 * For stations, it contains as a key the names of all stations and the value is a list of neighbor stations
 * For short-distances, it will contain as key the name of a station and as the value it will have a station and the total distance from Westside to the key
 * 
*/

public class TrainStationManager {

	/*This are the two main componets of the class */
	Map<String, List<Station>> stations = new HashTableSC<>(1, new SimpleHashFunction<>());
	Map<String, Station> shortes_distances = new HashTableSC<>(1, new SimpleHashFunction<>());
	/*This constructor will recive as a parameter an input file and it will populate sattions map
	 * it will also set up shortest_distance map to be ready to use in other method
	*/
	public TrainStationManager(String station_file) {
		try{
			BufferedReader station_reader = new BufferedReader(new FileReader("inputFiles/" + station_file));
			/*Skip first line*/
			station_reader.readLine();
			/*now loop until all line have been read*/
			String line;
			while((line = station_reader.readLine()) != null){
				/*now devide line into all parts [name1], [name2], dist*/
				String[] fullLine = line.split(",");
				if(stations.containsKey(fullLine[0])){
					stations.get(fullLine[0]).add(new Station(fullLine[1], Integer.parseInt(fullLine[2])));
				}
				else{
					ArrayList<Station> list = new ArrayList<>();
					list.add(new Station(fullLine[1], Integer.parseInt(fullLine[2])));
					stations.put(fullLine[0], list);
				}
				if(stations.containsKey(fullLine[1])){
					stations.get(fullLine[1]).add(new Station(fullLine[0], Integer.parseInt(fullLine[2])));
				}
				else{
					ArrayList<Station> list = new ArrayList<>();
					list.add(new Station(fullLine[0], Integer.parseInt(fullLine[2])));
					stations.put(fullLine[1], list);
				}

				/*Populate shortest distance 
				I want to add all stations with all the values set to be Station(Wstside, Integer.MaxNumber)
				*/
				Station defaulStation = new Station("Westside", Integer.MAX_VALUE);
				if(!shortes_distances.containsKey(fullLine[0])){
					shortes_distances.put(fullLine[0], defaulStation);
				}
				if(!shortes_distances.containsKey(fullLine[1])){
					shortes_distances.put(fullLine[1], defaulStation);
				}
				
			}
			station_reader.close();
		} 
		catch (IOException e) {
        	e.printStackTrace();
		}
		findShortestDistance();
		
	}

	/*This method calculate shortest dist from "Westside" to every other station 
	 * We will use Dijkstra's Algorithm to do so
	 * We will store all the information in a map called shortes_stations, where the key is the name of the station we want to know the distance from Westside
	 * and the value will contain a station that will have as distance the total distance and the name will be the name of the previous station in the path
	*/
	private void findShortestDistance() {
		//System.out.println(stations);
		
		/*First create stack of stations we want to visit and set to store all the preivously visited stations */
		LinkedListStack<Station> toVisit = new LinkedListStack<>();
		HashSet<String> visited = new HashSet<>();
	
		/* We start from Westside station with a distance of 0.*/
		Station westside = new Station("Westside", 0);
		toVisit.push(westside);
		shortes_distances.put("Westside", westside);

		/*Now that we hace set up shortes_distance, we are ready to implement dijkstras algorithim  */
		while (!toVisit.isEmpty()) {
			
			/*Mark currStation as visited by addding it to the stack */
			Station currStation = toVisit.pop();
			visited.add(currStation.getCityName());
	
			/*Extract neighbors of currStation to evaluate */
			List<Station> neighbors = stations.get(currStation.getCityName());
			for (Station neighbor : neighbors) {
				if (visited.isMember(neighbor.getCityName())) {
					continue;
				}
				/*Calculate new distance to compare with prev dist */
				int newDist = shortes_distances.get(currStation.getCityName()).getDistance() + neighbor.getDistance();
				Station neighborInMap = shortes_distances.get(neighbor.getCityName());

				/*If new dist is smaller, update distance to that */
				if (newDist < neighborInMap.getDistance()) {
					// System.out.println(currStation.getCityName());
					// System.out.println(newDist);
					Station updatedNeighbor = new Station(currStation.getCityName(), newDist);
					shortes_distances.put(neighbor.getCityName(), updatedNeighbor);
					// System.out.println(updatedNeighbor);
					
				}
				/*Sort stack */
				sortStack(neighbor, toVisit);
			}
			// System.out.println(shortes_distances);
			// System.out.println(visited);
		}				
	}
	/*This method is essentially a helper method for findShortestDistance
	 * It ensures that the stack used in dinShortestDistance is sorted
	 * The smaller the distance, the closer to the top it will be
	 */
	public void sortStack(Station station, Stack<Station> stackToSort) {
		Stack<Station> stack = new LinkedListStack<>();
		
		while(!stackToSort.isEmpty() && stackToSort.top().getDistance() < station.getDistance()){
			stack.push(stackToSort.pop());
		}

		stackToSort.push(station);

		while(!stack.isEmpty()){
			stackToSort.push(stack.pop());
		}
	}
	/*
	 * Returns a Map where the key is the station name, and the value is the time it takes to reach that station. 
	 * The time will be calculated in minutes. 
	 * The calculation will work as follows: It takes 2.5 minutes per kilometer (use the shortest distance for this) 
	 * and it takes 15 minutes for each station between Westside and the destination.
	 */
	public Map<String, Double> getTravelTimes() {
		/*
		5 minutes per kilometer
		15 min per station
		Initialize a new map to store travel times for each station.
		Create a new map to store travel times of all stations
		*/
		Map<String, Double> travelTimes = new HashTableSC<>(15, new SimpleHashFunction<>());

		for (String stationName : stations.getKeys()) {
			int shortestDistance = shortes_distances.get(stationName).getDistance();
			double distanceTime = shortestDistance * 2.5; 
			
			/*Calculate the number of stations between "Westside" and the destination station.
			 We subtract 1 to exclude "Westside" from the count.*/
			int stops = -1;
			if(stationName == "Westside"){
				stops = 0;
			}
			String temp = stationName;
			while(temp != "Westside"){
				stops++;
				temp = shortes_distances.get(temp).getCityName();
			}
			double stationTime = stops * 15;
			double totalTime = distanceTime + stationTime;
			travelTimes.put(stationName, totalTime);
		}
		return travelTimes;
	}
	/*Returns station map */
	public Map<String, List<Station>> getStations() {
		return this.stations;
	}

	/*Sets station map */
	public void setStations(Map<String, List<Station>> cities) {
		this.stations = cities;
	}

	/*Return shortes_distance map */
	public Map<String, Station> getShortestRoutes() {
		return this.shortes_distances;
	}


	public void setShortestRoutes(Map<String, Station> shortestRoutes) {
		this.shortes_distances = shortestRoutes;
	}
	
	/**
	 * BONUS EXERCISE THIS IS OPTIONAL
	 * Returns the path to the station given. 
	 * The format is as follows: Westside->stationA->.....stationZ->stationName
	 * Each station is connected by an arrow and the trace ends at the station given.
	 * 
	 * @param stationName - Name of the station whose route we want to trace
	 * @return (String) String representation of the path taken to reach stationName.
	 */
	public String traceRoute(String stationName) {
		// Remove if you implement the method, otherwise LEAVE ALONE
		// throw new UnsupportedOperationException();
		String result = "";
		String temp = stationName;
		String temp2;
		while(temp != "Westside"){
			temp2 = "->" + temp;
			result = temp2 + result;
			temp = shortes_distances.get(temp).getCityName();
		}
		result = "Westside" + result;
		return result;
	}

}