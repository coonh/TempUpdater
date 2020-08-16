package main;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.*;

public class Updater extends Thread{

    private String tempFileLocation;
    private String JSONLocation = "data/data.json";
    private String path = "data/ips.txt";
    private ArrayList<String> ips;
    private ArrayList<String> paths;
    public Boolean running = false;
    private ScheduledExecutorService scheduler;

    public Updater(){
        File l = new File(path);
        ips = new ArrayList<>();
        paths = new ArrayList<>();
        String content="";
        try {
           BufferedReader r = new BufferedReader(new FileReader(l));
           String st;
           while ((st =r.readLine())!=null) {
               ips.add(st);
           }
           r = new BufferedReader(new FileReader(new File("data/path.txt")));
           tempFileLocation = r.readLine();
            System.out.println(tempFileLocation);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String st:ips) {
            paths.add(tempFileLocation+"/"+st+"/w1_slave");
        }


    }

    @Override
    public synchronized void start() {
        scheduler = Executors.newScheduledThreadPool(1);
        running = true;
        Runnable task2 = () -> {
            update();
        };
        scheduler.scheduleAtFixedRate(task2,0,2500,TimeUnit.MILLISECONDS);

    }

    private void update() {
            JSONObject obj = new JSONObject();
            for (int i = 0; i < ips.size(); i++) {
                try {
                    BufferedReader r = new BufferedReader(new FileReader(new File(paths.get(i))));
                    r.readLine();
                    String line = r.readLine();
                    int t = line.indexOf('t');
                    float number = Integer.parseInt(line.substring(t + 2, line.length()));
                    number = Math.round(number/1000);
                    r.close();

                    if (i < 11) {
                        obj.put("w1_" + (i+1), number);
                    } else {
                        obj.put("w2_"+(i+1-ips.size()/2), number);
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(obj.toString());
            writeJSONFile(obj);

    }

    public void exit(){
        running=false;
        scheduler.shutdown();
        System.out.println("Scheduled task has shut down.");
    }

    private void writeJSONFile(JSONObject obj) {
        try {
            FileWriter fw = new FileWriter(JSONLocation);
            fw.write(obj.toString());
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
