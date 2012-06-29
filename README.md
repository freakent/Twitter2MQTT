Twitter2MQTT
============

Publishes a customizable real time feed from Twitter onto an MQTT topic

Set Up
======
Make sure you have a file called Twitter2MQTT.properties in your current directory or in your classpath. An 
example properties file is shown below and each property is described in more detail below that:

twitter.debug=false    
twitter.oauth.consumerKey=XXXXXXXXXXXXXXXXXXXXXX     
twitter.oauth.consumerSecret=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX     
twitter.oauth.accessToken=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX     
twitter.oauth.accessTokenSecret=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX     
twitter.filter.track=keyword1, keyword2, etc     
twitter.filter.users=user    
mqtt.topic=Twitter/filter   
mqtt.url=tcp://localhost:1883   
mqtt.clientId=MyClient   

Here is an explanation of each property value.
twitter.debug
-------------
Either yes or no

twitter.oauth.*
--------------
Register your application at http://dev.twitter.com to obtain these values. 
More information available here: http://twitter4j.org/en/configuration.html

twitter.filter.track
--------------------
A comma separated list of any words or phrases you want Twitter to supply. 
See https://dev.twitter.com/docs/api/1/post/statuses/filter see for more information.   

twitter.filter.users
-------------------
A comma separated list of Twitter user names (without the @) that you want to include in the Twitter stream. 
Twitter statuses returned are the same as the "follow" parameter described here: https://dev.twitter.com/docs/api/1/post/statuses/filter
 
mqtt.topic
----------
The full name of the Topic on your MQTT Broker

mqtt.url
--------
The full url used to access your MQTT Broker.
Default is tcp://localhost:1883 

mqtt.clientId
-------------
Uniquely identifies your instance of this application to the MQTT broker. Two separate running 
processes accessing the same broker will each require  a unique clientId.

Running
=======
The easiest way is to download the Twitter2MQTT.jar file from here https://github.com/downloads/freakent/Twitter2MQTT/Twitter2MQTT.jar and set up your properties files as above.
Once you have a properly configured Twitter2MQTT.properties file, just enter the following command:
$ java -jar Twitter2Mqtt

It can take a short while to connect and actually start streaming. To verify data is being processed you will need 
to run a separate MQTT subscriber to printout anything it receives on the Topic configured by property mqtt.topic.

