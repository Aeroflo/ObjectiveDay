package com.example.objectiveday.adapters

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.fragment.app.FragmentManager
import com.example.objectiveday.R
import com.example.objectiveday.controllers.ObjectiveBindingController
import com.example.objectiveday.databinding.ObjectiveMainObjectLayoutBinding
import com.example.objectiveday.models.ObjectiveModel
import kotlin.collections.HashMap

class ObjectiveListAdapter(private val context: Context, private val applicationContext: Context,
                    private var dataSource: List<ObjectiveModel>, private val supportFragementManager : FragmentManager
) : BaseAdapter() {

    //Map position view
    var mapObjectiveView : MutableMap<Int, ObjectiveMainObjectLayoutBinding> = HashMap()

    fun updateDataSource(newList: List<ObjectiveModel>){
        this.dataSource=newList;
        this.notifyDataSetChanged()
    }



    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        //need to bind here

        var binding: ObjectiveMainObjectLayoutBinding
        if (convertView == null) {
            binding = ObjectiveMainObjectLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
            binding.root.tag = binding
        } else {
            binding = convertView.tag as ObjectiveMainObjectLayoutBinding
        }

        var controller : ObjectiveBindingController = ObjectiveBindingController(this.context, this.applicationContext, this.supportFragementManager )
        binding = controller.prepareBinding(binding, getItem(position) as ObjectiveModel)!!
        //binding?.objectiveMainModel = getItem(position) as ObjectiveModel


        binding.objectiveAllLayout.setOnClickListener(View.OnClickListener {
            System.out.println("HHHHHH")
        })
        //if not found in map add...
        this.mapObjectiveView[position] = binding
        return binding.root
    }

    fun collapseOtherDetails (currentPosition : Int ){
        if(this.mapObjectiveView == null || this.mapObjectiveView.isEmpty()) return

        var sortedList : Map<Int, ObjectiveMainObjectLayoutBinding> = this.mapObjectiveView.filter { (k,v) -> k !=currentPosition
                //&& v.moreDetailsLayout.visibility==View.VISIBLE
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




}