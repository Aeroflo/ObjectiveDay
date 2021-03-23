package com.example.objectiveday.internalData

enum class Emoticones {
    THINKING_FACE(0x1F914),
    PARTY_FACE(0x1F973),
    CLAP(0x1F44F),
    WOOZY_FACE(0x1F974),
    UNAMUSED_FACE(0x1F612),
    PARTY_POPPER(0x1F389),
    DIZZY_FACE(0x1F4AB)
    ;

    var unicode : Int? = null
    constructor(unicode : Int)
    {
        this.unicode = unicode
    }

}