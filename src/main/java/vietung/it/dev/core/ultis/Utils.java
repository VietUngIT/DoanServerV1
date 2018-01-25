package vietung.it.dev.core.ultis;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by ThinhNK on 1/25/2018.
 */
public class Utils {
    private static Gson gson = new Gson();
    public static JsonObject toJsonObject(String json){
        try {
            return gson.fromJson(json, JsonObject.class);
        }catch (Exception e){
            return null;
        }
    }
}
