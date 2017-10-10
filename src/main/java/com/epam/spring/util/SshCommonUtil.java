package com.epam.spring.util;

import com.epam.spring.ssh.DelegatingSshSession;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

public class SshCommonUtil {
    public static String executeCommand(String username, String password, String host, int port, String command) {
        String commandResult = StringUtils.EMPTY;
        try(DelegatingSshSession sshSession = new DelegatingSshSession(username, host, port, password)) {
            commandResult = sshSession.executeCommand(command);
        }
        catch (IOException ex) {
            //do smth
        }

        return commandResult;
    }
}
