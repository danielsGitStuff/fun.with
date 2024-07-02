package fun.with;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Very simple convenient wrapper for {@link Pattern} and {@link Matcher}.
 * Allows you to ditch boilerplate code like storing a reference the {@link Matcher} from pattern.match()
 * and call matcher.find() afterward.
 */
public class Regex {

    public static Regex of(String regex, String notFoundValue, Integer option) {
        Regex r = new Regex();
        r.pattern = option == null ? Pattern.compile(regex) : Pattern.compile(regex, option);
        r.notFoundValue = notFoundValue;
        return r;
    }

    public static Regex of(String regex) {
        return Regex.of(regex, null, null);
    }

    public static Regex of(String regex, String notFoundValue) {
        return Regex.of(regex, notFoundValue, null);
    }

    private Pattern pattern;
    private String notFoundValue;
    private Matcher matcher;
    private boolean found = false;

    public Regex match(String input) {
        this.matcher = pattern.matcher(input);
        this.found = this.matcher.find();
        return this;
    }

    public String group() {
        if (!this.found)
            return this.notFoundValue;
        return this.matcher.group();
    }

    public String group(int group) {
        if (!this.found)
            return this.notFoundValue;
        return this.matcher.group(group);
    }
}
