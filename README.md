![Dr.Krusche & Partner PartG](https://raw.github.com/skrusche63/spark-elastic/master/images/dr-kruscheundpartner.png)

## Text Mining with GATE and Akka

[GATE](https://gate.ac.uk/) is a very popular library for text processing, and [Akka](http://akka.io/) is a fascinating toolkit and runtime for building highly concurrent, distributed, and fault tolerant event-driven applications.

> Why not bring those tools together to build highly concurrent, reactive text engineering applications?

### Actor Remoting

The configuration file below illustrates how to configure Akka to easily build a server.
```
akka {
  actor {
    provider = "akka.remote.RemoteActorRefProvider"
  }
  remote {
    enabled-transports = ["akka.remote.netty.tcp"]
    netty.tcp {
      hostname = "127.0.0.1"
      port = 2600
    }
    log-sent-messages = on
    log-received-messages = on
  }
}
```
