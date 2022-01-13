package example

//Java Functions
import java.nio.file.{Paths, Files}
import java.util.Calendar

//Spark Functions
import org.apache.spark.sql._
import scala.collection.mutable

//Scala Imports
import scala.collection.mutable.ListBuffer

class FileSystemFunctions {

  val spark = SparkSession
  .builder
  .appName("Parquet Condenser")
  .config("spark.master", "local[*]")
  .getOrCreate()
 
  def parquetCondenser() = {
    var dir = Paths.get("/home/maria_dev/AIOMyData/DataSets/OpenWeather/").toFile().listFiles()
    var filePathList = ListBuffer[String]()
    var testList = ListBuffer(0)
  
    for (file <- dir){
      if(file.getName().contains("crc")){}
      else{
        if (file.getName().contains("parquet") && file.length()<((6.4)*(math.pow(10,7)))){
          var fileName = ("file://"+file.getAbsoluteFile())
          filePathList += fileName 
        }//End of 'if (file.getName()....'
      }//End of 'else'
    }//End of 'for (file <- dir)'

    val todaysDate =  Calendar.getInstance().getTime().toString()

    val condensedDF = spark.read.parquet(filePathList:_*).coalesce(1)
    condensedDF.write.parquet("file:///home/maria_dev/AIOMyData/DataSets/OpenWeatherCondensed/"+todaysDate)
  }//End of parquetCondenser()




/* 

  This is a code snippet taken from  'https://medium.com/bigspark/compaction-merge-of-small-parquet-files-bef60847e60b'

  def get_repartition_factor(dir_size):
    block_size = sc._jsc.hadoopConfiguration().get(“dfs.blocksize”)
    return math.ceil(dir_size/block_size) # returns 2
    df=spark.read.parquet(“/path/to/source”)
    df.repartition(get_repartition_factor(217894092))
    .write
    .parquet("/path/to/output")

 */ 


}
