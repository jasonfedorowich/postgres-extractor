package com.extractor.postgres.message.process;

import com.extractor.postgres.cdc.StreamContext;
import com.extractor.postgres.message.type.Message;

@FunctionalInterface
public interface MessageSubscriber {

    void receive(Message message, StreamMessageContext streamMessageContext);

}
