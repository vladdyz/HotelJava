package ca.senecapolytechnic.application.apd545project;

import ca.senecapolytechnic.application.apd545project.config.GuiceModule;
import ca.senecapolytechnic.application.apd545project.config.HibernateUtil;
import ca.senecapolytechnic.application.apd545project.controllers.KioskController;
import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    private Injector injector;
    private Stage primaryStage;

    @Override
    public void init(){
        injector = Guice.createInjector(new GuiceModule());
    }
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("welcome-view.fxml"));
        fxmlLoader.setControllerFactory(injector::getInstance);
        //KioskController controller = injector.getInstance(KioskController.class);
        //fxmlLoader.setController(controller);
        Parent root = fxmlLoader.load();
        //Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        Scene scene = new Scene(root, 924, 694);
        stage.setTitle("Come On Inn!!");
        stage.setScene(scene);
        stage.show();
    }
    @Override
    public void stop(){
        try{
            HibernateUtil.shutdown();
        }finally {
            Platform.exit();
        }
    }
}
// ca.senecapolytechnic.application.apd545project.controllers.KioskController