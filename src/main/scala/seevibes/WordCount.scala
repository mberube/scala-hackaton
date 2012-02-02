package seevibes

import akka.actor.Actor


object WordCount {
  case object GetSize
  case object Get
  case class Add(word:String)
  case class WordCount(number:Int)
  case class Word(map:Map[String, Int])

  class WordCounterActor extends Actor {
    private var words = Map.empty[String,  Int]
    def receive = {
      case Add(word) =>
        val count = words.get(word).getOrElse(0)
        words = words + (word -> (count + 1))

      case GetSize =>
        self reply WordCount(words.size)

      case Get =>
        self reply Word(words)
    }
  }

  def main(args: Array[String]) {
    val actor = Actor.actorOf(new WordCounterActor).start()
    actor ! Add("patate")
    actor ! Add("patate")
    actor ! Add("patate")
    actor ! Add("patate2")
    val size = actor !! GetSize
    


    println("size of map is " + size)
    
    val words = actor !! Get
    println("current words is " + words)
    Actor.registry.shutdownAll()
  }
}
