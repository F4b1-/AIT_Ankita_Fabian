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
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapPolyline;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.byteowls.vaadin.chartjs.ChartJs;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Theme("mytheme")
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

	@Autowired
	HttpSession session;


    public VaadinUI(LocationRepository repo, LocationEditor editor) {
		this.repo = repo;
		this.editor = editor;
		this.grid = new Grid<>(Location.class);
		this.filter = new TextField();
		this.addNewBtn = new Button("New Location", FontAwesome.PLUS);
        setStyleName("background_image");
	}

	@Override
	protected void init(VaadinRequest request) {
		// build layout
		HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);
        HorizontalLayout option = new HorizontalLayout(grid);
        //actions.addComponent(generateBarChart());



        //this.getSession().setAttribute("hey", "123");



        // ***  WINDOW  ***
        Window subWindow = new Window("Sub-Window");

        Label labelName = new Label("");
        Label labelDuration = new Label("");
        Label labelDifficulty = new Label("");

        Button favButton = new Button("Add to favorites");
        favButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        favButton.addClickListener(clickEvent ->
                VaadinService.getCurrentRequest().getWrappedSession()
                        .setAttribute("hey", "button was pressed"));

        VerticalLayout infoLayout = new VerticalLayout(labelName, labelDuration, labelDifficulty, favButton);
        HorizontalLayout subContent = new HorizontalLayout(infoLayout, googleMap, generateBarChart());
        subWindow.setContent(subContent);
        subWindow.center();



		VerticalLayout mainLayout = new VerticalLayout(actions, option, editor);
        HorizontalLayout main = new HorizontalLayout(mainLayout);
		setContent(main);

		grid.setHeight(300, Unit.PIXELS);

        //grid.addColumn(location -> location.getDuration()).setCaption("Duration");
      //  grid.addColumn(location -> location.getLatLon().getLon()).setCaption("Longitude").setId("longitude");

		grid.setColumns("name", "difficulty", "duration");

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
		googleMap.setZoom(16);
		googleMap.setCenter(new LatLon(
                60.450403, 22.230399));

        ArrayList<LatLon> points = new ArrayList<LatLon>();
        points.add(new LatLon(60.448118, 22.253738));
        points.add(new LatLon(60.455144, 22.24198));
        points.add(new LatLon(60.460222, 22.211939));
        points.add(new LatLon(60.488224, 22.174602));
        points.add(new LatLon(60.486025, 22.169195));


		// Replace listing with filtered content when user changes filter
		filter.setValueChangeMode(ValueChangeMode.LAZY);
		filter.addValueChangeListener(e -> listLocations(e.getValue()));

		// Connect selected Customer to editor or hide if none is selected
		grid.asSingleSelect().addValueChangeListener(e -> {
		    //option.addComponent(googleMap);
		    Location currentLocation = e.getValue();
		    if(currentLocation != null) {
                String currentName = currentLocation.getName();
                LatLon currentLatLon = currentLocation.getLatLon();
                ArrayList<LatLon> currentRoute = currentLocation.getRoute();
                googleMap.setCenter(currentLatLon);
                googleMap.clearMarkers();
                googleMap.addMarker(currentName, currentLatLon, false, null);
                GoogleMapPolyline currentOverlay = new GoogleMapPolyline(
                        currentRoute, "#d31717", 0.8, 5);
                googleMap.addPolyline(currentOverlay);
                //VaadinService.getCurrentRequest().getWrappedSession().getAttribute("hey").toString()
                labelName.setValue(e.getValue().getName());
                labelDuration.setValue(e.getValue().getDuration().toString());
                labelDifficulty.setValue(e.getValue().getDifficulty().toString());

            }
			editor.editLocation(e.getValue());
            addWindow(subWindow);
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
                .labels("1km", "2km", "3km", "4km", "5km", "6km", "7km")
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
            List<Double> data = new ArrayList<>();
            data.add(1400.0);
            data.add(1600.0);
            data.add(1800.0);
            data.add(2400.0);
            data.add(2200.0);
            data.add(1600.0);
            data.add(1400.0);





            if (ds instanceof LineDataset) {
                LineDataset lds = (LineDataset) ds;
                lds.dataAsList(data);
            }
        }

        ChartJs chart = new ChartJs(config);
        chart.setWidth(600, Unit.PIXELS);
        chart.setJsLoggingEnabled(true);

        return chart;
    }

}
