package de.kp.nlp
/* Copyright (c) 2014 Dr. Krusche & Partner PartG
* 
* This file is part of the Akka-NLP project
* (https://github.com/skrusche63/akka-nlp).
* 
* Akka-NLP is free software: you can redistribute it and/or modify it under the
* terms of the GNU General Public License as published by the Free Software
* Foundation, either version 3 of the License, or (at your option) any later
* version.
* 
* Akka-NLP is distributed in the hope that it will be useful, but WITHOUT ANY
* WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
* A PARTICULAR PURPOSE. See the GNU General Public License for more details.
* You should have received a copy of the GNU General Public License along with
* Akka-NLP. 
* 
* If not, see <http://www.gnu.org/licenses/>.
*/

import gate._

import gate.creole.ANNIEConstants
import gate.util.persistence.PersistenceManager

import java.io.File
import java.net.URL

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

class GateWrapper {

  private val LANGUAGE_IDENTIFIER = "org.knallgrau.utils.textcat.LanguageIdentifier"
  private val POS_TAGGER          = "gate.creole.POSTagger"
  private val SENTENCE_SPLITTER   = "gate.opennlp.OpenNlpSentenceSplit"
    
  val home = Configuration.gate
  
  Gate.runInSandbox(true);
  Gate.setGateHome(new File(home));
	   
  Gate.setPluginsHome(new File(home, "plugins"))
  Gate.init()

  Gate.getCreoleRegister.registerDirectories(new URL("file://" + Gate.getPluginsHome + "/ANNIE"))
  Gate.getCreoleRegister.registerDirectories(new URL("file://" + Gate.getPluginsHome + "/Language_Identification"))
  Gate.getCreoleRegister.registerDirectories(new URL("file://" + Gate.getPluginsHome + "/OpenNLP"))

  val corpus = Factory.newCorpus("GATE Corpus")
  /*
   * This GATE wrapper requires a GATE application installed, e.g. ANNIE  
   */	   
  val url = new URL("file:" + home + "/application.xgapp");
  val application = PersistenceManager.loadObjectFromUrl(url).asInstanceOf[CorpusController]

  application.setCorpus(corpus)

  /*

  def extractAnnotationsFromDocument(document: Document): List[Map[String, String]] = {
    val posTaggedAnnotations = new ListBuffer[Map[String, String]]()
    val gateAnnotations = document.getAnnotations
    val iterator = gateAnnotations.iterator()

    while (iterator.hasNext) {
      val annotationImpl: Annotation = iterator.next()
      posTaggedAnnotations += Map(
        "start" -> annotationImpl.getStartNode.getOffset.toString,
        "end" -> annotationImpl.getEndNode.getOffset.toString,
        "type" -> annotationImpl.getType,
        "category" -> (annotationImpl.getFeatures.get("category") + ""),
        "text" -> annotationImpl.getFeatures.get("string").toString,
        "kind" -> annotationImpl.getFeatures.get("kind").toString)
    }

    posTaggedAnnotations.toList
  }

}
   */
  
  def getAnnotationsXml(text:String):String = {

	val document = Factory.newDocument(text)
	annotateDocument(document)
	   
	val xml = document.toXml()
	Factory.deleteResource(document)
	   
	xml
   
  }

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

  def getLanguage(text:String): String = {

    val document = Factory.newDocument(text)
    val analyzer = Factory.createResource(LANGUAGE_IDENTIFIER).asInstanceOf[LanguageAnalyser]

    analyzer.setDocument(document)
    analyzer.init()

    analyzer.execute()

    val language = document.getFeatures.get("lang").asInstanceOf[String]

    Factory.deleteResource(document)
    Factory.deleteResource(analyzer)

    language
    
  }
  
  def getPOS(text:String,lang:String,sentences:List[Map[String,String]]):List[Map[String,String]] = {

    val document = Factory.newDocument(text)
    val tagger = Factory.createResource(POS_TAGGER, Factory.newFeatureMap()).asInstanceOf[LanguageAnalyser]

    sentences.foreach(sentence => {
      
      val featureMap = Factory.newFeatureMap()
    
      featureMap.put("string", "")
      featureMap.put("kind",   "")
      
      document.getAnnotations.add(sentence("start").toLong, sentence("end").toLong, sentence("type"), featureMap)
    
    })
    
    tagger.setDocument(document)
    tagger.init()

    tagger.execute()

    val result = new ListBuffer[Map[String,String]]()

    val annotations = document.getAnnotations
    annotations.map(annotation => {
      
      val features = annotation.getFeatures
      
      result += Map(
        "start" -> annotation.getStartNode.getOffset.toString,
        "end"   -> annotation.getEndNode.getOffset.toString,
        
        "type"     -> annotation.getType,
        "category" -> (features.get("category") + ""),
        
        "text" -> features.get("string").toString,
        "kind" -> features.get("kind").toString
        
      )
      
    })
   
    
    Factory.deleteResource(document)
    Factory.deleteResource(tagger)

    result.toList
    

  }

  def getSentences(text:String,lang:String):List[Map[String,String]] = {

    val document = Factory.newDocument(text)
    val splitter = Factory.createResource(SENTENCE_SPLITTER, Factory.newFeatureMap()).asInstanceOf[LanguageAnalyser]

    splitter.setDocument(document)
    splitter.init()

    splitter.execute()

    val annotations = document.getAnnotations.get(ANNIEConstants.SENTENCE_ANNOTATION_TYPE)
    val sentences = annotations.map(annotation => {
      
      val start = annotation.getStartNode().getOffset()
      val end   = annotation.getEndNode().getOffset()

      Map(
        "start" -> start.toString,
        "end"   -> end.toString,
        "type"  -> annotation.getType(),
        "text"  -> Utils.stringFor(document, start, end) 
        )
     
    })
    
    Factory.deleteResource(splitter)
    Factory.deleteResource(document)

    sentences.toList

  }

  private def annotateDocument(document:gate.Document):gate.Document = {
	   
    corpus.add(document)
    application.execute()
      
    corpus.clear()
    document
   
  }

  def close() {

    Factory.deleteResource(corpus)
    Factory.deleteResource(application)
   
  }

}