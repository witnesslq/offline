package cn.jpush.stat.offline.v2.stats;

import cn.jpush.hbase.Hbase;
import cn.jpush.util.CalendarUtil;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengwu on 15/5/7.
 */
public class ClickTimesToHbase {

    private static Logger LOG = LoggerFactory.getLogger(ClickTimesToHbase.class);

    public static void main(String[] args) {
        String tableName = args[0];
        String familyStr = args[1];
        String parentHdfsPath = args[2];
        String statsDate = args[3];

        String uri = ClickTimesToHbaseByMR.getFilePathByDate(parentHdfsPath, statsDate);
        InputStream in = null;
        BufferedReader buf = null;
        long statsTime = CalendarUtil.transTimeStrToStamp(statsDate, CalendarUtil.hdf);
        byte[] qualifier = null;
        byte[] family = Bytes.toBytes(familyStr);
        List<Increment> incrs = new ArrayList<Increment>();
        try {
            FileSystem fs = FileSystem.get(URI.create(uri), Hbase.createConf());
            HTableInterface tableUserClickStats = Hbase.getTable(tableName);
            in = fs.open(new Path(uri));
            buf = new BufferedReader(new InputStreamReader(in));
            String line = "";
            line = buf.readLine();
            while (line != null) {
                String[] fields = line.split("\t");
                String appkey = fields[0];
                String platform = fields[1];
                int clickTypeCode = Integer.parseInt(fields[5]);
                if (1 == clickTypeCode) {
                    long itime = Long.parseLong(fields[4]) * 1000;
                    if (itime > statsTime || (itime + 30 * 60 * 60 * 24 * 1000L) < statsTime) {
                        itime = statsTime;
                    }
                    String hDate = CalendarUtil.format("yyyyMMddHH", itime);
                    StringBuilder sb = new StringBuilder(appkey).append("|")
                            .append(platform).append("|").append(hDate.substring(0, 8));

                    qualifier = Bytes.toBytes("h_" + hDate.substring(8));

                    byte[] rowkey = Bytes.toBytes(sb.toString());
                    Increment incr = new Increment(rowkey);
                    incr.addColumn(family, qualifier, 1L);
                    incrs.add(incr);
                }
                line = buf.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("click time to hbase error: " + e.getMessage());

        } finally {
            IOUtils.closeStream(in);
        }
    }
}
