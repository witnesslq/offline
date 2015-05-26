CREATE TABLE t_d_retention(
  appkey STRING,
  platform STRING,
  kpi DOUBLE
)partitioned by (date INT, internal INT) row format delimited fields terminated by '\t'
STORED AS TEXTFILE LOCATION '/user/log/hive/retention';


ALTER TABLE t_d_retention ADD IF NOT EXISTS PARTITION(date=20150417, internal=7) LOCATION '/user/log/hive/retention/20150417/7';