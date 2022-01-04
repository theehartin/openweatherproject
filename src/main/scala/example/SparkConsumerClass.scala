package example

import org.apache.spark.sql.SparkSession
//import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{IntegerType, StringType, StructType, DoubleType}

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
    .add(
      "coord", StringType)
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
    .select("jsonData.*")

    return flatDF
  }//End of parsed()

  def refineData(){
    val coordSchema = new StructType()
      .add("lat", StringType)
      .add("lon", StringType)

    val windSchema = new StructType()
      .add("speed", StringType)
      .add("deg", StringType)
      .add("gust", StringType)
    var flatDF = flattenRecord()
/* 
    var parsedDF = flatDF.withColumn("coordinates", from_json(col("coord"),coordSchema))
    .withColumn("wind",from_json(col("wind"),windSchema))
    .select("coordinates.*")
    .select("wind.*") */


    var parsedDF = flatDF
    .withColumn("wind",from_json(col("wind"),windSchema))
    .select("wind.*")
    printToConsole(parsedDF)

  }//End of refineData()


  def writeToParquet(){
    var flatDF = flattenRecord()
    .writeStream.
    outputMode("append")
    .format("parquet")
    .option("path", "file:///home/maria_dev/AIOMyData")
  }



}//End of Class


