package experiments.util;

import bddgraph.Edge;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class ReadFile {

    public static Stream<Edge> read(String filePath, boolean oneIndexed) throws IOException {
        Stream<String> stream = Files.lines(Paths.get(filePath));
        int offset = oneIndexed ? -1 : 0;
        return stream
                .filter(line -> line.matches("[0-9]+[- \t][0-9]+"))
                .map(line -> line.split("[- \t]"))
                .map(lines -> new Edge(Integer.parseInt(lines[0]) + offset, Integer.parseInt(lines[1]) + offset));
    }
}

