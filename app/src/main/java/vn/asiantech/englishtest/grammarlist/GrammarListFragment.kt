package vn.asiantech.englishtest.grammarlist

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_list_test.*
import vn.asiantech.englishtest.R
import vn.asiantech.englishtest.listtest.TestListActivity
import vn.asiantech.englishtest.listtest.TestListFragment
import vn.asiantech.englishtest.model.GrammarList
import vn.asiantech.englishtest.takingtest.TakingTestActivity

class GrammarListFragment : Fragment(), GrammarListAdapter.OnClickGrammarItem {

    private var grammarListAdapter: GrammarListAdapter? = null
    private var grammarList = arrayListOf<GrammarList>()
    private var databaseReference: DatabaseReference? = null

    companion object {
        const val ARG_GRAMMAR_LIST = "arg_grammar_list"

        fun getInstance(level: Int): GrammarListFragment =
            GrammarListFragment().apply {
                val bundle = Bundle().apply {
                    putInt(TestListFragment.ARG_LEVEL, level)
                }
                arguments = bundle
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_list_test, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initData()
    }

    override fun onClickGrammarItem(position: Int) = startActivity(
        Intent(activity, TakingTestActivity::class.java)
            .putExtra(TestListFragment.ARG_POSITION, position)
            .putExtra(TestListFragment.ARG_LEVEL, arguments?.getInt(TestListFragment.ARG_LEVEL))
            .putParcelableArrayListExtra(ARG_GRAMMAR_LIST, grammarList)
    )

    private fun initRecyclerView() = recycleViewListReadingTests.apply {
        layoutManager = LinearLayoutManager(activity)
        grammarListAdapter = GrammarListAdapter(grammarList, this@GrammarListFragment)
        adapter = grammarListAdapter
    }

    private fun initData() {
        (activity as TestListActivity).apply {
            initProgressDialog()
            notifyNetworkStatus()
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("grammar")
        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not implemented")
            }

            override fun onDataChange(grammarData: DataSnapshot) {
                (activity as TestListActivity).dismissProgressDialog()
                for (i in grammarData.children) {
                    val grammar = i.getValue(GrammarList::class.java)
                    grammar?.let {
                        grammarList.add(it)
                    }
                }
                grammarListAdapter?.notifyDataSetChanged()
            }
        })
    }
}
