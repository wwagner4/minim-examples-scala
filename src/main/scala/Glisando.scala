import java.io.{File, FileInputStream, InputStream}

import ddf.minim.Minim
import ddf.minim.javasound.JSMinim
import ddf.minim.spi.MinimServiceProvider

object Glisando extends App {

  val fileLoader = new FileLoaderUserHome()
  val serviceProvider: MinimServiceProvider = new JSMinim(fileLoader)
  val minim = new Minim(serviceProvider)
  val out = minim.getLineOut()

  out.pauseNotes()
  out.playNote(400)
  out.resumeNotes()

  closeAfter(3)

  private def closeAfter(seconds: Int): Unit = {
    Thread.sleep(seconds * 1000)
    out.close()
    println("closed after %d s" format seconds)
  }


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
