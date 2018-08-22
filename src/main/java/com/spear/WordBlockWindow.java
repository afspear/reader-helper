package com.spear;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.commons.lang3.StringUtils;

import java.util.StringJoiner;
import java.util.UUID;
import java.util.stream.IntStream;

/**
 * Created by aspear on 6/5/17.
 */
public class WordBlockWindow extends Window {
    public WordBlockWindow(String text, String mp3FileId) {

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

        ColorPicker textColorPicker = new ColorPicker("Text Color");
        textColorPicker.setCaption("Text Color");

        textColorPicker.addColorChangeListener(event -> {
            Color color = event.getColor();

            // Get the stylesheet of the page
            Page.getCurrent().getStyles().add(
                    ".v-label-customcolor pre { " +
                            "color:" + color.getCSS() + "; " +
                            "}");
        });

        NativeSelect letterSpacing = new NativeSelect("Letter Spacing");
        letterSpacing.setIcon(FontAwesome.ARROWS_H);
        IntStream.range(10, 40)
                .filter(i -> i % 5 == 0)
          .forEach(letterSpacing::addItem);

        letterSpacing.addValueChangeListener(event -> {

            Object spacing = event.getProperty().getValue();
            Page.getCurrent().getStyles().add(
              ".v-label-customspacing pre { " +
                    "letter-spacing:" + event.getProperty().getValue() + "px; " +
                "}");
        });

        NativeSelect letterSize = new NativeSelect("Letter Size");
        letterSize.setIcon(FontAwesome.TEXT_HEIGHT);
        IntStream.range(20, 60)
                .filter(i -> i % 5 == 0)
                .forEach(letterSize::addItem);

        letterSize.addValueChangeListener(event -> {

            Object spacing = event.getProperty().getValue();
            Page.getCurrent().getStyles().add(
                    ".v-label-customspacing pre { " +
                            "font-size:" + event.getProperty().getValue() + "px; " +
                            "}");
        });

        NativeSelect lineHeight = new NativeSelect("Line Height");
        lineHeight.setIcon(FontAwesome.ARROWS_V);
        IntStream.range(30, 100)
                .filter(i -> i % 5 == 0)
                .forEach(lineHeight::addItem);

        lineHeight.addValueChangeListener(event -> {

            Object spacing = event.getProperty().getValue();
            Page.getCurrent().getStyles().add(
                    ".v-label-customspacing pre { " +
                            "line-height:" + event.getProperty().getValue() + "px; " +
                            "}");
        });

        NativeSelect fontStyle = new NativeSelect("Font Style");
        fontStyle.setIcon(FontAwesome.FONT);
        Fonts
                .fonts
                .forEach(fontStyle::addItem);

        fontStyle.addValueChangeListener(event -> {

            String style = event.getProperty().getValue().toString();
            if(StringUtils.isBlank(style))
                return;

            Page.getCurrent().getStyles().add(
                    ".v-label-customspacing pre { " +
                            "font-family: '" + style + "'; " +
                            "}");
        });

        Audio sample = new Audio();
        final Resource audioResource = new ExternalResource(mp3FileId);
        sample.setSource(audioResource);
        sample.setHtmlContentAllowed(true);
        sample.setAltText("Can't play media");

        CssLayout cssLayout = new CssLayout() {
            @Override
            protected String getCss(Component c) {
                return "margin:5px;";
            }
        };


        cssLayout.addComponents( sample,new VerticalLayout(colorPicker,textColorPicker), new VerticalLayout(letterSpacing, letterSize), new VerticalLayout(lineHeight, fontStyle));
        cssLayout.forEach(component -> {
            component.setWidthUndefined();
        });

        VerticalLayout verticalLayout = new VerticalLayout(cssLayout, label, new Button("Close", (event) -> this.close()));
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
              "letter-spacing: 10px;" +
              "line-height: 57px;" +
              "color:black;" +
              "padding: 10px" +
          "}"
        );



    }
}
