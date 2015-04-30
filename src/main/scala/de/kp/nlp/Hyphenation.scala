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

import org.apache.lucene.analysis.compound.hyphenation._

import scala.xml
import scala.collection.mutable.ArrayBuffer

/**
 * https://code.google.com/p/myway/source/browse/trunk/config/hyph/de_DR.xml?r=4
 */
object Hyphenation {
    
  val is = getClass.getClassLoader.getResourceAsStream("de_DR.xml")
  val source = xml.Source.fromInputStream(is)
    
  val  hyphenator = new HyphenationTree()
  hyphenator.loadPatterns(source)

  /**
   * Component of Flesh Kincaid algorithm"
   */
  def main(args:Array[String]) {

    val start = System.currentTimeMillis()
    
    val words = List("Flugplatz","Dampfschiff","Wohnung","Arbeitsplatz")
    val res = words.map(syllables)   
    
    println(res.toList)
    
    val end = System.currentTimeMillis()
    
    println("Time: " + (end-start) + " ms")
  }
 
  def syllables(word:String):Seq[String] = {

    val hyphenation = hyphenator.hyphenate(word, 2, 2)
    if (hyphenation == null) {
      Array.empty[String]

    } else {

      val points = hyphenation.getHyphenationPoints
      val pairs = points.zip(points.tail)
   
      pairs.map(pair => word.substring(pair._1, pair._2))

    }
    
  }
  
}