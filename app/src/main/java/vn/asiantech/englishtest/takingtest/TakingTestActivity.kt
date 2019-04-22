@file:Suppress("DEPRECATION")

package vn.asiantech.englishtest.takingtest

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_taking_test.*
import kotlinx.android.synthetic.main.fragment_notify_dialog.view.*
import kotlinx.android.synthetic.main.fragment_test_result.*
import vn.asiantech.englishtest.R
import vn.asiantech.englishtest.grammardetail.GrammarDetailFragment
import vn.asiantech.englishtest.grammarlist.GrammarListFragment
import vn.asiantech.englishtest.listtest.TestListFragment
import vn.asiantech.englishtest.model.GrammarList
import vn.asiantech.englishtest.model.QuestionDetail
import vn.asiantech.englishtest.model.QuestionNumber
import vn.asiantech.englishtest.model.WordList
import vn.asiantech.englishtest.questiondetailviewpager.QuestionDetailAdapter
import vn.asiantech.englishtest.settings.SettingFragment
import vn.asiantech.englishtest.wordlist.WordListFragment
import vn.asiantech.englishtest.wordstudy.WordStudyFragment

@Suppress("DEPRECATION")
class TakingTestActivity : AppCompatActivity(), View.OnClickListener {

    private var dataReference: DatabaseReference? = null
    private var grammarList = mutableListOf<GrammarList>()
    private var wordList = mutableListOf<WordList>()
    var questionNumberList = mutableListOf<QuestionNumber>()
    var questionDetailList = arrayListOf<QuestionDetail>()
    var progressDialog: ProgressDialog? = null
    var mediaPlayer: MediaPlayer? = null
    var score = 0
    var review = false
    var level: Int = 0
    var position: Int = -1
    var isSwitchAnswerOn = false

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        level = intent.getIntExtra(TestListFragment.ARG_LEVEL, 0)
        position = intent.getIntExtra(TestListFragment.ARG_POSITION, -1)

