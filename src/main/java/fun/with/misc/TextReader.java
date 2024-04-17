package fun.with.misc;

import fun.with.Lists;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TextReader {
        public static Lists<String> read(File file) {
            List<String> lines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
                String line;
                boolean first = true;
                while ((line = reader.readLine()) != null) {
                    if (first){
                        // weird java BOM stuff
                        // https://stackoverflow.com/questions/4897876/reading-utf-8-bom-marker/4897993#4897993
                        line = line.replace("\uFEFF","");
                    }
                    lines.add(line);
                    first = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Lists.wrap(lines);
        }

}
