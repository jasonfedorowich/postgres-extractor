package com.extractor.postgres.message.process;

import com.extractor.postgres.cdc.StreamContext;
import com.extractor.postgres.message.type.Message;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingMessageSubscriber implements MessageSubscriber {

    @Override
    public void receive(Message message) {
        log.info("<< Message received: {}", message);
    }

}
