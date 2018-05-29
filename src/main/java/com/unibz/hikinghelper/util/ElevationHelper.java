package com.unibz.hikinghelper.util;

import com.unibz.hikinghelper.Constants.Constants;
import com.unibz.hikinghelper.model.Location;
import com.unibz.hikinghelper.dao.LocationRepository;
import com.unibz.hikinghelper.model.Elevation;
import com.unibz.hikinghelper.model.Results;
import com.vaadin.tapio.googlemaps.client.LatLon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;


@Component("elevationHelper")
public class ElevationHelper {


    LocationRepository repository;
    @Autowired
    public ElevationHelper(LocationRepository repository) {
        this.repository = repository;
    }

    private static final Logger log = LoggerFactory.getLogger(ElevationHelper.class);


    public Location generateElevationDataForLocation(Location location) {
        ArrayList<LatLon> route = location.getRoute();
        ArrayList<Double> elevationPoints = new ArrayList<Double>();

        for (LatLon latLon : route) {
            elevationPoints.add(Double.valueOf(getAltitude(latLon)));
        }

        location.setElevationPoints(elevationPoints);

        return this.repository.save(location);

    }


    private String getAltitude(LatLon latLon) {
        String singleAltitude = "";
        String elevationCall = "https://maps.googleapis.com/maps/api/elevation/json?locations=" + latLon.getLat() + "," + latLon.getLon() + "&key=" + Constants.GOOGLE_API_KEY;
        RestTemplate restTemplate = new RestTemplate();

        Elevation elevation = restTemplate.getForObject(elevationCall, Elevation.class);
        if (elevation != null) {
            List<Results> results = elevation.getResults();
            if (results.size() > 0) {
                singleAltitude = results.get(0).getElevation();
            }

        }

        return singleAltitude;
    }

}
