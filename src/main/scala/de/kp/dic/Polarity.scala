package de.kp.dic
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

import scala.io.Source

import com.googlecode.cqengine.{CQEngine,IndexedCollection}
import com.googlecode.cqengine.attribute.SimpleAttribute

object PolarityAttrs {

  val FEATURE = new SimpleAttribute[PolarityClue,String]("feature") {
    def getValue(clue:PolarityClue):String = clue.feature
  }
  
}

case class PolarityClue( 
  val feature:String,
  val lemma:String,
  val pos:String,
  val polarity:String,
  /*
   * Positive, negative and neutral rating probability
   * with respect to the underlying corpus
   */
  val rating:String,
  val other:String)


class PolarityClues {

  private val clues = CQEngine.newInstance[PolarityClue]()
  /**
   * Version 0.2 (April, 2012) dataset GermanPolarityClues-2012 dataset 
   * is synchronized with SentiWS v1.8c (Remus et al, 2010)
   * 
   */
  def build(path:String):IndexedCollection[PolarityClue] = {
   
    val lines = Source.fromFile(path + "GermanPolarityClues-Positive-21042012.tsv").getLines()
    lines.foreach(line => {
      
      val Array(feature,lemma,pos,polarity,rating,other) = line.split("\\t")
      val clue = PolarityClue(feature,lemma,pos,polarity,rating,other)
    
      clues.add(clue)
      
    })

    clues
    
  }
  
}