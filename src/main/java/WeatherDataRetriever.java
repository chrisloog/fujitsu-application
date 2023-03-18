package main.java;
import java.sql.*;
import java.util.HashMap;

public class WeatherDataRetriever {

    private static final String DATABASE_URL = "jdbc:h2:~/fujitsuDB";
    private static final String DATABASE_USER = "sa";
    private static final String DATABASE_PASSWORD = "";

    public HashMap<String, Object> getWeatherDataFromDatabase(String city) throws SQLException {
        Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);

        String query = "SELECT airTemp, windSpeed, weatherPhenomenon FROM WEATHER_AT WHERE cityName = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, city);

        ResultSet resultSet = statement.executeQuery();

        HashMap<String, Object> weatherData = new HashMap<>();
        if (resultSet.next()) {
            weatherData.put("airTemp", resultSet.getDouble("airTemp"));
            weatherData.put("windSpeed", resultSet.getDouble("windSpeed"));
            weatherData.put("weatherPhenomenon", resultSet.getString("weatherPhenomenon"));
        }

        connection.close();

        return weatherData;
    }
}
