package vn.asiantech.englishtest.settings

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_setting.*
import vn.asiantech.englishtest.R
import vn.asiantech.englishtest.takingtest.TakingTestActivity

class SettingFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isSwitchOnOrOff()
    }

    private fun isSwitchOnOrOff() {
        val switchState = activity?.getSharedPreferences("switchcase", Context.MODE_PRIVATE)
        val isSwitchChecked = switchState?.getBoolean("switchState", false)
        if(isSwitchChecked == true) {
            switchShowAnswer.isChecked = true
        }
        switchShowAnswer.setOnCheckedChangeListener { _, isChecked ->
            (activity as TakingTestActivity).isSwitchAnswerOn = isChecked
            switchState?.edit()?.putBoolean("switchState", (activity as TakingTestActivity).isSwitchAnswerOn)?.apply()
        }
    }
}
