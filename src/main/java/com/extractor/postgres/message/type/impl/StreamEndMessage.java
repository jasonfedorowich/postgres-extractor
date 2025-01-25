package com.extractor.postgres.message.type.impl;

import com.extractor.postgres.message.type.Message;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Builder
@ToString
@EqualsAndHashCode
public class StreamEndMessage implements Message {
}
