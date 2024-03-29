package com.github.hteph.generators.namegenerator;

import lombok.NonNull;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public abstract class NameGenerator {
    /**
     * Generates a name of at most {@code maxLength} characters.
     *
     * @param maxLength Maximum length of the generated name.
     * @return The name.
     */
    abstract String generate(final int maxLength);

	/**
	 * Determines the validity of a maximum length
	 *
	 * @param maxLength A maximum length.
	 * @throws IllegalArgumentException If {@code maxLength} <= 0.
	 */
	protected final void validateMaxLength(final int maxLength) throws IllegalArgumentException {
    	if (maxLength <= 0) {
    		throw new IllegalArgumentException("The maximum length, which is currently " + maxLength + " must be at least 1.");
		}
	}

	protected final void lowercaseAllElements(final @NonNull String[] array) {
		for (int i = 0 ; i < array.length ; i++) {
			array[i] = array[i].toLowerCase();
		}
	}

	/**
	 * Capitalizes the string held by a {@link StringBuilder}.
	 *
	 * @param sb A {@link StringBuilder}.
	 * @return The capitalized string.
	 */
	protected String capitalize(final StringBuilder sb) {
		return sb.substring(0, 1).toUpperCase(Locale.ROOT) + sb.substring(1);
	}

	/**
	 * Cleans the string held by a {@link StringBuilder} by performing the
	 * following operations:
	 *
	 * <ul>
	 *     <li>
	 *         Removes non-alphabetic characters from the start of the string.
	 *     </li>
	 *     <li>
	 *         Removes non-alphabetic characters from the end of the string.
	 *     </li>
	 * </ul>
	 *
	 * @param sb A {@link StringBuilder}.
	 * @return The cleaned {@link StringBuilder}.
	 */
	protected StringBuilder clean(final StringBuilder sb) {
		int codePoint = sb.codePointAt(0);
		if (!Character.isAlphabetic(codePoint)) {
			sb.deleteCharAt(0);
		}

		codePoint = sb.codePointAt(sb.length() - 1);
		if (!Character.isAlphabetic(codePoint)) {
			sb.deleteCharAt(sb.length() - 1);
		}

		return sb;
	}

	/**
	 * Returns a random element from the given array.
	 *
	 * @param array An array.
	 * @return A random element from the given array.
	 */
	protected String randomArrayElement(final String[] array) {
		if (array.length == 0) {
			throw new ArrayIndexOutOfBoundsException("The array is empty.");
		}

		return array[ThreadLocalRandom.current().nextInt(array.length)];
	}

	/**
	 * Produces a random max length which is guaranteed to be between
	 * <i>(0.5 * maxLength)</i> and <i>maxLength</i>.
	 *
	 * @param maxLength The initial max length.
	 * @return The randomized max length.
	 */
	protected int randomizeMaxLength(final int maxLength) {
		return ThreadLocalRandom.current().nextInt(
			(int) (maxLength * 0.5),
			maxLength + 1
		);
	}
}
