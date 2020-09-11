package sensors;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;

public class SensorManager {
    private ArrayList<Sensor> sensors;

    private static SensorManager instance;

    private SensorManager () {
        loadSensors();
    }

    public static SensorManager getInstance () {
        if (SensorManager.instance == null) {
            SensorManager.instance = new SensorManager ();
        }
        return SensorManager.instance;
    }

    private void loadSensors() {
        checkDir();;
        try {
           BufferedReader r = new BufferedReader(new FileReader(
                    new File("data"+File.separator+"sensors"+File.separator+"sensors.json")));
            String content = "";
            String line;
            while((line = r.readLine())!=null){
                content += line;
            }
            JSONObject obj = new JSONObject(content);



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkDir() {
        try {
            File dataDir = new File("data"+File.separator+"sensors");
            boolean dirB = dataDir.mkdirs();
            File sensorFile = new File("data"+File.separator+"sensors"+File.separator+"sensors.json");
            boolean sFboolean = sensorFile.createNewFile();
            System.out.println("===========================");
            System.out.println("/data/sensors created: "+dirB);
            System.out.println("sensors.json created: "+sFboolean);
            System.out.println("===========================");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveSensors(){
        //TODO Save all Sensors into a JSONFile
    }

    public ArrayList<Sensor> getSensors() {
        return sensors;
    }
}
