package com.gw.xb3ws;

import com.gw.xb3ws.server.NettyWebSocketServer;
import com.gw.xb3ws.utils.SpringHelper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

@SpringBootApplication(scanBasePackages = {"com.gw"})
public class Xb3wsApplication {

    public static void main(String[] args) {
        SpringApplication.run(Xb3wsApplication.class, args);
        NettyWebSocketServer server = SpringHelper.getBean(NettyWebSocketServer.class);
        try {
            server.start();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (SSLException e) {
            e.printStackTrace();
        }
    }

}
