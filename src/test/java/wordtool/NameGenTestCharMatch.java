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


public class NameGenTestCharMatch {

	private File getFileForRes(final String resFielName) {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("at/lume/wordgen/lib/" + resFielName).getFile());
		return file;
	}

	@Test
	public void testParsingPositionAndListSyllablesWithCharacterMatchWithoutNumber() throws IOException {
		WordGen ng = new WordGenParser.Builder().build().fromFile(getFileForRes("parse03.txt"));
		assertNotNull(ng);
		assertEquals( "a", ng.nextWord(1, 1));
		assertEquals("az", ng.nextWord(2, 2));
		assertEquals("abu", ng.nextWord(3, 3));
		assertEquals("abiz", ng.nextWord(4, 4));
		assertEquals( "abibu", ng.nextWord(5, 5));
		assertEquals( "abibiz", ng.nextWord(6, 6));
		assertEquals( "abibibu", ng.nextWord(7, 7));
	}
	
	@Test
	public void testParsingPositionAndListSyllablesWithCharacterMatchInclNumber() throws IOException {
		WordGen ng = new WordGenParser.Builder().build().fromFile(getFileForRes("parse05.txt"));
		assertNotNull(ng);
		assertEquals("a", ng.nextWord(1, 1));
		assertEquals( "a2", ng.nextWord(2, 2));
		assertEquals( "a1c", ng.nextWord(3, 3));
		assertEquals("a1b2", ng.nextWord(4, 4));
		assertEquals("a1b1c", ng.nextWord(5, 5));
		assertEquals( "a1b1b2", ng.nextWord(6, 6));
		assertEquals( "a1b1b1c", ng.nextWord(7, 7));
	}
	
	@Test
	public void testParsingPositionAndListSyllablesWithCharacterMatchAndLongSyllables() throws IOException {
		WordGen ng = new WordGenParser.Builder().build().fromFile(getFileForRes("parse07.txt"));
		assertNotNull(ng);
		assertEquals( "aa", ng.nextWord(1, 1));
		assertEquals( "aaxx", ng.nextWord(2, 2));
		assertEquals("aababex", ng.nextWord(3, 3));
		assertEquals("aababeexx", ng.nextWord(4, 4));
	}

	@Test
	public void testParsingPositionAndListSyllablesWithCharacterMatchAndUnknownVowels() throws IOException {
		WordGen ng = new WordGenParser.Builder().build().fromFile(getFileForRes("parse09.txt"));
		assertNotNull(ng);
		assertEquals("b", ng.nextWord(1, 1));
		assertEquals( "b", ng.nextWord(2, 2));
		assertEquals("b", ng.nextWord(3, 3));
		assertEquals( "b", ng.nextWord(4, 4));
	}
	
	@Test
	public void testParsingPositionAndListSyllablesWithCharacterMatchAndUnknownAndCustomVowels() throws IOException {
		WordGen ng = new WordGenParser.Builder()
				.setVowels(WordGenParser.VOWELS + "ȩ")
				.build()
				.fromFile(getFileForRes("parse09.txt"));
		assertNotNull(ng);
		assertEquals("b", ng.nextWord(1, 1));
		assertEquals("bȩc", ng.nextWord(3, 3));
		assertEquals("bȩȩc", ng.nextWord(4, 4));
	}
}
