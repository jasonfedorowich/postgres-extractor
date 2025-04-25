package com.extractor.postgres.message.process;

import com.extractor.postgres.message.type.impl.Commit;
import com.extractor.postgres.message.type.impl.Relation;
import com.extractor.postgres.message.type.impl.Transaction;
import com.extractor.postgres.message.type.impl.DataType;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@ToString
public class StreamMessageContext {

    private Map<Integer, Relation> relations;
    private Map<Integer, DataType> types;
    private Transaction transaction;
    private Commit commit;

    public StreamMessageContext() {
        relations = new HashMap<>();
    }

    public void addRelation(int oid, Relation relation) {
        relations.put(oid, relation);
    }

    public Relation getRelation(int oid) {
        if (!relations.containsKey(oid))
            throw new RuntimeException("Invalid relation mapping");
        return relations.get(oid);
    }

    public void addLastTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public void addLastCommit(Commit commit) {
        this.commit = commit;
    }

    public void addType(int oid, DataType dataType) {
        types.put(oid, dataType);
    }

}
