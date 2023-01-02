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

public class NameGenTestMinMaxLength {

	private File getFileForRes(final String resFielName) {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("at/lume/wordgen/lib/" + resFielName).getFile());
		return file;
	}

	@Test
	public void testParsingPositionAndListSyllablesWithMinMaxLength1() throws IOException {
		WordGen ng = new WordGenParser.Builder().build().fromFile(getFileForRes("parse06_1.txt"));
		assertNotNull(ng);
		assertEquals( "aaaa", ng.nextWord(1, 1));
		assertEquals( "aaaa", ng.nextWord(2, 2));
		assertEquals( "aaaa", ng.nextWord(3, 3));
		assertEquals( "aaaa", ng.nextWord(4, 4));
		assertEquals("aaaa", ng.nextWord(5, 5));
	}

	@Test
	public void testParsingPositionAndListSyllablesWithMinMaxLength2() throws IOException {
		WordGen ng = new WordGenParser.Builder().build().fromFile(getFileForRes("parse06_2.txt"));
		assertNotNull( ng);
		assertEquals("aaaa", ng.nextWord(1, 1));
		assertEquals("aaaac", ng.nextWord(2, 2));
		assertEquals( "aaaabc", ng.nextWord(3, 3));
		assertEquals( "aaaabc", ng.nextWord(4, 4));
		assertEquals("aaaabc", ng.nextWord(5, 5));
	}

	@Test
	public void testParsingPositionAndListSyllablesWithMinMaxLength3() throws IOException {
		WordGen ng = new WordGenParser.Builder().build().fromFile(getFileForRes("parse06_3.txt"));
		assertNotNull(ng);
		assertEquals( "aaaa", ng.nextWord(1, 1));
		assertEquals( "aaaac", ng.nextWord(2, 2));
		assertEquals( "aaaac", ng.nextWord(3, 3));
		assertEquals("aaaac", ng.nextWord(4, 4));
		assertEquals( "aaaac", ng.nextWord(5, 5));
	}
}
