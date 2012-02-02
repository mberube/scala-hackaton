package seevibes

import java.util.Properties

/**
 * Created by IntelliJ IDEA.
 * User: mberube
 * Date: 12-02-02
 * Time: 3:25 PM
 * To change this template use File | Settings | File Templates.
 */

object HackatonProperties {

  val properties : Properties = new Properties()
  var initialized : Boolean = false

  private def initialize() : Unit = {
    if(initialized) {
      return
    }

    properties.load(this.getClass.getResourceAsStream("/config/config.properties"))

  }

  def username() : String = {
    initialize()
    return properties.getProperty("username")
  }
  
  def password() : String = {
    initialize()
    return properties.getProperty("password")
  }

}
