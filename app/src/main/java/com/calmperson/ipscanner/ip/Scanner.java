package com.calmperson.ipscanner.ip;

import com.calmperson.ipscanner.HttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CancellationException;

public class Scanner implements Runnable {

    private final Model model;
    private final ArrayList<String> ips;
    private final ArrayList<String> log;

    Scanner(Model model) {
        this.model = model;
        this.ips = new ArrayList<>();
        this.log = new ArrayList<>();
    }

    @Override
    public void run() {
        HttpClient client = new HttpClient(model.getTimeout());
        Thread thisThread = Thread.currentThread();
        int sleep = model.getTimeout();
        boolean stop = false;
        while (!stop && model.hasNext()) {
            for (int port : model.getPorts()) {
                if (thisThread.isInterrupted()) {
                    stop = true;
                    break;
                }
                if (log.size() > 100) log.clear();
                String url = model.buildUrl(port);
                try {
                    int statusCode = client.connect(url);
                    if (statusCode == 200) ips.add(url);
                    log.add(String.format("Success: %s, status code %s", url, statusCode));
                } catch (IOException | CancellationException e) {
                    //e.printStackTrace();
                    log.add(String.format("Exception: %s", e.getMessage()));
                }
                try {
                    thisThread.sleep(sleep);
                } catch (InterruptedException e) {
                }
            }
            model.next();
        }
        client.close();
    }

    public ArrayList<String> getIps() {
        return ips;
    }

    public ArrayList<String> getLog() {
        return log;
    }
}
