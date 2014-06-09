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

import java.util.*;

public class TwitterCrawler implements Runnable {

    public static final int MAX_CRAWLS_PER_RUN = 400;
    public static final int CRAWL_WINDOW_SIZE = 15 * 60 * 1000;

    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(TwitterCrawler.class.getName());
    private final Twitter twitter;
    private final Query query;
    private final CSVOutput csv;

    private boolean running = false;

    public TwitterCrawler(CSVOutput csv, Twitter twitter, String query) {
        this.csv = csv;
        this.twitter = twitter;
        this.query = new Query(query);
        this.query.setCount(20);
        this.query.setLocale("en");
        this.query.setLang("en");
        new Thread(this).start();
    }

    @Override
    public void run() {

        running = true;
        while (running) {
            crwal(MAX_CRAWLS_PER_RUN);
            try {
                Thread.sleep(CRAWL_WINDOW_SIZE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void crwal(int limit) {
        int i = 0;
        int k = 0;
        while (i < limit && k < 100) {
            try {
                LOG.info("receiving tweets...");
                for (Status status : twitter.search(query).getTweets()) {
                    List<String> line = new ArrayList<>();
                    Collections.addAll(Arrays.asList(status.getId(), status.getCreatedAt(), status.getText(), status.getUser(), status.getPlace(), status.getLang()));
                    csv.writeResult(line);
                    i++;
                }
                k++;
                LOG.info("receiving tweets finished");
            } catch (TwitterException e) {
                LOG.warning("could not process tweets");
            }
        }
    }

    public void stop() {
        running = true;
    }
}
