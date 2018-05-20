package com.unibz.hikinghelper.ui;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.BarChartConfig;
import com.byteowls.vaadin.chartjs.data.Dataset;
import com.byteowls.vaadin.chartjs.data.LineDataset;
import com.byteowls.vaadin.chartjs.options.Position;
import com.unibz.hikinghelper.Constants.Constants;
import com.unibz.hikinghelper.Location;
import com.unibz.hikinghelper.util.ElevationHelper;
import com.unibz.hikinghelper.util.Utils;
import com.vaadin.server.VaadinService;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapPolyline;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@UIScope
public class DetailWindow extends Window {

    private Location location;

    private Label labelName;
    private Label labelDuration;
    private Label labelDifficulty;

    private ChartJs chart;


    public DetailWindow(Location location, ElevationHelper elevationHelper, Collection<GrantedAuthority> authorities) {
        super("Details");
        this.location = location;


        labelName = new Label(location.getName());
        labelDuration = new Label(String.valueOf(location.getDuration().toHours()));
        labelDifficulty = new Label(location.getDifficulty().toString());

        Button favButton = new Button("Add to favorites");
        favButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        favButton.addClickListener(clickEvent ->
                VaadinService.getCurrentRequest().getWrappedSession()
                        .setAttribute("hey", "button was pressed"));


        VerticalLayout infoLayout = new VerticalLayout(labelName, labelDuration, labelDifficulty, favButton);
        if (Utils.hasRole(Constants.ROLE_ADMIN, authorities)) {
            Button elevationButton = new Button("Calculate altitude");
            elevationButton.addClickListener(clickEvent -> {
                Location updatedLocation = elevationHelper.generateElevationDataForLocation(location);
                this.location = updatedLocation;

                chart.configure(generateBarConfig());
                chart.update();

            });
            infoLayout.addComponent(elevationButton);
        }

        chart = generateBarChart();
        HorizontalLayout subContent = new HorizontalLayout(infoLayout, generateGoogleMap(), chart);
        setContent(subContent);
        center();

    }

    public void setLocation(Location location) {
        this.location = location;
    }


    private ChartJs generateBarChart() {
        ChartJs chart = new ChartJs(generateBarConfig());
        chart.setWidth(600, Unit.PIXELS);
        chart.setJsLoggingEnabled(true);

        return chart;
    }

    private BarChartConfig generateBarConfig() {
        ArrayList<Double> elevationPoints = location.getElevationPoints();
        String[] labelsMarks = new String[elevationPoints.size()];
        for (int i = 0; i < elevationPoints.size(); i++) {
            labelsMarks[i] = String.valueOf(i + 1) + ". mark";
        }

        BarChartConfig config = new BarChartConfig();
        config
                .data()
                .labels(labelsMarks)
                .addDataset(new LineDataset().type().label("Altitude"))
                .and();

        config.
                options()
                .responsive(true)
                .title()
                .display(true)
                .position(Position.LEFT)
                .text("Altitude in meters")
                .and()
                .done();

        List<String> labels = config.data().getLabels();
        for (Dataset<?, ?> ds : config.data().getDatasets()) {


            if (ds instanceof LineDataset) {
                LineDataset lds = (LineDataset) ds;
                lds.dataAsList(elevationPoints);
            }
        }

        return config;
    }


    private GoogleMap generateGoogleMap() {
        GoogleMap googleMap = new GoogleMap("AIzaSyAdXfqEgqkjkDBBFC2dRoWU_-dST-S34dk", null, "english");
        googleMap.setSizeFull();

        googleMap.setHeight(300, Unit.PIXELS);
        googleMap.setWidth(300, Unit.PIXELS);

        googleMap.setMinZoom(4);
        googleMap.setMaxZoom(16);
        googleMap.setZoom(16);
        googleMap.setCenter(new LatLon(
                60.450403, 22.230399));

        LatLon latLon = location.getLatLon();
        googleMap.setCenter(latLon);
        googleMap.clearMarkers();
        googleMap.addMarker(location.getName(), latLon, false, null);
        GoogleMapPolyline currentOverlay = new GoogleMapPolyline(
                location.getRoute(), "#d31717", 0.8, 5);
        googleMap.addPolyline(currentOverlay);

        return googleMap;
    }


}
