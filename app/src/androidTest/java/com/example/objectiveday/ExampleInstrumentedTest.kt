package com.example.objectiveday

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.objectiveday", appContext.packageName)
    }

    @Test
    fun convertStringToBinary(){

        for(i in 1..127){
            var monday = integerToBinary(i, 0)
            var tuesday = integerToBinary(i, 1)
            var wednesday = integerToBinary(i, 2)
            var thurday = integerToBinary(i, 3)
            var friday = integerToBinary(i, 4)
            var saturday = integerToBinary(i, 5)
            var sunday = integerToBinary(i, 6)

            System.out.println(""+i+" "+Integer.toBinaryString(i)+" "+monday+" "+tuesday+" "+wednesday+" "+thurday+" "+friday+" "+saturday+" "+sunday)
        }
    }

    fun integerToBinary(value: Int, index : Int) : Boolean{
        return if(index < 0 || index > 7 || value < 0 || value > 127) false
        else {
            val r : String = String.format("%7s", Integer.toBinaryString(value))
            if(r.length <= index) return false
            return r.codePointAt(index).equals(49)
        }
        //return false; //to review

        //Note int = sunday X 1 + saturday * 2 + Friday * 4
    }
}
