package com.thetechlead.logpuller.service;


import com.jcraft.jsch.*;
import com.thetechlead.logpuller.model.RemoteHostCredential;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Vector;

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

    public void downloadFolders(RemoteHostCredential credential) throws JSchException {
        JSch jsch=new JSch();
        jsch.addIdentity(credential.getPrivateKeyFilePath(), credential.getPublicKeyFilePath(), credential.getPrivateKeyPassword().getBytes());
        Session session= jsch.getSession(credential.getUsername(), credential.getIpv4(), 22);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
        ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");

        String remoteFolder = credential.getRemoteFolderPath();
        try {
            channelSftp.connect();
            Vector result = channelSftp.ls(remoteFolder);
        } catch (SftpException e) {
            e.printStackTrace();

        } finally {
            channelSftp.disconnect();
            session.disconnect();
        }

    }

    // on local machine: ssh-keygen -t rsa -b 4096 -m PEM  -C "logpuller" -f ./id_rsa
    // on remote server:  setfacl -m u:logpuller:rwx,d:u:logpuller:r <folder containing log>
    public static void main(String... args) {
        ScpService scpService = ScpService.getInstance();
        RemoteHostCredential remoteHostCredential = RemoteHostCredential.builder()
                .id("test")
                .ipv4("34.126.78.135")
                .username("logpuller")
                .privateKeyFilePath("/Users/nguyenduyy/Workspaces/LogPuller/testdata/id_rsa")
                .publicKeyFilePath("/Users/nguyenduyy/Workspaces/LogPuller/testdata/id_rsa.pub")
                .privateKeyPassword("pppppp")
                .remoteFolderPath("/home/duyy.uit/guestapp/server/var/log/guestapp")
                .build();
        try {
            scpService.downloadFolders(remoteHostCredential);
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }
}
