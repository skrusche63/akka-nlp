![Dr.Krusche & Partner PartG](https://raw.github.com/skrusche63/spark-elastic/master/images/dr-kruscheundpartner.png)

## Named Entity Recognition (NER) in Scala

This project implements an NER micro service that may be easily integrated in any Akka-based loose coupling environment to bring the power of GATE text processing to distributed data processing systems.

> Keywords: Akka, ANNIE, GATE, Named Entity Recognition, Scala, Service Oriented Ennvironment, Text Processing


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

A certain text artifact is then processed by GATE with just a few lines of Scala:
```
  def getAnnotations(text:String):Seq[Map[String,String]] = {

    val document = Factory.newDocument(text)
	annotateDocument(document) 

	val annotations = document.getAnnotations()
	val result = annotations.map(annotation => {
	     
	  val features = annotation.getFeatures()
	  val map = features.map(feature => {
	       
	    val k = feature._1.toString
	    val v = feature._2.toString
	       
	    (k,v)
	       
	  })
	     
	  map.toMap
	     
	}).toSeq

	Factory.deleteResource(document)
    result
    
  }

  private def annotateDocument(document:Document):Document = {
	   
    corpus.add(document)
    application.execute()
      
    corpus.clear()
    document
   
  }

```

GATE retrieves a text artifact and returns a sequence of annotations described as a `Seq[Map[String,String]]`, where the map represents a GATE feature map with `feature name -> feature value`.

### Akka Remoting

Akka is a toolkit for build concurrent scalable applications, using the [Actor Model](http://en.wikipedia.org/wiki/Actor_model). Akka comes with a feature called *Akka Remoting*, which easily enables to setup a communication between software components in a peer-to-peer fashion.

Akka and Akka Remoting may therefore be used as an appropriate means to establish a communication between prior independent software components - easy and fast. The figure below illustrates the integration pattern provided with this project.
![Akka Client-Server Pattern](https://raw.githubusercontent.com/skrusche63/akka-nlp/master/images/Akka%20Client-Server%20Pattern.png)


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
