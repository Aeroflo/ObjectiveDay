package com.example.objectiveday.adapters

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.view.forEach
import androidx.core.view.isGone
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import com.example.objectiveday.R
import com.example.objectiveday.databinding.ObjectiveBinding
import com.example.objectiveday.databinding.ObjectiveMainObjectLayoutBinding
import com.example.objectiveday.models.ObjectiveModel
import kotlinx.android.synthetic.main.objective.view.*
import java.util.*
import kotlin.collections.HashMap

class ObjectiveListAdapter(private val context: Context,
                    private var dataSource: List<ObjectiveModel>) : BaseAdapter() {

    //Map position view
    var mapObjectiveView : MutableMap<Int, ObjectiveMainObjectLayoutBinding> = HashMap()

    fun updateDateSource(newList: List<ObjectiveModel>){
        this.dataSource=newList;
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        //need to bind here

        val binding: ObjectiveMainObjectLayoutBinding
        if (convertView == null) {
            binding = ObjectiveMainObjectLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
            binding.root.tag = binding
        } else {
            binding = convertView.tag as ObjectiveMainObjectLayoutBinding
        }

        binding?.objectiveMainModel = getItem(position) as ObjectiveModel

        binding.details.setOnClickListener {
            val view = binding.moreDetailsLayout
            val button = binding.details
            if(view.visibility == View.GONE){
                collapseOtherDetails(position)
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


        this.mapObjectiveView[position] = binding
        return binding.root
    }

    fun collapseOtherDetails (currentPosition : Int ){
        if(this.mapObjectiveView == null || this.mapObjectiveView.isEmpty()) return

        var sortedList : Map<Int, ObjectiveMainObjectLayoutBinding> = this.mapObjectiveView.filter { (k,v) -> k !=currentPosition
                && v.moreDetailsLayout.visibility==View.VISIBLE
        }
        sortedList.forEach { (k, v) ->
            System.out.println("HERE UPDATE DETAILS")
            val view = v.moreDetailsLayout
            val animation = AnimationUtils.loadAnimation(context, R.anim.slide_right_out)
            view.startAnimation(animation)
            Handler().postDelayed({
                view.visibility = View.GONE
                v.invalidateAll()
            }, 300)
        }
    }



    override fun getItem(position: Int): Any {
        return dataSource.get(position)
   }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return this.dataSource.size
    }

    override fun notifyDataSetChanged() {
        super.notifyDataSetChanged()
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


}