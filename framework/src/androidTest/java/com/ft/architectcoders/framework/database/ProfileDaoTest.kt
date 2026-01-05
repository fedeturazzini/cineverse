package com.ft.architectcoders.framework.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileDaoTest {
    private lateinit var database: CineVerseDatabase
    private lateinit var profileDao: ProfileDao

    @Before
    fun setUp() {
        database =
            Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                CineVerseDatabase::class.java,
            ).allowMainThreadQueries().build()
        profileDao = database.profileDao
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun saveAndGetProfile_returnsCorrectData() =
        runTest {
            // Given
            val profile = sampleDbProfile()

            // When
            profileDao.saveProfile(profile)

            // Then
            val result = profileDao.getProfile().first()
            assertEquals("Test User", result?.name)
            assertEquals("US", result?.region)
        }

    @Test
    fun getProfile_whenEmpty_returnsNull() =
        runTest {
            // When
            val result = profileDao.getProfile().first()

            // Then
            assertNull(result)
        }

    @Test
    fun saveProfile_replaceExisting_updatesData() =
        runTest {
            // Given
            val originalProfile = sampleDbProfile()
            profileDao.saveProfile(originalProfile)

            // When
            val updatedProfile = originalProfile.copy(name = "Updated User", region = "ES")
            profileDao.saveProfile(updatedProfile)

            // Then
            val result = profileDao.getProfile().first()
            assertEquals("Updated User", result?.name)
            assertEquals("ES", result?.region)
        }

    private fun sampleDbProfile() =
        DbProfile(
            id = 1,
            name = "Test User",
            profilePhotoPath = "/path/to/photo.jpg",
            region = "US",
            favoriteGenres = "Action, Comedy, Drama",
        )
}
