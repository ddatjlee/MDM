package com.example.test

import okhttp3.RequestBody.Companion.asRequestBody
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.google.android.gms.location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var tvLocation: TextView
    private lateinit var locationCallback: LocationCallback
    private lateinit var imageView: ImageView
    private lateinit var captureImageLauncher: ActivityResultLauncher<Intent>

    private var imageUri: Uri? = null
    private var currentLocationText: String? = null // Biến để lưu vị trí hiện tại

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://Your-IP/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvLocation = findViewById(R.id.tvLocation)
        imageView = findViewById(R.id.imageView)

        val buttonCapture = findViewById<Button>(R.id.buttonCapture)
        buttonCapture.setOnClickListener {
            captureImage()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        captureImageLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK && imageUri != null) {
                imageView.setImageURI(imageUri)
                uploadImageFromUri(imageUri!!)  // Tự động gửi lên server
            }
        }

        // Tạo LocationCallback
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val location: Location? = locationResult.lastLocation
                location?.let {
                    Log.d("Location Update", "Latitude: ${it.latitude}, Longitude: ${it.longitude}")
                    currentLocationText = getString(R.string.current_location) + "${it.latitude}, ${it.longitude}"
                    tvLocation.text = currentLocationText // Hiển thị vị trí
                } ?: run {
                    Log.d("Location Update", "No location available")
                }
            }
        }

        checkPermissions()
    }

    private fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            startLocationUpdates()
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE), 2)
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setIntervalMillis(10000)
                .setMinUpdateIntervalMillis(10000)
                .build()

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
            Log.d("Location Update", "Location updates started")

        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    private fun captureImage() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile = createImageFile()
        if (photoFile.exists()) {
            imageUri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", photoFile)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            captureImageLauncher.launch(intent)
        } else {
            tvLocation.text = getString(R.string.no_uri)
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: return File("")
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    // Phương thức upload file từ Uri
    private fun uploadImageFromUri(imageUri: Uri) {
        val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
        val tempFile = File.createTempFile("upload", ".jpg", cacheDir)

        tempFile.outputStream().use { outputStream ->
            inputStream?.copyTo(outputStream)
        }

        val requestFile = tempFile.asRequestBody("image/jpeg".toMediaType())
        val body = MultipartBody.Part.createFormData("image", tempFile.name, requestFile)

        val latitudeBody = (currentLocationText?.split(",")?.get(0)?.trim() ?: "0").toRequestBody("text/plain".toMediaType())
        val longitudeBody = (currentLocationText?.split(",")?.get(1)?.trim() ?: "0").toRequestBody("text/plain".toMediaType())

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.uploadImage(body, latitudeBody, longitudeBody)
                runOnUiThread {
                    if (response.isSuccessful) {
                        tvLocation.text = getString(R.string.upload_success)
                    } else {
                        tvLocation.text = getString(R.string.upload_failure)
                    }
                    tvLocation.text = currentLocationText ?: getString(R.string.location_placeholder)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    tvLocation.text = getString(R.string.connection_error, e.message)
                }
            }
        }
    }
    interface ApiService {
        @Multipart
        @POST("upload/upload.php")
        suspend fun uploadImage(
            @Part image: MultipartBody.Part,
            @Part("latitude") latitude: RequestBody,
            @Part("longitude") longitude: RequestBody
        ): retrofit2.Response<ResponseBody>
    }
}
