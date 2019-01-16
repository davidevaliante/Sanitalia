package com.hub.toolbox.mtg.sanitalia.home


import android.annotation.SuppressLint
import android.app.Activity
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import aqua.extensions.show
import com.hub.toolbox.mtg.sanitalia.R
import com.hub.toolbox.mtg.sanitalia.constants.Categories
import com.hub.toolbox.mtg.sanitalia.constants.Group
import com.hub.toolbox.mtg.sanitalia.constants.PhysiotherapistSpecs
import com.hub.toolbox.mtg.sanitalia.data.Operator
import com.squareup.picasso.Picasso
import getViewModelOf
import kotlinx.android.synthetic.main.fragment_category_list.*
import kotlinx.android.synthetic.main.list_card.view.*
import org.jetbrains.anko.toast


private const val GROUP_STRING = "param1"
private const val CATEGORY_STRING = "param2"

class ListFragment : Fragment() {

    private var group: Group = Group.ALL
    private var category: Categories = Categories.NONE
    val viewModel by lazy { getViewModelOf<HomeViewModel>(activity!!) }

    companion object {
        @JvmStatic
        fun newInstance(group: Group, category: Categories) =
            ListFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(GROUP_STRING, group)
                    putParcelable(CATEGORY_STRING, category)
                }
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity as HomeActivity).changeBottomBarForListFragment()
        arguments?.getParcelable<Group>(GROUP_STRING)?.let { group = it }
        arguments?.getParcelable<Categories>(CATEGORY_STRING)?.let { category = it }
        return inflater.inflate(R.layout.fragment_category_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoryListRecyclerView.layoutManager = LinearLayoutManager(activity)

        categoryListRecyclerView.adapter = OperatorListAdapter(linkedMapOf(), activity!!, viewModel)

        viewModel.operatorMap.observe(this, Observer { newOperatorMap ->
            (categoryListRecyclerView.adapter as OperatorListAdapter).updateList(newOperatorMap)
            newOperatorMap.values.forEach { l(it.spec?.toString()!!) }
        })

        viewModel.categoryFilters.observe(this, Observer { newFilterList ->
            viewModel.applyFilters()
            l(newFilterList.toString())
            newFilterList.forEach { l(PhysiotherapistSpecs.keys.toList().get(it)+" with index $it") }
        })

        viewModel.filteredMap.observe(this, Observer { newFilteredMap ->
            newFilteredMap?.let {
                (categoryListRecyclerView.adapter as OperatorListAdapter).updateList(newFilteredMap)
                newFilteredMap.values.forEach { l(it.spec?.toString()!!) }
            }

        })

        if (group == Group.HOME_SERVICES){
            when(category){
                Categories.PHYSIO -> {
                    viewModel.getListOfFisioterapisti(
                        onListFound = { viewModel.operatorMap.postValue(it) },
                        onListEmptyOrNull = { operationHasNoResult.show() }
                    )
                }
                Categories.ELDER_CARE -> {
                    viewModel.getListOfElderCare (
                            onListFound = { categoryListRecyclerView.adapter = OperatorListAdapter(it, activity!!, viewModel) },
                            onListEmptyOrNull = { operationHasNoResult.show() }
                    )
                }
                Categories.NURSE -> {
                    viewModel.getListOfNurse (
                            onListFound = { categoryListRecyclerView.adapter = OperatorListAdapter(it, activity!!, viewModel) },
                            onListEmptyOrNull = { operationHasNoResult.show() }
                    )
                }
                Categories.OSS -> {
                    viewModel.getListOfOss(
                            onListFound = { categoryListRecyclerView.adapter = OperatorListAdapter(it, activity!!, viewModel) },
                            onListEmptyOrNull = { operationHasNoResult.show() }
                    )
                }
                else -> {}
            }
        }

        if (group == Group.DOCTOR){
            viewModel.getListOfDoctors(
                    onListFound = { categoryListRecyclerView.adapter = OperatorListAdapter(it, activity!!, viewModel) },
                    onListEmptyOrNull = { operationHasNoResult.show() }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as HomeActivity).restoreBottomBarToOriginal()
    }

    class OperatorListAdapter(var map : LinkedHashMap<String, Operator>, val activity : FragmentActivity, val viewModel: HomeViewModel) : RecyclerView.Adapter<OperatorListAdapter.ViewHolder>(){

        val expandedCells = HashSet<Int>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(activity).inflate(R.layout.list_card, parent, false), activity, viewModel)
        }

        override fun getItemCount() = map.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(map.values.toList()[position], position)
        }

        fun updateList(newMap : LinkedHashMap<String, Operator>){
            this.map = newMap
            notifyDataSetChanged()
        }

        inner class ViewHolder(itemView : View,val activity: Activity, val viewModel: HomeViewModel) : RecyclerView.ViewHolder(itemView){
            @SuppressLint("SetTextI18n")
            fun bind(operator : Operator, position: Int){

                val operatorId = map.keys.toList()[position]

                val operatorLocation = Location("operator")
                operatorLocation.latitude = operator.lat!!
                operatorLocation.longitude = operator.lon!!

                val selfLocation = Location("userLocation")
                selfLocation.latitude = viewModel.userLat.value!!
                selfLocation.longitude = viewModel.userLon.value!!
                itemView.apply {
                    // expanded
                    Picasso.get().load(operator.image).into(itemView.expandedCardProfileImage)
                    expandedName.text = "${operator.firstName} ${operator.lastName}"
                    expandedAdress.text = "${operator.adressName}, ${operator.city}"
                    val specList: List<String> = if (operator.group == 0) when (operator.category) {
                        0 -> PhysiotherapistSpecs.filter { operator.spec!!.contains(it.value) }.keys.toList()
                        else -> emptyList()
                    } else emptyList()
                    var t = ""
                    specList.forEachIndexed { index, s -> if (index != specList.size - 1) t += s + ", " else t += s }
                    if (t.length > 100) t = t.substring(0, 100) + "..."
                    expandedSpecsList.text = t
                    expandedAddToFav.setOnClickListener { activity.toast("should add to fav $operatorId") }
                    expandedViewProfile.setOnClickListener { activity.toast("should go to profile $operatorId") }

                    // collapsed
                    Picasso.get().load(operator.image).into(itemView.cardProfileImage)
                    cardProfileName.text = "${operator.firstName?.capitalize()} ${operator?.lastName?.capitalize()}"
                    cardProfileAdress.text = "${operator.adressName?.capitalize()}, ${operator.city?.capitalize()}"
                    cardProfileDistance.text = selfLocation.distanceTo(operatorLocation).toString().split(".")[0] + " m"

                    if (expandedCells.contains(position)) {
                        folding_cell.unfold(true)
                        setOnClickListener {
                            if (expandedCells.contains(position)) {
                                folding_cell.fold(false)
                                expandedCells.remove(position)
                            } else {
                                folding_cell.unfold(false)
                                expandedCells.add(position)
                            }
                        }
                    } else {
                        folding_cell.fold(true)
                        setOnClickListener {
                            if (expandedCells.contains(position)) {
                                folding_cell.fold(false)
                                expandedCells.remove(position)
                            } else {
                                folding_cell.unfold(false)
                                expandedCells.add(position)
                            }
                        }
                    }
                }
            }
        }
    }

    fun l (string : String){
        Log.d("FILTER_FRAG", string)
    }

}
