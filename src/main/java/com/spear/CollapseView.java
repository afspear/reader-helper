package com.spear;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

public class CollapseView extends Panel {
    private Component content;
    private HorizontalLayout horizontalLayout = new HorizontalLayout();
    public CollapseView(String small, Component content) {
        this.content = content;

        Label label = new Label(small);


        Button button = new Button(FontAwesome.CARET_RIGHT);
        button.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        button.addClickListener(event -> {
            content.setVisible(!content.isVisible());
            if (content.isVisible()) {
                button.setIcon(FontAwesome.CARET_DOWN);
                horizontalLayout.replaceComponent(label, content);
            }

            else {
                button.setIcon(FontAwesome.CARET_RIGHT);
                horizontalLayout.replaceComponent(content, label);
                horizontalLayout.setComponentAlignment(label, Alignment.MIDDLE_LEFT);
            }

        });
        content.setVisible(false);
        horizontalLayout.addComponents(button, label);
        horizontalLayout.setComponentAlignment(label, Alignment.MIDDLE_LEFT);
        this.setContent(horizontalLayout);
    }



}
