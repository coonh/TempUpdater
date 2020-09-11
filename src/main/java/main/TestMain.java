package main;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class TestMain {
    public static void main(String[] args) {
        try {
            URL oracle = new URL("http://192.168.178.138/");
            BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
            String input="";
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                input += inputLine;
            in.close();
            JSONObject object = new JSONObject(input);
            System.out.println("Temperatur: "+ object.get("temperature")+"°C");
            System.out.println("Feuchtigkeit: "+ object.get("humidity")+"%");
            System.out.println("Druck: "+ object.get("pressure")+"Pa");
            System.out.println("Höhe: "+ object.get("altitude"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
