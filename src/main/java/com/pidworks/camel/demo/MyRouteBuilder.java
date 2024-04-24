package com.pidworks.camel.demo;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

public class MyRouteBuilder extends RouteBuilder {

    public void configure() {
        from("file:src/data?recursive=true&delete=true").to("direct:process");

        from("direct:process")
                .setProperty("failed", xpath("//failed/text()", java.lang.Integer.class)) //Number of failures in the XML - success != 1
                .setProperty("totalResponse", xpath("/summary/execution/response/records/text()", java.lang.Integer.class)) //Number of elements provided by the database as a response. To understand if the DB responded with the same number of elements we sent
                .setProperty("total", xpath("/summary/execution/records/text()", java.lang.Integer.class)) //Number of records we delivered
                .setProperty("type", xpath("//query"))
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        //Discover the table type
                        String type = exchange.getProperty("type", java.lang.String.class).split(" ")[2];
                        exchange.setProperty("type", type);

                        //Calculate the grouping key
                        exchange.setProperty("key", exchange.getIn().getHeader(Exchange.FILE_PARENT) + type);
                    }
                })
                .aggregate(exchangeProperty("key"), new CustomAggregationStrategy()).completionTimeout(10000).to("direct:log");

        from("direct:log").log("${header.CamelFileParent} - ${exchangeProperty.type} ${exchangeProperty.failed}/${exchangeProperty.totalResponse}/${exchangeProperty.total}");
    }
}