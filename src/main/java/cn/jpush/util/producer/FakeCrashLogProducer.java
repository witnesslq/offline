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

import cn.jpush.stat.offline.v2.entity.CrashLogAvro;
import cn.jpush.util.SystemConfig;

import java.util.Properties;

public class FakeCrashLogProducer {

    public static void main(String[] args) {
        Properties props = new Properties();

        String topic = args[0];
        int iters = Integer.parseInt(args[1]);
        props.put("metadata.broker.list", SystemConfig.getProperty("kafka.metadata.broker.list"));
        props.put("request.required.acks", "-1");
        props.put("serializer.class", "kafka.serializer.DefaultEncoder");
        props.put("key.serializer.class", "kafka.serializer.StringEncoder");
        props.put("producer.type", "sync");

        BaseProducer demo = new BaseProducer(props);

        CrashLogGen generator = new CrashLogGen();
        //File file = new File("/Users/fengwu/work/jpush/project/jpush-stats/offline/src/main/resources/avro/crashlog.json");
        CrashLogAvro[] crashLogAvros = new CrashLogAvro[iters];
        for (int i = 0; i < iters; i++) {
            crashLogAvros[i] = generator.nextCrashLog();
            demo.publish(crashLogAvros[i].serialize(), topic, CrashLogAvro.crashLogSchema);
        }
        //Baseproducer.writeJsonFile(file, crashLogAvros);
        demo.close();
    }
}
