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



import wordtool.ast.expression.AcceptExpression;
import wordtool.ast.expression.Expression;
import wordtool.ast.expression.FlagExpression;
import wordtool.ast.expression.NonRepeatableExpression;
import wordtool.ast.flag.Flag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NameGenTestPreConditions {

	private File getFileForRes(final String resFielName) {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("at/lume/wordgen/lib/" + resFielName).getFile());
		return file;
	}

	@Test
	public void testParsingFlagsAndPreConditions() throws IOException {
		WordGen ng = new WordGenParser.Builder().build().fromFile(getFileForRes("parse12_1.txt"));
		assertNotNull(ng);
		final List<Expression> exprList = new ArrayList<>();
		final FlagExpression flagExpr = new FlagExpression();
		flagExpr.setPreceding(false);
		flagExpr.setFlags(new ArrayList<>(List.of(new Flag("a"))));
		exprList.add(flagExpr);
		
		assertEquals( "a", ng.nextWord(1, 1, exprList));
		assertEquals( "aa", ng.nextWord(2, 2, exprList));
		assertEquals( "aaa", ng.nextWord(3, 3, exprList));
		assertEquals( "aaaa", ng.nextWord(4, 4, exprList));
		assertEquals( "aaaaa", ng.nextWord(5, 5, exprList));
		assertEquals("aaaaaa", ng.nextWord(6, 6, exprList));
		assertEquals("aaaaaaa", ng.nextWord(7, 7, exprList));
		assertNotEquals("aaaaaab", ng.nextWord(7, 7, exprList));
		assertNotEquals( "aaaaabb", ng.nextWord(7, 7, exprList));
		assertNotEquals( "aaaabbb", ng.nextWord(7, 7, exprList));
	}	

	@Test
	public void testParsingFlagsAndPreConditionsWithExpressionString() throws IOException {
		final WordGenParser wgp = new WordGenParser.Builder().build();
		final WordGen ng = wgp.fromFile(getFileForRes("parse12_1.txt"));
		assertNotNull( ng);
		final List<Expression> exprList = wgp.parseExpressions("+flag(a)");  		
		assertEquals("a", ng.nextWord(1, 1, exprList));
		assertEquals( "aa", ng.nextWord(2, 2, exprList));
		assertEquals( "aaa", ng.nextWord(3, 3, exprList));
		assertEquals("aaaa", ng.nextWord(4, 4, exprList));
		assertEquals("aaaaa", ng.nextWord(5, 5, exprList));
		assertEquals( "aaaaaa", ng.nextWord(6, 6, exprList));
		assertEquals("aaaaaaa", ng.nextWord(7, 7, exprList));
		assertNotEquals( "aaaaaab", ng.nextWord(7, 7, exprList));
		assertNotEquals("aaaaabb", ng.nextWord(7, 7, exprList));
		assertNotEquals("aaaabbb", ng.nextWord(7, 7, exprList));
	}
	
	@Test
	public void testParsingExpressionString() throws IOException {
		final String expressionString = "+flag(a) -accept(ab) +noRepeat #a #b #xyz";
		final WordGenParser wgp = new WordGenParser.Builder().build();
		final List<Expression> exprList = wgp.parseExpressions(expressionString);
		assertEquals( 3, exprList.size());
		assertEquals( FlagExpression.class, exprList.get(0).getClass());
		assertEquals(AcceptExpression.class, exprList.get(1).getClass());
		assertEquals(NonRepeatableExpression.class, exprList.get(2).getClass());
		
		final List<Flag> flagList = wgp.parseFlags(expressionString);
		assertEquals( 3, flagList.size());
		assertEquals( "a", flagList.get(0).getFlag());
		assertEquals( "b", flagList.get(1).getFlag());
		assertEquals("xyz", flagList.get(2).getFlag());
	}	
}
