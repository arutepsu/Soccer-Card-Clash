package de.htwg.se.soccercardclash.module

import com.google.inject.{AbstractModule, Guice}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.soccercardclash.model.fileIOComponent.IFileIO
import de.htwg.se.soccercardclash.model.fileIOComponent.base.FileIO
import de.htwg.se.soccercardclash.model.fileIOComponent.jSONComponent.JsonComponent
import de.htwg.se.soccercardclash.model.fileIOComponent.xmlComponent.XmlComponent
import de.htwg.se.soccercardclash.module.FileIOModule
import org.scalatestplus.mockito.MockitoSugar
object TestUtils extends MockitoSugar
import TestUtils.mock


class FileIOModuleSpec extends AnyWordSpec with Matchers {

  // Override module with mocks for JsonComponent and XmlComponent
  class MockedFileIOModule extends FileIOModule {
    override def configure(): Unit = {
      val mockJson = mock[JsonComponent]
      val mockXml = mock[XmlComponent]

      bind(classOf[JsonComponent]).toInstance(mockJson)
      bind(classOf[XmlComponent]).toInstance(mockXml)

      super.configure()
    }
  }

  "FileIOModule" should {

    "bind IFileIO to FileIO as singleton" in {
      val injector = Guice.createInjector(new MockedFileIOModule)
      val instance1 = injector.getInstance(classOf[IFileIO])
      val instance2 = injector.getInstance(classOf[IFileIO])

      instance1 shouldBe a[FileIO]
      instance1 shouldBe theSameInstanceAs(instance2)
    }
  }
}
