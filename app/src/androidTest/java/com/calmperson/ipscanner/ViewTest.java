package com.calmperson.ipscanner;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SdkSuppress;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ViewTest {

    private UiDevice device;
    private Context context;

    @Before
    public void startApp() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        device.pressHome();

        context = ApplicationProvider.getApplicationContext();
        String appPackageName = context.getPackageName();
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(appPackageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        device.wait(Until.hasObject(By.pkg(appPackageName).depth(0)), 5000);
    }

    @Test
    public void test1_allValidInput() throws UiObjectNotFoundException {
        fillInputFields(
                "200", "200", "200", "200",
                "230", "230", "230", "230",
                "300", "5000", "5050"
        );

        device.findObject(getUiSelectorById("scan")).click();
        Assert.assertEquals(
                Boolean.TRUE,
                device.hasObject(By.res("com.calmperson.ipexplorer", "progress_bar"))
        );
        device.findObject(getUiSelectorById("stop")).click();
    }

    @Test
    public void test2_invalidIpRange() throws UiObjectNotFoundException {
        fillInputFields(
                "256", "256", "256", "256",
                "300", "300", "300", "300",
                "300", "80", "5050"
        );
        device.findObject(getUiSelectorById("scan")).click();

        Assert.assertEquals(
                context.getResources().getString(R.string.invalid_ip_range),
                device.findObject(getUiSelectorById("message")).getText()
        );
        device.findObject(new UiSelector().text("OK")).click();

        Assert.assertEquals(
                Boolean.FALSE,
                device.hasObject(By.res("com.calmperson.ipexplorer", "progress_bar"))
        );
    }

    @Test
    public void test3_invalidTimeout() throws UiObjectNotFoundException {
        fillInputFields(
                "200", "200", "200", "200",
                "230", "230", "230", "230",
                "11000", "5000", "5050"
        );
        device.findObject(getUiSelectorById("scan")).click();

        Assert.assertEquals(
                context.getResources().getString(R.string.invalid_timeout),
                device.findObject(getUiSelectorById("message")).getText()
        );
        device.findObject(new UiSelector().text("OK")).click();

        Assert.assertEquals(
                Boolean.FALSE,
                device.hasObject(By.res("com.calmperson.ipexplorer", "progress_bar"))
        );
    }

    @Test
    public void test4_invalidPorts() throws UiObjectNotFoundException {
        fillInputFields(
                "200", "200", "200", "200",
                "230", "230", "230", "230",
                "300", "70000", "65540"
        );
        device.findObject(getUiSelectorById("scan")).click();

        Assert.assertEquals(
                context.getResources().getString(R.string.invalid_port),
                device.findObject(getUiSelectorById("message")).getText()
        );
        device.findObject(new UiSelector().text("OK")).click();

        Assert.assertEquals(
                Boolean.FALSE,
                device.hasObject(By.res("com.calmperson.ipexplorer", "progress_bar"))
        );
    }


    @Test
    public void test5_invalidAllInputFields() throws UiObjectNotFoundException {
        fillInputFields(
                "256", "256", "256", "256",
                "300", "300", "300", "300",
                "11000", "70000", "65540"
        );
        device.findObject(getUiSelectorById("scan")).click();

        Assert.assertEquals(
                context.getResources().getString(R.string.invalid_ip_range),
                device.findObject(getUiSelectorById("message")).getText()
        );
        device.findObject(new UiSelector().text("OK")).click();
        Assert.assertEquals(
                context.getResources().getString(R.string.invalid_timeout),
                device.findObject(getUiSelectorById("message")).getText()
        );
        device.findObject(new UiSelector().text("OK")).click();
        Assert.assertEquals(
                context.getResources().getString(R.string.invalid_port),
                device.findObject(getUiSelectorById("message")).getText()
        );
        device.findObject(new UiSelector().text("OK")).click();

        Assert.assertEquals(
                Boolean.FALSE,
                device.hasObject(By.res("com.calmperson.ipexplorer", "progress_bar"))
        );
    }

    private void fillInputFields(
            String first, String second, String third, String fourth,
            String endFirst, String endSecond, String endThird, String endFourth,
            String timeout, String firstPort, String secondPort
    ) throws UiObjectNotFoundException {
        device.findObject(getUiSelectorById("first")).setText(first);
        device.findObject(getUiSelectorById("second")).setText(second);
        device.findObject(getUiSelectorById("third")).setText(third);
        device.findObject(getUiSelectorById("fourth")).setText(fourth);
        device.findObject(getUiSelectorById("end_first")).setText(endFirst);
        device.findObject(getUiSelectorById("end_second")).setText(endSecond);
        device.findObject(getUiSelectorById("end_third")).setText(endThird);
        device.findObject(getUiSelectorById("end_fourth")).setText(endFourth);

        device.findObject(getUiSelectorById("timeout")).setText(timeout);
        device.findObject(getUiSelectorById("port_manager_button")).click();
        device.findObject(getUiSelectorById("port")).setText(firstPort);
        device.findObject(getUiSelectorById("add_port")).click();
        device.findObject(getUiSelectorById("port")).setText(secondPort);
        device.findObject(getUiSelectorById("add_port")).click();
        device.findObject(new UiSelector().text("BACK")).click();
    }

    private UiSelector getUiSelectorById(String id) {
        return new UiSelector().resourceId(String.format("com.calmperson.ipexplorer:id/%s", id));
    }
}