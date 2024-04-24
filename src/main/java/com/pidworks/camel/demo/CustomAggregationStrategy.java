package com.pidworks.camel.demo;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

public class CustomAggregationStrategy implements AggregationStrategy {
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            return newExchange;
        }
        //Aggregate the counters

        int oldFailed = oldExchange.getProperty("failed", Integer.class);
        int oldTotalResponse = oldExchange.getProperty("totalResponse", Integer.class);
        int oldTotal = oldExchange.getProperty("total", Integer.class);

        int newFailed = newExchange.getProperty("failed", Integer.class);
        int newTotalResponse = newExchange.getProperty("totalResponse", Integer.class);
        int newTotal = newExchange.getProperty("total", Integer.class);


        oldExchange.setProperty("failed", oldFailed + newFailed);
        oldExchange.setProperty("totalResponse", oldTotalResponse + newTotalResponse);
        oldExchange.setProperty("total", oldTotal + newTotal);

        return oldExchange;
    }
}
