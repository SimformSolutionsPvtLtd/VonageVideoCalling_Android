package com.simform.videocalldemo.videocall.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.simform.videocalldemo.R
import com.simform.videocalldemo.databinding.ActivityJoinCallBinding

class JoinCallActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJoinCallBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_join_call)
        binding.activity = this
    }

    fun onclickImpl(view: View) {
        when (view.id) {
            R.id.button_join -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }
}