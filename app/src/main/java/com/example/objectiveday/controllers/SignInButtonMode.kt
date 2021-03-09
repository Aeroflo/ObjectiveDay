package com.example.objectiveday.controllers

import android.graphics.Color

enum class SignInButtonMode {
    SIGN_IN("SIGN IN", "Signing! Please wait...", "SIGNED", "NOT SIGNED!"),
    REGISTER_DEVICE("REGISTER DEVICE", "Registering device! please wait", "DEVICE REGISTERED", "DEVICE NOT REGISTERED!"),
    NEW_USER("REGISTRER USER/DEVICE", "Registering user/device! please wait...", "USER/DEVICE REGISTERED", "USER/DEVICE NOT REGISTERED");

    var colorError = Color.RED
    var pressValue: String? = null;
    var runningValue: String? = null;
    var success:String? = null;
    var errorValue: String? = null;
    constructor(pressValue : String, runningValue: String, success: String, errorValue : String){
        this.pressValue = pressValue
        this.runningValue = runningValue
        this.success = success
        this.errorValue = errorValue
    }


}