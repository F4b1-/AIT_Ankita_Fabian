package hello;

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

import java.util.ArrayList;

@SpringUI
//@Theme("")
public class VaadinUI extends UI {

	private final LocationRepository repo;

	private final LocationEditor editor;

	final Grid<Location> grid;

	final TextField filter;

	private final Button addNewBtn;

	final GoogleMap googleMap = new GoogleMap("AIzaSyAdXfqEgqkjkDBBFC2dRoWU_-dST-S34dk", null, "english");


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
		VerticalLayout mainLayout = new VerticalLayout(actions, option, editor);
		setContent(mainLayout);

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
		    option.addComponentsAndExpand(googleMap);
		    Location currentLocation = e.getValue();
		    String currentName = currentLocation.getName();
		    LatLon currentLatLon = currentLocation.getLatLon();
            googleMap.setCenter(currentLatLon);
            googleMap.addMarker(currentName, currentLatLon, false, null);
			//editor.editCustomer(e.getValue());
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

}
