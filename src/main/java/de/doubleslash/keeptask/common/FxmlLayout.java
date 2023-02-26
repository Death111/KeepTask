package de.doubleslash.keeptask.common;

import javafx.fxml.FXMLLoader;
import org.springframework.context.ConfigurableApplicationContext;

public class FxmlLayout {

    private static ConfigurableApplicationContext springContext;

    // TODO I guess we can get the context also via Spring annotation
    public static void setContext(ConfigurableApplicationContext springContext) {
        FxmlLayout.springContext = springContext;
    }

    public static FXMLLoader createLoaderFor(Resources.RESOURCE resource) {
        final FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Resources.getResource(resource));
        loader.setControllerFactory(springContext::getBean);
        return loader;
    }
}
