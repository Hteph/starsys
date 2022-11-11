package wordtool.scanner;


import lombok.extern.slf4j.Slf4j;
import wordtool.scanner.stats.FrequencyStats;
import wordtool.scanner.stats.Stats;
import wordtool.util.CollectionsUtil;
import wordtool.util.FilesUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

@Slf4j
public class Scanner {

	private static final String FORBIDDEN_CHARS = "[«¦»!\"§$%&/\\(\\)=\\?1234567890\\{\\}\\[\\]\\\\\\+\\-\\*/;:,\\.<>|#'~–]";

	private static final String REMOVE_CHARS = "[,?!]$";
	
	public static class ScanConfig {
		final File file;
		final boolean useForStart;
		final boolean useForMid;
		final boolean useForEnding;
		final String exprAndFlags;
	
		public ScanConfig(final File file, final boolean useForStart, final boolean useForMid, 
				final boolean useForEnding) {
			this(file, useForStart, useForMid, useForEnding, null);
			
		}
		
		public ScanConfig(final File file, final boolean useForStart, final boolean useForMid, 
				final boolean useForEnding, final String exprAndFlags) {
			this.file = file;
			this.useForEnding = useForEnding;
			this.useForMid = useForMid;
			this.useForStart = useForStart;
			this.exprAndFlags = exprAndFlags;
		}

		public File getFile() {
			return file;
		}

		public boolean isUseForStart() {
			return useForStart;
		}

		public boolean isUseForMid() {
			return useForMid;
		}

		public boolean isUseForEnding() {
			return useForEnding;
		}

		public String getExprAndFlags() {
			return exprAndFlags;
		}
	}
	
	public static class Builder {
		Locale locale = Locale.US;
		boolean isCaseSensitive = false;
		String forbiddenCharsRegex = FORBIDDEN_CHARS;
		String removeCharsRegex = REMOVE_CHARS;
		
		public Scanner build() {
			final Scanner scanner = new Scanner();
			scanner.locale = this.locale;
			scanner.isCaseSensitive = this.isCaseSensitive;
			scanner.forbiddenCharsRegex = this.forbiddenCharsRegex;
			scanner.removeCharsRegex = this.forbiddenCharsRegex;
			return scanner;
		}
		
		public Builder setIsCaseSensitive(final boolean isCaseSensitive) {
			this.isCaseSensitive = isCaseSensitive;
			return this;
		}
		
		public Builder setLocale(final Locale locale) {
			this.locale = locale;
			return this;
		}
		
		public Builder setForbiddenChars(final String regex) {
			this.forbiddenCharsRegex = regex;
			return this;
		}
		
		public Builder setRemoveChars(final String regex) {
			this.removeCharsRegex = regex;
			return this;
		}
	}
	
	private final List<Stats> stats = new ArrayList<>();
	
	private Locale locale = Locale.US;
	
	private boolean isCaseSensitive = false;
	
	private String forbiddenCharsRegex = FORBIDDEN_CHARS;
	
	private String removeCharsRegex = REMOVE_CHARS;
	
	private Scanner() { }
	
	/**
	 * scans a file for words only
	 * @param files
	 * @return
	 * @throws IOException
	 */
	public TreeSet<String> scanFilesForWords(final File...files) throws IOException {
		final Pattern pattern = Pattern.compile(forbiddenCharsRegex);
		final Pattern removePatter = Pattern.compile(removeCharsRegex);
		final TreeSet<String> words = new TreeSet<>();
		
		for (final File file : files) {
			final List<String> lines = FilesUtil.readLines(file);
		
			for (final String line : lines) {
				for (String word : line.split("[\\s\\t]")) {
					word = word.trim();
					word = removePatter.matcher(word).replaceAll("");
					if (word.length() > 0 && !pattern.matcher(word).find()) {
						words.add(isCaseSensitive ? word : word.toLowerCase(locale));
					}
				}
			}
		}
		
		return words;
	}
	
