package landmark.com.mylandmark;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rengwuxian.materialedittext.MaterialEditText;

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

public class peta_geografis extends FragmentActivity implements android.location.LocationListener,GoogleMap.OnMarkerClickListener {
    ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
    String success,fhargamaks,fhargamin,fsertifikasi,url;
    private GoogleMap googleMap;
    ProgressDialog pDialog;
    Button map_normal,map_satelit,map_terrain,map_hybrid,filter,btn_resetfilter;
    JSONArray peta = null;
    ListView lve;
    Context c = this;
    HashMap<String, String> map;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    JSONParser jParser = new JSONParser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peta_geografis);
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        googleMap = fm.getMap();
        filter = (Button)findViewById(R.id.btn_filter);
        btn_resetfilter = (Button)findViewById(R.id.btn_reset);
        map_normal = (Button)findViewById(R.id.map_normal);
        map_satelit = (Button)findViewById(R.id.map_satelit);
        map_hybrid = (Button) findViewById(R.id.map_hybrid);
        map_terrain = (Button)findViewById(R.id.map_terrain);
        map_normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });
        map_satelit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });
        map_hybrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        });
        map_terrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            }
        });
        lve = (ListView) findViewById(R.id.listViewa2);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(c,Filter.class);
                startActivity(i);
                finish();
           //     i.putExtra("to_filter",1);
             //   startActivityForResult(i, 100);
            }
        });
        btn_resetfilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   url = "http://mylandmark.esy.es/mylandmark/landmark.php";
              //  new NetCheck().execute();
                Intent i = new Intent(peta_geografis.this,peta_geografis.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
                finish();
            }
        });
        Intent i = getIntent();
        fhargamaks = i.getStringExtra("harga_maksimal");
        fhargamin = i.getStringExtra("harga_minimal");
        fsertifikasi = i.getStringExtra("sertifikasi");
        if(fhargamaks!=null&&fhargamin!=null&fsertifikasi!=null) {
            if (fhargamaks.equals("Ke Atas")) {
                url = "http://mylandmark.esy.es/mylandmark/landmark.php?tag=harga_minimal&harga_minimal=" + fhargamin + "&sertifikasi=" + fsertifikasi;
                //   new NetCheck().execute();
            } else if (fhargamin.equals("Ke Bawah")) {
                url = "http://mylandmark.esy.es/mylandmark/landmark.php?tag=harga_maksimal&harga_maksimal=" + fhargamaks + "&sertifikasi=" + fsertifikasi;
                //   new NetCheck().execute();

            } else {
                url = "http://mylandmark.esy.es/mylandmark/landmark.php?tag=keduanya&harga_minimal=" + fhargamin + "&harga_maksimal=" + fhargamaks + "&sertifikasi=" + fsertifikasi;
                //   new NetCheck().execute();
            }
        }
        else {url="http://mylandmark.esy.es/mylandmark/landmark.php";}
        new NetCheck().execute();
    }

    public class AmbilData extends AsyncTask<String,String,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           }

        @Override
        protected String doInBackground(String... arg0) {
            JSONObject json = jParser.getJSONFromUrl(url);
            try {
                Log.e("error",json.getString("success"));
                peta = json.getJSONArray("daftar_iklan");
                    for (int i = 0; i < peta.length(); i++) {
                        JSONObject c = peta.getJSONObject(i);
                        map = new HashMap<>();
                        String id_1 = c.getString("id").trim();
                        String latitude_1 = c.getString("latitude");
                        String longitude_1 = c.getString("longitude");
                        String alamat_1 = c.getString("alamat");
                        String nama_1 = c.getString("nama");
                        String deskripsi_1 = c.getString("deskripsi");
                        String cp_1 = c.getString("cp");
                        String harga_1 = c.getString("harga");
                        String namapemilik_1 = c.getString("name");
                        String sertifikasi_1 = c.getString("sertifikasi");
                        map.put("id", id_1);
                        map.put("nama", nama_1);
                        map.put("latitude", latitude_1);
                        map.put("longitude", longitude_1);
                        map.put("alamat", alamat_1);
                        map.put("deskripsi", deskripsi_1);
                        map.put("name", namapemilik_1);
                        map.put("cp", cp_1);
                        map.put("harga", harga_1);
                        map.put("sertifikasi", sertifikasi_1);
                        dataList.add(map);
                    }
            } catch (JSONException e) {
                pDialog.dismiss();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
                for (int x = 0; x < dataList.size(); x = x + 1) {

                    double latasal = Double.parseDouble(dataList.get(x)
                            .get("latitude"));
                    double longasal = Double.parseDouble(dataList.get(x)
                            .get("longitude"));
                    LatLng marker = new LatLng(latasal, longasal);
                    String nama = dataList.get(x)
                            .get("nama");
                    String harga = dataList.get(x).get("harga");
                    googleMap.addMarker(new MarkerOptions().position(marker).title(nama).snippet("Rp " + harga));

                }
            lve.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    lve.getItemAtPosition(i);
                    googleMap.animateCamera(CameraUpdateFactory
                            .newLatLngZoom(new LatLng(Double.parseDouble(dataList.get(i).get("latitude")), Double.parseDouble(dataList.get(i).get("longitude"))), 21));
                }
                });


            }
    }

    public class putdata extends AsyncTask<String, String, String> {

        ArrayList<HashMap<String, String>> dataList2 = new ArrayList<HashMap<String, String>>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(peta_geografis.this);
            pDialog.setMessage("Loading Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            JSONObject json = jParser.getJSONFromUrl(url);
            try {
                success = json.getString("success");
                JSONArray peta = json.getJSONArray("daftar_iklan");
                if (success.equals("1")) {
                    for (int i = 0; i < peta.length(); i++) {
                        JSONObject c = peta.getJSONObject(i);
                        HashMap<String, String> map = new HashMap<String, String>();
                        String id = c.getString("id").trim();
                        String latitude = c.getString("latitude");
                        String longitude = c.getString("longitude");
                        String nama = c.getString("nama");
                        String hargaiklan = c.getString("harga");
                        String sertifikasi = c.getString("sertifikasi");
                        map.put("id", id);
                        map.put("nama", nama);
                        map.put("latitude", latitude);
                        map.put("longitude", longitude);
                        map.put("harga",hargaiklan);
                        map.put("sertifikasi",sertifikasi);
                        dataList2.add(map);
                    }
                } else {
                    Log.e("erro", "tidak bisa ambil data 0");
                    for (int i = 0; i < peta.length(); i++) {
                        JSONObject c = peta.getJSONObject(i);
                        HashMap<String, String> map = new HashMap<String, String>();
                        String id = c.getString("id").trim();
                        String latitude = c.getString("latitude");
                        String longitude = c.getString("longitude");
                        String nama = c.getString("nama");
                        String hargaiklan = c.getString("harga");
                        String sertifikasi = c.getString("sertifikasi");
                        map.put("id", id);
                        map.put("nama", nama);
                        map.put("latitude", latitude);
                        map.put("longitude", longitude);
                        map.put("harga",hargaiklan);
                        map.put("sertifikasi",sertifikasi);
                        dataList2.add(map);
                    }
                }
            } catch (Exception e) {
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
                final ListAdapter adapter = new SimpleAdapter(getApplicationContext(),
                        dataList2, R.layout.list_item, new String[]{"nama", "harga"}, new int[]{R.id.nama, R.id.harga});
                lve.setAdapter(adapter);
            } else {
                Toast.makeText(getApplicationContext(), "data tidak ada",Toast.LENGTH_LONG).show();
            }
        }
        }


    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.animateCamera(CameraUpdateFactory
                .newLatLngZoom(new LatLng(-7.045215,110.438905),14));
        googleMap.setOnMarkerClickListener(this);

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        // TODO Auto-generated method stub

        String id= marker.getId();
        id = id.substring(1);
        Intent x = new Intent(getApplicationContext(), DetailMarker.class);
        x.putExtra("judul", dataList.get(Integer.parseInt(id)).get("nama"));
        x.putExtra("alamat", dataList.get(Integer.parseInt(id)).get("alamat"));
        x.putExtra("deskripsi", dataList.get(Integer.parseInt(id)).get("deskripsi"));
        x.putExtra("cp",dataList.get(Integer.parseInt(id)).get("cp"));
        x.putExtra("namapemilik",dataList.get(Integer.parseInt(id)).get("name"));
        x.putExtra("harga",dataList.get(Integer.parseInt(id)).get("harga"));
        x.putExtra("sertifikasi",dataList.get(Integer.parseInt(id)).get("sertifikasi"));
        startActivity(x);
        return false;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //jika tombol BACK ditekan
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent i = new Intent(peta_geografis.this, MainActivity.class);
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
            nDialog = new ProgressDialog(peta_geografis.this);
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

            if(th){
                nDialog.dismiss();
                new putdata().execute();
               new AmbilData().execute();

            }
            else{
                nDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Peringatan, Cek Koneksi Internet",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(peta_geografis.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }
    }
   public void onSearch(View view){
        MaterialEditText etsearch = (MaterialEditText)findViewById(R.id.txtsearch);
        String lokasialamat = etsearch.getText().toString();
        if(lokasialamat!=null || !lokasialamat.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            List<Address> addressList = null;
            try {
                addressList = geocoder.getFromLocationName(lokasialamat, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng alamatdaerah = new LatLng(address.getLatitude(),address.getLongitude());
            googleMap.animateCamera(CameraUpdateFactory
                    .newLatLngZoom(alamatdaerah,14));
        }
    }
  /*  @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(resultCode== Activity.RESULT_OK&& data!=null){
            switch (requestCode){
                case 100:
                    Bundle returnData = data.getExtras();
                    int from = returnData.getInt("to_filter");
                    fhargamaks = returnData.getString("harga_maksimal");
                    fhargamin = returnData.getString("harga_minimal");
                    fsertifikasi = returnData.getString("sertifikasi");
                    if(from==1){
                        if(fhargamaks.equals("Ke Atas")){
                            url="http://mylandmark.esy.es/mylandmark/landmark.php?tag=harga_minimal&harga_minimal="+ fhargamin +"&sertifikasi="+fsertifikasi;
                         //   new NetCheck().execute();
                        }
                        else if(fhargamin.equals("Ke Bawah")){
                            url="http://mylandmark.esy.es/mylandmark/landmark.php?tag=harga_maksimal&harga_maksimal="+ fhargamaks +"&sertifikasi="+fsertifikasi;
                         //   new NetCheck().execute();

                        }
                        else {
                            url="http://mylandmark.esy.es/mylandmark/landmark.php?tag=keduanya&harga_minimal="+ fhargamin + "&harga_maksimal=" + fhargamaks+"&sertifikasi="+fsertifikasi;
                         //   new NetCheck().execute();
                        }
                    }
            }
        }
    }*/
 /* @Override
  protected void onRestart() {

      // TODO Auto-generated method stub
      super.onRestart();
      Intent i = new Intent(peta_geografis.this,peta_geografis.class);
      i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
      startActivity(i);
      finish();

  }
  */
}
