package com.thetechlead.logpuller.service;


import com.jcraft.jsch.*;
import com.thetechlead.logpuller.model.RemoteHostCredential;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.Vector;

public class ScpService {

    public static ScpService INSTANCE = null;

    public static final String PATHSEPARATOR = "/";

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
            recursiveFolderDownload(channelSftp, remoteFolder, credential.getDownloadLogLocalPath());
        } catch (SftpException e) {
            e.printStackTrace();

        } finally {
            channelSftp.disconnect();
            session.disconnect();
        }

    }

    private void recursiveFolderDownload(ChannelSftp channelSftp, String sourcePath, String destinationPath) throws SftpException {
        Vector<ChannelSftp.LsEntry> fileAndFolderList = channelSftp.ls(sourcePath); // Let list of folder content
        // Iterate through list of folder content
        for (ChannelSftp.LsEntry item : fileAndFolderList) {
            if (!item.getAttrs().isDir()) { // Check if it is a file (not a directory).
                if (!(new File(destinationPath + PATHSEPARATOR + item.getFilename())).exists()
                        || (item.getAttrs().getMTime() > Long
                        .valueOf(new File(destinationPath + PATHSEPARATOR + item.getFilename()).lastModified()
                                / (long) 1000)
                        .intValue())) { // Download only if changed later.
                    new File(destinationPath + PATHSEPARATOR + item.getFilename());
                    // Download file from source (sourcefilename, destination filename).
                    channelSftp.get(sourcePath + PATHSEPARATOR + item.getFilename(),
                            destinationPath + PATHSEPARATOR + item.getFilename());
                }
            } else if (!(".".equals(item.getFilename()) || "..".equals(item.getFilename()))) {
                new File(destinationPath + PATHSEPARATOR + item.getFilename()).mkdirs(); // Empty folder copy.
                // Enter found folder on server to read its contents and create locally.
                recursiveFolderDownload(channelSftp, sourcePath + PATHSEPARATOR + item.getFilename(),
                        destinationPath + PATHSEPARATOR + item.getFilename());
            }
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
                .downloadLogLocalPath("./data/guestapp")
                .build();
        try {
            scpService.downloadFolders(remoteHostCredential);
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }
}
