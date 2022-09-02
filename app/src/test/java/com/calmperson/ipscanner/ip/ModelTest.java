package com.calmperson.ipscanner.ip;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.regex.Pattern;

public class ModelTest {

    private final Pattern url = Pattern.compile("^https?://\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}:\\d{1,4}$");

    private Model model;

    @Before
    public void initModel() {
        this.model = new Model();
        model.setRange(
                46, 164, 154, 120,
                255, 255, 255, 255
        );
    }

    @Test
    public void startAndStopScanning() {
        model.startScanning();
        assertEquals(Boolean.TRUE, model.getScannerThread().isAlive());
        model.stopScanning();
        assertEquals(Boolean.FALSE, model.getScannerThread().isAlive());
    }

    @Test
    public void next() {
        int loop = 10;
        int[] except = new int[] { model.getFirst(), model.getSecond(), model.getThird(), model.getFourth() + loop };
        for (int i = 0; i < loop; i++) {
            model.next();
        }
        int[] actual = new int[] { model.getFirst(), model.getSecond(), model.getThird(), model.getFourth()};
        assertArrayEquals(except, actual);
    }

    @Test
    public void hasNext() {
        model.setRange(
                255, 255,
                255, 255,
                255, 255,
                255, 255
        );
        assertEquals(Boolean.TRUE, model.hasNext());
        model.next();
        assertEquals(Boolean.FALSE, model.hasNext());
    }

    @Test
    public void buildUrl() {
        model.setProtocol("http");
        System.out.println(model.buildUrl(80));
        assertEquals(Boolean.TRUE, url.matcher(model.buildUrl(80)).matches());
    }
}