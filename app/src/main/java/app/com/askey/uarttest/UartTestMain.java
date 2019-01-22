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
    protected SerialPort mSerialPort;
    protected InputStream mInputStream;
    protected OutputStream mOutputStream;
    private RadioGroup mRadioGroup;
    private RadioButton mRadioButton1, mRadioButton2, mRadioButton3, mRadioButton4, mRadioButton5, mRadioButton6;
    private Button mWriteBtn, mReadBtn, mClearBtn, mReOpenBtn;
    private EditText minputEditText, mEditTextReception;
    private String mInputStr;
    private int flag=0;

    private ScrollView mScrollView;
    private TextView mLog;

    //GPIO TEST
    //private String gpio10_path = "/dev/READING_PW_EN";  //set 0=Low  1=high,  control Level Shift
    //private String gpio89_path= "/dev/KEY_TOP_1";  //KEY_TOP_1, gpio89
    private String gpio_test= "/dev/EXTERNAL_PORT_EN";  //SAM TEST

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uart_test_main);
        mRadioButton1 = (RadioButton) findViewById(R.id.port_ttyHSL0);
        mRadioButton2 = (RadioButton) findViewById(R.id.port_ttyHSL1);
        mRadioButton3 = (RadioButton) findViewById(R.id.port_ttyHSL2);
        mRadioButton4 = (RadioButton) findViewById(R.id.port_ttyGS0);
        mRadioButton5 = (RadioButton) findViewById(R.id.port_ttyGS1);
        mRadioButton6 = (RadioButton) findViewById(R.id.gpio_rw);
        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
        mRadioGroup.setOnCheckedChangeListener(radGrpRegionOnCheckedChange);
        minputEditText = (EditText) findViewById(R.id.editText1);
        int inputType = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL;
        minputEditText.setInputType(inputType);
        mEditTextReception = (EditText) findViewById(R.id.editText2);
        mWriteBtn = (Button) findViewById(R.id.write_btn);
        mReadBtn = (Button) findViewById(R.id.read_btn);
        mClearBtn = (Button) findViewById(R.id.clear_btn);
        mReOpenBtn = (Button) findViewById(R.id.reopen_btn);

        mScrollView = (ScrollView) findViewById(R.id.scrollView);
        mLog = (TextView) findViewById(R.id.TextView2);

        mWriteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mInputStr = minputEditText.getText().toString();
                switch (flag) {
                    case 0:
                        Toast.makeText(getApplicationContext(), "choose one, please",Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        sendSerialPort("/dev/ttyHSL0", mInputStr);
                        break;
                    case 2:
                        sendSerialPort("/dev/ttyHSL1", mInputStr);
                        break;
                    case 3:
                        sendSerialPort("/dev/ttyHSL2", mInputStr);
                        break;
                    case 4:
                        sendSerialPort("/dev/ttyGS0", mInputStr);
                        break;
                    case 5:
                        sendSerialPort("/dev/ttyGS1", mInputStr);
                        break;
                    case 6:
                        //GPIO TEST
//                        writeToGpio("1", gpio10_path);  // "1"= high , "0"= Low, control Level Shift
//                        writeToGpio("1", gpio89_path);  //KEY_TOP_1, gpio89
                        writeToGpio(mInputStr, gpio_test);   //SAM TEST
                        break;
                }
            }
        });

        mReadBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                switch (flag) {
                    case 0:
                        Toast.makeText(getApplicationContext(), "choose one, please",Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        readFile("/dev/ttyHSL0");
                        break;
                    case 2:
                        readFile("/dev/ttyHSL1");
                        break;
                    case 3:
                        readFile("/dev/ttyHSL2");
                        break;
                    case 4:
                        readFile("/dev/ttyGS0");
                        break;
                    case 5:
                        readFile("/dev/ttyGS1");
                        break;
                    case 6:
                        readGpio(gpio_test);
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

        mReOpenBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (mSerialPort != null) {
                    mLog.append("[Return] uart is already open" +"\n"+mSerialPort+"\n");
                    mScrollView.fullScroll(View.FOCUS_DOWN);
                    Log.v(TAG, "[Return] uart is already open"+"\n");
                    Log.v(TAG, mSerialPort +"\n");
                } else {
                    Log.e(TAG, "re open"+"\n");
                    switch (flag) {
                        case 0:
                            Toast.makeText(getApplicationContext(), "choose one, please",Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            openSerialPort("/dev/ttyHSL0",115200);
                            break;
                        case 2:
                            openSerialPort("/dev/ttyHSL1",115200);
                            break;
                        case 3:
                            openSerialPort("/dev/ttyHSL2",115200);
                            break;
                        case 4:
                            openSerialPort("/dev/ttyGS0",9600);
                            break;
                        case 5:
                            openSerialPort("/dev/ttyGS1",9600);
                            break;
                        case 6:
                            break;
                    }
                }
            }
        });
    }

    private RadioGroup.OnCheckedChangeListener radGrpRegionOnCheckedChange = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.port_ttyHSL0:
                    Log.v(TAG, "choose port_ttyHSL0");
                    flag=1;
                    mReOpenBtn.setEnabled(true);
                    if (mSerialPort != null) {
                        closeSerialPort();
                    }
                    openSerialPort("/dev/ttyHSL0",115200);

                    Toast.makeText(getApplicationContext(), "choose /dev/ttyHSL0",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.port_ttyHSL1:
                    Log.v(TAG, "choose port_ttyHSL1");
                    flag=2;
                    mReOpenBtn.setEnabled(true);
                    if (mSerialPort != null) {
                        closeSerialPort();
                    }
                    openSerialPort("/dev/ttyHSL1",115200);

                    Toast.makeText(getApplicationContext(), "choose /dev/ttyHSL1",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.port_ttyHSL2:
                    Log.v(TAG, "choose port_ttyHSL2");
                    flag=3;
                    mReOpenBtn.setEnabled(true);
                    if (mSerialPort != null) {
                        closeSerialPort();
                    }
                    openSerialPort("/dev/ttyHSL2",115200);

                    Toast.makeText(getApplicationContext(), "choose /dev/ttyHSL2",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.port_ttyGS0:
                    Log.v(TAG, "choose port_ttyGS0");
                    flag=4;
                    mReOpenBtn.setEnabled(true);
                    if (mSerialPort != null) {
                        closeSerialPort();
                    }
                    openSerialPort("/dev/ttyGS0",9600);

                    Toast.makeText(getApplicationContext(), "choose /dev/ttyGS0",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.port_ttyGS1:
                    Log.v(TAG, "choose port_ttyGS1");
                    flag=5;
                    mReOpenBtn.setEnabled(true);
                    if (mSerialPort != null) {
                        closeSerialPort();
                    }
                    openSerialPort("/dev/ttyGS1",9600);

                    Toast.makeText(getApplicationContext(), "choose /dev/ttyGS1",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.gpio_rw:
                    Log.v(TAG, "choose gpio r/w");
                    flag=6;
                    mReOpenBtn.setEnabled(false);
                    if (mSerialPort != null) {
                        closeSerialPort();
                    }
                    Toast.makeText(getApplicationContext(), "choose gpio r/w",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void openSerialPort(String SerialPortpath, int baudrate) {
        if (mSerialPort == null) {
            try {
                mSerialPort = new SerialPort(new File(SerialPortpath), baudrate, 0);
                mInputStream = mSerialPort.getInputStream();
                mOutputStream = mSerialPort.getOutputStream();

                mLog.append("open  : " + SerialPortpath + "\n");
                mLog.append(mSerialPort +"\n");
                mScrollView.fullScroll(View.FOCUS_DOWN);
                Log.v(TAG, "open  : " + SerialPortpath + "\n");
                Log.v(TAG, mSerialPort+"\n");
            } catch (SecurityException e) {
                e.printStackTrace();

                mLog.append("open failed SecurityException : " + e + "\n");
                mLog.append("SerialPort : " +mSerialPort + "\n");
                mScrollView.fullScroll(View.FOCUS_DOWN);
                Log.e(TAG, "open  failed SecurityException : " + e);
            } catch (IOException e) {
                e.printStackTrace();

                mLog.append("open failed IOException : " + e +"\n");
                mLog.append("SerialPort : " +mSerialPort + "\n");
                mScrollView.fullScroll(View.FOCUS_DOWN);
                Log.e(TAG, "open  failed IOException : " + e);
            }
        }
    }

    public void closeSerialPort() {
        try {
            if (mInputStream != null) {
                mInputStream.close();
                mInputStream = null;
            }
            if (mOutputStream != null) {
                mOutputStream.close();
                mOutputStream = null;
            }
            if (mSerialPort != null){
                mSerialPort.close();
                mSerialPort = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendSerialPort(String path, String data) {
        if (mSerialPort == null){
            mLog.append("my SerialPort is null: " +mSerialPort+ "\n");
            mScrollView.fullScroll(View.FOCUS_DOWN);
        } else {
            try {
                mOutputStream.write(new String(data).getBytes());
                mOutputStream.flush();
                mLog.append(data+" __write to : " + path + "\n");
                mScrollView.fullScroll(View.FOCUS_DOWN);

                Log.v(TAG, data+" __write to : " + path + "\n");
            } catch (IOException e) {
                e.printStackTrace();
                mLog.append(e + "  __ IOException " + "\n");
                Log.e(TAG, e + "  __ IOException ");
            }
            Toast.makeText(getApplicationContext(), data,Toast.LENGTH_SHORT).show();
        }
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
                        Log.e(TAG, "UART recData = "+ "error");
                    } else {
                        mEditTextReception.append(recData);
                        Log.v(TAG, "UART recData = "+ recData.getBytes());
                    }
                }
            }
        });
    }


    /**
     * GPIO  r/w test function
     */
    //GPIO  create file & write
    public void writeToGpio(String data, String gpioPath) {
        File file = new File(gpioPath);
        try {
            FileWriter writer = new FileWriter(file, false);
            writer.write(data);
            writer.close();
            mLog.append("GPIO write = " + data +" to  " + gpioPath + "\n");
            mScrollView.fullScroll(View.FOCUS_DOWN);
            Log.v(TAG, "GPIO write = " + data +" to " + gpioPath);
        } catch (IOException e1) {
            e1.printStackTrace();
            mLog.append("GPIO writeData IOException=" + e1 + "\n");
            mScrollView.fullScroll(View.FOCUS_DOWN);
            Log.e(TAG, "GPIO writeData IOException=" + e1);

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
            mEditTextReception.append(GpioValue);
            mLog.append(gpio_path+"  read data=" + GpioValue + "\n");
            mScrollView.fullScroll(View.FOCUS_DOWN);
            Log.v(TAG, gpio_path+"  read data=" + GpioValue + "\n");

            mBufferedReader.close();
            mFileReader.close();
        } catch (Exception e) {
            e.printStackTrace();
            mLog.append("GPIO readData IOException=" + e + "\n");
            mScrollView.fullScroll(View.FOCUS_DOWN);
            Log.e(TAG, "GPIO readData IOException=" + e);
        }
    }

    protected void onDestroy() {
//        writeToGpio("0", gpio10_path);  // "1"= high , "0"= Low
//        writeToGpio("0", gpio89_path);   //KEY_TOP_1, gpio89
//        writeToGpio("0", gpio_test);   //SAM TEST
        closeSerialPort();
        super.onDestroy();
    }


}
