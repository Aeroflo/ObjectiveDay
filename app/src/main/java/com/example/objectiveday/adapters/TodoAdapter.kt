package com.example.objectiveday.adapters

import android.content.Context
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.objectiveday.controllers.ButtonSignIn
import com.example.objectiveday.controllers.TodoProgressBar
import com.example.objectiveday.controllers.TokenSingleton
import com.example.objectiveday.databinding.ObjectiveBinding
import com.example.objectiveday.databinding.ObjectiveMainObjectLayoutBinding
import com.example.objectiveday.internalData.DataSingleton
import com.example.objectiveday.models.ObjectiveModel
import com.example.objectiveday.webservices.apimodels.APIObjectives
import com.example.objectiveday.webservices.retrofit.RestAPIService
import com.google.android.gms.common.SignInButton

class TodoAdapter (private val context: Context,
private var dataSource: List<ObjectiveModel>) : BaseAdapter()  {



    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var binding : ObjectiveBinding
        if(convertView == null){
            binding = ObjectiveBinding.inflate(LayoutInflater.from(context), parent, false)
            binding.objectiveMain = getItem(position) as ObjectiveModel
            binding.root.tag = binding
        }else{
            binding = convertView.tag as ObjectiveBinding
        }

        var todoProgressBar = ButtonSignIn(this.context,binding.markasdone)
        todoProgressBar.setModeTodo(objectiveModel = binding.objectiveMain)
        binding.markasdone.setOnClickListener {
            mHandler.post({
                binding.markasdone.isClickable = false
                todoProgressBar.buttonActivated()
            })
            //get objective by id --> call API
            Thread{

                val apiService = RestAPIService(TokenSingleton.instance.url)
                var doneSuccess : APIObjectives? =  DataSingleton.instance.markObjectiveAsDone(this.context, binding.objectiveMain)//apiService.markObjectiveAsDone(TokenSingleton.instance.getToken()!!, binding.objectiveMain!!.id!!)
                Thread.sleep(2000)
                mHandler.post({
                    binding.invalidateAll()
                    if(doneSuccess!= null) todoProgressBar.buttonFinished(true)
                    else{
                        todoProgressBar.buttonFinished(false)
                        binding.markasdone.isClickable = true
                    }
                })
            }.start()
        }
        return binding.root
    }

    val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            System.out.println("MESSAGE")
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

}