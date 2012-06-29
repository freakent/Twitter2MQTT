/**
 * 
 */
package freakent.twitter;

import java.util.Arrays;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.MqttTopic;

import twitter4j.FilterQuery;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * @author martin
 *
 */
public class Twitter2MQTT implements StatusListener {
	
	
    private String[] track;
    private String[] users;     
    private String topicName;
    private String brokerUrl;
    private String clientId;
    
    private static Configuration config;
    //private ConfigurationBuilder twitterConfig;
    private twitter4j.conf.Configuration twitterConfig;
    
    private Twitter twitter;
    private MqttClient mqtt;
    private MqttTopic topic;

	/**
	 * @param args
	 * @throws ConfigurationException 
	 */
	public static void main(String[] args) {
		DefaultConfigurationBuilder configBuilder;
		try {
			configBuilder = new DefaultConfigurationBuilder("config.xml");
			config = configBuilder.getConfiguration();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		new Twitter2MQTT();
	}

	public Twitter2MQTT() {
		configure();

		System.out.println("clientId: " + clientId);
		System.out.println("track:    " + Arrays.toString(track));
		System.out.println("users:    " + Arrays.toString(users));

		
    	try {

    		// Construct the MqttClient instance
			mqtt = new MqttClient(brokerUrl, clientId);
			mqtt.connect();

			topic = mqtt.getTopic(topicName);

				    	
		} catch (MqttException e) {
			e.printStackTrace();
			log("Unable to set up MQTT connection: "+e.toString());
			System.exit(1);
		}

        twitter = new TwitterFactory(this.twitterConfig).getInstance();
        TwitterStream twitterStream = new TwitterStreamFactory(this.twitterConfig).getInstance();
        twitterStream.addListener(this);
        
        long[] userIds = userIds(users);
        FilterQuery filter = new FilterQuery().follow(userIds).track(track);
        twitterStream.filter(filter);

	}

    public void onStatus(Status status) {
        //System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
        publish(status);
    }

    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
    }

    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
        System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
    }

    public void onScrubGeo(long userId, long upToStatusId) {
        System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
    }

    public void onException(Exception ex) {
        ex.printStackTrace();
    }


    private void configure() {
    	this.track = config.getStringArray("twitter.filter.track");
    	this.users = config.getStringArray("twitter.filter.users");
        this.topicName = config.getString("mqtt.topic", "Twitter/filter");
        this.brokerUrl = config.getString("mqtt.url", "tcp://localhost:1883");
        this.clientId = config.getString("mqtt.clientId", this.getClass().getSimpleName());
        
        ConfigurationBuilder tcb = new ConfigurationBuilder();
        tcb.setDebugEnabled(config.getBoolean("twitter.debug"))
           .setOAuthConsumerKey(config.getString("twitter.oauth.consumerKey"))
           .setOAuthConsumerSecret(config.getString("twitter.oauth.consumerSecret"))
           .setOAuthAccessToken(config.getString("twitter.oauth.accessToken"))
           .setOAuthAccessTokenSecret(config.getString("twitter.oauth.accessTokenSecret"));
        twitterConfig = tcb.build();
    }
    
    private long[] userIds(String[] users) {
        long[] ids = new long[users.length] ;
    	
    	for (int x=0; x<users.length; x++) {
    		try {
    		 ids[x] = twitter.showUser(users[x]).getId();
    		} catch (TwitterException e) {
    			log("User " + users[x] + " not recognized on Twitter, user ignored.");
    			log(e.toString());
    		}
    	}
    	return ids;
    }
    
    
    private void publish(Status twitterMsg) {
    	try {
    		if (!mqtt.isConnected()) {
    			mqtt.connect();
    			topic = mqtt.getTopic(topicName);
    		}
            String txt = "@" + twitterMsg.getUser().getScreenName() + " - " + twitterMsg.getText();
    		MqttMessage message = new MqttMessage(txt.getBytes());
    		topic.publish(message);
    	} catch (MqttSecurityException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	} catch (MqttException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    	
    }
    
    private static void log(String msg) {
    	System.err.println(msg);
    }
	

}
