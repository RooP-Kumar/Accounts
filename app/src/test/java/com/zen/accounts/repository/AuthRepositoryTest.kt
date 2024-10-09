package com.zen.accounts.repository

import com.zen.accounts.data.db.model.User
import com.zen.accounts.data.api.AuthApi
import dagger.hilt.android.AndroidEntryPoint
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before
import org.junit.Test
import javax.inject.Inject
import org.hamcrest.MatcherAssert.*
import org.junit.Assert


class AuthRepositoryTest {
    @Test
    fun addition_isCorrect() {
        Assert.assertEquals(4, 2 + 2)
    }
}