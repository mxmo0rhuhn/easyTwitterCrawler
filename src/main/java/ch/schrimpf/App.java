package ch.schrimpf;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App
{
    private static final Logger LOG = Logger.getLogger(App.class.getName());

    /**
     * Provides the entrance point for the application.
     * Reads the configuration and launches either the GUI or executes the program without GUI
     *
     * @author Max Schrimpf
     */


    public App() {
        // Your API key
        String apiKey = null;

        // Mandatory Query that should be executed
        String query = null;

        // duration of the analysis in minutes
        // -1 for manual ending
        Integer duration = -1;

        // CSV File for saving the tweets
        String outputPath = "easyTwitterCrawler.csv";


        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("api.key"));
            prop.load(new FileInputStream("easyTwitterCrawler.properties"));

            apiKey = prop.getProperty("apiKey", apiKey);
            query = prop.getProperty("query", query);
            duration = Integer.parseInt(prop.getProperty("duration", "" + duration));
            outputPath = prop.getProperty("outputPath", outputPath);
        } catch (IOException e) {
            // Properties could not be load - proceed with defaults
        }

        if( apiKey == null || query == null) {
            LOG.log(Level.SEVERE, "No API key or no query specified - exiting");
            System.exit(1);
        }

        LOG.log(Level.INFO, "SentimentComputation Config: ");
        LOG.log(Level.INFO, "query = " + query);
        LOG.log(Level.INFO, "outputPath = " + outputPath);
        LOG.log(Level.INFO, "duration = " + duration);

        exit();
    }

    public static void main(String[] args) {
        new App();
    }

    public void exit() {
        System.exit(0);
    }
}
