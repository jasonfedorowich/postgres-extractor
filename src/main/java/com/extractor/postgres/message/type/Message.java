package com.extractor.postgres.message.type;

public interface Message {
    //todo add paramaeter to check type

    // private List<MessageField> fields = new LinkedList<>();
    //
    // public void addField(String name, Object value){
    // fields.add(new FieldValue(name, value));
    // }
    //
    // public void addField(String name, List<Object> messageFields){
    // fields.add(new MessageField(name, messageFields));
    // }
    //
    // public static abstract class MessageField{
    // private String name;
    //
    // public MessageField(String name){
    // this.name = name;
    // }
    // public String getName(){ return name;}
    // public abstract boolean isValue();
    // public abstract boolean isArray();
    // }
    //
    // public static class FieldValue extends MessageField{
    // private Object value;
    //
    // public FieldValue(String name, Object value){
    // super(name);
    // this.value = value;
    // }
    //
    //
    // @Override
    // public boolean isValue() {
    // return true;
    // }
    //
    // @Override
    // public boolean isArray() {
    // return false;
    // }
    // }

}
