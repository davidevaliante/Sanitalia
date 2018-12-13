package com.hub.toolbox.mtg.sanitalia.registration.standard


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.hub.toolbox.mtg.sanitalia.R
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsOptions
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import getViewModelOf
import kotlinx.android.synthetic.main.fragment_base_profile.*
import android.app.Activity.RESULT_OK
import android.util.Log
import android.view.Gravity
import android.widget.EditText
import androidx.core.content.res.ResourcesCompat
import aqua.extensions.*
import com.github.florent37.kotlin.pleaseanimate.please
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment
import com.hub.toolbox.mtg.sanitalia.constants.Provinces
import com.squareup.picasso.Callback
import com.google.android.gms.location.places.Places as Places
import com.squareup.picasso.Picasso
import java.lang.Exception


class BaseProfileFragment : Fragment() {

    private val quickPermissionsOption by lazy {QuickPermissionsOptions(
            handleRationale = false,
            rationaleMessage = getString(R.string.ask_for_external_storage_message),
            permanentlyDeniedMessage = "Custom permanently denied message",
            rationaleMethod = { req ->  },
            permanentDeniedMethod = { req ->  }
    )}
    private val viewModel by lazy {  getViewModelOf<OperatorProfileViewModel>(activity as FragmentActivity) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflate(R.layout.fragment_base_profile)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareViewsForEnterAnimation()
        setupAutocompleteFrament()

        // setta immagine iniziale se presente
        updateProfilePic(viewModel.profileImage.value)
        // osserva cambiamenti nell'Uri dell'immagine ed aggiorna
        viewModel.profileImage.observe(this, Observer { updateProfilePic(it) })

        viewModel.profileFromLocal.observe(this, Observer { newValue ->
            newValue.let {
                firstNameField.setText(it.firstName)
                lastNameField.setText(it.lastName)
                emailField.setText(it.email)
                phoneField.setText(it.phone)
            }
        })

        viewModel.temporaryOperatorProfile.observe(this, Observer { newValue ->
            Log.d("BASE_PROFILE", newValue.toString())
        })

        // ------------------------CLICKS-------------------------------------------------

        anagraphicNextButton.setOnClickListener {
            startExitAnimation()
        }

        cameraIcon.setOnClickListener { changeProfileImageWithPermissions() }

        ownProfileImage.setOnClickListener { changeProfileImageWithPermissions() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                viewModel.updateProfilePictureLocally(result.uri.toString())
                Picasso.get().load(result.uri.toString()).into(ownProfileImage)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                activity?.showMessage(error.toString())
            }
        }
    }



    private fun setupAutocompleteFrament(){
        val autoCompleteFilters = AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS).setCountry("IT").build()
        val autoComplete = childFragmentManager.findFragmentById(R.id.place_autocomplete_fragment) as SupportPlaceAutocompleteFragment
        autoComplete.setHint("Indirizzo lavorativo")
        autoComplete.setFilter(autoCompleteFilters)
        val autoCompleteEditText = autoComplete.view?.findViewById<EditText>(R.id.place_autocomplete_search_input)
        autoCompleteEditText?.textSize = 18.0f
        autoCompleteEditText?.typeface = ResourcesCompat.getFont(requireContext(), R.font.ubuntu_light)
        autoCompleteEditText?.setHintTextColor(activity!!.findColor(R.color.light_text_color))
        autoComplete.setOnPlaceSelectedListener(object : PlaceSelectionListener{
            override fun onPlaceSelected(place: Place?) {
                Log.d("PICKED_PLACE", place.toString())
                val province = place?.address?.toString()?.split(",")?.get(1)?.split(" ")?.last()
                Log.d("PICKED_PLACE", "Codice provincia : ${province}, Valore Stringa : ${Provinces.get(province)?.first} , Regione : ${Provinces.get(province)?.second}")

                place?.let {
                    var currentUserState = viewModel.profileFromLocal.value
                    currentUserState?.let{
                        it.fullAdress = place.address.toString()
                        it.lat = place.latLng.latitude
                        it.lon = place.latLng.longitude
                    }
                    viewModel.updateTemporaryProfile(currentUserState!!)
                }

            }

            override fun onError(error: Status?) {
                Log.i("PLACES_AUTO_ERR", error.toString())
            }

        })
    }

    private fun updateProfilePic(source : String?){
        Picasso.get().load(source).into(ownProfileImage, object : Callback{
            override fun onError(e: Exception?) {

            }

            override fun onSuccess() {
                please(duration = 200L) {
                    animate(ownProfileImage) toBe {
                        originalScale()
                    }
                }.start()
            }

        })
    }

    private fun changeProfileImageWithPermissions() =
    runWithPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, options = quickPermissionsOption){
        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(activity as Activity)
    }

    //--------------------------ANIMAZIONI--------------------------------------------------------
    private fun prepareViewsForEnterAnimation(){
        please {
            animate(ownProfileImage) toBe {
                scale(0f, 0f)
            }
        }.now()
    }

    private fun startExitAnimation(){
        please(duration = 300L) {
            animate(baseProfileRoot) toBe {
                outOfScreen(Gravity.START)
            }
        }.start().withEndAction {
            viewModel.goToCategoryFragment()
        }
    }
}
