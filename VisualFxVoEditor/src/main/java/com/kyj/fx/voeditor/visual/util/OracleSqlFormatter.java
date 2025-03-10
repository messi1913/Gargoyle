
package com.kyj.fx.voeditor.visual.util;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Performs formatting of basic SQL statements (DML + query).
 *
 * 2016-05-09 UNION ALL과 같은 합성 예약어 처리기능 적용
 *
 * @author Gavin King
 * @author Steve Ebersole
 */
public class OracleSqlFormatter implements Formatter {

	public static final String WHITESPACE = " \n\r\f\t";

	private static final Set<String> BEGIN_CLAUSES = new HashSet<String>();
	private static final Set<String> END_CLAUSES = new HashSet<String>();
	private static final Set<String> LOGICAL = new HashSet<String>();
	private static final Set<String> QUANTIFIERS = new HashSet<String>();
	private static final Set<String> DML = new HashSet<String>();
	private static final Set<String> MISC = new HashSet<String>();

	static {
		BEGIN_CLAUSES.add("left");
		BEGIN_CLAUSES.add("right");
		BEGIN_CLAUSES.add("inner");
		BEGIN_CLAUSES.add("outer");
		BEGIN_CLAUSES.add("group");
		BEGIN_CLAUSES.add("order");
		// BEGIN_CLAUSES.add("union");

		END_CLAUSES.add("where");
		END_CLAUSES.add("set");
		END_CLAUSES.add("having");
		END_CLAUSES.add("join");
		END_CLAUSES.add("from");
		END_CLAUSES.add("by");
		END_CLAUSES.add("join");
		END_CLAUSES.add("into");
		END_CLAUSES.add("union");

		LOGICAL.add("and");
		LOGICAL.add("or");
		LOGICAL.add("when");
		LOGICAL.add("else");
		LOGICAL.add("end");
		LOGICAL.add("#if");
		LOGICAL.add("#end");

		QUANTIFIERS.add("in");
		QUANTIFIERS.add("all");
		QUANTIFIERS.add("exists");
		QUANTIFIERS.add("some");
		QUANTIFIERS.add("any");

		DML.add("insert");
		DML.add("update");
		DML.add("delete");

		MISC.add("select");
		MISC.add("on");
	}

	static final String indentString = "    ";
	static final String initial = "\n";

	public String format(String source) {
		return new FormatProcess(source).perform();
	}

	private static class FormatProcess {
		boolean beginLine = true;
		boolean afterBeginBeforeEnd = false;
		boolean afterByOrSetOrFromOrSelect = false;
		boolean afterOn = false;
		boolean afterBetween = false;
		boolean afterInsert = false;
		boolean afterWhite = false;

		int inFunction = 0;
		int parensSinceSelect = 0;
		private LinkedList<Integer> parenCounts = new LinkedList<Integer>();
		private LinkedList<Boolean> afterByOrFromOrSelects = new LinkedList<Boolean>();
		int indent = 0;

		StringBuffer result = new StringBuffer();
		StringTokenizer tokens;
		String lastToken;
		String token;
		String lcToken;

		public FormatProcess(String sql) {
			tokens = new StringTokenizer(sql, "()+=<>'`\"[],/*-;" + WHITESPACE, true);
		}

