package org.dieschnittstelle.esa.webapi.client;

import java.util.*;

import org.apache.log4j.Logger;
import org.dieschnittstelle.esa.vertx.crud.testentities.StationaryTouchpointDoc;
import org.dieschnittstelle.jee.esa.entities.crm.Address;
import org.dieschnittstelle.jee.esa.entities.crm.StationaryTouchpoint;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by master on 01.06.16.
 */
public class CRUDWebapiAccessorTest {

    protected static Logger logger = Logger.getLogger(CRUDWebapiAccessorTest.class);

    private static int runnableId;
    private static int totalCount;
    private static int countdown;
    private static List<Long> allResponseTimes = new ArrayList<Long>();

    private static String baseUrl = /*"http://localhost:8080/api";*/"http://localhost:8080/org.dieschnittstelle.jee.esa.skeleton.webapp/api";
    private static int numofThreads = 1;
    private static long duration = 1000;
    private static long pause = 1;
    private static long startupPause = 1;

    private static long startTime;

    private static String crudprovider = "CRUDVerticleHibernate";

    public static void main(String[] args) {

        // parameters and default values
        if (args.length > 0) {
            baseUrl = args[0];
        }
        if (args.length > 1) {
            crudprovider = args[1];
        }
        if (args.length > 2) {
            numofThreads = Integer.parseInt(args[2]);
        }
        if (args.length > 3) {
            duration = Integer.parseInt(args[3]);
        }
        if (args.length > 4) {
            pause = Integer.parseInt(args[4]);
        }
        if (args.length > 5) {
            startupPause = Integer.parseInt(args[5]);
        }

        System.out.println("Running on " + baseUrl + " with parameters {numOfThreads: " + numofThreads + ", duration: " + duration + ", pause: " + pause + ", startupPause: " + startupPause + "}");

        countdown = numofThreads;

        // we use the global time, i.e. each thread may run differently long
        startTime = System.currentTimeMillis();

        for (int i=0;i<numofThreads;i++) {
            CRUDClientRunnable runnable = new CRUDClientRunnable(baseUrl,duration,pause,(totalNum, responseTimes) -> {
                incrementTotalCountAndResponseTimes(totalNum,responseTimes);
                if (countdown == 0) {
                    System.out.println("DONE after " + totalCount + " runs.");
                    calculateStatistics(allResponseTimes);
                }
                return null;
            });
            new Thread(runnable).start();
            try {
                Thread.sleep(startupPause);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

    public static void calculateStatistics (Collection<Long> collection) {

        DoubleSummaryStatistics stats = collection
                .stream()
                .mapToDouble(a -> a)
                .summaryStatistics();

        System.out.println("\n\nSTATISTICS (count/average/max/min):\n" + stats.getCount() + "\t" + stats.getAverage() + "\t" + stats.getMax() + "\t" + stats.getMin()+ "\n\n"
                /* second line for detail information, pastable to in excel */
                + crudprovider + "\t" + numofThreads + "\t" + duration + "\t" + pause + "\t" + startupPause + "\t" + stats.getCount() + "\t" + ((int)stats.getAverage()) + "\t" + ((int)stats.getMax()) + "\t" + ((int)stats.getMin()) + "\t" + baseUrl);
    }

    private static synchronized void incrementTotalCountAndResponseTimes(int count,Collection<Long> responseTimes) {
        totalCount+=count;
        countdown--;
        allResponseTimes.addAll(responseTimes);
    }

    private static class CRUDClientRunnable implements Runnable {

        private int id = runnableId++;
        private int count;

        private String baseUrl;
        private long duration;
        private long pause;
        private BiFunction<Integer,List<Long>,Void> doneCallback;
        List<Long> allResponseTimes = new ArrayList<Long>();

        public CRUDClientRunnable(String baseUrl, long duration, long pause, BiFunction<Integer,List<Long>,Void> doneCallback) {
            this.baseUrl = baseUrl;
            this.duration = duration;
            this.pause = pause;
            this.doneCallback = doneCallback;
        }

        public void run() {

            long startTime = System.currentTimeMillis();

            while ((System.currentTimeMillis() - startTime) < duration) {
                StationaryTouchpointDoc tp = new StationaryTouchpointDoc(-1, "dorem", new Address("lipsum", "-42", "olor", "adispiscing"));

                CRUDWebapiAccessor<StationaryTouchpointDoc> accessor = new CRUDWebapiAccessor<StationaryTouchpointDoc>("touchpoints", StationaryTouchpointDoc.class, baseUrl);

                logger.info("create tp: " + tp);

                long currentCallTime = System.currentTimeMillis();
                tp = accessor.create(tp,crudprovider);
                allResponseTimes.add((System.currentTimeMillis()-currentCallTime));


                logger.info("created: " + tp);
//
//                logger.info("got id: " + tp.getId());

//                tp = accessor.read(tp.getId());

//                logger.info("read tp: " + tp);

                count++;

                System.out.println("Runnable " + id + ": finished " + count + "th run.");

                try {
                    Thread.sleep(pause);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            doneCallback.apply(count,allResponseTimes);
        }


    }

}
