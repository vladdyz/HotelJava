package ca.senecapolytechnic.application.apd545project.utils;

import ca.senecapolytechnic.application.apd545project.models.AuditLog;
import ca.senecapolytechnic.application.apd545project.models.Feedback;
import ca.senecapolytechnic.application.apd545project.models.Room;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

// using printwriter instead of bufferedwriter for automated flushing, newlines, and obj data types
public class CsvExporter {
    // if root/export doesnt already exist, make it
    private static File getExportDir() {
        File exportDir = new File("export");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        return exportDir;
    }
    private static String timestamp() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
    }

    public static void exportAuditLogsCSV(List<AuditLog> logs) throws IOException {
        File file = new File(getExportDir(), "AuditLogs-" + timestamp() + ".csv"); //filename
        try (PrintWriter pw = new PrintWriter(file)) {
            pw.println("timestamp,actor,action,entityType,entityId,message"); // table headers
            for (AuditLog log : logs) { // columns
                pw.printf("%s,%s,%s,%s,%d,%s%n",
                        log.getTimestamp(),
                        escapeCSV(log.getActor()),
                        escapeCSV(log.getAction()),
                        log.getEntityType(),
                        log.getEntityId(),
                        escapeCSV(log.getMessage())
                );
            }
        }
    }
    // commasafety
    private static String escapeCSV(String s) {
        if (s.contains(",") || s.contains("\"")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    // the uml specified only pdf and csv export classes, so i merged the txt export here
    public static void exportAuditLogsTXT(List<AuditLog> logs) throws IOException {
        File file = new File(getExportDir(), "Audit Logs-" + timestamp() + ".txt");
        try (PrintWriter pw = new PrintWriter(file)) {
            for (AuditLog log : logs) {
                pw.println("-----| Audit Log |-----");
                pw.println("Time: " + log.getTimestamp());
                pw.println("Actor: " + log.getActor());
                pw.println("Action: " + log.getAction());
                pw.println("Entity: " +  log.getEntityType());
                pw.println("Entity ID: " +  log.getEntityId());
                pw.println("Message: " + log.getMessage());
                pw.println("-----------------------");
                pw.println();
            }
        }
    }

    public static void exportFeedbackCSV(List<Feedback> feedbackList) throws IOException {
        File file = new File(getExportDir(), "Feedback-" + timestamp() + ".csv");
        try (PrintWriter pw = new PrintWriter(file)) {
            pw.println("guest,rating,sentiment,message,reservation,date");
            for (Feedback f : feedbackList) {
                pw.printf("%s, %d, %s, %s, %d, %s%n",
                escapeCSV(f.getGuest().getName()),
                f.getRating(),
                f.getSentimentTag(),
                escapeCSV(f.getComments()),
                f.getReservation().getId(),
                f.getCreatedAt()
                );
            }
        }
    }

    public static void exportOccupancy(List<Room> roomsList, String selected, double percentage) throws IOException {
        File file = new File(getExportDir(), "OccupancyReport-" + timestamp() + ".csv");
        try (PrintWriter pw = new PrintWriter(file)) {
            pw.println("id,roomNum,roomType,beds,basePrice,status");
            for (Room r : roomsList) {
                pw.printf("%d, %d, %s, %d, %f, %s%n ",
                        r.getId(),
                        r.getRoomNumber(),
                        r.getRoomType(),
                        r.getBeds(),
                        r.getBasePrice(),
                        r.getRoomStatus()
                );
            }
            pw.println();
            pw.println("Room Occupancy Report for " + selected);
            pw.println("Occupancy Rate: " + percentage);

        }

    }



}
