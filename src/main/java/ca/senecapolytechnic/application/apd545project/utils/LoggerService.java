package ca.senecapolytechnic.application.apd545project.utils;

import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.IOException;
import java.util.logging.*;

public class LoggerService {
    public static void configure() {

        java.util.logging.Logger rootLogger =
                java.util.logging.Logger.getLogger("");

        rootLogger.setLevel(Level.INFO);

        try {
            FileHandler fileHandler = new FileHandler(
                    "logs/system_logs.%g.log",
                    1024 * 1024,   // 1 MB per file
                    10,            // keep 10 files
                    true           // append mode
            );

            fileHandler.setFormatter(new SimpleFormatter());
            rootLogger.addHandler(fileHandler);

        } catch (IOException e) {
            rootLogger.log(Level.SEVERE, "Failed to init file logger", e);
        }
    }
}
