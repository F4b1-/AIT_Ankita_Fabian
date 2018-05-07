package com.unibz.hikinghelper.ui;

import com.byteowls.vaadin.chartjs.config.BarChartConfig;
import com.byteowls.vaadin.chartjs.data.BarDataset;
import com.byteowls.vaadin.chartjs.data.Dataset;
import com.byteowls.vaadin.chartjs.data.LineDataset;
import com.byteowls.vaadin.chartjs.options.Position;
import com.unibz.hikinghelper.Location;
import com.unibz.hikinghelper.LocationRepository;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapPolyline;
import com.vaadin.ui.*;
import org.springframework.util.StringUtils;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.byteowls.vaadin.chartjs.ChartJs;

import java.util.ArrayList;
import java.util.List;

@SpringUI(path = "/application")
public class VaadinUI extends UI {

    // Menu navigation button listener
    class ButtonListener implements Button.ClickListener {
        String menuitem;
        public ButtonListener(String menuitem) {
            this.menuitem = menuitem;
        }

        @Override
        public void buttonClick(Button.ClickEvent event) {
            // Navigate to a specific state
            //navigator.navigateTo(MAINVIEW + "/" + menuitem);
        }
    }

	private final LocationRepository repo;

	private final LocationEditor editor;

	final Grid<Location> grid;

	final TextField filter;

	private final Button addNewBtn;

	final GoogleMap googleMap = new GoogleMap("AIzaSyAdXfqEgqkjkDBBFC2dRoWU_-dST-S34dk", null, "english");

	private final static String MENU_BUTTON_STYLENAME = "menu_button";


    public VaadinUI(LocationRepository repo, LocationEditor editor) {
		this.repo = repo;
		this.editor = editor;
		this.grid = new Grid<>(Location.class);
		this.filter = new TextField();
		this.addNewBtn = new Button("New Location", FontAwesome.PLUS);
	}

	@Override
	protected void init(VaadinRequest request) {
		// build layout
		HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);
        HorizontalLayout option = new HorizontalLayout(grid);
        option.addComponent(generateBarChart());

        VerticalLayout menuContent = new VerticalLayout();

        Button pigButton = new Button("Pig", new ButtonListener("pig"));
        pigButton.setPrimaryStyleName(MENU_BUTTON_STYLENAME);
        menuContent.addComponent(pigButton);
        menuContent.addComponent(new Button("Cat",
                new ButtonListener("cat")));
        menuContent.addComponent(new Button("Dog",
                new ButtonListener("dog")));
        menuContent.addComponent(new Button("Reindeer",
                new ButtonListener("reindeer")));
        menuContent.addComponent(new Button("Penguin",
                new ButtonListener("penguin")));
        menuContent.addComponent(new Button("Sheep",
                new ButtonListener("sheep")));


		VerticalLayout mainLayout = new VerticalLayout(menuContent, actions, option, editor);
        HorizontalLayout main = new HorizontalLayout(menuContent, mainLayout);
		setContent(main);

		grid.setHeight(300, Unit.PIXELS);




        grid.addColumn(location -> location.getLatLon().getLat()).setCaption("Latitude").setId("latitude");
        grid.addColumn(location -> location.getLatLon().getLon()).setCaption("Longitude").setId("longitude");

		grid.setColumns("name", "latitude", "longitude");

		filter.setPlaceholder("Filter by name");



		googleMap.setSizeFull();

		googleMap.setHeight(300, Unit.PIXELS);
        googleMap.setWidth(300, Unit.PIXELS);
		googleMap.addMarker("DRAGGABLE: Paavo Nurmi Stadion", new LatLon(
				60.442423, 22.26044), true, "VAADIN/1377279006_stadium.png");
		googleMap.addMarker("NOT DRAGGABLE: Iso-Heikkilä", new LatLon(
				60.450403, 22.230399), false, null);
		googleMap.setMinZoom(4);
		googleMap.setMaxZoom(16);
		googleMap.setCenter(new LatLon(
                60.450403, 22.230399));

