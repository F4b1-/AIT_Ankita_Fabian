package com.unibz.hikinghelper.ui;

import com.unibz.hikinghelper.Constants.Constants;
import com.unibz.hikinghelper.model.Location;
import com.unibz.hikinghelper.dao.LocationRepository;
import com.unibz.hikinghelper.model.Difficulty;
import com.unibz.hikinghelper.util.ElevationHelper;
import com.unibz.hikinghelper.util.UIHelper;
import com.unibz.hikinghelper.util.Utils;
import com.vaadin.annotations.Theme;
import com.vaadin.server.*;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.ComponentRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;

@Theme("mytheme")
@SpringUI(path = "/application")
public class MainUI extends UI {

    private final LocationRepository repo;

    private final LocationEditor editor;

    final Grid<Location> grid;

    final TextField filter;

    private final Button addNewBtn;

    private static final Logger log = LoggerFactory.getLogger(MainUI.class);

    @Autowired
    ElevationHelper elevationHelper;

    private final static String MENU_BUTTON_STYLENAME = "menu_button";


    public MainUI(LocationRepository repo, LocationEditor editor) {
        this.repo = repo;
        this.editor = editor;
        this.grid = new Grid<>(Location.class);
        this.filter = new TextField();
        this.addNewBtn = new Button("New Location", FontAwesome.PLUS);
        setStyleName("background_image");
    }

    @Override
    protected void init(VaadinRequest request) {

        Collection<GrantedAuthority> authorities =  getAuthorities();
    	
        CssLayout menu = UIHelper.createMenuBar(this);
        // build layout
        HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);
        HorizontalLayout option = new HorizontalLayout(grid, editor);

        VerticalLayout mainLayout1 = new VerticalLayout(actions, option);
		HorizontalLayout main = new HorizontalLayout(menu, mainLayout1);
		Image image = new Image();
    	image.setSource(new ThemeResource("logo.jpg")); 
    	image.setWidth("170px");
    	image.setHeight("80px");
    	
    	menu.addComponent(image);
    	image.setStyleName("logoStyle");
    	//menu.setComponentAlignment(image, Alignment.BOTTOM_LEFT);
		main.setExpandRatio(menu, 2);
		main.setExpandRatio(mainLayout1, 8);
		main.setSizeFull();
		setContent(main);
        grid.setHeight(300, Unit.PIXELS);

        //grid.addColumn(location -> location.getDuration()).setCaption("Duration");
        //grid.addColumn(location -> location.getLatLon().getLon()).setCaption("Longitude").setId("longitude");

        grid.setColumns("name", "difficulty", "duration");

        if (Utils.isAdmin(authorities)) {
            grid.addColumn(
                    location -> {
                        Button editButton = new Button(Constants.EDIT_BUTTON);
                        editButton.addClickListener(e -> editor.editLocation(location));
                        
                        return editButton;

                    },
                    new ComponentRenderer()
            );
        }

        filter.setPlaceholder("Filter by name");

        log.info(SecurityContextHolder.getContext().getAuthentication().getAuthorities().toArray()[0].toString());



        // Replace listing with filtered content when user changes filter
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(e -> listLocations(e.getValue()));

        // Connect selected Customer to editor or hide if none is selected
        grid.asSingleSelect().addValueChangeListener(e -> {
            Location currentLocation = e.getValue();
            if (currentLocation != null) {
                DetailWindow detailWindow = new DetailWindow(currentLocation, elevationHelper, authorities);
                addWindow(detailWindow);

            }
            //editor.editLocation(e.getValue());

        });

        // Instantiate and edit new Customer the new button is clicked
        addNewBtn.addClickListener(e -> editor.editLocation(new Location("", new LatLon(), Difficulty.MEDIUM, Duration.ofHours(0), new ArrayList<LatLon>() , new ArrayList<Double>())));

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
            Location foundLocation =repo.findByName(filterText);
            if(foundLocation != null) {
                grid.setItems(foundLocation);
            } else {
                grid.setItems(new ArrayList<>());

            }

        }
    }

    private Collection<GrantedAuthority> getAuthorities() {
        return (Collection<GrantedAuthority>)
                SecurityContextHolder.getContext().getAuthentication().getAuthorities();
    }



}
