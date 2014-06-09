package ch.schrimpf.core;

import twitter4j.Query;
import twitter4j.auth.AccessToken;

public class TwitterCrawler implements Runnable {

    private AccessToken accessToken;
    private Query query;

    public TwitterCrawler(AccessToken token, String query){

        this.query = new Query(query);

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
