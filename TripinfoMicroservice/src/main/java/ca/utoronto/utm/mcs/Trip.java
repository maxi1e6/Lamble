package ca.utoronto.utm.mcs;

import com.mongodb.util.JSON;
import com.sun.net.httpserver.HttpExchange;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import javax.print.Doc;
import java.io.IOException;

public class Trip extends Endpoint {

    /**
     * PATCH /trip/:_id
     * @param _id
     * @body distance, endTime, timeElapsed, totalCost
     * @return 200, 400, 404
     * Adds extra information to the trip with the given id when the 
     * trip is done. 
     */

    @Override
    public void handlePatch(HttpExchange r) throws IOException, JSONException {
        String[] splitUrl = r.getRequestURI().getPath().split("/");
        if (splitUrl.length != 3) {
            System.out.println("not 3");
            this.sendStatus(r, 400);
            return;
        }
        String _id = splitUrl[2];
        try{
        if(!ObjectId.isValid(_id))
        {
            this.sendStatus(r, 400);
            System.out.println("wrong format of _id");
            return;
        }
        } catch (IllegalArgumentException e){
            this.sendStatus(r, 400);
        }

        Document trip = this.dao.getTrip(_id);

        if(trip.isEmpty())
        {
            this.sendStatus(r, 404);
        }
        // check if uid given is integer, return 400 if not
        String body = Utils.convert(r.getRequestBody());
        JSONObject deserialized = new JSONObject(body);
        Integer distance = null;
        String totalCost = null;
        Integer endTime = null;
        Integer timeElapsed = null;
        if (deserialized.has("distance")) {
            if (deserialized.get("distance").getClass() != Integer.class) {
                System.out.println("not d");
                this.sendStatus(r, 400);
                return;
            }
            distance = deserialized.getInt("distance");
        }
        if (deserialized.has("endTime")) {
            if (deserialized.get("endTime").getClass() != Integer.class) {
                System.out.println("not e");
                this.sendStatus(r, 400);
                return;
            }
            endTime = deserialized.getInt("endTime");
        }
        if (deserialized.has("timeElapsed")) {
            if (deserialized.get("timeElapsed").getClass() != Integer.class) {
                System.out.println("not t");
                this.sendStatus(r, 400);
                return;
            }
            timeElapsed = deserialized.getInt("timeElapsed");
        }
        if (deserialized.has("totalCost")) {
            if (deserialized.get("totalCost").getClass() != String.class) {
                System.out.println("not toc");
                this.sendStatus(r, 400);
                return;
            }
            totalCost = deserialized.getString("totalCost");
        }
        if (distance == null && endTime == null && timeElapsed == null && totalCost == null) {
            System.out.println("null");
            this.sendStatus(r, 400);
            return;
        }
        this.dao.updateAttributes(_id, distance, totalCost, endTime, timeElapsed);
        this.sendStatus(r, 200);

    }
}
