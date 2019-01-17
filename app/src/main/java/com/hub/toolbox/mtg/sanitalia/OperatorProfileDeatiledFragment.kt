package com.hub.toolbox.mtg.sanitalia


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import aqua.extensions.getDrawable
import aqua.extensions.inflate
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.hub.toolbox.mtg.sanitalia.constants.DoctorsSpecs
import com.hub.toolbox.mtg.sanitalia.constants.NurseSpecs
import com.hub.toolbox.mtg.sanitalia.constants.PhysiotherapistSpecs
import com.hub.toolbox.mtg.sanitalia.data.Operator
import com.hub.toolbox.mtg.sanitalia.home.HomeActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_base_profile.*
import kotlinx.android.synthetic.main.fragment_operator_profile_deatiled.*


private const val OPERATOR_KEY = "OPERATOR"


class OperatorProfileDeatiledFragment : Fragment() {
    private var operatorToShow: Operator? = null
    companion object {
        @JvmStatic
        fun newInstance(operator: Operator) =
                OperatorProfileDeatiledFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(OPERATOR_KEY, operator)
                    }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            operatorToShow = it.getParcelable(OPERATOR_KEY)
        }


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity as HomeActivity).changeBottomBarForGroupOne()
        return inflate(R.layout.fragment_operator_profile_deatiled) as ViewGroup
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        operatorProfileName.text = operatorToShow?.firstName?.capitalize()
        operatorProfileLastName.text = operatorToShow?.lastName?.capitalize()
        operatorProfileAdress.text = operatorToShow?.adressName?.capitalize()
        operatorProfileCity.text = operatorToShow?.city?.capitalize()
        Picasso.get().load(operatorToShow?.image).into(operatorProfilePicture)
        profilePhoneField.text = "Chiama +39 ${operatorToShow?.phone}"
        profilePhoneField.setOnClickListener {
            // chiama
        }
        operatorProfileDesc.text = if (operatorToShow?.description == null) "${operatorToShow?.firstName} non ha ancora fornito informazioni aggiuntive"
        else "${operatorToShow?.description}"
        operatorToShow?.let {
            it.spec.let { specs ->
                if (specs!=null && specs.isNotEmpty()) operatorProfileSpecs.text = when (it.group){
                    0 -> when (it.category){
                        0 -> {
                            val operatorSpecs : List<Int>? = it.spec
                            operatorSpecs?.let {
                                val specIndeciesToShow : Set<Int> = PhysiotherapistSpecs.values.intersect(operatorSpecs.toList())
                                PhysiotherapistSpecs.filter { pair -> specIndeciesToShow.contains(pair.value) }.keys.joinToString()
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
}