	/**
	 * scans files for rules (and intially words) by using simple configs
	 * @param scanConfigs
	 * @return
	 * @throws IOException 
	 */
	public List<String> scanFilesForRules(final ScanConfig...scanConfigs) throws IOException {
		final Map<String, Set<String>> wordsStart = new HashMap<>();
		final Map<String, Set<String>> wordsMid = new HashMap<>();
		final Map<String, Set<String>> wordsEnd = new HashMap<>();
		for (final ScanConfig sc : scanConfigs) {
			final Set<String> words = scanFilesForWords(sc.getFile());
			if (sc.isUseForStart()) {
				processScanConfig(sc, wordsStart, words);
			}
			if (sc.isUseForMid()) {
				processScanConfig(sc, wordsMid, words);
			}
			if (sc.isUseForEnding()) {
				processScanConfig(sc, wordsEnd, words);
			}
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(processWordMap(wordsStart,"-"));
		sb.append(processWordMap(wordsMid,""));
		sb.append(processWordMap(wordsEnd,"+"));
		
		return Arrays.asList(sb.toString().split("\n"));
	}
	
	private void processScanConfig(final ScanConfig sc, final Map<String, Set<String>> wordMap, final Set<String> words) {
		Set<String> wordSet = wordMap.computeIfAbsent(sc.getExprAndFlags() == null ? "" : sc.getExprAndFlags(), k -> new TreeSet<>());
		wordSet.addAll(words);
	}

	private String processWordMap(final Map<String, Set<String>> wordMap, final String modifier) {
		final StringBuilder sb = new StringBuilder();
		for (final Entry<String, Set<String>> e : wordMap.entrySet()) {
			for (final String word : e.getValue()) {
				sb.append(modifier).append("[").append(word).append("]");
				sb.append(" ").append(e.getKey());
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	/**
	 * scans files for rules (and initially for words)
	 * @param files
	 * @return
	 * @throws IOException
	 */
	public List<String> scanFilesForRules(final File...files) throws IOException {
		final Set<String> words = scanFilesForWords(files);
		
		//stats.add(new LengthStats());
		final FrequencyStats fs1 = new FrequencyStats().setIgnoreSingles(false).setStringLength(1);
		final FrequencyStats fs2 = new FrequencyStats().setIgnoreSingles(false).setStringLength(2);
		final FrequencyStats fs3 = new FrequencyStats().setIgnoreSingles(false).setStringLength(3);
		stats.add(fs1);
		stats.add(fs2);
		stats.add(fs3);
		
		for (final Stats s : stats) {
			s.evaluate(words);
		}
		
		final Map<String, Set<String>> startMap = generateStartMap(fs2, fs3);

		final Map<String, Set<String>> midMap = generateMidMap(fs2, fs3);

		final Set<String> endList = new TreeSet<>();
		for (final Entry<String, FrequencyStats.FrequencyPositionStats> e : fs2.getCountMap().entrySet()) {
			if (e.getValue().getEndCount() > 0) {
				endList.add(e.getKey());
			}
		}
		for (final Entry<String, FrequencyStats.FrequencyPositionStats> e : fs1.getCountMap().entrySet()) {
			if (e.getValue().getEndCount() > 0) {
				endList.add(e.getKey());
			}
		}

		log.debug(fs3.prettyPrint());
		
		final List<String> rules = new ArrayList<>();
		rules.add(generateRules(startMap, "-"));
		rules.add(generateRules(midMap, ""));
		rules.add(generateRules(endList, "+"));
		return rules;
		
	}

	private Map<String, Set<String>> generateStartMap(final FrequencyStats fs, final FrequencyStats fsLonger) {
		final Map<String, Set<String>> startMap = new TreeMap<>();
		
		for (final Entry<String, FrequencyStats.FrequencyPositionStats> e : fs.getCountMap().entrySet()) {
			if (e.getValue().getStartCount() > 0) {
				for (final Entry<String, FrequencyStats.FrequencyPositionStats> ce : CollectionsUtil.getByPrefix(fsLonger.getCountMap(), e.getKey()).entrySet()) {
					if (ce.getValue().getStartCount() > 0) {
						Set<String> curMod = startMap.computeIfAbsent(e.getKey().substring(0, 1), k -> new TreeSet<>());
						curMod.add(ce.getKey().substring(1));
						
						final String subKey = e.getKey().substring(1) + ce.getKey().substring(e.getKey().length());
						for (final Entry<String, FrequencyStats.FrequencyPositionStats> subCe : CollectionsUtil.getByPrefix(fsLonger.getCountMap(), subKey).entrySet()) {
							if (subCe.getValue().getStartCount() > 0) {
								curMod = startMap.computeIfAbsent(e.getKey(), k -> new TreeSet<>());
								curMod.add(subCe.getKey().substring(1));
							}
						}
					}
				}
			}
		}

		return startMap;
	}
	
	private Map<String, Set<String>> generateMidMap(final FrequencyStats fs, final FrequencyStats fsLonger) {
		final Map<String, Set<String>> midMap = new TreeMap<>();
		for (final Entry<String, FrequencyStats.FrequencyPositionStats> e : fs.getCountMap().entrySet()) {
			if (e.getValue().getMidCount() > 0) {
				//it's a mid syllable
				for (final Entry<String, FrequencyStats.FrequencyPositionStats> ce : CollectionsUtil.getByPrefix(fsLonger.getCountMap(), e.getKey()).entrySet()) {
					//get all longer syllables starting with syllable
					if (ce.getValue().getMidCount() > 0) {
						final String subKey = e.getKey().substring(1) + ce.getKey().substring(e.getKey().length());
						for (final Entry<String, FrequencyStats.FrequencyPositionStats> subCe : CollectionsUtil.getByPrefix(fsLonger.getCountMap(), subKey).entrySet()) {
							if (subCe.getValue().getMidCount() > 0) {
								Set<String> curMod = midMap.computeIfAbsent(e.getKey(), k -> new TreeSet<>());
								curMod.add(subCe.getKey().substring(1));
							}
						}
					}
					
					if (ce.getValue().getEndCount() > 0) {
						Set<String> curMod = midMap.computeIfAbsent(e.getKey().substring(0, 1), k -> new TreeSet<>());
						curMod.add(ce.getKey().substring(1));
					}
				}
			}
		}
		return midMap;
	}

	private String generateRules(final Collection<String> syls, final String prefix) {
		final StringBuilder sb = new StringBuilder();
		for (final String s : syls) {
			sb.append(prefix).append("[").append(s).append("]\n");
		}
		return sb.toString();
	}
	
	private String generateRules(final Map<String, Set<String>> sylMap, final String prefix) {
		final StringBuilder sb = new StringBuilder();
		for (final Entry<String, Set<String>> e : sylMap.entrySet()) {
			sb.append(prefix).append("[").append(e.getKey()).append("]");
			if (e.getValue() != null && !e.getValue().isEmpty()) {
				sb.append(" +accept(");
				final Iterator<String> it = e.getValue().iterator();
				while (it.hasNext()) {
					sb.append(it.next());
					if (it.hasNext()) {
						sb.append(",");
					}
				}
				sb.append(")\n");
			}
		}

		return sb.toString();
	}	
}
