package vn.asiantech.englishtest.takingtest

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_taking_test.*
import kotlinx.android.synthetic.main.fragment_test_result.*
import vn.asiantech.englishtest.R
import vn.asiantech.englishtest.listtest.TestListFragment
import vn.asiantech.englishtest.model.TestList

class TestResultFragment : Fragment(), View.OnClickListener {

    private var position = -1
    private var level = 0

    companion object {
        const val KEY_TIME = "key_time"
        const val KEY_SCORE = "key_score"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        (activity as TakingTestActivity).apply {
            level = intent.getIntExtra(TestListFragment.ARG_LEVEL, 0)
        }
        return inflater.inflate(R.layout.fragment_test_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnReview.setOnClickListener(this)
        btnExit.setOnClickListener(this)
        (activity as TakingTestActivity).apply {
            tvDurationTime.text = chronometer.text.toString()
            tvCorrectAnswer.text = StringBuilder().append(score.toString())
                .append("/${questionDetailList.size}")
        }
        setTimeAndScore()
    }


    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnReview -> {
                (activity as TakingTestActivity).apply {
                    review = true
                    btnListQuestions.visibility = View.VISIBLE
                }
                activity?.apply {
                    frListQuestions?.visibility = View.GONE
                    questionDetailPager.apply {
                        adapter?.notifyDataSetChanged()
                        currentItem = 0
                    }
                }
            }
            R.id.btnExit -> {
                activity?.apply {
                    setResult(
                        Activity.RESULT_OK, Intent()
                            .putExtra(KEY_TIME, tvDurationTime.text.toString())
                            .putExtra(KEY_SCORE, (activity as TakingTestActivity).score.toString())
                            .putExtra(
                                TestListFragment.ARG_POSITION,
                                activity?.intent?.getIntExtra(TestListFragment.ARG_POSITION, -1)
                            )
                    )
                    finish()
                }
            }
        }
    }

    private fun setTimeAndScore() {
        val preferences = activity?.getSharedPreferences(getString(R.string.fileName), Context.MODE_PRIVATE)
        activity?.intent?.apply {
            position = getIntExtra(TestListFragment.ARG_POSITION, -1)
            level = getIntExtra(TestListFragment.ARG_LEVEL, -1)
        }
        val dataTimeAndScore = preferences?.getString("$level", "")
        val gson = GsonBuilder().setPrettyPrinting().create()
        val listTimeandScore =
            gson.fromJson(dataTimeAndScore, Array<TestList>::class.java)?.toList()?.toMutableList()
                ?: arrayListOf()
        listTimeandScore.add(
            TestList(
                "${getString(R.string.practice)} ${position + 1}",
                (activity as TakingTestActivity).chronometer.text.toString(),
                (activity as TakingTestActivity).score.toString()
            )
        )
        val json = Gson().toJson(listTimeandScore)
        preferences?.edit()?.apply {
            putString("$level", json)
            apply()
        }
    }
}
