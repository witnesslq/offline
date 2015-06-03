package cn.jpush.thrift;

import java.util.Map;

import cn.jpush.thrift.service.StatQueryService;
import org.apache.log4j.Logger;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * Created by fengwu on 15/6/3.
 */
public class StatsDBQueryClient {

    private static final Logger LOG = Logger.getLogger(StatsDBQueryClient.class);

    private String host;

    private int port;

    private StatQueryService.Client clientProxy;

    public StatsDBQueryClient(String host, int port) {
        this(host,port,null);
    }

    public StatsDBQueryClient(String host, int port, StatQueryService.Client clientProxy) {
        this.host = host;
        this.port = port;
        this.clientProxy = clientProxy;
    }

    private void init() {
        try {
            TTransport transport = new TSocket(host, port);
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            clientProxy = new StatQueryService.Client(protocol);

        } catch (TTransportException e) {
            LOG.error("Open TTransport error cause by : " + e);
        }
    }

    public Map<Integer, Map<Integer, Double>> queryHourKPI(String code,
                                                           int begindate,
                                                           int enddate,
                                                           String platform) throws TException {
        if (clientProxy == null) {
            init();
        }
        return clientProxy.queryHourKPI(code, begindate, enddate, platform);
    }

    public Map<Integer, Double> queryDayKPI(String code,
                                            int begindate,
                                            int enddate,
                                            String platform) throws TException {
        if (clientProxy == null) {
            init();
        }
        return clientProxy.queryDayKPI(code, begindate, enddate, platform);
    }
}
