package vn.asiantech.englishtest.grammarlist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_list_grammar.view.*
import vn.asiantech.englishtest.R
import vn.asiantech.englishtest.model.GrammarList

class GrammarListAdapter(
    private val grammar: MutableList<GrammarList>,
    private val grammarListener: OnClickGrammarItem
) : RecyclerView.Adapter<GrammarListAdapter.GrammarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): GrammarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_grammar, parent, false)
        return GrammarViewHolder(view)
    }

    override fun getItemCount() = grammar.size

    override fun onBindViewHolder(holder: GrammarViewHolder, position: Int) =
        holder.bindView(grammar[position])

    interface OnClickGrammarItem {
        fun onClickGrammarItem(position: Int)
    }

    inner class GrammarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        override fun onClick(view: View?) = grammarListener.onClickGrammarItem(layoutPosition)

        fun bindView(listGrammar: GrammarList) {
            with(itemView) {
                with(listGrammar) {
                    tvGrammarTitle.text = grammarTitle
                    tvGrammarExample.text = grammarExample
                }
                llGrammar.setOnClickListener(this@GrammarViewHolder)
            }
        }
    }
}
