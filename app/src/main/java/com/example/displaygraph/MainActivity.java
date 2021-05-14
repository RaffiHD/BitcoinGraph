package com.example.displaygraph;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String BPI_ENDPOINT_CURRENTPRICE = "https://api.coindesk.com/v1/bpi/currentprice.json";
    public static final String BPI_ENDPOINT_YESTERDAY = "https://api.coindesk.com/v1/bpi/historical/close.json?start=$YEAR-$MONTH-$DAY&end=$YEAR-$MONTH-$DAY";
    private OkHttpClient okHttpClient = new OkHttpClient();
    private TextView txtNow;
    private TextView txtYesterday;
    private TextView txtBuySell;
    private TextView txtGestrigerPreis;
    private TextView txtAktuellerPreis;
    private ImageView ArrowImage;
    private Timer myTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtNow = (TextView) findViewById(R.id.txtNow);
        txtYesterday = (TextView) findViewById(R.id.txtYesterday);
        txtBuySell = (TextView) findViewById(R.id.txtBuySell);
        txtGestrigerPreis = (TextView) findViewById(R.id.txtGestrigerPreis);
        txtAktuellerPreis = (TextView) findViewById(R.id.txtAktuellerPreis);
        ArrowImage = (ImageView) findViewById(R.id.arrowImage);


    }


    public void imageClick(View view) {

        load();

    }

    private void load() {

            getCurrentPrice();
            getYesterdayPrice();


    }

    private void getCurrentPrice(){
        Request request = new Request.Builder()
                .url(BPI_ENDPOINT_CURRENTPRICE)
                .build();


        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(MainActivity.this, "Fehler beim aktuellen BPI laden : "
                        + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response)
                    throws IOException {
                final String body = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        parseCurrentBpiResponse(body);
                    }
                });
            }
        });
    }

    private void getYesterdayPrice(){

        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, -1);
        String Year = String.valueOf(cal.get(Calendar.YEAR));
        String Month = ("0" + String.valueOf(cal.get(Calendar.MONTH)+1)).replace("00", "0");
        //String Day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        String Day = "12";

        Request request = new Request.Builder()
                .url(BPI_ENDPOINT_YESTERDAY.replace("$YEAR", Year).replace("$MONTH", Month).replace("$DAY", Day))
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(MainActivity.this, "Fehler beim gestrigen BPI laden : "
                        + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response)
                    throws IOException {
                final String body = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        parseYesterdayBpiResponse(body);
                    }

                });
            }
        });


    }

    private void parseCurrentBpiResponse(String body) {
        try {
            StringBuilder builder = new StringBuilder();

            JSONObject jsonObject = new JSONObject(body);

            JSONObject bpiObject = jsonObject.getJSONObject("bpi");
            JSONObject usdObject = bpiObject.getJSONObject("USD");
            builder.append(usdObject.getString("rate_float")).append("$");

            txtNow.setText(builder.toString());
            txtNow.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Fehler : "
                    + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void parseYesterdayBpiResponse(String body){

            Date date = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DATE, -1);
            String Year = String.valueOf(cal.get(Calendar.YEAR));
            String Month = String.valueOf(cal.get(Calendar.MONTH));
            String Day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));

            StringBuilder builderPrice = new StringBuilder();

        try {

            JSONObject jsonObject = new JSONObject(body);

            JSONObject bpiObject = jsonObject.getJSONObject("bpi");

            String yesterday = builderPrice.append(bpiObject.getString( /*Year + "-" + Month + "-" + Day*/"2021-05-12")).toString() + "$";
            txtYesterday.setText(yesterday);
            txtYesterday.setVisibility(View.VISIBLE);


        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Fehler : "
                    + e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        try {

            txtAktuellerPreis.setVisibility(View.VISIBLE);
            txtGestrigerPreis.setVisibility(View.VISIBLE);

            String priceNowUSDText = txtNow.getText().toString();
            String priceYesterdayUSDText = txtYesterday.getText().toString();

            if (Float.parseFloat(priceNowUSDText.replace("$", "")) >= Float.parseFloat(priceYesterdayUSDText.replace("$", ""))) {

                ArrowImage.setBackgroundResource(R.drawable.greenarrowincrease);
                ArrowImage.setVisibility((View.VISIBLE));
                txtBuySell.setText("BUY");
                txtBuySell.setVisibility(View.VISIBLE);

            } else {

                ArrowImage.setBackgroundResource(R.drawable.redarrowdecrease);
                ArrowImage.setVisibility(View.VISIBLE);
                txtBuySell.setText("SELL");
                txtBuySell.setVisibility(View.VISIBLE);

            }

        }
        catch(Exception e){

        }

    }

}