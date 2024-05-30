package com.smalkov;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class Application {

    @Autowired
    private Environment env;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Component
    class UserRouter extends RouteBuilder
    {
        @Override
        public void configure() throws Exception {
        restConfiguration()
            .contextPath("/camel")
            .port(env.getProperty("server.port", "8082"))
            .bindingMode(RestBindingMode.json)
            .dataFormatProperty("prettyPrint", "true");

        rest("/users")
            .post()
            .consumes("application/json")
            .type(User[].class)
            .to("direct:processUsers");

        from("direct:processUsers")
            .split(body())
            .filter(simple("${body.role} == 'USER'"))
            .marshal().json(JsonLibrary.Jackson)
            .setHeader(KafkaConstants.KEY, constant("Camel"))
            .log("Sending data to Kafka")
            .to("kafka:user-topic?brokers=localhost:9092");
    }
    }
}