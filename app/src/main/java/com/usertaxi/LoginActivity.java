package com.usertaxi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.usertaxi.gcmclasses.Config;
import com.usertaxi.utils.AppConstants;
import com.usertaxi.utils.AppPreferences;
import com.usertaxi.utils.ServiceHandler;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eyon on 11/25/2015.
 */
public class LoginActivity extends AppCompatActivity {
    Button btn_loginsend, send, canclebutton;
    EditText medit_email, medit_paasword;
    EditText emailforgot;
    public static String email, pasword;
    ProgressDialog asynDialog;
    public static String name = "";
    public static String imei = "";
    TextView txt_forgotpaas, txtheader, mtextone, mtexttwo, mtextthree, mtextfour, mtextfive;
    PopupWindow pwindo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = getString(R.string.prompt_login);
        getSupportActionBar().setTitle(title);
        btn_loginsend = (Button) findViewById(R.id.btn_login);
        medit_email = (EditText) findViewById(R.id.email_login);
        medit_paasword = (EditText) findViewById(R.id.password_login);
        txt_forgotpaas = (TextView) findViewById(R.id.textforgot);
        mtextone = (TextView) findViewById(R.id.textone);
        mtexttwo = (TextView) findViewById(R.id.texttwo);
        mtextthree = (TextView) findViewById(R.id.textthree);
        mtextfour = (TextView) findViewById(R.id.textfour);
        mtextfive = (TextView) findViewById(R.id.textfive);
        String mystring = new String("Forgot Password?");
        String mystringterm = new String("Term of service");
        String mystringpolicy = new String("Privacy policy");
        String mystringcontent = new String("Content policy");
        SpannableString content = new SpannableString(mystring);
        SpannableString contenttwo = new SpannableString(mystringterm);
        SpannableString contentthree = new SpannableString(mystringpolicy);
        SpannableString contentfour = new SpannableString(mystringcontent);
        content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
        contenttwo.setSpan(new UnderlineSpan(), 0, mystringterm.length(), 0);
        contentthree.setSpan(new UnderlineSpan(), 0, mystringpolicy.length(), 0);
        contentfour.setSpan(new UnderlineSpan(), 0, mystringcontent.length(), 0);
        txt_forgotpaas.setText(content);
        mtexttwo.setText(contenttwo);
        mtextthree.setText(contentthree);
        mtextfive.setText(contentfour);

        Typeface tf = Typeface.createFromAsset(this.getAssets(),
                "Montserrat-Regular.ttf");
        btn_loginsend.setTypeface(tf);
        medit_email.setTypeface(tf);
        medit_paasword.setTypeface(tf);
        txt_forgotpaas.setTypeface(tf);
        mtextone.setTypeface(tf);
        mtexttwo.setTypeface(tf);
        mtextthree.setTypeface(tf);
        mtextfour.setTypeface(tf);
        mtextfive.setTypeface(tf);

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(getResources().getColor(R.color.colorbutton), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);



        btn_loginsend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                medit_email.setError(null);
                medit_paasword.setError(null);

