package valium.benchmark
import org.scalameter.api._
import org.scalameter.execution.SeparateJvmsExecutor

trait MultiValueStorage {
  var val1: Long = 0
  var val2: Long = 0
  var val3: Long = 0
  var val4: Long = 0
  var val5: Long = 0

  var ref1: Object = _
  var ref2: Object = _
  var ref3: Object = _
  var ref4: Object = _
  var ref5: Object = _
}

class BasicMultiValueStorage extends MultiValueStorage

object MultivalueBenchmark extends PerformanceTest {

  lazy val executor = LocalExecutor(
    new Executor.Warmer.Default,
    Aggregator.median,
    new Measurer.Default)
  lazy val reporter = new LoggingReporter
  lazy val persistor = Persistor.None

  case class Three(a: Long, b: Long, c: Long)
  case class SumProd(sum: Long, prod: Long)

  val NumIterations = 1000000

  var r: SumProd = SumProd(1,2)

  def fCaseClass(x: Three): SumProd = SumProd(x.a+x.b+x.c, x.a*x.b*x.c)

  performance of "Case class" in  {
    using(Gen.unit("")) in {
      (_) => {
        var i = NumIterations
        while (i > 0) {
          r = fCaseClass(Three(r.sum, r.prod, r.sum + r.prod))
          i -= 1
        }
      }
    }
  }

  // Using ThreadLocal
  lazy val _storage = new ThreadLocal[MultiValueStorage]
  def getStorage(): MultiValueStorage = {
    val s = _storage.get()
    if (s != null) {
      s
    } else {
      val ns = new BasicMultiValueStorage
      _storage.set(ns)
      ns
    }
  }

  var rs1: Long = 1
  var rp1: Long = 2

  def fMultivalueReturn(val1: Long, val2: Long, val3: Long): MultiValueStorage = {
    val a = val1
    val b = val2
    val c = val3

    val s = getStorage()
    s.val1 = a+b+c
    s.val2 = a*b*c

    s
  }

  performance of "MultivalueReturn" in {
    using(Gen.unit("")) in {
      (_) => {
        var i = NumIterations
        while (i > 0) {
          val val1 = rs1
          val val2 = rp1
          val val3 = rs1 + rp1

          val s2 = fMultivalueReturn(val1, val2, val3)

          rs1 = s2.val1
          rp1 = s2.val2

          i -= 1
        }
      }
    }
  }

  var rs2: Long = 1
  var rp2: Long = 2

  def fMultivalue(s: MultiValueStorage): MultiValueStorage = {
    val a = s.val1
    val b = s.val2
    val c = s.val3

    s.val1 = a+b+c
    s.val2 = a*b*c

    s
  }

  performance of "Multivalue" in {
    using(Gen.unit("")) in {
      (_) => {
        var i = NumIterations
        while (i > 0) {
          val s1 = getStorage()
          s1.val1 = rs2
          s1.val2 = rp2
          s1.val3 = rs2 + rp2

          val s2 = fMultivalue(s1)

          rs2 = s2.val1
          rp2 = s2.val2

          i -= 1
        }
      }
    }
  }

  performance of "Multivalue Reuse" in {
    using(Gen.unit("")) in {
      (_) => {
        var i = NumIterations
        val s = getStorage()
        while (i > 0) {
          val s1 = s
          s1.val1 = rs2
          s1.val2 = rp2
          s1.val3 = rs2 + rp2

          val s2 = fMultivalue(s1)

          rs2 = s2.val1
          rp2 = s2.val2

          i -= 1
        }
      }
    }
  }

//  var ov3: Long = 0
//  var ov4: Long = 0
//  var ov5: Long = 0
//  var or1: Object = _
//  var or2: Object = _
//  var or3: Object = _
//  var or4: Object = _
//  var or5: Object = _
//  val ref: Object = ""
//
//  performance of "Multivalue Generic Overhead 5" in {
//    using(Gen.unit("")) in {
//      (_) => {
//        var i = NumIterations
//        while (i > 0) {
//          val s1 = getStorage()
//          s1.val1 = rs
//          s1.val2 = rp
//          s1.val3 = rs + rp
//
//          // Generic Overhead
//          s1.val4 = -1
//          s1.val5 = -1
//          s1.ref1 = ref
//          s1.ref2 = ref
//          s1.ref3 = ref
//          s1.ref4 = ref
//          s1.ref5 = ref
//
//          val s2 = fMultivalue(s1)
//
//          rs = s2.val1
//          rp = s2.val2
//
//          // Generic Overhead
//          ov3 = s2.val3
//          ov4 = s2.val4
//          ov5 = s2.val5
//          or1 = s2.ref1
//          or2 = s2.ref2
//          or3 = s2.ref3
//          or4 = s2.ref4
//          or5 = s2.ref5
//
//          i -= 1
//        }
//      }
//    }
//  }
//
//  performance of "Multivalue Generic Overhead 5 Reuse" in {
//    using(Gen.unit("")) in {
//      (_) => {
//        var i = NumIterations
//        val s = getStorage()
//        while (i > 0) {
//          val s1 = s
//          s1.val1 = rs
//          s1.val2 = rp
//          s1.val3 = rs + rp
//
//          // Generic Overhead
//          s1.val4 = -1
//          s1.val5 = -1
//          s1.ref1 = ref
//          s1.ref2 = ref
//          s1.ref3 = ref
//          s1.ref4 = ref
//          s1.ref5 = ref
//
//          val s2 = fMultivalue(s1)
//
//          rs = s2.val1
//          rp = s2.val2
//
//          // Generic Overhead
//          ov3 = s2.val3
//          ov4 = s2.val4
//          ov5 = s2.val5
//          or1 = s2.ref1
//          or2 = s2.ref2
//          or3 = s2.ref3
//          or4 = s2.ref4
//          or5 = s2.ref5
//
//          i -= 1
//        }
//      }
//    }
//  }

}
