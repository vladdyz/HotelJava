module ca.senecapolytechnic.application.apd545project {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.hibernate.orm.core;
    requires org.slf4j;
    requires java.persistence;
    requires jbcrypt;
    requires java.logging;
    requires com.google.guice;
    requires java.naming;
    requires javafx.graphics;
    requires java.sql;
    requires jul.to.slf4j;
    requires javafx.base;
    requires layout;
    requires kernel;
    requires jdk.xml.dom;
    requires java.desktop;


    opens ca.senecapolytechnic.application.apd545project to javafx.fxml;
    opens ca.senecapolytechnic.application.apd545project.config to com.google.guice;
    opens ca.senecapolytechnic.application.apd545project.repositories to com.google.guice;
    opens ca.senecapolytechnic.application.apd545project.services to com.google.guice;
    opens ca.senecapolytechnic.application.apd545project.security to com.google.guice;
    opens ca.senecapolytechnic.application.apd545project.controllers to com.google.guice, javafx.fxml;
    opens ca.senecapolytechnic.application.apd545project.models to org.hibernate.orm.core;
    opens ca.senecapolytechnic.application.apd545project.utils to com.google.guice;

    exports ca.senecapolytechnic.application.apd545project;
}