package com.calmperson.ipscanner.ip;

import android.content.Context;

import com.calmperson.ipscanner.Contract;
import com.calmperson.ipscanner.HttpClient;
import com.calmperson.ipscanner.R;

public class Presenter implements Contract.Presenter {

    private final Contract.View view;
    private final Contract.Model model;
    private final Context context;


    public Presenter(Contract.View view, Contract.Model model) {
        this.view = view;
        this.model = model;
        this.context = View.context;
    }

    @Override
    public void stopScanning() {
        model.stopScanning();
    }

    @Override
    public void startScanning() {
        model.startScanning();
    }

    @Override
    public boolean setRange(int startFirst, int startSecond, int startThird, int startFourth,
                         int endFirst, int endSecond, int endThird, int endFourth) {
        for (int octet : new int[] {startFirst, startSecond, startThird, startFourth,
                endFirst, endSecond, endThird, endFourth}) {
            if (octet < 0 || octet > 255) {
                view.invalidInputDialog(context.getResources().getString(R.string.invalid_ip_range));
                return false;
            }
        }
        model.setRange(startFirst, startSecond, startThird, startFourth,
                endFirst, endSecond, endThird, endFourth);
        return true;
    }

    @Override
    public boolean setPorts(int[] ports) {
        if (ports.length == 0) {
            view.invalidInputDialog(context.getResources().getString(R.string.invalid_port));
            return false;
        }
        for (int port : ports) {
            if (port < 0 || port > 65536) {
                view.invalidInputDialog(context.getResources().getString(R.string.invalid_port));
                return false;
            }
        }
        model.setPorts(ports);
        return true;
    }

    @Override
    public boolean setTimeout(int milliseconds) {
        if (milliseconds < 0 || milliseconds > 10000) {
            view.invalidInputDialog(context.getResources().getString(R.string.invalid_timeout));
            return false;
        }
        model.setTimeout(milliseconds);
        return true;
    }

    @Override
    public void useHttps(boolean isTrue) {
        String protocol = isTrue ? HttpClient.HTTPS : HttpClient.HTTP;
        model.setProtocol(protocol);
    }

    @Override
    public String[] getIps() {
        return model.getIps();
    }

    @Override
    public String[] getLog() {
        return model.getLog();
    }
}
