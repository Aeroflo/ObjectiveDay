package com.example.objectiveday.controllers

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Message
import android.text.Layout
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.CheckBox
import android.widget.Switch
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.objectiveday.R
import com.example.objectiveday.databinding.ObjectiveMainObjectLayoutBinding
import com.example.objectiveday.models.ObjectiveModel
import com.example.objectiveday.webservices.apimodels.APIObjectives
import com.example.objectiveday.webservices.retrofit.RestAPIService
import kotlinx.android.synthetic.main.qr_code_layout.view.*

class ObjectiveBindingController {

    var context : Context
    constructor(context: Context) {
        this.context = context
    }

    fun prepareBinding(binding: ObjectiveMainObjectLayoutBinding?, objectiveModel: ObjectiveModel) : ObjectiveMainObjectLayoutBinding?
    {
        if(binding == null) return binding
        binding.objectiveMainModel = objectiveModel

        binding.details.setOnClickListener {
            val view = binding.moreDetailsLayout
            val button = binding.details
            if(view.visibility == View.GONE){
                //collapseOtherDetails(position)
                view.visibility = View.VISIBLE
                val animation = AnimationUtils.loadAnimation(context, R.anim.slide_left)
                view.startAnimation(animation)
            } else{
                val animation = AnimationUtils.loadAnimation(context, R.anim.slide_right_out)
                view.startAnimation(animation)
                Handler().postDelayed({
                    view.visibility = View.GONE
                }, 200)
            }
        }

        binding.monday.setOnCheckedChangeListener { buttonView, isChecked -> verifyWeekDayCheckBoxAndValidate(binding, buttonView, isChecked) }
        binding.tuesday.setOnCheckedChangeListener { buttonView, isChecked -> verifyWeekDayCheckBoxAndValidate(binding, buttonView, isChecked) }
        binding.wednesday.setOnCheckedChangeListener { buttonView, isChecked -> verifyWeekDayCheckBoxAndValidate(binding, buttonView, isChecked) }
        binding.thursday.setOnCheckedChangeListener { buttonView, isChecked -> verifyWeekDayCheckBoxAndValidate(binding, buttonView, isChecked) }
        binding.friday.setOnCheckedChangeListener { buttonView, isChecked -> verifyWeekDayCheckBoxAndValidate(binding, buttonView, isChecked) }

        binding.saturday.setOnCheckedChangeListener { buttonView, isChecked -> verifyWeekEndCheckBoxAndValidate(binding, buttonView, isChecked) }
        binding.sunday.setOnCheckedChangeListener { buttonView, isChecked -> verifyWeekEndCheckBoxAndValidate(binding, buttonView, isChecked) }

        binding.weekday.setOnClickListener(){view -> verifySwitchAndValidate(binding, view)}
        binding.weekend.setOnClickListener{view -> verifySwitchAndValidate(binding, view) }



        binding.saveProgress.setOnClickListener{
            var buttonSave: ButtonProgressBar = ButtonProgressBar(this.context, it)
            buttonSave.buttonActivated()
            //binding.saveProgress.isClickable = false
            //binding.saveProgress.invalidate()

            //check values
            var apiObjectives : APIObjectives? = isObjectiveChecked(binding.objectiveMainModel)

            if(apiObjectives == null){
                Toast.makeText(this.context, "Not saved", Toast.LENGTH_LONG).show()
                buttonSave.buttonFinished(false)
            }
            else{
                Thread{
                    val apiService = RestAPIService(TokenSingleton.instance.url)

                    apiObjectives = apiService.saveObjective(TokenSingleton.instance.getToken()!!, apiObjectives!!)
                    if(apiObjectives == null)
                    {
                        buttonSave.buttonFinished(false)
                    }
                    else{
                        mHandler.post({
                            binding.objectiveMainModel = apiObjectives!!.toModel()
                            binding.invalidateAll()
                            buttonSave.buttonFinished(true)
                        })
                    }

                }.start()
            }
        }

        var qrLayout : View = binding.qrCode
        var buttonQR : Button = qrLayout.findViewById(R.id.genqrBTN)
        buttonQR.setOnClickListener{
            buttonQR.isEnabled = false
            buttonQR.text = "Generating QR"

            var qrView : QRView = QRView(this.context, qrLayout)
            qrView.showProgressBar()
            mHandler.post({
                qrView.loadQRCode(binding.objectiveMainModel)
                qrView.hideProgressBar()
                buttonQR.isEnabled = true
                buttonQR.text = "Generated"
                qrLayout.invalidate()
                binding.invalidateAll()
            })
        }
        return binding
    }

