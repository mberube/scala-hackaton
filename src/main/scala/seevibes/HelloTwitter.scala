package seevibes

import twitter4j.conf.ConfigurationBuilder
import twitter4j._
import akka.actor.{ActorRef, Actors}

object HelloTwitter {

  class StreamListener(val actor:ActorRef)
    extends StatusListener
    with RateLimitStatusListener
    with ConnectionLifeCycleListener {

    def onRateLimitStatus(event: RateLimitStatusEvent) {
      println("Rate Limit Status: " + event)
    }

    def onRateLimitReached(event: RateLimitStatusEvent) {
      println("Rate Limit Reached: " + event)
    }

    def onStatus(status: Status) {
      println("@" + status.getUser.getScreenName + " : " + status.getText)

      status.getText.split(" ").foreach(e => actor ! Add(e))
      val dictionary = actor !! Get

      println("got words " + dictionary)
    }

    def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) {}

    def onTrackLimitationNotice(numberOfLimitedStatuses: Int) {}

    def onScrubGeo(userId: Long, upToStatusId: Long) {}

    def onException(ex: Exception) {
      println(ex)
    }

    def onConnect() {
      println("Connected")
    }

    def onDisconnect() {
      println("Disconnected")
    }

    def onCleanUp() {
      println("Clean up")
    }
  }

  private def setupListeners(stream: TwitterStream, actor: ActorRef) {
    val listener = new StreamListener(actor)
    stream.addListener(listener)
    stream.addRateLimitStatusListener(listener)
    stream.addConnectionLifeCycleListener(listener)
  }

  def fetchTwitter(username: String, password: String, terms : Array[String], actor: ActorRef) = {
    val stream = buildStream(username, password)
    setupListeners(stream, actor)

    val query = new FilterQuery
    query.setIncludeEntities(true)
    query.track(terms)
    stream.filter(query)
    stream
  }

  def main(args: Array[String]) {
    if (args.length < 3) {
      println("USAGE: scala seevibes.HelloWorldTest TWITTER_USERNAME TWITTER_PASSWORD KEYWORD1 [KEYWORD2 [KEYWORDn...]]")
      println("Received arguments are: \"" + args.mkString("\", \"") + "\"")
      System.exit(1)
    }

    fetchTwitter(args(0), args(1), args.drop(2), null)

    Thread.sleep(10000)
    Actors.registry().shutdownAll()
    Thread.sleep(100)
  }

  private def buildStream(username: String, password: String) = {
    val cb = new ConfigurationBuilder
    cb.setUser(username)
      .setPassword(password)
      .setIncludeEntitiesEnabled(true)
      .setDebugEnabled(true)

    new TwitterStreamFactory(cb build).getInstance()
  }
}
