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

import com.typesafe.config.ConfigFactory

class LingConfig {

  private val path = "application.conf"
  private val config = ConfigFactory.load(path).getConfig("model")
    
  val detector = SentenceDetector(config)
  if (detector == null) throw new Exception("[ERROR] SentenceDetector cannot be created.")
 
  val hyphenator = Hyphenator(config)
  if (hyphenator == null) throw new Exception("[ERROR] Hyphenator cannot be created.")

  val namefinder = NEFinder(config)
  if (namefinder == null) throw new Exception("[ERROR] Namefinder cannot be created.")
    
  val tagger = POSTagger(config)
  if (tagger == null) throw new Exception("[ERROR] POSTagger cannot be created.")
 
  val tokenizer = Tokenizer(config)
  if (tokenizer == null) throw new Exception("[ERROR] Tokenizer cannot be created.")
  
  val sentimentFinder = SAFinder(config)
  if (sentimentFinder == null) throw new Exception("[ERROR] SentimentFinder cannot be created.")

}