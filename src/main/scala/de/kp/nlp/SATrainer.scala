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

import java.nio.charset.Charset
import java.io.FileInputStream

import opennlp.tools.doccat.{DoccatModel,DocumentCategorizerME,DocumentSampleStream}
import opennlp.tools.util.{ObjectStream,PlainTextByLineStream}

object SATrainer {

  def apply(
    /*
     * Annotated text file (see Apache OpenNLP documentation),
     * that serves an input file for building an SA model
     */
    infile:String,
    /*
     * Binary file that specifies the SA model
     */
    outfile:String,
    /*
     * Number of iterations to build a certain SA model
     */
    iterations:Int = 100,
    /*
     * The cutoff used to specify the SA model
     */
    cutoff:Int = 0):Boolean = {
    
    val charset = Charset.forName("UTF-8")
    val lineStream = new PlainTextByLineStream(new FileInputStream(infile), charset)

    val sampleStream = new DocumentSampleStream(lineStream)
    val modelOut = new java.io.BufferedOutputStream(new java.io.FileOutputStream(outfile))
    
    try {
   
      val model = DocumentCategorizerME.train("de", sampleStream, cutoff, iterations)
      model.serialize(modelOut)
  
      true
        
    } catch {
      case e:Exception => false
      
    } finally {
    
      sampleStream.close
      if (modelOut != null) modelOut.close()      

    }
    
  }
}