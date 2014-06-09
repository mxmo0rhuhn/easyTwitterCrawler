package ch.schrimpf.core;

import twitter4j.Query;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class TwitterCrawler implements Runnable {

    private Twitter twitter;
    private AccessToken accessToken;
    private Query query;

    public TwitterCrawler(String key, String secret, String query) throws TwitterException {

        this.query = new Query(query);
        twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(key, secret);
        accessToken = twitter.getOAuthAccessToken();
        new Thread(this).start();
    }

    @Override
    public void run() {

        /*while(true)
        {
            if()
            {
                break;
            }
        }*/
    }

}
