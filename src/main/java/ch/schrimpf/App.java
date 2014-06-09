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

import ch.schrimpf.core.AccessHandler;
import ch.schrimpf.core.CSVOutput;
import ch.schrimpf.core.TwitterCrawler;
import twitter4j.auth.AccessToken;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {
    private static final Logger LOG = Logger.getLogger(App.class.getName());
    private CSVOutput csvOutput;
    private TwitterCrawler crawler;

    /**
     * Provides the entrance point for the application.
     * Reads the configuration and launches the crawler for a given time
     *
     * Created by Max Schrimpf
     */
    public App() {
        // Mandatory Query that should be executed
        String query = null;
        // duration of the analysis in minutes
        // -1 for manual ending
        Integer duration = -1;
        // CSV File for saving the tweets
        String outputPath = "easyTwitterCrawler.csv";

        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("easyTwitterCrawler.properties"));
            query = prop.getProperty("query");
            duration = Integer.parseInt(prop.getProperty("duration", "" + duration));
            outputPath = prop.getProperty("outputPath", outputPath);
        } catch (IOException e) {
            // Properties could not be load - proceed with defaults
        }

        if (query == null) {
            LOG.log(Level.SEVERE, "No query specified - exiting");
            exit(1);
        }

        LOG.log(Level.INFO, "Crawler Config: ");
        LOG.log(Level.INFO, "query = " + query);
        LOG.log(Level.INFO, "outputPath = " + outputPath);
        LOG.log(Level.INFO, "duration = " + duration);

        AccessToken token = AccessHandler.loadToken();
        try {
            csvOutput = new CSVOutput(outputPath);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Could open output file - exiting");
            exit(1);
        }

        if (token != null) {
            crawler = new TwitterCrawler(token, query);
        } else {
            LOG.log(Level.SEVERE, "Could not get token - exiting");
            exit(1);
        }

        if (duration > 0) {
            int sleepTime = duration * 1000 * 60;
            try {
                Thread.sleep(sleepTime);
                exit();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                LOG.log(Level.SEVERE, "Could not sleep for the given duration");
                exit();
            }
        }
    }

    public void exit(int status) {
        csvOutput.close();
        crawler.stop();
        System.exit(status);
    }

    public void exit() {
        exit(0);
    }

    public static void main(String[] args) {
        new App();
    }
}
