package vn.asiantech.englishtest.grammardetail

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_grammar_detail.view.*
import vn.asiantech.englishtest.R
import vn.asiantech.englishtest.model.ToeicIntro

class ToeicIntroAdapter(private val toeicIntro: MutableList<ToeicIntro>) :
    RecyclerView.Adapter<ToeicIntroAdapter.ToeicIntroViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ToeicIntroViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_grammar_detail, parent, false)
        return ToeicIntroViewHolder(view)
    }

    override fun getItemCount() = toeicIntro.size

    override fun onBindViewHolder(holder: ToeicIntroViewHolder, position: Int) =
        holder.bindView(toeicIntro[position])

    inner class ToeicIntroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(toeicIntro: ToeicIntro) = with(itemView) {
            with(toeicIntro) {
                tvGrammarDetailTitle.text = introTitle
                tvGrammarDetailDescription.text = introContent
            }
        }
    }
}
