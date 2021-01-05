package com.example.objectiveday


import com.example.objectiveday.controllers.ObjectiveWeekController
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.objectiveday.database.ObjectiveDBHelper
import com.example.objectiveday.databinding.ObjectiveBinding
import com.example.objectiveday.models.ObjectiveModel
import kotlinx.android.synthetic.main.objective.*
import java.time.LocalTime
import java.util.*

class ObjectiveView : AppCompatActivity() {

    init{
        System.out.println("INIT CALL")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        System.out.println("ON CREATE CALL")
         super.onCreate(savedInstanceState)
        /*setContentView(R.layout.objective)*/

        //need to get the model
        val currentObjectiveModel : ObjectiveModel= getIntent().getSerializableExtra("objectiveModel") as ObjectiveModel

        var binding: ObjectiveBinding =DataBindingUtil.setContentView(this, R.layout.objective)
        binding.objectiveMainModel = currentObjectiveModel

        //binding = ObjectiveBindingImpl.inflate(this)//inflate(LayoutInflater.from(this), parent, false)
        //binding.root.tag = binding

        //binding?.objectiveMainModel = null
        /*val binding: ? = DataBindingUtil.setContentView(
            this, R.layout.objective)
        binding?.objectiveMainMode*/
        //need to create object which impl DataBindingComponent

        val objectiveWeekModel = ObjectiveWeekController.Builder()
            .withMondayChk(binding.mondayChk)
            .withTuesdayChk(binding.tuesdayChk)
            .withWednesdayChk(binding.wednesdayChk)
            .withThurdayChk(binding.thursdayChk)
            .withFridayChk(binding.fridayChk)
            .withSaturdayChk(binding.saturdayChk)
            .withSundayChk(binding.sundayChk)
            .withWeekDaysSW(binding.weekDaysSw)
            .withWeekEndSW(binding.weekEndSw)
            .build();

        objectiveWeekModel.setCheckBoxesActionListeners();

        if(binding.pickTimeBtn != null){
            binding.pickTimeBtn.setOnClickListener {
                val cal = Calendar.getInstance()
                val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                    cal.set(Calendar.HOUR_OF_DAY, hour)
                    cal.set(Calendar.MINUTE, minute)
                    var localTime = LocalTime.now().withHour(hour).withMinute(minute)
                    currentObjectiveModel.time = localTime//cal.time.
                    currentObjectiveModel.validateDays(objectiveWeekModel.getAllDaysInteger())
                    binding.invalidateAll()
                }
                TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
            }
        }

        if(binding.saveBtn != null){
            binding.saveBtn.setOnClickListener{
                var needToUpdate = isObjectiveToUpdate(binding, currentObjectiveModel, objectiveWeekModel);
                if(needToUpdate){
                    currentObjectiveModel.dayChecker = objectiveWeekModel.getAllDaysInteger()
                    var dbHelper = ObjectiveDBHelper(this)
                    if(dbHelper.saveObjective(currentObjectiveModel))
                    {
                        popToast("Objective saved")
                    }
                }
            }
        }

        hideKeyboard()
    }

    fun isObjectiveToUpdate(objectiveBinding: ObjectiveBinding, objectiveModel : ObjectiveModel, objectiveWeekModel : ObjectiveWeekController) : Boolean{
        //hide keyboard
        hideKeyboard()

        var somethingToUpdate : Boolean = false
        //Check description. not empty and updated
        if(objectiveBinding.objectiveText.text.toString() == null || objectiveBinding.objectiveText.text.isEmpty()){
            popToast("Not saved: please set an objective description")
            return false
        }
        else if(objectiveBinding.objectiveText.text.toString().equals(objectiveModel.description)){
            somethingToUpdate = true
        }
        //Check time. not empty and good format hh:dd
        if(objectiveBinding.timeField.text != null && !objectiveBinding.timeField.text.isEmpty()){
            var localTime = Utils.stringToTime(time_field.text.toString());
            if(localTime == null) {
                popToast("Not Saved: please set a time")
                return false
            }
        }

        if(objectiveWeekModel.getAllDaysInteger() == 0){
            popToast("Not Saved: please set days")
            return false
        }
        return somethingToUpdate
    }

    fun popToast(msg : String){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    fun AppCompatActivity.hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        // else {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        // }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }
}