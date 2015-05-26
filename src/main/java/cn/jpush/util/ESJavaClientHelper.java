/**
 * Project Name:offline File Name:ESClientUtil.java Package Name:cn.jpush.util
 * Date:2014年12月19日下午4:49:49 Copyright (c) 2014, wufeng@jpush.cn All Rights Reserved.
 * 
 */

package cn.jpush.util;

import static cn.jpush.util.StatConstantUtil.PROVINCE;
import static cn.jpush.util.StatConstantUtil.REG_DATE;

import org.apache.hadoop.hive.metastore.parser.ExpressionTree.FilterBuilder;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import static org.elasticsearch.index.query.FilterBuilders.*;

import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.MetricsAggregationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.jpush.stat.offline.v2.stats.DistributionStats;

/**
 * ClassName:ESClientUtil <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2014年12月19日 下午4:49:49 <br/>
 * 
 * @author wufeng
 * @version
 * @since JDK 1.7
 * @see
 */
public class ESJavaClientHelper {

    private static Logger logger = LoggerFactory.getLogger(ESJavaClientHelper.class);

    public static TransportClient client = null;

    @SuppressWarnings("resource")
    public static TransportClient getTransportClient() {
        if (null == client) {

            String esHost = SystemConfig.getProperty("es.cluster.ip");
            int esPort = SystemConfig.getIntProperty("es.cluster.tcp.port", 9300);
            Settings settings =
                    ImmutableSettings.settingsBuilder().put("client.transport.sniff", true).build();

            client = new TransportClient(settings)
                            .addTransportAddress(new InetSocketTransportAddress(esHost, esPort));
        }
        return client;
    }

    public static SearchRequestBuilder getSearchRequestBuilder(String indexName, String typeName,
            String statsDate, String provinceCode, String cardinalityName, String fieldName,
            String esColumnName) {
        QueryBuilder qb = QueryBuilders.termQuery( esColumnName, statsDate);
//        AndFilterBuilder fb = andFilter(FilterBuilders.termFilter(PROVINCE, provinceCode),
//            FilterBuilders.termFilter(esColumnName, statsDate)).cache(true);
        
        SearchRequestBuilder srb =
                getTransportClient().prepareSearch(indexName).setTypes(typeName)
                        .setSearchType(SearchType.COUNT).setQuery(qb)
                        .setPostFilter(FilterBuilders.termFilter(PROVINCE, provinceCode));
        
        
//        SearchRequestBuilder srb = getTransportClient().prepareSearch(indexName).setTypes(typeName)
//              .setSearchType(SearchType.COUNT).setQuery(fb.toString());
//        logger.info("the search request is" + srb.toString());
        return srb;
    }

    public static CountResponse getCountResponse(String indexName, String typeName,
            String statsDate, String provinceCode, String cardinalityName, String fieldName,
            String esColumnName) {
        QueryBuilder qb = QueryBuilders.termQuery(PROVINCE, provinceCode);
        CountRequestBuilder crb = getTransportClient().prepareCount(indexName)
                        .setTypes(typeName)
                        .setQuery(QueryBuilders.filteredQuery(qb,
                         FilterBuilders.termFilter(esColumnName, statsDate)));
        logger.info("the count request is" + crb.toString());
        return  crb.execute().actionGet();
    }

    public static SearchResponse getSearchResponse(String indexName, String typeName,
            String statsDate, String provinceCode, String cardinalityName, String fieldName,
            String esColumnName) {
        return getSearchRequestBuilder(indexName, typeName, statsDate, provinceCode,
                cardinalityName, fieldName, esColumnName).execute().actionGet();
    }

    @SuppressWarnings("rawtypes")
    public static MetricsAggregationBuilder getAggregationBuilder(String cardinalityName,
            String fieldName) {
        return AggregationBuilders.cardinality(cardinalityName).field(fieldName);
    }

    public static void closeESClient() {
        if (null != client) {
            client.close();
        }
    }
}
