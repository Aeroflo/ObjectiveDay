package com.example.objectiveday.controllers

import android.app.TimePickerDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Message
import android.text.Layout
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.fragment.app.FragmentManager
import com.example.objectiveday.R
import com.example.objectiveday.databinding.ObjectiveMainObjectLayoutBinding
import com.example.objectiveday.dialogs.ObjectiveFilterDialog
import com.example.objectiveday.internalData.DataSingleton
import com.example.objectiveday.models.ObjectiveModel
import com.example.objectiveday.models.ObjectiveStatus
import com.example.objectiveday.progressbars.ObjectiveProgressBar
import com.example.objectiveday.webservices.apimodels.APIObjectives
import java.lang.StringBuilder
import java.time.LocalTime
import java.util.*

class ObjectiveBindingController {

    var context : Context
    var applicationContext : Context
    var supportFragmentManager : FragmentManager? = null

    var todoProgressBar: ObjectiveProgressBar? = null

    constructor(context: Context, applicationContext: Context, supportFragmentManager : FragmentManager) {
        this.context = context
        this.applicationContext = applicationContext
        this.supportFragmentManager = supportFragmentManager
    }

    fun prepareBinding(binding: ObjectiveMainObjectLayoutBinding?, objectiveModel: ObjectiveModel) : ObjectiveMainObjectLayoutBinding?
    {
        if(binding == null) return binding
        binding.objectiveMainModel = objectiveModel

        //Image to do / done / neutral

        this.todoProgressBar =  ObjectiveProgressBar(binding.actiontodo, this.context!!)
        binding.actiontodo.setOnClickListener{

            if(todoProgressBar == null){
                Toast.makeText(this.context, "Objective not set as done!", Toast.LENGTH_LONG).show()
                Thread{todoProgressBar!!.stopTheAnim()}
            }
            when(todoProgressBar!!.getObjectiveStatus()){
                ObjectiveStatus.TODO -> {
                    mHandler.post({
                        todoProgressBar!!.startTheAnim()
                        Thread{binding.invalidateAll()}
                    })
                    Thread{
                        //val apiService = RestAPIService(TokenSingleton.instance.url)

                        var apiObjectives = DataSingleton.instance.markObjectiveAsDone(this.context, binding.objectiveMainModel)
                        //apiObjectives = apiService.saveObjective(TokenSingleton.instance.getToken()!!, apiObjectives!!)
                        if(apiObjectives == null)
                        {
                            todoProgressBar!!.stopTheAnim()
                        }
                        else{
                            Thread.sleep(2000)
                            mHandler.post({
                                binding.objectiveMainModel = apiObjectives!!.toModel()
                                verifyObjectiveStatusAndValidate(binding)
                                binding.invalidateAll()
                                todoProgressBar!!.stopTheAnim()
                            })
                        }

                    }.start()
                }
                ObjectiveStatus.DONE -> {
                    Toast.makeText(this.context, "Objective already done!", Toast.LENGTH_LONG).show()
                    Thread{todoProgressBar!!.stopTheAnim()}.start()
                }
                ObjectiveStatus.NEUTRAL -> {
                    Toast.makeText(this.context, "Objective  not to do!", Toast.LENGTH_LONG).show()
                    Thread{todoProgressBar!!.stopTheAnim()}.start()
                }
            }
        }

        verifyObjectiveStatusAndValidate(binding)
        var buttonSave: ButtonSignIn = ButtonSignIn(context, binding.saveProgress)

        buttonSave.setModeSave()


        binding.buttondetails.setOnClickListener{
            val view = binding.moreDetailsLayout
            val imageView = binding.buttondetails as ImageView
            if(view.visibility == View.GONE){
                imageView.animate().rotation(180.0f).start()
                view.visibility = View.VISIBLE
                val animation = AnimationUtils.loadAnimation(context, R.anim.slide_left)
                view.startAnimation(animation)
                mHandler.post {
                    buttonSave.buttonDisplay()
                    buttonSave.buttonSetText("Save")
                    binding.invalidateAll()
                }
            } else{
                imageView.animate().rotation(360.0f).start()
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



        getTime(binding, this.applicationContext)

        binding.saveProgress.setOnClickListener{

            mHandler.post({
                buttonSave.buttonActivated()
                binding.invalidateAll()
            })

            //binding.saveProgress.invalidate()

            //check values
            var apiObjectives : APIObjectives? = isObjectiveChecked(binding.objectiveMainModel, binding.notifySW.isChecked)

            if(apiObjectives == null){
                Toast.makeText(this.context, "Not saved", Toast.LENGTH_LONG).show()
                buttonSave.buttonFinished(false)
            }
            else{
                Thread{
                    //val apiService = RestAPIService(TokenSingleton.instance.url)

                    apiObjectives = DataSingleton.instance.saveAPIObjectiveLocal(this.context, apiObjectives!!)
                    //apiObjectives = apiService.saveObjective(TokenSingleton.instance.getToken()!!, apiObjectives!!)
                    if(apiObjectives == null)
                    {
                        buttonSave.buttonFinished(false) //Todo set this line in handler???
                    }
                    else{
                        Thread.sleep(2000)
                        mHandler.post({
                            binding.objectiveMainModel = apiObjectives!!.toModel()
                            binding.invalidateAll()
                            buttonSave.buttonFinished(true)
                        })
                    }

                }.start()
            }
        }

        //QR CODE
        binding.qrcodeimage.setOnClickListener{
            System.out.println("QR Touched")
            var qrView = QRView(binding.objectiveMainModel)
            qrView.show(this.supportFragmentManager, QRView.TAG)
            //qrView.loadQRCode(binding.objectiveMainModel)

        }

        //reset last time done
        binding.resetdoneimg.setOnClickListener{
            binding.objectiveMainModel?.lastDoneTime = null
            verifyObjectiveStatusAndValidate(binding)
            binding.invalidateAll()
        }




        return binding
    }

    fun getTime( binding : ObjectiveMainObjectLayoutBinding,  context: Context){

        val button = binding.timepickerlayout


        button.setOnClickListener {
            val cal = Calendar.getInstance()

            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)

                mHandler.post(
                    {
                        var localTime: LocalTime =
                            LocalTime.now().withHour(hour).withMinute(minute).withSecond(0)
                                .withNano(0)
                        var stringBuilder: StringBuilder = StringBuilder()
                        stringBuilder =
                            stringBuilder.append(String.format("%02d", hour)).append(":")
                                .append(String.format("%02d", minute))
                        binding.time.text = stringBuilder.toString()
                        binding.objectiveMainModel!!.time = localTime
                        binding.invalidateAll()
                    }
                )
            }
            TimePickerDialog(context, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }
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
                verifyObjectiveStatusAndValidate(binding)
                objectiveModel.getNextDateList(true)

                binding.invalidateAll()
            } catch (e: Exception) {
                System.out.println("Error " + e)
            }
        }
    }

    fun verifyObjectiveStatusAndValidate(binding: ObjectiveMainObjectLayoutBinding){
        //set text background color
        var objectiveModel = binding.objectiveMainModel
        var drawableObject : Drawable? = null
        when(objectiveModel!!.getObjectiveStatus()){
            ObjectiveStatus.NEUTRAL ->{
                 drawableObject= context.getDrawable(R.drawable.objectivecolapseborder)
                binding.actiontodo.visibility = View.INVISIBLE
            }
            ObjectiveStatus.TODO -> {
                drawableObject = context.getDrawable(R.drawable.objectivecolapsebordertodo)
                if(this.todoProgressBar != null) this.todoProgressBar!!.setAsTodo()
                binding.actiontodo.visibility = View.VISIBLE
            }
            ObjectiveStatus.DONE -> {
                drawableObject = context.getDrawable(R.drawable.objectivecolapseborderdone)
                if(this.todoProgressBar != null) this.todoProgressBar!!.setAsDone()
                binding.actiontodo.visibility = View.VISIBLE
            }
        }
        binding.objectiveAllContraintLayout.background = drawableObject
    }

    fun updateNotify(binding: ObjectiveMainObjectLayoutBinding, view: View, isChecked: Boolean){
        var objectiveModel :  ObjectiveModel? = binding.objectiveMainModel
        objectiveModel!!.isNotifiable = isChecked
         binding.notifySW.isChecked = isChecked
        binding.invalidateAll()
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
                verifyObjectiveStatusAndValidate(binding)
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
                verifyObjectiveStatusAndValidate(binding)
                binding.invalidateAll()
            } catch (e: Exception) {
                System.out.println("Error " + e)
            }
        }
    }

    fun isObjectiveChecked(objectiveModel: ObjectiveModel?, notify : Boolean) : APIObjectives?{
        if(objectiveModel == null) return null
        if(objectiveModel.description == null || objectiveModel.description.isBlank()) return null
        var time = ""
        if(objectiveModel.time != null )  time = objectiveModel.time.toString()

        var lastDoneTime :String? =  null
        if(objectiveModel.lastDoneTime != null) lastDoneTime = objectiveModel.lastDoneTime!!.toString()
        var apiObjective : APIObjectives = APIObjectives(objectiveModel.id, objectiveModel.parentId, objectiveModel.description,
                            objectiveModel.isMonday, objectiveModel.isTuesday, objectiveModel.isWednesday, objectiveModel.isThursday, objectiveModel.isFriday, objectiveModel.isSaturday, objectiveModel.isSunday,
                            time, notify, "", "", null, true, null,  lastDoneTime)
        return apiObjective
    }

    private fun colapseHideDetails(binding : ObjectiveMainObjectLayoutBinding){
        val view = binding.moreDetailsLayout
        val imageView = binding.buttondetails as ImageView
        if(view.visibility == View.GONE){
            imageView.animate().rotation(180.0f).start()
            view.visibility = View.VISIBLE
            val animation = AnimationUtils.loadAnimation(context, R.anim.slide_left)
            view.startAnimation(animation)
            mHandler.post {
                binding.invalidateAll()
            }
        } else{
            imageView.animate().rotation(360.0f).start()
            val animation = AnimationUtils.loadAnimation(context, R.anim.slide_right_out)
            view.startAnimation(animation)
            Handler().postDelayed({
                view.visibility = View.GONE
            }, 200)
        }
    }

    private fun colapseAll(binding : ObjectiveMainObjectLayoutBinding){
        var mainView = binding.root
        val animation = AnimationUtils.loadAnimation(context, R.anim.slide_left)
        mainView.visibility = View.INVISIBLE
        mainView.startAnimation(animation)
        binding.invalidateAll()
    }

    public fun rundeleteAnimation(binding : ObjectiveMainObjectLayoutBinding){
        binding.root.isClickable = false;
        colapseHideDetails(binding)
        colapseAll(binding)
    }
}