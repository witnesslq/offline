package exercises

import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by fengwu on 15/1/27.
 */
object Chapter3 {
    // exercise 1
    def randArr(n: Int) = {
        (for (i <- 1 to n) yield util.Random.nextInt(n)).toArray
    }

    // exercise 2
    def swapPairsInPlace(arr: Array[Int]) = {
        for (i <- 0 until(if (arr.length % 2 == 0) arr.length else arr.length - 1, 2)) {
            val tmp = arr(i)
            arr(i) = arr(i + 1)
            arr(i + 1) = tmp
        }
        arr
    }

    // exercise 3
    def swapPairs(arr: Array[Int]) = {
        (for (i <- 0 until arr.length)
            yield if (i == arr.length - 1 & i % 2 == 0) arr(i) else if (i % 2 == 0) arr(i + 1) else arr(i - 1)).toArray
    }

    // exercise 4
    def posThenNeg(arr: Array[Int]) = {
        Array.concat(arr.filter(_ > 0), arr.filter(_ <= 0))
    }


    def main(args: Array[String]): Unit = {
        val a = randArr(10)
        val b = Array(-1, 2, -3, 0, 4, 10)
        println(a.toList)
        //println(swapPairsInPlace(a).toList)
        //println(swapPairs(a).toList)
        //println(posThenNeg(b).toBuffer.sortWith(_ > _))

        val conf = new SparkConf()

        var sc = new SparkContext(conf)
    }
}
