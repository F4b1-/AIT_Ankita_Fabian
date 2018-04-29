package hello;

import com.vaadin.tapio.googlemaps.client.LatLon;
import org.springframework.data.annotation.Id;


public class Location {

	@Id
	public String id;

	public String name;
	public LatLon latLon;

	public Location() {}

	public Location(String name, LatLon latLon) {
		this.name = name;
		this.latLon = latLon;
	}


	public String getId() {
		return id;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LatLon getLatLon() {
		return latLon;
	}

	public void setLatLon(LatLon latLon) {
		this.latLon = latLon;
	}

	@Override
	public String toString() {
		return String.format("Customer[id=%s, name='%s', latlon='%s']", id,
				name, "" + latLon.getLat() + "," + latLon.getLon());
	}



}