package example

object Main{
  def main(args: Array[String]) {

    var loop = true
    var producerRunning = false
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
          "\n2. Parsed Consumer" +
          "\n3. Write to Parquet" +
          "\n4. FlatDF" +
          "\n5. " +
          "\n6. " +
          "\n7. Quit Application" +
          "\n8. ")
    try {
      
      val option = scala.io.StdIn.readInt()
      //print("\u001b[2J")
      println()
      option match{
        case 1 => {
          producerRunning = true
          val producer = new ProducerClass
          println("I AM THE PRODUCER")
          producer.produce
        }
        case 2 => {
          val sparkConsumer = new SparkConsumerClass
          println("I AM THE SPARK PARSED CONSUMER")
          val df = sparkConsumer.refineData()
          sparkConsumer.printToConsole(df)
        }
        case 3 => {
          val sparkConsumer = new SparkConsumerClass
          println("I AM THE PARQUET WRITER")
          val df = sparkConsumer.refineData()
          sparkConsumer.writeToParquet(df)
          
        }
        case 4 => {
          val sparkConsumer = new SparkConsumerClass
          println("I AM THE FLATTENED DF")
          val flatDF = sparkConsumer.flattenRecord()
          sparkConsumer.printToConsole(flatDF)
          
        }
        case 5 => {
          
        }
        case 6 => {
          
        }
        case 7 => {
          loop = false
          if(producerRunning == true){
            println()
            println()
            println()
            println("Press Ctrl+C to stop the producer and completely shut down application")
          }//End of 'if(producerRunning == true)'
        }//End of 'case 7'
      }//End of 'match'
    }catch {
      case e: MatchError => println("Please pick a number between 0~5\n")
      case e: NumberFormatException => println("\nPlease enter a number\n") 
    }
  
  } while(loop) 
   
    
  

    

 }//End of Main
}//End of Object 

