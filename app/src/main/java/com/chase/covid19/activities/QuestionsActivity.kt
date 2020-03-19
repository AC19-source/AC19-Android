package com.chase.covid19.activities

import android.content.Intent
import android.os.Bundle
import android.text.util.Linkify
import android.util.Log
import android.view.View
import com.chase.covid19.R
import com.chase.covid19.ext.getParcelableExtra
import com.chase.covid19.model.Question
import com.chase.covid19.model.ResultData
import com.chase.covid19.network.apiCall
import com.chase.covid19.utils.Extra
import com.chase.covid19.utils.Prefs
import com.chase.covid19.views.ProgressBarResultDialog
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import kotlinx.android.synthetic.main.activity_questions.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast

class QuestionsActivity : BaseActivity() {
    var resultData: ResultData? = null
    var mStep: String = ""
    var questions = ArrayList<Question>()
    var quesKey: List<String?>? = listOf()
    val imageLoader: ImageLoader = ImageLoader.getInstance() // Get singleton instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        questions = arrayListOf()
        resultData = getParcelableExtra(Extra.ResultData)
        imageLoader.init(ImageLoaderConfiguration.createDefault(applicationContext))

        setContentView(R.layout.activity_questions)

        // I think using Dispatchers.IO is better for REST calls.
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = apiCall {
                    getQuestions1("+98" + Prefs.mobile, Prefs.verificataionCode, resultData?.name!!)
                }

                if (response.isSuccessful) {
                    if (response.value != null) {
                        val res = response.value
                        if (res != null) {
                            questions.addAll(res.questionsList)
                            quesKey = res.questions?.keys?.toList()

                            if (questions.size > 0) {
                                txt_question.text = questions[0].txt
                                Linkify.addLinks(txt_question, Linkify.WEB_URLS)

                                if (questions[0].img.isNotEmpty())
                                    imageLoader.displayImage(questions[0].img, image_view)
                            }

                            step_view.statusView.run {
                                stepCount = questions.size
                            }
                        }
                    }
                } else {
                    toast("error in get data" + response.code)
                    finish()
                }
            } catch (e: Exception) {
                toast(R.string.general_network_error)
                finish()
            }
        }

        back_button.setOnClickListener {
            onBackPressed()
        }

        btn_no.setOnClickListener {
            setCurrent("0")
        }

        btn_yes.setOnClickListener {
            setCurrent("1")
        }
    }

    private fun setCurrent(answer: String) {
        try {
            if (resultData?.questions == null) {
                resultData?.questions = hashMapOf()
            }
            resultData?.questions?.put(quesKey?.get(step_view.statusView.currentCount - 1).toString(),
                                       answer)
            gotoNextStep()
        } catch (e: Exception) {
            // Please handle exception or remove this catch block.
        }
    }

    private fun gotoNextStep() {
        try {
            if (step_view.statusView.currentCount < step_view.statusView.stepCount) {
                step_view.scrollToStep(step_view.statusView.currentCount - 1)
                step_view.statusView.currentCount += 1
                txt_question.text = questions[step_view.statusView.currentCount - 1].txt
                Linkify.addLinks(txt_question, Linkify.WEB_URLS)

                if (questions[step_view.statusView.currentCount - 1].img.isNotEmpty())
                    imageLoader.displayImage(questions[step_view.statusView.currentCount - 1].img,
                                             image_view)
                else
                    image_view.visibility = View.INVISIBLE

            } else {
                submitRequest()
            }
        } catch (e: Exception) {
            // Please handle exception or remove this catch block.
        }
    }

    private fun submitRequest() {
        val resultData = this.resultData

        if (pbloading.visibility == View.VISIBLE || resultData == null)
            return

        pbloading.visibility = View.VISIBLE
        // I think using Dispatchers.IO is better for REST calls.
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response =
                    apiCall {
                        submit("+98" + Prefs.mobile,
                               Prefs.verificataionCode,
                               resultData.name ?: "",
                               mStep,
                               resultData)
                    }

                if (response.isSuccessful) {
                    val step = response.value?.step
                    if (!step.isNullOrBlank()) {
                        mStep = step
                        val nextResponse = apiCall {
                            getQuestions("+98" + Prefs.mobile, Prefs.verificataionCode,
                                         resultData.name!!, step)
                        }

                        val config = nextResponse.value
                        if (nextResponse.isSuccessful) {
                            if (config != null) {
                                resultData.questions?.clear()
                                questions = arrayListOf()
                                questions.addAll(config.questionsList)
                                quesKey = config.questions?.keys?.toList()
                                step_view.statusView.currentCount = 0

                                step_view.statusView.run {
                                    stepCount = questions.size
                                }
                                gotoNextStep()
                                step_view.run {
                                    postDelayed({
                                                    scrollToStep(statusView.currentCount - 1)
                                                }, 200)
                                }
                            } else {
                                toast(R.string.general_network_error)
                            }
                        } else {
                            toast(R.string.general_network_error)
                        }
                    } else if (!response.value?.view.isNullOrEmpty()) {
                        finish()
                        startActivity(
                            Intent(applicationContext,
                                   WebViewActivity::class.java).putExtra(Extra.url,
                                                                         "https://covid19.tfone.ir/" + response.value?.view
                            ))
                    } else {
                        response.value?.message?.let {
                            ProgressBarResultDialog(
                                this@QuestionsActivity,
                                this@QuestionsActivity,
                                it,
                                response.value?.color
                            ).show()
                        }
                    }
                } else {
                    toast("Error network operation failed with ${response.code()}")
                    pbloading.visibility = View.GONE
                }
            } catch (e: Throwable) {
                Log.e("REQUEST", "Ooops: Something else went wrong")
            }
            pbloading.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}