/**
 * Project Name:offline
 * File Name:StatsContext.java
 * Package Name:cn.jpush.stat.offline.v2.bean
 * Date:2015年1月7日下午5:07:59
 * Copyright (c) 2015, wufeng@jpush.cn All Rights Reserved.
 *
*/

package cn.jpush.stat.offline.v2.entity;
/**
 * ClassName:StatsContext <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2015年1月7日 下午5:07:59 <br/>
 * @author   wufeng
 * @version  
 * @since    JDK 1.7
 * @see 	 
 */
public class Statistic {
    
    private String statsDate;
    
    private String statsType;
    
    private String platform;
    
    // 统计周期
    private String frequency;
    

    public Statistic() {
        super();
    }

    public Statistic(String statsDate, String statsType, String platform, String frequency) {
        super();
        this.statsDate = statsDate;
        this.statsType = statsType;
        this.platform = platform;
        this.frequency = frequency;
    }

    public String getStatsDate() {
        return statsDate;
    }

    public void setStatsDate(String statsDate) {
        this.statsDate = statsDate;
    }

    public String getStatsType() {
        return statsType;
    }

    public void setStatsType(String statsType) {
        this.statsType = statsType;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    @Override
    public String toString() {
        return "Statistic [statsDate=" + statsDate + ", statsType=" + statsType + ", platform="
                + platform + ", frequency=" + frequency + "]";
    }
    
}

