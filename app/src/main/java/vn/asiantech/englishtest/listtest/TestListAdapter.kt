package vn.asiantech.englishtest.listtest

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_list_test.view.*
import vn.asiantech.englishtest.R
import vn.asiantech.englishtest.model.TestList

class TestListAdapter(
    private val listTests: MutableList<TestList>,
    private val listener: OnClickTestItem
) :
    RecyclerView.Adapter<TestListAdapter.ListReadingTestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ListReadingTestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_test, parent, false)
        return ListReadingTestViewHolder(view)
    }

    override fun getItemCount() = listTests.size

    override fun onBindViewHolder(holder: ListReadingTestViewHolder, position: Int) =
        holder.bindView(listTests[position])

    interface OnClickTestItem {
        fun onClickTestItem(position: Int)
    }

    inner class ListReadingTestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        override fun onClick(v: View?) = listener.onClickTestItem(layoutPosition)

        fun bindView(listItems: TestList) {
            with(itemView) {
                with(listItems) {
                    tvTestName.text = testNumber
                    tvTimeDisplay.text = timeDisplay
                    tvScoreDisplay.text = scoreDisplay
                }
                clPractice.setOnClickListener(this@ListReadingTestViewHolder)
            }
        }
    }
}
