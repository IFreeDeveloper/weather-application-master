package mg.studio.weatherappdesign;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.*;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    Context context;
    boolean flag = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        int state = getConnState();
        if(state==0){
            new DownloadUpdate().execute("");
        }
    }

    public void btnClick(View view) {
        int state = getConnState();
        if(state==0){
            new DownloadUpdate().execute("");
            if(flag) {
                Toast.makeText(MainActivity.this, "The data of the weather is updated", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private int getConnState(){
        ConnectivityManager connectivityManager = (ConnectivityManager)this.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null){
            if(networkInfo.isConnected()){
                return 0;
            }
            else {
                Toast.makeText(this,"Network connection failed",Toast.LENGTH_SHORT).show();
                return 1;
            }
        }
        else{
            int mode = 0;
            try {
                mode = Settings.Global.getInt(getContentResolver(), Settings.Global.AIRPLANE_MODE_ON);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            if (mode == 1) {
                Toast.makeText(this, "Please turn off flight mode and try again", Toast.LENGTH_SHORT).show();
                return 2;
            }
            else{
                Toast.makeText(this,"Network connection failed",Toast.LENGTH_SHORT).show();
                return 1;
            }
        }
    }

    private class DownloadUpdate extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            String dailyUrl = new GetWeatherURL("auto_ip","en","").getDailyURL();
            HttpURLConnection urlConnection = null;
            BufferedReader reader;

            try {
                URL daily = new URL(dailyUrl);
                // Create the request to get the information from the server, and open the connection
                urlConnection = (HttpURLConnection) daily.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(1000);
                urlConnection.setReadTimeout(1000);
                urlConnection.connect();
                int code = urlConnection.getResponseCode();
                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Mainly needed for debugging
                    Log.d("TAG", line);
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                //The temperature
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String temperature) {
            if(temperature==null){
                Toast.makeText(context,"Unable to get access to the target Server",Toast.LENGTH_SHORT).show();
                flag = false;
                return;
            }
            Gson gson = new Gson();
            WeatherBean wb = gson.fromJson(temperature,WeatherBean.class);

            //Set Location
            String location = wb.getHeWeather6().get(0).getBasic().getLocation();
            ((TextView)findViewById(R.id.tv_location)).setText(location);

            //Set WeekDays
            String[] weekDays = { "Sun", "Mon", "Tues", "Wed", "Thur", "Fri", "Sat" };
            String date = wb.getHeWeather6().get(0).getDaily_forecast().get(0).getDate();
            int d = dateToWeek(date);
            ((TextView) findViewById(R.id.day1)).setText(weekDays[d]);
            ((TextView) findViewById(R.id.day2)).setText(weekDays[(d+1)%7]);
            ((TextView) findViewById(R.id.day3)).setText(weekDays[(d+2)%7]);
            ((TextView) findViewById(R.id.day4)).setText(weekDays[(d+3)%7]);
            ((TextView) findViewById(R.id.day5)).setText(weekDays[(d+4)%7]);

            //Set Date
            ((TextView) findViewById(R.id.tv_date)).setText(date);

            //Set Icon
            Context ctx=getBaseContext();
            String icon = wb.getHeWeather6().get(0).getDaily_forecast().get(0).getCond_code_d();
            int resId = getResources().getIdentifier("w"+icon, "drawable" , ctx.getPackageName());
            ((ImageView)findViewById(R.id.img_weather_condition1)).setImageResource(resId);


            icon = wb.getHeWeather6().get(0).getDaily_forecast().get(1).getCond_code_d();
            resId = getResources().getIdentifier("w"+icon, "drawable" , ctx.getPackageName());
            ((ImageView)findViewById(R.id.img_weather_condition2)).setImageResource(resId);

            icon = wb.getHeWeather6().get(0).getDaily_forecast().get(2).getCond_code_d();
            resId = getResources().getIdentifier("w"+icon, "drawable" , ctx.getPackageName());
            ((ImageView)findViewById(R.id.img_weather_condition2)).setImageResource(resId);

            icon = wb.getHeWeather6().get(0).getDaily_forecast().get(3).getCond_code_d();
            resId = getResources().getIdentifier("w"+icon, "drawable" , ctx.getPackageName());
            ((ImageView)findViewById(R.id.img_weather_condition3)).setImageResource(resId);

            icon = wb.getHeWeather6().get(0).getDaily_forecast().get(4).getCond_code_d();
            resId = getResources().getIdentifier("w"+icon, "drawable" , ctx.getPackageName());
            ((ImageView)findViewById(R.id.img_weather_condition4)).setImageResource(resId);

            //Update the temperature displayed
            String max = wb.getHeWeather6().get(0).getDaily_forecast().get(0).getTmp_max();
            String min = wb.getHeWeather6().get(0).getDaily_forecast().get(0).getTmp_min();
            ((TextView) findViewById(R.id.temperature_of_the_day1)).setText(max);

            max = wb.getHeWeather6().get(0).getDaily_forecast().get(1).getTmp_max();
            min = wb.getHeWeather6().get(0).getDaily_forecast().get(1).getTmp_min();
            ((TextView) findViewById(R.id.temperature_of_the_day2)).setText(min+"-"+max);

            max = wb.getHeWeather6().get(0).getDaily_forecast().get(2).getTmp_max();
            min = wb.getHeWeather6().get(0).getDaily_forecast().get(2).getTmp_min();
            ((TextView) findViewById(R.id.temperature_of_the_day3)).setText(min+"-"+max);

            max = wb.getHeWeather6().get(0).getDaily_forecast().get(3).getTmp_max();
            min = wb.getHeWeather6().get(0).getDaily_forecast().get(3).getTmp_min();
            ((TextView) findViewById(R.id.temperature_of_the_day4)).setText(min+"-"+max);

            max = wb.getHeWeather6().get(0).getDaily_forecast().get(4).getTmp_max();
            min = wb.getHeWeather6().get(0).getDaily_forecast().get(4).getTmp_min();
            ((TextView) findViewById(R.id.temperature_of_the_day5)).setText(min+"-"+max);

            flag = true;
        }

        public int dateToWeek(String datetime) {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
            String[] weekDays = { "Sun", "Mon", "Tues", "Wed", "Thur", "Fri", "Sat" };
            Calendar cal = Calendar.getInstance(); // 获得一个日历
            Date datet = null;
            try {
                datet = f.parse(datetime);
                cal.setTime(datet);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
            int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
            if (w < 0)
                w = 0;
            return w;
        }
    }
}
