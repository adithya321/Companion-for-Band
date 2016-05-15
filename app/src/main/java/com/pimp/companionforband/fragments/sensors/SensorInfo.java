package com.pimp.companionforband.fragments.sensors;

public class SensorInfo {
    private String name;
    private String data;
    private boolean options;
    private boolean graph;
    private String details;

    public SensorInfo(String name, String data, boolean options, boolean graph, String details) {
        this.name = name;
        this.data = data;
        this.options = options;
        this.graph = graph;
        this.details = details;
    }

    public String getName() {
        return name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
