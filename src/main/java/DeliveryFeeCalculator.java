package main.java;
import java.sql.SQLException;
import java.util.HashMap;

public class DeliveryFeeCalculator {
    
    private static final double CAR_TALLINN_BASE_FEE = 4.0;
    private static final double SCOOTER_TALLINN_BASE_FEE = 3.5;
    private static final double BIKE_TALLINN_BASE_FEE = 3.0;

    private static final double CAR_TARTU_BASE_FEE = 3.5;
    private static final double SCOOTER_TARTU_BASE_FEE = 3.0;
    private static final double BIKE_TARTU_BASE_FEE = 2.5;

    private static final double CAR_PARNU_BASE_FEE = 3.0;
    private static final double SCOOTER_PARNU_BASE_FEE = 2.5;
    private static final double BIKE_PARNU_BASE_FEE = 2.0;

    private static final double LESS_THAN_NEGATIVE_10C_TEMP_FEE = 1.0;
    private static final double BETWEEN_NEGATIVE_10C_AND_0C_TEMP_FEE = 0.5;

    private static final double BETWEEN_10MPS_AND_20MPS_WIND_FEE = 0.5;

    private static final double SNOW_OR_SLEET_WEATHER_FEE = 1.0;
    private static final double RAIN_WEATHER_FEE = 0.5;

    /**
     * Calculates the delivery fee for a delivery based on the city and delivery vehicle type.
     * The fee consists of a regional base fee and extra fees based on the weather conditions and delivery vehicle type.
     *
     * @param city              the city where the delivery is being made (Tallinn, Tartu or Pärnu).
     * @param deliveryVehicle   the type of delivery vehicle (Car, Scooter or Bike).
     * @return                  the total delivery fee, which is the sum of the regional base fee and extra fees.
     * @throws IllegalArgumentException if an invalid city is entered or if the selected delivery vehicle is not allowed
     * in the given weather conditions (for example, if the wind speed is too high or if there is hail or thunder).
     */
    public double calculateDeliveryFee(String city, String deliveryVehicle) {
        double regionalBaseFee = calculateRegionalBaseFee(city, deliveryVehicle);

        HashMap<String, Object> weatherData = getDataFromDatabase(city);

        double airTemperature = (double) weatherData.get("airTemp");
        double windSpeed = (double) weatherData.get("windSpeed");
        String weatherPhenomenon = (String) weatherData.get("weatherPhenomenon");

        double extraFees = calculateExtraFees(deliveryVehicle, airTemperature, windSpeed, weatherPhenomenon);
        return regionalBaseFee + extraFees;
    }

    private double calculateRegionalBaseFee(String city, String deliveryVehicle) {
        double regionalBaseFee = 0.0;
        switch (city) {
            case "Tallinn":
                switch (deliveryVehicle) {
                    case "Car" -> regionalBaseFee = CAR_TALLINN_BASE_FEE;
                    case "Scooter" -> regionalBaseFee = SCOOTER_TALLINN_BASE_FEE;
                    case "Bike" -> regionalBaseFee = BIKE_TALLINN_BASE_FEE;
                }
                break;
            case "Tartu":
                switch (deliveryVehicle) {
                    case "Car" -> regionalBaseFee = CAR_TARTU_BASE_FEE;
                    case "Scooter" -> regionalBaseFee = SCOOTER_TARTU_BASE_FEE;
                    case "Bike" -> regionalBaseFee = BIKE_TARTU_BASE_FEE;
                }
                break;
            case "Pärnu":
                switch (deliveryVehicle) {
                    case "Car" -> regionalBaseFee = CAR_PARNU_BASE_FEE;
                    case "Scooter" -> regionalBaseFee = SCOOTER_PARNU_BASE_FEE;
                    case "Bike" -> regionalBaseFee = BIKE_PARNU_BASE_FEE;
                }
                break;
        }
        return regionalBaseFee;
    }

    private double calculateExtraFees(String deliveryVehicle, double windSpeed, double airTemperature, String weatherPhenomenon) {
        double extraFees = 0.0;
        
        if ((deliveryVehicle.equals("Scooter") || deliveryVehicle.equals("Bike")) && airTemperature < -10) {
            extraFees += LESS_THAN_NEGATIVE_10C_TEMP_FEE;
        } else if ((deliveryVehicle.equals("Scooter") || deliveryVehicle.equals("Bike")) && airTemperature >= -10 && airTemperature < 0) {
            extraFees += BETWEEN_NEGATIVE_10C_AND_0C_TEMP_FEE;
        }
        
        if (deliveryVehicle.equals("Bike") && windSpeed >= 10 && windSpeed < 20) {
            extraFees += BETWEEN_10MPS_AND_20MPS_WIND_FEE;
        } else if (deliveryVehicle.equals("Bike") && windSpeed >= 20) {
            throw new IllegalArgumentException("Usage of selected vehicle type is forbidden");
        }
        
        if ((deliveryVehicle.equals("Scooter") || deliveryVehicle.equals("Bike")) && (weatherPhenomenon.equals("Snow") || weatherPhenomenon.equals("Sleet"))) {
            extraFees += SNOW_OR_SLEET_WEATHER_FEE;
        } else if ((deliveryVehicle.equals("Scooter") || deliveryVehicle.equals("Bike")) && weatherPhenomenon.equals("Rain")) {
            extraFees += RAIN_WEATHER_FEE;
        } else if ((deliveryVehicle.equals("Scooter") || deliveryVehicle.equals("Bike")) && (weatherPhenomenon.equals("Glaze") || weatherPhenomenon.equals("Hail") || weatherPhenomenon.equals("Thunder"))) {
            throw new IllegalArgumentException("Usage of selected vehicle type is forbidden");
        }
        
        return extraFees;
    }

    private HashMap<String, Object> getDataFromDatabase(String city) {
        HashMap<String, Object> weatherData;

        WeatherDataRetriever wRetriever = new WeatherDataRetriever();

        try {
            weatherData = wRetriever.getLatestWeatherDataFromDatabase(city);
        } catch (SQLException e) {
            throw new IllegalArgumentException("Invalid city entered.");
        }

        return weatherData;
    }
}
