package ch.schrimpf.core;

import twitter4j.*;
import twitter4j.Logger;
import twitter4j.auth.AccessToken;

import java.util.*;
import java.util.logging.*;

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

        while (i < limit) {
            try {
                for (Status status : twitter.search(query).getTweets()) {
                    List<String> line = new ArrayList<>();
                    Collections.addAll(Arrays.asList(status.getId(), status.getCreatedAt(), status.getText(), status.getUser(), status.getPlace(), status.getLang()));
                    csv.writeResult(line);
                    i++;
                }
            } catch (TwitterException e) {
                LOG.warning("could not process tweets");
            }
        }
    }

    public void stop() {
        running = true;
    }
}
