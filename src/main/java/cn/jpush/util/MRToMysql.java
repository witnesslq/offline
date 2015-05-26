package cn.jpush.util;

/**
 * Created by fengwu on 15/4/17.
 */

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.db.DBConfiguration;
import org.apache.hadoop.mapreduce.lib.db.DBOutputFormat;
import org.apache.hadoop.mapreduce.lib.db.DBWritable;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;

/**
 * 将mapreduce的结果数据写入mysql中
 *
 * @author wufeng
 */
public class MRToMysql {

    private static String KPI_FIELD = "kpi_";

    private static String TABLE_NAME_PREFIX = "t_d_";

    /**
     * 重写DBWritable
     *
     * @author asheng TblsWritable需要向mysql中写入数据
     */
    public static class TblsWritable implements Writable, DBWritable {
        String tbl_name;
        String tbl_type;

        public TblsWritable() {

        }

        public TblsWritable(String tbl_name, String tab_type) {
            this.tbl_name = tbl_name;
            this.tbl_type = tab_type;
        }


        public void write(PreparedStatement statement) throws SQLException {
            statement.setString(1, this.tbl_name);
            statement.setString(2, this.tbl_type);
        }


        public void readFields(ResultSet resultSet) throws SQLException {
            this.tbl_name = resultSet.getString(1);
            this.tbl_type = resultSet.getString(2);
        }


        public void write(DataOutput out) throws IOException {
            out.writeUTF(this.tbl_name);
            out.writeUTF(this.tbl_type);
        }

        public void readFields(DataInput in) throws IOException {
            this.tbl_name = in.readUTF();
            this.tbl_type = in.readUTF();
        }

        public String toString() {
            return new String(this.tbl_name + " " + this.tbl_type);
        }
    }

    public static class ConnMysqlMapper extends
            Mapper<LongWritable, Text, Text, Text>
            // TblsRecord是自定义的类型，也就是上面重写的DBWritable类
    {
        enum Counter {
            LINESKIP,
        }
        //key对于本程序没有太大的意义，没有使用
        private Text value = new Text();
        //private final static IntWritable one = new IntWritable(1);

        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            try {
                String line = value.toString();
                String[] strings = line.split("\t");
                String appkey = strings[0];
                String platform = strings[1];
                String iTime = strings[2];
                KPI_FIELD = KPI_FIELD + strings[3];
                String kpiField = KPI_FIELD + strings[3];
                String kpiValue = strings[4];
                if (iTime.length() == 19) {
                    SimpleDateFormat sdf = new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss");
                    Date date = sdf.parse(iTime);

                    //context.write(new Text(iTime.substring(0, 10)), one);
                } else {
                    // System.err.println(initTime);
                    context.getCounter(Counter.LINESKIP).increment(1);
                }
                // } catch (ArrayIndexOutOfBoundsException e) {
            } catch (ArrayIndexOutOfBoundsException e) {
                context.getCounter(Counter.LINESKIP).increment(1);
                return;
            } catch (ParseException e) {
                context.getCounter(Counter.LINESKIP).increment(1);
                return;
            }
        }

    }

    public static class ConnMysqlReducer extends
            Reducer<Text, Text, TblsWritable, TblsWritable> {
        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            int count = 0;
            for (Iterator<Text> itr = values.iterator(); itr.hasNext(); itr
                    .next()) {
                count++;
            }
            context.write(
                    new TblsWritable(key.toString(), String.valueOf(count)),
                    null);
        }
    }

    public static void main(String args[]) throws IOException,
            InterruptedException, ClassNotFoundException {
        String date = CalendarUtil.format("yyyymmddhh", System.currentTimeMillis() - 3600 * 1000);
        String input = "";
        if (args.length != 0) {
            input = args[0];
        }
        String indexName = args[1];
        TABLE_NAME_PREFIX = TABLE_NAME_PREFIX + indexName + "_";
        Configuration conf = new Configuration();

        DBConfiguration.configureDB(conf, "com.mysql.jdbc.Driver",
                "jdbc:mysql://127.0.0.1:3306/XINGXUNTONG", "hadoop", "123456");
        //Job job = new Job(conf, "test mysql connection");
        Job job = Job.getInstance(conf, "supply msgRecv with appkey and platfomr for " + date);
        job.setJarByClass(MRToMysql.class);

        job.setMapperClass(ConnMysqlMapper.class);
        job.setReducerClass(ConnMysqlReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(DBOutputFormat.class);
        FileInputFormat.addInputPath(job, new Path(input));

        DBOutputFormat.setOutput(job, "t_d_retention_off_", "appkey", "itime",KPI_FIELD);
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
