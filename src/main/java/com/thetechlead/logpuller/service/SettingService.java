package com.thetechlead.logpuller.service;

public class SettingService {

    public static SettingService INSTANCE = null;

    public static SettingService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SettingService();
        }

        return INSTANCE;
    }

    public void load(){

    }

    public void save() {

    }
}