        ArrayList<LatLon> points = new ArrayList<LatLon>();
        points.add(new LatLon(60.448118, 22.253738));
        points.add(new LatLon(60.455144, 22.24198));
        points.add(new LatLon(60.460222, 22.211939));
        points.add(new LatLon(60.488224, 22.174602));
        points.add(new LatLon(60.486025, 22.169195));

        GoogleMapPolyline overlay = new GoogleMapPolyline(
                points, "#d31717", 0.8, 10);
        googleMap.addPolyline(overlay);


		// Replace listing with filtered content when user changes filter
		filter.setValueChangeMode(ValueChangeMode.LAZY);
		filter.addValueChangeListener(e -> listLocations(e.getValue()));

		// Connect selected Customer to editor or hide if none is selected
		grid.asSingleSelect().addValueChangeListener(e -> {
		    option.addComponent(googleMap);
		    Location currentLocation = e.getValue();
		    if(currentLocation != null) {
                String currentName = currentLocation.getName();
                LatLon currentLatLon = currentLocation.getLatLon();
                googleMap.setCenter(currentLatLon);
                googleMap.clearMarkers();
                googleMap.addMarker(currentName, currentLatLon, false, null);
            }
			editor.editLocation(e.getValue());
		});

		// Instantiate and edit new Customer the new button is clicked
		addNewBtn.addClickListener(e -> editor.editLocation(new Location("", new LatLon())));

		// Listen changes made by the editor, refresh data from backend
		editor.setChangeHandler(() -> {
			editor.setVisible(false);
            listLocations(filter.getValue());
		});


		// Initialize listing
		listLocations(null);
	}

	// tag::listCustomers[]
/*	void listCustomers(String filterText) {
		if (StringUtils.isEmpty(filterText)) {
			grid.setItems(repo.findAll());
		}
		else {
			grid.setItems(repo.findByLastName(filterText));
		}
	} */
	// end::listCustomers[]

    // tag::listCustomers[]
    void listLocations(String filterText) {
        if (StringUtils.isEmpty(filterText)) {
            grid.setItems(repo.findAll());
        }
        else {
            grid.setItems(repo.findByName(filterText));
        }
    }
    // end::listCustomers[]


    private ChartJs  generateBarChart() {
        BarChartConfig config = new BarChartConfig();
        config
                .data()
                .labels("January", "February", "March", "April", "May", "June", "July")
                .addDataset(new BarDataset().type().label("Dataset 1").backgroundColor("rgba(151,187,205,0.5)").borderColor("white").borderWidth(2))
                .addDataset(new LineDataset().type().label("Dataset 2").backgroundColor("rgba(151,187,205,0.5)").borderColor("white").borderWidth(2))
                .addDataset(new BarDataset().type().label("Dataset 3").backgroundColor("rgba(220,220,220,0.5)"))
                .and();

        config.
                options()
                .responsive(true)
                .title()
                .display(true)
                .position(Position.LEFT)
                .text("Chart.js Combo Bar Line Chart")
                .and()
                .done();

        List<String> labels = config.data().getLabels();
        for (Dataset<?, ?> ds : config.data().getDatasets()) {
            List<Double> data = new ArrayList<>();
            for (int i = 0; i < labels.size(); i++) {
                data.add((double) (Math.random() > 0.5 ? 1.0 : -1.0) * Math.round(Math.random() * 100));
            }

            if (ds instanceof BarDataset) {
                BarDataset bds = (BarDataset) ds;
                bds.dataAsList(data);
            }

            if (ds instanceof LineDataset) {
                LineDataset lds = (LineDataset) ds;
                lds.dataAsList(data);
            }
        }

        ChartJs chart = new ChartJs(config);
        chart.setJsLoggingEnabled(true);

        return chart;
    }

}
