package com.spear;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import com.ejt.vaadin.sizereporter.ComponentResizeEvent;
import com.ejt.vaadin.sizereporter.ComponentResizeListener;
import com.ejt.vaadin.sizereporter.SizeReporter;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.BootstrapFragmentResponse;
import com.vaadin.server.BootstrapListener;
import com.vaadin.server.BootstrapPageResponse;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.easyuploads.UploadField;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
@Push
public class MyUI extends UI {

    Image image = new Image();
    final Label name = new Label("Give me a photo of a page, and I may can help...");
    Panel panel = new Panel(image);
    Button doAnother = new Button("Do Another");

    AtomicInteger panelWidth = new AtomicInteger();
    AtomicInteger panelHeight = new AtomicInteger();
    private WordBlocks wordBlocks;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout layout = new VerticalLayout();
        image.setSource(null);
        
        name.setStyleName(ValoTheme.LABEL_LARGE);

        final UploadField uploadField = new UploadField();
        uploadField.setDisplayUpload(false);
        uploadField.setButtonCaption("Choose Photo of Page");
        uploadField.setFieldType(UploadField.FieldType.BYTE_ARRAY);

        uploadField.addValueChangeListener(valueChangeEvent -> {
            Object value = valueChangeEvent.getProperty().getValue();
            name.setValue("Got it. Let me see what i can do. Sit tight...");
            System.out.println();
            String mime = uploadField.getLastMimeType();
            if (!mime.contains("image")) {
                name.setValue("That was a " + mime.split("/")[1] + ". I actually need a photo.");
                return;
            }
            System.out.println(uploadField.getLastFileName());

            layout.removeComponent(uploadField);
            showUploadedImage(value);

        });

        image.setVisible(false);
        image.setSizeFull();

        SizeReporter sizeReporter = new SizeReporter(panel);
        sizeReporter.addResizeListener((ComponentResizeListener)event -> {
            panelWidth.set(event.getWidth());
            panelHeight.set(event.getHeight());
        });



        doAnother.setVisible(false);
        doAnother.addClickListener(event -> Page.getCurrent().reload());
        HorizontalLayout horizontalLayout = new HorizontalLayout(name, doAnother);
        horizontalLayout.setWidth("100%");
        horizontalLayout.setExpandRatio(name, 1.0f);
        horizontalLayout.setComponentAlignment(doAnother, Alignment.MIDDLE_RIGHT);

        layout.addComponents(horizontalLayout,uploadField, panel);
        layout.setMargin(true);
        layout.setSpacing(true);
        
        setContent(layout);
    }

    private void showUploadedImage(Object value) {
        final byte[] data = (byte[]) value;

        image.setSource(new ExternalResource("https://upload.wikimedia.org/wikipedia/commons/f/f5/Blender3D_KolbenZylinderAnimation.gif"));
        image.setVisible(true);

        new Thread(() -> {

            String ocrData = OCR.readImage(data);
            try{

            Graphics graphics =  new Graphics(data);
            wordBlocks = new WordBlocks(graphics.getHeight(), graphics.getWidth());

            OCR.consumeAllBlockPolygons(ocrData, polygonStringMap -> {
                graphics.drawPolygon(polygonStringMap.getKey());
                wordBlocks.addWordBlock(polygonStringMap.getKey(), polygonStringMap.getValue());
            });


            graphics.dispose();

            // Return a stream from the buffer
            MyUI.getCurrent().access(() -> {
                name.setValue("See if this helps...");
                doAnother.setVisible(true);
                image.setSource(createStreamResource(graphics.getbImageFromConvert()));
                panel.addClickListener(event -> {
                    double percentX = (double) event.getRelativeX() / (double) panelWidth.get();
                    double percentY = (double) event.getRelativeY() / (double) panelHeight.get();

                    if (wordBlocks !=null) {
                        String text = wordBlocks.getTextForPoint(percentX, percentY);
                        if (StringUtils.isBlank(text))
                            return;
                        this.addWindow(new WordBlockWindow(text));
                    }


                });

            });

            } catch (IOException e) {
                e.printStackTrace();
            }




        }).start();


    }

    private StreamResource createStreamResource(BufferedImage bi) {
        return new StreamResource((StreamResource.StreamSource)() -> {

            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ImageIO.write(bi, "png", bos);
                return new ByteArrayInputStream(bos.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }, UUID.randomUUID().toString()+".png");
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
        @Override
        protected void servletInitialized() throws ServletException {
            super.servletInitialized();
            getService().addSessionInitListener(new SessionInitListener() {

                private static final long serialVersionUID = 1L;

                @Override
                public void sessionInit(SessionInitEvent event) {
                    event.getSession()
                      .addBootstrapListener(new BootstrapListener() {

                          private static final long serialVersionUID = 1L;

                          @Override
                          public void modifyBootstrapFragment(BootstrapFragmentResponse response) {
                              // TODO Auto-generated method stub

                          }

                          @Override
                          public void modifyBootstrapPage(BootstrapPageResponse response) {

                              // Meta

                              response.getDocument()
                                .head()
                                .prependElement("meta")
                                .attr("name", "viewport")
                                .attr("content", "user-scalable=no; width=device-width, initial-scale=1.0, maximum-scale=1");
                              response.getDocument()
                                .head()
                                .prependElement("meta")
                                .attr("name", "apple-mobile-web-app-capable")
                                .attr("content", "yes");
                              response.getDocument()
                                .head()
                                .prependElement("meta")
                                .attr("name", "mobileoptimized")
                                .attr("content", "0");

                              response.getDocument()
                                .head()
                                .prependElement("link")
                                .attr("rel", "apple-touch-icon")
                                .attr("sizes", "57x57")
                                .attr("href", "/VAADIN/themes/mytheme/img/appIcon.jpg");


                              if (response.getRequest()
                                .getHeader("User-Agent")
                                .contains("IEMobile/10.0")) {
                                  response.getDocument()
                                    .head()
                                    .prependElement("style")
                                    .attr("type", "text/css")
                                    .append("@-ms-viewport{width:auto!important}");
                              }

                          }
                      });

                }
            });
        }
    }


}
