package yapp.dev_diary

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class
baseActivity<T: ViewDataBinding> : AppCompatActivity()  {

    lateinit var viewDataBinding : T

    abstract val layoutResourceId : Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewDataBinding = DataBindingUtil.setContentView(this, layoutResourceId)
    }
}