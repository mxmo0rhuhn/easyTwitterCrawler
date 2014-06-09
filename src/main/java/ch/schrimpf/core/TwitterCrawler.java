package ch.schrimpf.core;

import sun.plugin.AppletStatusListener;
import twitter4j.Query;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
        while(true)
        {
            if()
            {
                break;
            }
        }
    }

    /*
    private AccessToken connect(String key, String secret) throws TwitterException {
        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(key, secret);
        RequestToken requestToken = twitter.getOAuthRequestToken();
        AccessToken accessToken = null;
        while (accessToken == null) {
            System.out.println("Open the following URL and grant access to your account:");
            System.out.println(requestToken.getAuthenticationURL());
            System.out.print("Enter the PIN (if available) or just hit enter. [PIN]:");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            try {
                String pin = br.readLine();
                if (pin.length() > 0) {
                    accessToken = twitter.getOAuthAccessToken(requestToken, pin);
                } else {
                    accessToken = twitter.getOAuthAccessToken();
                }
            } catch (IOException e) {
                throw new TwitterException(e);
            }
        }
        return accessToken;
    }*/
}
