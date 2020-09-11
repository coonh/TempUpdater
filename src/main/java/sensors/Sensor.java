package sensors;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class Sensor {
    private String name;
    private URL ip;
    private JSONObject data;

    public Sensor(String name, String ip, JSONObject data){
        this.name = name;
        try {
            this.ip = new URL(ip);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        this.data = data;
    }

    public void updateData(String ptr){
        try {
            data = new JSONObject(ptr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject getData(){
        return data;
    }

    public URL getIp() {
        return ip;
    }
}
