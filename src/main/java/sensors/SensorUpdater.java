package sensors;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SensorUpdater {

    private ScheduledExecutorService scheduler;
    private boolean running=false;
    private int delay = 2000;


    public void start() {
        scheduler = Executors.newScheduledThreadPool(1);
        running = true;
        Runnable task2 = this::update;
        scheduler.scheduleAtFixedRate(task2,delay,delay, TimeUnit.MILLISECONDS);

    }

    private void update() {
        ArrayList<Sensor> sensors = SensorManager.getInstance().getSensors();
        for (Sensor sensor: sensors) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(sensor.getIp().openStream()));
                String input="";
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    input += inputLine;
                in.close();
                sensor.updateData(input);
                printToFile(sensor);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void printToFile(Sensor sensor) {
        try {
            File sensorFile = new File("data"+File.separator+"sensors"
                    +File.separator+"data"+File.separator+sensor.getName()+".json");
            FileWriter fw = new FileWriter(sensorFile);
            fw.write(sensor.getData().toString());
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exit() {
        running=false;
        scheduler.shutdown();
        System.out.println("Sensor task has shut down.");
    }
}
