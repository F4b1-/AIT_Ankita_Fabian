package com.unibz.hikinghelper.util;

import com.vaadin.navigator.Navigator;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

public class UIHelper {

    public static CssLayout createMenuBar(UI ui) {
        Label title = new Label("Menu");
        title.addStyleName(ValoTheme.MENU_TITLE);

        Button homeButton = new Button("Home");
        homeButton.addStyleNames(ValoTheme.BUTTON_LINK, ValoTheme.MENU_ITEM);
        homeButton.addClickListener(clickEvent -> {
            ui.getPage().setLocation("/application");
        });
        Button favButton = new Button("Favorites");
        favButton.addStyleNames(ValoTheme.BUTTON_LINK, ValoTheme.MENU_ITEM);
        favButton.addClickListener(clickEvent -> {
            ui.getPage().setLocation("/favorites");
        });
        Button aboutUsButton = new Button("About Us");
        aboutUsButton.addStyleNames(ValoTheme.BUTTON_LINK, ValoTheme.MENU_ITEM);
        aboutUsButton.addClickListener(clickEvent -> {
            //ui.getPage().setLocation("/aboutUs");
        });

        CssLayout menu = new CssLayout(title, homeButton, favButton, aboutUsButton);
        menu.addStyleName(ValoTheme.MENU_ROOT);
        return menu;

    }
}
