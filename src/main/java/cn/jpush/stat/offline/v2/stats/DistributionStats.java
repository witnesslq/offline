/**
 * Project Name:offline
 * File Name:AppDistributuionStats.java
 * Package Name:cn.jpush.stat.offline.v2.stats
 * Date:2014年12月18日上午11:53:36
 * Copyright (c) 2014, wufeng@jpush.cn All Rights Reserved.
 *
*/

package cn.jpush.stat.offline.v2.stats;

import static cn.jpush.util.ESJavaClientHelper.*;

import java.util.HashMap;

import org.elasticsearch.action.search.SearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.jpush.util.MySqLImporter;

import com.google.common.collect.Maps;

/**
 * ClassName:AppDistributuionStats <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2014年12月18日 上午11:53:36 <br/>
 * @author   wufeng
 * @version  
 * @since    JDK 1.7
 * @see 	 
 */
public class DistributionStats {
    private static Logger logger = LoggerFactory.getLogger(DistributionStats.class);
    
    public static void main(String[] args) throws Exception{
        String index = "userprofile";
        String type = args[1];
        String statsDate = args[2];
        String envFlag = args[3];
        String statsType = args[4];
        String[] provinces = args[5].split(",");
        String statsDateType = args[6];
                         
        HashMap<String, Long> distributions = Maps.newHashMap();
        for(String province : provinces){
            long startCr = System.currentTimeMillis();
            SearchResponse srp = getSearchResponse(index, type, statsDate, province, statsType, "uid", statsDateType);
            long cnt = srp.getHits().getTotalHits();
//            Cardinality agg = srp.getAggregations().get(statsType);
//            long value = agg.getValue();
            
//            CountResponse crp = getCountResponse(index, type, statsDate, province, statsType, "uid", statsDateType);
//            long cnt = crp.getCount();

            logger.info(String.format("%s get SearchResponse hits total %d and cost %d ms", province, cnt,
                    System.currentTimeMillis() - startCr));
            
//            System.out.println(String.format("%s get SearchResponse hits total %d , "
//                    + "distinct uid %d and cost %d ms ",
//                province, 
//                cnt,
//                value,
//                System.currentTimeMillis() - startCr));
            
            distributions.put(province, cnt);
        }
        MySqLImporter.distinctDomainStats(envFlag, statsType, statsDate, type, distributions);
        
        closeESClient(); 
    }
}

