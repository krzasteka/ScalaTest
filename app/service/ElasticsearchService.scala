package service

import model.IDTrait
import com.sksamuel.elastic4s.ElasticDsl.index
import com.sksamuel.elastic4s.source.JsonDocumentSource
import org.json4s.jackson.Serialization._
import org.json4s.{DefaultFormats, Formats, FieldSerializer}
import com.sksamuel.elastic4s.{ElasticClient}
import com.sksamuel.elastic4s.ElasticDsl._
import org.json4s.FieldSerializer.renameTo
import org.elasticsearch.common.settings.ImmutableSettings

/**
 * Created by benjarman on 4/27/16.
 */
object ElasticsearchService {

  val settings = ImmutableSettings.settingsBuilder()

  val client = ElasticClient.remote(settings.build(), "172.0.0.1", 9300)

  /**
   * @param record Is the instance of the case class you intend to save to ES
   * @param indexName The index you are saving to
   * @param indexType The type within the index you are writing to
   * @tparam T The type param of the case class being save. For this to work IDTrait should be extended in this case class
   * @tparam E The type param of the _id field of the case class being saved. Usually this will be String
   */
  def save[T <: IDTrait[E]: Manifest, E](record: T, indexName: String, indexType: String): Unit = {
    val rename = FieldSerializer[T](renameTo("_id", s"${indexType}_id"))
    implicit val format: Formats = DefaultFormats + rename ++ org.json4s.ext.JodaTimeSerializers.all

    val _id = record._id
    val jsonString = write(record)
    
    client.execute {
      index into indexName -> indexType doc JsonDocumentSource(jsonString) id _id
    }
  }

  def close = {
    client.close()
  }

}