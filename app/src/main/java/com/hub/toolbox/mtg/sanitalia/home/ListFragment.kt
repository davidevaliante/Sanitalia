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
import com.hub.toolbox.mtg.sanitalia.R.id.*
import com.hub.toolbox.mtg.sanitalia.constants.*
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        arguments?.getParcelable<Group>(GROUP_STRING)?.let { group = it }
        arguments?.getParcelable<Categories>(CATEGORY_STRING)?.let { category = it }

        (activity as HomeActivity).apply {
            if (group == Group.HOME_SERVICES){
                when(category){
                    Categories.PHYSIO -> changeBottomBarForPhysioListFragment()
                    Categories.NURSE -> changeBottomBarForNurseListFragment()
                    else -> changeBottomBarForGroupOne()
                }
            }
            if (group == Group.DOCTOR) changeBottomBarForDoctorListFragment()
        }

        return inflater.inflate(R.layout.fragment_category_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoryListRecyclerView.layoutManager = LinearLayoutManager(activity)

        categoryListRecyclerView.adapter = OperatorListAdapter(linkedMapOf(), activity!!, viewModel)

        viewModel.operatorMap.observe(this, Observer { newOperatorMap ->
            (categoryListRecyclerView.adapter as OperatorListAdapter).updateList(newOperatorMap)
        })

        viewModel.categoryFilters.observe(this, Observer { newFilterList ->
            viewModel.applyFilters()
        })

        viewModel.filteredMap.observe(this, Observer { newFilteredMap ->
            newFilteredMap?.let {
                (categoryListRecyclerView.adapter as OperatorListAdapter).updateList(newFilteredMap)
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
                            onListFound = { viewModel.operatorMap.postValue(it) },
                            onListEmptyOrNull = { operationHasNoResult.show() }
                    )
                }
                Categories.NURSE -> {
                    viewModel.getListOfNurse (
                            onListFound = { viewModel.operatorMap.postValue(it) },
                            onListEmptyOrNull = { operationHasNoResult.show() }
                    )
                }
                Categories.OSS -> {
                    viewModel.getListOfOss(
                            onListFound = { viewModel.operatorMap.postValue(it) },
                            onListEmptyOrNull = { operationHasNoResult.show() }
                    )
                }
                else -> {}
            }
        }

        if (group == Group.DOCTOR){
            viewModel.getListOfDoctors(
                    onListFound = { viewModel.operatorMap.postValue(it) },
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
                    expandedAddToFav.setOnClickListener { activity.toast("should add to fav $operatorId") }
                    expandedViewProfile.setOnClickListener { activity.toast("should go to profile $operatorId") }

                    // expanded -> specs
                    // ritorna una map di tutte le specializzazioni indipendentemente dai filtri
                    val specList: Map<String, Int> = when {
                        operator.group == 0 -> when (operator.category) {
                            0 -> PhysiotherapistSpecs.filter { operator.spec!!.contains(it.value) }
                            2 -> NurseSpecs.filter { operator.spec!!.contains(it.value) }
                            else -> mapOf("Non ha ancora specificato nessuna specializzazione" to -1)
                        }
                        operator.group == 1 -> DoctorsSpecs.filter { operator.spec!!.contains(it.value) }
                        else -> mapOf("Non ha ancora specificato nessuna specializzazione" to -1)
                    }

                    viewModel.categoryFilters.value?.let { currentFilters ->
                        // se i filtri non sono null o 0
                        val orderedSpecList =
                            if (currentFilters.isNotEmpty()){
                                val matching : Set<Int> = specList.values.intersect(currentFilters)
                                val notMatching : Set<Int> = specList.values.subtract(currentFilters)
                                val listOfMatching = specList.filter { spec -> matching.contains(spec.value) }.keys
                                val listOfNotMatching = specList.filter { spec -> notMatching.contains(spec.value) }.keys

                                "${listOfMatching.joinToString()},${listOfNotMatching.joinToString()}"
                            } else specList.keys.joinToString()

                        expandedSpecsList.text = if (orderedSpecList.length > 100) orderedSpecList.substring(0, 100) + Typography.ellipsis
                                                 else orderedSpecList
                    }


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
