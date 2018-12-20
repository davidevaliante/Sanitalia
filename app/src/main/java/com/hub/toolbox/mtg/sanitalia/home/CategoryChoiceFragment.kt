package com.hub.toolbox.mtg.sanitalia.home


import android.app.Activity
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import aqua.extensions.getDrawable
import com.hub.toolbox.mtg.sanitalia.R
import getViewModelOf
import kotlinx.android.synthetic.main.category_icon.view.*
import kotlinx.android.synthetic.main.fragment_category_choice.*

class CategoryChoiceFragment : Fragment() {

    val viewModel by lazy { getViewModelOf<HomeViewModel>(activity!!) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)

        setEnterSharedElementCallback(object : SharedElementCallback(){
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_category_choice, container, false) as ViewGroup


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firstCategoryRecyclerView.layoutManager = GridLayoutManager(activity, 3)
        val list = listOf(
                Pair("Fisioterapisti", getDrawable(R.drawable.ic_stethoscope)),
                Pair("Categoria 2", getDrawable(R.drawable.ic_stethoscope)),
                Pair("Categoria 3", getDrawable(R.drawable.ic_stethoscope)),

                Pair("Assistenza Anziani", getDrawable(R.drawable.ic_stethoscope)),
                Pair("Categoria 5", getDrawable(R.drawable.ic_stethoscope)),
                Pair("Categoria 6", getDrawable(R.drawable.ic_stethoscope)),
                Pair("Infermieri", getDrawable(R.drawable.ic_stethoscope)),
                Pair("Categoria 8", getDrawable(R.drawable.ic_stethoscope)),
                Pair("Categoria 9", getDrawable(R.drawable.ic_stethoscope))
        )

        firstCategoryRecyclerView.adapter = FirstButtonRecyclerAdapter(list, activity as FragmentActivity)
    }

    class FirstButtonRecyclerAdapter(val items : List<Pair<String,Drawable>>, val activity : Activity) : RecyclerView.Adapter<FirstButtonRecyclerAdapter.ViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(activity).inflate(R.layout.category_icon, parent, false), activity)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items[position])
            holder.setTransitionName(position)
        }

        override fun getItemCount() = items.size


        class ViewHolder(itemView : View,val activity: Activity) : RecyclerView.ViewHolder(itemView){
            fun bind(data : Pair<String,Drawable>){
                itemView.apply {
                    categoryIcon.setImageDrawable(data.second)
                    categoryName.text  = data.first
                }
                itemView.setOnClickListener {
                    (activity as HomeActivity).instantiateListFrag()
                }
            }

            fun setTransitionName(position : Int){
                when(position){
                    0 -> itemView.transitionName = "type_1"
                    3 -> itemView.transitionName = "type_2"
                    6 -> itemView.transitionName = "type_3"
                }
            }
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) = CategoryChoiceFragment().apply { }
    }
}
