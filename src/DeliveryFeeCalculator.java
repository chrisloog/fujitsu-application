public class DeliveryFeeCalculator {
    
    private static final double CAR_TALLINN_BASE_FEE = 4.0;
    private static final double SCOOTER_TALLINN_BASE_FEE = 3.5;
    private static final double BIKE_TALLINN_BASE_FEE = 3.0;

    private static final double CAR_TARTU_BASE_FEE = 3.5;
    private static final double SCOOTER_TARTU_BASE_FEE = 3.0;
    private static final double BIKE_TARTU_BASE_FEE = 2.5;

    private static final double CAR_PÄRNU_BASE_FEE = 3.0;
    private static final double SCOOTER_PÄRNU_BASE_FEE = 2.5;
    private static final double BIKE_PÄRNU_BASE_FEE = 2.0;

    private static final double LESS_THAN_NEGATIVE_10C_TEMP_FEE = 1.0;
    private static final double BETWEEN_NEGATIVE_10C_AND_0C_TEMP_FEE = 0.5;

    private static final double BETWEEN_10MPS_AND_20MPS_WIND_FEE = 0.5;

    private static final double SNOW_OR_SLEET_WEATHER_FEE = 1.0;
    private static final double RAIN_WEATHER_FEE = 0.5;

    public double calculateDeliveryFee(String city, String deliveryVehicle, double windSpeed, double airTemperature, String weatherPhenomenon) {
        double regionalBaseFee = calculateRegionalBaseFee(city, deliveryVehicle);
        double extraFees = calculateExtraFees(deliveryVehicle, city, airTemperature, windSpeed, weatherPhenomenon);
        return regionalBaseFee + extraFees;
    }

    private double calculateRegionalBaseFee(String city, String deliveryVehicle) {
        double regionalBaseFee = 0.0;
        switch (city) {
            case "Tallinn":
                switch (deliveryVehicle) {
                    case "Car":
                        regionalBaseFee = CAR_TALLINN_BASE_FEE;
                        break;
                    case "Scooter":
                        regionalBaseFee = SCOOTER_TALLINN_BASE_FEE;
                        break;
                    case "Bike":
                        regionalBaseFee = BIKE_TALLINN_BASE_FEE;
                        break;
                }
                break;
            case "Tartu":
                switch (deliveryVehicle) {
                    case "Car":
                        regionalBaseFee = CAR_TARTU_BASE_FEE;
                        break;
                    case "Scooter":
                        regionalBaseFee = SCOOTER_TARTU_BASE_FEE;
                        break;
                    case "Bike":
                        regionalBaseFee = BIKE_TARTU_BASE_FEE;
                        break;
                }
                break;
            case "Pärnu":
                switch (deliveryVehicle) {
                    case "Car":
                        regionalBaseFee = CAR_PÄRNU_BASE_FEE;
                        break;
                    case "Scooter":
                        regionalBaseFee = SCOOTER_PÄRNU_BASE_FEE;
                        break;
                    case "Bike":
                        regionalBaseFee = BIKE_PÄRNU_BASE_FEE;
                        break;
                }
                break;
        }
        return regionalBaseFee;
    }

    private double calculateExtraFees(String city, String deliveryVehicle, double windSpeed, double airTemperature, String weatherPhenomenon) {
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
}
