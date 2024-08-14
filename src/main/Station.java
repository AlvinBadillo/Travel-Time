package main;
/**This class will contain all the data regarding a specific station
 * It has two main components, name and distance
 * The name will be the stations name and the distance will be a number that can represent any desired distance
 */
public class Station {
	
	String name;
	int dist;
	/*Constructor that recives name and distance */
	public Station(String name, int dist) {
		this.name = name;
		this.dist = dist;

	}
	/*Returns name of station */
	public String getCityName() {
		return this.name;
	}
	/*Sets the name of the satation */
	public void setCityName(String cityName) {
		this.name = cityName;
	}
	/*Return distance of the station */
	public int getDistance() {
		return this.dist;
	}
	/*Sets distance of station */
	public void setDistance(int distance) {
		this.dist = distance;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Station other = (Station) obj;
		return this.getCityName().equals(other.getCityName()) && this.getDistance() == other.getDistance();
	}
	@Override
	public String toString() {
		return "(" + this.getCityName() + ", " + this.getDistance() + ")";
	}

}