                if (medit_email.length()> 0) {
                    email = medit_email.getText().toString();
                    if (medit_paasword.length() > 0) {
                        pasword = medit_paasword.getText().toString();
                        new Login(LoginActivity.this).execute(
                                medit_email.getText().toString(), medit_paasword
                                        .getText().toString());
                    } else if (pasword.length() == 0) {
                        medit_paasword.setError(getResources().getString(
                                R.string.error_field_required));
                    } else if (email.length() == 0) {
                        medit_email.setError(getResources().getString(
                                R.string.error_field_required));
                    }
                }
            }
        });

        getDeviceId();

    }


    class Login extends AsyncTask<String, Void, String> {

        String status = "", result = "", loginId = "";

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            asynDialog = new ProgressDialog(LoginActivity.this);
            asynDialog.setTitle("Login");
            asynDialog.setMessage("Please wait...");
            asynDialog.show();
            super.onPreExecute();
        }

        Context context;

        public Login(Context context) {
            this.context = context;

        }

        @Override
        protected String doInBackground(String... params) {
            ServiceHandler serviceHandler = new ServiceHandler();
            List<NameValuePair> values = new ArrayList<>();
            values.add(new BasicNameValuePair("email", params[0]));
            values.add(new BasicNameValuePair("password", params[1]));
            values.add(new BasicNameValuePair("regId", getDeviceId()));
            values.add(new BasicNameValuePair("latitute", AppPreferences.getLatutude(LoginActivity.this)));
            values.add(new BasicNameValuePair("longitude", AppPreferences.getLongitude(LoginActivity.this)));


            Log.d("login_value", values.toString());
            String login_json = serviceHandler.makeServiceCall(
                    AppConstants.LOGIN, ServiceHandler.POST, values);
            if (login_json != null) {
                Log.d("login_json", login_json.toString());
                try {
                    JSONObject jsonObject = new JSONObject(login_json);
                    status = jsonObject.getString("status");
                    result = jsonObject.getString("result");


                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                        loginId = jsonObject2.getString("id");
                        AppPreferences
                                .setCustomerId(context, Integer.parseInt(jsonObject2.getString("id")));
                        AppPreferences
                                .setId(context, jsonObject2.getString("email"));
                        Log.d("email------", AppPreferences.getId(LoginActivity.this));
                        AppPreferences
                                .setUsername(context, jsonObject2.getString("userName"));
                        Log.d("name---------", AppPreferences.getUsername(LoginActivity.this));
                        AppPreferences
                                .setMobileuser(context, jsonObject2.getString("mobile"));


                        Log.d("loginId", loginId);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            medit_email.setText("");
            medit_paasword.setText("");
            asynDialog.dismiss();
            finish();

            Log.d("data from server", status + "   " + loginId);
            if (status.equals("200") && !loginId.equals("")) {
                Intent mintent_home = new Intent(LoginActivity.this,
                        DrawerMainActivity.class);

                startActivity(mintent_home);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Check network connection",
                        Toast.LENGTH_LONG).show();

            }
        }
    }

    public String getDeviceId() {

        GCMRegistrar.checkDevice(this.getApplicationContext());
        GCMRegistrar.checkManifest(this.getApplicationContext());
        final String regId = GCMRegistrar.getRegistrationId(this
                .getApplicationContext());

        // Check if regid already presents
        if (regId.equals("")) {
            Log.i("GCM K", "--- Regid = ''" + regId);
            GCMRegistrar.register(this.getApplicationContext(),
                    Config.GOOGLE_SENDER_ID);
            AppPreferences.setDeviceid(this, regId);
            return regId;
        } else {

            if (GCMRegistrar.isRegisteredOnServer(this)) {

                Log.i("GCM K2", "--- Regid = ''" + regId);
            } else {
                Log.i("GCM K3", "--- Regid = ''" + regId);

            }
            AppPreferences.setDeviceid(this, regId);
            return regId;
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.request, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }


    public class ForgotPasswordTask extends AsyncTask<String, Void, Integer> {
        private String email;
        private Activity activity;
        private JSONObject jsonObj;
        private int status;
        private String message;
        private String TAG = ForgotPasswordTask.class.getSimpleName();

        public ForgotPasswordTask(Activity activity, String email) {
            this.email = email;
            this.activity = activity;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Integer doInBackground(String... params) {
            try {
                HttpParams httpParameters = new BasicHttpParams();
                ConnManagerParams.setTimeout(httpParameters, AppConstants.NETWORK_TIMEOUT_CONSTANT);
                HttpConnectionParams.setConnectionTimeout(httpParameters, AppConstants.NETWORK_CONNECTION_TIMEOUT_CONSTANT);
                HttpConnectionParams.setSoTimeout(httpParameters, AppConstants.NETWORK_SOCKET_TIMEOUT_CONSTANT);
                HttpClient httpclient = new DefaultHttpClient(httpParameters);
                HttpPost httppost = new HttpPost(AppConstants.FORGETPASSWORD);

                JSONArray jsonArray = new JSONArray();

                StringEntity se = null;
                jsonObj = new JSONObject();

                jsonObj.put("email", email);

                jsonArray.put(jsonObj);

                try {
                    se = new StringEntity(jsonArray.toString(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                System.out.println("JSON sent is : " + jsonArray.toString());
                httppost.setEntity(se);
                HttpResponse response = null;
                response = httpclient.execute(httppost);
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String jsonString = "";
                try {
                    jsonString = reader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (jsonString != null) {
                    JSONObject jsonObj = new JSONObject(jsonString);
                    System.out.println("jsonstring-------------------------------" + jsonString);
                    if (jsonObj.getString("status").equalsIgnoreCase("200")) {
                        System.out
                                .println("--------- message 200 got ----------");
                        status = 200;


                    } else if (jsonObj.getString("status").equalsIgnoreCase("400")) {
                        Log.v(TAG, "Status error");
                        status = 404;
                    } else if (jsonObj.getString("status").equalsIgnoreCase("500")) {
                        Log.v(TAG, "No Data Recieved in Request");
                        status = 500;
                    } else {
                        Log.v(TAG, "200 not recieved");
                    }
                }

            } catch (ConnectTimeoutException e) {
            } catch (SocketTimeoutException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }
            return status;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            try {
                System.out.println("status--" + status);
                if (status == 200) {
                    System.out.println("200 dialog if");
                    Toast.makeText(activity.getApplicationContext(), "The new password has been send on your Email Successfully.", Toast.LENGTH_SHORT).show();
                    // ResetFields();
                } else if (status == 404) {
                    System.out.println("400 dialog if");
                    Toast.makeText(activity.getApplicationContext(), "Email does not exist.", Toast.LENGTH_SHORT).show();
                } else {
                    System.out.println("500 dialog if");
                    Toast.makeText(activity.getApplicationContext(), "Email does not exist.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void ResetFields() {
        emailforgot.setText("");
        emailforgot.setHint("Type Your Email-id");
        pwindo.dismiss();
    }





}
