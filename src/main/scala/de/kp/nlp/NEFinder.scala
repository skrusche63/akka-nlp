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
import opennlp.tools.namefind.{NameFinderME,TokenNameFinderModel}

import scala.collection.mutable.Map

object NEParams {
  
  val PERSON:String   = "person"
  val LOCATION:String = "location"
  
  val ORGANIZATION:String = "organization" 
  
}

class NEModel(val models:Map[String,NameFinderME]) {
  
  def find(tokens:Array[String],category:String):Seq[Entity] = {
    
    val model = models(category)
    
    val names = model.find(tokens)
    val probs = model.probs(names)
    
    names.map(name => {
     
      val text = tokens.slice(name.getStart, name.getEnd).mkString(" ")
      val cate = name.getType

      (text,cate)
    
    }).zip(probs).map{case ((text,cate),prob) => Entity(text,cate,prob)}
    
    
  }
  
  def clear() {
    
     models(NEParams.PERSON).clearAdaptiveData()
     models(NEParams.LOCATION).clearAdaptiveData()

     models(NEParams.ORGANIZATION).clearAdaptiveData()

  }
  
}

object NEFinder {
  
  def apply(config:Config):NEModel = {

    /* Load person finder */
    val perFinder = loadModel(config,NEParams.PERSON)    
    if (perFinder == null) return null

    /* Load location finder */
    val locFinder = loadModel(config,NEParams.LOCATION)    
    if (locFinder == null) return null

    /* Load organization finder */
    val orgFinder = loadModel(config,NEParams.ORGANIZATION)    
    if (orgFinder == null) return null
    
    new NEModel(Map(
      NEParams.PERSON       -> perFinder,
      NEParams.LOCATION     -> locFinder, 
      NEParams.ORGANIZATION -> orgFinder
    ))
    
  }

  private def loadModel(config:Config,category:String):NameFinderME = {
    
    val fis = getClass.getClassLoader.getResourceAsStream(config.getString(category))
    val nameFinder = try {
      
      val model = new TokenNameFinderModel(fis)
      new NameFinderME(model)
      
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

    nameFinder
    
  } 
  
}