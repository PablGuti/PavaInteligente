package com.example.pavainteligente;

import java.io.Serializable;

public class Pava implements Serializable {
    public String color;
    public String name;
    public String house;
    public String status;
    public Double temperature;
    public Boolean switchStatus;

    public Pava(String color, String name, String house, String status, Double temperature, Boolean switchStatus) {
        this.color = color;
        this.name = name;
        this.house = house;
        this.status = status;
        this.temperature = temperature;
        this.switchStatus = switchStatus;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHouse() {
        return house;
    }

    public String getStatus() {
        return status;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = (( temperature)*100)/(1024*2);
    }

    public Boolean getSwitchStatus() {
        return switchStatus;
    }

    public void setSwitchStatus(Boolean switchStatus) {
        this.switchStatus = switchStatus;
    }

}
