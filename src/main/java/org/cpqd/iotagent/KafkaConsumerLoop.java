package org.cpqd.iotagent;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.*;

// Copied shamelessly from https://www.confluent.io/blog/tutorial-getting-started-with-the-new-apache-kafka-0-9-consumer-client/

public class KafkaConsumerLoop implements Runnable {
    private final KafkaConsumer<String, String> consumer;
    private final List<String> topics;
    private final int id;

    private  String TENANCY_MANAGER_SUBJECT = "dojot.tenancy";
    private String TENANCY_MANAGER_URL = "http://auth:5000";
    private String DATA_BROKER_MANAGER = "http://localhost:80/topic/dojot.device-manager.device";




    public KafkaConsumerLoop(int id,
                        String groupId,
                        List<String> topics) {
        this.id = id;
        this.topics = topics;
        String adminTopic = GetTopic("admin");
        this.topics.add(adminTopic);
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", groupId);
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        this.consumer = new KafkaConsumer<>(props);
    }

    private String GetTopic(String service) {
        try{
            String token = TenancyManager.GetJwtToken(service);
            HttpResponse<JsonNode> response = Unirest.get(DATA_BROKER_MANAGER)
                    .header("Authorization", "Bearer " + token).asJson();
            return response.getBody().getObject().getString("topic");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }


    @Override
    public void run() {
        try {
            consumer.subscribe(topics);

            Map<String, List<PartitionInfo> > topics = consumer.listTopics();
            for (Map.Entry<String, List<PartitionInfo>> entry : topics.entrySet())
            {
                System.out.println(entry.getKey() + "/" + entry.getValue());
            }


            System.out.println(topics);


            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Long.MAX_VALUE);
                for (ConsumerRecord<String, String> record : records) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("partition", record.partition());
                    data.put("offset", record.offset());
                    data.put("value", record.value());
                    System.out.println(this.id + ": " + data);
                }
            }
        } catch (WakeupException e) {
            // ignore for shutdown 
        } finally {
            consumer.close();
        }
    }

    public void shutdown() {
        consumer.wakeup();
    }
}