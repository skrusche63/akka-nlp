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

import com.googlecode.cqengine.query.QueryFactory

object PolarityApp {

  def main(args:Array[String]) {
    
    val path = "/Users/Krusche/Downloads/GermanPolarityClues-2012/"
    
    val polarityClues = new PolarityClues()    
    val clues = polarityClues.build(path)

    val query = QueryFactory.equal(PolarityAttrs.FEATURE,"Abmachung")
    val rs = clues.retrieve(query)
    
    val iterator = rs.iterator()
    while (iterator.hasNext()) {
      val clue = iterator.next
      println(clue)
    }

  }
}