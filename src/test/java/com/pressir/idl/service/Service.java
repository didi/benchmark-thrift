package com.pressir.idl.service;

import com.pressir.idl.Info;
import com.pressir.idl.Shop;
import com.pressir.idl.ShopStatus;
import com.pressir.idl.Soda;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.THsHaServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransportException;

import java.util.Collections;
import java.util.List;

/**
 * @ClassName Service
 * @Description TODO
 * @Author didi
 * @Date 2019-09-09 15:17
 */
public class Service {

    public static void main(String[] args) throws TTransportException {
        run();
    }
    public static void run() throws TTransportException {
        TNonblockingServerSocket tNonblockingServerTransport = new TNonblockingServerSocket(8090);
        Impl impl = new Impl();
        Soda.Processor processor = new Soda.Processor<>(impl);
        TProtocolFactory factory = TCompactProtocol::new;
        THsHaServer.Args args1 = new THsHaServer.Args(tNonblockingServerTransport);
        args1.protocolFactory(factory);
        args1.processor(processor);
        THsHaServer tHsHaServer = new THsHaServer(args1);
        System.out.println("server start");
        tHsHaServer.serve();
    }

    public static class Impl implements Soda.Iface {
        @Override
        public ShopStatus getShopStatus(String shopId) {

            Shop shop = new Shop();
            shop.setShopId(shopId);
            shop.setStatus(ShopStatus.OPEN);
            return shop.getStatus();
        }

        @Override
        public List<Info> getInfos(Shop shop) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (shop.getStatus() == ShopStatus.CLOSE) {
                return null;
            }
            Info info = new Info();
            info.setName("info");
            info.setPrice(64L);
            info.setShopId(shop.shopId);
            return Collections.singletonList(info);
        }
    }
}
