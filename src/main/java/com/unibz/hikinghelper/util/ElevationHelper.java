package com.unibz.hikinghelper.util;

import com.unibz.hikinghelper.Application;
import com.unibz.hikinghelper.Location;
import com.unibz.hikinghelper.LocationRepository;
import com.unibz.hikinghelper.model.Elevation;
import com.unibz.hikinghelper.model.Results;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.tapio.googlemaps.client.LatLon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
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


    public void generateElevationDataForLocation(Location location) {
        ArrayList<LatLon> route = location.getRoute();
        ArrayList<Double> elevationPoints = new ArrayList<Double>();
        for (LatLon latLon : route) {
            elevationPoints.add(Double.valueOf(getAltitute(latLon)));
        }

        location.setElevationPoints(elevationPoints);

        this.repository.save(location);

    }


    private String getAltitute(LatLon latLon) {
        String singleAltitude = "";
        String elevationCall = "https://maps.googleapis.com/maps/api/elevation/json?locations=" + latLon.getLat() + "," + latLon.getLon() + "&key=AIzaSyAdXfqEgqkjkDBBFC2dRoWU_-dST-S34dk";
        RestTemplate restTemplate = new RestTemplate();

        Elevation elevation = restTemplate.getForObject(elevationCall, Elevation.class);
        if (elevation != null) {
            List<Results> results = elevation.getResults();
            if (results.size() > 0) {
                singleAltitude = results.get(0).getElevation();
            }

        }
        log.info(singleAltitude);
        return singleAltitude;
    }

}
