package com.example.alex.finalmk4;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hoho.android.usbserial.driver.CdcAcmSerialDriver;
import com.hoho.android.usbserial.driver.ProbeTable;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.graphics.Color.blue;
import static android.graphics.Color.green;
import static android.graphics.Color.red;
import static android.graphics.Color.rgb;

public class MainActivity extends Activity implements TextureView.SurfaceTextureListener {
    private Camera mCamera;
    private TextureView mTextureView;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Bitmap bmp = Bitmap.createBitmap(640, 480, Bitmap.Config.ARGB_8888);
    private Canvas canvas = new Canvas(bmp);
    private Paint paint1 = new Paint();
    private TextView mTextView;
    private UsbManager manager;
    private UsbSerialPort sPort;
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private SerialInputOutputManager mSerialIoManager;
    SeekBar myControl;
    SeekBar myControl2;
    SeekBar myControl3;
    SeekBar myControl4;



    TextView myTextView;
    TextView myTextView2;
    TextView myTextView4;
    TextView myTextView5;
    TextView myTextView6;
    TextView myTextView7;
    TextView myTextView8;
    TextView myTextView9;



    int range;
    int thresh;
    int gain;
    int cent;
    int PWMright;
    int PWMleft;
    int counter;
    int MAX_DUTY;




