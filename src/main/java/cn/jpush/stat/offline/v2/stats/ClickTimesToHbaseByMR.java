package cn.jpush.stat.offline.v2.stats;

import cn.jpush.stat.offline.v2.mr.ImportMapper;
import org.apache.commons.cli.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by fengwu on 15/5/5.
 */
public class ClickTimesToHbaseByMR extends AbstractHiveStats implements Tool {

    private static Logger LOG = LoggerFactory.getLogger(ClickTimesToHbaseByMR.class);

    public static final String KPI_CODE = "pushclicktimes";

    public static final String NAME = "StatsClickTimesByHbase";

    public static final String USER_CLICK_HDFS_PATH = "/user/log/hive/userclick/";

    private Configuration conf;

    public ClickTimesToHbaseByMR(String kpiCode) {
        super(kpiCode);
    }

    public enum Counters {LINES}

    public static String getFilePathByDate(String dir, String date) {
        StringBuilder path = new StringBuilder(dir);
        if (!dir.endsWith(File.separator)) {
            path.append(File.separator);
        }
        path.append(date.substring(0, 4)).append(File.separator)
                .append(date.substring(4, 6)).append(File.separator)
                .append(date.substring(6, 8)).append(File.separator).append(date);

        return path.toString();
    }

    @Override
    public String parseHiveStatsHql() {
        return null;
    }

    public int run(String[] args) throws Exception {

        Configuration conf = getConf();
        String[] otherArgs =
                new GenericOptionsParser(conf,args).getRemainingArgs();//Give the command line arguments to the generic parser first to handle "-Dxyz" properties.

        CommandLine cmd = parseArgs(otherArgs);
        //String table = cmd.getOptionValue("t");
        String table = args[0];
        //String input = cmd.getOptionValue("i");

        //String family = cmd.getOptionValue("f");
        String family = args[1];
        String input = args[2];
        //String statsDate = cmd.getOptionValue("sd");
        String statsDate = args[3];
        conf.set("conf.table", table);
        conf.set("conf.family", family);
        conf.set("conf.stats.date", statsDate);

        Job job = Job.getInstance(conf, "Import from file " + input +
                " into table " + table);
        job.setJarByClass(ClickTimesToHbaseByMR.class);
        job.setMapperClass(ImportMapper.class);
        job.setOutputFormatClass(TableOutputFormat.class);
        job.getConfiguration().set(TableOutputFormat.OUTPUT_TABLE, table);
        LOG.info("table name is:" + table);
        job.setOutputKeyClass(ImmutableBytesWritable.class);
        job.setOutputValueClass(Writable.class);
        job.setNumReduceTasks(0); // MapOnly This is a map only job, therefore tell the framework to bypass the reduce step.
        FileInputFormat.addInputPath(job, new Path(input));
        return job.waitForCompletion(true) ? 0 : 1;
    }

    public void setConf(Configuration conf) {
        this.conf = conf;
    }

    public Configuration getConf() {
        return conf;
    }

    private static CommandLine parseArgs(String[] args) throws ParseException {
        Options options = new Options();
        Option o = new Option("t", "table", true,
                "table to import into (must exist)");
        o.setArgName("table-name");
        o.setRequired(true);
        options.addOption(o);
        options.addOption(o);
        o = new Option("f", "family", true,
                "column family to store row data into (must exist)");
        o.setArgName("family:");
        o.setRequired(true);
        options.addOption(o);
        o = new Option("i", "input", true,
                "the directory or file to read from");
        o.setArgName("path-in-HDFS");
        o.setRequired(true);
        options.addOption(o);

        o = new Option("sd", "stats-date", true,
                "the stats date or file name to read from");
        o.setArgName("stats-date");
        o.setRequired(true);
        options.addOption(o);
        options.addOption("d", "debug", false, "switch on DEBUG log level");
        CommandLineParser parser = new PosixParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage() + "\n");
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(NAME + " ", options, true);
            System.exit(-1);
        }

        return cmd;
    }

    public static void main(String[] args) throws Exception {
        for (String s : args) {
            LOG.info(s);
        }

        Configuration conf = HBaseConfiguration.create();

        int code = ToolRunner.run(conf, new ClickTimesToHbaseByMR(KPI_CODE), args);
        if (code != 0) {
            LOG.error("fail to import click times data to hbase");
            return;
        }
    }
}
