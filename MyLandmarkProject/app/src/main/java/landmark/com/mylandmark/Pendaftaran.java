package landmark.com.mylandmark;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.rengwuxian.materialedittext.MaterialEditText;
public class Pendaftaran extends AppCompatActivity {
    ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    MaterialEditText email, password,nama,no_ktp;
    Button daftar;
    String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendaftaran);
        no_ktp  =(MaterialEditText)findViewById(R.id.no_ktp);
        email   =(MaterialEditText)findViewById(R.id.email);
        password=(MaterialEditText)findViewById(R.id.password);
        nama    =(MaterialEditText)findViewById(R.id.nama);
        daftar = (Button)findViewById(R.id.daftar);
        daftar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
              url  = "http://mylandmark.esy.es/API_User/index.php";
                if (no_ktp.getText().toString().trim().length() > 0
                        && email.getText().toString().trim().length() > 0
                        && password.getText().toString().trim().length() > 0
                        && nama.getText().toString().trim().length() > 0)
                {
                   new NetCheck().execute();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Data Belum Terisi Lengkap", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //jika tombol BACK ditekan
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent i = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);
            finish();
        }
        return super.onKeyDown(keyCode,event );
    }
    public class daftarAku extends AsyncTask<String, String, String>
    {

        String success;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Pendaftaran.this);
            pDialog.setMessage("Proses mendaftar...");
            pDialog.setIndeterminate(false);
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            url  = "http://mylandmark.esy.es/API_User/index.php";
            String noktp     = no_ktp.getText().toString();
            String strnama      = nama.getText().toString();
            String stremail     = email.getText().toString();
            String strpassword  = password.getText().toString();

            List<NameValuePair> nvp = new ArrayList<NameValuePair>();
            nvp.add(new BasicNameValuePair("tag", "register"));
            nvp.add(new BasicNameValuePair("no_ktp", noktp));
            nvp.add(new BasicNameValuePair("name", strnama));
            nvp.add(new BasicNameValuePair("email", stremail));
            nvp.add(new BasicNameValuePair("password", strpassword));

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
                Toast.makeText(getApplicationContext(), "Registrasi sukses", Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(),Login.class);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Registrasi gagal", Toast.LENGTH_LONG).show();
            }
        }

    }

    private class NetCheck extends AsyncTask<String,String,Boolean>
    {
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(Pendaftaran.this);
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
                new daftarAku().execute();
            }
            else{
                nDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Peringatan, Cek Koneksi Internet",Toast.LENGTH_SHORT).show();
            }
        }
    }

}