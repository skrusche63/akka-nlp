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

import scala.collection.mutable.HashMap

class LingContext(val config:LingConfig) extends Serializable {

  private val CARD_TAG = "CARD"
  private val EXCLUDE_TAGS = List(CARD_TAG)
  
  /**
   * The main method to annotate a text document with detected sentences, 
   * words as associated part of speech and syllables. In addition the 
   * Flesh-Reading-Ease index (German version) is used to determine the
   * readability of the document.
   */
  def annotateText(text:String):Document = {
    
    val start = System.currentTimeMillis()
    /*
     * The text requires extra cleaning; otherwise sentence detection
     * and subsequent linguistic tasks have errors
     */
    val cleaned = clean(text)
    
    /* Determine annotated sentences */
    val sentences = annotateSentences(detectSentences(cleaned))
    
    /* Build response */ 
    val document = Document(
        text = cleaned,
        sentences = sentences,
        readability = readability(sentences)
    )

    val nameFinder = config.namefinder
    nameFinder.clear
   
    val end = System.currentTimeMillis()
    println("Text annotated in " + (end-start) + " ms")
    
    document
    
  }
  /**
   * The English (original) version of the Flesch index is
   * used here in combination with an adapted (to German)
   * evaluation matrix
   */
  private def readability(sentences:Seq[Sentence],lang:String="en"):Double = {
    
    val r = new Readability(sentences)
    if (lang == "en") r.english else r.german
    
  }
  /**
   * This method evaluates a text document and detects the respective sentences. 
   * Each sentence is described by a unique identifier and a probability score
   * that indicates the quality of detection.
   */
  private def detectSentences(text:String):Seq[Sentence] = {
    
    val detector = config.detector
    
    val sentences = detector.sentDetect(text)
    val scores = detector.getSentenceProbabilities
    
    /*
     * Determine statistics from scores to detect 
     * outlier sentences
     */
    val stats = new Statistics(scores).stats
    val threshold = stats.mean - stats.stdev
    
    val scoredSentences = sentences.zip(scores)
    val pairedSentences = scoredSentences.zip(scoredSentences.tail)
    
    sentences.zip(scores).zipWithIndex.map{case ((text,score),sid) => Sentence(sid,text,score)}

  }
  /**
   * This method evaluate a list of sentence on the respective
   * term level and determines associated words, part of speech
   * and related syllables.
   */
  private def annotateSentences(sentences:Seq[Sentence]):Seq[Sentence] = {
    sentences.map(annotateSentence)
  }  
  
  private def annotateSentence(sentence:Sentence):Sentence = {
    
    val tokenizer = config.tokenizer
    val tokens = tokenizer.tokenize(sentence.text)
    
    entities(tokens)
    
    Sentence(
      sentence.sid,
      sentence.text,
      sentence.score,
      terms(tokens)
    )

  }
  
  private def entities(tokens:Array[String]):Seq[Entity] = {

    val nameFinder = config.namefinder
    
    /* PERSON */
    val persons = nameFinder.find(tokens,NEParams.PERSON)
    
    /* LOCATION */
    val locations = nameFinder.find(tokens,NEParams.LOCATION)
    
    /* ORGANIZATION */
    val organizations = nameFinder.find(tokens,NEParams.ORGANIZATION)
    
    val entities = persons ++ locations ++ organizations
    entities

  }
  
  /**
   * This method provides part of speech tagging, and also
   * computes the syllables for each detected term.  
   */
  private def terms(tokens:Array[String]):Seq[Term] = {
   
    val tagger = config.tagger
    val tags = tagger.tag(tokens)
    
    /* 
     * Filter words, i.e. all tokens that do not refer to EXCLUDE_TAGS 
     * and determine associated syllables for each detected word.
     */
    tokens.zip(tags).filter{case(term,tag) => 
      EXCLUDE_TAGS.contains(tag) == false && tag.startsWith("$") == false
    }.map{case(term,tag) => Term(term,tag,syllables(term))}
    
  }
  
  /**
   * A private method to determine syllables for German terms
   * based on linguistic patterns, originally for LaTex
   */
  private def syllables(word:String):Seq[String] = {
    
    val hyphenator = config.hyphenator
    val hyphenation = hyphenator.hyphenate(word.toLowerCase, 2, 2)
    
    if (hyphenation == null) {
      Seq(word)

    } else {

      val points = hyphenation.getHyphenationPoints
      val pairs = points.zip(points.tail)
   
      pairs.map(pair => word.substring(pair._1, pair._2))

    }
    
  }
  
  private def clean(text:String):String = {
    /* Replace multiple dots to enable sentence detection */
    text.trim()
      .replace("-"," ")
      // multiple points
      .replaceAll("\\.+",".")
      // brackets
      .replaceAll("\\(|\\)|\\[|\\]","")
      .replace("\"","")
      .replaceAll("\\r\\n|\\r|\\n", " ")
      .replaceAll(" +", " ")

  }

}