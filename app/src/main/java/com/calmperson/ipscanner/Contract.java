package com.calmperson.ipscanner;

public interface Contract {
    interface View {    //on
        void invalidInputDialog(String msg);
    }

    interface Model {
        void setRange(int startFirst, int startSecond, int startThird, int startFourth,
                         int endFirst, int endSecond, int endThird, int endFourth);
        void setPorts(int[] ports);
        void setTimeout(int milliseconds);
        void setProtocol(String protocol);
        String[] getIps();
        String[] getLog();
        void stopScanning();
        void startScanning();
    }

    interface Presenter { //do
        boolean setRange(int startFirst, int startSecond, int startThird, int startFourth,
                      int endFirst, int endSecond, int endThird, int endFourth);
        boolean setPorts(int[] ports);
        boolean setTimeout(int milliseconds);
        void useHttps(boolean isTrue);
        String[] getIps();
        String[] getLog();
        void stopScanning();
        void startScanning();
    }
}
