package com.github.microtweak.jvolumes.google.emulator;

import com.google.auth.oauth2.OAuth2Credentials;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MinioCredentials extends OAuth2Credentials {

    private static final long serialVersionUID = -1159613759175506313L;

    private String accessKey;
    private String secretKey;

}
