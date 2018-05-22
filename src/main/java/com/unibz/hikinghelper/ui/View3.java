package com.unibz.hikinghelper.ui;

import com.vaadin.navigator.View;
import com.vaadin.ui.Composite;
import com.vaadin.ui.Label;

/**
 * @author Alejandro Duarte
 */
public class View3 extends Composite implements View {

    public View3() {
        setCompositionRoot(new Label("We are an interracial couple, now go away!"));
    }
}