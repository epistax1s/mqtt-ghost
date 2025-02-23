package space.left4dev.handler

import space.left4dev.config.action.ScheduleConfig

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class ScheduleManager {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4)

    ScheduledFuture<?> schedule(Runnable task, ScheduleConfig schedule) {
        if (schedule.interval) {
            return scheduler.scheduleAtFixedRate(task, 0, schedule.interval, TimeUnit.MILLISECONDS)
        }
        // Поддержка cronExpression может быть добавлена в будущем
        scheduler.submit(task) as ScheduledFuture<?>
    }
}