    val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            System.out.println("MESSAGE")
        }
    }

    fun verifyWeekDayCheckBoxAndValidate(binding: ObjectiveMainObjectLayoutBinding, v: View, isChecked : Boolean ){
        //GetModel
        var objectiveModel :  ObjectiveModel? = binding.objectiveMainModel
        var viewName: String = this.context.resources.getResourceEntryName(v.id)

        if(objectiveModel != null) {
            try {
                v as CheckBox
                when {
                    viewName.equals("monday") -> {
                        objectiveModel.isMonday = v.isChecked
                    }
                    viewName.equals("tuesday") -> {
                        objectiveModel.isTuesday = v.isChecked
                    }
                    viewName.equals("wednesday") -> {
                        objectiveModel.isWednesday = v.isChecked
                    }
                    viewName.equals("thursday") -> {
                        objectiveModel.isThursday = v.isChecked
                    }
                    viewName.equals("friday") -> {
                        objectiveModel.isFriday = v.isChecked
                    }
                }
                binding.weekday.isChecked = objectiveModel.isWeekDaySet()

                objectiveModel.getNextDateList(true)

                binding.invalidateAll()
            } catch (e: Exception) {
                System.out.println("Error " + e)
            }
        }
    }

    fun verifyWeekEndCheckBoxAndValidate(binding: ObjectiveMainObjectLayoutBinding, v: View, isChecked : Boolean ){
        //GetModel
        var objectiveModel :  ObjectiveModel? = binding.objectiveMainModel
        var viewName: String = this.context.resources.getResourceEntryName(v.id)

        if(objectiveModel != null) {
            try {
                v as CheckBox
                when {
                    viewName.equals("saturday") -> {
                        objectiveModel.isSaturday = v.isChecked
                    }
                    viewName.equals("sunday") -> {
                        objectiveModel.isSunday = v.isChecked
                    }
                }
                binding.weekend.isChecked = objectiveModel.isWeekEndSet()

                objectiveModel.getNextDateList(true)

                binding.invalidateAll()
            } catch (e: Exception) {
                System.out.println("Error " + e)
            }
        }
    }

    fun verifySwitchAndValidate(binding: ObjectiveMainObjectLayoutBinding, v: View){
        var objectiveModel :  ObjectiveModel? = binding.objectiveMainModel
        var viewName: String = this.context.resources.getResourceEntryName(v.id)

        if(objectiveModel != null) {
            try {
                v as Switch
                when {
                    viewName.equals("weekday") -> {
                        var isChecked = v.isChecked

                        objectiveModel.isMonday = isChecked
                        binding.monday.isChecked = isChecked

                        objectiveModel.isTuesday = isChecked
                        binding.tuesday.isChecked = isChecked

                        objectiveModel.isWednesday = isChecked
                        binding.wednesday.isChecked = isChecked

                        objectiveModel.isThursday = isChecked
                        binding.thursday.isChecked = isChecked

                        objectiveModel.isFriday = isChecked
                        binding.friday.isChecked = isChecked
                    }
                    viewName.equals("weekend") -> {
                        var isChecked = v.isChecked

                        objectiveModel.isSaturday = isChecked
                        binding.saturday.isChecked = isChecked

                        objectiveModel.isSunday = isChecked
                        binding.sunday.isChecked = isChecked
                    }
                }
                binding.weekday.isChecked = objectiveModel.isWeekDaySet()
                binding.weekend.isChecked = objectiveModel.isWeekEndSet()
                binding.invalidateAll()
            } catch (e: Exception) {
                System.out.println("Error " + e)
            }
        }
    }

    fun isObjectiveChecked(objectiveModel: ObjectiveModel?) : APIObjectives?{
        if(objectiveModel == null) return null
        if(objectiveModel.description == null || objectiveModel.description.isBlank()) return null
        var time = ""
        if(objectiveModel.time != null )  time = objectiveModel.time.toString()

        var apiObjective : APIObjectives = APIObjectives(objectiveModel.id, objectiveModel.parentId, objectiveModel.description,
                            objectiveModel.isMonday, objectiveModel.isTuesday, objectiveModel.isWednesday, objectiveModel.isThursday, objectiveModel.isFriday, objectiveModel.isSaturday, objectiveModel.isSunday,
                            time, objectiveModel.isNotifiable, "", "", null, true, null)
        return apiObjective
    }
}