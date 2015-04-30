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

class Readability(sentences:Seq[Sentence]) {

  /**
   * German version of Flesh-Reading-Ease (FRE) index:
   * 
   * FRE = 180 - ASL - 58.5 * ASW
   * 
   * ASL: Average Sentence Length
   * Total number of words in a certain document, divided by the number of sentences in the text.
   * 
   * ASW: Average Number of Syllables per Word
   * Total number of syllables in a certain document, divided by the number of words in the text.
   * 
   * 
   * The following evaluation of the index is used:
   * 
   * 0–30:   very difficult (university degree)
   * 30–50:  difficult  
   * 50–60:  mean-difficult  
   * 60–70:  mean  (13–15 year old pupil)
   * 70–80:  mean-easy  
   * 80–90:  easy  
   * 90–100: very easy (11 year old pupil)
   * 
   */
  def german:Double = {
    
    val sentnum = sentences.length
    val wordnum = sentences.map(s => s.terms.length).sum
    
    val syllnum = sentences.map(s => s.terms.map(t => t.syllables.length).sum).sum

    val ASL = wordnum.toDouble / sentnum
    val ASW = syllnum.toDouble / wordnum
    
    println("German ASL: " + ASL)
    println("German ASW: " + ASW)
    
    val FRE = 180 - ASL - 58.5 * ASW
    FRE
    
  }
  
  /**
   * English version of Flesh-Reading-Ease (FRE) index: http://www.leichtlesbar.ch/html/_ergebnis.html
   * 
   * FRE = 206.835 - 84.6 * ASW - 1.015 * ASL
   * 
   * ASL: Average Sentence Length
   * Total number of words in a certain document, divided by the number of sentences in the text.
   * 
   * ASW: Average Number of Syllables per Word
   * Total number of syllables in a certain document, divided by the number of words in the text.
   * 
   * 
   * The following evaluation of the index is used:
   * 
   *  0–20:  very difficult
   * 21–30:  difficult  
   * 31–40:  mean-difficult  
   * 41–60:  mean
   * 61–70:  mean-easy  
   * 71–80:  easy  
   * 81–100: very easy
   * 
   */
  
  def english:Double = {
    
    val sentnum = sentences.length
    val wordnum = sentences.map(s => s.terms.length).sum
    /*
     * Flesch-Kincaid does not take final syllables into
     * account that end with an 'e'
     */
    val syllnum = sentences.map(s => {
      s.terms.map(t => {
        
        val sylls = t.syllables
        val l = sylls.length
        
        if (sylls(l-1).endsWith("e")) {
          sylls.slice(0, l-1).length
        
        } else sylls.length
        
      }).sum
    
    }).sum

    val ASL = wordnum.toDouble / sentnum
    val ASW = syllnum.toDouble / wordnum
    
    println("English ASL: " + ASL)
    println("English ASW: " + ASW)
    
    val FRE = 206.835 - 84.6 * ASW - 1.015 * ASL
    FRE
    
  }
  
}