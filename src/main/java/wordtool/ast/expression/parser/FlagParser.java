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
package wordtool.ast.expression.parser;

import wordtool.WordGenParser;

import wordtool.ast.expression.FlagExpression;
import wordtool.ast.flag.Flag;


import java.util.ArrayList;
import java.util.regex.Matcher;

public class FlagParser implements WordGenParser.ExpressionParser<FlagExpression> {

	@Override
	public FlagExpression parse(Matcher matcher, String expression) {
		// group 1 = + or -
		// group 2 = flag
		// group 3 = flags: (flag,flag,...)
		if (matcher.groupCount() == 3) {
			boolean isPreceding = !"+".equals(matcher.group(1));
			final FlagExpression expr = new FlagExpression();
			expr.setFlags(new ArrayList<Flag>());
			for (final String s : matcher.group(3).split(",")) {
				expr.getFlags().add(new Flag(s.trim()));
			}
			expr.setPreceding(isPreceding);
			
			return expr;
		}
		
		return null;
	}

}
