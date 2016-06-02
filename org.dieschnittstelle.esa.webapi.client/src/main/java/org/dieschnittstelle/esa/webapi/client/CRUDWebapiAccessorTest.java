package org.dieschnittstelle.esa.webapi.client;

import org.apache.log4j.Logger;
import org.dieschnittstelle.jee.esa.entities.crm.Address;
import org.dieschnittstelle.jee.esa.entities.crm.StationaryTouchpoint;

import java.util.function.Function;

/**
 * Created by master on 01.06.16.
 */
public class CRUDWebapiAccessorTest {

    protected static Logger logger = Logger.getLogger(CRUDWebapiAccessorTest.class);

    private static int runnableId;
    private static int totalCount;
    private static int countdown;

    private static long startTime;

    public static void main(String[] args) {

        // parameters and default values
        String baseUrl = /*"http://localhost:8080/api";*/"http://localhost:8080/org.dieschnittstelle.jee.esa.skeleton.webapp/api";
        int numofThreads = 20;
        long duration = 5000;
        long pause = 1;
        long startupPause = 1;

        if (args.length > 0) {
            baseUrl = args[0];
        }
        if (args.length > 1) {
            numofThreads = Integer.parseInt(args[1]);
        }
        if (args.length > 2) {
            duration = Integer.parseInt(args[2]);
        }
        if (args.length > 3) {
            pause = Integer.parseInt(args[3]);
        }
        if (args.length > 4) {
            startupPause = Integer.parseInt(args[4]);
        }

        System.out.println("Running on " + baseUrl + " with parameters {numOfThreads: " + numofThreads + ", duration: " + duration + ", pause: " + pause + ", startupPause: " + startupPause + "}");

        countdown = numofThreads;

        // we use the global time, i.e. each thread may run differently long
        startTime = System.currentTimeMillis();

        for (int i=0;i<numofThreads;i++) {
            CRUDClientRunnable runnable = new CRUDClientRunnable(baseUrl,duration,pause,totalNum -> {
                incrementTotalCount(totalNum);
                if (countdown == 0) {
                    System.out.println("DONE after " + totalCount + " runs.");
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

    private static synchronized void incrementTotalCount(int count) {
        totalCount+=count;
        countdown--;
    }

    private static class CRUDClientRunnable implements Runnable {

        private int id = runnableId++;
        private int count;

        private String baseUrl;
        private long duration;
        private long pause;
        private Function<Integer,Void> doneCallback;

        public CRUDClientRunnable(String baseUrl, long duration, long pause, Function<Integer,Void> doneCallback) {
            this.baseUrl = baseUrl;
            this.duration = duration;
            this.pause = pause;
            this.doneCallback = doneCallback;
        }

        public void run() {

            long startTime = System.currentTimeMillis();

            while ((System.currentTimeMillis() - startTime) < duration) {
                StationaryTouchpoint tp = new StationaryTouchpoint(-1, "dorem", new Address("lipsum", "-42", "olor", "adispiscing"));

                CRUDWebapiAccessor<StationaryTouchpoint> accessor = new CRUDWebapiAccessor<StationaryTouchpoint>("touchpoints", StationaryTouchpoint.class, baseUrl);

//                logger.info("create tp: " + tp);

                tp = accessor.create(tp);


//                logger.info("created: " + tp);
//
//                logger.info("got id: " + tp.getId());

                tp = accessor.read(tp.getId());

//                logger.info("read tp: " + tp);

                count++;

                System.out.println("Runnable " + id + ": finished " + count + "th run.");

                try {
                    Thread.sleep(pause);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            doneCallback.apply(count);
        }


    }

}
