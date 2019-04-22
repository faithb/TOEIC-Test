package vn.asiantech.englishtest.questiondetailviewpager

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import vn.asiantech.englishtest.model.QuestionDetail

class QuestionDetailAdapter(fm: FragmentManager, private var questionList: ArrayList<QuestionDetail>) :
    FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int) = QuestionDetailFragment.getInstance(position, questionList[position])

    override fun getCount() = questionList.size

    override fun getItemPosition(`object`: Any) = PagerAdapter.POSITION_NONE
}
