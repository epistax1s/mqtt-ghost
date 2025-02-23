package space.left4dev.config.action

class ActionBaseConfig {

    Closure before
    Closure after

    ScheduleConfig schedule

    void schedule(Closure closure) {
        this.schedule = new ScheduleConfig()
        closure.delegate = this.schedule
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure()
    }
}
