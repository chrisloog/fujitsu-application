package main.java;
import java.sql.*;
import java.util.HashMap;

public class WeatherDataRetriever {

    private static final String DATABASE_URL = "jdbc:h2:~/fujitsuDB";
    private static final String DATABASE_USER = "sa";
    private static final String DATABASE_PASSWORD = "";

    /**
     * Retrieves the latest weather data for a given city from the database.
     * @param city The name of the city for which to retrieve weather data.
     * @return A HashMap containing the latest air temperature, wind speed, and weather phenomenon for the given city.
     * @throws SQLException if there's an error executing the SQL query.
     */
    public HashMap<String, Object> getLatestWeatherDataFromDatabase(String city) throws SQLException {
        Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);

        String query = "SELECT airTemp, windSpeed, weatherPhenomenon FROM WEATHER_AT WHERE cityName = ? ORDER BY DATE DESC LIMIT 1";
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
