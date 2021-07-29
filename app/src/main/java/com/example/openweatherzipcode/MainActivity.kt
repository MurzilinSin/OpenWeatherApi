package com.example.openweatherzipcode

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.openweatherzipcode.view.MainFragment
import com.example.openweatherzipcode.view.MainFragmentJava

class  MainActivity : AppCompatActivity() {
    //MainActivity служит для запуска основного фрагмента, который является единственным в данной программе
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow()
        }
    }
}