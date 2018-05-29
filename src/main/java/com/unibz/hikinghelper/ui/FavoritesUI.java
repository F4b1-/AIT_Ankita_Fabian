package com.unibz.hikinghelper.ui;

import com.unibz.hikinghelper.Constants.Constants;
import com.unibz.hikinghelper.model.Location;
import com.unibz.hikinghelper.dao.LocationRepository;
import com.unibz.hikinghelper.util.DownloadFileCreator;
import com.unibz.hikinghelper.util.ElevationHelper;
import com.unibz.hikinghelper.util.UIHelper;
import com.vaadin.annotations.Theme;
import com.vaadin.server.*;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

@Theme("mytheme")
@SpringUI(path = "/favorites")
public class FavoritesUI extends UI {

    private final LocationRepository repo;


    final Grid<Location> grid;

    private final Button addNewBtn;

    private static final Logger log = LoggerFactory.getLogger(FavoritesUI.class);

    @Autowired
    ElevationHelper elevationHelper;

    private final static String MENU_BUTTON_STYLENAME = "menu_button";


    public FavoritesUI(LocationRepository repo) {
        this.repo = repo;
        this.grid = new Grid<>(Location.class);
        this.addNewBtn = new Button("New Location", FontAwesome.PLUS);
        setStyleName("background_image");
    }

    @Override
    protected void init(VaadinRequest request) {
        CssLayout menu = UIHelper.createMenuBar(this);
        // build layout
        grid.setHeight(300, Unit.PIXELS);


        ComboBox<String> formatComboBox = new ComboBox<>("Desired download format");
        formatComboBox.setEmptySelectionAllowed(false);
        formatComboBox.setItems(Constants.GPX_FILE, Constants.CSV_FILE);
        formatComboBox.setSelectedItem(Constants.GPX_FILE);

        formatComboBox.addValueChangeListener(event -> {
            grid.clearSortOrder();
        });


        VerticalLayout gridLayout = new VerticalLayout(formatComboBox, grid);
        HorizontalLayout main = new HorizontalLayout(menu, gridLayout);
        main.setExpandRatio(menu, 2);
        main.setExpandRatio(gridLayout, 8);
        main.setSizeFull();
        setContent(main);

        grid.setColumns("name", "difficulty");
        grid.addColumn(location -> location.getDuration().toHours()).setCaption("Duration in h");


        grid.addColumn(
                location -> {
                    StreamResource myResource = DownloadFileCreator.createFileLocation(location, formatComboBox.getValue());
                    FileDownloader fileDownloader = new FileDownloader(myResource);
                    Button downloadButton = new Button(Constants.DOWNLOAD_BUTTON );
                    downloadButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
                    fileDownloader.extend(downloadButton);
                    return downloadButton;

                } ,
                new ComponentRenderer()
        );

        // Initialize listing
        WrappedSession wrappedSession = VaadinService.getCurrentRequest().getWrappedSession();
        ArrayList<Location> favorites= (ArrayList<Location>) wrappedSession.getAttribute("favorites");
        if(favorites != null) {
            grid.setItems(favorites);
        }

    }


    private Button buildDownloadButton() {
        Button downloadButton = new Button("Download");
        downloadButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        //button.addClickListener(e -> deletePerson(p));
        return downloadButton;
    }



}
