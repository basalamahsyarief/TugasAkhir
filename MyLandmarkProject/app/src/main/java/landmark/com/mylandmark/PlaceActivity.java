package landmark.com.mylandmark;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

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


public class PlaceActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMapClickListener {
    ProgressDialog pDialog,pdialog2;
    ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
    JSONParser jParser = new JSONParser();
    HashMap<String, String> map;
    String status= "1";
    Button map_normal,map_satelit,map_terrain,map_hybrid;
    JSONArray peta = null;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private MarkerOptions markerOptions;
    private Marker markeruser = null;
    private String alamat = "";
    private int from = 1;
    Context c = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        from = getIntent().getIntExtra("from_textfield", 1);

        markerOptions = new MarkerOptions();
        map_normal = (Button)findViewById(R.id.map_normalp);
        map_satelit = (Button)findViewById(R.id.map_satelitp);
        map_hybrid = (Button) findViewById(R.id.map_hybridp);
        map_terrain = (Button)findViewById(R.id.map_terrainp);
        //       setUpMapIfNeeded();

        setUpMap();
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
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        markerOptions = new MarkerOptions();

    }

    @Override
    public void onMapClick(LatLng latLng) {
        mMap.clear();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        markerOptions.position(latLng);
        markerOptions.title("lokasi baru");
        markerOptions.snippet("lat:" + String.valueOf(latLng.latitude) + ",Lng:" + String.valueOf(latLng.longitude));
        mMap.addMarker(markerOptions).showInfoWindow();
        new ReverseGeoCodingTask(c).execute(latLng);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        new NetCheck().execute();
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-7.045215, 110.438905), 14));
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(this);
        map_normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });
        map_satelit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });
        map_hybrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        });
        map_terrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            }
        });
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                double lat = marker.getPosition().latitude;
                double lng = marker.getPosition().longitude;
                Intent iBack = getIntent();
                Bundle dataBack = iBack.getExtras();
                dataBack.putDouble("latitude", lat);
                dataBack.putDouble("longitude", lng);
                dataBack.putString("alamat", alamat);
                iBack.putExtras(dataBack);
                setResult(Activity.RESULT_OK, iBack);
                finish();
            }
        });

    }

    private Marker addMarker(Double lat, Double lng, String judul) {
        markerOptions.position(new LatLng(lat, lng));
        markerOptions.title(judul);

        return mMap.addMarker(markerOptions);
    }
    private class ReverseGeoCodingTask extends AsyncTask<LatLng, Void, String> {//
        Context mContext;

        public ReverseGeoCodingTask(Context context) {
            super();
            mContext = context;
        }

        //finding address using reverse geocoding
        @Override
        protected String doInBackground(LatLng... params) {//menerima parameter array
            Geocoder geocoder = new Geocoder(mContext);
            double latitude = params[0].latitude;
            double longitude = params[0].longitude;
            List<Address> adresses = null;
            String addressText = "";
            try {
                adresses = geocoder.getFromLocation(latitude, longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (adresses != null && adresses.size() > 0) {
                android.location.Address address = adresses.get(0);

                addressText = String.format("%s, %s, %s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getLocality(),
                        address.getCountryName());
            }
            return addressText;
        }
        @Override
        protected void onPostExecute(String addressText) {
            //Setting the title for the marker
            //this will displayed on taping the marker
            markerOptions.title("Lokasi Tanah");
            markerOptions.snippet(addressText);

            alamat = addressText; //dataBack Geocoder

            //placing a marker on the touched position
            mMap.addMarker(markerOptions).showInfoWindow();
        }
    }
    public class AmbilData extends AsyncTask<String,String,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... arg0) {
            String url;
            url = "http://mylandmark.esy.es/mylandmark/landmark.php";
            JSONObject json = jParser.getJSONFromUrl(url);
            try {
                peta = json.getJSONArray("daftar_iklan");
                Log.e("error", json.getString("success"));
                for (int i = 0; i < peta.length(); i++) {
                    JSONObject c = peta.getJSONObject(i);
                    map = new HashMap<>();
                    String id_1 = c.getString("id").trim();
                    String latitude_1 = c.getString("latitude").trim();
                    String longitude_1 = c.getString("longitude").trim();
                    String alamat_1 = c.getString("alamat").trim();
                    String nama_1 = c.getString("nama").trim();
                    String deskripsi_1 = c.getString("deskripsi").trim();
                    String cp_1 = c.getString("cp").trim();
                    String namapemilik_1 = c.getString("name");
                    map.put("id", id_1);
                    map.put("nama", nama_1);
                    map.put("latitude", latitude_1);
                    map.put("longitude", longitude_1);
                    map.put("alamat",alamat_1);
                    map.put("deskripsi", deskripsi_1);
                    map.put("name",namapemilik_1);
                    map.put("cp",cp_1);

                    dataList.add(map);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (status.equals("0")) {
                Toast.makeText(getApplicationContext(), "data tidak ada",
                        Toast.LENGTH_SHORT).show();

            }
            for (int x = 0; x < dataList.size(); x = x + 1) {

                double latasal = Double.parseDouble(dataList.get(x)
                        .get("latitude"));
                double longasal = Double.parseDouble(dataList.get(x)
                        .get("longitude"));
                LatLng marker = new LatLng(latasal, longasal);
                String nama = dataList.get(x)
                        .get("nama");

                mMap.addMarker(new MarkerOptions().position(marker).title(nama));
            }

        }
    }

    private class NetCheck extends AsyncTask<String,String,Boolean>
    {
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(PlaceActivity.this);
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
                new AmbilData().execute();
            }
            else{
                nDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Peringatan, Cek Koneksi Internet",Toast.LENGTH_SHORT).show();
            }
        }
    }
}