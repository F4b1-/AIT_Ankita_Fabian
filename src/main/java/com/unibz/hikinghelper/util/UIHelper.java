package com.unibz.hikinghelper.util;

import com.vaadin.navigator.Navigator;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

public class UIHelper {

    public static CssLayout createMenuBar(UI ui) {
        Button homeButton = new Button("Home");
        homeButton.addStyleNames(ValoTheme.BUTTON_LINK, ValoTheme.MENU_ITEM, "menuItem1");
        homeButton.addClickListener(clickEvent -> {
            ui.getPage().setLocation("/application");
        });
        Button favButton = new Button("Favorites");
        favButton.addStyleNames(ValoTheme.BUTTON_LINK, ValoTheme.MENU_ITEM, "menuItem2");
        favButton.addClickListener(clickEvent -> {
            ui.getPage().setLocation("/favorites");
        });
        Button aboutUsButton = new Button("About Us");
        aboutUsButton.addStyleNames(ValoTheme.BUTTON_LINK, ValoTheme.MENU_ITEM, "menuItem3");
        aboutUsButton.addClickListener(clickEvent -> {
            ui.getPage().setLocation("/aboutUs.html");
        });
        Button logoutButton = new Button("Logout");
        logoutButton.addStyleNames(ValoTheme.BUTTON_LINK, ValoTheme.MENU_ITEM, "menuItem3");
        logoutButton.addClickListener(clickEvent -> {
            ui.getPage().setLocation("/logout");
        });

        CssLayout menu = new CssLayout(homeButton, favButton, aboutUsButton, logoutButton);
        menu.addStyleName(ValoTheme.MENU_ROOT);
        return menu;

    }
}
