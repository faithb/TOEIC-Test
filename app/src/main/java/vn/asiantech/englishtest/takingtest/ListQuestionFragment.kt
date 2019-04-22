package vn.asiantech.englishtest.takingtest

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_taking_test.*
import kotlinx.android.synthetic.main.fragment_list_question.*
import vn.asiantech.englishtest.R
import vn.asiantech.englishtest.listtest.TestListFragment

class ListQuestionFragment : Fragment(), ListQuestionAdapter.OnClickQuestionNumber {

    private var level: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        level = activity?.intent?.getIntExtra(TestListFragment.ARG_LEVEL, 0)
        return inflater.inflate(R.layout.fragment_list_question, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycleView()
        onClickSubmit()
        if ((activity as TakingTestActivity).review) {
            btnSubmit.visibility = View.GONE
        }
    }

    override fun onClickQuestionNumber(position: Int) {
        (activity as TakingTestActivity).apply {
            frListQuestions?.visibility = View.GONE
            questionDetailPager?.currentItem = position
        }
    }

    private fun initRecycleView() = recycleViewListQuestions.apply {
        setHasFixedSize(true)
        layoutManager = GridLayoutManager(activity, 5)
        adapter = ListQuestionAdapter((activity as TakingTestActivity).questionNumberList, this@ListQuestionFragment)
    }

    private fun onClickSubmit() = btnSubmit.setOnClickListener {
        fragmentManager?.beginTransaction()?.apply {
            setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
            replace(R.id.frListQuestions, TestResultFragment())
            commit()
        }
        (activity as TakingTestActivity).apply {
            chronometer?.stop()
            with(View.GONE) {
                chronometer?.visibility = this
                btnListQuestions?.visibility = this
            }
            questionDetailList.forEach { listQuestionDetailItem ->
                if (listQuestionDetailItem.correctAnswer == listQuestionDetailItem.myAnswer) {
                    score += 1
                }
            }
            mediaPlayer?.apply {
                stop()
                release()
            }
            mediaPlayer = null
        }
    }
}
