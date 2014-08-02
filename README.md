![Dr.Krusche & Partner PartG](https://raw.github.com/skrusche63/spark-elastic/master/images/dr-kruscheundpartner.png)

## Text Processing in Scala with GATE and Akka

[GATE](https://gate.ac.uk/) is a very popular library for text processing, and [Akka](http://akka.io/) is a fascinating toolkit and runtime for building highly concurrent, distributed, and fault tolerant event-driven applications.

> Why not bring those tools together to build highly concurrent, reactive text engineering applications?

### ANNIE

GATE is distributed with an information extraction (IE) system called ANNIE, which is short for A Nearly-New IE system. ANNIE relies on finite state algorithms and the JAPE (Regular Expressions over Annotations) language, and consists of the following components:

* Unicode Tokenizer,
* Sentence Splitter,
* Part of Speech Tagger,
* Semantic Tagger,
* Gazetteer, and

more. All these components may be used to build sophisticated text processing pipelines.


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
