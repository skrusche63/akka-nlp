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

class GateWrapper {

  private val LANGUAGE_IDENTIFIER = "org.knallgrau.utils.textcat.LanguageIdentifier"
  private val SENTENCE_SPLITTER   = "gate.opennlp.OpenNlpSentenceSplit"
    
  val home = Configuration.gate
  
  Gate.runInSandbox(true);
  Gate.setGateHome(new File(home));
	   
  Gate.setPluginsHome(new File(home, "plugins"))
  Gate.init()

  Gate.getCreoleRegister.registerDirectories(new URL("file://" + Gate.getPluginsHome + "/Language_Identification"))
  Gate.getCreoleRegister.registerDirectories(new URL("file://" + Gate.getPluginsHome + "/OpenNLP"))

  val corpus = Factory.newCorpus("GATE Corpus")
  /*
   * This GATE wrapper requires a GATE application installed, e.g. ANNIE  
   */	   
  val url = new URL("file:" + home + "/application.xgapp");
  val application = PersistenceManager.loadObjectFromUrl(url).asInstanceOf[CorpusController]

  application.setCorpus(corpus)

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

  def getSentences(text:String,lang:String):List[String] = {

    val document = Factory.newDocument(text)
    val splitter = Factory.createResource(SENTENCE_SPLITTER, Factory.newFeatureMap()).asInstanceOf[LanguageAnalyser]

    splitter.setDocument(document)
    splitter.init()

    splitter.execute()

    //val sentences =  getAnnotations(document)

    val annotations = document.getAnnotations.get(ANNIEConstants.SENTENCE_ANNOTATION_TYPE)
    val sentences = annotations.map(annotation => {
      
      val start = annotation.getStartNode().getOffset()
      val end   = annotation.getEndNode().getOffset()

      val sentence = Utils.stringFor(document, start, end)   
      sentence
      
    })
    
    Factory.deleteResource(splitter)
    Factory.deleteResource(document)

    sentences.toList

  }

  private def annotateDocument(document:Document):Document = {
	   
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