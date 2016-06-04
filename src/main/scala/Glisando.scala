import java.io.{File, FileInputStream, InputStream}

import ddf.minim.Minim
import ddf.minim.javasound.JSMinim
import ddf.minim.spi.MinimServiceProvider

object Glisando extends App {

  val  fileLoader = new FileLoaderUserHome();
  val serviceProvider: MinimServiceProvider = new JSMinim(fileLoader);
  val minim = new Minim(serviceProvider);
  System.out.println("Created minim: " + minim);
  val out = minim.getLineOut();

  Thread.sleep(1000)
  out.close()
  println("closed after 1000ms")

  class FileLoaderUserHome {

    def sketchPath(fileName: String) = {
      val file = getCreateFile(fileName)
      file.getAbsolutePath
    }

    def createInput(fileName: String): InputStream = {
      try {
        new FileInputStream(fileName)
      } catch {
        case e: Exception =>
          throw new IllegalStateException("Error creating input stream. " + e.getMessage)
      }
    }
  }

  def getCreateFile(fileName: String): File = {
    val home = new File(System.getProperty("user.home"))
    val outDir = new File(home, "minim")
    outDir.mkdirs()
    new File(outDir, fileName)
  }

}
