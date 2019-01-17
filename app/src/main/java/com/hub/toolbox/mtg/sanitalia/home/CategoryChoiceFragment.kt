package com.hub.toolbox.mtg.sanitalia.home


import android.annotation.SuppressLint
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
import aqua.extensions.*
import com.hub.toolbox.mtg.sanitalia.R
import com.hub.toolbox.mtg.sanitalia.constants.Group
import com.hub.toolbox.mtg.sanitalia.constants.HomeServicesCategoriesWithImages
import com.hub.toolbox.mtg.sanitalia.data.Operator_.zoneId
import com.hub.toolbox.mtg.sanitalia.data.Zuldru
import getViewModelOf
import kotlinx.android.synthetic.main.category_choice_card.view.*
import kotlinx.android.synthetic.main.fragment_category_choice.*

class CategoryChoiceFragment : Fragment() {

    val viewModel by lazy { getViewModelOf<HomeViewModel>(activity!!) }
    private lateinit var counters : HashMap<String,Int>
    lateinit var group : Group

    companion object {
        @JvmStatic
        fun newInstance(group: Group) = CategoryChoiceFragment().apply {
            this.group = group
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_category_choice, container, false) as ViewGroup
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firstCategoryRecyclerView.layoutManager = GridLayoutManager(activity, 2)
        val rcDecorator = ItemOffsetDecoration(requireContext(), R.dimen.recyclerViewSpacing)
        firstCategoryRecyclerView.addItemDecoration(rcDecorator)

        if (group == Group.HOME_SERVICES){
            viewModel.isLoading.postValue(true)
            Zuldru.getNumberOfOperatorsForZoneId(viewModel.zoneId.value!!, onSuccess = { list ->
                if (isAdded){
                    if (list.size > 0){
                        counters = list
                        val l = HomeServicesCategoriesWithImages.map { pair -> Pair(pair.first, getDrawable(pair.second)) }
                        firstCategoryRecyclerView.adapter = HomeServiceCategories(l, activity as FragmentActivity, counters)
                        viewModel.isLoading.postValue(false)
                    } else {
                        noOperatorsFound.show()
                        firstCategoryRecyclerView.hide()
                    }
                }
            })
        }
    }

    class HomeServiceCategories(val items : List<Pair<String,Drawable>>, val activity : Activity, private val countList : HashMap<String,Int>) : RecyclerView.Adapter<HomeServiceCategories.ViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(activity).inflate(R.layout.category_choice_card, parent, false), activity, countList)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items[position], position)
        }

        override fun getItemCount() = items.size

        class ViewHolder(itemView : View, val activity: Activity, private val countList : HashMap<String,Int>) : RecyclerView.ViewHolder(itemView){
            @SuppressLint("SetTextI18n")
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

                    when(typeId){
                        1 -> categoryName.textSize = 14.0f
                    }
                }
                itemView.setOnClickListener {
                    when(typeId){
                        0 -> (activity as HomeActivity).listFragForPhysio()
                        1 -> (activity as HomeActivity).listFragForOss()
                        2 -> (activity as HomeActivity).listFragForNurse()
                        3 -> (activity as HomeActivity).listFragForElderCare()
                    }
                }
            }
        }
    }
}
