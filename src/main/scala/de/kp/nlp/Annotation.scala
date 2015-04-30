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

case class Document(
  val text:String,
  val sentences:Seq[Sentence] = Seq.empty[Sentence],
  val readability:Double = 0) extends Serializable {
  
  /**
   * This method retrieves all unique nouns, that
   * describe the text document
   */
  def getNouns():Seq[String] = {
    sentences.flatMap(sentence => sentence.terms.filter(t => t.tag == "NN").map(_.term)).distinct
  }
  
}

case class Sentence(
  val sid:Int,
  val text:String,
  val score:Double,
  /*
   * 
   */
  val terms:Seq[Term] = Seq.empty[Term],
  /*
   * Named entities extracted from this sentence
   */
  val entities:Seq[Entity] = Seq.empty[Entity])

case class Term(
  val term:String,val tag:String,val syllables:Seq[String] = Seq.empty[String],val total:Int = 0)
  
case class Entity(
  val name:String,val category:String,score:Double
)
