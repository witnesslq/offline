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
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Properties;

public class BaseProducer {

    private final Producer<String, byte[]> kafkaProducer;

    public BaseProducer(Properties props) {

        kafkaProducer = new Producer<String, byte[]>(new ProducerConfig(props));
    }

    public static byte[] serializeAvro(Schema schema, GenericRecord event) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BinaryEncoder binaryEncoder = EncoderFactory.get().binaryEncoder(stream, null);
        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<GenericRecord>(schema);
        datumWriter.write(event, binaryEncoder);
        binaryEncoder.flush();
        IOUtils.closeQuietly(stream);

        return stream.toByteArray();
    }

    public static byte[] serializeJson(Schema schema, GenericRecord event) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        JsonEncoder jsonEncoder = EncoderFactory.get().jsonEncoder(schema, stream);
        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<GenericRecord>(schema);
        datumWriter.write(event, jsonEncoder);
        jsonEncoder.flush();
        IOUtils.closeQuietly(stream);

        return stream.toByteArray();
    }



    /**
     * Writes out Java objects into a binary Avro-encoded file
     *
     * @param file      where to store serialized Avro records
     * @param crashLogAvros is an object of CrashLog to be serialized
     * @throws IOException
     */
    public static void writeJsonFile(File file, CrashLogAvro[] crashLogAvros) {
        try {
            GenericDatumWriter writer = new GenericDatumWriter(CrashLogAvro.crashLogSchema);
            JsonEncoder e = EncoderFactory.get().jsonEncoder(CrashLogAvro.crashLogSchema, new FileOutputStream(file));

            for (CrashLogAvro crashLogAvro : crashLogAvros)
                writer.write(crashLogAvro.serialize(), e);
            e.flush();

        } catch (IOException e) {
            throw new RuntimeException("Avro serialization failure", e);
        }
    }

    public static void writeAvroFile(File file, CrashLogAvro[] crashLogAvros) {
        try {
            GenericDatumWriter datum = new GenericDatumWriter(CrashLogAvro.crashLogSchema);
            DataFileWriter writer = new DataFileWriter(datum);

            writer.create(CrashLogAvro.crashLogSchema, file);
            for (int i = 0; i < crashLogAvros.length; i++)
                writer.append(crashLogAvros[i].serialize());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException("Avro serialization failure", e);
        }
    }


    public void publish(GenericRecord event, String topic, Schema schema) {
        try {
            //byte[] m = serializeAvro(schema, event);
            byte[] m = serializeJson(schema, event);
            // test read this byte
            DatumReader<GenericData.Record> reader = new GenericDatumReader<GenericData.Record>(CrashLogAvro.crashLogSchema);
            InputStream in = new ByteArrayInputStream(m);
            JsonDecoder decoder = DecoderFactory.get().jsonDecoder(CrashLogAvro.crashLogSchema, in);
            GenericData.Record datum = reader.read(null, decoder);
            Long uid = (Long)datum.get("uid");
            GenericData.Array<GenericData.Record> content = (GenericData.Array<GenericData.Record>)datum.get("content");
            String platform = datum.get("platform").toString();
            String app_key = datum.get("app_key").toString();

            //String record = new String(m);

            KeyedMessage<String, byte[]> km = new KeyedMessage<String, byte[]>(topic, m);
            kafkaProducer.send(km);
        } catch (IOException e) {
            throw new RuntimeException("Avro serialization failure", e);
        }
    }

    public void publish(byte[] bytes, String topic){
        KeyedMessage<String, byte[]> km = new KeyedMessage<String, byte[]>(topic, bytes);
        kafkaProducer.send(km);
    }

    public void close() {
        kafkaProducer.close();
    }
}
