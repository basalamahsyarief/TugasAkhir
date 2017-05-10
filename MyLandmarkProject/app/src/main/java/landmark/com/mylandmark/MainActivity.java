package landmark.com.mylandmark;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.vstechlab.easyfonts.EasyFonts;

public class MainActivity extends AppCompatActivity {
    DialogInterface.OnClickListener listener;
    TextView mylandmark,hashtag;
    ImageView user_account,view_map,about,register;
    protected AlphaAnimation btnAnimasi = new AlphaAnimation(1F, 0.5F);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user_account = (ImageView)findViewById(R.id.user_account);
        view_map = (ImageView)findViewById(R.id.view_map);
        about = (ImageView)findViewById(R.id.about);
        register = (ImageView)findViewById(R.id.register);
        mylandmark = (TextView)findViewById(R.id.mylandmark);
        hashtag = (TextView) findViewById(R.id.hashtag);
        mylandmark.setTypeface(EasyFonts.walkwayBlack(this));
        hashtag.setTypeface(EasyFonts.tangerineRegular(this));
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(btnAnimasi);
                Intent i = new Intent(getApplicationContext(),Pendaftaran.class);
                startActivity(i);
                finish();
            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(btnAnimasi);
                Intent i = new Intent(getApplicationContext(),About.class);
                startActivity(i);
                finish();
            }
        });
        view_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("myapp","go to my landmark!");
                view.startAnimation(btnAnimasi);
                Intent i = new Intent(getApplicationContext(),peta_geografis.class);
                startActivity(i);
                finish();
            }
        });
        user_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("myapp","go to my landmark!");
                view.startAnimation(btnAnimasi);
                Intent i = new Intent(getApplicationContext(),Login.class);
                startActivity(i);
                finish();
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //jika tombol BACK ditekan
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Keluar();
        }
        return super.onKeyDown(keyCode,event );
    }
    //method untuk keluar aplikasi menggunakan dialog terlebih dahulu
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

}
