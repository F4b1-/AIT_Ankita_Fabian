package com.unibz.hikinghelper.util;

import com.unibz.hikinghelper.Constants.Constants;
import com.unibz.hikinghelper.model.Location;
import com.vaadin.server.StreamResource;
import io.jenetics.jpx.GPX;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;

public class DownloadFileCreator {


    public static StreamResource createFileLocation(Location location, String fileType) {
        StreamResource streamResource = null;
        if (fileType.equals(Constants.CSV_FILE)) {
            streamResource = createCSV(location);
        } else if (fileType.equals(Constants.GPX_FILE)) {
            streamResource = createGPX(location);
        }
        return streamResource;
    }


    private static StreamResource createGPX(Location location) {
        return new StreamResource((StreamResource.StreamSource) () -> {

            GPX gpx = GPX.builder()
                    .addTrack(track -> track
                            .addSegment(segment -> location.getRoute().forEach((latLon) -> segment.addPoint(p -> p.lat(latLon.getLat()).lon(latLon.getLon())))))
                    .build();

            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                GPX.write(gpx, bos);
                return new ByteArrayInputStream(bos.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }, location.getName() + Constants.GPX_FILE);
    }


    private static StreamResource createCSV(Location location) {
        return new StreamResource((StreamResource.StreamSource) () -> {

            try (ByteArrayOutputStream stream = new ByteArrayOutputStream();
                 OutputStreamWriter streamWriter = new OutputStreamWriter(stream);
                 BufferedWriter writer = new BufferedWriter(streamWriter);
                 CSVPrinter printer = getDefaultCsvPrinter(writer)) {

                printer.printRecord(location.getName(), location.difficulty, location.duration, location.getLatLon().getLat(), location.getLatLon().getLon());
                //printer.printRecord("2", "Satya Nadella", "CEO", "Microsoft");
                //printer.printRecord("3", "Tim cook", "CEO", "Apple");

                // Should suffice to flush the Writer
                writer.flush();
                return new ByteArrayInputStream(stream.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }, location.getName() + Constants.CSV_FILE) ;
    }

    private static CSVPrinter getDefaultCsvPrinter(BufferedWriter writer) throws IOException {

        return new CSVPrinter(writer, CSVFormat.DEFAULT
                .withHeader("Name", "Difficulty", "Duration", "Latitude", "Longitude"));
    }
}
