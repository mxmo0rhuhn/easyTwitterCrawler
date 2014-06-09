/*
* Copyright (c) 2014 Philipp Gamper and Max Schrimpf
*
* This file is part of the easy Twitter crawler project.
* It enables you to specify a custom query and execute it for a while.
*
* It is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* The program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with the easy Twitter Crawler.  If not, see <http://www.gnu.org/licenses/>.
*/

package ch.schrimpf;

import ch.schrimpf.core.TwitterCrawler;
import twitter4j.TwitterException;

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
     * Reads the configuration and launches the crawler for a given time
     *
     * @author Max Schrimpf
     */
    public App() {
        // Your API key
        String apiKey = null;

        // Your APIsecret
        String apiSecret = null;

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
            apiSecret = prop.getProperty("apiSecret", apiSecret);

            query = prop.getProperty("query", query);
            duration = Integer.parseInt(prop.getProperty("duration", "" + duration));
            outputPath = prop.getProperty("outputPath", outputPath);
        } catch (IOException e) {
            // Properties could not be load - proceed with defaults
        }

        if( apiKey == null || apiSecret == null || query == null) {
            LOG.log(Level.SEVERE, "No API key or no query specified - exiting");
            System.exit(1);
        }

        LOG.log(Level.INFO, "SentimentComputation Config: ");
        LOG.log(Level.INFO, "query = " + query);
        LOG.log(Level.INFO, "outputPath = " + outputPath);
        LOG.log(Level.INFO, "duration = " + duration);

        try {
            new TwitterCrawler(apiKey, apiSecret, query);
        } catch (TwitterException e) {
            LOG.log(Level.SEVERE, "Could not authenticate");
            e.printStackTrace();
        }

        if (duration > 0) {
            int sleepTime = duration * 1000 * 60;
            try {
                Thread.sleep(duration);
                exit();
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
                LOG.log(Level.SEVERE, "Could not sleep for the given duration");
                exit();
            }
        }
    }

    public static void main(String[] args) {
        new App();
    }

    public void exit() {
        System.exit(0);
    }
}
