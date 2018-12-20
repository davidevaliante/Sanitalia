package com.hub.toolbox.mtg.sanitalia.home


import android.app.Activity
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import aqua.extensions.goTo
import com.hub.toolbox.mtg.sanitalia.profiles.ProfileActivity

import com.hub.toolbox.mtg.sanitalia.R
import com.hub.toolbox.mtg.sanitalia.data.OperatorRegistration
import com.squareup.picasso.Picasso
import getViewModelOf
import kotlinx.android.synthetic.main.fragment_category_list.*
import kotlinx.android.synthetic.main.list_card.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CategoryListFrag : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    val viewModel by lazy { getViewModelOf<HomeViewModel>(activity!!) }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoryListRecyclerView.layoutManager = LinearLayoutManager(activity)

        viewModel.getListOfFisioterapisti{
            categoryListRecyclerView.adapter = CategoryListAdapter(it, activity!!, viewModel)
        }

    }

    class CategoryListAdapter(var list : List<OperatorRegistration>, val activity : FragmentActivity, val viewModel: HomeViewModel) : RecyclerView.Adapter<CategoryListAdapter.ViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(activity).inflate(R.layout.list_card, parent, false), activity, viewModel)
        }

        override fun getItemCount() = list.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(list[position])
        }

        fun updateList(newList : List<OperatorRegistration>){
            this.list = newList
            notifyDataSetChanged()
        }

        class ViewHolder(itemView : View,val activity: Activity, val viewModel: HomeViewModel) : RecyclerView.ViewHolder(itemView){
            fun bind(data : OperatorRegistration){

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
                        (activity as AppCompatActivity).goTo<ProfileActivity>()
                    }
                }
            }
        }
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                CategoryListFrag().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