    private final SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {
                @Override
                public void onRunError(Exception e) {

                }

                @Override
                public void onNewData(final byte[] data) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.this.updateReceivedData(data);
                        }
                    });
                }
            };

    @Override
    protected void onPause(){
        super.onPause();
        stopIoManager();
        if(sPort != null){
            try{
                sPort.close();
            } catch (IOException e){ }
            sPort = null;
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        ProbeTable customTable = new ProbeTable();
        customTable.addProduct(0x04D8,0x000A, CdcAcmSerialDriver.class);
        UsbSerialProber prober = new UsbSerialProber(customTable);

        final List<UsbSerialDriver> availableDrivers = prober.findAllDrivers(manager);

        if(availableDrivers.isEmpty()) {
            //check
            return;
        }

        UsbSerialDriver driver = availableDrivers.get(0);
        sPort = driver.getPorts().get(0);

        if (sPort == null){
            //check
        }else{
            final UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
            UsbDeviceConnection connection = usbManager.openDevice(driver.getDevice());
            if (connection == null){
                //check
                PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent("com.android.example.USB_PERMISSION"), 0);
                usbManager.requestPermission(driver.getDevice(), pi);
                return;
            }

            try {
                sPort.open(connection);
                sPort.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);

            }catch (IOException e) {
                //check
                try{
                    sPort.close();
                } catch (IOException e1) { }
                sPort = null;
                return;
            }
        }
        onDeviceStateChange();
    }

    private void stopIoManager(){
        if(mSerialIoManager != null) {
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager() {
        if(sPort != null){
            mSerialIoManager = new SerialInputOutputManager(sPort, mListener);
            mExecutor.submit(mSerialIoManager);
        }
    }

    private void onDeviceStateChange(){
        stopIoManager();
        startIoManager();
    }

    private void updateReceivedData(byte[] data) {
        //do something with received data

        //for displaying:
        String rxString = null;
        try {
            rxString = new String(data, "UTF-8"); // put the data you got into a string
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    static long prevtime = 0; // for FPS calculation

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // keeps the screen from turning off

        mTextView = (TextView) findViewById(R.id.cameraStatus);

        // see if the app has permission to use the camera
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);
            mSurfaceHolder = mSurfaceView.getHolder();

            mTextureView = (TextureView) findViewById(R.id.textureview);
            mTextureView.setSurfaceTextureListener(this);

            // set the paintbrush for writing text on the image
            paint1.setColor(0xffff0000); // red
            paint1.setTextSize(24);

            mTextView.setText("started camera");
        } else {
            mTextView.setText("no camera permissions");
        }

        myControl = (SeekBar) findViewById(R.id.seek1);
        myControl2 = (SeekBar) findViewById(R.id.seek2);
        myControl3 = (SeekBar) findViewById(R.id.seek3);
        myControl4 = (SeekBar) findViewById(R.id.seek4);




        myTextView = (TextView) findViewById(R.id.textView01);
        myTextView.setText("Range");

        myTextView2 = (TextView) findViewById(R.id.textView02);
        myTextView2.setText("Threshold");

        myTextView4 = (TextView) findViewById(R.id.textView04);
        myTextView4.setText("PWMleft");

        myTextView5 = (TextView) findViewById(R.id.textView05);
        myTextView5.setText("PWMright");

        myTextView6 = (TextView) findViewById(R.id.textView06);
        myTextView6.setText("Upper");

        myTextView7 = (TextView) findViewById(R.id.textView07);
        myTextView7.setText("Lower");

        myTextView8 = (TextView) findViewById(R.id.textView08);
        myTextView8.setText("Gain");

        myTextView9 = (TextView) findViewById(R.id.textView09);
        myTextView9.setText("Max Duty");


        setMyControlListener();

        manager = (UsbManager) getSystemService(Context.USB_SERVICE);

    }

    private void setMyControlListener() {
        myControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int progressChanged = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
                range = progress;
                myTextView.setText("Range is: "+ progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        myControl2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int progressChanged = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
                thresh = progress;
                myTextView2.setText("Threshold is: "+ progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        myControl3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int progressChanged = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
                gain = progress;
                myTextView8.setText("Gain is: "+ progress*.03);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        myControl4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int progressChanged = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
                MAX_DUTY = 12*progress;
                myTextView9.setText("Max_Duty is: "+ progress*12);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }


    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mCamera = Camera.open();
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(640, 480);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY); // no autofocusing
        parameters.setAutoExposureLock(true); // keep the white balance constant
        mCamera.setParameters(parameters);
        mCamera.setDisplayOrientation(90); // rotate to portrait mode

        try {
            mCamera.setPreviewTexture(surface);
            mCamera.startPreview();
        } catch (IOException ioe) {

        }
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        // Ignored, Camera does all the work for us
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mCamera.stopPreview();
        mCamera.release();
        return true;
    }

    // the important function
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // every time there is a new Camera preview frame
        mTextureView.getBitmap(bmp);
        int left;
        int right;



        final Canvas c = mSurfaceHolder.lockCanvas();

        if (c != null) {
            int[] pixels = new int[bmp.getWidth()]; // pixels[] is the RGBA data

            int startY = bmp.getHeight()*2/3; // which row in the bitmap to analyze to read
            bmp.getPixels(pixels, 0, bmp.getWidth(), 0, startY, bmp.getWidth(), 1);

            int sum_mr = 0; // the sum of the mass times the radius
            int sum_m = 0; // the sum of the masses
            int R;
            int T;

            for (int i = 0; i < bmp.getWidth(); i++) {
                R = range;
                T = thresh;
                if (((green(pixels[i]) - red(pixels[i])) > -R)&&((green(pixels[i]) - red(pixels[i])) < R)&&(green(pixels[i])  > T)) {
                    pixels[i] = rgb(1, 1, 1); // set the pixel to almost 100% black

                    sum_m = sum_m + green(pixels[i])+red(pixels[i])+blue(pixels[i]);
                    sum_mr = sum_mr + (green(pixels[i])+red(pixels[i])+blue(pixels[i]))*i;
                }
            }
            // only use the data if there were a few pixels identified, otherwise you might get a divide by 0 error
            if(sum_m>5){
                cent = sum_mr / sum_m;
                // send to pic
                counter ++;

            }
            else{
                cent = 0;
            }

            int pos = cent;
            canvas.drawCircle(pos, startY, 5, paint1); // x position, y position, diameter, color







            int error;


            error = cent - 240; // 240 means the dot is in the middle of the screen
            if (error<0) { // slow down the left motor to steer to the left
                error  = -error;
                left = MAX_DUTY - (gain*3*error/100);
                right = MAX_DUTY + (gain*3*error/100);
                if (left < 0){
                    left = 0;
                }
            }
            else { // slow down the right motor to steer to the right
                right = MAX_DUTY - (gain*3*error/100);
                left = MAX_DUTY + (gain*3*error/100);
                if (right<0) {
                    right = 0;
                }
            }

            PWMleft = left;
            PWMright = right;

            myTextView4.setText("PWMleft is: "+ PWMleft);
            myTextView5.setText("PWMright is: "+ PWMright);

            String sendString = Integer.toString(PWMright) + " " + Integer.toString(PWMleft) + '\n';
            try {
                sPort.write(sendString.getBytes(), 10); // 10 is the timeout
            } catch (IOException e) { }

            counter = 0;

        }


        // write the pos as text
        //canvas.drawText("pos = " + pos, 10, 200, paint1);
        c.drawBitmap(bmp, 0, 0, null);
        mSurfaceHolder.unlockCanvasAndPost(c);

        // calculate the FPS to see how fast the code is running
        long nowtime = System.currentTimeMillis();
        long diff = nowtime - prevtime;
        mTextView.setText("FPS " + 1000 / diff);
        prevtime = nowtime;
    }
}