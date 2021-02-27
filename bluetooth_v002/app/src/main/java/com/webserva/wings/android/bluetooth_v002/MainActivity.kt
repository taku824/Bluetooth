import android.R
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

//public class MainActivity extends ActionBarActivity implements Runnable, View.OnClickListener {
class MainActivity : AppCompatActivity(), Runnable, View.OnClickListener {
    /* Bluetooth Adapter */
    private var mAdapter: BluetoothAdapter? = null

    /* Bluetoothデバイス */
    private var mDevice: BluetoothDevice? = null

    /* Bluetooth UUID(固定) */
    private val MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    /* デバイス名 環境に合わせて変更*/
    private val DEVICE_NAME = "RNBT-205F"

    /* Soket */
    private var mSocket: BluetoothSocket? = null

    /* Thread */
    private var mThread: Thread? = null

    /* Threadの状態を表す */
    private var isRunning = false

    /** 接続ボタン.  */
    private var connectButton: Button? = null

    /** 書込みボタン.  */
    private var writeButton: Button? = null

    /** ステータス.  */
    private var mStatusTextView: TextView? = null

    /** Bluetoothから受信した値.  */
    private var mInputTextView: TextView? = null

    /** Connect確認用フラグ  */
    private var connectFlg = false

    /** BluetoothのOutputStream.  */
    var mmOutputStream: OutputStream? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Layoutにて設定したビューを表示
        setContentView(R.layout.activity_main)

        // TextViewの設定(Layoutにて設定したものを関連付け)
        mInputTextView = findViewById<View>(R.id.inputValue) as TextView
        mStatusTextView = findViewById<View>(R.id.statusValue) as TextView

        // Buttonの設定(Layoutにて設定したものを関連付け)
        connectButton = findViewById<View>(R.id.connectButton) as Button
        writeButton = findViewById<View>(R.id.writeButton) as Button

        // ボタンのイベント設定
        connectButton!!.setOnClickListener(this)
        writeButton!!.setOnClickListener(this)

        // Bluetoothのデバイス名を取得
        // デバイス名は、RNBT-XXXXになるため、
        // DVICE_NAMEでデバイス名を定義
        mAdapter = BluetoothAdapter.getDefaultAdapter()
        mStatusTextView!!.text = "SearchDevice"
        val devices = mAdapter.getBondedDevices()
        for (device in devices) {
            if (device.name == DEVICE_NAME) {
                mStatusTextView!!.text = "find: " + device.name
                mDevice = device
            }
        }
    }

    // 別のアクティビティが起動した場合の処理
    override fun onPause() {
        super.onPause()
        isRunning = false
        connectFlg = false
        try {
            mSocket!!.close()
        } catch (e: Exception) {
        }
    }

    // スレッド処理(connectボタン押下後に実行)
    override fun run() {
        var mmInStream: InputStream? = null
        var valueMsg = Message()
        valueMsg.what = VIEW_STATUS
        valueMsg.obj = "connecting..."
        mHandler.sendMessage(valueMsg)
        try {

            // 取得したデバイス名を使ってBluetoothでSocket接続
            mSocket = mDevice!!.createRfcommSocketToServiceRecord(MY_UUID)
            mSocket.connect()
            mmInStream = mSocket.getInputStream()
            mmOutputStream = mSocket.getOutputStream()

            // InputStreamのバッファを格納
            val buffer = ByteArray(1024)

            // 取得したバッファのサイズを格納
            var bytes: Int
            valueMsg = Message()
            valueMsg.what = VIEW_STATUS
            valueMsg.obj = "connected."
            mHandler.sendMessage(valueMsg)
            connectFlg = true
            while (isRunning) {

                // InputStreamの読み込み
                bytes = mmInStream.read(buffer)
                Log.i(TAG, "bytes=$bytes")
                // String型に変換
                val readMsg = String(buffer, 0, bytes)

                // null以外なら表示
                if (readMsg.trim { it <= ' ' } != null && readMsg.trim { it <= ' ' } != "") {
                    Log.i(TAG, "value=" + readMsg.trim { it <= ' ' })
                    valueMsg = Message()
                    valueMsg.what = VIEW_INPUT
                    valueMsg.obj = readMsg
                    mHandler.sendMessage(valueMsg)
                }
            }
        } // エラー処理
        catch (e: Exception) {
            valueMsg = Message()
            valueMsg.what = VIEW_STATUS
            valueMsg.obj = "Error1:$e"
            mHandler.sendMessage(valueMsg)
            try {
                mSocket!!.close()
            } catch (ee: Exception) {
            }
            isRunning = false
            connectFlg = false
        }
    }

    // ボタン押下時の処理
    override fun onClick(v: View) {
        if (v == connectButton) {
            // 接続されていない場合のみ
            if (!connectFlg) {
                mStatusTextView!!.text = "try connect"
                mThread = Thread(this)
                // Threadを起動し、Bluetooth接続
                isRunning = true
                mThread!!.start()
            }
        } else if (v == writeButton) {
            // 接続中のみ書込みを行う
            if (connectFlg) {
                try {
                    // Writeボタン押下時、'2'を送信
                    mmOutputStream!!.write("2".toByteArray())
                    // 画面上に"Write:"を表示
                    mStatusTextView!!.text = "Write:"
                } catch (e: IOException) {
                    val valueMsg = Message()
                    valueMsg.what = VIEW_STATUS
                    valueMsg.obj = "Error2:$e"
                    mHandler.sendMessage(valueMsg)
                }
            } else {
                mStatusTextView!!.text = "Please push the connect button"
            }
        }
    }

    /**
     * 描画処理はHandlerでおこなう
     */
    var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            val action = msg.what
            val msgStr = msg.obj as String
            if (action == VIEW_INPUT) {
                mInputTextView!!.text = msgStr
            } else if (action == VIEW_STATUS) {
                mStatusTextView!!.text = msgStr
            }
        }
    }

    companion object {
        /* tag */
        private const val TAG = "BluetoothSample"

        /** Action(ステータス表示).  */
        private const val VIEW_STATUS = 0

        /** Action(取得文字列).  */
        private const val VIEW_INPUT = 1
    }
}