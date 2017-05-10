package landmark.com.mylandmark;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.rengwuxian.materialedittext.MaterialEditText;

public class Login extends AppCompatActivity {
    Button login,forpass;
    MaterialEditText noktp,password;
    String url, success;
    SessionManager session;
    JSONParser jParser = new JSONParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        session = new SessionManager(getApplicationContext());
        Toast.makeText(getApplicationContext(),
                "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG)
                .show();
        login = (Button) findViewById(R.id.login);
        noktp = (MaterialEditText) findViewById(R.id.no_ktp);
        password = (MaterialEditText) findViewById(R.id.password);
        forpass = (Button)findViewById(R.id.btnforpass);
        forpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),LupaPassword.class);
                startActivity(i);
            }
        });


        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                url = "http://mylandmark.esy.es/API_User/get_pendaftaran.php?" + "no_ktp="
                        + noktp.getText().toString() + "&password="
                        + password.getText().toString();

                if (noktp.getText().toString().trim().length() > 0
                        && password.getText().toString().trim().length() > 0)
                {

                  new NetCheck().execute();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Username/password masih kosong", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //jika tombol BACK ditekan
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent i = new Intent(Login.this, MainActivity.class);
            startActivity(i);
            finish();
        }
        return super.onKeyDown(keyCode,event );
    }

    public class Masuk extends AsyncTask<String, String, String>
    {
        ArrayList<HashMap<String, String>> contactList = new ArrayList<HashMap<String, String>>();
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            pDialog = new ProgressDialog(Login.this);
            pDialog.setMessage("Logging In...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... arg0) {

            JSONObject json = jParser.getJSONFromUrl(url);

            try {
                success = json.getString("success");

                Log.e("error", "nilai sukses=" + success);

                JSONArray hasil = json.getJSONArray("login");

                if (success.equals("1")) {

                    for (int i = 0; i < hasil.length(); i++) {

                        JSONObject c = hasil.getJSONObject(i);
                        String no_ktp = c.getString("no_ktp").trim();
                        String password = c.getString("password").trim();
                        session.createLoginSession(no_ktp, password);
                        Log.e("ok", " ambil data");
                    }
                } else {
                    Log.e("erro", "tidak bisa ambil data 0");
                }

            } catch (Exception e) {
                // TODO: handle exception
                Log.e("erro", "tidak bisa ambil data 1");
            }

            return null;

        }
        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            pDialog.dismiss();
            if (success.equals("1")) {
             Intent   a = new Intent(Login.this, UserMain.class);
                startActivity(a);
                finish();
            } else {

                Toast.makeText(getApplicationContext(), "Nomor KTP/Password Salah !", Toast.LENGTH_LONG).show();
            }

        }

    }
    private class NetCheck extends AsyncTask<String,String,Boolean>
    {
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(Login.this);
            nDialog.setTitle("Connecting Network");
            nDialog.setMessage("Loading..");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        }
        /**
         * Gets current device state and checks for working internet connection by trying Google.
         **/
        @Override
        protected Boolean doInBackground(String... args){



            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                try {
                    URL url = new URL("http://www.mylandmark.esy.es");
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setConnectTimeout(3000);
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) {
                        return true;
                    }
                } catch (MalformedURLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return false;

        }
        @Override
        protected void onPostExecute(Boolean th){

            if(th == true){
                nDialog.dismiss();
                new Masuk().execute();
            }
            else{
                nDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Peringatan, Cek Koneksi Internet",Toast.LENGTH_SHORT).show();
            }
        }
    }

}
