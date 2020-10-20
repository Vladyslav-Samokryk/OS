package com.company;

import com.company.Client;
import com.company.Server;
import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        Process server;
        Client client;
        server = Server.start();
        client = Client.start();
        String resp2 = client.sendMessage("166786438");
        System.out.println(resp2);

}
}