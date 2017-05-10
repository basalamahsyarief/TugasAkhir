package landmark.com.mylandmark;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class Filter extends AppCompatActivity {
    Spinner filter_harga_minimum, filter_harga_maksimum, filter_sertifikasi;
    Button hasil;
    private int from = 1;
    String hargamax,hargamin,sertifikasi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        from = getIntent().getIntExtra("to_filter", 1);
        filter_harga_minimum = (Spinner) findViewById(R.id.filter_harga_minimum);
        filter_harga_maksimum = (Spinner) findViewById(R.id.filter_harga_maksimum);
        filter_sertifikasi = (Spinner)findViewById(R.id.filter_sertifikasi);
        hasil = (Button)findViewById(R.id.hasil);
        filter_harga_maksimum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hargamax =String.valueOf(parent.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        filter_harga_minimum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hargamin = String.valueOf(parent.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        filter_sertifikasi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        hasil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hargamax.equals("Ke Atas")&&hargamin.equals("Ke Bawah")){
                    Toast.makeText(Filter.this, "Parameter Salah", Toast.LENGTH_SHORT).show();
                }
                else {
             /*       Intent iBack = getIntent();
                    Bundle dataBack = iBack.getExtras();
                    dataBack.putString("harga_minimal", hargamin);
                    dataBack.putString("harga_maksimal", hargamax);
                    dataBack.putString("sertifikasi", sertifikasi);
                    iBack.putExtras(dataBack);
                    setResult(Activity.RESULT_OK, iBack);*/
                    Intent i = new Intent(getApplicationContext(),peta_geografis.class);
                    i.putExtra("harga_minimal",hargamin);
                    i.putExtra("harga_maksimal",hargamax);
                    i.putExtra("sertifikasi",sertifikasi);
                    startActivity(i);
                    finish();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_filter, menu);
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
            Intent i = new Intent(Filter.this, peta_geografis.class);
            startActivity(i);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}
