package com.extractor.postgres.cdc;

import com.extractor.postgres.message.process.MessageSubscriber;
import com.extractor.postgres.message.process.StreamMessageContext;
import com.extractor.postgres.message.type.*;
import com.extractor.postgres.message.type.impl.*;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class PGOutputStreamProcessor {

    private final StreamMessageContext context;

    private final List<MessageSubscriber> subscribers;

    public PGOutputStreamProcessor(List<MessageSubscriber> subscribers, StreamMessageContext streamMessageContext,
            StreamContext streamContext) {
        this.subscribers = Objects.requireNonNullElse(subscribers, new LinkedList<>());
        this.context = streamMessageContext;
        streamContext.setProcessor(this);
    }

    public void process(@NonNull ByteBuffer buffer) {
        char t = (char) buffer.get();
        switch (t) {
        case 'R':
            processRelationMessage(buffer);
            break;
        case 'B':
            processBeginMessage(buffer);
            break;
        case 'M':
            processDecodingMessage(buffer);
            break;
        case 'C':
            processCommitMessage(buffer);
            break;
        case 'O':
            processOriginMessage(buffer);
            break;
        case 'Y':
            processTypeMessage(buffer);
            break;
        case 'I':
            processInsertMessage(buffer);
            break;
        case 'U':
            processUpdateMessage(buffer);
            break;
        case 'D':
            processDeleteMessage(buffer);
            break;
        case 'T':
            processTruncateMessage(buffer);
            break;
        case 'S':
            processStreamStartMessage(buffer);
            break;
        case 'E':
            processStreamEndMessage(buffer);
            break;
        case 'c':
            processStreamCommitMessage(buffer);
            break;
        case 'A':
            processStreamAbortMessage(buffer);
            break;
        case 'b':
            processBeginPrepareMessage(buffer);
            break;
        case 'P':
            processPrepareMessage(buffer);
            break;
        case 'K':
            processCommitPrepareMessage(buffer);
            break;
        case 'r':
            processRollbackPrepareMessage(buffer);
            break;
        case 'p':
            processStreamPrepareMessage(buffer);
            break;
        default:
            return;
        }
    }

    private void processStreamPrepareMessage(@NonNull ByteBuffer buffer) {
        byte flags = buffer.get();
        long xLogRecPtrStart = buffer.getLong();
        long xLogRecPtrEnd = buffer.getLong();
        long timestampTz = buffer.getLong();
        int transactionId = buffer.getInt();
        String gid = getString(buffer);

        publish(StreamPrepareMessage.builder().xLogRecPtrEnd(xLogRecPtrEnd).xLogRecPtrStart(xLogRecPtrStart)
                .timestampTz(timestampTz).transactionId(transactionId).gid(gid).build());
    }

    private void processRollbackPrepareMessage(@NonNull ByteBuffer buffer) {
        byte flags = buffer.get();
        long xLogRecPtrStart = buffer.getLong();
        long xLogRecPtrEnd = buffer.getLong();
        long timestampTz = buffer.getLong();
        int transactionId = buffer.getInt();
        String gid = getString(buffer);

        publish(RollbackPrepareMessage.builder().xLogRecPtrEnd(xLogRecPtrEnd).xLogRecPtrStart(xLogRecPtrStart)
                .timestampTz(timestampTz).transactionId(transactionId).gid(gid).build());
    }

    private void processCommitPrepareMessage(@NonNull ByteBuffer buffer) {
        byte flags = buffer.get();
        long xLogRecPtrStart = buffer.getLong();
        long xLogRecPtrEnd = buffer.getLong();
        long timestampTz = buffer.getLong();
        int transactionId = buffer.getInt();
        String gid = getString(buffer);

        publish(PrepareCommitMessage.builder().xLogRecPtrEnd(xLogRecPtrEnd).xLogRecPtrStart(xLogRecPtrStart)
                .timestampTz(timestampTz).transactionId(transactionId).gid(gid).build());
    }

    private void processPrepareMessage(@NonNull ByteBuffer buffer) {
        byte flags = buffer.get();
        long xLogRecPtrStart = buffer.getLong();
        long xLogRecPtrEnd = buffer.getLong();
        long timestampTz = buffer.getLong();
        int transactionId = buffer.getInt();
        String gid = getString(buffer);

        publish(PrepareMessage.builder().xLogRecPtrEnd(xLogRecPtrEnd).xLogRecPtrStart(xLogRecPtrStart)
                .timestampTz(timestampTz).transactionId(transactionId).gid(gid).build());
    }

    private void processBeginPrepareMessage(@NonNull ByteBuffer buffer) {
        long xLogRecPtrStart = buffer.getLong();
        long xLogRecPtrEnd = buffer.getLong();
        long timestampTz = buffer.getLong();
        int transactionId = buffer.getInt();
        String gid = getString(buffer);

        publish(StreamBeginMessage.builder().xLogRecPtrEnd(xLogRecPtrEnd).xLogRecPtrStart(xLogRecPtrStart)
                .timestampTz(timestampTz).transactionId(transactionId).gid(gid).build());
    }

    private void processStreamAbortMessage(@NonNull ByteBuffer buffer) {
        int transactionId = buffer.getInt();
        long xLogRecPtr = buffer.getLong();
        long timestampTz = buffer.getLong();

        publish(StreamAbortMessage.builder().xLogRecPtr(xLogRecPtr).transactionId(transactionId)
                .timestampTz(timestampTz).build());
    }

    private void processStreamCommitMessage(@NonNull ByteBuffer buffer) {
        byte flags = buffer.get();
        long xLogRecPtrStart = buffer.getLong();
        long xLogRecPtrEnd = buffer.getLong();
        long timestampTz = buffer.getLong();
        publish(StreamCommitMessage.builder().flags(flags).xLogRecPtrEnd(xLogRecPtrEnd).xLogRecPtrStart(xLogRecPtrStart)
                .timestampTz(timestampTz).build());

    }

    private void processStreamEndMessage(@NonNull ByteBuffer buffer) {
        publish(StreamEndMessage.builder().build());
    }

    private void processStreamStartMessage(@NonNull ByteBuffer buffer) {
        byte start = buffer.get();
        publish(StreamStartMessage.builder().start(start).build());
    }

    private void processTruncateMessage(@NonNull ByteBuffer buffer) {
        int numberOfRelations = buffer.getInt();
        byte option = buffer.get();
        int oid = buffer.getInt();
        Relation relation = context.getRelation(oid);

        publish(Truncate.builder().oid(oid).option(option).relation(relation).numberOfRelations(numberOfRelations)
                .build());

    }

    private void processDeleteMessage(@NonNull ByteBuffer buffer) {
        int oid = buffer.getInt();
        char tupleDataType = (char) buffer.get();
        TupleData oldTupleData = processTupleData(buffer, oid, context);
        publish(DeleteMessage.builder().oid(oid).oldTupleData(oldTupleData).tupleDataType(tupleDataType).build());
    }

    private void processUpdateMessage(@NonNull ByteBuffer buffer) {
        int oid = buffer.getInt();
        char tupleDataType = (char) buffer.get();
        TupleData oldTuple = switch (tupleDataType) {
        case 'O', 'K' -> processTupleData(buffer, oid, context);
        default -> null;
        };
        char newDataType = (char) buffer.get();
        TupleData newData = processTupleData(buffer, oid, context);
        publish(UpdateMessage.builder().newDataType(newDataType).oid(oid).tupleDataType(tupleDataType).newData(newData)
                .oldTuple(oldTuple).build());
    }

    private void processInsertMessage(@NonNull ByteBuffer buffer) {
        int oid = buffer.getInt();
        char tupleDataType = (char) buffer.get();
        TupleData tupleData = processTupleData(buffer, oid, context);
        publish(InsertMessage.builder().oid(oid).tupleDataType(tupleDataType).tupleData(tupleData).build());
    }

    private void processTypeMessage(@NonNull ByteBuffer buffer) {
        int oid = buffer.getInt();
        String schema = getString(buffer);
        String dataType = getString(buffer);

        var typeMessage = DataType.builder().dataType(dataType).oid(oid).schema(schema).build();

        context.addType(oid, typeMessage);
        publish(typeMessage);
    }

    private void processOriginMessage(@NonNull ByteBuffer buffer) {
        long xLogRecPtr = buffer.getLong();
        String name = getString(buffer);

        var originMessage = OriginMessage.builder().xLogRecPtr(xLogRecPtr).name(name).build();
        publish(originMessage);
    }

    private void processCommitMessage(@NonNull ByteBuffer buffer) {
        byte flags = buffer.get();
        long xLogRecPtr = buffer.getLong();
        long xLogRecPtrEnd = buffer.getLong();
        long timestamp = buffer.getLong();

        var commit = Commit.builder().flags(flags).xLogRecPtr(xLogRecPtr).xLogRecPtrEnd(xLogRecPtrEnd)
                .timestamp(timestamp).build();

        context.addLastCommit(commit);
        publish(commit);
    }

    private void processDecodingMessage(@NonNull ByteBuffer buffer) {
        byte flags = buffer.get();
        long xLogRecPtr = buffer.getLong();
        String prefix = getString(buffer);
        int length = buffer.getInt();
        byte[] content = new byte[length];
        buffer.get(content);

        var decodingMessage = DecodingMessage.builder().flags(flags).xLogRecPtr(xLogRecPtr).prefix(prefix)
                .content(content).build();

        publish(decodingMessage);
    }

    private void processBeginMessage(@NonNull ByteBuffer buffer) {
        long xLogRecPtr = buffer.getLong();
        long timestampTz = buffer.getLong();
        int transactionId = buffer.getInt();

        var transaction = Transaction.builder().transactionId(transactionId).timestampTz(timestampTz)
                .xLogRecPtr(xLogRecPtr).build();

        context.addLastTransaction(transaction);
        publish(transaction);

    }

    private void processRelationMessage(@NonNull ByteBuffer buffer) {
        int oid = buffer.getInt();
        String schema = getString(buffer);
        String table = getString(buffer);
        byte replicaIdentitySetting = buffer.get();
        short numberOfColumns = buffer.getShort();

        List<Column> columns = new LinkedList<>();

        for (int i = 0; i < numberOfColumns; i++) {
            columns.add(Column.builder().flags(buffer.get()).name(getString(buffer)).oid(buffer.getInt())
                    .typeModifier(buffer.getInt()).build());
        }
        var relation = Relation.builder().oid(oid).columns(columns).numberOfColumns(numberOfColumns).schema(schema)
                .replicaIdentitySetting(replicaIdentitySetting).table(table).build();

        context.addRelation(oid, relation);
        publish(relation);

    }

    private void publish(Message message) {
        for (MessageSubscriber subscriber : subscribers) {
            subscriber.receive(message);
        }
    }

    public void subscribe(MessageSubscriber messageSubscriber) {
        subscribers.add(messageSubscriber);
    }

    private static String getString(ByteBuffer buffer) {
        StringBuilder builder = new StringBuilder();
        byte b;
        while ((b = buffer.get()) != 0) {
            builder.append((char) b);
        }
        return builder.toString();
    }

    private static String readBufferString(ByteBuffer buffer) {
        int length = buffer.getInt();
        byte[] data = new byte[length];
        buffer.get(data);
        return new String(data, StandardCharsets.UTF_8);
    }

    private static TupleData processTupleData(@NonNull ByteBuffer buffer, int oid, StreamMessageContext context) {
        Relation relation = context.getRelation(oid);
        short numberOfColumns = buffer.getShort();
        List<Column> columns = relation.getColumns();

        List<TupleData.ColumnData> columnData = new ArrayList<>(numberOfColumns);

        for (int i = 0; i < numberOfColumns; i++) {
            Column column = columns.get(i);
            char type = (char) buffer.get();
            switch (type) {
            case 't':
                columnData.add(TupleData.ColumnData.builder().data(new Data.ValueData(readBufferString(buffer), type))
                        .column(column).build());
                break;
            case 'u':
                columnData
                        .add(TupleData.ColumnData.builder().data(new Data.UnchangedData(type)).column(column).build());
                break;
            case 'n':
                columnData.add(TupleData.ColumnData.builder().data(new Data.NullData(type)).column(column).build());
                break;
            default:
                throw new RuntimeException("Unsupported tuple type"); // todo change error message
            }
        }

        return TupleData.builder().columnData(columnData).build();

    }
}
