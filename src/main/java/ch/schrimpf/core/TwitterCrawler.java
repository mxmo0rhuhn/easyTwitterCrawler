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

package ch.schrimpf.core;

import twitter4j.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 *
 */
public class TwitterCrawler implements Runnable {

    public static final int MAX_CRAWLS_PER_RUN = 400;
    public static final int CRAWL_WINDOW_SIZE = 15 * 60 * 1000;

    public static final int DEFAULT_QUERY_LIMIT = 20;
    public static final String DEFAULT_LOCALE = "en";
    public static final String DEFAULT_LANG = "en";
    // default is London UK
    public static final GeoLocation DEFAULT_GEO_LOCATION = new GeoLocation(51.30, 0.08);
    public static final double DEFAULT_RADIUS = 5;

    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(TwitterCrawler.class.getName());
    private final Twitter twitter;
    private final Query query;
    private final CSVOutput csv;

    private boolean running = false;
    private int tweets = 0;
    private long last = 0;

    /**
     * Instantiates a new TwitterCrawler and start it as e new thread.
     *
     * @param csv     to write in
     * @param twitter to crawl on
     * @param query   to execute
     */
    public TwitterCrawler(CSVOutput csv, Twitter twitter, String query) {
        this.csv = csv;
        this.twitter = twitter;
        this.query = initQuery(query);
        new Thread(this).start();
    }

    /**
     * @param queryString describes keywords and filters
     * @return an initialized Query
     */
    private Query initQuery(String queryString) {
        Query query = new Query(queryString);
        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream("easyTwitterCrawler.properties"));
            query.setCount(Integer.parseInt(prop.getProperty("queryLimit")));
            query.setLocale(prop.getProperty("locale"));
            query.setLang(prop.getProperty("lang"));
            GeoLocation location = new GeoLocation(Double.parseDouble(prop.getProperty("latitude")), Double.parseDouble(prop.getProperty("longitude")));
            double radius = Double.parseDouble(prop.getProperty("radius"));
            query.setGeoCode(location, radius, Query.KILOMETERS);
        } catch (IOException e) {
            // Properties could not be load
            query.setCount(DEFAULT_QUERY_LIMIT);
            query.setLocale(DEFAULT_LOCALE);
            query.setLang(DEFAULT_LANG);
            query.setGeoCode(DEFAULT_GEO_LOCATION, DEFAULT_RADIUS, Query.KILOMETERS);
        }
        return query;
    }

    /**
     * Crawls tweets regarding to the limitation defined by Twitter.
     * Runs until thread is triggered to stop or system exit.
     */
    @Override
    public void run() {

        running = true;
        while (running) {
            crwal(MAX_CRAWLS_PER_RUN);
            try {
                // sleep until crawling tweets is allowed again
                Thread.sleep(CRAWL_WINDOW_SIZE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Performs a single crawl step according to the previous initialized query
     * until the specified limit is reached. Received tweets are stored in the
     * *.csv specified in the easyTwitterCrawler.properties file.
     * <p/>
     * TODO make selecting values flexible
     *
     * @param limit to stop on
     */
    public void crwal(int limit) {
        LOG.info("receiving tweets...");
        int i = 0;
        while (i < limit && running) {
            try {
                QueryResult res = twitter.search(query);
                if (res.getMaxId() > last) {
                    for (Status status : res.getTweets()) {
                        String[] line = {String.valueOf(status.getId()), String.valueOf(status.getCreatedAt()), status.getText(), String.valueOf(status.getUser()), String.valueOf(status.getPlace()), status.getLang()};
                        csv.writeResult(Arrays.asList(line));
                        i++;
                    }
                    last = res.getMaxId();
                } else {
                    break;
                }
            } catch (TwitterException e) {
                LOG.warning("could not process tweets");
            }
        }
        tweets += i;
        LOG.info(i + " tweets received in this crawl");
        LOG.info("totally " + tweets + " received");
    }

    /**
     * simply stops the crawler after it has finished the current run
     */
    public void stop() {
        running = true;
    }
}
