package streams.KStreams;


import io.confluent.kafka.serializers.AbstractKafkaAvroSerDe;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.omg.SendingContext.RunTime;
//import io.confluent.kafka.streams.serdes.avro.Spec

import java.util.Properties;

public class TestKStreams1 {

    public static void main(String[] args) {
        final String bootstrapServers = "localhost:9092";
        final Properties streamsConfiguration = getStreamsConfiguration(bootstrapServers);

        final StreamsBuilder builder = new StreamsBuilder();
        createTestStream(builder);

        final KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration);
        streams.start();

        streams.localThreadsMetadata().forEach(data -> System.out.println(data));
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

    }

    static void createTestStream(final StreamsBuilder builder){

        final Serde<String> stringSerde_TestStream = Serdes.String();

        final KStream<String,String> ts = builder.stream("TestStream");
          ts.print(Printed.toSysOut());


    }

    static Properties getStreamsConfiguration(final String bootstrapservers) {
        final Properties config = new Properties();

        config.put(StreamsConfig.APPLICATION_ID_CONFIG,"data");
        config.put(StreamsConfig.CLIENT_ID_CONFIG,"data-client");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapservers);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG,"specificAvroserde.class");
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,"");
        config.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,"http://localhost:8081");

        return config;

    }
}
