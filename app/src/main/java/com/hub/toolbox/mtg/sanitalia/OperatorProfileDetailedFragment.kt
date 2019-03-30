package com.hub.toolbox.mtg.sanitalia


import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import aqua.extensions.getDrawable
import aqua.extensions.inflate
import aqua.extensions.setViewBackgroundWithoutResettingPadding
import com.hub.toolbox.mtg.sanitalia.constants.DoctorsSpecs
import com.hub.toolbox.mtg.sanitalia.constants.NurseSpecs
import com.hub.toolbox.mtg.sanitalia.constants.PhysiotherapistSpecs
import com.hub.toolbox.mtg.sanitalia.data.Operator
import com.hub.toolbox.mtg.sanitalia.home.HomeActivity
import com.hub.toolbox.mtg.sanitalia.home.HomeViewModel
import com.squareup.picasso.Picasso
import getViewModelOf
import kotlinx.android.synthetic.main.operator_profile_lean_layout.*


private const val OPERATOR_KEY = "OPERATOR"
private const val OPERATOR_ID = "OPERATOR_ID"

class OperatorProfileDetailedFragment : Fragment() {
    private var operatorToShow: Operator? = null
    private var operatorId : String? = null
    private var isFav : Boolean = false
    val viewModel by lazy { getViewModelOf<HomeViewModel>(activity!!) }

    companion object {
        @JvmStatic
        fun newInstance(operator: Operator, operatorId : String) =
                OperatorProfileDetailedFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(OPERATOR_KEY, operator)
                        putString(OPERATOR_ID, operatorId)
                    }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            operatorToShow = it.getParcelable(OPERATOR_KEY)
            operatorId = it.getString(OPERATOR_ID)

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity as HomeActivity).hideBottomBar()
        return inflate(R.layout.operator_profile_lean_layout) as ViewGroup
    }

    override fun onDestroy() {
        (activity as HomeActivity).restoreBottomBar()
        super.onDestroy()
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backTop.setOnClickListener { activity?.onBackPressed() }
        operatorProfileCallButton.setOnClickListener { startPhoneCall(operatorToShow?.phone) }
        viewModel.isFavorited(operatorId as String) { result ->
            starTop.apply {
                background = if(result) getDrawable(R.drawable.ic_star)
                             else getDrawable(R.drawable.white_start_empty)
            }
            isFav = result
        }
        when(operatorToShow?.group){
            0 -> when(operatorToShow?.category){
                    0 -> userTypeImage.setImageDrawable(getDrawable(R.drawable.ic_physiotherapy))
                    1 -> userTypeImage.setImageDrawable(getDrawable(R.drawable.ic_hands))
                    2 -> userTypeImage.setImageDrawable(getDrawable(R.drawable.ic_nurse))
                    3 -> userTypeImage.setImageDrawable(getDrawable(R.drawable.ic_elder_care))

                else -> {getDrawable(R.drawable.places_ic_clear)}
            }
            1 -> userTypeImage.setImageDrawable(getDrawable(R.drawable.doctor_colored))
            else -> {userTypeImage.setImageDrawable(getDrawable(R.drawable.places_ic_clear))}
        }

        starTop.setOnClickListener {
            if (isFav)  viewModel.removeFromFavorites(operatorId as String, operatorToShow!!,
                        onSuccess = {
                            starTop.background = getDrawable(R.drawable.white_start_empty)
                            isFav = !isFav
                        })

            else        viewModel.addToFavorites(operatorId as String, operatorToShow!!,
                        onSuccess = {
                            starTop.background = getDrawable(R.drawable.ic_star)
                            isFav = !isFav
                        })
        }

        operatorProfileName.text = operatorToShow?.firstName?.capitalize()
        operatorProfileLastName.text = operatorToShow?.lastName?.capitalize()
        operatorProfileAdress.text = operatorToShow?.adressName?.capitalize()
        operatorProfileCity.text = operatorToShow?.city?.capitalize()
        Picasso.get().load(operatorToShow?.image).into(operatorProfilePicture)
        operatorProfileDesc.text = if (operatorToShow?.description == null) "${operatorToShow?.firstName} non ha ancora fornito informazioni aggiuntive"
        else operatorToShow?.description
        operatorToShow?.let {
            it.spec.let { specs ->
                if (specs!=null && specs.isNotEmpty()) operatorProfileSpecs.text = when (it.group){
                    0 -> when (it.category){
                        0 -> {
                            val operatorSpecs : List<Int>? = it.spec
                            operatorSpecs?.let {
                                val specIndeciesToShow : Set<Int> = PhysiotherapistSpecs.values.intersect(operatorSpecs.toList())
                                PhysiotherapistSpecs.filter { pair -> specIndeciesToShow.contains(pair.value) }.keys.joinToString(separator = "\n")
                            }
                        }

                        2 -> {
                            val operatorSpecs : List<Int>? = it.spec
                            operatorSpecs?.let {
                                val specIndeciesToShow : Set<Int> = NurseSpecs.values.intersect(operatorSpecs.toList())
                                NurseSpecs.filter { pair -> specIndeciesToShow.contains(pair.value) }.keys.joinToString()
                            }
                        }
                        else -> "Nessuna specializzazione specificata"
                    }
                    1 -> {
                        val operatorSpecs : List<Int>? = it.spec
                        operatorSpecs?.let {
                            val specIndeciesToShow : Set<Int> = DoctorsSpecs.values.intersect(operatorSpecs.toList())
                            DoctorsSpecs.filter { pair -> specIndeciesToShow.contains(pair.value) }.keys.joinToString()
                        }
                    }
                    else -> "Nessuna specializzazione specificata"
                } else {operatorProfileSpecs.text = "Nessuna specializzazione specificata"}
            }
        }
    }

    private fun startPhoneCall(phoneNumber : String?){
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phoneNumber")
        startActivity(intent)
    }
}
