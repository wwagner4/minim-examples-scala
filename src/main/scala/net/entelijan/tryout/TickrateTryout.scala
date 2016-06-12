package net.entelijan.tryout

import java.io.{File, FileInputStream, InputStream}

import ddf.minim.Minim
import ddf.minim.ugens._

/**
  * Load a sample and apply the tickrate ugen to it.
  */
object TickrateTryout extends App {

  println("Tickrate with Sample")

  val fileLoader = FileLoaderResources
  println("Fileloader created")

  val minim = new Minim(fileLoader)
  val out = minim.getLineOut()

  out.playNote(0f, 1f, new Inst(1.5f))
  out.playNote(0.5f, 1f, new Inst(0.5f))
  out.playNote(1f, 1f, new Inst(1.0f))
  out.playNote(1.5f, 1f, new Inst(0.8f))
  out.playNote(2f, 1f, new Inst(1.2f))

  Thread.sleep(5000)
  out.close()
  println("Closed audio output")

  case class Inst(rate: Double) extends Instrument {

    val sampler = new Sampler("h1.wav", 4, minim)
    val tickrate = new TickRate(rate.toFloat)

    sampler.patch(tickrate)

    override def noteOn(duration: Float): Unit = {
      tickrate.patch(out)
      sampler.trigger
    }

    override def noteOff(): Unit = {
      sampler.stop()
      tickrate.unpatch(out)
    }
  }

  case object FileLoaderResources {

    val path: File = new File("src/main/resources/")
    require(path.exists(), "Path %s must exist" format path.getAbsolutePath)
    require(path.isDirectory, "Path %s must be a directory" format path.getAbsolutePath)

    def sketchPath(fileName: String): String = {
      new File(path, fileName).getAbsolutePath
    }

    def createInput(fileName: String): InputStream = {
      val file = new File(path, fileName)
      require(file.exists(), "File %s does not exist" format file.getAbsolutePath)
      new FileInputStream(file)
    }
  }

}
