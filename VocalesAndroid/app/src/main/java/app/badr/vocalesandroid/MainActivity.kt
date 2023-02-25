package app.badr.vocalesandroid

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import app.badr.vocalesandroid.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import java.math.BigInteger
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress

class MainActivity : AppCompatActivity() {

  private lateinit var appBarConfiguration: AppBarConfiguration
  private lateinit var binding: ActivityMainBinding

  private lateinit var textViewIpAddress: TextView
  private lateinit var textViewPacket: TextView

  private var requestCodeMicrophone = 100


  // Audio Recorder config
  private lateinit var audioRecorder: AudioRecord
  private var recorderSource: Int = MediaRecorder.AudioSource.MIC
  private val sampleRate = 44100
  private val channel: Int = AudioFormat.CHANNEL_IN_MONO
  private val format: Int = AudioFormat.ENCODING_PCM_16BIT
  private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channel, format)
  private val buffer = ByteArray(bufferSize)

  // Streaming thread
  private val localHostPort: Int = 1337
  private var isStreaming: Boolean = false
  private lateinit var streamingThread: Thread

  @RequiresApi(Build.VERSION_CODES.M)
  override fun onCreate(savedInstanceState: Bundle?) {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    super.onCreate(savedInstanceState)

    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    setSupportActionBar(binding.toolbar)

    val navController = findNavController(R.id.nav_host_fragment_content_main)
    appBarConfiguration = AppBarConfiguration(navController.graph)
    setupActionBarWithNavController(navController, appBarConfiguration)

    // Views
    textViewIpAddress = findViewById(R.id.textViewIpAddress)
    textViewPacket = findViewById(R.id.textViewPacket)

    binding.fab.setOnClickListener { view ->
      if (isStreaming) {
        streamingThread.interrupt()
        isStreaming = false
        Toast.makeText(this, "Stopping stream", Toast.LENGTH_SHORT).show()

        return@setOnClickListener
      }

      when (ContextCompat.checkSelfPermission(
        this, Manifest.permission.RECORD_AUDIO
      )) {
        PackageManager.PERMISSION_GRANTED -> {
          enableMicrophone()
          sendStream()
        }
        PackageManager.PERMISSION_DENIED -> {
          this.requestPermissions(
            arrayOf(Manifest.permission.RECORD_AUDIO),
            requestCodeMicrophone
          )
        }
      }
      Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        .setAnchorView(R.id.fab)
        .setAction("Action", null).show()
    }
  }

  @SuppressLint("MissingPermission")
  private fun enableMicrophone() {
    audioRecorder = AudioRecord(recorderSource, sampleRate, channel, format, bufferSize)
    audioRecorder.startRecording()
  }

  private fun sendStream() {
    // Make datagram socket and bind address as null initially
    val datagramSocket = DatagramSocket(null)

    // Bind IPv4 address of the device to the socket
    val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val localHostAddress = wifiManager.connectionInfo.ipAddress
    val localHostAddressString = InetAddress.getByAddress(
      BigInteger.valueOf(localHostAddress.toLong()).toByteArray()
    ).hostAddress

    // Bind socket to the local host and set view
    datagramSocket.bind(
      InetSocketAddress(localHostAddressString, localHostPort)
    )

    textViewIpAddress.append(localHostAddressString)

    streamingThread = Thread {
      while (true) {
        // Get microphone stream bytes
        val recorderBytes = audioRecorder.read(buffer, 0, buffer.size)

        // Make datagram packet and send
        val packet = DatagramPacket(buffer, recorderBytes)
        datagramSocket.send(packet)

        textViewPacket.text = "${buffer.toString().subSequence(0, 5)}"
      }
    }

    streamingThread.start()
    isStreaming = true
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    // Inflate the menu; this adds items to the action bar if it is present.
    menuInflater.inflate(R.menu.menu_main, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    return when(item.itemId) {
      R.id.action_settings -> true
      else -> super.onOptionsItemSelected(item)
    }
  }

  override fun onSupportNavigateUp(): Boolean {
    val navController = findNavController(R.id.nav_host_fragment_content_main)
    return navController.navigateUp(appBarConfiguration)
            || super.onSupportNavigateUp()
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    when (requestCode) {
      requestCodeMicrophone -> {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          enableMicrophone()
        } else {
          Toast.makeText(
            this,
            "Enable your microphone to use this feature",
            Toast.LENGTH_LONG).show()
        }
      } else -> {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
      }
    }
  }
}