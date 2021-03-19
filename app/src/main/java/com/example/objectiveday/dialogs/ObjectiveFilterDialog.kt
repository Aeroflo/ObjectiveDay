package com.example.objectiveday.dialogs


import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.example.objectiveday.R
import kotlinx.android.synthetic.main.objectivefilterdialog.*
import java.lang.Exception


class ObjectiveFilterDialog : DialogFragment() {

    public interface OnInputListener{
        fun sendInput(filter:ObjectiveFilter?)
    }

    var onInputListener: OnInputListener? = null
    companion object {
        const val TAG = "FilterDialog"
    }

    //RadioGroup
    var selectFilterRG : RadioGroup? = null

    //View Description
    var filterByDescriptionRB : RadioButton? = null
    var layoutDecription : View? = null
    var description : EditText? = null

    //View Days
    var filterByDaysRB : RadioButton? = null
    var layoutDays : View? = null
    var mondayCB : CheckBox? = null
    var tuesdayCB : CheckBox? = null
    var wednesdayCB : CheckBox? = null
    var thurdayCB : CheckBox? = null
    var fridayCB : CheckBox? = null
    var saturdayCB : CheckBox? = null
    var sundayCB : CheckBox? = null

    //View done date
    var filterByDoneDate : RadioButton? = null
    //Add layout view

    //View done
    var filterDone : RadioButton? = null

    var filterTodo : RadioButton? = null



    //filter button
    var filterButton : Button? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.objectivefilterdialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView(view)
        setUpClickListener(view)
    }

    fun setupView(view: View){

        this.selectFilterRG = view.findViewById(R.id.filterradiobutton)

        this.filterByDescriptionRB = view.findViewById(R.id.filterdescriptionRB)
        this.layoutDecription = view.findViewById(R.id.objectivefilterdescriptionlayout)
        this.description = view.findViewById(R.id.filterdescriptionfield)

        this.filterByDaysRB =view.findViewById(R.id.filterdaysRB)
        this.layoutDays = view.findViewById(R.id.objectivefilterdays)
        this.mondayCB = view.findViewById(R.id.filtermonday)
        this.tuesdayCB = view.findViewById(R.id.filtertuesday)
        this.wednesdayCB = view.findViewById(R.id.filterwednesday)
        this.thurdayCB = view.findViewById(R.id.filterthursday)
        this.fridayCB = view.findViewById(R.id.filterfriday)
        this.saturdayCB = view.findViewById(R.id.filtersaturday)
        this.sundayCB = view.findViewById(R.id.filtersunday)


        this.filterByDoneDate = view.findViewById(R.id.filterdonedateRB)

        this.filterDone = view.findViewById(R.id.filterdoneRB)

        this.filterTodo = view.findViewById(R.id.filtertodoRB)

        this.filterButton = view.findViewById(R.id.filterbutton)
    }

    fun setUpClickListener(view: View){
        this.selectFilterRG!!.setOnCheckedChangeListener{ group, checkedId ->
                when(checkedId){
                    R.id.filterdescriptionRB -> {
                        this.layoutDecription!!.visibility = View.VISIBLE
                        this.layoutDays!!.visibility = View.GONE
                    }
                    R.id.filterdaysRB -> {
                        this.layoutDecription!!.visibility = View.GONE
                        this.layoutDays!!.visibility = View.VISIBLE
                    }
                    R.id.filterdonedateRB ->{
                        this.layoutDecription!!.visibility = View.GONE
                        this.layoutDays!!.visibility = View.GONE
                    }
                    R.id.filtertodoRB -> {
                        this.layoutDecription!!.visibility = View.GONE
                        this.layoutDays!!.visibility = View.GONE
                    }
                    R.id.filterdonedateRB ->{
                        this.layoutDecription!!.visibility = View.GONE
                        this.layoutDays!!.visibility = View.GONE
                    }
                }

            }

        this.filterButton!!.setOnClickListener {
            var sortByDescription = this.filterByDescriptionRB!!.isChecked
            var sortByDays = this.filterByDaysRB!!.isChecked
            var sortByDoneDate = this.filterdonedateRB!!.isChecked
            var sortByDone = this.filterDone!!.isChecked
            var sortByTodo = this.filterTodo!!.isChecked

            var filter = ObjectiveFilter.Builder()
            if(sortByDescription){
                filter.withFilterType(ObjectiveFilterType.DESCRIPTION)
                filter.withDescriptionFilter(this.description!!.text.toString())
            }
            else if(sortByDays){
                filter.withFilterType(ObjectiveFilterType.DAYS)
                filter.withDaysFilters(
                    this.mondayCB!!.isChecked,
                    this.tuesdayCB!!.isChecked,
                    this.wednesdayCB!!.isChecked,
                    this.thurdayCB!!.isChecked,
                    this.fridayCB!!.isChecked,
                    this.saturdayCB!!.isChecked,
                    this.sundayCB!!.isChecked
                )
            }
            else if(sortByDone){
                filter.withFilterType(ObjectiveFilterType.DONE)
            }
            else if(sortByTodo){
                filter.withFilterType(ObjectiveFilterType.TODO)
            }
            else if(sortByDoneDate){
                filter.withFilterType(ObjectiveFilterType.LAST_TIME_DONE)
            }

            onInputListener!!.sendInput(filter.build())
            this.dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    public final fun getFilterButtonT(): Button? {
        return filterButton
    }

    public fun getObjectiveFilterModel() : ObjectiveFilter{
        return ObjectiveFilter.Builder(ObjectiveFilterType.NONE).build()
    }


    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        try{
            onInputListener = activity as OnInputListener
        }catch(e:Exception){

        }
    }
}