package vn.asiantech.englishtest.questiondetailviewpager

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_taking_test.*
import kotlinx.android.synthetic.main.fragment_question_detail.*
import vn.asiantech.englishtest.R
import vn.asiantech.englishtest.listtest.TestListFragment
import vn.asiantech.englishtest.model.QuestionDetail
import vn.asiantech.englishtest.takingtest.TakingTestActivity
import java.text.SimpleDateFormat

@Suppress("DEPRECATION")
class QuestionDetailFragment : Fragment(), View.OnClickListener {

    private var data: QuestionDetail? = null
    private var position = -1
    private var level: Int? = null
    private var isDestroy = false

    companion object {
        const val ARG_POSITION = "arg_position"
        const val ARG_DATA = "arg_data"

        fun getInstance(position: Int, question: QuestionDetail): QuestionDetailFragment =
            QuestionDetailFragment().apply {
                val bundle = Bundle().apply {
                    putInt(ARG_POSITION, position)
                    putParcelable(ARG_DATA, question)
                }
                arguments = bundle
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        arguments?.let {
            position = it.getInt(ARG_POSITION)
            data = it.getParcelable(ARG_DATA) as QuestionDetail
        }
        (activity as TakingTestActivity).apply {
            progressDialog?.dismiss()
        }
        level = activity?.intent?.getIntExtra(TestListFragment.ARG_LEVEL, 0)
        return inflater.inflate(R.layout.fragment_question_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as TakingTestActivity).apply {
            mediaPlayer = MediaPlayer()
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        }
        showView()
        val switchState = activity?.getSharedPreferences("switchcase", Context.MODE_PRIVATE)
        val isSwitchAnswer = switchState?.getBoolean("switchState", false)
        if (isSwitchAnswer == true) {
            onClickOptions()
        } else
            setValueForMyAnswer()
        onClickPlayAudio()
        setDataFirebase()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rbAnswerA -> {
                data?.myAnswer =
                    (activity as TakingTestActivity).questionDetailList[(activity as TakingTestActivity).questionDetailPager.currentItem].answerA
                showAnswerAfterOnClick()
            }
            R.id.rbAnswerB -> {
                data?.myAnswer =
                    (activity as TakingTestActivity).questionDetailList[(activity as TakingTestActivity).questionDetailPager.currentItem].answerB
                showAnswerAfterOnClick()
            }
            R.id.rbAnswerC -> {
                data?.myAnswer =
                    (activity as TakingTestActivity).questionDetailList[(activity as TakingTestActivity).questionDetailPager.currentItem].answerC
                showAnswerAfterOnClick()
            }
            R.id.rbAnswerD -> {
                data?.myAnswer =
                    (activity as TakingTestActivity).questionDetailList[(activity as TakingTestActivity).questionDetailPager.currentItem].answerD
                showAnswerAfterOnClick()
            }
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (!isVisibleToUser && isResumed) {
            (activity as TakingTestActivity).mediaPlayer?.pause()
            imgState.setImageResource(R.drawable.ic_play_arrow_black_24dp)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as TakingTestActivity).mediaPlayer?.stop()
        isDestroy = true
    }

    private fun showView() {
        when (level) {
            R.id.itemPart1 -> {
                with(View.VISIBLE) {
                    tvQuestionContent.visibility = this
                    imgQuestionTitle.visibility = this
                    cardViewAudio.visibility = this
                }
                tvQuestionTitle.visibility = View.GONE
                setLayoutHeight()
            }
            R.id.itemPart2 -> {
                with(View.VISIBLE) {
                    tvQuestionContent.visibility = this
                    cardViewAudio.visibility = this
                }
                with(View.GONE) {
                    tvQuestionTitle.visibility = this
                    rbAnswerD.visibility = this
                    divider4.visibility = this
                }
                setLayoutHeight()
            }
            R.id.itemPart3, R.id.itemPart4 -> {
                with(View.VISIBLE) {
                    tvQuestionContent.visibility = this
                    cardViewAudio.visibility = this
                }
                with(View.GONE) {
                    tvQuestionTitle.visibility = this
                }
                setLayoutHeight()
            }
            R.id.itemPart6, R.id.itemPart7 -> {
                tvQuestionContent.visibility = View.VISIBLE
                tvQuestionTitle.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                if (level == R.id.itemPart7) {
                    setLayoutHeight()
                }
            }
        }
    }

    private fun setDataFirebase() = data?.let {
        with(it) {
            when (level) {
                R.id.itemPart1 -> Glide.with(activity as TakingTestActivity).load(questionTitle).into(
                    imgQuestionTitle
                )
            }
            tvQuestionContent.text = questionContent
            if (level != R.id.itemPart1 && level != R.id.itemPart2) {
                tvQuestionTitle.text = questionTitle
                tvQuestionContent.text = questionContent
                rbAnswerA.text = answerA
                rbAnswerB.text = answerB
                rbAnswerC.text = answerC
                rbAnswerD.text = answerD
                tvExplanation.text = explanation
                tvTranslation.text = translation
            }
        }
        if ((activity as TakingTestActivity).review) {
            showAnswerAfterOnClick()
        }
    }

    private fun onClickPlayAudio() {
        @SuppressLint("SimpleDateFormat")
        val timeFormat = SimpleDateFormat("mm:ss")
        imgState.setOnClickListener {
            (activity as TakingTestActivity).mediaPlayer?.apply {
                try {
                    setDataSource(data?.audio)
                    setOnPreparedListener { mp -> mp.start() }
                    prepare()
                } catch (e: Exception) {
                }
                seekBarPlay.max = duration
                tvTotalTime.text = timeFormat.format(duration)
                (activity as TakingTestActivity).runOnUiThread(object : Runnable {
                    override fun run() {
                        if (isDestroy) {
                            return
                        }
                        try {
                            (activity as TakingTestActivity).mediaPlayer?.currentPosition.apply {
                                seekBarPlay.progress = this ?: 0
                                tvCurrentTime.text = timeFormat.format(this)
                            }
                            Handler().postDelayed(this, 1000)
                        } catch (e: Exception) {
                        }
                    }
                })
                seekBarChangeListener()
                if (isPlaying) {
                    pause()
                    imgState.setImageResource(R.drawable.ic_play_arrow_black_24dp)
                } else {
                    start()
                    imgState.setImageResource(R.drawable.ic_pause_black_24dp)
                }
            }
        }
    }

    private fun setValueForMyAnswer() = rgAnswer.setOnCheckedChangeListener { _, _ ->
        (activity as TakingTestActivity).questionNumberList[(activity as TakingTestActivity).questionDetailPager.currentItem].apply {
            data?.apply {
                when {
                    rbAnswerA.isChecked -> {
                        myAnswer = answerA
                        isQuestionChecked = true
                    }
                    rbAnswerB.isChecked -> {
                        myAnswer = answerB
                        isQuestionChecked = true
                    }
                    rbAnswerC.isChecked -> {
                        myAnswer = answerC
                        isQuestionChecked = true
                    }
                    rbAnswerD.isChecked -> {
                        myAnswer = answerD
                        isQuestionChecked = true
                    }
                }
            }
        }
    }

    private fun onClickOptions() {
        rbAnswerA.setOnClickListener(this)
        rbAnswerB.setOnClickListener(this)
        rbAnswerC.setOnClickListener(this)
        rbAnswerD.setOnClickListener(this)
    }

    private fun showAnswerAfterOnClick() {
        (activity as TakingTestActivity).questionNumberList[(activity as TakingTestActivity).questionDetailPager.currentItem].apply {
            data?.apply {
                isQuestionChecked = true
                if (level == R.id.itemPart1 || level == R.id.itemPart2) {
                    rbAnswerA.text = answerA
                    rbAnswerB.text = answerB
                    rbAnswerC.text = answerC
                    rbAnswerD.text = answerD
                    tvQuestionContent.text =
                        if (level == R.id.itemPart2) questionDetail else questionContent
                } else {
                    cardViewExplanation.visibility = View.VISIBLE
                }
                with(this) {
                    if (myAnswer != correctAnswer) {
                        when (correctAnswer) {
                            answerA -> rbAnswerA.setBackgroundColor(
                                if (myAnswer.isBlank()) resources.getColor(R.color.colorOrange) else resources.getColor(
                                    R.color.colorBlueAnswer
                                )
                            )
                            answerB -> rbAnswerB.setBackgroundColor(
                                if (myAnswer.isBlank()) resources.getColor(R.color.colorOrange) else resources.getColor(
                                    R.color.colorBlueAnswer
                                )
                            )
                            answerC -> rbAnswerC.setBackgroundColor(
                                if (myAnswer.isBlank()) resources.getColor(R.color.colorOrange) else resources.getColor(
                                    R.color.colorBlueAnswer
                                )
                            )
                            answerD -> rbAnswerD.setBackgroundColor(
                                if (myAnswer.isBlank()) resources.getColor(R.color.colorOrange) else resources.getColor(
                                    R.color.colorBlueAnswer
                                )
                            )
                        }
                        when (myAnswer) {
                            answerA -> rbAnswerA.setBackgroundColor(resources.getColor(R.color.colorPink))
                            answerB -> rbAnswerB.setBackgroundColor(resources.getColor(R.color.colorPink))
                            answerC -> rbAnswerC.setBackgroundColor(resources.getColor(R.color.colorPink))
                            answerD -> rbAnswerD.setBackgroundColor(resources.getColor(R.color.colorPink))
                        }
                    }
                }
                with(false) {
                    rbAnswerA.isClickable = this
                    rbAnswerB.isClickable = this
                    rbAnswerC.isClickable = this
                    rbAnswerD.isClickable = this
                }
            }
        }
    }

    private fun seekBarChangeListener() =
        seekBarPlay.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    (activity as TakingTestActivity).mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

    private fun setLayoutHeight() = with(ViewGroup.LayoutParams.WRAP_CONTENT) {
        rbAnswerA.layoutParams.height = this
        rbAnswerB.layoutParams.height = this
        rbAnswerC.layoutParams.height = this
        rbAnswerD.layoutParams.height = this
    }
}
