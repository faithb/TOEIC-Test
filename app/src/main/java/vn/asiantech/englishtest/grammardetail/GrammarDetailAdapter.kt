package vn.asiantech.englishtest.grammardetail

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_grammar_detail.view.*
import vn.asiantech.englishtest.R
import vn.asiantech.englishtest.model.GrammarDetail

class GrammarDetailAdapter(private val grammarDetail: MutableList<GrammarDetail>) :
    RecyclerView.Adapter<GrammarDetailAdapter.GrammarDetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): GrammarDetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_grammar_detail, parent, false)
        return GrammarDetailViewHolder(view)
    }

    override fun getItemCount() = grammarDetail.size

    override fun onBindViewHolder(holder: GrammarDetailViewHolder, position: Int) =
        holder.bindView(grammarDetail[position])

    inner class GrammarDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(grammarDetail: GrammarDetail) = with(itemView) {
            with(grammarDetail) {
                tvGrammarDetailTitle.text = grammarDetailTitle
                tvGrammarDetailDescription.text = grammarDetailDescription
            }
        }
    }
}
