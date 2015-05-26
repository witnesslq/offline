package cn.jpush.stat.offline.v2.mr;

import cn.jpush.stat.offline.v2.stats.ClickTimesToHbaseByMR;
import cn.jpush.util.CalendarUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Created by fengwu on 15/5/10.
 */
public class ImportMapper extends Mapper<LongWritable, Text, ImmutableBytesWritable, Mutation> {
    private static Logger LOG = LoggerFactory.getLogger(ImportMapper.class);
    private byte[] family = null;
    private HTableInterface table = null;
    private long statsTime;

    /**
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void setup(Context context) throws
            IOException, InterruptedException {
        Configuration conf = context.getConfiguration();
        String familyStr = conf.get("conf.family");
        family = Bytes.toBytes(familyStr);
        String tableName = conf.get("conf.table");
        //table = Hbase.getTable(tableName);
        statsTime = CalendarUtil.transTimeStrToStamp(conf.get("conf.stats.date"), CalendarUtil.hdf);
    }

    /**
     * @param offset  The current offset into the input file
     * @param line    The current line of the file
     * @param context The task context
     * @throws IOException When mapping the input fails
     */
    @Override
    public void map(LongWritable offset, Text line, Context context)
            throws IOException {
        try {
            // line:bcf6dd1d0973a703da5beb2a        a       1900455445      1291446388      1430874000      1
            // appkey \t platform \t mid \t uid \t itime \t clickType
            byte[] qualifier = null;
            String lineString = line.toString();
//            StringTokenizer stringTokenizer = new StringTokenizer(lineString, "\t");
//            String appkey = stringTokenizer.nextToken();
//            String platform = stringTokenizer.nextToken();
//            String itimeStr = stringTokenizer.
            String[] fields = lineString.split("\t");
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
                //table.increment(incr);
                context.write(new ImmutableBytesWritable(rowkey), incr);
                context.getCounter(ClickTimesToHbaseByMR.Counters.LINES).increment(1);
            }

        } catch (Exception e) {
            LOG.error("catch exception in map function: " + e);
            e.printStackTrace();
        }
    }
}
