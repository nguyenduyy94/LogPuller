package com.thetechlead.logpuller.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RemoteHostCredential {

    String id;

    String name;

    String ipv4;

    Integer port;

    String username;

    String password;

    String privateKeyFilePath;

    String privateKetPassword;

    String remoteFolderPath;

    String mainLogFileName;
}
