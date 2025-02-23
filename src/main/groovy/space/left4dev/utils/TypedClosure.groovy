package space.left4dev.utils

class TypedClosure<V> {

    Closure<V> closure
    String type

    TypedClosure(String type, Closure<V> closure) {
        this.type = type
        this.closure = closure
    }

    def call() {
        closure.call()
    }

}
