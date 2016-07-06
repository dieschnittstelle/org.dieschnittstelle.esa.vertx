package org.dieschnittstelle.esa.webapi.client;

import java.util.*;

import org.apache.log4j.Logger;
import org.dieschnittstelle.esa.vertx.crud.testentities.StationaryTouchpointDoc;
import org.dieschnittstelle.jee.esa.entities.crm.Address;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by master on 01.06.16.
 */
public class CRUDWebapiTestRunner {

    /*
     * describe a test and calculate a test result given a list of response times
     */
    public static class Test {

        /* if a test has training mode its results will not be considered for a testsuite */
        private boolean training = true;

        private String baseUrl = "http://localhost:8080/api";

        private int errorCount;

        private int numofThreads = 1;
        private long duration = 5;
        private long pause = 1;
        private long startupPause = 1;
        private String crudprovider = "CRUDVerticleHibernate";

        private DoubleSummaryStatistics result;

        public Test() {

        }

        public Test(String baseUrl, int numofThreads, long duration, long pause, long startupPause, String crudprovider) {
            this.baseUrl = baseUrl;
            this.numofThreads = numofThreads;
            this.duration = duration;
            this.pause = pause;
            this.startupPause = startupPause;
            this.crudprovider = crudprovider;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public int getNumofThreads() {
            return numofThreads;
        }

        public void setNumofThreads(int numofThreads) {
            this.numofThreads = numofThreads;
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public long getPause() {
            return pause;
        }

        public void setPause(long pause) {
            this.pause = pause;
        }

        public long getStartupPause() {
            return startupPause;
        }

        public void setStartupPause(long startupPause) {
            this.startupPause = startupPause;
        }

        public String getCrudprovider() {
            return crudprovider;
        }

        public void setCrudprovider(String crudprovider) {
            this.crudprovider = crudprovider;
        }

        public DoubleSummaryStatistics getResult() {
            return result;
        }

        public void setResult(DoubleSummaryStatistics result) {
            this.result = result;
        }

        public boolean isTraining() {
            return training;
        }

        public void setTraining(boolean training) {
            this.training = training;
        }

        public void replaceLocalhost(String replace) {
            this.baseUrl = this.baseUrl.replace("/localhost",("/" + replace));
        }

        public void replace8080Port(String replace) {
            this.baseUrl =  this.baseUrl.replace(":8080",(":"+replace));
        }

        public int getErrorCount() {
            return errorCount;
        }

        public void setErrorCount(int errorCount) {
            System.out.println("setErrorCount(): " + errorCount);
            this.errorCount = errorCount;
        }

        public void calculateStatistics (Collection<Long> collection) {

            this.result = collection
                    .stream()
                    .mapToDouble(a -> a)
                    .summaryStatistics();

            System.out.println("\n\nSTATISTICS (count/average/max/min/errors):\n" + this.result.getCount() + "\t" + this.result.getAverage() + "\t" + this.result.getMax() + "\t" + this.result.getMin()+ "\t" + this.errorCount + "\n\n"
                /* second line for detail information, pastable to in excel */
                    + getStatisticsRow());
        }

        public String getStatisticsRow(String separator) {
            return crudprovider + separator + numofThreads + separator + duration + separator + pause + separator + startupPause + separator + this.result.getCount() + separator + ((int)this.result.getAverage()) + separator + ((int)this.result.getMax()) + separator + ((int)this.result.getMin()) + separator + this.errorCount + separator + baseUrl + "\n";
        }

        public String getStatisticsRow() {
            return getStatisticsRow("\t");
        }


    }

    protected static Logger logger = Logger.getLogger(CRUDWebapiTestRunner.class);

    /*
     * start a single test
     */
    public static void main(String[] args) {

        String baseUrl = /*"http://localhost:8080/api";*/"http://localhost:8080/org.dieschnittstelle.jee.esa.skeleton.webapp/api";
        int numofThreads = 1;
        long duration = 1000;
        long pause = 1;
        long startupPause = 1;
        long startTime;

        String crudprovider = "CRUDVerticleHibernate";


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

        // create a Test instance
        Test test = new Test(baseUrl, numofThreads, duration, pause, startupPause, crudprovider);

        // create a new Runner
        CRUDWebapiTestRunner runner = new CRUDWebapiTestRunner(test);
        runner.run(tst -> {
            System.out.println("test done.");
            return null;
        });

    }


    private int runnableId;
    private int totalCount;
    private int totalErrorCount;
    private int countdown;
    private List<Long> allResponseTimes = new ArrayList<Long>();

    private long startTime;
    private Test test;
    private Function<Test,Void> ondone;

    public CRUDWebapiTestRunner(Test test) {
        this.test = test;
        countdown = test.getNumofThreads();

        System.out.println("Running on " + test.getBaseUrl() + " with parameters {numOfThreads: " + test.getNumofThreads() + ", duration: " + test.getDuration() + ", pause: " + test.getPause() + ", startupPause: " + test.getStartupPause() + "}");
    }

    public void run() {

        // we use the global time, i.e. each thread may run differently long
        startTime = System.currentTimeMillis();

        for (int i=0;i<test.getNumofThreads();i++) {
            CRUDClientRunnable runnable = new CRUDClientRunnable(test,runnableId++,(errorNum, responseTimes) -> {
                incrementTotalCountAndResponseTimes(errorNum,responseTimes);
                if (countdown == 0) {
                    System.out.println("DONE after " + totalCount + " runs.");
                    test.setErrorCount(totalErrorCount);
                    test.calculateStatistics(allResponseTimes);

                    if (ondone != null) {
                        ondone.apply(test);
                    }
                }
                return null;
            });
            new Thread(runnable).start();
            try {
                Thread.sleep(test.getStartupPause());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void run(Function<Test,Void> ondone) {
        this.ondone = ondone;
        this.run();
    }

    private synchronized void incrementTotalCountAndResponseTimes(int errorCount,Collection<Long> responseTimes) {
        totalErrorCount+=errorCount;
        totalCount+=responseTimes.size();
        countdown--;
        allResponseTimes.addAll(responseTimes);
    }

    private static class CRUDClientRunnable implements Runnable {

        private int id;
        private int count;
        private int errorCount;

        private String baseUrl;
        private long duration;
        private long pause;
        private BiFunction<Integer,List<Long>,Void> doneCallback;
        List<Long> allResponseTimes = new ArrayList<Long>();
        private String crudprovider;
        private Test test;

        public CRUDClientRunnable(Test test, int runnableId, BiFunction<Integer,List<Long>,Void> doneCallback) {
            this.baseUrl = test.getBaseUrl();
            this.duration = test.getDuration();
            this.pause = test.getPause();
            this.doneCallback = doneCallback;
            this.id = runnableId;
            this.crudprovider = test.getCrudprovider();
            this.test = test;
        }

        public void run() {
            CRUDWebapiAccessorJAXRS accessor = new CRUDWebapiAccessorJAXRS("touchpoints", StationaryTouchpointDoc.class, baseUrl);
            System.out.println("using accessor: " + accessor.getClass());

            long startTime = System.currentTimeMillis();

            while ((System.currentTimeMillis() - startTime) < duration) {

                try {
                    StationaryTouchpointDoc tp = new StationaryTouchpointDoc(-1, "dorem", new Address("lipsum", "-42", "olor", "adispiscing"));

                    logger.info("create tp: " + tp);

                    long currentCallTime = System.currentTimeMillis();
                    tp = accessor.create(tp, crudprovider);
                    allResponseTimes.add((System.currentTimeMillis() - currentCallTime));


                    logger.info("created: " + tp);
//
//                logger.info("got id: " + tp.getId());

//                tp = accessor.read(tp.getId());

//                logger.info("read tp: " + tp);
                }
                catch (Throwable e) {
                    logger.error("got exception: " + e,e);
                    errorCount++;
                }

                count++;

                if (test.isTraining()) {
                    System.out.println("Runnable " + id + ": finished " + count + "th run.");
                }

                try {
                    Thread.sleep(pause);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            doneCallback.apply(errorCount,allResponseTimes);
        }


    }

}
