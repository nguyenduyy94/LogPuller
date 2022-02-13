package com.thetechlead.logpuller.service;


import com.thetechlead.logpuller.model.RemoteHostCredential;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

public class ScpService {

    public static ScpService INSTANCE = null;

    public static ScpService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScpService();
        }

        return INSTANCE;
    }

    private ScpService() {

    }

    public void downloadFolders(RemoteHostCredential credential) {

    }
}
