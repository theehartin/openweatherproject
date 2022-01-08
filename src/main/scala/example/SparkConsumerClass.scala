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



/* 
  val schema = new StructType()
    .add(
      "coord", 
      new StructType()
        .add("lon", StringType)
        .add("lat", StringType)
    )
    .add("weather", StringType)
    .add("base", StringType)
    .add("main", StringType)
    .add("visibility", StringType)
    .add("wind", StringType)
    .add("clouds", StringType)
    .add("dt", StringType)
    .add("sys", StringType)
    .add("timezone",StringType)
    .add("id",StringType)
    .add("name", StringType)
    .add("cod",StringType)

 */
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

  def refineData(){



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
 /* 
    var coordDF = flatDF
    .withColumn("coordinates", from_json(col("coord"),coordSchema))  
    .select("coordinates.*")
  

    var windDF = flatDF
    .withColumn("wind",from_json(col("wind"),windSchema))
    .select("wind.speed")
   */



    var combinedDF = flatDF
    .withColumn("coord", from_json(col("coord"),coordSchema))
    .withColumn("wind",from_json(col("wind"),windSchema))
    .withColumn("main",from_json(col("main"),mainSchema))
    .withColumn("clouds",from_json(col("clouds"),cloudSchema))
    .withColumn("rain",from_json(col("rain"),rainSchema))
    .withColumn("snow",from_json(col("snow"),snowSchema))
    .withColumn("sys",from_json(col("sys"),sysSchema))
    .withColumn("weather_split", split(col("weather"), "},"))
    .select(
    $"coord.*"
    ,explode($"weather_split").as("weather")
    ,$"wind.*"
    ,$"main.*"
    ,$"visibility"
    ,$"clouds"
    ,$"rain" 
    ,$"snow"
    ,$"dt"
    ,$"sys.*"
    ,$"id"
    ,$"name"
    )

     var combinedDF2 = flatDF
    .withColumn("coord", from_json(col("coord"),coordSchema))
    .withColumn("wind",from_json(col("wind"),windSchema))
    .withColumn("main",from_json(col("main"),mainSchema))
    .withColumn("clouds",from_json(col("clouds"),cloudSchema))
    .withColumn("rain",from_json(col("rain"),rainSchema))
    .withColumn("snow",from_json(col("snow"),snowSchema))
    .withColumn("sys",from_json(col("sys"),sysSchema))
    .withColumn("weather_replace", regexp_replace(col("weather"), "},", "}~"))
    .select(
    "coord.*"
    ,"weather_replace"
    ,"wind.*"
    ,"main.*"
    ,"visibility"
    ,"clouds"
    ,"rain" 
    ,"snow"
    ,"dt"
    ,"sys.*"
    ,"id"
    ,"name"
    )

    var combinedDF3 = combinedDF2
    .withColumn("weather_replace", regexp_replace(col("weather_replace"), "\\[", ""))
    .select("*")
    


    var combinedDF4 = combinedDF3
    .withColumn("weather_split", split(col("weather_replace"), "~" ))
    .select(
    explode($"weather_split").as("weather")
    ,$"*")

 

    var combinedDF5 = combinedDF4
    .withColumn("weather_final", from_json(col("weather"), weatherSchema))
    .select("weather_final.*", "*")

 
    
    var finalDF = combinedDF
    .withColumn("weather", from_json(col("weather"), weatherSchema))
    .select("weather.*", "*")

    printToConsole(combinedDF5)

/* 


    val weatherSchema = new StructType()
      .add("weather", StringType)

    
 
    var weatherDF = flatDF
    .withColumn("weather_split", split(col("weather"), "},"))
    .select($"key"
    ,$"name"
    ,explode($"weather_split"))

    
 */
/*    
   
   def weatherSchema(arr: Array[String]): StructType = {
      var inc = 0
      var varSchema = new StructType()
      var length = arr.length

      for (x <- 0 until length){
        varSchema = varSchema.add(s"Weather_$inc", StringType)
      }
      return varSchema
   }

   //var weatherInfo = new Array[String]

   var weatherDF2 = flatDF.map(f=>{
      var weatherInfo = f.getAs[String](1).replaceAll("[\\[]\\]", "").split(",")
      weatherInfo
    })

    var weatherDF3 = weatherDF2.toDF("Arr_of_weather")
 

    var weatherDF4 = weatherDF3
    .withColumn("weather",explode(col("Arr_of_weather")))

    var weatherInfo = flatDF.getAs[String](1).replaceAll("[\\[]\\]", "").split(",")

    var weatherDF5 = weatherDF4
    .withColumn("weatherConditions", from_json(col("weather"), weatherSchema(weatherInfo)))
    .select("weatherConditions.*")
  */
   
  

    
/*     
    var parsedDF = flatDF.map(f=>{
      val coordSplit = f.getAs[String](0).split(",")
      })
 */






 
/* 

import spark.implicits._
    
 
 try{
  var coordDF2= flatDF.map(f=>{
    val coordSplit = f.getAs[String](0).split(",")
    val mainSplit = f.getAs[String](3).split(",")
    val visibility = f.getAs[String](4) //Using 'getAs[Int]' or 'getDouble()' threw exceptions
    val wind = f.getAs[String](5).split(",")
    (coordSplit(0)
    ,coordSplit(1)
    ,mainSplit(0)
    ,mainSplit(1)
    ,mainSplit(2)
    ,mainSplit(3)
    ,mainSplit(4)
    ,mainSplit(5)
    ,visibility
    ,wind(0)
    ,wind(1)
    ,wind(2)
    )
  })

  var parsedDF = coordDF2.toDF(
    "Longitude"
    ,"Latitude"
    ,"Temperature"
    ,"Feels Like"
    ,"Minimum Temperature"
    ,"Maximum Temperature"
    ,"Pressure"
    ,"Humidity"
    ,"Visibility"
    ,"Speed"
    ,"Degree"
    ,"Gust"
    )

  printToConsole(parsedDF)
 } catch{
   case _ : Throwable => refineData()
 } 
   


 */


/* 
  var parsedDF = coordDF2.toDF(
  "Longitude"
  ,"Latitude"
  ,"Temperature"
  ,"Feels Like"
  ,"Minimum Temperature"
  ,"Maximum Temperature"
  ,"Pressure"
  ,"Humidity"
  ,"Visibility"
  ,"Speed"
  ,"Degree"
  ,"Gust"
  )

  printToConsole(parsedDF)
 */
  }//End of refineData()
















  def writeToParquet(){
    var flatDF = flattenRecord()
    .writeStream.
    outputMode("append")
    .format("parquet")
    .option("path", "file:///home/maria_dev/AIOMyData")
  }



}//End of Class


