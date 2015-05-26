/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.jpush.util.producer;

import cn.jpush.stat.offline.v2.entity.ContentRecordAvro;
import cn.jpush.stat.offline.v2.entity.CrashLogAvro;
import cn.jpush.stat.offline.v2.entity.CrashLogsAvroRecord;
import org.apache.avro.generic.GenericData;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CrashLogGen {

    /**
     * Public constructors
     */
    CrashLogGen() {
        r = new Random();
    }

    CrashLogGen(long seed) {
        r = new Random(seed);
    }

    private String message;
    private int count;
    private String stacktrace;
    private String versionname;
    private long crashtime;

    /**
     * Constants
     */
    final int NO_WORDS = 5;              // Number of each type word
    final String type = "crash_log";
    final String versioncode = "34";
    final String networktypes[] = {"WIFI", "3G", "4G", "IPv4", "IPv6"};
    final String platforms[] = {"a", "i", "w"};
    final String SPACE = " ";
    final String appkey[] = {"c56e1a24235859270a39d733",
            "013055744607fcf14c4560f9",
            "d3bb3bbfe35c6b048f0da506",
            "921f50c9761d74ca64cf1b59",
            "2ed9310b6f782018f2a6cec0",
            "ddbd2f4a18ba2c1f4ce5c3e9",
            "a1c772c4a3910f3121b1e3f2"};
    final Long uids[] = new Long[]{Long.valueOf(825014446), Long.valueOf(833233775),
            Long.valueOf(870477075), Long.valueOf(898010137),
            Long.valueOf(900516004), Long.valueOf(900516036), Long.valueOf(900130699), Long.valueOf(900130705)};
    final String exceptions[] = {
            "java.lang.NullPointerException",
            "java.lang.ClassNotFoundexception",
            "java.lang.ArithmeticException",
            "java.lang.ClassCastException",
            "java.lang.IllegalAccessException",
            "java.lang.IllegalArgumentException",
            "java.lang.IndexOutOfBoundsException",
            "java.lang.InterruptedException",
            "java.lang.RuntimeException",
            "java.lang.TypeNotPresentException"};


    /**
     * Protected instance variable
     */
    Random r;            // For random numbers

    /**
     * Class methods
     */

    public CrashLogAvro nextCrashLog() {

        Map<String, String> deviceInfo = new HashMap<String, String>();
        deviceInfo.put("timezone", "+8");
        deviceInfo.put("model", "InFocus M320");
        deviceInfo.put("app_versioncode", "40");
        deviceInfo.put("cpu_info", "3.7.1");
        deviceInfo.put("sdk_version", "1.6.3");
        deviceInfo.put("app_key", "c56e1a24235859270a39d733");
        deviceInfo.put("resolution", "720*1280");
        deviceInfo.put("language", "zh_TW");
        deviceInfo.put("channel", "developer-default");
        deviceInfo.put("os_version", "19");

        int itime = (int) (System.currentTimeMillis() / 1000);
        count = rand();
        stacktrace = "java.lang.NullPointerException\n\tat com.qooservice.QooApkDownloadService.a(Unknown Source)\n\tat com.qooservice.QooApkDownloadService.a(Unknown Source)\n\tat com.qooservice.d.run(Unknown Source)\n";
        versionname = "3.4";
        long crashtime = System.currentTimeMillis();
        String type = "crash_log";

        String app_key = appkey[rand() % appkey.length];    // Initialize next random app_key
        String platform = "a";
        long uid = uids[rand() % uids.length];
        String networktype = networktypes[rand() % networktypes.length];
        String message = exceptions[rand() % exceptions.length];

        // initial crash logs record
        CrashLogsAvroRecord crashLogsAvroRecord = new CrashLogsAvroRecord(message, versioncode,
                networktype, count, stacktrace, versionname, crashtime);

        GenericData.Array<GenericData.Record> crashLogsRecords = new GenericData.Array<GenericData.Record>(1,
                CrashLogAvro.crashLogSchema.getField("crashlogs").schema());
        crashLogsRecords.add(crashLogsAvroRecord.serialize());

        // initial content record
//        ContentRecordAvro contentRecordAvro = new ContentRecordAvro(type,crashLogsRecords,deviceInfo,itime);
//        // initial crash log record
//        GenericData.Array<GenericData.Record> content = new GenericData.Array<GenericData.Record>(1,
//                CrashLogAvro.crashLogSchema.getField("content").schema());
//        content.add(contentRecordAvro.serialize());

        CrashLogAvro crashLogAvro = new CrashLogAvro(uid, crashLogsRecords, app_key, platform, itime, deviceInfo);

        CrashLogsAvroRecord crashLog = new CrashLogsAvroRecord(message, versioncode,
                networktype, count, stacktrace, versionname, crashtime, deviceInfo, app_key,
                uid, platform);

        return crashLogAvro;
        //return null;
    }

    int rand() {
        int ri = r.nextInt() % NO_WORDS;
        if (ri < 0)
            ri += NO_WORDS;
        return ri;
    }

    public static void main(String[] args) {
        CrashLogGen generator = new CrashLogGen();
        generator.nextCrashLog();
    }
}