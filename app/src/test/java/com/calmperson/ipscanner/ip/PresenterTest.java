package com.calmperson.ipscanner.ip;

import com.calmperson.ipscanner.Contract;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PresenterTest {

    private Contract.Presenter presenter;
    private Contract.View view;

    @Before
    public void initModel() {
        this.presenter = new Presenter(view, new Model());
    }

    @Test
    public void setRange_success() {
        Assert.assertEquals(
                Boolean.TRUE,
                presenter.setRange(
                        200, 200, 200, 200,
                        255, 255, 255, 255
                )
        );
    }

    @Test
    public void setRange_fail() {
        try {
            presenter.setRange(
                    256, 256, 256, 256,
                    300, 300, 300, 300
            );
            Assert.assertFalse(Boolean.FALSE);
        } catch (NullPointerException e) {
            Assert.assertTrue(Boolean.TRUE);
        }
    }

    @Test
    public void setPorts_success() {
        Assert.assertEquals(Boolean.TRUE, presenter.setPorts(new int[] {80, 5000, 5050}));
    }

    @Test
    public void setPorts_fail() {
        try {
            Assert.assertEquals(Boolean.FALSE, presenter.setPorts(new int[] {65537, 70000, 9999}));
            Assert.assertFalse(Boolean.FALSE);
        } catch (NullPointerException e) {
            Assert.assertTrue(Boolean.TRUE);
        }
    }

    @Test
    public void setTimeout_success() {
        Assert.assertEquals(Boolean.TRUE, presenter.setTimeout(300));
    }

    @Test
    public void setTimeout_fail() {
        try {
            Assert.assertEquals(Boolean.FALSE, presenter.setTimeout(11000));
            Assert.assertFalse(Boolean.FALSE);
        } catch (NullPointerException e) {
            Assert.assertTrue(Boolean.TRUE);
        }
    }
}