package fun.with;

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

    public Lists<Character> toLists() {
        return Lists.wrap(this.toCharArray());
    }


}
