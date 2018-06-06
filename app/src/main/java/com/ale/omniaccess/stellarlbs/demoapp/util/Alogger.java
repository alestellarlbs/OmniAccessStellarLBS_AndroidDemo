/*
 * Copyright (C) 2018 Alcatel-Lucent Enterprise
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ale.omniaccess.stellarlbs.demoapp.util;

import de.mindpipe.android.logging.log4j.LogConfigurator;
import org.apache.log4j.Logger;

public class Alogger {

    private final static LogConfigurator _logConfigurator = new LogConfigurator();
    private static boolean _authorization;
    public static void setAuthorization(boolean value)
    {
        _authorization = value;
    }
    public static boolean getAuthorization()
    {
        return _authorization;
    }
    public static void Configure(String fileName, String filePattern,
                                 int maxBackupSize, long maxFileSize) {

        // set the name of the log file
        _logConfigurator.setFileName(fileName);
        // set output format of the log line
        _logConfigurator.setFilePattern(filePattern);
        // Maximum number of backed up log files
        _logConfigurator.setMaxBackupSize(maxBackupSize);
        // Maximum size of log file until rolling
        _logConfigurator.setMaxFileSize(maxFileSize);

        // configure


        _logConfigurator.configure();

    }

    public static void setJournal(String pLoggerName, String pLog)
    {
        if (_authorization)
        {
            Logger logger = Logger.getLogger(pLoggerName);
            logger.debug(pLog);
        }
        else android.util.Log.e("Alogger", "no rights to log");
    }
}
//JEL-