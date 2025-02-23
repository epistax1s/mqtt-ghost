package space.left4dev.config.connect

class ConnectConfig {

    String clientId
    String host
    int port = 1883
    String username
    String password

    SslConfig ssl
    WillConf will

    int keepAlive
    boolean cleanStart
    int sessionExpiryInterval

    void ssl(Closure closure) {
        this.ssl = new SslConfig()
        closure.delegate = this.ssl
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure()
    }

    void will(Closure closure) {
        this.will = new WillConf()
        closure.delegate = this.will
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure()
    }
}
