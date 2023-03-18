package main.java;

import javax.ws.rs.*;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/delivery-fee")
public class RestInterface {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response calculateDeliveryFee(@QueryParam("city") String city, @QueryParam("vehicle") String deliveryVehicle) {
        DeliveryFeeCalculator calculator = new DeliveryFeeCalculator();
        double fee = calculator.calculateDeliveryFee(city, deliveryVehicle);
        return Response.ok().entity("The delivery fee for " + deliveryVehicle + " in " + city + " is " + fee).build();
    }
}