        setContentView(R.layout.activity_taking_test)
        window.statusBarColor = resources.getColor(R.color.colorBlue)
        progressDialog = ProgressDialog(this)
        initData()
        btnBackToListTest.setOnClickListener(this)
        btnListQuestions.setOnClickListener(this)
    }

    override fun onBackPressed() = when {
        supportFragmentManager.findFragmentById(R.id.frListQuestions) is TestResultFragment -> setResult()
        supportFragmentManager.findFragmentById(R.id.frListQuestions) is GrammarDetailFragment -> finish()
        supportFragmentManager.findFragmentById(R.id.frListQuestions) is WordStudyFragment -> finish()
        supportFragmentManager.findFragmentById(R.id.frListQuestions) is SettingFragment -> finish()
        frListQuestions.visibility == View.VISIBLE -> with(frListQuestions) {
            animation = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_out_bottom)
            visibility = View.GONE
        }
        else -> initAlertDialog()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnListQuestions -> {
                if (frListQuestions.visibility == View.GONE) {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.frListQuestions, ListQuestionFragment())
                        addToBackStack(null)
                        commit()
                    }
                    with(frListQuestions) {
                        animation = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_in_bottom)
                        visibility = View.VISIBLE
                    }
                } else {
                    with(frListQuestions) {
                        animation = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_out_bottom)
                        visibility = View.GONE
                    }
                }
            }
            R.id.btnBackToListTest -> {
                onBackPressed()
            }
        }
    }

    private fun setResult() {
        setResult(
            Activity.RESULT_OK, Intent()
                .putExtra(TestResultFragment.KEY_TIME, tvDurationTime.text.toString())
                .putExtra(TestResultFragment.KEY_SCORE, score.toString())
                .putExtra(
                    TestListFragment.ARG_POSITION,
                    intent.getIntExtra(TestListFragment.ARG_POSITION, 0)
                )
        )
        finish()
    }

    private fun initData() {
        initProgressDialog()
        notifyNetworkStatus()
        FirebaseDatabase.getInstance().apply {
            when (level) {
                R.id.itemPart1 -> {
                    tvLevel.text = getString(R.string.part1)
                    dataReference = getReference("part1-0${position + 1}")
                }
                R.id.itemPart2 -> {
                    tvLevel.text = getString(R.string.part2)
                    dataReference = getReference("part2-0${position + 1}")
                }
                R.id.itemPart3 -> {
                    tvLevel.text = getString(R.string.part3)
                    dataReference = getReference("part3-0${position + 1}")
                }
                R.id.itemPart4 -> {
                    tvLevel.text = getString(R.string.part4)
                    dataReference = getReference("part4-0${position + 1}")
                }
                R.id.itemPart5Basic -> {
                    tvLevel.text = getString(R.string.part5Basic)
                    dataReference = getReference("part5Basic0${position + 1}")
                }
                R.id.itemPart5Intermediate -> {
                    tvLevel.text = getString(R.string.part5Intermediate)
                    dataReference = getReference("part5Intermediate0${position + 1}")
                }
                R.id.itemPart5Advanced -> {
                    tvLevel.text = getString(R.string.part5Advanced)
                    dataReference = getReference("part5Advanced0${position + 1}")
                }
                R.id.itemPart6 -> {
                    tvLevel.text = getString(R.string.part6)
                    dataReference = getReference("part6-0${position + 1}")
                }
                R.id.itemPart7 -> {
                    tvLevel.text = getString(R.string.part7)
                    dataReference = getReference("part7-0${position + 1}")
                }
                R.id.itemGrammar -> {
                    grammarList = intent.getParcelableArrayListExtra(GrammarListFragment.ARG_GRAMMAR_LIST)
                    tvLevel.text = grammarList[position].grammarTitle
                    initGrammarDetailFragment()
                }
                R.id.itemWordStudy -> {
                    wordList = intent.getParcelableArrayListExtra(WordListFragment.ARG_LIST_TEST_TITLE)
                    tvLevel.text = wordList[position].testTitle
                    initGrammarDetailFragment()
                }
                else -> {
                    dismissProgressDialog()
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.frListQuestions, SettingFragment())
                        commit()
                    }
                    with(View.GONE) {
                        chronometer.visibility = this
                        btnListQuestions.visibility = this
                    }
                    frListQuestions.visibility = View.VISIBLE
                    tvLevel.text = getString(R.string.settings)
                }
            }
        }

        if (level != R.id.itemGrammar && level != R.id.itemWordStudy) {
            dataReference?.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(dataPractice: DatabaseError) {
                    TODO("Not implemented")
                }

                override fun onDataChange(dataPractice: DataSnapshot) {
                    dismissProgressDialog()
                    notifyNetworkStatus()
                    for (i in dataPractice.children) {
                        val question = i.getValue(QuestionDetail::class.java)
                        question?.let {
                            questionDetailList.add(it)
                        }
                    }
                    setListQuestionNumber()
                    questionDetailPager?.adapter = QuestionDetailAdapter(supportFragmentManager, questionDetailList)
                    chronometer.start()
                }
            })
        }
    }

    private fun setListQuestionNumber() {
        for (i in 0 until questionDetailList.size) {
            (questionNumberList as ArrayList<QuestionNumber>).add(
                QuestionNumber(
                    when (level) {
                        R.id.itemPart1 -> 1 + i
                        R.id.itemPart2 -> 11 + i
                        R.id.itemPart3 -> 41 + i
                        R.id.itemPart4 -> 71 + i
                        R.id.itemPart6 -> 141 + i
                        R.id.itemPart7 -> 147 + i
                        else -> 101 + i
                    }
                )
            )
        }
    }

    private fun initGrammarDetailFragment() {
        supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
            replace(
                R.id.frListQuestions,
                if (level == R.id.itemGrammar) GrammarDetailFragment() else WordStudyFragment()
            )
            commit()
        }
        frListQuestions.visibility = View.VISIBLE
        with(View.GONE) {
            chronometer.visibility = this
            btnListQuestions.visibility = this
        }
    }

    @SuppressLint("InflateParams")
    private fun initAlertDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.fragment_notify_dialog, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView)
        val alertDialog = dialog.show()
        dialogView.btnStay.setOnClickListener { alertDialog.dismiss() }
        dialogView.btnLeave.setOnClickListener {
            alertDialog.dismiss()
            finish()
        }
    }

    private fun initProgressDialog() = progressDialog?.apply {
        setProgressStyle(ProgressDialog.STYLE_SPINNER)
        setMessage(getString(R.string.loadingData))
        show()
        setCancelable(false)
    }

    fun dismissProgressDialog() = progressDialog?.dismiss()

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }

    fun notifyNetworkStatus() {
        if (!isNetworkAvailable()) {
            Handler().postDelayed({
                dismissProgressDialog()
                Toast.makeText(this, getString(R.string.networkNotification), Toast.LENGTH_LONG).show()
            }, 5000)
        }
    }
}
