package vn.asiantech.englishtest.wordlist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_test_title.view.*
import vn.asiantech.englishtest.R
import vn.asiantech.englishtest.model.WordList

class WordListAdapter(
    private val wordList: MutableList<WordList>,
    private val wordListListener: OnWordListClickListener
) :
    RecyclerView.Adapter<WordListAdapter.WordListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): WordListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_test_title, parent, false)
        return WordListViewHolder(view)
    }

    override fun getItemCount() = wordList.size

    override fun onBindViewHolder(holder: WordListViewHolder, position: Int) =
        holder.bindView(wordList[position])

    interface OnWordListClickListener {
        fun onClickTestTitle(position: Int)
    }

    inner class WordListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        override fun onClick(view: View?) = wordListListener.onClickTestTitle(layoutPosition)

        fun bindView(wordList: WordList) = with(itemView.tvTestTitle) {
            text = wordList.testTitle
            setOnClickListener(this@WordListViewHolder)
        }
    }
}
