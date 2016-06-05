package net.entelijan.tryout

import java.io.{File, FileInputStream, InputStream}

import ddf.minim.javasound.JSMinim
import ddf.minim.spi.MinimServiceProvider
import ddf.minim.ugens._
import ddf.minim.{AudioOutput, Minim}

object Glisando extends App {

  sealed trait Dir

  case object Dir_Up extends Dir

  case object Dir_Down extends Dir

  case class Note(time: Double, dur: Double, freq: Double, dir: Dir)

  val fileLoader = FileLoaderUserHome("minim/glisando")
  val serviceProvider: MinimServiceProvider = new JSMinim(fileLoader)
  val minim = new Minim(serviceProvider)
  val out = minim.getLineOut()

  val f1 = 4.0 / 3.0
  val f2 = 3.0 / 2.0

  val basePitch = 700.0
  val pitches = List(basePitch / f1, basePitch / f2, basePitch, basePitch * f1, basePitch * f2)

  val notes = List(
    Note(0, 1.0, pitches(0), Dir_Up),
    Note(1, 2.0, pitches(1), Dir_Down),
    Note(3, 0.5, pitches(4), Dir_Down),
    Note(4, 1.0, pitches(3), Dir_Up),
    Note(5, 1.0, pitches(1), Dir_Up),
    Note(5.5, 2.0, pitches(0), Dir_Down),
    Note(6, 0.5, pitches(1), Dir_Down),
    Note(6.5, 1.0, pitches(3), Dir_Up),
    Note(7, 1.0, pitches(4), Dir_Up),
    Note(7.5, 2.0, pitches(2), Dir_Down),
    Note(8, 0.5, pitches(1), Dir_Down),
    Note(9, 1.0, pitches(0), Dir_Down))

  out.pauseNotes()
  notes.foreach {
    case Note(time, dur, freq, dir) =>
      def inst(freq: Double, dir: Dir): Inst = dir match {
        case Dir_Up => Up(freq, out)
        case Dir_Down => Down(freq, out)
      }
      out.playNote(time.toFloat, dur.toFloat, inst(freq, dir))
  }
  out.resumeNotes()

  closeAfter(14)

  private def closeAfter(seconds: Int): Unit = {
    Thread.sleep(seconds * 1000)
    out.close()
    println("closed audio output after %d s" format seconds)
  }

  trait Inst extends Instrument {

    def freq: Double

    def freqFact: Double

    def out: AudioOutput

    val oscil = new Oscil(0f, 0.3f, Waves.SQUARE)
    val adsr = new ADSR(1f, 0.05f, 0.5f, 0.1f, 0.5f)

    val line = new Line(2f, freq.toFloat, freq.toFloat * freqFact.toFloat)

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

  case class Up(freq: Double, out: AudioOutput) extends Inst {
    override def freqFact: Double = 1.3
  }

  case class Down(freq: Double, out: AudioOutput) extends Inst {
    override def freqFact: Double = 0.7
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

    private def getCreateFile(fileName: String): File = {
      val home = new File(System.getProperty("user.home"))
      val outDir = new File(home, path)
      outDir.mkdirs()
      new File(outDir, fileName)
    }
  }


}
