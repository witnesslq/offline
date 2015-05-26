/**
 * Project Name:offline
 * File Name:StatisProcessor.java
 * Package Name:cn.jpush.stat.offline.processor
 * Date:2014年10月21日下午1:16:34
 * Copyright (c) 2014, wufeng@jpush.cn All Rights Reserved.
 *
*/

package cn.jpush.stat.offline.v1.processor;

import java.sql.SQLException;

/**
 * ClassName:StatisProcessor <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2014年10月21日 下午1:16:34 <br/>
 * @author   wufeng
 * @version  
 * @since    JDK 1.7
 * @see 	 
 */
public interface StatisProcessor {
    
    public void prepare();
    
    public void stats();
    
    public void clear() throws Exception, Throwable;
    
    public void run() throws Throwable;

}

