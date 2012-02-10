package seevibes

import akka.actor.Actor
case object GetSize
case object Get
case class Add(word:String)
case class Palindrome(word:String)
case object PalindromeCount
case class WordCount(number:Int)
case class Word(map:Map[String, Int])

object WordCount {


  class WordCounterActor extends Actor {
    private var words = Map.empty[String,  Int]
    private var palindrome = Map.empty[String,  Int]
    def receive = {
      case Add(word) =>
        words = increment(words, word)
        if(word == word.reverse) self ! Palindrome(word)


      case GetSize =>
        self reply WordCount(words.size)

      case Get =>
        self reply Word(words)

      case Palindrome(word) =>
        palindrome = increment(palindrome, word)

      case PalindromeCount =>
        self reply palindrome.size

    }
    
    def increment(map:Map[String,  Int], word : String) : Map[String,  Int] = {
      val count = map.getOrElse(word, 0)
      return map + (word -> (count + 1))
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

    val stream = HelloTwitter.fetchTwitter(HackatonProperties.username(), HackatonProperties.password(), Array("java", "lang:en"), actor)

    Thread.sleep(20000)

    stream.shutdown()

    while(actor.dispatcher.mailboxSize(actor) > 0) {
      Thread.sleep(100)
    }



    val totalCount = (actor ? GetSize).as[WordCount]
    val palindromeCount = (actor ? PalindromeCount).as[Int]

    println("total count " + totalCount.get.number)
    println("palindrome count " + palindromeCount.get)
    println("palindrome is " + (actor ? Palindrome))
    println("palindrome % is " + ((palindromeCount.get)/(totalCount.get.number.asInstanceOf[Double])))

    Actor.registry.shutdownAll()

    Thread.sleep(100)
  }

}
