package pmf.novak101.practice3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    ExecutorService executor = Executors.newFixedThreadPool(4);
    Handler handler = new Handler(Looper.getMainLooper());

    TextView cityName;
    TextView resultWeather;
    JSONObject jsonObjectCondition;

    public void getWeatherInfo(View view){
        executor.execute(() -> {
            HttpURLConnection connection = null;

            try {
                jsonObjectCondition = null;
                URL url = new URL("https://api.weatherapi.com/v1/current.json?key=e52667a0dd69409d851215133210411&q="+cityName.getText()+"&aqi=no");
                connection = (HttpURLConnection) url.openConnection();
                InputStream input = connection.getInputStream();

                JSONObject jsonObject = (JSONObject) new JSONParser().parse(new InputStreamReader(input, "UTF-8"));
                if(jsonObject == null)
                    return;
                jsonObjectCondition  = (JSONObject) ((JSONObject) jsonObject.get("current")).get("condition");

                // Hides the keyboard after the search
                InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(cityName.getWindowToken(), 0);

            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e("MalformedURLException","Bad api url");
            } catch (IOException e) {
                Log.e("IOException","Error establishing the connection to api");
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
                Log.e("ParseException","Error parsing JSON");
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

            handler.post(() -> {
                if(jsonObjectCondition == null){
                    resultWeather.setText("No results.");
                    return;
                }
                resultWeather.setText((String) jsonObjectCondition.get("text"));
            });
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = findViewById(R.id.inputCityName);
        resultWeather = findViewById(R.id.textViewResult);
    }
}