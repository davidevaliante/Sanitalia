package com.hub.toolbox.mtg.sanitalia.home


import android.app.Activity
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import aqua.extensions.ItemOffsetDecoration
import aqua.extensions.getDrawable
import com.hub.toolbox.mtg.sanitalia.R
import com.hub.toolbox.mtg.sanitalia.constants.Group
import com.hub.toolbox.mtg.sanitalia.constants.HomeServicesCategoriesWithImages
import getViewModelOf
import kotlinx.android.synthetic.main.category_icon.view.*
import kotlinx.android.synthetic.main.fragment_category_choice.*
import kotlinx.android.synthetic.main.fragment_category_choice.view.*

class CategoryChoiceFragment : Fragment() {

    val viewModel by lazy { getViewModelOf<HomeViewModel>(activity!!) }
    lateinit var group : Group

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_category_choice, container, false) as ViewGroup
        if (group == Group.HOME_SERVICES) root.categoryFilterTitle.text = "Assistenza Domiciliare"

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firstCategoryRecyclerView.layoutManager = GridLayoutManager(activity, 2)
        val rcDecorator = ItemOffsetDecoration(requireContext(), R.dimen.recyclerViewSpacing)
        firstCategoryRecyclerView.addItemDecoration(rcDecorator)

        if (group == Group.HOME_SERVICES){
            val list = HomeServicesCategoriesWithImages.map { pair -> Pair(pair.first, getDrawable(pair.second)) }
            firstCategoryRecyclerView.adapter = HomeServiceCategories(list, activity as FragmentActivity)
        }
    }

    class HomeServiceCategories(val items : List<Pair<String,Drawable>>, val activity : Activity) : RecyclerView.Adapter<HomeServiceCategories.ViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(activity).inflate(R.layout.category_icon, parent, false), activity)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items[position])
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
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(group: Group) = CategoryChoiceFragment().apply {
            this.group = group
        }
    }
}
