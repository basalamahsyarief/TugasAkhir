package landmark.com.mylandmark;

import android.app.Activity;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class tambahiklan extends AppCompatActivity {
    ProgressDialog pDialog;
    Spinner tambahsertifikasi;
    JSONParser jsonParser = new JSONParser();
    MaterialEditText tambahcp,tambahalamat,tambahlatitude ,tambahjudul,tambahlongitude,tambahdeskripsi,tambahharga;
    SessionManager session;
    String noktp,sertifikasi;
    Button tambahiklan;
    Context c = this;
    ImageView upload_foto;
    private Double latAwal, lngAwal;
    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambahiklan);
        tambahjudul = (MaterialEditText) findViewById(R.id.tambah_judul);
        tambahcp = (MaterialEditText)findViewById(R.id.tambah_cp);
        tambahlatitude = (MaterialEditText) findViewById(R.id.tambah_latitude);
        tambahlongitude = (MaterialEditText) findViewById(R.id.tambah_longitude);
        tambahdeskripsi = (MaterialEditText) findViewById(R.id.tambah_deskripsi);
        tambahalamat = (MaterialEditText)findViewById(R.id.tambah_alamat);
        tambahharga = (MaterialEditText)findViewById(R.id.tambah_harga);
        tambahsertifikasi = (Spinner)findViewById(R.id.tambahsertifikasi);
        tambahsertifikasi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sertifikasi =String.valueOf(parent.getSelectedItem());
                if (sertifikasi.equals("SHM - Sertifikat Hak Milik")){
                    sertifikasi = "SHM";
                }
                else if (sertifikasi.equals("HGB - Hak Guna Bangun")){
                    sertifikasi = "HGB";
                }
                else if(sertifikasi.equals("Lainnya (PPJB,Girik,Adat,dll)")){
                    sertifikasi = "Lainnya";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        tambahharga.addTextChangedListener(new NumberSeparator(tambahharga));
        session = new SessionManager(getApplicationContext());
      session.checkLogin();
        tambahalamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent i = new Intent(c,PlaceActivity.class);
                i.putExtra("from_textfield",1);
                startActivityForResult(i,321);
            }
        });
        HashMap<String, String> user = session.getUserDetails();
      noktp = user.get(SessionManager.KEY_NOKTP);
 //          noktp = user.get(SessionManager.KEY_NOKTP);
       tambahiklan = (Button) findViewById(R.id.menambah_iklan);
        tambahiklan.setOnClickListener(new View.OnClickListener() {
        @Override
            public void onClick(View view){
            if (tambahjudul.getText().toString().trim().length() > 0
                    && tambahlatitude.getText().toString().trim().length() > 0 && tambahlongitude.getText().toString().trim().length() > 0
                    && tambahcp.getText().toString().trim().length() > 0 && tambahalamat.getText().toString().trim().length() > 0 && tambahharga.getText().toString().trim().length() > 0)
            {

               new NetCheck().execute();

            }
            else {
                Toast.makeText(getApplicationContext(), "Data Belum Lengkap", Toast.LENGTH_LONG).show();
            }

            }
        });
    }

    public class menambahiklan extends AsyncTask<String, String, String>
    {

        String success;
      @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(tambahiklan.this);
            pDialog.setMessage("Menambah Iklan..");
            pDialog.setIndeterminate(false);
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            url="http://mylandmark.esy.es/API_User/tambah_iklan.php";
            String strjudul      = tambahjudul.getText().toString();
            String strlat     = tambahlatitude.getText().toString();
            String strlong     = tambahlongitude.getText().toString();
            String strdesk  = tambahdeskripsi.getText().toString();
            String stralamat = tambahalamat.getText().toString();
            String strcp = tambahcp.getText().toString();
            String strharga= tambahharga.getText().toString();

            List<NameValuePair> nvp = new ArrayList<NameValuePair>();
            nvp.add(new BasicNameValuePair("no_ktp", noktp));
            nvp.add(new BasicNameValuePair("nama", strjudul));
            nvp.add(new BasicNameValuePair("alamat",stralamat));
            nvp.add(new BasicNameValuePair("cp",strcp));
            nvp.add(new BasicNameValuePair("latitude", strlat));
            nvp.add(new BasicNameValuePair("longitude", strlong));
            nvp.add(new BasicNameValuePair("deskripsi", strdesk));
            nvp.add(new BasicNameValuePair("harga",strharga));
            nvp.add(new BasicNameValuePair("sertifikasi",sertifikasi));

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
                Toast.makeText(getApplicationContext(), "Iklan Sukses Ditambahkan", Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(),UserMain.class);
                startActivity(i);
                finish();

            } else {
                Toast.makeText(getApplicationContext(), "Iklan Gagal Ditambahkan", Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(),UserMain.class);
                startActivity(i);
                finish();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tambahiklan, menu);
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
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(resultCode== Activity.RESULT_OK&& data!=null){
            switch (requestCode){
                case 321:
                    Bundle returnData = data.getExtras();
                    int from = returnData.getInt("from_textfield");
                    if(from==1){
                        latAwal = returnData.getDouble("latitude");
                        lngAwal = returnData.getDouble("longitude");
                        tambahalamat.setText(returnData.getString("alamat"));
                        tambahlatitude.setText(latAwal.toString());
                        tambahlongitude.setText(lngAwal.toString());
                    }
                    break;
            }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //jika tombol BACK ditekan
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent i = new Intent(getApplicationContext(), UserMain.class);
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
            nDialog = new ProgressDialog(tambahiklan.this);
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
                new menambahiklan().execute();
            }
            else{
                nDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Peringatan, Cek Koneksi Internet",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
