package com.pany;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.*;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This UI is the application entry point. A UI may either represent a browser window
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
public class MyUI extends UI implements baseDesignGame {
    VerticalLayout mainLayout = new VerticalLayout();
    HorizontalLayout layout = null;
    Button buton;
    List<Button> buttonList;
    List<HorizontalLayout> horLayouts;
    Map<HorizontalLayout, Map<Integer, FontAwesome>> genelKontrol;
    List<Button> buttonListExpectForNullData;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        Button button = new Button("Start Game");
        button.addStyleName(ValoTheme.BUTTON_PRIMARY);
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                putButtonsOnBoard(mainLayout);
                startGame();
                button.setEnabled(false);
            }
        });
        mainLayout.addComponent(button);
        setContent(mainLayout);
    }

    @Override
    public void putButtonsOnBoard(VerticalLayout mainLayout) {

        horLayouts = new ArrayList<>();
        buttonList = new ArrayList<>();
        Map<Integer, FontAwesome> verticalButtonsControllerMap = new HashMap<>();

        genelKontrol = new HashMap<>();
        int buttonYLocal = 0;

        for (int horizontalButtonCount = 0; horizontalButtonCount <= 8; horizontalButtonCount++) {
            int buttonXLocal = 1;

            layout = new HorizontalLayout();

            for (int verticalButtonCount = 0; verticalButtonCount <= 8; verticalButtonCount++) {

                if (horizontalButtonCount == 0) {
                    createHorizontalNumberButon(verticalButtonCount);
                } else if (horizontalButtonCount != 0 && verticalButtonCount == 0) {
                    createVerticalNumberButons(horizontalButtonCount);
                } else {
                    createGameButtons();

                }
                buttonXLocal++;
                addButtonLocal(buttonYLocal, buttonXLocal);
                verticalButtonsControllerMap.put(buttonXLocal, FontAwesome.CIRCLE_O);
                layout.addComponent(buton);
                buttonList.add(buton);

            }
            buttonYLocal++;
            horLayouts.add(layout);
            mainLayout.addComponent(layout);
            genelKontrol.put(layout, verticalButtonsControllerMap);
        }
    }

    @Override
    public void addButtonLocal(int buttonYLocal, int buttonXLocal) {
        Map<Integer, Integer> butonLocals = new HashMap<>();
        butonLocals.put(buttonXLocal, buttonYLocal);
        buton.setData(butonLocals);
    }

    @Override
    public void createGameButtons() {
        buton = new Button();
        buton.setWidth("50px");
        buton.setHeight("50px");
        buton.setIcon(FontAwesome.CIRCLE_O);
    }

    private void startGame() {

        for (HorizontalLayout horizontalLayout : horLayouts) {
            for (int i = 0; i <= 8; i++) {

                buttonAddClickListener(genelKontrol, horizontalLayout, i);
            }
        }
    }


    private List<Button> ButtonListWithData() {
        buttonListExpectForNullData = new ArrayList<>();
        for (Button button : buttonList) {
            buttonListExpectForNullData.add(button);
        }
        return buttonListExpectForNullData;
    }


    private void buttonAddClickListener(final Map<HorizontalLayout, Map<Integer, FontAwesome>> genelKontrol, final HorizontalLayout horizontalLayout, int i) {
        Button selectedButton = (Button) horizontalLayout.getComponent(i);

        selectedButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                int result = 0;

                int xCrossLocal = 0;
                int yCrossLocal = 0;

                int horizontalButtonCount = 0;
                int verticalButtonCount = 0;
                int crossLeftCount = 0;
                int crossRightCount = 0;

                if (selectedButton.getIcon().equals(FontAwesome.RA)) {
                    selectedButton.setIcon(FontAwesome.CIRCLE_O);
                    selectedButton.removeStyleName(ValoTheme.BUTTON_DANGER);
                    selectedButton.removeStyleName(ValoTheme.BUTTON_FRIENDLY);
                    ControlWin();

                } else {
                    Map<Integer, Integer> dataSecbtn = (Map<Integer, Integer>) selectedButton.getData();
                    if (dataSecbtn != null) {

                        for (Integer key : dataSecbtn.keySet()) {
                            result = key % 10;
                            yCrossLocal = dataSecbtn.get(key);
                            xCrossLocal = key;
                        }

                        /**
                         * row button control
                         */
                        horizontalButtonCount = horizontalButtonControl(horizontalLayout, horizontalButtonCount);

                        /**
                         * column button control
                         */
                        verticalButtonCount = verticalButtonControl(genelKontrol, result, verticalButtonCount);

                        /**
                         Left side cross button control
                         */
                        crossLeftCount = crossLeftCount(ButtonListWithData(), xCrossLocal, yCrossLocal, crossLeftCount);

                        /**
                         Right side cross button control
                         */
                        crossRightCount = crossRightCount(ButtonListWithData(), xCrossLocal, yCrossLocal, crossRightCount);

                        if (horizontalButtonCount == 8 && verticalButtonCount == 8 && crossLeftCount == 0 && crossRightCount == 0) {
                            selectedButton.setIcon(FontAwesome.RA);
                            selectedButton.addStyleName(ValoTheme.BUTTON_DANGER);

                        }
                        ControlWin();
                    }
                }
            }

            private void ControlWin() {
                List<Button> buttonListForWin = buttonList.stream().filter(button -> (button.getIcon() != null && button.getIcon().equals(FontAwesome.RA))).collect(Collectors.toList());
                if (buttonListForWin.size() == 8) {
                    Notification.show("CONGRATULATIONS, YOU WON", Notification.Type.HUMANIZED_MESSAGE);
                    for (Button button : buttonListForWin) {
                        button.removeStyleName(ValoTheme.BUTTON_DANGER);
                        button.addStyleName(ValoTheme.BUTTON_FRIENDLY);

                    }
                } else {
                    for (Button button : buttonListForWin) {
                        button.removeStyleName(ValoTheme.BUTTON_FRIENDLY);
                        button.addStyleName(ValoTheme.BUTTON_DANGER);

                    }
                }
            }
        });
    }

    private void createHorizontalNumberButon(int buttonCaption) {
        String buttonCaptions = String.valueOf(buttonCaption);

        buton = new Button(buttonCaptions);
        if (buttonCaption == 0) {
            buton.setIcon(FontAwesome.TIMES_CIRCLE);
            buton.setCaption("");
            buton.setEnabled(true);
            buton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {
                    restartGame();
                }
            });
        } else {
            buton.setEnabled(false);
        }
        buton.setWidth("50px");
        buton.setHeight("50px");
    }

    private void restartGame() {
        for (Button button : buttonList) {
            if (button.getIcon() != null && button.getIcon().equals(FontAwesome.RA)) {
                button.setIcon(FontAwesome.CIRCLE_O);
                button.removeStyleName(ValoTheme.BUTTON_DANGER);
                button.removeStyleName(ValoTheme.BUTTON_FRIENDLY);
            }
        }
    }

    private void createVerticalNumberButons(int buttonCaption) {
        String buttonCaptions = String.valueOf(buttonCaption);

        buton = new Button(buttonCaptions);
        if (buttonCaption == 0) {
            buton.setIcon(FontAwesome.TIMES_CIRCLE);
            buton.setCaption("");
            buton.setEnabled(true);
        } else {
            buton.setEnabled(false);
        }
        buton.setWidth("50px");
        buton.setHeight("50px");
    }

    private int crossRightCount(List<Button> buttonList, int xCrossLocal, int yCrossLocal, int crossRightCount) {
        for (int i = xCrossLocal; i <= 8; i++) {
            if (yCrossLocal == 1 || i == 8) {
                xCrossLocal = i;
                break;
            }
            yCrossLocal--;
        }

        for (Button button : buttonList) {
            Map<Integer, Integer> MapControlForCrossRightButton = new HashMap<>();
            MapControlForCrossRightButton.put(xCrossLocal, yCrossLocal);
            if (button.getData().equals(MapControlForCrossRightButton)) {
                if (button.getIcon() != null && button.getIcon().equals(FontAwesome.RA)) {
                    crossRightCount++;

                }

                MapControlForCrossRightButton = new HashMap<>();
                xCrossLocal = xCrossLocal - 1;
                yCrossLocal++;
                MapControlForCrossRightButton.put(xCrossLocal, yCrossLocal);
            }

        }
        return crossRightCount;
    }

    private int crossLeftCount(List<Button> buttonList, int xCrossLocal, int yCrossLocal, int crossLeftCount) {

        for (int i = xCrossLocal; i >= 1; i--) {
            if (yCrossLocal == 1 || i == 1) {
                xCrossLocal = i;
                break;
            }
            yCrossLocal--;
        }

        for (Button button : buttonList) {
            Map<Integer, Integer> MapControlForCrossLeftButton = new HashMap<>();
            MapControlForCrossLeftButton.put(xCrossLocal, yCrossLocal);
            if (button.getData().equals(MapControlForCrossLeftButton)) {
                if (button.getIcon() != null && button.getIcon().equals(FontAwesome.RA)) {
                    crossLeftCount++;

                }

                MapControlForCrossLeftButton = new HashMap<>();
                xCrossLocal = xCrossLocal + 1;
                yCrossLocal++;
                MapControlForCrossLeftButton.put(xCrossLocal, yCrossLocal);
            }
        }

        return crossLeftCount;
    }

    private int verticalButtonControl(Map<HorizontalLayout, Map<Integer, FontAwesome>> genelKontrol, int sonuc, int dikeyButonControl) {
        Set<HorizontalLayout> keys = genelKontrol.keySet();
        for (HorizontalLayout horizontal : keys) {
            for (int i = 0; i <= 8; i++) {
                Button kontroledilenButon = (Button) horizontal.getComponent(i);
                Map<Integer, Integer> kontroledenDatas = (Map<Integer, Integer>) kontroledilenButon.getData();
                    Set<Integer> keyList = kontroledenDatas.keySet();
                    for (Integer keyKontrolEdenData : keyList) {
                        if (keyKontrolEdenData % 10 == sonuc) {
                            if (kontroledilenButon.getIcon() != null && kontroledilenButon.getIcon().equals(FontAwesome.CIRCLE_O)) {
                                dikeyButonControl++;
                            }
                        }
                    }
            }
        }
        return dikeyButonControl;

    }

    private int horizontalButtonControl(HorizontalLayout horizontalLayout, int horizontalButtonCount) {

        for (int i = 1; i <= 8; i++) {

            Button kontroledilenButon = (Button) horizontalLayout.getComponent(i);

            Resource icon = kontroledilenButon.getIcon();
            if (icon != null && icon.equals(FontAwesome.CIRCLE_O)) {
                horizontalButtonCount++;
            }
        }
        return horizontalButtonCount;
    }


    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
