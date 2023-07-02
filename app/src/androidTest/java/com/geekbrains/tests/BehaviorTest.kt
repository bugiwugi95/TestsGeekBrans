package com.geekbrains.tests

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 18)
class BehaviorTest {

    private val uiDevice: UiDevice = UiDevice.getInstance(getInstrumentation())
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val packageName = context.packageName

    @Before
    fun setup() {
        uiDevice.pressHome()
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
        uiDevice.wait(Until.hasObject(By.pkg(packageName).depth(0)), TIMEOUT)
    }
    @Test
    fun test_MainActivityIsStarted() {
        val editText = uiDevice.findObject(By.res(packageName, "searchEditText"))
        Assert.assertNotNull(editText)
    }
    @Test
    fun test_SearchIsPositive() {
        val btnSearch = uiDevice.findObject(By.res(packageName, "searchButton"))
        val editText = uiDevice.findObject(By.res(packageName, "searchEditText"))
        editText.text = "UiAutomator"
        Espresso.onView(ViewMatchers.withId(R.id.searchEditText))
            .perform(ViewActions.pressImeActionButton())
        btnSearch.click()

        val changedText =
            uiDevice.wait(
                Until.findObject(By.res(packageName, "totalCountTextView")),
                TIMEOUT
            )
        Assert.assertEquals(changedText.text.toString(), "Number of results: 672")
    }

    @Test
    fun test_OpenDetailsScreen() {
        val toDetails: UiObject2 = uiDevice.findObject(
            By.res(
                packageName,
                "toDetailsActivityButton"
            )
        )
        toDetails.click()
        val changedText =
            uiDevice.wait(
                Until.findObject(By.res(packageName, "totalCountTextView_details")),
                TIMEOUT
            )
        Assert.assertEquals(changedText.text, "Number of results: 0")
    }

    //Убеждаемся, что DetailsScreen открывается
    @Test
    fun test_DetailsScreen() {

        // Кнопка поиска
        val btnSearch = uiDevice.findObject(By.res(packageName, "searchButton"))

        // Кнопка перехода на Details
        val toDetails: UiObject2 =
            uiDevice.findObject(By.res(packageName, "toDetailsActivityButton"))

        //Через uiDevice находим editText
        val editText = uiDevice.findObject(By.res(packageName, "searchEditText"))

        //Устанавливаем значение
        editText.text = "UiAutomator"
        //Отправляем запрос
        btnSearch.click()

        //Ожидаем  ответ.
        val changedText =
            uiDevice.wait(
                Until.findObject(By.res(packageName, "totalCountTextView")),
                TIMEOUT
            )
        val result = changedText.text.toString()

        // Переходим на Details
        toDetails.click()

        //Ожидаем конкретного события: появления текстового поля totalCountTextView_details.
        //Это будет означать, что DetailsScreen открылся и это поле видно на экране.
        val changedTextDetails =
            uiDevice.wait(
                Until.findObject(By.res(packageName, "totalCountTextView_details")),
                TIMEOUT
            )

        //Убеждаемся, что поле содержит предполагаемый текст.
        Assert.assertEquals(result, changedTextDetails.text.toString())
    }


    companion object {
        private const val TIMEOUT = 5000L
    }
}