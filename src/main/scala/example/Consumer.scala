package example

//Kafka Imports
import java.util.{Collections, Properties}
import org.apache.kafka.clients.consumer.KafkaConsumer
import scala.collection.JavaConverters._

//Json Imports
import net.liftweb.json.DefaultFormats
import net.liftweb.json._


class ConsumerClass {

  val props: Properties = new Properties()
  props.put("group.id", "init_consumer")
  // props.put("bootstrap.servers","localhost:9092")
  props.put("bootstrap.servers", "sandbox-hdp.hortonworks.com:6667")
  props.put(
    "key.deserializer",
    "org.apache.kafka.common.serialization.StringDeserializer"
  )
  props.put(
    "value.deserializer",
    "org.apache.kafka.common.serialization.StringDeserializer"
  )
  props.put("enable.auto.commit", "true")
  props.put("auto.commit.interval.ms", "1000")
  val consumer = new KafkaConsumer(props)
  val topics = List("OpenWeather_OneCall")
  
  def consume(){
    implicit val formats = DefaultFormats
    var jsonString: String =""
    var latitude: Double = 0
    var longitude: Double = 0
    var weather_id: Int = 0
    var weather_main: String = ""
    var weather_description: String = ""
    var main_temp: Double = 0
    var main_feelsLike: Double = 0
    var main_tempMin: Double = 0
    var main_tempMax: Double = 0
    var main_pressure: Double = 0
    var main_humidity: Double = 0
    var wind_speed: Double = 0
    var name: String = ""

    
    try {
      consumer.subscribe(topics.asJava)
      while (true) {
        val records = consumer.poll(10)
        for (record <- records.asScala) {
          
          jsonString = record.value().toString()
          var jValue = parse(jsonString)
          latitude = (jValue \ "coord" \ "lat").extract[Double]
          longitude = (jValue \ "coord" \ "lon").extract[Double]
          weather_id = (jValue \ "weather" \ "id").extract[Int]
          weather_main = (jValue \ "weather" \ "main").extract[String]
          weather_description = (jValue \ "weather" \ "description").extract[String]
          main_temp = (jValue \ "main" \ "temp").extract[Double]
          main_feelsLike = (jValue \ "main" \ "feels_like").extract[Double]
          main_tempMin = (jValue \ "main" \ "temp_min").extract[Double]
          main_tempMax = (jValue \ "main" \ "temp_max").extract[Double]
          main_pressure = (jValue \ "main" \ "pressure").extract[Double]
          main_humidity = (jValue \ "main" \ "humidity").extract[Double]
          wind_speed = (jValue \ "wind" \ "speed").extract[Double]
          name  = (jValue \ "name").extract[String]


          println(
          latitude +"\n"+
          longitude+"\n"+
          weather_id+"\n"+
          weather_main+"\n"+
          weather_description+"\n"+
          main_temp+"\n"+
          main_feelsLike+"\n"+
          main_tempMin+"\n"+
          main_tempMax+"\n"+
          main_humidity+"\n"+
          main_pressure+"\n"+
          main_humidity+"\n"+
          wind_speed+"\n"+
          name
          )

        }//End of 'for (record <- records.asScala)'        
      }//End of 'while(true)'
    } catch {
      case e: Exception => e.printStackTrace()
    } finally {
      consumer.close()
    }

  }//End of consume()

   def parser(string: String){
    
    var jValue = parse(string)

  }


}//End of Class
