/*
 * Copyright 2017 Codemate Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codemate.koffeemate.ui.userselector

import com.codemate.koffeemate.BuildConfig
import com.codemate.koffeemate.data.network.SlackApi
import com.codemate.koffeemate.data.network.SlackService
import com.codemate.koffeemate.data.network.models.User
import com.codemate.koffeemate.testutils.getResourceFile
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.core.IsEqual.equalTo
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import rx.observers.TestSubscriber
import rx.schedulers.Schedulers

class LoadUsersUseCaseTest {
    lateinit var mockServer: MockWebServer
    lateinit var slackApi: SlackApi
    lateinit var useCase: LoadUsersUseCase
    lateinit var testSubscriber: TestSubscriber<List<User>>

    @Before
    fun setUp() {
        mockServer = MockWebServer()
        mockServer.start()

        slackApi = SlackService.getApi(mockServer.url("/"))
        useCase = LoadUsersUseCase(
                slackApi,
                Schedulers.immediate(),
                Schedulers.immediate()
        )

        testSubscriber = TestSubscriber<List<User>>()
    }

    @After
    fun tearDown() {
        mockServer.shutdown()
    }

    @Test
    fun execute_MakesRequestToRightPathWithToken() {
        mockServer.enqueue(MockResponse().setBody(""))
        useCase.execute().subscribe(testSubscriber)

        val request = mockServer.takeRequest()
        assertThat(request.path, equalTo("/users.list?token=${BuildConfig.SLACK_AUTH_TOKEN}"))
    }

    @Test
    fun execute_WithValidData_CallsOnNextWithUsersAndCompletes() {
        val userListJson = getResourceFile("seeds/sample_userlist_response.json").readText()
        mockServer.enqueue(MockResponse().setBody(userListJson))

        useCase.execute().subscribe(testSubscriber)
        testSubscriber.assertValueCount(1)

        val userList = testSubscriber.onNextEvents[0]
        assertThat(userList.size, equalTo(2))

        with(userList[0]) {
            assertThat(id, equalTo("abc123"))
            assertThat(name, equalTo("bobby"))
            assertThat(is_bot, equalTo(false))
            assertThat(profile.first_name, equalTo("Bobby"))
            assertThat(profile.last_name, equalTo("Tables"))
            assertThat(profile.real_name, equalTo("Bobby Tables"))
        }

        with(userList[1]) {
            assertThat(id, equalTo("123abc"))
            assertThat(name, equalTo("john"))
            assertThat(is_bot, equalTo(false))
            assertThat(profile.first_name, equalTo("John"))
            assertThat(profile.last_name, equalTo("Smith"))
            assertThat(profile.real_name, equalTo("John Smith"))
        }

        testSubscriber.assertCompleted()
        testSubscriber.assertNoErrors()
    }
}