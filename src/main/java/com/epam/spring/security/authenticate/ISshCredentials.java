package com.epam.spring.security.authenticate;

import com.epam.loader.config.credentials.SshCredentials;

public interface ISshCredentials {
  SshCredentials getSshCredentials();

  void setSshCredentials( SshCredentials sshCredentials );
}
