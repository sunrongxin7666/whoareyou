package srx.awesome.code.security.wiremock;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * 设置WireMock
 */
public class MockServer {
    public static void main(String[] args) throws IOException {
        configureFor(8062);
        removeAllMappings();//清空配置


        ClassPathResource pathResource = new ClassPathResource("mock/response/01.txt");
        String s = FileUtils.readFileToString(pathResource.getFile(),"UTF-8");
        stubFor(get(urlPathEqualTo("/order/1")).willReturn(
                aResponse().withBody(s).withStatus(200)));
    }
}
