package net.entelijan.tryout

import java.io.{File, FileInputStream, InputStream}

import ddf.minim.Minim
import ddf.minim.ugens._

/**
  * Load a sample and apply the tickrate ugen to it.
  */
object DelayTryout extends App {

  println("Tickrate with Sample")

  val fileLoader = FileLoaderResources
  println("Fileloader created")

  val minim = new Minim(fileLoader)
  val out = minim.getLineOut()

  out.playNote(0f, 1f, Inst)
  out.playNote(2f, 1f, Inst)
  out.playNote(4f, 1f, Inst)

  Thread.sleep(5000)
  out.close()
  println("Closed audio output")

  case object Inst extends Instrument {

    val sampler = new Sampler("h1.wav", 3, minim)
    val delay1 = new Delay(0.1f, 0.5f)
    val delay2 = new Delay(0.2f, 0.2f)
    val delay3 = new Delay(0.3f, 0.1f)
    val sum = new Summer

    sampler.patch(delay1)
    sampler.patch(delay2)
    sampler.patch(delay3)

    delay1.patch(sum)
    delay2.patch(sum)
    delay3.patch(sum)

    override def noteOn(duration: Float): Unit = {
      sum.patch(out)
      sampler.begin
    }

    override def noteOff(): Unit = {
      sampler.end
      sum.unpatch(out)
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
      new FileInputStream(file)
    }
  }

}
