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

object NameTrainer {
 
  def main(args:Array[String]) {

  val infile = "/eclipse/workspace/scala-nlp/src/main/resources/de-organization.txt"
  val outfile = "/eclipse/workspace/scala-nlp/src/main/resources/de-organization.bin"

  val category = "location"
  
  val iterations = 100
  val cutoff = 0
  
  val res = NETrainer(infile,outfile,category,iterations,cutoff)
     
  }

}