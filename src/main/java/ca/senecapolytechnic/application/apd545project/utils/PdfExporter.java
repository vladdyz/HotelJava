package ca.senecapolytechnic.application.apd545project.utils;

import ca.senecapolytechnic.application.apd545project.models.Room;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import org.w3c.dom.css.CSS2Properties;

public class PdfExporter {

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


    public static void exportRoomPDF(List<Room> rooms, String selected, double percentage) throws Exception {
        File file = new File(getExportDir(), "OccupancyReport-" + timestamp() + ".pdf");
        PdfWriter writer = new PdfWriter(new FileOutputStream(file));
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        //PdfWriter.getInstance(document, new FileOutputStream(file));
        //document.open();

        // the date gave me too much problems
        //document.add(new Paragraph("Room Occupancy Report for " + selected.format(DateTimeFormatter.ofPattern("yyyy-MON-dd"))));
        document.add(new Paragraph("Room Occupancy Report for " + selected));
        document.add(new Paragraph("Generated: " + LocalDateTime.now()));
        document.add(new Paragraph("\n"));

        Table table = new Table(6);
        table.addCell("ID");
        table.addCell("Room Number");
        table.addCell("Room Type");
        table.addCell("Beds");
        table.addCell("Base Price");
        table.addCell("Occupancy Status");

        // some annoying conversion to get this working
        for (Room room : rooms) {
            int roomNum = room.getRoomNumber();
            table.addCell(room.getId().toString());
            table.addCell(Integer.toString(roomNum));
            table.addCell(room.getRoomType().toString());
            table.addCell(Integer.toString(room.getBeds()));
            table.addCell(Double.toString(room.getBasePrice()));
            table.addCell(room.getRoomStatus().toString());
        }

        document.add(table);
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("Occupancy Rate: " + percentage));

        document.close();
    }


}
