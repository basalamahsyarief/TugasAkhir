package landmark.com.mylandmark;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChangePassword extends AppCompatActivity {
    ProgressDialog pDialog;
    EditText passbaru;
    TextView email_changepass;
    String oldpass,noktp;
    Button btchangepass,btcancel;
    SessionManager session;
    JSONArray emailaccount;
    JSONParser jsonParser = new JSONParser();
    String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        passbaru = (EditText)findViewById(R.id.newpass);
        noktp = user.get(SessionManager.KEY_NOKTP);
        oldpass = user.get(SessionManager.KEY_PASSWORD);
        new GetEmail().execute();
        btchangepass = (Button)findViewById(R.id.changepassbtn);
        btchangepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passbaru.getText().toString().trim().length() > 0){
                    new NetCheck().execute();    
                }
                else{
                    Toast.makeText(ChangePassword.this, "Isikan Password Baru Anda", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btcancel = (Button)findViewById(R.id.btcancel);
        btcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),UserMain.class);
                startActivity(i);
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_change_password, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public class gantipassword extends AsyncTask<String, String, String>
    {

        String success;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ChangePassword.this);
            pDialog.setMessage("Proses mendaftar...");
            pDialog.setIndeterminate(false);
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
          url  = "http://mylandmark.esy.es/API_User/index.php";
            String newpass     = passbaru.getText().toString();
            String email = email_changepass.getText().toString();

            List<NameValuePair> nvp = new ArrayList<NameValuePair>();
            nvp.add(new BasicNameValuePair("tag", "chgpass"));
            nvp.add(new BasicNameValuePair("newpass", newpass));
            nvp.add(new BasicNameValuePair("no_ktp",noktp));
            nvp.add(new BasicNameValuePair("email", email));

            JSONObject json = jsonParser.makeHttpRequest(url, "POST", nvp);
            try {
                success = json.getString("success");

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();

            if (success.equals("1")) {
                Toast.makeText(getApplicationContext(), "Password Sukses Diganti", Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(),Login.class);
                startActivity(i);
                finish();

            } else {
                Toast.makeText(getApplicationContext(), "Proses Gagal", Toast.LENGTH_LONG).show();
            }
        }

    }
    private class GetEmail extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            email_changepass = (TextView)findViewById(R.id.email_changepass);
            pDialog = new ProgressDialog(ChangePassword.this);
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            url = "http://mylandmark.esy.es/API_User/get_pendaftaran.php?" + "no_ktp=" + noktp  +"&password=" + oldpass;
            JSONParser jParser = new JSONParser();

            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(url);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            try {
                emailaccount = json.getJSONArray("login");
                JSONObject c = emailaccount.getJSONObject(0);
                String email = c.getString("email");
                email_changepass.setText(email);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent i = new Intent(getApplicationContext(),UserMain.class);
            startActivity(i);
            finish();
        }
        return super.onKeyDown(keyCode,event );
    }
    private class NetCheck extends AsyncTask<String,String,Boolean>
    {
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(ChangePassword.this);
            nDialog.setTitle("Connecting Network");
            nDialog.setMessage("Loading..");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        }


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

            if(th){
                nDialog.dismiss();
                new gantipassword().execute();
            }
            else{
                nDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Peringatan, Cek Koneksi Internet",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
