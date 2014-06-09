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

    Twitter twitter = new TwitterFactory().getInstance();

    private static final Logger LOG = Logger.getLogger(AccessHandler.class.getName());

    public Twitter connect() throws TwitterException {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("api.key"));

        } catch (IOException e) {
            // Properties could not be load
            throw new TwitterException("could not get api key properties");
        }
        twitter.setOAuthConsumer(prop.getProperty("apiKey"), prop.getProperty("apiSecret"));
        twitter.setOAuthAccessToken(loadToken());
        return twitter;
    }

    private AccessToken register() throws TwitterException {
        // The factory instance is re-useable and thread safe.
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

    private AccessToken loadToken() throws TwitterException {
        try {
            FileInputStream fin = new FileInputStream("token.ser");
            ObjectInputStream ois = new ObjectInputStream(fin);
            AccessToken token = (AccessToken) ois.readObject();
            LOG.info("stored token loaded");
            return token;
        } catch (IOException | ClassNotFoundException e) {
            LOG.log(Level.WARNING, "could not dematerialize stored token");
        }
        return register();
    }
}
