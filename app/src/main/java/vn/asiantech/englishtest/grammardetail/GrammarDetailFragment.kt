package vn.asiantech.englishtest.grammardetail

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_grammar_detail.*
import vn.asiantech.englishtest.R
import vn.asiantech.englishtest.listtest.TestListActivity
import vn.asiantech.englishtest.listtest.TestListFragment
import vn.asiantech.englishtest.model.GrammarDetail
import vn.asiantech.englishtest.model.ToeicIntro
import vn.asiantech.englishtest.takingtest.TakingTestActivity

class GrammarDetailFragment : Fragment() {

    private var grammarDetailAdapter: GrammarDetailAdapter? = null
    private var toeicIntroAdapter: ToeicIntroAdapter? = null
    private var grammarDetailList = mutableListOf<GrammarDetail>()
    private var toeicIntroList = mutableListOf<ToeicIntro>()
    private var databaseReference: DatabaseReference? = null
    private var level: Int? = null

    companion object {

        fun getInstance(level: Int): GrammarDetailFragment =
            GrammarDetailFragment().apply {
                val bundle = Bundle().apply {
                    putInt(TestListFragment.ARG_LEVEL, level)
                }
                arguments = bundle
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        level = arguments?.getInt(TestListFragment.ARG_LEVEL)
        return inflater.inflate(R.layout.fragment_grammar_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initData()
    }

    private fun initRecyclerView() = recycleViewGrammarDetail.apply {
        layoutManager = LinearLayoutManager(activity)
        grammarDetailAdapter = GrammarDetailAdapter(grammarDetailList)
        toeicIntroAdapter = ToeicIntroAdapter(toeicIntroList)
        adapter = if (level == R.id.itemToeicIntroduction) toeicIntroAdapter else grammarDetailAdapter
    }

    private fun initData() {
        val position = activity?.intent?.getIntExtra(TestListFragment.ARG_POSITION, 0)
        databaseReference = when (level) {
            R.id.itemToeicIntroduction -> {
                (activity as TestListActivity).apply {
                    initProgressDialog()
                    notifyNetworkStatus()
                }
                FirebaseDatabase.getInstance().getReference("toeicIntroduction")
            }
            else -> {
                (activity as TakingTestActivity).notifyNetworkStatus()
                FirebaseDatabase.getInstance().getReference("grammarDetail0${position?.plus(1)}")
            }
        }
        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not implemented")
            }

            override fun onDataChange(grammarDetailData: DataSnapshot) {
                when (level) {
                    R.id.itemToeicIntroduction -> {
                        (activity as TestListActivity).dismissProgressDialog()
                        for (i in grammarDetailData.children) {
                            val introDetail = i.getValue(ToeicIntro::class.java)
                            introDetail?.let {
                                toeicIntroList.add(it)
                            }
                        }
                        toeicIntroAdapter?.notifyDataSetChanged()
                    }
                    else -> {
                        (activity as TakingTestActivity).dismissProgressDialog()
                        for (i in grammarDetailData.children) {
                            val introDetail = i.getValue(GrammarDetail::class.java)
                            introDetail?.let {
                                grammarDetailList.add(it)
                            }
                        }
                        grammarDetailAdapter?.notifyDataSetChanged()
                    }
                }
            }
        })
    }
}
