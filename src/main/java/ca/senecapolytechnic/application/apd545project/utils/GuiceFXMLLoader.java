package ca.senecapolytechnic.application.apd545project.utils;

import com.google.inject.Inject;
import com.google.inject.Injector;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;


public class GuiceFXMLLoader {

    private final Injector injector;
    // i need this to pass some objects that aren't trac


    // use fxmlloader class custom controller factory (a generic callback) to reference the guice instance
    // then have guice construct controller
    //step 1 : FXMLLoader loader = ...
    //step 2 : injector
    //step 3 : set the controller factory and get the instance
    //step 4: load
    @Inject
    public GuiceFXMLLoader(Injector injector) {
        this.injector = injector;
    }

    public Parent load(String resourcePath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resourcePath));
        loader.setControllerFactory(injector::getInstance);
        return loader.load();
    }
    // for manually loading fxml
    public Injector getInjector() {
        return injector;
    }







}