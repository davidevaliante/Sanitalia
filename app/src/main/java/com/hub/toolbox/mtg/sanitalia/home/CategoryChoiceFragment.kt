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
import com.hub.toolbox.mtg.sanitalia.R.id.*
import com.hub.toolbox.mtg.sanitalia.constants.Group
import com.hub.toolbox.mtg.sanitalia.constants.HomeServiceCategories
import com.hub.toolbox.mtg.sanitalia.constants.HomeServicesCategoriesWithImages
import com.hub.toolbox.mtg.sanitalia.data.Operator_.zoneId
import com.hub.toolbox.mtg.sanitalia.data.Zuldru
import getViewModelOf
import kotlinx.android.synthetic.main.category_icon.view.*
import kotlinx.android.synthetic.main.fragment_category_choice.*
import kotlinx.android.synthetic.main.fragment_category_choice.view.*

class CategoryChoiceFragment : Fragment() {

    val viewModel by lazy { getViewModelOf<HomeViewModel>(activity!!) }
    lateinit var counters : HashMap<String,Int>
    lateinit var group : Group

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_category_choice, container, false) as ViewGroup
        Zuldru.getNumberOfOperatorsForZoneId(viewModel.zoneId.value!!, onSuccess = { list ->
            counters = list
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firstCategoryRecyclerView.layoutManager = GridLayoutManager(activity, 2)
        val rcDecorator = ItemOffsetDecoration(requireContext(), R.dimen.recyclerViewSpacing)
        firstCategoryRecyclerView.addItemDecoration(rcDecorator)

        if (group == Group.HOME_SERVICES){
            Zuldru.getNumberOfOperatorsForZoneId(viewModel.zoneId.value!!, onSuccess = { list ->
                counters = list
                val list = HomeServicesCategoriesWithImages.map { pair -> Pair(pair.first, getDrawable(pair.second)) }
                firstCategoryRecyclerView.adapter = HomeServiceCategories(list, activity as FragmentActivity, counters)
            })

        }
    }

    class HomeServiceCategories(val items : List<Pair<String,Drawable>>, val activity : Activity,val countList : HashMap<String,Int>) : RecyclerView.Adapter<HomeServiceCategories.ViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(activity).inflate(R.layout.category_icon, parent, false), activity, countList)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items[position], position)
        }

        override fun getItemCount() = items.size


        class ViewHolder(itemView : View,val activity: Activity,val countList : HashMap<String,Int>) : RecyclerView.ViewHolder(itemView){
            fun bind(data : Pair<String,Drawable>, typeId : Int){
                itemView.apply {
                    categoryIcon.setImageDrawable(data.second)
                    categoryName.text  = data.first
                    if (zoneId != null){
                        when(typeId){
                            0 -> count.text = "${countList["physio"].toString()} professionisti"
                            1 -> count.text = "${countList["oss"].toString()} professionisti"
                            2 -> count.text = "${countList["nurse"].toString()} professionisti"
                            3 -> count.text = "${countList["eldercare"].toString()} professionisti"
                        }
                    }
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