		public String perform() {
			while (tokens.hasMoreTokens()) {

				token = tokens.nextToken();
				lcToken = token.toLowerCase();

				if ("'".equals(token)) {
					String t;
					do {
						t = tokens.nextToken();
						token += t;
					} while (!"'".equals(t) && tokens.hasMoreTokens());
				} else if ("\"".equals(token)) {
					String t;
					do {
						t = tokens.nextToken();
						token += t;
					} while (!"\"".equals(t));

				}

				// 주석 제거
				if ("/".equals(token)) {
					String t;
					t = tokens.nextToken();
					token += t;
					if ("*".equals(t)) {
						do {
							t = tokens.nextToken();
							token += t;
						} while (!token.endsWith("*/"));
						token += "\n";
					}
					out();
				}
				// 주석 제거
				else if ("-".equals(token)) {
					String t;
					t = tokens.nextToken();
					token += t;
					do {
						t = tokens.nextToken();
						token += t;
					} while (!token.endsWith("\n"));
					out();
				} else if (afterByOrSetOrFromOrSelect && ",".equals(token)) {
					commaAfterByOrFromOrSelect();
				} else if (afterOn && ",".equals(token)) {
					commaAfterOn();
				}

				else if ("(".equals(token)) {
					openParen();
				} else if (")".equals(token)) {
					closeParen();
				}

				else if (BEGIN_CLAUSES.contains(lcToken)) {
					beginNewClause();
				}

				else if (END_CLAUSES.contains(lcToken)) {
					endNewClause();
				} else if ("select".equals(lcToken)) {
					select();
				}

				else if (DML.contains(lcToken)) {
					updateOrInsertOrDelete();
				}

				else if ("values".equals(lcToken)) {
					values();
				}

				else if ("on".equals(lcToken)) {
					on();
				}

				else if (afterBetween && lcToken.equals("and")) {
					misc();
					afterBetween = false;
				}

				else if (LOGICAL.contains(lcToken)) {
					logical();
				}

				else if (isWhitespace(token)) {
					white();
				} else if (";".equals(lcToken)) {
					reset();
				}
				/* 2015-05-09 KYJ UNION ALL과 같이 합성 예약어 처리를 위한 코드*/
				else if ("union".equals(lastToken.toLowerCase()) && "all".equals(lcToken)) {

					// beginLine = false;
					int lastIndexOf = result.lastIndexOf(lastToken);
					result.delete(lastIndexOf, result.length());
					result.append(lastToken).append(" ");
					result.append(token).append(System.lineSeparator());
					// afterWhite = false;

				} else {
					misc();
				}

				if (!isWhitespace(token)) {
					lastToken = token;
				}

			}

			return result.toString();
		}

		private void commaAfterOn() {

			out();
			indent--;
			newline();
			afterOn = false;
			afterByOrSetOrFromOrSelect = true;
		}

		private void commaAfterByOrFromOrSelect() {

			out();
			newline();
		}

		private void logical() {

			if ("end".equals(lcToken)) {
				indent--;
			}
			newline();
			out();

			beginLine = false;
		}

		private void on() {

			indent++;
			afterOn = true;
			newline();
			out();
			beginLine = false;
		}

		private void misc() {

			out();
			if ("between".equals(lcToken)) {
				afterBetween = true;
			}
			// if (!afterBeginBeforeEnd && afterInsert) {
			// afterInsert = false;
			// } else

			if (afterInsert) {
				newline();
				afterInsert = false;
			} else {
				beginLine = false;
				if ("case".equals(lcToken)) {
					indent++;
				}
			}
		}

		private void white() {

			if (!beginLine && !afterWhite) {
				result.append(" ");
				afterWhite = true;
			}
		}

		private void updateOrInsertOrDelete() {

			out();
			indent++;
			beginLine = false;
			if ("update".equals(lcToken)) {
				newline();
			}
			if ("insert".equals(lcToken)) {
				afterInsert = true;
			}
		}

		private void select() {

			out();
			indent++;
			newline();
			parenCounts.addLast(Integer.valueOf(parensSinceSelect));
			afterByOrFromOrSelects.addLast(Boolean.valueOf(afterByOrSetOrFromOrSelect));
			parensSinceSelect = 0;
			afterByOrSetOrFromOrSelect = true;

		}

		private void out() {
			result.append(token);
			afterWhite = false;
		}

		private void endNewClause() {
			if (!afterBeginBeforeEnd) {
				indent--;
				if (afterOn) {
					indent--;
					afterOn = false;
				}
				newline();
			}
			out();
			if (!"union".equals(lcToken)) {
				indent++;
			}
			newline();
			afterBeginBeforeEnd = false;
			afterByOrSetOrFromOrSelect = "by".equals(lcToken) || "set".equals(lcToken) || "from".equals(lcToken);
		}

		private void beginNewClause() {

			if (!afterBeginBeforeEnd) {
				if (afterOn) {
					indent--;
					afterOn = false;
				}
				indent--;
				newline();
			}
			out();
			beginLine = false;
			afterBeginBeforeEnd = true;
		}

