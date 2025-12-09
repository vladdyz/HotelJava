package ca.senecapolytechnic.application.apd545project.utils;

import com.google.inject.Inject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Observable {
    private static Observable instance;
    private final StringProperty latestNotification = new SimpleStringProperty("");

    @Inject
    private Observable() {}

    public static Observable getInstance() {
        if (instance == null) {
            instance = new Observable();
        }
        return instance;
    }

    public StringProperty notificationProperty() {
        return latestNotification;
    }

    public void notify(String msg) {
        latestNotification.set(msg);
    }
}
