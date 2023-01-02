/*******************************************************************************
 *   Copyright 2018 Lukasz Budryk (https://github.com/lume115)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package wordtool;



import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class NameGenTestListSyllable {

	private File getFileForRes(final String resFielName) {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("at/lume/wordgen/lib/" + resFielName).getFile());
		return file;
	}

	@Test
	public void testParsingPositionAndListSyllablesWithoutAny() throws IOException {
		WordGen ng = new WordGenParser.Builder().build().fromFile(getFileForRes("parse02.txt"));
		assertNotNull( ng);
		assertEquals("a", ng.nextWord(1, 1));
		assertEquals( "ac", ng.nextWord(2, 2));
		assertEquals( "abc", ng.nextWord(3, 3));
		assertEquals( "abbc", ng.nextWord(4, 4));
		assertEquals( "abbbc", ng.nextWord(5, 5));
	}

	@Test
	public void testParsingPositionAndListSyllablesMultiWithoutAny() throws IOException {
		WordGen ng = new WordGenParser.Builder().build().fromFile(getFileForRes("parse04.txt"));
		assertNotNull( ng);
		assertEquals( "bax", ng.nextWord(1, 1));
		assertEquals( "baxfix", ng.nextWord(2, 2));
		assertEquals("baxdexfix", ng.nextWord(3, 3));
		assertEquals( "baxdexdexfix", ng.nextWord(4, 4));
		assertEquals( "baxdexdexdexfix", ng.nextWord(5, 5));
	}
}
