package digitalmidges.com.sensorsexample;

import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * Created with care by odedfunt on 14/01/2019.
 * <p>
 * TODO: Add a class header comment!
 */
public class MainActivity extends AppCompatActivity {


    private ToggleButton mTbWifi;
    private ToggleButton mTbBluetooth;
    private ToggleButton mTbGps;
    private ToggleButton mTbDimScreen;
    private SeekBar mSeekBar;

   private  BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        beforeInit();
        init();
        afterInit();

    }

    private void beforeInit() {

    }

    private void init() {
        mTbWifi = findViewById(R.id.tbWifi);
        mTbBluetooth = findViewById(R.id.tbBluetooth);
        mTbGps = findViewById(R.id.tbGPS);
        mTbDimScreen = findViewById(R.id.tbDimScreen);
        mSeekBar = findViewById(R.id.seekBar);
    }

    private void afterInit() {
        setDefaultViewsBehavior();
        setViewsClickListeners();
        setSeekbar();
    }




    private void setDefaultViewsBehavior() {

        mTbWifi.setChecked(isWifiEnabled());
        mTbBluetooth.setChecked(mBluetoothAdapter.isEnabled());
        mTbDimScreen.setChecked(false);
        mSeekBar.setVisibility(View.GONE);
        setScreenFullBrightIfPermissionsHasGiven();
    }

    private boolean isWifiEnabled() {

        WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        return wifiMgr.isWifiEnabled();
    }


    private void setViewsClickListeners() {


        mTbWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setWifiMode(isWifiEnabled());
            }
        });

        mTbBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (mBluetoothAdapter.isEnabled()){
                   turnBluetootOff();
               }else{
                   turnBluetoothOn();
               }
            }
        });

        mTbGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                turnGPSOff();

                Toast.makeText(MainActivity.this,"MUST HAVE SECURE SETTINGS PERMISSION TO MANAGE THAT",Toast.LENGTH_LONG).show();
            }
        });

        mTbDimScreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    dimScreen(mSeekBar.getProgress());
                    mSeekBar.setVisibility(View.VISIBLE);
                }else{
                    setScreenFullBrightIfPermissionsHasGiven();
                    mSeekBar.setVisibility(View.GONE);
                }
            }
        });
//        mTbDimScreen.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if (mTbDimScreen.isChecked()){
//                    mSeekBar.setVisibility(View.GONE);
//                }else{
//                    mSeekBar.setVisibility(View.VISIBLE);
//                }
////                dimScreen();
//            }
//        });


    }

    float getScreenBrightness() {
        float brightness = 100;
        if (hasWriteSettingsPermission()) {

            // Get system brightness
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC); // enable auto brightness
            brightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, -1);  // in the range [0, 255]
        }
        return brightness;
    }

    private void setSeekbar() {



        mSeekBar.setMax(255);
        mSeekBar.setProgress((int) getScreenBrightness());
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {

                if (fromUser){
                    dimScreen(progressValue);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }


    private void dimScreen(int screenBrightnessValue) {
        // Get app context object.
        Context context = getApplicationContext();

        // Check whether has the write settings permission or not.
        boolean settingsCanWrite = hasWriteSettingsPermission();


        // If do not have then open the Can modify system settings panel.
        if(!settingsCanWrite) {
            changeWriteSettingsPermission(context);
        }else {
            changeScreenBrightness(screenBrightnessValue);
        }
    }


    // Check whether this app has android write settings permission.
    private boolean hasWriteSettingsPermission()
    {
        boolean ret = true;
        // Get the result from below code.
        ret = Settings.System.canWrite(this);
        return ret;
    }

    // Start can modify system settings panel to let user change the write settings permission.
    private void changeWriteSettingsPermission(Context context)
    {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        context.startActivity(intent);
    }


    private void setScreenFullBrightIfPermissionsHasGiven(){
        // Check whether has the write settings permission or not.
        boolean settingsCanWrite = hasWriteSettingsPermission();
        if (settingsCanWrite){
            changeScreenBrightness(255);

        }
    }

    private void changeScreenBrightness(int screenBrightnessValue)
    {
        // Change the screen brightness change mode to manual.
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        // Apply the screen brightness value to the system, this will change the value in Settings ---> Display ---> Brightness level.
        // It will also change the screen brightness for the device.
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, screenBrightnessValue);

        /*
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.screenBrightness = screenBrightnessValue / 255f;
        window.setAttributes(layoutParams);
        */
    }


    private void setWifiMode(boolean isWifiEnabled) {
        WifiManager wifiManager = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(!isWifiEnabled);
        mTbWifi.setChecked(!isWifiEnabled);
    }

    private void turnBluetoothOn(){
        mBluetoothAdapter.enable();
        mTbBluetooth.setChecked(true);
    }

    private void turnBluetootOff(){
        mBluetoothAdapter.disable();
        mTbBluetooth.setChecked(false);
    }

    private void turnGPSOn(){
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(!provider.contains("gps")){ //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
    }

    private void turnGPSOff(){
        final ContentResolver cr = getContentResolver();
        Settings.Secure.setLocationProviderEnabled(cr,
                LocationManager.NETWORK_PROVIDER, false);
    }


}
