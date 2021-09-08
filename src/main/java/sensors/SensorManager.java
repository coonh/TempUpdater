package sensors;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public class SensorManager {
    private ArrayList<Sensor> sensors;

    private static SensorManager instance;

    private SensorManager () {
        sensors = new ArrayList<>();
        loadSensors();
        printSensors();
    }

    private void printSensors() {
        for (Sensor s: sensors) {
            System.out.println("Sensor: "+s.getName()+" with Ip: "+s.getIp());
        }
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
            Iterator<String> keys = obj.keys();
            while(keys.hasNext()){
                String key = keys.next();
                Sensor sensor = new Sensor(key,obj.getString(key));
                sensors.add(sensor);
            }


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
            File sensorsDir = new File("data"+File.separator+"sensors");
            boolean dirB = sensorsDir.mkdirs();
            File dataDir = new File("data"+File.separator+"sensors"+File.separator+"data");
            boolean dirB2 = dataDir.mkdirs();
            File sensorFile = new File("data"+File.separator+"sensors"+File.separator+"sensors.json");
            boolean sFboolean = sensorFile.createNewFile();
            System.out.println("===========================");
            System.out.println("/data/sensors created: "+dirB);
            System.out.println("/data/sensors/data created: "+dirB2);
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
