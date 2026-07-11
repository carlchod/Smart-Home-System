package server;

import com.google.gson.*;
import shared.*;

import java.lang.reflect.Type;

public class SmartDeviceDeserializer implements JsonDeserializer<SmartDevice> {
    
    @Override
    public SmartDevice deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        // Wir prüfen, welche Felder im JSON existieren, um den Typ zu erraten
        if (jsonObject.has("zielTemperatur")) {
            return context.deserialize(jsonObject, HeizungsThermostat.class);
        } else if (jsonObject.has("status")) {
            return context.deserialize(jsonObject, Lichtschalter.class);
        } else if (jsonObject.has("oeffnungsGrad")) {
            return context.deserialize(jsonObject, Jalousie.class);
        } else if (jsonObject.has("basisTemperatur")) {
            return context.deserialize(jsonObject, Thermometer.class);
        } else if (jsonObject.has("alarmAktiv")) {
            return context.deserialize(jsonObject, Rauchmelder.class);
        }

        throw new JsonParseException("Unbekannter Gerätetyp im JSON gefunden!");
    }
}