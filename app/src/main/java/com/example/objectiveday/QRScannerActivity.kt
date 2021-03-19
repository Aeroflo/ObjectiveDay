package com.example.objectiveday


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.util.SparseArray
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.util.isNotEmpty
import com.example.objectiveday.controllers.TokenSingleton
import com.example.objectiveday.models.ObjectiveModel
import com.example.objectiveday.webservices.apimodels.APIObjectives
import com.example.objectiveday.webservices.retrofit.RestAPI
import com.example.objectiveday.webservices.retrofit.RestAPIService
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.qrcodescanner.*
import okhttp3.internal.Util
import java.lang.Exception
import java.time.Duration
import java.util.jar.Manifest

class QRScannerActivity : AppCompatActivity() {

    private val requestCodeCameraPermission = 1001
    private lateinit var cameraSource : CameraSource
    private lateinit var dedector : BarcodeDetector

    private var firstDetection : Boolean =false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qrcodescanner)
        if(ContextCompat.checkSelfPermission(
                this@QRScannerActivity,
                android.Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED
            )
        {
            askForCameraPermission()
        }
        else
        {
            setupControls()
        }
    }

    private fun setupControls(){
        dedector = BarcodeDetector.Builder(this@QRScannerActivity).build()
        dedector.setProcessor(
            object : Detector.Processor<Barcode>{
            override fun release() {
                System.out.println("")
            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>?) {
                if(detections != null && detections.detectedItems.isNotEmpty() && !firstDetection){
                    val qrCodes : SparseArray<Barcode> = detections.detectedItems
                    val code = qrCodes.valueAt(0)

                    if(code.displayValue != null && code.displayValue.startsWith("ObjectiveDay=") && code.displayValue.length > 13 ){
                        firstDetection = true
                        var ojectiveAPIString : String= code.displayValue.subSequence(13, code.displayValue.length).toString()

                        if(!ojectiveAPIString.isNullOrBlank()) {
                            val gson = Gson()
                            val arrayTutorialType =
                                object : TypeToken<APIObjectives>() {}.type
                            var apiObjectives: APIObjectives =
                                gson.fromJson(ojectiveAPIString, arrayTutorialType)

                            val objectiveModel : ObjectiveModel = apiObjectives.toModel()

                            runOnUiThread{callObjective(objectiveModel)}
                        }
                        else{
                            firstDetection = false
                        }
                    }
                    else{
                        //runOnUiThread{Toast.makeText(this@QRScannerActivity, "Not an Objective!", Toast.LENGTH_LONG).show()}
                    }
                }
            }
        })
        cameraSource = CameraSource.Builder(this@QRScannerActivity,dedector)
            .setFacing(CameraSource.CAMERA_FACING_BACK)
            .setAutoFocusEnabled(true)
            .build()
        cameraQRScannerView.holder.addCallback(surgaceCallBack)


        if(!dedector.isOperational()){
            Toast.makeText(this,"Barcode detector NOT OPERATIONAL ", Toast.LENGTH_LONG).show()
        }
    }
    private fun askForCameraPermission(){
        ActivityCompat.requestPermissions(
            this@QRScannerActivity, arrayOf(android.Manifest.permission.CAMERA),
            requestCodeCameraPermission
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == requestCodeCameraPermission && grantResults.isEmpty()){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                setupControls()
            }else{
                Toast.makeText(applicationContext, "Permission denied", Toast.LENGTH_LONG)
            }
        }
    }

    private val surgaceCallBack = object : SurfaceHolder.Callback{
        override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            System.out.println("Test")
        }

        override fun surfaceDestroyed(holder: SurfaceHolder?) {
            cameraSource.stop()
        }

        @SuppressLint("MissingPermission")
        override fun surfaceCreated(holder: SurfaceHolder?) {
            try{
                cameraSource.start(holder)
            }catch(e : Exception){
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun callObjective(objectiveModel: ObjectiveModel){
        vibratePhoneObjectivePhone()
        destroyAll()

        if(objectiveModel.children.isNullOrEmpty()) {
            objectiveModel.parentId = objectiveModel.id
            objectiveModel.id = null

            val myIntent = Intent(this.applicationContext, NewObjectiveView::class.java) //Intent(this, NewObjectiveView::class.java)
            myIntent.putExtra("objective", objectiveModel)
            this.startActivity(myIntent)
        }else if(objectiveModel.children!!.size == 1){

            val myIntent = Intent(this.applicationContext, NewObjectiveView::class.java) //Intent(this, NewObjectiveView::class.java)
            myIntent.putExtra("objective", objectiveModel.children!!.get(0))
            this.startActivity(myIntent)
        }else{

        }

    }

    private fun destroyAll(){
        dedector.release()
        cameraSource.stop()
        cameraSource.release()
    }

    fun vibratePhoneObjectivePhone(){
        val vibrator = this.applicationContext?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            val mVibratePattern =
                longArrayOf(0, 100)
            val effect = VibrationEffect.createWaveform(mVibratePattern, -1)
            vibrator.vibrate(effect);
        } else {
            vibrator.vibrate(200)
        }
    }
}