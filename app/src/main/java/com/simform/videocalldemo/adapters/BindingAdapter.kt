package com.simform.videocalldemo.adapters

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import com.simform.videocalldemo.R

class BindingAdapter {
  companion object{
    @JvmStatic
    @BindingAdapter("timer_text")
    fun setTimerText(textView:TextView, seconds:MutableLiveData<Int>){
      val second = (seconds.value?.div(1000))?.rem(60)
      val minutes = (seconds.value?.div(1000))?.div(60)
      textView.text = textView.context.getString(R.string.msg_call_timer,minutes,second)
    }
  }
}