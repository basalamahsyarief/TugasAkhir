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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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

public class EditIklan extends AppCompatActivity {
MaterialEditText edittxtcp, edittxtjudul,edittxtlatitude,edittxtlongitude,edittxtdeskripsi,edittxtalamat,edittxtharga;
    Spinner edittxtsertifikasi;
    String getid,getjudul,getlatitude,getlongitude,getdeskripsi,getalamat,noktp,getcp,getharga,sertifikasi;
    Context c=this;
    Button edit_iklan;
    ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    SessionManager session;
    private Double latAwal, lngAwal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_iklan);
        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        noktp = user.get(SessionManager.KEY_NOKTP);
        edittxtjudul = (MaterialEditText)findViewById(R.id.edittxtjudul);
        edittxtlatitude = (MaterialEditText)findViewById(R.id.edittxtlatitude);
        edittxtlongitude = (MaterialEditText)findViewById(R.id.edittxtlongitude);
        edittxtdeskripsi = (MaterialEditText)findViewById(R.id.edittxtdeskripsi);
        edittxtalamat = (MaterialEditText)findViewById(R.id.edittxtalamat);
        edittxtcp = (MaterialEditText)findViewById(R.id.edittxtcp);
        edittxtharga = (MaterialEditText)findViewById(R.id.edittxtharga);
        edittxtsertifikasi = (Spinner)findViewById(R.id.editsertifikasi);
        edittxtsertifikasi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        edittxtharga.addTextChangedListener(new NumberSeparator(edittxtharga));
        edittxtalamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(c,PlaceActivity.class);
                i.putExtra("from_textfield",1);
                startActivityForResult(i,321);
            }
        });

        Intent i = getIntent();
        getid = i.getStringExtra("id");
        getjudul = i.getStringExtra("nama");
        getlatitude = i.getStringExtra("latitude");
        getlongitude = i.getStringExtra("longitude");
        getdeskripsi = i.getStringExtra("deskripsi");
        getcp = i.getStringExtra("cp");
        getalamat = i.getStringExtra("alamat");
        getharga = i.getStringExtra("harga");
        edittxtjudul.setText(getjudul);
        edittxtlatitude.setText(getlatitude);
        edittxtlongitude.setText(getlongitude);
        edittxtdeskripsi.setText(getdeskripsi);
        edittxtalamat.setText(getalamat);
        edittxtcp.setText(getcp);
        edittxtharga.setText(getharga);
        edit_iklan = (Button)findViewById(R.id.edit_iklan);
        edit_iklan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edittxtjudul.getText().toString().trim().length() > 0
                        && edittxtlatitude.getText().toString().trim().length() > 0
                        && edittxtlongitude.getText().toString().trim().length() > 0
                        && edit_iklan.getText().toString().trim().length() > 0
                        && edittxtalamat.getText().toString().trim().length() > 0
                        && edittxtharga.getText().toString().trim().length() > 0)
                {

                    new NetCheck().execute();

                }
                else {
                    Toast.makeText(getApplicationContext(), "Data Belum Lengkap", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public class editiklan extends AsyncTask<String, String, String>
    {

        String success;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditIklan.this);
            pDialog.setMessage("Mengedit Iklan...");
            pDialog.setIndeterminate(false);
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            String url = "http://mylandmark.esy.es/API_User/edit_iklan.php?" + "no_ktp=" + noktp  +"&id=" + getid;
            String strjudul = edittxtjudul.getText().toString();
            String strlat   = edittxtlatitude.getText().toString();
            String strlong  = edittxtlongitude.getText().toString();
            String strdesk  = edittxtdeskripsi.getText().toString();
            String stralamat = edittxtalamat.getText().toString();
            String strcp = edittxtcp.getText().toString();
            String strharga = edittxtharga.getText().toString();

            List<NameValuePair> nvp = new ArrayList<NameValuePair>();
            nvp.add(new BasicNameValuePair("nama", strjudul));
            nvp.add(new BasicNameValuePair("alamat", stralamat));
            nvp.add(new BasicNameValuePair("latitude", strlat));
            nvp.add(new BasicNameValuePair("longitude", strlong));
            nvp.add(new BasicNameValuePair("deskripsi", strdesk));
            nvp.add(new BasicNameValuePair("cp",strcp));
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
                Toast.makeText(getApplicationContext(), "Proses Berhasil", Toast.LENGTH_LONG).show();


            } else {
                Toast.makeText(getApplicationContext(), "Proses Gagal", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_iklan, menu);
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
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(resultCode== Activity.RESULT_OK){
            switch (requestCode){
                case 321:
                    Bundle returnData = data.getExtras();
                    int from = returnData.getInt("from_textfield");
                    if(from==1){
                        latAwal = returnData.getDouble("latitude");
                        lngAwal = returnData.getDouble("longitude");
                        edittxtalamat.setText(returnData.getString("alamat"));
                        edittxtlatitude.setText(latAwal.toString());
                        edittxtlongitude.setText(lngAwal.toString());
                    }
                    break;

            }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //jika tombol BACK ditekan
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent i = new Intent(EditIklan.this, UserMain.class);
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
            nDialog = new ProgressDialog(EditIklan.this);
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
                new editiklan().execute();
            }
            else{
                nDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Peringatan, Cek Koneksi Internet",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
