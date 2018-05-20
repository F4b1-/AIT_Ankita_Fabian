package com.unibz.hikinghelper.ui;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.BarChartConfig;
import com.byteowls.vaadin.chartjs.data.Dataset;
import com.byteowls.vaadin.chartjs.data.LineDataset;
import com.byteowls.vaadin.chartjs.options.Position;
import com.unibz.hikinghelper.Location;
import com.unibz.hikinghelper.LocationRepository;
import com.unibz.hikinghelper.util.ElevationHelper;
import com.vaadin.data.Binder;
import com.vaadin.data.ValueProvider;
import com.vaadin.event.ShortcutAction;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Setter;
import com.vaadin.server.VaadinService;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapPolyline;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@UIScope
@SpringUI
public class DetailWindow extends Window {

    private Location location;

    private Label labelName;
    private Label labelDuration;
    private Label labelDifficulty;

    @Autowired
    ElevationHelper elevationHelper;

    public DetailWindow(Location location) {
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


        Button elevationButton = new Button("Calculate altitude");
        elevationButton.addClickListener(clickEvent -> elevationHelper.generateElevationDataForLocation(location));

        VerticalLayout infoLayout = new VerticalLayout(labelName, labelDuration, labelDifficulty, favButton, elevationButton);
        HorizontalLayout subContent = new HorizontalLayout(infoLayout, generateGoogleMap(), generateBarChart());
        setContent(subContent);
        center();
    }


    private ChartJs generateBarChart() {
        ArrayList<Double> elevationPoints = location.getElevationPoints();
        String[] labelsMarks = new String[elevationPoints.size()];
        for(int i=0; i < elevationPoints.size(); i++) {
            labelsMarks[i] = String.valueOf(i) + ". mark";
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
                .text("Changes in Altitude")
                .and()
                .done();

        List<String> labels = config.data().getLabels();
        for (Dataset<?, ?> ds : config.data().getDatasets()) {



            if (ds instanceof LineDataset) {
                LineDataset lds = (LineDataset) ds;
                lds.dataAsList(elevationPoints);
            }
        }

        ChartJs chart = new ChartJs(config);
        chart.setWidth(600, Unit.PIXELS);
        chart.setJsLoggingEnabled(true);

        return chart;
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
