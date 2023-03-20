package main.java;

import javax.ws.rs.*;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/delivery-fee")
public class RestInterface {

    /**
     Calculates the delivery fee based on the city and the delivery vehicle type.
     @param city the city to calculate the delivery fee for
     @param deliveryVehicle the type of delivery vehicle to calculate the fee for
     @return a JSON response with the calculated delivery fee for the given city and delivery vehicle type
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response calculateDeliveryFee(@QueryParam("city") String city, @QueryParam("vehicle") String deliveryVehicle) {
        DeliveryFeeCalculator calculator = new DeliveryFeeCalculator();
        double fee = calculator.calculateDeliveryFee(city, deliveryVehicle);
        return Response.ok().entity("The delivery fee for " + deliveryVehicle + " in " + city + " is " + fee).build();
    }
}
