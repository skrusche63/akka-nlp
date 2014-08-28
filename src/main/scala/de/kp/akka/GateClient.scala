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

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

import akka.pattern.ask

import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.duration.Duration._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class GateClient {

  private val name = "gate-client"
  private val conf = "client.conf"
    
  private val url = "akka.tcp://gate-server@127.0.0.1:2600/user/gate-master"
      
  implicit val timeout = Timeout(5.seconds)
    
  private val system = ActorSystem(name, ConfigFactory.load(conf))
  private val remote = system.actorSelection(url)

  def send(req:Any):Future[Any] = ask(remote, req)    
  def shutdown() = system.shutdown

}