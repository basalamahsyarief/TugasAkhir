package landmark.com.mylandmark;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DetailMarker extends AppCompatActivity {
    TextView nama,deskripsi,alamat,namapemilik,cp,harga,sertifikasi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_marker);
        nama = (TextView)findViewById(R.id.marker_nama);
        alamat = (TextView)findViewById(R.id.marker_alamat);
        deskripsi = (TextView)findViewById(R.id.marker_deskripsi);
        namapemilik = (TextView)findViewById(R.id.marker_namapemilik);
        cp = (TextView)findViewById(R.id.marker_cp);
        harga = (TextView)findViewById(R.id.marker_harga);
        sertifikasi = (TextView)findViewById(R.id.marker_sertifikasi);
        Intent i = getIntent();
        nama.setText(i.getStringExtra("judul"));
        namapemilik.setText(i.getStringExtra("namapemilik"));
        cp.setText(i.getStringExtra("cp"));
        alamat.setText(i.getStringExtra("alamat"));
        deskripsi.setText(i.getStringExtra("deskripsi"));
        harga.setText("Rp " + i.getStringExtra("harga"));
        sertifikasi.setText(i.getStringExtra("sertifikasi"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_marker, menu);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //jika tombol BACK ditekan
        if(keyCode == KeyEvent.KEYCODE_BACK){
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
