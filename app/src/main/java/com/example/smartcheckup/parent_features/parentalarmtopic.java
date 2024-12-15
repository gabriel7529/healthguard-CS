package com.example.smartcheckup.parent_features;

public class parentalarmtopic {

    public String topic;
    public parentalarmtopic(String topic)
    {
        if(topic.isEmpty())
            topic="";
        this.topic=topic;
    }
}
