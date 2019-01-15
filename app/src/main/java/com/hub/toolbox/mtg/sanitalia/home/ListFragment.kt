package com.hub.toolbox.mtg.sanitalia.home


import android.annotation.SuppressLint
import android.app.Activity
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.hub.toolbox.mtg.sanitalia.R
import com.hub.toolbox.mtg.sanitalia.data.Operator
import com.hub.toolbox.mtg.sanitalia.extensions.log
import com.squareup.picasso.Picasso
import getViewModelOf
import kotlinx.android.synthetic.main.fragment_category_list.*
import kotlinx.android.synthetic.main.list_card.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    val viewModel by lazy { getViewModelOf<HomeViewModel>(activity!!) }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity as HomeActivity).changeBottomBarForListFragment()
        return inflater.inflate(R.layout.fragment_category_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoryListRecyclerView.layoutManager = LinearLayoutManager(activity)

        viewModel.getListOfFisioterapisti{
            log("callback")
            categoryListRecyclerView.adapter = CategoryListAdapter(it, activity!!, viewModel)
        }

    }

    class CategoryListAdapter(var list : List<Operator>, val activity : FragmentActivity, val viewModel: HomeViewModel) : RecyclerView.Adapter<CategoryListAdapter.ViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(activity).inflate(R.layout.list_card, parent, false), activity, viewModel)
        }

        override fun getItemCount() = list.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(list[position])
        }

        fun updateList(newList : List<Operator>){
            this.list = newList
            notifyDataSetChanged()
        }

        class ViewHolder(itemView : View,val activity: Activity, val viewModel: HomeViewModel) : RecyclerView.ViewHolder(itemView){
            @SuppressLint("SetTextI18n")
            fun bind(data : Operator){

                val operatorLocation = Location("operator")
                operatorLocation.latitude = data.lat!!
                operatorLocation.longitude = data.lon!!

                val selfLocation = Location("userLocation")
                selfLocation.latitude = viewModel.userLat.value!!
                selfLocation.longitude = viewModel.userLon.value!!


                itemView.apply {
                    Picasso.get().load(data.image).into(cardProfileImage)
                    cardProfileName.text = "${data.firstName} ${data.lastName}"
                    cardProfileAdress.text = data.fullAdress
                    cardProfileDistance.text = selfLocation.distanceTo(operatorLocation).toString().split(".")[0]+" m"
                    setOnClickListener {
                    }
                }
            }
        }
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                ListFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
