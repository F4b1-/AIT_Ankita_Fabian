package hello;

import com.vaadin.tapio.googlemaps.client.LatLon;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LocationRepository extends MongoRepository<Location, String> {

	public Location findByName(String name);
	public List<Location> findByLatLon(LatLon latLon);

}