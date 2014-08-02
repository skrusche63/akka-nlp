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

more. All these components may be used to build sophisticated text processing pipelines. It is worth to mention, that the Gazetteer is an easy to use component for (supervised) Named Entity Recognition (NER): 

The Gazetter requires plain text files, where each text line represents a name, such as a name of a city, organisation or product. These text files are then compiled into finite state machines, prepared for fast reading.


In this project, we wrap GATE to especially get access to ANNIE. The code example below illustrates how to configure and run GATE (or ANNIE) within a Scala application:
```
class AnnieWrapper(home:String) {

  Gate.runInSandbox(true);
  Gate.setGateHome(new File(home));
	   
  Gate.setPluginsHome(new File(home, "plugins"))
  Gate.init();

  val corpus = Factory.newCorpus("GATE Corpus")
  /*
   * This GATE wrapper requires a GATE application installed, e.g. ANNIE  
   */	   
  val url = new URL("file:" + home + "/application.xgapp");
  val application = PersistenceManager.loadObjectFromUrl(url).asInstanceOf[CorpusController]

  application.setCorpus(corpus)

```



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
