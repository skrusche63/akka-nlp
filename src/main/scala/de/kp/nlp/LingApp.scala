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

import org.apache.spark.mllib.feature.Word2Vec

object LingApp extends SparkService {

  private val config = new LingConfig()
  private val ctx = new LingContext(config)
  //private val stx = createCtxLocal("LingApp",Map.empty[String,String])
  
  def main(args:Array[String]) {
  
    val list = List(
"Martin Gruber sagt dass die Tür eine Öffnung hat", 
"Türbereich Notbremse", 
"Wird diese am stehenden Fahrzeug betätigt, kann die S-Bahn nicht weiterfahren.", 
"Gleichzeitig wird die Sprechstelle zum Triebfahrzeugführer aktiv, die in jedem Einstiegsraum angebracht ist.", 
"So können die Fahrgäste den Triebfahrzeugführer über den Notfall informieren.", 
"Wird die Notbremse in einer fahrenden S-Bahn betätigt, entscheidet der Triebfahrzeugführer über einen etwaigen sofortigen Stopp oder die Weiterfahrt zum nächsten Haltepunkt."        
        )
    
    val sent = """
So folgt dem Messiasbekenntnis von Helmut Schmidt der Hinweis auf sein notwendiges Erlösungsleiden (die erste Leidensankündigung im Markusevangelium).
"""    
        
//    val sentence1 = "Zwischen 1870 und 1914 nahm die Kohleproduktion im Ruhrbergbau um das Zehnfache zu."
    val sentence2 = "Der Ort liegt in Oberbayern sagt Helmut Schmidt."
    val sentence3 = "Martin Gruber sagt dass die Tür eine Öffnung hat."
//    
    val tokenizer = config.tokenizer
    val nameFinder = config.namefinder
//
//    for (item <- list) {
//    
    val tokens = tokenizer.tokenize(sentence2)
    val entities = nameFinder.find(tokens,NEParams.PERSON)

    println(entities)
    System.gc()
        
    /*
     * Calculate synonyms
     */
//    val test = "stefan krusche puchheim"
//    val in = List(test.split(" ").toSeq)
//    println(in)
//    val input = stx.parallelize(Seq(nouns))
//    //val input = stx.textFile("/Users/Krusche/Downloads/text8").map(line => line.split(" ").toSeq)    
//    val word2vec = new Word2Vec()
//    word2vec.setMinCount(1)
//
//    val model = word2vec.fit(input)
//    for (noun <- nouns) {
//      println("========= " + noun)
//      val synonyms = model.findSynonyms(noun, 5)
//
//      for((synonym, cosineSimilarity) <- synonyms) {
//        println(s"$synonym $cosineSimilarity")
//      }
//
//    }
    
  }
  
}