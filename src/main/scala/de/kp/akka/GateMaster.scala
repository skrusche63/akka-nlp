package de.kp.akka
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

import akka.actor.{Actor,ActorLogging,ActorRef,Props}

import akka.pattern.ask
import akka.util.Timeout

import akka.actor.{OneForOneStrategy, SupervisorStrategy}
import akka.routing.RoundRobinRouter

import com.typesafe.config.ConfigFactory

import scala.concurrent.duration.DurationInt

class GateMaster extends Actor with ActorLogging {

  /**
   * Construct AnnieWrapper
   */
  val path = "application.conf"
  val config = ConfigFactory.load(path)

  val home = config.getConfig("gate").getString("home")
  val gate = new AnnieWrapper(home)  
  
  val routerCfg = config.getConfig("router")
  
  val retries = routerCfg.getInt("retries")
  val time = routerCfg.getInt("time")
  
  val workers = routerCfg.getInt("workers")
  
  override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries=retries,withinTimeRange = DurationInt(time).minutes) {
    case _ : Exception => SupervisorStrategy.Restart
  }

  val router = context.actorOf(Props(new GateWorker(gate)).withRouter(RoundRobinRouter(workers)), name="gate-router")
    
  def receive = {
    
    case req:String => {

      implicit val ec = context.dispatcher
      implicit val timeout:Timeout = 1.second

	  val origin = sender

	  val response = ask(router, req).mapTo[Seq[Map[String,String]]]
      response.onSuccess {
        case result => origin ! result       
      }
      response.onFailure {
        case result => origin ! Seq.empty[Map[String,String]]	      
	  }
     
    }
    
    case _ => log.info("Unknown request")
  
  }

}