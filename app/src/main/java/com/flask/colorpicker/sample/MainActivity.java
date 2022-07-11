package com.flask.colorpicker.sample;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorChangedListener;
import com.flask.colorpicker.OnColorSelectedListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {
    //private final String URL = "http://raspberryzero:5000/rgb";
    private final String URL1 = "http://192.168.1.100:5000/rgb"; //For Teltonika router
    private final String URL2 = "http://192.168.10.100:5000/rgb"; // For Trendnet router
    private final String URL3 = "http://192.168.100.100:5000/rgb"; // For 4G LTE router
    private final String URL4 = "http://192.168.31.57:5000/rgb"; // For DEV router
    private String colorStr = "#000000";
    private Button btn_ok;
    private Button btn_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ColorPickerView colorPickerView = findViewById(R.id.color_picker_view);
        btn_ok = findViewById(R.id.btn_ok);
        btn_cancel = findViewById(R.id.btn_cancel);

        colorPickerView.addOnColorChangedListener(new OnColorChangedListener() {
            @Override public void onColorChanged(int selectedColor) {
                // Handle on color change
                Log.d("ColorPicker", "onColorChanged: 0x" + Integer.toHexString(selectedColor));
            }
        });
        colorPickerView.addOnColorSelectedListener(new OnColorSelectedListener() {
            @Override
            public void onColorSelected(int selectedColor) {
                colorStr = Integer.toHexString(selectedColor).toUpperCase();
                Toast.makeText(
                        MainActivity.this,
                        "selectedColor: " + Integer.toHexString(selectedColor).toUpperCase(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    setColor(URL1, colorStr);
                    setColor(URL2, colorStr);
                    setColor(URL3, colorStr);
                    setColor(URL4, colorStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    setColor(URL1,"0x00000000");
                    setColor(URL2,"0x00000000");
                    setColor(URL3,"0x00000000");
                    setColor(URL4,"0x00000000");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setColor(String URL, String colorStr) throws JSONException{
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JSONObject jsonBody = new JSONObject();

//            int argb = Integer.parseInt("ffffff");
//            int alpha = 0xFF & (argb >> 24);
//            int red = 0xFF & (argb >> 16);
//            int green = 0xFF & (argb >> 8);
//            int blue = 0xFF & (argb >> 0);
//            float alphaFloat = (float)alpha / 255;
//
//            colorStr = rgbaToRGB(255, 255, 255, red, green, blue, alphaFloat);

            int r = Integer.valueOf( colorStr.substring( 2, 4 ), 16);
            int g = Integer.valueOf( colorStr.substring( 4, 6 ), 16);
            int b = Integer.valueOf( colorStr.substring( 6, 8 ), 16);

            jsonBody.put("r", r);
            jsonBody.put("g", g);
            jsonBody.put("b", b);
            final String mRequestBody = jsonBody.toString();

            Log.d("colorStr", "colorStr = " + colorStr);
            Log.d("rgb", "rgb = " + mRequestBody);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("LOG_RESPONSE", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("LOG_RESPONSE", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected String rgbaToRGB(int rgb_background_red, int rgb_background_green, int rgb_background_blue,
                               int rgba_color_red, int rgba_color_green, int rgba_color_blue, float alpha) {

        float red = (1 - alpha) * rgb_background_red + alpha * rgba_color_red;
        float green = (1 - alpha) * rgb_background_green + alpha * rgba_color_green;
        float blue = (1 - alpha) * rgb_background_blue + alpha * rgba_color_blue;

        String redStr = Integer.toHexString((int) red);
        String greenStr = Integer.toHexString((int) green);
        String blueStr = Integer.toHexString((int) blue);

        String colorHex = "#" + redStr + greenStr + blueStr;

        //return Color.parseColor(colorHex);
        return colorHex;
    }
}
