import java.io.{File, FileInputStream, InputStream}

import ddf.minim.{AudioOutput, Minim}
import ddf.minim.javasound.JSMinim
import ddf.minim.spi.{AudioOut, MinimServiceProvider}
import ddf.minim.ugens.{ADSR, Instrument, Oscil, Waves}

object Glisando extends App {

  val fileLoader = FileLoaderUserHome("minim/glisando")
  val serviceProvider: MinimServiceProvider = new JSMinim(fileLoader)
  val minim = new Minim(serviceProvider)
  val out = minim.getLineOut()

  out.pauseNotes()
  out.playNote(0, 1.5f, Inst(out))
  out.resumeNotes()

  closeAfter(3)

  private def closeAfter(seconds: Int): Unit = {
    Thread.sleep(seconds * 1000)
    out.close()
    println("closed audio output after %d s" format seconds)
  }


  case class Inst(out: AudioOutput) extends Instrument {

    val oscil = new Oscil(333, 0.3f, Waves.SINE)
    val adsr = new ADSR(1f, 0.1f, 0.0f, 1f, 0.5f)


    oscil.patch(adsr)

    override def noteOn(duration: Float): Unit = {
      adsr.patch(out)
      adsr.noteOn()
    }

    override def noteOff(): Unit = {
      adsr.noteOff()
      adsr.unpatchAfterRelease(out)
    }

  }
  case class FileLoaderUserHome(path: String) {

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

    def getCreateFile(fileName: String): File = {
      val home = new File(System.getProperty("user.home"))
      val outDir = new File(home, path)
      outDir.mkdirs()
      new File(outDir, fileName)
    }
  }



}
