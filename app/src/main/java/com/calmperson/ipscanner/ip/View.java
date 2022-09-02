package com.calmperson.ipscanner.ip;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.calmperson.ipscanner.Contract;
import com.calmperson.ipscanner.R;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.chip.Chip;

public class View extends AppCompatActivity implements Contract.View {
    private Contract.Presenter presenter;
    public static Context context;

    private final Handler handler = new Handler();

    private StringBuilder log;

    private EditText first;
    private EditText second;
    private EditText third;
    private EditText fourth;
    private EditText endFirst;
    private EditText endSecond;
    private EditText endThird;
    private EditText endFourth;
    private EditText timeout;

    private Button startButton;
    private Button stopButton;
    private Button openPortManagerBottom;
    private Button openConnectionLog;
    private Button addPortButton;

    private Switch useHttpsSwitch;

    private ProgressBar progressBar;

    private LinearLayout tableContainer;
    private LinearLayout portManagerView;
    private LinearLayout connectionLogView;

    private AlertDialog portManager;
    private AlertDialog connectionLog;

    private FlexboxLayout ports;

    private final Runnable tableUpdater = new Runnable() {
        @Override
        public void run() {
            fillTable();
            handler.postDelayed(this, 5000);
        }
    };

    private final Runnable logUpdater = new Runnable() {
        @Override
        public void run() {
            log = new StringBuilder();
            for (String record : presenter.getLog()) {
                log.append(record).append("\n");
                log.append("-------------------------------").append("\n");
            }
            handler.postDelayed(logUpdater, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        presenter = new Presenter(this, new Model());

        first = findViewById(R.id.first);
        second = findViewById(R.id.second);
        third = findViewById(R.id.third);
        fourth = findViewById(R.id.fourth);
        endFirst = findViewById(R.id.end_first);
        endSecond = findViewById(R.id.end_second);
        endThird = findViewById(R.id.end_third);
        endFourth = findViewById(R.id.end_fourth);

        useHttpsSwitch = findViewById(R.id.https_on);
        timeout = findViewById(R.id.timeout);

        startButton = findViewById(R.id.scan);
        stopButton = findViewById(R.id.stop);
        openPortManagerBottom = findViewById(R.id.port_manager_button);
        openConnectionLog = findViewById(R.id.open_connection_log_button);

        progressBar = findViewById(R.id.progress_bar);

        tableContainer = findViewById(R.id.table_container);

        portManagerView = (LinearLayout) getLayoutInflater().inflate(R.layout.port_manager_view, null);
        connectionLogView = (LinearLayout) getLayoutInflater().inflate(R.layout.connection_log_view, null);

        portManager = new AlertDialog.Builder(this)
                .setView(portManagerView)
                .setPositiveButton(R.string.back, null)
                .create();
        connectionLog = new AlertDialog.Builder(this)
                .setView(connectionLogView)
                .setPositiveButton(R.string.back, null)
                .create();

        ports = portManagerView.findViewById(R.id.ports);
        addPortButton = portManagerView.findViewById(R.id.add_port);

        startButton.setOnClickListener(v -> {
            int milliseconds = timeout.getText().length() == 0 ? 300 : Integer.parseInt(timeout.getText().toString());
            boolean didSetPorts = presenter.setPorts(getPortsFromChipsInFlexboxLayout(ports));
            boolean didSetTimeout = presenter.setTimeout(milliseconds);
            boolean didSetRange = presenter.setRange(
                    Integer.parseInt(first.getText().toString()),
                    Integer.parseInt(second.getText().toString()),
                    Integer.parseInt(third.getText().toString()),
                    Integer.parseInt(fourth.getText().toString()),
                    Integer.parseInt(endFirst.getText().toString()),
                    Integer.parseInt(endSecond.getText().toString()),
                    Integer.parseInt(endThird.getText().toString()),
                    Integer.parseInt(endFourth.getText().toString())
            );
            if (didSetRange && didSetPorts && didSetTimeout) {
                presenter.useHttps(useHttpsSwitch.isChecked());
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
                progressBar.setVisibility(android.view.View.VISIBLE);
                presenter.startScanning();
                handler.postDelayed(tableUpdater, 3000);
                handler.postDelayed(logUpdater, 1000);
            }
        });

        stopButton.setOnClickListener(v -> {
            startButton.setEnabled(true);
            progressBar.setVisibility(android.view.View.GONE);
            presenter.stopScanning();
            handler.removeCallbacks(tableUpdater);
            handler.removeCallbacks(logUpdater);
        });

        openPortManagerBottom.setOnClickListener(v -> {
            portManager.show();
        });

        addPortButton.setOnClickListener(v -> {
            Chip chip = new Chip(this);
            chip.setOnClickListener(l -> ports.removeView(chip));
            String port = ((EditText) portManager.findViewById(R.id.port)).getText().toString();
            chip.setText(port.length() == 0 ? this.getResources().getString(R.string.default_port) : port);
            ports.addView(chip);
        });

        openConnectionLog.setOnClickListener(v -> showLog());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(tableUpdater);
        presenter.stopScanning();
    }

    @Override
    public void invalidInputDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LinearLayout invalidInputDialog = (LinearLayout) getLayoutInflater().inflate(R.layout.ivalid_input_dialog, null);
        ((TextView) invalidInputDialog.findViewById(R.id.message)).setText(msg);
        builder.setTitle("Invalid range!")
                .setView(invalidInputDialog)
                .setPositiveButton(R.string.OK, null)
                .show();
    }

    private void showLog() {
        TextView textView = connectionLogView.findViewById(R.id.log);
        textView.setText(log.toString());
        System.out.println(textView.getText().toString());
        connectionLog.setView(connectionLogView);
        connectionLog.show();
    }

    private void fillTable() {
        String[] ips = presenter.getIps();
        if (ips.length != 0) {
            tableContainer.removeAllViews();
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            TableLayout ipList = new TableLayout(this);
            for (String ip : ips) {
                TableRow row = (TableRow) getLayoutInflater().inflate(R.layout.table_row, null);
                TextView textView = row.findViewById(R.id.row_value);
                textView.setText(ip);
                textView.setOnClickListener(v -> {
                    WebView wb = (WebView) getLayoutInflater().inflate(R.layout.web_view, null);
                    wb.setWebViewClient(new WebViewClient());
                    wb.loadUrl(ip);
                    dialogBuilder
                            .setTitle(R.string.safe_preview)
                            .setView(wb)
                            .setNegativeButton(R.string.back, null)
                            .setPositiveButton(R.string.open_in_browser, (l, i) -> openPageInBrowser(ip))
                            .show();
                });
                ipList.addView(row);
            }
            tableContainer.addView(ipList);
        }
    }

    private void openPageInBrowser(String uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(uri));
        startActivity(intent);
    }

    private int[] getPortsFromChipsInFlexboxLayout(FlexboxLayout layout) {
        int size = layout.getChildCount();
        int[] ports = new int[size];
        for (int i = 0; i < size; i++) {
            ports[i] = Integer.parseInt(((Chip) layout.getChildAt(i)).getText().toString());
        }
        return ports;
    }
}
