package com.example.objectiveday.controllers

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.CheckBox
import android.widget.Switch
import androidx.databinding.DataBindingUtil
import com.example.objectiveday.R
import com.example.objectiveday.database.ObjectiveDBHelper
import com.example.objectiveday.databinding.ObjectiveBinding
import com.example.objectiveday.models.ObjectiveModel


class ObjectiveHandlers {

    var context : Context;

    constructor(context : Context){
        this.context = context
    }

    fun saveObjective(view: View , objectiveModel : ObjectiveModel) {
        var id = view.id
        var name = view.context.resources.getResourceEntryName(view.id)
        if(isObjectiveToUpdate(objectiveModel)){
            var dbHelper = ObjectiveDBHelper(this.context)
            if(dbHelper.saveObjective(objectiveModel))
            {

            }
        }
    }

    fun isObjectiveToUpdate(objectiveModel : ObjectiveModel) : Boolean{

        var somethingToUpdate : Boolean = true
        //Check description. not empty and updated
        if(objectiveModel.description == null || objectiveModel.description.isEmpty()){
            return false
        }

        //Check time. not empty and good format hh:dd
        if(objectiveModel.time == null){
            return false
        }

        /*if(objectiveModel. == 0){
            return false
        }*/

        return somethingToUpdate
    }

    //function to getevent on checkboxes and update model
    fun handlerCheckBox(view : View, objectiveModel : ObjectiveModel) {
        /*var name = view.context.resources.getResourceEntryName(view.id)


        try{
            view as CheckBox
            when {
                name.equals("monday_chk") -> {objectiveModel.isMonday = view.isChecked}
                name.equals("tuesday_chk") -> {objectiveModel.isTuesday = view.isChecked}
                name.equals("wednesday_chk") -> {objectiveModel.isWednesday = view.isChecked}
                name.equals("thursday_chk") -> {objectiveModel.isThursday = view.isChecked}
                name.equals("friday_chk") -> {objectiveModel.isFriday = view.isChecked}
                name.equals("saturday_chk") -> {objectiveModel.isSaturday = view.isChecked}
                name.equals("sunday_chk") -> {objectiveModel.isSunday = view.isChecked}
            }




            /*var binding: ObjectiveBinding =DataBindingUtil.setContentView(context as Activity, R.layout.objective)
            binding.weekEndSw.isChecked = true;
            binding.invalidateAll();


            //var test = DataBindingUtil.getBinding<>(view);*/
            objectiveModel.isWeekDaySet()
            objectiveModel.isWeekEndSet()

        }catch(e:Exception) {
            System.out.println("Error "+e)
        }*/
    }
}