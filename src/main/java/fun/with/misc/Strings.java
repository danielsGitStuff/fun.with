package fun.with.misc;

import fun.with.Lists;

public class Strings {

    public static Strings wrap(String s) {
        return new Strings(s);
    }

    public static boolean hasContent(String s) {
        return Strings.notNullOrEmpty(s);
    }

    public static boolean notNullOrEmpty(String s) {
        return s != null && !s.isEmpty();
    }

    private final String s;

    public Strings(String s) {
        this.s = s;
    }

    /**
     * Add right padding to String if it is shorter than stringLength.
     *
     * @param s                String to pad
     * @param stringLength     length to which s will be padded.
     * @param paddingCharacter must have length of 1.
     * @return right padded String.
     */
    public static String rightPad(String s, int stringLength, String paddingCharacter) {
        if (s.length() >= stringLength)
            return s;
        if (paddingCharacter.length() != 1)
            throw new RuntimeException("Padding string must have length 1. It was " + paddingCharacter.length());
        int missing = stringLength - s.length();
        return s + Lists.of(paddingCharacter).repeat(missing).join("");
    }

    public String get() {
        return this.s;
    }

    public Character[] toCharArray() {
        Character[] characters = new Character[this.s.length()];
        for (int i = 0; i < this.s.length(); i++) {
            characters[i] = this.s.charAt(i);
        }
        return characters;
    }

    public Lists<Character> ls() {
        return Lists.of(this.toCharArray());
    }


}
