package module
import com.google.inject.{AbstractModule, Singleton}
import model.fileIOComponent.IFileIO
import model.fileIOComponent.base.FileIO
import model.fileIOComponent.xmlComponent.XmlComponent
import model.fileIOComponent.jSONComponent.JsonComponent


class FileIOModule extends AbstractModule {
  
  override def configure(): Unit = {
    bind(classOf[IFileIO]).to(classOf[FileIO]).asEagerSingleton()

    bind(classOf[FileIO])
      .toConstructor(classOf[FileIO].getConstructor(classOf[JsonComponent], classOf[XmlComponent]))
      .in(classOf[Singleton])
  }
}
