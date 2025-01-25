package com.extractor.postgres.message.process;

import com.extractor.postgres.cdc.PGOutputStreamProcessor;
import com.extractor.postgres.cdc.StreamContext;
import com.extractor.postgres.message.type.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class PGOutputStreamProcessorTest {

    private PGOutputStreamProcessor pgOutputStreamProcessor;

    @Mock
    private StreamMessageContext streamMessageContext;

    @Mock
    private StreamContext streamContext;

    static List<Message> MESSAGES = new ArrayList<>();

    static String DEFAULT_RELATION_NAME = "animals";

    List<MessageSubscriber> messageSubscriberList = new ArrayList<>() {
        {
            add(new MessageSubscriber() {
                @Override
                public void receive(Message message) {
                    MESSAGES.add(message);
                }
            });
        }
    };

    @BeforeEach
    void setUp() {
        pgOutputStreamProcessor = new PGOutputStreamProcessor(messageSubscriberList, streamMessageContext,
                streamContext);
        MESSAGES.clear();
    }

    @Test
    void when_process_eventIsCaptured_thenPublished() {

       // when()
    }

    @Test
    void subscribe() {
    }

    ByteBuffer buildDefaultRelation(){
        ByteBuffer buffer = ByteBuffer.allocate(4096);
        buffer.putChar('r');
        buffer.putInt(1);
        for(int i = 0; i < DEFAULT_RELATION_NAME.length(); i++){
            buffer.putChar(DEFAULT_RELATION_NAME.charAt(i));
        }
        buffer.put((byte)0);
        return buffer;
    }
}