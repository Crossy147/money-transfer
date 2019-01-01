package rest.routes

import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model.MediaTypes.`application/json`
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import akka.util.ByteString
import com.avsystem.commons.serialization.GenCodec
import com.avsystem.commons.serialization.json.{JsonStringInput, JsonStringOutput}

trait GenCodecMarshalling {
  implicit def unmarshaller[A: GenCodec]: FromEntityUnmarshaller[A] =
    Unmarshaller.byteStringUnmarshaller
      .forContentTypes(`application/json`)
      .mapWithCharset {
        case (ByteString.empty, _) => throw Unmarshaller.NoContentException
        case (data, charset) => data.decodeString(charset.nioCharset.name)
      }.map(data => JsonStringInput.read[A](data))

  implicit def marshaller[A: GenCodec]: ToEntityMarshaller[A] =
    Marshaller.oneOf(`application/json`)(Marshaller.stringMarshaller).compose(JsonStringOutput.write(_))
}
