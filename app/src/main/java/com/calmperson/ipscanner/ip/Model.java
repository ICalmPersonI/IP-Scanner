package com.calmperson.ipscanner.ip;

import com.calmperson.ipscanner.Contract;
import com.calmperson.ipscanner.HttpClient;

public class Model implements Contract.Model {

    private int startFirst;
    private int startSecond;
    private int startThird;
    private int startFourth;

    private int endFirst;
    private int endSecond;
    private int endThird;
    private int endFourth;

    private int first;
    private int second;
    private int third;
    private int fourth;

    private int[] ports;
    private int timeout;

    private String protocol;

    private Thread scannerThread;
    private final Scanner scanner;

    Model() {
        this.startFirst = 0;
        this.startSecond = 0;
        this.startThird = 0;
        this.startFourth = 0;

        this.endFirst = 1;
        this.endSecond = 1;
        this.endThird = 1;
        this.endFourth = 1;

        this.first = startFirst;
        this.second = startSecond;
        this.third = startThird;
        this.fourth = startFourth;

        this.ports = new int[0];
        this.timeout = 100;

        this.scanner = new Scanner(this);
        this.protocol = HttpClient.HTTP;
    }

    @Override
    public void setRange(int startFirst, int startSecond, int startThird, int startFourth,
                         int endFirst, int endSecond, int endThird, int endFourth) {
        this.startFirst = startFirst;
        this.startSecond = startSecond;
        this.startThird = startThird;
        this.startFourth = startFourth;

        this.endFirst = endFirst;
        this.endSecond = endSecond;
        this.endThird = endThird;
        this.endFourth = endFourth;

        this.first = startFirst;
        this.second = startSecond;
        this.third = startThird;
        this.fourth = startFourth;
    }

    @Override
    public void setPorts(int[] ports) {
        this.ports = ports;
    }

    @Override
    public void setTimeout(int milliseconds) {
        this.timeout = milliseconds;
    }

    @Override
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @Override
    public void startScanning() {
        try {
            scannerThread = new Thread(scanner);
            scannerThread.start();
        } catch(RuntimeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopScanning() {
        this.first = startFirst;
        this.second = startSecond;
        this.third = startThird;
        this.fourth = startFourth;

        while (true) {
            if (scannerThread == null) break;
            if (!scannerThread.isAlive()) break;
            try {
                scannerThread.interrupt();
            } catch(RuntimeException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    void next() {
        if (fourth <= endFourth) {
            fourth++;
        }
        if (fourth > endFourth) {
            fourth = startFourth;
            third++;
        }
        if (third > endThird) {
            third = startThird;
            second++;
        }
        if (second > endSecond) {
            second = startSecond;
            first++;
        }
    }

    boolean hasNext() { // fix
        return first <= endFirst;
    }

    String buildUrl(int port) {
        return String.format("%s://%s.%s.%s.%s:%s", protocol, first, second, third, fourth, port);
    }

    @Override
    public String[] getIps() {
        return scanner.getIps().toArray(new String[0]);
    }

    @Override
    public String[] getLog() {
        return scanner.getLog().toArray(new String[0]);
    }

    public int getStartFirst() {
        return startFirst;
    }

    public int getStartSecond() {
        return startSecond;
    }

    public int getStartThird() {
        return startThird;
    }

    public int getStartFourth() {
        return startFourth;
    }

    public int getFirst() {
        return first;
    }

    public int getSecond() {
        return second;
    }

    public int getThird() {
        return third;
    }

    public int getFourth() {
        return fourth;
    }

    public int getEndFirst() {
        return endFirst;
    }

    public int getEndSecond() {
        return endSecond;
    }

    public int getEndThird() {
        return endThird;
    }

    public int getEndFourth() {
        return endFourth;
    }

    public int getTimeout() {
        return timeout;
    }

    public int[] getPorts() {
        return ports;
    }

    public String getProtocol() {
        return protocol;
    }

    public Thread getScannerThread() {
        return scannerThread;
    }
}
