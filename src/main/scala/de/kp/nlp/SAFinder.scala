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

import com.typesafe.config.Config
import opennlp.tools.doccat.{DoccatModel,DocumentCategorizerME}

class SAModel(model:DocumentCategorizerME) {
  
  def find(text:String):String = {
    
    val outcome = model.categorize(text)
    model.getBestCategory(outcome) 
    
  }
  
}

object SAFinder {

  def apply(config:Config):SAModel = {
    loadModel(config)
  }
  
  private def loadModel(config:Config):SAModel = {
    
    val fis = getClass.getClassLoader.getResourceAsStream(config.getString("sentiment"))
    val categorizer = try {
      
      val model = new DoccatModel(fis)
      new SAModel(new DocumentCategorizerME(model))
      
    } catch {
      case e:Exception => {
        null
      }
      
    } finally {
  
      if (fis != null) {
        try {
          fis.close()
        
        } catch {
          case e:Exception => 
        }
    
      }
  
    }

    categorizer
    
  } 

}