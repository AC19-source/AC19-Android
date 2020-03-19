package com.chase.covid19.activities

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import com.chase.covid19.R
import com.chase.covid19.adapters.CoronaTestAdapter
import com.chase.covid19.adapters.TestClickCallback
import com.chase.covid19.model.Post
import com.chase.covid19.network.apiCall
import com.chase.covid19.utils.Extra
import com.chase.covid19.utils.Prefs
import ir.hamsaa.persiandatepicker.util.PersianCalendar
import kotlinx.android.synthetic.main.activity_main.recyclerview
import kotlinx.android.synthetic.main.activity_test_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.toptas.fancyshowcase.FancyShowCaseView
import org.jetbrains.anko.toast


class TestListActivity : BaseActivity(), CoroutineScope by CoroutineScope(Dispatchers.Main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_list)
        getData()

        rl_add_test.setOnClickListener {
            startActivity(Intent(applicationContext, SendResultActivity::class.java))
        }
        floating_add.setOnClickListener {
            startActivity(Intent(applicationContext, SendResultActivity::class.java))
        }

        back_button.setOnClickListener {
            onBackPressed()
        }

        recyclerview.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(this)
        recyclerview.adapter = testAdapter

        if (!Prefs.isTutorialForAddButtonShown) {
            val spanned: Spanned =
                Html.fromHtml("<font color='#2D8EFF' fontFamily>برای ورود اطلاعات برای تست جدید از این گزینه استفاده کنید</font>")
            FancyShowCaseView.Builder(this)
                .focusOn(floating_add)
                .title(spanned)
                .enterAnimation(null)
                .exitAnimation(null)
                .enableAutoTextPosition()
                .build()
                .show()
            if (Prefs.isTutorialForAddButtonShownSecondTime){
                Prefs.isTutorialForAddButtonShown = true
            }
            Prefs.isTutorialForAddButtonShownSecondTime = true
        }
    }

    private fun initRecyclerView(list: MutableList<Post>) {
        testAdapter.updateList(list)
    }

    private val testAdapterCallback: TestClickCallback = object : TestClickCallback {
        override fun openTestDetail(item: Post, position: Int) {
        }

        override fun updateTest(item: Post, position: Int) {
            launch {
                testAdapter.setLoading(position)

                val response = apiCall {
                    getTestDetail("+98" + Prefs.mobile,
                                  Prefs.verificataionCode,
                                  item.title)
                }

                val detail = response.value
                if (response.isSuccessful && detail != null) {
                    startActivity(
                        Intent(this@TestListActivity, QuestionsActivity::class.java)
                            .putExtra(Extra.ResultData, detail)
                    )
                } else {
                    toast(R.string.general_network_error)
                }

                testAdapter.setLoading(-1)
            }
        }

        override fun removeTest(item: Post, position: Int) {
            launch {
                testAdapter.setLoading(position)

                val response = apiCall {
                    delete("+98" + Prefs.mobile,
                           Prefs.verificataionCode,
                           item.title)
                }

                val detail = response.value
                if (response.isSuccessful && detail != null) {
                    testAdapter.removeItem(position)
                } else {
                    toast(R.string.general_network_error)
                }

                testAdapter.setLoading(-1)
            }
        }
    }

    private val testAdapter = CoronaTestAdapter(arrayListOf(), this, testAdapterCallback)

    override fun onResume() {
        super.onResume()

        getData()
    }

    private fun getData() {
        launch {
            val response = apiCall { getNames("+98" + Prefs.mobile, Prefs.verificataionCode) }
            val data = response.value
            if (response.isSuccessful && data != null) {
                initRecyclerView(data.mapNotNull {
                    val name = it.key
                    val date = it.value
                    if (name == null || date == null) {
                        null
                    } else {
                        Post(0, 0, name, PersianCalendar(date*1000).relativeFormattedPersianDate())
                    }
                }.reversed().toMutableList())
            } else {
                toast(R.string.general_network_error)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
