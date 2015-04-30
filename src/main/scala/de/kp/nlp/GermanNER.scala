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

import org.apache.spark.SparkContext

import scala.io.Source
import scala.collection.mutable.ArrayBuffer

object NERApp {
  
  private val infile = "/eclipse/workspace/scala-nlp/src/main/resources/NER-de-train.tsv"
  private val outfile = "/eclipse/workspace/scala-nlp/src/main/resources/de-organization.txt"
  
  def main(args:Array[String]) {
    
    val builder = new GermanNE("organization")
    builder.build(infile,outfile)
    
  }
  
}

case class NESentence(tokens:Seq[NEToken])

case class NEToken(pos:Int,token:String,outerSpan:String,innerSpan:String)

class GermanNE(val category:String) {

  private val mapper = Map(
    "person" -> "PER","organization" -> "ORG","location" -> "LOC","other" -> "OTH"
  )
  
  def build(infile:String,outfile:String) {
    
    val writer = new java.io.PrintWriter(new java.io.File(outfile))
    try {
      
      val lines = Source.fromFile(infile).getLines()
      /* 
       * The data contains a source identifier for each sentence, starting
       * with '#', and sentences are separated by an empty line
       */
      val data = lines.filter(line => !line.startsWith("#")).filter(line => line != "")
    /*
     * Transform a text line into a token tuple
     */
    val tokens = data.map(line => {
      /*
       * Each line contains a token number (position within the sentence),
       * the token, the assigned outer span & inner span
       * 
       * The entities from the data set are to be classified in four main categories 
       * 
       * - PER: person
       * - ORG: organization
       * - LOC: location
       * - OTH: other
       * 
       * with three subclasses 
       * 
       * - main, a NE comprises the full span
       * - part, a NE takes only part of the span and 
       * - deriv, the span is a derivation of a NE.
       * 
       */
      val Array(position,token,outerSpan,innerSpan) = line.split("\\t")
      NEToken(position.toInt,token,outerSpan,innerSpan)
      
    })
    
    val sentence = ArrayBuffer.empty[NEToken]
    for (token <- tokens) {
      
      if (token.pos == 1) {
        /*
         * Start a new sentence
         */
        if (sentence.isEmpty) {
          sentence += token
        
        } else {
          
          val sent = buildSentence(sentence,category)
          if (sent.contains("<START")) writer.println(sent)
          
          sentence.clear()
          sentence += token
          
        }    
        
      } else {
        sentence += token
      
      }
      
    }
      
    } catch {
      case e:Exception => println(e.getMessage)
    
    } finally {
      writer.close()
    }
    
  }
  
  /* 
   * <START:person> Pierre Vinken <END> , 61 years old , will join the board as a 
   * nonexecutive director Nov. 29 .
   */
  private def buildSentence(tokens:Seq[NEToken],category:String):String = {
    
    val span = mapper(category)
    val sb = new StringBuffer()

    var begin = false
    for (token <- tokens) {
            
      val outerSpan = token.outerSpan
      if (outerSpan == "B-" + span) {
        
        if (begin) sb.append(" <END>")
        
        begin = true        
        sb.append(" <START:" + category + ">")
        
      } else if (outerSpan == "I-" + span) {
        // do nothing as this is an inner span
        
      } else {
        
        if (begin) sb.append(" <END>")
        begin = false
        
      }

      sb.append(" " + token.token)
      
    }
    
    sb.toString.trim
    
  }
}