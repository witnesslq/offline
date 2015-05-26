drop view if exists retention_${internal}_${stat_time};
drop view if exists reg_nums_${internal}_${stat_time};
drop view if exists onlinereg_uid_${internal}_${stat_time};
drop view if exists onlinereg_uid_nums_${internal}_${stat_time};

CREATE VIEW IF NOT EXISTS reg_nums_${internal}_${stat_time} AS
  select appkey, platform, count(uid) regnums
  from user_incr
  where  month=${ymonth}
  and day=${dd}
  group by appkey, platform;

CREATE VIEW IF NOT EXISTS onlinereg_uid_${internal}_${stat_time} AS
  select b.appkey, b.platform, b.uid 
  from user_online_external a join user_incr b
  on a.uid=b.uid 
  and a.appkey=b.appkey 
  and a.platform= b.platform
  WHERE a.year = '${year}' and a.month = '${month}' and a.day = '${day}' 
  and b.month = ${ymonth}
  and b.day = ${dd};

CREATE VIEW IF NOT EXISTS onlinereg_uid_nums_${internal}_${stat_time} AS
  select appkey, platform, 
  count(distinct uid) onlinenums 
  from onlinereg_uid_${internal}_${stat_time}
  group by appkey, platform; 

CREATE VIEW IF NOT EXISTS retention_${internal}_${stat_time} AS
  select a.appkey, a.platform, a.regnums, 
  COALESCE(b.onlinenums, 0L) onlinenums 
  from reg_nums_${internal}_${stat_time} a
  left outer join onlinereg_uid_nums_${internal}_${stat_time} b 
  on a.appkey =b.appkey
  and a.platform =b.platform;

INSERT OVERWRITE TABLE t_d_retention PARTITION(date, internal)
select appkey, lcase(platform), round(onlinenums / regnums, 4) * 100, ${stat_time} , ${internal} from retention_${internal}_${stat_time};
	
drop view if exists retention_${internal}_${stat_time};
drop view if exists reg_nums_${internal}_${stat_time}
drop view if exists onlinereg_uid_${internal}_${stat_time};
drop view if exists onlinereg_uid_nums_${internal}_${stat_time};






