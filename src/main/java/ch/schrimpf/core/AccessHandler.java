package ch.schrimpf.core;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AccessHandler {

    private static final Logger LOG = Logger.getLogger(AccessHandler.class.getName());

    public static AccessToken register(String key, String secret) throws TwitterException {
        // The factory instance is re-useable and thread safe.
        Twitter twitter = TwitterFactory.getSingleton();
        twitter.setOAuthConsumer(key, secret);
        RequestToken requestToken = twitter.getOAuthRequestToken();
        AccessToken accessToken = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (null == accessToken) {
            System.out.println("Open the following URL and grant access to your account:");
            System.out.println(requestToken.getAuthorizationURL());
            System.out.print("Enter the PIN(if aviailable) or just hit enter.[PIN]:");
            try {
                String pin = br.readLine();
                if (pin.length() > 0) {
                    accessToken = twitter.getOAuthAccessToken(requestToken, pin);
                } else {
                    accessToken = twitter.getOAuthAccessToken();
                }
            } catch (TwitterException te) {
                if (401 == te.getStatusCode()) {
                    System.out.println("Unable to get the access token.");
                } else {
                    te.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FileOutputStream fout = new FileOutputStream("token.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(accessToken);
            LOG.info("token saved in token.ser");
        } catch (IOException e) {
            LOG.severe("Could not store access token.");
        }
        return accessToken;
    }

    public static AccessToken loadToken() {
        try {
            FileInputStream fin = new FileInputStream("token.ser");
            ObjectInputStream ois = new ObjectInputStream(fin);
            AccessToken token = (AccessToken) ois.readObject();
            LOG.info("stored token loaded");
            return token;
        } catch (IOException | ClassNotFoundException e) {
            LOG.log(Level.WARNING, "could not dematerialize stored token");
        }
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("api.key"));
            return register(prop.getProperty("apiKey"), prop.getProperty("apiSecret"));
        } catch (IOException e) {
            // Properties could not be load
            LOG.severe("could not get api key properties");
        } catch (TwitterException e) {
            LOG.severe("could not get new token");
        }
        return null;
    }
}
