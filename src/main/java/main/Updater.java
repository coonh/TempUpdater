package main;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.*;

public class Updater extends Thread{

    private String tempFileLocation;
    private String JSONLocation = "data"+File.separator+"data.json";
    private String path = "data"+File.separator+"ips.txt";
    private ArrayList<String> ips;
    private ArrayList<String> paths;
    public Boolean running = false;
    private ScheduledExecutorService scheduler;
    private final int delay = 3000;

    public Updater(){
        checkForDir();
        File l = new File(path);
        ips = new ArrayList<>();
        paths = new ArrayList<>();
        String content="";
        try {
           BufferedReader r = new BufferedReader(new FileReader(l));
           String st;
            System.out.println("===========================");
            System.out.println("Searching for the following Sensors: ");
            System.out.println("===========================");
           while ((st =r.readLine())!=null) {
               ips.add(st);
               System.out.println(st);
               System.out.println("---------------------------");
           }
           r = new BufferedReader(new FileReader(new File("data"+File.separator+"path.txt")));
           tempFileLocation = r.readLine();
            System.out.println("Location of sensors: "+tempFileLocation);
            System.out.println("===========================");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String st:ips) {
            paths.add(tempFileLocation+File.separator+st+File.separator+"w1_slave");
        }


    }

    private void checkForDir() {
        try {
            File dataDir = new File("data");
            boolean dirB = dataDir.mkdirs();
            File ipsTXT = new File("data"+File.separator+"ips.txt");
            File pathTXT = new File("data"+File.separator+"path.txt");
            File dataJSON = new File("data"+File.separator+"data.json");
            boolean ipB = ipsTXT.createNewFile();
            boolean pathB = pathTXT.createNewFile();
            boolean dataB = dataJSON.createNewFile();
            System.out.println("===========================");
            System.out.println("ip.txt created: "+ipB);
            System.out.println("path.txt created: "+pathB);
            System.out.println("data.json created: "+dataB);
            System.out.println("===========================");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void start() {
        scheduler = Executors.newScheduledThreadPool(1);
        running = true;
        Runnable task2 = () -> {
            update();
        };
        scheduler.scheduleAtFixedRate(task2,delay,delay,TimeUnit.MILLISECONDS);

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
                    try {
                        if (i < 11) {
                            System.err.println("Sensor w1_"+ (i+1)+" was not found!");
                            obj.put("w1_" + (i+1), "ERR");
                        } else {
                            System.err.println("Sensor w2_"+(i+1-ips.size()/2)+" was not found!");
                            obj.put("w2_"+(i+1-ips.size()/2), "ERR");
                        }
                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Update successful "+ new Date());
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
