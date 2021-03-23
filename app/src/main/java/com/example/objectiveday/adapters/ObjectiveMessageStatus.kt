package com.example.objectiveday.adapters

import com.example.objectiveday.Utils
import com.example.objectiveday.internalData.Emoticones

enum class ObjectiveMessageStatus {
    MAIN("NO OBJECTIVES SET "+Utils.getEmojiByUnicode(Emoticones.THINKING_FACE.unicode)+""),
    FILTERED("NO OBJECTIVES FOUND WITH FILTERS! "+Utils.getEmojiByUnicode(Emoticones.DIZZY_FACE.unicode)),
    TODO(Utils.getEmojiByUnicode(Emoticones.PARTY_POPPER.unicode)+" WOOHOO!!! NOTHING TO DO TODAY!"+Utils.getEmojiByUnicode(Emoticones.CLAP.unicode)),
    DONE("THERE IS NOTHING DONE TODAY! "+Utils.getEmojiByUnicode(Emoticones.UNAMUSED_FACE.unicode));

    var message : String? = null
    constructor(message: String){
        this.message = message
    }

}