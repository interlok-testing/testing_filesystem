package com.adaptris.filesystem.test;

import com.adaptris.testing.SingleAdapterFunctionalTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.io.entity.HttpEntities;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DefaultFunctionalTest extends SingleAdapterFunctionalTest {
    ObjectMapper om = new ObjectMapper();

    static class TestCaseData {
        String url;
        String input;
        String expected;

        TestCaseData(String url, String resource, String expected) {
            this.url = url;
            this.input = resource;
            this.expected = expected;
        }

        TestCaseData(String url, String expected) {
            this(url, null, expected);
        }
    }

    @BeforeAll
    public void setup() throws Exception {
        super.setup();
//        File testDir = Paths.get("..","resources", "test").toFile();
//        if (testDir.exists()) FileUtils.forceDelete(testDir);
    }

    @Override
    protected void customiseVariablesIfExists(Properties props) {
        props.put("test-base-directory-path", Path.of("").toAbsolutePath().getParent().resolve("resources").resolve("test").toString());
        props.put("test-base-directory-path-url", "file://localhost/" + Path.of("..", "resources", "test"));
        props.put("test-folder-path", Path.of("${test-base-directory-path}", "${test-folder}").toString());
        props.put("file-separator", File.separator);
        super.customiseVariablesIfExists(props);
    }

    List<TestCaseData> test_check_data() throws IOException {
        final String offsetDateTimeFormat = "yyyy-MM-dd'T'HH:mm:ssZ";
        final SimpleDateFormat sdf = new SimpleDateFormat(offsetDateTimeFormat);
        String lastModifiedDateTimeStr = sdf.format(new Date(Path.of("..", "resources", "test", "messages" , "INTERLOK-3555.json").toFile().lastModified()));
        String adapterBaseUrl = getBaseAdapterUrl();
        return List.of(
                new TestCaseData(adapterBaseUrl + "/check?field=updatedAt&ticket=INTERLOK-3542", lastModifiedDateTimeStr),
                new TestCaseData(adapterBaseUrl + "/check?field=size&ticket=INTERLOK-3551", "167"),
                new TestCaseData(adapterBaseUrl + "/check?field=createdAt&ticket=INTERLOK-3555", lastModifiedDateTimeStr),
                new TestCaseData(adapterBaseUrl + "/check?field=absolutePath&ticket=INTERLOK-3557", Paths.get("..","resources", "test", "messages", "INTERLOK-3557.json" ).toFile().getCanonicalPath().toString())

        );
    }

    @ParameterizedTest
    @Order(1)
    @MethodSource("test_check_data")
    public void testCheck(TestCaseData testCaseData) throws Exception {
        HttpGet httpGet = new HttpGet(testCaseData.url);
        try (CloseableHttpResponse response = client.execute(httpGet)) {
            String responseStr = new String(response.getEntity().getContent().readAllBytes());
            Assertions.assertEquals(StringUtils.deleteWhitespace(testCaseData.expected)
                    ,StringUtils.deleteWhitespace(responseStr));
        }
    }


    private static final String EXPECTED_LIST_VALUE = """
            {
            "files":[
            "INTERLOK-3542.json",
            "INTERLOK-3543.json",
            "INTERLOK-3545.json",
            "INTERLOK-3546.json",
            "INTERLOK-3547.json",
            "INTERLOK-3548.json",
            "INTERLOK-3551.json",
            "INTERLOK-3552.json",
            "INTERLOK-3555.json",
            "INTERLOK-3556.json",
            "INTERLOK-3557.json",
            "INTERLOK-3558.json",
            "INTERLOK-3559.json",
            "INTERLOK-3560.json",
            "INTERLOK-3561.json",
            "INTERLOK-3563.json",
            "INTERLOK-3564.json",
            "INTERLOK-3566.json",
            "INTERLOK-3567.json",
            "INTERLOK-3568.json",
            "INTERLOK-3569.json",
            "INTERLOK-3570.json",
            "INTERLOK-3571.json",
            "INTERLOK-3574.json",
            "INTERLOK-3575.json",
            "INTERLOK-3576.json",
            "INTERLOK-3577.json",
            "INTERLOK-3579.json",
            "INTERLOK-3581.json",
            "INTERLOK-3582.json",
            "INTERLOK-3583.json",
            "INTERLOK-3585.json",
            "INTERLOK-3587.json",
            "INTERLOK-3588.json",
            "INTERLOK-3590.json",
            "INTERLOK-3591.json",
            "INTERLOK-3599.json",
            "INTERLOK-3600.json",
            "INTERLOK-3704.json",
            "INTERLOK-3705.json",
            "INTERLOK-3707.json",
            "INTERLOK-3708.json",
            "INTERLOK-3710.json"
            ]
}""";
    @Order(2)
    @Test
    public void testList() throws Exception {
        HttpGet httpGet = new HttpGet(getBaseAdapterUrl() + "/list?folder=messages");
        try (CloseableHttpResponse response = client.execute(httpGet)) {
            String responseStr = new String(response.getEntity().getContent().readAllBytes());
            JsonNode responseFiles = om.readTree(responseStr);
            JsonNode expectedFiles = om.readTree(EXPECTED_LIST_VALUE);
            Set responseSet = copyArrayNodeToSet(responseFiles.get("files"));
            Set expectedSet = copyArrayNodeToSet(expectedFiles.get("files"));
            Assertions.assertEquals(expectedSet, responseSet);
        }
    }


    private static String EXPECTED_MOVE_LIST_VALUE = """
            {"files":["INTERLOK-3542.json","INTERLOK-3543.json","INTERLOK-3545.json","INTERLOK-3546.json","INTERLOK-3547.json"]}
            """;
    @Order(5)
    @Test
    public void testMove() throws Exception {

        HttpGet httpGet = new HttpGet(getBaseAdapterUrl() + "/move?folder=test-folder&newfolder=test-folder2");
        try (CloseableHttpResponse response = client.execute(httpGet)) {
            String responseStr = new String(response.getEntity().getContent().readAllBytes());
            Assertions.assertEquals("move directory success", responseStr);
        }

        httpGet = new HttpGet(getBaseAdapterUrl() + "/list?folder=test-folder");
        try (CloseableHttpResponse response = client.execute(httpGet)) {
            String responseStr = new String(response.getEntity().getContent().readAllBytes());
            Assertions.assertEquals("null", responseStr);
        }

        httpGet = new HttpGet(getBaseAdapterUrl() + "/list?folder=test-folder2");
        try (CloseableHttpResponse response = client.execute(httpGet)) {
            String responseStr = new String(response.getEntity().getContent().readAllBytes());
            JsonNode responseFiles = om.readTree(responseStr);
            JsonNode expectedFiles = om.readTree(EXPECTED_MOVE_LIST_VALUE);
            Set responseSet = copyArrayNodeToSet(responseFiles.get("files"));
            Set expectedSet = copyArrayNodeToSet(expectedFiles.get("files"));
            Assertions.assertEquals(expectedSet, responseSet);

        }
    }

    @Order(3)
    @Test
    public void testZipService() throws Exception {
        HttpGet httpGet = new HttpGet(getBaseAdapterUrl() + "/zip?folder=test-folder");
        try (CloseableHttpResponse response = client.execute(httpGet)) {
            byte[] responseBytes = response.getEntity().getContent().readAllBytes();
            File responseFile = File.createTempFile("testing_filesystem", ".zip");
            FileUtils.writeByteArrayToFile(responseFile, responseBytes);
            File expectedFile = Path.of("..", "resources", "test", "test.zip").toFile();
            Set<String> responseEntryNames = new ZipFile(responseFile).stream().map(e -> e.getName().replaceAll("\\\\", "/")).collect(Collectors.toSet());
            Set<String> expectedEntryNames = new ZipFile(expectedFile).stream().map(e -> e.getName().replaceAll("\\\\", "/")).collect(Collectors.toSet());
            Assertions.assertEquals(expectedEntryNames, responseEntryNames);
        }
    }


    private Set copyArrayNodeToSet(JsonNode node) {
        HashSet s1 = new HashSet();
        for (Iterator itr = node.iterator(); itr.hasNext();) {
            Object o = itr.next();
            s1.add(o);
        }
        return s1;
    }
}
