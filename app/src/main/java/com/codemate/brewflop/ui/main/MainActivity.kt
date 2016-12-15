package com.codemate.brewflop.ui.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import com.codemate.brewflop.R
import com.codemate.brewflop.data.BrewingProgressUpdater
import com.codemate.brewflop.data.local.CoffeePreferences
import com.codemate.brewflop.data.local.RealmCoffeeStatisticLogger
import com.codemate.brewflop.data.network.SlackApi
import com.codemate.brewflop.data.network.SlackService
import com.codemate.brewflop.ui.secretsettings.SecretSettingsActivity
import com.codemate.brewflop.ui.userselector.UserSelectorActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), MainView {
    private val BREWING_TIME = TimeUnit.MINUTES.toMillis(7)
    private val TOTAL_UPDATE_STEPS = 30

    lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        val coffeePreferences = CoffeePreferences(this)
        val coffeeStatisticLogger = RealmCoffeeStatisticLogger()
        val brewingProgressUpdater = BrewingProgressUpdater(BREWING_TIME, TOTAL_UPDATE_STEPS)
        val slackApi = SlackService.getApi(SlackApi.BASE_URL)

        presenter = MainPresenter(
                coffeePreferences,
                coffeeStatisticLogger,
                brewingProgressUpdater,
                slackApi
        )
        presenter.attachView(this)

        val newCoffeeMessage = getString(R.string.new_coffee_available)
        coffeeProgressView.onClick {
            presenter.startDelayedCoffeeAnnouncement(newCoffeeMessage)
        }

        coffeeProgressView.onLongClick {
            startActivity(intentFor<SecretSettingsActivity>())
            true
        }

        logAccidentButton.onClick {
            startActivity(intentFor<UserSelectorActivity>())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun noChannelNameSet() {
        longToast(R.string.no_channel_name_set)
        startActivity(intentFor<SecretSettingsActivity>())
    }

    override fun newCoffeeIsComing() {
        coffeeStatusTitle.text = getString(R.string.coffee_is_coming_title)
        coffeeStatusMessage.text = getString(R.string.coffee_is_coming_message)
        coffeeProgressView.animate()
                .alpha(1f)
                .start()
    }

    override fun updateCoffeeProgress(newProgress: Int) {
        // For UX: this way the user gets instant feedback, as the waves
        // can't be below 10%
        val adjustedProgress = if (newProgress < 10) 10 else newProgress

        coffeeProgressView.setProgress(adjustedProgress)
    }

    override fun resetCoffeeViewStatus() {
        coffeeProgressView.setProgress(0)
        coffeeStatusTitle.text = getString(R.string.did_you_start_the_coffee_machine)
        coffeeStatusMessage.text = getString(R.string.touch_here_to_notify_when_coffee_ready)
        coffeeProgressView.animate()
                .alpha(0.2f)
                .start()
    }

    override fun showCancelCoffeeProgressPrompt() {
        alert {
            title(R.string.really_cancel_coffee_progress_title)
            message(R.string.really_cancel_coffee_progress_message)

            cancelButton()
            okButton { presenter.cancelCoffeeCountDown() }
        }.show()
    }
}