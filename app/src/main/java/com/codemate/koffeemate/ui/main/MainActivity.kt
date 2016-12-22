package com.codemate.koffeemate.ui.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import com.codemate.koffeemate.KoffeemateApp
import com.codemate.koffeemate.R
import com.codemate.koffeemate.data.ScreenSaver
import com.codemate.koffeemate.data.local.models.CoffeeBrewingEvent
import com.codemate.koffeemate.ui.settings.SettingsActivity
import com.codemate.koffeemate.ui.userselector.UserSelectorActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), MainView {
    @Inject
    lateinit var presenter: MainPresenter
    lateinit var screensaver: ScreenSaver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        KoffeemateApp.appComponent.inject(this)

        screensaver = ScreenSaver(this)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        presenter.attachView(this)
        setUpListeners()
    }

    fun setUpListeners() {
        coffeeProgressView.onClick {
            screensaver.defer()

            val newCoffeeMessage = getString(R.string.new_coffee_available)
            presenter.startDelayedCoffeeAnnouncement(newCoffeeMessage)
        }

        coffeeProgressView.onLongClick {
            screensaver.defer()

            startActivity(intentFor<SettingsActivity>())
            true
        }

        logAccidentButton.onClick {
            screensaver.defer()
            presenter.launchUserSelector()
        }
    }

    override fun onStart() {
        super.onStart()
        screensaver.start()
    }

    override fun onResume() {
        super.onResume()
        presenter.updateLastBrewingEventTime()
    }

    override fun onStop() {
        super.onStop()
        screensaver.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun noAnnouncementChannelSet() {
        longToast(R.string.no_announcement_channel_set)
        startActivity(intentFor<SettingsActivity>())
    }

    override fun noAccidentChannelSet() {
        longToast(R.string.no_accident_channel_set)
        startActivity(intentFor<SettingsActivity>())
    }

    override fun launchUserSelector() {
        startActivity(intentFor<UserSelectorActivity>())
    }

    override fun setLastBrewingEvent(event: CoffeeBrewingEvent) {
        lastBrewingEventTime.setTime(event.time)
    }

    override fun newCoffeeIsComing() {
        coffeeStatusTitle.text = getString(R.string.title_coffeeview_brewing)
        coffeeStatusMessage.text = getString(R.string.message_coffeeview_brewing)
        coffeeProgressView.animate()
                .alpha(1f)
                .start()
    }

    override fun updateCoffeeProgress(newProgress: Int) {
        coffeeProgressView.setProgress(newProgress)
    }

    override fun resetCoffeeViewStatus() {
        coffeeStatusTitle.text = getString(R.string.title_coffeeview_idle)
        coffeeStatusMessage.text = getString(R.string.message_coffeeview_idle)
        coffeeProgressView.animate()
                .alpha(0.2f)
                .start()
    }

    override fun showCancelCoffeeProgressPrompt() {
        alert {
            title(R.string.really_cancel_coffee_progress_title)
            message(R.string.really_cancel_coffee_progress_message)

            negativeButton(R.string.no)
            positiveButton(R.string.yes) {
                presenter.cancelCoffeeCountDown()
            }
        }.show()
    }
}