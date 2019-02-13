package frc.team2767.deepspace;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MainTest {

  @Test
  void testHello() {
    String hello = "hello";
    assertThat(hello).isEqualTo("hello");
  }

  @ParameterizedTest
  @ValueSource(strings = {"cat", "bat", "was"})
  void lengthThree(String candidate) {
    assertThat(candidate.length()).isEqualTo(3);
  }
}
