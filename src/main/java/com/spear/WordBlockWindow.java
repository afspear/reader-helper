package com.spear;

import com.vaadin.server.Page;
import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ColorPicker;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import java.util.StringJoiner;
import java.util.stream.IntStream;

/**
 * Created by aspear on 6/5/17.
 */
public class WordBlockWindow extends Window {
    public WordBlockWindow(String text) {

        Label label = new Label(text);
        label.setStyleName(ValoTheme.LABEL_HUGE);
        label.setContentMode(ContentMode.PREFORMATTED);
        label.addStyleName("wrap");
        label.addStyleName("customcolor");
        label.addStyleName("customspacing");
        ColorPicker colorPicker = new ColorPicker("Background Color");
        colorPicker.setCaption("Background Color");
        colorPicker.addColorChangeListener(event -> {
            Color color = event.getColor();

            // Get the stylesheet of the page
            Page.getCurrent().getStyles().add(
              ".v-label-customcolor pre { " +
                "background:" + color.getCSS() + "; " +
            "}");
        });

        NativeSelect lineSpacing = new NativeSelect("Line Spacing");
        IntStream.range(10, 40)
          .forEach(lineSpacing::addItem);

        lineSpacing.addValueChangeListener(event -> {

            Object spacing = event.getProperty().getValue();
            Page.getCurrent().getStyles().add(
              ".v-label-customspacing pre { " +
                    "letter-spacing:" + event.getProperty().getValue() + "px; " +
                "}");
        });

        HorizontalLayout horizontalLayout = new HorizontalLayout(colorPicker, lineSpacing);
        horizontalLayout.setSpacing(true);
        VerticalLayout verticalLayout = new VerticalLayout(horizontalLayout, label, new Button("Close", (event) -> this.close()));
        verticalLayout.setMargin(true);
        this.setContent(verticalLayout);
        this.setDraggable(true);
        this.setResizable(true);
        this.setModal(true);
        this.setWidth("100%");
        this.setCaption("Helper");

        Page.Styles styles = Page.getCurrent().getStyles();

        styles.add(
          ".v-label-wrap pre { " +
              "white-space: pre-wrap; " +
              "word-wrap: break-word;" +
              "letter-spacing: 27px;" +
              "line-height: 57px;" +
              "color:black;" +
          "}"
        );



    }
}
