package com.epam.spring.authenticate;

import com.epam.spring.config.SshCredentials;

public interface ISshCredentials {
  SshCredentials getSshCredentials();

  void setSshCredentials( SshCredentials sshCredentials );
}
