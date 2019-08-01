package com.how2java.ws;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServeTest {
    public static ExecutorService threadPool = Executors.newCachedThreadPool();
    public void startTest(){
        threadPool.submit(() ->{
           while (true){
               Scanner sc = new Scanner(System.in);
               String recieve  = sc.next();

           }
        });
    }
}
