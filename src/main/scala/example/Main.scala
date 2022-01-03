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
    print("\u001b[2J")
    println("Please select information to display:")
    println("1. Run Producer" +
          "\n2. Consumer" +
          "\n3. -" +
          "\n4. -" +
          "\n5. Quit Application")
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
        
        }
        case 4 => {
          
        }
        case 5 => {
          loop = false
        }
      }//End of 'match'
    }catch {
      case e: MatchError => println("Please pick a number between 0~5\n")
      case e: NumberFormatException => println("\nPlease enter a number\n") 
    }
  
  } while(loop) 
   
    val producer = new ProducerClass
    var data = producer.produce
     

    

 }//End of Main
}//End of Object 

