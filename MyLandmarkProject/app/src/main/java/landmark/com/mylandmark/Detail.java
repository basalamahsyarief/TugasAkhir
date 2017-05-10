package landmark.com.mylandmark;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
public class Detail extends AppCompatActivity {
    ProgressDialog pDialog;
    DialogInterface.OnClickListener listener;
    JSONArray detil = null;
    TextView txtid,txtjudul,txtlatitude,txtlongitude, txtdeskripsi,txtalamat,txtcp,txthargaiklan,txtsertifikasi;
    SessionManager session;
    String noktp,getid,url;
    Button edit_iklan,hapus_iklan;
    JSONParser jParser = new JSONParser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        noktp= user.get(SessionManager.KEY_NOKTP);
        txtid = (TextView)findViewById(R.id.txtid);
        txtdeskripsi = (TextView) findViewById(R.id.txtdeskripsi);
        txtjudul = (TextView) findViewById(R.id.txtjudul);
        txtlatitude = (TextView)findViewById(R.id.txtlatitude);
        txtlongitude = (TextView) findViewById(R.id.txtlongitude);
        txtalamat = (TextView)findViewById(R.id.txtalamat);
        txtcp = (TextView)findViewById(R.id.txtcp);
        txthargaiklan = (TextView)findViewById(R.id.txthargaiklan);
        txtsertifikasi = (TextView)findViewById(R.id.txtsertifikasi);
        Intent i = getIntent();
        getid = i.getStringExtra("id");
        new NetCheckDetail().execute();
        edit_iklan = (Button) findViewById(R.id.edit_iklan);
        hapus_iklan = (Button)findViewById(R.id.hapus_iklan);
        edit_iklan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String parsingid = ((TextView) findViewById(R.id.txtid)).getText().toString();
                String parsingnama = ((TextView) findViewById(R.id.txtjudul)).getText().toString();
                String parsingalamat = ((TextView) findViewById(R.id.txtalamat)).getText().toString();
                String parsinglatitude = ((TextView) findViewById(R.id.txtlatitude)).getText().toString();
                String parsinglongitude = ((TextView) findViewById(R.id.txtlongitude)).getText().toString();
                String parsingdeskripsi = ((TextView) findViewById(R.id.txtdeskripsi)).getText().toString();
                String parsingharga = ((TextView)findViewById(R.id.txthargaiklan)).getText().toString();
                String parsingcp = ((TextView)findViewById(R.id.txtcp)).getText().toString();
                String parsingsertifikasi = ((TextView)findViewById(R.id.txtsertifikasi)).getText().toString();
                Intent i = new Intent(getApplicationContext(), EditIklan.class);
                i.putExtra("id", parsingid);
                i.putExtra("nama", parsingnama);
                i.putExtra("latitude", parsinglatitude);
                i.putExtra("longitude",parsinglongitude);
                i.putExtra("alamat",parsingalamat);
                i.putExtra("deskripsi",parsingdeskripsi);
                i.putExtra("cp",parsingcp);
                i.putExtra("harga",parsingharga);
                i.putExtra("sertifikasi",parsingsertifikasi);
                startActivity(i);
                finish();
            }
        });
        hapus_iklan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Hapus_Iklan();
            }
        });

    }
    public class hapusiklan extends AsyncTask<String, String, String>
    {
        String success;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Detail.this);
            pDialog.setMessage("Menghapus Iklan...");
            pDialog.setIndeterminate(false);
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            url = "http://mylandmark.esy.es/API_User/hapus_iklan.php?" + "no_ktp=" + noktp  +"&id=" + getid;
            JSONObject json = jParser.getJSONFromUrl(url);
            try {
                success = json.getString("success");
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            }
            return null;
        }
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();

            if (success.equals("1")) {
                Toast.makeText(getApplicationContext(), "Data dihapus", Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(),UserMain.class);
                startActivity(i);
                finish();

            } else {
                Toast.makeText(getApplicationContext(), "Proses gagal", Toast.LENGTH_LONG).show();
            }
        }

    }
   private class TampilDetail extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            txtid = (TextView)findViewById(R.id.txtid);
            txtdeskripsi = (TextView) findViewById(R.id.txtdeskripsi);
            txtjudul = (TextView) findViewById(R.id.txtjudul);
            txtlatitude = (TextView)findViewById(R.id.txtlatitude);
            txtlongitude = (TextView) findViewById(R.id.txtlongitude);
            txtalamat = (TextView)findViewById(R.id.txtalamat);
            pDialog = new ProgressDialog(Detail.this);
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            url = "http://mylandmark.esy.es/API_User/detail.php?" + "no_ktp=" + noktp  +"&id=" + getid;


            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(url);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            try {
                // Getting JSON Array
                detil = json.getJSONArray("daftar_iklan");
                JSONObject c = detil.getJSONObject(0);

                // Storing  JSON item in a Variable
                String id = c.getString("id");
                String nama = c.getString("nama");
                String alamat = c.getString("alamat");
                String latitude = c.getString("latitude");
                String longitude = c.getString("longitude");
                String deskripsi = c.getString("deskripsi");
                String harga = c.getString("harga");
                String cp = c.getString("cp");
                String sertifikasi = c.getString("sertifikasi");
                //Set JSON Data in TextView
                txtid.setText(id);
                txtjudul.setText(nama);
                txtlatitude.setText(latitude);
                txtlongitude.setText(longitude);
                txtalamat.setText(alamat);
                txtdeskripsi.setText(deskripsi);
                txthargaiklan.setText(harga);
                txtcp.setText(cp);
                txtsertifikasi.setText(sertifikasi);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
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
    private void Hapus_Iklan() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin ingin menghapus iklan ini?");
        builder.setCancelable(false);//tombol BACK tidak bisa tekan
        //Membuat listener untuk tombol DIALOG
        listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==DialogInterface.BUTTON_POSITIVE){
                   new NetCheckHapus().execute();
                }else if(which== DialogInterface.BUTTON_NEGATIVE){
                    dialog.cancel(); //batal keluar
                }
            }
        };
        builder.setPositiveButton("Ya", listener);
        builder.setNegativeButton("Tidak", listener);
        builder.show(); //menampilkan dialog
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //jika tombol BACK ditekan
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent i = new Intent(Detail.this, UserMain.class);
            startActivity(i);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    private class NetCheckDetail extends AsyncTask<String,String,Boolean>
    {
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(Detail.this);
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
                    URL url = new URL("http://mylandmark.esy.es");
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
                new TampilDetail().execute();
            }
            else{
                nDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Peringatan, Cek Koneksi Internet",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Detail.this, UserMain.class);
                startActivity(i);
                finish();
            }
        }
    }
    private class NetCheckHapus extends AsyncTask<String,String,Boolean>
    {
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(Detail.this);
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

            if(th){
                nDialog.dismiss();
                new hapusiklan().execute();
            }
            else{
                nDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Peringatan, Cek Koneksi Internet",Toast.LENGTH_SHORT).show();
            }
        }
    }

}
