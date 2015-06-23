package io.inbot.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import org.testng.annotations.Test;

@Test
public class CompressionUtilsTest {

    public void shouldCompressAndDecompress() {
        String sample = "hello world";
        byte[] input = sample.toString().getBytes(StandardCharsets.UTF_8);
        byte[] compressed = CompressionUtils.compress(input);
        byte[] decompressed = CompressionUtils.decompress(compressed);
        assertThat(decompressed).isEqualTo(input);
    }
}
