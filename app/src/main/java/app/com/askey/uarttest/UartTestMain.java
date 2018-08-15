package app.com.askey.uarttest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import android_serialport_api.SerialPort;

public class UartTestMain extends Activity {
    private static final String TAG = "UartTestMain";

    //UART
    protected SerialPort mSerialPort;
    protected InputStream mInputStream;
    protected OutputStream mOutputStream;
    private RadioGroup mRadioGroup;
    private RadioButton mRadioButton0, mRadioButton1, mRadioButton2;
    private Button mWriteBtn, mReadBtn, mClearBtn;
    private EditText minputEditText, mEditTextReception;
    private String mInputStr;
    private int flag=3;

    //GPIO TEST
    //private String gpio10_path = "/dev/READING_PW_EN";  //set 0=Low  1=high,  control Level Shift
    //private String gpio89_path= "/dev/KEY_TOP_1";  //KEY_TOP_1, gpio89

    //private String gpio_test= "/dev/EXTERNAL_PORT_EN";  //SAM TEST
    //GPIO END

    private ScrollView mScrollView;
    private TextView mLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uart_test_main);
        mRadioButton0 = (RadioButton) findViewById(R.id.port_ttyHSL0);
        mRadioButton1 = (RadioButton) findViewById(R.id.port_ttyHSL1);
        mRadioButton2 = (RadioButton) findViewById(R.id.port_ttyHSL2);
        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
        mRadioGroup.setOnCheckedChangeListener(radGrpRegionOnCheckedChange);
        minputEditText = (EditText) findViewById(R.id.editText1);
        int inputType = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL;
        minputEditText.setInputType(inputType);
        mEditTextReception = (EditText) findViewById(R.id.editText2);
        mWriteBtn = (Button) findViewById(R.id.write_btn);
        mReadBtn = (Button) findViewById(R.id.read_btn);
        mClearBtn = (Button) findViewById(R.id.clear_btn);

        mScrollView = (ScrollView) findViewById(R.id.scrollView);
        mLog = (TextView) findViewById(R.id.TextView2);

        //GPIO TEST
        //  writeToGpio("1", gpio10_path);  // "1"= high , "0"= Low, control Level Shift
        // writeToGpio("1", gpio89_path);   //KEY_TOP_1, gpio89

        // writeToGpio("1", gpio_test);   //SAM TEST
        //GPIO END

        mWriteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mInputStr = minputEditText.getText().toString();
                switch (flag) {
                    case 0:
                        writeToFile("/dev/ttyHSL0", mInputStr);
                        break;
                    case 1:
                        writeToFile("/dev/ttyHSL1", mInputStr);
                        break;
                    case 2:
                        writeToFile("/dev/ttyHSL2", mInputStr);
                        break;
                }
            }
        });


        mReadBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                switch (flag) {
                    case 0:
                        readFile("/dev/ttyHSL0");
                        break;
                    case 1:
                        readFile("/dev/ttyHSL1");
                        break;
                    case 2:
                        readFile("/dev/ttyHSL2");
                        break;
                }
            }
        });

        mClearBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mEditTextReception.setText("");
                mLog.setText("");
            }

        });
    }

    private RadioGroup.OnCheckedChangeListener radGrpRegionOnCheckedChange = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.port_ttyHSL0:
                    Log.v(TAG, "choose port_ttyHSL0");
                    flag=0;
                    try {
                        mSerialPort = new SerialPort(new File("/dev/ttyHSL0"), 115200, 0);
                        mInputStream = mSerialPort.getInputStream();
                        mOutputStream = mSerialPort.getOutputStream();
                        mLog.append("open UART : " + "/dev/ttyHSL0" + "\n");
                        mScrollView.fullScroll(View.FOCUS_DOWN);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                        mLog.append("open  UART  failed SecurityException : " + e + "\n");
                        Log.e(TAG, "open  UART failed SecurityException : " + e);
                        mScrollView.fullScroll(View.FOCUS_DOWN);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "open  UART failed IOException : " + e);
                        mLog.append("open  UART failed IOException : " + e + "\n");
                        mScrollView.fullScroll(View.FOCUS_DOWN);
                    }
                    Toast.makeText(getApplicationContext(), "open /dev/ttyHSL0",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.port_ttyHSL1:
                    Log.v(TAG, "choose port_ttyHSL1");
                    flag=1;
                    try {
                        mSerialPort = new SerialPort(new File("/dev/ttyHSL1"), 115200, 0);
                        mInputStream = mSerialPort.getInputStream();
                        mOutputStream = mSerialPort.getOutputStream();
                        mLog.append("open  UART : " + "/dev/ttyHSL1" + "\n");
                        mScrollView.fullScroll(View.FOCUS_DOWN);

                    } catch (SecurityException e) {
                        e.printStackTrace();
                        mLog.append("open  UART failed SecurityException : " + e + "\n");
                        mScrollView.fullScroll(View.FOCUS_DOWN);
                        Log.e(TAG, "open  UART failed SecurityException : " + e);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "open  UART failed IOException : " + e);
                        mLog.append("open  UART failed IOException : " + e + "\n");
                        mScrollView.fullScroll(View.FOCUS_DOWN);
                    }
                    Toast.makeText(getApplicationContext(), "open /dev/ttyHSL1",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.port_ttyHSL2:
                    Log.v(TAG, "choose port_ttyHSL2");
                    flag=2;
                    try {
                        mSerialPort = new SerialPort(new File("/dev/ttyHSL2"), 115200, 0);
                        mInputStream = mSerialPort.getInputStream();
                        mOutputStream = mSerialPort.getOutputStream();
                        mLog.append("open  UART : " + "/dev/ttyHSL2" + "\n");
                        mScrollView.fullScroll(View.FOCUS_DOWN);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                        mLog.append("open  UART failed SecurityException : " + e + "\n");
                        mScrollView.fullScroll(View.FOCUS_DOWN);
                        Log.e(TAG, "open  UART failed SecurityException : " + e);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "open  UART failed IOException : " + e);
                        mLog.append("open  UART failed IOException : " + e + "\n");
                        mScrollView.fullScroll(View.FOCUS_DOWN);
                    }
                    Toast.makeText(getApplicationContext(), "open /dev/ttyHSL2",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void writeToFile(String path, String stBuffer ) {
        try {
            mOutputStream.write(new String(stBuffer).getBytes());
            mOutputStream.write('\n');
            mLog.append(stBuffer+" write to UART: " + path + "\n");
            mScrollView.fullScroll(View.FOCUS_DOWN);
            Log.v(TAG, stBuffer+" write to UART: " + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), stBuffer,Toast.LENGTH_SHORT).show();
    }

    private void readFile(String path) {
        int size;
        try {
            byte[] buffer = new byte[64];
            if (mInputStream == null) return;
            size = mInputStream.read(buffer);
            if (size > 0) {
                onDataReceived(buffer, size);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    void onDataReceived(final byte[] buffer, final int size) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (mEditTextReception != null) {
                    String recData = new String(buffer, 0, size );
                    if (recData == "") {
                        mEditTextReception.append("UART error" + "\n");
                        Log.d(TAG, "UART recData = "+ "error");
                    } else {
                        mEditTextReception.append(recData);
                        Log.d(TAG, "UART recData = "+ recData.getBytes());
                    }
                }
            }
        });
    }


    /**
     * GPIO  RWtest function
     * line 227~268
     */
    //GPIO  create file & write
    public void writeToGpio(String data, String gpioPath) {
        File file = new File(gpioPath);
        try {
            FileWriter writer = new FileWriter(file, false);
            writer.write(data);
            writer.close();
            mLog.append("GPIO write = " + data +" to " + gpioPath + "\n");
            mScrollView.fullScroll(View.FOCUS_DOWN);
            Log.d(TAG, "GPIO write = " + data +" to " + gpioPath);
        } catch (IOException e1) {
            e1.printStackTrace();
            Log.e(TAG, "GPIO writeData IOException=" + e1);
            mLog.append("GPIO writeData IOException=" + e1 + "\n");
            mScrollView.fullScroll(View.FOCUS_DOWN);
        }
    }

    //GPIO  read
    private void readGpio(String gpio_path) {
        try {
            String GpioValue=null;
            FileReader mFileReader = new FileReader(gpio_path);
            BufferedReader mBufferedReader = new BufferedReader(mFileReader);
            char[] buffer = new char[20];
            int s = mBufferedReader.read(buffer, 0, 20);

            GpioValue = (String.valueOf(buffer).trim());
            Log.v(TAG, "GpioValue=" + GpioValue);
            mLog.append("GpioValue=" + GpioValue + "\n");
            mScrollView.fullScroll(View.FOCUS_DOWN);
            mEditTextReception.append(GpioValue);

            mBufferedReader.close();
            mFileReader.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "GPIO readData IOException=" + e);
            mLog.append("GPIO readData IOException=" + e + "\n");
            mScrollView.fullScroll(View.FOCUS_DOWN);
        }
    }

    protected void onDestroy() {

//        writeToGpio("0", gpio10_path);  // "1"= high , "0"= Low
//        writeToGpio("0", gpio89_path);   //KEY_TOP_1, gpio89
//        writeToGpio("0", gpio_test);   //SAM TEST

        if (mSerialPort != null){
            mSerialPort.close();
            mSerialPort = null;
        }
        super.onDestroy();
    }


}
