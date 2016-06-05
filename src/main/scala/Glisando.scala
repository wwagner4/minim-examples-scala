import java.io.{File, FileInputStream, InputStream}

import ddf.minim.javasound.JSMinim
import ddf.minim.spi.MinimServiceProvider
import ddf.minim.ugens._
import ddf.minim.{AudioOutput, Minim}

object Glisando extends App {

  val fileLoader = FileLoaderUserHome("minim/glisando")
  val serviceProvider: MinimServiceProvider = new JSMinim(fileLoader)
  val minim = new Minim(serviceProvider)
  val out = minim.getLineOut()

  out.pauseNotes()


  sealed trait Dir

  case object Dir_Up extends Dir

  case object Dir_Down extends Dir

  case class Note(time: Double, freq: Double, dir: Dir)

  val fl = List(1.0, 1.5, 0.4, 1.2).foldLeft(List.empty[Double])((cuml, fact) => cuml match {
    case Nil => 500 * fact :: cuml
    case freq :: _ => freq * fact :: cuml
  }).reverse

  println(fl)
  val notes = List(
    Note(0, fl(0), Dir_Up),
    Note(1, fl(1), Dir_Down),
    Note(3, fl(2), Dir_Down),
    Note(4, fl(3), Dir_Up))

  notes.foreach {
    case Note(time, freq, Dir_Up) => out.playNote(time.toFloat, 1.5f, Up(freq, out))
    case Note(time, freq, Dir_Down) => out.playNote(time.toFloat, 1.5f, Down(freq, out))
  }

  out.resumeNotes()

  closeAfter(8)

  private def closeAfter(seconds: Int): Unit = {
    Thread.sleep(seconds * 1000)
    out.close()
    println("closed audio output after %d s" format seconds)
  }


  case class Up(freq: Double, out: AudioOutput) extends Inst {
    override def freqFact: Double = 1.3
  }

  case class Down(freq: Double, out: AudioOutput) extends Inst {
    override def freqFact: Double = 0.7
  }

  trait Inst extends Instrument {

    def freq: Double

    def freqFact: Double

    def out: AudioOutput

    val oscil = new Oscil(0f, 0.3f, Waves.SINE)
    val adsr = new ADSR(1f, 0.1f, 0.0f, 1f, 0.5f)

    val line = new Line(4f, freq.toFloat, freq.toFloat * freqFact.toFloat)

    line.patch(oscil.frequency)


    oscil.patch(adsr)

    override def noteOn(duration: Float): Unit = {
      adsr.patch(out)
      adsr.noteOn()
      line.activate()
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
