/*-
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2016 Spotify AB
 * Copyright (C) 9/2019 - now Dimitris Mandalidis
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */

package org.mandas.docker.client;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.describedAs;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

public class CompressedDirectoryMatchFilepathTest {

  static Stream<Arguments> data() {

    // Data copy-pasted from http://golang.org/src/path/filepath/match_test.go#L22
    // Patterns we don't correctly handle are commented out
    return Stream.of(
        Arguments.of("abc", "abc", true, null),
        Arguments.of("*", "abc", true, null),
        Arguments.of("*c", "abc", true, null),
        Arguments.of("a*", "a", true, null),
        Arguments.of("a*", "abc", true, null),
        Arguments.of("a*", "ab/c", false, null),
        Arguments.of("a*/b", "abc/b", true, null),
        Arguments.of("a*/b", "a/c/b", false, null),
        Arguments.of("a*b*c*d*e*/f", "axbxcxdxe/f", true, null),
        Arguments.of("a*b*c*d*e*/f", "axbxcxdxexxx/f", true, null),
        Arguments.of("a*b*c*d*e*/f", "axbxcxdxe/xxx/f", false, null),
        Arguments.of("a*b*c*d*e*/f", "axbxcxdxexxx/fff", false, null),
        Arguments.of("a*b?c*x", "abxbbxdbxebxczzx", true, null),
        Arguments.of("a*b?c*x", "abxbbxdbxebxczzy", false, null),
        Arguments.of("ab[c]", "abc", true, null),
        Arguments.of("ab[b-d]", "abc", true, null),
        Arguments.of("ab[e-g]", "abc", false, null),
        Arguments.of("ab[^c]", "abc", false, null),
        Arguments.of("ab[^b-d]", "abc", false, null),
        Arguments.of("ab[^e-g]", "abc", true, null),
        Arguments.of("a\\*b", "a*b", true, null),
        Arguments.of("a\\*b", "ab", false, null),
        Arguments.of("a?b", "a☺b", true, null),
        Arguments.of("a[^a]b", "a☺b", true, null),
        Arguments.of("a???b", "a☺b", false, null),
        Arguments.of("a[^a][^a][^a]b", "a☺b", false, null),
        Arguments.of("[a-ζ]*", "α", true, null),
        Arguments.of("*[a-ζ]", "A", false, null),
        Arguments.of("a?b", "a/b", false, null),
        Arguments.of("a*b", "a/b", false, null),
        Arguments.of("[\\]a]", "]", true, null),
        Arguments.of("[\\-]", "-", true, null),
        Arguments.of("[x\\-]", "x", true, null),
        Arguments.of("[x\\-]", "-", true, null),
        Arguments.of("[x\\-]", "z", false, null),
        Arguments.of("[\\-x]", "x", true, null),
        Arguments.of("[\\-x]", "-", true, null),
        Arguments.of("[\\-x]", "a", false, null),
        Arguments.of("[]a]", "]", false, PatternSyntaxException.class),
        // Arguments.of("[-]", "-", false, PatternSyntaxException.class),
        // Arguments.of("[x-]", "x", false, PatternSyntaxException.class),
        // Arguments.of("[x-]", "-", false, PatternSyntaxException.class),
        // Arguments.of("[x-]", "z", false, PatternSyntaxException.class),
        // Arguments.of("[-x]", "x", false, PatternSyntaxException.class),
        // Arguments.of("[-x]", "-", false, PatternSyntaxException.class),
        // Arguments.of("[-x]", "a", false, PatternSyntaxException.class),
        // Arguments.of("\\", "a", false, PatternSyntaxException.class),
        // Arguments.of("[a-b-c]", "a", false, PatternSyntaxException.class),
        Arguments.of("[", "a", false, PatternSyntaxException.class),
        Arguments.of("[^", "a", false, PatternSyntaxException.class),
        Arguments.of("[^bc", "a", false, PatternSyntaxException.class),
        // Arguments.of("a[", "a", false, null),
        Arguments.of("a[", "ab", false, PatternSyntaxException.class),
        Arguments.of("*x", "xxx", true, null)
    );
  }

  private FileSystem fs;

  @BeforeEach
  public void setUp() throws Exception {
    fs = Jimfs.newFileSystem(Configuration.unix());
  }

  @ParameterizedTest(name = "Pattern {0} matching {1}: {2} throwing {3}")
  @MethodSource("data")
  public void testMatchFilepath(String pattern, String pathString, boolean matched, 
                                  Class<? extends Exception> exception) {
    final Path path = fs.getPath(pathString);
    if (exception != null) {
    	assertThrows(exception, () -> CompressedDirectory.goPathMatcher(fs, pattern).matches(path)); 
    } else {
	    final boolean result = CompressedDirectory.goPathMatcher(fs, pattern).matches(path);
	
	    final String description;
	    if (matched) {
	      description = MessageFormat.format("the pattern {0} to match {1}", pattern, pathString);
	    } else {
	      description = MessageFormat.format("the pattern {0} not to match {1}", pattern, pathString);
	    }
	
	    assertThat(result, describedAs(description, is(matched)));
    }
  }

}
