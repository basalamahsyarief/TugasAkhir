package landmark.com.mylandmark;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class UserMain extends AppCompatActivity {
    JSONArray collect;
    String success;
    ListView daftariklan;
    Button logout,btntambah_iklan,changepassbtn;
    SessionManager session;
    String noktp;
    ProgressDialog pDialog;
    JSONParser jParser = new JSONParser();
    DialogInterface.OnClickListener listener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);
        daftariklan = (ListView) findViewById(R.id.daftar_iklan);
       session = new SessionManager(getApplicationContext());
        Toast.makeText(getApplicationContext(),
                "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG)
                .show();
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        noktp = user.get(SessionManager.KEY_NOKTP);
        TextView myktp = (TextView) findViewById(R.id.txtktp);
        myktp.setText(Html.fromHtml(noktp));
        new NetCheck().execute();
        logout = (Button) findViewById(R.id.logout);
        changepassbtn = (Button)findViewById(R.id.changepassbtn);
        btntambah_iklan = (Button) findViewById(R.id.btntambahiklan);
        changepassbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),ChangePassword.class);
                startActivity(i);
                finish();
            }
        });
        btntambah_iklan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),tambahiklan.class);
                startActivity(i);
                finish();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                session.logoutUser();
                finish();
            }
        });
        daftariklan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                String parsingid = ((TextView) view.findViewById(R.id.id_iklan)).getText().toString();
                String parsingnama = ((TextView) view.findViewById(R.id.nama_iklan)).getText().toString();
                String parsingalamat = ((TextView) view.findViewById(R.id.alamat_iklan)).getText().toString();
                String parsinglatitude = ((TextView) view.findViewById(R.id.latitude_iklan)).getText().toString();
                String parsinglongitude = ((TextView) view.findViewById(R.id.longitude_iklan)).getText().toString();
                String parsingdeskripsi = ((TextView) view.findViewById(R.id.deskripsi_iklan)).getText().toString();
                String parsingcp = ((TextView) view.findViewById(R.id.cp_iklan)).getText().toString();
                String parsingsertifikasi = ((TextView) view.findViewById(R.id.sertifikasi_iklan)).getText().toString();

                Intent i = new Intent(getApplicationContext(), Detail.class);
                i.putExtra("id", parsingid);
                i.putExtra("nama", parsingnama);
                i.putExtra("alamat", parsingalamat);
                i.putExtra("latitude", parsinglatitude);
                i.putExtra("longitude", parsinglongitude);
                i.putExtra("deskripsi", parsingdeskripsi);
                i.putExtra("sertifikasi", parsingsertifikasi);
                i.putExtra("cp", parsingcp);
                startActivity(i);
                finish();
            }
        });

    }

    public class Daftar_Iklan extends AsyncTask<String, String, String> {
        ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UserMain.this);
            pDialog.setMessage("Loading Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            String url="http://mylandmark.esy.es/API_User/daftar_iklan.php?"+ "&no_ktp="
                    + noktp;
            JSONObject json = jParser.getJSONFromUrl(url);
            try {
                success = json.getString("success");
                collect = json.getJSONArray("daftar_iklan");
                Log.e("error", "nilai sukses=" + success);
                if (success.equals("1")) {
                    for (int i = 0; i < collect.length(); i++) {
                        JSONObject c = collect.getJSONObject(i);
                        HashMap<String, String> map = new HashMap<String, String>();
                        String id = c.getString("id").trim();
                        String latitude = c.getString("latitude");
                        String longitude = c.getString("longitude");
                        String nama = c.getString("nama");
                        String alamat = c.getString("alamat");
                        String deskripsi = c.getString("deskripsi");
                        String sertifikasi = c.getString("sertifikasi");
                        String cp = c.getString("cp");
                        String harga = c.getString("harga");
                        String aktivasi = c.getString("aktivasi");
                        map.put("id", id);
                        map.put("nama", nama);
                        map.put("latitude", latitude);
                        map.put("longitude", longitude);
                        map.put("alamat",alamat);
                        map.put("deskripsi",deskripsi);
                        map.put("sertifikasi",sertifikasi);
                        map.put("cp",cp);
                        map.put("harga",harga);
                        map.put("aktivasi",aktivasi);
                        dataList.add(map);
                    }
                } else {
                    Log.e("erro", "tidak bisa ambil data 0");
                    for (int i = 0; i < collect.length(); i++) {
                        JSONObject c = collect.getJSONObject(i);
                        HashMap<String, String> map = new HashMap<String, String>();
                        String id = c.getString("id").trim();
                        String latitude = c.getString("latitude");
                        String longitude = c.getString("longitude");
                        String nama = c.getString("nama");
                        String alamat = c.getString("alamat");
                        String deskripsi = c.getString("deskripsi");
                        String sertifikasi = c.getString("sertifikasi");
                        String cp = c.getString("cp");
                        String harga = c.getString("harga");
                        String aktivasi = c.getString("aktivasi");
                        map.put("id", id);
                        map.put("nama", nama);
                        map.put("latitude", latitude);
                        map.put("longitude", longitude);
                        map.put("alamat",alamat);
                        map.put("deskripsi",deskripsi);
                        map.put("sertifikasi",sertifikasi);
                        map.put("cp",cp);
                        map.put("harga",harga);
                        map.put("aktivasi",aktivasi);
                        dataList.add(map);
                    }

                }
            } catch (Exception e) {
                Log.e("erro", "tidak bisa ambil data 1, Nilai Sukses = " +success);
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            pDialog.dismiss();
            if (success.equals("1")) {
                ListAdapter adapter = new SimpleAdapter(getApplicationContext(),
                        dataList, R.layout.list_iklan_user, new String[]{"id", "nama", "latitude", "longitude", "alamat", "deskripsi", "sertifikasi", "cp", "harga", "aktivasi"},
                        new int[]{R.id.id_iklan, R.id.nama_iklan, R.id.latitude_iklan, R.id.longitude_iklan, R.id.alamat_iklan, R.id.deskripsi_iklan, R.id.sertifikasi_iklan, R.id.cp_iklan, R.id.harga_iklan, R.id.status_iklan});
                daftariklan.setAdapter(adapter);
            }
            else {
                Toast.makeText(getApplicationContext(), "data tidak ada",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //jika tombol BACK ditekan
        if(keyCode == KeyEvent.KEYCODE_BACK){
           Keluar();
        }
        return super.onKeyDown(keyCode,event );
    }
    private void Keluar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin ingin keluar?");
        builder.setCancelable(false);//tombol BACK tidak bisa tekan
        //Membuat listener untuk tombol DIALOG
        listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==DialogInterface.BUTTON_POSITIVE){
                    finish(); //keluar aplikasi
                }else if(which== DialogInterface.BUTTON_NEGATIVE){
                    dialog.cancel(); //batal keluar
                }
            }
        };
        builder.setPositiveButton("Ya", listener);
        builder.setNegativeButton("Tidak", listener);
        builder.show(); //menampilkan dialog
    }
    private class NetCheck extends AsyncTask<String,String,Boolean>
    {
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(UserMain.this);
            nDialog.setTitle("Checking Network");
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
                new Daftar_Iklan().execute();
            }
            else{
                nDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Peringatan, Cek Koneksi Internet",Toast.LENGTH_SHORT).show();
            }
        }
    }
}