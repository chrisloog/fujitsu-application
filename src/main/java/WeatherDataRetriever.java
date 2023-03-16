package main.java;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;

public class WeatherDataRetriever {

    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5080/fujitsuDB";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "44UwqmttPC97";

    public HashMap<String, Object> getWeatherDataByCityAndDate(String city, LocalDate date) throws SQLException {
        Connection connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);

        String query = "SELECT airTemp, windSpeed, weatherPhenomenon FROM WEATHER_AT WHERE cityName = ? AND timeStamp::date = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, city);
        statement.setObject(2, date != null ? date : LocalDate.now());

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

    public HashMap<String, Object> getWeatherDataByCity(String city) throws SQLException {
        return getWeatherDataByCityAndDate(city, null);
    }
}
