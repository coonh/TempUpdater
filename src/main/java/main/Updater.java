package main;

import database.DatabaseConnector;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.*;

public class Updater extends Thread{

    private String tempFileLocation;
    private final String JSONLocation = "data"+File.separator+"data.json";
    protected final String path = "data"+File.separator+"ips.txt";
    private final ArrayList<String> ips;
    private final ArrayList<String> paths;
    public Boolean running = false;
    private ScheduledExecutorService scheduler;
    protected final int delay = 3000;
    private int [] values;
    private String columnString;

    public Updater(){
        checkForDir();
        File l = new File(path);
        ips = new ArrayList<>();
        paths = new ArrayList<>();
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
           values = new int[ips.size()];
           buildIpString();
           r = new BufferedReader(new FileReader(new File("data"+File.separator+"path.txt")));
           tempFileLocation = r.readLine();
            System.out.println("Location of sensors: "+tempFileLocation);
            System.out.println("===========================");

        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String st:ips) {
            paths.add(tempFileLocation+File.separator+st+File.separator+"w1_slave");
        }


    }

    private void buildIpString() {
        columnString ="";
        for(int i=1;i<=ips.size();i++){
            if(i<=(ips.size()/2)){
                columnString = columnString + "w1_"+i+", ";
            }else if(i>ips.size()/2){
                columnString = columnString + "w2_"+(i-(ips.size()/2))+", ";
            }
        }
        columnString = columnString.substring(0, columnString.length() - 2);
        System.out.println("Database needs following columns: \n"+ columnString);
        System.out.println("===========================");
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
            System.out.println("/data created: "+dirB);
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
        Runnable task2 = this::update;
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
                    float number = Integer.parseInt(line.substring(t + 2));
                    number = Math.round(number/1000);
                    //the next to lines generate random values
                    //Random rand = new Random();
                    //number = rand.nextInt((70 - 20) + 1) + 20;
                    r.close();
                    values[i]=(int) number;

                    if (i < 11) {
                        obj.put("w1_" + (i+1), number);
                    } else {
                        obj.put("w2_"+(i+1-ips.size()/2), number);
                    }

                } catch (FileNotFoundException | JSONException e) {
                    try {
                        if (i <= ips.size()/2) {
                            System.err.println("Sensor w1_"+ (i+1)+" was not found!");
                            obj.put("w1_" + (i+1), "ERR");
                        } else {
                            System.err.println("Sensor w2_"+(i+1-ips.size()/2)+" was not found!");
                            obj.put("w2_"+(i+1-ips.size()/2), "ERR");
                        }
                        values[i]= -1;
                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Update successful "+ new Date());
        insertIntoDatabase();
            writeJSONFile(obj);
    }

    private void insertIntoDatabase() {
        String output ="";
        for (int i : values){
            output = output + i+", ";
        }
        output = output.substring(0,output.length()-2);
        DatabaseConnector.getInstance().sqlInsertRequest("INSERT INTO temperaturdaten ("+ columnString +") " +
                "VALUES ("+output+");");
        System.out.println("Database Entry: "+output+" : successful");
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
