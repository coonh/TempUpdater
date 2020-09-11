package sensors;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
