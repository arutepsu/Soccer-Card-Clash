package de.htwg.se.soccercardclash.module

import com.google.inject.*
import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.model.fileIOComponent.IFileIO
import de.htwg.se.soccercardclash.model.fileIOComponent.jSONComponent.JsonComponent
import de.htwg.se.soccercardclash.model.fileIOComponent.xmlComponent.XmlComponent
import de.htwg.se.soccercardclash.model.gameComponent.service.GameDeserializer
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.factory.IMementoManagerFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class SoccerCardClashModuleSpec extends AnyWordSpec with Matchers {

  "SoccerCardClashModule" should {

    "successfully inject all major components" in {
      val injector = Guice.createInjector(new SoccerCardClashModule())

      val controller = injector.getInstance(classOf[IController])
      val fileIO = injector.getInstance(classOf[IFileIO])
      val json = injector.getInstance(classOf[JsonComponent])
      val xml = injector.getInstance(classOf[XmlComponent])
      val deserializer = injector.getInstance(classOf[GameDeserializer])
      val mementoFactory = injector.getInstance(classOf[IMementoManagerFactory])

      controller should not be null
      fileIO should not be null
      json should not be null
      xml should not be null
      deserializer should not be null
      mementoFactory should not be null
    }
  }
}
