package main.java;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

public class WeatherDataCronJob {
    // Set default frequency to once every hour, 15 minutes after a full hour (HH:15:00)
    private static final long DEFAULT_FREQUENCY_MS = 3600000L;
    private static final long DEFAULT_DELAY_MS = 900000L;

    private static final String DATABASE_URL = "jdbc:h2:~/fujitsuDB";
    private static final String DATABASE_USER = "sa";
    private static final String DATABASE_PASSWORD = "";

    private static final String WEATHER_DATA_URL = "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php";
    private static final String WEATHER_STATIONS_QUERY = "SELECT * FROM WEATHER_STATION WHERE NAME IN ('Tallinn-Harku', 'Tartu-Tõravere', 'Pärnu')";
    private static final String INSERT_WEATHER_AT_QUERY = "INSERT INTO WEATHER_AT (WEATHER_STATION_ID, AIRTEMPERATURE, WINDSPEED, WEATHERPHENOMENON) VALUES (?, ?, ?, ?)";

    /**
     * This method is the entry point of the WeatherDataCronJob application.
     * It sets the frequency and delay based on command line arguments, or uses default values.
     * It then creates a timer to run the cron job on schedule.
     *
     * @param args Command line arguments that specify the frequency and delay of the cron job.
     *             If not provided, default values will be used.
     */
    public static void main(String[] args) {
        // Set the frequency and delay based on the command line arguments, or use default values
        long frequency = args.length >= 1 ? Long.parseLong(args[0]) : DEFAULT_FREQUENCY_MS;
        long delay = args.length >= 2 ? Long.parseLong(args[1]) : DEFAULT_DELAY_MS;

        // Create a timer to run the cron job on schedule
        Timer timer = new Timer();
        timer.schedule(new WeatherDataTask(), delay, frequency);
    }

    private static class WeatherDataTask extends TimerTask {
        @Override
        public void run() {
            try {
                Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);

                // Query the database for the weather stations of interest
                PreparedStatement stationQuery = connection.prepareStatement(WEATHER_STATIONS_QUERY);
                stationQuery.execute();
                var stationsResult = stationQuery.getResultSet();

                while (stationsResult.next()) {
                    String stationName = stationsResult.getString("NAME");
                    String stationWmoCode = stationsResult.getString("WMOCODE");

                    URL url = new URL(WEATHER_DATA_URL);
                    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openStream());
                    doc.getDocumentElement().normalize();
                    NodeList stationList = doc.getElementsByTagName("station");

                    for (int i = 0; i < stationList.getLength(); i++) {
                        var station = stationList.item(i);
                        var nameNode = station.getChildNodes().item(0);
                        var wmocodeNode = station.getChildNodes().item(1);

                        int stationId = 0;
                        switch (wmocodeNode.toString()) {
                            case "26038" -> stationId = 1;
                            case "26242" -> stationId = 2;
                            case "41803" -> stationId = 3;
                        }

                        if (nameNode != null && nameNode.getTextContent().equals(stationName) && wmocodeNode.getTextContent().equals(stationWmoCode)) {
                            var airTempNode = station.getChildNodes().item(9);
                            var windSpeedNode = station.getChildNodes().item(11);
                            var phenomenonNode = station.getChildNodes().item(7);
                            if (airTempNode != null && windSpeedNode != null && phenomenonNode != null) {

                                var airTemperature = Double.parseDouble(airTempNode.getTextContent());
                                var windSpeed = Double.parseDouble(windSpeedNode.getTextContent());
                                var weatherPhenomenon = phenomenonNode.getTextContent();

                                insertWeatherData(stationId, airTemperature, windSpeed, weatherPhenomenon);
                                System.out.println("Data inserted successfully!");
                            } else {
                                System.out.println("Missing required data for station: " + stationName);
                            }
                        }
                    }
                }
            } catch (IOException | ParserConfigurationException | SAXException | SQLException e) {
                e.printStackTrace();
            }
        }

        private static void insertWeatherData(int stationId, double airTemperature, double windSpeed, String weatherPhenomenon) {
            try (var connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
                 var statement = connection.prepareStatement(INSERT_WEATHER_AT_QUERY)) {
                statement.setInt(1, stationId);
                statement.setDouble(2, airTemperature);
                statement.setDouble(3, windSpeed);
                statement.setString(4, weatherPhenomenon);
                statement.executeUpdate();
                System.out.println("Weather data inserted for station with ID " + stationId);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
