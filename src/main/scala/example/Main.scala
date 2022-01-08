package example

object Main{
  def main(args: Array[String]) {

    var loop = true
    println()
    println()
    println()
    println("SUCCESS")
    println()
    println()
    println()

    do{
    //print("\u001b[2J")
    println("Please select information to display:")
    println("1. Run Producer" +
          "\n2. Consumer" +
          "\n3. Spark Consumer" +
          "\n4. FlatDF" +
          "\n5. Parsed Consumer" +
          "\n6. Console Consumer" +
          "\n7. Join Method Consumer" +
          "\n8. Quit Application")
    try {
      
      val option = scala.io.StdIn.readInt()
      //print("\u001b[2J")
      println()
      option match{
        case 1 => {
          val producer = new ProducerClass
          println("I AM THE PRODUCER")
          producer.produce
        }
        case 2 => {
          val consumer = new ConsumerClass
          println("I AM THE CONSUMER")
          consumer.consume()
        }
        case 3 => {
          val sparkConsumer = new SparkConsumerClass
          println("I AM THE SPARK CONSOLE CONSUMER")
          sparkConsumer.console
          
        }
        case 4 => {
          val sparkConsumer = new SparkConsumerClass
          println("I AM THE FLATTENED DF")
          val flatDF = sparkConsumer.flattenRecord()
          sparkConsumer.printToConsole(flatDF)
          
        }
        case 5 => {
          val sparkConsumer = new SparkConsumerClass
          println("I AM THE SPARK PARSED CONSUMER")
          sparkConsumer.refineData()
        }
        case 6 => {
          val consumer = new ConsumerClass
          println("I AM THE CONSOLE CONSUMER")
          consumer.consoleConsumer()
        }
        case 7 => {
          loop = false
        }
      }//End of 'match'
    }catch {
      case e: MatchError => println("Please pick a number between 0~5\n")
      case e: NumberFormatException => println("\nPlease enter a number\n") 
    }
  
  } while(loop) 
   
    
     

    

 }//End of Main
}//End of Object 

