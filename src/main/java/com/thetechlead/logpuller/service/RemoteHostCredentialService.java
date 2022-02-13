package com.thetechlead.logpuller.service;

public class RemoteHostCredentialService {
    public static RemoteHostCredentialService INSTANCE = null;

    public static RemoteHostCredentialService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RemoteHostCredentialService();
        }

        return INSTANCE;
    }

}
