package example

import org.apache.spark.sql.SparkSession
//import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{ArrayType,IntegerType, StringType, StructType, DoubleType}
import com.google.flatbuffers.Struct



class SparkConsumerClass {

  val spark = SparkSession
  .builder
  .appName("Kafka Source")
  .config("spark.master", "local[*]")
  .getOrCreate()
  
  import spark.implicits._
  spark.sparkContext.setLogLevel("ERROR")

  val initDF = spark.readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", "sandbox-hdp.hortonworks.com:6667")
    .option("subscribe", "OpenWeather_OneCall")   
    .load()

  val dfOut = initDF
    .selectExpr("CAST(key AS STRING)","CAST(value AS STRING)")
    .select("key","value")


  val schema = new StructType()
    .add("weather", StringType)
    .add("coord", StringType)   
    .add("base", StringType)
    .add("main", StringType)
    .add("visibility", StringType)
    .add("wind", StringType)
    .add("clouds", StringType)
    .add("rain", StringType)
    .add("snow", StringType)
    .add("dt", StringType)
    .add("sys", StringType)
    .add("timezone",StringType)
    .add("id",StringType)
    .add("name", StringType)
    .add("cod",StringType)


  def printToConsole(df: DataFrame){
    df.writeStream
      .format("console")
      .start()
      .awaitTermination()
  }//End of printToConsole()

  
  def console(){
    dfOut.writeStream
      .format("console")
      .start()
      .awaitTermination()
  }//End of console()

  def flattenRecord(): DataFrame = {
    var flatDF = dfOut
    .withColumn("jsonData", from_json(col("value"),schema))
    .select("key","jsonData.*")

    return flatDF
  }//End of parsed()

  def refineData(): DataFrame = {

    val coordSchema = new StructType()
      .add("lat", StringType)
      .add("lon", StringType)

    val weatherSchema = new StructType()
      .add("id", StringType)
      .add("main", StringType)
      .add("description", StringType)
      .add("icon", StringType)


    val windSchema = new StructType()
      .add("speed", StringType)
      .add("deg", StringType)
      .add("gust", StringType)

    val mainSchema = new StructType()
      .add("temp", StringType)
      .add("feels_like", StringType)
      .add("temp_min", StringType)
      .add("temp_max", StringType)
      .add("pressure", StringType)
      .add("humidity", StringType)

    val cloudSchema =  new StructType()
      .add("all", StringType)

    val rainSchema = new StructType()
      .add("1h", StringType)

    val snowSchema = new StructType()
      .add("1h", StringType)

    val sysSchema = new StructType()
      .add("type", StringType)
      .add("id", StringType)      
      .add("country", StringType)
      .add("sunrise", StringType)
      .add("sunset", StringType)

    var flatDF = flattenRecord()

    var combinedDF2 = flatDF.withColumn("weather_replace", regexp_replace(col("weather"), "},", "}~"))

    var combinedDF3 = combinedDF2.withColumn("weather_replace", regexp_replace(col("weather_replace"), "\\[", ""))

    var combinedDF4 = combinedDF3
    .withColumn("weather_split", split(col("weather_replace"), "~" ))
    .select(
    explode($"weather_split").as("weatherTEST")
    ,$"*")

    var combinedDF5 = combinedDF4
    .withColumn("weather_final", from_json(col("weatherTEST"), weatherSchema))
    .withColumn("coord", from_json(col("coord"),coordSchema))
    .withColumn("wind",from_json(col("wind"),windSchema))
    .withColumn("main",from_json(col("main"),mainSchema))
    .withColumn("clouds",from_json(col("clouds"),cloudSchema))
    .withColumn("rain",from_json(col("rain"),rainSchema))
    .withColumn("snow",from_json(col("snow"),snowSchema))
    .withColumn("sys",from_json(col("sys"),sysSchema))
    .select(
    col("weather_final.id").as("weather_id")
    ,col("weather_final.main").as("weather_brief")
    ,col("weather_final.description").as("weather_description")
    ,col("coord.lat").as("latidude")
    ,col("coord.lon").as("longitude")
    ,col("wind.speed").as("wind_speed")
    ,col("wind.gust").as("wind_gust")
    ,col("wind.deg").as("wind_degree")
    ,col("main.temp").as("temperature")
    ,col("main.feels_like").as("feels_like_temperature")
    ,col("main.temp_min").as("minimum_temperature")
    ,col("main.temp_max").as("maximum_temperature")
    ,col("main.pressure")
    ,col("main.humidity")
    ,col("visibility")
    ,col("clouds")
    ,col("rain" )
    ,col("snow")
    ,col("sys.sunrise").as("sunrise")
    ,col("sys.sunset").as("sunset")
    ,col("id").as("City_id")
    ,col("name").as("City_name") 
    )


    return combinedDF5
    

  }//End of refineData()

  def writeToParquet(df: DataFrame){
    df
    .writeStream.
    outputMode("append")
    .format("parquet")
    .option("path", "file:///home/maria_dev/AIOMyData/DataSets/OpenWeather/")

  }



}//End of Class


