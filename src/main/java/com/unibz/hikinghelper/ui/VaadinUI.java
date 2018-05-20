package com.unibz.hikinghelper.ui;

import com.byteowls.vaadin.chartjs.config.BarChartConfig;
import com.byteowls.vaadin.chartjs.data.BarDataset;
import com.byteowls.vaadin.chartjs.data.Dataset;
import com.byteowls.vaadin.chartjs.data.LineDataset;
import com.byteowls.vaadin.chartjs.options.Position;
import com.unibz.hikinghelper.Application;
import com.unibz.hikinghelper.Location;
import com.unibz.hikinghelper.LocationRepository;
import com.unibz.hikinghelper.util.ElevationHelper;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.byteowls.vaadin.chartjs.ChartJs;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Theme("mytheme")
@SpringUI(path = "/application")
public class VaadinUI extends UI {

    private final LocationRepository repo;

    private final LocationEditor editor;

    final Grid<Location> grid;

    final TextField filter;

    private final Button addNewBtn;

    private static final Logger log = LoggerFactory.getLogger(VaadinUI.class);

    @Autowired
    ElevationHelper elevationHelper;

    private final static String MENU_BUTTON_STYLENAME = "menu_button";


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

        VerticalLayout mainLayout = new VerticalLayout(actions, option, editor);



        HorizontalLayout main = new HorizontalLayout(mainLayout);
        setContent(main);
        grid.setHeight(300, Unit.PIXELS);

        //grid.addColumn(location -> location.getDuration()).setCaption("Duration");
        //grid.addColumn(location -> location.getLatLon().getLon()).setCaption("Longitude").setId("longitude");

        grid.setColumns("name", "difficulty", "duration");
        filter.setPlaceholder("Filter by name");

        log.info(SecurityContextHolder.getContext().getAuthentication().getAuthorities().toArray()[0].toString());



        // Replace listing with filtered content when user changes filter
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(e -> listLocations(e.getValue()));

        // Connect selected Customer to editor or hide if none is selected
        grid.asSingleSelect().addValueChangeListener(e -> {
            Location currentLocation = e.getValue();
            if (currentLocation != null) {
                Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>)
                        SecurityContextHolder.getContext().getAuthentication().getAuthorities();


                DetailWindow detailWindow = new DetailWindow(currentLocation, elevationHelper, authorities);
                addWindow(detailWindow);

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


    void listLocations(String filterText) {
        if (StringUtils.isEmpty(filterText)) {
            grid.setItems(repo.findAll());
        } else {
            grid.setItems(repo.findByName(filterText));
        }
    }


}
