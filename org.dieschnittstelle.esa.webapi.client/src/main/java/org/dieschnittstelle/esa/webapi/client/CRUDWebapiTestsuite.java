package org.dieschnittstelle.esa.webapi.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.io.File;

/**
 * Created by master on 17.06.16.
 */
public class CRUDWebapiTestsuite {

    /*
     * TODO: a wrapper object as the mapper does not work on generic collection types - quite sure that we are missing something here...
     */
    public static class TestSuite {
        public List<CRUDWebapiTestRunner.Test> getTests() {
            return tests;
        }

        public void setTests(List<CRUDWebapiTestRunner.Test> tests) {
            this.tests = tests;
        }

        private List<CRUDWebapiTestRunner.Test> tests;
    }

    protected static Logger logger = Logger.getLogger(CRUDWebapiTestsuite.class);

    private static int countup;

    private static long commonduration;

    private static String localhostreplace;

    private static String portreplace;

    public static void main(String[] args) {

        System.out.println("args are: " + Arrays.asList(args));

        File file = new File(args.length > 0 ? args[0] : "./testsuite.json");
        if (args.length > 1) {
            commonduration = Long.parseLong(args[1]);
        }
        if (args.length > 2) {
            localhostreplace = args[2];
        }
        if (args.length > 3) {
            portreplace = args[3];
        }

        logger.info("running testsuite from file: " + file);

        ObjectMapper mapper = new ObjectMapper();
        List<CRUDWebapiTestRunner.Test> testsuite = new ArrayList<CRUDWebapiTestRunner.Test>();
        try {
            testsuite = mapper.readValue(file, TestSuite.class).getTests();
            StringBuffer resultsbuf = new StringBuffer();
            countup = 0;
            syncRunTest(testsuite,resultsbuf,file);
        }
        catch (Exception e) {
            logger.error("got exception running testsuite: " + e,e);
        }

    }

    // we need to run the tests synchronized one after the other
    private static void syncRunTest(List<CRUDWebapiTestRunner.Test> tests,StringBuffer resultsbuf,File testsuitefile) {
        // create a new runner
        CRUDWebapiTestRunner.Test currenttest = tests.get(countup);
        if (localhostreplace != null && !"".equals(localhostreplace)) {
            System.out.println("replacing localhost by: " + localhostreplace);
            currenttest.replaceLocalhost(localhostreplace);
        }
        if (portreplace != null && !"".equals(portreplace)) {
            System.out.println("replacing port by: " + portreplace);
            currenttest.replace8080Port(portreplace);
        }
        if (commonduration > 0) {
            if (!currenttest.isTraining()) {
                System.out.println("overriding duration of test, using value: " + commonduration);
                currenttest.setDuration(commonduration);
            }
            else {
                System.out.println("will not override duration of training test.");
            }
        }
        CRUDWebapiTestRunner runner = new CRUDWebapiTestRunner(currenttest);
        runner.run(tst -> {
            logger.info("one test has been run");
            if (!tst.isTraining()) {
                resultsbuf.append(tst.getStatisticsRow());
            }
            else {
                logger.info("we have a training test, will not write results to buffer.");
            }
            if (countup == tests.size()-1) {
                System.out.println("all tests have been run!");
                // we write the result to a file
                try {
                    PrintWriter out = new PrintWriter(new File(testsuitefile.getParent(), testsuitefile.getName() + ".results." + System.currentTimeMillis() + ".txt"));
                    out.print(resultsbuf.toString());
                    out.flush();
                    out.close();
                }
                catch (Exception e) {
                    logger.error("got exception trying to write results to file: " + e,e);
                }
            }
            else {
                countup++;
                syncRunTest(tests,resultsbuf,testsuitefile);
            }
            return null;
        });
    }

}



