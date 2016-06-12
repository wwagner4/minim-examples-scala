package net.entelijan.tryout

/**
  * Experimenting with frequencies
  */
object TwelveTones extends App {

  _12


  def fac: Unit = {
    for (x <- 3 to 30) {
      for (y <- 1 to x) {
        val z = x.toDouble / y
        val zs = if (z > 1.0 && z < 2.0) "%10.4f" format z else "-"
        println("%d / %d = %s" format(x, y, zs))
      }
    }
  }

  def _12: Unit = {
    val f12 = math.pow(2, 1.0 / 12)
    println("fk:%.4f%n" format f12)

    val f32 = 3.0 / 2.0 // Quint
    val f43 = 4.0 / 3.0 // Quart
    val f53 = 5.0 / 3.0
    val f54 = 5.0 / 4.0
    val f65 = 6.0 / 5.0
    val f74 = 7.0 / 4.0
    val f75 = 7.0 / 5.0
    val f76 = 7.0 / 6.0



    var b = 1.0
    for (i <- 0 to 25) {
      println("(%4d)%10.6f %10s %10s %10s %10s %10s %10s %10s %10s" format(i, b,
        format(b, f32), format(b, f43), format(b, f53), format(b, f54),
        format(b, f65), format(b, f74), format(b, f75), format(b, f76)))
      b = b * f12
    }
  }

  def format(freq: Double, fac: Double): String = {
    val diff = math.abs(freq - fac)
    if (diff < 0.02) "%.5f" format diff
    else "-"
  }


}
