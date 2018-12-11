package com.hub.toolbox.mtg.sanitalia.constants

enum class RegistrationProviders{
    FACEBOOK, GOOGLE, PHONE, EMAIL, NONE
}

enum class RegistrationError {
    NO_ERROR,
    // per Facebook
    FACEBOOK_LOGIN_DISMISSED,
    FACEBOOK_INTENT_FAILED,
    FACEBOOK_LOGIN_FAIL,
    FACEBOOK_LOGIN_ERROR,
    // per Google
    GOOGLE_INTENT_FAILED,
    GOOGLE_LOGIN_FAIL,
    // per Phone
    INVALID_PHONE,
    INVALID_PHONE_CREDENTIALS,
    // per Email e pass
    EMAIL_NOT_VALID,
    PASS_NOT_LONG_ENOUGH,
    PASS_NOT_MATCHING
}

enum class RegistrationStage {
    PICKING_A_PROVIDER,
    PROVIDER_PICKED,
    WAITING_FOR_PHONE_VERIFICATION,
    FIREBASE_REGISTRATION_COMPLETE,
    EMAIL_AND_PASSWORD_SUBMITTED
}