		private void values() {

			indent--;
			newline();
			out();
			indent++;
			newline();
		}

		private void closeParen() {

			parensSinceSelect--;
			if (parensSinceSelect < 0) {
				indent--;
				parensSinceSelect = parenCounts.removeLast().intValue();
				afterByOrSetOrFromOrSelect = afterByOrFromOrSelects.removeLast().booleanValue();
			}
			if (inFunction > 0) {
				inFunction--;
				out();
			} else {
				if (!afterByOrSetOrFromOrSelect) {
					indent--;
					newline();
				}
				out();
			}
			beginLine = false;
		}

		private void openParen() {

			if (isFunctionName(lastToken) || inFunction > 0) {
				inFunction++;
			}
			beginLine = false;
			if (inFunction > 0) {
				out();
			} else {
				out();
				if (!afterByOrSetOrFromOrSelect) {
					indent++;
					newline();
					beginLine = true;
				}
			}
			parensSinceSelect++;
		}

		private void reset() {
			out();
			beginLine = true;
			afterBeginBeforeEnd = false;
			afterByOrSetOrFromOrSelect = false;
			afterOn = false;
			afterBetween = false;
			afterInsert = false;
			inFunction = 0;
			parensSinceSelect = 0;
			parenCounts.clear();
			afterByOrFromOrSelects.clear();
			indent = 0;
			result.append("\n\n\n");
		}

		private static boolean isFunctionName(String tok) {
			final char begin = tok.charAt(0);
			final boolean isIdentifier = Character.isJavaIdentifierStart(begin) || '"' == begin;
			return isIdentifier && !LOGICAL.contains(tok) && !END_CLAUSES.contains(tok) && !QUANTIFIERS.contains(tok) && !DML.contains(tok)
					&& !MISC.contains(tok);
		}

		private static boolean isWhitespace(String token) {
			return WHITESPACE.indexOf(token) >= 0;
		}

		private void newline() {
			result.append("\n");
			for (int i = 0; i < indent; i++) {
				result.append(indentString);
			}
			beginLine = true;
		}

		// private void removeLastNewline() {
		// int lastIndexOf = result.lastIndexOf("\n");
		// result.delete(lastIndexOf, result.length());
		// }
	}

	@Override
	public String toUpperCase(String sql) {
		return toUpperLowerCase(sql, true);
	}

	@Override
	public String toLowerCase(String sql) {
		return toUpperLowerCase(sql, false);
	}

	private String toUpperLowerCase(String sql, boolean isUpper) {
		StringBuffer result = new StringBuffer();
		StringTokenizer tokens = new StringTokenizer(sql, "'-/*\n", true);
		String token;
		while (tokens.hasMoreTokens()) {
			token = tokens.nextToken();
			// 변수
			if ("'".equals(token)) {
				String t;
				do {
					t = tokens.nextToken();
					token += t;
				} while (!"'".equals(t) && tokens.hasMoreTokens());

				result.append(token);
			}
			// 주석 /**/
			else if ("/".equals(token)) {
				String t;
				t = tokens.nextToken();
				token += t;
				if ("*".equals(t)) {
					do {
						t = tokens.nextToken();
						token += t;
					} while (!token.endsWith("*/"));
				}

				result.append(token);
			}
			// 주석 --
			else if ("-".equals(token)) {
				String t;
				t = tokens.nextToken();
				token += t;
				if ("-".equals(t)) {
					do {
						t = tokens.nextToken();
						token += t;
					} while (!token.endsWith("\n"));
				}
				result.append(token);
			}
			// 문자 저장
			else {
				if (isUpper) {
					result.append(token.toUpperCase());
				} else {
					result.append(token.toLowerCase());
				}
			}
		}
		return result.toString();
	}

	@Override
	public String split(String sql, int caretPosition) {
		StringBuffer result = new StringBuffer();
		String[] split = sql.split("\n\n");
		int textLength = 0;
		for (String str : split) {
			if (ValueUtil.isEmpty(str.trim())) {
				result.append(str);

			} else {
				result.setLength(0);
				result.append(str);
			}
			textLength += str.length();
			textLength += 2;

			if (textLength >= caretPosition) {
				break;
			}
		}

		return result.toString();
	}
}