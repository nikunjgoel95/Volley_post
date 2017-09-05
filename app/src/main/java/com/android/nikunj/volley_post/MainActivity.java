package com.android.nikunj.volley_post;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    private Button start;
    private Button end;
    private Button getimage;
    private ImageView imageView;
    ProgressDialog progressDialog;
    public final String posturl = "http://posttestserver.com/post.php?dir=XYZ";
    public final String geturl = "http://www.google.com";
    public final String imgurl = "https://upload.wikimedia.org/wikipedia/commons/1/1e/Stonehenge.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volleypostfunc(posturl);
            }
        });

        end = (Button) findViewById(R.id.get);
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volleygeturl(geturl);
            }
        });

        getimage = (Button) findViewById(R.id.imageGetButton);
        getimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volleygetImagefunc(imgurl);
            }
        });
        imageView = (ImageView) findViewById(R.id.image_view);
    }

    private void volleygeturl(String geturl) {
        RequestQueue queue = Volley.newRequestQueue(this);


// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, geturl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i("Response is: ",""+ response.substring(0,500));

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Get response","That didn't work!");
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void volleygetImagefunc(String imgurl)
    {
        RequestQueue queue = Volley.newRequestQueue(this);

        Toast.makeText(MainActivity.this,"Inside function",Toast.LENGTH_SHORT).show();

        final ImageRequest imageRequest=new ImageRequest (imgurl, new Response.Listener<Bitmap>() {

            @Override
            public void onResponse(Bitmap response) {
                imageView.setImageBitmap(response);

            }
        },0,0, ImageView.ScaleType.CENTER_CROP,null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,"Some Thing Goes Wrong",Toast.LENGTH_SHORT).show();
                error.printStackTrace();

            }
        });
        queue.add(imageRequest);
    }

    private void volleypostfunc(String posturl) {
        try {

            RequestQueue requestQueue = Volley.newRequestQueue(this);

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Image upload In progresss ......");
            progressDialog.show();

            // Drawable image needs to be converted to Bitmap then to ByteArryoutstream basically a blob.

            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

            String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
            //blob



            JSONObject jsonBody = new JSONObject();
            jsonBody.put("firstkey", "firstvalue");
            jsonBody.put("secondkey", "secondobject");
            jsonBody.put("imageblob", encodedImage);


            final String mRequestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, posturl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    Log.i("LOG_VOLLEY", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("LOG_VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return super.getBodyContentType();
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
}