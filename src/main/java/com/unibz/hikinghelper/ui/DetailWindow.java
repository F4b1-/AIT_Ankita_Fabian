package com.unibz.hikinghelper.ui;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.vaadin.icons.VaadinIcons;
import com.byteowls.vaadin.chartjs.config.BarChartConfig;
import com.byteowls.vaadin.chartjs.data.Dataset;
import com.byteowls.vaadin.chartjs.data.LineDataset;
import com.byteowls.vaadin.chartjs.options.Position;
import com.unibz.hikinghelper.Constants.Constants;
import com.unibz.hikinghelper.Location;
import com.unibz.hikinghelper.util.ElevationHelper;
import com.unibz.hikinghelper.util.Utils;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinService;
import com.vaadin.server.WrappedSession;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapPolyline;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.security.core.GrantedAuthority;
import sun.management.counter.Units;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.Icon;


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
        labelName.setIcon(VaadinIcons.MAP_MARKER);
        labelDuration = new Label(String.valueOf(location.getDuration().toHours()));
        labelDuration.setIcon(VaadinIcons.STOPWATCH);
        labelDifficulty = new Label(location.getDifficulty().toString());
        labelDifficulty.setIcon(VaadinIcons.EXIT);

        Button favButton = new Button("Add to favorites");
        favButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        favButton.addClickListener(clickEvent -> {
            WrappedSession wrappedSession = VaadinService.getCurrentRequest().getWrappedSession();
            ArrayList<Location> favorites= (ArrayList<Location>) wrappedSession.getAttribute("favorites");
            if(favorites == null) {
                favorites = new ArrayList<>();
            }
            favorites.add(location);
            wrappedSession.setAttribute("favorites", favorites);
        });


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

        HorizontalLayout infoContent = new HorizontalLayout(infoLayout, chart);
        HorizontalLayout mapContent = new HorizontalLayout(generateGoogleMap());
        mapContent.setWidth(100, Unit.PERCENTAGE);
        VerticalLayout subContent = new VerticalLayout(infoContent, mapContent);
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
                .addDataset(new LineDataset().type().label("Altitude").backgroundColor("rgba(46, 215, 245, 0.35)"))
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
        GoogleMap googleMap = new GoogleMap(Constants.GOOGLE_API_KEY, null, "english");
        googleMap.setSizeFull();

        googleMap.setMinZoom(4);
        googleMap.setMaxZoom(16);
        googleMap.setZoom(14);
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
