package com.fnproject.fn.examples;

import com.fnproject.fn.testing.FnTestingRule;
import com.fnproject.fn.testing.flow.FlowTesting;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class ThumbnailsFunctionTest {

    @Rule
    public final FnTestingRule fn = FnTestingRule.createDefault();
    private final FlowTesting flow = FlowTesting.create(fn);

    @Rule
    public final WireMockRule mockServer = new WireMockRule(0);

    @Test
    public void testThumbnail() {
        fn
          .setConfig("OBJECT_STORAGE_URL", "http://localhost:" + mockServer.port())
          .setConfig("OBJECT_STORAGE_ACCESS", "alpha")
          .setConfig("OBJECT_STORAGE_SECRET", "betabetabetabeta")
          .setConfig("RESIZE_128_FN_ID","myapp/resize128")
          .setConfig("RESIZE_256_FN_ID","myapp/resize256")
          .setConfig("RESIZE_512_FN_ID","myapp/resize512");


        flow
          .givenFn("myapp/resize128")
          .withAction((data) -> "128".getBytes())
          .givenFn("myapp/resize256")
          .withAction((data) -> "256".getBytes())
          .givenFn("myapp/resize512")
          .withAction((data) -> "512".getBytes());

        fn
          .givenEvent()
          .withBody("fn".getBytes())
          .enqueue();

        // Mock the http endpoint
        mockMinio();

        fn.thenRun(ThumbnailsFunction.class, "handleRequest");

        // Check the final image uploads were performed
        mockServer.verify(putRequestedFor(urlMatching("/alpha/.*\\.png")).withRequestBody(containing("fn")));
        mockServer.verify(putRequestedFor(urlMatching("/alpha/.*\\.png")).withRequestBody(containing("128")));
        mockServer.verify(putRequestedFor(urlMatching("/alpha/.*\\.png")).withRequestBody(containing("256")));
        mockServer.verify(putRequestedFor(urlMatching("/alpha/.*\\.png")).withRequestBody(containing("512")));
        mockServer.verify(4, putRequestedFor(urlMatching(".*")));
    }

    @Test
    public void anExternalFunctionFailure() {
        fn
          .setConfig("OBJECT_STORAGE_URL", "http://localhost:" + mockServer.port())
          .setConfig("OBJECT_STORAGE_ACCESS", "alpha")
          .setConfig("OBJECT_STORAGE_SECRET", "betabetabetabeta")
          .setConfig("RESIZE_128_FN_ID","myapp/resize128")
          .setConfig("RESIZE_256_FN_ID","myapp/resize256")
          .setConfig("RESIZE_512_FN_ID","myapp/resize512");;

        flow
          .givenFn("myapp/resize128")
          .withResult("128".getBytes())
          .givenFn("myapp/resize256")
          .withResult("256".getBytes())
          .givenFn("myapp/resize512")
          .withFunctionError();

        fn
          .givenEvent()
          .withBody("fn".getBytes())
          .enqueue();

        // Mock the http endpoint
        mockMinio();

        fn.thenRun(ThumbnailsFunction.class, "handleRequest");

        // Confirm that one image upload didn't happen
        mockServer.verify(0, putRequestedFor(urlMatching("/alpha/.*\\.png")).withRequestBody(equalTo("512")));

        mockServer.verify(3, putRequestedFor(urlMatching(".*")));

    }

    private void mockMinio() {

        mockServer.stubFor(get(urlMatching("/alpha.*"))
          .willReturn(aResponse().withBody(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
              "<ListBucketResult>\n" +
              "    <Name>alpha</Name>\n" +
              "    <Prefix/>\n" +
              "    <KeyCount>0</KeyCount>\n" +
              "    <MaxKeys>100</MaxKeys>\n" +
              "    <IsTruncated>false</IsTruncated>\n" +
              "</ListBucketResult>")));

        mockServer.stubFor(WireMock.head(urlMatching("/alpha.*")).willReturn(aResponse().withStatus(200)));

        mockServer.stubFor(WireMock.put(urlMatching(".*")).willReturn(aResponse().withStatus(200)));

    }

}
