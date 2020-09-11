package main;

import sensors.SensorUpdater;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Updater u1 = new Updater();
        u1.start();
        SensorUpdater u = new SensorUpdater();
        u.start();
        Thread t1 = new Thread(){
            @Override
            public synchronized void run() {
                while (u1.running){
                    try{
                        String input = scanner.nextLine();
                        if(!input.isEmpty()){
                            System.out.println("Got input: "+input);
                            if(input.equals("stop")||input.equals("quit")||input.equals("q")){
                                System.out.println("Shutting down...");
                                u1.exit();
                                u.exit();
                                System.out.println("System shutdown with no Errors!");
                            }
                        }

                    }catch (NoSuchElementException e){

                    }
                }
            }
        };
        t1.start();
    }

    private static void checkFolder() {

    }
}
