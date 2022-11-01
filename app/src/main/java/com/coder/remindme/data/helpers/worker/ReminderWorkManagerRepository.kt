package com.coder.remindme.data.helpers.worker

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.coder.core.util.Constants
import com.coder.remindme.R
import com.coder.remindme.data.local.dao.ReminderDao
import com.coder.remindme.domain.model.RemindType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ReminderWorkManagerRepository @Inject constructor(private val reminderDao: ReminderDao) {

    val HOURS_PER_DAY = 24

    /**
     * Minutes per hour.
     */
    val MINUTES_PER_HOUR = 60

    /**
     * Minutes per day.
     */
    val MINUTES_PER_DAY = MINUTES_PER_HOUR * HOURS_PER_DAY

    val DAYS_PER_WEEK = 7

    val HOURS_PER_WEEK = DAYS_PER_WEEK * HOURS_PER_DAY

    fun createWorkRequestAndEnqueue(
        context: Context,
        reminderId: Long,
        time: Instant,
        isFirstTime: Boolean = true
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.i(Constants.TAG, "createWorkRequestAndEnqueue")
            val data: Data.Builder = Data.Builder()
            data.putLong(
                context.resources.getString(R.string.reminder_instance_key),
                reminderId
            )

            val workerTag =
                "${context.resources.getString(R.string.reminder_worker_tag)}$reminderId"

            data.putString(context.resources.getString(R.string.worker_tag), workerTag)

            val reminder = reminderDao.getReminderById(reminderId).toReminder()

            if (time.toEpochMilli() > reminder.reminderEnd.toEpochMilli())
                return@launch

            val initialDelay = if (isFirstTime) {
                val timeDiff = time.toEpochMilli() - Instant.now(Clock.systemUTC()).toEpochMilli()
                Log.i(
                    Constants.TAG,
                    "isFirstTime $timeDiff ${time.epochSecond} ${Instant.now(Clock.systemUTC()).epochSecond}"
                )
                timeDiff
            } else {
                getDurationInMilli(reminderStrategy = reminder.remindType, reminderTime = time)
            }

            val dailyWorkRequest: WorkRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(
                    initialDelay,
                    TimeUnit.MILLISECONDS
                )
                .setInputData(data.build())
                .addTag(workerTag)
                .build()
            WorkManager.getInstance(context)
                .enqueue(dailyWorkRequest)
        }
    }

    private fun getDurationInMilli(
        reminderStrategy: RemindType,
        reminderTime: Instant
    ): Long {
        val duration: Long = when (reminderStrategy) {
            RemindType.DAILY -> Instant.now().plus(1, ChronoUnit.DAYS).toEpochMilli()
            RemindType.WEEKLY -> Instant.now().plus(HOURS_PER_WEEK.toLong(), ChronoUnit.HOURS)
                .toEpochMilli()
            RemindType.HOURLY -> Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
            else -> reminderTime.toEpochMilli()
        }
        return duration - Instant.now().toEpochMilli()
    }

    fun cancelWorkRequest(context: Context, tag: String) =
        WorkManager.getInstance(context).cancelAllWorkByTag(tag)
}