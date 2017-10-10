package com.epam.spring.authenticate;

import com.epam.spring.config.SshCredentials;

public interface ISshCredentials {
  SshCredentials getSshSession();

  void setSshCredentials( SshCredentials sshCredentials );
}
