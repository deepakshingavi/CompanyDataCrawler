import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.ds.training.InMemoryCompanyStorage
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

class FileLoadTest extends AsyncFlatSpec with Matchers with ScalatestRouteTest {
  override def testConfigSource = "akka.loglevel = WARNING"

//  override implicit val system = ActorSystem()

  it should "load all the company data" in {
    val dataSize = InMemoryCompanyStorage.companyMapData.size
    assertResult(10005)(dataSize)
  }
}
