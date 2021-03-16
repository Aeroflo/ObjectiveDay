package com.example.objectiveday.controllers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import com.example.objectiveday.R
import com.example.objectiveday.models.ObjectiveModel
import com.example.objectiveday.webservices.apimodels.APIObjectives
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.qr_code_layout.view.*
import java.lang.StringBuilder

class QRView{

    var context: Context? = null
    var qrView : ImageView?= null
    var progressBar : ProgressBar? = null

    constructor(context: Context, view : View){
        this.context = context
        this.qrView = view.findViewById(R.id.qrCodeIMG)
        this.progressBar = view.findViewById(R.id.progressBarQr)
    }

    fun loadQRCode(objectiveModel: ObjectiveModel?){
        if(objectiveModel != null){
            var code : StringBuilder = StringBuilder("ObjectiveDay=");
            val apiObjectives = APIObjectives(objectiveModel.id, objectiveModel.parentId, objectiveModel.description,
                objectiveModel.isMonday, objectiveModel.isTuesday, objectiveModel.isWednesday, objectiveModel.isThursday, objectiveModel.isFriday, objectiveModel.isSaturday, objectiveModel.isSunday,
                objectiveModel.time.toString(), objectiveModel.isNotifiable, null, null, null, objectiveModel.isActive, null, null)
            code = code.append(apiObjectives.toString())
            //if(parentId != null) code = code.append(parentId)
            //else code = code.append(id)

            var bitmap: Bitmap = generateQRCode(code.toString())
            qrView?.setImageBitmap(bitmap)
            qrView?.visibility= View.VISIBLE
            qrView?.invalidate()
        }

    }

    private fun generateQRCode(text: String): Bitmap {
        val width = 500
        val height = 500
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val codeWriter = MultiFormatWriter()



        try {
            //val barcodeEncoder : BarcodeEncoder = BarcodeEncoder()
            //val matrix : BitMatrix = barcodeEncoder.encode(toString(), BarcodeFormat.QR_CODE, width, height)
            val bitMatrix = codeWriter.encode(text, BarcodeFormat.QR_CODE, width, height)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        } catch (e: WriterException) {
            System.out.println("ERROR GEN QR "+e.message)
        }
        return bitmap
    }

    fun showProgressBar(){
        this.progressBar?.visibility =View.VISIBLE
    }

    fun hideProgressBar(){
        this.progressBar?.visibility = View.GONE
    }
}