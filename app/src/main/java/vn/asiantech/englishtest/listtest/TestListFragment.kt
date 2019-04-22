package vn.asiantech.englishtest.listtest

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_list_test.*
import vn.asiantech.englishtest.R
import vn.asiantech.englishtest.model.TestList
import vn.asiantech.englishtest.takingtest.TakingTestActivity
import vn.asiantech.englishtest.takingtest.TestResultFragment

class TestListFragment : Fragment(), TestListAdapter.OnClickTestItem {

    private var testList = mutableListOf<TestList>()
    private var testAdapter: TestListAdapter? = null
    private var level: Int? = null

    companion object {
        const val ARG_LEVEL = "arg_level"
        const val ARG_POSITION = "position"
        const val REQUEST_CODE_TIME_AND_SCORE = 1001

        fun getInstance(level: Int): TestListFragment =
            TestListFragment().apply {
                val bundle = Bundle().apply {
                    putInt(ARG_LEVEL, level)
                }
                arguments = bundle
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        arguments?.let {
            level = it.getInt(ARG_LEVEL)
        }
        return inflater.inflate(R.layout.fragment_list_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycleView()
        setData()
    }

    override fun onClickTestItem(position: Int) = startActivityForResult(
        Intent(activity, TakingTestActivity::class.java)
            .putExtra(ARG_POSITION, position)
            .putExtra(ARG_LEVEL, arguments?.getInt(ARG_LEVEL)), REQUEST_CODE_TIME_AND_SCORE
    )

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_TIME_AND_SCORE) {
            if (resultCode == Activity.RESULT_OK) {
                data?.apply {
                    testList[getIntExtra(TestListFragment.ARG_POSITION, -1)].apply {
                        timeDisplay = getStringExtra(TestResultFragment.KEY_TIME)
                        scoreDisplay = getStringExtra(TestResultFragment.KEY_SCORE)
                    }
                    testAdapter?.notifyDataSetChanged()
                }
            }
        }
    }

    private fun initRecycleView() = recycleViewListReadingTests.apply {
        layoutManager = LinearLayoutManager(activity)
        testAdapter = TestListAdapter(testList, this@TestListFragment)
        adapter = testAdapter
    }

    private fun setData() {
        val maxTestNumber = 10
        for (i in 0 until maxTestNumber) {
            testList.add(
                TestList(
                    "${getString(R.string.practice)} ${i + 1}",
                    getString(R.string.timeDefault), getString(R.string.scoreDefault)
                )
            )
        }
        handleDataSharedPreferences()
    }

    private fun handleDataSharedPreferences() {
        val preferences = activity?.getSharedPreferences(getString(R.string.fileName), Context.MODE_PRIVATE)
        val json = preferences?.getString("$level", null)
        if (json != null) {
            val gson = GsonBuilder().setPrettyPrinting().create()
            val listTimeandScore = gson.fromJson(json, Array<TestList>::class.java).toList()
            for (testPosition in testList) {
                for (timeAndScorePosition in listTimeandScore) {
                    if (testPosition.testNumber == timeAndScorePosition.testNumber) {
                        testPosition.scoreDisplay = timeAndScorePosition.scoreDisplay
                        testPosition.timeDisplay = timeAndScorePosition.timeDisplay
                    }
                }
            }
        }
    }
}
